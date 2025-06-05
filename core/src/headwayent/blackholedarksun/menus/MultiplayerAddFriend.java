/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/24/21, 5:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import static headwayent.blackholedarksun.menus.language.MenuTexts.*;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.ContainerListenerWithBus;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerAddFriendContainerListener;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.net.clientapi.tables.UserFriend;
import headwayent.blackholedarksun.net.clientapi.tables.UserFriendInvitation;
import headwayent.blackholedarksun.net.clientapi.viewdatas.FriendViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.gui.simpleview.ENG_TableView;
import headwayent.hotshotengine.gui.simpleview.ENG_TextField;
import headwayent.hotshotengine.gui.simpleview.ENG_TextView;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class MultiplayerAddFriend extends ENG_Container {

    private static final int COLUMN_COUNT = 2;
    public static final int IGNORE_SEARCH_CHAR_COUNT = 3;
    private final ENG_TableView friendsTableView;
    private final int rowNum = 1;
    private final int selectedRowNum = -1;
    private UserFriend currentSelectedUserFriend;
    private boolean inviteButtonClicked;
    private boolean acceptInvitationButtonClicked;
    private List<UserFriend> userFriendList;
    private List<UserFriendInvitation> userFriendInvitationList;
    private final ArrayList<FriendViewData> friendViewDataList = new ArrayList<>();
    private int userFriendInvitationRowNum = -1;
    private int savedUserFriendInvitationRowNum = -1;

    public static class MultiplayerAddFriendFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new MultiplayerAddFriend(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection UnstableApiUsage, deprecation */
    public MultiplayerAddFriend(String name, final Bundle bundle) {
        super(name, bundle);

        ENG_TextView titleView = (ENG_TextView) createView("title", "textview", 0.0f, 0.0f, 100.0f, 20.0f);
        ENG_TextView friendSearchTextView = (ENG_TextView) createView("sessionSearchText", "textview", 0.0f, 22.0f, 50.0f, 27.0f);
        final ENG_TextField friendSearchTextField = (ENG_TextField) createView("sessionSearch", "textfield", 10.0f, 29.0f, 30.0f, 35.0f);
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(MULTIPLAYER_ADD_FRIEND_FRIENDS);
        columnNames.add(MULTIPLAYER_ADD_FRIEND_STATUS);
        Bundle tableBundle = new Bundle();
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_COUNT, COLUMN_COUNT);
        tableBundle.putObject(ENG_TableView.BUNDLE_COLUMN_NAME_LIST, columnNames);
//        tableBundle.putFloat(ENG_TableView.BUNDLE_COLUMN_NAME_HEIGHT, 20.0f);
        tableBundle.putInt(ENG_TableView.BUNDLE_COLUMN_NAME_TEXT_SIZE, MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        friendsTableView = (ENG_TableView) createView("friends_table", "tableview", 10.0f, 37.0f, 90.0f, 82.0f, tableBundle);
        ENG_Button addFriend = (ENG_Button) createView("add_friend", "button", 34.0f, 20.0f, 61.0f, 35.0f);
        ENG_Button acceptFriend = (ENG_Button) createView("accept_friend", "button", 62.0f, 20.0f, 89.0f, 35.0f);
        ENG_Button cancel = (ENG_Button) createView("cancel", "button", 0.0f, 84.0f, 100.0f, 94.0f);

        titleView.setText(MULTIPLAYER_ADD_FRIEND_TITLE);
//		titleView.setEllipsize(ENG_TextView.Ellipsize.END);
//        titleView.setHorizontalAlignment(ENG_TextView.HorizontalAlignment.CENTER);
        friendSearchTextView.setText(MULTIPLAYER_ADD_ENTER_FRIEND_NAME);
        friendSearchTextField.setText("");
        addFriend.setText(MULTIPLAYER_ADD_FRIEND_SEND_FRIEND_REQUEST);
        acceptFriend.setText(MULTIPLAYER_ADD_FRIEND_ACCEPT_FRIEND_REQUEST);
        cancel.setText(MULTIPLAYER_ADD_FRIEND_BACK);
        addFriend.setTextColor(ENG_ColorValue.WHITE);
        acceptFriend.setTextColor(ENG_ColorValue.WHITE);
        cancel.setTextColor(ENG_ColorValue.WHITE);

        titleView.setTextSize(APP_Game.GORILLA_DEJAVU_LARGE);
        friendSearchTextView.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        friendSearchTextField.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        friendsTableView.setTextSize(MainApp.Platform.isMobile() ? APP_Game.GORILLA_DEJAVU_SMALL : APP_Game.GORILLA_DEJAVU_MEDIUM);
        addFriend.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        acceptFriend.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);
        cancel.setTextSize(APP_Game.GORILLA_DEJAVU_MEDIUM);

        addFriend.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        acceptFriend.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);
        cancel.setNinePatchBackground(APP_Game.GORILLA_DEJAVU_NOT_PRESSED, APP_Game.GORILLA_DEJAVU_PRESSED);

        setCurrentFocusedView(friendSearchTextField);

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
            // Make sure we don't spam the server just by keeping a key down :).
            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerAddFriendContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.GetFriendIdEvent(userFriend));
                    break;
                }
            }
        });

        friendsTableView.setOnRowClick(new ENG_TableView.OnRowClick() {
            @Override
            public boolean onSelectedRow(int row) {
                String username = friendsTableView.getText(row, 0);
                boolean userFriendFound = false;
                boolean userFriendInvitationFound = false;
                if (userFriendList != null) {
                    for (UserFriend userFriend : userFriendList) {
                        if (userFriend.getFriendName().equalsIgnoreCase(username)) {
                            userFriendFound = true;
                            break;
                        }
                    }
                }
                if (userFriendInvitationList != null && !userFriendFound) {
                    for (UserFriendInvitation userFriendInvitation : userFriendInvitationList) {
                        if (userFriendInvitation.getInvitedFriendName().equalsIgnoreCase(username)) {
                            userFriendInvitationFound = true;
                            break;
                        }
                    }
                }
                if (userFriendInvitationFound) {
                    userFriendInvitationRowNum = row;
                } else {
                    userFriendInvitationRowNum = -1;
                }

                return true;
            }

            @Override
            public boolean onUnselectedRow(int oldRow) {
                return true;
            }
        });

        addFriend.setOnClickListener((x, y) -> {
            if (inviteButtonClicked) {
                return true;
            }
            if (currentSelectedUserFriend == null) {
                showToast(MULTIPLAYER_ADD_FRIEND_NO_FRIEND_NAME_SELECTED);
                return true;
            }
            inviteButtonClicked = true;
            final APP_Game game = MainApp.getGame();
            User user = game.getUser();
            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerAddFriendContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.AddFriendEvent(user.getAuthToken(), currentSelectedUserFriend));
                    break;
                }
            }
            return true;
        });

        acceptFriend.setOnClickListener((x, y) -> {
            if (acceptInvitationButtonClicked) {
                return true;
            }
            if (userFriendInvitationRowNum == -1) {
                showToast(MULTIPLAYER_ADD_FRIEND_NO_FRIEND_NAME_SELECTED);
                return true;
            }
            acceptInvitationButtonClicked = true;
            UserFriendInvitation userFriendInvitation = userFriendInvitationList.get(userFriendInvitationRowNum);
            // We need to save this because it might change until we get the answer from the server.
            savedUserFriendInvitationRowNum = userFriendInvitationRowNum;
            final APP_Game game = MainApp.getGame();
            User user = game.getUser();
            ArrayList<ContainerListener> listeners = getListeners();
            for (ContainerListener listener : listeners) {
                if (listener instanceof MultiplayerAddFriendContainerListener) {
                    ((ContainerListenerWithBus) listener).getBus().post(new ClientAPI.AcceptFriendInvitationEvent(userFriendInvitation));
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

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onFriendIdReceivedEvent(ClientAPI.FriendIdReceivedEvent event) {
        currentSelectedUserFriend = event.userFriend;

    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onGetFriendIdErrorEvent(ClientAPI.GetFriendIdErrorEvent event) {
        showToast(MULTIPLAYER_ADD_FRIEND_COULD_NOT_FIND_THE_SELECTED_FRIEND_NAME);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onFriendAddedEvent(ClientAPI.FriendAddedEvent event) {
        if (!inviteButtonClicked) {
            return;
        }
        showToast(MULTIPLAYER_ADD_FRIEND_FRIEND_ADDED);
        inviteButtonClicked = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onAddFriendFailedEvent(ClientAPI.AddFriendErrorEvent event) {
        if (!inviteButtonClicked) {
            return;
        }
        showToast(/*"Could not add friend"*/event.restError.getMessage());
        inviteButtonClicked = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onAcceptFriendInvitationEvent(ClientAPI.AcceptFriendInvitationSuccessEvent event) {
        if (!acceptInvitationButtonClicked) {
            return;
        }
        // If the selected row has been accepted then we must remove it from the table.
        String invitedFriendName = event.userFriendInvitation.getInvitedFriendName();
        String usernameColumn = friendsTableView.getText(savedUserFriendInvitationRowNum, 0);
        if (!invitedFriendName.equals(usernameColumn)) {
            showToast(MULTIPLAYER_ADD_FRIEND_THE_USER_NAME_DOES_NOT_MATCH);
        }
        friendsTableView.removeRow(savedUserFriendInvitationRowNum);
        acceptInvitationButtonClicked = false;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onAcceptFriendInvitationEventFailed(ClientAPI.AcceptFriendInvitationErrorEvent event) {
        if (!acceptInvitationButtonClicked) {
            return;
        }
        acceptInvitationButtonClicked = false;
    }

    public void addFriendViewData(List<FriendViewData> data) {
        if (friendViewDataList.isEmpty()) {
            for (FriendViewData friendViewData : data) {
                addRow(friendViewData.name, friendViewData.status);
            }
            friendViewDataList.addAll(data);

        } else {
            ArrayList<FriendViewData> toAdd = new ArrayList<>();
            ArrayList<FriendViewData> toRemove = new ArrayList<>();
            for (FriendViewData friendViewData : data) {
                boolean found = false;
                for (FriendViewData localFriendViewData : friendViewDataList) {
                    if (localFriendViewData.equals(friendViewData)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    toRemove.add(friendViewData);
                }
                toAdd.add(friendViewData);
            }
            int sizeBeforeUpdate = friendViewDataList.size();
            friendViewDataList.removeAll(toRemove);
            friendViewDataList.addAll(toAdd);
            int sizeAfterUpdate = friendViewDataList.size();
            if (sizeBeforeUpdate != sizeAfterUpdate) {
                // We have changed some things so reset our row selection.
                int userFriendRowNum = -1;
                userFriendInvitationRowNum = -1;
            }
            sortFriendViewDataList();

            friendsTableView.removeAllRows();

            for (FriendViewData viewData : friendViewDataList) {
                addRow(viewData.name, viewData.status);
            }
        }

    }

    public void addRow(String friendName, String status) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, friendName);
        map.put(1, status);
        friendsTableView.addRowByPosition(map);
    }

    public void setFriendDataList(List<UserFriend> userFriendList) {
        this.userFriendList = userFriendList;
    }

    public void setUserFriendInvitationList(List<UserFriendInvitation> userFriendInvitationList) {
        this.userFriendInvitationList = userFriendInvitationList;
    }

    private void sortFriendViewDataList() {
        Collections.sort(friendViewDataList, (f1, f2) -> {
            if (f1.type == FriendViewData.Type.INVITATION && f2.type == FriendViewData.Type.FRIEND) {
                return -1;
            }
            if (f1.type == FriendViewData.Type.FRIEND && f2.type == FriendViewData.Type.INVITATION) {
                return 1;
            }
            return 0;
        });
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
