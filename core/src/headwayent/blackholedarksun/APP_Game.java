/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/18/23, 4:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
//import com.badlogic.gdx.backends.iosrobovm.DefaultIOSInput;
//import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
//import com.badlogic.gdx.backends.iosrobovm.IOSScreenBounds;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMath;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import headwayent.blackholedarksun.animations.PortalEnteringWithoutRenderingAnimation;
import headwayent.blackholedarksun.animations.PortalExitingWithoutRenderingAnimation;
import headwayent.blackholedarksun.compositor.SceneCompositor;
import headwayent.blackholedarksun.entitydata.AsteroidData;
import headwayent.blackholedarksun.entitydata.DebrisData;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.StaticEntityData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.exception.GameDataException;
import headwayent.blackholedarksun.gamestatedebugger.Frame;
import headwayent.blackholedarksun.input.KeyBindings;
import headwayent.blackholedarksun.loaders.LevelLoader;
import headwayent.blackholedarksun.loaders.MultiplayerMapCompiler;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewMenuManager;
import headwayent.blackholedarksun.net.GameResourceUpdateChecker;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.Map;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.osspecific.IOS;
import headwayent.blackholedarksun.physics.EntityContactListener;
import headwayent.blackholedarksun.physics.EntityInternalTickCallback;
import headwayent.blackholedarksun.physics.InvisibleWallsManager;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_CrashDataManager;
import headwayent.hotshotengine.ENG_DateUtils;
import headwayent.hotshotengine.ENG_GameDescription;
import headwayent.hotshotengine.ENG_IMainThreadSequentialWorker;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.ENG_MainThread;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.SharedPreferences;
import headwayent.hotshotengine.SharedPreferencesImpl;
import headwayent.hotshotengine.android.AndroidRenderWindow;
import headwayent.hotshotengine.audio.ENG_ISoundRoot;
import headwayent.hotshotengine.audio.ENG_ISoundRoot.PlayType;
import headwayent.hotshotengine.exception.ENG_InvalidPathException;
import headwayent.hotshotengine.gorillagui.ENG_SilverBack;
import headwayent.hotshotengine.gui.ENG_GUIOverlayManager;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.input.ENG_InputConvertor;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.input.ENG_TouchListener;
import headwayent.hotshotengine.networking.ENG_NetUtility;
import headwayent.hotshotengine.renderer.ENG_BillboardSet;
import headwayent.hotshotengine.renderer.ENG_CompositorManager;
import headwayent.hotshotengine.renderer.ENG_FontManager;
import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgramManager;
import headwayent.hotshotengine.renderer.ENG_MeshManager;
import headwayent.hotshotengine.renderer.ENG_ParticleSystemFactory;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_SkeletonManager;
import headwayent.hotshotengine.renderer.ENG_TextAreaOverlayElement;
import headwayent.hotshotengine.renderer.ENG_TextureManager;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_CompositorWorkspaceNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallWithRepeatRendering;
import headwayent.hotshotengine.renderer.opengles.glsl.GLSLLinkProgramManager;
import headwayent.hotshotengine.resource.ENG_ResourceLoaderBlock;
import headwayent.hotshotengine.statedebugger.ENG_Frame;
import headwayent.hotshotengine.util.ENG_Decompress;
import headwayent.hotshotengine.vfs.ENG_FileUtils;
import headwayent.microedition.rms.InvalidRecordIDException;
import headwayent.microedition.rms.RecordEnumeration;
import headwayent.microedition.rms.RecordStore;
import headwayent.microedition.rms.RecordStoreException;
import headwayent.microedition.rms.RecordStoreFullException;
import headwayent.microedition.rms.RecordStoreNotFoundException;

public abstract class APP_Game extends ENG_GameDescription {

    public static final boolean REDIRECT_PRINTLN_OUTPUT = false;
    public static final boolean NOTCH_SIMULATE = true;
    public static final boolean GAME_RESOURCE_UPDATE_CHECKER_ENABLED = true;
    protected HudManager hudManager;
    protected SimpleViewMenuManager simpleViewMenuManager;
    protected NetManager netManager;
    protected ClientAPI clientAPI;
    protected ENG_RenderRoot renderRoot;
    protected ENG_GUIOverlayManager guiOverlayManager;
    protected ENG_ContainerManager containerManager;
    protected ENG_InputManager inputManager;
    protected SimpleViewGameMenuManager simpleViewGameMenuManager;
    /** @noinspection FieldCanBeLocal*/
    private btDbvtBroadphase btDbvtBroadphase;
    /** @noinspection FieldCanBeLocal*/
    private btDefaultCollisionConfiguration btDefaultCollisionConfiguration;
    /** @noinspection FieldCanBeLocal*/
    private btCollisionDispatcher btCollisionDispatcher;
    /** @noinspection FieldCanBeLocal*/
    private btSequentialImpulseConstraintSolver btSequentialImpulseConstraintSolver;
    protected com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld btDiscreteDynamicsWorld;
    private boolean showingLoadingScreen;
//    protected WorldManager worldManager;
    protected ENG_CompositorWorkspaceNativeWrapper compositorWorkspace;
    private EntityContactListener entityContactListener;
    private double notchHeight;
    private boolean inGamePaused;
    private final ReentrantLock keyboardVisibleLock = new ReentrantLock();
    private boolean keyboardVisible;

    protected static void redirectPrintlnOutput() {
        if (REDIRECT_PRINTLN_OUTPUT) {
            // Delete the old ones first.
            ArrayList<FileHandle> printlnOutputsList = ENG_FileUtils.getRedirectOutputFiles(true);
            // How many of the newer ones to leave alone?
            int size = printlnOutputsList.size() - 10;
            for (int i = 0; i < size; ++i) {
                FileHandle file = printlnOutputsList.get(i);
                boolean b = file.delete();
                System.out.println("println output file: " + file.name() + " has been deleted with result: " + b);
            }

            String timeStamp = ENG_DateUtils.getCurrentDateTimestamp();
            if (ENG_Utility.isConsolePrintWriterOverwritten()) {
                ENG_Utility.restorePrintlnOut();
            }
            ENG_Utility.setPrintlnOut("println_output_" + timeStamp + ".txt");

            System.out.println("native redirected output file creation error: " +
                    BlackholeDarksunMain.getNativeRedirectOutputResult());
        }
    }

    public static void copyToLocalFolder(FileHandle internal) {
        FileHandle rawFolder = Gdx.files.local(FOLDER_RAW + File.separator);
//        if (rawFolder.exists()) {
//            boolean rawFileDeleted = rawFolder.delete();
//            System.out.println("Raw dir was a file and it was deleted: " + rawFileDeleted);
//            if (!rawFileDeleted) {
//                boolean rawFolderDeleted = rawFolder.deleteDirectory();
//                System.out.println("Raw dir was a folder and was deleted: " + rawFolderDeleted);
//            }
//        }
        if (!rawFolder.exists()) {
            rawFolder.mkdirs();
        }
        for (FileHandle fileHandle : internal.list()) {
            if (fileHandle.path().startsWith("/")) {
                fileHandle = Gdx.files.internal(fileHandle.path().substring(1));
            }


//            if (!fileHandle.isDirectory()) {
//            fileHandle = Gdx.files.internal("2.0");
                try {
                    fileHandle.copyTo(Gdx.files.local(FOLDER_RAW + File.separator/* + fileHandle.name()*/));
                } catch (GdxRuntimeException e) {
                    e.printStackTrace();
                }
//            }
        }
    }

    protected void uploadUnsentCrashData() {
        if (!preferences.isLastShutdownSuccessful()) {
            ENG_CrashDataManager.getSingleton().uploadUnsentCrashData();
        }
    }

    protected void loadLevelList() {
        singlePlayerLevelList = LevelLoader.loadLevelList("level_list.txt", getGameResourcesDir());

        multiPlayerLevelList = LevelLoader.loadLevelList("multiplayer_level_list.txt", getGameResourcesDir());
    }

    protected void createMeshMappings() {
        headwayent.blackholedarksun.entitydata.ShipData.MapWithFilenameAndName mappings = headwayent.blackholedarksun.entitydata.ShipData.createShipMappings();
//            filenameShipMap = mappings.filenameShipMap;
        nameShipMap = mappings.nameShipMap;
        WeaponData.createWeaponMappings();
        asteroidDataMap = AsteroidData.createAsteroidMappings();
        debrisDataMap = DebrisData.createDebrisMappings();
        staticEntityDataMap = StaticEntityData.createDebrisMappings();
    }

    public TreeMap<String, ShipData> getNameShipMap() {
        return nameShipMap;
    }

    public enum GameMode {
        SP, MP
    }

    public enum GameState {
        NONE,
        ESSENTIAL_RAW_DATA_LOADED,
        LOADING_SCREEN_SHOWN,
        RAW_DATA_LOADED,
        GAME_DATA_DOWNLOADED,
        LOADED_MATERIAL_DATA,
        ALL_RESOURCES_LOADED,
        ALL_MULTIPLAYER_RESOURCES_LOADED
    }

