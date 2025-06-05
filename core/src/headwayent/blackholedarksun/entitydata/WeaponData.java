/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.entitydata;

import headwayent.blackholedarksun.EntityData;
import headwayent.blackholedarksun.HudManager.CrosshairType;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.TrackerProperties;
import headwayent.blackholedarksun.components.EntityProperties.OnDestroyedEvent;
import headwayent.blackholedarksun.loaders.WeaponDataCompiler;
import headwayent.blackholedarksun.world.WorldManagerBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;

import com.artemis.Entity;

public class WeaponData extends EntityData {

    public static final int INFINITE_AMMO = -1;

    public static class WeaponComparator implements Comparator<WeaponType> {

        @Override
        public int compare(WeaponType arg0, WeaponType arg1) {

            int rank0 = WeaponType.rank(arg0);
            int rank1 = WeaponType.rank(arg1);
            if (rank0 > rank1) {
                return -1;
            } else if (rank0 < rank1) {
                return 1;
            }
            return 0;

        }

    }

    public static class WeaponOnDestroyedEvent implements OnDestroyedEvent {

        private final Entity entity;

        public WeaponOnDestroyedEvent(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void execute() {

            WorldManagerBase worldManager = WorldManagerBase.getSingleton();
            TrackerProperties trackerProperties = worldManager.getTrackerPropertiesComponentMapper().getSafe(entity);
            if (trackerProperties != null) {
                Entity ship = MainApp.getGame().getWorldManager().getEntityByItemId(trackerProperties.getTrackedEntityId());
                if (ship != null) {
                    worldManager.getShipPropertiesComponentMapper().get(ship).removeChasingProjectile(
                            worldManager.getEntityPropertiesComponentMapper().get(entity).getEntityId());
                }
            }
        }

    }

    public enum WeaponType {
        LASER_GREEN, LASER_RED, LASER_GREEN_QUAD, LASER_RED_QUAD,
        CONCUSSION, HOMING, MEGA, PLASMA, PIRANHA;

        public static boolean hasInfiniteAmmo(WeaponType wpn) {
            switch (wpn) {
                case LASER_GREEN:
                case LASER_GREEN_QUAD:
                case LASER_RED:
                case LASER_RED_QUAD:
                    return true;
                default:
                    return false;
            }
        }

        public static int rank(WeaponType wpn) {
            switch (wpn) {
                case LASER_GREEN:
                case LASER_RED:
                    return 1;
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                    return 2;
                case CONCUSSION:
                    return 3;
                case PLASMA:
                    return 4;
                case HOMING:
                    return 5;

                case PIRANHA:
                    return 6;
                case MEGA:
                    return 7;
                default:
                    throw new IllegalArgumentException(wpn + " is an invalid weapon type");
            }
        }

        public static boolean getSpecialWeapon(WeaponType wpn) {
            switch (wpn) {
                case MEGA:
                case PIRANHA:
                    return true;
                case LASER_GREEN:
                case LASER_RED:
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                case CONCUSSION:
                case HOMING:
                case PLASMA:
                    return false;
                default:
                    throw new IllegalArgumentException(wpn + " is an invalid weapon type");
            }
        }

        public static float getWeaponDistance(WeaponType wpn) {
            switch (wpn) {
                case CONCUSSION:
                    return 1000.0f;
                case HOMING:
                    return 1000.0f;
                case LASER_GREEN:
                case LASER_RED:
                    return 100.0f;
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                    return 100.0f;
                case MEGA:
                    return 1000.0f;
                case PIRANHA:
                    return 1000.0f;
                case PLASMA:
                    return 1000.0f;
            }
            throw new IllegalArgumentException("Invalid weapon type");
        }

        public static String getWeapon(WeaponType wpn) {
            // TODO add localization.
            switch (wpn) {
                case CONCUSSION:
                    return "Concussion Missile";
                case HOMING:
                    return "Homing Missile";
                case LASER_GREEN:
                case LASER_RED:
                    return "Laser";
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                    return "Quad Laser";
                case MEGA:
                    return "Mega Missile";
                case PIRANHA:
                    return "Piranha Missile";
                case PLASMA:
                    return "Plasma";
            }
            throw new IllegalArgumentException("Invalid weapon type");
        }

        public static int getDefaultMissileNumber(WeaponType wpn) {
            return weaponMap.get(wpn).defaultMissileNumber;
        }

        public static long getWeaponCooldownTime(WeaponType wpn) {
            return weaponMap.get(wpn).cooldownTime;
        }

        public static long getWeaponEnemySelectionTime(WeaponType wpn) {
            return weaponMap.get(wpn).enemySelectionTime;
        }

