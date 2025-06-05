/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.menusystemsimpleview.containerlisteners;

import com.google.common.eventbus.EventBus;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.gui.simpleview.ENG_Container;

/**
 * Created by Sebastian on 13.05.2015.
 */
public abstract class ContainerListenerWithBus extends ENG_Container.ContainerListener {

    /** @noinspection UnstableApiUsage*/
    private EventBus bus;

    /** @noinspection deprecation*/
    public ContainerListenerWithBus(String type, ENG_Container container, Bundle bundle) {
        super(type, container, bundle);
    }

    /** @noinspection UnstableApiUsage */
    @Override
    public void onActivation() {
        bus = MainApp.getGame().getEventBus();
        bus.register(this);
        bus.register(getParentContainer());
    }

    /** @noinspection UnstableApiUsage */
    @Override
    public void onDestruction() {
        if (bus != null) {
            bus.unregister(getParentContainer());
            bus.unregister(this);
        }
    }

    /** @noinspection UnstableApiUsage*/
    public EventBus getBus() {
        if (bus == null) {
            throw new IllegalStateException("call super.onActivation() to init the event bus");
        }
        return bus;
    }
}
