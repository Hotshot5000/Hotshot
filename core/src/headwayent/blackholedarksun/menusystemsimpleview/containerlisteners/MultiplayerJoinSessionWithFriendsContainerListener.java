/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import java.util.ArrayList;

import headwayent.blackholedarksun.menus.MultiplayerJoinSessionWithFriends;
import headwayent.blackholedarksun.net.clientapi.tables.Lobby;
import headwayent.blackholedarksun.net.clientapi.viewdatas.LobbyViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

public class MultiplayerJoinSessionWithFriendsContainerListener extends ContainerListenerWithBusAndWithLobbyInvitationCheck {

    public static class MultiplayerJoinSessionWithFriendsContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerJoinSessionWithFriendsMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerJoinSessionWithFriendsContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerJoinSessionWithFriendsContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }

    private boolean hasLobbyFriendViewDataChanged(ArrayList<LobbyViewData> lobbyViewDataList) {
        MultiplayerJoinSessionWithFriends container = (MultiplayerJoinSessionWithFriends) getParentContainer();
        ArrayList<LobbyViewData> lobbyViewData = container.getLobbyViewData();
        if (lobbyViewData == null) {
            return true;
        }
        return !(lobbyViewData.containsAll(lobbyViewDataList) && lobbyViewDataList.containsAll(lobbyViewData));
    }

    @Override
    public void onActivation() {
        super.onActivation();
        setLobbyInvitationReceived(event -> {
            MultiplayerJoinSessionWithFriends container = (MultiplayerJoinSessionWithFriends) getParentContainer();
            ArrayList<LobbyViewData> lobbyViewData = new ArrayList<>();
            for (Lobby lobby : event.lobbyList.lobbyList) {
                LobbyViewData data = new LobbyViewData();
                data.lobbyLeader = lobby.getLobbyLeaderName();
                data.userStatus = lobby.getJoinedPlayerNum() + " / " + lobby.getExpectedPlayerNum();
                data.status = Lobby.LobbyStatus.getLobbyStatusAsString(lobby.getStatus());
                lobbyViewData.add(data);
            }

            if (hasLobbyFriendViewDataChanged(lobbyViewData)) {
                container.addLobbyViewData(lobbyViewData);
            }
            container.setLobbyList(event.lobbyList.lobbyList);
        });
    }
}
