/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/5/18, 5:50 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 24.06.2017.
 */

public class ENG_BillboardSetNative extends ENG_AttachableObject {
    /** @noinspection deprecation*/
    private ENG_BillboardSet.BillboardOrigin mOriginType;
    /** @noinspection deprecation*/
    private ENG_BillboardSet.BillboardRotationType mRotationType;
    /** @noinspection deprecation*/
    private ENG_BillboardSet.BillboardType mBillboardType;

//    private long[] ptr = new long[1];
//    private long id;
//    private String name;
//    private boolean nativePtrSet;
//    private boolean attached;

    public ENG_BillboardSetNative(long id, String name) {
        this(id, name, 20);
    }

    public ENG_BillboardSetNative(long id, String name, int poolSize) {
        this.id = id;
        this.name = name;
        ENG_NativeCalls.sceneManager_createBillboardSet(this, poolSize, name);
    }

    public ENG_BillboardNative createBillboard(float x, float y, float z, ENG_ColorValue color) {
        return createBillboard(new ENG_Vector3D(x, y, z), color);
    }

    public ENG_BillboardNative createBillboard(ENG_Vector3D pos, ENG_ColorValue color) {
        ENG_BillboardNative billboardNative = new ENG_BillboardNative();
        ENG_NativeCalls.billboardSet_createBillboard(this, billboardNative, pos, color);
        // TODO pBillboardSet->setRenderQueueSubGroup(1); on the blackholedarksunmain.cpp does the trick. No idea why though...
//        ENG_NativeCalls.movableObject_setRenderQueueGroup(this, ENG_SceneManager.V_1_FAST_RENDER_QUEUE);
        return billboardNative;
    }

    public void destroyBillboard(ENG_BillboardNative billboardNative) {
        ENG_NativeCalls.billboardSet_destroyBillboard(this, billboardNative);
    }

    // Cannot use this since we don't send the pointer of the billboard so we don't know to removePointerFromMap() on the native side.
//    public void destroyBillboard(int pos) {
//        ENG_NativeCalls.billboardSet_destroyBillboard(this, pos);
//    }

    public void destroy() {
        ENG_NativeCalls.sceneManager_destroyBillboardSet(this);
        destroyed = true;
    }

    public void setCommonUpVector(ENG_Vector4D upDirection) {
        ENG_NativeCalls.billboardSet_setCommonUpVector(this, upDirection);
    }

    public void setCommonDirection(ENG_Vector4D commonDir) {
        ENG_NativeCalls.billboardSet_setCommonDirection(this, commonDir);
    }

    public void setDefaultDimensions(float xDim, float yDim) {
        ENG_NativeCalls.billboardSet_setDefaultDimensions(this, xDim, yDim);
    }

    /**
     * WARNING! THIS SETS THE MATERIAL FOR OLD MATERIAL SYSTEM. USE setDatablockName if you want Hlms.
     * @param materialName
     * @param materialGroup
     */
    public void setMaterialName(String materialName, String materialGroup) {
        ENG_NativeCalls.billboardSet_setMaterialName(this, materialName, materialGroup);
    }

    /**
     * This is only for Hlms materials. Do not mix it up with the old material system.
     * @param materialName
     */
    public void setDatablockName(String materialName) {
        ENG_NativeCalls.billboardSet_setDatablockName(this, materialName);
    }

    /** @noinspection deprecation*/
    public void setBillboardOrigin(ENG_BillboardSet.BillboardOrigin o) {
        mOriginType = o;
        ENG_NativeCalls.billboardSet_setBillboardOrigin(this, o);
    }

    /** @noinspection deprecation*/
    public ENG_BillboardSet.BillboardOrigin getBillboardOrigin() {
        return mOriginType;
    }

    /** @noinspection deprecation*/
    public void setBillboardRotationType(ENG_BillboardSet.BillboardRotationType t) {
        mRotationType = t;
        ENG_NativeCalls.billboardSet_setBillboardRotationType(this, t);
    }

    /** @noinspection deprecation*/
    public ENG_BillboardSet.BillboardRotationType getBillboardRotationType() {
        return mRotationType;
    }

    /** @noinspection deprecation*/
    public ENG_BillboardSet.BillboardType getBillboardType() {
        return mBillboardType;
    }

    /** @noinspection deprecation*/
    public void setBillboardType(ENG_BillboardSet.BillboardType type) {
        mBillboardType = type;
        ENG_NativeCalls.billboardSet_setBillboardType(this, type);
    }
}
