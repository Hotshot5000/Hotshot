/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.menus.LevelSelection;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Button;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by Sebi on 17.05.2014.
 */
public class HideInvalidLevelsContainerListener extends ENG_Container.ContainerListener {

    public static class HideInvalidLevelsContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "HideInvalidLevels";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(
                ENG_Container container, Bundle bundle) {
            return new HideInvalidLevelsContainerListener(TYPE, container);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

//    private ENG_Container container;

    public HideInvalidLevelsContainerListener(String type, ENG_Container container) {
        super(type, container, null);
//        this.container = container;
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }

    @Override
    public void onActivation() {

        int maxLevelReached = MainApp.getGame().getMaxLevelReached();
        LevelSelection container = (LevelSelection) getParentContainer();
        int i = 0;
        for (ENG_Button button : container.getButtonList()) {
            if (i++ >= maxLevelReached) {
                button.setVisible(false);
            }
        }
    }

    @Override
    public void onDestruction() {

    }
}
