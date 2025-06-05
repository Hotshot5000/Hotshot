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
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.renderer.ENG_Particle;
import headwayent.hotshotengine.renderer.ENG_ParticleAffector;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

import java.util.Iterator;

public class ColourFaderAffector extends ENG_ParticleAffector {

    public static class CmdRedAdjust implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ColourFaderAffector) target).getRedAdjust());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ColourFaderAffector) target).setRedAdjust(Float.parseFloat(val));
        }

    }

    public static class CmdGreenAdjust implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ColourFaderAffector) target).getGreenAdjust());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ColourFaderAffector) target).setGreenAdjust(Float.parseFloat(val));
        }

    }

    public static class CmdBlueAdjust implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ColourFaderAffector) target).getBlueAdjust());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ColourFaderAffector) target).setBlueAdjust(Float.parseFloat(val));
        }

    }

    public static class CmdAlphaAdjust implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((ColourFaderAffector) target).getAlphaAdjust());
        }

        @Override
        public void doSet(Object target, String val) {

            ((ColourFaderAffector) target).setAlphaAdjust(Float.parseFloat(val));
        }

    }

    public static final CmdRedAdjust msRedCmd = new CmdRedAdjust();
    public static final CmdGreenAdjust msGreenCmd = new CmdGreenAdjust();
    public static final CmdBlueAdjust msBlueCmd = new CmdBlueAdjust();
    public static final CmdAlphaAdjust msAlphaCmd = new CmdAlphaAdjust();

    private final ENG_Float f = new ENG_Float();

    protected float mRedAdj;
    protected float mGreenAdj;
    protected float mBlueAdj;
    protected float mAlphaAdj;

    protected static void applyAdjustWithClamp(ENG_Float pComponent, float adjust) {
        pComponent.setValue(pComponent.getValue() + adjust);
        // Limit to 0
        if (pComponent.getValue() < 0.0f) {
            pComponent.setValue(0.0f);
        }
        // Limit to 1
        else if (pComponent.getValue() > 1.0f) {
            pComponent.setValue(1.0f);
        }
    }

    public ColourFaderAffector(ENG_ParticleSystem pSys) {
        super(pSys);
        

        mRedAdj = mGreenAdj = mBlueAdj = mAlphaAdj = 0;
        mType = "ColourFader";

        // Init parameters
        if (getStringInterface().createParamDictionary("ColourFaderAffector")) {
            ENG_ParamDictionary dict = getStringInterface().getParamDictionary();

            dict.addParameter(new ENG_ParameterDef("red",
                    "The amount by which to adjust the red component of particles per second.",
                    ParameterType.PT_REAL), msRedCmd);
            dict.addParameter(new ENG_ParameterDef("green",
                    "The amount by which to adjust the green component of particles per second.",
                    ParameterType.PT_REAL), msGreenCmd);
            dict.addParameter(new ENG_ParameterDef("blue",
                    "The amount by which to adjust the blue component of particles per second.",
                    ParameterType.PT_REAL), msBlueCmd);
            dict.addParameter(new ENG_ParameterDef("alpha",
                    "The amount by which to adjust the alpha component of particles per second.",
                    ParameterType.PT_REAL), msAlphaCmd);


        }
    }

    @Override
    public void _affectParticles(ENG_ParticleSystem pSystem, float timeElapsed) {


        ENG_Particle p = null;
        float dr, dg, db, da;

        // Scale adjustments by time
        dr = mRedAdj * timeElapsed;
        dg = mGreenAdj * timeElapsed;
        db = mBlueAdj * timeElapsed;
        da = mAlphaAdj * timeElapsed;

        Iterator<ENG_Particle> iterator = pSystem._getIterator();
        while (iterator.hasNext()) {
            ENG_Particle next = iterator.next();
            f.setValue(next.colour.r);
            applyAdjustWithClamp(f, dr);
            next.colour.r = f.getValue();
            f.setValue(next.colour.g);
            applyAdjustWithClamp(f, dg);
            next.colour.g = f.getValue();
            f.setValue(next.colour.b);
            applyAdjustWithClamp(f, db);
            next.colour.b = f.getValue();
            f.setValue(next.colour.a);
            applyAdjustWithClamp(f, da);
            next.colour.a = f.getValue();
        }
    }

    public void setAdjust(float red, float green, float blue, float alpha) {
        mRedAdj = red;
        mGreenAdj = green;
        mBlueAdj = blue;
        mAlphaAdj = alpha;
    }

    public void setRedAdjust(float red) {
        mRedAdj = red;
    }

    public float getRedAdjust() {
        return mRedAdj;
    }

    public void setGreenAdjust(float green) {
        mGreenAdj = green;
    }

    public float getGreenAdjust() {
        return mGreenAdj;
    }

    public void setBlueAdjust(float blue) {
        mBlueAdj = blue;
    }

    public float getBlueAdjust() {
        return mBlueAdj;
    }

    public void setAlphaAdjust(float alpha) {
        mAlphaAdj = alpha;
    }

    public float getAlphaAdjust() {
        return mAlphaAdj;
    }

}
