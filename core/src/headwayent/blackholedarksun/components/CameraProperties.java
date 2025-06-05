/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

import com.artemis.Component;

public class CameraProperties extends Component {

    public enum CameraType {
        FIRST_PERSON, THIRD_PERSON
    }

    private CameraType type;
    private ENG_SceneNode node;
    private ENG_Camera camera;
    private ENG_Vector4D unadjustedCameraPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D collisionClosestPoint = new ENG_Vector4D(true);
    private float collisionDistance = Float.MAX_VALUE;
    private float closestTargetDistance = Float.MAX_VALUE;
    private boolean animatedCamera; // When true it means the MovementSystem does not update the camera position and orientation.

    public CameraProperties() {

    }

    public CameraProperties(ENG_Camera camera, ENG_SceneNode node) {
        this.camera = camera;
        this.node = node;
    }

    public void setNode(ENG_SceneNode node) {
        this.node = node;
    }

    public void setCamera(ENG_Camera camera) {
        this.camera = camera;
    }

    public CameraType getType() {
        return type;
    }

    public void setType(CameraType type) {
        this.type = type;
    }

    public ENG_SceneNode getNode() {
        return node;
    }

    public ENG_Camera getCamera() {
        return camera;
    }

    public boolean isAnimatedCamera() {
        return animatedCamera;
    }

    public void setAnimatedCamera(boolean animatedCamera) {
        this.animatedCamera = animatedCamera;
    }

    public ENG_Vector4D getUnadjustedCameraPositionOrig() {
        return unadjustedCameraPosition;
    }

    public ENG_Vector4D getUnadjustedCameraPosition() {
        return new ENG_Vector4D(unadjustedCameraPosition);
    }

    public void setUnadjustedCameraPosition(ENG_Vector4D unadjustedCameraPosition) {
        this.unadjustedCameraPosition.set(unadjustedCameraPosition);
    }

    public ENG_Vector4D getCollisionClosestPointOrig() {
        return collisionClosestPoint;
    }

    public ENG_Vector4D getCollisionClosestPoint() {
        return new ENG_Vector4D(collisionClosestPoint);
    }

    public void setCollisionClosestPoint(ENG_Vector4D collisionClosestPoint) {
        this.collisionClosestPoint.set(collisionClosestPoint);
    }

    public float getCollisionDistance() {
        return collisionDistance;
    }

    public void setCollisionDistance(float collisionDistance) {
        this.collisionDistance = collisionDistance;
    }

    public float getClosestTargetDistance() {
        return closestTargetDistance;
    }

    public void setClosestTargetDistance(float closestTargetDistance) {
        this.closestTargetDistance = closestTargetDistance;
    }

    public void resetClosestTargetDistance() {
        closestTargetDistance = Float.MAX_VALUE;
    }

    public void resetCollision() {
        collisionClosestPoint.set(ENG_Math.PT4_ZERO);
        collisionDistance = Float.MAX_VALUE;
    }
}
