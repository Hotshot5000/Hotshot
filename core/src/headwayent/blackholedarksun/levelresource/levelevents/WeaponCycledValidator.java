/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 12:47 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelevents;

import com.artemis.Entity;

import java.util.ArrayList;

import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelEventValidator;
import headwayent.blackholedarksun.levelresource.LevelEventValidatorFactory;
import headwayent.blackholedarksun.world.WorldManager;

public class WeaponCycledValidator implements LevelEventValidator {

    public static class WeaponCycledValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "WeaponCycledValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new WeaponCycledValidator(paramList);
        }

        @Override
        public int readAhead() {
            return 1;
        }
    }

    private final WeaponData.WeaponType weaponType;
    private WeaponData.WeaponType currentWeaponType;

    public WeaponCycledValidator(ArrayList<String> paramList) {
        weaponType = WeaponData.WeaponType.getValueOf(paramList.get(0));
    }

    @Override
    public boolean validate(LevelEvent levelEvent) {
        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        if (playerShip != null) {
            WeaponProperties weaponProperties = worldManager.getWeaponPropertiesComponentMapper().getSafe(playerShip);
            if (weaponProperties != null) {
                return weaponType == weaponProperties.getCurrentWeaponType();
            }
        }
        return false;
    }
}
