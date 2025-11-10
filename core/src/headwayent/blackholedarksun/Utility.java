/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/2/21, 5:37 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

/**
 * Created by sebas on 03-Jan-18.
 */

public class Utility {

    public static void rotateToPosition(EntityProperties entityProperties, EntityProperties otherEntityProperties,
                                        float maxAngularVelocity) {
//        Vector3 linearVelocity = new Vector3();
//        Vector3 angularVelocity = new Vector3();
//        Matrix4 worldTransform = entityProperties.getRigidBody().getWorldTransform();
//        Matrix4 otherWorldTransform = otherEntityProperties.getRigidBody().getWorldTransform();
//        System.out.println("worldTransform: " + worldTransform + " otherWorldTransform: " + otherWorldTransform);
        ENG_Vector4D entityPos = entityProperties.getNode().getPosition();
        ENG_Quaternion entityOrientation = entityProperties.getNode().getOrientation();
        ENG_Vector4D othEntityPosition = otherEntityProperties.getNode().getPosition();
        ENG_Quaternion othEntityOrientation = otherEntityProperties.getNode().getOrientation();
        ENG_Vector4D newDir = otherEntityProperties.getNode().getPosition().subAsVec(entityProperties.getNode().getPosition());
        newDir.normalize();

        ENG_Vector4D currentDir = entityProperties.getNode().getLocalInverseZAxis();
//        System.out.println("entityPos: " + entityPos + " entityOrientation: " + entityOrientation +
//                " othEntityPos: " + othEntityPosition + " othEntityOrientation: " + othEntityOrientation);
        float cosAngle = currentDir.dotProduct(newDir);
        if (cosAngle > 0.999f) {
            entityProperties.getRigidBody().setAngularVelocity(new Vector3());
        } else {
            if (cosAngle <= -1.0f) {
                System.out.println("dafuq cosAngle: " + cosAngle);
            } else {
                float angle = ENG_Math.acos(cosAngle);
                ENG_Vector4D axis = currentDir.crossProduct(newDir);
//                axis.normalize();
                Vector3 angularVelocity = new Vector3(axis.x, axis.y, axis.z);
                float EXPONENTIAL_TIMESCALE = 0.5f;
                float deltaTime = MainApp.getMainThread().getCurrentElapsedTime();
                if (deltaTime > EXPONENTIAL_TIMESCALE) {
                    EXPONENTIAL_TIMESCALE = 1.5f * deltaTime;
                }
                float angularSpeed = angle / EXPONENTIAL_TIMESCALE * 10000.0f;
//                System.out.println("angularVelocity: " + angularVelocity + " angularSpeed: " + angularSpeed +
//                        " maxAngularVelocity: " + maxAngularVelocity + " angle in deg: " + (angle * ENG_Math.RADIANS_TO_DEGREES));
                if (angularSpeed > maxAngularVelocity) {
                    angularSpeed = maxAngularVelocity;
                }
                angularVelocity.x *= angularSpeed;
                angularVelocity.y *= angularSpeed;
                angularVelocity.z *= angularSpeed;
                entityProperties.getRigidBody().setAngularVelocity(angularVelocity);
            }
        }
//        btTransformUtil.calculateVelocity(worldTransform,
//                otherWorldTransform,
//                updateInverval, linearVelocity, angularVelocity);
//        btTransformUtil.calculateVelocityQuaternion(new Vector3(entityPos.x, entityPos.y, entityPos.z),
//                new Vector3(othEntityPosition.x, othEntityPosition.y, othEntityPosition.z),
//                new Quaternion(entityOrientation.x, entityOrientation.y, entityOrientation.z, entityOrientation.w),
//                new Quaternion(rotationTo.x, rotationTo.y, rotationTo.z, rotationTo.w),
//                MainApp.getMainThread().getCurrentElapsedTime(), linearVelocity, angularVelocity);
////        entityProperties.getRigidBody().setLinearVelocity(linearVelocity);
//        angularVelocity.nor();
//        angularVelocity.x *= 50.0f;
//        angularVelocity.y *= 50.0f;
//        angularVelocity.z *= 50.0f;
//        System.out.println("angularVelocity: " + angularVelocity + " len: " + angularVelocity.len());
//        entityProperties.getRigidBody().applyTorqueImpulse(angularVelocity);
    }

