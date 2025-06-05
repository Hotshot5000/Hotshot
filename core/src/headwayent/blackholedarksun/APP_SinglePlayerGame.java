/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/31/22, 1:22 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.artemis.Entity;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.GroupManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.TeamManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.common.eventbus.EventBus;

import headwayent.blackholedarksun.automationframework.MultiPlayerCreateSessionWithFriendsAutomation;
import headwayent.blackholedarksun.automationframework.MultiPlayerJoinSessionWithFriendsAutomation;
import headwayent.blackholedarksun.automationframework.SinglePlayerMenuAutomation;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.compositor.SceneCompositor;
import headwayent.blackholedarksun.input.InGameInputConvertor;
import headwayent.blackholedarksun.input.InGameInputConvertorFactory;
import headwayent.blackholedarksun.input.InGameInputConvertorListener;
import headwayent.blackholedarksun.input.KeyBindings;
import headwayent.blackholedarksun.loaders.BriefingLoader;
import headwayent.blackholedarksun.menus.*;
import headwayent.blackholedarksun.menus.language.Language;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.*;
import headwayent.blackholedarksun.multiplayer.systems.*;
import headwayent.blackholedarksun.net.GameResourceUpdateChecker;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.systems.*;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.world.WorldManagerSP;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.android.AndroidRenderWindow;
import headwayent.hotshotengine.audio.ENG_Sound;
import headwayent.hotshotengine.db.DatabaseConnection;
import headwayent.hotshotengine.gorillagui.ENG_SilverBack;
import headwayent.hotshotengine.gorillagui.ENG_TextureAtlas;
import headwayent.hotshotengine.gui.ENG_GUIOverlayManager;
import headwayent.hotshotengine.gui.ENG_InputConvertorToGUIListener;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.input.*;
import headwayent.hotshotengine.networking.ENG_NetUtility;
import headwayent.hotshotengine.renderer.*;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_ViewportNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;
import headwayent.hotshotengine.renderer.plugins.particlefx.ParticleFXPlugin;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;
import headwayent.hotshotengine.statedebugger.ENG_Frame;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by sebas on 27.10.2015.
 */
public class APP_SinglePlayerGame extends APP_Game {

//    private MultiplayerEntityProcessingClientSystem multiplayerEntityProcessingClientSystem;

    private static final boolean FORCE_DATA_COPY = true;
    public static final int CAMERA_FOVY = 59; // iphone has a 19.5:9 iphone ratio but we use standard 90 for fovy.

    private WorldConfiguration spWorldConfiguration;
    private WorldConfiguration mpWorldConfiguration;
//    private GameWorld spGameWorld;
//    private GameWorld mpGameWorld;

    private GameMode clientGameMode;
    private WorldManager worldManager;
    private LoadingScreen loadingScreenContainer;
    private ENG_SceneNode floatingCameraNode;

    public APP_SinglePlayerGame() {
        setWorldManagerMode(WorldManagerMode.SINGLEPLAYER);
    }

    /** @noinspection deprecation*/
    @Override
    public void reloadResources() {

    }

    public boolean gameStart(ENG_Frame currentFrame) {

//		int capacity = ByteBuffer.allocateDirect(4).asFloatBuffer().capacity();

        checkLocalDirAvailable();

        final ENG_MainThread mainThread = MainApp.getMainThread();

        initPhysicsEngine();

//        if (!isLoadingScreenShown()) {

//        setSkipMainThread(true);

        mainThread.addWorker(new LoadEssentialRawData());

        mainThread.addWorker(new ShowLoadingScreen());

        mainThread.addWorker(new LoadResourcesPart0());
        mainThread.addWorker(new LoadResourcesPart1());
        mainThread.addWorker(new LoadResourcesPart2());
        mainThread.addWorker(new LoadResourcesPart3());
        mainThread.addWorker(new LoadResourcesPart4());
        mainThread.addWorker(new LoadResourcesPart5());
        mainThread.addWorker(new LoadResourcesPart6());
        mainThread.addWorker(new LoadResourcesPart7());
        mainThread.addWorker(new LoadResourcesPart8());
        mainThread.addWorker(new LoadResourcesPart9());
        mainThread.addWorker(new LoadResourcesPart10());
        mainThread.addWorker(new LoadResourcesPart11());

        mainThread.addWorker(new LoadRawData());

        Language.getSingleton().loadCurrentLanguage();

//        mainThread.addWorker(new ENG_IMainThreadSequentialWorker() {
//            @Override
//            public void run() {
//                ENG_TextureAtlas.setBasePath(getGameResourcesDir());
//                ENG_SilverBack.getSingleton().loadAtlas(GORILLA_FONT, getGameResourcesDir());
//                // We need the container manager if we have a fatal error in order to display it, well before the rest of the data has been loaded.
//                ENG_ContainerManager containerManager = new ENG_ContainerManager();
//            }
//        });


//            setLoadingScreenShown(true);
//            return false;

//        } else {
//            setLoadingScreenShown(false);
//        }

//        ENG_RenderRoot renderRoot = ENG_RenderRoot.getRenderRoot();
//        final ENG_SceneManager sceneManager = renderRoot.getSceneManager(SCENE_MANAGER);
//        final ENG_Camera camera = sceneManager.getCamera(MAIN_CAM);

        if (MainApp.PLATFORM == MainApp.Platform.ANDROID/* || MainApp.PLATFORM == MainApp.Platform.IOS*/) {
            mainThread.addWorker(new UpdateGameDataAvailable());

            mainThread.addWorker(new CheckGameDataAvailable(mainThread));

        }
//		setGameDataChecked(true);
//		setFirstTimeGameChecked(true);
//		if (!isGameDataAvailable()) {
//			return;
//		}

        mainThread.addWorker(new LoadLevelResources());

        mainThread.addWorker(new LoadRestOfResources());

        return true;
    }

    public void gameEnd() {
        // No longer make any GL calls or any gets from MainApp
//        getSound().disposeOfAllSounds();
//        getEventBus().unregister(this);
    }

