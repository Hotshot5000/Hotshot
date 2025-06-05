/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 2:35 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.math.Vector3;
import com.google.common.collect.EvictingQueue;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.EntityData;
import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.Utility;
import headwayent.blackholedarksun.animations.CargoShipExplosionAnimation;
import headwayent.blackholedarksun.animations.CountermeasuresAnimationFactory;
import headwayent.blackholedarksun.animations.ExplosionAnimation;
import headwayent.blackholedarksun.animations.PortalEnteringAnimation;
import headwayent.blackholedarksun.animations.PortalEnteringPlayerShipAnimation;
import headwayent.blackholedarksun.animations.PortalExitingAnimation;
import headwayent.blackholedarksun.animations.PortalExitingPlayerShipAnimation;
import headwayent.blackholedarksun.animations.ProjectileExplosionAnimation;
import headwayent.blackholedarksun.animations.ShipExplosionAnimation;
import headwayent.blackholedarksun.animations.ShipHitAnimationFactory;
import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.BeaconProperties;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.CargoProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.TrackerProperties;
import headwayent.blackholedarksun.components.WaypointProperties;
import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.entitydata.AsteroidData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.input.InGameInputConvertorListener;
import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.levelresource.MultiplayerClientLevel;
import headwayent.blackholedarksun.levelresource.MultiplayerClientLevelEvent;
import headwayent.blackholedarksun.levelresource.MultiplayerClientLevelStart;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerLevelEndedContainerListener;
import headwayent.blackholedarksun.menusystemsimpleview.containerlisteners.MultiplayerShipDestroyedContainerListener;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameUDP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDP;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.multiplayer.components.PlayerState;
import headwayent.blackholedarksun.multiplayer.rmi.UserStatsList;
import headwayent.blackholedarksun.multiplayer.systems.ClientEntityInterpolationSystem;
import headwayent.blackholedarksun.multiplayer.systems.DataSenderMPSystem;
import headwayent.blackholedarksun.multiplayer.systems.GameLogicEntityRemoverMPSystem;
import headwayent.blackholedarksun.multiplayer.systems.PlayerEntityDestroyedVerifierMPSystem;
import headwayent.blackholedarksun.multiplayer.systems.ProjectileUpdateMPSystem;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.physics.EntityMotionState;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.physics.PlayerShipMotionState;
import headwayent.blackholedarksun.systems.EntityDeleterSystem;
import headwayent.blackholedarksun.systems.StaticEntityDeleterSystem;
import headwayent.hotshotengine.AsyncTask;
import headwayent.hotshotengine.Bundle;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.gui.simpleview.ENG_ContainerManager;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_CameraNative;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_Workflows;

/**
 * Created by sebas on 05.11.2015.
 */
public class WorldManagerMP extends WorldManager {

    private static final boolean TEST_MODE = true;
    private static final int MESSAGE_QUEUE_SIZE = 2000;
    private static final int PING_WAIT_TIME = 1000;
    private static final int CLIENT_DROP_FRAMES_LIMIT = PlayerState.CLIENT_DROP_FRAMES_LIMIT;
    /** @noinspection UnstableApiUsage */ //    private MultiplayerServerFrameTCP currentServerFrameTCP;
//    private MultiplayerServerFrameUDP currentServerFrameUDP;
    private final EvictingQueue<MultiplayerServerFrameTCP> serverFrameTCPs = EvictingQueue.create(MESSAGE_QUEUE_SIZE);
    /** @noinspection UnstableApiUsage */
    private final EvictingQueue<MultiplayerServerFrameUDP> serverFrameUDPs = EvictingQueue.create(MESSAGE_QUEUE_SIZE);
    private MultiplayerEntityTCP currentlyAddedEntity;
//    private MultiplayerEntityTCP currentlyAddedProjectileEntity;
    private final ArrayList<Entity> createdProjectileList = new ArrayList<>();
    private final MultiplayerClientFrameTCP clientFrameTCP = new MultiplayerClientFrameTCP();
    private final MultiplayerClientFrameUDP clientFrameUDP = new MultiplayerClientFrameUDP();
    private int tcpMissingFrameNum;
    private int udpMissingFrameNum;
    private final ENG_Vector4D levelObjectTranslation = new ENG_Vector4D(true);
    private final ENG_Quaternion levelObjectRotation = new ENG_Quaternion();
    private long nextFrameNumTCP;
    private long nextFrameNumUDP;
    private final HashMap<Long, Entity> entityIdToEntityMap = new HashMap<>();
//    private final HashMap<Long, Entity> userIdToEntityMap = new HashMap<>();

    // Client ids contained in this map.
    private final HashMap<Long, Entity> clientIdToProjectileEntityMap = new HashMap<>();
    private final HashMap<Long, Entity> clientIdToEntityMap = new HashMap<>();
    private final ArrayList<MultiplayerEntityTCP> addedProjectiles = new ArrayList<>();
    private MultiplayerEntityTCP currentProjectileEntity;

    private final HashMap<Long, MultiplayerEntityTCP> entityIdToMultiplayerEntityTCP = new HashMap<>();

    private final ArrayList<MultiplayerEntityTCP> projectilesToBeAdded = new ArrayList<>();

    private final ReentrantLock addServerFrameLock = new ReentrantLock();
    private long pingCurrentCheckTime;
    private UserStatsList userStatsList;
    private int ping;
    private AsyncTask<Void, Void, Void> exitGameAsyncTask;
    private AsyncTask<Void, Void, Void> exitToMainMenuAsyncTask;
    private boolean exitGameReady;
    private boolean exitToMainMenuReady;

    public WorldManagerMP() {
        MainApp.getGame().setGameMode(APP_Game.GameMode.MP);
        resetIdCounter();
    }

    @Override
    public void resetWorld() {
        super.resetWorld();
        tcpMissingFrameNum = 0;
        udpMissingFrameNum = 0;
        nextFrameNumTCP = 0;
        nextFrameNumUDP = 0;
        currentlyAddedEntity = null;
        entityIdToEntityMap.clear();
        ping = 0;
        userStatsList = null;
    }

    public MultiplayerClientFrameTCP getClientFrameTCP() {
        return clientFrameTCP;
    }

    public MultiplayerClientFrameUDP getClientFrameUDP() {
        return clientFrameUDP;
    }

    @Override
    public void onPlayerShipDestroyedAnimationFinished() {
        Bundle bundle = getUserStatsBundle();
        ENG_ContainerManager.getSingleton().setCurrentContainer(
                ENG_ContainerManager.getSingleton().createContainer("MultiplayerShipDestroyed", "MultiplayerShipDestroyed", null, false,
                        new ENG_ContainerManager.ContainerListenerObject(
                                MultiplayerShipDestroyedContainerListener.MultiplayerShipDestroyedContainerListenerFactory.TYPE, bundle)));
//        PlayerEntityDestroyedVerifierMPSystem system = gameWorld.getSystem(PlayerEntityDestroyedVerifierMPSystem.class);
//        system.setPlayerEntityId(-1);
    }
    
