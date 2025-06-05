/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/19/21, 4:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.Utility;
import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.TrackerProperties;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class MovementSystem extends IntervalEntityProcessingSystem {

    private static final float TRACKING_MISSILE_MINIMUM_DISTANCE_RAND = 50.0f;
    private static final float TRACKING_MISSILE_MAXIMUM_DISTANCE_RAND = 300.0f;
    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<CameraProperties> cameraPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private ComponentMapper<ProjectileProperties> projectilePropertiesMapper;
    private ComponentMapper<TrackerProperties> trackerPropertiesMapper;
    private ComponentMapper<AIProperties> aiPropertiesMapper;

    private static final int threadNum = ENG_Utility.getNumberOfCores();

//    private final ListeningScheduledExecutorService service = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(ENG_Utility.getNumberOfCores()));
    private final Runnable[] movementRunnable = new Runnable[threadNum];
    private final Thread[] movementThread = new Thread[threadNum];
    private final BlockingQueue<Entity> entityQueue = new LinkedBlockingQueue<>();
    private final Queue<EntityProperties> entitiesToDestroyQueue = new ConcurrentLinkedQueue<>();
    private final Queue<EntityProperties> entitiesToPlaySoundQueue = new ConcurrentLinkedQueue<>();
    private CountDownLatch movementEndLatch;
    private final ReentrantLock queueConsumerLock = new ReentrantLock();
    private final AtomicBoolean queueAddingEnded = new AtomicBoolean();
    private final ReentrantLock physicsUpdateLock = new ReentrantLock(); // Cannot call physics functions from multiple threads simultaneously.
    private boolean threadsCreated;
//    private long beginTime;

    private class MovementRunnable implements Runnable {

        private final ENG_Vector4D tempVelocity = new ENG_Vector4D();
        private final ENG_Quaternion tempOrientation = new ENG_Quaternion(true);
        private final ENG_Vector4D velocity = new ENG_Vector4D();
        private final ENG_Vector4D dir = new ENG_Vector4D();
        private final ENG_Vector4D finalPos = new ENG_Vector4D(true);
        private final ENG_Vector4D halfSize = new ENG_Vector4D();
        private final AtomicBoolean started = new AtomicBoolean();
//        private ThreadLocal<String> threadName = new ThreadLocal<String>() {
//            @Override
//            protected String initialValue() {
//                return Thread.currentThread().getName();
//            }
//        };

        public MovementRunnable() {

            reset();
        }


        public void stop() {
            started.set(false);
        }

        public void reset() {
            started.set(true);
        }

        @Override
        public void run() {

            try {
                while (started.get()) {
//                    System.out.println("looped " + Thread.currentThread().getName());
//                    Entity e = null;
//                    boolean unlocked = false;
//                    queueConsumerLock.lock();
////                    System.out.println("Locked " + Thread.currentThread().getName());
//                    try {
//                        e = entityQueue.poll();
//                        System.out.println("polled : " + (e != null) + " thread: " + Thread.currentThread().getName());
//                        if (e == null) {
//                            if (movementEndLatch != null && queueAddingEnded.get()) {
//                                System.out.println("countDown(): " + movementEndLatch.getCount());
//                                movementEndLatch.countDown();
//
//                            }
//                            queueConsumerLock.unlock();
////                            System.out.println("Unlocked take() " + Thread.currentThread().getName());
//                            unlocked = true;
//                            e = entityQueue.take();
//                            System.out.println("taken : " + (e != null) + " thread: " + Thread.currentThread().getName());
//                        }
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    } finally {
//                        if (!unlocked) {
//                            queueConsumerLock.unlock();
//                            System.out.println("Unlocked " + Thread.currentThread().getName());
//                        }
//                    }

                    Entity e = entityQueue.take();

//                    System.out.println("Entity null: " + (e == null) + " thread: " + Thread.currentThread().getName());
                    EntityProperties entityProperties = entityPropertiesMapper.get(e);
                    if (entityProperties.isUnmovable()) {
                        // Check if we are debris.
                        if (entityProperties.getRigidBodyType() == PhysicsProperties.RigidBodyType.DEBRIS) {
                            if (ENG_Utility.hasTimePassed(FrameInterval.DEBRIS_TRACKING_DELAY + entityProperties.getNode().getName(),
                                    entityProperties.getDebrisLifeBeginTime(),
                                    entityProperties.getDebrisLifeTime())) {
                                entitiesToDestroyQueue.offer(entityProperties);
                            }
                        }
                        movementEndLatch.countDown();
                        continue;
                    }
//                    System.out.println("Consuming " + entityProperties.getName());
//        if (entityProperties == null) {
//            throw new NullPointerException("Missing entity properties for this entity");
//        }
                    entityProperties.getVelocityAsVec(tempVelocity);
//                    System.out.println("tempVelocity: " + tempVelocity);
                    // tempVelocity.z = -tempVelocity.z;
                    ENG_SceneNode node = entityProperties.getNode();
                    node.getOrientation(tempOrientation);
//                    tempVelocity.mul(GameWorld.getWorld().getDelta());
                    tempOrientation.mul(tempVelocity, velocity);
                    // tempOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, dir);
                    // dir.mul(tempVelocity, velocity);

//                    entityProperties.move(velocity);
                    physicsUpdateLock.lock();
                    try {
                        // TODO when on server side we should check if the reported position by the client is possible given time diff.
                        // This is in order to avoid cheaters from teleporting across the map.
                        entityProperties.setLinearVelocity(velocity);
                        // node._update(true, true);
//                    node._getDerivedPosition(finalPos);

                        // Also set the position for multiplayer. This set position ignores the collision resolution that happens in CollisionDetectionSystem.
//                    entityProperties.setPositionWithoutCollisionResolution(finalPos);
                        entityProperties.setLinearVelocityForMP(velocity);

                        btRigidBody rigidBody = entityProperties.getRigidBody();
//                    Matrix4 centerOfMassTransform = rigidBody.getCenterOfMassTransform();
//                    Quaternion entityRotation = new Quaternion();
//                    Vector3 entityPos = new Vector3();
//                    Vector3 entityScale = new Vector3();
//                    centerOfMassTransform.getRotation(entityRotation);
//                    centerOfMassTransform.getTranslation(entityPos);
//                    centerOfMassTransform.getScale(entityScale);
//                    entityRotation.set(tempOrientation.x, tempOrientation.y, tempOrientation.z, tempOrientation.w);
//                    centerOfMassTransform.set(entityPos, entityRotation, entityScale);
//                    rigidBody.setCenterOfMassTransform(centerOfMassTransform);
                        rigidBody.setActivationState(Collision.ACTIVE_TAG);
//                    rigidBody.setDamping(rigidBody.getLinearDamping(), 0.8f);
//                    rigidBody.clearForces();
//                    btTransform btTransform = new btTransform();
//                    rigidBody.setWorldTransform();
                    } finally {
                        physicsUpdateLock.unlock();
                    }

//        if (e.getComponent(ProjectileProperties.class) != null) {
//            System.out.println("entity: " + entityProperties.getName() + " movement system pos: " + finalPos);
//        }

                    // checkEntityInLevelBounds(node, entityProperties);
                    // entityProperties.updateSectionList();

                    // We no longer can update the camera position here. Must do it after physics update.
                    CameraProperties cameraProperties = cameraPropertiesMapper.getSafe(e);
                    // If the ship is destroyed then the movement system no longer controls the camera.


                    ShipProperties shipProperties = shipPropertiesMapper.getSafe(e);
                    if (shipProperties != null) {
                        // Only ships the finalPos has been set don't fuck with it between
                        // the get and here
                        // WRONG everybody gets the sections or we don't have any collision
                        // detection!!!
                        updateAfterburner(e, entityProperties, shipProperties);

                    }

                    ProjectileProperties projectileProperties = projectilePropertiesMapper.getSafe(e);
                    // If in MP when the projectile is created it's updated here. But after the
                    // server takes over with the updates it doesn't make sense to also update them
                    // again here.
                    if (projectileProperties != null && !projectileProperties.isUpdateHandledByServer()) {
                        // Moved to EntityMotionState.
//                        projectileProperties.addToDistanceTraveled(velocity.length());
                        WeaponData weaponData = WeaponData.getWeaponData(projectileProperties.getType());
                        // If a projectile hits the level limits destroy it
                        // This is now handled directly in EntityContactListener.
//                        if (projectileProperties != null && entityProperties.isLimitsReached()) {
//                            entitiesToDestroyQueue.offer(entityProperties);
//                        }

                        if (projectileProperties.getDistanceTraveled() > weaponData.maxDistance) {
                            // entityProperties.setDestroyed(true);
                            TrackerProperties trackerProperties = trackerPropertiesMapper.getSafe(e);
                            if (trackerProperties != null) {
                                String shipName = "";
                                Entity ship = MainApp.getGame().getWorldManager().getEntityByItemId(trackerProperties.getTrackedEntityId());
                                if (ship != null) {
                                    EntityProperties followedShipEntityProperties = entityPropertiesMapper.getSafe(ship);
                                    if (followedShipEntityProperties != null) {
                                        shipName = followedShipEntityProperties.getNode().getName();
                                    }
                                }
//                                System.out.println("Projectile: " + entityProperties.getNode().getName() + projectileProperties.getId() + " has reached max distance following ship: " + shipName);
                            }
                            entitiesToDestroyQueue.offer(entityProperties);
                        } else {
                            // Check if tracking missile and rotate accordingly
                            TrackerProperties trackerProperties = trackerPropertiesMapper.getSafe(e);
                            if (trackerProperties != null && ENG_Utility.hasTimePassed(
                                    FrameInterval.PROJECTILE_TRACKING_DELAY + entityProperties.getNode().getName() + projectileProperties.getId(),
                                    trackerProperties.getTrackingDelayTimeStarted(),
                                    trackerProperties.getTrackingDelay())) {

                                Entity ship = MainApp.getGame().getWorldManager().getEntityByItemId(trackerProperties.getTrackedEntityId());
                                if (ship != null) {
                                    EntityProperties followedShipEntityProperties = entityPropertiesMapper.get(ship);
                                    if (!followedShipEntityProperties.isDestroyed()) {
//                                        ENG_Quaternion rotation = new ENG_Quaternion();
//                                        ENG_Math.rotateTowardPositionDeg(followedShipEntityProperties.getNode().getPosition(),
//                                                entityProperties.getNode().getPosition(),
//                                                entityProperties.getNode().getLocalInverseZAxis(),
//                                                entityProperties.getNode().getLocalYAxis(), rotation, trackerProperties.getMaxTurnAngle(),
//                                                new ENG_Vector4D(), new ENG_Vector4D());
//                                        entityProperties.rotate(rotation, true, TransformSpace.TS_WORLD);
                                        Utility.rotateToPosition(entityProperties, followedShipEntityProperties,
                                                trackerProperties.getMaxAngularVelocity());
//                                        Utility.rotateToPosition(entityProperties.getNode().getLocalInverseZAxis(),
//                                                followedShipEntityProperties.getNode().getPosition().subAsVec(entityProperties.getNode().getPosition()),
//                                                updateInterval, entityProperties,
//                                                trackerProperties.getMaxAngularVelocity());
//                                        Utility.rotateToPosition2(entityProperties.getNode().getOrientation(),
//                                                followedShipEntityProperties.getNode().getOrientation(), updateInterval,
//                                                entityProperties);

                                        // Check for countermeasures
                                        ShipProperties trackedShipProps = shipPropertiesMapper.get(ship);
                                        if (trackedShipProps.isCountermeasureTrackingDefenseActive()) {
                                            float length = followedShipEntityProperties.getNode().getPosition().distance(entityProperties.getNode().getPosition());
                                            int rand = (int) (ENG_Math.clamp(length,
                                                    TRACKING_MISSILE_MINIMUM_DISTANCE_RAND,
                                                    TRACKING_MISSILE_MAXIMUM_DISTANCE_RAND));
                                            if (rand <= 0) {
                                                rand = 1;
                                            }
                                            if (length < TRACKING_MISSILE_MAXIMUM_DISTANCE_RAND
                                                    && ENG_Utility.hasRandomChanceHit(
                                                    FrameInterval.TRACKING_MISSILE_MINIMUM_DISTANCE_RAND
                                                            + entityProperties.getNode().getName() + projectileProperties.getId(), rand)) {
                                                System.out.println("Projectile: " + entityProperties.getNode().getName() + projectileProperties.getId() + " has been evaded by: " + followedShipEntityProperties.getNode().getName());
                                                entitiesToDestroyQueue.offer(entityProperties);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    movementEndLatch.countDown();
                }
            } catch (Throwable e) {
                System.out.println("thread of exception: " + Thread.currentThread().getName());
                e.printStackTrace();
//                System.exit(0);
            }
        }

        private void updateAfterburner(Entity e, EntityProperties entityProperties, ShipProperties shipProperties) {
            if (shipProperties.isAfterburnerActive()) {
                if (!shipProperties.isLastSpeedSet()) {
//                    System.out.println("MovementSystem updateAfterBurner isLastSpeedSet: " + shipProperties.isLastSpeedSet() + " isAfterburnerSoundEmitted: " + shipProperties.isAfterburnerSoundEmitted());
                    if (!shipProperties.isAfterburnerSoundEmitted()) {
                        shipProperties.setAfterburnerSoundEmitted(true);
                        if (MainApp.getMainThread().getApplicationSettings().applicationMode == MainApp.Mode.CLIENT) {
                            entitiesToPlaySoundQueue.offer(entityProperties);

                        }
                    }
                    entityProperties.getVelocityAsVec(tempVelocity);
                    shipProperties.setLastSpeed(tempVelocity);
                    AIProperties aiProperties = aiPropertiesMapper.getSafe(e);
                    if (aiProperties == null || (!AISystem.isInsideWaypointSector(aiProperties))) {
                        entityProperties.setVelocity(
                                shipProperties.getAfterburnerMaxSpeedCoeficient() * shipProperties.getShipData().maxSpeed);
                    }
                    shipProperties.setLastSpeedSet(true);
                    if (MainApp.getMainThread().getApplicationSettings().applicationMode == MainApp.Mode.CLIENT) {
                        if (HudManager.getSingleton().isVisible()) {
                            shipProperties.setLastSpeedScrollPercentageBeforeAfterburner(
                                    HudManager.getSingleton().getSpeedScrollOverlay().getPercentage());
                            if (WorldManager.getSingleton().getPlayerShip() == e) {
                                HudManager.getSingleton().getSpeedScrollOverlay().setPercentage(100);
                            }
                        }
                    }
                }
            } else {
                shipProperties.setAfterburnerSoundEmitted(false);
                if (shipProperties.isLastSpeedSet()) {
                    shipProperties.setLastSpeedSet(false);
                    entityProperties.setVelocity(shipProperties.getLastSpeed());
                    if (MainApp.getMainThread().getApplicationSettings().applicationMode == MainApp.Mode.CLIENT) {
                        if (HudManager.getSingleton().isVisible()) {
                            if (WorldManager.getSingleton().getPlayerShip() == e) {
                                HudManager.getSingleton().getSpeedScrollOverlay().setPercentage(
                                        shipProperties.getLastSpeedScrollPercentageBeforeAfterburner());
                            }
                        }
                    }
                }
            }
        }
    }



    public MovementSystem(double interval) {
        super(Aspect.all(EntityProperties.class), (float) interval);

        float updateInterval = (float) interval;
    }

    public void createThreads() {
        destroyThreads();
        for (int i = 0; i < movementRunnable.length; ++i) {
            movementRunnable[i] = new MovementRunnable();
            movementThread[i] = new Thread(movementRunnable[i], "MovementSystem_thread_" + i);
            movementThread[i].start();

        }
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            System.out.println("MovementSystem.createThreads");
        }
        threadsCreated = true;
    }

    public void destroyThreads() {
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            System.out.println("MovementSystem.DestroyThreads() begin");
        }
        threadsCreated = false;
        for (int i = 0; i < movementRunnable.length; ++i) {
            if (movementThread[i] != null) {
                try {
                    movementThread[i].interrupt();
                    movementThread[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                movementThread[i] = null;
            }
        }
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            System.out.println("MovementSystem.DestroyThreads() end");
        }
    }

    @Override
    protected void begin() {
//        super.begin();
//        beginTime = ENG_Utility.currentTimeMillis();
//        Bag<Entity> entities = getEntities();
//        int entitiesNum = entities.size();
//        System.out.println("entitiesNum: " + entitiesNum);
//        int latchCount;
//        if (entitiesNum > threadNum) {
//            latchCount = threadNum;
//        } else if (entitiesNum > 0) {
//            latchCount = entitiesNum;
//        } else {
//            return;
//        }
//        movementEndLatch = new CountDownLatch(latchCount);
//        queueAddingEnded.set(false);
//        System.out.println("latchCount: " + latchCount);
//        int entitiesNum = 0;
//        int size = entities.size();
//        for (int i = 0; i < size; ++i) {
//            if (!entityPropertiesMapper.get(entities.get(i)).isUnmovable()) {
//                ++entitiesNum;
//            }
//        }
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            System.out.println("MovementSystem begin");
        }
        // This is an attempt to prevent a possible condition where the movement threads
        // have been destroyed but begin is called so in end() we end up waiting forever
        // since movementEndLatch != null.
        if (!threadsCreated) {
            if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
                System.out.println("MovementSystem threads not created");
            }
            return;
        }
        movementEndLatch = new CountDownLatch(/*entitiesNum*/getEntities().size());
//        Bag<Entity> entities = getEntities();
////        System.out.println("Adding entities");
//        for (int i = 0; i < entitiesNum; ++i) {
//            EntityProperties entityProperties = entities.get(i).getComponent(EntityProperties.class);
////            System.out.println(entityProperties.getNode().getName());
//            entityProperties.clearNodeNameToCurrentThreadName();
//        }
//        System.out.println("Added entities num: " + entitiesNum);
    }

    @Override
    protected void process(Entity e) {
        

//        if (!entityPropertiesMapper.get(e).isUnmovable()) {
            entityQueue.add(e);
//            System.out.println("Adding " + entityPropertiesMapper.get(e).getName());
//        }

    }

    @Override
    protected void end() {
        if (movementEndLatch == null) {
            return;
        }
//        queueAddingEnded.set(true);
//        long beginWaitTime = ENG_Utility.currentTimeMillis();
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            System.out.println("MovementSystem.end() begin");
        }
        try {
            movementEndLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//            System.out.println("MovementSystem.end() end");
        }
//        long endTime = ENG_Utility.currentTimeMillis() - beginWaitTime;
//        System.out.println("movementEndLatch wait time: " + endTime);
        movementEndLatch = null;
//        System.out.println("ended");
        EntityProperties entityProperties;
        while ((entityProperties = entitiesToDestroyQueue.poll()) != null) {
//            System.out.println("Destroying entity MovementSystem: " + entityProperties.getName());
            MainApp.getGame().getWorldManager().destroyEntity(entityProperties);
        }
        while ((entityProperties = entitiesToPlaySoundQueue.poll()) != null) {
//            System.out.println("Playing sound for entity: " + entityProperties.getName());
            WorldManager.getSingleton().playSoundBasedOnDistance(
                    entityProperties,
                    ShipData.getAfterburnerSoundName(ENG_Utility.getRandom()
                            .nextInt(FrameInterval.UPDATE_AFTERBURNER_SOUND_NUM + entityProperties.getNode().getName(), ShipData.AFTERBURNER_SOUND_NUM)));
        }
//        System.out.println("MovementSystem time: " + (ENG_Utility.currentTimeMillis() - beginTime));
//        super.end();
    }

//    public void resetCameraPositions() {
//        for (Runnable r : movementRunnable) {
//            ((MovementRunnable) r).resetCameraPositions();
//        }
//
//    }
}
