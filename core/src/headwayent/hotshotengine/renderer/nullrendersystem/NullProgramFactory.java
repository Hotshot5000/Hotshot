/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/6/16, 9:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nullrendersystem;

import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgram;
import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgramFactory;

/**
 * Created by sebas on 17.11.2015.
 */
public class NullProgramFactory extends ENG_HighLevelGpuProgramFactory {

    public static final String LANGUAGE_NAME = "null";

    @Override
    public String getLanguage() {
        return LANGUAGE_NAME;
    }

    @Override
    public ENG_HighLevelGpuProgram create(String name) {
        return new NullProgram(name);
    }

    @Override
    public void destroy(ENG_HighLevelGpuProgram prog, boolean skipGLDelete) {
        prog.destroy(skipGLDelete);
    }
}
