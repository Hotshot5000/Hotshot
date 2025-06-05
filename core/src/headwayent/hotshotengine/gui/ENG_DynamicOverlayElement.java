/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gui;

import headwayent.blackholedarksun.MainActivity;
import headwayent.hotshotengine.ENG_Box;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Utility.Endianness;
import headwayent.hotshotengine.exception.ENG_UnsupportedPixelFormatException;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_HardwarePixelBuffer;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;
import headwayent.hotshotengine.renderer.ENG_PixelBox;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.util.ArrayList;
import java.util.LinkedList;

public class ENG_DynamicOverlayElement implements ENG_NativePointerWithSetter {

    public static class ChangedElement {
        public final ENG_Box rect = new ENG_Box();
        public final ArrayList<ENG_ColorValue> color = new ArrayList<>();

        public ChangedElement() {

        }

        public ChangedElement(ENG_Box rect, ENG_ColorValue col) {

            this.rect.set(rect);
            addSameColorForAllBoxArea(rect, col);
        }

        private void addSameColorForAllBoxArea(ENG_Box rect, ENG_ColorValue col) {
            int area = rect.getWidth() * rect.getHeight();
            for (int i = 0; i < area; ++i) {
                color.add(col);
            }
        }

        public ChangedElement(int x, int y, int xLen, int yLen, ENG_ColorValue col) {
            rect.left = x;
            rect.top = y;
            rect.right = x + xLen;
            rect.bottom = y + yLen;
            addSameColorForAllBoxArea(rect, col);
        }

        public ChangedElement(ENG_Box rect, ArrayList<ENG_ColorValue> col) {
            this.rect.set(rect);
            color.addAll(col);
        }

        public ChangedElement(int x, int y, int xLen, int yLen,
                              ArrayList<ENG_ColorValue> col) {
            rect.left = x;
            rect.top = y;
            rect.right = x + xLen;
            rect.bottom = y + yLen;
            color.addAll(col);
        }

        @Override
        public boolean equals(Object o) {

            if (o instanceof ChangedElement) {
                ChangedElement elem = (ChangedElement) o;
                return rect.equals(elem.rect) && color.equals(elem.color);
            } else {
                throw new IllegalArgumentException("Object to compare must be of " +
                        "ChangedElement type");
            }
        }
    }

    private final long[] ptr = new long[1];
    private boolean nativePtrSet;

    @Override
    public long getPointer() {
        return ptr[0];
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr[0] = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePtrSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePtrSet = set;
    }


    private final LinkedList<ChangedElement> changedElements =
            new LinkedList<>();
    private ENG_HardwarePixelBuffer currentBuffer;
    private ENG_PixelBox currentLock;
    private boolean isLocked;

    public ENG_DynamicOverlayElement(ENG_OverlayElement elem, String textureName, String groupName) {
        ENG_NativeCalls.dynamicOverlayELement_Ctor(this, elem, textureName, groupName);
    }

    public void destroy() {
        ENG_NativeCalls.dynamicOverlayELement_Dtor(this);
        nativePtrSet = false;
        ptr[0] = 0;
    }

    public void resetToInitialTexture() {
        //	setPoints(changedElements);
        ENG_NativeCalls.dynamicOverlayElement_resetTexture(this);
//        lock();
//        while (changedElements.peek() != null) {
//            ChangedElement element = changedElements.pop();
//            // Set area without adding to changedElements
//            if (MainActivity.isDebugmode()) {
//                //	System.out.println("resetting width " + element.rect.getWidth() +
//                //			" height " + element.rect.getHeight());
//            }
//            setAreaToColor(element, true);
//        }
//        unlock();
//        changedElements.clear();
    }

    public void updateFinalTexture() {
        ENG_NativeCalls.dynamicOverlayElement_updateFinalTexture(this);
    }

    public void setPointScreenSpace(
            float x, float y, int pixelLen, ENG_ColorValue val) {
        setPointScreenSpace(x, y, pixelLen, val, false);
    }

/*	public void setPointsScreenSpace(ArrayList<ENG_Vector2D> ptList, 
			ArrayList<ENG_ColorValue> colList) {
		setPointsScreenSpace(ptList, colList, false);
	}*/

