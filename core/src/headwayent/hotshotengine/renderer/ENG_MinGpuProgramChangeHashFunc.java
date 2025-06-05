/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_MinGpuProgramChangeHashFunc extends ENG_HashFunc {

    public static final ENG_MinGpuProgramChangeHashFunc sMinGpuProgramChangeHashFunc =
            new ENG_MinGpuProgramChangeHashFunc();

    private ENG_MinGpuProgramChangeHashFunc() {

    }

    @Override
    public int hash(ENG_Pass p) {
        
//        p.mGpuProgramChangeMutex.lock();
        int hash = p.getIndex().getValue() << 28;
        if (p.hasVertexProgram()) {
            hash += (p.getVertexProgramName().hashCode() % (1 << 14)) << 14;
        }
        if (p.hasFragmentProgram()) {
            hash += (p.getFragmentProgramName().hashCode() % (1 << 14));
        }
//        p.mGpuProgramChangeMutex.unlock();
        return hash;
    }

}
