/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Vector4D;

@Deprecated
public class ENG_Billboard {

    protected boolean mOwnDimensions;
    protected boolean mUseTexcoordRect;
    protected short mTexcoordIndex;      // index into the BillboardSet array of texture coordinates
    protected final ENG_RealRect mTexcoordRect = new ENG_RealRect();    // individual texture coordinates
    protected float mWidth;
    protected float mHeight;

    // Note the intentional public access to main internal variables used at runtime
    // Forcing access via get/set would be too costly for 000's of billboards
    public final ENG_Vector4D mPosition = new ENG_Vector4D(true);
    // Normalised direction vector
    public final ENG_Vector4D mDirection = new ENG_Vector4D();
    /** @noinspection deprecation*/
    public ENG_BillboardSet mParentSet;
    public final ENG_ColorValue mColour = new ENG_ColorValue(ENG_ColorValue.WHITE);
    public float mRotation;

    public ENG_Billboard() {

    }

    /** @noinspection deprecation*/
    public ENG_Billboard(ENG_Vector4D pos,
                         ENG_BillboardSet owner, ENG_ColorValue col) {
        mPosition.set(pos);
        mParentSet = owner;
        mColour.set(col);
    }

    public float getRotation() {
        return mRotation;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
        if (rotation != 0.0f) {
            mParentSet._notifyBillboardRotated();
        }
    }

    public void setPosition(ENG_Vector4D pos) {
        mPosition.set(pos);
    }

    public void setPosition(float x, float y, float z) {
        mPosition.set(x, y, z);
    }

    public void getPosition(ENG_Vector4D ret) {
        ret.set(mPosition);
    }

    public ENG_Vector4D getPosition() {
        return new ENG_Vector4D(mPosition);
    }

    public void setDimensions(float width, float height) {
        mOwnDimensions = true;
        mWidth = width;
        mHeight = height;
        mParentSet._notifyBillboardResized();
    }

    public void resetDimensions() {
        mOwnDimensions = false;
    }

    public void setColour(ENG_ColorValue col) {
        mColour.set(col);
    }

    public ENG_ColorValue getColour() {
        return new ENG_ColorValue(mColour);
    }

    public void getColour(ENG_ColorValue ret) {
        ret.set(mColour);
    }

    public boolean hasOwnDimensions() {
        return mOwnDimensions;
    }

    public float getOwnWidth() {
        return mWidth;
    }

    public float getOwnHeight() {
        return mHeight;
    }

    /** @noinspection deprecation*/
    public void _notifyOwner(ENG_BillboardSet parent) {
        mParentSet = parent;
    }

    public boolean isUseTexcoordRect() {
        return mUseTexcoordRect;
    }

    public void setTexcoordIndex(short texcoordIndex) {
        mTexcoordIndex = texcoordIndex;
        mUseTexcoordRect = false;
    }

    public short getTexcoordIndex() {
        return mTexcoordIndex;
    }

    public void setTexcoordRect(ENG_RealRect rect) {
        mTexcoordRect.set(rect);
    }

    public void setTexcoordRect(float u0, float v0, float u1, float v1) {
        mTexcoordRect.set(u0, v0, u1, v1);
    }

    public void getTexcoordRect(ENG_RealRect ret) {
        ret.set(mTexcoordRect);
    }

    public ENG_RealRect getTexcoordRect() {
        return new ENG_RealRect(mTexcoordRect);
    }
}
