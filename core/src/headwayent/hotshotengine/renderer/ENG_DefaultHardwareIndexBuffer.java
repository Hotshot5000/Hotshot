/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Utility;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ENG_DefaultHardwareIndexBuffer extends ENG_HardwareIndexBuffer {

    protected final ByteBuffer data;

    public ENG_DefaultHardwareIndexBuffer(IndexType indexType, int numIndexes,
                                          int usage) {
        super(null, indexType, numIndexes, usage, true, false);
        data = ENG_Utility.allocateDirect(sizeInBytes);
    }

    public ENG_DefaultHardwareIndexBuffer(ENG_HardwareBufferManagerBase mgr,
                                          IndexType indexType, int numIndexes,
                                          int usage) {
        super(mgr, indexType, numIndexes, usage, true, false);
        data = ENG_Utility.allocateDirect(sizeInBytes);
    }

    @Override
    protected Buffer lockImpl(int offset, int length, LockOptions options) {

        data.limit(data.capacity());
        data.position(offset);
        return data;
    }

    @Override
    public void readData(int offset, int length, Buffer dest) {

        if ((offset + length) > sizeInBytes) {
            throw new IllegalArgumentException("(offset + length) > sizeInBytes");
        }
        if (dest instanceof ByteBuffer) {
            ByteBuffer d = (ByteBuffer) dest;
            data.limit(offset + length);
            data.position(offset);

            int limit = d.limit();
            d.limit(d.position() + length);
            d.mark();
            d.put(data);
            d.reset();
            d.limit(limit);
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    protected void unlockImpl() {


    }

    @Override
    public void writeData(int offset, int length, Buffer source,
                          boolean discardWholeBuffer) {

        if ((offset + length) > sizeInBytes) {
            throw new IllegalArgumentException("(offset + length) > sizeInBytes");
        }
        if (source instanceof ByteBuffer) {
            ByteBuffer s = (ByteBuffer) source;
            data.limit(offset + length);
            data.position(offset);
            int limit = s.limit();
            s.limit(s.position() + length);
            s.mark();
            data.put(s);
            s.reset();
            s.limit(limit);
        } else {
            throw new ClassCastException();
        }
    }

    public Buffer lock(int offset, int length, LockOptions options) {
        isLocked = true;
        data.limit(/*data.capacity()*/offset + length);
        data.position(offset);
        return data;
    }

    public void unlock() {
        isLocked = false;
    }

}
