/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 1:56 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import headwayent.hotshotengine.ENG_Utility;

/**
 * For tracking missiles
 *
 * @author sebi
 */
public class TrackerProperties extends MultiplayerComponent {

    private long trackedEntityId;
//    private transient String trackedShip;
    private transient float maxAngularVelocity; // New way for max turn angle with physics.
    private transient long trackingDelay;
    private long trackingDelayTimeStarted;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public TrackerProperties() {

    }

//    public TrackerProperties(String trackedShip, float maxTurnAngle) {
//        this.trackedShip = trackedShip;
//        this.maxTurnAngle = maxTurnAngle;
//    }
//
//    public TrackerProperties(String trackedShip, float maxTurnAngle, long trackingDelay) {
//        this.trackedShip = trackedShip;
//        this.maxTurnAngle = maxTurnAngle;
//        this.trackingDelay = trackingDelay;
//        setTrackingDelayTimeStarted();
//    }
//
//    public TrackerProperties(String trackedShip) {
//        this.trackedShip = trackedShip;
//        setTrackingDelayTimeStarted();
//    }

    /**
     * Normally component properties are created using componentMapper.create()
     * but in this case we need a copy of an already created component just for
     * sending it into MultiplayerEntityTCP object.
     * @param trackedEntityId
     * @param maxTurnAngle
     * @param trackingDelay
     */
    public TrackerProperties(long trackedEntityId, float maxTurnAngle, long trackingDelay) {
        this.trackedEntityId = trackedEntityId;
        this.trackingDelay = trackingDelay;
        setTrackingDelayTimeStarted();
    }

    public TrackerProperties(TrackerProperties oth) {
        set(oth);
    }

    public void set(TrackerProperties oth) {
        this.trackedEntityId = oth.getTrackedEntityId();
        this.maxAngularVelocity = oth.getMaxAngularVelocity();
        this.trackingDelay = oth.getTrackingDelay();
        this.trackingDelayTimeStarted = oth.getTrackingDelayTimeStarted();
    }



    public long getTrackedEntityId() {
        return trackedEntityId;
    }

    public void setTrackedEntityId(long trackedEntityId) {
        this.trackedEntityId = trackedEntityId;
    }

//    public String getTrackedShipName() {
//        return trackedShip;
//    }

    public long getTrackingDelay() {
        return trackingDelay;
    }

    public float getMaxAngularVelocity() {
        return maxAngularVelocity;
    }

    public void setMaxAngularVelocity(float maxAngularVelocity) {
        this.maxAngularVelocity = maxAngularVelocity;
    }

    public void setTrackingDelay(long trackingDelay) {
        this.trackingDelay = trackingDelay;
    }

    public long getTrackingDelayTimeStarted() {
        return trackingDelayTimeStarted;
    }

    public void setTrackingDelayTimeStarted() {
        this.trackingDelayTimeStarted = ENG_Utility.currentTimeMillis();
    }
}
