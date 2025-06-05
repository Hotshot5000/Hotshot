/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;

public abstract class ENG_RenderTexture extends ENG_RenderTarget {

    protected final ENG_HardwarePixelBuffer mBuffer;
    protected final int mZOffset;

    public ENG_RenderTexture(ENG_HardwarePixelBuffer buffer, int zoffset) {
        mBuffer = buffer;
        mZOffset = zoffset;
        mPriority = OGRE_REND_TO_TEX_RT_GROUP;
        mWidth = buffer.getWidth();
        mHeight = buffer.getHeight();
        mColourDepth = ENG_PixelUtil.getNumElemBits(buffer.getFormat());
    }

    public void copyContentsToMemory(ENG_PixelBox dst, FrameBuffer buffer) {
        if (buffer == FrameBuffer.FB_AUTO) {
            buffer = FrameBuffer.FB_FRONT;
        }
        if (buffer != FrameBuffer.FB_FRONT) {
            throw new IllegalArgumentException("Invalid buffer.");
        }
        mBuffer.blitToMemory(dst);
    }

    public PixelFormat suggestPixelFormat() {
        return mBuffer.getFormat();
    }

}
