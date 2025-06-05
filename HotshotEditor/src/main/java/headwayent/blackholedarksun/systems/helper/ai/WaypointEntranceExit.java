/*
 * Created by Sebastian Bugiu on 18/02/2025, 11:36
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 18/02/2025, 11:36
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai;

import headwayent.hotshotengine.ENG_Vector4D;

public class WaypointEntranceExit {

    private final ENG_Vector4D entranceExitDirection = new ENG_Vector4D();
    private float entranceExitAngle; // In Radians.
    private float entranceExitMinDistance;

    boolean isInCone(ENG_Vector4D position, ENG_Vector4D waypointPosition) {
        ENG_Vector4D diff = position.subAsPt(waypointPosition);
        if (diff.length() < entranceExitMinDistance) {
            return false;
        }
        diff.normalize();
        return diff.angleBetween(entranceExitDirection) < entranceExitAngle;
    }

    public ENG_Vector4D getEntranceExitDirection() {
        return entranceExitDirection;
    }

    public void setEntranceExitDirection(ENG_Vector4D direction) {
        entranceExitDirection.set(direction);
    }

    public float getEntranceExitAngle() {
        return entranceExitAngle;
    }

    public void setEntranceExitAngle(float entranceExitAngle) {
        this.entranceExitAngle = entranceExitAngle;
    }

    public float getEntranceExitMinDistance() {
        return entranceExitMinDistance;
    }

    public void setEntranceExitMinDistance(float entranceExitMinDistance) {
        this.entranceExitMinDistance = entranceExitMinDistance;
    }
}
