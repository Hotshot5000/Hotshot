/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

import java.util.EnumMap;

import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Created by sebas on 02-Oct-17.
 */

public class InvisibleWallsManager {

    private static final float WALL_LENGTH = 20000.0f;
    private static final float WALL_THICKNESS = 100.0f;
    private static final float WALL_POSITION = 10000.0f;
    private static final InvisibleWallsManager mgr = new InvisibleWallsManager();

    private final EnumMap<WallType, InvisibleWall> walls = new EnumMap<>(WallType.class);
    private btDiscreteDynamicsWorld world;
    private boolean created;

    public enum WallType {
        FRONT, BACK, LEFT, RIGHT, UP, DOWN;

        public void getLimitReached(ENG_Vector4D limitReached) {
            switch (this.ordinal()) {
                case 0:
                    limitReached.z = -1.0f;
                    break;
                case 1:
                    limitReached.z = 1.0f;
                    break;
                case 2:
                    limitReached.x = -1.0f;
                    break;
                case 3:
                    limitReached.x =  1.0f;
                    break;
                case 4:
                    limitReached.y = 1.0f;
                    break;
                case 5:
                    limitReached.y = -1.0f;
                    break;
                default:
                    // Should never get here.
                    throw new IllegalArgumentException(this.toString() + this.ordinal());
            }
        }
    }

    private InvisibleWallsManager() {

    }

    public void createWalls() {
        if (!created) {
            walls.put(WallType.FRONT, createInvisibleBox(world, new Vector3(0.0f, 0.0f, -WALL_POSITION), new Vector3(WALL_LENGTH, WALL_LENGTH, WALL_THICKNESS), WallType.FRONT));
            walls.put(WallType.BACK, createInvisibleBox(world, new Vector3(0.0f, 0.0f, WALL_POSITION), new Vector3(WALL_LENGTH, WALL_LENGTH, WALL_THICKNESS), WallType.BACK));
            walls.put(WallType.LEFT, createInvisibleBox(world, new Vector3(-WALL_POSITION, 0.0f, 0.0f), new Vector3(WALL_THICKNESS, WALL_LENGTH, WALL_LENGTH), WallType.LEFT));
            walls.put(WallType.RIGHT, createInvisibleBox(world, new Vector3(WALL_POSITION, 0.0f, 0.0f), new Vector3(WALL_THICKNESS, WALL_LENGTH, WALL_LENGTH), WallType.RIGHT));
            walls.put(WallType.UP, createInvisibleBox(world, new Vector3(0.0f, WALL_POSITION, 0.0f), new Vector3(WALL_LENGTH, WALL_THICKNESS, WALL_LENGTH), WallType.UP));
            walls.put(WallType.DOWN, createInvisibleBox(world, new Vector3(0.0f, -WALL_POSITION, 0.0f), new Vector3(WALL_LENGTH, WALL_THICKNESS, WALL_LENGTH), WallType.DOWN));
            created = true;
        }
    }

    public void destroyWalls() {
        if (created) {
            for (InvisibleWall wall : walls.values()) {
                destroyInvisibleBox(world, wall);
            }
            walls.clear();
            created = false;
        }
    }

    private InvisibleWall createInvisibleBox(btDiscreteDynamicsWorld world, Vector3 pos, Vector3 scale, WallType wallType) {
        Matrix4 transform = new Matrix4();
        transform.idt();
        transform.setToTranslation(pos);

        btDefaultMotionState motionState = new btDefaultMotionState(transform);

        scale.scl(0.5f);
        btBoxShape boxShape = new btBoxShape(scale);

        Vector3 localInertia = new Vector3();
        boxShape.calculateLocalInertia(0.0f, localInertia);

        short collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
        short collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

        InvisibleWallRigidBody invisibleWallRigidBody = new InvisibleWallRigidBody(0.0f, motionState, boxShape, localInertia);
        invisibleWallRigidBody.setUserPointer(PhysicsEntityType.INVISIBLE_WALL.getType());

        InvisibleWall invisibleWall = new InvisibleWall(wallType, transform, motionState, boxShape, invisibleWallRigidBody,  collisionGroup, collisionMask);
        invisibleWallRigidBody.setInvisibleWall(invisibleWall);

        world.addRigidBody(invisibleWallRigidBody, collisionGroup, collisionMask);

        return invisibleWall;
    }

    private void destroyInvisibleBox(btDiscreteDynamicsWorld world, InvisibleWall wall) {
        world.removeRigidBody(wall.getRigidBody());
    }

    public btDiscreteDynamicsWorld getWorld() {
        return world;
    }

    public void setWorld(btDiscreteDynamicsWorld world) {
        this.world = world;
    }

    public static InvisibleWallsManager getSingleton() {
        return mgr;
    }
}
