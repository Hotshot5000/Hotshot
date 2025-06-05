/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;

public class ENG_GpuProgramUsage {

    protected final GpuProgramType mType;
    protected final ENG_Pass mParent;

    protected ENG_GpuProgram mProgram;

    protected ENG_GpuProgramParameters mParameters;

    /// Whether to recreate parameters next load
    protected boolean mRecreateParams;

    protected void recreateParameters() {
        // Keep a reference to old ones to copy
        ENG_GpuProgramParameters savedParams = mParameters;

        // Create new params
        mParameters = mProgram.createParameters();

        // Copy old (matching) values across
        // Don't use copyConstantsFrom since program may be different
        if (savedParams != null) {
            mParameters.copyMatchingNamedConstantsFrom(savedParams);
        }

        mRecreateParams = false;
    }

    public ENG_GpuProgramUsage(GpuProgramType type, ENG_Pass parent) {
        mType = type;
        mParent = parent;
    }

    public ENG_GpuProgramUsage(ENG_GpuProgramUsage oth, ENG_Pass parent) {
        mType = oth.getType();
        mProgram = oth.getProgram();
        mParent = parent;
        mParameters = new ENG_GpuProgramParameters(oth.mParameters);
        mRecreateParams = false;
    }

    public void destroy() {

    }

    public void setProgramName(String name, boolean resetParams) {
        if (mProgram != null) {
            mRecreateParams = true;
        }
        mProgram = ENG_GpuProgramManager.getSingleton().getByName(name);

        if (mProgram == null) {
            throw new IllegalArgumentException("Could not find program " + name);
        }
        // Reset parameters
        if (resetParams || mParameters == null || mRecreateParams) {
            recreateParameters();
        }
    }

    public void setParameters(ENG_GpuProgramParameters params) {
        mParameters = params;
    }

    public ENG_GpuProgramParameters getParameters() {
        if (mParameters == null) {
            throw new NullPointerException("You must specify a program before " +
                    "you can retrieve parameters.");
        }
        return mParameters;
    }

    public void setProgram(ENG_GpuProgram prog) {
        mProgram = prog;
    }

    public GpuProgramType getType() {
        return mType;
    }

    public ENG_GpuProgram getProgram() {
        return mProgram;
    }

    public String getProgramName() {
        return mProgram.getName();
    }

    public void _load() {
        
        if (!mProgram.isLoaded()) {
            mProgram.load();
        }
    }
}
