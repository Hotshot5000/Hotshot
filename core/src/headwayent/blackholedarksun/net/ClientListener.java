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
import com.esotericsoftware.kryonet.Server;
import headwayent.blackholedarksun.ServerVirtualizationService;
import headwayent.blackholedarksun.net.registeredclasses.*;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerClientFrameUDP;
import headwayent.hotshotengine.ENG_Log;

/**
 * Created by sebas on 24.11.2015.
 */
public class ClientListener extends Listener {
    private final String[] args;
    private WorldManagerServerSide worldManagerServerSide;
    private ServerVirtualizationService serverVirtualizationService;
    private Server server;
    private boolean serverCreated;

    public ClientListener(String[] args) {
        this.args = args;
    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
        System.out.println("ClientListener connected");
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
        System.out.println("ClientListener disconnected");
    }

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);

        if (object instanceof NetBase) {
            NetBase netBase = (NetBase) object;
            switch (netBase.getType()) {
                case SERVER_CONNECTION_REQUEST: {
                    if (!serverCreated) {
                        ServerConnectionRequest serverConnectionRequest = (ServerConnectionRequest) netBase;
                        serverVirtualizationService = new ServerVirtualizationService(server);
                        // We need to pass the listener so we can use it inside the server to set the WorldManagerServerSide.
                        serverVirtualizationService.createServer(serverConnectionRequest, connection, this, args);
                        serverCreated = false; // To allow for rapid retesting of creating the server.
                    } else {
                        // The server connection request is satisfied outside the running server. This means that an user is trying to create
                        // the already created server so we must ignore this.
                        ServerConnectionRequest serverConnectionRequest = (ServerConnectionRequest) netBase;
                        sendErrorResponse(connection, ServerConnectionResponse.ERROR_SERVER_ALREADY_STARTED);
                    }
                }
                break;
                case JOIN_SERVER_CONNECTION_REQUEST: {
                    JoinServerConnectionRequest joinServerConnectionRequest = (JoinServerConnectionRequest) netBase;
                    if (worldManagerServerSide != null) {
                        if (worldManagerServerSide.checkGameType(joinServerConnectionRequest)) {
                            worldManagerServerSide.addPlayer(connection, joinServerConnectionRequest);
                        } else {
                            sendErrorResponse(connection, ServerConnectionResponse.ERROR_GAME_TYPE_NOT_COMPATIBLE);
                        }
                    } else {
                        ENG_Log.getInstance().log("Join server attempt while loading the server", ENG_Log.TYPE_ERROR);
                        sendErrorResponse(connection, ServerConnectionResponse.ERROR_SERVER_NOT_INITIALIZED);
                    }
                }
                break;
                case MULTIPLAYER_CLIENT_FRAME_TCP: {
                    MultiplayerClientFrameTCP multiplayerClientFrameTCP = (MultiplayerClientFrameTCP) netBase;
                    if (worldManagerServerSide != null) {
                        worldManagerServerSide.addClientFrameTCP(multiplayerClientFrameTCP);
                    } else {
                        ENG_Log.getInstance().log("Client frame TCP received while loading the server", ENG_Log.TYPE_ERROR);
                        sendErrorResponse(connection, ServerConnectionResponse.ERROR_SERVER_NOT_JOINED);
                    }
                }
                break;
                case MULTIPLAYER_CLIENT_FRAME_UDP: {
                    MultiplayerClientFrameUDP multiplayerClientFrameUDP = (MultiplayerClientFrameUDP) netBase;
                    if (worldManagerServerSide != null) {
                        worldManagerServerSide.addClientFrameUDP(multiplayerClientFrameUDP);
                    } else {
                        ENG_Log.getInstance().log("Client frame UDP received while loading the server", ENG_Log.TYPE_ERROR);
                        sendErrorResponse(connection, ServerConnectionResponse.ERROR_SERVER_NOT_JOINED);
                    }
                }
                break;
                case SERVER_LEAVE_REQUEST: {
                    ServerLeaveRequest serverLeaveRequest = (ServerLeaveRequest) netBase;
                    worldManagerServerSide.removePlayer(serverLeaveRequest.getUserId());
                }
                break;
                case SERVER_RESPAWN_REQUEST: {
                    ServerRespawnRequest serverRespawnRequest = (ServerRespawnRequest) netBase;
                    worldManagerServerSide.addRespawnRequest(serverRespawnRequest);
                }
                break;
                default:
                    throw new IllegalArgumentException("Unknown net object type: " + netBase.getType());
            }
        }
    }

    private void sendErrorResponse(Connection connection, int errorServerNotInitialized) {
        ServerConnectionResponse serverConnectionResponse = new ServerConnectionResponse();
        serverConnectionResponse.setErrorCode(errorServerNotInitialized);
        connection.sendTCP(serverConnectionResponse);
    }

    @Override
    public void idle(Connection connection) {
        super.idle(connection);
    }

    public WorldManagerServerSide getWorldManagerServerSide() {
        return worldManagerServerSide;
    }

    public void setWorldManagerServerSide(WorldManagerServerSide worldManagerServerSide) {
        this.worldManagerServerSide = worldManagerServerSide;
    }

    public ServerVirtualizationService getServerVirtualizationService() {
        return serverVirtualizationService;
    }

    public void setServerVirtualizationService(ServerVirtualizationService serverVirtualizationService) {
        this.serverVirtualizationService = serverVirtualizationService;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void resetServerCreated()  {
        serverCreated = false;
    }
}
