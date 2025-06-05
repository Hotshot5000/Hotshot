/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Box;
import headwayent.hotshotengine.exception.ENG_BufferLockException;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;

import java.nio.Buffer;


public abstract class ENG_HardwarePixelBuffer extends ENG_HardwareBuffer {

    protected final int width;
    protected final int height;
    protected final int depth;
    protected int rowPitch;
    protected int slicePitch;
    public final PixelFormat format;
    public ENG_PixelBox currentLock = new ENG_PixelBox();
    public final ENG_Box lockedBox = new ENG_Box();
    protected LockOptions mCurrentLockOptions;

    public ENG_HardwarePixelBuffer(int width, int height, int depth,
                                   PixelFormat pixelFormat,
                                   int usage, boolean systemMemory, boolean useShadowBuffer) {
        super(usage, systemMemory, useShadowBuffer);
        
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.format = pixelFormat;
        rowPitch = width;
        slicePitch = width * height;
        sizeInBytes = slicePitch * ENG_PixelUtil.getNumElemBytes(pixelFormat);

        if (useShadowBuffer) {
            shadowBuffer = new ENG_DefaultHardwarePixelBuffer(
                    width, height, depth, pixelFormat, Usage.HBU_DYNAMIC.getUsage());
        }
    }

    @Override
    public void _updateFromShadow() {

        if ((useShadowBuffer) && (shadowUpdated) && (!supressHardwareUpdate)) {
            ENG_PixelBox lock = ((ENG_HardwarePixelBuffer) shadowBuffer).lock(
                    lockedBox, LockOptions.HBL_READ_ONLY);
            LockOptions opts;
            if (lockedBox.getWidth() == lock.getWidth() &&
                    lockedBox.getHeight() == lock.getHeight()) {
                opts = LockOptions.HBL_DISCARD;
            } else {
                opts = LockOptions.HBL_NORMAL;
            }
            ENG_PixelBox dest = lockImpl(lockedBox, opts);
            dest.data.position(dest.bufferPos);
            lock.data.position(lock.bufferPos);
            dest.data.put(lock.data);
            dest.data.rewind();
            lock.data.rewind();
            unlockImpl();
            shadowBuffer.unlock();
            shadowUpdated = false;
        }
    }

    public abstract ENG_PixelBox lockImpl(ENG_Box lockBox, LockOptions lockOptions);

    public abstract void lockImpl(ENG_Box lockBox, LockOptions lockOptions,
                                  ENG_PixelBox pixelbox);

    public void _clearSliceRTT(int zoffset) {

    }

    public Buffer lock(int offset, int length, LockOptions options) {
        if (isLocked) {
            throw new ENG_BufferLockException("Buffer already locked");
        }
        if ((offset != 0) || (length != sizeInBytes)) {
            throw new IllegalArgumentException(
                    "Cannot lock memory region, most lock box or entire buffer");
        }
        /*Image::Box myBox(0, 0, 0, mWidth, mHeight, mDepth);
        const PixelBox &rv = lock(myBox, options);
        return rv.data;*/
        ENG_Box myBox = new ENG_Box(0, 0, 0, width, height, depth);
        ENG_PixelBox rv = lock(myBox, options);
        lockedBox.set(myBox);
        return rv.data;
    }

    public ENG_PixelBox lock(ENG_Box box, LockOptions options) {
		/*if (mUseShadowBuffer)
        {
            if (options != HBL_READ_ONLY)
            {
                // we have to assume a read / write lock so we use the shadow buffer
                // and tag for sync on unlock()
                mShadowUpdated = true;
            }

            mCurrentLock = static_cast<HardwarePixelBuffer*>(mpShadowBuffer)->lock(lockBox, options);
        }
        else
        {
            // Lock the real buffer if there is no shadow buffer 
            mCurrentLock = lockImpl(lockBox, options);
            mIsLocked = true;
        }

        return mCurrentLock;*/

        if (useShadowBuffer) {
            if (options != LockOptions.HBL_READ_ONLY) {
                shadowUpdated = true;
            }
            currentLock = ((ENG_HardwarePixelBuffer) shadowBuffer).lock(box, options);
        } else {
            currentLock = lockImpl(box, options);
            isLocked = true;
        }
        lockedBox.set(box);
        return currentLock;
    }

