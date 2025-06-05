/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_HardwareBufferManagerBase;
import headwayent.hotshotengine.renderer.ENG_HardwareVertexBuffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class GLDefaultHardwareVertexBuffer extends ENG_HardwareVertexBuffer {

    protected final ByteBuffer mpData;

    public GLDefaultHardwareVertexBuffer(int vertexSize, int numVertices, int usage) {
        super(null, vertexSize, numVertices, usage, true, false);

        mpData = ENG_Utility.allocateDirect(getSizeInBytes());
    }

    public GLDefaultHardwareVertexBuffer(ENG_HardwareBufferManagerBase mgr,
                                         int vertexSize, int numVertices, int usage) {
        super(mgr, vertexSize, numVertices, usage, true, false);

        mpData = ENG_Utility.allocateDirect(getSizeInBytes());
    }

    public Buffer lock(int offset, int length, LockOptions options) {
        super.isLocked = true;
        return lockImpl(offset, length, options);
    }

    public void unlock() {
        super.isLocked = false;
    }

    @Override
    protected Buffer lockImpl(int offset, int length, LockOptions options) {
        
        mpData.position(offset);
        return mpData;
    }

    @Override
    public void readData(int offset, int length, Buffer dest) {
        
        if ((offset + length) > this.getSizeInBytes()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mpData.position(offset);
        ENG_Utility.memcpy(dest, mpData, length);
    }

    @Override
    protected void unlockImpl() {
        

    }

    @Override
    public void writeData(int offset, int length, Buffer source,
                          boolean discardWholeBuffer) {
        
        if ((offset + length) > this.getSizeInBytes()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        mpData.position(offset);
        ENG_Utility.memcpy(mpData, source, length);
    }

}
