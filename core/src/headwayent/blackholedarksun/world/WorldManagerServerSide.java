/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/20/21, 2:38 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.esotericsoftware.kryonet.Connection;

import org.apache.commons.io.FilenameUtils;

import headwayent.blackholedarksun.*;
import headwayent.blackholedarksun.animations.*;
import headwayent.blackholedarksun.components.*;
import headwayent.blackholedarksun.entitydata.AsteroidData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.levelresource.Level;
import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.levelresource.LevelSpawnPoint;
import headwayent.blackholedarksun.menusystemsimpleview.SimpleViewGameMenuManager;
import headwayent.blackholedarksun.multiplayer.*;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityUDP;
import headwayent.blackholedarksun.multiplayer.components.PlayerState;
import headwayent.blackholedarksun.multiplayer.rmi.UserStatsListImpl;
import headwayent.blackholedarksun.multiplayer.systems.*;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.registeredclasses.JoinServerConnectionRequest;
import headwayent.blackholedarksun.net.registeredclasses.ServerConnectionResponse;
import headwayent.blackholedarksun.net.registeredclasses.ServerRespawnRequest;
import headwayent.blackholedarksun.physics.EntityMotionState;
import headwayent.blackholedarksun.physics.PhysicsProperties;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.blackholedarksun.systems.FollowingShipCounterResetSystem;
import headwayent.blackholedarksun.systems.MovementSystem;
import headwayent.blackholedarksun.systems.helper.ai.skynet.SquadManager;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_Workflows;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

/**
 * Created by sebas on 05.11.2015.
 */
public class WorldManagerServerSide extends WorldManagerBase {

    private static final int FRAMES_AHEAD = 3;
//    private static WorldManagerServerSide mgr;
    private final ReentrantLock serverThreadLock = new ReentrantLock();
    //    private ClientListener clientListener = new ClientListener(this);
    private final LinkedList<MultiplayerClientFrame> clientFrames = new LinkedList<>();
    private final HashMap<Long, PlayerState> playerStateMap = new HashMap<>();
    private final HashMap<Long, PlayerRespawnData> playerRespawnDataMap = new HashMap<>();
    private final ArrayList<Long> playerStateToRemove = new ArrayList<>();
    private final HashMap<Long, MultiplayerServerFrameTCP> tcpFramesMap = new HashMap<>();
    private final HashMap<Long, MultiplayerServerFrameUDP> udpFramesMap = new HashMap<>();
//    private HashMap<Long, Long> nextFramesTCPMap = new HashMap<>();
//    private HashMap<Long, Long> nextFramesUDPMap = new HashMap<>();
    private final ArrayList<MultiplayerServerFrameTCP> tcpFramesList = new ArrayList<>();
    private final ArrayList<MultiplayerServerFrameUDP> udpFramesList = new ArrayList<>();
//    private final HashSet<Long> clientSet = new HashSet<>();
//    private final HashSet<Long> receivedCurrentFrameClientData = new HashSet<>(); // We only care about UDP data.
//    private final HashMap<Long, ENG_Integer> clientDroppedFramesMap = new HashMap<>();

//    private HashMap<Long, MultiplayerEntityHolder> multiplayerEntityMap = new HashMap<>();
    private final HashMap<Long, Entity> entityByUserIdMap = new HashMap<>();
//    private final HashSet<Long> uninitializedClientSet = new HashSet<>();
    private final HashMap<Long, HashMap<Long, Long>> userIdToServerProjectileIdToClientProjectileId = new HashMap<>();
    private final HashMap<Long, HashMap<Long, Long>> userIdToClientProjectileIdToServerProjectileId = new HashMap<>();

    // We need a separate MultiplayerEntityUDP that is used only by the client whose entityId we transformed from the server to the client.
    private final HashMap<Long, HashMap<Long, MultiplayerEntityTCP>> userIdToServerProjectileIdToEntityTCP = new HashMap<>();
    private final HashMap<Long, HashMap<Long, MultiplayerEntityUDP>> userIdToServerProjectileIdToEntityUDP = new HashMap<>();

    // Current frame data.
    private final ArrayList<AddedPlayer> addedPlayerList = new ArrayList<>();
    private final ArrayList<Long> removedPlayerList = new ArrayList<>();
    private final ArrayList<ServerConnectionResponse> connectionResponseList = new ArrayList<>();
    private final ArrayList<MultiplayerClientFrameTCP> clientFrameTCPList = new ArrayList<>();
    private final ArrayList<MultiplayerClientFrameUDP> clientFrameUDPList = new ArrayList<>();
    private final ArrayList<ServerRespawnRequest> serverRespawnRequestList = new ArrayList<>();
    private MultiplayerEntityTCP currentProjectile;
    private MultiplayerClientFrameTCP currentMultiplayerClientFrameTCP;
    private PlayerState currentPlayerState;
    private final ReentrantLock userStatsUpdaterSystemLock = new ReentrantLock();
    private final UserStatsListImpl userStatsList = new UserStatsListImpl();
    private final HashMap<Long, Integer> userIdToPingMap = new HashMap<>();
    private LevelEvent.EventState endLevelState;
    private boolean shouldBreak;

    public WorldManagerServerSide() {
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        mgr = this;
    }

    public static WorldManagerServerSide getSingleton() {
//        return mgr;
        return (WorldManagerServerSide) MainApp.getGame().getWorldManager();
    }

    @Override
    void resetWorld() {
//        entityMap.clear();
        resetIdCounter();
//        movableObjectsToEntities.clear();
//        movableObjectsToShips.clear();
        levelEventList.clear();
        levelObjectToEntityMap.clear();
//        for (Entity e : entityList) {
//            // removeRemovableEntities(entityList);
//            e.deleteFromWorld();
//        }
//        entityList.clear();
//        finalStateList.clear();
        setLevelState(LevelState.NONE);
        cargoIdList.clear();
        cargoNameToIdMap.clear();
        cutsceneMap.clear();
//        availableNameList.clear();
        gameWorld.getSystem(MovementSystem.class).destroyThreads();
        SquadManager.getInstance().reset();
    }

    @Override
    void prepareLevel() {
        Level level = (Level) gameWorld.getCurrentLevel();
        initializeEndEvents(level);
    }

    @Override
    protected void updateLevelEvents() {
        super.updateLevelEvents();
        Level level = (Level) gameWorld.getCurrentLevel();
        if (level.cutsceneActive) {
            return;
        }
        if (level.levelEndedSent) {
            endLevel(level, endLevelState);
        }
    }

    @Override
    public void update(long currentTime) {
        if (levelState == LevelState.STARTED) {



            processAddedPlayers();

            processRemovedPlayers();

            processRespawnRequests();

            processUpdates();
            gameWorld.getSystem(DataReceiverSystem.class).process();

            gameWorld.getSystem(FollowingShipCounterResetSystem.class).process();
            gameWorld.getSystem(FollowingShipCounterServerSideSystem.class).process();

//            updateProjectiles();
            gameWorld.getSystem(ProjectileUpdateServerSideSystem.class).process();

            updateLevelEvents();

            // If we set the level to null we can't go on with DataSenderSystem.
            if (shouldBreak) {
                return;
            }

//            gameWorld.getSystem(MultiplayerEntityProcessingServerSystem.class).process();

//            sendUpdates();
            gameWorld.getSystem(DataSenderSystem.class).process();

            updateUserStats();

            gameWorld.getSystem(GameLogicEntityRemoverServerSideSystem.class).process();

            updateAnimations();

//            clearCurrentFrameLists();

            checkLatency();
        }
    }

    private void updateUserStats() {
        userStatsUpdaterSystemLock.lock();
        try {
//                userStatsList.clearUserStats();
            gameWorld.getSystem(UserStatsUpdaterSystem.class).process();
        } finally {
            userStatsUpdaterSystemLock.unlock();
        }
    }

    private void checkLatency() {
        for (PlayerState playerState : playerStateMap.values()) {
            Connection connection = playerState.getConnection();
            if (connection.isConnected()) {
                connection.updateReturnTripTime();
                userIdToPingMap.put(playerState.getUserId(), connection.getReturnTripTime());
            }
        }

    }

    public HashMap<Long, PlayerState> getPlayerStateMap() {
        return playerStateMap;
    }

    public HashMap<Long, PlayerRespawnData> getPlayerRespawnDataMap() {
        return playerRespawnDataMap;
    }

