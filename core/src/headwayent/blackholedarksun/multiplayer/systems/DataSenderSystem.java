/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 2:22 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.kryonet.Connection;
import com.google.common.eventbus.Subscribe;
import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.*;
import headwayent.blackholedarksun.levelresource.Level;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDPBreaker;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityUDP;
import headwayent.blackholedarksun.multiplayer.components.PlayerState;
import headwayent.blackholedarksun.multiplayer.rmi.UserStats;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.net.clientapi.ClientAPI;
import headwayent.blackholedarksun.net.clientapi.tables.User;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.hotshotengine.ENG_MainThread;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sebas on 01.01.2016.
 */
public class DataSenderSystem extends MultiplayerDataHolderIntervalSystem {

    private static final boolean IGNORE_DROP_PLAYER = false;
    private static final double INTERVAL = ENG_MainThread.UPDATE_INTERVAL * 3;
    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private ComponentMapper<ProjectileProperties> projectilePropertiesMapper;
    private ComponentMapper<TrackerProperties> trackerPropertiesMapper;
    private ComponentMapper<MultiplayerEntityTCP> multiplayerEntityTCPMapper;
    private ComponentMapper<MultiplayerEntityUDP> multiplayerEntityUDPMapper;
    private ComponentMapper<PlayerState> playerStateMapper;
    private final MultiplayerServerFrameUDPBreaker serverFrameBreaker = new MultiplayerServerFrameUDPBreaker(NetManager.MAX_UDP_DATAGRAM_SIZE);
    private ArrayList<MultiplayerEntityTCP> modifiedMultiplayerComponentsList;
    private ArrayList<MultiplayerEntityTCP> createdMultiplayerComponentsList;
    private int dataSentPerSecond;
    private long dataSentPerSecondBeginTime;

    /** @noinspection UnstableApiUsage*/
    public DataSenderSystem() {
        // We are sending only to those entities that have a PlayerState, but we are not
        // only sending other entities that have PlayerStates we also send other entities
        // such as asteroids. We need the actives to contain all the objects in the scene that
        // are changeable.
        super(Aspect.all(EntityProperties.class), (float) INTERVAL);
        MainApp.getGame().getEventBus().register(this);
    }

    @Override
    protected void begin() {
        super.begin();
        ImmutableBag<Entity> actives = getActiveEntityList();
        ChangedMultiplayerComponents changedMultiplayerComponentsList = getChangedMultiplayerComponentsList(actives);
        this.modifiedMultiplayerComponentsList = changedMultiplayerComponentsList.modifiedEntities;
        this.createdMultiplayerComponentsList = changedMultiplayerComponentsList.createdEntities;
        for (int i = 0; i < actives.size(); ++i) {
            Entity othEntity = actives.get(i);
            EntityProperties othEntityProperties = entityPropertiesMapper.get(othEntity);
            othEntityProperties.updateMultiplayerCoordsForSendServerSide();
        }
//        System.out.println("DataSenderSystem.begin() actives size: " + actives.size());
//        for (MultiplayerEntityTCP multiplayerEntityTCP : createdMultiplayerComponentsList) {
//            System.out.println("multiplayerEntityTCP: " + multiplayerEntityTCP);
//        }

//        if (ENG_Utility.hasTimePassed(dataSentPerSecondBeginTime, 1000)) {
//            System.out.println("dataSentPerSecond: " + dataSentPerSecond);
//            dataSentPerSecond = 0;
//            dataSentPerSecondBeginTime = ENG_Utility.currentTimeMillis();
//        }
    }

