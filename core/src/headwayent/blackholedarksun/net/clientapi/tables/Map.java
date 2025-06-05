/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:33 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;


/**
 * Created by Sebastian on 28.03.2015.
 */

public class Map {

    private long id;
    private long localId;
    private String mapName;
    private int teamBlueSpawnPointNum;
    private int teamRedSpawnPointNum;

    private String error;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getTeamBlueSpawnPointNum() {
        return teamBlueSpawnPointNum;
    }

    public void setTeamBlueSpawnPointNum(int teamBlueSpawnPointNum) {
        this.teamBlueSpawnPointNum = teamBlueSpawnPointNum;
    }

    public int getTeamRedSpawnPointNum() {
        return teamRedSpawnPointNum;
    }

    public void setTeamRedSpawnPointNum(int teamRedSpawnPointNum) {
        this.teamRedSpawnPointNum = teamRedSpawnPointNum;
    }
}