    /**
     * Params must be in screen space between 0 and 1
     *
     * @param x
     * @param y
     * @param overwriteTransparentPixels
     */
    public boolean setPointScreenSpace(
            float x, float y, int pixelLen,
            ENG_ColorValue val, boolean overwriteTransparentPixels) {
        if (!ENG_GUIUtils.isScreenSpace(x, y)) {
            return false;
        }
        //	ENG_HardwarePixelBuffer buffer = elem.getMaterial().getTechnique((short) 0)
        //			.getPass((short) 0).getTextureUnitState(0)._getTexturePtr().getBuffer();

//        int xPos = (int) (x * currentLock.getWidth());
//        int yPos = (int) (y * currentLock.getHeight());

        ENG_NativeCalls.dynamicOverlayElement_setPointScreenSpace(this, x, y, pixelLen, val, overwriteTransparentPixels);

        return true;
//        setPoint(xPos, yPos, pixelLen, val, overwriteTransparentPixels);
    }
	

    public void setPoint(int x, int y, int pixelLen, ENG_ColorValue val) {
        setPoint(x, y, pixelLen, val, false);
    }

    public void lock() {
//        if (isLocked) {
//            throw new ENG_BufferLockException("Buffer is already locked!");
//        }
//        currentBuffer = elem.getMaterial().getTechnique((short) 0)
//                .getPass((short) 0).getTextureUnitState(0)._getTexturePtr().getBuffer();
//
//        Buffer lock = currentBuffer.lock(LockOptions.HBL_NORMAL);
//        currentLock = currentBuffer.getCurrentLock();
//        isLocked = true;

        ENG_NativeCalls.dynamicOverlayElement_lock(this);
    }

    public void unlock() {
//        if (!isLocked) {
//            throw new ENG_BufferLockException("Buffer is not locked!");
//        }
//        currentBuffer.unlock();
//        currentBuffer = null;
//        currentLock = null;
//        isLocked = false;

        ENG_NativeCalls.dynamicOverlayElement_unlock(this);
    }

    public void setArea(ChangedElement elem) {
        setArea(elem, false);
    }

    private void setAreaToColor(ChangedElement elem) {
        setAreaToColor(elem, false);
    }

    public void setArea(ChangedElement elem, boolean overwriteTransparentPixels) {
        setArea(elem.rect, elem.color, overwriteTransparentPixels);
    }

    private void setAreaToColor(ChangedElement elem, boolean overwriteTransparentPixels) {
        setAreaToColor(elem.rect, elem.color, overwriteTransparentPixels);
    }

    public void setArea(ENG_Box rect, ArrayList<ENG_ColorValue> val) {
        setArea(rect, val, false);
    }

    public void setArea(ENG_Box rect, ENG_ColorValue val) {
        setArea(rect, val, false);
    }

    public void setArea(ENG_Box rect, ENG_ColorValue val,
                        boolean overwriteTransparentPixels) {

        ENG_NativeCalls.dynamicOverlayElement_setArea(this, rect, val, overwriteTransparentPixels);
//        ArrayList<ENG_ColorValue> list = new ArrayList<>();
//        int len = rect.getWidth() * rect.getHeight();
//        for (int i = 0; i < len; ++i) {
//            list.add(val);
//        }
//        setArea(rect, list, overwriteTransparentPixels);
    }

    public void setArea(ENG_Box rect, ArrayList<ENG_ColorValue> val,
                        boolean overwriteTransparentPixels) {

        ENG_NativeCalls.dynamicOverlayElement_setAreaVec(this, rect, val, overwriteTransparentPixels);

//        ChangedElement oldElem = setAreaToColor(rect, val,
//                overwriteTransparentPixels);
//
//        changedElements.push(oldElem);
    }