    @Override
    protected void process(Entity e) {
        EntityProperties entityProperties = entityPropertiesMapper.get(e);
        ShipProperties shipProperties = shipPropertiesMapper.getSafe(e);

        ImmutableBag<Entity> actives = getActiveEntityList();

        WorldManagerServerSide worldManagerServerSide = WorldManagerServerSide.getSingleton();

        PlayerState playerState = playerStateMapper.getSafe(e);

        if (playerState != null && shipProperties != null) {
            MultiplayerServerFrameTCP tcpFrame = playerState.getMultiplayerServerFrameTCP();
            MultiplayerServerFrameUDP udpFrame = playerState.getMultiplayerServerFrameUDP();
            tcpFrame.prepareNextFrame();
            tcpFrame.incrementFrameNum(1);
//        tcpFrame.setFrameNum(playerState.getDataReceivedForCurrentFrameFrameNumTCP());
            udpFrame.prepareNextFrame();
//        udpFrame.setTimestampToCurrentTime();
//        udpFrame.setFrameNum(playerState.getDataReceivedForCurrentFrameFrameNumUDP());


            if (playerState.isJustAdded()) {
                for (int i = 0; i < actives.size(); ++i) {
                    Entity entity = actives.get(i);
                    MultiplayerEntityTCP multiplayerEntityTCP = multiplayerEntityTCPMapper.get(entity);
                    multiplayerEntityTCP.getEntityProperties().updateMultiplayerCoordsForSendServerSide();
                    tcpFrame.addMultiplayerEntityAddedCurrentFrame(multiplayerEntityTCP);
                }
                playerState.setJustAdded(false);
            } else {
                for (int i = 0; i < createdMultiplayerComponentsList.size(); ++i) {
                    MultiplayerEntityTCP multiplayerEntityTCP = createdMultiplayerComponentsList.get(i);
                    multiplayerEntityTCP.getEntityProperties().updateMultiplayerCoordsForSendServerSide();
                    if (multiplayerEntityTCP.getProjectileProperties() != null) {
                        // Make sure this isn't a projectile created by this client.
//                    MultiplayerClientFrameTCP lastMultiplayerClientFrameTCP = entityProperties.getLastMultiplayerClientFrameTCP();
                        boolean found = false;
//                    if (lastMultiplayerClientFrameTCP != null) {
                        for (MultiplayerEntityTCP projectile : shipProperties.getClientAddedProjectiles()) {
                            Long clientId = worldManagerServerSide.getClientId(playerState.getUserId(),
                                    multiplayerEntityTCP.getEntityProperties().getEntityId());
                            if (clientId != null && clientId == projectile.getEntityProperties().getEntityId()) {
                                found = true;
                                break;
                            }
                        }

//                    }
                        if (!found) {
                            System.out.println("Sending added projectile: " + multiplayerEntityTCP.getEntityProperties().getName());
                            tcpFrame.addProjectileAddedCurrentFrame(multiplayerEntityTCP);
                        }
                    } else {
                        System.out.println("Sending added entity: " + multiplayerEntityTCP.getEntityProperties().getName());
                        tcpFrame.addMultiplayerEntityAddedCurrentFrame(multiplayerEntityTCP);
                    }
                }
//            if (entityProperties.getLastMultiplayerClientFrameTCP() != null) {
//                System.out.println("Setting last multiplayer client to null");
//            }
//            entityProperties.setLastMultiplayerClientFrameTCP(null);
                shipProperties.clearClientAddedProjectilesList();
            }

            for (int i = 0; i < modifiedMultiplayerComponentsList.size(); ++i) {
                MultiplayerEntityTCP multiplayerEntityTCP = modifiedMultiplayerComponentsList.get(i);
                EntityProperties othEntityProperties = multiplayerEntityTCP.getEntityProperties();
                othEntityProperties.updateMultiplayerCoordsForSendServerSide();
                Long clientId = worldManagerServerSide.getClientId(playerState.getUserId(), othEntityProperties.getEntityId());
                long lastClientId = othEntityProperties.getEntityId();
                if (clientId != null) {
                    EntityProperties specificEntityIdEntityProperties = new EntityProperties(clientId, othEntityProperties);
//                ShipProperties specificEntityIdShipProperties = null;
//                if (multiplayerEntityTCP.getShipProperties() != null) {
//                    specificEntityIdShipProperties = new ShipProperties(playerState.getUserId(), multiplayerEntityTCP.getShipProperties());
//                }
                    MultiplayerEntityTCP clientSpecificMultiplayerEntityTCP = new MultiplayerEntityTCP();
                    clientSpecificMultiplayerEntityTCP.setEntityProperties(specificEntityIdEntityProperties);
                    clientSpecificMultiplayerEntityTCP.setShipProperties(multiplayerEntityTCP.getShipProperties());
                    clientSpecificMultiplayerEntityTCP.setProjectileProperties(multiplayerEntityTCP.getProjectileProperties());
                    clientSpecificMultiplayerEntityTCP.setTrackerProperties(multiplayerEntityTCP.getTrackerProperties());
                    multiplayerEntityTCP = clientSpecificMultiplayerEntityTCP;
                }
//                System.out.println("Sending modified entity: " + othEntityProperties.getName()
//                        + " with entityId: " + multiplayerEntityTCP.getEntityProperties().getEntityId() + " destroyed: " + othEntityProperties.isDestroyed());
                tcpFrame.addUpdateEntity(multiplayerEntityTCP);
            }


//        if (playerState.isDataReceivedForCurrentFrame()) {
            // Send updated entity states.
            // Make sure that we are using the client specific id if necessary.
            for (int i = 0; i < actives.size(); ++i) {
                Entity othEntity = actives.get(i);
                // Also send the player position so it can interpolate and find the real position that is, the server position.
//                if (e == othEntity) {
//                    continue;
//                }
                EntityProperties othEntityProperties = entityPropertiesMapper.get(othEntity);
                if (!othEntityProperties.isDirtyUdp()) {
                    continue;
                }
                MultiplayerEntityUDP multiplayerEntityUDP = multiplayerEntityUDPMapper.get(othEntity);
                // Check if this entity isn't a client created entity. That means we should transform the id back to the client id before sending.
                Long clientId = worldManagerServerSide.getClientId(playerState.getUserId(), othEntityProperties.getEntityId());
                long lastClientId = multiplayerEntityUDP.getEntityId();
                if (clientId != null) {
                    multiplayerEntityUDP = new MultiplayerEntityUDP(multiplayerEntityUDP);
                    multiplayerEntityUDP.setEntityId(clientId);
                }
                multiplayerEntityUDP.setTranslate(othEntityProperties.getTranslate());
                multiplayerEntityUDP.setRotate(othEntityProperties.getRotate());
                multiplayerEntityUDP.setVelocity(othEntityProperties.getVelocityOriginal());

                // Also send the physics stuff.
//                EntityRigidBody othRigidBody = othEntityProperties.getRigidBody();
//                multiplayerEntityUDP.setLinearVelocity(PhysicsUtility.convertVector4(othRigidBody.getLinearVelocity()));
//                multiplayerEntityUDP.setAngularVelocity(PhysicsUtility.convertVector4(othRigidBody.getAngularVelocity()));

//                ENG_Vector4D entityAxis = new ENG_Vector4D();
//                float entityAngle = entityProperties.getRotate().toAngleAxisDeg(entityAxis);

//                if (entityProperties.getNode().getName().startsWith("John") && Math.abs(entityAngle) > ENG_Math.FLOAT_EPSILON) {
//                    System.out.println("John axis: " + entityAxis + " angle: " + entityAngle);
//                }
//                if (entityProperties.getNode().getName().startsWith("John")) {
//                    System.out.println("John DataSenderSystem translate: " + entityProperties.getTranslate());
//                }
//                System.out.println("Sending entity: " + othEntityProperties.getName() +
//                        " with timestamp: " + udpFrame.getTimestamp() +
//                        " with coords: " + multiplayerEntityUDP.toString());
//                if (clientId != null) {
//                    udpFrame.addClientSpecificEntity(multiplayerEntityUDP);
//                } else {
                udpFrame.addEntity(multiplayerEntityUDP);
//                }
            }
//        }

            if (playerState.shouldRemovePlayerForDroppedFrames() && !IGNORE_DROP_PLAYER) {
                System.out.println("Removing player for dropped frames: " + playerState.getShipName() + " userId: " + playerState.getUserId());
                worldManagerServerSide.removePlayer(playerState);
            }

            Level level = (Level) GameWorld.getWorld().getCurrentLevel();
            LevelEvent.EventState endLevelState = worldManagerServerSide.getEndLevelState();
            if (level.levelEnded) {
                tcpFrame.setLevelEnded();
                if (endLevelState == LevelEvent.EventState.WON) {
                    tcpFrame.setLevelWon(true);
                } else if (endLevelState == LevelEvent.EventState.LOST) {
                    tcpFrame.setLevelWon(false);
                } else {
                    throw new IllegalStateException("The end level event must be either won or lost");
                }
            }

            for (MultiplayerEntityUDP multiplayerEntityUDP : udpFrame.getEntities()) {
//            if (multiplayerEntityUDP.getEntityName().startsWith("Sebi_Concussion")) {
//                System.out.println(multiplayerEntityUDP.getEntityName() + " position: " + multiplayerEntityUDP.getTranslate());
//            }
            }


            if (!MainApp.getMainThread().isInputState()) {
                Connection connection = playerState.getConnection();
                sendTCPFrame(tcpFrame, connection);
                if (!udpFrame.isEmptyFrame()) {
                    int sentFrames = sendUDPFrame(udpFrame, connection);
                    udpFrame.incrementFrameNum(sentFrames);
                    ++dataSentPerSecond;
                }
            }
        }
    }

