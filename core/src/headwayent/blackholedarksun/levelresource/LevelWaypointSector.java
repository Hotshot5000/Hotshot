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
    public ENG_AxisAlignedBox box = new ENG_AxisAlignedBox();
    public ArrayList<LevelWaypoint> waypoints = new ArrayList<>();
    public ArrayList<Integer> nextSectorIds = new ArrayList<>();
    public int maxTotalWaypointAttachmentCount = 10;
    public int[][] waypointTable;
}
