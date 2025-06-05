/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:36 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.components;

import com.artemis.Component;
import com.esotericsoftware.kryonet.Connection;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameTCP;
import headwayent.blackholedarksun.multiplayer.MultiplayerServerFrameUDP;
import headwayent.blackholedarksun.multiplayer.rmi.UserStats;

import java.util.HashMap;

/**
 * Created by sebas on 10.11.2015.
 */
public class PlayerState extends Component {

    public static final int CLIENT_DROP_FRAMES_LIMIT = 800;
    private static final int UNINITIALIZED_CLIENT_DROP_FRAMES_LIMIT = 1600;

    private long userId;
    private transient Connection connection;
    private String shipName;
    private final HashMap<Long, Long> serverToClientProjectileIdMap = new HashMap<>();
    private final HashMap<Long, Long> clientToServerProjectileIdMap = new HashMap<>();
    
    private MultiplayerServerFrameTCP multiplayerServerFrameTCP = new MultiplayerServerFrameTCP();
    private MultiplayerServerFrameUDP multiplayerServerFrameUDP = new MultiplayerServerFrameUDP();

    private UserStats userStats = new UserStats();
    private int lastKills; // The registered kills before the last death.

    private int tcpDroppedFrames;
    private int udpDroppedFrames;

    private long dataReceivedForCurrentFrameFrameNumTCP = -1;
    private long dataReceivedForCurrentFrameFrameNumUDP = -1;
    // Used to differentiate between the just added player that needs to receive the full server info
    // and the sending of data to all players when a new entity gets created.
    // Without this we also send the data to the just added new player uselessly.
    private boolean justAdded;
    private boolean clientInitialized;
    private boolean dataReceivedForCurrentFrameTCP;
    private boolean dataReceivedForCurrentFrameUDP;


    public PlayerState() {

    }

