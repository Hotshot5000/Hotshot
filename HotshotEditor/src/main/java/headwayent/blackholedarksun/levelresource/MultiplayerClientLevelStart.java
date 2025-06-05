package headwayent.blackholedarksun.levelresource;

import java.util.HashMap;

/**
 * Created by sebas on 30.11.2015.
 */
public class MultiplayerClientLevelStart extends LevelStart {

    public final HashMap<String, LevelObject> levelObjectMap = new HashMap<>();
    public final HashMap<String, LevelObject> playerShipSelectionMap = new HashMap<>();

    public void initializeMaps() {
        for (LevelObject startObject : startObjects) {
            levelObjectMap.put(startObject.name, startObject);
        }
        for (LevelObject playerShipSelectionObject : playerShipSelectionObjects) {
            playerShipSelectionMap.put(playerShipSelectionObject.name, playerShipSelectionObject);
        }

    }
}
