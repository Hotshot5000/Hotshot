/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.hotshotengine.basictypes.ENG_Float;

public class ENG_ControllerTypeFloat implements ENG_IControllerType<ENG_Float> {

    public final ENG_Float value = new ENG_Float();
    public final ENG_Float lowerLimit = new ENG_Float(0.0f);
    public final ENG_Float upperLimit = new ENG_Float(1.0f);
    public final ENG_Float step = new ENG_Float(1.0f);

    @Override
    public void add(ENG_Float val) {

        value.setValue(value.getValue() + val.getValue());
    }

    @Override
    public void div(ENG_Float val) {

        value.setValue(value.getValue() / val.getValue());
    }

    @Override
    public ENG_Float getLowerLimit() {

        return lowerLimit;
    }

    @Override
    public ENG_Float getStep() {

        return step;
    }

    @Override
    public ENG_Float getUpperLimit() {

        return upperLimit;
    }

    @Override
    public void mul(ENG_Float val) {

        value.setValue(value.getValue() * val.getValue());
    }

    @Override
    public void sub(ENG_Float val) {

        value.setValue(value.getValue() - val.getValue());
    }

    @Override
    public int compare(ENG_Float arg0, ENG_Float arg1) {

        return arg0.compareTo(arg1);
    }

    @Override
    public ENG_Float get() {

        return value;
    }

}
