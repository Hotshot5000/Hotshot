/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.MultiplayerAddFriend;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.UserFriend;
import headwayent.blackholedarksun.net.clientapi.tables.UserFriendInvitation;
import headwayent.blackholedarksun.net.clientapi.viewdatas.FriendViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.Handler;
import headwayent.hotshotengine.Looper;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

public class MultiplayerAddFriendContainerListener extends ContainerListenerWithBus {

    private static final long GET_FRIEND_LIST_DELAY = 5000;
    private static final long GET_FRIEND_INVITATION_LIST_DELAY = 10000;
    private long lastFriendListUpdate;
    private long lastFriendInvitationUpdate;
    private boolean shouldStopUpdates;

    public static class MultiplayerAddFriendContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerAddFriendContainerListenerMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerAddFriendContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerAddFriendContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }

    @Override
    public void onActivation() {
        super.onActivation();
        resendGetFriendListEvent();
        resendGetFriendInvitationListEvent();
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onFriendInvitationListReceived(ClientAPI.FriendInvitationListReceivedEvent event) {
        if (event.list.getErrorCode() != 0) {
            getParentContainer().showToast(event.list.getError());
            return;
        }
        ArrayList<FriendViewData> friendViewData = new ArrayList<>();
        for (UserFriendInvitation invitation : event.list.userFriendInvitationList) {
            FriendViewData data = new FriendViewData();
            data.name = invitation.getInvitedFriendName();
            data.status = "Accept Invitation";
            friendViewData.add(data);
        }
        MultiplayerAddFriend multiplayerAddFriend = (MultiplayerAddFriend) getParentContainer();
        multiplayerAddFriend.addFriendViewData(friendViewData);
        multiplayerAddFriend.setUserFriendInvitationList(event.list.userFriendInvitationList);
        resendGetFriendInvitationListEvent();
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onGetFriendInvitationListFailed(ClientAPI.GetFriendInvitationListErrorEvent event) {
        getParentContainer().showToast(event.restError.getMessage());
    }

    /** @noinspection UnstableApiUsage*/
    private void resendGetFriendInvitationListEvent() {
        if (shouldStopUpdates) {
            return;
        }
        long timeDiff = ENG_Utility.currentTimeMillis() - lastFriendInvitationUpdate;
        long delay = timeDiff > GET_FRIEND_INVITATION_LIST_DELAY ? 0 : (GET_FRIEND_INVITATION_LIST_DELAY - timeDiff);
        new Handler(Looper.myLooper()).postDelayed(() -> {
            if (getBus() == null || MainApp.getGame().getUser() == null || MainApp.getGame().getUser().getAuthToken() == null) {
                // We have destroyed the container but this is still lingering.
                return;
            }
            getBus().post(new ClientAPI.GetFriendInvitationListEvent(MainApp.getGame().getUser().getAuthToken()));
            lastFriendInvitationUpdate = ENG_Utility.currentTimeMillis();
        }, delay);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onFriendsListLoaded(ClientAPI.FriendListReceivedEvent event) {
//        UserFriend userFriend = event.friendList.get(0);
//        getParentContainer().showToast(userFriend.getFriendName() + " status: " + userFriend.getStatus());
        if (event.friendList.getErrorCode() != 0) {
            getParentContainer().showToast(event.friendList.getError());
            return;
        }
        ArrayList<FriendViewData> friendViewData = getFriendViewData(event);
        MultiplayerAddFriend multiplayerAddFriend = (MultiplayerAddFriend) getParentContainer();
        multiplayerAddFriend.addFriendViewData(friendViewData);
        multiplayerAddFriend.setFriendDataList(event.friendList.userFriends);
        resendGetFriendListEvent();
    }

    private ArrayList<FriendViewData> getFriendViewData(ClientAPI.FriendListReceivedEvent event) {
        ArrayList<FriendViewData> friendViewData = new ArrayList<>();
        for (UserFriend friend : event.friendList.userFriends) {
            FriendViewData data = new FriendViewData();
            data.name = friend.getFriendName();
            data.status = UserFriend.getStatusAsText(friend);
            friendViewData.add(data);
        }
        return friendViewData;
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onFriendListLoadedError(ClientAPI.GetFriendsListErrorEvent event) {
        getParentContainer().showToast(event.restError.getMessage());
    }

    /** @noinspection UnstableApiUsage*/
    private void resendGetFriendListEvent() {
        if (shouldStopUpdates) {
            return;
        }
        long timeDiff = ENG_Utility.currentTimeMillis() - lastFriendListUpdate;
        long delay = timeDiff > GET_FRIEND_LIST_DELAY ? 0 : (GET_FRIEND_LIST_DELAY - timeDiff);
        new Handler(Looper.myLooper()).postDelayed(() -> {
            if (getBus() == null || MainApp.getGame().getUser() == null || MainApp.getGame().getUser().getAuthToken() == null) {
                // We have destroyed the container but this is still lingering.
                return;
            }
            getBus().post(new ClientAPI.GetFriendListEvent(MainApp.getGame().getUser().getAuthToken()));
            lastFriendListUpdate = ENG_Utility.currentTimeMillis();
        }, delay);
    }
}
