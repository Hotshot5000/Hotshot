/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:33 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;

/**
 * Created by sebas on 14.10.2015.
 */
public class Server extends GenericTransient {

    private long id;
    private String serverName;
    private String ip;
    private String tcpPort;
    private String udpPort;
    private int serverLoad;
    private int sessionNum;
    private int userNum;
    private int online;
    private int sessionId;
    private long mapId;
    private int spawnPoint;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(String tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(String udpPort) {
        this.udpPort = udpPort;
    }

    public int getServerLoad() {
        return serverLoad;
    }

    public void setServerLoad(int serverLoad) {
        this.serverLoad = serverLoad;
    }

    public int getSessionNum() {
        return sessionNum;
    }

    public void setSessionNum(int sessionNum) {
        this.sessionNum = sessionNum;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public boolean isOnlineBool() {
        return online == 1;
    }

    public void setOnlineBool(boolean b) {
        online = b ? 1 : 0;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public int getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(int spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
}
