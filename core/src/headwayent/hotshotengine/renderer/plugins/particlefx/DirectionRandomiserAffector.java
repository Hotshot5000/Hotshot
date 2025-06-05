/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_Particle;
import headwayent.hotshotengine.renderer.ENG_ParticleAffector;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

import java.util.Iterator;

public class DirectionRandomiserAffector extends ENG_ParticleAffector {

    public static class CmdRandomness implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(
                    ((DirectionRandomiserAffector) target).getRandomness());
        }

        @Override
        public void doSet(Object target, String val) {

            ((DirectionRandomiserAffector) target).setRandomness(Float.parseFloat(val));
        }

    }

    public static class CmdScope implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(
                    ((DirectionRandomiserAffector) target).getScope());
        }

        @Override
        public void doSet(Object target, String val) {

            ((DirectionRandomiserAffector) target).setScope(Float.parseFloat(val));
        }

    }

    public static class CmdKeepVelocity implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(
                    ((DirectionRandomiserAffector) target).getKeepVelocity());
        }

        @Override
        public void doSet(Object target, String val) {

            ((DirectionRandomiserAffector) target).setKeepVelocity(
                    Boolean.parseBoolean(val));
        }

    }

    /// Command objects
    protected static final CmdRandomness msRandomnessCmd = new CmdRandomness();
    protected static final CmdScope msScopeCmd = new CmdScope();
    protected static final CmdKeepVelocity msKeepVelocityCmd = new CmdKeepVelocity();

    protected float mRandomness;
    protected float mScope;
    protected boolean mKeepVelocity;

    public DirectionRandomiserAffector(ENG_ParticleSystem pSys) {
        super(pSys);
        
        mType = "DirectionRandomiser";

        // defaults
        mRandomness = 1.0f;
        mScope = 1.0f;
        mKeepVelocity = false;

        // Set up parameters
        if (getStringInterface().createParamDictionary("DirectionRandomiserAffector")) {
            addBaseParameters();
            // Add extra paramaters
            ENG_ParamDictionary dict = getStringInterface().getParamDictionary();
            dict.addParameter(new ENG_ParameterDef("randomness",
                    "The amount of randomness (chaos) to apply to the particle movement.",
                    ParameterType.PT_REAL), msRandomnessCmd);
            dict.addParameter(new ENG_ParameterDef("scope",
                    "The percentage of particles which is affected.",
                    ParameterType.PT_REAL), msScopeCmd);
            dict.addParameter(new ENG_ParameterDef("keep_velocity",
                    "Detemines whether the velocity of the particles is changed.",
                    ParameterType.PT_BOOL), msKeepVelocityCmd);
        }
    }

    private final ENG_Vector4D temp = new ENG_Vector4D();

    @Override
    public void _affectParticles(ENG_ParticleSystem pSystem, float timeElapsed) {


        float length = 0.0f;
        Iterator<ENG_Particle> iterator = pSystem._getIterator();
        while (iterator.hasNext()) {
            ENG_Particle p = iterator.next();
            if (mScope > ENG_Utility.getRandom().nextFloat()) {
                if (!p.direction.isZeroLength()) {
                    if (mKeepVelocity) {
                        length = p.direction.length();
                    }

                    temp.set(ENG_Utility.rangeRandom(-mRandomness, mRandomness) *
                                    timeElapsed,
                            ENG_Utility.rangeRandom(-mRandomness, mRandomness) *
                                    timeElapsed,
                            ENG_Utility.rangeRandom(-mRandomness, mRandomness) *
                                    timeElapsed);

                    p.direction.addInPlace(temp);

                    if (mKeepVelocity) {
                        p.direction.mul(length / p.direction.length());
                    }
                }
            }
        }
    }

    public float getRandomness() {
        return mRandomness;
    }

    public void setRandomness(float mRandomness) {
        this.mRandomness = mRandomness;
    }

    public float getScope() {
        return mScope;
    }

    public void setScope(float mScope) {
        this.mScope = mScope;
    }

    public boolean getKeepVelocity() {
        return mKeepVelocity;
    }

    public void setKeepVelocity(boolean mKeepVelocity) {
        this.mKeepVelocity = mKeepVelocity;
    }

}
