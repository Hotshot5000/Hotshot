/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/26/21, 7:35 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android;

//import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.backends.iosrobovm.IOSInput;

import org.robovm.apple.uikit.UITextFieldDelegateAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.TreeMap;

import headwayent.blackholedarksun.BlackholeDarksunMain;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.osspecific.IOS;
import headwayent.blackholedarksun.osspecific.Win32;
import headwayent.hotshotengine.renderer.ENG_PixelBox;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointer;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.egl.AndroidRenderWindowNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

public class AndroidRenderWindow extends ENG_RenderWindow implements ENG_NativePointer {

    private final AndroidRenderWindowNativeWrapper wrapper = new AndroidRenderWindowNativeWrapper();

    // For Win32. Nice design! AndroidRenderWindow now means nothing.
    private long hwnd;
    private long hdc;

    // For iOS. Same as above!
    private static long uiView;
    private static long uiViewController;
    private static long appDelegate;
    private static long uiTextFieldDelegateAdapterHandle;

    public AndroidRenderWindow(String name, int width, int height) {

        super._setName(name);
        super._setWidth(width);
        super._setHeight(height);
//        initializeIOSViews(iosAppConfig.uiWindowHandle);
    }

    public static long getUiView() {
        return uiView;
    }

    public static long getUiViewController() {
        return uiViewController;
    }

    public static long getAppDelegate() {
        return appDelegate;
    }

    public static long getUiTextFieldDelegateAdapterHandle() {
        return uiTextFieldDelegateAdapterHandle;
    }

    @Override
    public Object getCustomAttribute(String name, Object data) {

        //Overwrite here it's useless anyway...
        return null;
    }

    public static void initializeIOSViews(long uiWindowHandle) {
        initializeViewControllerCallbacks(BlackholeDarksunMain.main);
//        initializeMetalViewCallbacks((IOSInput) Gdx.input);
        uiViewController = initializeViewController(uiWindowHandle);
        uiView = initializeView();
        appDelegate = initializeAppDelegate();

        try {
            Field textDelegate = Gdx.input.getClass().getDeclaredField("textDelegate");
            textDelegate.setAccessible(true);
            uiTextFieldDelegateAdapterHandle = IOS.getTextFieldDelegateAdapter(textDelegate);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(String name, int width, int height, boolean fullScreen,
                       TreeMap<String, String> miscParams) {

//        ENG_NativeCalls.RenderWindowAndViewportData renderWindowAndViewportData = ENG_NativeCalls.callRoot_CreateRenderWindow(
//                ENG_RenderRoot.getRenderRoot().getPointer(), name, width, height, fullScreen,
//                miscParams);
        long[] renderWindowResults = ENG_NativeCalls.callRoot_CreateRenderWindow(ENG_RenderRoot.getRenderRoot().getPointer(), name, width, height, fullScreen,
                miscParams, uiViewController);
        long renderWindowPtr = renderWindowResults[0];
        if (MainApp.DesktopPlatform.isWin32Desktop()) {
            hwnd = renderWindowResults[1];
            hdc = renderWindowResults[2];
            Win32.injectHandlers(hwnd, hdc);
        }
        wrapper.setPtr(renderWindowPtr);
//        ENG_Viewport viewport = addViewport(null, 0,
//                renderWindowAndViewportData.left,
//                renderWindowAndViewportData.top,
//                renderWindowAndViewportData.width,
//                renderWindowAndViewportData.height);
//        viewport.setPointer(renderWindowAndViewportData.viewportPtr);
    }

    @Override
    public void destroy(boolean skipGLDelete) {


    }

    @Override
    public void resize(int width, int height) {


    }

    @Override
    public void reposition(int left, int top) {


    }

    @Override
    public boolean isClosed() {

        return false;
    }

    @Override
    public void copyContentsToMemory(ENG_PixelBox dst, FrameBuffer buffer) {


    }

    @Override
    public boolean requiresTextureFlipping() {

        return false;
    }

    @Override
    public long getPointer() {
        return wrapper.getPtr();
    }

    public long getHwnd() {
        return hwnd;
    }

    public long getHdc() {
        return hdc;
    }

    public static native void setWindowProc(Class windowsDisplayClass, Method javaWindowProc);

    private static native void initializeViewControllerCallbacks(BlackholeDarksunMain main);
//    private static native void initializeMetalViewCallbacks(IOSInput iosInput);

    /**
     * Just returns the already created view. Already created by the view controller.
     * @return
     */
    private static native long initializeView();

    private static native long initializeViewController(long uiWindowHandle);

    private static native long initializeAppDelegate();
}