    public static final String SYSTEMS_BUNDLE_SP = "SP_Systems";
    public static final String SYSTEMS_BUNDLE_MP = "MP_Systems";

    public static final String IN_GAME_INPUT_STACK = "inGameInputStack";
    public static final String TO_IN_GAME_LISTENER = "toInGameListener";
    public static final String TOUCH_INPUT_STACK = "touchInputStack";
    public static final String TO_GUI_LISTENER = "toGUIListener";
    public static final int GORILLA_DEJAVU_SMALL = 9;
    public static final int GORILLA_DEJAVU_MEDIUM = 14;
    public static final int GORILLA_DEJAVU_LARGE = 24;
    public static final String GORILLA_FONT = "dejavu";
    public static final String GORRILA_NINE_PATCH_BUTTON = "nine_patch_test";
    public static final String GORILLA_DEJAVU_NOT_PRESSED = "menu_button_not_pressed";
    public static final String GORILLA_DEJAVU_PRESSED = "menu_button_pressed";
    public static final String BASE_IP = //"http://188.25.39.240";
                                         "http://headwayentertainment.net";
    public static final String APP_SERVER_IP = "http://188.26.10.46";
    public static final String URL_ADDRESS = BASE_IP + ":80";
    private static final String URL_GAMEDATA = MainApp.TEST_DATA ?
            URL_ADDRESS + "/hotshot_gamedata.zip" :
            URL_ADDRESS + "/hotshot_gamedata.zip";
//                    "http://referendum-online.ro/hotshot_gamedata_test.zip" :
//                    "http://referendum-online.ro/hotshot_gamedata.zip";

    public static final String URL_GAMEDATA_UPDATE = URL_ADDRESS + "/" +
            "gamedata" + "/" + "hotshot_gamedata_update.zip";

    private static final String URL_GAMEDATA_VER = MainApp.TEST_DATA ?
            URL_ADDRESS + "/hotshot_gamedata_ver.txt" :
            URL_ADDRESS + "/hotshot_gamedata_ver.txt";
//                    "http://referendum-online.ro/hotshot_gamedata_ver_test.txt" :
//                    "http://referendum-online.ro/hotshot_gamedata_ver.txt";
    public static final String FOLDER_COMPANY = "HeadwayEntertainment";
    public static final String FOLDER_GAME = "Hotshot";
    public static final String FOLDER_RAW = "raw";
    public static final String GAMEDATA_DIR = FOLDER_COMPANY + "/" + FOLDER_GAME + "/" + FOLDER_RAW;

    // public static final boolean DEMO = false;
    public static final int MAX_LEVEL = MainApp.DEMO ? 5 : 9;
    public static final int ASTEROID_SOUND_NUM = 1;
    public static final int DEBRIS_HIT_SOUND_NUM = 1;
    public static final String SCENE_MANAGER = "Main";
    public static final String MAIN_CAM = "MainCam";
    protected static final double MOVEMENT_SYSTEM_INTERVAL = ENG_MainThread.UPDATE_INTERVAL;
    protected static final double COLLISION_DETECTION_SYSTEM_INTERVAL = ENG_MainThread.UPDATE_INTERVAL;
    public static final double AI_SYSTEM_INTERVAL = ENG_MainThread.UPDATE_INTERVAL * 2;
    public static final String SHARED_PREFERENCES_FILENAME = "pref.txt";

    public static final boolean AUTOMATION_ENABLED = false;

    public enum MultiplayerGameInitializationEnum {
        NONE, CREATE_SESSION, JOIN_SESSION
    }

    public static final MultiplayerGameInitializationEnum MULTIPLAYER_GAME_INITIALIZATION_ENUM = MultiplayerGameInitializationEnum.NONE;
//    public static final MultiplayerGameInitializationEnum MULTIPLAYER_GAME_INITIALIZATION_ENUM = MultiplayerGameInitializationEnum.CREATE_SESSION;
//    public static final MultiplayerGameInitializationEnum MULTIPLAYER_GAME_INITIALIZATION_ENUM = MultiplayerGameInitializationEnum.JOIN_SESSION;

    public static final boolean DATA_IN_APK = true;
    public static final boolean DATA_IN_APK_COMPRESSED = false;
    public static final boolean CLASSIC_MULTIPLAYER_MENUS = false;
    public static String[] levelTitleList;/*
                                     * = { "Night patrol", "Retribution",
									 * "Cargo inspection", "Escape!", "Atack",
									 * "Escort", "Asteroid field", "War"};
									 */
    public static String[] multiplayerLevelTitleList;
    public static String[] missionBriefingList;// = {
    // "", "mis2", "mis3", "mis4", "mis5", "mis6", "mis7", "mis8"};
//    public static final TreeMap<Integer, ArrayList<String>> levelShipSelectionList = new TreeMap<Integer, ArrayList<String>>();
//    public static final TreeMap<Integer, ArrayList<ArrayList<String>>> multiplayerLevelShipSelectionList = new TreeMap<>();

    public static final String[] skyboxList = {"skybox0", "skybox1", "skybox2", "skybox3", "skybox4", "skybox5", "skybox6", "skybox7"};

    public static final int MULTIPLAYER_MAX_PLAYER_NUM = 8;

    private static final float MAX_INCLINATION_ANGLE_X = 20.0f;
    private static final float MAX_INCLINATION_ANGLE_Y = 20.0f;
    private boolean mainMenuBackgroundCreated;
    /** @noinspection UnstableApiUsage*/
    protected EventBus eventBus;
    private boolean mapListLoadedFromServer;
    protected Preferences preferences;
    private Gson gson;
    private boolean mapListLoadedLocal;
    protected ENG_Frame currentFrame;
    protected ArrayList<String> singlePlayerLevelList;
    protected ArrayList<String> multiPlayerLevelList;
//    private final HashMap<String, ENG_ModelResource> loaderShipMap = new HashMap<>();
//    private final HashMap<String, ENG_ModelResource> loaderWeaponMap = new HashMap<>();
//    private final HashMap<String, ENG_ModelResource> loaderSkyboxMap = new HashMap<>();
//    private final HashMap<String, ENG_ModelResource> loaderAsteroidMap = new HashMap<>();
//    private final HashMap<String, ENG_ModelResource> loaderMiscMap = new HashMap<>();
//    private final EnumMap<headwayent.blackholedarksun.entitydata.WeaponData.WeaponType, ENG_ModelResource> loaderWeaponTypeMap = new EnumMap<>(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.class);
//    private final HashMap<String, ENG_ModelResource> loaderMap = new HashMap<>();

    public static String getAsteroidExplosionSoundName(int num) {
        switch (num) {
            case 0:
                return "asteroid_explosion0";
        }
        throw new IllegalArgumentException(num + " is an invalid asteroid explosion sound num");
    }

    public static String getDebrisHitSoundName(int num) {
        switch (num) {
            case 0:
                return "debris_hit0";
        }
        throw new IllegalArgumentException(num + " is an invalid debris hit sound num");
    }

    protected ENG_RenderWindow renderWindow;
    private ENG_SceneNode node;
    private float angle;
    private ENG_InputConvertor touchInputConverter;
    /** @noinspection deprecation*/
    private ENG_BillboardSet set;
    private ENG_TextAreaOverlayElement fpsIndicator;
    protected GameWorld world;// = new GameWorld();
    protected ReentrantLock mainThreadCreationLock = new ReentrantLock();
//    protected TreeMap<String, headwayent.blackholedarksun.entitydata.ShipData> filenameShipMap;
    protected TreeMap<String, headwayent.blackholedarksun.entitydata.ShipData> nameShipMap;
    protected HashMap<String, AsteroidData> asteroidDataMap;
    protected HashMap<String, DebrisData> debrisDataMap;
    protected HashMap<String, StaticEntityData> staticEntityDataMap;
    protected ENG_ISoundRoot soundRoot;
    private final AtomicBoolean accelerometerEnabled = new AtomicBoolean();
    private boolean soundsEnabled;
    private final ReentrantLock soundsEnabledLock = new ReentrantLock();
    protected boolean loadingScreenLoaded;
    private final AtomicBoolean exiting = new AtomicBoolean();
    protected ENG_TouchListener touchListener;
    private String nextStartMenuName;
//    private SensorManagerSimulator sensorManager;
//    protected ENG_AccelerometerInput accelerometerInput;
    private int maxLevelReached;
    private final ReentrantLock maxLevelReachedLock = new ReentrantLock();
    private final CountDownLatch accelerometerInputCreated = new CountDownLatch(1);
//    private InputConvertorToMovement inputConvertorToMovement;
    private boolean accelerometerListenersRegistered;
    private final ReentrantLock registerAcceleration = new ReentrantLock();
    private final AtomicBoolean invertYAxis = new AtomicBoolean();
    private final ReentrantLock exitLock = new ReentrantLock();
//    private final ReentrantLock gameDataLock = new ReentrantLock();
    private boolean gameDataAvailable;
    private final AtomicBoolean gameDataChecked = new AtomicBoolean();
    // We need this to make sure the first time we enter the game
    // we don't go into an infinte loop when exiting the no sd card
    // menu
    private final AtomicBoolean firstTimeGameChecked = new AtomicBoolean();
    private final AtomicBoolean activityCreatedGameChecked = new AtomicBoolean();
/** @noinspection FieldCanBeLocal*/ //    private int gameDataVersion;
//    private String previousMenu;
//    protected boolean reenableDemo;
    protected ENG_SceneNode rootSceneNode;
    protected KeyBindings keyBindings;
    private final AtomicBoolean thirdPersonCamera = new AtomicBoolean();
    private final AtomicBoolean vibration = new AtomicBoolean();
    private final AtomicBoolean aimAssist = new AtomicBoolean();