    public UserStatsListImpl getUserStatsList() {
        return userStatsList;
    }

    public ReentrantLock getUserStatsUpdaterSystemLock() {
        return userStatsUpdaterSystemLock;
    }

    //    @Override
//    protected void updateProjectiles() {
//        for (Entity entity : projectileList) {
//            ProjectileProperties projectileProperties = entity.getComponent(ProjectileProperties.class);
//            if (projectileProperties != null) {
//                Entity ship = getEntityFromLevelObjectEntityId(projectileProperties.getParentName());
//                if (ship != null) {
//                    WeaponProperties launcher = ship.getComponent(WeaponProperties.class);
//                    ShipProperties shipProperties = ship.getComponent(ShipProperties.class);
//                    EntityProperties projectileEntityProperties = entity.getComponent(EntityProperties.class);
//                    if (launcher != null && entity.getComponent(EntityProperties.class).isDestroyed()) {
//                        // System.out.println("removing weapon id " + id +
//                        // " from ship: " +
//                        // getEntityFromLevelObjectEntityId(
//                        // projectileProperties.getParentName())
//                        // .getComponent(ShipProperties.class).getName());
//                        launcher.removeId(projectileProperties.getType(), projectileProperties.getId());
////                        if (entityByUserIdMap.containsKey(shipProperties.getUserId())) {
////                            userIdToServerProjectileIdToEntityUDP.get(shipProperties.getUserId()).remove(projectileEntityProperties.getEntityId());
////                        }
//                        // removeFromWorld(entity, false);
//                        entitiesToRemove.add(entity);
//                    }
//                }
//            }
//        }
//        clearEntitiesFromList(projectileList);
//    }

    private void processUpdates() {
        ArrayList<MultiplayerClientFrameTCP> multiplayerClientFrameTCPs = new ArrayList<>();
        ArrayList<MultiplayerClientFrameUDP> multiplayerClientFrameUDPs = new ArrayList<>();
        serverThreadLock.lock();
        try {
            multiplayerClientFrameTCPs.addAll(clientFrameTCPList);
            clientFrameTCPList.clear();
            multiplayerClientFrameUDPs.addAll(clientFrameUDPList);
            clientFrameUDPList.clear();
        } finally {
            serverThreadLock.unlock();
        }
        if (MainApp.getMainThread().isInputState()) {
            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            multiplayerClientFrameTCPs.addAll(currentFrameInterval.getTcpClientFrames());
            multiplayerClientFrameUDPs.addAll(currentFrameInterval.getUdpClientFrames());
        }
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            currentFrameInterval.getTcpClientFrames().addAll(multiplayerClientFrameTCPs);
            currentFrameInterval.getUdpClientFrames().addAll(multiplayerClientFrameUDPs);
        }
        DataReceiverSystem dataReceiverSystem = gameWorld.getSystem(DataReceiverSystem.class);
        dataReceiverSystem.addMultiplayerClientFrameTCPs(multiplayerClientFrameTCPs);
        dataReceiverSystem.addMultiplayerClientFrameUDPs(multiplayerClientFrameUDPs);
//        processTCPUpdates(multiplayerClientFrameTCPs);
//        processUDPUpdates(multiplayerClientFrameUDPs);
    }

//    private void processUDPUpdates(ArrayList<MultiplayerClientFrameUDP> multiplayerClientFrameUDPs) {
//        for (MultiplayerClientFrameUDP multiplayerClientFrameUDP : multiplayerClientFrameUDPs) {
//            addToReceivedCurrentFrameClientData(multiplayerClientFrameUDP);
//        }
//
//    }

//    private void processTCPUpdates(ArrayList<MultiplayerClientFrameTCP> multiplayerClientFrameTCPs) {
//        for (MultiplayerClientFrameTCP multiplayerClientFrameTCP : multiplayerClientFrameTCPs) {
//            long userId = multiplayerClientFrameTCP.getUserId();
//            Entity entity = getEntityByUserId(userId);
//            uninitializedClientSet.remove(userId);
//            currentMultiplayerClientFrameTCP = multiplayerClientFrameTCP;
//            for (int i = 0; i < multiplayerClientFrameTCP.getProjectileListSize(); ++i) {
//                currentProjectile = multiplayerClientFrameTCP.getProjectile(i);
//                createProjectile(entity);
//            }
//        }
//        currentProjectile = null;
//    }

    public Entity getEntityByUserId(long userId) {
        return getEntityByUserId(userId, true);
    }

    public Entity getEntityByUserId(long userId, boolean throwException) {
        Entity entity = entityByUserIdMap.get(userId);
        if (entity == null && throwException) {
            throw new IllegalArgumentException("userId: " + userId + " is not in the entityList");
        }
        return entity;
    }

//    private MultiplayerEntityHolder getMultiplayerEntityHolder(long userId) {
//        MultiplayerEntityHolder multiplayerEntityHolder = multiplayerEntityByUserIdMap.get(userId);
//        if (multiplayerEntityHolder == null) {
//            throw new IllegalArgumentException("userId: " + userId + " is not in the multiplayerEntityList");
//        }
//        return multiplayerEntityHolder;
//    }

//    private void clearCurrentFrameLists() {
//        clearFrameLists();
////        multiplayerEntitiesToRemove.clear();
//    }

//    private void sendUpdates() {
//        ArrayList<MultiplayerEntityHolder> multiplayerEntities = new ArrayList<>();
//        for (Entity entity : entityList) {
//            EntityProperties entityProperties = entity.getComponent(EntityProperties.class);
//            ShipProperties shipProperties = entity.getComponent(ShipProperties.class);
//            MultiplayerEntityHolder multiplayerEntity = new MultiplayerEntityHolder();
//            multiplayerEntity.setEntityProperties(entityProperties);
//            multiplayerEntity.setShipProperties(shipProperties);
//            multiplayerEntities.add(multiplayerEntity);
//        }
        // For just added entities we must send the whole game state
