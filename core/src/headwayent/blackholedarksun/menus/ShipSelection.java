/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import headwayent.blackholedarksun.*;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.multiplayer.rmi.UserStatsList;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.net.registeredclasses.ServerConnectionResponse;
import headwayent.blackholedarksun.systems.MovementSystem;
import headwayent.blackholedarksun.world.Light;
import headwayent.blackholedarksun.world.LightingManager;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerMP;
import headwayent.hotshotengine.AsyncTask;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_Workflows;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;
import headwayent.hotshotengine.statedebugger.ENG_State;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import headwayent.blackholedarksun.loaders.LevelLoader;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

/**
 * Created by Sebi on 18.05.2014.
 */
public class ShipSelection extends ENG_Container {

    public static final String PREVIOUS_SHIP = "previous_ship";
    public static final String NEXT_SHIP = "next_ship";
    public static final String SELECT_SHIP = "select_ship";
    public static final String BACK = "back";
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final String LIGHT = "Light0";
    private final ENG_TextView shipNameView;
    private final ENG_TextView shipSpeedView;
    private final ENG_TextView shipArmorView;
    private final ENG_TextView shipWeaponsView;
    private final ENG_Button backButton;
    private ArrayList<headwayent.blackholedarksun.entitydata.ShipData> shipList;
    private boolean shipCreated;
    private int currentShipDataIndex;
    private long mapId = -1;
    private boolean multiplayer;
    private LevelBase level;
    private long sessionId;
    private String serverIp;
    private String sessionName;
    private int tcpPort;
    private int udpPort;
    private boolean createSession;
    private AsyncTask<Void, Integer, ServerConnectionResponse> multiplayerConnectTask;
    private ENG_Item selectionShip;
    private boolean lightCreated;
    private int spawnPoint;

    public static class ShipSelectionContainerFactory extends ENG_ContainerManager.ContainerFactory {

        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new ShipSelection(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    public static final String BUNDLE_SHIP_LIST = "ship_list";

    private int currentPos;

    public ShipSelection(String name, Bundle bundle) {
        super(name, bundle);


        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 15.0f);

        shipNameView = (ENG_TextView) createView("ship_name", "textview", 0.0f, 17.0f, 100.0f, 22.0f);
        shipSpeedView = (ENG_TextView) createView("ship_speed", "textview", 0.0f, 23.0f, 100.0f, 28.0f);
        shipArmorView = (ENG_TextView) createView("ship_armor", "textview", 0.0f, 29.0f, 100.0f, 34.0f);
        shipWeaponsView = (ENG_TextView) createView("ship_weapons", "textview", 0.0f, 35.0f, 100.0f, 55.0f);

        ENG_Button previousShipButton = (ENG_Button) createView(PREVIOUS_SHIP, "button", 0.0f, 52.0f, 30.0f, 62.0f);
        ENG_Button nextShipButton = (ENG_Button) createView(NEXT_SHIP, "button", 70.0f, 52.0f, 100.0f, 62.0f);
        ENG_Button doneButton = (ENG_Button) createView(SELECT_SHIP, "button", 0.0f, 87.0f, 100.0f, 100.0f);
        backButton = (ENG_Button) createView(BACK, "button", 80.0f, 75.0f, 100.0f, 85.0f);

        titleView.setText(SHIP_SELECTION_TITLE);
        previousShipButton.setText(SHIP_SELECTION_PREVIOUS);
        nextShipButton.setText(SHIP_SELECTION_NEXT);
        doneButton.setText(SHIP_SELECTION_DONE);
        backButton.setText(SHIP_SELECTION_BACK1);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        shipNameView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        shipSpeedView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        shipArmorView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        shipWeaponsView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        previousShipButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        nextShipButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        doneButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        backButton.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        previousShipButton.setTextColor(ENG_ColorValue.WHITE);
        previousShipButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        nextShipButton.setTextColor(ENG_ColorValue.WHITE);
        nextShipButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        doneButton.setTextColor(ENG_ColorValue.WHITE);
        doneButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        backButton.setTextColor(ENG_ColorValue.WHITE);
        backButton.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        titleView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);

        backButton.setVisible(false);

        previousShipButton.setOnClickListener((x, y) -> {
            prev();
            return true;
        });
        nextShipButton.setOnClickListener((x, y) -> {
            next();
            return true;
        });
        doneButton.setOnClickListener((x, y) -> {
            done();
            return true;
        });
        backButton.setOnClickListener((x, y) -> {
            onBackPressed();
            return true;
        });

    }

