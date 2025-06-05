/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/24/21, 5:23 PM
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
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerJoinSessionWithFriendsContainerListener;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerLobbyContainerListener;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.Lobby;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.net.clientapi.viewdatas.LobbyViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TableView;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class MultiplayerJoinSessionWithFriends extends ENG_Container {

    public static final int COLUMN_COUNT = 3;
    public static final String LOBBY_TABLE = "lobby_table";
    public static final String OK_BUTTON = "ok";
    public static final String CANCEL_BUTTON = "cancel";
    private final ENG_TableView lobbyInvitationsTableView;
    private int selectedRowNum = -1;
    private ArrayList<LobbyViewData> lobbyViewData;
    private final ArrayList<Lobby> lobbyList = new ArrayList<>();
    private boolean okPressed;

    public static class MultiplayerJoinSessionWithFriendsFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerJoinSessionWithFriends(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection UnstableApiUsage, deprecation */
    public MultiplayerJoinSessionWithFriends(String name, Bundle bundle) {
        super(name, bundle);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
//        ENG_TextView friendSearchTextView = (ENG_TextView) createView("friendSearchText", "textview", 0.0f, 22.0f, 50.0f, 27.0f);
//        final ENG_TextField friendSearchTextField = (ENG_TextField) createView("sessionSearch", "textfield", 10.0f, 29.0f, 35.0f, 35.0f);
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_LOBBY_LEADER);
        columnNames.add(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_JOINED_EXPECTED);
        columnNames.add(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_STATUS);
        Bundle tableBundle = new Bundle();
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_COUNT, COLUMN_COUNT);
        tableBundle.putObject(ENG_TableView.BUNDLE_COLUMN_NAME_LIST, columnNames);
//        tableBundle.putFloat(ENG_TableView.BUNDLE_COLUMN_NAME_HEIGHT, 20.0f);
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_NAME_TEXT_SIZE, MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        lobbyInvitationsTableView = (ENG_TableView) createView(LOBBY_TABLE, "tableview", 10.0f, 22.0f, 90.0f, 70.0f, tableBundle);
//        ENG_Button inviteFriend = (ENG_Button) createView("invite_friend", "button", 52.0f, 22.0f, 100.0f, 27.0f);
        ENG_Button ok = (ENG_Button) createView(OK_BUTTON, "button", 0.0f, 72.0f, 100.0f, 82.0f);
        ENG_Button cancel = (ENG_Button) createView(CANCEL_BUTTON, "button", 0.0f, 84.0f, 100.0f, 94.0f);

        titleView.setText(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_TITLE);
//		titleView.setEllipsize(ENG_TextView.Ellipsize.END);
//        titleView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
//        friendSearchTextView.setText("Add friends to Lobby:");
//        friendSearchTextField.setText("");
//        inviteFriend.setText("Invite friend");
        ok.setText(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_JOIN_GAME);
        cancel.setText(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_BACK);
        ok.setTextColor(ENG_ColorValue.WHITE);
        cancel.setTextColor(ENG_ColorValue.WHITE);

//        inviteFriend.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
//        friendSearchTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
//        friendSearchTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        lobbyInvitationsTableView.setTextSize(MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        ok.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        ok.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        lobbyInvitationsTableView.setRowHeightType(ENG_TableView.RowHeightType.TABLE_BASED);
        lobbyInvitationsTableView.setTextFieldNumInTable(3);

        lobbyInvitationsTableView.setColumnNamesHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        lobbyInvitationsTableView.setColumnNamesVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
        lobbyInvitationsTableView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        lobbyInvitationsTableView.setVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
//        lobbyInvitationsTableView.setBoxRectangleBorder(0, 5.0f, ENG_ColorValue.RED);

        lobbyInvitationsTableView.setOnRowClick(new ENG_TableView.OnRowClick() {
            @Override
            public boolean onSelectedRow(int row) {
                lobbyInvitationsTableView.setBoxRectangleBorder(row, 5.0f, ENG_ColorValue.WHITE);
                selectedRowNum = row;
                return true;
            }

            @Override
            public boolean onUnselectedRow(int oldRow) {
                return false;
            }
        });

        ok.setOnClickListener((x, y) -> {
            if (okPressed) {
                return true;
            }
            if (selectedRowNum == -1) {
                showToast(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_PLEASE_SELECT_A_ROW_TO_JOIN_A_GAME);
                return true;
            }
            okPressed = true;
            APP_Game game = MainApp.getGame();
            User user = game.getUser();
            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerJoinSessionWithFriendsContainerListener) {
                    if (selectedRowNum >= 0 && selectedRowNum < lobbyList.size()) {
                        Lobby lobby = lobbyList.get(selectedRowNum);
                        ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.JoinLobbyEvent(user.getAuthToken(), lobby));
                    } else {
                        showToast(MULTIPLAYER_JOIN_SESSION_WITH_FRIENDS_INVALID_LOBBY_SELECTED);
                    }
                    break;
                }
            }
            return true;
        });

        cancel.setOnClickListener((x, y) -> {
            onBackPressed();
            return true;
        });
    }

    /** @noinspection UnstableApiUsage, deprecation */
    @Subscribe
    public void onLobbyJoined(ClientAPI.LobbyJoinedEvent event) {
        if (!okPressed) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putObject("lobby", event.lobby);
        bundle.putBoolean("created", false);
        ENG_ContainerManager.getSingleton().createContainerListener(
                SimpleViewGameMenuManager.MULTIPLAYER_LOBBY,
                MultiplayerLobbyContainerListener.MultiplayerLobbyContainerListenerFactory.TYPE,
                bundle);
        SimpleViewGameMenuManager.setCurrentMenu(SimpleViewGameMenuManager.MULTIPLAYER_LOBBY);
        okPressed = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyJoinedFailed(ClientAPI.JoinLobbyErrorEvent event) {
        if (!okPressed) {
            return;
        }
        showToast(event.restError.getMessage());
        okPressed = false;
    }

    private boolean paramJoinLobby;

    public void addLobbyViewData(ArrayList<LobbyViewData> lobbyViewData) {
        for (LobbyViewData data : lobbyViewData) {
            addRow(data.lobbyLeader, data.userStatus, data.status);
        }
        this.lobbyViewData = lobbyViewData;

        if (!paramJoinLobby && !lobbyViewData.isEmpty() &&
                MainApp.getMainThread().isAutomationEnabled(MultiPlayerJoinSessionWithFriendsAutomation.NAME)) {
            MainApp.getMainThread().setParameterForAutomation(MultiPlayerJoinSessionWithFriendsAutomation.NAME,
                    MultiPlayerJoinSessionWithFriendsAutomation.PARAM_JOIN_LOBBY, true);
            paramJoinLobby = true;
        }
    }

    public void addRow(String lobbyLeader, String userStatus, String status) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, lobbyLeader);
        map.put(1, userStatus);
        map.put(2, status);
        lobbyInvitationsTableView.addRowByPosition(map);
    }

    public ArrayList<LobbyViewData> getLobbyViewData() {
        return lobbyViewData;
    }

    public void setLobbyList(ArrayList<Lobby> lobbyList) {
        this.lobbyList.clear();
        this.lobbyList.addAll(lobbyList);
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
