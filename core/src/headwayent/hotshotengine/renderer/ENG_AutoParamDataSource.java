/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/6/17, 7:59 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_Frustum.ProjectionType;
import headwayent.hotshotengine.renderer.ENG_Light.LightTypes;

import java.util.ArrayList;

public class ENG_AutoParamDataSource {

    protected final ENG_Matrix4[] mWorldMatrix = new ENG_Matrix4[256];
    protected int mWorldMatrixCount;
    protected ENG_Matrix4[] mWorldMatrixArray;
    protected final ENG_Matrix4 mWorldViewMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mViewProjMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mWorldViewProjMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mInverseWorldMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mInverseWorldViewMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mInverseViewMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mInverseTransposeWorldMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mInverseTransposeWorldViewMatrix = new ENG_Matrix4();
    protected final ENG_Vector4D mCameraPosition = new ENG_Vector4D();
    protected final ENG_Vector4D mCameraPositionObjectSpace = new ENG_Vector4D();
    protected final ENG_Matrix4[] mTextureViewProjMatrix = new ENG_Matrix4[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final ENG_Matrix4[] mTextureWorldViewProjMatrix = new ENG_Matrix4[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final ENG_Matrix4[] mSpotlightViewProjMatrix = new ENG_Matrix4[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final ENG_Matrix4[] mSpotlightWorldViewProjMatrix = new ENG_Matrix4[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final ENG_Vector4D[] mShadowCamDepthRanges = new ENG_Vector4D[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final ENG_Matrix4 mViewMatrix = new ENG_Matrix4();
    protected final ENG_Matrix4 mProjectionMatrix = new ENG_Matrix4();
    protected float mDirLightExtrusionDistance;
    protected final ENG_Vector4D mLodCameraPosition = new ENG_Vector4D();
    protected final ENG_Vector4D mLodCameraPositionObjectSpace = new ENG_Vector4D();

    protected boolean mWorldMatrixDirty = true;
    protected boolean mViewMatrixDirty = true;
    protected boolean mProjMatrixDirty = true;
    protected boolean mWorldViewMatrixDirty = true;
    protected boolean mViewProjMatrixDirty = true;
    protected boolean mWorldViewProjMatrixDirty = true;
    protected boolean mInverseWorldMatrixDirty = true;
    protected boolean mInverseWorldViewMatrixDirty = true;
    protected boolean mInverseViewMatrixDirty = true;
    protected boolean mInverseTransposeWorldMatrixDirty = true;
    protected boolean mInverseTransposeWorldViewMatrixDirty = true;
    protected boolean mCameraPositionDirty = true;
    protected boolean mCameraPositionObjectSpaceDirty = true;
    protected final boolean[] mTextureViewProjMatrixDirty = new boolean[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final boolean[] mTextureWorldViewProjMatrixDirty = new boolean[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final boolean[] mSpotlightViewProjMatrixDirty = new boolean[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final boolean[] mSpotlightWorldViewProjMatrixDirty = new boolean[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final boolean[] mShadowCamDepthRangesDirty = new boolean[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected final ENG_ColorValue mAmbientLight = new ENG_ColorValue();
    protected final ENG_ColorValue mFogColour = new ENG_ColorValue();
    protected final ENG_Vector4D mFogParams = new ENG_Vector4D();
    protected int mPassNumber;
    protected final ENG_Vector4D mSceneDepthRange = new ENG_Vector4D();
    protected boolean mSceneDepthRangeDirty = true;
    protected boolean mLodCameraPositionDirty = true;
    protected boolean mLodCameraPositionObjectSpaceDirty = true;

    protected ENG_Renderable mCurrentRenderable;
    protected ENG_Camera mCurrentCamera;
    protected boolean mCameraRelativeRendering;
    protected ENG_Vector4D mCameraRelativePosition = new ENG_Vector4D();
    /** @noinspection deprecation*/
    protected ArrayList<ENG_Light> mCurrentLightList;
    protected final ENG_Frustum[] mCurrentTextureProjector = new ENG_Frustum[ENG_Config.MAX_SIMULTANEOUS_LIGHTS];
    protected ENG_RenderTarget mCurrentRenderTarget;
    protected ENG_Viewport mCurrentViewport;
    protected ENG_SceneManager mCurrentSceneManager;
    protected ENG_VisibleObjectsBoundsInfo mMainCamBoundsInfo;
    protected ENG_Pass mCurrentPass;

    /** @noinspection deprecation */
    protected final ENG_Light mBlankLight = new ENG_Light();

    private static final ENG_Matrix4 PROJECTIONCLIPSPACE2DTOIMAGESPACE_PERSPECTIVE =
            new ENG_Matrix4(0.5f, 0.0f, 0.0f, 0.5f,
                    0.0f, -0.5f, 0.0f, 0.5f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f);

    private final ENG_Vector4D worldMatrixTemp = new ENG_Vector4D();
    private final ENG_Vector4D cameraPosTemp = new ENG_Vector4D();
    private final ENG_Vector4D lodCameraPosTemp = new ENG_Vector4D();
    private final ENG_Vector4D lodCameraPositionObjectSpaceTemp = new ENG_Vector4D();
    private final ENG_Matrix4 viewMatrix = new ENG_Matrix4();
    private final ENG_Matrix4 viewMatrixProjTextureTemp = new ENG_Matrix4();
    private final ENG_Matrix4 spotlightViewProjMatrixTemp = new ENG_Matrix4();

    public ENG_AutoParamDataSource() {
        mBlankLight.setDiffuseColour(ENG_ColorValue.BLACK);
        mBlankLight.setSpecularColour(ENG_ColorValue.BLACK);
        mBlankLight.setAttenuation(0.0f, 1.0f, 0.0f, 0.0f);

        for (int i = 0; i < ENG_Config.MAX_SIMULTANEOUS_LIGHTS; ++i) {
            mTextureViewProjMatrixDirty[i] = true;
            mTextureWorldViewProjMatrixDirty[i] = true;
            mSpotlightViewProjMatrixDirty[i] = true;
            mSpotlightWorldViewProjMatrixDirty[i] = true;
        }
        for (int i = 0; i < mWorldMatrix.length; ++i) {
            mWorldMatrix[i] = new ENG_Matrix4();
        }
    }

    /** @noinspection deprecation*/
    public ENG_Light getLight(int index) {
        if ((mCurrentLightList != null) &&
                (index >= 0) && (index < mCurrentLightList.size())) {
            return mCurrentLightList.get(index);
        } else {
            return mBlankLight;
        }
    }

    public void setCurrentRenderable(ENG_Renderable rend) {
        mCurrentRenderable = rend;
        mWorldMatrixDirty = true;
        mViewMatrixDirty = true;
        mProjMatrixDirty = true;
        mWorldViewMatrixDirty = true;
        mViewProjMatrixDirty = true;
        mWorldViewProjMatrixDirty = true;
        mInverseWorldMatrixDirty = true;
        mInverseViewMatrixDirty = true;
        mInverseWorldViewMatrixDirty = true;
        mInverseTransposeWorldMatrixDirty = true;
        mInverseTransposeWorldViewMatrixDirty = true;
        mCameraPositionObjectSpaceDirty = true;
        mLodCameraPositionObjectSpaceDirty = true;
        for (int i = 0; i < ENG_Config.MAX_SIMULTANEOUS_LIGHTS; ++i) {
            mTextureWorldViewProjMatrixDirty[i] = true;
            mSpotlightWorldViewProjMatrixDirty[i] = true;
        }
    }

    public void setCurrentCamera(ENG_Camera cam, boolean useCameraRelative) {
        mCurrentCamera = cam;
        mCameraRelativeRendering = useCameraRelative;
        mCameraRelativePosition = cam.getDerivedPosition();
        mViewMatrixDirty = true;
        mProjMatrixDirty = true;
        mWorldViewMatrixDirty = true;
        mViewProjMatrixDirty = true;
        mWorldViewProjMatrixDirty = true;
        mInverseViewMatrixDirty = true;
        mInverseWorldViewMatrixDirty = true;
        mInverseTransposeWorldViewMatrixDirty = true;
        mCameraPositionObjectSpaceDirty = true;
        mCameraPositionDirty = true;
        mLodCameraPositionObjectSpaceDirty = true;
        mLodCameraPositionDirty = true;
    }

    /** @noinspection deprecation*/
    public void setCurrentLightList(ArrayList<ENG_Light> ll) {
        mCurrentLightList = ll;
        for (int i = 0; (i < ll.size()) && (i < ENG_Config.MAX_SIMULTANEOUS_LIGHTS); ++i) {
            mSpotlightViewProjMatrixDirty[i] = true;
            mSpotlightWorldViewProjMatrixDirty[i] = true;
        }
    }

    public float getLightNumber(int index) {
        return (float) getLight(index)._getIndexInFrame();
    }

    public ENG_ColorValue getLightDiffuseColour(int index) {
        return getLight(index).getDiffuseColour();
    }

    public void getLightDiffuseColour(int index, ENG_ColorValue ret) {
        getLight(index).getDiffuseColour(ret);
    }

    public ENG_ColorValue getLightSpecularColour(int index) {
        return getLight(index).getSpecularColour();
    }

    public void getLightSpecularColour(int index, ENG_ColorValue ret) {
        getLight(index).getSpecularColour(ret);
    }

    /** @noinspection deprecation*/
    public ENG_ColorValue getLightDiffuseColourWithPower(int index) {
        ENG_Light l = getLight(index);
        ENG_ColorValue scaled = new ENG_ColorValue(l.getDiffuseColour());
        float power = l.getPowerScale();
        // scale, but not alpha
        scaled.r *= power;
        scaled.g *= power;
        scaled.b *= power;
        return scaled;
    }

    /** @noinspection deprecation*/
    public void getLightDiffuseColourWithPower(int index, ENG_ColorValue scaled) {
        ENG_Light l = getLight(index);
        scaled.set(l.getDiffuseColour());
        float power = l.getPowerScale();
        // scale, but not alpha
        scaled.r *= power;
        scaled.g *= power;
        scaled.b *= power;

    }

    /** @noinspection deprecation*/
    public ENG_ColorValue getLightSpecularColourWithPower(int index) {
        ENG_Light l = getLight(index);
        ENG_ColorValue scaled = new ENG_ColorValue(l.getSpecularColour());
        float power = l.getPowerScale();
        // scale, but not alpha
        scaled.r *= power;
        scaled.g *= power;
        scaled.b *= power;
        return scaled;
    }

    /** @noinspection deprecation*/
    public void getLightSpecularColourWithPower(int index, ENG_ColorValue scaled) {
        ENG_Light l = getLight(index);
        scaled.set(l.getSpecularColour());
        float power = l.getPowerScale();
        // scale, but not alpha
        scaled.r *= power;
        scaled.g *= power;
        scaled.b *= power;

    }

    public ENG_Vector4D getLightPosition(int index) {
        return getLight(index).getDerivedPosition(true);
    }

    public void getLightPosition(int index, ENG_Vector4D ret) {
        getLight(index).getDerivedPosition(true, ret);
    }

    public ENG_Vector4D getLightAs4DVector(int index) {
        return getLight(index).getAs4DVector(true);
    }

    public void getLightAs4DVector(int index, ENG_Vector4D ret) {
        getLight(index).getAs4DVector(true, ret);
    }

    public ENG_Vector4D getLightDirection(int index) {
        return getLight(index).getDerivedDirection();
    }

    public void getLightDirection(int index, ENG_Vector4D ret) {
        getLight(index).getDerivedDirection(ret);
    }

    public float getLightPowerScale(int index) {
        return getLight(index).getPowerScale();
    }

    /** @noinspection deprecation*/
    public ENG_Vector4D getLightAttenuation(int index) {
        ENG_Light l = getLight(index);
        return new ENG_Vector4D(l.getAttenuationRange(),
                l.getAttenuationConstant(),
                l.getAttenuationLinear(),
                l.getAttenuationQuadric());
    }

    /** @noinspection deprecation*/
    public void getLightAttenuation(int index, ENG_Vector4D ret) {
        ENG_Light l = getLight(index);
        ret.set(l.getAttenuationRange(),
                l.getAttenuationConstant(),
                l.getAttenuationLinear(),
                l.getAttenuationQuadric());
    }

    /** @noinspection deprecation*/
    public ENG_Vector4D getSpotlightParams(int index) {
        ENG_Light l = getLight(index);
        if (l.getType() == LightTypes.LT_SPOTLIGHT) {
            return new ENG_Vector4D(ENG_Math.cos(l.getSpotlightInnerAngle().valueRadians() * 0.5f),
                    ENG_Math.cos(l.getSpotlightOuterAngle().valueRadians() * 0.5f),
                    l.getSpotlightFalloff(),
                    1.0f);
        } else {
            // Use safe values which result in no change to point & dir light calcs
            // The spot factor applied to the usual lighting calc is
            // pow((dot(spotDir, lightDir) - y) / (x - y), z)
            // Therefore if we set z to 0.0f then the factor will always be 1
            // since pow(anything, 0) == 1
            // However we also need to ensure we don't overflow because of the division
            // therefore set x = 1 and y = 0 so divisor doesn't change scale
            return new ENG_Vector4D(1.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    /** @noinspection deprecation*/
    public void getSpotlightParams(int index, ENG_Vector4D ret) {
        ENG_Light l = getLight(index);
        if (l.getType() == LightTypes.LT_SPOTLIGHT) {
            ret.set(ENG_Math.cos(l.getSpotlightInnerAngle().valueRadians() * 0.5f),
                    ENG_Math.cos(l.getSpotlightOuterAngle().valueRadians() * 0.5f),
                    l.getSpotlightFalloff(),
                    1.0f);
        } else {
            // Use safe values which result in no change to point & dir light calcs
            // The spot factor applied to the usual lighting calc is
            // pow((dot(spotDir, lightDir) - y) / (x - y), z)
            // Therefore if we set z to 0.0f then the factor will always be 1
            // since pow(anything, 0) == 1
            // However we also need to ensure we don't overflow because of the division
            // therefore set x = 1 and y = 0 so divisor doesn't change scale
            ret.set(1.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    public void setMainCamBoundsInfo(ENG_VisibleObjectsBoundsInfo info) {
        mMainCamBoundsInfo = info;
        mSceneDepthRangeDirty = true;
    }

    public void setCurrentSceneManager(ENG_SceneManager sm) {
        mCurrentSceneManager = sm;
    }

    public void setWorldMatrices(ENG_Matrix4[] m, int count) {
        mWorldMatrixArray = m;
        mWorldMatrixCount = count;
        mWorldMatrixDirty = false;
    }

    public ENG_Matrix4 getWorldMatrix() {
        if (mWorldMatrixDirty) {
            mWorldMatrixArray = mWorldMatrix;
            mCurrentRenderable.getWorldTransforms(mWorldMatrix);
            mWorldMatrixCount = mCurrentRenderable.getNumWorldTransforms();
            if (mCameraRelativeRendering) {
                for (int i = 0; i < mWorldMatrixCount; ++i) {
                    mWorldMatrix[i].getTrans(worldMatrixTemp);
                    worldMatrixTemp.subInPlace(mCameraRelativePosition);
                    mWorldMatrix[i].setTrans(worldMatrixTemp);
                }
            }
            mWorldMatrixDirty = false;
        }
        return mWorldMatrixArray[0];
    }

    public int getWorldMatrixCount() {
        getWorldMatrix();
        return mWorldMatrixCount;
    }

    public ENG_Matrix4[] getWorldMatrixArray() {
        getWorldMatrix();
        return mWorldMatrixArray;
    }

    public ENG_Matrix4 getViewMatrix() {
        if (mViewMatrixDirty) {
            if ((mCurrentRenderable != null) &&
                    (mCurrentRenderable.getUseIdentityView())) {
                mViewMatrix.setIdentity();
            } else {
                mCurrentCamera.getViewMatrix(true, mViewMatrix);
                if (mCameraRelativeRendering) {
                    mViewMatrix.setTrans(ENG_Math.PT4_ZERO);
                }
            }
            mViewMatrixDirty = false;
        }
        return mViewMatrix;
    }

    public ENG_Matrix4 getViewProjectionMatrix() {
        if (mViewProjMatrixDirty) {
            getProjectionMatrix().concatenate(getViewMatrix(), mViewProjMatrix);
            mViewProjMatrixDirty = false;
        }
        return mViewProjMatrix;
    }

    public ENG_Matrix4 getProjectionMatrix() {
        if (mProjMatrixDirty) {
            if ((mCurrentRenderable != null) &&
                    (mCurrentRenderable.getUseIdentityView())) {
                ENG_RenderRoot.getRenderRoot().getRenderSystem()._convertProjectionMatrix(
                        ENG_Math.MAT4_IDENTITY, mProjectionMatrix, true);
            } else {
                mCurrentCamera.getProjectionMatrixWithRSDepth(mProjectionMatrix);
            }
            if ((mCurrentRenderTarget != null) &&
                    (mCurrentRenderTarget.requiresTextureFlipping())) {
                mProjectionMatrix.set(1, 0, -mProjectionMatrix.get(1, 0));
                mProjectionMatrix.set(1, 1, -mProjectionMatrix.get(1, 1));
                mProjectionMatrix.set(1, 2, -mProjectionMatrix.get(1, 2));
                mProjectionMatrix.set(1, 3, -mProjectionMatrix.get(1, 3));
            }
            mProjMatrixDirty = false;
        }
        return mProjectionMatrix;
    }

    public ENG_Matrix4 getWorldViewMatrix() {
        if (mWorldViewMatrixDirty) {
            getViewMatrix().concatenateAffine(getWorldMatrix(), mWorldViewMatrix);
            mWorldViewMatrixDirty = false;
        }
        return mWorldViewMatrix;
    }

    public ENG_Matrix4 getWorldViewProjMatrix() {
        if (mWorldViewProjMatrixDirty) {
            getProjectionMatrix().concatenate(
                    getWorldViewMatrix(), mWorldViewProjMatrix);
            mWorldViewProjMatrixDirty = false;
        }
        return mWorldViewProjMatrix;
    }

    public ENG_Matrix4 getInverseWorldMatrix() {
        if (mInverseWorldMatrixDirty) {
            getWorldMatrix().invertAffine(mInverseWorldMatrix);
            mInverseWorldMatrixDirty = false;
        }
        return mInverseWorldMatrix;
    }

    public ENG_Matrix4 getInverseWorldViewMatrix() {
        if (mInverseWorldViewMatrixDirty) {
            getWorldViewMatrix().invertAffine(mInverseWorldViewMatrix);
            mInverseWorldViewMatrixDirty = false;
        }
        return mInverseWorldViewMatrix;
    }

    public ENG_Matrix4 getInverseViewMatrix() {
        if (mInverseViewMatrixDirty) {
            getViewMatrix().invertAffine(mInverseViewMatrix);
            mInverseViewMatrixDirty = false;
        }
        return mInverseViewMatrix;
    }

    public ENG_Matrix4 getInverseTransposeWorldMatrix() {
        if (mInverseTransposeWorldMatrixDirty) {
            getInverseWorldMatrix().transpose(mInverseTransposeWorldMatrix);
            mInverseTransposeWorldMatrixDirty = false;
        }
        return mInverseTransposeWorldMatrix;
    }

    public ENG_Matrix4 getInverseTransposeWorldViewMatrix() {
        if (mInverseTransposeWorldViewMatrixDirty) {
            getInverseWorldViewMatrix().transpose(mInverseTransposeWorldViewMatrix);
            mInverseTransposeWorldViewMatrixDirty = false;
        }
        return mInverseTransposeWorldViewMatrix;
    }

    public ENG_Vector4D getCameraPosition() {
        if (mCameraPositionDirty) {
            mCurrentCamera.getDerivedPosition(cameraPosTemp);
            if (mCameraRelativeRendering) {
                cameraPosTemp.subInPlace(mCameraRelativePosition);

            }
            mCameraPosition.set(
                    cameraPosTemp.x, cameraPosTemp.y, cameraPosTemp.z, 1.0f);
            mCameraPositionDirty = false;
        }
        return mCameraPosition;
    }

    public ENG_Vector4D getCameraPositionObjectSpace() {
        if (mCameraPositionObjectSpaceDirty) {
            if (mCameraRelativeRendering) {
                getInverseWorldMatrix().transformAffine(
                        ENG_Math.PT4_ZERO, mCameraPositionObjectSpace);

            } else {
                getInverseWorldMatrix().transformAffine(
                        mCurrentCamera.getDerivedPosition(), mCameraPositionObjectSpace);
            }
            mCameraPositionObjectSpaceDirty = false;
        }
        return mCameraPositionObjectSpace;
    }

    public ENG_Vector4D getLodCameraPosition() {
        if (mLodCameraPositionDirty) {
            mCurrentCamera.getLodCamera().getDerivedPosition(lodCameraPosTemp);
            if (mCameraRelativeRendering) {
                lodCameraPosTemp.subInPlace(mCameraRelativePosition);
            }
            mLodCameraPosition.set(
                    lodCameraPosTemp.x, lodCameraPosTemp.y, lodCameraPosTemp.z, 1.0f);
            mLodCameraPositionDirty = false;
        }
        return mLodCameraPosition;
    }

    public ENG_Vector4D getLodCameraPositionObjectSpace() {
        if (mLodCameraPositionObjectSpaceDirty) {
            if (mCameraRelativeRendering) {
                mCurrentCamera.getLodCamera().getDerivedPosition(
                        lodCameraPositionObjectSpaceTemp);
                lodCameraPositionObjectSpaceTemp.subInPlace(mCameraRelativePosition);
                getInverseWorldMatrix().transformAffine(
                        lodCameraPositionObjectSpaceTemp, mLodCameraPositionObjectSpace);
            } else {
                getInverseWorldMatrix().transformAffine(
                        mCurrentCamera.getLodCamera().getDerivedPosition(),
                        mLodCameraPositionObjectSpace);
            }
            mLodCameraPositionObjectSpaceDirty = false;
        }
        return mLodCameraPositionObjectSpace;
    }

    public void setAmbientLightColour(ENG_ColorValue ambient) {
        mAmbientLight.set(ambient);
    }

    public float getLightCount() {
        return (float) mCurrentLightList.size();
    }

    public float getLightCastsShadows(int index) {
        return getLight(index).getCastShadows() ? 1.0f : 0.0f;
    }

    public ENG_ColorValue getAmbientLightColour() {
        return mAmbientLight;
    }

    public void setCurrentPass(ENG_Pass pass) {
        mCurrentPass = pass;
    }

    public ENG_Pass getCurrentPass() {
        return mCurrentPass;
    }

    public ENG_Vector4D getTextureSize(int index) {
        ENG_Vector4D size = new ENG_Vector4D();
        getTextureSize(index, size);
        return size;
    }

    public void getTextureSize(int index, ENG_Vector4D size) {
        size.set(1.0f);
        if (index < mCurrentPass.getNumTextureUnitStates()) {
            ENG_Texture tex = mCurrentPass.getTextureUnitState(
                    (short) index)._getTexturePtr();
            if (tex != null) {
                size.set(tex.getWidth(), tex.getHeight(), tex.getDepth());
            }
        }
    }

    public ENG_Vector4D getInverseTextureSize(int index) {
        ENG_Vector4D v = getTextureSize(index);
        ENG_Vector4D.divInv(1.0f, v, v);
        return v;
    }

    public void getInverseTextureSize(int index, ENG_Vector4D size) {
        getTextureSize(index, size);
        ENG_Vector4D.divInv(1.0f, size, size);
    }

    public ENG_Vector4D getPackedTextureSize(int index) {
        ENG_Vector4D v = getTextureSize(index);
        return new ENG_Vector4D(v.x, v.y, 1.0f / v.x, 1.0f / v.y);
    }

    public void getPacketTextureSize(int index, ENG_Vector4D v) {
        getTextureSize(index, v);
        v.set(v.x, v.y, 1.0f / v.x, 1.0f / v.y);
    }

    public void setFog(ENG_ColorValue colour,
                       float expDensity, float linearStart, float linearEnd) {
        mFogColour.set(colour);
        mFogParams.x = expDensity;
        mFogParams.y = linearStart;
        mFogParams.z = linearEnd;
        mFogParams.w =
                linearEnd != linearStart ? 1.0f / (linearEnd - linearStart) : 0.0f;
    }

    public ENG_ColorValue getFogColour() {
        return mFogColour;
    }

    public ENG_Vector4D getFogParams() {
        return mFogParams;
    }

    public void setTextureProjector(ENG_Frustum frust) {
        setTextureProjector(frust, 0);
    }

    public void setTextureProjector(ENG_Frustum frust, int index) {
        if (index < ENG_Config.MAX_SIMULTANEOUS_LIGHTS) {
            mCurrentTextureProjector[index] = frust;
            mTextureViewProjMatrixDirty[index] = true;
            mTextureWorldViewProjMatrixDirty[index] = true;
            mShadowCamDepthRangesDirty[index] = true;
        }
    }

    public ENG_Matrix4 getTextureViewProjMatrix(int index) {
        if (index < ENG_Config.MAX_SIMULTANEOUS_LIGHTS) {
            if ((mTextureViewProjMatrixDirty[index]) &&
                    (mCurrentTextureProjector[index] != null)) {
                if (mCameraRelativeRendering) {
                    mCurrentTextureProjector[index].calcViewMatrixRelative(
                            mCurrentCamera.getDerivedPosition(), viewMatrix);
                    PROJECTIONCLIPSPACE2DTOIMAGESPACE_PERSPECTIVE.concatenate(
                            mCurrentTextureProjector[index].getProjectionMatrixWithRSDepth(),
                            viewMatrixProjTextureTemp);
                    viewMatrixProjTextureTemp.concatenate(viewMatrix,
                            mTextureViewProjMatrix[index]);
                } else {
                    PROJECTIONCLIPSPACE2DTOIMAGESPACE_PERSPECTIVE.concatenate(
                            mCurrentTextureProjector[index].getProjectionMatrixWithRSDepth(),
                            viewMatrixProjTextureTemp);
                    viewMatrixProjTextureTemp.concatenate(
                            mCurrentTextureProjector[index].getViewMatrix(),
                            mTextureViewProjMatrix[index]);
                }
                mTextureViewProjMatrixDirty[index] = false;
            }
            return mTextureViewProjMatrix[index];
        } else {
            return ENG_Math.MAT4_IDENTITY;
        }
    }

    public ENG_Matrix4 getTextureWorldViewProjMatrix(int index) {
        if (index < ENG_Config.MAX_SIMULTANEOUS_LIGHTS) {
            if ((mTextureViewProjMatrixDirty[index]) &&
                    (mCurrentTextureProjector[index] != null)) {
                getTextureViewProjMatrix(index).concatenate(getViewMatrix(),
                        mTextureWorldViewProjMatrix[index]);
                mTextureWorldViewProjMatrixDirty[index] = false;
            }
            return mTextureWorldViewProjMatrix[index];
        } else {
            return ENG_Math.MAT4_IDENTITY;
        }
    }

    /** @noinspection deprecation*/
    public ENG_Matrix4 getSpotlightViewProjMatrix(int index) {
        if (index < ENG_Config.MAX_SIMULTANEOUS_LIGHTS) {
            ENG_Light l = getLight(index);
            if ((l != mBlankLight) && (l.getType() == LightTypes.LT_SPOTLIGHT) &&
                    (mSpotlightViewProjMatrixDirty[index])) {
                ENG_Frustum frust = new ENG_Frustum("");
                ENG_SceneNode dummyNode = new ENG_SceneNode(null);
                dummyNode.attachObject(frust);

                frust.setProjectionType(ProjectionType.PT_PERSPECTIVE);
                frust.setFOVy(l.getSpotlightOuterAngle());
                frust.setAspectRatio(1.0f);
                // set near clip the same as main camera, since they are likely
                // to both reflect the nature of the scene
                frust.setNearClipDistance(mCurrentCamera.getNearClipDistance());
                // Calculate position, which same as spotlight position, in camera-relative coords if required
                dummyNode.setPosition(l.getDerivedPosition(true));

                // Calculate direction, which same as spotlight direction
                ENG_Vector4D dir = l.getDerivedDirection().invert();
                dir.normalize();
                ENG_Vector4D up = ENG_Math.VEC4_Y_UNIT;

                if (Math.abs(up.dotProduct(dir)) >= 1.0f) {
                    // Use camera up
                    up = ENG_Math.VEC4_Z_UNIT;
                }
                // cross twice to rederive, only direction is unaltered
                ENG_Vector4D left = dir.crossProduct(up);
                left.normalize();
                up = dir.crossProduct(left);
                up.normalize();
                // Derive quaternion from axes
                ENG_Quaternion q = new ENG_Quaternion();
                q.fromAxes(left, up, dir);
                dummyNode.setOrientation(q);

                // The view matrix here already includes camera-relative changes if necessary
                // since they are built into the frustum position
                PROJECTIONCLIPSPACE2DTOIMAGESPACE_PERSPECTIVE.concatenate(
                        frust.getProjectionMatrixWithRSDepth(),
                        spotlightViewProjMatrixTemp);
                spotlightViewProjMatrixTemp.concatenate(
                        frust.getViewMatrix(), mSpotlightViewProjMatrix[index]);

                mSpotlightViewProjMatrixDirty[index] = false;
            }
            return mSpotlightViewProjMatrix[index];
        } else {
            return ENG_Math.MAT4_IDENTITY;
        }
    }

    /** @noinspection deprecation*/
    public ENG_Matrix4 getSpotlightWorldViewProjMatrix(int index) {
        if (index < ENG_Config.MAX_SIMULTANEOUS_LIGHTS) {
            ENG_Light l = getLight(index);
            if ((l != mBlankLight) && (l.getType() == LightTypes.LT_SPOTLIGHT) &&
                    (mSpotlightViewProjMatrixDirty[index])) {
                getSpotlightViewProjMatrix(index).concatenate(getWorldMatrix(),
                        mSpotlightWorldViewProjMatrix[index]);
                mSpotlightWorldViewProjMatrixDirty[index] = false;
            }
            return mSpotlightWorldViewProjMatrix[index];
        } else {
            return ENG_Math.MAT4_IDENTITY;
        }
    }

    public ENG_Matrix4 getTextureTransformMatrix(int index) {
        if (mCurrentPass == null) {
            throw new NullPointerException("current pass is NULL!");
        }
        if (index < mCurrentPass.getNumTextureUnitStates()) {
            return mCurrentPass.getTextureUnitState((short) index).getTextureTransform();
        } else {
            return ENG_Math.MAT4_IDENTITY;
        }
    }

    public void setCurrentRenderTarget(ENG_RenderTarget target) {
        mCurrentRenderTarget = target;
    }

    public ENG_RenderTarget getCurrentRenderTarget() {
        return mCurrentRenderTarget;
    }

    public void setCurrentViewport(ENG_Viewport viewport) {
        mCurrentViewport = viewport;
    }

    public ENG_Viewport getCurrentViewport() {
        return mCurrentViewport;
    }

    public void setShadowDirLightExtrusionDistance(float dist) {
        mDirLightExtrusionDistance = dist;
    }

    /** @noinspection deprecation*/
    public float getShadowExtrusionDistance() {
        ENG_Light l = getLight(0);
        if (l.getType() == LightTypes.LT_DIRECTIONAL) {
            return mDirLightExtrusionDistance;
        } else {
            ENG_Vector4D objPos = getInverseWorldMatrix().transformAffineRet(
                    l.getDerivedPosition(true));
            return l.getAttenuationRange() - objPos.length();
        }
    }

    public ENG_Renderable getCurrentRenderable() {
        return mCurrentRenderable;
    }

    public ENG_Matrix4 getInverseViewProjMatrix() {
        return getViewProjectionMatrix().invertRet();
    }

    public void getInverseViewProjMatrix(ENG_Matrix4 ret) {
        getViewProjectionMatrix().invert(ret);
    }

    public ENG_Matrix4 getInverseTransposeViewProjMatrix() {
        return getInverseViewProjMatrix().transposeRet();
    }

    public void getInverseTransposeViewProjMatrix(ENG_Matrix4 ret) {
        getInverseViewProjMatrix().transpose(ret);
    }

    public ENG_Matrix4 getTransposeViewProjMatrix() {
        return getViewProjectionMatrix().transposeRet();
    }

    public void getTransposeViewProjMatrix(ENG_Matrix4 ret) {
        getViewProjectionMatrix().transpose(ret);
    }

    public ENG_Matrix4 getTransposeViewMatrix() {
        return getViewMatrix().transposeRet();
    }

    public void getTransposeViewMatrix(ENG_Matrix4 ret) {
        getViewMatrix().transpose(ret);
    }

    public ENG_Matrix4 getInverseTransposeViewMatrix() {
        return getInverseViewMatrix().transposeRet();
    }

    public void getInverseTransposeViewMatrix(ENG_Matrix4 ret) {
        getInverseViewMatrix().transpose(ret);
    }

    public ENG_Matrix4 getTransposeProjectionMatrix() {
        return getProjectionMatrix().transposeRet();
    }

    public void getTransposeProjectionMatrix(ENG_Matrix4 ret) {
        getProjectionMatrix().transpose(ret);
    }

    public ENG_Matrix4 getInverseProjectionMatrix() {
        return getProjectionMatrix().invertRet();
    }

    public void getInverseProjectionMatrix(ENG_Matrix4 ret) {
        getProjectionMatrix().invert(ret);
    }

    public ENG_Matrix4 getInverseTransposeProjectionMatrix() {
        return getInverseProjectionMatrix().transposeRet();
    }

    public void getInverseTransposeProjectionMatrix(ENG_Matrix4 ret) {
        getInverseProjectionMatrix().transpose(ret);
    }

    public ENG_Matrix4 getTransposeWorldViewProjMatrix() {
        return getWorldViewProjMatrix().transposeRet();
    }

    public void getTransposeWorldViewProjMatrix(ENG_Matrix4 ret) {
        getWorldViewProjMatrix().transpose(ret);
    }

    public ENG_Matrix4 getInverseWorldViewProjMatrix() {
        return getWorldViewProjMatrix().invertRet();
    }

    public void getInverseWorldViewProjMatrix(ENG_Matrix4 ret) {
        getWorldViewProjMatrix().invert(ret);
    }

    public ENG_Matrix4 getInverseTransposeWorldViewProjMatrix() {
        return getInverseWorldViewProjMatrix().transposeRet();
    }

    public void getInverseTransposeWorldViewProjMatrix(ENG_Matrix4 ret) {
        getInverseWorldViewProjMatrix().transpose(ret);
    }

    public ENG_Matrix4 getTransposeWorldViewMatrix() {
        return getWorldViewMatrix().transposeRet();
    }

    public void getTransposeWorldViewMatrix(ENG_Matrix4 ret) {
        getWorldViewMatrix().transpose(ret);
    }

    public ENG_Matrix4 getTransposeWorldMatrix() {
        return getWorldMatrix().transposeRet();
    }

    public void getTransposeWorldMatrix(ENG_Matrix4 ret) {
        getWorldMatrix().transpose(ret);
    }

    public float getTime() {
        return ENG_ControllerManager.getSingleton().getElapsedTime();
    }

    public float getTime_0_X(float x) {
        return getTime() % x;
    }

    public float getCosTime_0_X(float x) {
        return ENG_Math.cos(getTime_0_X(x));
    }

    public float getSinTime_0_X(float x) {
        return ENG_Math.sin(getTime_0_X(x));
    }

    public float getTanTime_0_X(float x) {
        return ENG_Math.tan(getTime_0_X(x));
    }

    public ENG_Vector4D getTime_0_X_packed(float x) {
        float t = getTime_0_X(x);
        return new ENG_Vector4D(t, ENG_Math.sin(t), ENG_Math.cos(t), ENG_Math.tan(t));
    }

    public void getTime_0_X_packed(float x, ENG_Vector4D ret) {
        float t = getTime_0_X(x);
        ret.set(t, ENG_Math.sin(t), ENG_Math.cos(t), ENG_Math.tan(t));
    }

    public float getTime_0_1(float x) {
        return getTime_0_X(x) / x;
    }

    public float getCosTime_0_1(float x) {
        return ENG_Math.cos(getTime_0_1(x));
    }

    public float getSinTime_0_1(float x) {
        return ENG_Math.sin(getTime_0_1(x));
    }

    public float getTanTime_0_1(float x) {
        return ENG_Math.tan(getTime_0_1(x));
    }

    public ENG_Vector4D getTime_0_1_packed(float x) {
        float t = getTime_0_1(x);
        return new ENG_Vector4D(t, ENG_Math.sin(t), ENG_Math.cos(t), ENG_Math.tan(t));
    }

    public void getTime_0_1_packed(float x, ENG_Vector4D ret) {
        float t = getTime_0_1(x);
        ret.set(t, ENG_Math.sin(t), ENG_Math.cos(t), ENG_Math.tan(t));
    }

    public float getTime_0_2Pi(float x) {
        return getTime_0_X(x) / x * 2.0f * ENG_Math.PI;
    }

    public float getCosTime_0_2Pi(float x) {
        return ENG_Math.cos(getTime_0_2Pi(x));
    }

    public float getSinTime_0_2Pi(float x) {
        return ENG_Math.sin(getTime_0_2Pi(x));
    }

    public float getTanTime_0_2Pi(float x) {
        return ENG_Math.tan(getTime_0_2Pi(x));
    }

    public ENG_Vector4D getTime_0_2Pi_packed(float x) {
        float t = getTime_0_2Pi(x);
        return new ENG_Vector4D(t, ENG_Math.sin(t), ENG_Math.cos(t), ENG_Math.tan(t));
    }

    public void getTime_0_2Pi_packed(float x, ENG_Vector4D ret) {
        float t = getTime_0_2Pi(x);
        ret.set(t, ENG_Math.sin(t), ENG_Math.cos(t), ENG_Math.tan(t));
    }

    public float getFrameTime() {
        return ENG_ControllerManager.getSingleton().getFrameTimeSource().getValue().get().getValue();
    }

    public float getFPS() {
        return mCurrentRenderTarget.getLastFPS();
    }

    public float getViewportWidth() {
        return mCurrentViewport.getActualWidth();
    }

    public float getViewportHeight() {
        return mCurrentViewport.getActualHeight();
    }

    public float getInverseViewportWidth() {
        return 1.0f / mCurrentViewport.getActualWidth();
    }

    public float getInverseViewportHeight() {
        return 1.0f / mCurrentViewport.getActualHeight();
    }

    public ENG_Vector4D getViewDirection() {
        return mCurrentCamera.getDerivedDirection();
    }

    public ENG_Vector4D getViewSideVector() {
        return mCurrentCamera.getDerivedRight();
    }

    public ENG_Vector4D getViewUpVector() {
        return mCurrentCamera.getDerivedUp();
    }

    public float getFOV() {
        return mCurrentCamera.getFOVy();
    }

    public float getNearClipDistance() {
        return mCurrentCamera.getNearClipDistance();
    }

    public float getFarClipDistance() {
        return mCurrentCamera.getFarClipDistance();
    }

/*	public int getPassNumber() {
		return mPassNumber;
	}
	
	public void incPassNumber() {
		++mPassNumber;
	}*/

    public ENG_Vector4D getSceneDepthRange() {
        if (mSceneDepthRangeDirty) {
            float depthRange =
                    mMainCamBoundsInfo.maxDistanceInFrustum -
                            mMainCamBoundsInfo.minDistanceInFrustum;
            if (depthRange > ENG_Math.FLT_EPSILON) {
                mSceneDepthRange.set(mMainCamBoundsInfo.minDistanceInFrustum,
                        mMainCamBoundsInfo.maxDistanceInFrustum,
                        depthRange,
                        1.0f / depthRange);
            } else {
                mSceneDepthRange.set(0.0f, 100000.0f, 100000.0f, 1.0f / 100000.0f);
            }
            mSceneDepthRangeDirty = false;
        }
        return mSceneDepthRange;
    }

    public ENG_Vector4D getShadowSceneDepthRange(int index) {
        if (!mCurrentSceneManager.isShadowTechniqueTextureBased()) {
            return new ENG_Vector4D(0.0f, 100000.0f, 100000.0f, 1.0f / 100000.0f);
        }

        if (index < ENG_Config.MAX_SIMULTANEOUS_LIGHTS) {
            if ((mShadowCamDepthRangesDirty[index]) &&
                    (mCurrentTextureProjector[index] != null)) {
                ENG_VisibleObjectsBoundsInfo info =
                        mCurrentSceneManager.getVisibleObjectsBoundsInfo(
                                (ENG_Camera) mCurrentTextureProjector[index]);
                float depthRange =
                        info.maxDistanceInFrustum - info.minDistanceInFrustum;
                if (depthRange > ENG_Math.FLT_EPSILON) {
                    mShadowCamDepthRanges[index].set(info.minDistanceInFrustum,
                            info.maxDistanceInFrustum,
                            depthRange,
                            1.0f / depthRange);
                } else {
                    mShadowCamDepthRanges[index].set(
                            0.0f, 100000.0f, 100000.0f, 1.0f / 100000.0f);
                }
                mShadowCamDepthRangesDirty[index] = false;
            }
            return mShadowCamDepthRanges[index];
        }
        return new ENG_Vector4D(0.0f, 100000.0f, 100000.0f, 1.0f / 100000.0f);
    }

    public ENG_ColorValue getShadowColour() {
        return mCurrentSceneManager.getShadowColour();
    }

    /** @noinspection deprecation*/
    public void updateLightCustomGpuParameter(
            ENG_AutoConstantEntry constantEntry, ENG_GpuProgramParameters params) {
        short lightIndex = (short) (constantEntry.data & 0xFFFF);
        short paramIndex = (short) ((constantEntry.data >> 16) & 0xFFFF);
        if ((mCurrentLightList != null) && (lightIndex < mCurrentLightList.size())) {
            ENG_Light light = getLight(lightIndex);
            light._updateCustomGpuParameter(paramIndex, constantEntry, params);
        }
    }

    public ENG_ColorValue getSurfaceAmbientColour() {
        return mCurrentPass.getAmbient();
    }

    public ENG_ColorValue getSurfaceDiffuseColour() {
        return mCurrentPass.getDiffuse();
    }

    public ENG_ColorValue getSurfaceSpecularColour() {
        return mCurrentPass.getSpecular();
    }

    public ENG_ColorValue getSurfaceEmissiveColour() {
        return mCurrentPass.getSelfIllumination();
    }

    public float getSurfaceShininess() {
        return mCurrentPass.getShininess();
    }

    public ENG_ColorValue getDerivedAmbientLightColour() {
        return getAmbientLightColour().mul(getSurfaceAmbientColour());
    }

    public ENG_ColorValue getDerivedSceneColour() {
        ENG_ColorValue result = getDerivedAmbientLightColour().add(
                getSurfaceEmissiveColour());
        result.a = getSurfaceDiffuseColour().a;
        return result;
    }

    public void setPassNumber(int passNum) {
        mPassNumber = passNum;
    }

    public int getPassNumber() {
        return mPassNumber;
    }

    public void incPassNumber() {
        ++mPassNumber;
    }
}
