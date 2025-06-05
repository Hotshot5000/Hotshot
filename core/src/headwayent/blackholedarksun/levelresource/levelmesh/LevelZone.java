/*
 * Created by Sebastian Bugiu on 01/07/24, 18:03
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 01/07/24, 18:03
 * Copyright (c) 2024.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelmesh;

import java.util.ArrayList;

import headwayent.hotshotengine.ENG_Vector4D;

public class LevelZone {

    public String meshName;
    public String nodeName;
    public String zoneTypeName = "ZoneType_Default";
    public ENG_Vector4D nodePosition = new ENG_Vector4D(true);
    public String zoneName;
    public ArrayList<LevelPortal> levelPortals = new ArrayList<>();
}