    private void setLevelEndedContainer(LevelEvent.EventState eventState) {
        Bundle bundle = getUserStatsBundle();
        bundle.putObject("eventState", eventState);
        System.out.println("setLevelEndedContainer");
        ENG_ContainerManager.getSingleton().setCurrentContainer(
                ENG_ContainerManager.getSingleton().createContainer("MultiplayerLevelEnded", "MultiplayerLevelEnded", bundle, false,
                        new ENG_ContainerManager.ContainerListenerObject(
                                MultiplayerLevelEndedContainerListener.MultiplayerLevelEndedContainerListenerFactory.TYPE, bundle)));
    }

    /** @noinspection deprecation*/
    private Bundle getUserStatsBundle() {
        Bundle bundle = new Bundle();
        bundle.putObject("userStatsList", userStatsList);
        return bundle;
    }

    @Override
    public void onPlayerShipDestroyed(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties, CameraProperties cameraProperties) {
        setDeathCam(entity, entityProperties, cameraProperties);
    }

    public void resetPlayerShip() {
//        playerShip = null;
        playerShipEntityId = -1;
    }

    /** @noinspection UnstableApiUsage*/
    public void addServerFrameTCP(MultiplayerServerFrameTCP serverFrameTCP) {
        addServerFrameLock.lock();
        try {
//            currentServerFrameTCP = serverFrameTCP;
            serverFrameTCPs.add(serverFrameTCP);
            for (MultiplayerEntityTCP multiplayerEntityTCP : serverFrameTCP.getAddedEntities()) {
                System.out.println("added entity: " + multiplayerEntityTCP.getEntityProperties().getName());
            }

        } finally {
            addServerFrameLock.unlock();
        }
    }

    /** @noinspection UnstableApiUsage*/
    public void addServerFrameUDP(MultiplayerServerFrameUDP serverFrameUDP) {
        addServerFrameLock.lock();
        try {
//            currentServerFrameUDP = serverFrameUDP;
            serverFrameUDPs.add(serverFrameUDP);
        } finally {
            addServerFrameLock.unlock();
        }
//        System.out.println("Copy time diff: " + (ENG_Utility.currentTimeMillis() - serverFrameUDP.getReceivedTimestamp()));
    }

    @Override
    public void resetIdCounter() {
        // The client ids are negative. Positive is coming only from server.
        gameEntityId = Long.MIN_VALUE;
    }

    @Override
    public void createProjectile(Entity ship) {
        if (currentProjectileEntity != null) {
            // This projectile is created by the server.
            createProjectileByServer(ship, currentProjectileEntity);
        } else {
            // Client created projectile.
            createdProjectileList.clear();
            createProjectileFromShip(ship, createdProjectileList);
            for (Entity entity : createdProjectileList) {
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
                ProjectileProperties projectileProperties = projectilePropertiesComponentMapper.get(entity);
                TrackerProperties trackerProperties = trackerPropertiesComponentMapper.getSafe(entity);
                MultiplayerEntityTCP multiplayerEntityTCP = addMultiplayerComponentsToEntity(entityProperties.getEntityId(), entity);
                multiplayerEntityTCP.setEntityProperties(entityProperties);
                multiplayerEntityTCP.setProjectileProperties(projectileProperties);
                // The trackerProperties tracked entity id contains the local item id
                // for the ship. What we need is the server entity id.
                if (trackerProperties != null && trackerProperties.getTrackedEntityId() > 0) {
                    Entity entityByUniqueId = getEntityByItemId(trackerProperties.getTrackedEntityId());
                    if (entityByUniqueId != null) {
                        EntityProperties trackedShipEntityProperties = entityPropertiesComponentMapper.getSafe(entityByUniqueId);
                        if (trackedShipEntityProperties != null) {
                            System.out.println("Creating tracking projectile by client with currentSelectedEnemy item id: " + trackerProperties.getTrackedEntityId() + " tracked server entity id: " + trackedShipEntityProperties.getEntityId() + " item name: " + trackedShipEntityProperties.getItem().getName());
                            TrackerProperties trackerPropertiesUpdated = new TrackerProperties(trackerProperties);
                            trackerPropertiesUpdated.setTrackedEntityId(trackedShipEntityProperties.getEntityId());
                            multiplayerEntityTCP.setTrackerProperties(trackerPropertiesUpdated);
                        } else {
                            System.out.println("trackedShipEntityProperties is null");
                        }
                    } else {
                        System.out.println("entityByUniqueId is null for: " + trackerProperties.getTrackedEntityId() + " for launcher ship: " + entityPropertiesComponentMapper.get(entity).getName());
                    }
                }
//                if (trackerProperties != null) {
//                    System.out.println("Creating tracking projectile by client with currentSelectedEnemy: " + trackerProperties.getTrackedEntityId());
//                }
//                clientFrameTCP.addProjectile(multiplayerEntityTCP);
                entityIdToEntityMap.put(entityProperties.getEntityId(), entity);
                addedProjectiles.add(multiplayerEntityTCP);
//                clientIdToProjectileEntityMap.put(entityProperties.getEntityId(), entity);
//                clientIdToEntityMap.put(entityProperties.getEntityId(), entity);
            }
            // Create sound for only one projectile not all of them.
            if (!createdProjectileList.isEmpty()) {
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(ship);
                WeaponProperties weaponProperties = weaponPropertiesComponentMapper.get(ship);
                headwayent.blackholedarksun.entitydata.WeaponData.WeaponType weaponType = weaponProperties.getCurrentWeaponType();
                playSoundBasedOnDistance(entityProperties, headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getProjectileLaunchSoundName(weaponType));
            }
        }


    }

    /** @noinspection deprecation */
    private void createProjectileByServer(Entity ship, MultiplayerEntityTCP projectileEntity) {
        EntityProperties serverEntityProperties = projectileEntity.getEntityProperties();
        ProjectileProperties serverProjectileProperties = projectileEntity.getProjectileProperties();
        TrackerProperties serverTrackerProperties = projectileEntity.getTrackerProperties();

        EntityProperties projectileLauncherEntityProperties = entityPropertiesComponentMapper.get(ship);
        ShipProperties projectileLauncherShipProperties = shipPropertiesComponentMapper.get(ship);
        headwayent.blackholedarksun.entitydata.WeaponData weaponData = WeaponData.getWeaponData(serverProjectileProperties.getType());

        String name = projectileLauncherEntityProperties.getName() + "_" + WeaponData.WeaponType.getWeapon(weaponData.weaponType) +
                serverEntityProperties.getEntityId();

        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(name, serverEntityProperties.getEntityId()),
                serverEntityProperties.getEntityId(), weaponData.filename, "", ENG_Workflows.MetallicWorkflow);
//        ENG_Entity entity = sceneManager.createEntity(EntityProperties.generateUniqueName(name, serverEntityProperties.getEntityId()),
//                serverEntityProperties.getEntityId(), weaponData.filename, ENTITY_GROUP_NAME);
        // This is a blocking call!!!
        EntityAabb entityAabb = getEntityAabb(weaponData.filename);
        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
        System.out.println("creating projectile: " + item.getName());
        node.attachObject(item);

