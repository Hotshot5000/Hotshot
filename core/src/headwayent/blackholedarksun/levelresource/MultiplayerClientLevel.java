/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/1/21, 9:45 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

/**
 * Created by sebas on 30.11.2015.
 */
public class MultiplayerClientLevel extends LevelBase {

    public MultiplayerClientLevelStart levelStart;
    public final ArrayList<MultiplayerClientLevelEvent> levelEventList = new ArrayList<>();

    public MultiplayerClientLevel() {
        multiplayer = true;
    }

    @Override
    public void setLevelStart(LevelStart levelStart) {
        this.levelStart = (MultiplayerClientLevelStart) levelStart;
    }

    @Override
    public LevelStart getLevelStart() {
        return levelStart;
    }

    @Override
    public void addLevelEvent(LevelEvent levelEvent) {
        levelEventList.add((MultiplayerClientLevelEvent) levelEvent);
    }

    @Override
    public LevelEvent getLevelEvent(int num) {
        return levelEventList.get(num);
    }

    @Override
    public int getLevelEventNum() {
        return levelEventList.size();
    }


}
