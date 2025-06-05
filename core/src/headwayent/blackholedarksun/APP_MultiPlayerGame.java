/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/31/21, 7:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.GroupManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.TeamManager;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.google.common.eventbus.EventBus;

import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.loaders.BriefingLoader;
import headwayent.blackholedarksun.loaders.LevelLoader;
import headwayent.blackholedarksun.menus.language.Language;
import headwayent.blackholedarksun.multiplayer.systems.*;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.registeredclasses.JoinServerConnectionRequest;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.systems.*;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.hotshotengine.ApplicationStartSettings;
import headwayent.hotshotengine.ENG_MainThread;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.db.DatabaseConnection;
import headwayent.hotshotengine.networking.ENG_NetUtility;
import headwayent.hotshotengine.renderer.ENG_MaterialManager;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.plugins.particlefx.ParticleFXPlugin;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;
import headwayent.hotshotengine.statedebugger.ENG_Frame;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by sebas on 27.10.2015.
 */
public class APP_MultiPlayerGame extends APP_Game {

    public static final String PREDEFINED_PARAM_MAP_ID = "map_id";
    private WorldManagerServerSide worldManager;

    /** @noinspection UnstableApiUsage */
    @Override
    public boolean gameStart(ENG_Frame currentFrame) {

//        redirectPrintlnOutput();

        setWorldManagerMode(WorldManagerMode.MULTIPLAYER_SERVER_SIDE);

        checkLocalDirAvailable();

        final ENG_MainThread mainThread = MainApp.getMainThread();

        initPhysicsEngine();

        preferences.setLastShutdownSuccessful(false);
        uploadUnsentCrashData();

        final DatabaseConnection databaseConnection = DatabaseConnection.getConnection();
        databaseConnection.setDatabaseFile(FOLDER_COMPANY + File.separator + FOLDER_GAME + File.separator + "database");
        uploadUnsentStacktraces();

//        DefaultClassLoader defaultClassLoader = new DefaultClassLoader(getClass().getClassLoader());
//        try {
//            Class engUtility = defaultClassLoader.loadClass("headwayent.hotshotengine.ENG_Utility");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        ENG_Utility.createRandomNumberGenerator();


        eventBus = new EventBus();
        eventBus.register(this);

        boolean networkAvailable = ENG_NetUtility.isNetworkAvailable();

        ENG_CompilerUtil.setBasePath(getGameDir());

        createSharedPreferencesFile();

        preferences = new Preferences(getSharedPreferences(), getGson());

        Language.getSingleton().loadCurrentLanguage();

        // We need this initialized before reading the map list
        clientAPI = new ClientAPI();

//        ENG_RenderRoot.setContinuousRendering(true);

        renderRoot = new ENG_RenderRoot(MainApp.getApplicationMode());
        // RenderRoot not initialized until now so can't use getRenderRoot().
        renderRoot.initialise(false);
        ENG_MaterialManager.getSingleton().initialise();
        ParticleFXPlugin.install();

        ApplicationStartSettings applicationSettings = MainApp.getMainThread().getApplicationSettings();
        int width = applicationSettings.screenWidth;//1024;//Gdx.graphics.getWidth();//renderSurface.getWidth();
        int height = applicationSettings.screenHeight;//768;//Gdx.graphics.getHeight();//renderSurface.getHeight();
        if (MainApp.Platform.isMobile()) {
            width = Gdx.graphics.getWidth();
            height = Gdx.graphics.getHeight();
        }

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
            miscParams.put("gamma", "true");
            miscParams.put("FSAA", "");
            miscParams.put("vsync", "");
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
        loadEssentialResources();

        loadResources("AnimatedTextures");
        loadResources("AsteroidMeshes");
        loadResources("AsteroidTextures");
        loadResources("AsteroidMaterials");
        loadResources("CargoMeshes");
        loadResources("CargoTextures");
        loadResources("FlagMeshes");
        loadResources("FlagTextures");
        loadResources("MiscTextures");
        loadResources("ShipMeshes");
        loadResources("ShipTextures");
        loadResources("ShipMaterialsBig");
        loadResources("ShipMaterialsSmall");
        loadResources("Skyboxes");
        loadResources("SkyboxesMaterials");
        loadResources("Sounds");
        loadResources("WeaponMeshes");
        loadResources("WeaponTextures");
        loadResources("WeaponMaterials");
        loadResources("Rest");

        ENG_SceneManager sceneManager = renderRoot.createSceneManager(SCENE_MANAGER);
        sceneManager._setDestinationRenderSystem(renderRoot.getActiveRenderSystem());
        // The listener is found in the overlay system ptr not in the overlay manager!!!
//        sceneManager.addRenderQueueListener(ENG_OverlayManager.getSingleton().getOverlaySystemWrapper().getPtr());
        sceneManager.setShadowDirectionalLightExtrusionDistance(500.0f);
        sceneManager.setShadowFarDistance(500.0f);

        levelTitleList = ENG_Utility.getStringAsPrimitiveArray(BriefingLoader.loadLevelTitleList("level_titles.txt", getGameResourcesDir()));
//        multiplayerLevelTitleList = ENG_Utility.getStringAsPrimitiveArray(BriefingLoader.loadLevelTitleList("multiplayer_level_titles.txt", getGameResourcesDir()));
        missionBriefingList = ENG_Utility.getStringAsPrimitiveArray(BriefingLoader.loadLevelMissionBriefingList("mission_briefing_list.txt", getGameResourcesDir()));

        // Read maps after we have loaded the level titles for multiplayer
        readMapList();

        netManager = new NetManager();

//        initLevelShipSelectionList();
        loadLevelList();
        createMeshMappings();

        // The movement system must be put before the collision detection system
        MovementSystem movementSystem = new MovementSystem(MOVEMENT_SYSTEM_INTERVAL);
        movementSystem.createThreads();
//        CollisionDetectionSystem collisionDetectionSystem = new CollisionDetectionSystem(COLLISION_DETECTION_SYSTEM_INTERVAL);
        AISystem aiSystem = new AISystem(AI_SYSTEM_INTERVAL);
        // Order is important here.
        DataReceiverSystem dataReceiverSystem = new DataReceiverSystem();
//        dataReceiverSystem.setEnabled(false);
        dataReceiverSystem.setPassive(true);
        MultiplayerEntityProcessingServerSystem multiplayerEntityProcessingServerSystem = new MultiplayerEntityProcessingServerSystem();
        multiplayerEntityProcessingServerSystem.setEnabled(false);
        DataSenderSystem dataSenderSystem = new DataSenderSystem();
//        dataSenderSystem.setEnabled(false);
        dataSenderSystem.setPassive(true);
        ProjectileUpdateServerSideSystem projectileUpdateServerSideSystem = new ProjectileUpdateServerSideSystem();
//        projectileUpdateServerSideSystem.setEnabled(false);
        projectileUpdateServerSideSystem.setPassive(true);
        FollowingShipCounterResetSystem followingShipCounterResetSystem = new FollowingShipCounterResetSystem();
        followingShipCounterResetSystem.setPassive(true);
        FollowingShipCounterServerSideSystem followingShipCounterServerSideSystem = new FollowingShipCounterServerSideSystem();
        followingShipCounterServerSideSystem.setPassive(true);
        GameLogicEntityRemoverServerSideSystem entityRemoverServerSideSystem = new GameLogicEntityRemoverServerSideSystem();
//        entityRemoverServerSideSystem.setEnabled(false);
        entityRemoverServerSideSystem.setPassive(true);
        UserStatsUpdaterSystem userStatsUpdaterSystem = new UserStatsUpdaterSystem();
//        userStatsUpdaterSystem.setEnabled(false);
        userStatsUpdaterSystem.setPassive(true);

        PlayerManager playerManager = new PlayerManager();
        GroupManager groupManager = new GroupManager();
        TeamManager teamManager = new TeamManager();
        TagManager tagManager = new TagManager();

        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder().with(
                movementSystem,
//                collisionDetectionSystem,
                aiSystem,
                dataReceiverSystem,
                multiplayerEntityProcessingServerSystem,
                dataSenderSystem,
                projectileUpdateServerSideSystem,
                followingShipCounterResetSystem,
                followingShipCounterServerSideSystem,
                entityRemoverServerSideSystem,
                userStatsUpdaterSystem,
                playerManager,
                groupManager,
                teamManager,
                tagManager
        ).build();

//        world.setManager(new PlayerManager());
//        world.setManager(new GroupManager());
//        world.setManager(new TeamManager());
//        world.setManager(new TagManager());
//
//        // DON'T FORGET
//        world.initialize();
        world = new GameWorld(worldConfiguration);

        worldManager = new WorldManagerServerSide();
        worldManager.setSceneManager(sceneManager);

//        loadResourceLists();

        long mapId;
        if (MainApp.getMainThread().isInputState()) {
            mapId = Long.parseLong(MainApp.getMainThread().getDebuggingState().getPredefinedParameter(PREDEFINED_PARAM_MAP_ID, 0));
        } else {
            mapId = applicationSettings.serverConnectionRequest.getMapId();
            if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
                MainApp.getMainThread().getDebuggingState().addPredefinedParameter(PREDEFINED_PARAM_MAP_ID, String.valueOf(mapId));
            }
        }
        long pos = getMapPosInTitleListByServerId(mapId);
        LevelBase level = LevelLoader.compileLevel((int) pos, getMultiPlayerLevelList(), false);
        level.mapId = mapId;
        LevelLoader.loadLevel(level);

//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                System.out.println("working in background");
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                System.out.println("Showing results");
//            }
//        }.execute();

