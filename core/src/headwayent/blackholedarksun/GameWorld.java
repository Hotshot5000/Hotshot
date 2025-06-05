/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import headwayent.blackholedarksun.levelresource.LevelBase;

import com.artemis.World;
import com.artemis.WorldConfiguration;

public class GameWorld extends World {

    /**
     * The maximum distance allowed to travel on x or y or z for any entity
     */
    public static final float MAX_DISTANCE = 10000.0f;
    public static final float SECTION_SIZE = 1000.0f;
    private static GameWorld world;
    private LevelBase currentLevel;

    public GameWorld(WorldConfiguration worldConfiguration) {
        super(worldConfiguration);
//        if (world == null) {
//            world = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
        world = this;
    }

    public String getLevelName() {
        return currentLevel.name;
    }

    public LevelBase getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(LevelBase level) {
        this.currentLevel = level;
    }

    public static GameWorld getWorld() {
        if (world == null && MainActivity.isDebugmode()) {
            throw new NullPointerException("GameWorld not initialized");
        }
        return world;
    }
}
