/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/31/21, 9:59 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.physics;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestNotMeRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.RayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeX;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShapeZ;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;

import java.util.ArrayList;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.StaticEntityProperties;
import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Matrix3;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;

/**
 * Created by sebas on 11-Sep-17.
 */

public class PhysicsUtility {

    private static final boolean DEBUG = true;

    public static ClosestRayResultCallback createRayTest(ENG_Vector4D rayFrom, ENG_Vector4D rayTo,
                                                         PhysicsProperties.CollisionGroup collisionFilterGroup,
                                                         PhysicsProperties.CollisionMask collisionMask) {
        ClosestRayResultCallback callback = new ClosestRayResultCallback(toVector3(rayFrom), toVector3(rayTo));
        callback.setCollisionFilterGroup(collisionFilterGroup.getVal());
        callback.setCollisionFilterMask(collisionMask.getVal());
        return callback;
    }

    public static void createRayTest(ClosestRayResultCallback callback, ENG_Vector4D rayFrom, ENG_Vector4D rayTo,
                                     PhysicsProperties.CollisionGroup collisionFilterGroup,
                                     PhysicsProperties.CollisionMask collisionMask) {
        createRayTest(callback, toVector3(rayFrom), toVector3(rayTo), collisionFilterGroup.getVal(), collisionMask.getVal());
    }

    public static void createRayTest(ClosestRayResultCallback callback, Vector3 rayFrom, Vector3 rayTo,
                                     int collisionFilterGroup, int collisionMask) {
        callback.setCollisionObject(null);
        callback.setClosestHitFraction(1.0f);
        callback.setCollisionFilterGroup(collisionFilterGroup);
        callback.setCollisionFilterMask(collisionMask);
        callback.setRayFromWorld(rayFrom);
        callback.setRayToWorld(rayTo);
    }

    public static ClosestNotMeRayResultCallback createRayTest(btCollisionObject collisionObject,
                                                              ENG_Vector4D rayFrom, ENG_Vector4D rayTo,
                                                              PhysicsProperties.CollisionGroup collisionFilterGroup,
                                                              PhysicsProperties.CollisionMask collisionMask) {
        return createRayTest(collisionObject, toVector3(rayFrom), toVector3(rayTo),
                collisionFilterGroup.getVal(), collisionMask.getVal());
    }

    public static ClosestNotMeRayResultCallback createRayTest(btCollisionObject collisionObject,
                                                              Vector3 rayFrom, Vector3 rayTo,
                                                              int collisionFilterGroup, int collisionMask) {
        ClosestNotMeRayResultCallback callback = new ClosestNotMeRayResultCallback(collisionObject);
        createRayTest(callback, rayFrom, rayTo, collisionFilterGroup, collisionMask);
        return callback;
    }

    public static void rayTest(btDynamicsWorld world, ENG_Vector4D rayFrom, ENG_Vector4D rayTo, RayResultCallback callback) {
        rayTest(world, toVector3(rayFrom), toVector3(rayTo), callback);
    }

    public static void rayTest(btDynamicsWorld world, Vector3 rayFrom, Vector3 rayTo, RayResultCallback callback) {
        world.rayTest(rayFrom, rayTo, callback);
    }

    public static Vector3 getHitPosition(ClosestRayResultCallback rayResultCallback) {
        if (DEBUG && !rayResultCallback.hasHit()) {
            throw new IllegalArgumentException("check that rayResultCallback has actually hit something!");
        }
        Vector3 from = new Vector3();
        Vector3 to = new Vector3();
        rayResultCallback.getRayFromWorld(from);
        rayResultCallback.getRayToWorld(from);
        Vector3 hitPosition = from.lerp(to, rayResultCallback.getClosestHitFraction());
        return hitPosition;
    }

