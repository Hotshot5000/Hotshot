/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by Sebi on 17.05.2014.
 */
public class ShowDemoContainerListener extends ENG_Container.ContainerListener {

    public static class ShowDemoContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "ShowDemo";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(
                ENG_Container container, Bundle bundle) {
            return new ShowDemoContainerListener(TYPE);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    public ShowDemoContainerListener(String type) {
        super(type);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }

    @Override
    public void onActivation() {

//        if (!MainApp.getGame().isReloadingResources()) {
            MainApp.getGame().createMainMenuBackgroundDemo();
//        }
    }

    @Override
    public void onDestruction() {
        SimpleViewGameMenuManager.removeBackgroundAndDemo();
    }
}