    // TODO Work needs to be done in syncing threads around here somewhere
    // On faster cpus the game doesn't start and something is wrong with the
    // glthread
    public void gameActivate() {
        // Debug.startMethodTracing("calc");
        super.setGameActive(true);
        ENG_Log.getInstance().setLogActive(true);
		/*
		 * if (!isFirstTimeGameChecked() && isGameDataChecked() &&
		 * !isGameDataAvailable()) { updateGameDataAvailable(); } if
		 * (isFirstTimeGameChecked()) { setFirstTimeGameChecked(false); }
		 */
        if (ENG_InputManager.getSingleton() != null) {
//			registerAccelerationListeners();
        }
        // Make sure we have inited the countDownLatch before we attempt
        // to resume the thread to avoid NullPointerException
//        createResourceRecreatedCountDownLatch();
//        System.out.println("resuming GLRenderSurface");
//		GLRenderSurface.getSingleton().onResume();
        // If we have any resources loaded into the glThread we must wait for
        // them
        // to reload
        // But it was written thou shall not block the UI thread or you will
        // face the wrath of gods!

        // So move it in the resuming main thread part

        ENG_MainThread mainThread = MainApp.getMainThread();
        mainThread.resumeGame();
//        mainThreadCreationLock.lock();
//        try {
//            if (!mainThread.isAlive()) {
//                System.out.println("Main thread started");
//                // No more separate thread for game logic
////				mainThread.start();
//            }
//        } finally {
//            mainThreadCreationLock.unlock();
//        }

        if (WorldManager.getSingleton() != null
                && WorldManager.getSingleton().getLevelState() == WorldManagerBase.LevelState.PAUSED) {
            WorldManager.getSingleton().setLevelState(WorldManagerBase.LevelState.STARTED);

        }
//        if (accelerometerInput == null) {
            // setAccelerometerInput();
            // setAccelerometerInputCreated();
//        }

    }

    /** @noinspection deprecation*/
    public void gameDeactivate() {
        if (!isGameActive()) {
            return; // Already deactivated when we exited the game
        }
        super.setGameActive(false);
        if (!MainApp.getMainThread().getShouldStop()) {
            MainApp.getMainThread().pauseGame();
            // Here we pause the game but we still may have frames in the
            // pipeline
            // being rendered by the glThread
//            mainThreadCreationLock.lock();
//            try {
//                if (MainApp.getMainThread().isAlive()) {
//                    MainApp.getMainThread().waitForMainThreadPause();
//                }
//            } finally {
//                mainThreadCreationLock.unlock();
//            }
            System.out.println("pauseing GLRenderSurface");
        } else {
            // Wait for the main thread to exit
//            MainApp.getMainThread().waitForMainThreadToExitMainLoop();
        }
//		GLRenderSurface.getSingleton().onPause();
        // Debug.stopMethodTracing();

        // Update the world state if started
        if (WorldManager.getSingleton() != null
                && WorldManager.getSingleton().getLevelState() == WorldManagerBase.LevelState.STARTED) {
            WorldManager.getSingleton().setLevelState(WorldManagerBase.LevelState.PAUSED);

        }

        if (isExiting()) {
            // ALL THIS IS DONE WRONGLY ON THE UI THREAD INSTEAD OF THE GL THREAD!!!!!! MOVE!
            if (areResourcesCreated()) {
                WorldManager.getSingleton().stopLevel();
            }
            // Resetting the level does not destroy the nodes so clear the scene

            ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(SCENE_MANAGER);
            sceneManager.clearScene();
            MTGLES20.setGlRenderSurface(null);
            resetExiting();
        }
//        if (ENG_InputManager.getSingleton() != null) {
//            unregisterAccelerationListeners();
//        }
        // Also flush the log
        ENG_Log.getInstance().setLogActive(false);
    }

    public void gameLoop(long currentTime, double dt, double lastTimeDt, ENG_Frame currentFrame) {

        long gameLoopBeginTime = ENG_Utility.currentTimeMillis();

        if (!areResourcesCreated() && !isIgnoreResourcesCreated()) {
            return;
        }
        if (isReloadingResources()) {
            return;
        }
        this.currentFrame = currentFrame;
        if (isIgnoreResourcesCreated()) {
            ENG_ContainerManager.getSingleton().update();
        } else {
            world.setDelta((float) dt);
            HudManager hudManager = HudManager.getSingleton();

            hudManager.lockRadarData();
            hudManager.resetRadar();
            world.process();
            hudManager.updateRadarFinalState();
            hudManager.unlockRadarData();

            renderWindow.updateStats();

            InGameStatisticsManager statisticsManager = InGameStatisticsManager.getInstance();
            InGameStatistics statistics = statisticsManager.getInGameStatistics();
            statistics.performanceStatistics.averageFps = renderWindow.getAverageFPS();
            statistics.performanceStatistics.maxFps = renderWindow.getBestFPS();
            statistics.performanceStatistics.minFps = renderWindow.getWorstFPS();

            if (HudManager.SHOW_DEBUGGING_INDICATORS) {
                hudManager.setFps(renderWindow.getLastFPS());
//                System.out.println("LastFPS: " + renderWindow.getLastFPS());
                Entity playerShip = WorldManager.getSingleton().getPlayerShip();
                if (playerShip != null) {
                    EntityProperties entityProperties = playerShip.getComponent(EntityProperties.class);
                    ENG_Vector3D playerShipPos = new ENG_Vector3D();
                    entityProperties.getNode().getPosition(playerShipPos);
                    hudManager.setPlayerPos(playerShipPos);
                }

                if (GAME_RESOURCE_UPDATE_CHECKER_ENABLED) {
                    checkGameResourcesUpdateAvailable();
                }
            }

            if (MainApp.getGame().getGameMode() == APP_Game.GameMode.MP || !isInGamePaused()) {
                // Updating the physics should happen before calling WorldManager.update() because in update()
                // you call GameLogicEntityRemoverSystem which removes collided objects. The idea is to remove objects
                // from the scene as soon as possible and not wait another frame.
//            long physicsBeginTime = System.nanoTime();
//            System.out.println("lastTimeDt: " + lastTimeDt * 0.001f + " dt: " + dt);
                long stepSimulationBeginTime = ENG_Utility.currentTimeMillis();
//            System.out.println("gameLoop time until stepSimulation: " + (stepSimulationBeginTime - gameLoopBeginTime));
                lastTimeDt += (stepSimulationBeginTime - gameLoopBeginTime);
                PhysicsUtility.stepSimulation(btDiscreteDynamicsWorld, (float) lastTimeDt * 0.001f, 5, (float) dt);
//            long physicsEndTime = System.nanoTime() - physicsBeginTime;
//            System.out.println("physicsEndTime: " + physicsEndTime);

//            long hudManagerBeginTime = ENG_Utility.currentTimeMillis();
                hudManager.update();
//            System.out.println("hudManager time: " + (ENG_Utility.currentTimeMillis() - hudManagerBeginTime));
//
                WorldManager.getSingleton().update(currentTime);

//        System.out.println("Frame time: " + (ENG_Utility.currentTimeMillis() - currentTime));

            }

            ENG_ContainerManager.getSingleton().update();


//		GameMenuManager.getSingleton().update();
//        SimpleViewGameMenuManager.getSingleton().update();
        }

    }

