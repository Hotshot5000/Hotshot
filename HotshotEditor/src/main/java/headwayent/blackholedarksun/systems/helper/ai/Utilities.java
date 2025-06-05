/*
 * Created by Sebastian Bugiu on 16/02/2025, 13:40
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 13:40
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems.helper.ai;

import headwayent.hotshotengine.ENG_Vector4D;

public class Utilities {

    private static final ENG_Vector4D currentPos = new ENG_Vector4D(true);
    private static final ENG_Vector4D otherPos = new ENG_Vector4D(true);
    private static final ENG_Vector4D distVec = new ENG_Vector4D(true);
    private static final ENG_Vector4D currentFrontVec = new ENG_Vector4D();
    private static final ENG_Vector4D otherFrontVec = new ENG_Vector4D();
    private static final ENG_Vector4D currentUpVec = new ENG_Vector4D();
    private static float updateInterval;

    /**
     * Non thread safe!
     *
     * @param entityProperties
     * @param shipProperties
     * @param pos
     */
//    public static void rotateTowardPosition(EntityProperties entityProperties,
//                                      ShipProperties shipProperties, ENG_Vector4D pos) {
//        entityProperties.getNode().getPosition(currentPos);
//        entityProperties.getNode().getLocalInverseZAxis(currentFrontVec);
//        entityProperties.getNode().getLocalYAxis(currentUpVec);
////        ENG_Math.rotateTowardPositionDeg(pos, currentPos, currentFrontVec, currentUpVec, rotation, getRotationAngle(shipProperties));
////        entityProperties.rotate(rotation, true, TransformSpace.TS_WORLD);
//        Utility.rotateToPosition(currentFrontVec, pos, updateInterval, entityProperties,
//                shipProperties.getShipData().maxAngularVelocity);
//    }
    public static float getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(float updateInterval) {
        Utilities.updateInterval = updateInterval;
    }
}
