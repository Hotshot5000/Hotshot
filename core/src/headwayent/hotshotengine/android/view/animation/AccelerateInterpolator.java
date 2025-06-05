/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.view.animation;

/**
 * An interpolator where the rate of change starts out slowly and
 * and then accelerates.
 */
//@HasNativeInterpolator
public class AccelerateInterpolator implements Interpolator/*, NativeInterpolatorFactory*/ {
    private final float mFactor;
    private final double mDoubleFactor;

    public AccelerateInterpolator() {
        mFactor = 1.0f;
        mDoubleFactor = 2.0;
    }

    /**
     * Constructor
     *
     * @param factor Degree to which the animation should be eased. Seting
     *               factor to 1.0f produces a y=x^2 parabola. Increasing factor above
     *               1.0f  exaggerates the ease-in effect (i.e., it starts even
     *               slower and ends evens faster)
     */
    public AccelerateInterpolator(float factor) {
        mFactor = factor;
        mDoubleFactor = 2 * mFactor;
    }

//    public AccelerateInterpolator(/*Context context,*/ AttributeSet attrs) {
//        this(/*context.getResources(), context.getTheme(),*/ attrs);
//    }

//    /** @hide */
//    public AccelerateInterpolator(/*Resources res, Theme theme,*/ AttributeSet attrs) {
////        TypedArray a;
////        if (theme != null) {
////            a = theme.obtainStyledAttributes(attrs, R.styleable.AccelerateInterpolator, 0, 0);
////        } else {
////            a = res.obtainAttributes(attrs, R.styleable.AccelerateInterpolator);
////        }
////
////        mFactor = a.getFloat(R.styleable.AccelerateInterpolator_factor, 1.0f);
////        mDoubleFactor = 2 * mFactor;
////
////        a.recycle();
//    }

    public float getInterpolation(float input) {
        if (mFactor == 1.0f) {
            return input * input;
        } else {
            return (float) Math.pow(input, mDoubleFactor);
        }
    }

//    /** @hide */
//    @Override
//    public long createNativeInterpolator() {
//        return NativeInterpolatorFactoryHelper.createAccelerateInterpolator(mFactor);
//    }
}
