/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/19/16, 9:57 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by sebas on 05.07.2016.
 */
public class MultiplayerLevelEndedContainerListener extends MultiplayerEventFinalizationContainerListener {

    public static class MultiplayerLevelEndedContainerListenerFactory extends ENG_Container.ContainerListenerFactory {

        public static final String TYPE = "MultiplayerLevelEndedMenu";

        /** @noinspection deprecation*/
        @Override
        public ENG_Container.ContainerListener createContainerListener(ENG_Container container, Bundle bundle) {
            return new MultiplayerLevelEndedContainerListener(TYPE, container, bundle);
        }

        @Override
        public void destroyContainerListener(ENG_Container.ContainerListener listener) {

        }
    }

    /** @noinspection deprecation*/
    public MultiplayerLevelEndedContainerListener(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    @Override
    public void onActivation() {
        LevelEvent.EventState eventState = (LevelEvent.EventState) getBundle().getObject("eventState");
        if (eventState != LevelEvent.EventState.CONNECTION_LOST) {
            super.onActivation();
        }
    }
}