    @Override
    protected void end() {
        ImmutableBag<Entity> actives = getActiveEntityList();
        for (int i = 0; i < actives.size(); ++i) {
            Entity othEntity = actives.get(i);
            EntityProperties othEntityProperties = entityPropertiesMapper.get(othEntity);
            othEntityProperties.resetDirtyUdp();
        }

        Level level = (Level) GameWorld.getWorld().getCurrentLevel();
        for (MultiplayerEntityTCP multiplayerEntityTCP : modifiedMultiplayerComponentsList) {
            EntityProperties entityProperties = multiplayerEntityTCP.getEntityProperties();
            ShipProperties shipProperties = multiplayerEntityTCP.getShipProperties();
            if (entityProperties.isDestroyed()) {
                entityProperties.setDestroyedSent(true);
            }
            if (entityProperties.isDestroyedDuringAnimation()) {
                entityProperties.setDestroyedDuringAnimationSent(true);
            }
            if (entityProperties.isDestroyedAnimationFinished()) {
                entityProperties.setDestroyedAnimationFinishedSent(true);
            }
            if (shipProperties != null) {
                boolean exitedOrLeft = false;
                if (shipProperties.isExited()) {
                    shipProperties.setExitedSent(true);
                    exitedOrLeft = true;
                }
                if (shipProperties.isPlayerLeft()) {
                    shipProperties.setPlayerLeftSent(true);
                    exitedOrLeft = true;
                }
                if (exitedOrLeft) {
                    HashMap<Long, PlayerState> playerStateMap = WorldManagerServerSide.getSingleton().getPlayerStateMap();
                    HashMap<Long, WorldManagerServerSide.PlayerRespawnData> playerRespawnDataMap = WorldManagerServerSide.getSingleton().getPlayerRespawnDataMap();
                    WorldManagerServerSide.PlayerRespawnData playerRespawnData = playerRespawnDataMap.remove(shipProperties.getUserId());
//                    if (playerRespawnData == null && MainActivity.isDebugmode()) {
//                        throw new IllegalStateException("playerRespawnData for user id: " + shipProperties.getUserId() + " is null");
//                    }
                    PlayerState playerState = null;
                    if (!level.levelEnded) {
                        playerState = playerStateMap.remove(shipProperties.getUserId());
                    }
//                    if (playerState == null) {
//                        if (MainActivity.isDebugmode()) {
//                            throw new IllegalStateException("playerState for user id: " + shipProperties.getUserId() + " is null");
//                        }
//                    }
                    // Close connection only for actual player ships. Ignore the AI.
                    if (playerState != null) {
                        playerState.getConnection().close();
                        updateUserStats(playerState);
                    }
                }
            }
        }


        if (level.levelEnded) {
            // If the level has ended we must update all player stats and make sure that all clients get their updated stats.
            HashMap<Long, PlayerState> playerStateMap = WorldManagerServerSide.getSingleton().getPlayerStateMap();
            for (PlayerState playerState : playerStateMap.values()) {
                updateUserStats(playerState);
//                break;
            }
            playerStateMap.clear();

        }

        super.end();
    }

