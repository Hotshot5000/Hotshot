/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/1/21, 9:45 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.parser.ast.Cutscene;

/**
 * Created by sebas on 30.11.2015.
 */
public abstract class LevelBase {
    public String name;
    public LevelEnd levelEnd;
    // For world manager
    public Cutscene activeCutscene;
    public Cutscene.CutsceneType currentCutsceneType;
    public boolean levelEnded;
    public boolean cutsceneActive;
    public boolean cutsceneCameraNodeCreated;
    public boolean multiplayer; // For statistics.
    // For WorldManagerServerSide
    public long mapId;
    public boolean levelEndedSent;
    public boolean userStatsUpdated;

    public abstract void setLevelStart(LevelStart levelStart);

    public abstract LevelStart getLevelStart();

    public abstract void addLevelEvent(LevelEvent levelEvent);

    public abstract LevelEvent getLevelEvent(int num);

    public abstract int getLevelEventNum();
}
