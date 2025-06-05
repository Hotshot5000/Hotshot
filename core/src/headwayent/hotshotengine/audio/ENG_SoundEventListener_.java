/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.audio;

import headwayent.hotshotengine.audio.ENG_ISoundRoot_.EventType;

/**
 * An abstract class for listening to triggered sound events.
 *
 * @author Sebi
 */
public abstract class ENG_SoundEventListener_ {

    /**
     * The method that should be overridden in order to listen to a registered event.
     *
     * @param name      The sound name for which the event has been triggered.
     * @param eventType The type of event for which this event has been triggered.
     */
    public abstract void fireEvent(String name, EventType eventType);
}
