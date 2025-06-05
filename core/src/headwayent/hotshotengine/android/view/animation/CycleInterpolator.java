/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.view.animation;

/**
 * Repeats the animation for a specified number of cycles. The
 * rate of change follows a sinusoidal pattern.
 */
//@HasNativeInterpolator
public class CycleInterpolator implements Interpolator/*, NativeInterpolatorFactory*/ {
    public CycleInterpolator(float cycles) {
        mCycles = cycles;
    }

//    public CycleInterpolator(Context context, AttributeSet attrs) {
//        this(context.getResources(), context.getTheme(), attrs);
//    }
//
//    /** @hide */
//    public CycleInterpolator(Resources resources, Theme theme, AttributeSet attrs) {
//        TypedArray a;
//        if (theme != null) {
//            a = theme.obtainStyledAttributes(attrs, R.styleable.CycleInterpolator, 0, 0);
//        } else {
//            a = resources.obtainAttributes(attrs, R.styleable.CycleInterpolator);
//        }
//
//        mCycles = a.getFloat(R.styleable.CycleInterpolator_cycles, 1.0f);
//
//        a.recycle();
//    }

    public float getInterpolation(float input) {
        return (float) (Math.sin(2 * mCycles * Math.PI * input));
    }

    private final float mCycles;

//    /** @hide */
//    @Override
//    public long createNativeInterpolator() {
//        return NativeInterpolatorFactoryHelper.createCycleInterpolator(mCycles);
//    }
}
