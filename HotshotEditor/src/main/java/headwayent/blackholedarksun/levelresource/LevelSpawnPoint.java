package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Created by sebas on 19.10.2015.
 */
public class LevelSpawnPoint {

    public String name;
    public ShipData.ShipTeam team;
    public final ENG_Vector4D position = new ENG_Vector4D(true);
    public final ENG_Quaternion orientation = new ENG_Quaternion(true);

    public LevelSpawnPoint() {
    }

    public String getName() {
        return name;
    }

    public ShipData.ShipTeam getTeam() {
        return team;
    }

    public ENG_Vector4D getPosition() {
        return position;
    }

    public ENG_Quaternion getOrientation() {
        return orientation;
    }
}
