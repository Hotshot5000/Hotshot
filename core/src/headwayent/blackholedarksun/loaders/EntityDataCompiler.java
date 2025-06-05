/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/12/21, 9:54 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import headwayent.blackholedarksun.EntityData;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.io.DataInputStream;

/**
 * Created by sebas on 22.10.2015.
 */
public class EntityDataCompiler {

    public static final String DATA = "data";
    public static final String FILENAME = "filename";
    public static final String NAME = "name";
    public static final String MAX_SPEED = "max_speed";
    public static final String TURN_ANGLE = "turn_angle";
    public static final String MAX_ANGULAR_VELOCITY = "max_angular_velocity";
    public static final String WEIGHT = "weight";
    public static final String LINEAR_DAMPING = "linear_damping";
    public static final String ANGULAR_DAMPING = "angular_damping";
    public static final String DAMPING = "damping";
    public static final String ACCELERATION = "acceleration";
    public static final String HEALTH = "health";
    public static final String LOCAL_INTERTIA = "local_inertia";
    public static final String COLLISION_SHAPE = "collision_shape";

    public static boolean parseEntityData(DataInputStream fp0, String s, EntityData entityData) {
        if (s.equalsIgnoreCase(FILENAME)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            ENG_AbstractCompiler.checkNull(s);
            entityData.filename = s;
        } else if (s.equalsIgnoreCase(NAME)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            ENG_AbstractCompiler.checkNull(s);
            entityData.name = s;
        } else if (s.equalsIgnoreCase(MAX_SPEED)) {
            entityData.maxSpeed = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equalsIgnoreCase(TURN_ANGLE)) {
            entityData.turnAngle = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equalsIgnoreCase(ACCELERATION)) {
            entityData.acceleration = ENG_AbstractCompiler.getInt(fp0);
        } else if (s.equalsIgnoreCase(MAX_ANGULAR_VELOCITY)) {
            entityData.maxAngularVelocity = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equals(WEIGHT)) {
            entityData.weight = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equals(LINEAR_DAMPING)) {
            entityData.linearDamping = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equals(ANGULAR_DAMPING)) {
            entityData.angularDamping = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equals(DAMPING)) {
            entityData.linearDamping = ENG_AbstractCompiler.getFloat(fp0);
            entityData.angularDamping = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equalsIgnoreCase(HEALTH)) {
            entityData.health = ENG_AbstractCompiler.getInt(fp0);
        } else if (s.equalsIgnoreCase(LOCAL_INTERTIA)) {
            entityData.localInertia.x = ENG_AbstractCompiler.getFloat(fp0);
            entityData.localInertia.y = ENG_AbstractCompiler.getFloat(fp0);
            entityData.localInertia.z = ENG_AbstractCompiler.getFloat(fp0);
        } else if (s.equalsIgnoreCase(COLLISION_SHAPE)) {
            s = ENG_CompilerUtil.getNextWord(fp0);
            ENG_AbstractCompiler.checkNull(s);
            entityData.collisionShape = PhysicsProperties.CollisionShape.getCollisionShape(s);
            if (entityData.collisionShape == PhysicsProperties.CollisionShape.BOX) {
                entityData.collisionBoxCentre = ENG_AbstractCompiler.getVector3D(fp0);
                entityData.collisionBoxHalfSize = ENG_AbstractCompiler.getVector3D(fp0);
            } else if (entityData.collisionShape == PhysicsProperties.CollisionShape.CAPSULE ||
                        entityData.collisionShape == PhysicsProperties.CollisionShape.CAPSULE_X ||
                        entityData.collisionShape == PhysicsProperties.CollisionShape.CAPSULE_Z) {
                entityData.collisionCapsuleRadius = ENG_AbstractCompiler.getFloat(fp0);
                entityData.collisionCapsuleHeight = ENG_AbstractCompiler.getFloat(fp0);
            } else {
                // TODO TBD
            }
        } else {
            return false;
        }
        return true;
    }
}
