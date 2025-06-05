/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.MultiplayerCreateSessionWithFriends;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.UserFriend;
import headwayent.blackholedarksun.net.clientapi.viewdatas.FriendViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

public class MultiplayerCreateSessionWithFriendsContainerListener extends ContainerListenerWithBus {

    public static class MultiplayerCreateSessionWithFriendsContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerCreateSessionWithFriendsMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerCreateSessionWithFriendsContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerCreateSessionWithFriendsContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    /** @noinspection UnstableApiUsage*/
    @Override
    public void onActivation() {
        super.onActivation();
        getBus().post(new ClientAPI.GetFriendListEvent(MainApp.getGame().getUser().getAuthToken()));
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

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
        ArrayList<FriendViewData> friendViewData = new ArrayList<>();
        for (UserFriend friend : event.friendList.userFriends) {
            FriendViewData data = new FriendViewData();
            data.name = friend.getFriendName();
            data.status = UserFriend.getStatusAsText(friend);
            friendViewData.add(data);
        }
        MultiplayerCreateSessionWithFriends multiplayerCreateSessionWithFriends = (MultiplayerCreateSessionWithFriends) getParentContainer();
        multiplayerCreateSessionWithFriends.addFriendViewData(friendViewData);
        multiplayerCreateSessionWithFriends.addFriendDataList(event.friendList.userFriends);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onFriendListLoadedError(ClientAPI.GetFriendsListErrorEvent event) {
        getParentContainer().showToast(event.restError.getMessage());
    }
}