    private SharedPreferences sharedPreferences;
    private boolean localDirAvailable;

    private final HashMap<Long, String> mapMap = new HashMap<>();
    private final TreeSet<Long> mapIds = new TreeSet<>();
    private final HashMap<String, Long> mapNameToIdsMap = new HashMap<>();
    private final HashMap<Long, Long> mapIdToMapPosInTitleList = new HashMap<>();
    private GameState gameState = GameState.NONE;

    protected User user;

    private GameMode gameMode = GameMode.SP;

    private final ENG_ResourceLoaderBlock resourceLoaderBlock = new ENG_ResourceLoaderBlock();

    private boolean skipMainThread;

    public enum WorldManagerMode {
        SINGLEPLAYER, MULTIPLAYER, MULTIPLAYER_SERVER_SIDE
    }

    private WorldManagerMode worldManagerMode;
    private boolean destroyPreviousShipSelection;

    private static final CountDownLatch viewDidLoadLatch = new CountDownLatch(1);

    public APP_Game() {
        createSharedPreferencesFile();
        initializeGson();
        InGameStatisticsManager statisticsManager = InGameStatisticsManager.getInstance();
        statisticsManager.setGson(gson); // Must set gson before initializing.
        statisticsManager.init();
        preferences = new Preferences(getSharedPreferences(), getGson());
        ENG_Log log = ENG_Log.getInstance();
        log.setWriteToConsole(MainActivity.isDebugmode());
        log.setWriteToSystemOut(true);
        log.setLogActive(true);
        log.log("Welcome to HE!", ENG_Log.TYPE_MESSAGE);
    }

    /**
     * This is running on the iOS main thread. Not our own application main thread ENG_MainThread.
     */
    public static class LoadRenderer implements ENG_IMainThreadSequentialWorker {

        @Override
        public void run() {
            IOS.waitForMetalRenderSystemToLoad(viewDidLoadLatch);
        }
    }

