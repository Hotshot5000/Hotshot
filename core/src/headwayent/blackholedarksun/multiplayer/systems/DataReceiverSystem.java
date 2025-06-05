/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/20/21, 2:46 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;

import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameUDP;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.multiplayer.components.PlayerState;
import headwayent.blackholedarksun.world.WorldManagerServerSide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sebas on 01.01.2016.
 */
public class DataReceiverSystem extends MultiplayerDataHolderSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private ComponentMapper<PlayerState> playerStateMapper;
    private final ArrayList<MultiplayerClientFrameTCP> multiplayerClientFrameTCPs = new ArrayList<>();
    private final ArrayList<MultiplayerClientFrameUDP> multiplayerClientFrameUDPs = new ArrayList<>();
    private final HashMap<Long, ArrayList<MultiplayerClientFrameTCP>> entityIdToMultiplayerClientFrameTCP = new HashMap<>();
    private final HashMap<Long, ArrayList<MultiplayerClientFrameUDP>> entityIdToMultiplayerClientFrameUDP = new HashMap<>();
    private boolean projectileAdded;

    public DataReceiverSystem() {
        super(Aspect.all(PlayerState.class));
    }

    @Override
    protected void begin() {
        super.begin();
        entityIdToMultiplayerClientFrameTCP.clear();
        entityIdToMultiplayerClientFrameUDP.clear();
        for (MultiplayerClientFrameTCP clientFrameTCP : multiplayerClientFrameTCPs) {
            ArrayList<MultiplayerClientFrameTCP> multiplayerClientFrameTCPS = entityIdToMultiplayerClientFrameTCP.get(clientFrameTCP.getUserId());
            if (multiplayerClientFrameTCPS == null) {
                multiplayerClientFrameTCPS = new ArrayList<>();
                entityIdToMultiplayerClientFrameTCP.put(clientFrameTCP.getUserId(), multiplayerClientFrameTCPS);
            }
            multiplayerClientFrameTCPS.add(clientFrameTCP);
//            if (!clientFrameTCP.getAddedProjectiles().isEmpty()) {
//                testSpecificProjectileAdded = true;
//                System.out.println("DataReceiverSystem begin projectiles num: " + clientFrameTCP.getAddedProjectiles().size() + " client userId: " + clientFrameTCP.getUserId());
//            }
        }
        for (MultiplayerClientFrameUDP clientFrameUDP : multiplayerClientFrameUDPs) {
            ArrayList<MultiplayerClientFrameUDP> multiplayerClientFrameUDPS = entityIdToMultiplayerClientFrameUDP.get(clientFrameUDP.getUserId());
            if (multiplayerClientFrameUDPS == null) {
                multiplayerClientFrameUDPS = new ArrayList<>();
                entityIdToMultiplayerClientFrameUDP.put(clientFrameUDP.getUserId(), multiplayerClientFrameUDPS);
            }
            multiplayerClientFrameUDPS.add(clientFrameUDP);
        }
        projectileAdded = false;
        multiplayerClientFrameTCPs.clear();
        multiplayerClientFrameUDPs.clear();
//        System.out.println("DataReceiverSystem begin");
    }

    @Override
    protected void process(Entity e) {
        EntityProperties entityProperties = entityPropertiesMapper.get(e);
        ShipProperties shipProperties = shipPropertiesMapper.get(e);
        PlayerState playerState = playerStateMapper.get(e);

//        System.out.println("DataReceiverSystem process player userId: " + playerState.getUserId());

        ArrayList<MultiplayerClientFrameTCP> multiplayerClientFrameTCPList = entityIdToMultiplayerClientFrameTCP.get(playerState.getUserId());
        ArrayList<MultiplayerClientFrameUDP> multiplayerClientFrameUDPList = entityIdToMultiplayerClientFrameUDP.get(playerState.getUserId());

//        if (multiplayerClientFrameTCP != null && !multiplayerClientFrameTCP.getAddedProjectiles().isEmpty()) {
//            System.out.println("DataReceiverSystem processing projectile num: " + multiplayerClientFrameTCP.getAddedProjectiles().size());
//        }

        boolean dataReceivedForCurrentFrameTCP = multiplayerClientFrameTCPList != null;
        boolean dataReceivedForCurrentFrameUDP = multiplayerClientFrameUDPList != null;
        long maxFrameNumTCP = -1;
        long maxFrameNumUDP = -1;
        if (dataReceivedForCurrentFrameTCP) {
            for (MultiplayerClientFrameTCP multiplayerClientFrameTCP : multiplayerClientFrameTCPList) {
                if (maxFrameNumTCP < multiplayerClientFrameTCP.getFrameNum()) {
                    maxFrameNumTCP = multiplayerClientFrameTCP.getFrameNum();
                }
            }

        }
        if (dataReceivedForCurrentFrameUDP) {
            for (MultiplayerClientFrameUDP multiplayerClientFrameUDP : multiplayerClientFrameUDPList) {
                if (maxFrameNumUDP < multiplayerClientFrameUDP.getFrameNum()) {
                    maxFrameNumUDP = multiplayerClientFrameUDP.getFrameNum();
                }
            }

        }
        playerState.setDataReceivedForCurrentFrameTCP(dataReceivedForCurrentFrameTCP, maxFrameNumTCP);
        playerState.setDataReceivedForCurrentFrameUDP(dataReceivedForCurrentFrameUDP, maxFrameNumUDP);

        playerState.updateDroppedFrames();

//        if (!dataReceivedForCurrentFrameTCP) {
//            System.out.println("dataReceivedForCurrentFrameTCP false for userId: " + playerState.getUserId());
//        }

        if (dataReceivedForCurrentFrameTCP) {
            for (MultiplayerClientFrameTCP multiplayerClientFrameTCP : multiplayerClientFrameTCPList) {
                processTCP(e, multiplayerClientFrameTCP, entityProperties, shipProperties, playerState);
            }


//            if (testSpecificProjectileAdded) {
//                System.out.println("DataReceiverSystem process multiplayerClientFrameTCP addedProjectiles num: " + multiplayerClientFrameTCPList.getAddedProjectiles().size() + " userId: " + playerState.getUserId());
//            }
        }

        if (dataReceivedForCurrentFrameUDP) {
            for (MultiplayerClientFrameUDP multiplayerClientFrameUDP : multiplayerClientFrameUDPList) {
                processUDP(multiplayerClientFrameUDP, entityProperties, shipProperties, playerState);
            }


        }
    }

    private void processUDP(MultiplayerClientFrameUDP multiplayerClientFrameUDP,
                            EntityProperties entityProperties, ShipProperties shipProperties, PlayerState playerState) {
//        System.out.println("Frame: " + multiplayerClientFrameUDP.getFrameNum() +
//                " user position: " + multiplayerClientFrameUDP._getTranslate() + " rotation: " + multiplayerClientFrameUDP._getRotate().toString());

        // Translation is handled on server side but we use the orientation from client side.
        // TODO check the above comment. For now we shall consider the client position as truth. TRUSTING CLIENT IS NOT GOOD!
        entityProperties.setTransform(multiplayerClientFrameUDP._getTranslate(), multiplayerClientFrameUDP._getRotate(), true);

//        entityProperties.setTranslate(multiplayerClientFrameUDP._getTranslate());
//        entityProperties.setRotate(multiplayerClientFrameUDP._getRotate());
        // TODO we no longer set the velocity and just accept the position and orientation from the client
//        entityProperties.setVelocity(multiplayerClientFrameUDP._getVelocity());

//        entityProperties.setAngularVelocity(multiplayerClientFrameUDP._getAngularVelocity());
//        entityProperties.updateMultiplayerCoordsForReceive();

//        if (entityProperties.getNode().getName().startsWith("Sebi")) {
//            if (multiplayerClientFrameUDP._getTranslate().distance(entityProperties.getPosition()) > 30.0f) {
//                System.out.println("Player distance len: " + multiplayerClientFrameUDP._getTranslate().distance(entityProperties.getPosition()));
//            }
//        }

//        if (entityProperties.getNode().getName().startsWith("John")) {
//            System.out.println("John DataReceiverSystem processUDP translate: " + multiplayerClientFrameUDP._getTranslate() + " current node pos: " + entityProperties.getPosition());
//        }
    }

    private void processTCP(Entity entity, MultiplayerClientFrameTCP multiplayerClientFrameTCP,
                            EntityProperties entityProperties, ShipProperties shipProperties, PlayerState playerState) {
//        entityProperties.setLastMultiplayerClientFrameTCP(multiplayerClientFrameTCP);
        shipProperties.addToClientAddedProjectilesList(multiplayerClientFrameTCP.getAddedProjectiles());
//        System.out.println(entityProperties.getName() + " set last multiplayer client frame tcp with projectile num: "
//                + multiplayerClientFrameTCP.getProjectileListSize());
        WorldManagerServerSide worldManagerServerSide = WorldManagerServerSide.getSingleton();
        for (int i = 0; i < multiplayerClientFrameTCP.getProjectileListSize(); ++i) {
            MultiplayerEntityTCP projectile = multiplayerClientFrameTCP.getProjectile(i);
            if (projectile.getTrackerProperties() != null) {
                EntityProperties trackedEntityProperties = worldManagerServerSide.getEntityPropertiesComponentMapper()
                        .getSafe(worldManagerServerSide.getEntityByGameEntityId(
                                projectile.getTrackerProperties().getTrackedEntityId()));
                if (trackedEntityProperties != null) {
                    System.out.println("creating projectile from client: " + entityProperties.getName() + " with tracking of player entity id: " + trackedEntityProperties.getEntityId() + " name: " + trackedEntityProperties.getItem().getName());
                } else {
                    System.out.println("Could not find tracked entityId for: " + projectile.getTrackerProperties().getTrackedEntityId());
                }
            }
            worldManagerServerSide.createProjectile(entity, projectile);
        }
        // Make sure that the added projectiles cannot collide between themselves.
        worldManagerServerSide.setAddedProjectilesToIgnoreSelfCollision();
        if (multiplayerClientFrameTCP.getProjectileListSize() > 0) {
            projectileAdded = true;
//            System.out.println("Received projectiles to create num: " + multiplayerClientFrameTCP.getAddedProjectiles().size());
        }

        // Don't set it everytime we get here. Only if true.
        if (multiplayerClientFrameTCP.isAfterburnerActive()) {
            shipProperties.setAfterburnerActive(true);
        }
        if (multiplayerClientFrameTCP.isCountermeasuresLaunched()) {
            worldManagerServerSide.createCountermeasures(entity);
        }
    }

    @Override
    protected void end() {
        if (projectileAdded) {
//            GameWorld.getWorld().updateAddedEntities();
            GameWorld.getWorld().updateEntityStates();
            System.out.println("updated added entities");
        }
//        System.out.println("DataReceiverSystem end");
        boolean testSpecificProjectileAdded = false;
        super.end();
    }

    public void addMultiplayerClientFrameTCPs(ArrayList<MultiplayerClientFrameTCP> multiplayerClientFrameTCPs) {
        this.multiplayerClientFrameTCPs.addAll(multiplayerClientFrameTCPs);
//        for (MultiplayerClientFrameTCP multiplayerClientFrameTCP : multiplayerClientFrameTCPs) {
//            if (!multiplayerClientFrameTCP.getAddedProjectiles().isEmpty()) {
//                System.out.println("addMultiplayerClientFrameTCPs projectiles num: " + multiplayerClientFrameTCP.getAddedProjectiles().size());
//            }
//        }

    }

    public void addMultiplayerClientFrameUDPs(ArrayList<MultiplayerClientFrameUDP> multiplayerClientFrameUDPs) {
        this.multiplayerClientFrameUDPs.addAll(multiplayerClientFrameUDPs);
    }
}