        Entity gameEntity = gameWorld.createEntity();
        addEntityByGameEntityId(serverEntityProperties.getEntityId(), gameEntity);
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(serverEntityProperties.getEntityId(), item.getId());
//        entityList.add(gameEntity);
//        projectileList.add(gameEntity);
        EntityProperties entityProperties = entityPropertiesComponentMapper.create(gameEntity);
//        EntityProperties entityProperties = new EntityProperties(gameEntity, entity, node, serverEntityProperties.getEntityId(), name);
        entityProperties.setGameEntity(gameEntity);
//        entityProperties.setEntity(entity);
        entityProperties.setItem(item);
        entityProperties.setNode(node);
        entityProperties.setEntityId(serverEntityProperties.getEntityId());
        entityProperties.setName(name);
        // entityProp.setPosition(i == 0 ? laserLeftOffset : laserRightOffset);
        // entityProp.getNode().rotate(currentShipOrientation,
        // TransformSpace.TS_PARENT);
        entityProperties.setOrientation(serverEntityProperties.getRotate());
        entityProperties.setPosition(serverEntityProperties.getTranslate());

        node.setPosition(serverEntityProperties.getTranslate().x, serverEntityProperties.getTranslate().y, serverEntityProperties.getTranslate().z);
        node.setOrientation(serverEntityProperties.getRotate());
        node._updateWithoutBoundsUpdate(false, false);

        ProjectileProperties projectileProp = projectilePropertiesComponentMapper.create(gameEntity);
        projectileProp.setType(weaponData.weaponType);
        projectileProp.setParentName(projectileLauncherEntityProperties.getName());
        projectileProp.setParentId(serverProjectileProperties.getParentId());
        projectileProp.setId(serverEntityProperties.getEntityId());
//        gameEntity.addComponent(entityProperties);
//        gameEntity.addComponent(projectileProp);
        entityProperties.setDamage(weaponData.damage);
        entityProperties.setHealth(weaponData.health);
        entityProperties.setWeight(weaponData.weight);
        entityProperties.setVelocity(weaponData.maxSpeed);
        entityProperties.setMaxSpeed(weaponData.maxSpeed);
        entityProperties.setDestructionSoundName(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getProjectileHitSoundName(weaponData.weaponType));
        entityProperties.setDestroyedAnimation(new ProjectileExplosionAnimation(item.getName(), gameEntity));
        // projectileProp.setMaxTurnAngle(weaponData.turnAngle);
        if (serverTrackerProperties != null) {
            // ENG_MovableObject currentSelectedEnemy =
            // HudManager.getSingleton().getCurrentSelectedEnemy();
            entityProperties.setOnDestroyedEvent(new headwayent.blackholedarksun.entitydata.WeaponData.WeaponOnDestroyedEvent(gameEntity));
            long currentSelectedEnemy = projectileLauncherShipProperties.getCurrentSelectedEnemy();
            System.out.println("Creating tracking projectile by server with currentSelectedEnemy: " + currentSelectedEnemy);
            if (currentSelectedEnemy != -1) {
                TrackerProperties trackingProperties = trackerPropertiesComponentMapper.create(gameEntity);
                trackingProperties.setTrackedEntityId(currentSelectedEnemy);
                trackingProperties.setMaxAngularVelocity(weaponData.maxAngularVelocity);
                trackingProperties.setTrackingDelay(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getHomingMissileTrackingDelay(weaponData.weaponType));
                trackingProperties.setTrackingDelayTimeStarted();
//                gameEntity.addComponent(trackingProperties);
                Entity trackedShip = getEntityByItemId(currentSelectedEnemy);
                if (trackedShip != null) {
                    shipPropertiesComponentMapper.get(trackedShip).addChasingProjectile(serverEntityProperties.getEntityId());
                }
            }
        }
//        gameWorld.addEntity(gameEntity);

        EntityMotionState motionState = new EntityMotionState(entityProperties, projectileProp, serverTrackerProperties);
        short collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
        short collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

        float weight = entityProperties.getWeight();

        createPhysicsBody(gameEntity, entityAabb.halfSize, entityProperties, motionState, collisionGroup, collisionMask, weight);

        // Disable collision between the ship and the just launched projectile.
        entityProperties.getRigidBody().setIgnoreCollisionCheck(entityProperties.getRigidBody(), true);

        entityIdToEntityMap.put(entityProperties.getEntityId(), gameEntity);

        playSoundBasedOnDistance(entityProperties, headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getProjectileLaunchSoundName(weaponData.weaponType));

        System.out.println(entityProperties.getName() + " projectile added with entity id: " + entityProperties.getEntityId());
    }

    @Override
    public void createReloaderEntity() {

    }

    @Override
    void prepareLevel() {
        MultiplayerClientLevel level = (MultiplayerClientLevel) gameWorld.getCurrentLevel();

        // Don't init the end events. We rely on the server to keep us posted.
//        initializeEndEvents(level);

        // Add all the cargos to a list for use in the hud manager
        for (LevelObject obj : level.levelStart.startObjects) {
            if (obj.type == LevelObject.LevelObjectType.CARGO) {
                cargoIdList.add(cargoNameToIdMap.get(obj.name));
            }
        }
    }

    @Override
    public void update(long currentTime) {
        if (levelState == LevelState.STARTED) {

//            System.out.println("Current time diff: " + (ENG_Utility.currentTimeMillis() - currentTime));

//            long beginTime = ENG_Utility.currentTimeMillis();



            initFrames();

            // We need to set the client time before processing the received updates or the addToQueue() method will have the last currentTime from the last
            // frame.
            ClientEntityInterpolationSystem clientEntityInterpolationSystem = gameWorld.getSystem(ClientEntityInterpolationSystem.class);
            clientEntityInterpolationSystem.setClientTime(currentTime);

            processReceivedUpdate();


            clientEntityInterpolationSystem.setLatency(ping);
            clientEntityInterpolationSystem.process();

            // No need for FollowindShipCounter*System because it all happens on the server.

//            updateProjectiles();
            gameWorld.getSystem(ProjectileUpdateMPSystem.class).process();

            // We load the level and have everything ready but we're not running anything. Everything script related is run on server
            // We need the level loaded so that we know what to load when we receive a createEntity() message.
//            updateLevelEvents();

            gameWorld.getSystem(PlayerEntityDestroyedVerifierMPSystem.class).process();

            gameWorld.getSystem(DataSenderMPSystem.class).process();

            gameWorld.getSystem(GameLogicEntityRemoverMPSystem.class).process();

            updateSounds(currentTime);

            updateAnimations();

            clearLists();

            checkLatency();

            checkDataReceivedFromServer();

//            long diff = ENG_Utility.currentTimeMillis() - beginTime;
//            System.out.println("Frame time: " + diff);
        }
    }

    private void checkDataReceivedFromServer() {
        if (!TEST_MODE && (tcpMissingFrameNum >= CLIENT_DROP_FRAMES_LIMIT || udpMissingFrameNum >= CLIENT_DROP_FRAMES_LIMIT)) {
            MultiplayerClientLevel level = (MultiplayerClientLevel) gameWorld.getCurrentLevel();
            endLevel(level, LevelEvent.EventState.CONNECTION_LOST);
        }
    }

    private void checkLatency() {
        if (!MainApp.getMainThread().isInputState() && ENG_Utility.hasTimePassed(pingCurrentCheckTime, PING_WAIT_TIME)) {
            pingCurrentCheckTime = ENG_Utility.currentTimeMillis();
            ping = NetManager.getSingleton().checkPing();
//            if (ping == 0) {
//                ping = -1;
//            }
            HudManager.getSingleton().setPing(ping);
        }
    }