    /**
     * Internal method separated from setArea() so that we can reset the texture
     * without readding to the changedElements, so that we avoid an infinite loop.
     *
     * @param rect
     * @param val
     * @param overwriteTransparentPixels
     * @return
     */
    private ChangedElement setAreaToColor(ENG_Box rect,
                                          ArrayList<ENG_ColorValue> val, boolean overwriteTransparentPixels) {
        ENG_NativeCalls.dynamicOverlayElement_setAreaVec(this, rect, val, overwriteTransparentPixels);

        // Check if enough color values
        if (rect.getWidth() * rect.getHeight() != val.size()) {
            throw new IllegalArgumentException("The rect area number of pixels must " +
                    "match the size of the color array");
        }
        // Make sure that because of size of pixel we do not go out of the
        // pixelbox bounds
        rect.intersectInPlace(currentLock);
        if (MainActivity.isDebugmode()) {
            if (rect.getWidth() != 2 || rect.getHeight() != 2) {
                //	System.out.println("rect has width " + rect.getWidth() + " height " +
                //			rect.getHeight());
            }
        }
        ENG_PixelBox volume = currentLock.getSubVolume(rect);
        //	volume.data.mark();
        //	volume.data.position(volume.bufferPos);
        int bytesPerPixel = ENG_PixelUtil.getNumElemBytes(currentLock.pixelFormat);
        //	int pos = (rect.left + rect.top * currentLock.rowPitch) * bytesPerPixel;
        //	ArrayList<ENG_ColorValue> oldColorList = new ArrayList<ENG_ColorValue>();
        ChangedElement oldElement = new ChangedElement();
        oldElement.rect.set(rect);
        int bufferPos = volume.bufferPos;
        int linePos = bufferPos;
        int colorPos = 0;
        for (int y = 0; y < volume.bottom; ++y) {
            for (int x = 0; x < volume.right; ++x) {
                int prevData = volume.data.getInt(linePos);
                ENG_ColorValue oldCol = new ENG_ColorValue();
                switch (currentLock.pixelFormat) {
                    case PF_A8R8G8B8:
                        if (ENG_Utility.getEndianness() == Endianness.LITTLE_ENDIAN) {
                            oldCol.setAsBGRA(prevData);
                        } else {
                            oldCol.setAsARGB(prevData);
                        }

                        break;
                    case PF_R8G8B8A8:
                        if (ENG_Utility.getEndianness() == Endianness.LITTLE_ENDIAN) {
                            oldCol.setAsABGR(prevData);
                        } else {
                            oldCol.setAsRGBA(prevData);
                        }

                        break;
                    default:
                        throw new ENG_UnsupportedPixelFormatException(
                                "Only A8R8G8B8 and R8G8B8A8");
                }
                // Add it anyway for corner cases when even though we don't
                // overwrite we still need the value for when we reset the texture
                oldElement.color.add(oldCol);
                if (!overwriteTransparentPixels && oldCol.a == 0.0f) {
                    // Don't forget to add the offset!!!!!!
                    linePos += 4;
                    continue;
                }

                ENG_ColorValue newCol = val.get(colorPos++);
                int newData;
                switch (currentLock.pixelFormat) {
                    case PF_A8R8G8B8:
                        if (ENG_Utility.getEndianness() == Endianness.LITTLE_ENDIAN) {
                            newData = newCol.getAsBGRA();
                        } else {
                            newData = newCol.getAsARGB();
                        }
                        break;
                    case PF_R8G8B8A8:
                        if (ENG_Utility.getEndianness() == Endianness.LITTLE_ENDIAN) {
                            newData = newCol.getAsABGR();
                        } else {
                            newData = newCol.getAsRGBA();
                        }
                        break;
                    default:
                        throw new ENG_UnsupportedPixelFormatException(
                                "Only A8R8G8B8 and R8G8B8A8");
                }

                volume.data.putInt(linePos, newData);
                linePos += 4;
            }
            //	volume.data.position(volume.data.position() +
            //			(currentLock.rowPitch) * bytesPerPixel);
            bufferPos += currentLock.rowPitch * bytesPerPixel;
            linePos = bufferPos;
        }
        //	volume.data.reset();
        return oldElement;
    }

    /**
     * Use for setting one pixel. If you want to set more than one than for improved
     * efficiency use the vector variant (no longer true but make sure you
     * lock and unlock)
     *
     * @param x
     * @param y
     * @param val
     * @param overwriteTransparentPixels
     */
    public void setPoint(int x, int y, int pixelLen, ENG_ColorValue val,
                         boolean overwriteTransparentPixels) {

        setArea(new ChangedElement(x, y, pixelLen, pixelLen, val));
    }
}
