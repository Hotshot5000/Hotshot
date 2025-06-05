/*
 * Created by Sebastian Bugiu on 16/02/2025, 18:30
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 18:30
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

import headwayent.hotshotengine.ENG_AxisAlignedBox;

public class LevelWaypointSector {

    public int id = -1;
    public int nextWaypointId = 1;
    public ENG_AxisAlignedBox box = new ENG_AxisAlignedBox();
    public ArrayList<LevelWaypoint> waypoints = new ArrayList<>();
    public ArrayList<Integer> nextSectorIds = new ArrayList<>();
    public int maxTotalWaypointAttachmentCount = 10;
    public int[][] waypointTable;
    public ArrayList<ArrayList<Integer>> waypointTableList = new ArrayList<>();

    public LevelWaypointSector() {
    }

    public int getId() {
        return id;
    }

    public int getNextWaypointId() {
        return nextWaypointId;
    }

    public ENG_AxisAlignedBox getBox() {
        return box;
    }

    public ArrayList<LevelWaypoint> getWaypoints() {
        return waypoints;
    }

    public ArrayList<Integer> getNextSectorIds() {
        return nextSectorIds;
    }

    public int getMaxTotalWaypointAttachmentCount() {
        return maxTotalWaypointAttachmentCount;
    }

    public int[][] getWaypointTable() {
        return waypointTable;
    }

    public ArrayList<ArrayList<Integer>> getWaypointTableList() {
        return waypointTableList;
    }
}