    public static void rotateToPositionParallel(ENG_Vector4D currentFrontVec, ENG_Vector4D destVec, float updateInterval,
                                        EntityProperties entityProperties, float maxAngularVelocity) {
        ENG_Vector4D torque = new ENG_Vector4D();
        ENG_Math.rotateToPositionTorque(currentFrontVec, destVec, updateInterval, torque, maxAngularVelocity);


        btRigidBody rigidBody = entityProperties.getRigidBody();
//        Matrix3 inertiaTensorWorld = rigidBody.getInvInertiaTensorWorld().inv();
//        Matrix4 inertiaTensorWorldMat4 = new Matrix4();
//        inertiaTensorWorldMat4.set(inertiaTensorWorld);
//        Matrix4 invWorldTransform = rigidBody.getWorldTransform().inv();
////        inertiaTensorWorldMat4.mul(invWorldTransform);
//        Quaternion inertiaTensorRotation = new Quaternion();
//        inertiaTensorWorldMat4.getRotation(inertiaTensorRotation);
//        ENG_Vector3D axis = new ENG_Vector3D();
//        ENG_Quaternion inertiaTensorRotationOgre = new ENG_Quaternion(inertiaTensorRotation.x, inertiaTensorRotation.y, inertiaTensorRotation.z, inertiaTensorRotation.w);
//        float angle = inertiaTensorRotationOgre.toAngleAxisDeg(axis);
////        System.out.println("inertiaTensorRotationOgre of entity: " + entityProperties.getName() + " : " + inertiaTensorRotationOgre + " inertiaTensorRotationOgre axis: " + axis + " angle: " + angle);
//        Quaternion qGdx = rigidBody.getOrientation().mul(inertiaTensorRotation);
//        ENG_Quaternion q = new ENG_Quaternion(qGdx.x, qGdx.y, qGdx.z, qGdx.w);
////        ENG_Vector3D axis = new ENG_Vector3D();
////        float angle = q.toAngleAxisDeg(axis);
////        System.out.println("q of entity: " + entityProperties.getName() + " : " + q + " q axis: " + axis + " angle: " + angle);
//
////        float[] matValues = inertiaTensorWorld.getValues();
////        ENG_Matrix4 rotationMatrix = new ENG_Matrix4();
////        rotationMatrix.setRow(0, new ENG_Vector3D(matValues[Matrix3.M00], matValues[Matrix3.M01], matValues[Matrix3.M02]));
////        rotationMatrix.setRow(1, new ENG_Vector3D(matValues[Matrix3.M10], matValues[Matrix3.M11], matValues[Matrix3.M12]));
////        rotationMatrix.setRow(2, new ENG_Vector3D(matValues[Matrix3.M20], matValues[Matrix3.M21], matValues[Matrix3.M22]));
////        ENG_Quaternion inertiaTensorRotation = ENG_Quaternion.fromRotationMatrixRet(rotationMatrix);
////        float[] f = new float[16];
////        Quaternion orientationGdx = rigidBody.getOrientation();
////        ENG_Quaternion orientation = new ENG_Quaternion(orientationGdx.x, orientationGdx.y, orientationGdx.z, orientationGdx.w);
////        ENG_Quaternion q = orientation.mulRet(inertiaTensorRotation);
//
//        ENG_Quaternion inverseQ = q.inverseRet();
//        ENG_Vector4D transformedTorque = inverseQ.mul(torque);
////        System.out.println("Torque applied to entity: " + entityProperties.getName() + " : " + torque + " torque len: " + torque.length()
////                + " transformedTorque: " + transformedTorque + " transformedTorque len: " + transformedTorque.length());
//        Vector3 inertiaGdx = new Vector3();
//        entityProperties.getCollisionShape().calculateLocalInertia(entityProperties.getWeight(), inertiaGdx);
//        ENG_Vector4D inertia = new ENG_Vector4D(inertiaGdx.x, inertiaGdx.y, inertiaGdx.z, 1.0f);
////        System.out.println("Inertia of entity: " + entityProperties.getName() + " : " + inertia + " inertia len: " + inertia.length());
//
//        ENG_Vector4D finalTorque = q.mul(transformedTorque);
////        Vector3 btTorque = new Vector3(finalTorque.x, finalTorque.y, finalTorque.z);
        Vector3 btTorque = new Vector3(torque.x, torque.y, torque.z);
        rigidBody.applyTorqueImpulse(btTorque);

        if (MainActivity.isDebugmode()) {
            if (torque.isNaN()) {
                throw new IllegalArgumentException("torque is NAN");
            }
        }

//        System.out.println("Torque applied to entity: " + entityProperties.getName() + " : " + torque + " torque len: " + torque.length()
//        + " finalTorque: " + finalTorque + " finalTorque len: " + finalTorque.length());
    }

