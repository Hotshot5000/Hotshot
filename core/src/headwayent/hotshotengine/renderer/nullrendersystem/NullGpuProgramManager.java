/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/15, 11:04 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nullrendersystem;

import headwayent.hotshotengine.renderer.ENG_GpuProgram;
import headwayent.hotshotengine.renderer.ENG_GpuProgramManager;

/**
 * Created by sebas on 18.11.2015.
 */
public class NullGpuProgramManager extends ENG_GpuProgramManager {
    @Override
    protected ENG_GpuProgram createImpl() {
        return null;
    }
}
