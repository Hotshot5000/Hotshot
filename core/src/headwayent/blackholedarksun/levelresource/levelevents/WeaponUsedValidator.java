/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 12:32 PM
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

public class WeaponUsedValidator implements LevelEventValidator {

    public static class WeaponUsedValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "WeaponUsedValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new WeaponUsedValidator(paramList);
        }

        @Override
        public int readAhead() {
            return 2;
        }
    }

    private final WeaponData.WeaponType weaponType;
    private final int usedNum;
    private int currentUsedNum;
    private int weaponStartingNum = -1;
    private boolean sticky;

    public WeaponUsedValidator(ArrayList<String> paramList) {
        weaponType = WeaponData.WeaponType.getValueOf(paramList.get(0));
        usedNum = Integer.parseInt(paramList.get(1));
    }

    @Override
    public boolean validate(LevelEvent levelEvent) {
        if (sticky) {
            return true;
        }
        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        if (playerShip != null) {
            WeaponProperties weaponProperties = worldManager.getWeaponPropertiesComponentMapper().getSafe(playerShip);
            if (weaponProperties != null) {
                int currentWeaponAmmo = weaponProperties.getWeaponAmmo(weaponType);
                if (currentWeaponAmmo < weaponStartingNum) {
                    ++currentUsedNum;
                }
                weaponStartingNum = currentWeaponAmmo;
                sticky = currentUsedNum >= usedNum;
            }
        }
        return sticky;
    }
}