    private static final ENG_Vector4D torque = new ENG_Vector4D();
//    private static Matrix4 inertiaTensorWorldMat4 = new Matrix4();
//    private static Quaternion inertiaTensorRotation = new Quaternion();
//    private static ENG_Vector3D axis = new ENG_Vector3D();
//    private static ENG_Quaternion inertiaTensorRotationOgre = new ENG_Quaternion();
//    private static ENG_Quaternion q = new ENG_Quaternion();
//    private static Vector3 inertiaGdx = new Vector3();
//    private static ENG_Quaternion inverseQ = new ENG_Quaternion();
//    private static ENG_Vector4D transformedTorque = new ENG_Vector4D();
//    private static ENG_Vector4D inertia = new ENG_Vector4D();
    private static final Vector3 btTorque = new Vector3();

    public static void applyTorque(EntityProperties entityProperties, ENG_Vector4D torque) {
        btTorque.set(torque.x, torque.y, torque.z);
        entityProperties.getRigidBody().applyTorqueImpulse(btTorque);

        if (MainActivity.isDebugmode()) {
            if (torque.isNaN()) {
                throw new IllegalArgumentException("torque is NAN");
            }
        }
    }

    public static void applyTorqueParallel(EntityProperties entityProperties, ENG_Vector4D torque) {
        entityProperties.getRigidBody().applyTorqueImpulse(new Vector3(torque.x, torque.y, torque.z));

        if (MainActivity.isDebugmode()) {
            if (torque.isNaN()) {
                throw new IllegalArgumentException("torque is NAN");
            }
        }
    }

    public static void rotateToPosition(ENG_Vector4D currentFrontVec, ENG_Vector4D destVec, float updateInterval,
                                        EntityProperties entityProperties, float maxAngularVelocity) {
        ENG_Math.rotateToPositionTorque(currentFrontVec, destVec, updateInterval, torque, maxAngularVelocity);


        btRigidBody rigidBody = entityProperties.getRigidBody();
//        Matrix3 inertiaTensorWorld = rigidBody.getInvInertiaTensorWorld().inv();
//        inertiaTensorWorldMat4.set(inertiaTensorWorld);
//        Matrix4 invWorldTransform = rigidBody.getWorldTransform().inv();
////        inertiaTensorWorldMat4.mul(invWorldTransform);
//        inertiaTensorWorldMat4.getRotation(inertiaTensorRotation);
//        inertiaTensorRotationOgre.set(inertiaTensorRotation.x, inertiaTensorRotation.y, inertiaTensorRotation.z, inertiaTensorRotation.w);
//        float angle = inertiaTensorRotationOgre.toAngleAxisDeg(axis);
////        System.out.println("inertiaTensorRotationOgre of entity: " + entityProperties.getName() + " : " + inertiaTensorRotationOgre + " inertiaTensorRotationOgre axis: " + axis + " angle: " + angle);
//        Quaternion qGdx = rigidBody.getOrientation().mul(inertiaTensorRotation);
//        q.set(qGdx.x, qGdx.y, qGdx.z, qGdx.w);
////        ENG_Vector3D axis = new ENG_Vector3D();
////        float angle = q.toAngleAxisDeg(axis);
////        System.out.println("q of entity: " + entityProperties.getName() + " : " + q + " q axis: " + axis + " angle: " + angle);
//
////        float[] matValues = inertiaTensorWorld.getValues();
////        ENG_Matrix4 rotationMatrix = new ENG_Matrix4();
////        rotationMatrix.setRow(0, new ENG_Vector3D(matValues[Matrix3.M00], matValues[Matrix3.M01], matValues[Matrix3.M02]));
////        rotationMatrix.setRow(1, new ENG_Vector3D(matValues[Matrix3.M10], matValues[Matrix3.M11], matValues[Matrix3.M12]));
////        rotationMatrix.setRow(2, new ENG_Vector3D(matValues[Matrix3.M20], matValues[Matrix3.M21], matValues[Matrix3.M22]));
////        ENG_Quaternion inertiaTensorRotation = ENG_Quaternion.fromRotationMatrixRet(rotationMatrix);
////        float[] f = new float[16];
////        Quaternion orientationGdx = rigidBody.getOrientation();
////        ENG_Quaternion orientation = new ENG_Quaternion(orientationGdx.x, orientationGdx.y, orientationGdx.z, orientationGdx.w);
////        ENG_Quaternion q = orientation.mulRet(inertiaTensorRotation);
//
//        q.inverseRet(inverseQ);
//        inverseQ.mul(torque, transformedTorque);
////        System.out.println("Torque applied to entity: " + entityProperties.getName() + " : " + torque + " torque len: " + torque.length()
////                + " transformedTorque: " + transformedTorque + " transformedTorque len: " + transformedTorque.length());
//        entityProperties.getCollisionShape().calculateLocalInertia(entityProperties.getWeight(), inertiaGdx);
//        inertia.set(inertiaGdx.x, inertiaGdx.y, inertiaGdx.z, 1.0f);
////        System.out.println("Inertia of entity: " + entityProperties.getName() + " : " + inertia + " inertia len: " + inertia.length());
//
//        ENG_Vector4D finalTorque = q.mul(transformedTorque);
////        Vector3 btTorque = new Vector3(finalTorque.x, finalTorque.y, finalTorque.z);
        btTorque.set(torque.x, torque.y, torque.z);
        rigidBody.applyTorqueImpulse(btTorque);

        if (MainActivity.isDebugmode()) {
            if (torque.isNaN()) {
                throw new IllegalArgumentException("torque is NAN");
            }
        }

//        System.out.println("Torque applied to entity: " + entityProperties.getName() + " : " + torque + " torque len: " + torque.length()
//        + " finalTorque: " + finalTorque + " finalTorque len: " + finalTorque.length());
    }

//    public static void rotateToPosition2(ENG_Quaternion currentOrientation, ENG_Quaternion targetOrientation, float updateInterval,
//                                  EntityProperties entityProperties) {
//        btQuaternion curOrientation = new btQuaternion(currentOrientation.x, currentOrientation.y, currentOrientation.z, currentOrientation.w);
//        btQuaternion destOrientation = new btQuaternion(targetOrientation.x, targetOrientation.y, targetOrientation.z, targetOrientation.w);
//        Quaternion inverse = curOrientation.inverse();
//        Quaternion deltaOrientation = destOrientation.operatorMultiplicationAssignment(inverse);
//        btQuaternion quat = new btQuaternion(deltaOrientation.x, deltaOrientation.y, deltaOrientation.z, deltaOrientation.w);
//        btVector3 euler = new btVector3();
//        ENG_Math.quaternionToEulerXYZ(quat, euler);
//        btVector3 torqueToApply = new btVector3(euler.x(), euler.y(), euler.z());
//        Vector3 temp = torqueToApply.operatorMultiplicationAssignment(100.5f);
//        Vector3 btTorque = new Vector3(temp.x, temp.y, temp.z);
//        entityProperties.getRigidBody().applyTorque(btTorque);
////        Matrix3 mat = entityProperties.getRigidBody().getInvInertiaTensorWorld().inv();
////        Matrix4 worldTransform = entityProperties.getRigidBody().getWorldTransform();
////        btTransform trans = new btTransform();
////        trans.operatorAssignment(worldTransform);
////        Matrix3 basis = trans.getBasis();
////        Matrix3 worldTransByBasis = mat.mul(basis);
////        Vector3 finalTorque = btTorque.mul(worldTransByBasis);
////        entityProperties.getRigidBody().applyTorqueImpulse(finalTorque);
//    }

