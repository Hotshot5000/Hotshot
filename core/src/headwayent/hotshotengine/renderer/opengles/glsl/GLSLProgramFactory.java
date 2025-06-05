/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.glsl;

import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgram;
import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgramFactory;

public class GLSLProgramFactory extends ENG_HighLevelGpuProgramFactory {

    private final GLSLLinkProgramManager mLinkProgramManager = new GLSLLinkProgramManager();

    public static final String LANGUAGE_NAME = "glsl";

    public GLSLProgramFactory() {

    }

    @Override
    public ENG_HighLevelGpuProgram create(String name) {

        return new GLSLProgram(name);
    }

    @Override
    public void destroy(ENG_HighLevelGpuProgram prog, boolean skipGLDelete) {


        prog.destroy(skipGLDelete);
    }

    @Override
    public String getLanguage() {

        return LANGUAGE_NAME;
    }

    public GLSLLinkProgramManager getLinkProgramManager() {
        return mLinkProgramManager;
    }
}