//        for (MultiplayerEntityHolder multiplayerEntityHolder : addedShipMultiplayerEntities) {
//            long userId = multiplayerEntityHolder.getMultiplayerEntityTCP().getShipProperties().getUserId();
//            // We send the data to the client for it to start loading the level. Since that will probably take more than one frame
//            // then we shouldn't consider this as dropped frames
//            uninitializedClientSet.add(userId);
//            PlayerState playerState = playerStateMap.get(userId);
//            MultiplayerServerFrameTCP tcpFrame = getTCPFrame(playerState.getUserId());
//            if (tcpFrame.getErrorCode() == MultiplayerFrame.NO_ERROR) {
//                for (MultiplayerEntityHolder otherMultiplayerEntities : multiplayerEntityHolderMap.values()) {
//                    // Also send the player self so he will know what position to spawn the client into.
//                    otherMultiplayerEntities.getMultiplayerEntityTCP().getEntityProperties().updateMultiplayerCoordsForSendServerSide();
//                    tcpFrame.addMultiplayerEntityAddedCurrentFrame(otherMultiplayerEntities);
//                }
//            }
//        }
//        ArrayList<MultiplayerEntityTCP> modifiedMultiplayerComponentsList = getModifiedMultiplayerComponentsList();
//        for (PlayerState playerState : playerStateMap.values()) {
//            if (!checkCurrentFrameClientDataReceived(playerState.getUserId())) {
//                // If we haven't received anything from the client there is no point in sending further data.
//                System.out.println("Player: " + playerState.getUserId() + " hasn't sent data");
//                continue;
//            }
//            MultiplayerServerFrameTCP tcpFrame = getTCPFrame(playerState.getUserId());
//            MultiplayerServerFrameUDP udpFrame = getUDPFrame(playerState.getUserId());
//            if (!playerState.isJustAdded()) {
//                for (MultiplayerEntityHolder multiplayerEntityHolder : addedMultiplayerEntities) {
//                    multiplayerEntityHolder.getMultiplayerEntityTCP().getEntityProperties().updateMultiplayerCoordsForSendServerSide();
//                    tcpFrame.addMultiplayerEntityAddedCurrentFrame(multiplayerEntityHolder);
//                }
//            }
//            playerState.setJustAdded(false);
//            for (MultiplayerEntityTCP multiplayerEntityTCP : modifiedMultiplayerComponentsList) {
//                // If it's a projectile and it is this client's then we must convert it to this particular's client entityId.
//                boolean usingClientId = false;
//                HashMap<Long, Long> serverProjectileIdToClientProjectileId = userIdToServerProjectileIdToClientProjectileId.get(playerState.getUserId());
//                if (serverProjectileIdToClientProjectileId != null) {
//                    Long clientProjectileId = serverProjectileIdToClientProjectileId.get(multiplayerEntityTCP.getEntityId());
//                    if (clientProjectileId != null) {
//                        MultiplayerEntityTCP multiplayerEntityTCPClientSpecific = new MultiplayerEntityTCP(clientProjectileId, multiplayerEntityTCP);
//                        tcpFrame.addUpdateEntity(multiplayerEntityTCPClientSpecific);
//                    }
//                }
//                if (!usingClientId) {
//                    tcpFrame.addUpdateEntity(multiplayerEntityTCP);
//                }
//            }
//
////            for (MultiplayerEntityHolder multiplayerEntityHolder : multiplayerEntitiesToRemove) {
////                tcpFrame.addEntityToRemove(multiplayerEntityHolder);
////            }
//            for (MultiplayerEntityHolder multiplayerEntityHolder : currentFrameAddedProjectileList) {
//                boolean usingClientId = false;
//                HashMap<Long, MultiplayerEntityUDP> serverIdToUDPEntityMap = userIdToServerProjectileIdToEntityUDP.get(playerState.getUserId());
//                if (serverIdToUDPEntityMap != null) {
//                    MultiplayerEntityUDP multiplayerEntityUDPClientSpecific =
//                            serverIdToUDPEntityMap.get(multiplayerEntityHolder.getMultiplayerEntityTCP().getEntityId());
//                    if (multiplayerEntityUDPClientSpecific != null) {
//                        multiplayerEntityHolder.getMultiplayerEntityTCP().getEntityProperties().updateMultiplayerCoordsForSendServerSide();
//                        tcpFrame.addClientSpecificProjectileAddedCurrentFrame(multiplayerEntityHolder);
//                        usingClientId = true;
//                    }
//                }
//                if (!usingClientId) {
//                    EntityProperties entityProperties = multiplayerEntityHolder.getMultiplayerEntityTCP().getEntityProperties();
//                    entityProperties.updateMultiplayerCoordsForSendServerSide();
//                    tcpFrame.addProjectileAddedCurrentFrame(multiplayerEntityHolder);
//                    System.out.println("Sending projectile: " + entityProperties.getName() + " with entity id: " + entityProperties.getEntityId() +
//                    " to player: " + playerState.getUserId());
//                }
//            }
//
//            for (Map.Entry<Long, MultiplayerEntityHolder> entry : /*multiplayerEntities*/multiplayerEntityHolderMap.entrySet()) {
//                Long entityId = entry.getKey();
//                MultiplayerEntityHolder multiplayerEntityHolder = entry.getValue();
//                ShipProperties shipProperties = multiplayerEntityHolder.getMultiplayerEntityTCP().getShipProperties();
//                if (shipProperties != null && shipProperties.getUserId() == playerState.getUserId()) {
//                    continue;
//                }
//                MultiplayerEntityUDP multiplayerEntityUDP = multiplayerEntityHolder.getMultiplayerEntityUDP();
//                Entity entity = entityByIdMap.get(multiplayerEntityUDP.getEntityId());
//                EntityProperties entityProperties = entity.getComponent(EntityProperties.class);
//                entityProperties.updateMultiplayerCoordsForSendServerSide();
//                multiplayerEntityUDP.setTranslate(entityProperties.getTranslate());
//                multiplayerEntityUDP.setRotate(entityProperties.getRotate());
//                multiplayerEntityUDP.setVelocity(entityProperties.getVelocityOriginal());
//                udpFrame.addEntity(multiplayerEntityHolder);
//            }
//            for (MultiplayerEntityHolder multiplayerEntityHolder : multiplayerProjectileList) {
//                MultiplayerEntityUDP multiplayerEntityUDP = multiplayerEntityHolder.getMultiplayerEntityUDP();
//
//                // When sending to the projectile update to the projectile's creator we must make sure that we convert it back to
//                // the client's id.
//                boolean usingClientId = false;
//                HashMap<Long, MultiplayerEntityUDP> serverIdToUDPEntityMap = userIdToServerProjectileIdToEntityUDP.get(playerState.getUserId());
//                if (serverIdToUDPEntityMap != null) {
//                    MultiplayerEntityUDP multiplayerEntityUDPClientSpecific = serverIdToUDPEntityMap.get(multiplayerEntityUDP.getEntityId());
//                    if (multiplayerEntityUDPClientSpecific != null) {
//                        // This client is the proper owner of this projectile and we must send it with its id.
//                        multiplayerEntityUDPClientSpecific.set(multiplayerEntityUDP);
//                        udpFrame.addEntity(multiplayerEntityUDPClientSpecific);
//                        usingClientId = true;
//                    }
//                }
//                if (!usingClientId) {
//                    udpFrame.addEntity(multiplayerEntityHolder);
//                }
//            }
//
//
//        }
////        clearInvalidPlayerStates();
//    }



//    private void clearInvalidPlayerStates() {
//        for (Long id : playerStateToRemove) {
//            PlayerState remove = playerStateMap.remove(id);
//            if (remove == null) {
//                throw new IllegalStateException(id + " could not be removed as it's already missing");
//            }
//        }
//
//    }



    //    @Override
//    protected void updateLevelEvents() {
//
//    }

    @Override
    protected void exitObjectsFromLevel(LevelEvent levelEvent) {
        animateShipExit(levelEvent.exitObjects);
    }

    @Override
    protected void spawnObjects(LevelEvent levelEvent) {
//        loadLevelObjects(levelEvent.spawn);

        createEntities(levelEvent.spawn);

        animateShipSpawn(levelEvent.spawn);
    }

    @Override
    protected void endLevel(LevelBase level, LevelEvent.EventState eventState) {
        if (level.levelEndedSent) {
            if (level.userStatsUpdated) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (PlayerState playerState : playerStateMap.values()) {
                    removePlayer(playerState);
                }

                setLevelState(LevelState.ENDED);
                resetWorld();
                // For the light direction to reset to default
                gameWorld.setCurrentLevel(null);
                MainApp.getGame().endGame();

                // This is somewhat of a hack to get out of update() as soon as possible.
                shouldBreak = true;
            } else {
                updateUserStats();
                level.userStatsUpdated = true;
            }
        } else {
            level.levelEnded = true;
            endLevelState = eventState;
        }

    }

    public LevelEvent.EventState getEndLevelState() {
        return endLevelState;
    }

    @Override
    protected void createStaticEntity(String modelName, LevelObject obj) {
        super.createStaticEntity(modelName, obj);
    }

    @Override
    public void createDebris(Entity entity) {
        // Don't create anything server side.
    }

    /** @noinspection deprecation*/
    @Override
    protected void createEntity(String modelName, LevelObject obj) {
        long beginTime = currentTimeMillis();
//        availableNameList.add(obj.name);

        // This is copied from createEntities since multiplayer player ship creation doesn't go through createEntities().
        String extension = FilenameUtils.getExtension(obj.meshName);
        if (extension.isEmpty()) {
            obj.meshName = obj.meshName + ".mesh";
        }

        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(obj.name, gameEntityId),
                ENG_Utility.getUniqueId(), obj.meshName, "", getWorkflow(obj));

        EntityAabb entityAabb = getEntityAabb(obj.meshName);
        System.out.println("centre: " + entityAabb.centre.toString() + " halfSize: " + entityAabb.halfSize.toString());

        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
//        node.attachObject(entity);
        node.attachObject(item);
        Entity gameEntity = gameWorld.createEntity();
//        MultiplayerEntityHolder multiplayerEntityHolder = new MultiplayerEntityHolder();
//        multiplayerEntityUDP.setEntityName(entity.getName());
//        multiplayerEntityHolder.setMultiplayerEntityTCP(multiplayerEntityTCP);
//        multiplayerEntityHolder.setMultiplayerEntityUDP(multiplayerEntityUDP);
//        movableObjectsToEntities.put(entity.getName(), gameEntity);
//        multiplayerEntityMap.put(id, multiplayerEntityHolder);
//        entityList.add(gameEntity);
//        multiplayerEntityHolderMap.put(id, multiplayerEntityHolder);
        EntityProperties entityProp = entityPropertiesComponentMapper.create(gameEntity);
        entityProp.setGameEntity(gameEntity);
        entityProp.setItem(item);
        entityProp.setNode(node);
        entityProp.setEntityId(gameEntityId);
        entityProp.setName(obj.name);
        entityProp.setHealth(obj.health);
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

        MultiplayerEntityTCP multiplayerEntityTCP = addMultiplayerComponentsToEntity(gameEntityId, gameEntity);
        multiplayerEntityTCP.setEntityProperties(entityProp);
        LevelEntity levelEntity = new LevelEntity(gameEntity);

        // Don't add player ships since they are ephemeral.
        if (obj.userId == 0) {
            addLevelObjectByName(obj.name, levelEntity);
            addLevelObjectById(gameEntityId, levelEntity);
        }
