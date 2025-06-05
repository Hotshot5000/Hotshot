/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:29 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.parser.dispatchers.AbstractEventDispatcher;

public abstract class Event {

    public long currentStartingTime;

    public enum EventState {
        NONE, STARTABLE, STARTED, FINISHABLE, WAITING_FOR_END_DELAY, FINISHED
    }

    private static final boolean DEBUG = false;
    private EventState state = EventState.NONE;
    public final String name;
    // If continuous then the event is called every frame. If not, only once.
    private boolean continuous = true;
    private boolean ranOnce;

    public Event(String name) {
        this.name = name;
    }

    public void init() {

    }

    /**
     *
     * @param dispatcher
     * @return true if the event was handled.
     */
    public abstract boolean accept(AbstractEventDispatcher dispatcher);

    public boolean acceptConditionally(AbstractEventDispatcher dispatcher) {
        if (!continuous && ranOnce) {
            return true;
        }
        boolean ret = accept(dispatcher);
        if (DEBUG) {
            System.out.println("Event: " + name + " accepted by event dispatcher with ret: " + ret);
        }
        if (!continuous && ret) {
            ranOnce = true;
        }
        return ret;
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }
}
