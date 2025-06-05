/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:37 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import java.util.ArrayList;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.levelresource.LevelPlayerShipSelection;
import headwayent.blackholedarksun.loaders.LevelLoader;
import headwayent.blackholedarksun.menus.ShipSelection;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by Sebi on 19.05.2014.
 */
public class ShipDataInjectorContainerListener extends ENG_Container.ContainerListener {

    public static class ShipDataInjectorContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "ShipDataInjector";

        @Override
        public ENG_Container.ContainerListener createContainerListener(
                ENG_Container container, Bundle bundle) {
            return new ShipDataInjectorContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    public static final String BUNDLE_LEVEL_NUM = "level";
    public static final String BUNDLE_MAP_ID = "map_id";
    public static final String BUNDLE_MULTIPLAYER = "multiplayer";
    public static final String BUNDLE_TEAM = "ship_team";
    public static final String BUNDLE_MULTIPLAYER_MAP = "multiplayer_map";
    public static final String BUNDLE_SESSION_NAME = "session_name";
    public static final String BUNDLE_SERVER_IP = "server_ip";
    public static final String BUNDLE_TCP_PORT = "tcp_port";
    public static final String BUNDLE_UDP_PORT = "udp_port";
    public static final String BUNDLE_SESSION_ID = "session_id";
    public static final String BUNDLE_CREATE_SESSION = "create_session"; // Create session (true) or join session (false).
    public static final String BUNDLE_SPAWN_POINT = "spawn_point";

    public ShipDataInjectorContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void preContainerUpdate() {

        ShipSelection container = (ShipSelection) getParentContainer();
    }

    @Override
    public void postContainerUpdate() {

    }

    /** @noinspection deprecation*/
    @Override
    public void onActivation() {
        Bundle bundle = getBundle();
        boolean multiplayer = bundle.getBoolean(BUNDLE_MULTIPLAYER);
        int teamNum = bundle.getInt(BUNDLE_TEAM);
        int levelNum = bundle.getInt(BUNDLE_LEVEL_NUM, -1);
        if (levelNum == -1) {
            throw new IllegalArgumentException("add level in bundle");
        }
        long mapId = bundle.getLong(BUNDLE_MAP_ID, -1);
        if (multiplayer && mapId == -1) {
            throw new IllegalArgumentException("map id missing");
        }
        String serverIp = bundle.getString(BUNDLE_SERVER_IP);
        if (multiplayer && serverIp == null) {
            throw new IllegalArgumentException("server ip missing");
        }
        int tcpPort = bundle.getInt(BUNDLE_TCP_PORT, -1);
        if (multiplayer && tcpPort == -1) {
            throw new IllegalArgumentException("tcp port missing");
        }
        int udpPort = bundle.getInt(BUNDLE_UDP_PORT, -1);
        if (multiplayer && udpPort == -1) {
            throw new IllegalArgumentException("udp port missing");
        }
        String sessionName = bundle.getString(BUNDLE_SESSION_NAME);
        if (multiplayer && sessionName == null) {
            throw new IllegalArgumentException("session name missing");
        }
        long sessionId = bundle.getLong(BUNDLE_SESSION_ID, -1);
        if (multiplayer && sessionId == -1) {
            throw new IllegalArgumentException("session id missing");
        }
        int spawnPoint = bundle.getInt(BUNDLE_SPAWN_POINT, -1);
        if (multiplayer && spawnPoint == -1) {
            throw new IllegalArgumentException("spawn point missing");
        }
        boolean createSession = bundle.getBoolean(BUNDLE_CREATE_SESSION);
        headwayent.blackholedarksun.entitydata.ShipData.ShipTeam shipTeam = headwayent.blackholedarksun.entitydata.ShipData.ShipTeam.getTeamByNum(teamNum);

        ArrayList<headwayent.blackholedarksun.entitydata.ShipData> shipDataList = new ArrayList<>();

        APP_Game game = MainApp.getGame();
//        MainApp.getGame().showLoadingScreen();
        LevelBase level = LevelLoader.compileLevel(levelNum, multiplayer ?
                game.getMultiPlayerLevelList() : game.getSinglePlayerLevelList(), multiplayer);
        LevelPlayerShipSelection playerShipSelection = level.getLevelStart().playerShipSelectionMap.get(shipTeam);
//        ArrayList<ENG_ModelResource> modelResources = new ArrayList<>();
        for (String shipName : playerShipSelection.shipNameList) {
            shipDataList.add(MainApp.getGame().getNameToShipMap(shipName));
//            modelResources.add(game.getShipResource(shipName));
        }

//        ENG_ModelLoader.loadModelResourceListModernized(modelResources, WorldManagerBase.LOAD_FROM_SDCARD);

//        MainApp.getGame().hideLoadingScreen();

        ShipSelection container = (ShipSelection) getParentContainer();
        container.setData(level, shipDataList, mapId, multiplayer, sessionId, sessionName, serverIp, tcpPort, udpPort, spawnPoint, createSession);
    }

    @Override
    public void onDestruction() {

    }
}