        setGameState(GameState.ALL_MULTIPLAYER_RESOURCES_LOADED);
        // Let the server continue in order to receive join requests.
        applicationSettings.waitForServerToStart.countDown();
        return true;
    }

    public void addClient(Connection connection, JoinServerConnectionRequest request) {
        WorldManagerServerSide.getSingleton().addPlayer(connection, request);
    }

    public void removeClient() {

    }

    @Override
    public void gameEnd() {

    }

    @Override
    public void gameActivate() {

    }

    @Override
    public void gameDeactivate() {

    }

    @Override
    public void gameLoop(long currentTime, double dt, double lastTimeDt, ENG_Frame currentFrame) {
        long gameLoopBeginTime = ENG_Utility.currentTimeMillis();

        this.currentFrame = currentFrame;
        world.setDelta((float) dt);
        world.process();

        // Updating the physics should happen before calling WorldManager.update() because in update()
        // you call GameLogicEntityRemoverSystem which removes collided objects. The idea is to remove objects
        // from the scene as soon as possible and not wait another frame.
        long stepSimulationBeginTime = ENG_Utility.currentTimeMillis();
//            System.out.println("gameLoop time until stepSimulation: " + (stepSimulationBeginTime - gameLoopBeginTime));
        lastTimeDt += (stepSimulationBeginTime - gameLoopBeginTime);
        PhysicsUtility.stepSimulation(btDiscreteDynamicsWorld, (float) lastTimeDt * 0.001f, 5, (float) dt);
//            long physicsEndTime = System.nanoTime() - physicsBeginTime;
//            System.out.println("physicsEndTime: " + physicsEndTime);

        WorldManagerServerSide.getSingleton().update(currentTime);
    }

    @Override
    public void reloadResources() {

    }

    @Override
    public WorldManagerBase getWorldManager() {
//        return WorldManagerServerSide.getSingleton();
        return worldManager;
    }
}
