/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 11:51 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource.levelevents;

import java.util.ArrayList;

import headwayent.blackholedarksun.levelresource.LevelEvent;
import headwayent.blackholedarksun.levelresource.LevelEventValidator;
import headwayent.blackholedarksun.levelresource.LevelEventValidatorFactory;
import headwayent.blackholedarksun.world.WorldManager;

public class ReloaderButtonPressedValidator extends ButtonPressedValidator {

    public static class ReloaderButtonPressedValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "ReloaderButtonPressedValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new ReloaderButtonPressedValidator();
        }
    }

    @Override
    public boolean checkSticky(LevelEvent levelEvent) {
        sticky = WorldManager.getSingleton().getCurrentReloaderShipId() != -1;
        return sticky;
    }
}