    public static ArrayList<btBvhTriangleMeshShape> createBvhTriangleMeshShape(String file) {
        // We need to convert the vertex and index data from OgreMesh2 to libGDX Model.
//        ModelBuilder modelBuilder = new ModelBuilder();
//        modelBuilder.begin();
//        MeshBuilder meshBuilder = new MeshBuilder();
//        modelBuilder.part("part1", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, new Material());
////        modelBuilder.part("part2", null, VertexAttributes.Usage.Position, new Material());
//        Model model = modelBuilder.end();//new Model();
//        btCollisionShape btCollisionShape = Bullet.obtainStaticNodeShape(model.nodes);
//
//        Vector3 vertex1 = new Vector3(0.0f, 0.0f, 0.0f);
//        Vector3 vertex2 = new Vector3(10.0f, 0.0f, 0.0f);
//        Vector3 vertex3 = new Vector3(10.0f, 0.0f, 10.0f);


//        triangleMesh.addTriangle(vertex1, vertex2, vertex3);

        ArrayList<btBvhTriangleMeshShape> meshes = new ArrayList<>();

        ModelData modelData = new G3dModelLoader(new JsonReader()).parseModel(Gdx.files.local(file));
        for (ModelMesh mesh : modelData.meshes) {
            Vector3 scale = null;
            Quaternion rotation = null;
            String partId = mesh.parts[0].id;
            for (ModelNode node : modelData.nodes) {
                boolean partFound = false;
                if (node.parts == null) continue;
                for (ModelNodePart part : node.parts) {
                    if (partId.equals(part.meshPartId)) {
                        partFound = true;
                        break;
                    }
                }
                if (partFound) {
                    scale = node.scale;
                    rotation = node.rotation;
                    break;
                }
            }

            int startOffset = 0;
            int endOffset = 0;
            boolean positionReached = false;
            VertexAttribute[] attributes = mesh.attributes;
            for (int i = 0; i < attributes.length; i++) {
                VertexAttribute attribute = attributes[i];
                switch (attribute.usage) {
                    case VertexAttributes.Usage.Position:
                        positionReached = true;
                        break;
                    case VertexAttributes.Usage.Normal:
                    case VertexAttributes.Usage.Tangent:
                    case VertexAttributes.Usage.BiNormal: {
                        if (positionReached) {
                            endOffset += 3;
                        } else {
                            startOffset += 3;
                        }
                    }
                        break;
                    case VertexAttributes.Usage.TextureCoordinates: {
                        if (positionReached) {
                            endOffset += 2;
                        } else {
                            startOffset +=2;
                        }
                    }
                        break;
                    case VertexAttributes.Usage.ColorUnpacked:
                    case VertexAttributes.Usage.ColorPacked:
                    case VertexAttributes.Usage.Generic:
                    case VertexAttributes.Usage.BoneWeight:
                    default:
                        throw new IllegalStateException("Unexpected value: " + attribute.type);
                }
            }

            btTriangleMesh triangleMesh = new btTriangleMesh();
            float[] vertices = mesh.vertices;
            Vector3 vertex = new Vector3();
            for (int i = 0, verticesLength = vertices.length; i < verticesLength; i += 3) {
                i += startOffset;
                float vertex0 = vertices[i];
                float vertex1 = vertices[i + 1];
                float vertex2 = vertices[i + 2];
                vertex.set(vertex0, vertex1, vertex2);
                if (scale != null) {
                    vertex.scl(scale);
                }
                if (rotation != null) {
                    vertex.mul(rotation);
                }
                triangleMesh.findOrAddVertex(vertex, false);
                i += endOffset;
            }

            for (ModelMeshPart part : mesh.parts) {
                short[] indices = part.indices;
                if (indices.length % 3 != 0) {
                    throw new IllegalStateException(indices.length + " not a multiple of 3!");
                }
                for (int i = 0, indicesLength = indices.length; i < indicesLength; i += 3) {
                    short index0 = indices[i];
                    short index1 = indices[i + 1];
                    short index2 = indices[i + 2];
                    triangleMesh.addTriangleIndices(index0, index1, index2);
                }

            }
            btBvhTriangleMeshShape meshShape = new btBvhTriangleMeshShape(triangleMesh, true);
            // DO NOT DISPOSE THE TRIANGLE MESH. THIS IS NOT COPIED INSIDE THE btBvhTriangleMeshShape and will be used as ptr.
//            triangleMesh.dispose();
            meshes.add(meshShape);
        }


//        btBvhTriangleMeshShape meshShape = btBvhTriangleMeshShape.obtain(model1.meshParts);

        // Dispose of everything!!!
//        model.dispose();
//        model1.dispose();
//        btCollisionShape.dispose();
        return meshes;
    }