    @Override
    public void destroy(boolean skipRecreation, boolean skipGLDelete) {
        destroySelectedShip();
        destroyLight();
        super.destroy(skipRecreation, skipGLDelete);
    }

    public void setData(LevelBase level,
                        ArrayList<ShipData> shipList,
                        long mapId,
                        boolean multiplayer,
                        long sessionId,
                        String sessionName,
                        String serverIp,
                        int tcpPort,
                        int udpPort,
                        int spawnPoint, boolean createSession) {
        this.level = level;
        this.shipList = shipList;
        this.mapId = mapId;
        this.multiplayer = multiplayer;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.serverIp = serverIp;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.spawnPoint = spawnPoint;
        this.createSession = createSession;
        createLight();
        show();
    }

    private void next() {
        if ((++currentShipDataIndex) >= shipList.size()) {
            currentShipDataIndex = 0;
        }
        show();
    }

    private void prev() {
        if ((--currentShipDataIndex) < 0) {
            currentShipDataIndex = shipList.size() - 1;
        }
        show();
    }

    private void show() {
        headwayent.blackholedarksun.entitydata.ShipData shipData = shipList.get(currentShipDataIndex);

        shipNameView.setText(SHIP_SELECTION_NAME + shipData.name);
        shipSpeedView.setText(SHIP_SELECTION_MAX_SPEED + (int) shipData.maxSpeed);
        shipArmorView.setText(SHIP_SELECTION_ARMOR + shipData.health);
        StringBuilder str = new StringBuilder();
        String weapons = SHIP_SELECTION_WEAPONS;
        int numLines = 0;
        for (WeaponData.WeaponType wpn : shipData.weaponTypeList) {
            str.append(WeaponData.WeaponType.getWeapon(wpn)).append(", ");//.append("\n");
//            for (int i = 0; i < weapons.length(); ++i) {
//                str.append(" ");
//            }
            ++numLines;
        }
        if (shipData.weaponTypeList.isEmpty()) {
            ++numLines;
        }
        str.delete(str.length() - 2, str.length());
        String wpnStr = weapons + str.toString().trim();
        shipWeaponsView.setText(wpnStr);
        createSelectedShip(shipData);

        if (multiplayer) {
            backButton.setVisible(true);
        }
//        markDirty();
    }

    private ShipData getCurrentShipData() {
        return shipList.get(currentShipDataIndex);
    }

