/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 2:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelevents;

import com.artemis.Entity;

import java.util.ArrayList;
import java.util.Map;

import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.entitydata.WeaponData;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelEventValidator;
import headwayent.blackholedarksun.levelresource.LevelEventValidatorFactory;
import headwayent.blackholedarksun.world.WorldManager;

/**
 * Right now it only checks if all weapons are at max load for player ship.
 * No custom weapon loadout check available.
 */
public class WeaponLoadValidator implements LevelEventValidator {

    public static class WeaponLoadValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "WeaponLoadValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new WeaponLoadValidator(paramList);
        }

        @Override
        public int readAhead() {
            return 0;
        }
    }

    private boolean sticky;

    public WeaponLoadValidator(ArrayList<String> paramList) {

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
                boolean allWeaponsFull = true;
                for (Map.Entry<WeaponData.WeaponType, Integer> entry : weaponProperties.getWeaponAmmo().entrySet()) {
                    if (WeaponData.WeaponType.getDefaultMissileNumber(entry.getKey()) != entry.getValue()) {
                        allWeaponsFull = false;
                        break;
                    }
                }
                sticky = allWeaponsFull;
            }
        }
        return sticky;
    }
}
