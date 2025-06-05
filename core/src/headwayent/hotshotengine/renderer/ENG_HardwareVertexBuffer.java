/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;


import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ENG_HardwareVertexBuffer extends ENG_HardwareBuffer {
//implements Comparable<ENG_HardwareVertexBuffer> {

    public final ENG_HardwareBufferManagerBase mgr;
    public final int numVertices;
    public final int vertexSize;
    public final AtomicBoolean destroyed = new AtomicBoolean();

    public ENG_HardwareVertexBuffer(ENG_HardwareBufferManagerBase mgr,
                                    int vertexSize, int numVertices, int usage, boolean systemMemory,
                                    boolean useShadowBuffer) {
        super(usage, systemMemory, useShadowBuffer);
        
        this.mgr = mgr;
        this.numVertices = numVertices;
        this.vertexSize = vertexSize;
        sizeInBytes = numVertices * vertexSize;
        if (useShadowBuffer) {
            shadowBuffer = new ENG_DefaultHardwareVertexBuffer(mgr,
                    numVertices, vertexSize, Usage.HBU_DYNAMIC.getUsage());
        }
    }

    public void destroy(boolean skipGLDelete) {
        destroyed.set(true);
        if (mgr != null) {
            mgr._notifyVertexBufferDestroyed(this);
        }
    }

    public boolean isDestroyed() {
        return destroyed.get();
    }

    public int compareTo(ENG_HardwareVertexBuffer buf) {
        return Integer.compare(buf.getSizeInBytes(), sizeInBytes);
    }

/*	@Override
    protected Buffer lockImpl(int offset, int length, LockOptions options) {

		return null;
	}

	@Override
	public void readData(int offset, int length, Buffer dest) {


	}

	@Override
	protected void unlockImpl() {


	}

	@Override
	public void writeData(int offset, int length, Buffer source,
			boolean discardWholeBuffer) {


	}*/

    /**
     * @return the mgr
     */
    public ENG_HardwareBufferManagerBase getMgr() {
        return mgr;
    }

    /**
     * @return the numVertices
     */
    public int getNumVertices() {
        return numVertices;
    }

    /**
     * @return the vertexSize
     */
    public int getVertexSize() {
        return vertexSize;
    }

}
