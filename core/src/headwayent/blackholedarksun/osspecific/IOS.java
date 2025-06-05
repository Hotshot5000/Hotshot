/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/21/20, 11:19 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.osspecific;

//import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import com.badlogic.gdx.Gdx;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.android.AndroidRenderWindow;

public final class IOS {

    public static long getTextFieldDelegateAdapter(Field textDelegate) throws IllegalAccessException {
//        UITextFieldDelegateAdapter uiTextFieldDelegateAdapter = (UITextFieldDelegateAdapter) textDelegate.get(Gdx.input);
//        return uiTextFieldDelegateAdapter.getHandle();
        return 0;
    }

    public static void waitForMetalRenderSystemToLoad(CountDownLatch viewDidLoadLatch) {
//        IOSApplicationConfiguration iosAppConfig = MainApp.getMainThread().getApplicationSettings().iosConfig;
//        AndroidRenderWindow.initializeIOSViews(iosAppConfig.uiWindowHandle);
        // We wait fot the viewDidLoad to happen on the native side which means we are ready to
        // initialize the MetalRenderSystem.
//        try {
//            viewDidLoadLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // We must initialize the MetalRenderSystem here since it also initializez the UIView that we need
        // before leaving the viewDidLoad function on the native side.
        // The previous comment seems not to actually be true since we managed to return from viewDidLoad
        // without initializing anything. Remains to be seen if this is the correct thing to do but
        // it's easier for us from a design perspective.
    }

    public static void pauseNative(long uiViewController) {
        MainActivity.pauseNative(uiViewController);
    }

    public static void resumeNative(long uiViewController) {
        MainActivity.resumeNative(uiViewController);
    }
}
