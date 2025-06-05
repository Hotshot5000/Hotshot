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
import headwayent.hotshotengine.renderer.plugins.particlefx.ColourFaderAffector.CmdAlphaAdjust;
import headwayent.hotshotengine.renderer.plugins.particlefx.ColourFaderAffector.CmdBlueAdjust;
import headwayent.hotshotengine.renderer.plugins.particlefx.ColourFaderAffector.CmdGreenAdjust;
import headwayent.hotshotengine.renderer.plugins.particlefx.ColourFaderAffector.CmdRedAdjust;

import java.util.Iterator;

public class ColourFaderAffector2 extends ENG_ParticleAffector {

    public static class CmdRedAdjust1 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getRedAdjust1());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setRedAdjust1(Float.parseFloat(val));
        }

    }

    public static class CmdGreenAdjust1 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getGreenAdjust1());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setGreenAdjust1(Float.parseFloat(val));
        }

    }

    public static class CmdBlueAdjust1 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getBlueAdjust1());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setBlueAdjust1(Float.parseFloat(val));
        }

    }

    public static class CmdAlphaAdjust1 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getAlphaAdjust1());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setAlphaAdjust1(Float.parseFloat(val));
        }

    }

    public static class CmdRedAdjust2 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getRedAdjust2());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setRedAdjust2(Float.parseFloat(val));
        }

    }

    public static class CmdGreenAdjust2 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getGreenAdjust2());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setGreenAdjust2(Float.parseFloat(val));
        }

    }

    public static class CmdBlueAdjust2 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getBlueAdjust2());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setBlueAdjust2(Float.parseFloat(val));
        }

    }

    public static class CmdAlphaAdjust2 implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getAlphaAdjust2());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setAlphaAdjust2(Float.parseFloat(val));
        }

    }

    public static class CmdStateChange implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {
            
            return String.valueOf(((ColourFaderAffector2) target).getStateChange());
        }

        @Override
        public void doSet(Object target, String val) {
            
            ((ColourFaderAffector2) target).setStateChange(Float.parseFloat(val));
        }

    }

    public static final CmdRedAdjust msRedCmd1 = new CmdRedAdjust();
    public static final CmdGreenAdjust msGreenCmd1 = new CmdGreenAdjust();
    public static final CmdBlueAdjust msBlueCmd1 = new CmdBlueAdjust();
    public static final CmdAlphaAdjust msAlphaCmd1 = new CmdAlphaAdjust();

    public static final CmdRedAdjust msRedCmd2 = new CmdRedAdjust();
    public static final CmdGreenAdjust msGreenCmd2 = new CmdGreenAdjust();
    public static final CmdBlueAdjust msBlueCmd2 = new CmdBlueAdjust();
    public static final CmdAlphaAdjust msAlphaCmd2 = new CmdAlphaAdjust();

    public static final CmdStateChange msStateCmd = new CmdStateChange();

    protected float mRedAdj1, mRedAdj2;
    protected float mGreenAdj1, mGreenAdj2;
    protected float mBlueAdj1, mBlueAdj2;
    protected float mAlphaAdj1, mAlphaAdj2;
    protected float StateChangeVal;

    public ColourFaderAffector2(ENG_ParticleSystem pSys) {
        super(pSys);
        

        mRedAdj1 = mGreenAdj1 = mBlueAdj1 = mAlphaAdj1 = 0;
        mRedAdj2 = mGreenAdj2 = mBlueAdj2 = mAlphaAdj2 = 0;
        mType = "ColourFader2";
        StateChangeVal = 1;    // Switch when there is 1 second left on the TTL

        // Init parameters
        if (getStringInterface().createParamDictionary("ColourFaderAffector2")) {
            ENG_ParamDictionary dict = getStringInterface().getParamDictionary();

            // Phase 1
            dict.addParameter(new ENG_ParameterDef("red1",
                    "The amount by which to adjust the red component of particles per second.",
                    ParameterType.PT_REAL), msRedCmd1);
            dict.addParameter(new ENG_ParameterDef("green1",
                    "The amount by which to adjust the green component of particles per second.",
                    ParameterType.PT_REAL), msGreenCmd1);
            dict.addParameter(new ENG_ParameterDef("blue1",
                    "The amount by which to adjust the blue component of particles per second.",
                    ParameterType.PT_REAL), msBlueCmd1);
            dict.addParameter(new ENG_ParameterDef("alpha1",
                    "The amount by which to adjust the alpha component of particles per second.",
                    ParameterType.PT_REAL), msAlphaCmd1);

            // Phase 2
            dict.addParameter(new ENG_ParameterDef("red2",
                    "The amount by which to adjust the red component of particles per second.",
                    ParameterType.PT_REAL), msRedCmd2);
            dict.addParameter(new ENG_ParameterDef("green2",
                    "The amount by which to adjust the green component of particles per second.",
                    ParameterType.PT_REAL), msGreenCmd2);
            dict.addParameter(new ENG_ParameterDef("blue2",
                    "The amount by which to adjust the blue component of particles per second.",
                    ParameterType.PT_REAL), msBlueCmd2);
            dict.addParameter(new ENG_ParameterDef("alpha2",
                    "The amount by which to adjust the alpha component of particles per second.",
                    ParameterType.PT_REAL), msAlphaCmd2);

            // State Change Value
            dict.addParameter(new ENG_ParameterDef("state_change",
                    "When the particle has this much time to live left, it will switch to state 2.",
                    ParameterType.PT_REAL), msStateCmd);

        }
    }

    private final ENG_Float f = new ENG_Float();

    @Override
    public void _affectParticles(ENG_ParticleSystem pSystem, float timeElapsed) {


        float dr1, dg1, db1, da1;
        float dr2, dg2, db2, da2;

        // Scale adjustments by time
        dr1 = mRedAdj1 * timeElapsed;
        dg1 = mGreenAdj1 * timeElapsed;
        db1 = mBlueAdj1 * timeElapsed;
        da1 = mAlphaAdj1 * timeElapsed;

        // Scale adjustments by time
        dr2 = mRedAdj2 * timeElapsed;
        dg2 = mGreenAdj2 * timeElapsed;
        db2 = mBlueAdj2 * timeElapsed;
        da2 = mAlphaAdj2 * timeElapsed;

        Iterator<ENG_Particle> iterator = pSystem._getIterator();
        while (iterator.hasNext()) {
            ENG_Particle next = iterator.next();
            if (next.timeToLive > StateChangeVal) {
                f.setValue(next.colour.r);
                ColourFaderAffector.applyAdjustWithClamp(f, dr1);
                next.colour.r = f.getValue();
                f.setValue(next.colour.g);
                ColourFaderAffector.applyAdjustWithClamp(f, dg1);
                next.colour.g = f.getValue();
                f.setValue(next.colour.b);
                ColourFaderAffector.applyAdjustWithClamp(f, db1);
                next.colour.b = f.getValue();
                f.setValue(next.colour.a);
                ColourFaderAffector.applyAdjustWithClamp(f, da1);
                next.colour.a = f.getValue();
            } else {
                f.setValue(next.colour.r);
                ColourFaderAffector.applyAdjustWithClamp(f, dr2);
                next.colour.r = f.getValue();
                f.setValue(next.colour.g);
                ColourFaderAffector.applyAdjustWithClamp(f, dg2);
                next.colour.g = f.getValue();
                f.setValue(next.colour.b);
                ColourFaderAffector.applyAdjustWithClamp(f, db2);
                next.colour.b = f.getValue();
                f.setValue(next.colour.a);
                ColourFaderAffector.applyAdjustWithClamp(f, da2);
                next.colour.a = f.getValue();
            }
        }
    }

    public void setAdjust1(float red, float green, float blue, float alpha) {
        mRedAdj1 = red;
        mGreenAdj1 = green;
        mBlueAdj1 = blue;
        mAlphaAdj1 = alpha;
    }

    public void setRedAdjust1(float red) {
        mRedAdj1 = red;
    }

    public float getRedAdjust1() {
        return mRedAdj1;
    }

    public void setGreenAdjust1(float green) {
        mGreenAdj1 = green;
    }

    public float getGreenAdjust1() {
        return mGreenAdj1;
    }

    public void setBlueAdjust1(float blue) {
        mBlueAdj1 = blue;
    }

    public float getBlueAdjust1() {
        return mBlueAdj1;
    }

    public void setAlphaAdjust1(float alpha) {
        mAlphaAdj1 = alpha;
    }

    public float getAlphaAdjust1() {
        return mAlphaAdj1;
    }

    public void setAdjust2(float red, float green, float blue, float alpha) {
        mRedAdj2 = red;
        mGreenAdj2 = green;
        mBlueAdj2 = blue;
        mAlphaAdj2 = alpha;
    }

    public void setRedAdjust2(float red) {
        mRedAdj2 = red;
    }

    public float getRedAdjust2() {
        return mRedAdj2;
    }

    public void setGreenAdjust2(float green) {
        mGreenAdj2 = green;
    }

    public float getGreenAdjust2() {
        return mGreenAdj2;
    }

    public void setBlueAdjust2(float blue) {
        mBlueAdj2 = blue;
    }

    public float getBlueAdjust2() {
        return mBlueAdj2;
    }

    public void setAlphaAdjust2(float alpha) {
        mAlphaAdj2 = alpha;
    }

    public float getAlphaAdjust2() {
        return mAlphaAdj2;
    }

    public void setStateChange(float time) {
        StateChangeVal = time;
    }

    public float getStateChange() {
        return StateChangeVal;
    }

}
