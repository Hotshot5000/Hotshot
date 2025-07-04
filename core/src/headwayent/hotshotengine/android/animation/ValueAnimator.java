/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.animation;

//import android.os.Looper;

import headwayent.hotshotengine.android.view.Choreographer;
import headwayent.hotshotengine.android.view.animation.AccelerateDecelerateInterpolator;
import headwayent.hotshotengine.android.view.animation.AnimationUtils;
import headwayent.hotshotengine.android.view.animation.LinearInterpolator;
import headwayent.hotshotengine.Looper;

import java.util.ArrayList;
import java.util.HashMap;

//import android.util.AndroidRuntimeException;

/**
 * This class provides a simple timing engine for running animations
 * which calculate animated values and set them on target objects.
 * <p/>
 * <p>There is a single timing pulse that all animations use. It runs in a
 * custom handler to ensure that property changes happen on the UI thread.</p>
 * <p/>
 * <p>By default, ValueAnimator uses non-linear time interpolation, via the
 * {@link AccelerateDecelerateInterpolator} class, which accelerates into and decelerates
 * out of an animation. This behavior can be changed by calling
 * {@link ValueAnimator#setInterpolator(TimeInterpolator)}.</p>
 * <p/>
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For more information about animating with {@code ValueAnimator}, read the
 * <a href="{@docRoot}guide/topics/graphics/prop-animation.html#value-animator">Property
 * Animation</a> developer guide.</p>
 * </div>
 */
@SuppressWarnings("unchecked")
public class ValueAnimator extends Animator {

    /**
     * Internal constants
     */
    private static float sDurationScale = 1.0f;

    /**
     * Values used with internal variable mPlayingState to indicate the current state of an
     * animation.
     */
    static final int STOPPED = 0; // Not yet playing
    static final int RUNNING = 1; // Playing normally
    static final int SEEKED = 2; // Seeked to some time value

    /**
     * Internal variables
     * NOTE: This object implements the clone() method, making a deep copy of any referenced
     * objects. As other non-trivial fields are added to this class, make sure to add logic
     * to clone() to make deep copies of them.
     */

    // The first time that the animation's animateFrame() method is called. This time is used to
    // determine elapsed time (and therefore the elapsed fraction) in subsequent calls
    // to animateFrame()
    long mStartTime;

    /**
     * Set when setCurrentPlayTime() is called. If negative, animation is not currently seeked
     * to a value.
     */
    long mSeekTime = -1;

    /**
     * Set on the next frame after pause() is called, used to calculate a new startTime
     * or delayStartTime which allows the animator to continue from the point at which
     * it was paused. If negative, has not yet been set.
     */
    private long mPauseTime;

    /**
     * Set when an animator is resumed. This triggers logic in the next frame which
     * actually resumes the animator.
     */
    private boolean mResumed = false;


    // The static sAnimationHandler processes the internal timing loop on which all animations
    // are based
    /**
     * @hide
     */
    protected static final ThreadLocal<AnimationHandler> sAnimationHandler =
            new ThreadLocal<>();

    // The time interpolator to be used if none is set on the animation
    private static final headwayent.hotshotengine.android.animation.TimeInterpolator sDefaultInterpolator =
            new AccelerateDecelerateInterpolator();

    /**
     * Used to indicate whether the animation is currently playing in reverse. This causes the
     * elapsed fraction to be inverted to calculate the appropriate values.
     */
    private boolean mPlayingBackwards = false;

    /**
     * This variable tracks the current iteration that is playing. When mCurrentIteration exceeds the
     * repeatCount (if repeatCount!=INFINITE), the animation ends
     */
    private int mCurrentIteration = 0;

    /**
     * Tracks current elapsed/eased fraction, for querying in getAnimatedFraction().
     */
    private float mCurrentFraction = 0f;

    /**
     * Tracks whether a startDelay'd animation has begun playing through the startDelay.
     */
    private boolean mStartedDelay = false;

    /**
     * Tracks the time at which the animation began playing through its startDelay. This is
     * different from the mStartTime variable, which is used to track when the animation became
     * active (which is when the startDelay expired and the animation was added to the active
     * animations list).
     */
    private long mDelayStartTime;

    /**
     * Flag that represents the current state of the animation. Used to figure out when to start
     * an animation (if state == STOPPED). Also used to end an animation that
     * has been cancel()'d or end()'d since the last animation frame. Possible values are
     * STOPPED, RUNNING, SEEKED.
     */
    int mPlayingState = STOPPED;

    /**
     * Additional playing state to indicate whether an animator has been start()'d. There is
     * some lag between a call to start() and the first animation frame. We should still note
     * that the animation has been started, even if it's first animation frame has not yet
     * happened, and reflect that state in isRunning().
     * Note that delayed animations are different: they are not started until their first
     * animation frame, which occurs after their delay elapses.
     */
    private boolean mRunning = false;

    /**
     * Additional playing state to indicate whether an animator has been start()'d, whether or
     * not there is a nonzero startDelay.
     */
    private boolean mStarted = false;

    /**
     * Tracks whether we've notified listeners of the onAnimationStart() event. This can be
     * complex to keep track of since we notify listeners at different times depending on
     * startDelay and whether start() was called before end().
     */
    private boolean mStartListenersCalled = false;

    /**
     * Flag that denotes whether the animation is set up and ready to go. Used to
     * set up animation that has not yet been started.
     */
    boolean mInitialized = false;

    //
    // Backing variables
    //

    // How long the animation should last in ms
    private long mDuration = (long) (300 * sDurationScale);
    private long mUnscaledDuration = 300;

    // The amount of time in ms to delay starting the animation after start() is called
    private long mStartDelay = 0;
    private long mUnscaledStartDelay = 0;

    // The number of times the animation will repeat. The default is 0, which means the animation
    // will play only once
    private int mRepeatCount = 0;

