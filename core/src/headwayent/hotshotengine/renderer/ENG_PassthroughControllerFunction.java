/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_ControllerFunction;
import headwayent.hotshotengine.ENG_ControllerTypeFloat;

public class ENG_PassthroughControllerFunction extends
        ENG_ControllerFunction<ENG_ControllerTypeFloat> {

    public ENG_PassthroughControllerFunction() {
        this(new ENG_ControllerTypeFloat(), false);
    }

    public ENG_PassthroughControllerFunction(
            ENG_ControllerTypeFloat deltaCount, boolean deltaInput) {
        super(deltaCount, deltaInput);
        
    }

    @Override
    public ENG_ControllerTypeFloat calculate(ENG_ControllerTypeFloat object) {

        return getAdjustedInput(object);
    }

    @Override
    public int compareTo(ENG_ControllerTypeFloat arg0) {

        return 0;
    }


}
