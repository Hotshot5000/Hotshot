package headwayent.blackholedarksun.levelresource;

import java.util.HashMap;

/**
 * Created by sebas on 30.11.2015.
 */
public class MultiplayerClientLevelEvent extends LevelEvent {

    public final HashMap<String, LevelObject> spawnMap = new HashMap<>();

    public void initializeMaps() {
        for (LevelObject levelObject : spawn) {
            spawnMap.put(levelObject.name, levelObject);
        }

    }
}
