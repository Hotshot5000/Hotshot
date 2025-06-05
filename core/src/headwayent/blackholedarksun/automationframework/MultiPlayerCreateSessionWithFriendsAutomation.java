/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.GenericMenu;
import headwayent.blackholedarksun.menus.MultiplayerCreateSessionWithFriends;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TableView;

public class MultiPlayerCreateSessionWithFriendsAutomation extends AutomationFramework {

    public static final String NAME = "multiplayer_create_session_with_friends";

    public static final String PARAM_LOGGED_IN = "logged_in";
    public static final String PARAM_CREATE_LOBBY = "create_lobby";
    public static final String PARAM_INVITE_FRIEND = "invite_friend";
    public static final String PARAM_SERVER_CREATED = "create_server_created";

    private enum State {
        MAIN_MENU, MULTIPLAYER_MENU_SIGN_OUT, MULTIPLAYER_MENU_LOGIN, ACCOUNT_LOGIN, WAIT_FOR_LOGIN, MULTIPLAYER_MENU_CREATE_LOBBY,
        WAIT_FOR_FRIEND_LIST_LOAD, CREATE_LOBBY, WAIT_FOR_FRIEND_INVITE, LOBBY, SHIP_SELECTION, IN_GAME
    }

    private State state = State.MAIN_MENU;

    public MultiPlayerCreateSessionWithFriendsAutomation() {
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
                AutomationFrameworkUtils.login("Sebi", "123456");
                state = State.WAIT_FOR_LOGIN;
            }
                break;
            case WAIT_FOR_LOGIN: {

            }
                break;
            case MULTIPLAYER_MENU_CREATE_LOBBY: {
                GenericMenu multiplayerMenu = (GenericMenu) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_LOGGED_IN_MENU);
                ENG_Button createLobby = multiplayerMenu.getButton(SimpleViewGameMenuManager.CREATE_LOBBY);
                createLobby.handleOnClickListener(0, 0);
                state = State.WAIT_FOR_FRIEND_LIST_LOAD;
            }
                break;
            case WAIT_FOR_FRIEND_LIST_LOAD:
                break;
            case CREATE_LOBBY: {
                MultiplayerCreateSessionWithFriends createSessionWithFriendsMenu =
                        (MultiplayerCreateSessionWithFriends) containerManager.getContainer(SimpleViewGameMenuManager.MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS);
                ENG_TableView friendsTable = (ENG_TableView) createSessionWithFriendsMenu._getView(MultiplayerCreateSessionWithFriends.FRIENDS_TABLE);
                ENG_Button inviteFriendButton = (ENG_Button) createSessionWithFriendsMenu._getView(MultiplayerCreateSessionWithFriends.INVITE_FRIEND_BUTTON);
                ENG_Button okButton = (ENG_Button) createSessionWithFriendsMenu._getView(MultiplayerCreateSessionWithFriends.OK_BUTTON);
                ArrayList<ENG_RealRect> currentRowsRect = friendsTable.getCurrentRowsRect();
                // Wait until the data becomes available.
                if (currentRowsRect.isEmpty()) {
                    break;
                }
                friendsTable._setSelectedRow(0, currentRowsRect.get(0));
                inviteFriendButton.handleOnClickListener(0, 0);
                okButton.handleOnClickListener(0, 0);
                state = State.WAIT_FOR_FRIEND_INVITE;
            }
                break;
            case WAIT_FOR_FRIEND_INVITE:
                break;
            case LOBBY: {
                MainApp.getMainThread().runOnMainThread(() -> MainApp.getMainThread().removeAutomation(NAME));
            }
                break;
            case SHIP_SELECTION: {
                AutomationFrameworkUtils.selectShip();
                state = State.IN_GAME;
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
                state = State.MULTIPLAYER_MENU_CREATE_LOBBY;
                break;
            case PARAM_CREATE_LOBBY:
                state = State.CREATE_LOBBY;
                break;
            case PARAM_INVITE_FRIEND:
                state = State.LOBBY;
                break;
            case PARAM_SERVER_CREATED:
                state = State.SHIP_SELECTION;
                break;
        }
    }
}
