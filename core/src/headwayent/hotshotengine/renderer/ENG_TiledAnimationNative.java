/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 18-Oct-17.
 */

public class ENG_TiledAnimationNative implements ENG_NativePointerWithSetter, ENG_IdObject {

    public static final int FRAME_NUM_UNINITIALIZED = -1;
    private final long[] ptr = new long[1];
    private final long id;
    private final ENG_BillboardSetNative billboardSetNative;
    private final String name;
    private final String unlitMaterialName;
    private final float speed;
    private final int horizontalFramesNum;
    private final int verticalFramesNum;
    private int currentFrameNum = FRAME_NUM_UNINITIALIZED;
    private boolean nativePtrSet;

    public ENG_TiledAnimationNative(long id, ENG_BillboardSetNative billboardSetNative, String name, String unlitMaterialName,
                                    float speed, int horizontalFramesNum, int verticalFramesNum) {
        this.id = id;
        this.billboardSetNative = billboardSetNative;
        this.name = name;
        this.unlitMaterialName = unlitMaterialName;
        this.speed = speed;
        this.horizontalFramesNum = horizontalFramesNum;
        this.verticalFramesNum = verticalFramesNum;
        ENG_NativeCalls.sceneManager_createTiledAnimation(
                this, billboardSetNative, name, unlitMaterialName, speed, horizontalFramesNum, verticalFramesNum);
    }

    public void updateCurrentFrame() {
        ENG_NativeCalls.tiledAnimation_updateCurrentFrameNum(this);
    }

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

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ENG_BillboardSetNative getBillboardSetNative() {
        return billboardSetNative;
    }

    public String getUnlitMaterialName() {
        return unlitMaterialName;
    }

    public float getSpeed() {
        return speed;
    }

    public int getHorizontalFramesNum() {
        return horizontalFramesNum;
    }

    public int getVerticalFramesNum() {
        return verticalFramesNum;
    }

    /**
     * Always check if initialized! (FRAME_NUM_UNINITIALIZED)
     * @return
     */
    public int getCurrentFrameNum() {
        return currentFrameNum;
    }

    /**
     * Only call as response from native.
     * @param currentFrameNum
     */
    public void setCurrentFrameNum(int currentFrameNum) {
        this.currentFrameNum = currentFrameNum;
    }

    public void destroy() {
        ENG_NativeCalls.sceneManager_destroyTiledAnimation(this);
    }
}