        public static CrosshairType getWeaponCrosshairType(WeaponType wpn) {
            switch (wpn) {
                case LASER_GREEN:
                case LASER_RED:
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                case CONCUSSION:
                case PLASMA:
                    return CrosshairType.CROSSHAIR;
                case HOMING:
                case MEGA:
                case PIRANHA:
                    return CrosshairType.SELECTION;

            }
            throw new IllegalArgumentException("Invalid weapon type");
        }

        public static boolean isHomingMissileType(WeaponType wpn) {
            switch (wpn) {
                case LASER_GREEN:
                case LASER_RED:
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                case CONCUSSION:
                case PLASMA:
                    return false;
                case HOMING:
                case MEGA:
                case PIRANHA:
                    return true;

            }
            throw new IllegalArgumentException("Invalid weapon type");
        }

        public static boolean isMissileType(WeaponType wpn) {
            switch (wpn) {
                case LASER_GREEN:
                case LASER_RED:
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                case PLASMA:
                    return false;
                case CONCUSSION:
                case HOMING:
                case MEGA:
                case PIRANHA:
                    return true;

            }
            throw new IllegalArgumentException("Invalid weapon type");
        }

        public static long getHomingMissileTrackingDelay(WeaponType wpn) {
            return weaponMap.get(wpn).trackingDelay;
        }

        public static String getProjectileHitSoundName(WeaponType wpn) {
            switch (wpn) {
                case LASER_GREEN:
                case LASER_RED:
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                    return "laser_hit";
                case CONCUSSION:
                    return "concussion_hit";
                case PLASMA:
                    return "plasma_hit";
                case HOMING:
                    return "homing_hit";
                case MEGA:
                    return "mega_hit";
                case PIRANHA:
                    return "piranha_hit";

            }
            throw new IllegalArgumentException("Invalid weapon type");
        }

        public static String getProjectileLaunchSoundName(WeaponType wpn) {
            switch (wpn) {
                case LASER_GREEN:
                case LASER_RED:
                case LASER_GREEN_QUAD:
                case LASER_RED_QUAD:
                    return "laser_launch";
                case CONCUSSION:
                    return "concussion_launch";
                case PLASMA:
                    return "plasma_launch";
                case HOMING:
                    return "homing_launch";
                case MEGA:
                    return "mega_launch";
                case PIRANHA:
                    return "piranha_launch";

            }
            throw new IllegalArgumentException("Invalid weapon type");
        }

        public static WeaponType getValueOf(String s) {
            if (LASER_GREEN.toString().equalsIgnoreCase(s)) {
                return LASER_GREEN;
            } else if (LASER_RED.toString().equalsIgnoreCase(s)) {
                return LASER_RED;
            } else if (LASER_GREEN_QUAD.toString().equalsIgnoreCase(s)) {
                return LASER_GREEN_QUAD;
            } else if (LASER_RED_QUAD.toString().equalsIgnoreCase(s)) {
                return LASER_RED_QUAD;
            } else if (CONCUSSION.toString().equalsIgnoreCase(s)) {
                return CONCUSSION;
            } else if (PLASMA.toString().equalsIgnoreCase(s)) {
                return PLASMA;
            } else if (HOMING.toString().equalsIgnoreCase(s)) {
                return HOMING;
            } else if (MEGA.toString().equalsIgnoreCase(s)) {
                return MEGA;
            } else if (PIRANHA.toString().equalsIgnoreCase(s)) {
                return PIRANHA;
            } else {
                throw new IllegalArgumentException(s + " is an invalid weapon type");
            }
        }
    }

    private static final EnumMap<WeaponData.WeaponType, WeaponData> weaponMap = new EnumMap<>(WeaponType.class);

    public WeaponType weaponType;
    public int damage;
    public float maxDistance = 4000.0f;
    public float weight = 10.0f;
    public long cooldownTime = 5000;
    public int defaultMissileNumber;
    public long enemySelectionTime;
    public long trackingDelay;

    public static EnumMap<WeaponData.WeaponType, WeaponData> createWeaponMappings() {
//        if (weaponMappingsCreated) {
//            throw new ENG_MultipleSingletonConstructAttemptException("weapon mappings already created");
//        }
        boolean weaponMappingsCreated = true;
        weaponMap.clear();
        ArrayList<WeaponData> weaponDataList = new WeaponDataCompiler().compile("weapon_data_list.txt",
                MainApp.getGame().getGameResourcesDir(), WorldManagerBase.LOAD_FROM_SDCARD);
        for (WeaponData weapon : weaponDataList) {
            weaponMap.put(weapon.weaponType, weapon);
            WorldManagerBase.getEntityAabb(weapon.filename);
        }

        return weaponMap;
    }

    public static WeaponData getWeaponData(WeaponData.WeaponType wpn) {
        WeaponData weaponData = weaponMap.get(wpn);
        if (weaponData == null) {
            throw new IllegalArgumentException(wpn
                    + " is not in the weapon map");
        }
        return weaponData;
    }
}