//        addLevelObjectById(id, levelEntity);

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
            entityProp.setDestroyedAnimation(new ExplosionWithoutRenderingAnimation(
                    "ExplosionAnimation " + node.getName(), gameEntity, ExplosionAnimation.EXPLOSION_SMALL_MAT, 3.0f));
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
            headwayent.blackholedarksun.entitydata.ShipData shipData = MainApp.getGame().getNameToShipMap(modelName);
            entityData = shipData;
            entityProp.setHealth(shipData.health); // We also have armor for the future.
            entityProp.setMaxSpeed(shipData.maxSpeed);
            gameWorld.getManager(GroupManager.class).add(gameEntity, shipData.team.toString());
            setShipData(shipProp, shipData);
//            gameEntity.addComponent(shipProp);
            setShipWeapons(gameEntity, shipData);

            motionState = new EntityMotionState(entityProp, shipProp);
            collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
            collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

            // Must create the ship properties before setting up an explosion
            // animation
            if (obj.type == LevelObject.LevelObjectType.FIGHTER_SHIP) {
                entityProp.setDestroyedAnimation(new ShipExplosionWithoutRenderingAnimation(
                        "ShipExplosionAnimation " + obj.name + " entityId " + entityProp.getEntityId(), gameEntity));
            } else if (obj.type == LevelObject.LevelObjectType.CARGO_SHIP) {
                entityProp.setDestroyedAnimation(new CargoShipExplosionWithoutRenderingAnimation(
                        "CargoShipExplosionAnimation " + obj.name + " entityId " + entityProp.getEntityId(), gameEntity));
            }
            shipProp.setCountermeasuresAnimationFactory(new CountermeasuresWithoutRenderingAnimationFactory(
                    "CountermeasureAnimation " + entityProp.getName() + " entityId " + entityProp.getEntityId()));
            shipProp.setAiEnabled(obj.ai);
//            entityMap.put(id, gameEntity);
            shipProp.setShipDataModelName(shipData.inGameName);
            multiplayerEntityTCP.setShipProperties(shipProp);
            if (obj.userId != 0) {
                // We have a player ship
                // We must send a whole world snapshot to the added player since he cannot know the current game state by deriving from the locally loaded map.
//                addedShipMultiplayerEntities.add(multiplayerEntityHolder);
//                multiplayerEntityByUserIdMap.put(obj.userId, multiplayerEntityHolder);
                // Needed for projectile creation by clients.
                // The clients create the projectile (for now) and send the projectile entity to us.
                // Here we need to extract the game entity since that is what we need for createProjectile(Entity e).
                entityByUserIdMap.put(obj.userId, gameEntity);
                PlayerState playerState = playerStateComponentMapper.create(gameEntity);
                playerState.set(currentPlayerState);
                // Overwrite the current playerState from the map since that one will have justAdded == true forever.
                playerStateMap.put(obj.userId, playerState);
//                gameEntity.addComponent(currentPlayerState);
            }
//            ++id;
        }
        if (obj.ai) {
            aiProperties = addAIProperties(gameEntity, entityProp, obj);

        }


//        gameWorld.addEntity(gameEntity);

//        addedMultiplayerEntities.add(multiplayerEntityHolder);
        addEntityByGameEntityId(gameEntityId, gameEntity);
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(gameEntityId, item.getId());

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

        System.out.println("Created entity: " + entityProp.getName() + " with entity id: " + entityProp.getEntityId());

        incrementGameEntityId();
    }

    @Override
    public void loadLevel() {
        Level level = (Level) gameWorld.getCurrentLevel();

//        loadReloader(level);
//        loadLevelObjects(level.levelStart.startObjects);
        // Preload all the possible ships that are allowed for this level.
//        loadLevelObjects(level.levelStart.playerShipSelectionObjects);

        createEntities(level.levelStart.startObjects);

        prepareLevel();
        loadWaypoints();
        setLevelState(LevelState.STARTED);

        // We need to pass the WorldManagerServerSide in order to be able to receive further requests from clients.
        // Basically after the server is created all the messages are routed through this class instead of the ClientListener.
        // The same idea applies to the ServerListener.
        if (!MainApp.getMainThread().isInputState()) {
            MainApp.getMainThread().getApplicationSettings().clientListener.setWorldManagerServerSide(this);
        }

//        listen();
    }

    @Override
    protected void animateShipSpawn(Entity entity) {
        ShipProperties shipProperties = getShipPropertiesComponentMapper().getSafe(entity);
        if (shipProperties != null) {

            EntityProperties entityProperties = getEntityPropertiesComponentMapper().get(entity);
            PortalEnteringWithoutRenderingAnimation anim = new PortalEnteringWithoutRenderingAnimation("PortalEnteringWithoutRenderingAnimation "
                    + entityProperties.getUniqueName(), entity);
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
                PortalExitingWithoutRenderingAnimation anim =
                        new PortalExitingWithoutRenderingAnimation("PortalExitingWithoutRenderingAnimation " + entityProperties.getUniqueName(), entity);
                Utility.clearAngularVelocity(entityProperties.getRigidBody());
                startAnimation(entityProperties.getEntityId(), anim);
            }
        }
    }

    public void exitGame() {
        SimpleViewGameMenuManager.exitGame();
    }

//    private void listen() {
//        MainApp.getMainThread().getApplicationSettings().server.addListener(clientListener);
//    }

    private static class UserIdToServerId {
        public long userId, serverId;

        public UserIdToServerId() {

        }

        public UserIdToServerId(long userId, long serverId) {
            this.userId = userId;
            this.serverId = serverId;
        }
    }

    @Override
    public void onRemoveRemovableEntity(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties) {
        if (shipProperties != null) {
            entityByUserIdMap.remove(shipProperties.getUserId());
        }
        ArrayList<UserIdToServerId> userIdToServerIdsToRemove = new ArrayList<>();
        for (Map.Entry<Long, HashMap<Long, Long>> entry : userIdToServerProjectileIdToClientProjectileId.entrySet()) {
            Long userId = entry.getKey();
            for (Map.Entry<Long, Long> serverIdToClientId : entry.getValue().entrySet()) {
                if (serverIdToClientId.getKey() == entityProperties.getEntityId()) {
                    // We must remove this.
                    userIdToServerIdsToRemove.add(new UserIdToServerId(userId, serverIdToClientId.getKey()));
                }
            }
        }
        for (UserIdToServerId userIdToServerId : userIdToServerIdsToRemove) {
            HashMap<Long, Long> serverIdToClientId = userIdToServerProjectileIdToClientProjectileId.get(userIdToServerId.userId);
            serverIdToClientId.remove(userIdToServerId.serverId);
            if (serverIdToClientId.isEmpty()) {
                userIdToServerProjectileIdToClientProjectileId.remove(userIdToServerId.userId);
            }
        }

        ArrayList<UserIdToServerId> userIdToClientIdsToRemove = new ArrayList<>();
        for (Map.Entry<Long, HashMap<Long, Long>> entry : userIdToClientProjectileIdToServerProjectileId.entrySet()) {
            Long userId = entry.getKey();
            for (Map.Entry<Long, Long> clientIdToServerId : entry.getValue().entrySet()) {
                if (clientIdToServerId.getValue() == entityProperties.getEntityId()) {
                    // We must remove this.
                    userIdToClientIdsToRemove.add(new UserIdToServerId(userId, clientIdToServerId.getKey()));
                }
            }
        }
        for (UserIdToServerId userIdToServerId : userIdToClientIdsToRemove) {
            HashMap<Long, Long> clientIdToServerId = userIdToClientProjectileIdToServerProjectileId.get(userIdToServerId.userId);
            clientIdToServerId.remove(userIdToServerId.serverId);
            if (clientIdToServerId.isEmpty()) {
                userIdToClientProjectileIdToServerProjectileId.remove(userIdToServerId.userId);
            }
        }


//        removeMultiplayerEntity(entityProperties.getEntityId());
    }

