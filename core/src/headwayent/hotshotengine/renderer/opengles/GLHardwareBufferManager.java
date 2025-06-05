/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.nio.ByteBuffer;

public class GLHardwareBufferManager extends
        headwayent.hotshotengine.renderer.ENG_HardwareBufferManager {

    public GLHardwareBufferManager() {
        super(new GLHardwareBufferManagerBase());
        
    }

    public static int getGLUsage(int usage) {
        return GLHardwareBufferManagerBase.getGLUsage(usage);
    }

    public static int getGLType(VertexElementType type) {
        return GLHardwareBufferManagerBase.getGLType(type);
    }

    public ByteBuffer allocateScratch(int size) {
        return ((GLHardwareBufferManagerBase) impl).allocateScratch(size);
    }

    public void deallocateScratch(ByteBuffer ptr) {
        ((GLHardwareBufferManagerBase) impl).deallocateScratch(ptr);
    }

    public int getGLMapBufferThreshold() {
        return ((GLHardwareBufferManagerBase) impl).getGLMapBufferThreshold();
    }

    public void setGLMapBufferThreshold(int value) {
        ((GLHardwareBufferManagerBase) impl).setGLMapBufferThreshold(value);
    }

}
