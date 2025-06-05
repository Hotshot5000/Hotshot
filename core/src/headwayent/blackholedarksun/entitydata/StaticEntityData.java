/*
 * Created by Sebastian Bugiu on 08/04/2025, 21:54
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 08/04/2025, 21:54
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.entitydata;

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.blackholedarksun.EntityData;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.loaders.DebrisDataCompiler;
import headwayent.blackholedarksun.loaders.StaticEntityDataCompiler;
import headwayent.blackholedarksun.world.WorldManagerBase;

public class StaticEntityData extends EntityData {

    public static HashMap<String, StaticEntityData> createDebrisMappings() {
        ArrayList<StaticEntityData> staticEntityDataList = new StaticEntityDataCompiler().compile("static_entity_data_list.txt",
                MainApp.getGame().getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);

        HashMap<String, StaticEntityData> map = new HashMap<>();
        for (StaticEntityData staticEntityData : staticEntityDataList) {
            map.put(staticEntityData.name, staticEntityData);
        }
        return map;

    }
}