//    private void removeMultiplayerEntity(Long entityId) {
//        MultiplayerEntityHolder remove = multiplayerEntityHolderMap.remove(entityId);
//        if (remove == null) {
//            throw new IllegalArgumentException("Invalid ship name: " + entityId);
//        }
//        multiplayerEntitiesToRemove.add(remove);
//    }

//    @Override
//    public void startAnimation(Animation anim) {
//
//    }

    public void createProjectile(Entity playerShip, MultiplayerEntityTCP currentProjectile) {
        this.currentProjectile = currentProjectile;
        createProjectile(playerShip);
    }

    @Override
    public void createProjectile(Entity ship) {
        // We must determine if the projectile has been created by the server (for example the AI) or if the projectile was
        // created by a client. If it was created by a client we must take the properties from the received data.
        // Since the createProjectile() method only passes an entity ship we will use a hack by passing the data as private fields.
        if (currentProjectile != null) {
            // The projectile was created on the client.
            createProjectileEntityForClient(ship, currentProjectile, currentMultiplayerClientFrameTCP);
        } else {
            // AI created projectile.
            createProjectileByServer(ship);
        }
    }

    private void addPropertiesToProjectileMultiplayerEntityHolder(long entityId, Entity gameEntity, EntityProperties entityProperties,
                                                                  ProjectileProperties projectileProperties, TrackerProperties trackerProperties) {
//        MultiplayerEntityHolder multiplayerEntityHolder = new MultiplayerEntityHolder();
        MultiplayerEntityTCP multiplayerEntityTCP = addMultiplayerComponentsToEntity(entityId, gameEntity);
//        multiplayerEntityUDP.setEntityName(entityProperties.getName());
//        multiplayerEntityHolder.setMultiplayerEntityTCP(multiplayerEntityTCP);
//        multiplayerEntityHolder.setMultiplayerEntityUDP(multiplayerEntityUDP);
        multiplayerEntityTCP.setEntityProperties(entityProperties);
        multiplayerEntityTCP.setProjectileProperties(projectileProperties);



//        multiplayerProjectileList.add(multiplayerEntityHolder);
//        currentFrameAddedProjectileList.add(multiplayerEntityHolder);
//        multiplayerEntityHolderMap.put(id, multiplayerEntityHolder);
    }

    @Override
    protected Entity createProjectileEntity(long nextId, //WeaponProperties weaponProperties,
                                          EntityProperties entityProperties,
                                          ShipProperties shipProperties,
//                                          WeaponData.WeaponType weaponType,
                                          String meshName,
                                          headwayent.blackholedarksun.entitydata.WeaponData weaponData,
                                          ENG_Vector4D pos,
                                          ENG_Quaternion orientation,
                                          boolean tracking) {
        // Sometimes this bug may happen:
        // A ship gets destroyed and all its chasing projectiles get removed
        // but in the same frame, just before the entity remover gets to
        // remove the ship from the scene, a new projectile gets added in
        // the ship's chasingprojectilelist. So don't create tracking weapons
        // after a ship's death
        if (tracking) {
            if (checkShipDestroyed(shipProperties)) return null;
        }
//        int nextId = weaponProperties.getNextId();
        String name = entityProperties.getName() + "_" + headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getWeapon(weaponData.weaponType) + nextId;
        System.out.println("creating projectile: " + name);
        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(name, gameEntityId),
                ENG_Utility.getUniqueId(), weaponData.filename, "", ENG_Workflows.MetallicWorkflow);
//        ENG_Entity entity = sceneManager.createEntity(EntityProperties.generateUniqueName(name, gameEntityId), gameEntityId, meshName, ENTITY_GROUP_NAME);
        EntityAabb entityAabb = getEntityAabb(weaponData.filename);
        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
        node.attachObject(item);

        Entity gameEntity = gameWorld.createEntity();

//        movableObjectsToEntities.put(entity.getName(), gameEntity);
//        entityList.add(gameEntity);
//        projectileList.add(gameEntity);

        EntityProperties entityProp = entityPropertiesComponentMapper.create(gameEntity);
        entityProp.setGameEntity(gameEntity);
//        entityProp.setEntity(entity);
        entityProp.setItem(item);
        entityProp.setNode(node);
        entityProp.setEntityId(gameEntityId);
        entityProp.setName(name);
        addEntityByGameEntityId(gameEntityId, gameEntity);
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(gameEntityId, item.getId());
        entityProp.setOrientation(orientation);
        entityProp.setPosition(pos);

        node.setPosition(pos.x, pos.y, pos.z);
        node.setOrientation(orientation);
        node._updateWithoutBoundsUpdate(false, false);

        ProjectileProperties projectileProp = projectilePropertiesComponentMapper.create(gameEntity);
        projectileProp.setType(weaponData.weaponType);
        projectileProp.setParentName(entityProperties.getName());
        projectileProp.setParentId(entityProperties.getEntityId());
        projectileProp.setId(nextId);
//        gameEntity.addComponent(entityProp);
//        gameEntity.addComponent(projectileProp);
        entityProp.setDamage(weaponData.damage);
        entityProp.setHealth(weaponData.health);
        entityProp.setWeight(weaponData.weight);
        entityProp.setVelocity(weaponData.maxSpeed);
        entityProp.setMaxSpeed(weaponData.maxSpeed);
//        entityProp.setDestructionSoundName(WeaponData.WeaponType.getProjectileHitSoundName(weaponData.weaponType));
        entityProp.setDestroyedAnimation(new ProjectileExplosionWithoutRenderingAnimation(item.getName(), gameEntity));

        TrackerProperties trackerProperties = null;
        if (tracking) {
            entityProp.setOnDestroyedEvent(new headwayent.blackholedarksun.entitydata.WeaponData.WeaponOnDestroyedEvent(gameEntity));
            long currentSelectedEnemy = shipProperties.getCurrentSelectedEnemy();
            System.out.println("currentSelectedEnemy: " + currentSelectedEnemy);
            if (currentSelectedEnemy != -1) {
                trackerProperties = trackerPropertiesComponentMapper.create(gameEntity);
                trackerProperties.setTrackedEntityId(currentSelectedEnemy);
                trackerProperties.setMaxAngularVelocity(weaponData.maxAngularVelocity);
                trackerProperties.setTrackingDelay(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getHomingMissileTrackingDelay(weaponData.weaponType));
                trackerProperties.setTrackingDelayTimeStarted();
                Entity ship = getEntityByItemId(currentSelectedEnemy);
                if (ship != null) {
                    System.out.println("Adding chasing projectile to ship");
                    shipPropertiesComponentMapper.get(ship).addChasingProjectile(gameEntityId);
                } else {
                    System.out.println("Could not add chasing projectile since ship is null");
                }
            }
        }
//        gameWorld.addEntity(gameEntity);
        addPropertiesToProjectileMultiplayerEntityHolder(gameEntityId, gameEntity, entityProp, projectileProp, trackerProperties);

        EntityMotionState motionState = new EntityMotionState(entityProp, projectileProp, trackerProperties);
        short collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
        short collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

        float weight = entityProp.getWeight();

        createPhysicsBody(gameEntity, entityAabb.halfSize, entityProp, motionState, collisionGroup, collisionMask, weight);

        // Disable collision between the ship and the just launched projectile.
        entityProp.getRigidBody().setIgnoreCollisionCheck(entityProperties.getRigidBody(), true);

        incrementGameEntityId();

        System.out.println("Created projectile: " + entityProp.getName() + " with entity id: " + entityProp.getEntityId());
        return gameEntity;
    }

    private void createProjectileByServer(Entity ship) {
        createProjectileFromShip(ship, null);
    }


