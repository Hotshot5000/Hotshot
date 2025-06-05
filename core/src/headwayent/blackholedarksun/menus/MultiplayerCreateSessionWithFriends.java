/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/24/21, 5:52 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.automationframework.MultiPlayerCreateSessionWithFriendsAutomation;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerCreateSessionWithFriendsContainerListener;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerLobbyContainerListener;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.FriendData;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.net.clientapi.tables.UserFriend;
import headwayent.blackholedarksun.net.clientapi.viewdatas.FriendViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TableView;
import headwayent.hotshotengine.gui.simpleview.ENG_TextField;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class MultiplayerCreateSessionWithFriends extends ENG_Container {

    public static final int COLUMN_COUNT = 2;
    public static final int IGNORE_SEARCH_CHAR_COUNT = 3;
    public static final String FRIENDS_TABLE = "friends_table";
    public static final String INVITE_FRIEND_BUTTON = "invite_friend";
    public static final String OK_BUTTON = "ok";
    public static final String CANCEL_BUTTON = "cancel";
    private final ENG_TableView friendsTableView;
    private final int rowNum = 1;
    private int selectedRowNum = -1;
    private List<UserFriend> userFriendList;
    private final HashMap<Integer, UserFriend> selectedUserFriendMap = new HashMap<>();
    private boolean inviteFriendsClicked;
    private boolean createLobbyClicked;

    public static class MultiplayerCreateSessionWithFriendsFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerCreateSessionWithFriends(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection UnstableApiUsage, deprecation */
    public MultiplayerCreateSessionWithFriends(String name, Bundle bundle) {
        super(name, bundle);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView friendSearchTextView = (ENG_TextView) createView("friendSearchText", "textview", 0.0f, 22.0f, 50.0f, 27.0f);
        final ENG_TextField friendSearchTextField = (ENG_TextField) createView("sessionSearch", "textfield", 10.0f, 29.0f, 35.0f, 35.0f);
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_FRIENDS);
        columnNames.add(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_JOINED);
        Bundle tableBundle = new Bundle();
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_COUNT, COLUMN_COUNT);
        tableBundle.putObject(ENG_TableView.BUNDLE_COLUMN_NAME_LIST, columnNames);
//        tableBundle.putFloat(ENG_TableView.BUNDLE_COLUMN_NAME_HEIGHT, 20.0f);
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_NAME_TEXT_SIZE, MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        friendsTableView = (ENG_TableView) createView(FRIENDS_TABLE, "tableview", 10.0f, 37.0f, 90.0f, 70.0f, tableBundle);
        ENG_Button inviteFriend = (ENG_Button) createView(INVITE_FRIEND_BUTTON, "button", 52.0f, 22.0f, 100.0f, 27.0f);
        ENG_Button ok = (ENG_Button) createView(OK_BUTTON, "button", 0.0f, 72.0f, 100.0f, 82.0f);
        ENG_Button cancel = (ENG_Button) createView(CANCEL_BUTTON, "button", 0.0f, 84.0f, 100.0f, 94.0f);

        titleView.setText(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_TITLE);
//		titleView.setEllipsize(ENG_TextView.Ellipsize.END);
//        titleView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        friendSearchTextView.setText(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_ADD_FRIENDS_TO_LOBBY);
        friendSearchTextField.setText("");
        inviteFriend.setText(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_INVITE_FRIEND);
        ok.setText(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_START_GAME);
        cancel.setText(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_BACK);
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
        inviteFriend.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        friendsTableView.setRowHeightType(ENG_TableView.RowHeightType.TABLE_BASED);
        friendsTableView.setTextFieldNumInTable(3);

        friendsTableView.setColumnNamesHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        friendsTableView.setColumnNamesVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
        friendsTableView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        friendsTableView.setVerticalAlignment(ENG_TextView.VerticalAlignment.CENTER);
//        friendsTableView.setBoxRectangleBorder(0, 5.0f, ENG_ColorValue.RED);

        friendSearchTextField.addTextFieldChangeListener(text -> {
            if (text.length() < IGNORE_SEARCH_CHAR_COUNT) {
                return;
            }
            final APP_Game game = MainApp.getGame();
            User user = game.getUser();
            UserFriend userFriend = new UserFriend();
            userFriend.setUserId(user.getId());
            userFriend.setFriendName(text);
            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerCreateSessionWithFriendsContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.GetFriendIdEvent(userFriend));
                    break;
                }
            }
        });