//    public void removeProjectileClientId(long id) {
//        clientIdToProjectileEntityMap.remove(id);
//    }

    private void clearLists() {
//        addServerFrameLock.lock();
//        try {
//            currentServerFrameTCP = null;
//            currentServerFrameUDP = null;
//        } finally {
//            addServerFrameLock.unlock();
//        }
    }

    private void initFrames() {
        long userId = MainApp.getGame().getUser().getId();
//        clientFrameTCP = new MultiplayerClientFrameTCP(userId);
        clientFrameTCP.setUserId(userId);
        clientFrameTCP.setTimestampToCurrentTime();
        clientFrameTCP.setFrameNum(nextFrameNumTCP++);
//        clientFrameUDP = new MultiplayerClientFrameUDP(userId);
        clientFrameUDP.setUserId(userId);
        clientFrameUDP.setTimestampToCurrentTime();
        clientFrameUDP.setFrameNum(nextFrameNumUDP++);

        // Add the added projectiles for this frame.
        for (MultiplayerEntityTCP addedProjectile : addedProjectiles) {
            // We need to update for send here since updating in createProjectile means updating before MovementSystem had a chance to update
            // the projectile position without collision.
            addedProjectile.getEntityProperties().updateMultiplayerCoordsForSendClientSide();
            clientFrameTCP.addProjectile(addedProjectile);
        }
        addedProjectiles.clear();

    }

    private long beginFrameDiff;

    private void processReceivedUpdate() {
//        System.out.println("processReceivedUpdate");
        LinkedList<MultiplayerServerFrameTCP> multiplayerServerFrameTCPs = new LinkedList<>();
        LinkedList<MultiplayerServerFrameUDP> multiplayerServerFrameUDPs = new LinkedList<>();

        if (MainApp.getMainThread().isInputState()) {
            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            multiplayerServerFrameTCPs.addAll(currentFrameInterval.getTcpServerFrames());
            multiplayerServerFrameUDPs.addAll(currentFrameInterval.getUdpServerFrames());
        } else {
//            long beginTime = ENG_Utility.currentTimeMillis();
            addServerFrameLock.lock();
            try {
                multiplayerServerFrameTCPs.addAll(serverFrameTCPs);
                multiplayerServerFrameUDPs.addAll(serverFrameUDPs);
                serverFrameTCPs.clear();
                serverFrameUDPs.clear();
            } finally {
                addServerFrameLock.unlock();
            }
//            System.out.println("Copy time diff: " + (ENG_Utility.currentTimeMillis() - beginTime));
        }

//        long endFrameDiff = ENG_Utility.currentTimeMillis();
//        System.out.println("Frame diff: " + (endFrameDiff - beginFrameDiff));
//        beginFrameDiff = endFrameDiff;


//        System.out.println("BEGINNING FRAME TIME ITERATION");
//        for (MultiplayerServerFrameUDP multiplayerServerFrameUDP : multiplayerServerFrameUDPs) {
////            System.out.println("Frame time: " + (ENG_Utility.currentTimeMillis() - multiplayerServerFrameUDP.getReceivedTimestamp()));
//            System.out.println("Server Frame time: " + (ENG_Utility.currentTimeMillis() - multiplayerServerFrameUDP.getTimestamp()));
//        }

        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            currentFrameInterval.getTcpServerFrames().addAll(multiplayerServerFrameTCPs);
            currentFrameInterval.getUdpServerFrames().addAll(multiplayerServerFrameUDPs);
        }
        for (MultiplayerServerFrameTCP multiplayerServerFrameTCP : multiplayerServerFrameTCPs) {
            processReceivedUpdateTCP(multiplayerServerFrameTCP);
        }
        if (multiplayerServerFrameTCPs.isEmpty()) {
            ++tcpMissingFrameNum;
        } else {
            tcpMissingFrameNum = 0;
        }

        ClientEntityInterpolationSystem clientEntityInterpolationSystem = gameWorld.getSystem(ClientEntityInterpolationSystem.class);
        for (MultiplayerServerFrameUDP multiplayerServerFrameUDP : multiplayerServerFrameUDPs) {
//            processReceivedUpdateUDP(multiplayerServerFrameUDP);
            clientEntityInterpolationSystem.addToQueue(multiplayerServerFrameUDP);
        }
        if (multiplayerServerFrameUDPs.isEmpty()) {
            ++udpMissingFrameNum;
        } else {
            udpMissingFrameNum = 0;
        }
    }

    private void processReceivedUpdateTCP(MultiplayerServerFrameTCP currentServerFrameTCP) {
        projectilesToBeAdded.clear();
        for (MultiplayerEntityTCP addedEntity : currentServerFrameTCP.getAddedEntities()) {

            System.out.println("processReceivedUpdateTCP added entity: " + addedEntity.getEntityProperties().getName());

            // We need to pass the parameter to the createEntity().
            currentlyAddedEntity = addedEntity;
            EntityProperties currentEntityProperties = addedEntity.getEntityProperties();
            MultiplayerClientLevel level = (MultiplayerClientLevel) gameWorld.getCurrentLevel();
            MultiplayerClientLevelStart levelStart = (MultiplayerClientLevelStart) level.getLevelStart();
            boolean levelObjectFound = false;
            LevelObject levelObject = levelStart.levelObjectMap.get(currentEntityProperties.getName());
            if (levelObject == null) {
                // It's not in the start list. Maybe it's an object generated by an event.
                for (int i = 0; i < level.getLevelEventNum(); ++i) {
                    MultiplayerClientLevelEvent levelEvent = (MultiplayerClientLevelEvent) level.getLevelEvent(i);
                    levelObject = levelEvent.spawnMap.get(currentEntityProperties.getName());
                    if (levelObject != null) {
                        levelObjectFound = true;
                        break;
                    }
                }
            } else {
                levelObjectFound = true;
            }
            if (!levelObjectFound) { // Maybe it's a player ship so that is why we couldn't find it in the level.
                if (addedEntity.getShipProperties() == null) {
                    if (addedEntity.getProjectileProperties() != null) {
                        // If this is a projectile then we must use createProjectileByServer(), but since we might not have all the
                        // parent ships initialized, we must add this projectiles to be created later.
                        projectilesToBeAdded.add(addedEntity);
                        continue;
                    } else {
                        throw new IllegalStateException("entity: " + currentEntityProperties.getName() + " could not be found in level: " + level.name);
                    }
                } else {
                    // Init a special level object for a player.
                    levelObject = new LevelObject();
                    System.out.println("creating player ship: " + addedEntity.getEntityProperties().getName());
                }
            }
//            LevelObject levelObject = new LevelObject();
            String modelName;
//            levelObject.meshName = shipDataModelName;
            levelObject.name = currentEntityProperties.getName();
//            levelObject.type = currentEntityProperties.getType();

            currentEntityProperties.getTranslate(levelObjectTranslation);
            levelObject.position.set(levelObjectTranslation);
            currentEntityProperties.getRotate(levelObjectRotation);
            levelObject.orientation.set(levelObjectRotation);
            levelObject.health = currentEntityProperties.getHealth();
            levelObject.invincible = currentEntityProperties.isInvincible();

            if (addedEntity.getShipProperties() != null) {
                levelObject.userId = addedEntity.getShipProperties().getUserId();
                modelName = addedEntity.getShipProperties().getShipDataModelName();
                levelObject.meshName = modelName;
                // The server doesn't tell us if our ship is a player ship. Just that it's a figher ship. We must determine if this ship is in fact
                // our current player ship.
                User user = MainApp.getGame().getUser();
                if (user.getId() == levelObject.userId) {
                    // This is the player ship
                    levelObject.type = LevelObject.LevelObjectType.PLAYER_SHIP;
//                    levelObject.position.set(0.0f, 0.0f, 4000.0f);
                } else if (levelObject.userId > 0) {
                    // We have another player's ship.
                    levelObject.type = LevelObject.LevelObjectType.FIGHTER_SHIP;
                    HudManager.getSingleton().setPlayerSpawnInfoText("Player: " + levelObject.name + " has joined the fight");
                }
            } else {
//                    ENG_ModelResource data = MainApp.getGame().getResource(currentEntityProperties.getName());
//                    modelName = data.name;
                modelName = levelObject.meshName;
            }


            createEntity(modelName, levelObject);
        }

        for (MultiplayerEntityTCP addedProjectile : projectilesToBeAdded) {
            Entity parentShip = entityIdToEntityMap.get(addedProjectile.getProjectileProperties().getParentId());
            currentProjectileEntity = addedProjectile;
            createProjectile(parentShip);
        }

//        for (MultiplayerEntityTCP projectileEntityTCP : currentServerFrameTCP.getClientSpecificAddedProjectiles()) {
//            EntityProperties entityProperties = projectileEntityTCP.getEntityProperties();
//            Entity clientEntity = clientIdToProjectileEntityMap.get(entityProperties.getEntityId());
//            if (clientEntity != null) {
//
//            }
//        }

        for (MultiplayerEntityTCP projectileEntityTCP : currentServerFrameTCP.getAddedProjectiles()) {
            EntityProperties entityProperties = projectileEntityTCP.getEntityProperties();
            ProjectileProperties projectileProperties = projectileEntityTCP.getProjectileProperties();
            TrackerProperties trackerProperties = projectileEntityTCP.getTrackerProperties();
            Entity parentShip = entityIdToEntityMap.get(projectileProperties.getParentId());
            if (parentShip == null) {
                System.out.println("Invalid parent ship for projectile. Parent id: " + projectileProperties.getParentId());
                continue;
            }

            currentProjectileEntity = projectileEntityTCP;
            createProjectile(parentShip);
        }
        currentProjectileEntity = null;
        for (MultiplayerEntityTCP multiplayerEntityTCP : currentServerFrameTCP.getUpdateEntities()) {
            Entity entity = entityIdToEntityMap.get(multiplayerEntityTCP.getEntityProperties().getEntityId());
//            printEntityIdToEntityMap();

            if (entity != null) {
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
                ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
                ProjectileProperties projectileProperties = projectilePropertiesComponentMapper.getSafe(entity);
                TrackerProperties trackerProperties = trackerPropertiesComponentMapper.getSafe(entity);
                entityProperties.set(multiplayerEntityTCP.getEntityProperties());
                if (shipProperties != null) {
                    ShipProperties multiplayerEntityTCPShipProperties = multiplayerEntityTCP.getShipProperties();
                    shipProperties.set(multiplayerEntityTCPShipProperties);
                    if (multiplayerEntityTCP.getEntityProperties().getEntityId() != playerShipEntityId) {
                        // Make sure that the afterburner isn't already active before setting it on to avoid setting the application debugging var twice.
                        if (multiplayerEntityTCPShipProperties.isAfterburnerActiveMP() && !shipProperties.isAfterburnerActiveMP()) {
                            shipProperties.setAfterburnerActive(true);
                        }
                        if (multiplayerEntityTCPShipProperties.isCountermeasureLaunched()) {
                            createCountermeasures(entity);
                        }
                    }
                    HashMap<String, Animation> nameToAnimation = animationMap.get(entityProperties.getEntityId());
                    if (nameToAnimation != null) {
                        for (Animation animation : nameToAnimation.values()) {
                            // HACK
                            if ((!shipProperties.isShowPortalEntering() && animation.getName().contains("PortalEntering"))
                                    || (!shipProperties.isShowPortalExiting() && animation.getName().contains("PortalExiting"))) {
                                animation.stop();
                            }
                        }
                    }
                }
                if (projectileProperties != null) {
                    projectileProperties.set(multiplayerEntityTCP.getProjectileProperties());
                }
//                if (trackerProperties != null) {
//                    trackerProperties.set(multiplayerEntityTCP.getTrackerProperties());
//                }
                System.out.println("Updating entity id: " + multiplayerEntityTCP.getEntityProperties().getEntityId() + " destroyed: " +
                        multiplayerEntityTCP.getEntityProperties().isDestroyed());
            } else {
                System.out.println("entityId: " + multiplayerEntityTCP.getEntityProperties().getEntityId()
                        + " name: " + multiplayerEntityTCP.getEntityProperties().getName() + " missing");
            }
        }

        if (currentServerFrameTCP.isLevelEnded()) {
            MultiplayerClientLevel level = (MultiplayerClientLevel) gameWorld.getCurrentLevel();
            endLevel(level, currentServerFrameTCP.isLevelWon() ? LevelEvent.EventState.WON : LevelEvent.EventState.LOST);
        }

    }

    private void printEntityIdToEntityMap() {
        System.out.println("ENTITY_ID_TO_ENTITY_MAP");
        for (Map.Entry<Long, Entity> entityIdToEntityEntry : entityIdToEntityMap.entrySet()) {
            System.out.println("entityId: " + entityIdToEntityEntry.getKey()
                    + " name: " + entityIdToEntityEntry.getValue().getComponent(EntityProperties.class).getName());
        }
    }

