package headwayent.blackholedarksun.levelresource;

//import headwayent.blackholedarksun.parser.ast.Cutscene;

/**
 * Created by sebas on 30.11.2015.
 */
public abstract class LevelBase {
    public String name;
    public LevelEnd levelEnd;
    // For world manager
//    public Cutscene activeCutscene;
//    public Cutscene.CutsceneType currentCutsceneType;
    public boolean levelEnded;
    public boolean cutsceneActive;
    public boolean cutsceneCameraNodeCreated;
    // For WorldManagerServerSide
    public long mapId;
    public boolean levelEndedSent;
    public boolean userStatsUpdated;

    public abstract void setLevelStart(LevelStart levelStart);

    public abstract LevelStart getLevelStart();

    public abstract void addLevelEvent(LevelEvent levelEvent);

    public abstract LevelEvent getLevelEvent(int num);

    public abstract int getLevelEventNum();

    public String getName() {
        return name;
    }

    public LevelEnd getLevelEnd() {
        return levelEnd;
    }

    public boolean isLevelEnded() {
        return levelEnded;
    }

    public boolean isCutsceneActive() {
        return cutsceneActive;
    }

    public boolean isCutsceneCameraNodeCreated() {
        return cutsceneCameraNodeCreated;
    }

    public long getMapId() {
        return mapId;
    }

    public boolean isLevelEndedSent() {
        return levelEndedSent;
    }

    public boolean isUserStatsUpdated() {
        return userStatsUpdated;
    }
}
