/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/2/21, 9:52 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.LinkedList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;

/**
 * Created by sebas on 14-Sep-17.
 */

public class PlayerShipMotionState extends EntityMotionState {

    private static final int CAMERA_POSITIONS_NUM = 10;
    private static final int CAMERA_PITCH_ANGLES_NUM = 15;
    private static final float CAMERA_DIV = 1.0f / (CAMERA_POSITIONS_NUM + 1);
    private static final float CAMERA_PITCH_DIV = 1.0f / (CAMERA_PITCH_ANGLES_NUM + 1);
    private static final boolean PITCH_CAMERA_TO_TARGET = true;

    private final CameraProperties cameraProperties;
    private final ENG_Quaternion tempOrientation = new ENG_Quaternion(true);
    private final ENG_Vector4D cameraDelta = new ENG_Vector4D(0.0f, 50.0f, 200.0f, 1.0f);
    private final ENG_Quaternion cameraOrientation = ENG_Quaternion.fromAngleAxisDegRet(-25.0f, ENG_Math.VEC4_X_UNIT);
    private final ENG_Vector4D cameraDeltaFinal = new ENG_Vector4D(true);
    private final ENG_Vector4D cameraUnadjustedPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D finalCameraPos = new ENG_Vector4D();
    private final ENG_Vector4D tempCameraPos = new ENG_Vector4D();
    private final LinkedList<ENG_Vector4D> finalCameraPositions = new LinkedList<>();
    private final LinkedList<ENG_Float> cameraPitchAngles = new LinkedList<>();
    private final ENG_Vector4D playerPos = new ENG_Vector4D(true);
//    private final ENG_Quaternion tempOrientationChanged = new ENG_Quaternion(true);

