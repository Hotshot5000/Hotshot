/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Box;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_Image.Filter;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.opengles.GLPixelUtil;

public class ENG_DefaultHardwarePixelBuffer extends ENG_HardwarePixelBuffer {

    private final ENG_PixelBox mBuffer;

    public ENG_DefaultHardwarePixelBuffer(int width, int height, int depth,
                                          PixelFormat pixelFormat, int usage) {
        super(width, height, depth, pixelFormat, usage, false,
                false);
        
        mBuffer = new ENG_PixelBox(width, height, depth, pixelFormat);
        mBuffer.data = ENG_Utility.allocateDirect(sizeInBytes);
    }


    @Override
    public ENG_PixelBox lockImpl(ENG_Box lockBox, LockOptions lockOptions) {

        mCurrentLockOptions = lockOptions;
        this.lockedBox.set(lockBox);
        return mBuffer.getSubVolume(lockBox);
    }

    @Override
    public void lockImpl(ENG_Box lockBox, LockOptions lockOptions,
                         ENG_PixelBox pixelbox) {


        mCurrentLockOptions = lockOptions;
        this.lockedBox.set(lockBox);
        mBuffer.getSubVolume(lockBox, pixelbox);
    }

    @Override
    public void blitFromMemory(ENG_PixelBox src, ENG_Box dstBox) {


        if (!mBuffer.contains(dstBox)) {
            throw new IllegalArgumentException("destination box out of range");
        }
        ENG_PixelBox scaled = new ENG_PixelBox();

        if (src.getWidth() != dstBox.getWidth() ||
                src.getHeight() != dstBox.getHeight() ||
                src.getDepth() != dstBox.getDepth()) {
            //	allocateBuffer();
            mBuffer.getSubVolume(dstBox, scaled);
            ENG_Image.scale(src, scaled, Filter.FILTER_BILINEAR);
        } else if (GLPixelUtil.getGLOriginFormat(src.pixelFormat) == 0) {
            //	allocateBuffer();
            mBuffer.getSubVolume(dstBox, scaled);
            ENG_PixelUtil.bulkPixelConversion(src, scaled);
        } else {
            //	allocateBuffer();
            scaled.set(src);
        }

        upload(scaled, dstBox);
    }

    private void upload(ENG_PixelBox scaled, ENG_Box dstBox) {

        //	mBuffer.data.position((mBuffer.getWidth() * dstBox.top + dstBox.left) *
        //			ENG_PixelUtil.getNumElemBytes(getFormat()));
        scaled.data.mark();
        mBuffer.data.put(scaled.data);
        scaled.data.reset();
        mBuffer.data.rewind();
    }

    @Override
    public void blitToMemory(ENG_Box srcBox, ENG_PixelBox dst) {


        if (!mBuffer.contains(srcBox)) {
            throw new IllegalArgumentException("source box out of range");
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
            //	allocateBuffer();
            // Download entire buffer
            download(mBuffer);
            if (srcBox.getWidth() != dst.getWidth() ||
                    srcBox.getHeight() != dst.getHeight() ||
                    srcBox.getDepth() != dst.getDepth()) {
                ENG_Image.scale(mBuffer.getSubVolume(srcBox), dst, Filter.FILTER_BILINEAR);
            } else {
                ENG_PixelUtil.bulkPixelConversion(mBuffer.getSubVolume(srcBox), dst);
            }
            //	freeBuffer();
        }
    }

    private void download(ENG_PixelBox data) {

        if (data.getWidth() != getWidth() ||
                data.getHeight() != getHeight() ||
                data.getDepth() != getDepth()) {
            throw new IllegalArgumentException("Can download whole buffer only for now");
        }
        data.data.mark();
        data.data.put(mBuffer.data);
        data.data.reset();
        mBuffer.data.rewind();
    }

    @Override
    protected void unlockImpl() {


    }

}