    //    public WorldConfiguration getSpWorldConfiguration() {
//        return spWorldConfiguration;
//    }
//
//    public WorldConfiguration getMpWorldConfiguration() {
//        return mpWorldConfiguration;
//    }

    @Override
    public WorldManagerBase getWorldManager() {
//        switch (getWorldManagerMode()) {
//            case SINGLEPLAYER:
//                return WorldManagerSP.getSingleton();
//            case MULTIPLAYER:
//                return WorldManagerMP.getSingleton();
//            default:
//                throw new IllegalArgumentException("Invalid world manager mode: " + getWorldManagerMode());
//        }
        return worldManager;

    }

    // For setting the wm from ShipSelection.
    public void setWorldManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public void initializeWorld() {
        initializeWorldConfigs();
        switch (getGameMode()) {
            case SP:
                if (clientGameMode != GameMode.SP) {
                    world = new GameWorld(spWorldConfiguration);
                    clientGameMode = GameMode.SP;
                }
                break;
            case MP:
                if (clientGameMode != GameMode.MP) {
                    world = new GameWorld(mpWorldConfiguration);
                    clientGameMode = GameMode.MP;
                }
                break;
            default:
                throw new IllegalStateException(getGameMode() + " is an invalid game mode");
        }
    }

    private void loadGameResources() {
        // Depending on the loaders' state we may need to only load some of them.
    }



    private class LoadEssentialRawData implements ENG_IMainThreadSequentialWorker {
        /** @noinspection UnstableApiUsage, deprecation */
        @Override
        public void run() {



            redirectPrintlnOutput();

//            if (MainApp.PLATFORM == MainApp.Platform.ANDROID || MainApp.PLATFORM == MainApp.Platform.IOS) {
//                throw new ArithmeticException("division by 0!!!");
//            }

            preferences.setLastShutdownSuccessful(false);
            uploadUnsentCrashData();

            // Copy all files from internal to local for android
            if (MainApp.Platform.isMobile()) {
                if (FORCE_DATA_COPY) {
                    preferences.setDataCopied(false);
//                    preferences.setDataUnpacked(false);
                    boolean b = Gdx.files.local(FOLDER_RAW + File.separator/* + fileHandle.name()*/).deleteDirectory();
                    System.out.println("Folder: " + FOLDER_RAW + " deleted: " + b);
                }
                if (!preferences.isDataCopied()) {
                    FileHandle internal = Gdx.files.internal("");
//                    System.out.println("Printing files");
//                    for (FileHandle fileHandle : internal.list()) {
//                        if (fileHandle.isDirectory()) {
//                            for (FileHandle fileHandle2 : fileHandle.list()) {
//                                System.out.println(fileHandle2.name());
//                            }
//                        }
//                        System.out.println(fileHandle.name());
//                    }
                    copyToLocalFolder(internal);
                    if (!DATA_IN_APK_COMPRESSED) {
//                        FileHandle gamedata = Gdx.files.internal("hotshot_gamedata");
//                        copyToLocalFolder(gamedata);
                        preferences.setDataUnpacked(true);
                    }
                    preferences.setDataCopied(true);
                }
            }

//            if (MainApp.DEV) {
//                throw new NullPointerException("test null ptr");
//            }

            setWorldManagerMode(WorldManagerMode.SINGLEPLAYER);

            final DatabaseConnection databaseConnection = DatabaseConnection.getConnection();
            databaseConnection.setDatabaseFile(FOLDER_COMPANY + File.separator + FOLDER_GAME + File.separator + "database");
//            if (MainApp.PLATFORM == MainApp.Platform.ANDROID || MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
//                throw new IllegalArgumentException("bla bla");
//            }

            uploadUnsentStacktraces();

            ENG_Utility.createRandomNumberGenerator();


            eventBus = new EventBus();
            eventBus.register(APP_SinglePlayerGame.this);

            boolean networkAvailable = ENG_NetUtility.isNetworkAvailable();

            ENG_CompilerUtil.setBasePath(getGameDir());

            // We need this initialized before reading the map list
            clientAPI = new ClientAPI();


            user = preferences.getUser();

//			SharedPreferences.Editor edit = sharedPreferences.edit();
//			edit.putFloat("boss", 0.1f);
//			edit.putString("avion", "cu motor");
//			boolean commit = edit.commit();
//			float boss = sharedPreferences.getFloat("boss", -1.0f);
//			String string = sharedPreferences.getString("avion", "empty");

            soundRoot = new ENG_Sound(/*MainActivity.getInstance()*/);

            loadShipOptions();
            loadMaxLevelNum();

            ENG_RenderRoot.setContinuousRendering(true);

            renderRoot = new ENG_RenderRoot(MainApp.getApplicationMode());

            // RenderRoot not initialized until now so can't use getRenderRoot().
            ENG_RenderSystem renderSystem = renderRoot.getRenderSystem();
            // setConfig() is completely ignored on metal.
//            renderSystem.setConfigOption("sRGB Gamma Conversion", "Yes");
//            ENG_Utility.sleep(30000);
            renderRoot.initialise(false);
            ENG_MaterialManager.getSingleton().initialise();
            ParticleFXPlugin.install();
            //		GLRenderSurface renderSurface = GLRenderSurface.getSingleton();
            // ENG_TouchInput touchInput = new ENG_TouchInput();
            // touchInputConverter = new ENG_TouchInputConvertor("TouchConvertor",
            // touchInput, 300);

            //		ENG_RenderWindow renderSurface = renderRoot.getCurrentRenderWindow();
            // For desktop. Will be overwritten for Android platform.
            ApplicationStartSettings applicationSettings = MainApp.getMainThread().getApplicationSettings();
            int width = applicationSettings.screenWidth;//1024;//Gdx.graphics.getWidth();//renderSurface.getWidth();
            int height = applicationSettings.screenHeight;//768;//Gdx.graphics.getHeight();//renderSurface.getHeight();
            if (MainApp.Platform.isMobile()) {
                width = Gdx.graphics.getWidth();
                height = Gdx.graphics.getHeight();
            }
            ENG_Log.getInstance().log("renderWindow screenWidth: " + width + " screenHeight: " + height);

            TreeMap<String, String> miscParams = new TreeMap<>();
            if (MainApp.PLATFORM == MainApp.Platform.ANDROID) {
                miscParams.put("currentGLContext", "true");
            }
            // We are using this to get rid of the eglSwapBuffers() that we don't need since android does that for us.
            if (MainApp.PLATFORM == MainApp.Platform.ANDROID) {
                miscParams.put("externalGLContext", "1");
            }
            boolean fullscreen = true;
            if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
                fullscreen = false;
            }
            if (MainApp.PLATFORM == MainApp.Platform.IOS) {
                fullscreen = true;
//                width = 320;
//                height = 568;

                miscParams.put("FSAA", "");
                miscParams.put("vsync", "");

                if (MainApp.SUBPLATFORM != MainApp.Platform.XROS) {
                    initializeNotchHeight();
                }
            }

