/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 4:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameUDP;
import headwayent.blackholedarksun.net.NetManager;
import headwayent.blackholedarksun.systems.IntervalVoidEntitySystem;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerMP;
import headwayent.hotshotengine.ENG_MainThread;
import headwayent.hotshotengine.ENG_Utility;

/**
 * Created by sebas on 11.02.2016.
 */
public class DataSenderMPSystem extends IntervalVoidEntitySystem {

    private static final double INTERVAL = ENG_MainThread.UPDATE_INTERVAL * 1;
    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private int dataSentPerSecond;
    private long dataSentPerSecondBeginTime;
//    private MultiplayerClientFrameTCP clientFrameTCP = new MultiplayerClientFrameTCP();
//    private MultiplayerClientFrameUDP clientFrameUDP = new MultiplayerClientFrameUDP();

    public DataSenderMPSystem() {
        super((float) INTERVAL);
    }

    @Override
    protected void processSystem() {
        NetManager netManager = NetManager.getSingleton();
        Entity playerShip = WorldManager.getSingleton().getPlayerShip();
        if (playerShip == null) {
            // The ship hasn't been initialized by the server so there is nothing to send for now.
            return;
        }
        WorldManagerMP worldManager = (WorldManagerMP) WorldManager.getSingleton();
        MultiplayerClientFrameTCP clientFrameTCP = worldManager.getClientFrameTCP();
        MultiplayerClientFrameUDP clientFrameUDP = worldManager.getClientFrameUDP();
//        if (!clientFrameTCP.getAddedProjectiles().isEmpty()) {
//            System.out.println("Sending projectile num: " + clientFrameTCP.getAddedProjectiles().size());
//        }
        EntityProperties entityProperties = entityPropertiesMapper.get(playerShip);
        ShipProperties shipProperties = shipPropertiesMapper.get(playerShip);
//        clientFrameTCP.setHealth(entityProperties.getHealth());
//        clientFrameTCP.setDestroyed(entityProperties.isDestroyed());
        clientFrameTCP.setAfterburnerActive(shipProperties.isAfterburnerActiveSticky());
        shipProperties.setAfterburnerActiveSticky(false);
        clientFrameTCP.setCountermeasuresLaunched(shipProperties.isCountermeasureLaunchedSticky());
        shipProperties.setCountermeasureLaunchedSticky(false);
        entityProperties.updateMultiplayerCoordsForSendClientSide();
        clientFrameUDP.setTranslate(entityProperties.getTranslate());
        clientFrameUDP.setRotate(entityProperties.getRotate());

//        clientFrameUDP.setVelocity(entityProperties.getVelocityOriginal());

//        clientFrameUDP.setAngularVelocity(PhysicsUtility.convertVector4(entityProperties.getRigidBody().getAngularVelocity()));
//        System.out.println("Sending update: " + " with timestamp: " + clientFrameUDP.getTimestamp() + " " + clientFrameUDP.toString());

//        if (entityProperties.getNode().getName().startsWith("John")) {
//            System.out.println("John DataSenderMPSystem translate: " + entityProperties.getTranslate());
//        }

        int tcpLength = netManager.sendTCP(clientFrameTCP);
        int udpLength = netManager.sendUDP(clientFrameUDP);

//        System.out.println("tcpLen: " + tcpLength);
//        System.out.println("udpLen: " + udpLength);

        // Clear the frames now. We have sent the cummulated data and no longer need it.
        clientFrameTCP.clear();

        ++dataSentPerSecond;
        if (ENG_Utility.hasTimePassed(dataSentPerSecondBeginTime, 1000)) {
//            System.out.println("dataSentPerSecond: " + dataSentPerSecond);
            dataSentPerSecond = 0;
            dataSentPerSecondBeginTime = ENG_Utility.currentTimeMillis();
        }
    }
}