    public ENG_PixelBox getCurrentLock() {
        if (!isLocked()) {
            throw new ENG_BufferLockException("Buffer not locked");
        }
        return currentLock;
    }

    public Buffer lockImpl(int offset, int length, LockOptions options) {
        throw new UnsupportedOperationException(
                "lockImpl(offset,length) is not valid for PixelBuffers and should never be called");


    }

    public void blit(ENG_HardwarePixelBuffer src, ENG_Box srcBox, ENG_Box dstBox) {
        if (isLocked || src.isLocked()) {
            throw new IllegalArgumentException(
                    "Source and destination buffer may not be locked!");
        }
        if (this == src) {
            throw new IllegalArgumentException("Source must not be the same object");
        }
		/*const PixelBox &srclock = src->lock(srcBox, HBL_READ_ONLY);

		LockOptions method = HBL_NORMAL;
		if(dstBox.left == 0 && dstBox.top == 0 && dstBox.front == 0 &&
		   dstBox.right == mWidth && dstBox.bottom == mHeight &&
		   dstBox.back == mDepth)
			// Entire buffer -- we can discard the previous contents
			method = HBL_DISCARD;*/
        ENG_PixelBox srclock = src.lock(srcBox, LockOptions.HBL_READ_ONLY);
        LockOptions method = LockOptions.HBL_NORMAL;
        if ((dstBox.left == 0) && (dstBox.top == 0) && (dstBox.front == 0) &&
                (dstBox.right == width) && (dstBox.bottom == height) &&
                (dstBox.back == depth)) {
            method = LockOptions.HBL_DISCARD;
        }
		/*const PixelBox &dstlock = lock(dstBox, method);
		if(dstlock.getWidth() != srclock.getWidth() ||
        	dstlock.getHeight() != srclock.getHeight() ||
        	dstlock.getDepth() != srclock.getDepth())
		{
			// Scaling desired
			Image::scale(srclock, dstlock);
		}
		else
		{
			// No scaling needed
			PixelUtil::bulkPixelConversion(srclock, dstlock);
		}

		unlock();
		src->unlock();*/

        ENG_PixelBox dstlock = lock(dstBox, method);
        if ((dstlock.getWidth() != srclock.getWidth()) ||
                (dstlock.getHeight() != srclock.getHeight()) ||
                (dstlock.getDepth() != srclock.getDepth())) {

        } else {
            ENG_PixelUtil.bulkPixelConversion(srclock, dstlock);
        }
        unlock();
        src.unlock();
    }

    public void blit(ENG_HardwarePixelBuffer src) {
        blit(src,
                new ENG_Box(0, 0, 0, src.getWidth(), src.getHeight(), src.getDepth()),
                new ENG_Box(0, 0, 0, width, height, depth));
    }

    public abstract void blitFromMemory(ENG_PixelBox src, ENG_Box dstBox);

    public void blitFromMemory(ENG_PixelBox src) {
        blitFromMemory(src, new ENG_Box(0, 0, 0, width, height, depth));
    }

    public abstract void blitToMemory(ENG_Box srcBox, ENG_PixelBox dst);

    public void blitToMemory(ENG_PixelBox dst) {
        blitToMemory(new ENG_Box(0, 0, 0, width, height, depth), dst);
    }

    public void readData(int offset, int length, Buffer dest) {
        throw new UnsupportedOperationException(
                "Reading a byte range is not implemented. Use blitToMemory.");
    }

    public void writeData(int offset, int length, Buffer src,
                          boolean discardWholeBuffer) {
        throw new UnsupportedOperationException(
                "Writing a byte range is not implemented. Use blitFromMemory.");
    }

    public ENG_RenderTexture getRenderTarget() {
        return getRenderTarget(0);
    }

    public ENG_RenderTexture getRenderTarget(int slice) {
        throw new UnsupportedOperationException(
                "Not yet implemented for this rendersystem.");
    }


    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return the format
     */
    public PixelFormat getFormat() {
        return format;
    }

}