            miscParams.put("gamma", "true");
            miscParams.put("reverse_depth", "Yes");
            miscParams.put("title", "Hotshot");

            if (getNotchHeight() > 0.0) {

            }
//            com.badlogic.gdx.Graphics iosGraphics = (com.badlogic.gdx.Graphics) Gdx.graphics;
//            long iosViewHandler = iosGraphics.getViewHandle();
//            long iosViewControllerHandler = iosGraphics.getViewControllerHandle();
//            System.out.println("iosViewHandler: " + String.format("0x%016X", iosViewHandler) +
//                    " iosViewControllerHandler: " + String.format("0x%016X", iosViewControllerHandler));
            renderWindow = renderRoot.createRenderWindow("MainRenderWindow", width, height, fullscreen, miscParams);

            //		FileHandle[] file = Gdx.files.local("raw/").list();
            //		String absolutePath = file.getAbsolutePath();
            //		String[] parent = file.list();

            setupResources("resources2.cfg");
            // Load only what is needed in order to display the loading screen.
            loadEssentialResources();

//            ENG_MaterialLoader.loadMaterial("panel_programs.txt", getGameResourcesDir()/*FOLDER_RAW*/, /*MainApp.PLATFORM != MainApp.Platform.ANDROID*/ true);

            ENG_SceneManager sceneManager = renderRoot.createSceneManager(SCENE_MANAGER);
            sceneManager._setDestinationRenderSystem(renderRoot.getActiveRenderSystem());
            // The listener is found in the overlay system ptr not in the overlay manager!!!
            sceneManager.addRenderQueueListener(ENG_OverlayManager.getSingleton().getOverlaySystemWrapper().getPtr());
            sceneManager.setShadowDirectionalLightExtrusionDistance(500.0f);
            sceneManager.setShadowFarDistance(500.0f);

            ENG_Camera camera = sceneManager.createCamera(MAIN_CAM);
            camera.setNearClipDistance(5.0f);
            // The maximum distance that we must have visibility into is sqrt(3) of the lateral of the cube.
            camera.setFarClipDistance(GameWorld.MAX_DISTANCE/* * 2.0f * (float) Math.sqrt(3)*/);
//            ENG_Viewport viewport = renderWindow.addViewport(camera);
//            viewport.setBackgroundColour(new ENG_ColorValue(0.0f, 0.0f, 0.0f));
            camera.setAutoAspectRatio(true);
            camera.setFOVy(new ENG_Degree(CAMERA_FOVY).valueRadians());
//            camera.setAspectRatio(((float) viewport.getActualWidth()) / ((float) viewport.getActualHeight()));
            // Optional
//            camera.setPolygonMode(ENG_Common.PolygonMode.PM_SOLID);
            camera.setPosition(0, 0, 0);
            camera.lookAt(new ENG_Vector4D(0, 0, -100, 0));
//            floatingCameraNode = WorldManagerBase.createCameraNode("FloatingCameraNode", sceneManager);
            compositorWorkspace = SceneCompositor.getSingleton().createCompositorWorkspace("HotshotDefaultWorkspace", true);

            AndroidRenderWindow currentRenderWindow = (AndroidRenderWindow) ENG_RenderRoot.getRenderRoot().getCurrentRenderWindow();
            ENG_ViewportNativeWrapper viewportNativeWrapper = new ENG_ViewportNativeWrapper(currentRenderWindow.getPointer());
            ENG_NativeCalls.ViewportData viewportData = ENG_NativeCalls.getViewport(currentRenderWindow.getPointer());
            if (MainApp.PLATFORM == MainApp.Platform.IOS) {
                viewportData.width /= (int) renderRoot.getScreenDensity();
                viewportData.height /= (int) renderRoot.getScreenDensity();
            }
            currentRenderWindow.addViewport(null, 0, viewportData.left, viewportData.top, viewportData.width, viewportData.height);

            ENG_TextureAtlas.setBasePath(getGameResourcesRootDir());
            ENG_SilverBack.getSingleton().loadAtlas(GORILLA_FONT, getGameResourcesRootDir());
            // We need the container manager if we have a fatal error in order to display it, well before the rest of the data has been loaded.
            containerManager = new ENG_ContainerManager();

