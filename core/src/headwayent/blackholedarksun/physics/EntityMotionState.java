/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 6:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.TrackerProperties;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

/**
 * Created by sebas on 11-Sep-17.
 */

public class EntityMotionState extends btMotionState {

    private final ENG_SceneNode sceneNode;
    private final Vector3 pos = new Vector3();
    private final Quaternion quaternion = new Quaternion();
//    private btGhostObject ghostObject;
    private EntityProperties entityProperties;
    private ShipProperties shipProperties;
    private ProjectileProperties projectileProperties;
    private TrackerProperties trackerProperties;
    private final ENG_Vector3D prevPos = new ENG_Vector3D();
    private final ENG_Quaternion prevOrientation = new ENG_Quaternion();
    private ENG_Vector3D projectileDiff;

    private float prevAngle;
    private float prevPosLen;

    public EntityMotionState(ENG_SceneNode sceneNode) {
        this.sceneNode = sceneNode;
    }

    public EntityMotionState(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
        this.sceneNode = entityProperties.getNode();
    }

    public EntityMotionState(EntityProperties entityProperties, ShipProperties shipProperties) {
        this.entityProperties = entityProperties;
        this.shipProperties = shipProperties;
        this.sceneNode = entityProperties.getNode();
    }

    public EntityMotionState(EntityProperties entityProperties, ProjectileProperties projectileProperties) {
        this.entityProperties = entityProperties;
        this.projectileProperties = projectileProperties;
        this.sceneNode = entityProperties.getNode();
    }

    public EntityMotionState(EntityProperties entityProperties, ProjectileProperties projectileProperties, TrackerProperties trackerProperties) {
        this.entityProperties = entityProperties;
        this.projectileProperties = projectileProperties;
        this.trackerProperties = trackerProperties;
        this.sceneNode = entityProperties.getNode();
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        ENG_Vector4D derivedPosition = sceneNode._getDerivedPosition();
        ENG_Quaternion derivedOrientation = sceneNode._getDerivedOrientation();
        ENG_Vector4D derivedScale = sceneNode._getDerivedScale();
        prevPos.set(derivedPosition);
        prevOrientation.set(derivedOrientation);
        worldTrans.set(
                new Vector3(derivedPosition.x, derivedPosition.y, derivedPosition.z),
                new Quaternion(derivedOrientation.x, derivedOrientation.y, derivedOrientation.z, derivedOrientation.w),
                new Vector3(derivedScale.x, derivedScale.y, derivedScale.z));
//        ghostObject.setWorldTransform(worldTrans);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        worldTrans.getRotation(quaternion);
        worldTrans.getTranslation(pos);
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            if (sceneNode.getName().contains("Asteroid")) {
//                System.out.println("entity " + sceneNode.getName() + " pos: " + pos + " orientation: " + quaternion);
//            }
        }
        // These should probably be _setDerived*(). But for now use without derived. For compatibility when the physics is disabled.
//        sceneNode._setDerivedOrientationNative(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
//        sceneNode._setDerivedPositionNative(pos.x, pos.y, pos.z);
        APP_Game.WorldManagerMode worldManagerMode = MainApp.getGame().getWorldManagerMode();
        if (worldManagerMode == APP_Game.WorldManagerMode.SINGLEPLAYER ||
                worldManagerMode == APP_Game.WorldManagerMode.MULTIPLAYER ||
                (worldManagerMode == APP_Game.WorldManagerMode.MULTIPLAYER_SERVER_SIDE && (shipProperties == null || shipProperties.getUserId() == 0))) {
            // If in multiplayer mode server side only update the scene nodes which aren't clients' player ships.
            // The orientation in this case is only taken from what the client provides.
            // TODO check if the rotation of the player ship in a timespan is a valid rotation for the ship max angular velocity?
            sceneNode.setOrientation(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
        }

//        ENG_Vector4D test = new ENG_Vector4D();
//        float angleDeg = sceneNode.getOrientation().toAngleAxisDeg(test);
        sceneNode.setPosition(pos.x, pos.y, pos.z);

        if (projectileProperties != null) {
            if (projectileDiff == null) {
                projectileDiff = new ENG_Vector3D();
            }

            // Add to travelled distance.
            ENG_Vector4D currentPos = sceneNode.getPositionForNative();
            currentPos.sub(prevPos, projectileDiff);
//            System.out.println("projectileDiff: " + projectileDiff);
            projectileProperties.addToDistanceTraveled(projectileDiff.length());

//            ENG_Vector3D axis = new ENG_Vector3D();
//            float angle = sceneNode.getOrientationForNative().toAngleAxisDeg(axis);
//            System.out.println("Updated motion state for projectile: " + entityProperties.getUniqueName() + " with orientation axis: " +
//                   axis + " angle: " + angle);
        }

//        if (sceneNode.getName().contains("Sebi") && prevPosLen != pos.len()) {
//            System.out.println("Sebi len: " + pos.len());
//            prevPosLen = pos.len();
//        }

        prevPos.set(pos.x, pos.y, pos.z);
        prevOrientation.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
        // Don't bother with scaling for now.

//        ghostObject.setWorldTransform(worldTrans);

//        if (sceneNode.getName().contains("Piranha")) {
//            ENG_Vector4D othEntityAxis = new ENG_Vector4D();
//            float othEntityAngle = sceneNode.getOrientation().toAngleAxisDeg(othEntityAxis);
//            System.out.println("Piranha " + entityProperties.getItem().getName() + " angle: " + othEntityAngle + " angularVelocity: " + entityProperties.getRigidBody().getAngularVelocity());
////            PhysicsUtility.printRigidBody(entityProperties.getRigidBody());
//        }

//        if (entityProperties.getNode().getName().startsWith("John")) {
//            System.out.println("John setWorldTransform translate: " + PhysicsUtility.convertVector4(pos));
//        }

//        ENG_Vector4D othEntityAxis = new ENG_Vector4D();
//        float othEntityAngle = entityProperties.getRotate().toAngleAxisDeg(othEntityAxis);
//
//        if (entityProperties.getNode().getName().startsWith("John") && Math.abs(othEntityAngle) > ENG_Math.FLOAT_EPSILON && othEntityAngle != prevAngle) {
//            prevAngle = othEntityAngle;
//            System.out.println("John EntityMotionState axis: " + othEntityAxis + " angle: " + othEntityAngle);
//        }
    }

    public ENG_SceneNode getSceneNode() {
        return sceneNode;
    }

    public Vector3 getPos() {
        return pos;
    }

    public Quaternion getQuaternion() {
        return quaternion;
    }

//    public btGhostObject getGhostObject() {
//        return ghostObject;
//    }
//
//    public void setGhostObject(btGhostObject ghostObject) {
//        this.ghostObject = ghostObject;
//    }


    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public ShipProperties getShipProperties() {
        return shipProperties;
    }

    public ProjectileProperties getProjectileProperties() {
        return projectileProperties;
    }

    public TrackerProperties getTrackerProperties() {
        return trackerProperties;
    }

    public void getPreviousPosition(ENG_Vector3D prevPos) {
        prevPos.set(this.prevPos);
    }

    public void getPreviousOrientation(ENG_Quaternion prevOrientation) {
        prevOrientation.set(this.prevOrientation);
    }
}