    private void done() {
        if (multiplayer) {
            if (MainApp.getMainThread().isInputState()) {
                FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                MainApp.getMainThread().getDebuggingState().registerOnFrameIntervalChangedListener(new ENG_State.OnFrameIntervalChangeListener() {
                    @Override
                    public void onFrameIntervalChanged(ENG_FrameInterval frameInterval) {
                        FrameInterval gameFrameInterval = (FrameInterval) frameInterval;
                        ArrayList<ServerConnectionResponse> serverConnectionResponseList = gameFrameInterval.getServerConnectionResponses();
                        if (!serverConnectionResponseList.isEmpty()) {
                            checkServerConnectionResponse(serverConnectionResponseList.get(0));
                            MainApp.getMainThread().getDebuggingState().unregisterOnFrameIntervalChangedListener(this);
                        }
                    }
                });
            } else {
                if (multiplayerConnectTask != null) {
                    return;
                }
                multiplayerConnectTask = new AsyncTask<Void, Integer, ServerConnectionResponse>() {

                    @Override
                    protected ServerConnectionResponse doInBackground(Void... params) {
                        CountDownLatch countDownLatch = new CountDownLatch(1);
                        User user = MainApp.getGame().getUser();
                        NetManager.getSingleton().connectToServer(getCurrentShipData().inGameName,
                                mapId, sessionId, sessionName, serverIp, tcpPort, udpPort, createSession,
                                user.getUsername(), user.getId(), spawnPoint, countDownLatch);
                        boolean result = false;
                        try {
                            result = countDownLatch.await(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!result) {
                            // We have a timeout or interrupt
                            ServerConnectionResponse response = new ServerConnectionResponse();
                            response.setErrorCode(ServerConnectionResponse.ERROR_SERVER_TIMEOUT);
                            NetManager.getSingleton().setServerConnectionResponse(response);
                        }
                        return NetManager.getSingleton().getServerConnectionResponse();
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(final ServerConnectionResponse serverConnectionResponse) {
                        super.onPostExecute(serverConnectionResponse);
                        if (serverConnectionResponse != null) {
                            if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
                                MainApp.getMainThread().getDebuggingState().registerOnFrameIntervalChangedListener(new ENG_State.OnFrameIntervalChangeListener() {
                                    @Override
                                    public void onFrameIntervalChanged(ENG_FrameInterval frameInterval) {
                                        ((FrameInterval) frameInterval).getServerConnectionResponses().add(serverConnectionResponse);
                                        MainApp.getMainThread().getDebuggingState().unregisterOnFrameIntervalChangedListener(this);
                                    }
                                });
                            }
                            checkServerConnectionResponse(serverConnectionResponse);
                        } else {
                            // Connection timeout.
                        }
                        multiplayerConnectTask = null;
                    }
                };
                multiplayerConnectTask.execute();
            }

        } else {
            startSingleplayerGame();
        }

    }

    private void checkServerConnectionResponse(ServerConnectionResponse serverConnectionResponse) {
        if (serverConnectionResponse.getErrorCode() == ServerConnectionResponse.NO_ERROR) {
            if (mapId != serverConnectionResponse.getMapId()) {
                // The map has been changed before we selected the ship and attempted to join the server.
            } else {
                startMultiplayerGame();
            }
        } else {
            showToast(SHIP_SELECTION_COULD_NOT_JOIN_SERVER + getErrorString(serverConnectionResponse));
        }
    }

    private String getErrorString(ServerConnectionResponse serverConnectionResponse) {
        switch (serverConnectionResponse.getErrorCode()) {
            case ServerConnectionResponse.ERROR_SERVER_ALREADY_STARTED:
                return ServerConnectionResponse.ERROR_SERVER_ALREADY_STARTED_STR;
            case ServerConnectionResponse.ERROR_CODE_PLAYER_ALREADY_ADDED:
                return ServerConnectionResponse.ERROR_CODE_PLAYER_ALREADY_ADDED_STR;
            case ServerConnectionResponse.ERROR_GAME_TYPE_NOT_COMPATIBLE:
                return ServerConnectionResponse.ERROR_GAME_TYPE_NOT_COMPATIBLE_STR;
            case ServerConnectionResponse.ERROR_SERVER_NOT_INITIALIZED:
                return ServerConnectionResponse.ERROR_SERVER_NOT_INITIALIZED_STR;
            case ServerConnectionResponse.ERROR_SERVER_NOT_JOINED:
                return ServerConnectionResponse.ERROR_SERVER_NOT_JOINED_STR;
            case ServerConnectionResponse.ERROR_SERVER_TIMEOUT:
                return ServerConnectionResponse.ERROR_SERVER_TIMEOUT_STR;
            default:
                return "";
        }
    }

    private void startMultiplayerGame() {
        destroySelectedShip();
        ENG_ContainerManager.getSingleton().removeCurrentContainer();

        // Clear the WorldManagerSP and load WorldManagerMP instead.
        WorldManager.getSingleton().resetWorld();
        WorldManagerMP worldManagerMP = new WorldManagerMP();
        worldManagerMP.setSceneManager(ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER));

        APP_Game game = MainApp.getGame();
//        LevelBase currentLevel = GameWorld.getWorld().getCurrentLevel();
        ((APP_SinglePlayerGame) game).setWorldManager(worldManagerMP);
        ((APP_SinglePlayerGame) game).initializeWorld();
        WorldManager.getSingleton().setPlayerShipData(getCurrentShipData());
        GameWorld.getWorld().setCurrentLevel(level);

        GameWorld.getWorld().getSystem(MovementSystem.class).createThreads();

        setProgress(30);
        MainApp.getGame().showLoadingScreen();
        LevelLoader.loadLevel(level);
        MainApp.getGame().hideLoadingScreen();

        NetManager netManager = NetManager.getSingleton();
        // We can now start pushing the server frames to the WorldManagerMP.
        if (!MainApp.getMainThread().isInputState()) {
            netManager.getServerListener().setWaitForLoading(false);
        }

        User user = MainApp.getGame().getUser();
        UserStatsList userStatsList = (UserStatsList) netManager.getRemoteObject((int) user.getId(), UserStatsList.class);
        worldManagerMP.setUserStatsList(userStatsList);

    }

