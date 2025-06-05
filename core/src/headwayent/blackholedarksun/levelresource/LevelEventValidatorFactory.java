/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/17/21, 12:25 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.levelresource;

import java.util.ArrayList;

public abstract class LevelEventValidatorFactory {

    public abstract LevelEventValidator createLevelEventValidator(ArrayList<String> paramList);

    /**
     * After reading the name of the level event validator type if there are any
     * parameters that must be read then specify here the number of params.
     * These params should then be passed in the constructor of the LevelEventValidator.
     * @return the number of params to read ahead after the type of the validator.
     */
    public int readAhead() {
        return 0;
    }
}