//    private class ProjectileIdMapping {
//        public long clientId, serverId;
//    }

    public Long getClientId(long userId, long serverId) {
        HashMap<Long, Long> serverProjectileToClientId = userIdToServerProjectileIdToClientProjectileId.get(userId);
        if (serverProjectileToClientId != null) {
            return serverProjectileToClientId.get(serverId);
        }
        return null;
    }

    /**
     * We have to map between the id of the projectile when it was created by the client and sent to the server.
     * The server will have a different id for that projectile but when we will send it back to the client the client must receive its id, not
     * the server's.
     * @noinspection deprecation
     */
    private void createProjectileEntityForClient(Entity ship, MultiplayerEntityTCP clientProjectile, MultiplayerClientFrameTCP currentMultiplayerClientFrameTCP) {
        EntityProperties clientEntityProperties = clientProjectile.getEntityProperties();
        ProjectileProperties clientProjectileProperties = clientProjectile.getProjectileProperties();
        TrackerProperties clientTrackerProperties = clientProjectile.getTrackerProperties();
        EntityProperties entityProperties = entityPropertiesComponentMapper.get(ship);
        ShipProperties shipProperties = shipPropertiesComponentMapper.get(ship);
        headwayent.blackholedarksun.entitydata.WeaponData weaponData = WeaponData.getWeaponData(clientProjectileProperties.getType());
//        ENG_ModelResource weaponResource = MainApp.getGame().getWeaponResource(clientProjectileProperties.getType());

        // Could it happen that the ship has been destroyed on the server, but the client doesn't know it yet
        // and sends us a new createProjectile message which we can't execute since the ship is dead here???

        Entity serverEntity = entityByUserIdMap.get(shipProperties.getUserId());
        if (serverEntity == null) {
            // TODO REMOVE THIS AFTER TESTING!!!
            throw new IllegalArgumentException("Invalid ship user id: " + shipProperties.getUserId());
        }
        WeaponProperties serverWeaponProperties = weaponPropertiesComponentMapper.get(serverEntity);
        long nextId = serverWeaponProperties.getNextId();//clientProjectileProperties.getId();
        String name = entityProperties.getName() + "_" + headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getWeapon(clientProjectileProperties.getType()) + "_" + nextId;
        System.out.println("creating projectile: " + name);
        ENG_Item item = sceneManager.createItem(EntityProperties.generateUniqueName(name, gameEntityId),
                ENG_Utility.getUniqueId(), weaponData.filename, "", ENG_Workflows.MetallicWorkflow);
//        ENG_Entity entity = sceneManager.createEntity(EntityProperties.generateUniqueName(name, gameEntityId), gameEntityId, weaponData.filename, ENTITY_GROUP_NAME);
        EntityAabb entityAabb = getEntityAabb(weaponData.filename);
        ENG_SceneNode node = sceneManager.getRootSceneNode().createChildSceneNode(item.getName());
        node.attachObject(item);

        Entity gameEntity = gameWorld.createEntity();
//        movableObjectsToEntities.put(entity.getName(), gameEntity);
//        entityList.add(gameEntity);
//        projectileList.add(gameEntity);

        EntityProperties entityProp = entityPropertiesComponentMapper.create(gameEntity);
        entityProp.setGameEntity(gameEntity);
//        entityProp.setEntity(entity);
        entityProp.setItem(item);
        entityProp.setNode(node);
        entityProp.setEntityId(gameEntityId);
        entityProp.setName(name);
        addEntityByGameEntityId(gameEntityId, gameEntity);
        addEntityByItemId(item.getId(), gameEntity);
        addItemIdByEntityId(gameEntityId, item.getId());

        // Add client specific entity id translations.
        HashMap<Long, Long> serverToClientProjectileIdMap = userIdToServerProjectileIdToClientProjectileId.get(shipProperties.getUserId());
        if (serverToClientProjectileIdMap == null) {
            serverToClientProjectileIdMap = new HashMap<>();
            userIdToServerProjectileIdToClientProjectileId.put(shipProperties.getUserId(), serverToClientProjectileIdMap);
        }
        serverToClientProjectileIdMap.put(gameEntityId, clientEntityProperties.getEntityId());
        HashMap<Long, Long> clientToServerProjectileIdMap = userIdToClientProjectileIdToServerProjectileId.get(shipProperties.getUserId());
        if (clientToServerProjectileIdMap == null) {
            clientToServerProjectileIdMap = new HashMap<>();
            userIdToClientProjectileIdToServerProjectileId.put(shipProperties.getUserId(), clientToServerProjectileIdMap);
        }
        clientToServerProjectileIdMap.put(clientEntityProperties.getEntityId(), gameEntityId);
//        HashMap<Long, MultiplayerEntityTCP> serverIdToEntityTCP = userIdToServerProjectileIdToEntityTCP.get(shipProperties.getUserId());
//        if (serverIdToEntityTCP == null) {
//            serverIdToEntityTCP = new HashMap<>();
//            userIdToServerProjectileIdToEntityTCP.put(shipProperties.getUserId(), serverIdToEntityTCP);
//        }
//        serverIdToEntityTCP.put(id, new MultiplayerEntityTCP(clientEntityProperties.getEntityId(), entity.getName()));

//        HashMap<Long, MultiplayerEntityUDP> serverIdToEntityUDP = userIdToServerProjectileIdToEntityUDP.get(shipProperties.getUserId());
//        if (serverIdToEntityUDP == null) {
//            serverIdToEntityUDP = new HashMap<>();
//            userIdToServerProjectileIdToEntityUDP.put(shipProperties.getUserId(), serverIdToEntityUDP);
//        }
//        serverIdToEntityUDP.put(id, new MultiplayerEntityUDP(clientEntityProperties.getEntityId(), entity.getName()));


        // Use the same vars as in WorldManagerSP.
//        clientEntityProperties.updateMultiplayerCoordsForSendServerSide();
        clientEntityProperties.getRotate(currentShipOrientation);
        clientEntityProperties.getTranslate(currentShipPosition);
        clientEntityProperties.getVelocityAsVec(currentShipVelocity);
        entityProp.setOrientation(currentShipOrientation);
        entityProp.setPosition(currentShipPosition);

        // Try to pre-advance the projectile based on lag.
        preadvanceProjectile(shipProperties, node, entityProp);

        node.setPosition(currentShipPosition.x, currentShipPosition.y, currentShipPosition.z);
        node.setOrientation(currentShipOrientation);
        node._updateWithoutBoundsUpdate(false, false);

        ProjectileProperties projectileProp = projectilePropertiesComponentMapper.create(gameEntity);
        projectileProp.setType(weaponData.weaponType);
        projectileProp.setParentName(entityProperties.getName());
        projectileProp.setParentId(entityProperties.getEntityId());
        projectileProp.setId(nextId);
//        gameEntity.addComponent(entityProp);
//        gameEntity.addComponent(projectileProp);
        entityProp.setDamage(weaponData.damage);
        entityProp.setHealth(weaponData.health);
        entityProp.setWeight(weaponData.weight);
        entityProp.setVelocity(weaponData.maxSpeed);
        entityProp.setMaxSpeed(weaponData.maxSpeed);
        entityProp.setDestroyedAnimation(new ProjectileExplosionWithoutRenderingAnimation(item.getName(), gameEntity));

        TrackerProperties trackerProperties = null;
        if (clientTrackerProperties != null) {
            trackerProperties = trackerPropertiesComponentMapper.create(gameEntity);
            // We receive the unique entity id from the client but MovementSystem
            // uses the item id to track the target.
            Long trackedItemId = getItemIdByEntityId(clientTrackerProperties.getTrackedEntityId());
            if (trackedItemId != null) {
                trackerProperties.setTrackedEntityId(trackedItemId);
                trackerProperties.setMaxAngularVelocity(weaponData.maxAngularVelocity);
                trackerProperties.setTrackingDelay(headwayent.blackholedarksun.entitydata.WeaponData.WeaponType.getHomingMissileTrackingDelay(weaponData.weaponType));
                trackerProperties.setTrackingDelayTimeStarted();
                Entity followedShip = getEntityByGameEntityId(clientTrackerProperties.getTrackedEntityId());
                System.out.println("Creating tracking projectile for client with currentSelectedEnemy: " + clientTrackerProperties.getTrackedEntityId() + " followedShip: " + followedShip);
                if (followedShip != null) {
                    shipPropertiesComponentMapper.get(followedShip).addChasingProjectile(gameEntityId);
                }
            } else {
                System.out.println("Could not track entityId: " + clientTrackerProperties.getTrackedEntityId());
            }
        }

//        gameWorld.addEntity(gameEntity);
        addPropertiesToProjectileMultiplayerEntityHolder(gameEntityId, gameEntity, entityProp, projectileProp, trackerProperties);

        EntityMotionState motionState = new EntityMotionState(entityProp, projectileProp, trackerProperties);
        short collisionGroup = PhysicsProperties.CollisionGroup.STANDARD.getVal();
        short collisionMask = PhysicsProperties.CollisionMask.STANDARD.getVal();

        float weight = entityProp.getWeight();

        createPhysicsBody(gameEntity, entityAabb.halfSize, entityProp, motionState, collisionGroup, collisionMask, weight);

        // Disable collision between the ship and the just launched projectile.
        entityProp.getRigidBody().setIgnoreCollisionCheck(entityProperties.getRigidBody(), true);

        entityProp.setActivationState(Collision.ACTIVE_TAG);

        addedProjectilesPerPlayer.add(entityProp.getRigidBody());

        incrementGameEntityId();

        currentProjectile = null;
    }

    /** @noinspection deprecation*/
    private void preadvanceProjectile(ShipProperties shipProperties, ENG_SceneNode node, EntityProperties entityProp) {
        long latency = userIdToPingMap.get(shipProperties.getUserId());
        System.out.println("Initial latency: " + latency);
        long updateMillis = (long) (ENG_Math.ceil(((float) ENG_MainThread.UPDATE_INTERVAL) * 1000.0f));
        if (updateMillis > 0) {
//            if (latency < updateMillis) {
                latency += FRAMES_AHEAD * updateMillis + ClientEntityInterpolationSystem.CLIENT_DELAY;
//            }
            long framesAhead = latency / updateMillis;

            System.out.println("Final latency: " + latency);

            if (framesAhead > 0) {
                ENG_Vector4D tempVelocity = new ENG_Vector4D();
                currentShipVelocity.mul(GameWorld.getWorld().getDelta(), tempVelocity);
                tempVelocity.mul(framesAhead);
                ENG_Quaternion orientation = node.getOrientation();
                orientation.mul(tempVelocity, currentShipVelocity);
                entityProp.move(currentShipVelocity);
                System.out.println("Projectile created at pos: " + entityProp.getNode().getPosition());
            }
        }
    }

    @Override
    public void createReloaderEntity() {

    }

    @Override
    public void destroyEntity(EntityProperties entityProperties) {
        System.out.println(entityProperties.getName() + " destroyed");
        entityProperties.setDestroyed(true);
        Animation destroyedAnimation = entityProperties.getDestroyedAnimation();
        if (destroyedAnimation != null) {
            startAnimation(entityProperties.getEntityId(), destroyedAnimation);
        }
    }

    //    private MultiplayerServerFrameTCP getTCPFrame(Long userId) {
