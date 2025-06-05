/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import com.google.common.eventbus.Subscribe;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.ErrorCodes;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.Handler;
import headwayent.hotshotengine.Looper;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

public abstract class ContainerListenerWithBusAndWithLobbyInvitationCheck extends ContainerListenerWithBus {

    private static final long LOBBY_INVITATIONS_STATUS_UPDATE_DELAY = 5000;
    private long lastLobbyInvitationsUpdateTime;
    private boolean shouldStopLobbyInvitationRequests;
    private ClientAPI.LobbyInvitationsReceivedEvent lobbyInvitationReceivedEvent;
    private LobbyInvitationReceived lobbyInvitationReceived;

    public interface LobbyInvitationReceived {
        void onLobbyInvitationReceived(ClientAPI.LobbyInvitationsReceivedEvent event);
    }

    /** @noinspection deprecation*/
    public ContainerListenerWithBusAndWithLobbyInvitationCheck(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void onActivation() {
        super.onActivation();
        resendLobbyInvitationsEvent();
    }

    @Override
    public void onDestruction() {
        shouldStopLobbyInvitationRequests = true;
        lobbyInvitationReceivedEvent = null;
        lobbyInvitationReceived = null;
        super.onDestruction();
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyInvitationsUpdated(ClientAPI.LobbyInvitationsReceivedEvent event) {
        if (event.lobbyList.getError() != null && event.lobbyList.getErrorCode() != ErrorCodes.GET_LOBBY_INVITATIONS_NO_INVITATIONS) {
            getParentContainer().showToast(event.lobbyList.getError());
        }
        this.lobbyInvitationReceivedEvent = event;
        if (lobbyInvitationReceived != null) {
            lobbyInvitationReceived.onLobbyInvitationReceived(event);
        }
        resendLobbyInvitationsEvent();
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onLobbyInvitationsUpdateFailed(ClientAPI.GetLobbyInvitationsErrorEvent event) {
        getParentContainer().showToast(event.restError.getMessage());
        resendLobbyInvitationsEvent();
    }

    /** @noinspection UnstableApiUsage*/ // This only gets called if we end up in an *Updated() or *Failed() handler.
    // It should get called regardless of missing responses from server, or socket errors due to downed network connection.
    private void resendLobbyInvitationsEvent() {
        if (shouldStopLobbyInvitationRequests) {
            return;
        }
        long timeDiff = ENG_Utility.currentTimeMillis() - lastLobbyInvitationsUpdateTime;
        long delay = timeDiff > LOBBY_INVITATIONS_STATUS_UPDATE_DELAY ? 0 : (LOBBY_INVITATIONS_STATUS_UPDATE_DELAY - timeDiff);
        new Handler(Looper.myLooper()).postDelayed(() -> {
            if (getBus() == null || MainApp.getGame().getUser() == null || MainApp.getGame().getUser().getAuthToken() == null) {
                // We have destroyed the container but this is still lingering.
                return;
            }
            getBus().post(new ClientAPI.GetLobbyInvitationsEvent(MainApp.getGame().getUser().getAuthToken()));
            lastLobbyInvitationsUpdateTime = ENG_Utility.currentTimeMillis();
        }, delay);
    }

    public ClientAPI.LobbyInvitationsReceivedEvent getLobbyInvitationReceivedEvent() {
        return lobbyInvitationReceivedEvent;
    }

    public LobbyInvitationReceived getLobbyInvitationReceived() {
        return lobbyInvitationReceived;
    }

    public void setLobbyInvitationReceived(LobbyInvitationReceived lobbyInvitationReceived) {
        this.lobbyInvitationReceived = lobbyInvitationReceived;
    }
}
