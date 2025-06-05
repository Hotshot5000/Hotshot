/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 11:51 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelevents;

import java.util.ArrayList;

import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelEventValidator;
import headwayent.blackholedarksun.levelresource.LevelEventValidatorFactory;
import headwayent.blackholedarksun.world.WorldManager;

public class CountermeasuresButtonPressedValidator extends ButtonPressedValidator {

    public static class CountermeasuresButtonPressedValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "CountermeasuresButtonPressedValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new CountermeasuresButtonPressedValidator();
        }
    }

    @Override
    public boolean checkSticky(LevelEvent levelEvent) {
        if (super.checkSticky(levelEvent)) {
            WorldManager worldManager = WorldManager.getSingleton();
            ShipProperties shipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(playerShip);
            if (shipProperties == null) {
                return false;
            }
            if (shipProperties.isCountermeasureLaunched()) {
                sticky = true;
            }
            return shipProperties.isCountermeasureLaunched();
        }
        return false;
    }
}