    public PlayerShipMotionState(EntityProperties entityProperties,
                                 ShipProperties shipProperties, CameraProperties cameraProperties) {
        super(entityProperties, shipProperties);
        this.cameraProperties = cameraProperties;
        for (int i = 0; i < CAMERA_POSITIONS_NUM; ++i) {
            finalCameraPositions.add(new ENG_Vector4D());
        }
        for (int i = 0; i < CAMERA_PITCH_ANGLES_NUM; ++i) {
            cameraPitchAngles.add(new ENG_Float());
        }
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        super.setWorldTransform(worldTrans);
        Quaternion quaternion = getQuaternion();
        Vector3 pos = getPos();
//        System.out.println("PlayerShip Pos: " + pos);
        if (cameraProperties != null && !getEntityProperties().isDestroyed() && !cameraProperties.isAnimatedCamera()) {
            // System.out.println("tempVelocity: " + tempVelocity);
            // System.out.println("dir: " + dir);
            // System.out.println("velocity: " + velocity);
//            System.out.println("cameraProperties updated by setWorldTransform()");

            // PlayerShipMotionState operates only on client side.
            playerPos.set(pos.x, pos.y, pos.z);
            cameraProperties.getNode()._setDerivedPosition(playerPos);
            // System.out.println("camera node set address " +
            // cameraProperties.getNode());
            // System.out.println("camera node set at " + finalPos);
            // System.out.println("camera node derived pos " +
            // cameraProperties.getNode()._getDerivedPosition());
            /*
             * ENG_SceneNode parentSceneNode =
			 * ENG_RenderRoot.getRenderRoot().getSceneManager(
			 * APP_Game.SCENE_MANAGER)
			 * .getCamera(APP_Game.MAIN_CAM).getParentSceneNode(); if
			 * (parentSceneNode == cameraProperties.getNode()) {
			 * System.out.println("camera parent coincides with node"); } else {
			 * System.out.println("camera parent does not coincide with node");
			 * }
			 */
            // System.out.println("finalPos: " + finalPos);
            // tempOrientation.normalize();
            // System.out.println(tempOrientation);
            tempOrientation.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
            cameraProperties.getNode().setOrientation(tempOrientation);
//            if (!tempOrientation.equals(tempOrientationChanged)) {
//                tempOrientationChanged.set(tempOrientation);
//                ENG_Vector3D axis = new ENG_Vector3D();
//                float angleDeg = tempOrientation.toAngleAxisDeg(axis);
//                System.out.println("PlayerShip orientation changed currentTime: " + ENG_Utility.nanoTime() + " axis: " + axis + " angleDeg: " + angleDeg);
//            }

            // TODO Fix this later!!!
            if (MainApp.getGame().isThirdPersonCamera()) {
//				cameraProperties.getNode()._update(true, true);
//                cameraOrientation.mul(cameraDelta, cameraDeltaFinal);
                cameraDeltaFinal.set(cameraDelta);
                cameraUnadjustedPosition.set(cameraDelta);
                if (cameraProperties.getCollisionDistance() < Float.MAX_VALUE) {
                    float cameraDeltaLength = cameraDelta.length();
                    if (cameraProperties.getCollisionDistance() < cameraDeltaLength) {
                        cameraDeltaFinal.mul(cameraProperties.getCollisionDistance() / cameraDeltaLength);
                    }
                }
                // Orient the shortened camera pos for the real position.
                tempOrientation.mul(cameraDeltaFinal, tempCameraPos);
                finalCameraPos.set(tempCameraPos);
                for (ENG_Vector4D v : finalCameraPositions) {
                    finalCameraPos.addInPlace(v);
                }
                finalCameraPos.mul(CAMERA_DIV);
                ENG_Vector4D poll = finalCameraPositions.poll();
                poll.set(tempCameraPos);
                finalCameraPositions.add(poll);

                // Orient the "true" camera position without any collision adjustments.
                tempOrientation.mul(cameraUnadjustedPosition, tempCameraPos);
                tempCameraPos.addInPlace(playerPos);
                cameraProperties.setUnadjustedCameraPosition(tempCameraPos);

//                System.out.println("finalCamerPos: " + finalCameraPos);
//                ENG_Vector4D position = cameraProperties.getNode().getPosition();
                // Pitch the nose of the ship based on what the ship front vec ray test distance returns.
                if (PITCH_CAMERA_TO_TARGET) {
//                    if (cameraProperties.getClosestTargetDistance() < Float.MAX_VALUE) {
//                    ENG_Vector4D currentFrontVec = getSceneNode().getLocalInverseZAxis();
//                    currentFrontVec.normalize();
//                    currentFrontVec.mul(cameraProperties.getClosestTargetDistance());
//                    ENG_Vector4D targetPosition = getSceneNode().getPosition().addAsPt(currentFrontVec);
//                    Utility.lookAt(targetPosition, cameraProperties.getNode());
                        float angle = cameraProperties.getClosestTargetDistance() < Float.MAX_VALUE ?
                                ENG_Math.atan(cameraDelta.y / (cameraProperties.getClosestTargetDistance() + cameraDelta.z)) : 0.0f;
                        float angleAveraged = angle;
                        for (ENG_Float cameraPitchAngle : cameraPitchAngles) {
                            angleAveraged += cameraPitchAngle.getValue();
                        }
                        angleAveraged *= CAMERA_PITCH_DIV;
                        ENG_Float cameraAngle = cameraPitchAngles.poll();
                        cameraAngle.setValue(angle);
                        cameraPitchAngles.add(cameraAngle);
//                        System.out.println("pitch camera angle: " + (-angleAveraged * ENG_Math.RADIANS_TO_DEGREES));
                        cameraProperties.getNode().pitch(-angleAveraged);
//                    } else {
//                        // Also move back to original 0 deviation.
//                        ENG_Float cameraAngle = cameraPitchAngles.poll();
//                        cameraAngle.setValue(0.0f);
//                        cameraPitchAngles.add(cameraAngle);
//                    }
                }
                cameraProperties.getNode().translate(finalCameraPos);
            }
        }
    }

    public void resetCameraPositions() {
        for (ENG_Vector4D v : finalCameraPositions) {
            v.set(ENG_Math.PT4_ZERO);
        }
        for (ENG_Float cameraPitchAngle : cameraPitchAngles) {
            cameraPitchAngle.setValue(0.0f);;
        }

    }
}
