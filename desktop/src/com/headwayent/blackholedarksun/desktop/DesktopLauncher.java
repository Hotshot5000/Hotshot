package com.headwayent.blackholedarksun.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import headwayent.blackholedarksun.BlackholeDarksunMain;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ApplicationStartSettings;
import headwayent.hotshotengine.ENG_DefaultUncaughtExceptionHandler;
import headwayent.hotshotengine.ENG_MainThread;

import java.awt.*;

public class DesktopLauncher {

    private static final String OGRE_NEXT_3_0_PATH = "../native_ogre_integration/";
    private static final boolean OGRE_NEXT_3_0 = true;
    private static final boolean DEBUG_LIB = true;

    public static final String BUILD_DEBUG_NATIVE_CLIENT_LIB = "build/Debug/native-client-lib";

    public static final String BUILD_RELEASE_NATIVE_CLIENT_LIB = "build/Release/native-client-lib";

    static {
        if (DEBUG_LIB) {
            System.loadLibrary(OGRE_NEXT_3_0 ? OGRE_NEXT_3_0_PATH + BUILD_DEBUG_NATIVE_CLIENT_LIB : BUILD_DEBUG_NATIVE_CLIENT_LIB);
        } else {
            System.loadLibrary(OGRE_NEXT_3_0 ? OGRE_NEXT_3_0_PATH + BUILD_RELEASE_NATIVE_CLIENT_LIB : BUILD_RELEASE_NATIVE_CLIENT_LIB);
        }
//        System.loadLibrary("RenderSystem_GLES3_d");
//        System.loadLibrary("Plugin_ParticleFX_d");
    }

    private static final boolean FULLSCREEN = false;
    private static final boolean WAIT_FOR_RENDERDOC = false;

    private static final int[][][] resolutions = {
            // 4:3
            {{480, 320}, {640, 480}, {800, 600}, {1024, 768}, {1600, 1200}},
            // 16:9
            {{1280, 720}, {1920, 1080}, {2560, 1440}, {3840, 2160}},
            // 16:10
            {{640, 400}, {1280, 800}, {1440, 900}, {1680, 1050}, {1920, 1200}, {2560,1600}, {3840, 2400}}
    };

    public static void main(String[] arg) {
        MainApp.PLATFORM = MainApp.Platform.DESKTOP;
        MainApp.DESKTOP_PLATFORM = MainApp.DesktopPlatform.WIN32;
        test();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Hotshot";
//		cfg.useGL20 = true;
        int[] resolution = resolutions[1][0];
        cfg.width = FULLSCREEN ?
                (int) width :
                resolution[0];
        cfg.height = FULLSCREEN ?
                (int) height :
                resolution[1];
        cfg.fullscreen = FULLSCREEN;
//        cfg.addIcon("raw/ic_launcher_128.png", Files.FileType.Local);
//        cfg.addIcon("raw/ic_launcher_32.png", Files.FileType.Local);
//        cfg.addIcon("raw/ic_launcher_16.png", Files.FileType.Local);
        cfg.resizable = false;
        cfg.foregroundFPS = (int) ENG_MainThread.MAXIMUM_FRAME_RATE;
        if (WAIT_FOR_RENDERDOC) {
            synchronized (Thread.currentThread()) {
                try {
                    Thread.currentThread().wait(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//        cfg.useGL30 = true;
//		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        ApplicationStartSettings applicationStartSettings = new ApplicationStartSettings();
        applicationStartSettings.uncaughtExceptionHandler = new ENG_DefaultUncaughtExceptionHandler();
        applicationStartSettings.applicationMode = MainApp.Mode.CLIENT;
        applicationStartSettings.screenWidth = cfg.width;
        applicationStartSettings.screenHeight = cfg.height;
        new LwjglApplication(new BlackholeDarksunMain(arg, applicationStartSettings), cfg);
    }

    public static native void test();
}