//        MultiplayerServerFrameTCP multiplayerServerFrameTCP = tcpFramesMap.get(userId);
//        if (multiplayerServerFrameTCP == null) {
//            multiplayerServerFrameTCP = new MultiplayerServerFrameTCP();
//            multiplayerServerFrameTCP.setTimestampToCurrentTime();
//            Long frameNum = nextFramesTCPMap.get(userId);
//            multiplayerServerFrameTCP.setFrameNum(frameNum != null ? frameNum : 0);
//            tcpFramesMap.put(userId, multiplayerServerFrameTCP);
//            tcpFramesList.add(multiplayerServerFrameTCP);
//        }
//        return multiplayerServerFrameTCP;
//    }
//
//    private MultiplayerServerFrameUDP getUDPFrame(Long userId) {
//        MultiplayerServerFrameUDP multiplayerServerFrameUDP = udpFramesMap.get(userId);
//        if (multiplayerServerFrameUDP == null) {
//            multiplayerServerFrameUDP = new MultiplayerServerFrameUDP();
//            multiplayerServerFrameUDP.setTimestampToCurrentTime();
//            Long frameNum = nextFramesUDPMap.get(userId);
//            multiplayerServerFrameUDP.setFrameNum(frameNum != null ? frameNum : 0);
//            udpFramesMap.put(userId, multiplayerServerFrameUDP);
//            udpFramesList.add(multiplayerServerFrameUDP);
//        }
//        return multiplayerServerFrameUDP;
//    }

//    private void clearFrameLists() {
//        for (Map.Entry<Long, MultiplayerServerFrameTCP> entry : tcpFramesMap.entrySet()) {
//            nextFramesTCPMap.put(entry.getKey(), entry.getValue().getNextFrameNum());
//        }
//        tcpFramesMap.clear();
//        tcpFramesList.clear();
//        for (Map.Entry<Long, MultiplayerServerFrameUDP> entry : udpFramesMap.entrySet()) {
//            nextFramesUDPMap.put(entry.getKey(), entry.getValue().getNextFrameNum());
//        }
//        udpFramesMap.clear();
//        udpFramesList.clear();

//        receivedCurrentFrameClientData.clear();
//    }

//    private void addToClientSet(Long id) {
//        clientSet.add(id);
//    }
//
//    private void removeFromClientSet(Long id) {
//        clientSet.remove(id);
//    }

//    private ENG_Integer getDroppedFrames(Long userId) {
//        ENG_Integer droppedFrames = clientDroppedFramesMap.get(userId);
//        if (droppedFrames == null) {
//            droppedFrames = new ENG_Integer();
//        }
//        return droppedFrames;
//    }

//    private boolean checkCurrentFrameClientDataReceived(Long userId) {
//        if (receivedCurrentFrameClientData.contains(userId)) {
//            getDroppedFrames(userId).setValue(0);
//            return true;
//        }
//        ENG_Integer droppedFrames = getDroppedFrames(userId);
//        droppedFrames.addInPlace(1);
//        boolean playerUnitialized = uninitializedClientSet.contains(userId);
//        if (droppedFrames.getValue() >= (playerUnitialized ? UNINITIALIZED_CLIENT_DROP_FRAMES_LIMIT : CLIENT_DROP_FRAMES_LIMIT)) {
//            removePlayer(userId);
//            // Close the connection
//            PlayerState playerState = playerStateMap.get(userId);
//            if (!MainApp.getMainThread().isInputState()) {
//                playerState.getConnection().close();
//            }
//            uninitializedClientSet.remove(userId);
//            playerStateToRemove.add(userId);
//        }
//        if (playerUnitialized) {
//            // We must first send the data and wait for the response
//            return true;
//        }
//        return false;
//    }

