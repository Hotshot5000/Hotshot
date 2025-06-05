/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class GLDefaultHardwareIndexBuffer extends ENG_HardwareIndexBuffer {

    protected final ByteBuffer mpData;

    public GLDefaultHardwareIndexBuffer(
            IndexType indexType, int numIndexes, int usage
    ) {
        super(null, indexType, numIndexes, usage, true, false);
        
        mpData = ENG_Utility.allocateDirect(getSizeInBytes());
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
