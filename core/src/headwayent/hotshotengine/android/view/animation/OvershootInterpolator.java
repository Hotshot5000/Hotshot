/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.view.animation;

/**
 * An interpolator where the change flings forward and overshoots the last value
 * then comes back.
 */
//@HasNativeInterpolator
public class OvershootInterpolator implements Interpolator/*, NativeInterpolatorFactory*/ {
    private final float mTension;

    public OvershootInterpolator() {
        mTension = 2.0f;
    }

    /**
     * @param tension Amount of overshoot. When tension equals 0.0f, there is
     *                no overshoot and the interpolator becomes a simple
     *                deceleration interpolator.
     */
    public OvershootInterpolator(float tension) {
        mTension = tension;
    }

//    public OvershootInterpolator(Context context, AttributeSet attrs) {
//        this(context.getResources(), context.getTheme(), attrs);
//    }
//
//    /** @hide */
//    public OvershootInterpolator(Resources res, Theme theme, AttributeSet attrs) {
//        TypedArray a;
//        if (theme != null) {
//            a = theme.obtainStyledAttributes(attrs, R.styleable.OvershootInterpolator, 0, 0);
//        } else {
//            a = res.obtainAttributes(attrs, R.styleable.OvershootInterpolator);
//        }
//
//        mTension =
//                a.getFloat(R.styleable.OvershootInterpolator_tension, 2.0f);
//
//        a.recycle();
//    }

    public float getInterpolation(float t) {
        // _o(t) = t * t * ((tension + 1) * t + tension)
        // o(t) = _o(t - 1) + 1
        t -= 1.0f;
        return t * t * ((mTension + 1) * t + mTension) + 1.0f;
    }

//    /** @hide */
//    @Override
//    public long createNativeInterpolator() {
//        return NativeInterpolatorFactoryHelper.createOvershootInterpolator(mTension);
//    }
}
