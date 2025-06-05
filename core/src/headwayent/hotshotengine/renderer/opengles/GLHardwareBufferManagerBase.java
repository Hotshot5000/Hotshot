/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_HardwareBufferManagerBase;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer.IndexType;
import headwayent.hotshotengine.renderer.ENG_HardwareVertexBuffer;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.graphics.GL20;

@Deprecated
public class GLHardwareBufferManagerBase extends ENG_HardwareBufferManagerBase {

    private static final int OGRE_GL_DEFAULT_MAP_BUFFER_THRESHOLD = 32 * 1024;
    private static final int SCRATCH_POOL_SIZE = 1024 * 1024;

    private final ByteBuffer mScratchBufferPool;
    private final ReentrantLock mScratchMutex = new ReentrantLock();
    private int mMapBufferThreshold;

    public GLHardwareBufferManagerBase() {
        
        mScratchBufferPool = ENG_Utility.allocateDirect(SCRATCH_POOL_SIZE);
        mScratchBufferPool.putInt(1 << 31 + (SCRATCH_POOL_SIZE - 4));
        mScratchBufferPool.rewind();
    }

    @Override
    public ENG_HardwareIndexBuffer createIndexBuffer(IndexType type,
                                                     int numIndexes, int usage, boolean useShadowBuffer) {

        GLHardwareIndexBuffer buf = new GLHardwareIndexBuffer(this, type, numIndexes,
                usage, useShadowBuffer);
        indexBuffersMutex.lock();
        try {
            indexBuffers.add(buf);
        } finally {
            indexBuffersMutex.unlock();
        }
        return buf;
    }

    @Override
    public ENG_HardwareVertexBuffer createVertexBuffer(int vertexSize,
                                                       int numVertices, int usage, boolean useShadowBuffer) {

        GLHardwareVertexBuffer buf = new GLHardwareVertexBuffer(this, vertexSize, numVertices,
                usage, useShadowBuffer);
        vertexBuffersMutex.lock();
        try {
            vertexBuffers.add(buf);
        } finally {
            vertexBuffersMutex.unlock();
        }
        return buf;
    }

    public static int getGLUsage(int usage) {
        if (usage == Usage.HBU_STATIC.getUsage() ||
                usage == Usage.HBU_STATIC_WRITE_ONLY.getUsage()) {
            return GL20.GL_STATIC_DRAW;
        }
        if (usage == Usage.HBU_DYNAMIC.getUsage() ||
                usage == Usage.HBU_DYNAMIC_WRITE_ONLY.getUsage()) {
            return GL20.GL_DYNAMIC_DRAW;
        }
        if (usage == Usage.HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE.getUsage()) {
            return GL20.GL_STREAM_DRAW;
        }
        return GL20.GL_DYNAMIC_DRAW;

	/*	switch(usage)
        {
        case HBU_STATIC:
        case HBU_STATIC_WRITE_ONLY:
            return GLES20.GL_STATIC_DRAW;
        case HBU_DYNAMIC:
        case HBU_DYNAMIC_WRITE_ONLY:
            return GLES20.GL_DYNAMIC_DRAW;
        case HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE:
            return GLES20.GL_STREAM_DRAW;
        default:
            return GLES20.GL_DYNAMIC_DRAW;
        }*/
    }

    public static int getGLType(VertexElementType type) {
        switch (type) {
            case VET_FLOAT1:
            case VET_FLOAT2:
            case VET_FLOAT3:
            case VET_FLOAT4:
                return GL20.GL_FLOAT;
            case VET_SHORT1:
            case VET_SHORT2:
            case VET_SHORT3:
            case VET_SHORT4:
                return GL20.GL_SHORT;
            case VET_COLOUR:
            case VET_COLOUR_ABGR:
            case VET_COLOUR_ARGB:
            case VET_UBYTE4:
                return GL20.GL_UNSIGNED_BYTE;
            default:
                return 0;
        }
    }

    public ByteBuffer allocateScratch(int size) {
        mScratchMutex.lock();
        try {
            // Alignment - round up the size to 32 bits
            // control blocks are 32 bits too so this packs nicely
            if (size % 4 != 0) {
                size += 4 - (size % 4);
            }

            int bufferPos = 0;
            while (bufferPos < SCRATCH_POOL_SIZE) {
                //	mScratchBufferPool.rewind();
                //	mScratchBufferPool.position(bufferPos);
                int pNext = mScratchBufferPool.getInt(bufferPos);
                if (((pNext >>> 31) == 1) && ((pNext & 0x7FFFFFFF) >= size)) {
                    if (((pNext & 0x7FFFFFFF) > size + 4)) {
                        int offset = size + 4;

                        int pSplitAlloc = (1 << 31) + (pNext & 0x7FFFFFFF) - size - 4;


                        mScratchBufferPool.putInt(bufferPos, size);
                    }

                    mScratchBufferPool.putInt(
                            bufferPos, mScratchBufferPool.getInt(bufferPos) & 0x7FFFFFFF);

                    mScratchBufferPool.position(bufferPos + 4);
                    return mScratchBufferPool;
                }
                bufferPos += 4 + (pNext & 0x7FFFFFFF);
            }
        } finally {
            mScratchMutex.unlock();
        }
        return null;
    }

    public void deallocateScratch(ByteBuffer ptr) {
        mScratchMutex.lock();
        try {
            int bufferPos = 0;
            int pLast = 0;
            int pLastPos = -1;

            while (bufferPos < SCRATCH_POOL_SIZE) {

                int pCurrent = mScratchBufferPool.getInt(bufferPos);
                int pCurrentPos = bufferPos;
                if (bufferPos + 4 == ptr.position()) {
                    mScratchBufferPool.putInt(
                            bufferPos, mScratchBufferPool.getInt(bufferPos) + 1 << 31);

                    if ((pLastPos != -1) && ((pLast >>> 31) == 1)) {
                        bufferPos -= (pLast & 0x7FFFFFFF) + 4;
                    }

                    int offset = bufferPos + (pCurrent & 0x7FFFFFFF) + 4;
                    if (offset < SCRATCH_POOL_SIZE) {
                        int pNext = mScratchBufferPool.getInt(offset);
                        if ((pNext >>> 31) == 1) {
                            mScratchBufferPool.putInt(bufferPos,
                                    (pCurrent & 0x7FFFFFFF) + (pNext & 0x7FFFFFFF) + 4);
                        }
                    }

                    return;
                }

                bufferPos += (pCurrent & 0x7FFFFFFF) + 4;
                pLast = pCurrent;
                pLastPos = pCurrentPos;
            }
        } finally {
            mScratchMutex.unlock();
        }

        throw new ArrayIndexOutOfBoundsException("Memory deallocation error");
    }

    public int getGLMapBufferThreshold() {
        return mMapBufferThreshold;
    }

    public void setGLMapBufferThreshold(int value) {
        mMapBufferThreshold = value;
    }

}
