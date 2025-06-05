/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:31 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

import headwayent.blackholedarksun.MainApp;

/**
 * Created by sebas on 03.11.2015.
 */
public class JoinServerConnectionRequest extends NetBase {

    private long userId;
    private long sessionId;
    private String shipName;
    private String playerName;
    private MainApp.GameType gameType;
    private int spawnPoint;

    public JoinServerConnectionRequest() {
        super(Type.JOIN_SERVER_CONNECTION_REQUEST);
        setGameType();
    }

    public JoinServerConnectionRequest(Type type) {
        super(type);
        setGameType();
    }

    public MainApp.GameType getGameType() {
        return gameType;
    }

    private void setGameType() {
        gameType = MainApp.GameType.getActiveGameType();
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(int spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
}