    /**
     * The type of repetition that will occur when repeatMode is nonzero. RESTART means the
     * animation will start from the beginning on every new cycle. REVERSE means the animation
     * will reverse directions on each iteration.
     */
    private int mRepeatMode = RESTART;

    /**
     * The time interpolator to be used. The elapsed fraction of the animation will be passed
     * through this interpolator to calculate the interpolated fraction, which is then used to
     * calculate the animated values.
     */
    private headwayent.hotshotengine.android.animation.TimeInterpolator mInterpolator = sDefaultInterpolator;

    /**
     * The set of listeners to be sent events through the life of an animation.
     */
    ArrayList<AnimatorUpdateListener> mUpdateListeners = null;

    /**
     * The property/value sets being animated.
     */
    PropertyValuesHolder[] mValues;

    /**
     * A hashmap of the PropertyValuesHolder objects. This map is used to lookup animated values
     * by property name during calls to getAnimatedValue(String).
     */
    HashMap<String, PropertyValuesHolder> mValuesMap;

    /**
     * Public constants
     */

    /**
     * When the animation reaches the end and <code>repeatCount</code> is INFINITE
     * or a positive value, the animation restarts from the beginning.
     */
    public static final int RESTART = 1;
    /**
     * When the animation reaches the end and <code>repeatCount</code> is INFINITE
     * or a positive value, the animation reverses direction on every iteration.
     */
    public static final int REVERSE = 2;
    /**
     * This value used used with the {@link #setRepeatCount(int)} property to repeat
     * the animation indefinitely.
     */
    public static final int INFINITE = -1;


    /**
     * @hide
     */
    public static void setDurationScale(float durationScale) {
        sDurationScale = durationScale;
    }

    /**
     * @hide
     */
    public static float getDurationScale() {
        return sDurationScale;
    }

    /**
     * Creates a new ValueAnimator object. This default constructor is primarily for
     * use internally; the factory methods which take parameters are more generally
     * useful.
     */
    public ValueAnimator() {
    }

    /**
     * Constructs and returns a ValueAnimator that animates between int values. A single
     * value implies that that value is the one being animated to. However, this is not typically
     * useful in a ValueAnimator object because there is no way for the object to determine the
     * starting value for the animation (unlike ObjectAnimator, which can derive that value
     * from the target object and property being animated). Therefore, there should typically
     * be two or more values.
     *
     * @param values A set of values that the animation will animate between over time.
     * @return A ValueAnimator object that is set up to animate between the given values.
     */
    public static ValueAnimator ofInt(int... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(values);
        return anim;
    }

    /**
     * Constructs and returns a ValueAnimator that animates between color values. A single
     * value implies that that value is the one being animated to. However, this is not typically
     * useful in a ValueAnimator object because there is no way for the object to determine the
     * starting value for the animation (unlike ObjectAnimator, which can derive that value
     * from the target object and property being animated). Therefore, there should typically
     * be two or more values.
     *
     * @param values A set of values that the animation will animate between over time.
     * @return A ValueAnimator object that is set up to animate between the given values.
     */
    public static ValueAnimator ofArgb(int... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(values);
        anim.setEvaluator(ArgbEvaluator.getInstance());
        return anim;
    }

    /**
     * Constructs and returns a ValueAnimator that animates between float values. A single
     * value implies that that value is the one being animated to. However, this is not typically
     * useful in a ValueAnimator object because there is no way for the object to determine the
     * starting value for the animation (unlike ObjectAnimator, which can derive that value
     * from the target object and property being animated). Therefore, there should typically
     * be two or more values.
     *
     * @param values A set of values that the animation will animate between over time.
     * @return A ValueAnimator object that is set up to animate between the given values.
     */
    public static ValueAnimator ofFloat(float... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setFloatValues(values);
        return anim;
    }

    /**
     * Constructs and returns a ValueAnimator that animates between the values
     * specified in the PropertyValuesHolder objects.
     *
     * @param values A set of PropertyValuesHolder objects whose values will be animated
     *               between over time.
     * @return A ValueAnimator object that is set up to animate between the given values.
     */
    public static ValueAnimator ofPropertyValuesHolder(PropertyValuesHolder... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setValues(values);
        return anim;
    }

    /**
     * Constructs and returns a ValueAnimator that animates between Object values. A single
     * value implies that that value is the one being animated to. However, this is not typically
     * useful in a ValueAnimator object because there is no way for the object to determine the
     * starting value for the animation (unlike ObjectAnimator, which can derive that value
     * from the target object and property being animated). Therefore, there should typically
     * be two or more values.
     * <p/>
     * <p>Since ValueAnimator does not know how to animate between arbitrary Objects, this
     * factory method also takes a TypeEvaluator object that the ValueAnimator will use
     * to perform that interpolation.
     *
     * @param evaluator A TypeEvaluator that will be called on each animation frame to
     *                  provide the ncessry interpolation between the Object values to derive the animated
     *                  value.
     * @param values    A set of values that the animation will animate between over time.
     * @return A ValueAnimator object that is set up to animate between the given values.
     */
    public static ValueAnimator ofObject(TypeEvaluator evaluator, Object... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setObjectValues(values);
        anim.setEvaluator(evaluator);
        return anim;
    }

    /**
     * Sets int values that will be animated between. A single
     * value implies that that value is the one being animated to. However, this is not typically
     * useful in a ValueAnimator object because there is no way for the object to determine the
     * starting value for the animation (unlike ObjectAnimator, which can derive that value
     * from the target object and property being animated). Therefore, there should typically
     * be two or more values.
     * <p/>
     * <p>If there are already multiple sets of values defined for this ValueAnimator via more
     * than one PropertyValuesHolder object, this method will set the values for the first
     * of those objects.</p>
     *
     * @param values A set of values that the animation will animate between over time.
     */
    public void setIntValues(int... values) {
        if (values == null || values.length == 0) {
            return;
        }
        if (mValues == null || mValues.length == 0) {
            setValues(PropertyValuesHolder.ofInt("", values));
        } else {
            PropertyValuesHolder valuesHolder = mValues[0];
            valuesHolder.setIntValues(values);
        }
        // New property/values/target should cause re-initialization prior to starting
        mInitialized = false;
    }

