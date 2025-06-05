/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import headwayent.blackholedarksun.gamestatedebugger.Frame;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.osspecific.IOS;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.statistics.SessionStatistics;
import headwayent.hotshotengine.ApplicationStartSettings;
import headwayent.hotshotengine.ENG_DateUtils;
import headwayent.hotshotengine.ENG_MainThread;
import headwayent.hotshotengine.android.AndroidRenderWindow;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;
import headwayent.hotshotengine.renderer.nativeinterface.test.TestRenderingThread;

//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import com.badlogic.gdx.Gdx;

import java.io.File;
/*import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;*/
//import com.google.ads.AdRequest;
//import com.google.ads.AdSize;
//import com.google.ads.AdView;

public class MainActivity {

    /**
     * Called when the activity is first created.
     */
    private static final boolean debugMode = MainApp.DEV;
    private static final boolean SDCard = true;
    private static final boolean IGNORE_ARGS = true;
    private APP_Game game;
    private ENG_MainThread mainThread;
    private boolean fatalError;

    /** @noinspection deprecation */
    public void onCreate(String[] args, ApplicationStartSettings settings) {
        boolean inputApplicationDebuggingState;
        String inputStatePathAndFilename;
        CheckInputParameters checkInputParameters = new CheckInputParameters(args, IGNORE_ARGS).invoke();
        inputStatePathAndFilename = checkInputParameters.getInputStatePathAndFilename();
        inputApplicationDebuggingState = checkInputParameters.isInputApplicationDebuggingState();
        boolean ignoreArgs = checkInputParameters.isIgnoreArgs();
        if (Gdx.graphics != null) {
            boolean gl30Available = Gdx.graphics.isGL30Available();
            System.out.println("gl30Available: " + gl30Available);
            // On Android we have the width and height only after application init so we write it now.
            if (MainApp.Platform.isMobile()) {
                settings.screenWidth = Gdx.graphics.getWidth();
                settings.screenHeight = Gdx.graphics.getHeight();
            }
        }
        game = MainApp.getGame();
        System.out.println("onCreate started!");

        if (game == null) {
            initializeGame(settings);


            System.out.println("game created");
        } else {
            // Throw away the current game object and recreate it.
            if (!MainApp.getGame().areResourcesCreated()) {
//                MainApp.resetFatalError();
                initializeGame(settings);
            }
        }

        if (MainApp.getMainThread() == null) {
            ENG_RenderingThread.initialize();

            if (TestRenderingThread.TEST) {
                Gdx.graphics.setContinuousRendering(true);
                initializeMainThread(settings, inputApplicationDebuggingState, inputStatePathAndFilename, ignoreArgs);
                TestRenderingThread testRenderingThread = new TestRenderingThread();
                testRenderingThread.start();
            } else {
                initializeMainThread(settings, inputApplicationDebuggingState, inputStatePathAndFilename, ignoreArgs);
                if (MainApp.PLATFORM == MainApp.Platform.IOS) {
                    APP_SinglePlayerGame.LoadRenderer loadRenderer = new APP_Game.LoadRenderer();
                    loadRenderer.run();
                }
                if (settings.applicationMode == MainApp.Mode.CLIENT) {
                    Gdx.graphics.setContinuousRendering(false);
                }

                mainThread.start();
            }
        } else {
            // This is the resume
            if (!MainApp.getGame().areResourcesCreated()) {
                MainApp.resetFatalError();
                initializeMainThread(settings, inputApplicationDebuggingState, inputStatePathAndFilename, ignoreArgs);
            } else {
                MainApp.getGame().checkReloadResources();
            }
        }

        // Never gets called on desktop so must call here
        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
            onResume();


        }

    }

    private void initializeMainThread(ApplicationStartSettings settings,
                                      boolean inputApplicationDebuggingState,
                                      String inputStatePathAndFilename,
                                      boolean ignoreArgs) {
        mainThread = new ENG_MainThread();
        if (settings != null) {
            mainThread.setApplicationSettings(settings);
        }
        mainThread.setInputStateFile(inputStatePathAndFilename);
        if (inputApplicationDebuggingState && !ignoreArgs) {
            mainThread.setInputState(true);
        }
        MainApp.setMainThread(mainThread);
        MainApp.getMainThread().setGameDesc(game);
        mainThread.setOutputDebuggingStateEnabled(MainApp.isOutputDebuggingApplicationStateEnabled());
        mainThread.registerFrameFactory(new Frame.GameFrameFactory());
        mainThread.registerFrameIntervalFactory(new FrameInterval.GameFrameIntervalFactory());
    }

    private void initializeGame(ApplicationStartSettings settings) {
        switch (settings.applicationMode) {
            case CLIENT:
                game = new APP_SinglePlayerGame();
//                    game = new APP_MultiPlayerGame();
                break;
            case SERVER:
                game = new APP_MultiPlayerGame();
                break;
            default:
                throw new IllegalArgumentException();
        }
        MainApp.setApplicationMode(settings.applicationMode);
        MainApp.setGame(game);
        game.setOutputDebuggingStatePathAndFilename(APP_Game.FOLDER_COMPANY +
                File.separator + APP_Game.FOLDER_GAME +
                File.separator + settings.outputDebuggingStateDefaultFilename);
    }

    public void onResume() {
//                super.onResume();
        if (SDCard) {
//                	game.gameActivate();
            if (MainApp.PLATFORM == MainApp.Platform.ANDROID) {
//                GLMainThread.getSingleton().onSurfaceCreated();
                MainApp.getGame().checkReloadResources();
                // No need to reset thread locals since it's the same thread.
//                MainApp.getMainThread().resetThreadLocals();
            }
            MainApp.getGame().setGameActivated(true);
        } else {
//                	finish();
        }
        //	GLRenderSurface.getSingleton().requestRender();
        long uiViewController = AndroidRenderWindow.getUiViewController();
        if (uiViewController != 0) {
            IOS.resumeNative(uiViewController);
        }
        MainApp.getGame().getPreferences().setLastShutdownSuccessful(false);
        System.out.println("setLastShutdownSuccessful false");
    }

    public void onPause() {
        if (SDCard) {
//                game.gameDeactivate();
            MainApp.getGame().setGameActivated(false);
        }
//                super.onPause();
        long uiViewController = AndroidRenderWindow.getUiViewController();
        if (uiViewController != 0) {
            IOS.pauseNative(uiViewController);
        }
        InGameStatisticsManager statisticsManager = InGameStatisticsManager.getInstance();
        InGameStatistics statistics = statisticsManager.getInGameStatistics();
        SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
        if (latestSessionStatistics != null) {
            latestSessionStatistics.sessionEndDate = ENG_DateUtils.getCurrentDateTimestamp();
        }
        statisticsManager.saveCurrentStatistics();
        MainApp.getGame().getPreferences().setLastShutdownSuccessful(true);
        System.out.println("setLastShutdownSuccessful true");

    }

    public void onDestroy() {
//                super.onDestroy();
        game.endGame();
    }

    public static boolean isDebugmode() {
        return debugMode;
    }

    public APP_Game getGame() {
        return game;
    }

    public static native void pauseNative(long uiViewController);
    public static native void resumeNative(long uiViewController);


    public static class CheckInputParameters {
        private boolean ignoreArgs;
        private final String[] args;
        private boolean inputApplicationDebuggingState;
        private String inputStatePathAndFilename;

        public CheckInputParameters(String[] args) {
            this(args, true);
        }

        public CheckInputParameters(String[] args, boolean ignoreArgs) {
            this.args = args;
            this.ignoreArgs = ignoreArgs;
        }

        public boolean isInputApplicationDebuggingState() {
            return inputApplicationDebuggingState;
        }

        public String getInputStatePathAndFilename() {
            return inputStatePathAndFilename;
        }

        public boolean isIgnoreArgs() {
            return ignoreArgs;
        }

        public CheckInputParameters invoke() {
            if (/*!ignoreArgs && */args != null && args.length != 0) {
                for (int i = 0; i < args.length; ++i) {
                    switch (args[i]) {
                        case "inputstate":
                            if (i + 1 >= args.length) {
                                throw new IllegalArgumentException("Missing path and filename");
                            }
                            inputApplicationDebuggingState = true;
                            inputStatePathAndFilename = args[i + 1];
                            break;
                        case "ignore_args":
                            if (i + 1 >= args.length) {
                                throw new IllegalArgumentException("Missing value for ignore args. true or false");
                            }
                            switch (args[i + 1]) {
                                case "true":
                                    ignoreArgs = true;
                                    break;
                                case "false":
                                    ignoreArgs = false;
                                    break;
                                default:
                                    throw new IllegalArgumentException(args[i + 1]);
                            }
                            break;
                        default:
                            // Ignore for now
                    }
                }
            }
            return this;
        }
    }
}