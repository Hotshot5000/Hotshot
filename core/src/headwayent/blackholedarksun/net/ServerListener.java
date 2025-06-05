/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/25/21, 1:21 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import headwayent.blackholedarksun.multiplayer.components.MultiplayerEntityTCP;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerMP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDP;
import headwayent.blackholedarksun.net.registeredclasses.NetBase;
import headwayent.blackholedarksun.net.registeredclasses.ServerConnectionResponse;
import headwayent.hotshotengine.ENG_Utility;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sebas on 24.11.2015.
 */
public class ServerListener extends Listener {

    private ServerConnectionResponse serverConnectionResponse;
    private CountDownLatch latch;
    private final LinkedList<MultiplayerServerFrameTCP> tcpFramesQueue = new LinkedList<>();
    private final LinkedList<MultiplayerServerFrameUDP> udpFramesQueue = new LinkedList<>();
    private final AtomicBoolean waitForLoading = new AtomicBoolean(true);

    public ServerListener() {

    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
        System.out.println("ServerListener connected");
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
        System.out.println("ServerListener disconnected");
    }

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);
        if (object instanceof NetBase) {
            NetBase base = (NetBase) object;
            base.setReceivedTimestamp(ENG_Utility.currentTimeMillis());
            switch (base.getType()) {
                case SERVER_CONNECTION_RESPONSE: {
                    // We have a response from server check if everything is OK. If all good then we can expect
                    // more incoming data.
                    serverConnectionResponse = (ServerConnectionResponse) base;
                    NetManager.getSingleton().setServerConnectionResponse(serverConnectionResponse);
                    latch.countDown();
                }
                break;
                case MULTIPLAYER_SERVER_FRAME_TCP: {
                    MultiplayerServerFrameTCP multiplayerServerFrameTCP = (MultiplayerServerFrameTCP) base;
                    if (waitForLoading.get()) {
                        tcpFramesQueue.add(multiplayerServerFrameTCP);
                    } else {
                        WorldManagerMP worldManager = (WorldManagerMP) WorldManager.getSingleton();
                        MultiplayerServerFrameTCP serverFrameTCP;
                        while ((serverFrameTCP = tcpFramesQueue.poll()) != null) {
                            for (MultiplayerEntityTCP multiplayerEntityTCP : serverFrameTCP.getAddedEntities()) {
                                System.out.println("Receiving entity from tcp queue: "
                                        + multiplayerEntityTCP.getEntityProperties().getName() + " from connection:" + connection);
                            }

                            worldManager.addServerFrameTCP(serverFrameTCP);
                        }
                        tcpFramesQueue.clear();

                        worldManager.addServerFrameTCP(multiplayerServerFrameTCP);

                        for (MultiplayerEntityTCP multiplayerEntityTCP : multiplayerServerFrameTCP.getAddedEntities()) {
                            System.out.println("Receiving entity: "
                                    + multiplayerEntityTCP.getEntityProperties().getName() + " from connection:" + connection);
                        }

                    }
                }
                break;
                case MULTIPLAYER_SERVER_FRAME_UDP: {
                    MultiplayerServerFrameUDP multiplayerServerFrameUDP = (MultiplayerServerFrameUDP) base;
//                    System.out.println("time diff: " + (ENG_Utility.currentTimeMillis() - multiplayerServerFrameUDP.getTimestamp()));
                    if (waitForLoading.get()) {
                        udpFramesQueue.add(multiplayerServerFrameUDP);
                    } else {
                        WorldManagerMP worldManager = (WorldManagerMP) WorldManager.getSingleton();
                        MultiplayerServerFrameUDP serverFrameUDP;
                        while ((serverFrameUDP = udpFramesQueue.poll()) != null) {
                            worldManager.addServerFrameUDP(serverFrameUDP);
                        }

                        worldManager.addServerFrameUDP(multiplayerServerFrameUDP);
                    }

                }
                break;
                case SERVER_LEAVE_REQUEST: {

                }
                break;
                default:
                    throw new IllegalArgumentException("Unknown net object type: " + base.getType());
            }
        }
    }

    @Override
    public void idle(Connection connection) {
        super.idle(connection);
    }

    public ServerConnectionResponse getServerConnectionResponse() {
        return serverConnectionResponse;
    }

    public void setServerConnectionResponseLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public void setWaitForLoading(boolean waitForLoading) {
        this.waitForLoading.set(waitForLoading);
    }
}