//    private void processReceivedUpdateUDP(MultiplayerServerFrameUDP currentServerFrameUDP) {
//        for (MultiplayerEntityUDP multiplayerEntityUDP : currentServerFrameUDP.getClientSpecificEntities()) {
//            Entity entity = clientIdToEntityMap.get(multiplayerEntityUDP.getEntityId());
//            if (entity == null) {
//                System.out.println("Missing client specific entity: " + multiplayerEntityUDP.getEntityName());
//            }
//            updateEntityProperties(entity, multiplayerEntityUDP);
//        }

//        for (MultiplayerEntityUDP multiplayerEntityUDP : currentServerFrameUDP.getEntities()) {
//            Entity entity = entityIdToEntityMap.get(multiplayerEntityUDP.getEntityId());
//            if (entity == null) {
//                System.out.println("Missing server entity: " + multiplayerEntityUDP.getEntityName() + " with entity id: " + multiplayerEntityUDP.getEntityId());
//            }
//            updateEntityProperties(entity, multiplayerEntityUDP);
//        }
//    }

    public void removeEntityByEntityId(Long id) {
        entityIdToEntityMap.remove(id);
        System.out.println("Removing entity with id: " + id);
    }

//    private void updateEntityProperties(Entity entity, MultiplayerEntityUDP multiplayerEntityUDP) {
//        if (entity != null) {
//            EntityProperties entityProperties = entity.getComponent(EntityProperties.class);
//            // Don't just set the position directly. Interpolate between the server and client positions.
//            entityProperties.setPosition(multiplayerEntityUDP.getTranslate());
//            entityProperties.setOrientation(multiplayerEntityUDP.getRotate());
//            entityProperties.setVelocity(multiplayerEntityUDP.getVelocity());
//            System.out.println("Updating entity: " + entityProperties.getName() + " " + multiplayerEntityUDP.toString());
////            if (multiplayerEntityUDP.getEntityId() == 2) {
////                System.out.println(multiplayerEntityUDP);
////            }
//        } else {
////            throw new IllegalStateException(entity.getComponent(EntityProperties.class).getName());
//        }
//    }

