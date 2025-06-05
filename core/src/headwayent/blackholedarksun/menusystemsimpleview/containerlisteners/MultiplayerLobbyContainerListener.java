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
import headwayent.blackholedarksun.menus.MultiplayerLobby;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.Lobby;
import headwayent.blackholedarksun.net.clientapi.tables.LobbyInvitation;
import headwayent.blackholedarksun.net.clientapi.viewdatas.LobbyFriendViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.Handler;
import headwayent.hotshotengine.Looper;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

public class MultiplayerLobbyContainerListener extends ContainerListenerWithBus {

    private static final long LOBBY_STATUS_UPDATE_DELAY = 2000;
    private long lastLobbyStatusUpdateTime;
    private boolean shouldStopLobbyStatusUpdates;

    public static class MultiplayerLobbyContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerLobbyMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerLobbyContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerLobbyContainerListener(String type, ENG_Container container, Bundle bundle) {
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
        resendLobbyStatusEvent();
    }

    @Override
    public void onDestruction() {
        shouldStopLobbyStatusUpdates = true;
        super.onDestruction();
    }

    private boolean hasLobbyFriendViewDataChanged(ArrayList<LobbyFriendViewData> lobbyFriendViewDataList) {
        MultiplayerLobby container = (MultiplayerLobby) getParentContainer();
        ArrayList<LobbyFriendViewData> lobbyFriendViewData = container.getLobbyFriendViewData();
        if (lobbyFriendViewData == null) {
            return true;
        }
        return !(lobbyFriendViewData.containsAll(lobbyFriendViewDataList) && lobbyFriendViewDataList.containsAll(lobbyFriendViewData));
    }

    private boolean hasLobbyStatusChanged(int lobbyStatus) {
        MultiplayerLobby container = (MultiplayerLobby) getParentContainer();
        if (container.getLobbyStatus() == null) {
            return true;
        }
        return !container.getLobbyStatus().equals(Lobby.LobbyStatus.getLobbyStatus(lobbyStatus));
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyStatusUpdated(ClientAPI.LobbyStatusReceivedEvent event) {
        MultiplayerLobby container = (MultiplayerLobby) getParentContainer();
        ArrayList<LobbyFriendViewData> lobbyFriendViewDataList = new ArrayList<>();
        for (LobbyInvitation lobbyInvitation : event.lobbyInvitationList.lobbyInvitationList) {
            LobbyFriendViewData lobbyFriendViewData = new LobbyFriendViewData();
            lobbyFriendViewData.name = lobbyInvitation.getInvitedUserName();
            lobbyFriendViewData.status = LobbyInvitation.InvitedUserStatus.getInvitedUserStatusAsString((int) lobbyInvitation.getUserJoined());
            lobbyFriendViewDataList.add(lobbyFriendViewData);
        }
        if (hasLobbyFriendViewDataChanged(lobbyFriendViewDataList)) {
            container.addLobbyFriendViewData(lobbyFriendViewDataList);
        }
        if (hasLobbyStatusChanged(event.lobbyInvitationList.getLobbyStatus())) {
            container.setLobbyStatus(event.lobbyInvitationList.getLobbyStatus());
        }

        resendLobbyStatusEvent();
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyStatusUpdateFailed(ClientAPI.GetLobbyStatusErrorEvent event) {
        getParentContainer().showToast(event.restError.getMessage());
        resendLobbyStatusEvent();
    }

    /** @noinspection UnstableApiUsage*/
    private void resendLobbyStatusEvent() {
        if (shouldStopLobbyStatusUpdates) {
            return;
        }
        long timeDiff = ENG_Utility.currentTimeMillis() - lastLobbyStatusUpdateTime;
        long delay = timeDiff > LOBBY_STATUS_UPDATE_DELAY ? 0 : (LOBBY_STATUS_UPDATE_DELAY - timeDiff);
        new Handler(Looper.myLooper()).postDelayed(() -> {
            if (getBus() == null || MainApp.getGame().getUser() == null || MainApp.getGame().getUser().getAuthToken() == null) {
                // We have destroyed the container but this is still lingering.
                return;
            }
//                Lobby lobby = null;
//                ArrayList<ENG_Container.ContainerListener> listeners = ggetListeners();
//                for (ENG_Container.ContainerListener listener : listeners) {
//                    if (listener instanceof MultiplayerMenuContainerListener) {
//                        lobby = (Lobby) listener.getBundle().getObject("lobby");
//                        break;
//                    }
//                }
            Lobby lobby = (Lobby) getBundle().getObject("lobby");
            getBus().post(new ClientAPI.GetLobbyStatusEvent(MainApp.getGame().getUser().getAuthToken(),
                    lobby));
            lastLobbyStatusUpdateTime = ENG_Utility.currentTimeMillis();
        }, delay);
    }
}