//    private void addToReceivedCurrentFrameClientData(MultiplayerClientFrameUDP multiplayerClientFrameUDP) {
//        receivedCurrentFrameClientData.add(multiplayerClientFrameUDP.getUserId());
//        System.out.println("Received udp frame client data from: " + multiplayerClientFrameUDP.getUserId());
//    }

    private void addPositionAndOrientation(LevelObject obj, ArrayList<LevelSpawnPoint> spawnPoints, PlayerRespawnData playerRespawnData) {
        int spawnPoint = playerRespawnData.getSpawnPoint(); //ENG_Utility.getRandom().nextInt(FrameInterval.GENERATE_RANDOM_MULTIPLAYER_PLAYER_SHIP_POSITION, spawnPoints.size());
        LevelSpawnPoint levelSpawnPoint = spawnPoints.get(spawnPoint);
        obj.position.set(levelSpawnPoint.position);
        obj.orientation.set(levelSpawnPoint.orientation);
    }

    public void addClientFrameUDP(MultiplayerClientFrameUDP multiplayerClientFrameUDP) {
        serverThreadLock.lock();
        try {
            clientFrameUDPList.add(multiplayerClientFrameUDP);
        } finally {
            serverThreadLock.unlock();
        }
    }

    public void addClientFrameTCP(MultiplayerClientFrameTCP multiplayerClientFrameTCP) {
        serverThreadLock.lock();
        try {
            clientFrameTCPList.add(multiplayerClientFrameTCP);
        } finally {
            serverThreadLock.unlock();
        }
    }

    public void addRespawnRequest(ServerRespawnRequest serverRespawnRequest) {
        serverThreadLock.lock();
        try {
            serverRespawnRequestList.add(serverRespawnRequest);
        } finally {
            serverThreadLock.unlock();
        }
    }

    private void processRespawnRequests() {
        ArrayList<ServerRespawnRequest> serverRespawnRequests = new ArrayList<>();
        serverThreadLock.lock();
        try {
            serverRespawnRequests.addAll(serverRespawnRequestList);
            serverRespawnRequestList.clear();
        } finally {
            serverThreadLock.unlock();
        }
        for (ServerRespawnRequest serverRespawnRequest : serverRespawnRequests) {
            processRespawnRequest(serverRespawnRequest);
        }

    }

    private void processRespawnRequest(ServerRespawnRequest serverRespawnRequest) {
        PlayerState playerState = playerStateMap.get(serverRespawnRequest.getUserId());
        PlayerRespawnData playerRespawnData = playerRespawnDataMap.get(serverRespawnRequest.getUserId());
        if (MainActivity.isDebugmode()) {
            if (playerState == null) {
                throw new IllegalArgumentException("No user id: " + serverRespawnRequest.getUserId() + " in playerStateMap");
            }
            if (playerRespawnData == null) {
                throw new IllegalArgumentException("No user id: " + serverRespawnRequest.getUserId() + " in playerRespawnDataMap");
            }
        }
        spawnPlayerShip(playerState, playerRespawnData);
    }

    public static class AddedPlayer {
        public JoinServerConnectionRequest request;
        public PlayerState playerState;
    }

    public void addPlayer(Connection connection, JoinServerConnectionRequest request) {
        serverThreadLock.lock();
        try {
            PlayerState playerState = new PlayerState();
            playerState.setUserId(request.getUserId());
            playerState.setConnection(connection);
            playerState.setShipName(request.getShipName()); // model name ship_human0 etc.
            playerState.getUserStats().setUsername(request.getPlayerName());
            playerState.setJustAdded(true);
            long mapId = gameWorld.getCurrentLevel().mapId;
            ServerConnectionResponse serverConnectionResponse = new ServerConnectionResponse();
            serverConnectionResponse.setConnection(connection);
            serverConnectionResponse.setMapId(mapId);
            connectionResponseList.add(serverConnectionResponse);
            if (playerStateMap.get(playerState.getUserId()) != null) {
                serverConnectionResponse.setErrorCode(ServerConnectionResponse.ERROR_CODE_PLAYER_ALREADY_ADDED);
            } else {
//                MultiplayerServerFrameTCP serverFrameTCP = getTCPFrame(request.getUserId());
                AddedPlayer addedPlayer = new AddedPlayer();
                addedPlayer.request = request;
                addedPlayer.playerState = playerState;
                addedPlayerList.add(addedPlayer);
            }
        } finally {
            serverThreadLock.unlock();
        }
    }

    private void processAddedPlayers() {
        ArrayList<AddedPlayer> addedPlayers = new ArrayList<>();
        ArrayList<ServerConnectionResponse> serverConnectionResponses = new ArrayList<>();
        serverThreadLock.lock();
        try {
            addedPlayers.addAll(addedPlayerList);
            addedPlayerList.clear();
            serverConnectionResponses.addAll(connectionResponseList);
            connectionResponseList.clear();
        } finally {
            serverThreadLock.unlock();
        }
        if (MainApp.getMainThread().isInputState()) {
            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            addedPlayers.addAll(currentFrameInterval.getAddedPlayers());
            serverConnectionResponses.addAll(currentFrameInterval.getServerConnectionResponses());
        }
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            FrameInterval currentFrameInterval = (FrameInterval) MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            currentFrameInterval.getAddedPlayers().addAll(addedPlayers);
            currentFrameInterval.getServerConnectionResponses().addAll(serverConnectionResponses);
        }
        // Notify the client that the connection has been acknowledged. Even if it may not be serviced.
        if (!MainApp.getMainThread().isInputState()) {
            for (ServerConnectionResponse response : serverConnectionResponses) {
                response.getConnection().sendTCP(response);
            }
        }

        for (AddedPlayer addedPlayer : addedPlayers) {
            processAddedPlayer(addedPlayer.request, addedPlayer.playerState);
            System.out.println("Adding player: " + addedPlayer.playerState.getShipName() + " userId: " + addedPlayer.playerState.getUserId());
        }
    }

    public static class PlayerRespawnData {
        private final headwayent.blackholedarksun.entitydata.ShipData shipData;
        private final String playerName;
        private final int spawnPoint;

        public PlayerRespawnData(headwayent.blackholedarksun.entitydata.ShipData shipData, String playerName, int spawnPoint) {
            this.shipData = shipData;
            this.playerName = playerName;
            this.spawnPoint = spawnPoint;
        }

        public String getPlayerName() {
            return playerName;
        }

        public headwayent.blackholedarksun.entitydata.ShipData getShipData() {
            return shipData;
        }

        public int getSpawnPoint() {
            return spawnPoint;
        }
    }

    private void processAddedPlayer(JoinServerConnectionRequest request, PlayerState playerState) {
//        addToClientSet(request.getUserId());
        playerStateMap.put(playerState.getUserId(), playerState);
        userIdToPingMap.put(playerState.getUserId(), 0);
        headwayent.blackholedarksun.entitydata.ShipData shipData = MainApp.getGame().getNameToShipMap(request.getShipName());
        PlayerRespawnData playerRespawnData = new PlayerRespawnData(shipData, request.getPlayerName(), request.getSpawnPoint());
        playerRespawnDataMap.put(playerState.getUserId(), playerRespawnData);
        playerState.setClientInitialized(true);
        spawnPlayerShip(playerState, playerRespawnData);
        registerUserStatsRmi(playerState);
    }

    private void spawnPlayerShip(PlayerState playerState, PlayerRespawnData playerRespawnData) {
        LevelObject levelObject = new LevelObject();
        levelObject.meshName = playerRespawnData.getShipData().inGameName;
        levelObject.name = playerRespawnData.getPlayerName();
        levelObject.type = LevelObject.LevelObjectType.FIGHTER_SHIP;
        levelObject.userId = playerState.getUserId();
        Level level = (Level) gameWorld.getCurrentLevel();
        addPositionAndOrientation(levelObject, level.levelStart.spawnPoints, playerRespawnData);
        currentPlayerState = playerState;
        createEntity(playerRespawnData.getShipData().inGameName, levelObject);
        Entity entity = getEntityByUserId(playerState.getUserId());
        animateShipSpawn(entity);
    }

    private void registerUserStatsRmi(PlayerState playerState) {
        NetManager netManager = NetManager.getSingleton();
//        UserStats userStats = new UserStats();
//        userStats.setUsername("000");
//        userStats.setKills(100);

        netManager.registerObjectSpace((int) playerState.getUserId(), userStatsList);
        netManager.addConnectionToObjectSpace(playerState.getConnection());
//        userStatsList.addUserStats(userStats);
    }

    public void removePlayer(PlayerState playerState) {
        unregisterUserStatsRmi(playerState);
        // Don't remove the player state yet. Remove it when the playerLeft status has been broadcast to all other players.
//        playerStateMap.remove(playerState.getUserId());
        Entity entity = entityByUserIdMap.get(playerState.getUserId());
        if (entity == null) {
            if (MainActivity.isDebugmode()) {
                throw new IllegalArgumentException(playerState.getShipName() + " ship with id: " + playerState.getUserId() + " not found in entityByIdMap");
            }
        }
        ShipProperties shipProperties = shipPropertiesComponentMapper.get(entity);
        shipProperties.setPlayerLeft(true);
    }

    private void unregisterUserStatsRmi(PlayerState playerState) {
        NetManager netManager = NetManager.getSingleton();
        netManager.unregisterObjectSpace((int) playerState.getUserId());
        netManager.removeConnectionFromObjectSpace(playerState.getConnection());
    }

    private void processRemovedPlayers() {
        ArrayList<Long> removedPlayers = new ArrayList<>();
        serverThreadLock.lock();
        try {
            removedPlayers.addAll(removedPlayerList);
            removedPlayerList.clear();
        } finally {
            serverThreadLock.unlock();
        }
        for (Long id : removedPlayers) {
//            removeFromClientSet(id);
            PlayerState playerState = playerStateMap.get(id);
            if (playerState != null) {
                removePlayer(playerState);
            } else {
                if (MainActivity.isDebugmode()) {
                    throw new IllegalArgumentException(id + " is not a valid player state");
                }
            }
        }

    }

    public void removePlayer(Long id) {
        serverThreadLock.lock();
        try {
            removedPlayerList.add(id);
        } finally {
            serverThreadLock.unlock();
        }

//        nextFramesTCPMap.remove(id);
//        nextFramesUDPMap.remove(id);

//        userIdToServerProjectileIdToClientProjectileId.remove(id);
//        userIdToClientProjectileIdToServerProjectileId.remove(id);
//        userIdToServerProjectileIdToEntityUDP.remove(id);
    }

    public boolean checkGameType(JoinServerConnectionRequest request) {
        return MainApp.getMainThread().getApplicationSettings().serverConnectionRequest.getGameType() == request.getGameType();
    }
}