    public void set(PlayerState playerState) {
        this.userId = playerState.userId;
        this.connection = playerState.connection;
        this.shipName = playerState.shipName;
        this.serverToClientProjectileIdMap.putAll(playerState.serverToClientProjectileIdMap);
        this.clientToServerProjectileIdMap.putAll(playerState.clientToServerProjectileIdMap);
        this.multiplayerServerFrameTCP = playerState.multiplayerServerFrameTCP;
        this.multiplayerServerFrameUDP = playerState.multiplayerServerFrameUDP;
        this.userStats = playerState.userStats;
        this.lastKills = playerState.lastKills;
        this.tcpDroppedFrames = playerState.tcpDroppedFrames;
        this.udpDroppedFrames = playerState.udpDroppedFrames;
        this.justAdded = playerState.justAdded;
        this.clientInitialized = playerState.clientInitialized;
        this.dataReceivedForCurrentFrameFrameNumTCP = playerState.dataReceivedForCurrentFrameFrameNumTCP;
        this.dataReceivedForCurrentFrameFrameNumUDP = playerState.dataReceivedForCurrentFrameFrameNumUDP;
        this.dataReceivedForCurrentFrameTCP = playerState.dataReceivedForCurrentFrameTCP;
        this.dataReceivedForCurrentFrameUDP = playerState.dataReceivedForCurrentFrameUDP;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        userStats.setUserId(userId);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public boolean isJustAdded() {
        return justAdded;
    }

    public void setJustAdded(boolean justAdded) {
        this.justAdded = justAdded;
    }

    public void addProjectileMapping(Long serverId, Long clientId) {
        Long put = serverToClientProjectileIdMap.put(serverId, clientId);
        if (put != null) {
            throw new IllegalArgumentException("serverId: " + serverId + " clientId: " + clientId + " already exist");
        }
        Long put1 = clientToServerProjectileIdMap.put(clientId, serverId);
        if (put1 != null) {
            throw new IllegalArgumentException("serverId: " + serverId + " clientId: " + clientId + " already exist");
        }
    }

    public void removeProjectileMapping(Long serverId) {
        Long clientId = serverToClientProjectileIdMap.remove(serverId);
        if (clientId == null) {
            throw new IllegalArgumentException("Projectile with serverId: " + serverId + " is missing");
        }
        Long remove = clientToServerProjectileIdMap.remove(clientId);
        if (remove == null) {
            throw new IllegalArgumentException("Projectile with clientId: " + clientId + " is missing");
        }
    }

    public void removeAllProjectileMappings() {
        serverToClientProjectileIdMap.clear();
        clientToServerProjectileIdMap.clear();
    }

    public Long getClientProjectileMapping(Long serverId) {
        return serverToClientProjectileIdMap.get(serverId);
    }

    public Long getServerProjectileMapping(Long clientId) {
        return clientToServerProjectileIdMap.get(clientId);
    }

    public boolean isDataReceivedForCurrentFrameTCP() {
        return dataReceivedForCurrentFrameTCP;
    }

    public void setDataReceivedForCurrentFrameTCP(boolean dataReceivedForCurrentFrameTCP, long frameNum) {
        this.dataReceivedForCurrentFrameTCP = dataReceivedForCurrentFrameTCP;
        if (frameNum >= dataReceivedForCurrentFrameFrameNumTCP) {
            this.dataReceivedForCurrentFrameFrameNumTCP = frameNum;
        } else {
            if (frameNum != -1) {
                // How is this possible in TCP??
                throw new IllegalArgumentException(frameNum + " smaller than last frame num: " + dataReceivedForCurrentFrameFrameNumTCP);
            }
        }
    }

    public boolean isDataReceivedForCurrentFrameUDP() {
        return dataReceivedForCurrentFrameUDP;
    }

    public void setDataReceivedForCurrentFrameUDP(boolean dataReceivedForCurrentFrameUDP, long frameNum) {
        this.dataReceivedForCurrentFrameUDP = dataReceivedForCurrentFrameUDP;
        if (frameNum > dataReceivedForCurrentFrameFrameNumUDP) {
            this.dataReceivedForCurrentFrameFrameNumUDP = frameNum;
        }
    }

    public boolean isDataReceivedForCurrentFrame() {
        return dataReceivedForCurrentFrameTCP || dataReceivedForCurrentFrameUDP;
    }

    public long getDataReceivedForCurrentFrameFrameNumTCP() {
        return dataReceivedForCurrentFrameFrameNumTCP;
    }

    public long getDataReceivedForCurrentFrameFrameNumUDP() {
        return dataReceivedForCurrentFrameFrameNumUDP;
    }

    public boolean isClientInitialized() {
        return clientInitialized;
    }

    public void setClientInitialized(boolean clientInitialized) {
        this.clientInitialized = clientInitialized;
    }

    public void updateDroppedFrames() {
        tcpDroppedFrames = checkDroppedFrames(tcpDroppedFrames, dataReceivedForCurrentFrameTCP);
        udpDroppedFrames = checkDroppedFrames(udpDroppedFrames, dataReceivedForCurrentFrameUDP);
//        System.out.println("tcpDroppedFrames: " + tcpDroppedFrames + " udpDroppedFrames: " + udpDroppedFrames + " userId: " + userId);
    }

    private int checkDroppedFrames(int droppedFrames, boolean dataReceived) {
        if (dataReceived) {
            droppedFrames = 0;
        } else {
            ++droppedFrames;
        }
        return droppedFrames;
    }

    public boolean shouldRemovePlayerForTCP() {
        return clientInitialized ? tcpDroppedFrames >= CLIENT_DROP_FRAMES_LIMIT : tcpDroppedFrames >= UNINITIALIZED_CLIENT_DROP_FRAMES_LIMIT;
    }

    public boolean shouldRemovePlayerForUDP() {
        return clientInitialized ? udpDroppedFrames >= CLIENT_DROP_FRAMES_LIMIT : udpDroppedFrames >= UNINITIALIZED_CLIENT_DROP_FRAMES_LIMIT;
    }

    public boolean shouldRemovePlayerForDroppedFrames() {
        return shouldRemovePlayerForTCP() || shouldRemovePlayerForUDP();
    }

    public MultiplayerServerFrameTCP getMultiplayerServerFrameTCP() {
        return multiplayerServerFrameTCP;
    }

    public MultiplayerServerFrameUDP getMultiplayerServerFrameUDP() {
        return multiplayerServerFrameUDP;
    }

    public int getLastKills() {
        return lastKills;
    }

    public void setLastKills(int lastKills) {
        this.lastKills = lastKills;
    }

    public UserStats getUserStats() {
        return userStats;
    }
}