//        setCurrentFocusedView(friendSearchTextField);

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

        inviteFriend.setOnClickListener((x, y) -> {
            if (inviteFriendsClicked) {
                return true;
            }
            inviteFriendsClicked = true;
            if (selectedRowNum == -1) {
                inviteFriendsClicked = false;
                showToast(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_YOU_MUST_SELECT_A_FRIEND_TO_ADD_TO_THE_LOBBY);
                return true;
            }
            // We shouldn't be able to get here since there should be no selected row but just for safety.
            if (userFriendList == null || userFriendList.isEmpty()) {
                showToast(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_NO_ELEMENTS_IN_THE_SESSION_TABLE);
                inviteFriendsClicked = false;
                return true;
            }
            APP_Game game = MainApp.getGame();
            User user = game.getUser();
            UserFriend userFriend = userFriendList.get(selectedRowNum);
            selectedUserFriendMap.put(selectedRowNum, userFriend);
            // We are not waiting for any response for now...
            inviteFriendsClicked = false;

            return true;
        });

        ok.setOnClickListener((x, y) -> {
            if (createLobbyClicked) {
                return true;
            }
            APP_Game game = MainApp.getGame();
            User user = game.getUser();
            if (selectedUserFriendMap.isEmpty()) {
                showToast(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_YOU_MUST_ADD_AT_LEAST_ONE_FRIEND_IN_ORDER_TO_CREATE_A_LOBBY);
                return true;
            }
            createLobbyClicked = true;
            ArrayList<FriendData> friendDataList = new ArrayList<>();
            for (UserFriend userFriend : selectedUserFriendMap.values()) {
                FriendData friendData = new FriendData();
                friendData.setFriendId(userFriend.getFriendId());
                friendDataList.add(friendData);
            }

            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerCreateSessionWithFriendsContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.CreateLobbyEvent(user.getAuthToken(), friendDataList));
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
    public void onLobbyCreation(ClientAPI.LobbyCreatedEvent event) {
        if (!createLobbyClicked) {
            return;
        }
        createLobbyClicked = false;
        Bundle bundle = new Bundle();
        bundle.putObject("lobby", event.lobby);
        bundle.putBoolean("created", true);
        ENG_ContainerManager.getSingleton().createContainerListener(
                SimpleViewGameMenuManager.MULTIPLAYER_LOBBY,
                MultiplayerLobbyContainerListener.MultiplayerLobbyContainerListenerFactory.TYPE,
                bundle);
        SimpleViewGameMenuManager.setCurrentMenu(SimpleViewGameMenuManager.MULTIPLAYER_LOBBY);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyCreationFailed(ClientAPI.CreateLobbyErrorEvent event) {
        showToast(MULTIPLAYER_CREATE_SESSION_WITH_FRIENDS_COULD_NOT_CREATE_THE_LOBBY);
        if (!createLobbyClicked) {
            return;
        }
        createLobbyClicked = false;
    }

    private boolean paramCreateLobby;

    public void addFriendViewData(ArrayList<FriendViewData> friendViewData) {
        for (FriendViewData viewData : friendViewData) {
            addRow(viewData.name, viewData.status);
        }

        if (!paramCreateLobby && !friendViewData.isEmpty() && MainApp.getMainThread().isAutomationEnabled(MultiPlayerCreateSessionWithFriendsAutomation.NAME)) {
            MainApp.getMainThread().setParameterForAutomation(
                    MultiPlayerCreateSessionWithFriendsAutomation.NAME, MultiPlayerCreateSessionWithFriendsAutomation.PARAM_CREATE_LOBBY, true);
            paramCreateLobby = true;
        }
    }

    public void addRow(String friendName, String status) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, friendName);
        map.put(1, status);
        friendsTableView.addRowByPosition(map);
    }

    public void addFriendDataList(List<UserFriend> userFriendList) {
        this.userFriendList = userFriendList;
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
