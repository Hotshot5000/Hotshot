/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/15/21, 7:25 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import java.util.ArrayList;

import headwayent.blackholedarksun.menus.MultiplayerEventFinalization;
import headwayent.blackholedarksun.multiplayer.rmi.UserStats;
import headwayent.blackholedarksun.multiplayer.rmi.UserStatsList;
import headwayent.hotshotengine.AsyncTask;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by sebas on 05.07.2016.
 */
public class MultiplayerEventFinalizationContainerListener extends ENG_Container.ContainerListener {
    private AsyncTask<Void, Void, ArrayList<UserStats>> task;

    /** @noinspection deprecation*/
    public MultiplayerEventFinalizationContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }

    /** @noinspection deprecation*/
    @Override
    public void onActivation() {
        Bundle bundle = getBundle();
        final UserStatsList userStatsList = (UserStatsList) bundle.getObject("userStatsList");
        task = new AsyncTask<Void, Void, ArrayList<UserStats>>() {

            @Override
            protected ArrayList<UserStats> doInBackground(Void... params) {
                try {
                    return userStatsList.getList();
                } catch (Exception e) {
                    e.printStackTrace();
                    MultiplayerEventFinalization container = (MultiplayerEventFinalization) getParentContainer();
                    container.showToast("Could not get players stats!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<UserStats> userStatsList) {
                super.onPostExecute(userStatsList);
                if (userStatsList != null) {
                    MultiplayerEventFinalization container = (MultiplayerEventFinalization) getParentContainer();
                    container.addUserStatsList(userStatsList);
                }
            }
        };
        task.execute();
    }

    @Override
    public void onDestruction() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }
}
