/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.predefinedcontrollers;

import headwayent.hotshotengine.ENG_ControllerFunction;
import headwayent.hotshotengine.ENG_ControllerTypeFloat;
import headwayent.hotshotengine.basictypes.ENG_Float;

public class ScaleControllerFunction extends
        ENG_ControllerFunction<ENG_ControllerTypeFloat> {

    private final float mScale;

    public ScaleControllerFunction(float factor,
                                   boolean deltaInput) {
        super(new ENG_ControllerTypeFloat(), deltaInput);
        
        mScale = factor;
    }

    private final ENG_Float f = new ENG_Float();

    @Override
    public int compareTo(ENG_ControllerTypeFloat oth) {

        return getDeltaCount().compare(getDeltaCount().value, oth.value);
    }

    @Override
    public ENG_ControllerTypeFloat calculate(
            ENG_ControllerTypeFloat source) {

        f.setValue(mScale);
        source.mul(f);
        return getAdjustedInput(source);
    }

}
