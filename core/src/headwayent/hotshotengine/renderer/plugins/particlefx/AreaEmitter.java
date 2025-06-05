/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.plugins.particlefx;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_ParamCommand;
import headwayent.hotshotengine.ENG_ParamDictionary;
import headwayent.hotshotengine.ENG_ParameterDef;
import headwayent.hotshotengine.ENG_ParameterDef.ParameterType;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_ParticleEmitter;
import headwayent.hotshotengine.renderer.ENG_ParticleSystem;

public class AreaEmitter extends ENG_ParticleEmitter {

    public static class CmdWidth implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((AreaEmitter) target).getWidth());
        }

        @Override
        public void doSet(Object target, String val) {

            ((AreaEmitter) target).setWidth(Float.parseFloat(val));
        }

    }

    public static class CmdHeight implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((AreaEmitter) target).getHeight());
        }

        @Override
        public void doSet(Object target, String val) {

            ((AreaEmitter) target).setHeight(Float.parseFloat(val));
        }

    }

    public static class CmdDepth implements ENG_ParamCommand {

        @Override
        public String doGet(Object target) {

            return String.valueOf(((AreaEmitter) target).getDepth());
        }

        @Override
        public void doSet(Object target, String val) {

            ((AreaEmitter) target).setDepth(Float.parseFloat(val));
        }

    }

    /// Size of the area
    protected ENG_Vector4D mSize;// = new ENG_Vector4D();

    /// Local axes, not normalised, their magnitude reflects area size
    protected ENG_Vector4D mXRange;// = new ENG_Vector4D();
    protected ENG_Vector4D mYRange;// = new ENG_Vector4D();
    protected ENG_Vector4D mZRange;// = new ENG_Vector4D();

    /// Command objects
    protected static final CmdWidth msWidthCmd = new CmdWidth();
    protected static final CmdHeight msHeightCmd = new CmdHeight();
    protected static final CmdDepth msDepthCmd = new CmdDepth();

    protected void genAreaAxes() {
        ENG_Vector4D mLeft = mUp.crossProduct(mDirection);
        if (mSize == null) {
            mSize = new ENG_Vector4D();
        }
        if (mXRange == null) {
            mXRange = new ENG_Vector4D();
        }
        if (mYRange == null) {
            mYRange = new ENG_Vector4D();
        }
        if (mZRange == null) {
            mZRange = new ENG_Vector4D();
        }
        mLeft.mul(mSize.x * 0.5f, mXRange);
        mUp.mul(mSize.y * 0.5f, mYRange);
        mDirection.mul(mSize.z, mZRange);
    }

    protected boolean initDefaults(String t) {
        mDirection.set(ENG_Math.VEC4_Z_UNIT);
        mUp.set(ENG_Math.VEC3_Y_UNIT);

        setSize(100, 100, 100);
        mType = t;

        // Set up parameters
        if (getStringInterface().createParamDictionary(mType + "Emitter")) {

            addBaseParameters();
            ENG_ParamDictionary dict = getStringInterface().getParamDictionary();

            // Custom params
            dict.addParameter(new ENG_ParameterDef("width",
                    "Width of the shape in world coordinates.",
                    ParameterType.PT_REAL), msWidthCmd);
            dict.addParameter(new ENG_ParameterDef("height",
                    "Height of the shape in world coordinates.",
                    ParameterType.PT_REAL), msHeightCmd);
            dict.addParameter(new ENG_ParameterDef("depth",
                    "Depth of the shape in world coordinates.",
                    ParameterType.PT_REAL), msDepthCmd);
            return true;

        }
        return false;
    }

    @Override
    public void setDirection(ENG_Vector4D dir) {

        super.setDirection(dir);
        genAreaAxes();
    }

    public void setSize(ENG_Vector4D vec) {
        mSize.set(vec);
    }

    public void setSize(float x, float y, float z) {

        mSize.x = x;
        mSize.y = y;
        mSize.z = z;
        genAreaAxes();
    }

    public void setWidth(float width) {
        mSize.x = width;
        genAreaAxes();
    }

    public float getWidth() {
        return mSize.x;
    }

    public void setHeight(float height) {
        mSize.y = height;
        genAreaAxes();
    }

    public float getHeight() {
        return mSize.y;
    }

    public void setDepth(float depth) {
        mSize.z = depth;
        genAreaAxes();
    }

    public float getDepth() {
        return mSize.z;
    }

    public AreaEmitter(ENG_ParticleSystem p) {
        super(p);
        
    }

    @Override
    public short _getEmissionCount(float timeElapsed) {

        // Use basic constant emission
        return genConstantEmissionCount(timeElapsed);
    }

}
