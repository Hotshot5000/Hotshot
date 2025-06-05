/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/6/16, 9:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public abstract class ENG_HighLevelGpuProgramFactory {

    public ENG_HighLevelGpuProgramFactory() {

    }

    public abstract String getLanguage();

    public abstract ENG_HighLevelGpuProgram create(String name);

    public abstract void destroy(ENG_HighLevelGpuProgram prog, boolean skipGLDelete);
}
