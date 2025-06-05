/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/22/21, 9:36 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;

public class EntityData {

    public String inGameName;
    public String filename;
    public String name;
    public float maxSpeed;
    public float turnAngle;
    public int acceleration = 5; // percentage
    public float maxAngularVelocity;
    public float weight;
    public float linearDamping = 0.1f;
    public float angularDamping = 0.1f;
    public int health = 1;
    public final ENG_Vector3D localInertia = new ENG_Vector3D();
    public PhysicsProperties.CollisionShape collisionShape = PhysicsProperties.CollisionShape.BOX;
    public ENG_Vector4D collisionBoxCentre;
    public ENG_Vector4D collisionBoxHalfSize;
    public float collisionCapsuleRadius;
    public float collisionCapsuleHeight;
}