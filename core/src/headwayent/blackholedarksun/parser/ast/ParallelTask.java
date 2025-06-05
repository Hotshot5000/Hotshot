/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import java.util.ArrayList;

import headwayent.blackholedarksun.parser.dispatchers.AbstractEventDispatcher;

public class ParallelTask extends Event {

    public static final String TYPE = "ParallelTask";
    private final ArrayList<DelayedEvent> eventList;

    public ParallelTask(ArrayList<DelayedEvent> eventList) {
        super(TYPE);
        this.eventList = eventList;
    }

    @Override
    public void init() {
        super.init();
        for (DelayedEvent delayedEvent : eventList) {
            delayedEvent.init();
        }

    }

    public ArrayList<DelayedEvent> getEventList() {
        return eventList;
    }

    @Override
    public boolean accept(AbstractEventDispatcher dispatcher) {
        return dispatcher.dispatch(this);
    }
}