//    @Override
//    protected void updateLevelEvents() {
//
//    }

    @Override
    protected void exitObjectsFromLevel(LevelEvent levelEvent) {

    }

    @Override
    protected void spawnObjects(LevelEvent levelEvent) {

    }

    @Override
    protected void endLevel(LevelBase level, LevelEvent.EventState eventState) {
        System.out.println("level ended");
        HudManager.getSingleton().setVisible(false);
//		HudManager.getSingleton().destroyMovementFlares();
        ENG_InputManager.getSingleton().setInputStack(APP_Game.TOUCH_INPUT_STACK);
        // For the light direction to reset to default
        gameWorld.setCurrentLevel(null);
        setLevelEndedContainer(eventState);
        resetWorld();
    }

    @Override
    protected void createStaticEntity(String modelName, LevelObject obj) {
        super.createStaticEntity(modelName, obj);
    }

    @Override
    public void createDebris(Entity entity) {
        super.createDebris(entity);
    }

    /** @noinspection deprecation */
    @Override
    protected void createEntity(String modelName, LevelObject obj) {
        long beginTime = currentTimeMillis();
//        availableNameList.add(obj.name);

        // This is copied from createEntities since multiplayer player ship creation doesn't go through createEntities().
        String extension = FilenameUtils.getExtension(obj.meshName);
        if (extension.isEmpty()) {
            obj.meshName = obj.meshName + ".mesh";
        }

        EntityProperties serverEntityProperties = currentlyAddedEntity.getEntityProperties();

        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(obj.name, serverEntityProperties.getEntityId()),
                ENG_Utility.getUniqueId()/*serverEntityProperties.getEntityId()*/, obj.meshName, "", getWorkflow(obj));

        // This is a blocking call!!!
        EntityAabb entityAabb = getEntityAabb(obj.meshName);
        System.out.println("centre: " + entityAabb.centre.toString() + " halfSize: " + entityAabb.halfSize.toString());

        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
//        node.attachObject(entity);
        node.attachObject(item);
        Entity gameEntity = gameWorld.createEntity();
//        movableObjectsToEntities.put(entity.getName(), gameEntity);
//        entityList.add(gameEntity);

        addEntityByGameEntityId(serverEntityProperties.getEntityId(), gameEntity);
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(serverEntityProperties.getEntityId(), item.getId());

        EntityProperties entityProp = entityPropertiesComponentMapper.create(gameEntity);
//        EntityProperties entityProp = new EntityProperties(gameEntity, entity, node, serverEntityProperties.getEntityId(), obj.name);
        entityProp.setGameEntity(gameEntity);
//        entityProp.setEntity(entity);
        entityProp.setItem(item);
        entityProp.setNode(node);
        System.out.println("adding entity: " + obj.name + " entityId: " + serverEntityProperties.getEntityId());
        entityProp.setEntityId(serverEntityProperties.getEntityId());
        entityProp.setName(obj.name);
        entityProp.setHealth(serverEntityProperties.getHealth());
        System.out.println("Setting health to " + serverEntityProperties.getHealth() + " for entity: " + obj.name);
        entityProp.setDamage(obj.damage);
        entityProp.setTimedDamage(true);
        entityProp.setTimedDamageTime(1000);
        entityProp.setVelocity(obj.velocity);
        entityProp.setPosition(obj.position);
        entityProp.setOrientation(obj.orientation);
        entityProp.setRadius(obj.radius);
        entityProp.setInvincible(obj.invincible);
        entityProp.setModelName(modelName);
        entityProp.setObjectType(obj.type);

        node.setPosition(obj.position.x, obj.position.y, obj.position.z);
        node.setOrientation(obj.orientation);
        node._updateWithoutBoundsUpdate(false, false);

        EntityMotionState motionState = null;

        short collisionGroup = 0;
        short collisionMask = 0;

        LevelEntity levelEntity = new LevelEntity(gameEntity);

        // Don't add player ships since they are ephemeral.
        if (obj.userId == 0) {
            addLevelObjectByName(obj.name, levelEntity);
        }
//        addLevelObjectById(serverEntityProperties.getEntityId(), levelEntity);

        EntityData entityData = null;
        AIProperties aiProperties = null;
        if (obj.type == LevelObject.LevelObjectType.FLAG_BLUE
                || obj.type == LevelObject.LevelObjectType.FLAG_RED) {
            BeaconProperties beaconProperties = beaconPropertiesComponentMapper.create(gameEntity);
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.TRANSPARENT.getVal();
            collisionMask = PhysicsProperties.CollisionMask.TRANSPARENT.getVal();
        }
        if (obj.type == LevelObject.LevelObjectType.WAYPOINT) {
            WaypointProperties waypointProperties = waypointPropertiesComponentMapper.create(gameEntity);
            waypointProperties.setWaypointSectorId(obj.waypointSectorId);
            waypointProperties.setWaypointId(obj.waypointId);
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.TRANSPARENT.getVal();
            collisionMask = PhysicsProperties.CollisionMask.TRANSPARENT.getVal();
            addWaypointId(item.getId());
        }
        if (obj.type == LevelObject.LevelObjectType.CARGO) {
            cargoNameToIdMap.put(obj.name, serverEntityProperties.getEntityId());
            CargoProperties cargoProperties = cargoPropertiesComponentMapper.create(gameEntity);
            entityProp.setInvincible(true);
            entityProp.setScannable(true);
            entityProp.setUnmovable(true);
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();
        }
        if (obj.type == LevelObject.LevelObjectType.ASTEROID) {
            AsteroidData asteroidData = MainApp.getGame().getAsteroidData(modelName);
            entityData = asteroidData;
            entityProp.setUnmovable(true);
            entityProp.setDestroyedAnimation(new ExplosionAnimation("ExplosionAnimation " + node.getName(), gameEntity, ExplosionAnimation.EXPLOSION_SMALL_MAT, 3.0f));
            entityProp.setDestructionSoundName(APP_Game.getAsteroidExplosionSoundName(ENG_Utility.getRandom()
                    .nextInt(FrameInterval.ASTEROID_CREATE_ENTITY + obj.name, APP_Game.ASTEROID_SOUND_NUM)));
            motionState = new EntityMotionState(entityProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();
        }
        if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP
                || obj.type == LevelObject.LevelObjectType.CARGO_SHIP) {

            addToShipList(entityProp.getEntityId(), gameEntity);
            entityProp.setUpdateSectionList(true);
            entityProp.setScannable(true);
            entityProp.setShowHealth(true);
            ShipProperties shipProp = shipPropertiesComponentMapper.create(gameEntity);
            shipProp.setName(obj.name);
            shipProp.setUserId(obj.userId);
            shipProp.setScanRadius(obj.scanRadius);
            shipProp.setShowPortalEntering(currentlyAddedEntity.getShipProperties().isShowPortalEntering());
            headwayent.blackholedarksun.entitydata.ShipData shipData = MainApp.getGame().getNameToShipMap(modelName);
            entityData = shipData;
            entityProp.setHealth(serverEntityProperties.getHealth());
            entityProp.setMaxSpeed(shipData.maxSpeed);
            gameWorld.getManager(GroupManager.class).add(gameEntity, shipData.team.toString());
            setShipData(shipProp, shipData);
            setShipWeapons(gameEntity, shipData);

            motionState = new EntityMotionState(entityProp, shipProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

            // Must create the ship properties before setting up an explosion
            // animation
            if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP) {
                entityProp.setDestroyedAnimation(new ShipExplosionAnimation(
                        "ShipExplosionAnimation " + obj.name + " entityId " + entityProp.getEntityId(), gameEntity));
            } else if (obj.type == LevelObject.LevelObjectType.CARGO_SHIP) {
                entityProp.setDestroyedAnimation(new CargoShipExplosionAnimation(
                        "CargoShipExplosionAnimation " + obj.name + " entityId " + entityProp.getEntityId(), gameEntity));
            }
            if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP) {
                entityProp.setDestructionSoundName(headwayent.blackholedarksun.entitydata.ShipData.getShipDestroyedSound(ENG_Utility.getRandom()
                        .nextInt(FrameInterval.DESTRUCTION_SOUND_CREATE_ENTITY + obj.name, headwayent.blackholedarksun.entitydata.ShipData.SHIP_DESTROYED_SOUND_NUM)));
            } else if (obj.type == LevelObject.LevelObjectType.CARGO_SHIP) {
                entityProp.setDestructionSoundName(headwayent.blackholedarksun.entitydata.ShipData.getCargoShipDestroyedSound(
                        ENG_Utility.getRandom()
                                .nextInt(FrameInterval.CARGO_DESTRUCTION_SOUND_CREATE_ENTITY + obj.name, headwayent.blackholedarksun.entitydata.ShipData.CARGO_SHIP_DESTROYED_SOUND_NUM)));
            }
            shipProp.setCountermeasuresAnimationFactory(new CountermeasuresAnimationFactory(
                    "CountermeasureAnimation " + entityProp.getName() + " entityId " + entityProp.getEntityId()));
