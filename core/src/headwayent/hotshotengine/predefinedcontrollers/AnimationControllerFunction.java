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

public class AnimationControllerFunction extends
        ENG_ControllerFunction<ENG_ControllerTypeFloat> {

    private float mSeqTime;
    private float mTime;

    public AnimationControllerFunction(float sequenceTime) {
        this(sequenceTime, 0.0f);
    }

    public AnimationControllerFunction(float sequenceTime, float timeOffset) {
        super(new ENG_ControllerTypeFloat(), false);

        mSeqTime = sequenceTime;
        mTime = timeOffset;
    }

    @Override
    public int compareTo(ENG_ControllerTypeFloat another) {

        return getDeltaCount().compare(getDeltaCount().value, another.value);
    }

    @Override
    public ENG_ControllerTypeFloat calculate(
            ENG_ControllerTypeFloat source) {

        // Assume source is time since last update
        mTime += source.value.getValue();
        // Wrap
        while (mTime >= mSeqTime) mTime -= mSeqTime;
        while (mTime < 0) mTime += mSeqTime;

        // Return parametric
        ENG_ControllerTypeFloat f = new ENG_ControllerTypeFloat();
        f.value.setValue(mTime / mSeqTime);
        return f;
    }

    public void setTime(float offset) {
        mTime = offset;
    }

    public void setSequenceTime(float time) {
        mSeqTime = time;
    }

}
