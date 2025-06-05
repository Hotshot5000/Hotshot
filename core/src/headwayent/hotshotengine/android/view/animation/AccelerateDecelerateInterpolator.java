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
 * An interpolator where the rate of change starts and ends slowly but
 * accelerates through the middle.
 */
//@HasNativeInterpolator
public class AccelerateDecelerateInterpolator implements Interpolator/*, NativeInterpolatorFactory*/ {
    public AccelerateDecelerateInterpolator() {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public AccelerateDecelerateInterpolator(/*Context context,*/ AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }

    /** @hide */
//    @Override
//    public long createNativeInterpolator() {
//        return NativeInterpolatorFactoryHelper.createAccelerateDecelerateInterpolator();
//    }
}
