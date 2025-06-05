/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import com.google.common.eventbus.Subscribe;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.MultiplayerJoinSession;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.Session;
import headwayent.blackholedarksun.net.clientapi.viewdatas.SessionViewData;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 25.10.2015.
 */
public class MultiplayerJoinSessionContainerListener extends ContainerListenerWithBus {

    public static class MultiplayerJoinSessionContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerJoinSessionMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerJoinSessionContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerJoinSessionContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }

    /** @noinspection UnstableApiUsage*/
    @Override
    public void onActivation() {
        super.onActivation();
        getBus().post(new ClientAPI.GetSessionListEvent(MainApp.GameType.getActiveGameType().getGameType()));
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionListLoaded(ClientAPI.SessionListLoadedEvent event) {
        List<Session> sessionList = event.sessionList;
        int rowNum = 1;
        ArrayList<SessionViewData> viewDataList = new ArrayList<>();
        for (Session session : sessionList) {
            SessionViewData sessionViewData = new SessionViewData();
            sessionViewData.rowNum = String.valueOf(rowNum++);
            sessionViewData.name = session.getSessionName();
            sessionViewData.players = session.getUserNum() + "/" + session.getMaxPlayerNum();
            sessionViewData.map = MainApp.getGame().getMapNameById(session.getMapId());
            sessionViewData.latency = String.valueOf(session.getLatency());
            viewDataList.add(sessionViewData);
        }
        MultiplayerJoinSession container = (MultiplayerJoinSession) getParentContainer();
        container.addSessionViewDataList(viewDataList);
        container.addSessionList(sessionList);
    }
}