    public static void rotateAwayFromPositionParallel(ENG_Vector4D currentFrontVec, ENG_Vector4D destVec, float updateInterval,
                                              EntityProperties entityProperties, float maxAngularVelocity) {
        ENG_Vector4D torque = new ENG_Vector4D();
        ENG_Math.rotateAwayFromPositionTorque(currentFrontVec, destVec, updateInterval, torque, maxAngularVelocity);
        Vector3 btTorque = new Vector3(torque.x, torque.y, torque.z);
        if (MainActivity.isDebugmode()) {
            if (Float.isNaN(btTorque.x) || Float.isNaN(btTorque.y) || Float.isNaN(btTorque.z)) {
                throw new IllegalArgumentException("torque is NAN");
            }
        }
        entityProperties.getRigidBody().applyTorqueImpulse(btTorque);
    }

    private static final ENG_Vector4D torqueRotateAway = new ENG_Vector4D();
    private static final Vector3 btTorqueRotateAway = new Vector3();

    public static void rotateAwayFromPosition(ENG_Vector4D currentFrontVec, ENG_Vector4D destVec, float updateInterval,
                                              EntityProperties entityProperties, float maxAngularVelocity) {
        ENG_Math.rotateAwayFromPositionTorque(currentFrontVec, destVec, updateInterval, torqueRotateAway, maxAngularVelocity);
        btTorqueRotateAway.set(torqueRotateAway.x, torqueRotateAway.y, torqueRotateAway.z);
        if (MainActivity.isDebugmode()) {
            if (Float.isNaN(btTorqueRotateAway.x) || Float.isNaN(btTorqueRotateAway.y) || Float.isNaN(btTorqueRotateAway.z)) {
                throw new IllegalArgumentException("torque is NAN");
            }
        }
        entityProperties.getRigidBody().applyTorqueImpulse(btTorqueRotateAway);
    }

