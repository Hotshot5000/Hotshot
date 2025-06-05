/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_HardwareBufferManagerBase;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer;
import headwayent.hotshotengine.renderer.ENG_HardwareVertexBuffer;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer.IndexType;

public class GLDefaultHardwareBufferManagerBase extends
        ENG_HardwareBufferManagerBase {

    public GLDefaultHardwareBufferManagerBase() {

    }

    @Override
    public ENG_HardwareIndexBuffer createIndexBuffer(IndexType type,
                                                     int numIndexes, int usage, boolean useShadowBuffer) {

        return new GLDefaultHardwareIndexBuffer(type, numIndexes, usage);
    }

    @Override
    public ENG_HardwareVertexBuffer createVertexBuffer(int vertexSize,
                                                       int numVertices, int usage, boolean useShadowBuffer) {

        return new GLDefaultHardwareVertexBuffer(this, vertexSize, numVertices, usage);
    }

}
