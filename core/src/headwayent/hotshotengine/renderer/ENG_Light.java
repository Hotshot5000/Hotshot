/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Degree;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Plane;
import headwayent.hotshotengine.ENG_PlaneBoundedVolume;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.ENG_Plane.Side;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_Frustum.FrustumPlane;
import headwayent.hotshotengine.renderer.ENG_RenderableImpl.Visitor;

import java.util.ArrayList;
import java.util.TreeMap;

@Deprecated
public class ENG_Light extends ENG_MovableObject {

    @Override
    public ENG_AxisAlignedBox getBoundingBox() {
        
        return box;
    }

    @Override
    public void getBoundingBox(ENG_AxisAlignedBox ret) {
        

    }

    @Override
    public float getBoundingRadius() {
        
        return 0;
    }

    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue) {
        

    }

    /// Defines the type of light
    public enum LightTypes {
        /// Directional lights simulate parallel light beams from a distant source, hence have direction but no position
        LT_DIRECTIONAL(0),
        /// Point light sources give off light equally in all directions, so require only position not direction
        LT_POINT(1),
        /// Spotlights simulate a cone of light from a source so require position and direction, plus extra values for falloff
        LT_SPOTLIGHT(2),
        /// Virtual point lights, used for Instant Radiosity (Global Illumination fake / approximation)
        LT_VPL(3),

        NUM_LIGHT_TYPES(4);

        private final int type;

        LightTypes(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static LightTypes getType(int type) {
            switch (type) {
                case 0:
                    return LT_DIRECTIONAL;
                case 1:
                    return LT_POINT;
                case 2:
                    return LT_SPOTLIGHT;
                case 3:
                    return LT_VPL;
                default:
                    throw new IllegalArgumentException("type: " + type + " is an invalid light type");
            }
        }

        public static LightTypes getType(String type) {
            if (type.equalsIgnoreCase("directional")) {
                return LT_DIRECTIONAL;
            } else if (type.equalsIgnoreCase("point")) {
                return LT_POINT;
            } else if (type.equalsIgnoreCase("spotlight")) {
                return LT_SPOTLIGHT;
            }
            throw new IllegalArgumentException(type + " is an invalid light type");
        }
    }

    private static final ENG_AxisAlignedBox box = new ENG_AxisAlignedBox();

    protected LightTypes mLightType = LightTypes.LT_POINT;
    protected final ENG_Vector4D mPosition = new ENG_Vector4D();
    protected final ENG_ColorValue mDiffuse = new ENG_ColorValue(ENG_ColorValue.WHITE);
    protected final ENG_ColorValue mSpecular = new ENG_ColorValue(ENG_ColorValue.BLACK);

    protected final ENG_Vector4D mDirection = new ENG_Vector4D(ENG_Math.VEC4_Z_UNIT);

    protected final ENG_Radian mSpotOuter = new ENG_Radian(new ENG_Degree(40.0f));
    protected final ENG_Radian mSpotInner = new ENG_Radian(new ENG_Degree(30.0f));
    protected float mSpotFalloff = 1.0f;
    protected float mRange = 100000.0f;
    protected float mAttenuationConst = 1.0f;
    protected float mAttenuationLinear;
    protected float mAttenuationQuad;
    protected float mPowerScale = 1.0f;
    protected int mIndexInFrame;
    protected boolean mOwnShadowFarDist;
    protected float mShadowFarDist;
    protected float mShadowFarDistSquared;

    protected float mShadowNearClipDist = -1.0f;
    protected float mShadowFarClipDist = -1.0f;

    protected final ENG_Vector4D mDerivedPosition = new ENG_Vector4D();
    protected final ENG_Vector4D mDerivedDirection = new ENG_Vector4D(ENG_Math.VEC4_Z_UNIT);
    protected final ENG_Vector4D mDerivedCamRelativePosition = new ENG_Vector4D();

    protected boolean mDerivedCamRelativeDirty;
    protected ENG_Camera mCameraToBeRelativeTo;

    protected static String msMovableType;

    protected final ENG_PlaneBoundedVolume mNearClipVolume = new ENG_PlaneBoundedVolume();
    protected final ArrayList<ENG_PlaneBoundedVolume> mFrustumClipVolumes =
            new ArrayList<>();

    protected boolean mDerivedTransformDirty;

    protected ENG_ShadowCameraSetup mCustomShadowCameraSetup;

    protected final TreeMap<ENG_Short, ENG_Vector4D> mCustomParameters =
            new TreeMap<>();

    private final ENG_Quaternion parentOrientation = new ENG_Quaternion();
    private final ENG_Vector4D parentPosition = new ENG_Vector4D();

    public float tempSquareDist;

    public ENG_Light() {

    }

    public ENG_Light(String name) {
        super(name);
    }

    public void setType(LightTypes l) {
        mLightType = l;
    }

    public LightTypes getType() {
        return mLightType;
    }

    public void setPosition(float x, float y, float z) {
        mPosition.set(x, y, z);
        mDerivedTransformDirty = true;
        mDerivedCamRelativeDirty = true;
    }

    public void setPosition(ENG_Vector4D v) {
        mPosition.set(v);
        mDerivedTransformDirty = true;
        mDerivedCamRelativeDirty = true;
    }

    public void getPosition(ENG_Vector4D ret) {
        ret.set(mPosition);
    }

    public ENG_Vector4D getPosition() {
        return mPosition;
    }

    public ENG_Vector4D getPositionCopy() {
        return new ENG_Vector4D(mPosition);
    }

    public void setDirection(float x, float y, float z) {
        mDirection.set(x, y, z);
        mDerivedTransformDirty = true;
    }

    public void setDirection(ENG_Vector4D v) {
        mDirection.set(v);
        mDerivedTransformDirty = true;
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

    public void setSpotlightRange(ENG_Radian innerAngle, ENG_Radian outerAngle,
                                  float falloff) {
        if (mLightType != LightTypes.LT_SPOTLIGHT) {
            throw new ENG_InvalidFieldStateException(
                    "setSpotlightRange is only valid for spotlights.");
        }
        mSpotInner.set(innerAngle);
        mSpotOuter.set(outerAngle);
        mSpotFalloff = falloff;
    }

    public void setSpotlightRange(float innerAngle, float outerAngle,
                                  float falloff) {
        if (mLightType != LightTypes.LT_SPOTLIGHT) {
            throw new ENG_InvalidFieldStateException(
                    "setSpotlightRange is only valid for spotlights.");
        }
        mSpotInner.set(innerAngle);
        mSpotOuter.set(outerAngle);
        mSpotFalloff = falloff;
    }

    public void setSpotlightInnerAngle(ENG_Radian r) {
        mSpotInner.set(r);
    }

    public void setSpotlightInnerAngle(float r) {
        mSpotInner.set(r);
    }

    public void setSpotlightOuterAngle(ENG_Radian r) {
        mSpotOuter.set(r);
    }

    public void setSpotlightOuterAngle(float r) {
        mSpotOuter.set(r);
    }

    public void setSpotLightFalloff(float f) {
        mSpotFalloff = f;
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
    }

    public void setDiffuseColour(ENG_ColorValue diffuse) {
        mDiffuse.set(diffuse);
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
    }

    public void setSpecularColour(ENG_ColorValue diffuse) {
        mSpecular.set(diffuse);
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
    }

    public float getPowerScale() {
        return mPowerScale;
    }

    protected void update() {
        if (mDerivedTransformDirty) {
            if (mParentNode != null) {
                mParentNode._getDerivedOrientation(parentOrientation);
                mParentNode._getDerivedPosition(parentPosition);
                parentOrientation.mul(mPosition, mDerivedPosition);
                mDerivedPosition.addInPlace(parentPosition);
                parentOrientation.mul(mDirection, mDerivedDirection);
            } else {
                mDerivedPosition.set(mPosition);
                mDerivedDirection.set(mDirection);
            }
            mDerivedTransformDirty = false;
        }
        if ((mCameraToBeRelativeTo != null) && (mDerivedCamRelativeDirty)) {
            mDerivedPosition.sub(mCameraToBeRelativeTo.getDerivedPosition(),
                    mDerivedCamRelativePosition);
            mDerivedCamRelativeDirty = false;
        }
    }

    public void _notifyAttached(ENG_Node parent, boolean isTagPoint) {
        mDerivedTransformDirty = true;
        super._notifyAttached(parent, isTagPoint);
    }

    public void _notifyMoved() {
        mDerivedTransformDirty = true;
        super._notifyMoved();
    }

    /** @noinspection deprecation*/
    public String getMovableType() {
        return ENG_LightFactory.FACTORY_TYPE_NAME;
    }

    public void getDerivedPosition(ENG_Vector4D ret) {
        getDerivedPosition(false, ret);
    }

    public ENG_Vector4D getDerivedPosition() {
        return getDerivedPosition(false);
    }

    public ENG_Vector4D getDerivedPositionCopy() {
        return getDerivedPositionCopy(false);
    }

    public void getDerivedPosition(boolean cameraRelative, ENG_Vector4D ret) {
        update();
        if ((cameraRelative) && (mCameraToBeRelativeTo != null)) {
            ret.set(mDerivedCamRelativePosition);
        } else {
            ret.set(mDerivedPosition);
        }
    }

    public ENG_Vector4D getDerivedPosition(boolean cameraRelative) {
        update();
        if ((cameraRelative) && (mCameraToBeRelativeTo != null)) {
            return mDerivedCamRelativePosition;
        } else {
            return mDerivedPosition;
        }
    }

    public ENG_Vector4D getDerivedPositionCopy(boolean cameraRelative) {
        update();
        if ((cameraRelative) && (mCameraToBeRelativeTo != null)) {
            return new ENG_Vector4D(mDerivedCamRelativePosition);
        } else {
            return new ENG_Vector4D(mDerivedPosition);
        }
    }

    public void getDerivedDirection(ENG_Vector4D ret) {
        ret.set(mDerivedDirection);
    }

    public ENG_Vector4D getDerivedDirection() {
        return mDerivedDirection;
    }

    public ENG_Vector4D getDerivedDirectionCopy() {
        return new ENG_Vector4D(mDerivedDirection);
    }

    public void getAs4DVector(ENG_Vector4D ret) {
        getAs4DVector(false, ret);
    }

    public void getAs4DVector(boolean cameraRelativeIfSet, ENG_Vector4D ret) {
        if (mLightType == LightTypes.LT_DIRECTIONAL) {
            getDerivedDirection(ret);
            ret.invertInPlace();
            ret.w = 0.0f;
        } else {
            getDerivedPosition(cameraRelativeIfSet, ret);
            ret.w = 1.0f;
        }
    }

    public ENG_Vector4D getAs4DVector() {
        return getAs4DVector(false);
    }

    public ENG_Vector4D getAs4DVector(boolean cameraRelativeIfSet) {
        ENG_Vector4D ret = new ENG_Vector4D();
        getAs4DVector(cameraRelativeIfSet, ret);
        return ret;
    }

    public void _calcTempSquareDist(ENG_Vector4D worldPos) {
        if (mLightType == LightTypes.LT_DIRECTIONAL) {
            tempSquareDist = 0.0f;
        } else {
            tempSquareDist = worldPos.subAsVec(getDerivedPosition()).squaredLength();
        }
    }

    public ENG_PlaneBoundedVolume _getNearClipVolume(ENG_Camera cam) {
        mNearClipVolume.planes.clear();
        mNearClipVolume.outside = Side.NEGATIVE_SIDE;

        float n = cam.getNearClipDistance();

        ENG_Vector4D lightPos = getAs4DVector();

        ENG_Vector4D lightPos3 = new ENG_Vector4D(lightPos.x, lightPos.y, lightPos.z,
                1.0f);
        ENG_Vector4D eyeSpaceLight = new ENG_Vector4D();
        cam.getViewMatrixCopy().transform(lightPos, eyeSpaceLight);

        float d = eyeSpaceLight.dotProduct(
                new ENG_Vector4D(0.0f, 0.0f, -1.0f, -n));

        if ((d > 1e-6) || (d < -1e-6)) {
            ENG_Vector4D[] corner = cam.getWorldSpaceCorners();
            int winding = ((d < 0) ^ cam.isReflected()) ? +1 : -1;

            ENG_Vector4D normal = new ENG_Vector4D();
            ENG_Vector4D lightDir = new ENG_Vector4D();

            for (int i = 0; i < 4; ++i) {
                ENG_Vector4D t = corner[i].mulAsVec(lightPos.w);
                lightPos3.mul(t, lightDir);

                corner[i].subAsVec(corner[(i + winding) % 4]).crossProduct(
                        lightDir, normal);
                normal.normalize();
                mNearClipVolume.planes.add(new ENG_Plane(normal, corner[i]));
            }

            normal.set(cam.getFrustumPlane(
                    FrustumPlane.FRUSTUM_PLANE_NEAR.getPlane()).normal);
            if (d < 0.0f) {
                normal.invertInPlace();
            }

            mNearClipVolume.planes.add(new ENG_Plane(normal, cam.getDerivedPosition()));

            if (mLightType != LightTypes.LT_DIRECTIONAL) {
                mNearClipVolume.planes.add(new ENG_Plane(normal.invert(), lightPos3));
            }
        } else {
            mNearClipVolume.planes.add(new ENG_Plane(ENG_Math.VEC4_Z_UNIT, -n));
            mNearClipVolume.planes.add(new ENG_Plane(ENG_Math.VEC4_Z_UNIT.invert(), n));
        }
        return mNearClipVolume;
    }

    public ArrayList<ENG_PlaneBoundedVolume> _getFrustumClipVolumes(ENG_Camera cam) {
        ENG_Vector4D lightPos = getAs4DVector();
        ENG_Vector4D lightPos3 = new ENG_Vector4D(lightPos.x, lightPos.y, lightPos.z,
                1.0f);

        ENG_Vector4D[] clockwiseVerts = new ENG_Vector4D[4];
        ENG_Vector4D[] corner = cam.getWorldSpaceCorners();

        int windingPt0 = cam.isReflected() ? 1 : 0;
        int windingPt1 = cam.isReflected() ? 0 : 1;

        boolean infiniteViewDistance = (cam.getFarClipDistance() == 0.0f);

        ENG_Vector4D[] notSoFarCorners = new ENG_Vector4D[4];

        if (infiniteViewDistance) {
            ENG_Vector4D camPosition = cam.getRealPosition();
            notSoFarCorners[0] = corner[0].addAsPt(corner[0]).subAsPt(camPosition);
            notSoFarCorners[1] = corner[1].addAsPt(corner[1]).subAsPt(camPosition);
            notSoFarCorners[2] = corner[2].addAsPt(corner[2]).subAsPt(camPosition);
            notSoFarCorners[3] = corner[3].addAsPt(corner[3]).subAsPt(camPosition);
        }

        mFrustumClipVolumes.clear();
        ENG_Vector4D planeVec = new ENG_Vector4D();
        for (FrustumPlane fp : FrustumPlane.values()) {
            int n = fp.getPlane();
            if ((infiniteViewDistance) &&
                    (n == FrustumPlane.FRUSTUM_PLANE_FAR.getPlane())) {
                continue;
            }
            ENG_Plane plane = cam.getFrustumPlane(n);
            planeVec.set(plane.normal.x, plane.normal.y, plane.normal.z, plane.d);

            float d = planeVec.dotProduct(lightPos);

            if (d < -1e-06) {
                ENG_PlaneBoundedVolume vol = new ENG_PlaneBoundedVolume();
                mFrustumClipVolumes.add(vol);


                switch (fp) {
                    case FRUSTUM_PLANE_NEAR:
                        clockwiseVerts[0] = corner[3];
                        clockwiseVerts[1] = corner[2];
                        clockwiseVerts[2] = corner[1];
                        clockwiseVerts[3] = corner[0];
                        break;
                    case FRUSTUM_PLANE_FAR:
                        clockwiseVerts[0] = corner[7];
                        clockwiseVerts[1] = corner[6];
                        clockwiseVerts[2] = corner[5];
                        clockwiseVerts[3] = corner[4];
                        break;
                    case FRUSTUM_PLANE_LEFT:
                        clockwiseVerts[0] = infiniteViewDistance ? notSoFarCorners[1] : corner[5];
                        clockwiseVerts[1] = corner[1];
                        clockwiseVerts[2] = corner[2];
                        clockwiseVerts[3] = infiniteViewDistance ? notSoFarCorners[2] : corner[6];
                        break;
                    case FRUSTUM_PLANE_RIGHT:
                        clockwiseVerts[0] = infiniteViewDistance ? notSoFarCorners[3] : corner[7];
                        clockwiseVerts[1] = corner[3];
                        clockwiseVerts[2] = corner[0];
                        clockwiseVerts[3] = infiniteViewDistance ? notSoFarCorners[0] : corner[4];
                        break;
                    case FRUSTUM_PLANE_TOP:
                        clockwiseVerts[0] = infiniteViewDistance ? notSoFarCorners[0] : corner[4];
                        clockwiseVerts[1] = corner[0];
                        clockwiseVerts[2] = corner[1];
                        clockwiseVerts[3] = infiniteViewDistance ? notSoFarCorners[1] : corner[5];
                        break;
                    case FRUSTUM_PLANE_BOTTOM:
                        clockwiseVerts[0] = infiniteViewDistance ? notSoFarCorners[2] : corner[6];
                        clockwiseVerts[1] = corner[2];
                        clockwiseVerts[2] = corner[3];
                        clockwiseVerts[3] = infiniteViewDistance ? notSoFarCorners[3] : corner[7];
                        break;
                }

                ENG_Vector4D normal = new ENG_Vector4D();
                ENG_Vector4D lightDir;

                int infiniteViewDistanceInt = infiniteViewDistance ? 1 : 0;
                for (int i = 0; i < (4 - infiniteViewDistanceInt); ++i) {
                    lightDir = lightPos3.subAsVec(clockwiseVerts[i].mulAsVec(lightPos.w));
                    ENG_Vector4D edgeDir =
                            clockwiseVerts[(i + windingPt1) % 4].subAsVec(
                                    clockwiseVerts[(i + windingPt0) % 4]);
                    edgeDir.crossProduct(lightDir, normal);
                    normal.normalize();
                    vol.planes.add(new ENG_Plane(normal, clockwiseVerts[i]));
                }

                vol.planes.add(new ENG_Plane(plane.normal.invert(), plane.d));

                if (mLightType != LightTypes.LT_DIRECTIONAL) {
                    vol.planes.add(new ENG_Plane(plane.normal, lightPos3));
                }
            }
        }
        return mFrustumClipVolumes;
    }

    public int getTypeFlags() {
        return ENG_SceneManager.LIGHT_TYPE_MASK;
    }

    public int _getIndexInFrame() {
        return mIndexInFrame;
    }

    public void _setIndexInFrame(int index) {
        mIndexInFrame = index;
    }

    public void setShadowNearClipDistance(float nearClip) {
        mShadowNearClipDist = nearClip;
    }

    public float getShadowNearClipDistance() {
        return mShadowNearClipDist;
    }

    public void setShadowFarClipDistance(float farClip) {
        mShadowFarClipDist = farClip;
    }

    public float getShadowFarClipDistance() {
        return mShadowFarClipDist;
    }

    public void setCustomShadowCameraSetup(ENG_ShadowCameraSetup customShadowSetup) {
        mCustomShadowCameraSetup = customShadowSetup;
    }

    public ENG_ShadowCameraSetup getCustomShadowCameraSetup() {
        return mCustomShadowCameraSetup;
    }

    public void resetCustomShadowCameraSetup() {
        mCustomShadowCameraSetup = null;
    }

    public void setShadowFarDistance(float distance) {
        mOwnShadowFarDist = true;
        mShadowFarDist = distance;
        mShadowFarDistSquared = distance * distance;
    }

    public void resetShadowFarDistance() {
        mOwnShadowFarDist = false;
    }

    public float getShadowFarDistance() {
        if (mOwnShadowFarDist) {
            return mShadowFarDist;
        } else {
            return mManager.getShadowFarDistance();
        }
    }

    public float getShadowFarDistanceSquared() {
        if (mOwnShadowFarDist) {
            return mShadowFarDistSquared;
        } else {
            return mManager.getShadowFarDistanceSquared();
        }
    }

    public void _setCameraRelative(ENG_Camera cam) {
        mCameraToBeRelativeTo = cam;
        mDerivedCamRelativeDirty = true;
    }

    public float _deriveShadowNearClipDistance(ENG_Camera maincam) {
        if (mShadowNearClipDist > 0.0f) {
            return mShadowNearClipDist;
        } else {
            return maincam.getNearClipDistance();
        }
    }

    public float _deriveShadowFarClipDistance(ENG_Camera maincam) {
        if (mShadowFarClipDist >= 0.0f) {
            return mShadowFarClipDist;
        } else {
            if (mLightType == LightTypes.LT_DIRECTIONAL) {
                return 0.0f;
            } else {
                return mRange;
            }
        }
    }

    public void setCustomParameter(short index, ENG_Vector4D value) {
        mCustomParameters.put(new ENG_Short(index), value);
    }

    public void setCustomParameter(ENG_Short index, ENG_Vector4D value) {
        mCustomParameters.put(index, value);
    }

    public ENG_Vector4D getCustomParameter(short index) {
        ENG_Vector4D ret = mCustomParameters.get(new ENG_Short(index));
        if (ret == null) {
            throw new IllegalArgumentException(
                    "Parameter at the given index was not found.");
        }
        return ret;
    }

    public ENG_Vector4D getCustomParameter(ENG_Short index) {
        ENG_Vector4D ret = mCustomParameters.get(index);
        if (ret == null) {
            throw new IllegalArgumentException(
                    "Parameter at the given index was not found.");
        }
        return ret;
    }

    public void _updateCustomGpuParameter(short index,
                                          ENG_AutoConstantEntry constantEntry, ENG_GpuProgramParameters params) {
        ENG_Vector4D v = mCustomParameters.get(new ENG_Short(index));
        if (v != null) {
            params._writeRawConstant(
                    constantEntry.physicalIndex, v, constantEntry.elementCount);
        }
    }

    @Override
    public void visitRenderables(Visitor visitor, boolean debugRenderables) {
        

    }

}
