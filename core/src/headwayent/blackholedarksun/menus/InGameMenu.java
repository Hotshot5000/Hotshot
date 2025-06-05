/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menus;

import com.google.common.eventbus.Subscribe;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;

/**
 * Created by sebas on 19.03.2016.
 */
public class InGameMenu extends GenericMenu {

    public static class InGameMenuContainerFactory extends ENG_ContainerManager.ContainerFactory {

        /** @noinspection deprecation*/
        @Override
        public ENG_Container createContainer(String name, Bundle bundle) {
            return new InGameMenu(name, bundle);
        }

        @Override
        public void destroyContainer(ENG_Container c) {
            c.destroy();
        }
    }

    /** @noinspection deprecation*/
    public InGameMenu(String name, Bundle bundle) {
        super(name, bundle);
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void onSessionLeft(ClientAPI.SessionLeftEvent event) {
        System.out.println("Session left");
    }
}
