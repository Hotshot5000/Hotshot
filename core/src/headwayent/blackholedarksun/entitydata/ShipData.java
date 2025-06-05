/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.entitydata;

import headwayent.blackholedarksun.EntityData;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.loaders.ShipDataCompiler;
import headwayent.blackholedarksun.world.WorldManagerBase;

import java.util.ArrayList;
import java.util.TreeMap;

public class ShipData extends EntityData {

    public static final String SHIP_HUMAN3 = "Jovian Falcon";
    public static final String SHIP_HUMAN2 = "Aldemarin";
    public static final String SHIP_HUMAN1 = "Dark Colbert";
    public static final String SHIP_HUMAN0 = "Arcturus";
    public static final String SHIP_ALIEN0 = "Novgorod";
    public static final String SHIP_ALIEN1 = "Shegl";
    public static final String SHIP_ALIEN2 = "Quixijaba";
    public static final String SHIP_ALIEN3 = "Vshoyot";
    public static final String SHIP_ALIEN4 = "Kotzan";
    public static final float RELOADER_SCAN_RADIUS = 400.0f;
    public static final float RELOADER_SHIP_WEIGHT = 100.0f;
    public static final int COUNTERMEASURES_SOUND_NUM = 1;
    public static final int AFTERBURNER_SOUND_NUM = 1;
    public static final int COUNTERMEASURE_TIME = 5000;

    public enum ShipTeam {
        HUMAN(0), ALIEN(1);

        private final int teamNum;

        ShipTeam(int teamNum) {
            this.teamNum = teamNum;
        }

        public int getTeamNum() {
            return teamNum;
        }

