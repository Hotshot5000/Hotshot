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

public class ShipSpawnedValidator implements LevelEventValidator {

    public static class ShipSpawnedValidatorFactory extends LevelEventValidatorFactory {

        public static final String TYPE = "ShipSpawnedValidator";

        @Override
        public LevelEventValidator createLevelEventValidator(ArrayList<String> paramList) {
            return new ShipSpawnedValidator();
        }
    }

    @Override
    public boolean validate(LevelEvent levelEvent) {
        return false;
    }
}
