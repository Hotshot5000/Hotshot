/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Box;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_HardwarePixelBuffer;
import headwayent.hotshotengine.renderer.ENG_Image;
import headwayent.hotshotengine.renderer.ENG_Image.Filter;
import headwayent.hotshotengine.renderer.ENG_PixelBox;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;

import com.badlogic.gdx.graphics.GL20;

public class GLHardwarePixelBuffer extends ENG_HardwarePixelBuffer {

    protected ENG_PixelBox mBuffer;
    protected int mGLInternalFormat = GL20.GL_NONE;
//	protected LockOptions mCurrentLockOptions;

    public GLHardwarePixelBuffer(int width, int height, int depth,
                                 PixelFormat pixelFormat, int usage) {
        this(width, height, depth, pixelFormat, usage, false);
    }

    public GLHardwarePixelBuffer(int width, int height, int depth,
                                 PixelFormat pixelFormat, int usage, boolean useShadowBuffer) {
        super(width, height, depth, pixelFormat, usage, false, useShadowBuffer);

        mBuffer = new ENG_PixelBox(width, height, depth, pixelFormat);
    }

    public int getGLFormat() {
        return mGLInternalFormat;
    }

    public void allocateBuffer() {
        if (mBuffer.data != null) {
            return;
        }

        mBuffer.data = ENG_Utility.allocateDirect(getSizeInBytes());
    }

    public void freeBuffer() {
        if ((this.getUsage() & Usage.HBU_STATIC.getUsage()) != 0) {
            mBuffer.data = null;
        }
    }

    @Override
    public void blitFromMemory(ENG_PixelBox src, ENG_Box dstBox) {
        


        if (!mBuffer.contains(dstBox)) {
            throw new IllegalArgumentException("destination box out of range");
        }

        // Check for shadow buffer
        if (hasShadowBuffer()) {
            ENG_PixelBox lock = ((ENG_HardwarePixelBuffer) shadowBuffer).lock(
                    dstBox, LockOptions.HBL_READ_ONLY);
            //	lock.data.position((dstBox.getWidth() * dstBox.top + dstBox.left) *
            //			ENG_PixelUtil.getNumElemBytes(mBuffer.pixelFormat));
            lock.data.put(src.data);
            lock.data.rewind();
            src.data.rewind();
            shadowBuffer.unlock();
        }

        ENG_PixelBox scaled = new ENG_PixelBox();

        if (src.getWidth() != dstBox.getWidth() ||
                src.getHeight() != dstBox.getHeight() ||
                src.getDepth() != dstBox.getDepth()) {
            allocateBuffer();
            mBuffer.getSubVolume(dstBox, scaled);
            ENG_Image.scale(src, scaled, Filter.FILTER_BILINEAR);
        } else if (GLPixelUtil.getGLOriginFormat(src.pixelFormat) == 0) {
            allocateBuffer();
            mBuffer.getSubVolume(dstBox, scaled);
            ENG_PixelUtil.bulkPixelConversion(src, scaled);
        } else {
            allocateBuffer();
            scaled.set(src);
        }

        upload(scaled, dstBox);
        freeBuffer();
    }

    @Override
    public void blitToMemory(ENG_Box srcBox, ENG_PixelBox dst) {
        

        if (!mBuffer.contains(srcBox)) {
            throw new IllegalArgumentException("source box out of range");
        }

        if (hasShadowBuffer()) {
            ENG_PixelBox lock = ((ENG_HardwarePixelBuffer) shadowBuffer).lock(
                    srcBox, LockOptions.HBL_READ_ONLY);
            //	lock.data.position((dstBox.getWidth() * dstBox.top + dstBox.left) *
            //			ENG_PixelUtil.getNumElemBytes(mBuffer.pixelFormat));
            dst.data.put(lock.data);
            lock.data.rewind();
            dst.data.rewind();
            shadowBuffer.unlock();
            return;
        }

        if (srcBox.left == 0 && srcBox.right == getWidth() &&
                srcBox.top == 0 && srcBox.bottom == getHeight() &&
                srcBox.front == 0 && srcBox.back == getDepth() &&
                dst.getWidth() == getWidth() &&
                dst.getHeight() == getHeight() &&
                dst.getDepth() == getDepth() &&
                GLPixelUtil.getGLOriginFormat(dst.pixelFormat) != 0) {
            download(dst);
        } else {
            // Use buffer for intermediate copy
            allocateBuffer();
            // Download entire buffer
            download(mBuffer);
            if (srcBox.getWidth() != dst.getWidth() ||
                    srcBox.getHeight() != dst.getHeight() ||
                    srcBox.getDepth() != dst.getDepth()) {
                ENG_Image.scale(mBuffer.getSubVolume(srcBox), dst, Filter.FILTER_BILINEAR);
            } else {
                ENG_PixelUtil.bulkPixelConversion(mBuffer.getSubVolume(srcBox), dst);
            }
            freeBuffer();
        }
    }

    @Override
    public ENG_PixelBox lockImpl(ENG_Box lockBox, LockOptions lockOptions) {
        
        allocateBuffer();
        if (lockOptions != LockOptions.HBL_DISCARD) {
            download(mBuffer);
        }
        mCurrentLockOptions = lockOptions;
        //	this.lockedBox.set(lockBox);
        return mBuffer.getSubVolume(lockBox);
    }

    @Override
    public void lockImpl(ENG_Box lockBox, LockOptions lockOptions,
                         ENG_PixelBox pixelbox) {
        

        allocateBuffer();
        if (lockOptions != LockOptions.HBL_DISCARD) {
            download(mBuffer);
        }
        mCurrentLockOptions = lockOptions;
        //	this.lockedBox.set(lockBox);
        mBuffer.getSubVolume(lockBox, pixelbox);
    }

    @Override
    protected void unlockImpl() {
        

        if (mCurrentLockOptions != LockOptions.HBL_READ_ONLY) {
            upload(currentLock, lockedBox);
        }
        freeBuffer();
    }

    public void upload(ENG_PixelBox data, ENG_Box dest) {
        throw new UnsupportedOperationException(
                "Upload not possible for this pixelbuffer type");
    }

    public void download(ENG_PixelBox data) {
        throw new UnsupportedOperationException(
                "Download not possible for this pixelbuffer type");
    }

    public void bindToFramebuffer(int attachment, int zoffset) {
        throw new UnsupportedOperationException(
                "Framebuffer bind not possible for this pixelbuffer type");
    }

}