    /**
     * Sets float values that will be animated between. A single
     * value implies that that value is the one being animated to. However, this is not typically
     * useful in a ValueAnimator object because there is no way for the object to determine the
     * starting value for the animation (unlike ObjectAnimator, which can derive that value
     * from the target object and property being animated). Therefore, there should typically
     * be two or more values.
     * <p/>
     * <p>If there are already multiple sets of values defined for this ValueAnimator via more
     * than one PropertyValuesHolder object, this method will set the values for the first
     * of those objects.</p>
     *
     * @param values A set of values that the animation will animate between over time.
     */
    public void setFloatValues(float... values) {
        if (values == null || values.length == 0) {
            return;
        }
        if (mValues == null || mValues.length == 0) {
            setValues(PropertyValuesHolder.ofFloat("", values));
        } else {
            PropertyValuesHolder valuesHolder = mValues[0];
            valuesHolder.setFloatValues(values);
        }
        // New property/values/target should cause re-initialization prior to starting
        mInitialized = false;
    }

    /**
     * Sets the values to animate between for this animation. A single
     * value implies that that value is the one being animated to. However, this is not typically
     * useful in a ValueAnimator object because there is no way for the object to determine the
     * starting value for the animation (unlike ObjectAnimator, which can derive that value
     * from the target object and property being animated). Therefore, there should typically
     * be two or more values.
     * <p/>
     * <p>If there are already multiple sets of values defined for this ValueAnimator via more
     * than one PropertyValuesHolder object, this method will set the values for the first
     * of those objects.</p>
     * <p/>
     * <p>There should be a TypeEvaluator set on the ValueAnimator that knows how to interpolate
     * between these value objects. ValueAnimator only knows how to interpolate between the
     * primitive types specified in the other setValues() methods.</p>
     *
     * @param values The set of values to animate between.
     */
    public void setObjectValues(Object... values) {
        if (values == null || values.length == 0) {
            return;
        }
        if (mValues == null || mValues.length == 0) {
            setValues(PropertyValuesHolder.ofObject("", null, values));
        } else {
            PropertyValuesHolder valuesHolder = mValues[0];
            valuesHolder.setObjectValues(values);
        }
        // New property/values/target should cause re-initialization prior to starting
        mInitialized = false;
    }

    /**
     * Sets the values, per property, being animated between. This function is called internally
     * by the constructors of ValueAnimator that take a list of values. But a ValueAnimator can
     * be constructed without values and this method can be called to set the values manually
     * instead.
     *
     * @param values The set of values, per property, being animated between.
     */
    public void setValues(PropertyValuesHolder... values) {
        int numValues = values.length;
        mValues = values;
        mValuesMap = new HashMap<>(numValues);
        for (PropertyValuesHolder valuesHolder : values) {
            mValuesMap.put(valuesHolder.getPropertyName(), valuesHolder);
        }
        // New property/values/target should cause re-initialization prior to starting
        mInitialized = false;
    }

    /**
     * Returns the values that this ValueAnimator animates between. These values are stored in
     * PropertyValuesHolder objects, even if the ValueAnimator was created with a simple list
     * of value objects instead.
     *
     * @return PropertyValuesHolder[] An array of PropertyValuesHolder objects which hold the
     * values, per property, that define the animation.
     */
    public PropertyValuesHolder[] getValues() {
        return mValues;
    }

    /**
     * This function is called immediately before processing the first animation
     * frame of an animation. If there is a nonzero <code>startDelay</code>, the
     * function is called after that delay ends.
     * It takes care of the final initialization steps for the
     * animation.
     * <p/>
     * <p>Overrides of this method should call the superclass method to ensure
     * that internal mechanisms for the animation are set up correctly.</p>
     */
    void initAnimation() {
        if (!mInitialized) {
            int numValues = mValues.length;
            for (PropertyValuesHolder mValue : mValues) {
                mValue.init();
            }
            mInitialized = true;
        }
    }


