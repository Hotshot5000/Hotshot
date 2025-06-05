/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import java.util.Iterator;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.ENG_StringConverter;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_Particle;
import headwayent.hotshotengine.renderer.ENG_ParticleAffector;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

public class LinearForceAffector extends ENG_ParticleAffector {

    public static class CmdForceVector implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return ((LinearForceAffector) target).getForceVector().toString();
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((LinearForceAffector) target).setForceVector(
                    ENG_StringConverter.parseVector4(val));
        }

    }

    public static class CmdForceApp implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            ForceApplication fa = ((LinearForceAffector) target).getForceApplication();
            switch (fa) {
                case FA_ADD:
                    return "add";
                case FA_AVERAGE:
                    return "average";
            }
            //Should never get here
            throw new IllegalArgumentException("invalid ForceApplication!!!");
        }

        @Override
        public void doSet(Object target, String val) {
            
            ForceApplication fa;
            if (val.equalsIgnoreCase("average")) {
                fa = ForceApplication.FA_AVERAGE;
            } else if (val.equalsIgnoreCase("add")) {
                fa = ForceApplication.FA_ADD;
            } else {
                throw new IllegalArgumentException("either average or add");
            }
            ((LinearForceAffector) target).setForceApplication(fa);
        }

    }

    /// Command objects
    protected static final CmdForceVector msForceVectorCmd = new CmdForceVector();
    protected static final CmdForceApp msForceAppCmd = new CmdForceApp();

    /// Force vector
    protected final ENG_Vector4D mForceVector = new ENG_Vector4D();

    /// How to apply force
    protected ForceApplication mForceApplication;

    public enum ForceApplication {
        /// Take the average of the force vector and the particle momentum
        FA_AVERAGE,
        /// Add the force vector to the particle momentum
        FA_ADD
    }

    public LinearForceAffector(ENG_ParticleSystem pSys) {
        super(pSys);
        

        mType = "LinearForce";

        // Default to gravity-like
        mForceApplication = ForceApplication.FA_ADD;
        mForceVector.x = mForceVector.z = 0;
        mForceVector.y = -100;

        // Set up parameters
        if (getStringInterface().createParamDictionary("LinearForceAffector")) {
            addBaseParameters();
            // Add extra paramaters
            ENG_ParamDictionary dict = getStringInterface().getParamDictionary();
            dict.addParameter(new ENG_ParameterDef("force_vector",
                    "The vector representing the force to apply.",
                    ParameterType.PT_VECTOR3), msForceVectorCmd);
            dict.addParameter(new ENG_ParameterDef("force_application",
                    "How to apply the force vector to partices.",
                    ParameterType.PT_STRING), msForceAppCmd);

        }
    }

    private final ENG_Vector4D scaledVector = new ENG_Vector4D();

    @Override
    public void _affectParticles(ENG_ParticleSystem pSystem, float timeElapsed) {
        


        scaledVector.set(ENG_Math.VEC4_ZERO);

        // Precalc scaled force for optimisation
        if (mForceApplication == ForceApplication.FA_ADD) {
            // Scale force by time
            mForceVector.mul(timeElapsed, scaledVector);
        }

        Iterator<ENG_Particle> iterator = pSystem._getIterator();
        while (iterator.hasNext()) {
            ENG_Particle next = iterator.next();
            if (mForceApplication == ForceApplication.FA_ADD) {
                next.direction.addInPlace(scaledVector);
            } else // FA_AVERAGE
            {
                //	next.direction = (next.direction + mForceVector) / 2;
                next.direction.addInPlace(mForceVector);
                next.direction.mul(0.5f);
            }
        }
    }

    public ENG_Vector4D getForceVector() {
        return new ENG_Vector4D(mForceVector);
    }

    public void setForceVector(ENG_Vector4D mForceVector) {
        this.mForceVector.set(mForceVector);
    }

    public ForceApplication getForceApplication() {
        return mForceApplication;
    }

    public void setForceApplication(ForceApplication mForceApplication) {
        this.mForceApplication = mForceApplication;
    }

}
