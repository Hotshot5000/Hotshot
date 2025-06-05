/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.concurrent.atomic.AtomicBoolean;

import headwayent.hotshotengine.basictypes.ENG_Short;

public abstract class ENG_HardwareIndexBuffer extends ENG_HardwareBuffer {
//implements Comparable<ENG_HardwareIndexBuffer> {

    public enum IndexType {
        IT_16BIT,
        IT_32BIT
    }

    protected final ENG_HardwareBufferManagerBase mgr;
    protected final IndexType indexType;
    protected final int numIndexes;
    protected final int indexSize;
    protected final AtomicBoolean destroyed = new AtomicBoolean();

    public ENG_HardwareIndexBuffer(ENG_HardwareBufferManagerBase mgr,
                                   IndexType indexType, int numIndexes,
                                   int usage, boolean systemMemory,
                                   boolean useShadowBuffer) {
        super(usage, systemMemory, useShadowBuffer);

        this.mgr = mgr;
        this.numIndexes = numIndexes;
        this.indexType = indexType;

        switch (indexType) {
            default:
            case IT_16BIT:
                indexSize = ENG_Short.SIZE_IN_BYTES;
                break;
            case IT_32BIT:
                indexSize = ENG_Short.SIZE_IN_BYTES;
                break;

        }
        sizeInBytes = numIndexes * indexSize;

        if (useShadowBuffer) {
            shadowBuffer =
                    new ENG_DefaultHardwareIndexBuffer(mgr, indexType, numIndexes, usage);
        }
    }

    public void destroy(boolean skipGLDelete) {
        destroyed.set(true);
        if (mgr != null) {
            mgr._notifyIndexBufferDestroyed(this);
        }
    }

    public boolean isDestroyed() {
        return destroyed.get();
    }

    public int compareTo(ENG_HardwareIndexBuffer buf) {
        return Integer.compare(buf.getSizeInBytes(), sizeInBytes);
    }


    /**
     * @return the mgr
     */
    public ENG_HardwareBufferManagerBase getMgr() {
        return mgr;
    }

    /**
     * @return the indexType
     */
    public IndexType getIndexType() {
        return indexType;
    }

    /**
     * @return the numIndexes
     */
    public int getNumIndexes() {
        return numIndexes;
    }

    /**
     * @return the indexSize
     */
    public int getIndexSize() {
        return indexSize;
    }

}