    private void startSingleplayerGame() {
        destroySelectedShip();
        ENG_ContainerManager.getSingleton().removeCurrentContainer();
        WorldManager.getSingleton().setPlayerShipData(getCurrentShipData());



        APP_Game game = MainApp.getGame();
        ((APP_SinglePlayerGame) game).initializeWorld();
        GameWorld.getWorld().getSystem(MovementSystem.class).createThreads();
        MainApp.getGame().showLoadingScreen();
//        LevelLoader.loadLevel(mapId, multiplayer ? game.getMultiPlayerLevelList() : game.getSinglePlayerLevelList());
        LevelLoader.loadLevel(level);
        MainApp.getGame().hideLoadingScreen();
//        SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.IN_GAME_OVERLAY);
        // Set the scroll overlay in the in game listener
//        HudManager.getSingleton().getSpeedScrollOverlay().setPercentage(getCurrentShipData().initialSpeedPercentual);
    }

    private void createLight() {
        Light light = LightingManager.getSingleton().createDirectionalLight(LIGHT, "LightNode", 1.0f,
                new ENG_ColorValue(0.8f, 0.4f, 0.2f), // diffuse
                new ENG_ColorValue(0.8f, 0.4f, 0.2f), // specular
                new ENG_Vector4D(1.0f, 0.0f, 0.0f, 0.0f));// dir
        LightingManager.getSingleton().setAmbientLight(
                new ENG_ColorValue(0.3f, 0.5f, 0.7f).mul(0.1f).mul(0.75f), // upperHemi
                new ENG_ColorValue(0.6f, 0.45f, 0.3f).mul(0.065f).mul(0.75f), // lowerHemi
                new ENG_Vector3D(light.getLight().getDirectionCopy().mulAsVec(-1.0f).addAsVec(ENG_Math.VEC4_Y_UNIT.mulAsVec(0.2f)))); // hemiDir

        lightCreated = true;
    }

    private void destroyLight() {
        if (lightCreated) {
            LightingManager.getSingleton().destroyLight(LIGHT);
            lightCreated = false;
        }
    }

    private void createSelectedShip(EntityData shipData) {
        destroySelectedShip();
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager("Main");
        selectionShip = sceneManager.createItem("SelectionShip", ENG_Utility.getUniqueId(),
                /*FilenameUtils.getBaseName(shipData.filename)*/shipData.filename, "", ENG_Workflows.MetallicWorkflow);
//        ENG_Entity entity = sceneManager.createEntity("SelectionShip", FilenameUtils.getBaseName(shipData.filename), "");
//        selectionShip.setDatablockName("Rocks");
//        selectionShip.setVisibilityFlag( 0x000000001 );
        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode("ShipForSelection");
//        node.attachObject(entity);
        node.attachObject(selectionShip);
        node.setPosition(0.0f, -20.0f, -200.0f);
        node.setOrientation(ENG_Quaternion.fromAngleAxisDegRet(20.0f, ENG_Math.VEC4_X_UNIT));



//		ENG_AnimationState state = entity.getAnimationState("Default");
//		state.setEnabled(true);
//		state.setLoop(true);
        WorldManager.getSingleton().setNodeAutoRotate(0.0f, 1.0f, 0.0f, 10.0f, node);
        shipCreated = true;
    }

    private void destroySelectedShip() {
        if (shipCreated || MainApp.getGame().isDestroyPreviousShipSelection()) {
            WorldManager.getSingleton().removeNodeAutoRotate("ShipForSelection");
            ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager("Main");
            sceneManager.getRootSceneNode().removeAndDestroyChild("ShipForSelection");
//            sceneManager.destroyEntity("SelectionShip", MainApp.getGame().isDestroyPreviousShipSelection());
            sceneManager.destroyItem(selectionShip);
            selectionShip = null;
            MainApp.getGame().setDestroyPreviousShipSelection(false);
            shipCreated = false;
        }
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        recreateContainerListeners(previousContainer);
    }

    private void onBackPressed() {
        ENG_ContainerManager.getSingleton().setPreviousContainer();
//        ENG_ContainerManager.getSingleton().setCurrentContainer(SimpleViewGameMenuManager.MULTIPLAYER_JOIN_SESSION, true, false);
    }
}
