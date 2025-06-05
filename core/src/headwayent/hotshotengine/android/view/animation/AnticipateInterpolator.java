/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.view.animation;

/**
 * An interpolator where the change starts backward then flings forward.
 */
//@HasNativeInterpolator
public class AnticipateInterpolator implements Interpolator/*, NativeInterpolatorFactory*/ {
    private final float mTension;

    public AnticipateInterpolator() {
        mTension = 2.0f;
    }

    /**
     * @param tension Amount of anticipation. When tension equals 0.0f, there is
     *                no anticipation and the interpolator becomes a simple
     *                acceleration interpolator.
     */
    public AnticipateInterpolator(float tension) {
        mTension = tension;
    }

//    public AnticipateInterpolator(Context context, AttributeSet attrs) {
//        this(context.getResources(), context.getTheme(), attrs);
//    }
//
//    /** @hide */
//    public AnticipateInterpolator(Resources res, Theme theme, AttributeSet attrs) {
//        TypedArray a;
//        if (theme != null) {
//            a = theme.obtainStyledAttributes(attrs, R.styleable.AnticipateInterpolator, 0, 0);
//        } else {
//            a = res.obtainAttributes(attrs, R.styleable.AnticipateInterpolator);
//        }
//
//        mTension =
//                a.getFloat(R.styleable.AnticipateInterpolator_tension, 2.0f);
//
//        a.recycle();
//    }

    public float getInterpolation(float t) {
        // a(t) = t * t * ((tension + 1) * t - tension)
        return t * t * ((mTension + 1) * t - mTension);
    }

//    /** @hide */
//    @Override
//    public long createNativeInterpolator() {
//        return NativeInterpolatorFactoryHelper.createAnticipateInterpolator(mTension);
//    }
}
