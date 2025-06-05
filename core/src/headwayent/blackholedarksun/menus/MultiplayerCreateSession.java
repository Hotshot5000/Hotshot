/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 9:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import com.google.common.eventbus.Subscribe;
import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.automationframework.MultiPlayerCreateSessionWithFriendsAutomation;
import headwayent.blackholedarksun.automationframework.MultiPlayerJoinSessionWithFriendsAutomation;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.automationframework.MultiPlayerCreateServerMenuAutomation;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerCreateSessionContainerListener;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ShipDataInjectorContainerListener;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.ErrorCodes;
import headwayent.blackholedarksun.net.clientapi.tables.Server;
import headwayent.blackholedarksun.net.clientapi.tables.Session;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.*;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sebastian on 26.04.2015.
 */
public class MultiplayerCreateSession extends ENG_Container {

    public static final String MAP_LIST = "map_list";
    public static final String PLAYER_NUM = "player_num_list";
    public static final String TEAM_LIST = "team_list";
    public static final String SESSION_NAME_TEXT_FIELD = "sessionName";
    public static final String MAP_SELECTION_DROPDOWN_LIST = "mapSelection";
    public static final String PLAYER_NUM_SELECTION_DROPDOWN_LIST = "playerNumSelection";
    public static final String TEAM_SELECTION_DROPDOWN_LIST = "teamSelection";
    public static final String CREATE_SESSION_BUTTON = "login";
    public static final String CANCEL_BUTTON = "cancel";

    //    private final List<String> mapNames;
    private final HashMap<String, Long> mapNameToId = new HashMap<>();
    private Long selectedMapId;
    private String sessionName;
    private boolean clicked;

    public static class MultiplayerCreateSessionContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerCreateSession(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /**
     * @param name
     * @param bundle The bundle represents the map list in "map_list" key.
     * @noinspection UnstableApiUsage, deprecation
     */
    public MultiplayerCreateSession(String name, Bundle bundle) {
        super(name, bundle);

        Bundle bundle1 = bundle.getBundle(MAP_LIST);
        List<Long> mapIds = (List<Long>) bundle1.getObject(ENG_DropdownList.TEXT_LIST);
//        mapNames = (List<String>) bundle1.getObject(ENG_DropdownList.TEXT_LIST);
//        for (int i = 0; i)
        ArrayList<String> mapNameList = new ArrayList<>();
        final APP_Game game = MainApp.getGame();
        for (Long l : mapIds) {
            String mapNameById = game.getMapNameById(l);
            mapNameList.add(mapNameById);
            mapNameToId.put(mapNameById, l);
        }
        Bundle dropdownListBundle = new Bundle();
        dropdownListBundle.putObject(ENG_DropdownList.TEXT_LIST, mapNameList);
//        dropdownListBundle.putObject(ENG_DropdownList.TEXT_LIST, mapNames);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView sessionNameTextView = (ENG_TextView) createView("sessionNameText", "textview", 0.0f, 22.0f, 100.0f, 27.0f);
        final ENG_TextField sessionNameTextField = (ENG_TextField) createView(SESSION_NAME_TEXT_FIELD, "textfield", 30.0f, 29.0f, 70.0f, 34.0f);
        ENG_TextView mapSelectionTextView = (ENG_TextView) createView("mapSelectionText", "textview", 0.0f, 36.0f, 100.0f, 41.0f);
        final ENG_DropdownList mapSelectionDropdownList = (ENG_DropdownList) createView(MAP_SELECTION_DROPDOWN_LIST, "dropdownlist",
                30.0f, 43.0f, 70.0f, 48.0f, dropdownListBundle);
        ENG_TextView playerNumSelectionTextView = (ENG_TextView) createView("playerNumSelectionText", "textview", 0.0f, 50.0f, 100.0f, 55.0f);
        final ENG_DropdownList playerNumSelectionDropdownList = (ENG_DropdownList) createView(PLAYER_NUM_SELECTION_DROPDOWN_LIST, "dropdownlist",
                        30.0f, 57.0f, 70.0f, 62.0f, bundle.getBundle(PLAYER_NUM));
        ENG_TextView teamSelectionTextView = (ENG_TextView) createView("teamSelectionText", "textview", 0.0f, 64.0f, 100.0f, 69.0f);
        final ENG_DropdownList teamSelectionDropdownList = (ENG_DropdownList) createView(TEAM_SELECTION_DROPDOWN_LIST, "dropdownlist",
                        30.0f, 71.0f, 70.0f, 76.0f, bundle.getBundle(TEAM_LIST));
//        ENG_ProgressBar progressBar = (ENG_ProgressBar)
//                createView("progressbar", "progressbar",
//                        80.0f, 71.0f, 99.0f, 76.0f);
        ENG_Button create = (ENG_Button) createView(CREATE_SESSION_BUTTON, "button", 0.0f, 82.0f, 100.0f, 90.0f);
        ENG_Button cancel = (ENG_Button) createView(CANCEL_BUTTON, "button", 0.0f, 92.0f, 100.0f, 100.0f);

//        progressBar.setProgress(30);\

//        setProgress(50);

        titleView.setText(MULTIPLAYER_CREATE_SESSION_TITLE);
        sessionNameTextView.setText(MULTIPLAYER_CREATE_SESSION_SESSION_NAME);
        mapSelectionTextView.setText(MULTIPLAYER_CREATE_SESSION_SELECT_MAP);
        playerNumSelectionTextView.setText(MULTIPLAYER_CREATE_SESSION_SELECT_MAX_PLAYERS_NUMBER_THAT_CAN_JOIN);
        teamSelectionTextView.setText(MULTIPLAYER_CREATE_SESSION_SELECT_TEAM);
        sessionNameTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        mapSelectionTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        playerNumSelectionTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        teamSelectionTextView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);

        create.setText(MULTIPLAYER_CREATE_SESSION_CREATE_SESSION);
        cancel.setText(MULTIPLAYER_CREATE_SESSION_CANCEL);
        create.setTextColor(ENG_ColorValue.WHITE);
        cancel.setTextColor(ENG_ColorValue.WHITE);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        sessionNameTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        sessionNameTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        mapSelectionTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        mapSelectionDropdownList.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        playerNumSelectionTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        playerNumSelectionDropdownList.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        teamSelectionTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        teamSelectionDropdownList.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        create.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        setCurrentFocusedView(sessionNameTextField);

        // 4 players default
        playerNumSelectionDropdownList.setCurrentElement(2);

        create.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        cancel.setOnClickListener((x, y) -> {
            onBackPressed();
            return true;
        });

        create.setOnClickListener((x, y) -> {
            if (clicked) {
                return true;
            }
            clicked = true;
            sessionName = sessionNameTextField.getText();
            if (sessionName.isEmpty()) {
                showToast(MULTIPLAYER_CREATE_SESSION_PLEASE_ENTER_A_SESSION_NAME);
                clicked = false;
                return true;
            }
            String currentMapName = mapSelectionDropdownList.getCurrentElementText();
            selectedMapId = mapNameToId.get(currentMapName);
            String playerNumString = playerNumSelectionDropdownList.getCurrentElementText();
            int maxPlayerNum = Integer.parseInt(playerNumString);
            Session session = new Session();
            session.setSessionName(sessionName);
            session.setMapId(selectedMapId);
            session.setMaxPlayerNum(maxPlayerNum);
            session.setGameType(MainApp.GameType.getActiveGameType().getGameType());
            User user = game.getUser();

            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerCreateSessionContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.CreateSessionEvent(user.getAuthToken(), session));
                    break;
                }
            }
//                ((ContainerListenerWithBus) getListeners().get(0)).getBus().post(new ClientAPI.LeaveSessionEvent());
            return true;
        });
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionCreated(ClientAPI.SessionCreatedEvent event) {
        Server server = event.session;
        if (clicked) {
            onSessionAccepted(server, selectedMapId, sessionName, true);
        }
        clicked = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionCreatedError(ClientAPI.SessionCreationErrorEvent event) {
        clicked = false;
        showToast(MULTIPLAYER_CREATE_SESSION_COULD_NOT_CREATE_THE_SESSION_TRY_AGAIN);
    }

    /** @noinspection deprecation */
    public static void onSessionAccepted(Server server, Long selectedMapId, String sessionName, boolean createSession) {

        if (server.getErrorCode() == ErrorCodes.NO_ERROR) {
            NetManager.getSingleton().setServer(server);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ShipDataInjectorContainerListener.BUNDLE_MULTIPLAYER, true);
            bundle.putInt(ShipDataInjectorContainerListener.BUNDLE_TEAM, ShipData.ShipTeam.HUMAN.getTeamNum());
            bundle.putInt(ShipDataInjectorContainerListener.BUNDLE_LEVEL_NUM, (int) MainApp.getGame().getMapPosInTitleListByServerId(selectedMapId));
            bundle.putLong(ShipDataInjectorContainerListener.BUNDLE_MAP_ID, selectedMapId);
            bundle.putString(ShipDataInjectorContainerListener.BUNDLE_SESSION_NAME, sessionName);
            bundle.putLong(ShipDataInjectorContainerListener.BUNDLE_SESSION_ID, server.getSessionId());
            bundle.putString(ShipDataInjectorContainerListener.BUNDLE_SERVER_IP, server.getIp());
            bundle.putInt(ShipDataInjectorContainerListener.BUNDLE_TCP_PORT, Integer.parseInt(server.getTcpPort()));
            bundle.putInt(ShipDataInjectorContainerListener.BUNDLE_UDP_PORT, Integer.parseInt(server.getUdpPort()));
            bundle.putInt(ShipDataInjectorContainerListener.BUNDLE_SPAWN_POINT, server.getSpawnPoint());
            bundle.putBoolean(ShipDataInjectorContainerListener.BUNDLE_CREATE_SESSION, createSession);
            ENG_ContainerManager.getSingleton().createContainerListener(
                    SimpleViewGameMenuManager.SHIP_SELECTION,
                    ShipDataInjectorContainerListener.ShipDataInjectorContainerListenerFactory.TYPE,
                    bundle);
            SimpleViewGameMenuManager.setCurrentMenu(SimpleViewGameMenuManager.SHIP_SELECTION);

            if (MainApp.getMainThread().isAutomationEnabled(MultiPlayerCreateServerMenuAutomation.NAME)) {
                MainApp.getMainThread().setParameterForAutomation(
                        MultiPlayerCreateServerMenuAutomation.NAME, MultiPlayerCreateServerMenuAutomation.PARAM_SERVER_CREATED, true);
            }

            if (MainApp.getMainThread().isAutomationEnabled(MultiPlayerCreateSessionWithFriendsAutomation.NAME)) {
                MainApp.getMainThread().setParameterForAutomation(
                        MultiPlayerCreateSessionWithFriendsAutomation.NAME, MultiPlayerCreateSessionWithFriendsAutomation.PARAM_SERVER_CREATED, true);
            }
            if (MainApp.getMainThread().isAutomationEnabled(MultiPlayerJoinSessionWithFriendsAutomation.NAME)) {
                MainApp.getMainThread().setParameterForAutomation(
                        MultiPlayerJoinSessionWithFriendsAutomation.NAME, MultiPlayerJoinSessionWithFriendsAutomation.PARAM_SERVER_CREATED, true);
            }
        }
    }

    @Override
    public void onRecreation(ENG_Container previousContainer) {
        super.onRecreation(previousContainer);
        recreateContainerListeners(previousContainer);
    }

    private void onBackPressed() {
        ENG_ContainerManager.getSingleton().setPreviousContainer();
    }
}