            setGameState(GameState.ESSENTIAL_RAW_DATA_LOADED);
        }
    }

    private class ShowLoadingScreen implements ENG_IMainThreadSequentialWorker {
        @Override
        public void run() {

            ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
            containerManager.addFactory("LoadingScreen",
                    new LoadingScreen.LoadingScreenContainerFactory());
            loadingScreenContainer = (LoadingScreen) containerManager.createContainer("LoadingScreenProgressBar", "LoadingScreen",
                    null, true, null);
            setIgnoreResourcesCreated(true);
            containerManager.setCurrentContainer(loadingScreenContainer);

            showLoadingScreen();
//            ENG_RenderRoot.getRenderRoot().requestRenderingIfRequired();
//            renderRoot.setContinuousRendering(true);
            setGameState(GameState.LOADING_SCREEN_SHOWN);
//                setSkipMainThread(false);
        }
    }

    private class LoadResourcesPart0 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart0() {
            super("AnimatedTextures AsteroidMeshes", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("AnimatedTextures");
            loadResources("AsteroidMeshes");
            loadingScreenContainer.getProgressBar().setProgress(10);
        }
    }

    private class LoadResourcesPart1 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart1() {
            super("AsteroidTextures AsteroidMaterials", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("AsteroidTextures");
            loadResources("AsteroidMaterials");
            loadingScreenContainer.getProgressBar().setProgress(20);
        }
    }

    private class LoadResourcesPart2 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart2() {
            super("CargoMeshes CargoTextures", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("CargoMeshes");
            loadResources("CargoTextures");
            loadingScreenContainer.getProgressBar().setProgress(30);
        }
    }

    private class LoadResourcesPart3 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart3() {
            super("FlagMeshes FlagTextures MiscTextures", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("FlagMeshes");
            loadResources("FlagTextures");
            loadResources("MiscTextures");
            loadingScreenContainer.getProgressBar().setProgress(35);
        }
    }

    private class LoadResourcesPart4 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart4() {
            super("ShipMeshes", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("ShipMeshes");
            loadingScreenContainer.getProgressBar().setProgress(40);
        }
    }

    private class LoadResourcesPart5 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart5() {
            super("ShipTextures", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("ShipTextures");
            loadingScreenContainer.getProgressBar().setProgress(45);
        }
    }

    private class LoadResourcesPart6 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart6() {
            super("ShipMaterialsBig", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("ShipMaterialsBig");
            loadingScreenContainer.getProgressBar().setProgress(50);
        }
    }

    private class LoadResourcesPart7 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart7() {
            super("ShipMaterialsSmall", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("ShipMaterialsSmall");
            loadingScreenContainer.getProgressBar().setProgress(60);
        }
    }

    private class LoadResourcesPart8 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart8() {
            super("Skyboxes SkyboxesMaterials", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("Skyboxes");
            loadResources("SkyboxesMaterials");
            loadingScreenContainer.getProgressBar().setProgress(70);
        }
    }

    private class LoadResourcesPart9 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart9() {
            super("Sounds", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("Sounds");
            loadingScreenContainer.getProgressBar().setProgress(80);
        }
    }

    private class LoadResourcesPart10 extends ENG_MainThreadSequentialWorkerWithTimer {

        public LoadResourcesPart10() {
            super("WeaponMeshes WeaponTextures WeaponMaterials", true);
        }

        @Override
        public void runWithTimer() {
            loadResources("WeaponMeshes");
            loadResources("WeaponTextures");
            loadResources("WeaponMaterials");
            loadingScreenContainer.getProgressBar().setProgress(90);
        }
    }

    private class LoadResourcesPart11 implements ENG_IMainThreadSequentialWorker {

        @Override
        public void run() {
            loadingScreenContainer.getProgressBar().setProgress(100);
        }
    }

    private class LoadResourcesPart12 implements ENG_IMainThreadSequentialWorker {

        @Override
        public void run() {

        }
    }

    private class LoadRawData implements ENG_IMainThreadSequentialWorker {

        @Override
        public void run() {
            loadResources("Rest");
            loadingScreenContainer.getProgressBar().setProgress(100);
            setIgnoreResourcesCreated(false);
            for (int i = 0; i < MAX_LEVEL; ++i) {
//                if (i == 1) {
                    SceneCompositor.getSingleton().createCompositorWorkspace("SkyboxWorkspace" + i, false, true);
//                } else {
//                    SceneCompositor.getSingleton().createCompositorWorkspace("SkyboxWorkspace" + i, false);
//                }
            }
            setGameState(GameState.RAW_DATA_LOADED);
        }
    }

    private class UpdateGameDataAvailable implements ENG_IMainThreadSequentialWorker {
        @Override
        public void run() {
            if (DATA_IN_APK) {
                unpackLocalData();
            } else {
                updateGameDataAvailable();
            }
            if (isGameDataAvailable()) {
                setGameState(GameState.GAME_DATA_DOWNLOADED);
            }
        }
    }

    private class CheckGameDataAvailable implements ENG_IMainThreadSequentialWorker {
        private final ENG_MainThread mainThread;

        public CheckGameDataAvailable(ENG_MainThread mainThread) {
            this.mainThread = mainThread;
        }

        /** @noinspection deprecation */
        @Override
        public void run() {
            if (!isGameDataAvailable()) {
                // We must kill the application since we have no game data and no connection to download it. Remember to retry download next time.
                mainThread.clearWorkerQueue();
//                MainApp.getGame().exitGame();
                Bundle bundle = new Bundle();
                bundle.putString(FatalErrorMenu.ERROR_STR, "Could not access Internet. Make sure you are not in airplane mode and that you have a valid Internet connection!");
                ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
                containerManager.addFactory("FatalError", new FatalErrorMenu.FatalErrorMenuContainerFactory());
                ENG_Container fatalErrorContainer = containerManager.createContainer("FatalErrorMenu", "FatalError", bundle);
                initializeInputSystem();
                hideLoadingScreen();
                containerManager.setCurrentContainer(fatalErrorContainer);
                setIgnoreResourcesCreated(true);
                MainApp.setFatalError();
            } else {
                setIgnoreResourcesCreated(false);
            }
        }
    }

    private class LoadLevelResources implements ENG_IMainThreadSequentialWorker {
        @Override
        public void run() {
//                setSkipMainThread(true);
            levelTitleList = ENG_Utility.getStringAsPrimitiveArray(BriefingLoader.loadLevelTitleList("level_titles.txt", getGameResourcesDir()));
//        multiplayerLevelTitleList = ENG_Utility.getStringAsPrimitiveArray(BriefingLoader.loadLevelTitleList("multiplayer_level_titles.txt", getGameResourcesDir()));
            missionBriefingList = ENG_Utility.getStringAsPrimitiveArray(BriefingLoader.loadLevelMissionBriefingList("mission_briefing_list.txt", getGameResourcesDir()));

            // Read maps after we have loaded the level titles for multiplayer
            readMapList(true);

            ENG_SoundLoader.loadSoundList("sound_list.txt", getGameResourcesDir(), true);
            setGameState(GameState.LOADED_MATERIAL_DATA);

//            PhysicsUtility.createBvhTriangleMeshShape(getGameResourcesDir() + "/static_meshes/tunnel0.g3dj");
        }
    }

    private class LoadRestOfResources implements ENG_IMainThreadSequentialWorker {
        @Override
        public void run() {
            ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
            containerManager.setCurrentContainer((ENG_Container) null);
            containerManager.addFactory("Console",
                    new Console.ConsoleContainerFactory());
            containerManager.addFactory("Credits",
                    new Credits.CreditsContainerFactory());
            containerManager.addFactory("MissionBriefing",
                    new MissionBriefing.MissionBriefingContainerFactory());
            containerManager.addFactory("MissionDebriefing",
                    new MissionDebriefing.MissionDebriefingContainerFactory());
            containerManager.addFactory("EndGame",
                    new EndGame.EndGameContainerFactory());
            containerManager.addFactory("OptionsMenu",
                    new OptionsMenu.OptionsMenuContainerFactory());
            containerManager.addFactory("Help",
                    new Help.HelpContainerFactory());
            containerManager.addFactory("GenericMenu",
                    new GenericMenu.GenericMenuFactory());
            containerManager.addFactory("LevelSelection",
                    new LevelSelection.LevelSelectionFactory());
            containerManager.addFactory("ShipSelection",
                    new ShipSelection.ShipSelectionContainerFactory());
            containerManager.addFactory("MultiplayerCreateAccountMenu",
                    new MultiplayerCreateAccount.MultiplayerCreateAccountContainerFactory());
            containerManager.addFactory("MultiplayerLoginMenu",
                    new MultiplayerLogin.MultiplayerLoginContainerFactory());
//		containerManager.addFactory("MultiplayerLoggedInMenu",
//				new MultiplayerLogin.MultiplayerLoginContainerFactory());
            containerManager.addFactory("MultiplayerCreateSessionMenu",
                    new MultiplayerCreateSession.MultiplayerCreateSessionContainerFactory());
            containerManager.addFactory("MultiplayerJoinSessionMenu",
                    new MultiplayerJoinSession.MultiplayerJoinSessionContainerFactory());
//        containerManager.addFactory("MultiplayerLogin",
//                new MultiplayerLogin.MultiplayerLoginContainerFactory());
            containerManager.addFactory("MultiplayerShipDestroyed",
                    new MultiplayerShipDestroyed.MultiplayerShipDestroyedContainerFactory());
            containerManager.addFactory("MultiplayerLevelEnded",
                    new MultiplayerLevelEnded.MultiplayerLevelEndedContainerFactory());
            containerManager.addFactory("InGameMenu",
                    new InGameMenu.InGameMenuContainerFactory());
//            containerManager.addFactory("FatalError",
//                    new FatalErrorMenu.FatalErrorMenuContainerFactory());
            containerManager.addFactory("MultiplayerAddFriendMenu",
                    new MultiplayerAddFriend.MultiplayerAddFriendFactory());
            containerManager.addFactory("MultiplayerCreateSessionWithFriendsMenu",
                    new MultiplayerCreateSessionWithFriends.MultiplayerCreateSessionWithFriendsFactory());
            containerManager.addFactory("MultiplayerJoinSessionWithFriendsMenu",
                    new MultiplayerJoinSessionWithFriends.MultiplayerJoinSessionWithFriendsFactory());
            containerManager.addFactory("MultiplayerLobbyMenu",
                    new MultiplayerLobby.MultiplayerLobbyFactory());
            containerManager.addFactory("Subtitles",
                    new Subtitles.SubtitlesContainerFactory());

            containerManager.addContainerListenerFactory(
                    ShowDemoContainerListener.ShowDemoContainerListenerFactory.TYPE,
                    new ShowDemoContainerListener.ShowDemoContainerListenerFactory());
            containerManager.addContainerListenerFactory(
                    HideInvalidLevelsContainerListener.HideInvalidLevelsContainerListenerFactory.TYPE,
                    new HideInvalidLevelsContainerListener.HideInvalidLevelsContainerListenerFactory());
            containerManager.addContainerListenerFactory(
                    HideDemoContainerListener.HideDemoContainerListenerFactory.TYPE,
                    new HideDemoContainerListener.HideDemoContainerListenerFactory());
            containerManager.addContainerListenerFactory(
                    ShipDataInjectorContainerListener.ShipDataInjectorContainerListenerFactory.TYPE,
                    new ShipDataInjectorContainerListener.ShipDataInjectorContainerListenerFactory());
//        containerManager.addContainerListenerFactory(
//                MultiplayerMenuContainerListener.MultiplayerMenuContainerListenerFactory.TYPE,
//                new MultiplayerMenuContainerListener.MultiplayerMenuContainerListenerFactory());
            containerManager.addContainerListenerFactory(
                    MultiplayerCreateAccountContainerListener.MultiplayerCreateAccountMenuContainerListenerFactory.TYPE,
                    new MultiplayerCreateAccountContainerListener.MultiplayerCreateAccountMenuContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerLoginContainerListener.MultiplayerLoginContainerListenerFactory.TYPE,
                    new MultiplayerLoginContainerListener.MultiplayerLoginContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerCreateSessionContainerListener.MultiplayerCreateSessionContainerListenerFactory.TYPE,
                    new MultiplayerCreateSessionContainerListener.MultiplayerCreateSessionContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerJoinSessionContainerListener.MultiplayerJoinSessionContainerListenerFactory.TYPE,
                    new MultiplayerJoinSessionContainerListener.MultiplayerJoinSessionContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerCreateSessionWithFriendsContainerListener.MultiplayerCreateSessionWithFriendsContainerListenerFactory.TYPE,
                    new MultiplayerCreateSessionWithFriendsContainerListener.MultiplayerCreateSessionWithFriendsContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerLobbyContainerListener.MultiplayerLobbyContainerListenerFactory.TYPE,
                    new MultiplayerLobbyContainerListener.MultiplayerLobbyContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerJoinSessionWithFriendsContainerListener.MultiplayerJoinSessionWithFriendsContainerListenerFactory.TYPE,
                    new MultiplayerJoinSessionWithFriendsContainerListener.MultiplayerJoinSessionWithFriendsContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerAddFriendContainerListener.MultiplayerAddFriendContainerListenerFactory.TYPE,
                    new MultiplayerAddFriendContainerListener.MultiplayerAddFriendContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerMenuContainerListener.MultiplayerMenuContainerListenerFactory.TYPE,
                    new MultiplayerMenuContainerListener.MultiplayerMenuContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerShipDestroyedContainerListener.MultiplayerShipDestroyedContainerListenerFactory.TYPE,
                    new MultiplayerShipDestroyedContainerListener.MultiplayerShipDestroyedContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    MultiplayerLevelEndedContainerListener.MultiplayerLevelEndedContainerListenerFactory.TYPE,
                    new MultiplayerLevelEndedContainerListener.MultiplayerLevelEndedContainerListenerFactory()
            );
            containerManager.addContainerListenerFactory(
                    InGameMenuContainerListener.InGameMenuContainerListenerFactory.TYPE,
                    new InGameMenuContainerListener.InGameMenuContainerListenerFactory());
            containerManager.addContainerListenerFactory(
                    ShowKeyboardContainerListener.ShowKeyboardContainerListenerFactory.TYPE,
                    new ShowKeyboardContainerListener.ShowKeyboardContainerListenerFactory());
            containerManager.addContainerListenerFactory(ConsoleContainerListener.ConsoleContainerListenerFactory.TYPE,
                    new ConsoleContainerListener.ConsoleContainerListenerFactory());


            netManager = new NetManager();

            loadLevelList();
            createMeshMappings();

            ENG_RenderRoot root = ENG_RenderRoot.getRenderRoot();
            final ENG_SceneManager sceneManager = root.getSceneManager(SCENE_MANAGER);
            initializeWorld();

            worldManager = new WorldManagerSP();
            worldManager.setSceneManager(sceneManager);
            worldManager.setDynamicWorld(btDiscreteDynamicsWorld);

//            ENG_CompositorLoader.loadCompositorList("comp_list.txt", getGameResourcesDir(), true);

            // No need to reload on resource loading since we're not creating anything gl buffers related
//            ENG_ParticleCompiler.loadParticleSystemsFromFile("particle_system_list.txt", getGameResourcesDir(), true);

            initializeInputSystem();

            // Made it field so we can register and unregister easier

    /*
     * try { accelerometerInputCreated.await(); } catch
     * (InterruptedException e) {
     * e.printStackTrace(); }
     */
//		accelerometerInput = (ENG_AccelerometerInput) inputManager.createInput(
//				"accelerometerInput", ENG_AccelerometerInputFactory.TYPE);
//		accelerometerInput.setup(getSensorManager(), getDisplay());
//		ENG_AccelerometerInputConvertor accInputConvertor = (ENG_AccelerometerInputConvertor) inputManager
//				.createInputConvertor("accelerometerInputConvertor",
//						accelerometerInput, 100,
//						ENG_AccelerometerInputConvertorFactory.TYPE);
//		inputConvertorToMovement = new InputConvertorToMovement(
//				accInputConvertor);
//		inputConvertorToMovement.setSensitivity(MAX_INCLINATION_ANGLE_X,
//				MAX_INCLINATION_ANGLE_Y);
//		registerAccelerationListeners();



//            ENG_FontLoader.loadFont("font.txt", getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);

//            loadResourceLists();

            simpleViewMenuManager = new SimpleViewMenuManager();
            simpleViewGameMenuManager = new SimpleViewGameMenuManager();
            simpleViewGameMenuManager.initMenus();
            SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.IN_MENU);


            hudManager = new HudManager();
            hudManager.loadHudOverlays();


            setResoucesCreated();
            if (AUTOMATION_ENABLED && !MainApp.getMainThread().isInputState()) {
                MainApp.getMainThread().addAutomation(new SinglePlayerMenuAutomation());
//                switch (MULTIPLAYER_GAME_INITIALIZATION_ENUM) {
//
//                    default:
//                    case NONE:
//                        MainApp.getMainThread().addAutomation(new SinglePlayerMenuAutomation());
//                        break;
//                    case CREATE_SESSION:
//                        MainApp.getMainThread().addAutomation(new MultiPlayerCreateSessionWithFriendsAutomation());
//                        break;
//                    case JOIN_SESSION:
//                        MainApp.getMainThread().addAutomation(new MultiPlayerJoinSessionWithFriendsAutomation());
//                        break;
//                }

                if (MainApp.PLATFORM == MainApp.Platform.DESKTOP) {
//                    MainApp.getMainThread().addAutomation(new MultiPlayerCreateServerMenuAutomation());
                }
            }

            if (APP_Game.GAME_RESOURCE_UPDATE_CHECKER_ENABLED) {
                GameResourceUpdateChecker.getInstance().startMonitoringLiveGameResourceUpdates();
            }

//		createMainMenuBackgroundDemo();

            hideLoadingScreen();

            setGameState(GameState.ALL_RESOURCES_LOADED);

//                setSkipMainThread(false);
        }
    }

    private void initializeWorldConfigs() {
        ENG_RenderRoot root = ENG_RenderRoot.getRenderRoot();
        final ENG_SceneManager sceneManager = root.getSceneManager(SCENE_MANAGER);
        final ENG_Camera camera = sceneManager.getCamera(MAIN_CAM);
//        CameraSystem cameraSystem = new CameraSystem(camera);
        HudSystem hudSystem = new HudSystem();
        MovementSystem movementSystem = new MovementSystem(MOVEMENT_SYSTEM_INTERVAL);
        // The movement system must be put before the collision detection system
//        CollisionDetectionSystem collisionDetectionSystem = new CollisionDetectionSystem(COLLISION_DETECTION_SYSTEM_INTERVAL);
        AISystem aiSystem = new AISystem(AI_SYSTEM_INTERVAL);
        ProjectileUpdateSPSystem projectileUpdateSPSystem = new ProjectileUpdateSPSystem();
        ProjectileUpdateMPSystem projectileUpdateMPSystem = new ProjectileUpdateMPSystem();
        projectileUpdateSPSystem.setPassive(true);
        projectileUpdateMPSystem.setPassive(true);
        FollowingShipCounterResetSystem followingShipCounterResetSystem = new FollowingShipCounterResetSystem();
        followingShipCounterResetSystem.setPassive(true);
        FollowingShipCounterSPSystem followingShipCounterSPSystem = new FollowingShipCounterSPSystem();
        followingShipCounterSPSystem.setPassive(true);
        MultiplayerEntityProcessingClientSystem multiplayerEntityProcessingClientSystem = new MultiplayerEntityProcessingClientSystem();
        multiplayerEntityProcessingClientSystem.setPassive(true);
        GameLogicEntityRemoverSPSystem entityRemoverSPSystem = new GameLogicEntityRemoverSPSystem();
        GameLogicEntityRemoverMPSystem entityRemoverMPSystem = new GameLogicEntityRemoverMPSystem();
        entityRemoverSPSystem.setPassive(true);
        entityRemoverMPSystem.setPassive(true);
        EntityDeleterSystem entityDestructionSPSystem = new EntityDeleterSystem();
        EntityDeleterSystem entityDestructionMPSystem = new EntityDeleterSystem();
        StaticEntityDeleterSystem staticEntityDestructionSPSystem = new StaticEntityDeleterSystem();
        StaticEntityDeleterSystem staticEntityDestructionMPSystem = new StaticEntityDeleterSystem();
        entityDestructionSPSystem.setPassive(true);
        entityDestructionMPSystem.setPassive(true);
        staticEntityDestructionSPSystem.setPassive(true);
        staticEntityDestructionMPSystem.setPassive(true);
        PlayerEntityDestroyedVerifierSPSystem playerEntityDestroyedVerifierSPSystem = new PlayerEntityDestroyedVerifierSPSystem();
        PlayerEntityDestroyedVerifierMPSystem playerEntityDestroyedVerifierMPSystem = new PlayerEntityDestroyedVerifierMPSystem();
        playerEntityDestroyedVerifierSPSystem.setPassive(true);
        playerEntityDestroyedVerifierMPSystem.setPassive(true);
        ClientEntityInterpolationSystem clientEntityInterpolationSystem = new ClientEntityInterpolationSystem();
        clientEntityInterpolationSystem.setPassive(true);
        DataSenderMPSystem dataSenderMPSystem = new DataSenderMPSystem();
        dataSenderMPSystem.setPassive(true);

        PlayerManager playerManager = new PlayerManager();
        GroupManager groupManager = new GroupManager();
        TeamManager teamManager = new TeamManager();
        TagManager tagManager = new TagManager();

        spWorldConfiguration = new WorldConfigurationBuilder().with(
//                cameraSystem,
                hudSystem,
                aiSystem, // Apply ai movement logic before movement.
                movementSystem,
//                collisionDetectionSystem,

                projectileUpdateSPSystem,
                followingShipCounterResetSystem,
                followingShipCounterSPSystem,
                entityRemoverSPSystem,
                entityDestructionSPSystem,
                staticEntityDestructionSPSystem,
                playerEntityDestroyedVerifierSPSystem,
                playerManager,
                groupManager,
                teamManager,
                tagManager
        ).build();

        mpWorldConfiguration = new WorldConfigurationBuilder().with(
//                cameraSystem,
                hudSystem,
                aiSystem, // Apply ai movement logic before movement.
                movementSystem,
//                collisionDetectionSystem,

                projectileUpdateMPSystem,
                multiplayerEntityProcessingClientSystem,
                entityRemoverMPSystem,
                playerEntityDestroyedVerifierMPSystem,
                clientEntityInterpolationSystem,
                dataSenderMPSystem,
                entityDestructionMPSystem,
                staticEntityDestructionMPSystem,
                playerManager,
                groupManager,
                teamManager,
                tagManager
        ).build();
    }

    private void initializeInputSystem() {
        keyBindings = new KeyBindings();
        keyBindings.setDefaults();

        inputManager = new ENG_InputManager();
        inputManager.addInputFactory(new ENG_TouchInputFactory());
        inputManager.addInputFactory(new ENG_AccelerometerInputFactory());
        inputManager.addInputFactory(new ENG_MouseAndKeyboardInputFactory());
        // No longer used. It is now included in
        // ENG_MouseAndKeyboardInputFactory. The ENG_TouchInputConvertor
        // now uses the ENG_MouseAndKeyboardInput.

        // USED AGAIN! TOUCHINPUTCONVERTOR DOESN'T NEED DELTAS!!!!
        inputManager.addInputListenerFactory(new ENG_TouchListener.ENG_TouchListenerFactory());
        inputManager.addInputListenerFactory(new ENG_MouseAndKeyboardListener.ENG_MouseAndKeyboardListenerFactory());
        inputManager.addInputConvertorFactory(new ENG_TouchInputConvertorFactory());
        inputManager.addInputConvertorFactory(new ENG_AccelerometerInputConvertorFactory());
        inputManager.addInputConvertorFactory(new InGameInputConvertorFactory());

        ENG_TouchInput touchInput = (ENG_TouchInput) inputManager.createInput("touchInput", ENG_TouchInputFactory.TYPE);
        ENG_MouseAndKeyboardInput mouseAndKeyboardInput = (ENG_MouseAndKeyboardInput)
                inputManager.createInput("mouseAndKeyboardInput", ENG_MouseAndKeyboardInputFactory.TYPE);
//		touchListener = new ENG_TouchListener(touchInput);
        touchListener = (ENG_TouchListener) inputManager.createInputListener(
                "touchInputListener", touchInput, ENG_TouchListener.ENG_TouchListenerFactory.TYPE);

        ENG_MouseAndKeyboardListener mouseAndKeyboardListener =
                (ENG_MouseAndKeyboardListener) inputManager.createInputListener(
                        "mouseAndKeyboardInputListener", mouseAndKeyboardInput, ENG_MouseAndKeyboardListener.ENG_MouseAndKeyboardListenerFactory.TYPE);
//		mouseAndKeyboardListener.setCursorGrabbed(true);
        // THE MOUSE AND KEYBOARD LISTENER SENDS DELTAS WHILE
        // THE TOUCH LISTENER SEND SCREEN COORDS.
//		ENG_MouseAndKeyboardListener mouseAndKeyboardForGUIListener =
//				(ENG_MouseAndKeyboardListener) inputManager.createInputListener(
//						"mouseAndKeyboardInputForGUIListener",
//						mouseAndKeyboardInput,
//						ENG_MouseAndKeyboardListenerFactory.TYPE);
//		mouseAndKeyboardForGUIListener.setCursorGrabbed(false);
//		renderSurface.setOnTouchListener(touchListener);
//		inputManager.setInputListener(touchListener);
        // Changed to using ENG_MouseAndKeyboardInput
        ENG_TouchInputConvertor inputConvertor = (ENG_TouchInputConvertor) inputManager
                .createInputConvertor("touchInputConvertor", touchInput, 100, ENG_TouchInputConvertorFactory.TYPE);
        InGameInputConvertor inGameInputConvertor = (InGameInputConvertor)
                inputManager.createInputConvertor("inGameInputConvertor", mouseAndKeyboardInput, 100, InGameInputConvertorFactory.TYPE);
        inputManager.registerInputConvertorListener(TO_GUI_LISTENER, new ENG_InputConvertorToGUIListener(inputConvertor));
        InGameInputConvertorListener inGameInputConvertorListener = new InGameInputConvertorListener(inGameInputConvertor//,
                //speedScrollContainer
        );
        inGameInputConvertorListener.setSensitivity(preferences.getShipSensitivity());
        inputManager.registerInputConvertorListener(TO_IN_GAME_LISTENER, inGameInputConvertorListener); // moved after creating the HUD

        inputManager.createInputStack(TOUCH_INPUT_STACK, "mouseAndKeyboardInput", "touchInputListener", "touchInputConvertor", TO_GUI_LISTENER);
        inputManager.createInputStack(IN_GAME_INPUT_STACK, "mouseAndKeyboardInput", "mouseAndKeyboardInputListener", "inGameInputConvertor", TO_IN_GAME_LISTENER);
        inputManager.setInputStack(TOUCH_INPUT_STACK);

        guiOverlayManager = new ENG_GUIOverlayManager();
    }

//    public void setMultiplayerEntityProcessingClientSystem(MultiplayerEntityProcessingClientSystem multiplayerEntityProcessingClientSystem) {
//        this.multiplayerEntityProcessingClientSystem = multiplayerEntityProcessingClientSystem;
//    }
//
//    public MultiplayerEntityProcessingClientSystem getMultiplayerEntityProcessingClientSystem() {
//        return multiplayerEntityProcessingClientSystem;
//    }
}
