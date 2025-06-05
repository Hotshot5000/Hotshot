/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 4:51 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.blackholedarksun.menus.MultiplayerJoinSessionWithFriends;
import headwayent.blackholedarksun.menus.MultiplayerLobby;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TableView;

public class MultiPlayerJoinSessionWithFriendsAutomation extends AutomationFramework {

    public static final String NAME = "multiplayer_join_session_with_friends";

    public static final String PARAM_LOGGED_IN = "logged_in";
    public static final String PARAM_JOIN_LOBBY = "join_lobby";
    public static final String PARAM_SERVER_CREATED = "server_created";
    public static final String PARAM_SESSION_READY_TO_START = "session_ready_to_start";

    private enum State {
        MAIN_MENU, MULTIPLAYER_MENU_SIGN_OUT, MULTIPLAYER_MENU_LOGIN, ACCOUNT_LOGIN, WAIT_FOR_LOGIN,
        MULTIPLAYER_MENU_JOIN_LOBBY, WAIT_FOR_INVITATION_LIST_TO_LOAD, JOIN_LOBBY, WAIT_FOR_LOBBY_READY_TO_START, LOBBY,
        WAIT_FOR_SERVER_INIT_RESPONSE, SHIP_SELECTION, IN_GAME
    }

    private State state = State.MAIN_MENU;
    private long shipSelectionBeginTime;

    public MultiPlayerJoinSessionWithFriendsAutomation() {
        super(NAME);
    }

    @Override
    public void execute() {
        ENG_ContainerManager containerManager = ENG_ContainerManager.getSingleton();
        switch (state) {

            case MAIN_MENU: {
                AutomationFrameworkUtils.goToMultiplayerMenu();
                state = State.MULTIPLAYER_MENU_SIGN_OUT;
            }
                break;
            case MULTIPLAYER_MENU_SIGN_OUT: {
                AutomationFrameworkUtils.signOut();
                state = State.MULTIPLAYER_MENU_LOGIN;
            }
                break;
            case MULTIPLAYER_MENU_LOGIN: {
                AutomationFrameworkUtils.goToLoginMenu();
                state = State.ACCOUNT_LOGIN;
            }
                break;
            case ACCOUNT_LOGIN: {
                AutomationFrameworkUtils.login("John", "123456");
                state = State.WAIT_FOR_LOGIN;
            }
                break;
            case WAIT_FOR_LOGIN:
                break;
            case MULTIPLAYER_MENU_JOIN_LOBBY: {
                GenericMenu multiplayerMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_LOGGED_IN_MENU);
                ENG_Button createLobby = multiplayerMenu.getButton(SimpleViewGameMenuManager.JOIN_SESSION_WITH_FRIENDS_BUTTON);
                createLobby.handleOnClickListener(0, 0);
                state = State.WAIT_FOR_INVITATION_LIST_TO_LOAD;
            }
                break;
            case WAIT_FOR_INVITATION_LIST_TO_LOAD:
                break;
            case JOIN_LOBBY: {
                MultiplayerJoinSessionWithFriends joinSessionWithFriendsMenu =
                        (MultiplayerJoinSessionWithFriends) containerManager.getContainer(
                                SimpleViewGameMenuManager.MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS);
                ENG_TableView lobbyTable = (ENG_TableView) joinSessionWithFriendsMenu._getView(MultiplayerJoinSessionWithFriends.LOBBY_TABLE);
                ENG_Button okButton = (ENG_Button) joinSessionWithFriendsMenu._getView(MultiplayerJoinSessionWithFriends.OK_BUTTON);
                ENG_Button cancelButton = (ENG_Button) joinSessionWithFriendsMenu._getView(MultiplayerJoinSessionWithFriends.CANCEL_BUTTON);
                ArrayList<ENG_RealRect> currentRowsRect = lobbyTable.getCurrentRowsRect();
                // Wait until the data becomes available.
                if (currentRowsRect.isEmpty()) {
                    break;
                }
                lobbyTable._setSelectedRow(0, currentRowsRect.get(0));
                okButton.handleOnClickListener(0, 0);
                state = State.WAIT_FOR_LOBBY_READY_TO_START;
            }
                break;
            case WAIT_FOR_LOBBY_READY_TO_START:
                break;
            case LOBBY: {
                MultiplayerLobby lobby = (MultiplayerLobby) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_LOBBY);
                ENG_Button okButton = (ENG_Button) lobby._getView(MultiplayerLobby.OK_BUTTON);
                ENG_Button cancelButton = (ENG_Button) lobby._getView(MultiplayerLobby.CANCEL_BUTTON);
//                okButton.handleOnClickListener(0, 0);
                state = State.WAIT_FOR_SERVER_INIT_RESPONSE;
            }
                break;
            case WAIT_FOR_SERVER_INIT_RESPONSE:
                break;
            case SHIP_SELECTION: {
                if (shipSelectionBeginTime == 0) {
                    shipSelectionBeginTime = ENG_Utility.currentTimeMillis();
                } else if (ENG_Utility.hasTimePassed(shipSelectionBeginTime, 3000)) {
                    AutomationFrameworkUtils.selectShip();
                    state = State.IN_GAME;
                }
            }
                break;
            case IN_GAME: {
                MainApp.getMainThread().runOnMainThread(() -> MainApp.getMainThread().removeAutomation(NAME));
            }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void notifyParameterSet(String name) {
        switch (name) {
            case PARAM_LOGGED_IN:
                state = State.MULTIPLAYER_MENU_JOIN_LOBBY;
                break;
            case PARAM_JOIN_LOBBY:
                state = State.JOIN_LOBBY;
                break;
            case PARAM_SERVER_CREATED:
                state = State.SHIP_SELECTION;
                break;
            case PARAM_SESSION_READY_TO_START:
                state = State.LOBBY;
                break;
        }
    }
}
