/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/3/17, 5:56 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Created by sebas on 19.10.2015.
 */
public class LevelSpawnPoint {

    public String name;
    public ShipData.ShipTeam team;
    public final ENG_Vector4D position = new ENG_Vector4D(true);
    public final ENG_Quaternion orientation = new ENG_Quaternion(true);
}
