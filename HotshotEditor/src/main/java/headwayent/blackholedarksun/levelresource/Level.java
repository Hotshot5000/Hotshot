package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

//import headwayent.blackholedarksun.parser.ast.Cutscene;

public class Level extends LevelBase {

    public LevelStart levelStart;
    public final ArrayList<LevelEvent> levelEventList = new ArrayList<>();
    public final ArrayList<String> cutsceneNameList = new ArrayList<>();
    //    public final ArrayList<Cutscene> cutsceneList = new ArrayList<>();
    public boolean cutsceneListLoaded;

    public Level() {
    }

    @Override
    public void setLevelStart(LevelStart levelStart) {
        this.levelStart = levelStart;
    }

    @Override
    public LevelStart getLevelStart() {
        return levelStart;
    }

    @Override
    public void addLevelEvent(LevelEvent levelEvent) {
        levelEventList.add(levelEvent);
    }

    @Override
    public LevelEvent getLevelEvent(int num) {
        return levelEventList.get(num);
    }

    @Override
    public int getLevelEventNum() {
        return levelEventList.size();
    }

    public ArrayList<LevelEvent> getLevelEventList() {
        return levelEventList;
    }

    //        public LevelEvent.EventState levelEventState = LevelEvent.EventState.NONE;


}
