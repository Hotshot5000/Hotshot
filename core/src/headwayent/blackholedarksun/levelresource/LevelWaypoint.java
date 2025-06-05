/*
 * Created by Sebastian Bugiu on 16/02/2025, 18:30
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 18:30
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

import headwayent.hotshotengine.ENG_Vector4D;

public class LevelWaypoint {

    public int id = -1;
    public ENG_Vector4D position = new ENG_Vector4D(true);
    public ArrayList<Integer> nextIds = new ArrayList<>();
    public int maxWaypointAttachmentCount = 3;
    public float radius = 50.0f;
    public float weight = 1.0f;
    public ENG_Vector4D entranceOrExitDirection = new ENG_Vector4D();
    public float entranceOrExitAngle; // In Degrees.
    public float entranceOrExitMinDistance;
    public boolean entranceOrExitActive;
    public boolean active = true;
}
