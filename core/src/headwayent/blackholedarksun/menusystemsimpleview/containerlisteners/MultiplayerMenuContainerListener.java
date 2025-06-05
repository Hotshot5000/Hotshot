/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

public class MultiplayerMenuContainerListener extends ContainerListenerWithBusAndWithLobbyInvitationCheck {

    public static class MultiplayerMenuContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerLoggedInMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerMenuContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerMenuContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void preContainerUpdate() {

    }

    @Override
    public void postContainerUpdate() {

    }
}