    public static void notifyViewDidLoad() {
        viewDidLoadLatch.countDown();
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public WorldManagerMode getWorldManagerMode() {
        return worldManagerMode;
    }

    public void setWorldManagerMode(WorldManagerMode worldManagerMode) {
        this.worldManagerMode = worldManagerMode;
    }

    protected void checkLocalDirAvailable() {
        localDirAvailable = ENG_FileUtils.isLocalStorageAvailable();
        if (!localDirAvailable) {
            throw new ENG_InvalidPathException("Could not find local dir");
        }
    }

    public boolean isLocalDirAvailable() {
        return localDirAvailable;
    }

    @Override
    public String getGameDir() {
        return "";//FOLDER_RAW;//Gdx.files.getLocalStoragePath();//"";
//		return Environment.getExternalStorageDirectory() + File.separator 
//				+ FOLDER_COMPANY
//				+ File.separator + FOLDER_GAME + File.separator;
    }

    public String getGameResourcesRootDir() {
        return FOLDER_RAW;
    }

    @Override
    public String getGameResourcesDir() {
        return /*Gdx.files.getLocalStoragePath() + */ // MainApp.PLATFORM == MainApp.Platform.DESKTOP ?
                FOLDER_RAW + File.separator + "hotshot_gamedata";/* + File.separator + FOLDER_RAW*/ // : FOLDER_RAW;// + File.separator;
//		return Environment.getExternalStorageDirectory() + 
//				File.separator + GAMEDATA_DIR + File.separator;
    }

    public void setActivityCreatedGameChecked(boolean b) {
        activityCreatedGameChecked.set(b);
    }

    private boolean isActivityCreatedGameChecked() {
        return activityCreatedGameChecked.get();
    }

    private void setFirstTimeGameChecked(boolean b) {
        firstTimeGameChecked.set(b);
    }

    private boolean isFirstTimeGameChecked() {
        return firstTimeGameChecked.get();
    }

    private void setGameDataChecked(boolean b) {
        gameDataChecked.set(b);
    }

    private boolean isGameDataChecked() {
        return gameDataChecked.get();
    }

    public void setAccelerometerInputCreated() {
        accelerometerInputCreated.countDown();
    }

//	public void goToMainMenu() {
//		WorldManager.getSingleton().resetCurrentBundle();
//		MainApp.getMainThread().runOnMainThread(new ENG_IRunOnMainThread() {
//
//			@Override
//			public void run() {
//
//				MenuManager.getSingleton().showMenuOverlay("main_menu");
//				createMainMenuBackgroundDemo();
//			}
//		});
//
//	}

    public void setExiting() {
        exiting.set(true);
    }

    public void resetExiting() {
        exiting.set(false);
    }

    public boolean isExiting() {
        return exiting.get();
    }

    /**
     * When entering SimpleGUI from an old menu overlay and press the
     * home button we need to open the previous menu
     *
     * @param menuName
     */
//    public void setPreviousMenuName(String menuName) {
//        previousMenu = menuName;
//    }

    /**
     * For when the current SimpleGUI disables the demo show and
     * needs it reenabled for the previous menu overlay after pressing
     * the home button.
     *
     * @param b
     */
//    public void reenableDemo(boolean b) {
//        reenableDemo = b;
//    }

    /** @noinspection deprecation*/
//    public void clearAllManagers() {
//
//        ENG_CompositorManager.getSingleton().destroy(true);
//        GLSLLinkProgramManager.getSingleton().destroyAllLinkedPrograms(true);
//        ENG_HighLevelGpuProgramManager.getSingleton().destroyAllHighLevelGpuPrograms(true);
//
//        ENG_TextureManager.getSingleton().destroyAllTextures(true);
//        ENG_FontManager.getSingleton().destroyAllFonts();
//
//        ENG_SkeletonManager.getSingleton().destroyAllSkeletons();
//
//        ENG_MeshManager.getSingleton().destroyAllMeshes(true);
//
//        // Since the particle systems have gl buffers we must get rid of them
//        // here
//        // and everyone should recreate them after the reload
//        ENG_RenderRoot.getRenderRoot().getSceneManager(SCENE_MANAGER).destroyAllMovableObjectsByType(ENG_ParticleSystemFactory.FACTORY_TYPE_NAME, true);
//
//        // All containers must be recreated in initMenus() in GameMenuManager
//        // since they already get rendered and will be invalid when we try
//        // to show the loading screen.
//        ENG_ContainerManager.getSingleton().destroyAllContainers(true, true);
//
//        ENG_SilverBack.getSingleton().unloadAllAtlases();
//
//        SimpleViewMenuManager.getSingleton().removeAllMenus();
//    }

    private boolean checkGameDataAvailable() throws GameDataException {
        // Check if we already have data available. If we do then check if the version is the same with the one on the server.
        // If we cannot reach the server then we can still play singleplayer but not multiplayer.
        // Else redownload the data if the data from server is newer.
        boolean dataInSD = checkDataInSD();
        // Check if newer version exists.
        DownloadVersion downloadVersion = checkLatestDownloadedVersion();
        if (downloadVersion.shouldRedownload && downloadVersion.gameDataVersion != -1) {
            boolean downloadData = downloadData();
            if (downloadData) {
                preferences.setDataDownloaded(true);
                preferences.setDownloadedDataVersion(downloadVersion.gameDataVersion);
                preferences.setMultiplayerAllowed(true);
            } else {
                if (dataInSD) {
                    // We already have an older version. We can still play singleplayer.
                    preferences.setMultiplayerAllowed(false);
                } else {
                    // We're fucked. We must quit the game.
                    return false;
                }
            }
            return true;
        } else {
            // We could not reach the server. Check if local data available.
            if (dataInSD) {
                preferences.setMultiplayerAllowed(false);
                return true;
            } else {
                // We're fucked. We must quit the game.
                return false;
            }
        }
    }

    public boolean downloadData(String URL, String location, String filename, boolean overwrite) {
        boolean ok = true;
        // set the path where we want to save the file
        // in this case, going to save it on the root directory of the
        // sd card.
        checkLocalDirAvailable();
//        FileHandle SDCardRoot = Gdx.files.local(FOLDER_RAW);//Environment.getExternalStorageDirectory();
        // create a new file, specifying the path, and the filename
        // which we want to save the file as.
        GetGameDataLocation getGameDataLocation = new GetGameDataLocation(location, filename).invoke();
        FileHandle loc = getGameDataLocation.getLoc();
        File file = getGameDataLocation.getFile();
//        if (!file.exists() || redownload) {
		/*	if (file.getParentFile().listFiles() != null) {
				for (File f : file.getParentFile().listFiles()) {
					f.delete();
				}
			}*/
        if (!overwrite) {
            loc.emptyDirectory();
//			file.getParentFile().mkdirs();
            loc.mkdirs();
        }
        System.out.println("Downloading: " + file);
        System.out.println("Downloading game data...");
        ok = ENG_NetUtility.downloadFromServer(URL, file, (downloadedSize, totalSize) -> System.out.println("Downloading game data percentage done: " +
                ((int) ENG_Math.floor((float) downloadedSize / (float) totalSize * 100.0f))));

//            redownload = false;
//        }

        // if (!) {
        // ok = false;
        // return ok;
        // }

	/*	MainActivity.getInstance().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(MainActivity.getInstance(),
						"Unpacking data. Please wait...", Toast.LENGTH_LONG).show();
			}
		});*/

        boolean unzip = false;
        if (ok) {
            unzip = new ENG_Decompress(file.getAbsolutePath(), loc.file() + File.separator).unzip();
        }
        // file.delete();
        return unzip;
    }

    private boolean downloadData() {
        return downloadData(URL_GAMEDATA, FOLDER_RAW, (DATA_IN_APK_COMPRESSED ? "hotshot_gamedata.zip" : "hotshot_gamedata"), false);
    }

    public boolean unpackLocalData() {
        if (preferences.isDataUnpacked()) {
            gameDataAvailable = true;
        } else {
            GetGameDataLocation getGameDataLocation = new GetGameDataLocation().invoke();
            FileHandle loc = getGameDataLocation.getLoc();
            File file = getGameDataLocation.getFile();
            boolean unzip = new ENG_Decompress(file.getAbsolutePath(), loc.file() + "/").unzip();
            preferences.setMultiplayerAllowed(false);
            preferences.setDataUnpacked(unzip);
            gameDataAvailable = unzip;
        }
        return gameDataAvailable;
    }

//    private boolean downloadFromServer(File file) {
//	/*	MainActivity.getInstance().runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				Toast.makeText(MainActivity.getInstance(),
//						"Donwloading data. Please wait...", Toast.LENGTH_LONG).show();
//			}
//		});*/
//        boolean ok = true;
//        try {
//            HttpURLConnection urlConnection = ENG_NetUtility
//                    .getHttpURLConnection(URL_GAMEDATA);
//
//            // this will be used to write the downloaded data into the file we
//            // created
//            FileOutputStream fileOutput = new FileOutputStream(file);
//
//            // this will be used in reading the data from the internet
//            InputStream inputStream = urlConnection.getInputStream();
//
//            // this is the total size of the file
//            int totalSize = urlConnection.getContentLength();
//            // variable to store total downloaded bytes
//            int downloadedSize = 0;
//
//            // create a buffer...
//            byte[] buffer = new byte[1024 * 1024 * 2];
//            int bufferLength; // used to store a temporary size of the
//            // buffer
//
//            // now, read through the input buffer and write the contents to the
//            // file
//            while ((bufferLength = inputStream.read(buffer)) > 0) {
//                // add the data in the buffer to the file in the file output
//                // stream (the file on the sd card
//                fileOutput.write(buffer, 0, bufferLength);
//                // add up the size so we know how much is downloaded
//                downloadedSize += bufferLength;
//                // this is where you would do something to report the prgress,
//                // like this maybe
//                updateProgress(downloadedSize, totalSize);
//
//            }
//            // close the output stream when done
//            fileOutput.close();
//
//            // catch some possible errors...
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            ok = false;
//        } catch (Throwable e) {
//            e.printStackTrace();
//            ok = false;
//        }
//        // see
//        // http://androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
//        return ok;
//    }
//
//    private void updateProgress(int downloadedSize, int totalSize) {
//
//
//    }

    private boolean checkDataInSD() {
//        boolean dataAvailable;
	/*	File sdDir = new File(Environment.getExternalStorageDirectory()
				.getPath());
		// Current state of the external media
		String extState = Environment.getExternalStorageState();

		// External media can be written onto
		if (Environment.MEDIA_MOUNTED.equals(extState)) {
			File testDir = new File(sdDir.getAbsolutePath() + "/"
					+ FOLDER_COMPANY + "/" + FOLDER_GAME + "/" + FOLDER_RAW);
			dataAvailable = testDir.exists();
		} else {
			throw new ENG_DataStorageNotFoundException();
		}*/
//        dataAvailable = ENG_FileUtils.getFile(FOLDER_RAW).exists();
//        return dataAvailable;
        return preferences.isDataDownloaded();
    }

    private static class DownloadVersion {
        public final int gameDataVersion;
        public final boolean shouldRedownload;

        public DownloadVersion(int gameDataVersion, boolean shouldRedownload) {
            this.gameDataVersion = gameDataVersion;
            this.shouldRedownload = shouldRedownload;
        }
    }

    private DownloadVersion checkLatestDownloadedVersion() {
        boolean shouldRedownload = false;
        int version = -1;
        int latestVersion = getLatestVersion();
        int localVersion = preferences.getDownloadedDataVersion();
        if (latestVersion != -1 && latestVersion != localVersion) {

            version = latestVersion;
            shouldRedownload = true;
        }
        return new DownloadVersion(version, shouldRedownload);
    }

    private int getLatestVersion() {
        int ver = -1;
        try {
            HttpURLConnection urlConnection = ENG_NetUtility.getHttpURLConnection(URL_GAMEDATA_VER);
            urlConnection.setConnectTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine = in.readLine().trim();
            ver = Integer.parseInt(inputLine);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ver;
    }

    protected void loadShipOptions() {
        RecordStore rsShipOptions = null;
        try {
            rsShipOptions = RecordStore.openRecordStore(
                    //MainActivity.getInstance(),
                    "shipoptions", false);
        } catch (RecordStoreFullException e1) {

            e1.printStackTrace();
        } catch (RecordStoreException e1) {

            e1.printStackTrace();
        } catch (RecordStoreNotFoundException e1) {

            setSoundsEnabled(true);
            setVibrationEnabled(true);
            setAimAssistEnabled(getPreferences().isAimAssistEnabled());
            // this.setAccelerometerOn(true);

            // If no options selected then save screen orientation based on
            // current
            // screen orientation. Don't do it in the constructor. Do it when
            // set
            // this.setOrientationIndex(-1);
            setAccelerometerEnabled(false);
        }
        if (rsShipOptions != null) {
            RecordEnumeration recShipOptions = null;
            recShipOptions = rsShipOptions.enumerateRecords(null, null,
                    true);

            if (recShipOptions != null && recShipOptions.hasNextElement()) {
                byte[] b = null;
                try {
                    b = recShipOptions.nextRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (b != null) {
                    int index = b[0];
                    int soundsOn = b[1];
                    int accelOn = b[2];
                    int orientationIndex = b[3];
                    int invertYAxis = b[4];
                    int thirdPersonCam = b[5];
                    int vibrate = b[6];
                    // this.setShipOptionsIndex(index);
                    // this.setOrientationIndex(orientationIndex);
                    setSoundsEnabled(soundsOn == 1);
                    setAccelerometerEnabled(accelOn == 1);
                    setInvertYAxis(invertYAxis == 1);
                    setThirdPersonCamera(thirdPersonCam == 1);
                    setVibrationEnabled(vibrate == 1);
                    setAimAssistEnabled(getPreferences().isAimAssistEnabled());

                }
                recShipOptions.destroy();
            }
            try {
                rsShipOptions.closeRecordStore();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    protected void loadMaxLevelNum() {
        maxLevelReachedLock.lock();
        try {
            RecordStore rsLevelReached = null;
            try {
                rsLevelReached = RecordStore.openRecordStore(
                        //MainActivity.getInstance(),
                        "levelreached", true);
            } catch (RecordStoreFullException e2) {

                e2.printStackTrace();
            } catch (RecordStoreException e2) {

                e2.printStackTrace();
            } catch (RecordStoreNotFoundException e2) {

                e2.printStackTrace();
            }
            if (rsLevelReached != null) {
                RecordEnumeration levelReachedEnum = rsLevelReached
                        .enumerateRecords(null, null, false);
                if (levelReachedEnum.hasNextElement()) {
                    try {
                        byte[] b = levelReachedEnum.nextRecord();
                        maxLevelReached = b[0];
                    } catch (InvalidRecordIDException e) {

                        e.printStackTrace();
                    }
                } else {
                    maxLevelReached = 9; // Never played so we are on maximum of level 1.
                }
            }
        } finally {
            maxLevelReachedLock.unlock();
        }
    }

    public int getMaxLevelReached() {
        maxLevelReachedLock.lock();
        try {
            return maxLevelReached;
        } finally {
            maxLevelReachedLock.unlock();
        }
    }

    public void setMaxLevelReached(int maxLevel) {
        maxLevelReachedLock.lock();
        try {
            maxLevelReached = maxLevel;
        } finally {
            maxLevelReachedLock.unlock();
        }
    }

//    private static void addArrayToHashMap(ArrayList<ENG_ModelResource> list, HashMap<String, ENG_ModelResource> map) {
//        for (ENG_ModelResource res : list) {
//            map.put(res.name, res);
//        }
//    }
//
//    private static void addMapToMap(java.util.Map<String, ENG_ModelResource> src, java.util.Map<String, ENG_ModelResource> dest) {
//        for (java.util.Map.Entry<String, ENG_ModelResource> entry : src.entrySet()) {
//            ENG_ModelResource put = dest.put(entry.getKey(), entry.getValue());
//            if (put != null) {
//                throw new IllegalArgumentException("Multiple resources with name " + entry.getKey());
//            }
//        }
//    }

    @Deprecated
//    protected void loadResourceLists() {
//        ENG_ModelCompiler modelCompiler = new ENG_ModelCompiler();
//        ArrayList<ENG_ModelResource> loaderShipList = modelCompiler.compile("loader_ship_list.txt", getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
//        ArrayList<ENG_ModelResource> loaderWeaponList = modelCompiler.compile("loader_weapon_list.txt", getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
//        ArrayList<ENG_ModelResource> loaderSkyboxList = modelCompiler.compile("loader_skybox_list.txt", getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
//        ArrayList<ENG_ModelResource> loaderAsteroidList = modelCompiler.compile("loader_asteroid_list.txt", getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
//        ArrayList<ENG_ModelResource> loaderMiscList = modelCompiler.compile("loader_misc_list.txt", getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
//        addArrayToHashMap(loaderShipList, loaderShipMap);
//        addArrayToHashMap(loaderWeaponList, loaderWeaponMap);
//        addArrayToHashMap(loaderSkyboxList, loaderSkyboxMap);
//        addArrayToHashMap(loaderAsteroidList, loaderAsteroidMap);
//        addArrayToHashMap(loaderMiscList, loaderMiscMap);
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.LASER_GREEN, getWeaponResource("laser_green"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.LASER_GREEN_QUAD, getWeaponResource("laser_green"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.LASER_RED, getWeaponResource("laser_red"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.LASER_RED_QUAD, getWeaponResource("laser_red"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.CONCUSSION, getWeaponResource("missile_concssn"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.HOMING, getWeaponResource("missile_homing"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.PIRANHA, getWeaponResource("missile_piranha"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.MEGA, getWeaponResource("missile_mega"));
//        loaderWeaponTypeMap.put(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.PLASMA, getWeaponResource("plasma"));
//        addMapToMap(loaderShipMap, loaderMap);
//        addMapToMap(loaderWeaponMap, loaderMap);
//        addMapToMap(loaderSkyboxMap, loaderMap);
//        addMapToMap(loaderAsteroidMap, loaderMap);
//        addMapToMap(loaderMiscMap, loaderMap);
//    }

    public void uploadUnsentStacktraces() {
//        final DatabaseConnection databaseConnection = DatabaseConnection.getConnection();
//        databaseConnection.createConnection();
//        final ArrayList<DatabaseConnection.ExceptionsSelectResult> exceptionsNotSent = databaseConnection.getExceptionsNotSent();
//        if (!exceptionsNotSent.isEmpty()) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (DatabaseConnection.ExceptionsSelectResult result : exceptionsNotSent) {
//                        ENG_NetUtility.sendStacktracePost(ENG_UncaughtExceptionHandler.CRASH_REPORT_URL, null, result.exception, result.id);
//                    }
//                    databaseConnection.closeConnection();
//                }
//            }).start();
//        } else {
//            databaseConnection.closeConnection();
//        }
    }

    protected void initializeGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gson = gsonBuilder.create();
    }

    protected void readMapList() {
        readMapList(false);
    }

    /** @noinspection UnstableApiUsage */
    protected void readMapList(boolean forceLocal) {
        if (!forceLocal) {
            if (ENG_NetUtility.isNetworkAvailable()) {
                EventBus bus = getEventBus();
                bus.post(new ClientAPI.GetMapListEvent());
            }
        }
        // In the mean time check if we have some local list
        List<Map> mapList = getPreferences().getMapList();
        if (mapList == null) {
            mapList = new MultiplayerMapCompiler().compile("multiplayer_maps_ids.txt",
                    getGameResourcesDir(), true);
        }
        mapListLoadedLocal = true;
        extractMapMap(mapList);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onMapListLoaded(ClientAPI.MapListLoadedEvent event) {
        mapListLoadedFromServer = true;
        extractMapMap(event.mapList);
        getPreferences().setMapList(event.mapList);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onMapListLoadError(ClientAPI.MapListLoadErrorEvent event) {

    }

    private void extractMapMap(List<Map> mapList) {
        for (Map map : mapList) {
            mapMap.put(map.getId(), map.getMapName());
            mapIds.add(map.getId());
            mapNameToIdsMap.put(map.getMapName(), map.getId());
            mapIdToMapPosInTitleList.put(map.getId(), map.getLocalId());
        }
//        for (int i = 0; i < APP_Game.multiplayerLevelTitleList.length; ++i) {
//            Long id = mapNameToIdsMap.get(APP_Game.multiplayerLevelTitleList[i]);
//            mapIdToMapPosInTitleList.put(id, i);
//        }
    }

    protected void createSharedPreferencesFile() {
        File sharedPrefsFile = ENG_FileUtils.getFile(getGameDir() + File.separator + SHARED_PREFERENCES_FILENAME, true);//local.file();//new File(local.file().getAbsolutePath());
//			sharedPrefsFile.mkdir();
        if (!sharedPrefsFile.exists()) {
            try {
                boolean newFile = sharedPrefsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sharedPreferences = new SharedPreferencesImpl(sharedPrefsFile, 0);
    }

    public void createMainMenuBackgroundDemo() {
        if (mainMenuBackgroundCreated) {
            return;
        }
        WorldManager worldManager = WorldManager.getSingleton();
//		worldManager.setShowDemo(true);
//        boolean b = ENG_Utility.getRandom().nextBoolean("bla bla");
//        byte[] bytes = new byte[10];
//        for (int i = 0; i < bytes.length; ++i) {
//            bytes[i] = (byte) i;
//        }
//        ENG_Utility.getRandom().nextBytes("ala ala", bytes);
//        worldManager.createSkybox(skyboxList[ENG_Utility.getRandom().nextInt(FrameInterval.SKYBOX_RAND, skyboxList.length)]);
        SceneCompositor.getSingleton().setInGameCompositor("SkyboxWorkspace0");
        System.out.println("createMainBackgroundDemo() successful");
        mainMenuBackgroundCreated = true;
    }

    public boolean isMainMenuBackgroundCreated() {
        return mainMenuBackgroundCreated;
    }

    public void setMainMenuBackgroundCreated(boolean mainMenuBackgroundCreated) {
        this.mainMenuBackgroundCreated = mainMenuBackgroundCreated;
    }

    public void updateGameDataAvailable() {
//        gameDataLock.lock();
//        try {

        gameDataAvailable = checkGameDataAvailable();
//        } catch (GameDataException e) {
//            // Get out of the loading phase
//            gameDataAvailable = false;
//        } finally {
//            gameDataLock.unlock();
//        }

    }

//    public void registerAccelerationListeners() {
//        registerAcceleration.lock();
//        try {
//            ENG_InputManager inputManager = ENG_InputManager.getSingleton();
//            if (isAccelerometerEnabled() && !accelerometerListenersRegistered) {
//                accelerometerListenersRegistered = true;
//                accelerometerInput.registerListenersThread();
//                inputManager
//                        .registerInputConvertorListener(
//                                "toMovementListener",
//                                inputConvertorToMovement);
//            } else {
//            }
//        } finally {
//            registerAcceleration.unlock();
//        }
//    }

//    public void unregisterAccelerationListeners() {
//        registerAcceleration.lock();
//        try {
//            ENG_InputManager inputManager = ENG_InputManager.getSingleton();
//            if (isAccelerometerEnabled() && accelerometerListenersRegistered) {
//                accelerometerListenersRegistered = false;
////                accelerometerInput.unregisterListenersThread();
//                inputManager
//                        .unregisterInputConvertorListener(
//                                "toMovementListener"
//								/*inputConvertorToMovement*/);
//            }
//        } finally {
//            registerAcceleration.unlock();
//        }
//    }

    public void showLoadingScreen() {
//        renderRoot.setContinuousRendering(false);
//        ENG_RenderingThread.setAutomaticRenderOneFrameEnabled(true);
        ENG_SlowCallExecutor.execute(new ENG_SlowCallWithRepeatRendering() {
            @Override
            public long executeWithOptionalRendering() {
                setRepeatRendering(true);
                showLoadingScreenNative(((AndroidRenderWindow) renderWindow).getPointer(), ENG_RenderRoot.getRenderRoot().getScreenDensity());
                return 0;
            }
        });
        System.out.println("loading screen shown");
        setLoadingScreenShown(true);
//        renderRoot.requestRenderingIfRequired();
//        renderRoot.requestRenderingIfRequired();
//        if (showingLoadingScreen) {
//            return;
//        }
//        if (!loadingScreenLoaded) {
//            loadLoadingScreenResources();
//            loadingScreenLoaded = true;
//        }
//        ENG_Overlay loadingScreenOverlay = ENG_OverlayManager.getSingleton().getByName("loading_screen_overlay");
//        loadingScreenOverlay.show();
//        ENG_RenderRoot.getRenderRoot().renderOneFrame();
//        showingLoadingScreen = true;
    }

//    private void loadLoadingScreenResources() {
//        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
//        int width = window.getWidth();
//        int height = window.getHeight();
//        ENG_MaterialLoader.loadMaterial("material_loading_screen.txt", getGameResourcesDir(), true);
//        ENG_OverlayLoader.loadOverlay("overlay_loading_screen.txt", getGameResourcesDir(), true);
//        ENG_Overlay loadingScreenOverlay = ENG_OverlayManager.getSingleton().getByName("loading_screen_overlay");
//        ENG_OverlayContainer loadingScreen = loadingScreenOverlay.getChild("LoadingScreen");
//        loadingScreen.setMetricsMode(GuiMetricsMode.GMM_PIXELS);
//        ENG_Texture loadingScreenTexture = ENG_TextureManager.getSingleton().getByName(
//                ENG_MaterialManager.getSingleton().getByName("loading_screen_mat").getTechnique((short) 0).getPass((short) 0).getTextureUnitState(0).getTextureName());
//        float lsWidth = loadingScreenTexture.getWidth();
//        float lsHeight = loadingScreenTexture.getHeight();
//        loadingScreen.setLeft(width / 2 - lsWidth / 2);
//        loadingScreen.setTop(height / 2 - lsHeight / 2);
//        loadingScreen.setWidth(lsWidth);
//        loadingScreen.setHeight(lsHeight);
//
//    }

    public void hideLoadingScreen() {
//        ENG_RenderingThread.setAutomaticRenderOneFrameEnabled(false);
        ENG_SlowCallExecutor.execute(new ENG_SlowCallWithRepeatRendering() {
            @Override
            public long executeWithOptionalRendering() {
                hideLoadingScreenNative();
                setRepeatRendering(false);
                return 0;
            }
        });
        setLoadingScreenShown(false);
//        renderRoot.setContinuousRendering(true);
//        if (showingLoadingScreen) {
//            ENG_OverlayManager.getSingleton().getByName("loading_screen_overlay").hide();
//            showingLoadingScreen = false;
//        }
    }

//    public void destroyLoadingScreenResources(boolean skipGLDelete) {
//        ENG_OverlayManager.getSingleton().destroyOverlayAndChildren("loading_screen_overlay", skipGLDelete);
//    }

//    public headwayent.blackholedarksun.entitydata.ShipData getFilenameToShipMap(String filename) {
//        headwayent.blackholedarksun.entitydata.ShipData shipData = filenameShipMap.get(filename);
//        if (shipData == null) {
//            throw new IllegalArgumentException(filename
//                    + " is not a valid ship " + "filename");
//        }
//        return shipData;
//    }

    public headwayent.blackholedarksun.entitydata.ShipData getNameToShipMap(String shipName) {
        headwayent.blackholedarksun.entitydata.ShipData shipData = nameShipMap.get(shipName);
        if (shipData == null) {
            throw new IllegalArgumentException(shipName
                    + " is not a valid ship " + "name");
        }
        return shipData;
    }

//    public headwayent.blackholedarksun.entitydata.WeaponData getFilenameToWeapon(String filename) {
//        headwayent.blackholedarksun.entitydata.WeaponData weaponData = filenameWeaponMap.get(filename);
//        if (weaponData == null) {
//            throw new IllegalArgumentException(filename
//                    + " is not a valid weapon " + "filename");
//        }
//        return weaponData;
//    }
//
//    public headwayent.blackholedarksun.entitydata.WeaponData getNameToWeapon(String weaponName) {
//        headwayent.blackholedarksun.entitydata.WeaponData weaponData = nameWeaponMap.get(weaponName);
//        if (weaponData == null) {
//            throw new IllegalArgumentException(weaponName
//                    + " is not a valid weapon " + "name");
//        }
//        return weaponData;
//    }

    public AsteroidData getAsteroidData(String name) {
        AsteroidData asteroidData = asteroidDataMap.get(name);
        if (asteroidData == null) {
            throw new IllegalArgumentException(name + " is not a valid asteroid name");
        }
        return asteroidData;
    }

    public DebrisData getDebrisData(String name) {
        DebrisData debrisData = debrisDataMap.get(name);
        if (debrisData == null) {
            throw new IllegalArgumentException(name + " is not a valid debris name");
        }
        return debrisData;
    }

    public StaticEntityData getStaticEntityData(String name) {
        StaticEntityData staticEntityData = staticEntityDataMap.get(name);
        if (staticEntityData == null) {
            throw new IllegalArgumentException(name + " is not a valid static entity name");
        }
        return staticEntityData;
    }

    public ENG_ISoundRoot getSound() {
        return soundRoot;
    }

    public boolean isAccelerometerEnabled() {
        return accelerometerEnabled.get();
    }

    public void setAccelerometerEnabled(boolean accelerometerEnabled) {
        this.accelerometerEnabled.set(accelerometerEnabled);
    }

    /**
     * This should never be used if MINIAUDIO_3D is enabled.
     */
    @Deprecated
    public void playSoundMaxVolume(String name) {
//		soundRoot.setVolume(name, 100);
        soundRoot.playSound(name, PlayType.PLAY_ONCE);
    }

    public ENG_TouchListener getTouchListener() {
        return touchListener;
    }

    public String getNextStartMenuName() {
        return nextStartMenuName;
    }

    public void setNextStartMenuName(String nextStartMenuName) {
        this.nextStartMenuName = nextStartMenuName;
    }

//    /**
//     * @return the sensorManager
//     */
//    public SensorManagerSimulator getSensorManager() {
//        return sensorManager;
//    }
//
//    /**
//     * @param sensorManager the sensorManager to set
//     */
//    public void setSensorManager(SensorManagerSimulator sensorManager) {
//        this.sensorManager = sensorManager;
//    }

    /**
     * @return the accelerometerInput
     */
//    public ENG_AccelerometerInput getAccelerometerInput() {
//        return accelerometerInput;
//    }

//	public void setDisplay(Display display) {
//
//		this.display = display;
//	}
//
//	public Display getDisplay() {
//		return display;
//	}

	/*
	 * public void setAccelerometerInput() { ENG_InputManager inputManager = new
	 * ENG_InputManager(); accelerometerInput = (ENG_AccelerometerInput)
	 * ENG_InputManager.getSingleton().createInput( "accelerometerInput",
	 * ENG_AccelerometerInputFactory.TYPE); }
	 */

    public void incrementLevelReached() {
        maxLevelReachedLock.lock();
        try {
            RecordStore rsLevelReached = null;
            try {
                rsLevelReached = RecordStore.openRecordStore(
                        "levelreached", true);
            } catch (Exception e2) {

                e2.printStackTrace();
            }
            if (rsLevelReached != null && WorldManager.getSingleton().getCurrentLevel() + 1 == maxLevelReached
                    && maxLevelReached < MAX_LEVEL) {
                ++maxLevelReached;
                RecordEnumeration recEnum = null;
                recEnum = rsLevelReached.enumerateRecords(null, null, false);
                if (recEnum != null) {
                    while (recEnum.hasNextElement()) {
                        try {
                            rsLevelReached.deleteRecord(recEnum.nextRecordId());
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                    try {
                        rsLevelReached.addRecord(
                                new byte[]{(byte) maxLevelReached}, 0, 1);
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }
            if (rsLevelReached != null) {
                try {
                    rsLevelReached.closeRecordStore();
                } catch (Exception ex) {
                    Logger.getLogger(APP_Game.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            }
        } finally {
            maxLevelReachedLock.unlock();
        }
    }

    /**
     * @return the inputConvertorToMovement
     */
//    public InputConvertorToMovement getInputConvertorToMovement() {
//        return inputConvertorToMovement;
//    }

    /**
     * @return the soundsEnabled
     */
    public boolean isSoundsEnabled() {
        soundsEnabledLock.lock();
        try {
            return soundsEnabled;
        } finally {
            soundsEnabledLock.unlock();
        }
    }

    /**
     * @param soundsEnabled the soundsEnabled to set
     */
    public void setSoundsEnabled(boolean soundsEnabled) {
        soundsEnabledLock.lock();
        try {
            this.soundsEnabled = soundsEnabled;
            if (soundsEnabled) {
                getSound().enableSounds();
            } else {
                getSound().disableSounds();
            }
        } finally {
            soundsEnabledLock.unlock();
        }
    }

    public boolean isThirdPersonCamera() {
        return thirdPersonCamera.get();
    }

    public void setThirdPersonCamera(boolean b) {
        thirdPersonCamera.set(b);
    }

    public enum VibrationEvent {

        PLAYER_HIT(400, Input.VibrationStyle.CONTINUOUS),
        PLAYER_DEATH(2000, Input.VibrationStyle.CONTINUOUS),
        ADVANCE_WEAPON(150, Input.VibrationStyle.TRANSIENT),
        ADVANCE_SELECTION(150, Input.VibrationStyle.TRANSIENT),
        ATTACK_SELECTED_ENEMY(150, Input.VibrationStyle.TRANSIENT),
        DEFEND_PLAYER_SHIP(150, Input.VibrationStyle.TRANSIENT),
        PLAYER_FIRE_WEAPON(250, Input.VibrationStyle.CONTINUOUS),
        HOMING_BEEP(300, Input.VibrationStyle.CONTINUOUS),
        CARGO_SHIP_EXPLOSION(4000, Input.VibrationStyle.CONTINUOUS),
        PLAYER_PORTAL_OPENING((int) PortalEnteringWithoutRenderingAnimation.TOTAL_ANIM_TIME, Input.VibrationStyle.CONTINUOUS),
        PLAYER_PORTAL_EXITING((int) PortalExitingWithoutRenderingAnimation.TOTAL_ANIM_TIME, Input.VibrationStyle.CONTINUOUS);

        private final int duration;
        private final com.badlogic.gdx.Input.VibrationStyle vibrationStyle;

        VibrationEvent(int duration, com.badlogic.gdx.Input.VibrationStyle vibrationStyle) {
            this.duration = duration;
            this.vibrationStyle = vibrationStyle;
        }

        public int getDuration() {
            return duration;
        }

        public Input.VibrationStyle getVibrationStyle() {
            return vibrationStyle;
        }
    }

    public boolean isVibrationEnabled() {
        return vibration.get();
    }

    public void setVibrationEnabled(boolean enabled) {
        vibration.set(enabled);
    }

    public void vibrate(int millis) {
        vibrate(millis, Input.VibrationStyle.CONTINUOUS);
    }

    public void vibrate(int millis, Input.VibrationStyle vibrationStyle) {
        if (MainApp.Platform.isMobile() && vibration.get()) {
            Gdx.input.vibrate(millis, vibrationStyle);
        }
    }

    public void vibrate(VibrationEvent vibrationEvent) {
        vibrate(vibrationEvent.getDuration(), vibrationEvent.getVibrationStyle());
    }

//    public void vibrateStop() {
//        if (vibration.get()) {
//            Gdx.input.cancelVibrate();
//        }
//    }

    public boolean isAimAssistEnabled() {
        return aimAssist.get();
    }

    public void setAimAssistEnabled(boolean aimAssist) {
        this.aimAssist.set(aimAssist);
    }

    public boolean isInvertYAxis() {
        return invertYAxis.get();
    }

    public void setInvertYAxis(boolean b) {
        this.invertYAxis.set(b);
    }

    public void exitGame() {
        if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
            exitLock.lock();
            try {
                MainApp.getGame().setExiting();
                // Get rid of everything in the level and show the main menu
                // for next time when we come in
                Gdx.app.exit();
//			MainActivity.getInstance().finish();
            } finally {
                exitLock.unlock();
            }
        } else {
            throw new IllegalStateException("You are not supposed to close applications on mobile platforms");
        }
    }

    public boolean isGameDataAvailable() {
//        gameDataLock.lock();
//        try {
        return gameDataAvailable;
//        } finally {
//            gameDataLock.unlock();
//        }
    }

    public KeyBindings getKeyBindings() {
        return keyBindings;
    }

    public void setOnscreenKeyboardVisible(final boolean visible) {
        // TODO investigate why it crashes on iOS.
        // Ignore the NullPointerException for now.
//        try {
//            Gdx.input.setOnscreenKeyboardVisible(visible);
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }
        if (MainApp.PLATFORM == MainApp.Platform.IOS || MainApp.PLATFORM == MainApp.Platform.ANDROID) {
            keyboardVisibleLock.lock();
            try {
                if (keyboardVisible != visible) {
                    keyboardVisible = visible;
                    if (visible) {
//                        final DefaultIOSInput input = (DefaultIOSInput) Gdx.input;
//                        input.setOnScreenKeyboardCloser(() -> {
//                            setOnscreenKeyboardVisible(false);
//                            input.setOnScreenKeyboardCloser(null);
//                        });
                    }
                    BlackholeDarksunMain.main.addRenderQueueEvent(() -> setOnScreenKeyboardVisible(visible, AndroidRenderWindow.getUiViewController(), AndroidRenderWindow.getUiTextFieldDelegateAdapterHandle()));
                }
            } finally {
                keyboardVisibleLock.unlock();
            }
        }
    }

    /** @noinspection UnstableApiUsage*/
    public EventBus getEventBus() {
        return eventBus;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public String getMapNameById(long id) {
        if (!mapListLoadedFromServer && !mapListLoadedLocal) {
            throw new IllegalStateException("Map list not loaded!");
        }
        return mapMap.get(id);
    }

    public TreeSet<Long> getMapIds() {
        return new TreeSet<>(mapIds);
    }

    public HashMap<String, Long> getMapNameToIdsMap() {
        return mapNameToIdsMap;
    }

    public long getMapPosInTitleListByServerId(long id) {
        Long aLong = mapIdToMapPosInTitleList.get(id);
        return aLong != null ? aLong : 0;
    }

    public boolean isMapListLoadedFromServer() {
        return mapListLoadedFromServer;
    }

    public boolean isMapListLoadedLocal() {
        return mapListLoadedLocal;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    protected Gson getGson() {
        return gson;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        preferences.setUser(user);
    }

    public ENG_Frame getCurrentFrame() {
        return currentFrame;
    }

    public ArrayList<String> getSinglePlayerLevelList() {
        return singlePlayerLevelList;
    }

    public ArrayList<String> getMultiPlayerLevelList() {
        return multiPlayerLevelList;
    }

//    public ENG_ModelResource getShipResource(String name) {
//        ENG_ModelResource modelResource = loaderShipMap.get(name);
//        if (modelResource == null) {
//            throw new IllegalArgumentException(name + " not found");
//        }
//        return modelResource;
//    }
//
//    public ENG_ModelResource getWeaponResource(String name) {
//        ENG_ModelResource modelResource = loaderWeaponMap.get(name);
//        if (modelResource == null) {
//            throw new IllegalArgumentException(name + " not found");
//        }
//        return modelResource;
//    }
//
//    public ENG_ModelResource getWeaponResource(WeaponData.WeaponType type) {
//        ENG_ModelResource modelResource = loaderWeaponTypeMap.get(type);
//        if (modelResource == null) {
//            throw new IllegalArgumentException(type + " not found");
//        }
//        return modelResource;
//    }
//
//    public ENG_ModelResource getSkyboxResource(String name) {
//        ENG_ModelResource modelResource = loaderSkyboxMap.get(name);
//        if (modelResource == null) {
//            throw new IllegalArgumentException(name + " not found");
//        }
//        return modelResource;
//    }
//
//    public ENG_ModelResource getAsteroidResource(String name) {
//        ENG_ModelResource modelResource = loaderAsteroidMap.get(name);
//        if (modelResource == null) {
//            throw new IllegalArgumentException(name + " not found");
//        }
//        return modelResource;
//    }
//
//    public ENG_ModelResource getMiscResource(String name) {
//        ENG_ModelResource modelResource = loaderMiscMap.get(name);
//        if (modelResource == null) {
//            throw new IllegalArgumentException(name + " not found");
//        }
//        return modelResource;
//    }
//
//    public ENG_ModelResource getResource(String name) {
//        ENG_ModelResource modelResource = loaderMap.get(name);
//        if (modelResource == null) {
//            throw new IllegalArgumentException(name + " not found");
//        }
//        return modelResource;
//    }

    @Override
    public void preStartGameInit() {
        super.preStartGameInit();
        if (MainApp.getMainThread().isInputState()) {
            // Preinitialize class or we don't get to add the class to class map when we will need it to read and initialize object from gson.
            Frame.loadClass();
        }
        MainApp.getMainThread().resetThreadLocals();
    }

    public void checkReloadResources() {
        if (areResourcesCreated()) {
            // We need to reload all gl resources, because the android guys are smart...
//            MainApp.getGame().setGLThreadReadyForResourceReload();
//            reloadResources();
        }
    }

    private final static String customDesktopLib = "c:\\Sebi\\projects\\libgdx\\extensions\\gdx-bullet\\jni\\vs\\gdxBullet\\Debug\\gdxBullet.dll";
    private final static boolean debugBullet = false;


    protected void initPhysicsEngine() {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop && debugBullet) {
            System.load(customDesktopLib);
        } else {

        }
        Bullet.init(false, true);
        Gdx.app.log("Bullet", "Version = " + LinearMath.btGetVersion());

        Gdx.app.log("Bullet","test");
        btDbvtBroadphase = new btDbvtBroadphase();
        btDefaultCollisionConfiguration = new btDefaultCollisionConfiguration();
        btCollisionDispatcher = new btCollisionDispatcher(btDefaultCollisionConfiguration);
        btSequentialImpulseConstraintSolver = new btSequentialImpulseConstraintSolver();
        EntityInternalTickCallback internalTickCallback = new EntityInternalTickCallback();

        btDiscreteDynamicsWorld = new btDiscreteDynamicsWorld(
                btCollisionDispatcher, btDbvtBroadphase, btSequentialImpulseConstraintSolver, btDefaultCollisionConfiguration);
//        btDiscreteDynamicsWorld.setGravity(new Vector3(0.0f, 0.0f, 0.0f));

        internalTickCallback.attach(btDiscreteDynamicsWorld, false);
        // This does not work use attach() as above.
//        btDiscreteDynamicsWorld.setInternalTickCallback(internalTickCallback);

        InvisibleWallsManager wallsManager = InvisibleWallsManager.getSingleton();
        wallsManager.setWorld(btDiscreteDynamicsWorld);
        wallsManager.createWalls();

//        btDiscreteDynamicsWorld.getBroadphase().getOverlappingPairCache().setInternalGhostPairCallback(new btGhostPairCallback());

        entityContactListener = new EntityContactListener();
    }

    /** @noinspection deprecation*/
    public void initializeNotchHeight()  {
//        IOSApplication app = (IOSApplication) Gdx.app;
//
//        try {
//            Field lastScreenBoundsField = IOSApplication.class.getDeclaredField("lastScreenBounds");
//            Field uiAppField = IOSApplication.class.getDeclaredField("uiApp");
//            lastScreenBoundsField.setAccessible(true);
//            uiAppField.setAccessible(true);
//            IOSScreenBounds lastScreenBounds = (IOSScreenBounds) lastScreenBoundsField.get(app);
//            UIApplication uiApplication = (UIApplication) uiAppField.get(app);
//            NSArray<UIWindow> windows = uiApplication.getWindows();
//            UIWindow uiWindow = windows.get(0);
//            CGRect statusBarFrame = uiWindow.getWindowScene().getStatusBarManager().getStatusBarFrame();
////            Field x = IOSScreenBounds.class.getDeclaredField("x");
////            Field y = IOSScreenBounds.class.getDeclaredField("y");
////            Field width = IOSScreenBounds.class.getDeclaredField("width");
////            Field height = IOSScreenBounds.class.getDeclaredField("height");
////            Field backBufferWidth = IOSScreenBounds.class.getDeclaredField("backBufferWidth");
////            Field backBufferHeight = IOSScreenBounds.class.getDeclaredField("backBufferHeight");
//            notchHeight = lastScreenBounds.y;//(double) y.getInt(lastScreenBounds);
//            System.out.println("notchHeight: " + notchHeight);
//            System.out.println("screen bounds x: " + lastScreenBounds.x +
//                    " y: " + lastScreenBounds.y +
//                    " witdh: " + lastScreenBounds.width +
//                    " height: " + lastScreenBounds.height +
//                    " backbuffer width: " + lastScreenBounds.backBufferWidth +
//                    " backbuffer height: " + lastScreenBounds.backBufferHeight);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

    public double getNotchHeight() {
        return NOTCH_SIMULATE ? 100.0 : notchHeight;
    }

    protected void checkGameResourcesUpdateAvailable() {
        GameResourceUpdateChecker gameResourceUpdateChecker = GameResourceUpdateChecker.getInstance();
        if (gameResourceUpdateChecker.isGameResourceUpdateAvailable()) {
            int newGameResourceVersion = gameResourceUpdateChecker.getGameResourceVersion();
            if (newGameResourceVersion != -1) {
                int currentGameResourcesVersion = preferences.getGameResourcesVersion();
                if (newGameResourceVersion > currentGameResourcesVersion) {
                    // Make sure you overwrite the data in the hotshot_gamedata folder not in the
                    // base raw path.
                    boolean downloadData = downloadData(URL_GAMEDATA_UPDATE, getGameResourcesDir(),
                            "hotshot_gamedata_update.zip", true);
                    if (downloadData) {
                        preferences.setGameResourcesVersion(newGameResourceVersion);
                        loadLevelList();
                        createMeshMappings();
                        WorldManager.getSingleton().reloadLevelDataAndUpdateCurrentEntities();
                        HudManager.getSingleton().setGameResourcesCheckerStatus("Updated game resources", 1000);
                    }
                }
            } else {
                // Should never get here.
                System.out.println("newGameResourceVersion == -1 even when update available");
            }
        }
    }

    public abstract WorldManagerBase getWorldManager();

    public ENG_ResourceLoaderBlock getResourceLoaderBlock() {
        return resourceLoaderBlock;
    }

    public boolean isSkipMainThread() {
        return skipMainThread;
    }

    public void setSkipMainThread(boolean skipMainThread) {
        this.skipMainThread = skipMainThread;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public HudManager getHudManager() {
        return hudManager;
    }

    public SimpleViewMenuManager getSimpleViewMenuManager() {
        return simpleViewMenuManager;
    }

    public NetManager getNetManager() {
        return netManager;
    }

    public ClientAPI getClientAPI() {
        return clientAPI;
    }

    public ENG_RenderRoot getRenderRoot() {
        return renderRoot;
    }

    public ENG_GUIOverlayManager getGuiOverlayManager() {
        return guiOverlayManager;
    }

    public ENG_ContainerManager getContainerManager() {
        return containerManager;
    }

    public ENG_InputManager getInputManager() {
        return inputManager;
    }

    public SimpleViewGameMenuManager getSimpleViewGameMenuManager() {
        return simpleViewGameMenuManager;
    }

    public boolean isDestroyPreviousShipSelection() {
        return destroyPreviousShipSelection;
    }

    public void setDestroyPreviousShipSelection(boolean destroyPreviousShipSelection) {
        this.destroyPreviousShipSelection = destroyPreviousShipSelection;
    }

    private static class GetGameDataLocation {
        private FileHandle loc;
        private File file;
        private String location = /*FOLDER_COMPANY + File.separator + FOLDER_GAME + File.separator + */FOLDER_RAW;
        private String filename = location + File.separator + (DATA_IN_APK_COMPRESSED ? "hotshot_gamedata.zip" : "hotshot_gamedata");

        public GetGameDataLocation() {

        }

        public GetGameDataLocation(String location, String filename) {
            this.location = location;
            this.filename = location + File.separator + filename;
        }

        public FileHandle getLoc() {
            return loc;
        }

        public File getFile() {
            return file;
        }

        public GetGameDataLocation invoke() {
            loc = Gdx.files.local(location);
            file = ENG_FileUtils.getFile(filename);
            return this;
        }
    }

    public static void setupResources(final String resourceFilename) {
        ENG_SlowCallExecutor.execute(() -> {
            setupResources(getNativePath(), resourceFilename);
            return 0;
        });

    }

    public static String getNativePath() {
        return Gdx.files.getLocalStoragePath() + FOLDER_RAW;// + File.separator;
    }

    /**
     * Loading the normal resources takes quite a bit of time so instead of having
     * a black screen during the initial loading we will load the loading screen
     * and other essential resources first, show the loading screen, and then proceed
     * to loading the ton of high resolution texture maps.
     */
    public static void loadEssentialResources() {
        ENG_SlowCallExecutor.execute(() -> {
            loadEssentialResources(ENG_RenderRoot.getRenderRoot().getPointer(), getNativePath());
            return 0;
        });
    }

    public static void loadResources(final String resourceType) {
        ENG_SlowCallExecutor.execute(() -> {
            loadResources(ENG_RenderRoot.getRenderRoot().getPointer(), resourceType);
            return 0;
        });

    }

    public com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld getBtDiscreteDynamicsWorld() {
        return btDiscreteDynamicsWorld;
    }

    public EntityContactListener getEntityContactListener() {
        return entityContactListener;
    }

    public boolean isInGamePaused() {
        return inGamePaused;
    }

    public void setInGamePaused(boolean inGamePaused) {
        if (MainApp.getGame().getGameMode() == APP_Game.GameMode.MP) {
            this.inGamePaused = false; // Just to be sure
            return;
        }
        if (this.inGamePaused != inGamePaused) {
            if (inGamePaused) {
                MainApp.getMainThread().setPauseBeginTime();
            } else {
                MainApp.getMainThread().addPauseTimeMillis();
            }
        }
        this.inGamePaused = inGamePaused;

    }

    public static native void setupResources(String path, String filename);
    public static native void loadEssentialResources(long rootPtr, String path);
    public static native void loadResources(long rootPtr, String path);

    public static native void showLoadingScreenNative(long renderWindow, float screenDensity);
    public static native void hideLoadingScreenNative();

    public static native void setOnScreenKeyboardVisible(boolean visible, long gameViewController, long textDelegate);
}
