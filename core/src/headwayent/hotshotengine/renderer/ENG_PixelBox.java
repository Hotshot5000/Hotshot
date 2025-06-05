/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Box;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;

import java.nio.ByteBuffer;

public class ENG_PixelBox extends ENG_Box {

    public ByteBuffer data;
    public ENG_PixelUtil.PixelFormat pixelFormat;
    public int rowPitch;
    public int slicePitch;
    public int bufferPos;

    public ENG_PixelBox() {

    }

    private void setup(PixelFormat pixelFormat, ByteBuffer data) {
        this.pixelFormat = pixelFormat;
        if (data != null) {
            this.data = data;
            bufferPos = data.position();
        }
        setConsecutive();
    }

    public ENG_PixelBox(ENG_Box extents, PixelFormat pixelFormat) {
        this(extents, pixelFormat, null);
    }

    public ENG_PixelBox(ENG_Box extents, PixelFormat pixelFormat, ByteBuffer data) {
        super(extents);
        setup(pixelFormat, data);
    }

    public ENG_PixelBox(int width, int height, int depth,
                        PixelFormat pixelFormat) {
        this(width, height, depth, pixelFormat, null);
    }

    public ENG_PixelBox(int width, int height, int depth,
                        PixelFormat pixelFormat, ByteBuffer data) {
        super(0, 0, 0, width, height, depth);
        setup(pixelFormat, data);
    }

    public ENG_PixelBox(ENG_PixelBox box) {
        set(box);
    }

    public void set(int width, int height, int depth,
                    int pixelFormat, ByteBuffer data) {
        set(width, height, depth, PixelFormat.getPixelFormat(pixelFormat), data);
    }

    public void set(int width, int height, int depth,
                    PixelFormat pixelFormat, ByteBuffer data) {
        super.set(0, 0, 0, width, height, depth);
        this.pixelFormat = pixelFormat;
        this.data = data;
        bufferPos = data.position();
        setConsecutive();
    }

    public void set(ENG_PixelBox box) {
        super.set(box);
        this.pixelFormat = box.pixelFormat;
        this.data = box.data;
        this.rowPitch = box.rowPitch;
        this.slicePitch = box.slicePitch;
        this.bufferPos = box.bufferPos;
    }

    public void setConsecutive() {
        rowPitch = getWidth();
        slicePitch = getWidth() * getHeight();
    }

    public int getRowSkip() {
        return rowPitch - getWidth();
    }

    public int getSliceSkip() {
        return slicePitch - (getHeight() * rowPitch);
    }

    public boolean isConsecutive() {
        return ((rowPitch == getWidth()) && (slicePitch == (getWidth() * getHeight())));
    }

    public int getConsecutiveSize() {
        return ENG_PixelUtil.getMemorySize(
                getWidth(), getHeight(), getDepth(), pixelFormat);
    }

    public ENG_PixelBox getSubVolume(ENG_Box def) {
        ENG_PixelBox ret = new ENG_PixelBox();
        getSubVolume(def, ret);
        return ret;
    }

    public void getSubVolume(ENG_Box def, ENG_PixelBox ret) {
        /*if(PixelUtil::isCompressed(format))
		{
			if(def.left == left && def.top == top && def.front == front &&
			   def.right == right && def.bottom == bottom && def.back == back)
			{
				// Entire buffer is being queried
				return *this;
			}
			OGRE_EXCEPT(Exception::ERR_INVALIDPARAMS, "Cannot return subvolume of compressed PixelBuffer", "PixelBox::getSubVolume");
		}
		if(!contains(def))
			OGRE_EXCEPT(Exception::ERR_INVALIDPARAMS, "Bounds out of range", "PixelBox::getSubVolume");*/
        if (ENG_PixelUtil.isCompressed(pixelFormat)) {
            if (this.equals(def)) {
                ret.set(this);
                return;
            }
            throw new IllegalArgumentException(
                    "Cannot return subvolume of compressed PixelBuffer");
        }
        if (!this.contains(def)) {
            throw new IllegalArgumentException("Bounds out of range");
        }
		/*const size_t elemSize = PixelUtil::getNumElemBytes(format);
		// Calculate new data origin
		// Notice how we do not propagate left/top/front from the incoming box, since
		// the returned pointer is already offset
		PixelBox rval(def.getWidth(), def.getHeight(), def.getDepth(), format, 
			((uint8*)data) + ((def.left-left)*elemSize)
			+ ((def.top-top)*rowPitch*elemSize)
			+ ((def.front-front)*slicePitch*elemSize)
		);

		rval.rowPitch = rowPitch;
		rval.slicePitch = slicePitch;
		rval.format = format;

		return rval;*/
        int elemSize = ENG_PixelUtil.getNumElemBytes(pixelFormat);
        ret.set(0, 0, 0, def.getWidth(), def.getHeight(), def.getDepth());
        ret.bufferPos += ((def.left - left) * elemSize) +
                ((def.top - top) * rowPitch * elemSize) +
                ((def.front - front) * slicePitch * elemSize);
        ret.data = data;
        ret.rowPitch = rowPitch;
        ret.slicePitch = slicePitch;
        ret.pixelFormat = pixelFormat;
    }
}
