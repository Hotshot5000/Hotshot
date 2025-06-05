/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.resource.ENG_Resource;
import headwayent.hotshotengine.scriptcompiler.ENG_CompilerUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class ENG_HighLevelGpuProgram extends ENG_GpuProgram {

    /// Whether the high-level program (and it's parameter defs) is loaded
    protected boolean mHighLevelLoaded;
    /// The underlying assembler program
    protected ENG_GpuProgram mAssemblerProgram;
    /// Have we built the name->index parameter map yet?
    protected boolean mConstantDefsBuilt;

    public ENG_HighLevelGpuProgram(String name) {
        super(name);
        
    }

    public abstract void destroy(boolean skipGLDelete);

    protected void loadHighLevel() {
        if (!mHighLevelLoaded) {
            loadHighLevelImpl();
            mHighLevelLoaded = true;
            if (mDefaultParams != null) {
                ENG_GpuProgramParameters savedParams = mDefaultParams;

                mDefaultParams = createParameters();

                mDefaultParams.copyMatchingNamedConstantsFrom(savedParams);
            }
        }
    }

    protected void unloadHighLevel(boolean skipGLDelete) {
        if (mHighLevelLoaded) {
            unloadHighLevelImpl(skipGLDelete);
            // Clear saved constant defs
            mConstantDefsBuilt = false;
            createParameterMappingStructures(true);

            mHighLevelLoaded = false;
        }
    }

    protected void loadHighLevelImpl() {
        if (mLoadFromFile) {
            //Load from the filename and path
            //String[] split = mFilename.split("/");
            String path = mPath;
            String fname = mFilename;

            DataInputStream stream = null;
            try {
                stream = ENG_Resource.getFileAsStream(fname, path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder str = new StringBuilder();
                String tmp;
                boolean error = false;
                try {
                    while ((tmp = reader.readLine()) != null) {
                        str.append(tmp);
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                    error = true;
                }
                if (!error) {
                    mSource = str.toString();
                }
            } finally {
                ENG_CompilerUtil.close(stream);
            }
        }
        loadFromSource();
    }

    protected void loadImpl() {
        if (isSupported()) {
            // load self
            loadHighLevel();

            // create low-level implementation
            createLowLevelImpl();


        }
    }

    protected void unloadImpl(boolean skipGLDelete) {
        unloadHighLevel(skipGLDelete);
        resetCompileError();
    }

    protected abstract void createLowLevelImpl();

    protected abstract void unloadHighLevelImpl(boolean skipGLDelete);

    protected void populateParameterNames(ENG_GpuProgramParameters params) {
        getConstantDefinitions();
        params._setNamedConstants(mConstantDefs);
    }

    //protected abstract void buildConstantDefinitions();

    public ENG_GpuProgramParameters createParameters() {
        ENG_GpuProgramParameters params = ENG_GpuProgramManager.getSingleton().createParameters();

        if (isSupported()) {
            loadImpl();
            if (isSupported()) {
                populateParameterNames(params);
            }
        }

        if (mDefaultParams != null) {
            params.copyConstantsFrom(mDefaultParams);
        }
        return params;
    }

    public ENG_GpuProgram _getBindingDelegate() {
        return mAssemblerProgram;
    }

    public ENG_GpuNamedConstants getConstantDefinitions() {
        if (!mConstantDefsBuilt) {
            buildConstantDefinitions();
            mConstantDefsBuilt = true;
        }
        return mConstantDefs;
    }

    public ENG_GpuNamedConstants getNamedConstants() {
        return getConstantDefinitions();
    }

    protected abstract void buildConstantDefinitions();


    @Override
    protected void loadFromSource() {


    }

}