    public static void destroyBvhTriangleMeshShape(ArrayList<btBvhTriangleMeshShape> meshShapes) {
        for (btBvhTriangleMeshShape meshShape : meshShapes) {
            meshShape.dispose();
        }

    }

    public static btGhostObject createGhostObject() {
        return new btGhostObject();
    }

    public static EntityRigidBody createEntityRigidBody(btRigidBody.btRigidBodyConstructionInfo constructionInfo, Entity gameEntity) {
        return new EntityRigidBody(constructionInfo, gameEntity);
    }

    public static DebrisEntityRigidBody createDebrisRigidBody(btRigidBody.btRigidBodyConstructionInfo constructionInfo, Entity gameEntity) {
        return new DebrisEntityRigidBody(constructionInfo, gameEntity);
    }

    public static StaticEntityRigidBody createStaticEntityRigidBody(btRigidBody.btRigidBodyConstructionInfo constructionInfo, Entity gameEntity) {
        return new StaticEntityRigidBody(constructionInfo, gameEntity);
    }

    public static btCollisionShape createBoxCollisionShape(ENG_Vector3D halfExtents) {
        return new btBoxShape(new Vector3(halfExtents.x, halfExtents.y, halfExtents.z));
    }

    public static ENG_AxisAlignedBox getAxisAlignedBox(btRigidBody rigidBody) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getAxisAlignedBox(rigidBody, aabb);
        return aabb;
    }

    public static void getAxisAlignedBox(btRigidBody rigidBody, ENG_AxisAlignedBox aabb) {
        Vector3 minAabb = new Vector3();
        Vector3 maxAabb = new Vector3();
        rigidBody.getAabb(minAabb, maxAabb);
        // TODO test if getting the aabb is correct and faster than the commented out part using the particular collision shapes.
        aabb.setExtents(toVector4DAsPt(minAabb), toVector4DAsPt(maxAabb));
    }

    public static btCollisionShape createCapsuleCollisionShape(float radius, float height) {
        return new btCapsuleShape(radius, height);
    }

    public static btCollisionShape createCapsuleCollisionShapeX(float radius, float width) {
        return new btCapsuleShapeX(radius, width);
    }

    public static btCollisionShape createCapsuleCollisionShapeZ(float radius, float depth) {
        return new btCapsuleShapeZ(radius, depth);
    }

    public static ENG_AxisAlignedBox getBoxCollisionShape(btBoxShape shape) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getBoxCollisionShape(shape, aabb);
        return aabb;
    }

    public static ENG_AxisAlignedBox getBoxCollisionShape(btBoxShape shape, ENG_Vector4D centre) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getBoxCollisionShape(shape, centre, aabb);
        return aabb;
    }

    public static void getBoxCollisionShape(btBoxShape shape, ENG_Vector4D centre, ENG_AxisAlignedBox aabb) {
        Vector3 halfExtents = shape.getHalfExtentsWithoutMargin();
        ENG_Vector4D max = centre.addAsPt(toVector4DAsPt(halfExtents));
        ENG_Vector4D min = centre.subAsPt(toVector4DAsPt(halfExtents));
        aabb.setExtents(min, max);
    }

    public static void getBoxCollisionShape(btBoxShape shape, ENG_AxisAlignedBox aabb) {
        Vector3 halfExtents = shape.getHalfExtentsWithoutMargin();
        aabb.setExtents(toVector3D(halfExtents).invert(), toVector3D(halfExtents));
    }

    public static ENG_AxisAlignedBox getCapsuleCollisionShape(btCapsuleShape shape) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getCapsuleCollisionShape(shape, aabb);
        return aabb;
    }

    public static ENG_AxisAlignedBox getCapsuleCollisionShape(btCapsuleShapeX shape) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getCapsuleCollisionShape(shape, aabb);
        return aabb;
    }

    public static ENG_AxisAlignedBox getCapsuleCollisionShape(btCapsuleShapeZ shape) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getCapsuleCollisionShape(shape, aabb);
        return aabb;
    }

    public static ENG_AxisAlignedBox getCapsuleCollisionShape(btCapsuleShape shape, ENG_Vector4D centre) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getCapsuleCollisionShape(shape, centre, aabb);
        return aabb;
    }

    public static ENG_AxisAlignedBox getCapsuleCollisionShape(btCapsuleShapeX shape, ENG_Vector4D centre) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getCapsuleCollisionShape(shape, centre, aabb);
        return aabb;
    }

    public static ENG_AxisAlignedBox getCapsuleCollisionShape(btCapsuleShapeZ shape, ENG_Vector4D centre) {
        ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
        getCapsuleCollisionShape(shape, centre, aabb);
        return aabb;
    }

    public static void getCapsuleCollisionShape(btCapsuleShape shape, ENG_Vector4D centre, ENG_AxisAlignedBox aabb) {
        float radius = shape.getRadius();
        float halfHeight = shape.getHalfHeight();
        // The total height is height+2*radius, so the height is just the height between the center of each 'sphere' of the capsule caps.
        ENG_Vector4D half = new ENG_Vector4D(radius, halfHeight + 2.0f * radius, radius, 1.0f);
        ENG_Vector4D max = centre.addAsPt(half);
        ENG_Vector4D min = centre.subAsPt(half);
        aabb.setExtents(min, max);
    }

    public static void getCapsuleCollisionShape(btCapsuleShape shape, ENG_AxisAlignedBox aabb) {
        float radius = shape.getRadius();
        float halfHeight = shape.getHalfHeight();
        // The total height is height+2*radius, so the height is just the height between the center of each 'sphere' of the capsule caps.
        ENG_Vector3D max = new ENG_Vector3D(radius, halfHeight + 2.0f * radius, radius);
        aabb.setExtents(max.invert(), max);
    }

    public static void getCapsuleCollisionShape(btCapsuleShapeX shape, ENG_Vector4D centre, ENG_AxisAlignedBox aabb) {
        float radius = shape.getRadius();
        float halfHeight = shape.getHalfHeight();
        // The total height is height+2*radius, so the height is just the height between the center of each 'sphere' of the capsule caps.
        ENG_Vector4D half = new ENG_Vector4D(halfHeight + 2.0f * radius, radius, radius, 1.0f);
        ENG_Vector4D max = centre.addAsPt(half);
        ENG_Vector4D min = centre.subAsPt(half);
        aabb.setExtents(min, max);
    }

    public static void getCapsuleCollisionShape(btCapsuleShapeX shape, ENG_AxisAlignedBox aabb) {
        float radius = shape.getRadius();
        float halfHeight = shape.getHalfHeight();
        // The total height is height+2*radius, so the height is just the height between the center of each 'sphere' of the capsule caps.
        ENG_Vector3D max = new ENG_Vector3D(halfHeight + 2.0f * radius, radius, radius);
        aabb.setExtents(max.invert(), max);
    }

    public static void getCapsuleCollisionShape(btCapsuleShapeZ shape, ENG_Vector4D centre, ENG_AxisAlignedBox aabb) {
        float radius = shape.getRadius();
        float halfHeight = shape.getHalfHeight();
        // The total height is height+2*radius, so the height is just the height between the center of each 'sphere' of the capsule caps.
        ENG_Vector4D half = new ENG_Vector4D(radius, radius, halfHeight + 2.0f * radius, 1.0f);
        ENG_Vector4D max = centre.addAsPt(half);
        ENG_Vector4D min = centre.subAsPt(half);
        aabb.setExtents(min, max);
    }

    public static void getCapsuleCollisionShape(btCapsuleShapeZ shape, ENG_AxisAlignedBox aabb) {
        float radius = shape.getRadius();
        float halfHeight = shape.getHalfHeight();
        // The total height is height+2*radius, so the height is just the height between the center of each 'sphere' of the capsule caps.
        ENG_Vector3D max = new ENG_Vector3D(radius, radius, halfHeight + 2.0f * radius);
        aabb.setExtents(max.invert(), max);
    }

    public static float getFriction(btRigidBody rigidBody) {
        return rigidBody.getFriction();
    }

    public static void setFriction(btRigidBody rigidBody, float friction) {
        rigidBody.setFriction(friction);
    }

    public static void setLinearVelocity(btRigidBody rigidBody, ENG_Vector3D velocity) {
        rigidBody.setLinearVelocity(new Vector3(velocity.x, velocity.y, velocity.z));
    }

    public static void setAngularVelocity(btRigidBody rigidBody, Vector3 velocity) {
        rigidBody.setAngularVelocity(velocity);
    }

    public static void setAngularVelocity(btRigidBody rigidBody, ENG_Vector3D velocity) {
        rigidBody.setAngularVelocity(new Vector3(velocity.x, velocity.y, velocity.z));
    }

    public static void setLinearFactor(btRigidBody rigidBody, ENG_Vector3D velocity) {
        rigidBody.setLinearFactor(new Vector3(velocity.x, velocity.y, velocity.z));
    }

    public static void setAngularFactor(btRigidBody rigidBody, ENG_Vector3D velocity) {
        rigidBody.setAngularFactor(new Vector3(velocity.x, velocity.y, velocity.z));
    }

    public static ENG_Vector3D getAngularVelocity(btRigidBody rigidBody) {
        Vector3 angularVelocity = rigidBody.getAngularVelocity();
        return new ENG_Vector3D(angularVelocity.x, angularVelocity.y, angularVelocity.z);
    }

    public static ENG_Vector3D getLinearVelocity(btRigidBody rigidBody) {
        Vector3 linearVelocity = rigidBody.getLinearVelocity();
        return new ENG_Vector3D(linearVelocity.x, linearVelocity.y, linearVelocity.z);
    }

    public static void stepSimulation(btDynamicsWorld dynamicWorld, float timeStep) {
        dynamicWorld.stepSimulation(timeStep);
    }

    public static void stepSimulation(btDynamicsWorld dynamicWorld, float timeStep, int maxSteps) {
        dynamicWorld.stepSimulation(timeStep, maxSteps);
    }

    public static void stepSimulation(btDynamicsWorld dynamicWorld, float timeStep, int maxSteps, float fixedStep) {
        dynamicWorld.stepSimulation(timeStep, maxSteps, fixedStep);
    }

    public static void addRigidBody(btDynamicsWorld dynamicWorld, btRigidBody body, short collisionGroup, short collisionMask) {
        dynamicWorld.addRigidBody(body, collisionGroup, collisionMask);
    }

    public static void addGhostObject(btDynamicsWorld dynamicWorld, btGhostObject ghostObject, short collisionGroup, short collisionMask) {
        dynamicWorld.addCollisionObject(ghostObject, collisionGroup, collisionMask);
    }

    public static void addRigidBody(btDynamicsWorld dynamicWorld, btRigidBody body) {
        dynamicWorld.addRigidBody(body);
    }

    public static void removeRigidBody(btDynamicsWorld dynamicWorld, btRigidBody body) {
        dynamicWorld.removeRigidBody(body);
    }

    public static btRigidBody.btRigidBodyConstructionInfo createConstructionInfo(
            float mass, btMotionState motionState, btCollisionShape shape) {
        ENG_Vector3D fallInertia = calculateLocalInertia(shape, mass);
        Vector3 vec = new Vector3(fallInertia.x, fallInertia.y, fallInertia.z);
        return new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, vec);

    }

    public static btRigidBody.btRigidBodyConstructionInfo createConstructionInfo(
            float mass, btMotionState motionState, btCollisionShape shape, ENG_Vector3D fallInertia) {
        if (fallInertia == null && mass > 0.0f) {
            fallInertia = calculateLocalInertia(shape, mass);
        }
        Vector3 vec = mass > 0.0f ? new Vector3(fallInertia.x, fallInertia.y, fallInertia.z) : new Vector3();
        return new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, vec);

    }

    public static ENG_Vector3D calculateLocalInertia(btCollisionShape collisionShape, float mass) {
        Vector3 vec = new Vector3();
        collisionShape.calculateLocalInertia(mass, vec);
        return new ENG_Vector3D(vec.x, vec.y, vec.z);
    }

    public static void calculateLocalInertia(btCollisionShape collisionShape, float mass, ENG_Vector3D ret) {
        Vector3 vec = new Vector3();
        collisionShape.calculateLocalInertia(mass, vec);
        ret.set(vec.x, vec.y, vec.z);
    }

    public static void printRigidBody(btRigidBody rigidBody) {
        Matrix3 invInertiaTensorWorld = new Matrix3();
        Vector3 linearVelocity = new Vector3();
        Vector3 angularVelocity = new Vector3();
        ENG_Float invMass = new ENG_Float();
        Vector3 angularFactor = new Vector3();
        Vector3 linearFactor = new Vector3();
        Vector3 gravity = new Vector3();
        Vector3 invInertiaLocal = new Vector3();
        Vector3 totalForce = new Vector3();
        Vector3 totalTorque = new Vector3();
        ENG_Float linearDamping = new ENG_Float();
        ENG_Float angularDamping = new ENG_Float();
        ENG_Float linearSleepingThreshold = new ENG_Float();
        ENG_Float angularSleepingThreshold = new ENG_Float();
        serializeRigidBody(rigidBody, invInertiaTensorWorld, linearVelocity, angularVelocity,
                invMass, angularFactor, linearFactor, gravity, invInertiaLocal, totalForce,
                totalTorque, linearDamping, angularDamping, linearSleepingThreshold,
                angularSleepingThreshold);

        System.out.println("invInertiaTensorWorld: " + invInertiaTensorWorld +
                "\nlinearVelocity: " + linearVelocity +
                "\nangularVelocity: " + angularVelocity +
                "\ninvMass: " + invMass +
                "\nangularFactor: " + angularFactor +
                "\nlinearFactor: " + linearFactor +
                "\ngravity: " + gravity +
                "\ninvInertiaLocal: " + invInertiaLocal +
                "\ntotalForce: " + totalForce +
                "\ntotalTorque: " + totalTorque +
                "\nlinearDamping: " + linearDamping +
                "\nangularDamping: " + angularDamping +
                "\nlinearSleepingThreshold: " + linearSleepingThreshold +
                "\nangularSleepingThreshold: " + angularSleepingThreshold);
        if (!totalForce.isZero()) {
            System.out.println("totalForce not zero");
        }
        if (!totalTorque.isZero()) {
            System.out.println("totalTorque not zero");
        }
    }

    public static void serializeRigidBody(btRigidBody rigidBody,
                                          Matrix3 invInertiaTensorWorld,
                                          Vector3 linearVelocity,
                                          Vector3 angularVelocity,
                                          ENG_Float invMass,
                                          Vector3 angularFactor,
                                          Vector3 linearFactor,
                                          Vector3 gravity,
                                          Vector3 invInertiaLocal,
                                          Vector3 totalForce,
                                          Vector3 totalTorque,
                                          ENG_Float linearDamping,
                                          ENG_Float angularDamping,
                                          ENG_Float linearSleepingThreshold,
                                          ENG_Float angularSleepingThreshold) {
        invInertiaTensorWorld.set(rigidBody.getInvInertiaTensorWorld());
        linearVelocity.set(rigidBody.getLinearVelocity());
        angularVelocity.set(rigidBody.getAngularVelocity());
        invMass.setValue(rigidBody.getInvMass());
        angularFactor.set(rigidBody.getAngularFactor());
        linearFactor.set(rigidBody.getLinearFactor());
        gravity.set(rigidBody.getGravity());
        invInertiaLocal.set(rigidBody.getInvInertiaDiagLocal());
        totalForce.set(rigidBody.getTotalForce());
        totalTorque.set(rigidBody.getTotalTorque());
        linearDamping.setValue(rigidBody.getLinearDamping());
        angularDamping.setValue(rigidBody.getAngularDamping());
        linearSleepingThreshold.setValue(rigidBody.getLinearSleepingThreshold());
        angularSleepingThreshold.setValue(rigidBody.getAngularSleepingThreshold());
    }

    public static void deserializeRigidBody(btRigidBody rigidBody,
                                            Matrix3 invInertiaTensorWorld,
                                            Vector3 linearVelocity,
                                            Vector3 angularVelocity,
                                            ENG_Float mass,
                                            Vector3 angularFactor,
                                            Vector3 linearFactor,
                                            Vector3 gravity,
                                            Vector3 inertiaLocal,
                                            Vector3 totalForce,
                                            Vector3 totalTorque,
                                            ENG_Float linearDamping,
                                            ENG_Float angularDamping,
                                            ENG_Float linearSleepingThreshold,
                                            ENG_Float angularSleepingThreshold) {
        rigidBody.setLinearVelocity(linearVelocity);
        rigidBody.setAngularVelocity(angularVelocity);
        rigidBody.setMassProps(mass.getValue(), inertiaLocal);
        rigidBody.setLinearFactor(linearFactor);
        rigidBody.setAngularFactor(angularFactor);
        rigidBody.setGravity(gravity);
        rigidBody.setDamping(linearDamping.getValue(), angularDamping.getValue());
        rigidBody.setSleepingThresholds(linearSleepingThreshold.getValue(), angularSleepingThreshold.getValue());
    }

    public static Vector3 invertVector3Safe(Vector3 v) {
        Vector3 ret = new Vector3();
        invertVector3Safe(v, ret);
        return ret;
    }

    /**
     * Does 1.0f / v checking if v isn't 0.
     * @param v
     */
    public static void invertVector3Safe(Vector3 v, Vector3 ret) {
        ret.x = v.x != 0.0f ? (1.0f / v.x) : 0.0f;
        ret.y = v.y != 0.0f ? (1.0f / v.y) : 0.0f;
        ret.z = v.z != 0.0f ? (1.0f / v.z) : 0.0f;
    }

    public static void convertVector(Vector2 v, ENG_Vector2D ret) {
        ret.set(v.x, v.y);
    }

    public static ENG_Vector2D convertVector(Vector2 v) {
        ENG_Vector2D ret = new ENG_Vector2D();
        convertVector(v, ret);
        return ret;
    }

    public static void convertVector(Vector3 v, ENG_Vector3D ret) {
        ret.set(v.x, v.y, v.z);
    }

    public static ENG_Vector3D convertVector3(Vector3 v) {
        ENG_Vector3D ret = new ENG_Vector3D();
        convertVector(v, ret);
        return ret;
    }

    public static void convertVector(Vector3 v, ENG_Vector4D ret) {
        ret.set(v.x, v.y, v.z);
    }

    public static ENG_Vector4D convertVector4(Vector3 v) {
        ENG_Vector4D ret = new ENG_Vector4D();
        convertVector(v, ret);
        return ret;
    }

    public static void convertVector(ENG_Vector2D v, Vector2 ret) {
        ret.set(v.x, v.y);
    }

    public static Vector2 convertVector(ENG_Vector2D v) {
        Vector2 ret = new Vector2();
        convertVector(v, ret);
        return ret;
    }

    public static void convertVector(ENG_Vector3D v, Vector3 ret) {
        ret.set(v.x, v.y, v.z);
    }

    public static Vector3 convertVector(ENG_Vector3D v) {
        Vector3 ret = new Vector3();
        convertVector(v, ret);
        return ret;
    }

    public static void convertVector(ENG_Vector4D v, Vector3 ret) {
        ret.set(v.x, v.y, v.z);
    }

    public static Vector3 convertVector(ENG_Vector4D v) {
        Vector3 ret = new Vector3();
        convertVector(v, ret);
        return ret;
    }

    public static void convertMatrix(Matrix3 m, ENG_Matrix3 ret) {
        System.arraycopy(m.val, 0, ret.get(), 0, 9);
    }

    public static ENG_Matrix3 convertMatrix(Matrix3 m) {
        ENG_Matrix3 ret = new ENG_Matrix3();
        convertMatrix(m, ret);
        return ret;
    }

    public static void convertMatrix(Matrix4 m, ENG_Matrix4 ret) {
        System.arraycopy(m.val, 0, ret.get(), 0, 16);
    }

    public static ENG_Matrix4 convertMatrix(Matrix4 m) {
        ENG_Matrix4 ret = new ENG_Matrix4();
        convertMatrix(m, ret);
        return ret;
    }

    public static void convertMatrix(ENG_Matrix3 m, Matrix3 ret) {
        System.arraycopy(m.get(), 0, ret.val, 0, 9);
    }

    public static Matrix3 convertMatrix(ENG_Matrix3 m) {
        Matrix3 ret = new Matrix3();
        convertMatrix(m, ret);
        return ret;
    }

    public static void convertMatrix(ENG_Matrix4 m, Matrix4 ret) {
        System.arraycopy(m.get(), 0, ret.val, 0, 16);
    }

    public static Matrix4 convertMatrix(ENG_Matrix4 m) {
        Matrix4 ret = new Matrix4();
        convertMatrix(m, ret);
        return ret;
    }

    public static void getAabb(String meshName, String groupName, ENG_Vector3D centre, ENG_Vector3D halfSize) {
        getAabb(ENG_RenderRoot.getRenderRoot().getPointer(), meshName, groupName, centre, halfSize);
    }

    public static void setGravity(btRigidBody rigidBody, ENG_Vector3D gravity) {
        rigidBody.setGravity(new Vector3(gravity.x, gravity.y, gravity.z));
    }

    public static void disposePhysicsObject(EntityProperties entityProperties) {
//        System.out.println("Disposing physics object: " + entityProperties.getUniqueName());
        removeRigidBody(MainApp.getGame().getBtDiscreteDynamicsWorld(), entityProperties.getRigidBody());
        dispose(entityProperties.getRigidBody());
        dispose(entityProperties.getMotionState());
        dispose(entityProperties.getContructionInfo());
        dispose(entityProperties.getCollisionShape());
    }

    public static void disposePhysicsObject(StaticEntityProperties entityProperties) {
//        System.out.println("Disposing physics object: " + entityProperties.getUniqueName());
        removeRigidBody(MainApp.getGame().getBtDiscreteDynamicsWorld(), entityProperties.getRigidBody());
        dispose(entityProperties.getRigidBody());
        dispose(entityProperties.getMotionState());
        dispose(entityProperties.getContructionInfo());
        dispose(entityProperties.getCollisionShape());
    }

    public static void dispose(Disposable disposable) {
        disposable.dispose();
    }

    public static Vector3 toVector3(ENG_Vector3D in) {
        Vector3 out = new Vector3();
        toVector3(in, out);
        return out;
    }

    public static void toVector3(ENG_Vector3D in, Vector3 out) {
        out.set(in.x, in.y, in.z);
    }

    public static Vector3 toVector3(ENG_Vector4D in) {
        Vector3 out = new Vector3();
        toVector3(in, out);
        return out;
    }

    public static void toVector3(ENG_Vector4D in, Vector3 out) {
        out.set(in.x, in.y, in.z);
    }

    public static Vector2 toVector2(ENG_Vector2D in) {
        Vector2 out = new Vector2();
        toVector2(in, out);
        return out;
    }

    public static void toVector2(ENG_Vector2D in, Vector2 out) {
        out.set(in.x, in.y);
    }

    public static Quaternion toQuaternion(ENG_Quaternion in) {
        Quaternion out = new Quaternion();
        toQuaternion(in, out);
        return out;
    }

    public static void toQuaternion(ENG_Quaternion in, Quaternion out) {
        out.set(in.x, in.y, in.z, in.w);
    }

    public static ENG_Vector3D toVector3D(Vector3 in) {
        ENG_Vector3D out = new ENG_Vector3D();
        toVector3D(in, out);
        return out;
    }

    public static void toVector3D(Vector3 in, ENG_Vector3D out) {
        out.set(in.x, in.y, in.z);
    }

    public static void toVector4D(Vector3 in, ENG_Vector4D out) {
        out.set(in.x, in.y, in.z);
    }

    public static ENG_Vector4D toVector4DAsVec(Vector3 in) {
        ENG_Vector4D out = new ENG_Vector4D();
        toVector4DAsVec(in, out);
        return out;
    }

    public static void toVector4DAsVec(Vector3 in, ENG_Vector4D out) {
        out.set(in.x, in.y, in.z, 0.0f);
    }

    public static ENG_Vector4D toVector4DAsPt(Vector3 in) {
        ENG_Vector4D out = new ENG_Vector4D(true);
        toVector4DAsPt(in, out);
        return out;
    }

    public static void toVector4DAsPt(Vector3 in, ENG_Vector4D out) {
        out.set(in.x, in.y, in.z, 1.0f);
    }

    public static ENG_Vector2D toVector2D(Vector2 in) {
        ENG_Vector2D out = new ENG_Vector2D();
        toVector2D(in, out);
        return out;
    }

    public static void toVector2D(Vector2 in, ENG_Vector2D out) {
        out.set(in.x, in.y);
    }

    public static ENG_Quaternion toQuaternion(Quaternion in) {
        ENG_Quaternion out = new ENG_Quaternion();
        toQuaternion(in, out);
        return out;
    }

    public static void toQuaternion(Quaternion in, ENG_Quaternion out) {
        out.set(in.x, in.y, in.z, in.w);
    }

    public static native void getAabb(long root, String meshName, String groupName, ENG_Vector3D centre, ENG_Vector3D halfSize);

}
