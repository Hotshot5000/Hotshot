/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.animation;

import headwayent.hotshotengine.android.animation.Keyframe.IntKeyframe;

import java.util.ArrayList;

/**
 * This class holds a collection of IntKeyframe objects and is called by ValueAnimator to calculate
 * values between those keyframes for a given animation. The class internal to the animation
 * package because it is an implementation detail of how Keyframes are stored and used.
 *
 * <p>This type-specific subclass of KeyframeSet, along with the other type-specific subclass for
 * float, exists to speed up the getValue() method when there is no custom
 * TypeEvaluator set for the animation, so that values can be calculated without autoboxing to the
 * Object equivalents of these primitive types.</p>
 */
class IntKeyframeSet extends KeyframeSet implements Keyframes.IntKeyframes {
    private int firstValue;
    private int lastValue;
    private int deltaValue;
    private boolean firstTime = true;

    public IntKeyframeSet(IntKeyframe... keyframes) {
        super(keyframes);
    }

    @Override
    public Object getValue(float fraction) {
        return getIntValue(fraction);
    }

    @Override
    public IntKeyframeSet clone() {
        ArrayList<Keyframe> keyframes = mKeyframes;
        int numKeyframes = mKeyframes.size();
        IntKeyframe[] newKeyframes = new IntKeyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; ++i) {
            newKeyframes[i] = (IntKeyframe) keyframes.get(i).clone();
        }
        return new IntKeyframeSet(newKeyframes);
    }

    @Override
    public void invalidateCache() {
        firstTime = true;
    }

    @Override
    public int getIntValue(float fraction) {
        if (mNumKeyframes == 2) {
            if (firstTime) {
                firstTime = false;
                firstValue = ((IntKeyframe) mKeyframes.get(0)).getIntValue();
                lastValue = ((IntKeyframe) mKeyframes.get(1)).getIntValue();
                deltaValue = lastValue - firstValue;
            }
            if (mInterpolator != null) {
                fraction = mInterpolator.getInterpolation(fraction);
            }
            if (mEvaluator == null) {
                return firstValue + (int)(fraction * deltaValue);
            } else {
                return ((Number)mEvaluator.evaluate(fraction, firstValue, lastValue)).intValue();
            }
        }
        if (fraction <= 0f) {
            final IntKeyframe prevKeyframe = (IntKeyframe) mKeyframes.get(0);
            final IntKeyframe nextKeyframe = (IntKeyframe) mKeyframes.get(1);
            int prevValue = prevKeyframe.getIntValue();
            int nextValue = nextKeyframe.getIntValue();
            float prevFraction = prevKeyframe.getFraction();
            float nextFraction = nextKeyframe.getFraction();
            final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            float intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return mEvaluator == null ?
                    prevValue + (int)(intervalFraction * (nextValue - prevValue)) :
                    ((Number)mEvaluator.evaluate(intervalFraction, prevValue, nextValue)).
                            intValue();
        } else if (fraction >= 1f) {
            final IntKeyframe prevKeyframe = (IntKeyframe) mKeyframes.get(mNumKeyframes - 2);
            final IntKeyframe nextKeyframe = (IntKeyframe) mKeyframes.get(mNumKeyframes - 1);
            int prevValue = prevKeyframe.getIntValue();
            int nextValue = nextKeyframe.getIntValue();
            float prevFraction = prevKeyframe.getFraction();
            float nextFraction = nextKeyframe.getFraction();
            final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            float intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            return mEvaluator == null ?
                    prevValue + (int)(intervalFraction * (nextValue - prevValue)) :
                    ((Number)mEvaluator.evaluate(intervalFraction, prevValue, nextValue)).intValue();
        }
        IntKeyframe prevKeyframe = (IntKeyframe) mKeyframes.get(0);
        for (int i = 1; i < mNumKeyframes; ++i) {
            IntKeyframe nextKeyframe = (IntKeyframe) mKeyframes.get(i);
            if (fraction < nextKeyframe.getFraction()) {
                final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
                if (interpolator != null) {
                    fraction = interpolator.getInterpolation(fraction);
                }
                float intervalFraction = (fraction - prevKeyframe.getFraction()) /
                    (nextKeyframe.getFraction() - prevKeyframe.getFraction());
                int prevValue = prevKeyframe.getIntValue();
                int nextValue = nextKeyframe.getIntValue();
                return mEvaluator == null ?
                        prevValue + (int)(intervalFraction * (nextValue - prevValue)) :
                        ((Number)mEvaluator.evaluate(intervalFraction, prevValue, nextValue)).
                                intValue();
            }
            prevKeyframe = nextKeyframe;
        }
        // shouldn't get here
        return ((Number)mKeyframes.get(mNumKeyframes - 1).getValue()).intValue();
    }

    @Override
    public Class getType() {
        return Integer.class;
    }
}

