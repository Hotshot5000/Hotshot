/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/25/17, 7:35 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import static headwayent.blackholedarksun.physics.EntityContactListener.isEntityRigidBody;
import static headwayent.blackholedarksun.physics.EntityContactListener.isStaticEntityRigidBody;
import static headwayent.blackholedarksun.physics.EntityContactListener.isInvisibleWall;
import static headwayent.blackholedarksun.physics.EntityContactListener.isDebrisEntityRigidBody;
import static headwayent.blackholedarksun.physics.PhysicsUtility.toVector4D;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;

import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Created by sebas on 18-Sep-17.
 */

public class EntityInternalTickCallback extends InternalTickCallback {

    private final Vector3 ptA = new Vector3();
    private final Vector3 ptB = new Vector3();
    private final Vector3 normalOnB = new Vector3();
    private final ENG_Vector4D entityA = new ENG_Vector4D(true);
    private final ENG_Vector4D entityB = new ENG_Vector4D(true);
    private final ENG_Vector4D diff = new ENG_Vector4D();

    @Override
    public void onInternalTick(btDynamicsWorld dynamicsWorld, float timeStep) {
        super.onInternalTick(dynamicsWorld, timeStep);

        int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
        if (numManifolds == 0) return;

        for (int i = 0; i < numManifolds; i++) {
            btPersistentManifold contactManifold =  dynamicsWorld.getDispatcher().getManifoldByIndexInternal(i);
            btCollisionObject obA = contactManifold.getBody0();
            btCollisionObject obB = contactManifold.getBody1();
            long userPointerA = obA.getUserPointer();
            long userPointerB = obB.getUserPointer();
//            System.out.println("userPointerA: " + PhysicsEntityType.getType(userPointerA));
//            System.out.println("userPointerB: " + PhysicsEntityType.getType(userPointerB));

            int numContacts = contactManifold.getNumContacts();
            for (int j = 0; j < numContacts; j++) {
                btManifoldPoint pt = contactManifold.getContactPoint(j);
                if (pt.getDistance() < 0.0f) {
                    pt.getPositionWorldOnA(ptA);
                    pt.getPositionWorldOnB(ptB);
                    pt.getNormalWorldOnB(normalOnB);
                    toVector4D(ptA, entityA);
                    toVector4D(ptB, entityB);

                    // Get the collision direction vector and see if one is AI controlled in order to rotate away from the vector.
                    boolean obAResolved = false;
                    boolean obBResolved = false;
                    entityB.sub(entityA, diff);
                    diff.normalize();
                    if (isEntityRigidBody(userPointerA) && isStaticEntityRigidBody(userPointerB)) {
                        obAResolved = resolveCollision(obA, obB, diff);
                    }
                    entityA.sub(entityB, diff);
                    diff.normalize();
                    if (isEntityRigidBody(userPointerB) && isStaticEntityRigidBody(userPointerA)) {
                        obBResolved = resolveCollision(obB, obA, diff);
                    }

                    if (obAResolved) {

                    }
                    if (obBResolved) {

                    }
                }
            }
        }
    }

    private static boolean resolveCollision(btCollisionObject obA, btCollisionObject obB, ENG_Vector4D diff) {
        boolean resolved = false;
        EntityRigidBody eEntityRigidBody = (EntityRigidBody) obA;
        Entity e = eEntityRigidBody.getEntity();

        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        ComponentMapper<EntityProperties> entityPropertiesMapper = worldManager.getEntityPropertiesComponentMapper();
        ComponentMapper<ShipProperties> shipPropertiesMapper = worldManager.getShipPropertiesComponentMapper();
        ComponentMapper<ProjectileProperties> projectilePropertiesMapper = worldManager.getProjectilePropertiesComponentMapper();
        ComponentMapper<AIProperties> aIPropertiesMapper = worldManager.getAiPropertiesComponentMapper();

        EntityProperties entityProperties = entityPropertiesMapper.get(e);
        AIProperties aiComponent = aIPropertiesMapper.getSafe(e);
        if (aiComponent != null) {
            aiComponent.setCollisionEvasionDirectionFromStaticObject(diff);
            aiComponent.setCollidedWithStaticObject(true);
            aiComponent.setCollided(true);
            resolved = true;
        }
        return resolved;
    }
}