    public static void setPosition(btRigidBody body, btMotionState motionState,
                                   ENG_Vector4D position, ENG_Quaternion orientation, boolean clearForces) {
        setPosition(body, motionState, position, orientation, new Matrix4(), clearForces);
    }

    public static void setPosition(btRigidBody body, btMotionState motionState,
                                   ENG_Vector4D position, ENG_Quaternion orientation, Matrix4 initialTransform, boolean clearForces) {
        initialTransform.set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w);

        updatePositionPhysics(body, motionState, initialTransform, clearForces);
    }

    private static void updatePositionPhysics(btRigidBody body, btMotionState motionState, Matrix4 initialTransform, boolean clearForces) {
        if (clearForces) {
            body.clearForces();
        }

        body.setWorldTransform(initialTransform);
//        motionState.setWorldTransform(initialTransform);
    }

    public static void setPosition(btRigidBody body, btMotionState motionState, Vector3 position, Quaternion orientation, boolean clearForces) {
        setPosition(body, motionState, position, orientation, new Matrix4(), clearForces);
    }

    public static void setPosition(btRigidBody body, btMotionState motionState, Vector3 position,
                                   Quaternion orientation, Matrix4 initialTransform, boolean clearForces) {
        initialTransform.set(position, orientation);

        updatePositionPhysics(body, motionState, initialTransform, clearForces);
    }

    /**
     * Seems it doesn't actually cancel the angular velocity???
     * @param body
     */
    public static void clearForces(btRigidBody body) {
        body.clearForces();
    }

    public static void clearAngularVelocity(btRigidBody body) {
        body.setAngularVelocity(Vector3.Zero);
    }

    public static void clearLinearVelocity(btRigidBody body) {
        body.setLinearVelocity(Vector3.Zero);
    }

    public static void lookAt(ENG_SceneNode destination, ENG_SceneNode cameraNode) {
        lookAt(destination.getPosition(), cameraNode, false);
    }

    public static void lookAt(ENG_Vector4D destination, ENG_SceneNode cameraNode) {
        lookAt(destination, cameraNode, true);
    }

    public static void lookAtParallel(ENG_Vector4D destination, ENG_SceneNode cameraNode, boolean shouldCopyDestination) {
        ENG_Quaternion rot = new ENG_Quaternion();
        ENG_Math.rotateTowardPositionRad(shouldCopyDestination ? new ENG_Vector4D(destination) : destination,
                cameraNode.getPosition(),
                cameraNode.getLocalInverseZAxis(),
                cameraNode.getLocalYAxis(),
                rot,
                ENG_Math.PI);
        ENG_Quaternion orientation = cameraNode.getOrientation();
        rot.normalize();
        ENG_Quaternion orientationCopy = new ENG_Quaternion(orientation);
        ENG_Quaternion invOrientation = orientation.inverseRet();
        orientation.mulInPlace(invOrientation);
        orientation.mulInPlace(rot);
        orientation.mulInPlace(orientationCopy);
        cameraNode.setOrientation(orientation);
    }

    private static final ENG_Quaternion rot = new ENG_Quaternion();
    private static final ENG_Quaternion orientationCopy = new ENG_Quaternion();
    private static final ENG_Quaternion invOrientation = new ENG_Quaternion();
    private static final ENG_Quaternion orientationLookAt = new ENG_Quaternion();
    private static final ENG_Vector4D position = new ENG_Vector4D();
    private static final ENG_Vector4D localInverseZAxis = new ENG_Vector4D();
    private static final ENG_Vector4D localYAxis = new ENG_Vector4D();

    public static void lookAt(ENG_Vector4D destination, ENG_SceneNode cameraNode, boolean shouldCopyDestination) {
        cameraNode.getPosition(position);
        cameraNode.getLocalInverseZAxis(localInverseZAxis);
        cameraNode.getLocalYAxis(localYAxis);
        ENG_Math.rotateTowardPositionRad(shouldCopyDestination ? new ENG_Vector4D(destination) : destination,
                position,
                localInverseZAxis,
                localYAxis,
                rot,
                ENG_Math.PI);
        cameraNode.getOrientation(orientationLookAt);
        rot.normalize();
        orientationCopy.set(orientationLookAt);
        orientationLookAt.inverseRet(invOrientation);
        orientationLookAt.mulInPlace(invOrientation);
        orientationLookAt.mulInPlace(rot);
        orientationLookAt.mulInPlace(orientationCopy);
        cameraNode.setOrientation(orientationLookAt);
    }

}
