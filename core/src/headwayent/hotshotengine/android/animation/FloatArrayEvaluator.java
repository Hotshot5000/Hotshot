/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.animation;

/**
 * This evaluator can be used to perform type interpolation between <code>float[]</code> values.
 * Each index into the array is treated as a separate value to interpolate. For example,
 * evaluating <code>{100, 200}</code> and <code>{300, 400}</code> will interpolate the value at
 * the first index between 100 and 300 and the value at the second index value between 200 and 400.
 */
public class FloatArrayEvaluator implements TypeEvaluator<float[]> {

    private float[] mArray;

    /**
     * Create a FloatArrayEvaluator that does not reuse the animated value. Care must be taken
     * when using this option because on every evaluation a new <code>float[]</code> will be
     * allocated.
     *
     * @see #FloatArrayEvaluator(float[])
     */
    public FloatArrayEvaluator() {
    }

    /**
     * Create a FloatArrayEvaluator that reuses <code>reuseArray</code> for every evaluate() call.
     * Caution must be taken to ensure that the value returned from
     * {@link ValueAnimator#getAnimatedValue()} is not cached, modified, or
     * used across threads. The value will be modified on each <code>evaluate()</code> call.
     *
     * @param reuseArray The array to modify and return from <code>evaluate</code>.
     */
    public FloatArrayEvaluator(float[] reuseArray) {
        mArray = reuseArray;
    }

    /**
     * Interpolates the value at each index by the fraction. If
     * {@link #FloatArrayEvaluator(float[])} was used to construct this object,
     * <code>reuseArray</code> will be returned, otherwise a new <code>float[]</code>
     * will be returned.
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue The start value.
     * @param endValue   The end value.
     * @return A <code>float[]</code> where each element is an interpolation between
     *         the same index in startValue and endValue.
     */
    @Override
    public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
        float[] array = mArray;
        if (array == null) {
            array = new float[startValue.length];
        }

        for (int i = 0; i < array.length; i++) {
            float start = startValue[i];
            float end = endValue[i];
            array[i] = start + (fraction * (end - start));
        }
        return array;
    }
}
