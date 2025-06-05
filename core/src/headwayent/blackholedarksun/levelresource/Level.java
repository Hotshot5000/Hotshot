/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

import headwayent.blackholedarksun.parser.ast.Cutscene;

public class Level extends LevelBase {

    public LevelStart levelStart;
    public final ArrayList<LevelEvent> levelEventList = new ArrayList<>();
    public final ArrayList<String> cutsceneNameList = new ArrayList<>();
    public final ArrayList<Cutscene> cutsceneList = new ArrayList<>();
    public boolean cutsceneListLoaded;

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


    //        public LevelEvent.EventState levelEventState = LevelEvent.EventState.NONE;


}
