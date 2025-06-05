/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/21, 10:04 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

/**
 * Created by sebas on 02-Oct-17.
 */

public class InvisibleWall {

    private InvisibleWallsManager.WallType wallType;
    private Matrix4 transform;
    private btDefaultMotionState motionState;
    private btBoxShape boxShape;
    private btRigidBody rigidBody;
    private short collisionGroup;
    private short collisionMask;

    public InvisibleWall() {

    }

    public InvisibleWall(InvisibleWallsManager.WallType wallType, Matrix4 transform, btDefaultMotionState motionState,
                         btBoxShape boxShape, btRigidBody rigidBody, short collisionGroup, short collisionMask) {
        this.wallType = wallType;
        this.transform = transform;
        this.motionState = motionState;
        this.boxShape = boxShape;
        this.rigidBody = rigidBody;
        this.collisionGroup = collisionGroup;
        this.collisionMask = collisionMask;
    }

    public InvisibleWallsManager.WallType getWallType() {
        return wallType;
    }

    public Matrix4 getTransform() {
        return transform;
    }

    public btDefaultMotionState getMotionState() {
        return motionState;
    }

    public btBoxShape getBoxShape() {
        return boxShape;
    }

    public btRigidBody getRigidBody() {
        return rigidBody;
    }

    public short getCollisionGroup() {
        return collisionGroup;
    }

    public short getCollisionMask() {
        return collisionMask;
    }
}
