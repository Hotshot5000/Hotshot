/*
 * Created by Sebastian Bugiu on 22/10/2025, 16:00
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 22/10/2025, 16:00
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai.skynet;

import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

public class SquadMemberProperties {

    private long closestMemberId = -1;
    private long lastClosestMemberId = -1;
    private float closestMemberDistance = Float.POSITIVE_INFINITY;
    private float lastClosestMemberDistance = Float.POSITIVE_INFINITY;
    private float closestMemberSpeed = Float.POSITIVE_INFINITY;
    private float lastClosestMemberSpeed = Float.POSITIVE_INFINITY;
    private final ENG_Vector4D position = new ENG_Vector4D(true);
    private final ENG_Vector4D targetPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D lastPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D lastTargetPosition = new ENG_Vector4D(true);
    private final ENG_Quaternion orientation = new ENG_Quaternion();
    private final ENG_Quaternion targetOrientation = new ENG_Quaternion();
    private final ENG_Quaternion lastOrientation = new ENG_Quaternion();
    private final ENG_Quaternion lastTargetOrientation = new ENG_Quaternion();
    // Aggregate for all the targets calculated that specify where the ship should head next for this frame.
    private final ENG_Vector4D finalTargetPosition = new ENG_Vector4D(true);
    private long targetId = -1; // This could be long, lat since all members should be able to arrive at destination.
    private boolean avoidingCollision; // Always avoid to the right, like birds do? This would make it too predictable.
    private boolean leader;

    public void update() {

    }

    public long getClosestMemberId() {
        return closestMemberId;
    }

    public void setClosestMemberId(long closestMemberId) {
        this.closestMemberId = closestMemberId;
    }

    public long getLastClosestMemberId() {
        return lastClosestMemberId;
    }

    public void setLastClosestMemberId(long lastClosestMemberId) {
        this.lastClosestMemberId = lastClosestMemberId;
    }

    public float getClosestMemberDistance() {
        return closestMemberDistance;
    }

    public void setClosestMemberDistance(float closestMemberDistance) {
        this.closestMemberDistance = closestMemberDistance;
    }

    public float getLastClosestMemberDistance() {
        return lastClosestMemberDistance;
    }

    public void setLastClosestMemberDistance(float lastClosestMemberDistance) {
        this.lastClosestMemberDistance = lastClosestMemberDistance;
    }

    public float getClosestMemberSpeed() {
        return closestMemberSpeed;
    }

    public void setClosestMemberSpeed(float closestMemberSpeed) {
        this.closestMemberSpeed = closestMemberSpeed;
    }

    public float getLastClosestMemberSpeed() {
        return lastClosestMemberSpeed;
    }

    public void setLastClosestMemberSpeed(float lastClosestMemberSpeed) {
        this.lastClosestMemberSpeed = lastClosestMemberSpeed;
    }

    public ENG_Vector4D getPosition() {
        return position;
    }

    public void setPosition(ENG_Vector4D position) {
        this.position.set(position);
    }

    public ENG_Vector4D getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(ENG_Vector4D targetPosition) {
        this.targetPosition.set(targetPosition);
    }

    public ENG_Vector4D getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(ENG_Vector4D lastPosition) {
        this.lastPosition.set(lastPosition);
    }

    public ENG_Vector4D getLastTargetPosition() {
        return lastTargetPosition;
    }

    public void setLastTargetPosition(ENG_Vector4D lastTargetPosition) {
        this.lastTargetPosition.set(lastTargetPosition);
    }

    public ENG_Quaternion getOrientation() {
        return orientation;
    }

    public void setOrientation(ENG_Quaternion orientation) {
        this.orientation.set(orientation);
    }

    public ENG_Quaternion getTargetOrientation() {
        return targetOrientation;
    }

    public void setTargetOrientation(ENG_Quaternion targetOrientation) {
        this.targetOrientation.set(targetOrientation);
    }

    public ENG_Quaternion getLastOrientation() {
        return lastOrientation;
    }

    public void setLastOrientation(ENG_Quaternion lastOrientation) {
        this.lastOrientation.set(lastOrientation);
    }

    public ENG_Quaternion getLastTargetOrientation() {
        return lastTargetOrientation;
    }

    public void setLastTargetOrientation(ENG_Quaternion lastTargetOrientation) {
        this.lastTargetOrientation.set(lastTargetOrientation);
    }

    public void resetClosestMemberId() {
        closestMemberId = -1;
        closestMemberDistance = Float.POSITIVE_INFINITY;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public void getFinalTargetPosition(ENG_Vector4D destination) {
        destination.set(finalTargetPosition);
    }
}
