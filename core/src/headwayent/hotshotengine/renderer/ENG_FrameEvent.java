/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_FrameEvent {

    /**
     * Elapsed time in seconds since the last event.
     * This gives you time between frame start & frame end,
     * and between frame end and next frame start.
     *
     * @remarks This may not be the elapsed time but the average
     * elapsed time between recently fired events.
     */
    public float timeSinceLastEvent;
    /**
     * Elapsed time in seconds since the last event of the same type,
     * i.e. time for a complete frame.
     *
     * @remarks This may not be the elapsed time but the average
     * elapsed time between recently fired events of the same type.
     */
    public float timeSinceLastFrame;
}
