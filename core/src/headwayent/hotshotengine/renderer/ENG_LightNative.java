/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/4/18, 5:03 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Degree;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 09.07.2017.
 */

public class ENG_LightNative extends ENG_AttachableObject {

//    private long[] ptr = new long[1];
//    private long id;
//    private String name;

    /** @noinspection deprecation*/
    private ENG_Light.LightTypes mLightType;
//    private final ENG_Vector4D mPosition = new ENG_Vector4D();

    protected final ENG_Vector4D mPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D mDirection = new ENG_Vector4D(ENG_Math.VEC4_Z_UNIT);
    private final ENG_ColorValue mDiffuse = new ENG_ColorValue(ENG_ColorValue.WHITE);
    private final ENG_ColorValue mSpecular = new ENG_ColorValue(ENG_ColorValue.BLACK);
    private final ENG_Radian mSpotOuter = new ENG_Radian(new ENG_Degree(40.0f));
    private final ENG_Radian mSpotInner = new ENG_Radian(new ENG_Degree(30.0f));
    private float mSpotFalloff = 1.0f;
    private float mRange = 100000.0f;
    private float mAttenuationConst = 1.0f;
    private float mAttenuationLinear;
    private float mAttenuationQuad;
    private float mPowerScale = 1.0f;

//    private boolean nativePtrSet;
//    private boolean attached;

    public ENG_LightNative(String name, long id) {
        this.name = name;
        this.id = id;
        initializeNative();
    }

    public void initializeNative() {
        ENG_NativeCalls.sceneManager_createLight(this);
    }

    /** @noinspection deprecation*/
    public void setType(ENG_Light.LightTypes l) {
        mLightType = l;
        ENG_NativeCalls.light_setType(this, l);
    }

    /** @noinspection deprecation*/
    public ENG_Light.LightTypes getType() {
        return mLightType;
    }

    /** @noinspection deprecation*/
    public void setSpotlightRange(ENG_Radian innerAngle, ENG_Radian outerAngle,
                                  float falloff) {
        if (mLightType != ENG_Light.LightTypes.LT_SPOTLIGHT) {
            throw new ENG_InvalidFieldStateException(
                    "setSpotlightRange is only valid for spotlights.");
        }
        mSpotInner.set(innerAngle);
        mSpotOuter.set(outerAngle);
        mSpotFalloff = falloff;
        ENG_NativeCalls.light_setSpotlightRange(this, innerAngle.valueRadians(), outerAngle.valueRadians(), falloff);
    }

    /** @noinspection deprecation*/
    public void setSpotlightRange(float innerAngle, float outerAngle,
                                  float falloff) {
        if (mLightType != ENG_Light.LightTypes.LT_SPOTLIGHT) {
            throw new ENG_InvalidFieldStateException(
                    "setSpotlightRange is only valid for spotlights.");
        }
        mSpotInner.set(innerAngle);
        mSpotOuter.set(outerAngle);
        mSpotFalloff = falloff;
        ENG_NativeCalls.light_setSpotlightRange(this, innerAngle, outerAngle, falloff);
    }

    public void setSpotlightInnerAngle(ENG_Radian r) {
        mSpotInner.set(r);
        ENG_NativeCalls.light_setSpotlightRange(this, r.valueRadians(), mSpotOuter.valueRadians(), mSpotFalloff);
    }

    public void setSpotlightInnerAngle(float r) {
        mSpotInner.set(r);
        ENG_NativeCalls.light_setSpotlightRange(this, r, mSpotOuter.valueRadians(), mSpotFalloff);
    }

    public void setSpotlightOuterAngle(ENG_Radian r) {
        mSpotOuter.set(r);
        ENG_NativeCalls.light_setSpotlightRange(this, mSpotInner.valueRadians(), r.valueRadians(), mSpotFalloff);
    }

    public void setSpotlightOuterAngle(float r) {
        mSpotOuter.set(r);
        ENG_NativeCalls.light_setSpotlightRange(this, mSpotInner.valueRadians(), r, mSpotFalloff);
    }

    public void setSpotLightFalloff(float f) {
        mSpotFalloff = f;
        ENG_NativeCalls.light_setSpotlightRange(this, mSpotInner.valueRadians(), mSpotOuter.valueRadians(), f);
    }

    public void getSpotlightInnerAngle(ENG_Radian ret) {
        ret.set(mSpotInner);
    }

    public ENG_Radian getSpotlightInnerAngle() {
        return mSpotInner;
    }

    public ENG_Radian getSpotlightInnerAngleCopy() {
        return new ENG_Radian(mSpotInner);
    }

    public void getSpotlightOuterAngle(ENG_Radian ret) {
        ret.set(mSpotOuter);
    }

    public ENG_Radian getSpotlightOuterAngle() {
        return mSpotOuter;
    }

