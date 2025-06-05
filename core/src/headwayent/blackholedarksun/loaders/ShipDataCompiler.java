/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/2/21, 12:03 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

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
public class ShipDataCompiler extends ENG_AbstractCompiler<ArrayList<ShipData>> {

    public static final String SHIP_DATA = "ship_data";
    public static final String SHIP = "ship";
    public static final String TEAM = "team";
    public static final String SHIP_TYPE = "ship_type";
    public static final String WEAPON_LIST = "weapon_list";
    public static final String ARMOR = "armor";
    public static final String AFTERBURNER_MAX_SPEED_COEFICIENT = "afterburner_max_speed_coeficient";
    public static final String AFTERBURNER_TIME = "afterburner_time";
    public static final String AFTERBURNER_COOLDOWN_TIME = "afterburner_cooldown_time";
    public static final String ENGINE_SOUND_NAME = "engine_sound_name";

    @Override
    public ArrayList<ShipData> compileImpl(String fileName, String path, boolean fromSDCard) {
        DataInputStream fp0 = null;
        try {
            String fileAsString = ENG_Resource.getFileAsString(fileName, path, fromSDCard);
//            System.out.println("ship_data_list.txt");
//            System.out.println(fileAsString);
            fp0 = ENG_Resource.getFileAsStream(fileName, path, fromSDCard);
            String s;
            ArrayList<ShipData> shipDataList = new ArrayList<>();
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(EntityDataCompiler.DATA)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    if (s.equalsIgnoreCase(SHIP_DATA)) {
                        parseDataList(fp0, shipDataList);
                    }
                }
            }
            return shipDataList;
        } finally {
            ENG_CompilerUtil.close(fp0);
        }
    }

    private void parseDataList(DataInputStream fp0, ArrayList<ShipData> shipDataList) {
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;

            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (s.equalsIgnoreCase(SHIP)) {
                    shipDataList.add(parseShipData(fp0));
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
        }
    }

    private ShipData parseShipData(DataInputStream fp0) {
        ShipData shipData = new ShipData();
        String shipName = ENG_CompilerUtil.getNextWord(fp0);
        checkNull(shipName);
        shipData.inGameName = shipName;
        if (ENG_CompilerUtil.getNextWord(fp0).equals(BRACKET_OPEN)) {
            incrementBracketLevel();
            String s;
            while ((s = ENG_CompilerUtil.getNextWord(fp0)) != null) {
                if (EntityDataCompiler.parseEntityData(fp0, s, shipData)) {
                } else if (s.equalsIgnoreCase(TEAM)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    shipData.team = ShipData.ShipTeam.getValueOf(s);
                } else if (s.equalsIgnoreCase(SHIP_TYPE)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    shipData.shipType = ShipData.ShipType.getValueOf(s);
                } else if (s.equalsIgnoreCase(WEAPON_LIST)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    shipData.weaponTypeList.add(WeaponData.WeaponType.getValueOf(s));
                } else if (s.equalsIgnoreCase(ARMOR)) {
                    shipData.health = (int) getFloat(fp0);
                } else if (s.equalsIgnoreCase(AFTERBURNER_MAX_SPEED_COEFICIENT)) {
                    shipData.afterburnerMaxSpeedCoeficient = getFloat(fp0);
                } else if (s.equalsIgnoreCase(AFTERBURNER_TIME)) {
                    shipData.afterburnerTime = getLong(fp0);
                } else if (s.equalsIgnoreCase(AFTERBURNER_COOLDOWN_TIME)) {
                    shipData.afterburnerCooldownTime = getLong(fp0);
                } else if (s.equalsIgnoreCase(ENGINE_SOUND_NAME)) {
                    s = ENG_CompilerUtil.getNextWord(fp0);
                    checkNull(s);
                    shipData.engineSoundName = s;
                } else if (s.equalsIgnoreCase(BRACKET_CLOSE)) {
                    decrementBracketLevel();
                    break;
                }
            }
            return shipData;
        }
        throw new ENG_InvalidFormatParsingException();
    }
}
