/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.exception.ENG_GLException;
import headwayent.hotshotengine.renderer.ENG_HardwareBufferManager;
import headwayent.hotshotengine.renderer.ENG_HardwareBufferManagerBase;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.GL20;

public class GLHardwareIndexBuffer extends ENG_HardwareIndexBuffer {

    private final int mBufferId;
    // Scratch buffer handling
    private boolean mLockedToScratch;
    private int mScratchOffset;
    private int mScratchSize;
    private Buffer mScratchPtr;
    private boolean mScratchUploadOnUnlock;

    /** @noinspection deprecation */
    public GLHardwareIndexBuffer(ENG_HardwareBufferManagerBase mgr,
                                 IndexType indexType, int numIndexes, int usage,
                                 boolean useShadowBuffer) {
        super(mgr, indexType, numIndexes, usage, false, useShadowBuffer);


        //	int[] buf = new int[1];
        IntBuffer buf = ENG_Utility.allocateDirect(4).asIntBuffer();
        MTGLES20.glGenBuffersImmediate(1, buf);
        mBufferId = buf.get();
        if (mBufferId == 0) {
            throw new ENG_GLException("Cannot create GL index buffer");
        }
//        System.out.println("Creating index buffer id: " + mBufferId);
        MTGLES20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, mBufferId);
        // Shit LWJGL waste of good memory
        MTGLES20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, this.getSizeInBytes(),
                ENG_Utility.allocateDirect(getSizeInBytes()),
                GLHardwareBufferManager.getGLUsage(usage));
    }

    @Override
    protected Buffer lockImpl(int offset, int length, LockOptions options) {

        if (this.isLocked) {
            throw new ENG_GLException(
                    "Invalid attempt to lock an index buffer that has already been locked");
        }

        Buffer retPtr = null;

        GLHardwareBufferManager glBufManager =
                (GLHardwareBufferManager) ENG_HardwareBufferManager.getSingleton();

        if (length < glBufManager.getGLMapBufferThreshold()) {
            retPtr = glBufManager.allocateScratch(length);

            if (retPtr != null) {
                mLockedToScratch = true;
                mScratchOffset = offset;
                mScratchSize = length;
                mScratchPtr = retPtr;
                mScratchUploadOnUnlock = (options != LockOptions.HBL_READ_ONLY);

                if (options != LockOptions.HBL_DISCARD) {
                    readData(offset, length, retPtr);
                }
            }
        }

        if (retPtr == null) {
            //We are dead. No way to read back from buffer in GLES20
            throw new UnsupportedOperationException("glMapBuffer not supported yet");
        }
        isLocked = true;
        return retPtr;
    }

    @Override
    public void readData(int offset, int length, Buffer dest) {


        if (useShadowBuffer) {
            Buffer srcData = shadowBuffer.lock(offset, length, LockOptions.HBL_READ_ONLY);
            ENG_Utility.memcpy(dest, srcData, length);
            shadowBuffer.unlock();
        } else {
            throw new UnsupportedOperationException("glGetBufferSubData not supported yet");
        }
    }

    @Override
    protected void unlockImpl() {


        if (mLockedToScratch) {
            if (mScratchUploadOnUnlock) {
                // have to write the data back to vertex buffer
                writeData(mScratchOffset, mScratchSize, mScratchPtr,
                        mScratchOffset == 0 && mScratchSize == getSizeInBytes());

                ((GLHardwareBufferManager) ENG_HardwareBufferManager.getSingleton()).
                        deallocateScratch((ByteBuffer) mScratchPtr);

                mLockedToScratch = false;
            }
        } else {
            throw new UnsupportedOperationException("glUnmapBuffer not supported yet");
        }

        isLocked = false;
    }

    /** @noinspection deprecation */
    @Override
    public void writeData(int offset, int length, Buffer source,
                          boolean discardWholeBuffer) {



        MTGLES20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, mBufferId);

        if (useShadowBuffer) {
            Buffer destData = shadowBuffer.lock(offset, length,
                    discardWholeBuffer ? LockOptions.HBL_DISCARD : LockOptions.HBL_NORMAL);
            ENG_Utility.memcpy(destData, source, length);
            shadowBuffer.unlock();
        }

        if (offset == 0 && length == this.getSizeInBytes()) {
            MTGLES20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER,
                    this.getSizeInBytes(), source,
                    GLHardwareBufferManager.getGLUsage(usage), true);
        } else {
            if (discardWholeBuffer) {
                // Shit LWJGL
                MTGLES20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER,
                        this.getSizeInBytes(),
                        ENG_Utility.allocateDirect(getSizeInBytes()),
                        GLHardwareBufferManager.getGLUsage(usage), true);
            }

            MTGLES20.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, offset, length,
                    source, true);
        }
    }

    /** @noinspection deprecation */
    public void _updateFromShadow() {
        if (useShadowBuffer && shadowUpdated && !supressHardwareUpdate) {
            Buffer srcData = shadowBuffer.lock(lockStart, lockSize, LockOptions.HBL_READ_ONLY);

//            System.out.println("Updating index buffer with id: " + mBufferId);

            MTGLES20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, mBufferId);

            // Update whole buffer if possible, otherwise normal
            if (lockStart == 0 && lockSize == this.getSizeInBytes()) {
                MTGLES20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER,
                        this.getSizeInBytes(),
                        srcData, GLHardwareBufferManager.getGLUsage(usage), true);
            } else {
                MTGLES20.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER,
                        lockStart, lockSize, srcData, true);
            }

            shadowBuffer.unlock();
            shadowUpdated = false;
        }
    }

    public int getGLBufferId() {
        return mBufferId;
    }

    /** @noinspection deprecation*/
    @Override
    public void destroy(boolean skipGLDelete) {

        if (!skipGLDelete) {
            MTGLES20.glDeleteBuffers(1, new int[]{mBufferId}, 0);
        }
        super.destroy(skipGLDelete);
    }

}
