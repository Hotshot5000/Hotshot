/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Degree;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_StringConverter;
import headwayent.hotshotengine.ENG_StringIntefaceInterface;
import headwayent.hotshotengine.ENG_StringInterface;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;

public abstract class ENG_ParticleEmitter extends ENG_Particle implements
        ENG_StringIntefaceInterface {

    public static class CmdAngle implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return Float.toString(
                    (((ENG_ParticleEmitter) target).getAngle()).valueDegrees());
        }

        @Override
        public void doSet(Object target, String val) {

            // Bleah
            ((ENG_ParticleEmitter) target).setAngle(
                    new ENG_Radian(
                            new ENG_Degree(Float.parseFloat(val))));
        }

    }

    public static class CmdColour implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_ParticleEmitter) target).getColour().toString();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setColour(
                    ENG_StringConverter.parseColourValue(val));
        }

    }

    public static class CmdColourRangeStart implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_ParticleEmitter) target).getColourRangeStart().toString();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setColourRangeStart(
                    ENG_StringConverter.parseColourValue(val));
        }

    }

    public static class CmdColourRangeEnd implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_ParticleEmitter) target).getColourRangeEnd().toString();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setColourRangeEnd(
                    ENG_StringConverter.parseColourValue(val));
        }

    }

    public static class CmdDirection implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_ParticleEmitter) target).getDirection().toString();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setDirection(
                    ENG_StringConverter.parseVector4(val));
        }

    }

    public static class CmdEmissionRate implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target).getEmissionRate());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setEmissionRate(Float.parseFloat(val));
        }

    }

    public static class CmdMaxTTL implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target).getMaxTimeToLive());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMaxTimeToLive(Float.parseFloat(val));
        }

    }

    public static class CmdMinTTL implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target).getMinTimeToLive());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMinTimeToLive(Float.parseFloat(val));
        }

    }

    public static class CmdMaxVelocity implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getMaxParticleVelocity());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMaxParticleVelocity(
                    Float.parseFloat(val));
        }

    }

    public static class CmdMinVelocity implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getMinParticleVelocity());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMinParticleVelocity(
                    Float.parseFloat(val));
        }

    }

    public static class CmdPosition implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return ((ENG_ParticleEmitter) target).getPosition().toString();
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setPosition(
                    ENG_StringConverter.parseVector4(val));
        }

    }

    public static class CmdTTL implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getTimeToLive());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setTimeToLive(
                    Float.parseFloat(val));
        }

    }

    public static class CmdVelocity implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getParticleVelocity());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setParticleVelocity(
                    Float.parseFloat(val));
        }

    }

    public static class CmdDuration implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getDuration());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setDuration(
                    Float.parseFloat(val));
        }

    }

    public static class CmdMinDuration implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getMinDuration());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMinDuration(
                    Float.parseFloat(val));
        }

    }

    public static class CmdMaxDuration implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getMaxDuration());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMaxDuration(
                    Float.parseFloat(val));
        }

    }

    public static class CmdRepeatDelay implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getRepeatDelay());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setRepeatDelay(
                    Float.parseFloat(val));
        }

    }

    public static class CmdMinRepeatDelay implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getMinRepeatDelay());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMinRepeatDelay(
                    Float.parseFloat(val));
        }

    }

    public static class CmdMaxRepeatDelay implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getMaxRepeatDelay());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setMaxRepeatDelay(
                    Float.parseFloat(val));
        }

    }

    public static class CmdName implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getName());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setName(
                    val);
        }


    }

    public static class CmdEmittedEmitter implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ENG_ParticleEmitter) target)
                    .getEmittedEmitter());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ENG_ParticleEmitter) target).setEmittedEmitter(
                    val);
        }

    }

    private final ENG_StringInterface stringInterface = new ENG_StringInterface(this);

    @Override
    public ENG_StringInterface getStringInterface() {

        return stringInterface;
    }

    // Command object for setting / getting parameters
    protected static final CmdAngle msAngleCmd = new CmdAngle();
    protected static final CmdColour msColourCmd = new CmdColour();
    protected static final CmdColourRangeStart msColourRangeStartCmd = new CmdColourRangeStart();
    protected static final CmdColourRangeEnd msColourRangeEndCmd = new CmdColourRangeEnd();
    protected static final CmdDirection msDirectionCmd = new CmdDirection();
    protected static final CmdEmissionRate msEmissionRateCmd = new CmdEmissionRate();
    protected static final CmdMaxTTL msMaxTTLCmd = new CmdMaxTTL();
    protected static final CmdMaxVelocity msMaxVelocityCmd = new CmdMaxVelocity();
    protected static final CmdMinTTL msMinTTLCmd = new CmdMinTTL();
    protected static final CmdMinVelocity msMinVelocityCmd = new CmdMinVelocity();
    protected static final CmdPosition msPositionCmd = new CmdPosition();
    protected static final CmdTTL msTTLCmd = new CmdTTL();
    protected static final CmdVelocity msVelocityCmd = new CmdVelocity();
    protected static final CmdDuration msDurationCmd = new CmdDuration();
    protected static final CmdMinDuration msMinDurationCmd = new CmdMinDuration();
    protected static final CmdMaxDuration msMaxDurationCmd = new CmdMaxDuration();
    protected static final CmdRepeatDelay msRepeatDelayCmd = new CmdRepeatDelay();
    protected static final CmdMinRepeatDelay msMinRepeatDelayCmd = new CmdMinRepeatDelay();
    protected static final CmdMaxRepeatDelay msMaxRepeatDelayCmd = new CmdMaxRepeatDelay();
    protected static final CmdName msNameCmd = new CmdName();
    protected static final CmdEmittedEmitter msEmittedEmitterCmd = new CmdEmittedEmitter();

    /// Parent particle system
    protected final ENG_ParticleSystem mParent;
    /// Position relative to the center of the ParticleSystem
    protected final ENG_Vector4D mPosition = new ENG_Vector4D(true);
    /// Rate in particles per second at which this emitter wishes to emit particles
    protected float mEmissionRate;
    /// Name of the type of emitter, MUST be initialised by subclasses
    protected String mType = "";
    /// Base direction of the emitter, may not be used by some emitters
    protected final ENG_Vector4D mDirection = new ENG_Vector4D();
    // Notional up vector, just used to speed up generation of variant directions
    protected final ENG_Vector4D mUp = new ENG_Vector4D();
    /// Angle around direction which particles may be emitted, internally radians but angleunits for interface
    protected final ENG_Radian mAngle = new ENG_Radian();
    /// Min speed of particles
    protected float mMinSpeed;
    /// Max speed of particles
    protected float mMaxSpeed;
    /// Initial time-to-live of particles (min)
    protected float mMinTTL;
    /// Initial time-to-live of particles (max)
    protected float mMaxTTL;
    /// Initial colour of particles (range start)
    protected final ENG_ColorValue mColourRangeStart = new ENG_ColorValue();
    /// Initial colour of particles (range end)
    protected final ENG_ColorValue mColourRangeEnd = new ENG_ColorValue();

    /// Whether this emitter is currently enabled (defaults to true)
    protected boolean mEnabled;

    /// Start time (in seconds from start of first call to ParticleSystem to update)
    protected float mStartTime;
    /// Minimum length of time emitter will run for (0 = forever)
    protected float mDurationMin;
    /// Maximum length of time the emitter will run for (0 = forever)
    protected float mDurationMax;
    /// Current duration remainder
    protected float mDurationRemain;

    /// Time between each repeat
    protected float mRepeatDelayMin;
    protected float mRepeatDelayMax;
    /// Repeat delay left
    protected float mRepeatDelayRemain;

    // Fractions of particles wanted to be emitted last time
    protected float mRemainder;

    /// The name of the emitter. The name is optional unless it is used as an emitter that is emitted itself.
    protected String mName = "";

    /// The name of the emitter to be emitted (optional)
    protected String mEmittedEmitter = "";

    // If 'true', this emitter is emitted by another emitter.
    // NB. That doesn�t imply that the emitter itself emits other emitters (that could or could not be the case)
    protected boolean mEmitted;

    public void _initParticle(ENG_Particle particle) {
        particle.resetDimensions();
    }

    public abstract short _getEmissionCount(float timeElapsed);

    public String getType() {
        return mType;
    }

    public void addBaseParameters() {
        ENG_ParamDictionary dict = getStringInterface().getParamDictionary();

        dict.addParameter(new ENG_ParameterDef("angle",
                        "The angle up to which particles may vary in their initial direction " +
                                "from the emitters direction, in degrees.", ParameterType.PT_REAL),
                msAngleCmd);

        dict.addParameter(new ENG_ParameterDef("colour",
                        "The colour of emitted particles.", ParameterType.PT_COLOURVALUE),
                msColourCmd);

        dict.addParameter(new ENG_ParameterDef("colour_range_start",
                        "The start of a range of colours to be assigned to emitted particles.",
                        ParameterType.PT_COLOURVALUE),
                msColourRangeStartCmd);

        dict.addParameter(new ENG_ParameterDef("colour_range_end",
                        "The end of a range of colours to be assigned to emitted particles.",
                        ParameterType.PT_COLOURVALUE),
                msColourRangeEndCmd);

        dict.addParameter(new ENG_ParameterDef("direction",
                        "The base direction of the emitter.", ParameterType.PT_VECTOR3),
                msDirectionCmd);

        dict.addParameter(new ENG_ParameterDef("emission_rate",
                        "The number of particles emitted per second.", ParameterType.PT_REAL),
                msEmissionRateCmd);

        dict.addParameter(new ENG_ParameterDef("position",
                        "The position of the emitter relative to the particle system center.",
                        ParameterType.PT_VECTOR3),
                msPositionCmd);

        dict.addParameter(new ENG_ParameterDef("velocity",
                        "The initial velocity to be assigned to every particle, in world units per second.",
                        ParameterType.PT_REAL),
                msVelocityCmd);

        dict.addParameter(new ENG_ParameterDef("velocity_min",
                        "The minimum initial velocity to be assigned to each particle.",
                        ParameterType.PT_REAL),
                msMinVelocityCmd);

        dict.addParameter(new ENG_ParameterDef("velocity_max",
                        "The maximum initial velocity to be assigned to each particle.",
                        ParameterType.PT_REAL),
                msMaxVelocityCmd);

        dict.addParameter(new ENG_ParameterDef("time_to_live",
                        "The lifetime of each particle in seconds.", ParameterType.PT_REAL),
                msTTLCmd);

        dict.addParameter(new ENG_ParameterDef("time_to_live_min",
                        "The minimum lifetime of each particle in seconds.",
                        ParameterType.PT_REAL),
                msMinTTLCmd);

        dict.addParameter(new ENG_ParameterDef("time_to_live_max",
                        "The maximum lifetime of each particle in seconds.",
                        ParameterType.PT_REAL),
                msMaxTTLCmd);

        dict.addParameter(new ENG_ParameterDef("duration",
                        "The length of time in seconds which an emitter stays enabled for.",
                        ParameterType.PT_REAL),
                msDurationCmd);

        dict.addParameter(new ENG_ParameterDef("duration_min",
                        "The minimum length of time in seconds which an emitter stays enabled for.",
                        ParameterType.PT_REAL),
                msMinDurationCmd);

        dict.addParameter(new ENG_ParameterDef("duration_max",
                        "The maximum length of time in seconds which an emitter stays enabled for.",
                        ParameterType.PT_REAL),
                msMaxDurationCmd);

        dict.addParameter(new ENG_ParameterDef("repeat_delay",
                        "If set, after disabling an emitter will repeat (reenable) after this many seconds.",
                        ParameterType.PT_REAL),
                msRepeatDelayCmd);

        dict.addParameter(new ENG_ParameterDef("repeat_delay_min",
                        "If set, after disabling an emitter will repeat (reenable) after this minimum number of seconds.",
                        ParameterType.PT_REAL),
                msMinRepeatDelayCmd);

        dict.addParameter(new ENG_ParameterDef("repeat_delay_max",
                        "If set, after disabling an emitter will repeat (reenable) after this maximum number of seconds.",
                        ParameterType.PT_REAL),
                msMaxRepeatDelayCmd);

        dict.addParameter(new ENG_ParameterDef("name",
                        "This is the name of the emitter", ParameterType.PT_STRING),
                msNameCmd);

        dict.addParameter(new ENG_ParameterDef("emit_emitter",
                        "If set, this emitter will emit other emitters instead of visual particles",
                        ParameterType.PT_STRING),
                msEmittedEmitterCmd);
    }

    private final ENG_Radian zero = new ENG_Radian(0);

    protected void genEmissionDirection(ENG_Vector4D destVector) {
        if (!mAngle.equals(zero)) {
            ENG_Radian rad = new ENG_Radian(
                    ENG_Utility.getRandom().nextFloat() * mAngle.valueRadians());
            mDirection.randomDeviant(rad.valueRadians(), mUp, destVector);
        } else {
            destVector.set(mDirection);
        }
    }

    protected void genEmissionVelocity(ENG_Vector4D destVector) {
        float scalar;
        if (mMinSpeed != mMaxSpeed) {
            scalar = mMinSpeed +
                    (ENG_Utility.getRandom().nextFloat() * (mMaxSpeed - mMinSpeed));
        } else {
            scalar = mMinSpeed;
        }
        destVector.mul(scalar);
    }

    protected float genEmissionTTL() {
        if (mMinTTL != mMaxTTL) {
            return mMinTTL + (ENG_Utility.getRandom().nextFloat() * (mMaxTTL - mMinTTL));
        }
        return mMinTTL;
    }

    public short genConstantEmissionCount(float timeElapsed) {
        short intRequest;

        if (mEnabled) {
            // Keep fractions, otherwise a high frame rate will result in zero emissions!
            mRemainder += mEmissionRate * timeElapsed;
            intRequest = (short) mRemainder;
            mRemainder -= intRequest;

            // Check duration
            if (mDurationMax != 0.0f) {
                mDurationRemain -= timeElapsed;
                if (mDurationRemain <= 0) {
                    // Disable, duration is out (takes effect next time)
                    setEnabled(false);
                }
            }
            return intRequest;
        } else {
            // Check repeat
            if (mRepeatDelayMax != 0.0f) {
                mRepeatDelayRemain -= timeElapsed;
                if (mRepeatDelayRemain <= 0) {
                    // Enable, repeat delay is out (takes effect next time)
                    setEnabled(true);
                }
            }
            if (mStartTime != 0.0f) {
                mStartTime -= timeElapsed;
                if (mStartTime <= 0) {
                    setEnabled(true);
                    mStartTime = 0;
                }
            }
            return 0;
        }

    }

    protected void genEmissionColour(ENG_ColorValue destColour) {
        if (mColourRangeStart.notEquals(mColourRangeEnd)) {
            // Randomise
            //Real t = Math::UnitRandom();
            destColour.r = mColourRangeStart.r + (ENG_Utility.getRandom().nextFloat() *
                    (mColourRangeEnd.r - mColourRangeStart.r));
            destColour.g = mColourRangeStart.g + (ENG_Utility.getRandom().nextFloat() *
                    (mColourRangeEnd.g - mColourRangeStart.g));
            destColour.b = mColourRangeStart.b + (ENG_Utility.getRandom().nextFloat() *
                    (mColourRangeEnd.b - mColourRangeStart.b));
            destColour.a = mColourRangeStart.a + (ENG_Utility.getRandom().nextFloat() *
                    (mColourRangeEnd.a - mColourRangeStart.a));
        } else {
            destColour.set(mColourRangeStart);
        }
    }

    public boolean getEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {

        mEnabled = enabled;
        // Reset duration & repeat
        initDurationRepeat();
    }

    protected void initDurationRepeat() {

        if (mEnabled) {
            if (mDurationMin == mDurationMax) {
                mDurationRemain = mDurationMin;
            } else {
                mDurationRemain = ENG_Utility.rangeRandom(mDurationMin, mDurationMax);
            }
        } else {
            // Reset repeat
            if (mRepeatDelayMin == mRepeatDelayMax) {
                mRepeatDelayRemain = mRepeatDelayMin;
            } else {
                mRepeatDelayRemain =
                        ENG_Utility.rangeRandom(mRepeatDelayMax, mRepeatDelayMin);
            }

        }
    }

    public ENG_ParticleEmitter(ENG_ParticleSystem p) {
        mParent = p;

        // Reasonable defaults
        mAngle.set(0.0f);
        setDirection(ENG_Math.VEC4_X_UNIT);
        mEmissionRate = 10;
        mMaxSpeed = mMinSpeed = 1;
        mMaxTTL = mMinTTL = 5;
        mPosition.set(ENG_Math.PT4_ZERO);
        mColourRangeStart.set(ENG_ColorValue.WHITE);
        mColourRangeEnd.set(ENG_ColorValue.WHITE);
        mEnabled = true;
        mRemainder = 0;
        mName = "";
        mEmittedEmitter = "";
        mEmitted = false;
    }

    public void setPosition(ENG_Vector4D pos) {
        mPosition.set(pos);
    }

    public ENG_Vector4D getPosition() {
        return new ENG_Vector4D(mPosition);
    }

    public void getPosition(ENG_Vector4D ret) {
        ret.set(mPosition);
    }

    public void setDirection(ENG_Vector4D dir) {
        mDirection.set(dir);
        mDirection.normalize();
        // Generate an up vector (any will do)
        mDirection.perpendicular(mUp);
        //    mUp.normalize();
    }

    public ENG_Vector4D getDirection() {
        return new ENG_Vector4D(mDirection);
    }

    public void getDirection(ENG_Vector4D ret) {
        ret.set(mDirection);
    }

    public void setAngle(ENG_Radian rad) {
        mAngle.set(rad);
    }

    public ENG_Radian getAngle() {
        return new ENG_Radian(mAngle);
    }

    public void getAngle(ENG_Radian ret) {
        ret.set(mAngle);
    }

    public void setParticleVelocity(float s) {
        mMinSpeed = s;
        mMaxSpeed = s;
    }

    public void setParticleVelocity(float min, float max) {
        mMinSpeed = min;
        mMaxSpeed = max;
    }

    public void setEmissionRate(float rate) {
        mEmissionRate = rate;
    }

    public float getEmissionRate() {
        return mEmissionRate;
    }

    public void setTimeToLive(float f) {
        mMinTTL = f;
        mMaxTTL = f;
    }

    public void setTimeToLive(float min, float max) {
        mMinTTL = min;
        mMaxTTL = max;
    }

    public void setColour(ENG_ColorValue v) {
        mColourRangeStart.set(v);
        mColourRangeEnd.set(v);
    }

    public void setColour(ENG_ColorValue min, ENG_ColorValue max) {
        mColourRangeStart.set(min);
        mColourRangeEnd.set(max);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmittedEmitter() {
        return mEmittedEmitter;
    }

    public void setEmittedEmitter(String em) {
        mEmittedEmitter = em;
    }

    public boolean isEmitted() {
        return mEmitted;
    }

    public void setEmitted(boolean emitted) {
        mEmitted = emitted;
    }

    public float getParticleVelocity() {
        return mMinSpeed;
    }

    public float getMinParticleVelocity() {
        return mMinSpeed;
    }

    public float getMaxParticleVelocity() {
        return mMaxSpeed;
    }

    public void setMinParticleVelocity(float f) {
        mMinSpeed = f;
    }

    public void setMaxParticleVelocity(float f) {
        mMaxSpeed = f;
    }

    public float getTimeToLive() {
        return mMinTTL;
    }

    public float getMinTimeToLive() {
        return mMinTTL;
    }

    public float getMaxTimeToLive() {
        return mMaxTTL;
    }

    public void setMinTimeToLive(float f) {
        mMinTTL = f;
    }

    public void setMaxTimeToLive(float f) {
        mMaxTTL = f;
    }

    public ENG_ColorValue getColour() {
        return new ENG_ColorValue(mColourRangeStart);
    }

    public void getColour(ENG_ColorValue ret) {
        ret.set(mColourRangeStart);
    }

    public ENG_ColorValue getColourRangeStart() {
        return new ENG_ColorValue(mColourRangeStart);
    }

    public void getColourRangeStart(ENG_ColorValue ret) {
        ret.set(mColourRangeStart);
    }

    public ENG_ColorValue getColourRangeEnd() {
        return new ENG_ColorValue(mColourRangeEnd);
    }

    public void getColourRangeEnd(ENG_ColorValue ret) {
        ret.set(mColourRangeEnd);
    }

    public void setColourRangeStart(ENG_ColorValue c) {
        mColourRangeStart.set(c);
    }

    public void setColourRangeEnd(ENG_ColorValue c) {
        mColourRangeEnd.set(c);
    }

    public void setStartTime(float s) {
        setEnabled(false);
        mStartTime = s;
    }

    public float getStartTime() {
        return mStartTime;
    }

    public void setDuration(float duration) {
        setDuration(duration, duration);
    }

    public float getDuration() {
        return mDurationMin;
    }

    public void setDuration(float min, float max) {

        mDurationMin = min;
        mDurationMax = max;
        initDurationRepeat();
    }

    public void setMinDuration(float min) {
        mDurationMin = min;
        initDurationRepeat();
    }

    public void setMaxDuration(float max) {
        mDurationMax = max;
        initDurationRepeat();
    }

    public void setRepeatDelay(float delay) {
        setRepeatDelay(delay, delay);
    }

    public float getRepeatDelay() {
        return mRepeatDelayMin;
    }

    public void setRepeatDelay(float min, float max) {
        mRepeatDelayMin = min;
        mRepeatDelayMax = max;
        initDurationRepeat();
    }

    public void setMinRepeatDelay(float min) {
        mRepeatDelayMin = min;
        initDurationRepeat();
    }

    public void setMaxRepeatDelay(float max) {
        mRepeatDelayMax = max;
        initDurationRepeat();
    }

    public float getMinDuration() {
        return mDurationMin;
    }

    public float getMaxDuration() {
        return mDurationMax;
    }

    public float getMinRepeatDelay() {
        return mRepeatDelayMin;
    }

    public float getMaxRepeatDelay() {
        return mRepeatDelayMax;
    }

}