        public static ShipTeam getTeamByNum(int num) {
            switch (num) {
                case 0:
                    return HUMAN;
                case 1:
                    return ALIEN;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public static ShipTeam getOtherTeam(ShipTeam team) {
            switch (team) {
                case HUMAN:
                    return ALIEN;
                case ALIEN:
                    return HUMAN;
                default:
                    throw new IllegalArgumentException("Invalid ship team: " + team);
            }
        }

        public static String getOtherTeamAsString(ShipTeam team) {
            switch (team) {
                case HUMAN:
                    return ALIEN.toString();
                case ALIEN:
                    return HUMAN.toString();
                default:
                    throw new IllegalArgumentException("Invalid ship team: " + team);
            }
        }

        public static ShipTeam getValueOf(String s) {
            if (ALIEN.toString().equalsIgnoreCase(s)) {
                return ALIEN;
            }
            if (HUMAN.toString().equalsIgnoreCase(s)) {
                return HUMAN;
            }
            throw new IllegalArgumentException("No enum constant for " + s);
        }

        public static String getAsString(ShipTeam team) {
            switch (team) {
                case HUMAN:
                    return HUMAN.toString();
                case ALIEN:
                    return ALIEN.toString();
                default:
                    throw new IllegalArgumentException(team + " is invalid");
            }
        }
    }

    public enum ShipType {
        FIGHTER, CARGO, RELOADER;

        public static ShipType getValueOf(String s) {
            if (FIGHTER.toString().equalsIgnoreCase(s) || (FIGHTER + "_ship").equalsIgnoreCase(s)) {
                return FIGHTER;
            } else if (CARGO.toString().equalsIgnoreCase(s) || (CARGO + "_ship").equalsIgnoreCase(s)) {
                return CARGO;
            } else if (RELOADER.toString().equalsIgnoreCase(s) || (RELOADER + "_ship").equalsIgnoreCase(s)) {
                return RELOADER;
            } else {
                throw new IllegalArgumentException(s + " is an invalid ship type");
            }
        }
    }

    public static final int SHIP_DESTROYED_SOUND_NUM = 5;
    public static final int CARGO_SHIP_DESTROYED_SOUND_NUM = 5;

    public static String getShipDestroyedSound(int num) {
        switch (num) {
            case 0:
                return "ship_explosion0";
            case 1:
                return "ship_explosion1";
            case 2:
                return "ship_explosion2";
            case 3:
                return "ship_explosion3";
            case 4:
                return "ship_explosion4";
        }
        throw new IllegalArgumentException(num + " is an invalid sound num");
    }

    public static String getCargoShipDestroyedSound(int num) {
        switch (num) {
            case 0:
                return "cargo_ship_explosion0";
            case 1:
                return "cargo_ship_explosion1";
            case 2:
                return "cargo_ship_explosion2";
            case 3:
                return "cargo_ship_explosion3";
            case 4:
                return "cargo_ship_explosion4";
        }
        throw new IllegalArgumentException(num + " is an invalid sound num");
    }

    public static String getCountermeasuresSoundName(int num) {
        switch (num) {
            case 0:
                return "countermeasures";
        }
        throw new IllegalArgumentException(num + " is an invalid asteroid num");
    }

    public static String getAfterburnerSoundName(int num) {
        switch (num) {
            case 0:
                return "afterburner";
        }
        throw new IllegalArgumentException(num + " is an invalid asteroid num");
    }

    public static String getPortalEnteringSoundName(ShipType type) {
        switch (type) {
            case CARGO:
            case FIGHTER:
            case RELOADER:
                return "portal_entering";

        }
        throw new IllegalArgumentException(type + " is an invalid ship type");
    }

    public static String getPortalExitingSoundName(ShipType type) {
        switch (type) {
            case CARGO:
            case FIGHTER:
            case RELOADER:
                return "portal_exiting";

        }
        throw new IllegalArgumentException(type + " is an invalid ship type");
    }


    private static final float DEFAULT_AFTERBURNER_MAX_SPEED_COEFICIENT = 2.0f;
    private static final long DEFAULT_AFTERBURNER_TIME = 5000;
    private static final long DEFAULT_AFTERBURNER_COOLDOWN_TIME = 5000;

    public ShipTeam team;
    public ShipType shipType;
    public final ArrayList<WeaponData.WeaponType> weaponTypeList = new ArrayList<>();
    public float armor; // Not used yet. Only health in EntityData is used.
    public float afterburnerMaxSpeedCoeficient =
            DEFAULT_AFTERBURNER_MAX_SPEED_COEFICIENT;
    public long afterburnerTime = DEFAULT_AFTERBURNER_TIME;
    public long afterburnerCooldownTime = DEFAULT_AFTERBURNER_COOLDOWN_TIME;

    // For hud
    public final int maxPercentageAcceleration = 30;
    public final int initialSpeedPercentual = 0;

    public String engineSoundName;

    public static class MapWithFilenameAndName {
//        public final TreeMap<String, ShipData> filenameShipMap =
//                new TreeMap<>();
        public final TreeMap<String, ShipData> nameShipMap =
                new TreeMap<>();
    }

    public static final int TEAM_ALIEN = 0x1;
    public static final int TEAM_HUMAN = 0x2;
    public static final int TEAM_ANY = TEAM_ALIEN | TEAM_HUMAN;

    public static final int TYPE_CARGO = 0x1;
    public static final int TYPE_FIGHTER = 0x2;
    public static final int TYPE_RELOADER = 0x4;
    public static final int TYPE_ANY =
            TYPE_CARGO | TYPE_FIGHTER | TYPE_RELOADER;

//    private static final TreeMap<String, ShipData> filenameShipMap =
//            new TreeMap<>();
//    private static TreeMap<String, ShipData> nameShipMap =
//            new TreeMap<>();

    public static ArrayList<ShipData> getShipData(int team, int type) {
        ArrayList<ShipData> list = new ArrayList<>();
        for (ShipData data : MainApp.getGame().getNameShipMap().values()) {
            boolean teamFound = false;
            boolean typeFound = false;
            if ((team & TEAM_ALIEN) != 0 && data.team == ShipTeam.ALIEN) {
                teamFound = true;
            } else if ((team & TEAM_HUMAN) != 0 && data.team == ShipTeam.HUMAN) {
                teamFound = true;
            }

            if ((type & TYPE_CARGO) != 0 && data.shipType == ShipType.CARGO) {
                typeFound = true;
            } else if ((type & TYPE_FIGHTER) != 0 && data.shipType == ShipType.FIGHTER) {
                typeFound = true;
            } else if ((type & TYPE_RELOADER) != 0 && data.shipType == ShipType.RELOADER) {
                typeFound = true;
            }

            if (teamFound && typeFound) {
                list.add(data);
            }
        }
        return list;
    }

    public static MapWithFilenameAndName createShipMappings() {
//        if (shipMappingsCreated) {
//            throw new ENG_MultipleSingletonConstructAttemptException("ship mappings already created");
//        }
        boolean shipMappingsCreated = true;
        MapWithFilenameAndName map = new MapWithFilenameAndName();

        ArrayList<ShipData> shipDataList = new ShipDataCompiler().compile("ship_data_list.txt",
                MainApp.getGame().getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
        for (ShipData ship : shipDataList) {
//            map.filenameShipMap.put(ship.filename, ship);
            map.nameShipMap.put(ship.inGameName, ship);
            WorldManagerBase.getEntityAabb(ship.filename);
        }

//		// alien0
//		ShipData ship = new ShipData();
//		ship.filename = "ship_alien0.mesh";
//		ship.name = "Draco Tinte";
//		ship.team = ShipTeam.ALIEN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 120.0f;
//		ship.armor = 100.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_alien0_engine";
//		ship.weaponTypeList.add(WeaponType.LASER_RED);
//		ship.weaponTypeList.add(WeaponType.CONCUSSION);
//	//	ship.weaponTypeList.add(WeaponType.HOMING);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// alien1
//		ship = new ShipData();
//		ship.filename = "ship_alien1.mesh";
//		ship.name = "Bloody Grugu";
//		ship.team = ShipTeam.ALIEN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 100.0f;
//		ship.armor = 150.0f;
//		ship.turnAngle = 70.0f;
//		ship.engineSoundName = "ship_alien1_engine";
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_RED);
//		ship.weaponTypeList.add(WeaponType.CONCUSSION);
//		ship.weaponTypeList.add(WeaponData.WeaponType.HOMING);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// alien2
//		ship = new ShipData();
//		ship.filename = "ship_alien2.mesh";
//		ship.name = "Vengeful Ewaldi";
//		ship.team = ShipTeam.ALIEN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 140.0f;
//		ship.armor = 200.0f;
//		ship.turnAngle = 80.0f;
//		ship.engineSoundName = "ship_alien2_engine";
//		ship.weaponTypeList.add(WeaponType.LASER_RED);
//		ship.weaponTypeList.add(WeaponType.LASER_RED_QUAD);
//		ship.weaponTypeList.add(WeaponData.WeaponType.CONCUSSION);
//		ship.weaponTypeList.add(WeaponData.WeaponType.HOMING);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// alien3
//		ship = new ShipData();
//		ship.filename = "ship_alien3.mesh";
//		ship.name = "Gliurkiudooyiaian";
//		ship.team = ShipTeam.ALIEN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 200.0f;
//		ship.armor = 300.0f;
//		ship.turnAngle = 90.0f;
//		ship.engineSoundName = "ship_alien3_engine";
//		ship.weaponTypeList.add(WeaponType.LASER_RED);
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_RED_QUAD);
//		ship.weaponTypeList.add(WeaponData.WeaponType.CONCUSSION);
//		ship.weaponTypeList.add(WeaponType.HOMING);
//		ship.weaponTypeList.add(WeaponType.PLASMA);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// alien4
//		ship = new ShipData();
//		ship.filename = "ship_alien4.mesh";
//		ship.name = "Pollux";
//		ship.team = ShipTeam.ALIEN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 200.0f;
//		ship.armor = 400.0f;
//		ship.turnAngle = 100.0f;
//		ship.engineSoundName = "ship_alien4_engine";
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_RED);
//		ship.weaponTypeList.add(WeaponType.LASER_RED_QUAD);
//		ship.weaponTypeList.add(WeaponType.CONCUSSION);
//		ship.weaponTypeList.add(WeaponType.HOMING);
//		ship.weaponTypeList.add(WeaponData.WeaponType.PLASMA);
//		ship.weaponTypeList.add(WeaponData.WeaponType.PIRANHA);
//                ship.weaponTypeList.add(WeaponData.WeaponType.MEGA);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// alien5
//		ship = new ShipData();
//		ship.filename = "ship_alien_big0.mesh";
//		ship.name = "Druduian Corsair";
//		ship.team = ShipTeam.ALIEN;
//		ship.shipType = ShipType.CARGO;
//		ship.maxSpeed = 10.0f;
//		ship.armor = 1000.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_alien_big0_engine";
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// alien6
//		ship = new ShipData();
//		ship.filename = "ship_alien_big1.mesh";
//		ship.name = "Wolf";
//		ship.team = ShipTeam.ALIEN;
//		ship.shipType = ShipType.CARGO;
//		ship.maxSpeed = 20.0f;
//		ship.armor = 1500.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_alien_big1_engine";
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// human0
//		ship = new ShipData();
//		ship.filename = "ship_human0.mesh";
//		ship.name = SHIP_HUMAN0;
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 120.0f;
//		ship.armor = 100.0f;
//		ship.turnAngle = 110.0f;
//		ship.engineSoundName = "ship_human0_engine";
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_GREEN);
////		ship.weaponTypeList.add(WeaponType.LASER_GREEN_QUAD);
////		ship.weaponTypeList.add(WeaponType.PLASMA);
//		ship.weaponTypeList.add(WeaponType.CONCUSSION);
//		ship.weaponTypeList.add(WeaponType.HOMING);
////		ship.weaponTypeList.add(WeaponType.PIRANHA);
////		ship.weaponTypeList.add(WeaponType.MEGA);
//
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// human1
//		ship = new ShipData();
//		ship.filename = "ship_human1.mesh";
//		ship.name = SHIP_HUMAN1;
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 140.0f;
//		ship.armor = 150.0f;
//		ship.turnAngle = 100.0f;
//		ship.engineSoundName = "ship_human1_engine";
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_GREEN);
//		ship.weaponTypeList.add(WeaponData.WeaponType.CONCUSSION);
//		ship.weaponTypeList.add(WeaponType.HOMING);
//		ship.weaponTypeList.add(WeaponData.WeaponType.PLASMA);
////		ship.weaponTypeList.add(WeaponType.PIRANHA);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// human2
//		ship = new ShipData();
//		ship.filename = "ship_human2.mesh";
//		ship.name = SHIP_HUMAN2;
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 140.0f;
//		ship.armor = 300.0f;
//		ship.turnAngle = 70.0f;
//		ship.engineSoundName = "ship_human2_engine";
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_GREEN);
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_GREEN_QUAD);
//		ship.weaponTypeList.add(WeaponType.CONCUSSION);
//                ship.weaponTypeList.add(WeaponType.PLASMA);
//		ship.weaponTypeList.add(WeaponType.HOMING);
//
//		ship.weaponTypeList.add(WeaponType.PIRANHA);
//                ship.weaponTypeList.add(WeaponData.WeaponType.MEGA);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// human3
//		ship = new ShipData();
//		ship.filename = "ship_human3.mesh";
//		ship.name = SHIP_HUMAN3;
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.FIGHTER;
//		ship.maxSpeed = 150.0f;
//		ship.armor = 400.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_human3_engine";
//		ship.weaponTypeList.add(WeaponType.LASER_GREEN);
//		ship.weaponTypeList.add(WeaponData.WeaponType.LASER_GREEN_QUAD);
//		ship.weaponTypeList.add(WeaponData.WeaponType.CONCUSSION);
//		ship.weaponTypeList.add(WeaponType.HOMING);
//	//	ship.weaponTypeList.add(WeaponType.PLASMA);
//	//	ship.weaponTypeList.add(WeaponType.PIRANHA);
//	//	ship.weaponTypeList.add(WeaponType.MEGA);
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// human4
//		ship = new ShipData();
//		ship.filename = "ship_human_big0.mesh";
//		ship.name = "Polaris Crusher";
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.CARGO;
//		ship.maxSpeed = 10.0f;
//		ship.armor = 1000.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_human_big0_engine";
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// human5
//		ship = new ShipData();
//		ship.filename = "ship_human_big1.mesh";
//		ship.name = "Auriga Eagle";
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.CARGO;
//		ship.maxSpeed = 20.0f;
//		ship.armor = 2000.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_human_big1_engine";
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// human6
//		ship = new ShipData();
//		ship.filename = "ship_human_big2.mesh";
//		ship.name = "human_big2";
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.CARGO;
//		ship.maxSpeed = 20.0f;
//		ship.armor = 1500.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_human_big2_engine";
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);
//
//		// reloader
//		ship = new ShipData();
//		ship.filename = "reloader.mesh";
//		ship.name = "reloader";
//		ship.team = ShipTeam.HUMAN;
//		ship.shipType = ShipType.RELOADER;
//		ship.maxSpeed = 60.0f;
//		ship.armor = 100.0f;
//		ship.turnAngle = 60.0f;
//		ship.engineSoundName = "ship_reloader_engine";
//		map.filenameShipMap.put(ship.filename, ship);
//		map.nameShipMap.put(ship.name, ship);

//        filenameShipMap = map.filenameShipMap;
//        nameShipMap = map.nameShipMap;

        return map;
    }
}
