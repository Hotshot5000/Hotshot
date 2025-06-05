/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/6/16, 6:34 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nullrendersystem;

import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgram;

/**
 * Created by sebas on 17.11.2015.
 */
public class NullProgram extends ENG_HighLevelGpuProgram {
    public NullProgram(String name) {
        super(name);
    }

    @Override
    public void destroy(boolean skipGLDelete) {

    }

    @Override
    protected void createLowLevelImpl() {

    }

    @Override
    protected void unloadHighLevelImpl(boolean skipGLDelete) {

    }

    @Override
    protected void buildConstantDefinitions() {

    }
}
