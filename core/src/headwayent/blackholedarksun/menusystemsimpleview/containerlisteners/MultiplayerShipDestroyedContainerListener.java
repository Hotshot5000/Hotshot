/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by sebas on 28.01.2016.
 */
public class MultiplayerShipDestroyedContainerListener extends MultiplayerEventFinalizationContainerListener {

    public static class MultiplayerShipDestroyedContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerShipDestroyedMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerShipDestroyedContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerShipDestroyedContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

}
