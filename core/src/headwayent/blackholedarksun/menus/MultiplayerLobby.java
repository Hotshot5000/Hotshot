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

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.automationframework.MultiPlayerJoinSessionWithFriendsAutomation;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerLobbyContainerListener;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.Lobby;
import headwayent.blackholedarksun.net.clientapi.tables.Server;
import headwayent.blackholedarksun.net.clientapi.tables.Session;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.net.clientapi.viewdatas.LobbyFriendViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TableView;
import headwayent.hotshotengine.gui.simpleview.ENG_TextField;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class MultiplayerLobby extends ENG_Container {

    public static final int COLUMN_COUNT = 2;
    public static final String FRIENDS_TABLE = "friends_table";
    public static final String OK_BUTTON = "ok";
    public static final String CANCEL_BUTTON = "cancel";
    private final ENG_TableView friendsTableView;
    private int selectedRowNum = -1;
    private Lobby.LobbyStatus lobbyStatus;
    private ArrayList<LobbyFriendViewData> lobbyFriendViewData;
    private boolean onBackPressed;
    private boolean okClicked;

    public static class MultiplayerLobbyFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerLobby(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection UnstableApiUsage, deprecation */
    public MultiplayerLobby(String name, Bundle bundle) {
        super(name, bundle);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView friendSearchTextView = (ENG_TextView) createView("friendSearchText", "textview", 0.0f, 22.0f, 50.0f, 27.0f);
        final ENG_TextField friendSearchTextField = (ENG_TextField) createView("sessionSearch", "textfield", 10.0f, 29.0f, 35.0f, 35.0f);
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(MULTIPLAYER_LOBBY_FRIENDS);
        columnNames.add(MULTIPLAYER_LOBBY_JOINED);
        Bundle tableBundle = new Bundle();
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_COUNT, COLUMN_COUNT);
        tableBundle.putObject(ENG_TableView.BUNDLE_COLUMN_NAME_LIST, columnNames);
//        tableBundle.putFloat(ENG_TableView.BUNDLE_COLUMN_NAME_HEIGHT, 20.0f);
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_NAME_TEXT_SIZE, MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        friendsTableView = (ENG_TableView) createView(FRIENDS_TABLE, "tableview", 10.0f, 50.0f, 90.0f, 80.0f, tableBundle);
        ENG_Button inviteFriend = (ENG_Button) createView("invite_friend", "button", 52.0f, 22.0f, 100.0f, 27.0f);
        ENG_Button ok = (ENG_Button) createView(OK_BUTTON, "button", 0.0f, 82.0f, 100.0f, 90.0f);
        ENG_Button cancel = (ENG_Button) createView(CANCEL_BUTTON, "button", 0.0f, 92.0f, 100.0f, 100.0f);

        titleView.setText(MULTIPLAYER_LOBBY_TITLE);
//		titleView.setEllipsize(ENG_TextView.Ellipsize.END);
//        titleView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        friendSearchTextView.setText(MULTIPLAYER_LOBBY_ADD_FRIENDS_TO_LOBBY);
        friendSearchTextField.setText("");
        inviteFriend.setText(MULTIPLAYER_LOBBY_INVITE_FRIEND);
        ok.setText(MULTIPLAYER_LOBBY_START_GAME);
        cancel.setText(MULTIPLAYER_LOBBY_BACK);
        ok.setTextColor(ENG_ColorValue.WHITE);
        cancel.setTextColor(ENG_ColorValue.WHITE);

        inviteFriend.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        friendSearchTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        friendSearchTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        friendsTableView.setTextSize(MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        ok.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        ok.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        friendsTableView.setRowHeightType(ENG_TableView.RowHeightType.TABLE_BASED);
        friendsTableView.setTextFieldNumInTable(3);

        friendsTableView.setColumnNamesHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        friendsTableView.setColumnNamesVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
        friendsTableView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        friendsTableView.setVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
//        friendsTableView.setBoxRectangleBorder(0, 5.0f, ENG_ColorValue.RED);

        friendsTableView.setOnRowClick(new ENG_TableView.OnRowClick() {
            @Override
            public boolean onSelectedRow(int row) {
                friendsTableView.setBoxRectangleBorder(row, 5.0f, ENG_ColorValue.WHITE);
                selectedRowNum = row;
                return true;
            }

            @Override
            public boolean onUnselectedRow(int oldRow) {
                return false;
            }
        });

        ok.setOnClickListener((x, y) -> {
            if (okClicked) {
                return true;
            }
            if (lobbyStatus != Lobby.LobbyStatus.READY_TO_START_SERVER) {
                showToast(MULTIPLAYER_LOBBY_LOBBY_NOT_READY_FOR_STARTING_GAME);
                return true;
            }
            okClicked = true;


            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerLobbyContainerListener) {
                    Lobby lobby = (Lobby) listener.getBundle().getObject("lobby");
                    boolean created = listener.getBundle().getBoolean("created");
                    Session session = new Session();
                    session.setSessionName("");
//                session.setMapId(selectedMapId);
//                session.setMaxPlayerNum(maxPlayerNum);
                    session.setGameType(MainApp.GameType.getActiveGameType().getGameType());
                    session.setLobbyId(lobby.getId());
                    final APP_Game game = MainApp.getGame();
                    User user = game.getUser();
                    initializeSession((ContainerListenerWithBus) listener, user, session, created);
                    break;
                }
            }
            return true;
        });

        cancel.setOnClickListener((x, y) -> {
            if (onBackPressed) {
                return true;
            }
            onBackPressed = true;
            APP_Game game = MainApp.getGame();
            User user = game.getUser();
            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerLobbyContainerListener) {
                    Lobby lobby = (Lobby) listener.getBundle().getObject("lobby");
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.ExitLobbyEvent(user.getAuthToken(), lobby));
                    break;
                }
            }

            return true;
        });
    }

    /** @noinspection UnstableApiUsage */
    private static void initializeSession(ContainerListenerWithBus listener, User user, Session session, boolean created) {
        if (created) {
            listener.getBus().post(new ClientAPI.CreateSessionEvent(user.getAuthToken(), session));
        } else {
            listener.getBus().post(new ClientAPI.JoinSessionEvent(user.getAuthToken(), session));
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyExited(ClientAPI.LobbyExitedEvent event) {
        if (!onBackPressed) {
            return;
        }
        onBackPressed();
        onBackPressed = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyExitedFailed(ClientAPI.ExitLobbyErrorEvent event) {
        if (!onBackPressed) {
            return;
        }
        showToast(event.restError.getMessage());
        onBackPressed = false;
    }

    public void addLobbyFriendViewData(ArrayList<LobbyFriendViewData> lobbyFriendViewData) {
        for (LobbyFriendViewData data : lobbyFriendViewData) {
            addRow(data.name, data.status);
        }
        this.lobbyFriendViewData = lobbyFriendViewData;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionCreated(ClientAPI.SessionCreatedEvent event) {
        Server server = event.session;
        if (okClicked) {
            MultiplayerCreateSession.onSessionAccepted(server, server.getMapId(), "", true);
        }
        okClicked = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionCreatedFailed(ClientAPI.SessionCreationErrorEvent event) {
        showToast(event.restError.getMessage());
        okClicked = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionJoined(ClientAPI.SessionJoinedEvent event) {
        Server server = event.session;
        MultiplayerCreateSession.onSessionAccepted(server, server.getMapId(), "", false);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionJoinedFailed(ClientAPI.SessionJoinErrorEvent event) {
        showToast(event.restError.getMessage());
        okClicked = false;
    }

    public void addRow(String name, String status) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, name);
        map.put(1, status);
        friendsTableView.addRowByPosition(map);
    }

    public Lobby.LobbyStatus getLobbyStatus() {
        return lobbyStatus;
    }

    public void setLobbyStatus(int lobbyStatus) {
        this.lobbyStatus = Lobby.LobbyStatus.getLobbyStatus(lobbyStatus);
        // If we are waiting to join a session and everything is ready then autostart.
        if (this.lobbyStatus == Lobby.LobbyStatus.READY_TO_START_SERVER || this.lobbyStatus == Lobby.LobbyStatus.READY_TO_JOIN_SERVER) {
            Lobby lobby = (Lobby) getListeners().get(0).getBundle().getObject("lobby");
            if (lobby == null) {
                // How the hell do we receive a lobby status update when we haven't moved to
                // the lobby.
                this.lobbyStatus = null; // Force another try.
                return;
            }
            Session session = new Session();
            session.setSessionName("");
//                session.setMapId(selectedMapId);
//                session.setMaxPlayerNum(maxPlayerNum);
            session.setGameType(MainApp.GameType.getActiveGameType().getGameType());
            session.setLobbyId(lobby.getId());
            final APP_Game game = MainApp.getGame();
            User user = game.getUser();

            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerLobbyContainerListener) {
                    boolean created = listener.getBundle().getBoolean("created");
                    // If we are trying to join a game we don't know if the server has been created
                    // by the lobby leader. Only thing we can do is just wait for the
                    // server be considered in game so we can join.
                    if (!created && this.lobbyStatus == Lobby.LobbyStatus.READY_TO_START_SERVER) {
                        break;
                    }
                    initializeSession((ContainerListenerWithBus) listener, user, session, created);
                    break;
                }
            }

            if (MainApp.getMainThread().isAutomationEnabled(MultiPlayerJoinSessionWithFriendsAutomation.NAME)) {
                MainApp.getMainThread().setParameterForAutomation(
                        MultiPlayerJoinSessionWithFriendsAutomation.NAME,
                        MultiPlayerJoinSessionWithFriendsAutomation.PARAM_SESSION_READY_TO_START, true);
            }
        }
    }

    public ArrayList<LobbyFriendViewData> getLobbyFriendViewData() {
        return lobbyFriendViewData;
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
