/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by Sebi on 18.05.2014.
 */
public class HideDemoContainerListener extends ENG_Container.ContainerListener {

    public static class HideDemoContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "HideDemo";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new HideDemoContainerListener(TYPE);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    public HideDemoContainerListener(String type) {
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

        SimpleViewGameMenuManager.removeBackgroundAndDemo();
    }

    @Override
    public void onDestruction() {

    }
}
