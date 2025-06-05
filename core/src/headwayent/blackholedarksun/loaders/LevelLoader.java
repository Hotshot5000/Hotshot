/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/1/21, 9:45 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.loaders;

import headwayent.blackholedarksun.*;
import headwayent.blackholedarksun.levelresource.LevelBase;
import headwayent.blackholedarksun.statistics.InGameStatistics;
import headwayent.blackholedarksun.statistics.InGameStatisticsManager;
import headwayent.blackholedarksun.statistics.LevelStatistics;
import headwayent.blackholedarksun.statistics.SessionStatistics;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.hotshotengine.ENG_DateUtils;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.util.ArrayList;

public class LevelLoader {

//	private static ArrayList<String> levelList;

    public static ArrayList<String> loadLevelList(String fileName, String path) {
        return ENG_CompilerUtil.loadListFromFile(fileName, path);
    }

    public static void loadLevel(int levelNum, ArrayList<String> levelList, boolean multiplayer) {

        loadLevel(compileLevel(levelNum, levelList, multiplayer));
    }

    public static LevelBase compileLevel(int levelNum, ArrayList<String> levelList, boolean multiplayer) {
        if (levelList == null) {
            throw new NullPointerException("Init the level list first by calling loadLevelList()");
        }
        if (levelNum < 0 || levelNum >= levelList.size()) {
            throw new IllegalArgumentException(levelNum + " is an invalid levelnumber. Current maximum level is " + levelList.size());
        }

        String[] pathAndFileName = ENG_CompilerUtil.getPathAndFileName(levelList.get(levelNum));
        return compileLevel(pathAndFileName[1], pathAndFileName[0], multiplayer);
    }

    public static LevelBase compileLevel(String fileName, String path, boolean multiplayer) {
        return new LevelCompiler(multiplayer).compile(fileName, path, true);
    }

    public static void loadLevel(LevelBase level) {

        WorldManagerBase mgr = MainApp.getMainThread().getApplicationSettings().applicationMode == MainApp.Mode.CLIENT ?
                WorldManager.getSingleton() :
                WorldManagerServerSide.getSingleton();
        GameWorld world = GameWorld.getWorld();
        world.setCurrentLevel(level);
        mgr.loadLevel();
        //	mgr.prepareLevel();

        InGameStatistics statistics = InGameStatisticsManager.getInstance().getInGameStatistics();
        SessionStatistics latestSessionStatistics = statistics.getLatestSessionStatistics();
        if (latestSessionStatistics != null) {
            LevelStatistics levelStatistics = new LevelStatistics();
            levelStatistics.levelName = level.name;
            levelStatistics.levelStartDate = ENG_DateUtils.getCurrentDateTimestamp();
            levelStatistics.multiplayer = level.multiplayer;
            latestSessionStatistics.levelStatisticsList.add(levelStatistics);
        }
    }


//	public static ArrayList<String> getLevelList() {
//		return levelList;
//	}
}
