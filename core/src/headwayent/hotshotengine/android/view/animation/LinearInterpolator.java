/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.view.animation;

import headwayent.hotshotengine.android.util.AttributeSet;

/**
 * An interpolator where the rate of change is constant
 */
//@HasNativeInterpolator
public class LinearInterpolator implements Interpolator/*, NativeInterpolatorFactory*/ {

    public LinearInterpolator() {
    }

    public LinearInterpolator(/*Context context,*/ AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        return input;
    }

//    /** @hide */
//    @Override
//    public long createNativeInterpolator() {
//        return NativeInterpolatorFactoryHelper.createLinearInterpolator();
//    }
}
