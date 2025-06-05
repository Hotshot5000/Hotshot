/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by sebas on 16.04.2016.
 */
public class ShowKeyboardContainerListener extends ENG_Container.ContainerListener {

    public static class ShowKeyboardContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "ShowKeyboard";

        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new ShowKeyboardContainerListener(TYPE, container);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    public ShowKeyboardContainerListener(String type, ENG_Container container) {
        super(type, container, null);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }

    @Override
    public void onActivation() {
        ENG_Container container = getParentContainer();
        container.applyCurrentFocusedView();
    }

    @Override
    public void onDestruction() {

    }
}
