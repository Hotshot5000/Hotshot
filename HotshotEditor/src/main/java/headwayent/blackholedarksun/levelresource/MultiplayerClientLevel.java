package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

/**
 * Created by sebas on 30.11.2015.
 */
public class MultiplayerClientLevel extends LevelBase {

    public MultiplayerClientLevelStart levelStart;
    public final ArrayList<MultiplayerClientLevelEvent> levelEventList = new ArrayList<>();

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
