package headwayent.blackholedarksun.entitydata;

import java.util.ArrayList;
import java.util.HashMap;

import headwayent.blackholedarksun.EntityData;
//import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.loaders.AsteroidDataCompiler;
//import headwayent.blackholedarksun.world.WorldManagerBase;

/**
 * Created by sebas on 02-Oct-17.
 */

public class AsteroidData extends EntityData {

    public static HashMap<String, AsteroidData> createAsteroidMappings() {
        ArrayList<AsteroidData> asteroidDataList = new AsteroidDataCompiler().compile("asteroid_data_list.txt", "", false);

        HashMap<String, AsteroidData> map = new HashMap<>();
        for (AsteroidData asteroidData : asteroidDataList) {
            map.put(asteroidData.name, asteroidData);
        }
        return map;

    }
}
