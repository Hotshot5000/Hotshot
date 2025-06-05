/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.view.animation;

/**
 * An interpolator where the rate of change starts out quickly and
 * and then decelerates.
 */
//@HasNativeInterpolator
public class DecelerateInterpolator implements Interpolator/*, NativeInterpolatorFactory*/ {
    public DecelerateInterpolator() {
    }

    /**
     * Constructor
     *
     * @param factor Degree to which the animation should be eased. Setting factor to 1.0f produces
     *               an upside-down y=x^2 parabola. Increasing factor above 1.0f makes exaggerates the
     *               ease-out effect (i.e., it starts even faster and ends evens slower)
     */
    public DecelerateInterpolator(float factor) {
        mFactor = factor;
    }

//    public DecelerateInterpolator(Context context, AttributeSet attrs) {
//        this(context.getResources(), context.getTheme(), attrs);
//    }
//
//    /** @hide */
//    public DecelerateInterpolator(Resources res, Theme theme, AttributeSet attrs) {
//        TypedArray a;
//        if (theme != null) {
//            a = theme.obtainStyledAttributes(attrs, R.styleable.DecelerateInterpolator, 0, 0);
//        } else {
//            a = res.obtainAttributes(attrs, R.styleable.DecelerateInterpolator);
//        }
//
//        mFactor = a.getFloat(R.styleable.DecelerateInterpolator_factor, 1.0f);
//
//        a.recycle();
//    }

    public float getInterpolation(float input) {
        float result;
        if (mFactor == 1.0f) {
            result = 1.0f - (1.0f - input) * (1.0f - input);
        } else {
            result = (float) (1.0f - Math.pow((1.0f - input), 2 * mFactor));
        }
        return result;
    }

    private float mFactor = 1.0f;

//    /** @hide */
//    @Override
//    public long createNativeInterpolator() {
//        return NativeInterpolatorFactoryHelper.createDecelerateInterpolator(mFactor);
//    }
}