    /**
     * Sets the length of the animation. The default duration is 300 milliseconds.
     *
     * @param duration The length of the animation, in milliseconds. This value cannot
     *                 be negative.
     * @return ValueAnimator The object called with setDuration(). This return
     * value makes it easier to compose statements together that construct and then set the
     * duration, as in <code>ValueAnimator.ofInt(0, 10).setDuration(500).start()</code>.
     */
    public ValueAnimator setDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " +
                    duration);
        }
        mUnscaledDuration = duration;
        updateScaledDuration();
        return this;
    }

    private void updateScaledDuration() {
        mDuration = (long) (mUnscaledDuration * sDurationScale);
    }

    /**
     * Gets the length of the animation. The default duration is 300 milliseconds.
     *
     * @return The length of the animation, in milliseconds.
     */
    public long getDuration() {
        return mUnscaledDuration;
    }

    /**
     * Sets the position of the animation to the specified point in time. This time should
     * be between 0 and the total duration of the animation, including any repetition. If
     * the animation has not yet been started, then it will not advance forward after it is
     * set to this time; it will simply set the time to this value and perform any appropriate
     * actions based on that time. If the animation is already running, then setCurrentPlayTime()
     * will set the current playing time to this value and continue playing from that point.
     *
     * @param playTime The time, in milliseconds, to which the animation is advanced or rewound.
     */
    public void setCurrentPlayTime(long playTime) {
        initAnimation();
        long currentTime = AnimationUtils.currentAnimationTimeMillis();
        if (mPlayingState != RUNNING) {
            mSeekTime = playTime;
            mPlayingState = SEEKED;
        }
        mStartTime = currentTime - playTime;
        doAnimationFrame(currentTime);
    }

    /**
     * Gets the current position of the animation in time, which is equal to the current
     * time minus the time that the animation started. An animation that is not yet started will
     * return a value of zero.
     *
     * @return The current position in time of the animation.
     */
    public long getCurrentPlayTime() {
        if (!mInitialized || mPlayingState == STOPPED) {
            return 0;
        }
        return AnimationUtils.currentAnimationTimeMillis() - mStartTime;
    }

    /**
     * This custom, static handler handles the timing pulse that is shared by
     * all active animations. This approach ensures that the setting of animation
     * values will happen on the UI thread and that all animations will share
     * the same times for calculating their values, which makes synchronizing
     * animations possible.
     * <p/>
     * The handler uses the Choreographer for executing periodic callbacks.
     *
     * @hide
     */
    @SuppressWarnings("unchecked")
    protected static class AnimationHandler implements Runnable {
        // The per-thread list of all active animations
        /**
         * @hide
         */
        protected final ArrayList<ValueAnimator> mAnimations = new ArrayList<>();

        // Used in doAnimationFrame() to avoid concurrent modifications of mAnimations
        private final ArrayList<ValueAnimator> mTmpAnimations = new ArrayList<>();

        // The per-thread set of animations to be started on the next animation frame
        /**
         * @hide
         */
        protected final ArrayList<ValueAnimator> mPendingAnimations = new ArrayList<>();

        /**
         * Internal per-thread collections used to avoid set collisions as animations start and end
         * while being processed.
         *
         * @hide
         */
        protected final ArrayList<ValueAnimator> mDelayedAnims = new ArrayList<>();
        private final ArrayList<ValueAnimator> mEndingAnims = new ArrayList<>();
        private final ArrayList<ValueAnimator> mReadyAnims = new ArrayList<>();

        private final Choreographer mChoreographer;
        private boolean mAnimationScheduled;

        private AnimationHandler() {
            mChoreographer = Choreographer.getInstance();
        }

        /**
         * Start animating on the next frame.
         */
        public void start() {
            scheduleAnimation();
        }

        private void doAnimationFrame(long frameTime) {
            // mPendingAnimations holds any animations that have requested to be started
            // We're going to clear mPendingAnimations, but starting animation may
            // cause more to be added to the pending list (for example, if one animation
            // starting triggers another starting). So we loop until mPendingAnimations
            // is empty.
            while (!mPendingAnimations.isEmpty()) {
                ArrayList<ValueAnimator> pendingCopy =
                        (ArrayList<ValueAnimator>) mPendingAnimations.clone();
                mPendingAnimations.clear();
                int count = pendingCopy.size();
                for (int i = 0; i < count; ++i) {
                    ValueAnimator anim = pendingCopy.get(i);
                    // If the animation has a startDelay, place it on the delayed list
                    if (anim.mStartDelay == 0) {
                        anim.startAnimation(this);
                    } else {
                        mDelayedAnims.add(anim);
                    }
                }
            }
            // Next, process animations currently sitting on the delayed queue, adding
            // them to the active animations if they are ready
            int numDelayedAnims = mDelayedAnims.size();
            for (int i = 0; i < numDelayedAnims; ++i) {
                ValueAnimator anim = mDelayedAnims.get(i);
                if (anim.delayedAnimationFrame(frameTime)) {
                    mReadyAnims.add(anim);
                }
            }
            int numReadyAnims = mReadyAnims.size();
            if (numReadyAnims > 0) {
                for (int i = 0; i < numReadyAnims; ++i) {
                    ValueAnimator anim = mReadyAnims.get(i);
                    anim.startAnimation(this);
                    anim.mRunning = true;
                    mDelayedAnims.remove(anim);
                }
                mReadyAnims.clear();
            }

            // Now process all active animations. The return value from animationFrame()
            // tells the handler whether it should now be ended
            int numAnims = mAnimations.size();
            for (int i = 0; i < numAnims; ++i) {
                mTmpAnimations.add(mAnimations.get(i));
            }
            for (int i = 0; i < numAnims; ++i) {
                ValueAnimator anim = mTmpAnimations.get(i);
                if (mAnimations.contains(anim) && anim.doAnimationFrame(frameTime)) {
                    mEndingAnims.add(anim);
                }
            }
            mTmpAnimations.clear();
            if (!mEndingAnims.isEmpty()) {
                for (int i = 0; i < mEndingAnims.size(); ++i) {
                    mEndingAnims.get(i).endAnimation(this);
                }
                mEndingAnims.clear();
            }

            // If there are still active or delayed animations, schedule a future call to
            // onAnimate to process the next frame of the animations.
            if (!mAnimations.isEmpty() || !mDelayedAnims.isEmpty()) {
                scheduleAnimation();
            }
        }

        // Called by the Choreographer.
        @Override
        public void run() {
            mAnimationScheduled = false;
            doAnimationFrame(mChoreographer.getFrameTime());
        }

        private void scheduleAnimation() {
            if (!mAnimationScheduled) {
                mChoreographer.postCallback(Choreographer.CALLBACK_ANIMATION, this, null);
                mAnimationScheduled = true;
            }
        }
    }

    /**
     * The amount of time, in milliseconds, to delay starting the animation after
     * {@link #start()} is called.
     *
     * @return the number of milliseconds to delay running the animation
     */
    public long getStartDelay() {
        return mUnscaledStartDelay;
    }

    /**
     * The amount of time, in milliseconds, to delay starting the animation after
     * {@link #start()} is called.
     *
     * @param startDelay The amount of the delay, in milliseconds
     */
    public void setStartDelay(long startDelay) {
        this.mStartDelay = (long) (startDelay * sDurationScale);
        mUnscaledStartDelay = startDelay;
    }

    /**
     * The amount of time, in milliseconds, between each frame of the animation. This is a
     * requested time that the animation will attempt to honor, but the actual delay between
     * frames may be different, depending on system load and capabilities. This is a static
     * function because the same delay will be applied to all animations, since they are all
     * run off of a single timing loop.
     * <p/>
     * The frame delay may be ignored when the animation system uses an external timing
     * source, such as the display refresh rate (vsync), to govern animations.
     *
     * @return the requested time between frames, in milliseconds
     */
    public static long getFrameDelay() {
        return Choreographer.getFrameDelay();
    }

    /**
     * The amount of time, in milliseconds, between each frame of the animation. This is a
     * requested time that the animation will attempt to honor, but the actual delay between
     * frames may be different, depending on system load and capabilities. This is a static
     * function because the same delay will be applied to all animations, since they are all
     * run off of a single timing loop.
     * <p/>
     * The frame delay may be ignored when the animation system uses an external timing
     * source, such as the display refresh rate (vsync), to govern animations.
     *
     * @param frameDelay the requested time between frames, in milliseconds
     */
    public static void setFrameDelay(long frameDelay) {
        Choreographer.setFrameDelay(frameDelay);
    }

    /**
     * The most recent value calculated by this <code>ValueAnimator</code> when there is just one
     * property being animated. This value is only sensible while the animation is running. The main
     * purpose for this read-only property is to retrieve the value from the <code>ValueAnimator</code>
     * during a call to {@link AnimatorUpdateListener#onAnimationUpdate(ValueAnimator)}, which
     * is called during each animation frame, immediately after the value is calculated.
     *
     * @return animatedValue The value most recently calculated by this <code>ValueAnimator</code> for
     * the single property being animated. If there are several properties being animated
     * (specified by several PropertyValuesHolder objects in the constructor), this function
     * returns the animated value for the first of those objects.
     */
    public Object getAnimatedValue() {
        if (mValues != null && mValues.length > 0) {
            return mValues[0].getAnimatedValue();
        }
        // Shouldn't get here; should always have values unless ValueAnimator was set up wrong
        return null;
    }

    /**
     * The most recent value calculated by this <code>ValueAnimator</code> for <code>propertyName</code>.
     * The main purpose for this read-only property is to retrieve the value from the
     * <code>ValueAnimator</code> during a call to
     * {@link AnimatorUpdateListener#onAnimationUpdate(ValueAnimator)}, which
     * is called during each animation frame, immediately after the value is calculated.
     *
     * @return animatedValue The value most recently calculated for the named property
     * by this <code>ValueAnimator</code>.
     */
    public Object getAnimatedValue(String propertyName) {
        PropertyValuesHolder valuesHolder = mValuesMap.get(propertyName);
        if (valuesHolder != null) {
            return valuesHolder.getAnimatedValue();
        } else {
            // At least avoid crashing if called with bogus propertyName
            return null;
        }
    }

    /**
     * Sets how many times the animation should be repeated. If the repeat
     * count is 0, the animation is never repeated. If the repeat count is
     * greater than 0 or {@link #INFINITE}, the repeat mode will be taken
     * into account. The repeat count is 0 by default.
     *
     * @param value the number of times the animation should be repeated
     */
    public void setRepeatCount(int value) {
        mRepeatCount = value;
    }

    /**
     * Defines how many times the animation should repeat. The default value
     * is 0.
     *
     * @return the number of times the animation should repeat, or {@link #INFINITE}
     */
    public int getRepeatCount() {
        return mRepeatCount;
    }

    /**
     * Defines what this animation should do when it reaches the end. This
     * setting is applied only when the repeat count is either greater than
     * 0 or {@link #INFINITE}. Defaults to {@link #RESTART}.
     *
     * @param value {@link #RESTART} or {@link #REVERSE}
     */
    public void setRepeatMode(int value) {
        mRepeatMode = value;
    }

    /**
     * Defines what this animation should do when it reaches the end.
     *
     * @return either one of {@link #REVERSE} or {@link #RESTART}
     */
    public int getRepeatMode() {
        return mRepeatMode;
    }

    /**
     * Adds a listener to the set of listeners that are sent update events through the life of
     * an animation. This method is called on all listeners for every frame of the animation,
     * after the values for the animation have been calculated.
     *
     * @param listener the listener to be added to the current set of listeners for this animation.
     */
    public void addUpdateListener(AnimatorUpdateListener listener) {
        if (mUpdateListeners == null) {
            mUpdateListeners = new ArrayList<>();
        }
        mUpdateListeners.add(listener);
    }

    /**
     * Removes all listeners from the set listening to frame updates for this animation.
     */
    public void removeAllUpdateListeners() {
        if (mUpdateListeners == null) {
            return;
        }
        mUpdateListeners.clear();
        mUpdateListeners = null;
    }

    /**
     * Removes a listener from the set listening to frame updates for this animation.
     *
     * @param listener the listener to be removed from the current set of update listeners
     *                 for this animation.
     */
    public void removeUpdateListener(AnimatorUpdateListener listener) {
        if (mUpdateListeners == null) {
            return;
        }
        mUpdateListeners.remove(listener);
        if (mUpdateListeners.isEmpty()) {
            mUpdateListeners = null;
        }
    }


    /**
     * The time interpolator used in calculating the elapsed fraction of this animation. The
     * interpolator determines whether the animation runs with linear or non-linear motion,
     * such as acceleration and deceleration. The default value is
     * {@link AccelerateDecelerateInterpolator}
     *
     * @param value the interpolator to be used by this animation. A value of <code>null</code>
     *              will result in linear interpolation.
     */
    @Override
    public void setInterpolator(headwayent.hotshotengine.android.animation.TimeInterpolator value) {
        if (value != null) {
            mInterpolator = value;
        } else {
            mInterpolator = new LinearInterpolator();
        }
    }

    /**
     * Returns the timing interpolator that this ValueAnimator uses.
     *
     * @return The timing interpolator for this ValueAnimator.
     */
    @Override
    public headwayent.hotshotengine.android.animation.TimeInterpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * The type evaluator to be used when calculating the animated values of this animation.
     * The system will automatically assign a float or int evaluator based on the type
     * of <code>startValue</code> and <code>endValue</code> in the constructor. But if these values
     * are not one of these primitive types, or if different evaluation is desired (such as is
     * necessary with int values that represent colors), a custom evaluator needs to be assigned.
     * For example, when running an animation on color values, the {@link ArgbEvaluator}
     * should be used to get correct RGB color interpolation.
     * <p/>
     * <p>If this ValueAnimator has only one set of values being animated between, this evaluator
     * will be used for that set. If there are several sets of values being animated, which is
     * the case if PropertyValuesHolder objects were set on the ValueAnimator, then the evaluator
     * is assigned just to the first PropertyValuesHolder object.</p>
     *
     * @param value the evaluator to be used this animation
     */
    public void setEvaluator(TypeEvaluator value) {
        if (value != null && mValues != null && mValues.length > 0) {
            mValues[0].setEvaluator(value);
        }
    }

    private void notifyStartListeners() {
        if (mListeners != null && !mStartListenersCalled) {
            ArrayList<AnimatorListener> tmpListeners =
                    (ArrayList<AnimatorListener>) mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                tmpListeners.get(i).onAnimationStart(this);
            }
        }
        mStartListenersCalled = true;
    }

    /**
     * Start the animation playing. This version of start() takes a boolean flag that indicates
     * whether the animation should play in reverse. The flag is usually false, but may be set
     * to true if called from the reverse() method.
     * <p/>
     * <p>The animation started by calling this method will be run on the thread that called
     * this method. This thread should have a Looper on it (a runtime exception will be thrown if
     * this is not the case). Also, if the animation will animate
     * properties of objects in the view hierarchy, then the calling thread should be the UI
     * thread for that view hierarchy.</p>
     *
     * @param playBackwards Whether the ValueAnimator should start playing in reverse.
     */
    private void start(boolean playBackwards) {
        if (Looper.myLooper() == null) {
            throw new RuntimeException("Animators may only be run on Looper threads");
        }
        mPlayingBackwards = playBackwards;
        mCurrentIteration = 0;
        mPlayingState = STOPPED;
        mStarted = true;
        mStartedDelay = false;
        mPaused = false;
        updateScaledDuration(); // in case the scale factor has changed since creation time
        AnimationHandler animationHandler = getOrCreateAnimationHandler();
        animationHandler.mPendingAnimations.add(this);
        if (mStartDelay == 0) {
            // This sets the initial value of the animation, prior to actually starting it running
            setCurrentPlayTime(0);
            mPlayingState = STOPPED;
            mRunning = true;
            notifyStartListeners();
        }
        animationHandler.start();
    }

    @Override
    public void start() {
        start(false);
    }

    @Override
    public void cancel() {
        // Only cancel if the animation is actually running or has been started and is about
        // to run
        AnimationHandler handler = getOrCreateAnimationHandler();
        if (mPlayingState != STOPPED
                || handler.mPendingAnimations.contains(this)
                || handler.mDelayedAnims.contains(this)) {
            // Only notify listeners if the animator has actually started
            if ((mStarted || mRunning) && mListeners != null) {
                if (!mRunning) {
                    // If it's not yet running, then start listeners weren't called. Call them now.
                    notifyStartListeners();
                }
                ArrayList<AnimatorListener> tmpListeners =
                        (ArrayList<AnimatorListener>) mListeners.clone();
                for (AnimatorListener listener : tmpListeners) {
                    listener.onAnimationCancel(this);
                }
            }
            endAnimation(handler);
        }
    }

    @Override
    public void end() {
        AnimationHandler handler = getOrCreateAnimationHandler();
        if (!handler.mAnimations.contains(this) && !handler.mPendingAnimations.contains(this)) {
            // Special case if the animation has not yet started; get it ready for ending
            mStartedDelay = false;
            startAnimation(handler);
            mStarted = true;
        } else if (!mInitialized) {
            initAnimation();
        }
        animateValue(mPlayingBackwards ? 0f : 1f);
        endAnimation(handler);
    }

    @Override
    public void resume() {
        if (mPaused) {
            mResumed = true;
        }
        super.resume();
    }

    @Override
    public void pause() {
        boolean previouslyPaused = mPaused;
        super.pause();
        if (!previouslyPaused && mPaused) {
            mPauseTime = -1;
            mResumed = false;
        }
    }

    @Override
    public boolean isRunning() {
        return (mPlayingState == RUNNING || mRunning);
    }

    @Override
    public boolean isStarted() {
        return mStarted;
    }

    /**
     * Plays the ValueAnimator in reverse. If the animation is already running,
     * it will stop itself and play backwards from the point reached when reverse was called.
     * If the animation is not currently running, then it will start from the end and
     * play backwards. This behavior is only set for the current animation; future playing
     * of the animation will use the default behavior of playing forward.
     */
    @Override
    public void reverse() {
        mPlayingBackwards = !mPlayingBackwards;
        if (mPlayingState == RUNNING) {
            long currentTime = AnimationUtils.currentAnimationTimeMillis();
            long currentPlayTime = currentTime - mStartTime;
            long timeLeft = mDuration - currentPlayTime;
            mStartTime = currentTime - timeLeft;
        } else if (mStarted) {
            end();
        } else {
            start(true);
        }
    }

    /**
     * @hide
     */
    @Override
    public boolean canReverse() {
        return true;
    }

    /**
     * Called internally to end an animation by removing it from the animations list. Must be
     * called on the UI thread.
     *
     * @hide
     */
    protected void endAnimation(AnimationHandler handler) {
        handler.mAnimations.remove(this);
        handler.mPendingAnimations.remove(this);
        handler.mDelayedAnims.remove(this);
        mPlayingState = STOPPED;
        mPaused = false;
        if ((mStarted || mRunning) && mListeners != null) {
            if (!mRunning) {
                // If it's not yet running, then start listeners weren't called. Call them now.
                notifyStartListeners();
            }
            ArrayList<AnimatorListener> tmpListeners =
                    (ArrayList<AnimatorListener>) mListeners.clone();
            int numListeners = tmpListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                tmpListeners.get(i).onAnimationEnd(this);
            }
        }
        mRunning = false;
        mStarted = false;
        mStartListenersCalled = false;
        mPlayingBackwards = false;
