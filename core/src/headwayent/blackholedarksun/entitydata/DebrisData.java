/*
 * Created by Sebastian Bugiu on 08/04/2025, 21:54
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 08/04/2025, 21:54
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.entitydata;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import headwayent.blackholedarksun.EntityData;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.levelresource.LevelObject;
import headwayent.blackholedarksun.loaders.DebrisDataCompiler;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Utility;

public class DebrisData extends EntityData {

    public enum DebrisType {
        SMALL(0), MEDIUM(1), LARGE(2);

        private final int type;

        DebrisType(int i) {
            type = i;
        }

        public int getType() {
            return type;
        }

        public static DebrisType getType(String type) {
            if (type.equalsIgnoreCase("SMALL")) {
                return SMALL;
            } else if (type.equalsIgnoreCase("MEDIUM")) {
                return MEDIUM;
            } else if (type.equalsIgnoreCase("LARGE")) {
                return LARGE;
            }
            throw new IllegalArgumentException(type + " is not a valid debris type!");
        }
    }

//    private static ArrayList<ArrayList<DebrisData>> debrisDataList = new ArrayList<>();
    private static long randomSelection;
    private static EnumMap<DebrisType, EnumMap<LevelObject.LevelObjectType, ArrayList<DebrisData>>> debrisDataMap = new EnumMap<>(DebrisType.class);

    public long lifetime;
    public LevelObject.LevelObjectType type;
    public DebrisType debrisType;

    public static HashMap<String, DebrisData> createDebrisMappings() {
        ArrayList<DebrisData> debrisDataList = new DebrisDataCompiler().compile("debris_data_list.txt",
                MainApp.getGame().getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
//        debrisDataList.add(new DebrisDataCompiler().compile("debris_data_list.txt",
//                MainApp.getGame().getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD));
//        debrisDataList.add(new DebrisDataCompiler().compile("debris_medium_data_list.txt",
//                MainApp.getGame().getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD));
//        debrisDataList.add(new DebrisDataCompiler().compile("debris_large_data_list.txt",
//                MainApp.getGame().getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD));

//        WorldManagerBase worldManagerBase = WorldManagerBase.getSingleton();
        HashMap<String, DebrisData> map = new HashMap<>();
        for (DebrisData debrisData : debrisDataList) {
            map.put(debrisData.name, debrisData);
            if (debrisData.filename == null || debrisData.filename.isEmpty()) {
                debrisData.filename = debrisData.name + ".mesh";
            }
            // Preload.
            WorldManagerBase.getEntityAabb(debrisData.filename);
            EnumMap<LevelObject.LevelObjectType, ArrayList<DebrisData>> debrisDataByType = debrisDataMap.get(debrisData.debrisType);
            if (debrisDataByType == null) {
                debrisDataByType = new EnumMap<>(LevelObject.LevelObjectType.class);
                debrisDataMap.put(debrisData.debrisType, debrisDataByType);
            }
            ArrayList<DebrisData> debrisDataByTypeArr = debrisDataByType.get(debrisData.type);
            if (debrisDataByTypeArr == null) {
                debrisDataByTypeArr = new ArrayList<>();
                debrisDataByType.put(debrisData.type, debrisDataByTypeArr);
            }
            debrisDataByTypeArr.add(debrisData);
        }
        return map;
    }

    public static DebrisData getRandomDebris(LevelObject.LevelObjectType objectType) {
        return getRandomDebris(objectType, DebrisType.SMALL);
    }

    public static DebrisData getRandomDebris(LevelObject.LevelObjectType objectType, DebrisType debrisType) {
        EnumMap<LevelObject.LevelObjectType, ArrayList<DebrisData>> debrisDataByType = debrisDataMap.get(debrisType);
        if (debrisDataByType == null) {
            throw new IllegalArgumentException(debrisType + " not found");
        }
        ArrayList<DebrisData> debrisDataList = debrisDataByType.get(objectType);
        if (debrisDataList == null) {
            throw new IllegalArgumentException(debrisType + " size with: " + objectType + " object type not found");
        }
//        if (debrisDataList.size() - 1 < debrisType.getType()) {
//            throw new IllegalStateException("debrisDataList not initialized");
//        }
//        int i = ENG_Utility.getRandom().nextInt(debrisDataList.get(debrisType.getType()).size());
//        return debrisDataList.get(debrisType.getType()).get(i);
        return debrisDataList.get(ENG_Utility.getRandom().nextInt(
                FrameInterval.DEBRIS_RANDOM_SELECTION + (++randomSelection),
                debrisDataList.size()));
    }
}