//            shipProp.setAiEnabled(obj.ai);
            if (obj.type != LevelObject.LevelObjectType.PLAYER_SHIP) {
                Sound sound = playSoundBasedOnDistance(entityProp, shipData.engineSoundName, true, true);
                if (sound != null) {
                    sound.maxDistance = MAX_ENGINE_SOUND_DISTANCE;
                    sound.volumeMultiplier = 0.5f;
                    shipProp.setEngineSound(sound);
                }
            }
//            userIdToEntityMap.put(obj.userId, gameEntity);
//            entityMap.put(id, gameEntity);
//            ++id;
        }
        if (obj.ai) {
            aiProperties = addAIProperties(gameEntity, entityProp, obj);

        }
        if (obj.type == LevelObject.LevelObjectType.PLAYER_SHIP) {

            addToShipList(entityProp.getEntityId(), gameEntity);
            entityProp.setUpdateSectionList(true);
            ENG_Camera camera = sceneManager.getCamera(APP_Game.MAIN_CAM);
            ENG_CameraNative cameraNative = new ENG_CameraNative(camera);
//            ENG_SceneNode cameraNode;
            if (sceneManager.getRootSceneNode().hasChild(CAMERA_NODE_NAME)) {
                cameraNode = (ENG_SceneNode) sceneManager.getRootSceneNode().getChild(CAMERA_NODE_NAME);
            } else {
                cameraNode = sceneManager.getRootSceneNode().createChildSceneNode(CAMERA_NODE_NAME);
            }
            cameraNode.setNativeName();
            // sceneManager.getSkyboxNode();
            /*
			 * if (cameraNode == null) { throw new
			 * NullPointerException("set the skybox first before " +
			 * "trying to rotate the skybox around the camera"); }
			 */

            CameraProperties camProp = cameraPropertiesComponentMapper.create(gameEntity);
//            CameraProperties camProp = new CameraProperties(camera, cameraNode);
            camProp.setCamera(camera);
            camProp.setNode(cameraNode);
            boolean thirdPersonCamera = MainApp.getGame().isThirdPersonCamera();
            camProp.setType(thirdPersonCamera ? CameraProperties.CameraType.THIRD_PERSON : CameraProperties.CameraType.FIRST_PERSON);
//            gameEntity.addComponent(camProp);

            cameraNative.detachFromParent();
            cameraNode.attachObject(cameraNative);
//            if (!cameraNode.hasAttachedObject(camera.getName())) {
//                cameraNode.attachObject(camera);
//            }
            if (!thirdPersonCamera) {
                // Make the ship invisible
                node.flipVisibility(false);
            }
            // Get around the bug described at
            // http://www.ogre3d.org/forums/viewtopic.php?f=1&t=72872
            camera.invalidateView();
            // System.out.println("camera attached with address " + cameraNode);
            ShipProperties shipProp = shipPropertiesComponentMapper.create(gameEntity);
//            ShipProperties shipProp = new ShipProperties();
            shipProp.setName(obj.name);
            shipProp.setUserId(obj.userId);
            shipProp.setScanRadius(obj.scanRadius);
            if (playerShipData == null) {
                throw new NullPointerException("playerShipData is null");
            }
            entityData = playerShipData;
            setShipData(shipProp, playerShipData);
//            gameEntity.addComponent(shipProp);
            setShipWeapons(gameEntity, playerShipData);
//            entityMap.put(id, gameEntity);
//            ++id;
//            playerShip = gameEntity;
            playerShipEntityId = entityProp.getEntityId();
            PlayerEntityDestroyedVerifierMPSystem playerEntityDestroyedVerifierMPSystem = gameWorld.getSystem(PlayerEntityDestroyedVerifierMPSystem.class);
            // Reset or else we cannot detect a new player ship destruction !!!
            playerEntityDestroyedVerifierMPSystem.reset();
//            playerEntityDestroyedVerifierMPSystem.setPlayerEntityId(playerShipEntityId);
            GameLogicEntityRemoverMPSystem entityRemoverMPSystem = gameWorld.getSystem(GameLogicEntityRemoverMPSystem.class);
            entityRemoverMPSystem.setPlayerShipEntityId(playerShipEntityId);

            HudManager.getSingleton().setMaxScrollPercentageChange(playerShipData.maxPercentageAcceleration);
            HudManager.getSingleton().setScrollStartingPercentage(playerShipData.initialSpeedPercentual);
            entityProp.setVelocity(shipProp.getVelocity(playerShipData.initialSpeedPercentual));

            SimpleViewGameMenuManager.updateMenuState(SimpleViewGameMenuManager.MenuState.IN_GAME_OVERLAY);

            entityProp.setHitAnimationFactory(new ShipHitAnimationFactory("ShipHitAnimation " + obj.name + " "));
            shipProp.setCountermeasuresAnimationFactory(new CountermeasuresAnimationFactory(
                    "CountermeasureAnimation " + entityProp.getName() + " entityId " + entityProp.getEntityId()));

            entityProp.setDestroyedAnimation(new ShipExplosionAnimation("ShipExplosionAnimation " + obj.name, gameEntity));
            entityProp.setDestructionSoundName(headwayent.blackholedarksun.entitydata.ShipData.getShipDestroyedSound(ENG_Utility.getRandom()
                    .nextInt(FrameInterval.DESTRUCTION_SOUND_CREATE_ENTITY + obj.name, headwayent.blackholedarksun.entitydata.ShipData.SHIP_DESTROYED_SOUND_NUM)));

//            entityProp.setHealth((int) getPlayerShipData().armor);
            gameWorld.getManager(GroupManager.class).add(gameEntity, getPlayerShipData().team.toString());

            motionState = new PlayerShipMotionState(entityProp, shipProp, camProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

//            userIdToEntityMap.put(obj.userId, gameEntity);

            HudManager.getSingleton().setVisible(true);
            ENG_InputManager.getSingleton().setInputStack(APP_Game.IN_GAME_INPUT_STACK);
            // Set the scroll overlay in the in game listener
            HudManager.getSingleton().getSpeedScrollOverlay().setPercentage(playerShipData.initialSpeedPercentual);
            SimpleViewGameMenuManager.setScrollOverlayToInGameListener();
        }

        if (entityData == null) {
            setWeight(entityProp, obj.type);
            entityData = new EntityData();
        } else {
            entityProp.setWeight(entityData.weight);
        }

        createPhysicsSettings(entityProp, entityData, gameEntity, entityAabb, motionState, collisionGroup, collisionMask);
        if (aiProperties != null) {
            aiProperties.setRayResultCallback(PhysicsUtility.createRayTest(
                    entityProp.getRigidBody(), new Vector3(), new Vector3(), collisionGroup, collisionMask));
        }

        // Make sure it's an actual ship that we are talking about.
        if (currentlyAddedEntity.getShipProperties() != null && currentlyAddedEntity.getShipProperties().isShowPortalEntering()) {
            animateShipSpawn(gameEntity);
        }

//        gameWorld.addEntity(gameEntity);

        entityIdToEntityMap.put(entityProp.getEntityId(), gameEntity);

        // The id is set by the server not by the client!
//        ++id;
    }

    @Override
    public void loadLevel() {
        reinitializeWorld();
        MultiplayerClientLevel level = (MultiplayerClientLevel) gameWorld.getCurrentLevel();
        createSkybox(level.levelStart.skyboxName);

        createLevelLighting(level.levelStart);

//        loadReloader(level);
//        loadLevelObjects(level.levelStart.startObjects);

        prepareLevel();
        loadWaypoints();
        // When loading the level also reset the position from
        // which we calculate the pitch and yaw.
        if (MainApp.getGame().isAccelerometerEnabled()) {
//            MainApp.getGame().getInputConvertorToMovement()
//                    .resetOriginalOrientation();
        }
        ((InGameInputConvertorListener) ENG_InputManager.getSingleton().getInputConvertorListener(APP_Game.TO_IN_GAME_LISTENER)).reset();
        HudManager.getSingleton().reset();
        setLevelState(LevelState.STARTED);
    }

    @Override
    protected void animateShipSpawn(Entity entity) {
        ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
        if (shipProperties != null) {

            EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
            Animation anim;
            if (shipProperties.getUserId() == MainApp.getGame().getUser().getId()) {
                anim = new PortalEnteringPlayerShipAnimation("PortalEnteringPlayerShipAnimation " + entityProperties.getUniqueName(), entity);
            } else {
                anim = new PortalEnteringAnimation("PortalEnteringAnimation " + entityProperties.getUniqueName(), entity);
            }
//			Timer timer = ENG_Utility.createTimerAndStart();
            startAnimation(entityProperties.getEntityId(), anim);
//			ENG_Utility.stopTimer(timer, "startAnimation()");
        }
    }

    @Override
    protected void animateShipExit(Entity entity) {
        if (entity != null) {
            ShipProperties shipProperties = shipPropertiesComponentMapper.getSafe(entity);
            if (shipProperties != null) {
                EntityProperties entityProperties = entityPropertiesComponentMapper.get(entity);
                Animation anim;
                if (shipProperties.getUserId() == MainApp.getGame().getUser().getId()) {
                    anim = new PortalExitingPlayerShipAnimation("PortalExitingPlayerAnimation " + entityProperties.getUniqueName(), entity);
                } else {
                    anim = new PortalExitingAnimation("PortalExitingAnimation " + entityProperties.getUniqueName(), entity);
                }
                Utility.clearAngularVelocity(entityProperties.getRigidBody());
                startAnimation(entityProperties.getEntityId(), anim);
            }
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Override
    public void exitGame() {
        if (exitGameAsyncTask == null) {
            MainApp.getGame().getEventBus().post(new ClientAPI.LeaveSessionEvent());
            exitGameReady = false;
            exitGameAsyncTask = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    NetManager.getSingleton().disconnectFromServer();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    exitGameReady = true;
                    exitGame();
                    NetManager.getSingleton().closeClient();
                    exitGameAsyncTask = null;
                }
            };
            exitGameAsyncTask.execute();
        } else {
            if (exitGameReady) {
                super.exitGame();
            }
        }

    }

    /** @noinspection UnstableApiUsage*/
    @Override
    public void exitToMainMenu() {
        if (exitToMainMenuAsyncTask == null) {
            MainApp.getGame().getEventBus().post(new ClientAPI.LeaveSessionEvent());
            exitToMainMenuReady = false;
            exitToMainMenuAsyncTask = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    NetManager.getSingleton().disconnectFromServer();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    exitToMainMenuReady = true;
                    exitToMainMenu();
                    NetManager.getSingleton().closeClient();
                    exitToMainMenuAsyncTask = null;
                }
            };
            exitToMainMenuAsyncTask.execute();
        } else {
            if (exitToMainMenuReady) {
                super.exitToMainMenu();
            }
        }


    }

    @Override
    public void reloadLevelDataAndUpdateCurrentEntities() {

    }

    @Override
    public void destroyEntities() {
        gameWorld.getSystem(EntityDeleterSystem.class).process();
        gameWorld.getSystem(StaticEntityDeleterSystem.class).process();
    }

    public UserStatsList getUserStatsList() {
        return userStatsList;
    }

    public void setUserStatsList(UserStatsList userStatsList) {
        this.userStatsList = userStatsList;
    }

    //    public MultiplayerServerFrameTCP getCurrentServerFrameTCP() {
//        return currentServerFrameTCP;
//    }
//
//    public void setCurrentServerFrameTCP(MultiplayerServerFrameTCP currentServerFrameTCP) {
//        this.currentServerFrameTCP = currentServerFrameTCP;
//    }
//
//    public MultiplayerServerFrameUDP getCurrentServerFrameUDP() {
//        return currentServerFrameUDP;
//    }
//
//    public void setCurrentServerFrameUDP(MultiplayerServerFrameUDP currentServerFrameUDP) {
//        this.currentServerFrameUDP = currentServerFrameUDP;
//    }
}
