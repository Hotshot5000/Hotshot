/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */
package headwayent.hotshotengine.android.animation;

import java.util.ArrayList;

/**
 * This interface abstracts a collection of Keyframe objects and is called by
 * ValueAnimator to calculate values between those keyframes for a given animation.
 */
interface Keyframes extends Cloneable {

    /**
     * Sets the TypeEvaluator to be used when calculating animated values. This object
     * is required only for Keyframes that are not either IntKeyframes or FloatKeyframes,
     * both of which assume their own evaluator to speed up calculations with those primitive
     * types.
     *
     * @param evaluator The TypeEvaluator to be used to calculate animated values.
     */
    void setEvaluator(TypeEvaluator evaluator);

    /**
     * @return The value type contained by the contained Keyframes.
     */
    Class getType();

    /**
     * Gets the animated value, given the elapsed fraction of the animation (interpolated by the
     * animation's interpolator) and the evaluator used to calculate in-between values. This
     * function maps the input fraction to the appropriate keyframe interval and a fraction
     * between them and returns the interpolated value. Note that the input fraction may fall
     * outside the [0-1] bounds, if the animation's interpolator made that happen (e.g., a
     * spring interpolation that might send the fraction past 1.0). We handle this situation by
     * just using the two keyframes at the appropriate end when the value is outside those bounds.
     *
     * @param fraction The elapsed fraction of the animation
     * @return The animated value.
     */
    Object getValue(float fraction);

    /**
     * If subclass has variables that it calculates based on the Keyframes, it should reset them
     * when this method is called because Keyframe contents might have changed.
     */
    void invalidateCache();

    /**
     * @return A list of all Keyframes contained by this. This may return null if this is
     * not made up of Keyframes.
     */
    ArrayList<Keyframe> getKeyframes();

    Keyframes clone();

    /**
     * A specialization of Keyframes that has integer primitive value calculation.
     */
    interface IntKeyframes extends Keyframes {

        /**
         * Works like {@link #getValue(float)}, but returning a primitive.
         * @param fraction The elapsed fraction of the animation
         * @return The animated value.
         */
        int getIntValue(float fraction);
    }

    /**
     * A specialization of Keyframes that has float primitive value calculation.
     */
    interface FloatKeyframes extends Keyframes {

        /**
         * Works like {@link #getValue(float)}, but returning a primitive.
         * @param fraction The elapsed fraction of the animation
         * @return The animated value.
         */
        float getFloatValue(float fraction);
    }
}
