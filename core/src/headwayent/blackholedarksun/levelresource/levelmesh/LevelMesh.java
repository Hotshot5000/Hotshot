/*
 * Created by Sebastian Bugiu on 01/07/24, 17:56
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 01/07/24, 17:56
 * Copyright (c) 2024.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelmesh;

import java.util.ArrayList;

public class LevelMesh {

    public String levelMeshName;
    public String meshName;
    public String baseNodeName;
    public ArrayList<LevelZone> levelZones = new ArrayList<>();
    public ArrayList<LevelPortal> levelPortals = new ArrayList<>();
}