    /** @noinspection UnstableApiUsage*/
    private void updateUserStats(PlayerState playerState) {
        UserStats userStats = playerState.getUserStats();
        User user = new User();
        user.setId(playerState.getUserId());
        user.setSessionsPlayed(1);
        user.setSessionsWon(0);
        user.setSessionsLost(0);
        user.setKills(userStats.getKills());
        user.setDeaths(userStats.getDeaths());
        MainApp.getGame().getEventBus().post(new ClientAPI.UpdateUserAccountEvent(user));
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void userAccountUpdated(ClientAPI.UserAccountUpdatedEvent event) {
        // We don't care about the result we just need it because without a subscription the eventBus.post() does not get sent.
        System.out.println("Account updated: " + event.user.getUsername() + " userId: " + event.user.getId());
        exitIfNoPlayers();
    }

    private static void exitIfNoPlayers() {
        Level level = (Level) GameWorld.getWorld().getCurrentLevel();
        // If the level ended we still need to wait for the responses from updating the user accounts
        // to come in. They will be dispatched only if the main thread is allowed to run in order
        // to dispatch those responses. If we set the levelEndedSent to true in end() the
        // WorldManagerServerSide will block in endLevel(), thus not allowing the main thread
        // to process the messages dispatching.
        if (level.levelEnded) {
            level.levelEndedSent = true;
        }
        HashMap<Long, PlayerState> playerStateMap = WorldManagerServerSide.getSingleton().getPlayerStateMap();
        if (playerStateMap.isEmpty()) {
            // We must close this server.
            WorldManagerServerSide.getSingleton().exitGame();
        }
    }

    /** @noinspection UnstableApiUsage*/
    @Subscribe
    public void userAccountUpdateFailed(ClientAPI.UserAccountUpdateErrorEvent event) {
        System.out.println("Error updating user accound: " + event.restError.getMessage() +
                " code: " + event.restError.getCode() + " httpCode: " + event.restError.getHttpCode());
        exitIfNoPlayers();
    }

    private ImmutableBag<Entity> getActiveEntityList() {
        MultiplayerEntityProcessingServerSystem multiplayerEntityProcessingServerSystem =
                GameWorld.getWorld().getSystem(MultiplayerEntityProcessingServerSystem.class);
        return multiplayerEntityProcessingServerSystem.getEntities();//getActives();
    }

    private static class ChangedMultiplayerComponents {
        public ArrayList<MultiplayerEntityTCP> modifiedEntities;
        public ArrayList<MultiplayerEntityTCP> createdEntities;
    }

    private ChangedMultiplayerComponents getChangedMultiplayerComponentsList(ImmutableBag<Entity> actives) {
        ChangedMultiplayerComponents changedMultiplayerComponents = new ChangedMultiplayerComponents();
        ArrayList<MultiplayerEntityTCP> modifiedMultiplayerEntityTCPs = new ArrayList<>();
        ArrayList<MultiplayerEntityTCP> createdMultiplayerEntityTCPs = new ArrayList<>();
        changedMultiplayerComponents.modifiedEntities = modifiedMultiplayerEntityTCPs;
        changedMultiplayerComponents.createdEntities = createdMultiplayerEntityTCPs;
        for (int i = 0; i < actives.size(); ++i) {
            Entity e = actives.get(i);
            EntityProperties entityProperties = entityPropertiesMapper.get(e);
            ShipProperties shipProperties = shipPropertiesMapper.getSafe(e);
            ProjectileProperties projectileProperties = projectilePropertiesMapper.getSafe(e);
            TrackerProperties trackerProperties = trackerPropertiesMapper.getSafe(e);
            MultiplayerEntityTCP multiplayerEntityTCP = multiplayerEntityTCPMapper.getSafe(e);
            boolean added = checkAndResetDirty(modifiedMultiplayerEntityTCPs, multiplayerEntityTCP, entityProperties, false);
            added = checkAndResetDirty(modifiedMultiplayerEntityTCPs, multiplayerEntityTCP, shipProperties, added);
            added = checkAndResetDirty(modifiedMultiplayerEntityTCPs, multiplayerEntityTCP, projectileProperties, added);
            added = checkAndResetDirty(modifiedMultiplayerEntityTCPs, multiplayerEntityTCP, trackerProperties, added);

            if (entityProperties.isJustCreated()) {
                createdMultiplayerEntityTCPs.add(multiplayerEntityTCP);
                entityProperties.setJustCreated(false);
                System.out.println(entityProperties.getName() + " has been just created");
            }
        }
        return changedMultiplayerComponents;
    }

    private boolean checkAndResetDirty(ArrayList<MultiplayerEntityTCP> list, MultiplayerEntityTCP e, MultiplayerComponent c, boolean alreadyAdded) {
        if (c != null && c.isDirtyTcp()) {
            c.resetDirtyTcp();
            if (!alreadyAdded) {
                list.add(e);
                alreadyAdded = true;
            }
        }
        return alreadyAdded;
    }

    private int sendUDPFrame(MultiplayerServerFrameUDP udpFrame, Connection connection) {
        ArrayList<MultiplayerServerFrameUDP> multiplayerServerFrameUDPList = new ArrayList<>();
        multiplayerServerFrameUDPList.add(udpFrame);
        if (!serverFrameBreaker.breakUDP(connection, multiplayerServerFrameUDPList)) {
            return 0;
        }
        return multiplayerServerFrameUDPList.size();
    }

    private void sendTCPFrame(MultiplayerServerFrameTCP tcpFrame, Connection connection) {
        int tcpSentBytes = connection.sendTCP(tcpFrame);
//        System.out.println("tcpLen: " + tcpSentBytes);
    }
}
