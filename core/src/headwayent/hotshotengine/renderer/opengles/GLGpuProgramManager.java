/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_GpuProgram;
import headwayent.hotshotengine.renderer.ENG_GpuProgramManager;

public class GLGpuProgramManager extends ENG_GpuProgramManager {

    public GLGpuProgramManager() {
        
    }

    @Override
    protected ENG_GpuProgram createImpl() {

        //We do not use gpu program manager since no low level gl program impl in
        // OpenGL ES 2.0
        return null;
    }

}
