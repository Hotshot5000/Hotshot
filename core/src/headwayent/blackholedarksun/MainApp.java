/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/2/21, 10:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import headwayent.hotshotengine.ENG_MainThread;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainApp /*extends Application*/ {

    public enum GameType {
        DESKTOP(0), MOBILE(1), TEST(2), VR(3); // Allows everybody with everybody

        private final int gameType;

        GameType(int gameType) {
            this.gameType = gameType;
        }

        public int getGameType() {
            return gameType;
        }

        public static GameType getActiveGameType() {
            GameType gameType;
            if (DEV) {
                gameType = GameType.TEST;
            } else {
                switch (PLATFORM) {
                    case DESKTOP:
                    case HTML:
                        gameType = GameType.DESKTOP;
                        break;
                    case ANDROID:
                    case IOS:
                        gameType = GameType.MOBILE;
                        break;
                    case XROS:
                        gameType = GameType.VR;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid platform: " + PLATFORM);
                }
            }
            return gameType;
        }

        @Override
        public String toString() {
            switch (gameType) {
                case 0:
                    return "Desktop";
                case 1:
                    return "Mobile";
                case 2:
                    return "Test";
                default:
                    throw new IllegalArgumentException("Invalid gameType: " + gameType);
            }
        }
    }

    public enum Platform {
        DESKTOP(0), ANDROID(1), IOS(2), HTML(3), XROS(4);

        private final int id;

        Platform(int id) {
            this.id = id;
        }

        public static boolean isDesktop() {
            return PLATFORM.id == 0;
        }

        public static boolean isMobile() {
            return PLATFORM.id == 1 || PLATFORM.id == 2;
        }

        public static boolean isWeb() {
            return PLATFORM.id == 3;
        }

        public static boolean isVR() {
            return PLATFORM.id == 4;
        }

        @Override
        public String toString() {
            switch (id) {
                case 0:
                    return "desktop";
                case 1:
                    return "Android";
                case 2:
                    return "iOS";
                case 3:
                    return "HTML";
                case 4:
                    return "xrOS";
                default:
                    throw new IllegalArgumentException("Invalid id: " + id);
            }
        }
    }

    public enum DesktopPlatform {
        WIN32, LINUX, MACOS;

        public static boolean isWin32Desktop() {
            return PLATFORM == Platform.DESKTOP && DESKTOP_PLATFORM == WIN32;
        }

        public static boolean isLinuxDesktop() {
            return PLATFORM == Platform.DESKTOP && DESKTOP_PLATFORM == LINUX;
        }

        public static boolean isMacOSDesktop() {
            return PLATFORM == Platform.DESKTOP && DESKTOP_PLATFORM == MACOS;
        }
    }

    /**
     * This should be used to determine if we are running on a device,
     * not on the server PC. A client can have both SP and MP modes.
     * Use WorldManagerSP or WorldManagerMP to determine if we are running
     * a SP or MP game.
     */
    public enum Mode {
        CLIENT, SERVER
    }

    public enum Features {
        MULTIPLAYER(0x1),
        ALL(0xffffffff);

        private final int feature;

        Features(int feature) {
            this.feature = feature;
        }

        public boolean isFeatureEnabled(int features) {
            return (this.feature & features) != 0;
        }

        public static int createFeatures(Features... featuresList) {
            int features = 0;
            for (Features features1 : featuresList) {
                features |= features1.getFeature();
            }
            return features;
        }

        public static int getDisabled(Features features) {
            return ~features.getFeature();
        }

        public int getFeature() {
            return feature;
        }
    }

    // Change here when building for specific platform.
    // Cannot have same code on all platforms so the switch is necessary
    public static Platform PLATFORM;// = Platform.DESKTOP;
    public static Platform SUBPLATFORM;
    public static DesktopPlatform DESKTOP_PLATFORM;
    public static final int FEATURES_ENABLED = Features.createFeatures(Features.MULTIPLAYER);
    public static final boolean DEV = true;
    public static final boolean DEMO = false;
    public static final boolean LIBRARY = false;
    public static final boolean USE_NATIVE_OPTIMIZATIONS = false;
    public static final boolean TEST_DATA = false;
    public static final boolean GL_DEBUG = false;
    private static final boolean OUTPUT_DEBUGGING_APPLICATION_STATE = false;
    private static final AtomicBoolean fatalError = new AtomicBoolean();
//	private static final MainApp mainApp = new MainApp();

    private static APP_Game game;
    private static ENG_MainThread mainThread;
    // Gets overwritten if reading application state is enabled
    private static boolean outputDebuggingApplicationStateEnabled = OUTPUT_DEBUGGING_APPLICATION_STATE;
    private static Mode applicationMode = Mode.CLIENT;

    public static APP_Game getGame() {
        return game;
    }

    public static void setGame(APP_Game game) {
        MainApp.game = (game);
    }

    public static ENG_MainThread getMainThread() {
        return mainThread;
    }

    public static void setMainThread(ENG_MainThread mainThread) {
        MainApp.mainThread = (mainThread);
    }

    /**
     * Only for the server to be able to restart it if something goes wrong.
     */
    public static void resetFatalError() {
        fatalError.set(false);
    }

    public static void setFatalError() {
        fatalError.set(true);
    }

    public static boolean getFatalError() {
        return fatalError.get();
    }

    public static boolean isOutputDebuggingApplicationStateEnabled() {
        return outputDebuggingApplicationStateEnabled;
    }

    public static void setOutputDebuggingApplicationStateEnabled(boolean outputDebuggingApplicationStateEnabled) {
        MainApp.outputDebuggingApplicationStateEnabled = (outputDebuggingApplicationStateEnabled);
    }

    public static Mode getApplicationMode() {
        return applicationMode;
    }

    public static void setApplicationMode(Mode applicationMode) {
        MainApp.applicationMode = (applicationMode);
    }


}
