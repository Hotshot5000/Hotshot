package headwayent.blackholedarksun.loaders;

import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.hotshotengine.exception.ENG_InvalidFormatParsingException;
import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_AbstractCompiler;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.io.DataInputStream;
import java.util.ArrayList;

/**
 * Created by sebas on 22.10.2015.
 */
public class WeaponDataCompiler extends ENG_AbstractCompiler<ArrayList<WeaponData>> {

    public static final String WEAPON_DATA = "weapon_data";
    public static final String WEAPON = "weapon";
    public static final String WEAPON_TYPE = "weapon_type";
    public static final String DAMAGE = "damage";
    public static final String MAX_DISTANCE = "max_distance";
    public static final String WEIGHT = "weight";
    public static final String DEFAULT_MISSILE_NUMBER = "default_missile_number";
    public static final String COOLDOWN_TIME = "cooldown_time";
    public static final String ENEMY_SELECTION_TIME = "enemy_selection_time";
    public static final String TRACKING_DELAY = "tracking_delay";

    @Override
    public ArrayList<WeaponData> compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<WeaponData> weaponDataList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(EntityDataCompiler.DATA)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(WEAPON_DATA)) {
                        parseDataList(fp0, weaponDataList);
                    }
                }
            }
            return weaponDataList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    private void parseDataList(DataInputStream fp0, ArrayList<WeaponData> weaponDataList) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;

            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(WEAPON)) {
                    weaponDataList.add(parseWeaponData(fp0));
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
        }
    }

    private WeaponData parseWeaponData(DataInputStream fp0) {
        ShipData shipData = new ShipData();
        String weaponName = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(weaponName);
        shipData.inGameName = weaponName;
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            WeaponData weaponData = new WeaponData();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (EntityDataCompiler.parseEntityData(fp0, s, weaponData)) {
                } else if (s.equalsIgnoreCase(WEAPON_TYPE)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    weaponData.weaponType = WeaponData.WeaponType.getValueOf(s);
                } else if (s.equalsIgnoreCase(DAMAGE)) {
                    weaponData.damage = getInt(fp0);
                } else if (s.equalsIgnoreCase(MAX_DISTANCE)) {
                    weaponData.maxDistance = getFloat(fp0);
                } else if (s.equalsIgnoreCase(WEIGHT)) {
                    weaponData.weight = getFloat(fp0);
                } else if (s.equalsIgnoreCase(DEFAULT_MISSILE_NUMBER)) {
                    weaponData.defaultMissileNumber = getInt(fp0);
                } else if (s.equalsIgnoreCase(COOLDOWN_TIME)) {
                    weaponData.cooldownTime = getLong(fp0);
                } else if (s.equalsIgnoreCase(ENEMY_SELECTION_TIME)) {
                    weaponData.enemySelectionTime = getLong(fp0);
                } else if (s.equalsIgnoreCase(TRACKING_DELAY)) {
                    weaponData.trackingDelay = getLong(fp0);
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return weaponData;
        }
        throw new ENG_InvalidFormatParsingException();
    }
}
