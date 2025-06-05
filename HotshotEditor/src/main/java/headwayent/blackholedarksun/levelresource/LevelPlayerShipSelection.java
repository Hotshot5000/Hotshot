package headwayent.blackholedarksun.levelresource;

import headwayent.blackholedarksun.entitydata.ShipData;

import java.util.ArrayList;

/**
 * Created by sebas on 21.10.2015.
 */
public class LevelPlayerShipSelection {

    public ShipData.ShipTeam team;
    public final ArrayList<String> shipLoaderFilenameList = new ArrayList<>();
    public final ArrayList<String> shipNameList = new ArrayList<>();

    public LevelPlayerShipSelection() {
    }

    public ShipData.ShipTeam getTeam() {
        return team;
    }

    public ArrayList<String> getShipLoaderFilenameList() {
        return shipLoaderFilenameList;
    }

    public ArrayList<String> getShipNameList() {
        return shipNameList;
    }
}