    public ENG_Radian getSpotlightOuterAngleCopy() {
        return new ENG_Radian(mSpotOuter);
    }

    public float getSpotlightFalloff() {
        return mSpotFalloff;
    }

    public void setDiffuseColour(float r, float g, float b) {
        mDiffuse.set(r, g, b);
        ENG_NativeCalls.light_setDiffuseColour(this, r, g, b);
    }

    public void setDiffuseColour(ENG_ColorValue diffuse) {
        mDiffuse.set(diffuse);
        ENG_NativeCalls.light_setDiffuseColour(this, diffuse.r, diffuse.g, diffuse.b);
    }

    public void getDiffuseColour(ENG_ColorValue ret) {
        ret.set(mDiffuse);
    }

    public ENG_ColorValue getDiffuseColour() {
        return mDiffuse;
    }

    public ENG_ColorValue getDiffuseColourCopy() {
        return new ENG_ColorValue(mDiffuse);
    }

    public void setSpecularColour(float r, float g, float b) {
        mSpecular.set(r, g, b);
        ENG_NativeCalls.light_setSpecularColour(this, r, g, b);
    }

    public void setSpecularColour(ENG_ColorValue diffuse) {
        mSpecular.set(diffuse);
        ENG_NativeCalls.light_setSpecularColour(this, diffuse.r, diffuse.g, diffuse.b);
    }

    public void getSpecularColour(ENG_ColorValue ret) {
        ret.set(mSpecular);
    }

    public ENG_ColorValue getSpecularColour() {
        return mSpecular;
    }

    public ENG_ColorValue getSpecularColourCopy() {
        return new ENG_ColorValue(mSpecular);
    }

    public void setAttenuation(float range,
                               float constant, float linear, float quadratic) {
        mRange = range;
        mAttenuationConst = constant;
        mAttenuationLinear = linear;
        mAttenuationQuad = quadratic;
        ENG_NativeCalls.light_setAttenuation(this, range, constant, linear, quadratic);
    }

    public float getAttenuationRange() {
        return mRange;
    }

    public float getAttenuationConstant() {
        return mAttenuationConst;
    }

    public float getAttenuationLinear() {
        return mAttenuationLinear;
    }

    public float getAttenuationQuadric() {
        return mAttenuationQuad;
    }

    public void setPowerScale(float power) {
        mPowerScale = power;
        ENG_NativeCalls.light_setPowerScale(this, power);
    }

    public float getPowerScale() {
        return mPowerScale;
    }

    public void setAttenuationBasedOnRadius(float radius, float lumThreshold) {
        mAttenuationConst   = 0.5f;
        mAttenuationLinear  = 0.0f;
        mAttenuationQuad    = 0.5f / (radius * radius);

        /*
		lumThreshold = 1 / (c + l*d + q*d²)
        c + l*d + q*d² = 1 / lumThreshold

        if h = c - 1 / lumThreshold then
            (h + l*d + q*d²) = 0
            -l +- sqrt( l² - 4qh ) / 2a = d
        */
        float h = mAttenuationConst - 1.0f / lumThreshold;
        float rootPart = ENG_Math.sqrt( mAttenuationLinear * mAttenuationLinear - 4.0f * mAttenuationQuad * h );
        mRange = (-mAttenuationLinear + rootPart) / (2.0f * mAttenuationQuad);
        ENG_NativeCalls.light_setAttenuationBasedOnRange(this, radius, lumThreshold);
    }

//    public void setPosition(float x, float y, float z) {
//        mPosition.set(x, y, z);
//        ENG_NativeCalls.light_setPosition(this, x, y, z);
//    }
//
//    public void setPosition(ENG_Vector4D v) {
//        mPosition.set(v);
//        ENG_NativeCalls.light_setPosition(this, v.x, v.y, v.z);
//    }
//
//    public void getPosition(ENG_Vector4D ret) {
//        ret.set(mPosition);
//    }
//
//    public ENG_Vector4D getPosition() {
//        return mPosition;
//    }
//
//    public ENG_Vector4D getPositionCopy() {
//        return new ENG_Vector4D(mPosition);
//    }

    public void setDirection(float x, float y, float z) {
        mDirection.set(x, y, z);
        ENG_NativeCalls.light_setDirection(this, x, y, z);
    }

    public void setDirection(ENG_Vector4D v) {
        mDirection.set(v);
        ENG_NativeCalls.light_setDirection(this, v.x, v.y, v.z);
    }

    public void getDirection(ENG_Vector4D ret) {
        ret.set(mDirection);
    }

    public ENG_Vector4D getDirection() {
        return mDirection;
    }

    public ENG_Vector4D getDirectionCopy() {
        return new ENG_Vector4D(mDirection);
    }

    public void destroy() {
        ENG_NativeCalls.sceneManager_destroyLight(this);
        destroyed = true;
    }
}