//        if (Trace.isTagEnabled(Trace.TRACE_TAG_VIEW)) {
//            Trace.asyncTraceEnd(Trace.TRACE_TAG_VIEW, getNameForTrace(),
//                    System.identityHashCode(this));
//        }
    }

    /**
     * Called internally to start an animation by adding it to the active animations list. Must be
     * called on the UI thread.
     */
    private void startAnimation(AnimationHandler handler) {
//        if (Trace.isTagEnabled(Trace.TRACE_TAG_VIEW)) {
//            Trace.asyncTraceBegin(Trace.TRACE_TAG_VIEW, getNameForTrace(),
//                    System.identityHashCode(this));
//        }
        initAnimation();
        handler.mAnimations.add(this);
        if (mStartDelay > 0 && mListeners != null) {
            // Listeners were already notified in start() if startDelay is 0; this is
            // just for delayed animations
            notifyStartListeners();
        }
    }

    /**
     * Returns the name of this animator for debugging purposes.
     */
    String getNameForTrace() {
        return "animator";
    }


    /**
     * Internal function called to process an animation frame on an animation that is currently
     * sleeping through its <code>startDelay</code> phase. The return value indicates whether it
     * should be woken up and put on the active animations queue.
     *
     * @param currentTime The current animation time, used to calculate whether the animation
     *                    has exceeded its <code>startDelay</code> and should be started.
     * @return True if the animation's <code>startDelay</code> has been exceeded and the animation
     * should be added to the set of active animations.
     */
    private boolean delayedAnimationFrame(long currentTime) {
        if (!mStartedDelay) {
            mStartedDelay = true;
            mDelayStartTime = currentTime;
        }
        if (mPaused) {
            if (mPauseTime < 0) {
                mPauseTime = currentTime;
            }
            return false;
        } else if (mResumed) {
            mResumed = false;
            if (mPauseTime > 0) {
                // Offset by the duration that the animation was paused
                mDelayStartTime += (currentTime - mPauseTime);
            }
        }
        long deltaTime = currentTime - mDelayStartTime;
        if (deltaTime > mStartDelay) {
            // startDelay ended - start the anim and record the
            // mStartTime appropriately
            mStartTime = currentTime - (deltaTime - mStartDelay);
            mPlayingState = RUNNING;
            return true;
        }
        return false;
    }

    /**
     * This internal function processes a single animation frame for a given animation. The
     * currentTime parameter is the timing pulse sent by the handler, used to calculate the
     * elapsed duration, and therefore
     * the elapsed fraction, of the animation. The return value indicates whether the animation
     * should be ended (which happens when the elapsed time of the animation exceeds the
     * animation's duration, including the repeatCount).
     *
     * @param currentTime The current time, as tracked by the static timing handler
     * @return true if the animation's duration, including any repetitions due to
     * <code>repeatCount</code>, has been exceeded and the animation should be ended.
     */
    boolean animationFrame(long currentTime) {
        boolean done = false;
        switch (mPlayingState) {
            case RUNNING:
            case SEEKED:
                float fraction = mDuration > 0 ? (float) (currentTime - mStartTime) / mDuration : 1f;
                if (fraction >= 1f) {
                    if (mCurrentIteration < mRepeatCount || mRepeatCount == INFINITE) {
                        // Time to repeat
                        if (mListeners != null) {
                            int numListeners = mListeners.size();
                            for (int i = 0; i < numListeners; ++i) {
                                mListeners.get(i).onAnimationRepeat(this);
                            }
                        }
                        if (mRepeatMode == REVERSE) {
                            mPlayingBackwards = !mPlayingBackwards;
                        }
                        mCurrentIteration += (int) fraction;
                        fraction = fraction % 1f;
                        mStartTime += mDuration;
                    } else {
                        done = true;
                        fraction = Math.min(fraction, 1.0f);
                    }
                }
                if (mPlayingBackwards) {
                    fraction = 1f - fraction;
                }
                animateValue(fraction);
                break;
        }

        return done;
    }

    /**
     * Processes a frame of the animation, adjusting the start time if needed.
     *
     * @param frameTime The frame time.
     * @return true if the animation has ended.
     */
    final boolean doAnimationFrame(long frameTime) {
        if (mPlayingState == STOPPED) {
            mPlayingState = RUNNING;
            if (mSeekTime < 0) {
                mStartTime = frameTime;
            } else {
                mStartTime = frameTime - mSeekTime;
                // Now that we're playing, reset the seek time
                mSeekTime = -1;
            }
        }
        if (mPaused) {
            if (mPauseTime < 0) {
                mPauseTime = frameTime;
            }
            return false;
        } else if (mResumed) {
            mResumed = false;
            if (mPauseTime > 0) {
                // Offset by the duration that the animation was paused
                mStartTime += (frameTime - mPauseTime);
            }
        }
        // The frame time might be before the start time during the first frame of
        // an animation.  The "current time" must always be on or after the start
        // time to avoid animating frames at negative time intervals.  In practice, this
        // is very rare and only happens when seeking backwards.
        final long currentTime = Math.max(frameTime, mStartTime);
        return animationFrame(currentTime);
    }

    /**
     * Returns the current animation fraction, which is the elapsed/interpolated fraction used in
     * the most recent frame update on the animation.
     *
     * @return Elapsed/interpolated fraction of the animation.
     */
    public float getAnimatedFraction() {
        return mCurrentFraction;
    }

    /**
     * This method is called with the elapsed fraction of the animation during every
     * animation frame. This function turns the elapsed fraction into an interpolated fraction
     * and then into an animated value (from the evaluator. The function is called mostly during
     * animation updates, but it is also called when the <code>end()</code>
     * function is called, to set the final value on the property.
     * <p/>
     * <p>Overrides of this method must call the superclass to perform the calculation
     * of the animated value.</p>
     *
     * @param fraction The elapsed fraction of the animation.
     */
    void animateValue(float fraction) {
        fraction = mInterpolator.getInterpolation(fraction);
        mCurrentFraction = fraction;
        int numValues = mValues.length;
        for (PropertyValuesHolder mValue : mValues) {
            mValue.calculateValue(fraction);
        }
        if (mUpdateListeners != null) {
            int numListeners = mUpdateListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                mUpdateListeners.get(i).onAnimationUpdate(this);
            }
        }
    }

    @Override
    public ValueAnimator clone() {
        final ValueAnimator anim = (ValueAnimator) super.clone();
        if (mUpdateListeners != null) {
            ArrayList<AnimatorUpdateListener> oldListeners = mUpdateListeners;
            anim.mUpdateListeners = new ArrayList<>();
            int numListeners = oldListeners.size();
            for (int i = 0; i < numListeners; ++i) {
                anim.mUpdateListeners.add(oldListeners.get(i));
            }
        }
        anim.mSeekTime = -1;
        anim.mPlayingBackwards = false;
        anim.mCurrentIteration = 0;
        anim.mInitialized = false;
        anim.mPlayingState = STOPPED;
        anim.mStartedDelay = false;
        PropertyValuesHolder[] oldValues = mValues;
        if (oldValues != null) {
            int numValues = oldValues.length;
            anim.mValues = new PropertyValuesHolder[numValues];
            anim.mValuesMap = new HashMap<>(numValues);
            for (int i = 0; i < numValues; ++i) {
                PropertyValuesHolder newValuesHolder = oldValues[i].clone();
                anim.mValues[i] = newValuesHolder;
                anim.mValuesMap.put(newValuesHolder.getPropertyName(), newValuesHolder);
            }
        }
        return anim;
    }

    /**
     * Implementors of this interface can add themselves as update listeners
     * to an <code>ValueAnimator</code> instance to receive callbacks on every animation
     * frame, after the current frame's values have been calculated for that
     * <code>ValueAnimator</code>.
     */
    public interface AnimatorUpdateListener {
        /**
         * <p>Notifies the occurrence of another frame of the animation.</p>
         *
         * @param animation The animation which was repeated.
         */
        void onAnimationUpdate(ValueAnimator animation);

    }

    /**
     * Return the number of animations currently running.
     * <p/>
     * Used by StrictMode internally to annotate violations.
     * May be called on arbitrary threads!
     *
     * @hide
     */
    public static int getCurrentAnimationsCount() {
        AnimationHandler handler = sAnimationHandler.get();
        return handler != null ? handler.mAnimations.size() : 0;
    }

    /**
     * Clear all animations on this thread, without canceling or ending them.
     * This should be used with caution.
     *
     * @hide
     */
    public static void clearAllAnimations() {
        AnimationHandler handler = sAnimationHandler.get();
        if (handler != null) {
            handler.mAnimations.clear();
            handler.mPendingAnimations.clear();
            handler.mDelayedAnims.clear();
        }
    }

    private static AnimationHandler getOrCreateAnimationHandler() {
        AnimationHandler handler = sAnimationHandler.get();
        if (handler == null) {
            handler = new AnimationHandler();
            sAnimationHandler.set(handler);
        }
        return handler;
    }

    @Override
    public String toString() {
        StringBuilder returnVal = new StringBuilder("ValueAnimator@" + Integer.toHexString(hashCode()));
        if (mValues != null) {
            for (PropertyValuesHolder mValue : mValues) {
                returnVal.append("\n    ").append(mValue.toString());
            }
        }
        return returnVal.toString();
    }

    /**
     * <p>Whether or not the ValueAnimator is allowed to run asynchronously off of
     * the UI thread. This is a hint that informs the ValueAnimator that it is
     * OK to run the animation off-thread, however ValueAnimator may decide
     * that it must run the animation on the UI thread anyway. For example if there
     * is an {@link AnimatorUpdateListener} the animation will run on the UI thread,
     * regardless of the value of this hint.</p>
     * <p/>
     * <p>Regardless of whether or not the animation runs asynchronously, all
     * listener callbacks will be called on the UI thread.</p>
     * <p/>
     * <p>To be able to use this hint the following must be true:</p>
     * <ol>
     * <li>{@link #getAnimatedFraction()} is not needed (it will return undefined values).</li>
     * <li>The animator is immutable while {@link #isStarted()} is true. Requests
     * to change values, duration, delay, etc... may be ignored.</li>
     * <li>Lifecycle callback events may be asynchronous. Events such as
     * {@link AnimatorListener#onAnimationEnd(Animator)} or
     * {@link AnimatorListener#onAnimationRepeat(Animator)} may end up delayed
     * as they must be posted back to the UI thread, and any actions performed
     * by those callbacks (such as starting new animations) will not happen
     * in the same frame.</li>
     * <li>State change requests ({@link #cancel()}, {@link #end()}, {@link #reverse()}, etc...)
     * may be asynchronous. It is guaranteed that all state changes that are
     * performed on the UI thread in the same frame will be applied as a single
     * atomic update, however that frame may be the current frame,
     * the next frame, or some future frame. This will also impact the observed
     * state of the Animator. For example, {@link #isStarted()} may still return true
     * after a call to {@link #end()}. Using the lifecycle callbacks is preferred over
     * queries to {@link #isStarted()}, {@link #isRunning()}, and {@link #isPaused()}
     * for this reason.</li>
     * </ol>
     *
     * @hide
     */
    @Override
    public void setAllowRunningAsynchronously(boolean mayRunAsync) {
        // It is up to subclasses to support this, if they can.
    }
}
