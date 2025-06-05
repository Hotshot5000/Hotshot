/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/25/21, 6:02 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.backends.iosrobovm.IOSInput;

//import org.lwjgl.opengl.Display;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;

import headwayent.blackholedarksun.osspecific.Win32;
import headwayent.hotshotengine.ApplicationStartSettings;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;

public class BlackholeDarksunMain extends ApplicationAdapter {


    private final String[] args;
    private final ApplicationStartSettings settings;
    private final MainActivity mainActivity = new MainActivity();
    private final Method pollDevices;
    public static BlackholeDarksunMain main;
    private final ConcurrentLinkedQueue<Runnable> renderThreadEventQueue = new ConcurrentLinkedQueue<>();

    private static int nativeRedirectOutputResult;

    public BlackholeDarksunMain(String[] args, ApplicationStartSettings settings) {
        main = this;
        this.args = args;
        this.settings = settings;

        initJvmData();
        nativeRedirectOutputResult = stringTest("test string TEST STRING");

        pollDevices = Win32.makePollDevicesMethodAccessible(settings);
    }

    @Override
    public void create() {
        mainActivity.onCreate(args, settings);
    }

    @Override
    public void dispose() {
        mainActivity.onDestroy();
    }

    public void addRenderQueueEvent(Runnable runnable) {
        renderThreadEventQueue.add(runnable);
    }

    private int frame;
    private long currentSecond;

    private long outsideRenderLoopBeginTime;

    @Override
    public void render() {

//        System.out.println("outsideRenderEndTime: " + ((ENG_Utility.nanoTime() - outsideRenderLoopBeginTime) / (double) 1000000));
//		System.out.println("Starting rendering");
//        GLMainThread.getSingleton().onDrawFrame();
//        MainApp.getMainThread().run();
//        glTest();
//        ByteBuffer buffer = ENG_Utility.allocateDirect(32);
//        buffer.putInt(5);
//        buffer.putInt(6);
//        buffer.putInt(7);
//        buffer.putInt(8);
//        buffer.flip();
//        ENG_RenderingThread.writeBytes(buffer);
//        ENG_RenderingThread.flushPipeline();

        // We cannot use Display.processMessages(); because we don't actually handle the display
        // in lwjgl but in Ogre.
        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP && settings.applicationMode == MainApp.Mode.CLIENT) {
            try {
                pollDevices.invoke(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (MainApp.PLATFORM == MainApp.Platform.IOS) {
//            ((IOSInput) Gdx.input).processEvents();
            // We need to call UI stuff from the main thread.
            Runnable runnable = renderThreadEventQueue.poll();
            if (runnable != null) {
                runnable.run();
            }
        }

//        System.out.println("Happy camper!");

//        long renderBeginTime = ENG_Utility.nanoTime();
        ENG_RenderingThread.renderFrame();
//        long renderEndTime = ENG_Utility.nanoTime() - renderBeginTime;
//        System.out.println("renderEndTime: " + (renderEndTime / (double) 1000000));

//        long l = System.nanoTime();
//        if (l - currentSecond > 1000000000) {
//            System.out.println("Rendering finished for frame: " + (++frame));
//            frame = 0;
//            currentSecond = l;
//        } else {
//            ++frame;
//        }

//        outsideRenderLoopBeginTime = ENG_Utility.nanoTime();
    }

    @Override
    public void resize(int width, int height) {
//        GLMainThread.getSingleton().onSurfaceChanged(width, height);
    }

    @Override
    public void pause() {
        if (MainApp.Platform.isMobile()) {
            mainActivity.onPause();
//		MainApp.getGame().setGameActivated(false);
        }
    }

    @Override
    public void resume() {
        System.out.println("RESUMING MAIN ACTIVITY");
        if (MainApp.Platform.isMobile()) {
            mainActivity.onResume();
//		MainApp.getGame().setGameActivated(true);
        }
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public static int getNativeRedirectOutputResult() {
        return nativeRedirectOutputResult;
    }

    public native void glTest();
    public native void initJvmData();
    public native int stringTest(String s);
}
