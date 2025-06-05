/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 9:24 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Plane;
import headwayent.hotshotengine.ENG_PlaneBoundedVolume;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Ray;
import headwayent.hotshotengine.ENG_Sphere;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.renderer.ENG_Common.PolygonMode;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_CameraNativeWrapper;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

import java.util.ArrayList;


public class ENG_Camera extends ENG_Frustum {

    @Override
    public long getPointer() {
        return wrapper.getPtr();
    }

    public interface Listener {

        void cameraPreRenderScene(ENG_Camera cam);

        void cameraPostRenderScene(ENG_Camera cam);

        void cameraDestroyed(ENG_Camera cam);
    }

    //protected String mName;
    protected final ENG_CameraNativeWrapper wrapper = new ENG_CameraNativeWrapper();
    protected final ENG_SceneManager mSceneMgr;
    protected final ENG_Quaternion mOrientation = new ENG_Quaternion();
    protected final ENG_Vector4D mPosition = new ENG_Vector4D(true);

    protected final ENG_Vector4D mDerivedPosition = new ENG_Vector4D(true);
    protected final ENG_Quaternion mDerivedOrientation = new ENG_Quaternion();

    protected final ENG_Vector4D mRealPosition = new ENG_Vector4D(true);
    protected final ENG_Quaternion mRealOrientation = new ENG_Quaternion();

    protected boolean mYawFixed;
    protected final ENG_Vector4D mYawFixedAxis = new ENG_Vector4D();

    protected PolygonMode mSceneDetail;

    /// Stored number of visible faces in the last render
    protected int mVisFacesLastRender;

    /// Stored number of visible faces in the last render
    protected int mVisBatchesLastRender;

    /// Shared class-level name for Movable type
    protected static String msMovableType;

    /// SceneNode which this Camera will automatically track
    protected ENG_SceneNode mAutoTrackTarget;
    /// Tracking offset for fine tuning
    protected final ENG_Vector4D mAutoTrackOffset = new ENG_Vector4D();

    // Scene LOD factor used to adjust overall LOD
    protected float mSceneLodFactor;
    /// Inverted scene LOD factor, can be used by Renderables to adjust their LOD
    protected float mSceneLodFactorInv;

    /**
     * Viewing window.
     *
     * @remarks Generalize camera class for the case, when viewing frustum doesn't cover all viewport.
     */
    protected float mWLeft, mWTop, mWRight, mWBottom;
    /// Is viewing window used.
    protected boolean mWindowSet;
    /// Windowed viewport clip planes
    protected final ArrayList<ENG_Plane> mWindowClipPlanes = new ArrayList<>();
    // Was viewing window changed.
    protected boolean mRecalcWindow;
    /// The last viewport to be added using this camera
    protected ENG_Viewport mLastViewport;
    /**
     * Whether aspect ratio will automatically be recalculated
     * when a viewport changes its size
     */
    protected boolean mAutoAspectRatio;
    /// Custom culling frustum
    protected ENG_Frustum mCullFrustum;
    /// Whether or not the rendering distance of objects should take effect for this camera
    protected boolean mUseRenderingDistance;
    /// Camera to use for LOD calculation
    protected ENG_Camera mLodCamera;

    protected final ArrayList<Listener> mListeners = new ArrayList<>();

    private final ENG_Vector4D dir = new ENG_Vector4D();
    private final ENG_Vector4D rdir = new ENG_Vector4D();
    private final ENG_Vector4D up = new ENG_Vector4D();

    private final ENG_Vector4D v0 = new ENG_Vector4D();
    private final ENG_Vector4D v1 = new ENG_Vector4D();
    private final ENG_Vector4D axis = new ENG_Vector4D();

    private final ENG_Vector4D lookAtTempVec = new ENG_Vector4D();

    private final ENG_Vector4D moveRelativeTempVec = new ENG_Vector4D();

    private final ENG_Vector4D zAdjustVec = new ENG_Vector4D();
    private final ENG_Vector4D xVec = new ENG_Vector4D();
    private final ENG_Vector4D yVec = new ENG_Vector4D();

    private final ENG_Vector4D xAxis = new ENG_Vector4D();
    private final ENG_Vector4D yAxis = new ENG_Vector4D();
    private final ENG_Vector4D zAxis = new ENG_Vector4D();

    private final ENG_Matrix4 mat = new ENG_Matrix4();

    private final ENG_Quaternion targetWorldOrientation = new ENG_Quaternion();
    private final ENG_Quaternion rotQuat = new ENG_Quaternion();

    private final ENG_Vector4D temp = new ENG_Vector4D();

    private final ENG_Vector4D rotXAxis = new ENG_Vector4D();
    private final ENG_Vector4D rotYAxis = new ENG_Vector4D();
    private final ENG_Vector4D rotZAxis = new ENG_Vector4D();

    private final ENG_Quaternion qnorm = new ENG_Quaternion();
    private final ENG_Quaternion q = new ENG_Quaternion();

    private final ENG_Vector4D autoTrackTemp = new ENG_Vector4D();

    private final ENG_Matrix4 inverseVP = new ENG_Matrix4();
    private final ENG_Matrix4 inverseVPTemp0 = new ENG_Matrix4();
    private final ENG_Matrix4 inverseVPTemp1 = new ENG_Matrix4();
    private final ENG_Vector4D nearPoint = new ENG_Vector4D();
    private final ENG_Vector4D midPoint = new ENG_Vector4D();

    private final ENG_Vector4D rayOrigin = new ENG_Vector4D();
    private final ENG_Vector4D rayTarget = new ENG_Vector4D();
    private final ENG_Vector4D rayDirection = new ENG_Vector4D();

    private final ENG_Vector4D tempNative = new ENG_Vector4D();
    private final ENG_Boolean projectionMatrixSet = new ENG_Boolean(false);
    private final ENG_Boolean viewMatrixSet = new ENG_Boolean(false);

    public ENG_Camera(String name, ENG_SceneManager sm, long cameraPtr) {
        super(name, true);
        wrapper.setPtr(cameraPtr);
        setWrapper(wrapper);
        setNativePointer(true);
        mSceneMgr = sm;
        mOrientation.set(ENG_Math.QUAT_IDENTITY);
        mPosition.set(ENG_Math.PT4_ZERO);
        mSceneDetail = PolygonMode.PM_SOLID;
        mSceneLodFactor = 1.0f;
        mSceneLodFactorInv = 1.0f;
        mUseRenderingDistance = true;

        // Reasonable defaults to camera params
        mFOVy = ENG_Math.PI / 4.0f;
        mNearDist = 100.0f;
        mFarDist = 100000.0f;
        mAspect = 1.33333333333333f;
        mProjType = ProjectionType.PT_PERSPECTIVE;
        // Default to fixed yaw, like freelook since most people expect this
        setFixedYawAxis(true, ENG_Math.VEC4_Y_UNIT);

        //We must update here since it's no longer happening in the superclass!
        updateView();
        updateFrustum();


        invalidateFrustum();
        invalidateView();

        // Init matrices
        mViewMatrix = new ENG_Matrix4(ENG_Math.MAT4_ZERO);
        mProjMatrixRS = new ENG_Matrix4(ENG_Math.MAT4_ZERO);

        mParentNode = null;

        // no reflection
        mReflect = false;

        visible = false;
    }

    protected boolean isViewOutOfDate() {
        if (mParentNode != null) {
            if ((mRecalcView) ||
                    (mParentNode._getDerivedOrientation().notEquals(mLastParentOrientation)) ||
                    (mParentNode._getDerivedPosition().notEquals(mLastParentPosition))) {
                mParentNode._getDerivedOrientation(mLastParentOrientation);
                mParentNode._getDerivedPosition(mLastParentPosition);
                mLastParentOrientation.mul(mOrientation, mRealOrientation);
                mLastParentOrientation.mul(mPosition, mRealPosition);
                mRealPosition.addInPlace(mLastParentPosition);
                mRecalcView = true;
                mRecalcWindow = true;
            }
        } else {
            //	if (mRealOrientation != null && mOrientation != null) {
            mRealOrientation.set(mOrientation);
            //	}
            //	if (mRealPosition != null && mPosition != null) {
            mRealPosition.set(mPosition);
            //	}
        }

        if (mRecalcView) {
            if (mReflect) {
                mRealOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, dir);
                dir.reflect(mReflectPlane.normal, rdir);
                mRealOrientation.mul(ENG_Math.VEC4_Y_UNIT, up);
                dir.getRotationTo(rdir, up, mDerivedOrientation, v0, v1, axis);
                mDerivedOrientation.mulInPlace(mRealOrientation);

                mReflectMatrix.transformAffine(mRealPosition, mDerivedPosition);
            } else {
                //	if (mDerivedOrientation != null && mRealOrientation != null) {
                mDerivedOrientation.set(mRealOrientation);
                //	}
                //	if (mDerivedPosition != null && mRealPosition != null) {
                mDerivedPosition.set(mRealPosition);
                //	}
            }
        }
        return mRecalcView;
    }

    public void invalidateView() {
        mRecalcWindow = true;
        super.invalidateView();
    }

    public void invalidateFrustum() {
        mRecalcWindow = true;
        super.invalidateFrustum();
    }

    public void _renderScene(ENG_Viewport vp, boolean includeOverlays) {
        int len = mListeners.size();
        for (int i = 0; i < len; ++i) {
            mListeners.get(i).cameraPreRenderScene(this);
        }

        mSceneMgr._renderScene(this, vp, includeOverlays);

        for (int i = 0; i < len; ++i) {
            mListeners.get(i).cameraPostRenderScene(this);
        }
    }

    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void removeListener(Listener l) {
        mListeners.remove(l);
    }

    public void setFixedYawAxis(boolean useFixed, ENG_Vector4D fixedAxis) {
        mYawFixed = useFixed;
        mYawFixedAxis.set(fixedAxis);
    }

    public void _notifyRenderedFaces(int numfaces) {
        mVisFacesLastRender = numfaces;
    }

    public void _notifyRenderedBatches(int numbatches) {
        mVisBatchesLastRender = numbatches;
    }

    public int _getNumRenderedFaces() {
        return mVisFacesLastRender;
    }

    public int _getNumRenderedBatches() {
        return mVisBatchesLastRender;
    }

    public ENG_Quaternion getOrientation() {
        return mOrientation;
    }

    public ENG_Quaternion getOrientationCopy() {
        return new ENG_Quaternion(mOrientation);
    }

    public void getOrientation(ENG_Quaternion ret) {
        ret.set(mOrientation);
    }

    public void setOrientation(ENG_Quaternion q) {
        mOrientation.set(q);
        mOrientation.normalize();
        invalidateView();
    }

    public void getDerivedPosition(ENG_Vector4D ret) {
        updateView();
        ret.set(mDerivedPosition);
    }

    public void getViewMatrix(ENG_Matrix4 ret) {
        if (mCullFrustum != null) {
            mCullFrustum.getViewMatrix(ret);
        } else {
            super.getViewMatrix(ret);
        }
    }

    public ENG_Matrix4 getViewMatrix() {
        if (mCullFrustum != null) {
            return mCullFrustum.getViewMatrix();
        } else {
            return super.getViewMatrix();
        }
    }

    public ENG_Matrix4 getViewMatrix(boolean ownFrustumOnly) {
        if (ownFrustumOnly) {
            return super.getViewMatrix();
        } else {
            return getViewMatrix();
        }
    }

    public void getViewMatrix(boolean ownFrustumOnly, ENG_Matrix4 ret) {
        if (ownFrustumOnly) {
            super.getViewMatrix(ret);
        } else {
            getViewMatrix(ret);
        }
    }

    public ENG_Quaternion getDerivedOrientation() {
        updateView();
        return mDerivedOrientation;
    }

    public ENG_Quaternion getDerivedOrientationCopy() {
        updateView();
        return new ENG_Quaternion(mDerivedOrientation);
    }

    public void getDerivedOrientation(ENG_Quaternion ret) {
        updateView();
        ret.set(mDerivedOrientation);
    }

    public ENG_Vector4D getDerivedPositionCopy() {
        updateView();
        return new ENG_Vector4D(mDerivedPosition);
    }

    public ENG_Vector4D getDerivedPosition() {
        updateView();
        return mDerivedPosition;
    }

    public ENG_Vector4D getDerivedDirection() {
        updateView();
        return mDerivedOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT);
    }

    public void getDerivedDirection(ENG_Vector4D ret) {
        updateView();
        mDerivedOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, ret);
    }

    public ENG_Vector4D getDerivedUp() {
        updateView();
        return mDerivedOrientation.mul(ENG_Math.VEC4_Y_UNIT);
    }

    public void getDerivedUp(ENG_Vector4D ret) {
        updateView();
        mDerivedOrientation.mul(ENG_Math.VEC4_Y_UNIT, ret);
    }

    public ENG_Vector4D getDerivedRight() {
        updateView();
        return mDerivedOrientation.mul(ENG_Math.VEC4_X_UNIT);
    }

    public void getDerivedRight(ENG_Vector4D ret) {
        updateView();
        mDerivedOrientation.mul(ENG_Math.VEC4_X_UNIT, ret);
    }

    public ENG_Quaternion getRealOrientationCopy() {
        updateView();
        return new ENG_Quaternion(mRealOrientation);
    }

    public ENG_Quaternion getRealOrientation() {
        updateView();
        return mRealOrientation;
    }

    public void getRealOrientation(ENG_Quaternion ret) {
        updateView();
        ret.set(mRealOrientation);
    }

    public ENG_Vector4D getRealPositionCopy() {
        updateView();
        return new ENG_Vector4D(mRealPosition);
    }

    public ENG_Vector4D getRealPosition() {
        updateView();
        return mRealPosition;
    }

    public void getRealPosition(ENG_Vector4D ret) {
        updateView();
        ret.set(mRealPosition);
    }

    public ENG_Vector4D getRealDirection() {
        updateView();
        return mRealOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT);
    }

    public void getRealDirection(ENG_Vector4D ret) {
        updateView();
        mRealOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, ret);
    }

    public ENG_Vector4D getRealUp() {
        updateView();
        return mRealOrientation.mul(ENG_Math.VEC4_Y_UNIT);
    }

    public void getRealUp(ENG_Vector4D ret) {
        updateView();
        mRealOrientation.mul(ENG_Math.VEC4_Y_UNIT, ret);
    }

    public ENG_Vector4D getRealRight() {
        updateView();
        return mRealOrientation.mul(ENG_Math.VEC4_X_UNIT);
    }

    public void getRealRight(ENG_Vector4D ret) {
        updateView();
        mRealOrientation.mul(ENG_Math.VEC4_X_UNIT, ret);
    }

    public String getMovableType() {
        return msMovableType;
    }

    public void setAutoTracking(boolean enabled, ENG_SceneNode target,
                                ENG_Vector4D offset) {
        if (enabled) {
            if (target == null) {
                throw new NullPointerException(
                        "target cannot be a null pointer if tracking is enabled");
            }
            mAutoTrackTarget = target;
            mAutoTrackOffset.set(offset);
        } else {
            mAutoTrackTarget = null;
        }
    }

    public void _autoTrack() {
        if (mAutoTrackTarget != null) {
            mAutoTrackTarget._getDerivedPosition(autoTrackTemp);
            autoTrackTemp.addInPlace(mAutoTrackOffset);
        }
    }

    public void setLodBias(float factor) {
        if (factor < 0.0f) {
            throw new IllegalArgumentException("Bias factor must be > 0!");
        }
        mSceneLodFactor = factor;
        mSceneLodFactorInv = 1.0f / factor;
    }

    public float getLodBias() {
        return mSceneLodFactor;
    }

    public float _getLodBiasInverse() {
        return mSceneLodFactorInv;
    }

    public void setLodCam(ENG_Camera lod) {
        if (lod == this) {
            mLodCamera = null;
        } else {
            mLodCamera = lod;
        }
    }

    public ENG_Camera getLodCamera() {
        return (mLodCamera != null) ? mLodCamera : this;
    }

    public ENG_Ray getCameraToViewportRay(float screenX, float screenY) {
        ENG_Ray ret = new ENG_Ray();
        getCameraToViewportRay(screenX, screenY, ret);
        return ret;
    }

    public void getCameraToViewportRay(float screenX, float screenY, ENG_Ray outRay) {
        getProjectionMatrix(inverseVPTemp0);
        getViewMatrix(true, inverseVPTemp1);

        inverseVPTemp0.concatenate(inverseVPTemp1, inverseVP);
        inverseVP.invert();

        float nx = (2.0f * screenX) - 1.0f;
        float ny = 1.0f - (2.0f * screenY);
        nearPoint.set(nx, ny, -1.0f);
        midPoint.set(nx, ny, 0.0f);

        inverseVP.transform(nearPoint, rayOrigin);
        inverseVP.transform(midPoint, rayTarget);

        rayTarget.sub(rayOrigin, rayDirection);
        rayDirection.normalize();

        outRay.origin.set(rayOrigin);
        outRay.dir.set(rayDirection);
    }

    public ENG_PlaneBoundedVolume getCameraToViewportVolume(float screenLeft, float screenTop,
                                                            float screenRight, float screenBottom) {
        return getCameraToViewportVolume(screenLeft, screenTop, screenRight, screenBottom,
                false);
    }

    public ENG_PlaneBoundedVolume getCameraToViewportVolume(float screenLeft, float screenTop,
                                                            float screenRight, float screenBottom, boolean includeFarPlane) {
        ENG_PlaneBoundedVolume ret = new ENG_PlaneBoundedVolume();
        getCameraToViewportVolume(screenLeft, screenTop, screenRight, screenBottom,
                ret, includeFarPlane);
        return ret;
    }

    public void getCameraToViewportVolume(float screenLeft, float screenTop,
                                          float screenRight, float screenBottom, ENG_PlaneBoundedVolume outVolume) {
        getCameraToViewportVolume(screenLeft, screenTop, screenRight, screenBottom,
                outVolume, false);
    }

    public void getCameraToViewportVolume(float screenLeft, float screenTop,
                                          float screenRight, float screenBottom, ENG_PlaneBoundedVolume outVolume,
                                          boolean includeFarPlane) {
        outVolume.planes.clear();

        if (mProjType == ProjectionType.PT_PERSPECTIVE) {
            // Use the corner rays to generate planes
            ENG_Ray ul = getCameraToViewportRay(screenLeft, screenTop);
            ENG_Ray ur = getCameraToViewportRay(screenRight, screenTop);
            ENG_Ray bl = getCameraToViewportRay(screenLeft, screenBottom);
            ENG_Ray br = getCameraToViewportRay(screenRight, screenBottom);

            ENG_Vector4D normal;

            normal = ul.dir.crossProduct(ur.dir);
            normal.normalize();
            outVolume.planes.add(new ENG_Plane(normal, getDerivedPositionCopy()));

            normal = ur.dir.crossProduct(br.dir);
            normal.normalize();
            outVolume.planes.add(new ENG_Plane(normal, getDerivedPositionCopy()));

            normal = br.dir.crossProduct(bl.dir);
            normal.normalize();
            outVolume.planes.add(new ENG_Plane(normal, getDerivedPositionCopy()));

            normal = bl.dir.crossProduct(ul.dir);
            normal.normalize();
            outVolume.planes.add(new ENG_Plane(normal, getDerivedPositionCopy()));
        } else {
            // ortho planes are parallel to frustum planes

            ENG_Ray ul = getCameraToViewportRay(screenLeft, screenTop);
            ENG_Ray br = getCameraToViewportRay(screenRight, screenBottom);

            updateFrustumPlanes();

            outVolume.planes.add(new ENG_Plane(
                    mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_TOP.getPlane()].normal,
                    ul.origin));
            outVolume.planes.add(new ENG_Plane(
                    mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_RIGHT.getPlane()].normal,
                    br.origin));
            outVolume.planes.add(new ENG_Plane(
                    mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_BOTTOM.getPlane()].normal,
                    br.origin));
            outVolume.planes.add(new ENG_Plane(
                    mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_LEFT.getPlane()].normal,
                    ul.origin));
        }

        outVolume.planes.add(getFrustumPlane(FrustumPlane.FRUSTUM_PLANE_NEAR.getPlane()));
        if (includeFarPlane) {
            outVolume.planes.add(getFrustumPlane(FrustumPlane.FRUSTUM_PLANE_FAR.getPlane()));
        }
    }

    public void setWindow(float Left, float Top, float Right, float Bottom) {
        mWLeft = Left;
        mWTop = Top;
        mWRight = Right;
        mWBottom = Bottom;

        mWindowSet = true;
        mRecalcWindow = true;
    }

    public void resetWindow() {
        mWindowSet = false;
    }

    public void setWindowImpl() {
        if ((!mWindowSet) || (!mRecalcWindow)) {
            return;
        }
        float[] param = new float[4];
        calcProjectionParameters(param);

        float vpWidth = param[2] - param[0];
        float vpHeight = param[1] - param[3];

        float wvpLeft = param[0] + mWLeft * vpWidth;
        float wvpRight = param[0] + mWRight * vpWidth;
        float wvpTop = param[1] - mWTop * vpHeight;
        float wvpBottom = param[1] - mWBottom * vpHeight;

        ENG_Vector4D vp_ul = new ENG_Vector4D(wvpLeft, wvpTop, -mNearDist, 1.0f);
        ENG_Vector4D vp_ur = new ENG_Vector4D(wvpRight, wvpTop, -mNearDist, 1.0f);
        ENG_Vector4D vp_bl = new ENG_Vector4D(wvpLeft, wvpBottom, -mNearDist, 1.0f);
        ENG_Vector4D vp_br = new ENG_Vector4D(wvpRight, wvpBottom, -mNearDist, 1.0f);

        ENG_Matrix4 inv = mViewMatrix.invertAffineRet();

        ENG_Vector4D vw_ul = inv.transformAffineRet(vp_ul);
        ENG_Vector4D vw_ur = inv.transformAffineRet(vp_ur);
        ENG_Vector4D vw_bl = inv.transformAffineRet(vp_bl);
        ENG_Vector4D vw_br = inv.transformAffineRet(vp_br);

        mWindowClipPlanes.clear();

        if (mProjType == ProjectionType.PT_PERSPECTIVE) {
            ENG_Vector4D position = getPositionForViewUpdate();
            mWindowClipPlanes.add(new ENG_Plane(position, vw_bl, vw_ul));
            mWindowClipPlanes.add(new ENG_Plane(position, vw_ul, vw_ur));
            mWindowClipPlanes.add(new ENG_Plane(position, vw_ur, vw_br));
            mWindowClipPlanes.add(new ENG_Plane(position, vw_br, vw_bl));
        } else {
            ENG_Vector4D x_axis = new ENG_Vector4D(inv.get(0, 0), inv.get(0, 1), inv.get(0, 2), 0.0f);
            ENG_Vector4D y_axis = new ENG_Vector4D(inv.get(1, 0), inv.get(1, 1), inv.get(1, 2), 0.0f);
            x_axis.normalize();
            y_axis.normalize();
            mWindowClipPlanes.add(new ENG_Plane(x_axis, vw_bl));
            mWindowClipPlanes.add(new ENG_Plane(x_axis.invert(), vw_ur));
            mWindowClipPlanes.add(new ENG_Plane(y_axis, vw_bl));
            mWindowClipPlanes.add(new ENG_Plane(y_axis.invert(), vw_ur));
        }

        mRecalcWindow = false;
    }

    public ArrayList<ENG_Plane> getWindowPlanes() {
        updateView();
        setWindowImpl();
        return mWindowClipPlanes;
    }

    public float getBoundingRadius() {
        return mNearDist * 1.5f;
    }

    public ENG_Vector4D getPositionForViewUpdate() {
        return mRealPosition;
    }

    public ENG_Quaternion getOrientationForViewUpdate() {
        return mRealOrientation;
    }

    public boolean getAutoAspectRatio() {
        return mAutoAspectRatio;
    }

    public void setAutoAspectRatio(boolean enabled) {
        wrapper.setAutoAspectRatio(enabled);
        mAutoAspectRatio = enabled;
    }

    public boolean isVisible(ENG_AxisAlignedBox bound, int[] culledBy) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, culledBy);
        } else {
            return super.isVisible(bound, culledBy);
        }
    }

    public boolean isVisible(ENG_Sphere bound, int[] culledBy) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, culledBy);
        } else {
            return super.isVisible(bound, culledBy);
        }
    }

    public boolean isVisible(ENG_Vector4D bound, int[] culledBy) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, culledBy);
        } else {
            return super.isVisible(bound, culledBy);
        }
    }

    public boolean isVisible(ENG_Vector3D bound, int[] culledBy) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, culledBy);
        } else {
            return super.isVisible(bound, culledBy);
        }
    }

    public boolean isVisible(ENG_AxisAlignedBox bound) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, null);
        } else {
            return super.isVisible(bound, null);
        }
    }

    public boolean isVisible(ENG_Sphere bound) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, null);
        } else {
            return super.isVisible(bound, null);
        }
    }

    public void isVisibleNative(ENG_Vector4D pos, ENG_Boolean ret, ENG_Boolean retSet) {
        ENG_NativeCalls.camera_isVisibleVec(this, pos, ret, retSet);
    }

    public void isVisibleNative(ENG_AxisAlignedBox box, ENG_Boolean ret, ENG_Boolean retSet) {
        ENG_NativeCalls.camera_isVisibleAxisAlignedBox(this, box, ret, retSet);
    }

    public boolean isVisible(ENG_Vector4D bound) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, null);
        } else {
            return super.isVisible(bound, null);
        }
    }

    public boolean isVisible(ENG_Vector3D bound) {
        if (mCullFrustum != null) {
            return mCullFrustum.isVisible(bound, null);
        } else {
            return super.isVisible(bound, null);
        }
    }

    public ENG_Vector4D[] getWorldSpaceCorners() {
        if (mCullFrustum != null) {
            return mCullFrustum.getWorldSpaceCorners();
        } else {
            return super.getWorldSpaceCorners();
        }
    }

    public ENG_Plane getFrustumPlane(int plane) {
        if (mCullFrustum != null) {
            return mCullFrustum.getFrustumPlane(plane);
        } else {
            return super.getFrustumPlane(plane);
        }
    }

    public boolean projectSphere(ENG_Sphere sphere, float[] pos) {
        if (mCullFrustum != null) {
            return mCullFrustum.projectSphere(sphere, pos);
        } else {
            return super.projectSphere(sphere, pos);
        }
    }

    public float getNearClipDistance() {
        if (mCullFrustum != null) {
            return mCullFrustum.getNearClipDistance();
        } else {
            return super.getNearClipDistance();
        }
    }

    public float getFarClipDistance() {
        if (mCullFrustum != null) {
            return mCullFrustum.getFarClipDistance();
        } else {
            return super.getFarClipDistance();
        }
    }

/*	public ENG_Matrix4 getViewMatrix() {
		if (mCullFrustum != null) {
			return mCullFrustum.getViewMatrix();
		} else {
			return super.getViewMatrix();
		}
	}*/

    public void setCullingFrustum(ENG_Frustum cullFrustum) {
        mCullFrustum = cullFrustum;
    }

    public ENG_Frustum getCullingFrustum() {
        return mCullFrustum;
    }

    public ENG_Viewport getViewport() {
        return mLastViewport;
    }

    public void _notifyViewport(ENG_Viewport vp) {
        mLastViewport = vp;
    }

    public boolean isWindowSet() {
        return mWindowSet;
    }

    public ENG_SceneNode getAutoTrackTarget() {
        return mAutoTrackTarget;
    }

    public ENG_Vector4D getAutoTrackOffsetCopy() {
        return new ENG_Vector4D(mAutoTrackOffset);
    }

    public ENG_Vector4D getAutoTrackOffset() {
        return mAutoTrackOffset;
    }

    public void getAutoTrackOffset(ENG_Vector4D ret) {
        ret.set(mAutoTrackOffset);
    }

    public void setUseRenderingDistance(boolean use) {
        mUseRenderingDistance = use;
    }

    public boolean getUseRenderingDistance() {
        return mUseRenderingDistance;
    }

    public void synchroniseBaseSettingsWith(ENG_Camera cam) {
        setPosition(cam.getPosition());
        setProjectionType(cam.getProjectionType());
        setOrientation(cam.getOrientation());
        setAspectRatio(cam.getAspectRatio());
        setNearClipDistance(cam.getNearClipDistance());
        setFarClipDistance(cam.getFarClipDistance());
        setUseRenderingDistance(cam.getUseRenderingDistance());
        setFOVy(cam.getFOVy());
        setFocalLength(cam.getFocalLength());
    }

    public ArrayList<ENG_Vector4D> getRayForwardIntersect(ENG_Vector4D anchor,
                                                          ENG_Vector4D[] dir, float planeOffset) {
        ArrayList<ENG_Vector4D> res = new ArrayList<>();
        if (dir == null) {
            return res;
        }

        int[] infpt = new int[4];
        ENG_Vector4D[] vec = new ENG_Vector4D[4];

        for (int i = 0; i < 4; ++i) {
            vec[i] = new ENG_Vector4D();
        }

        float delta = planeOffset - anchor.z;

        for (int i = 0; i < 4; ++i) {
            float test = dir[i].z * delta;
            if (test == 0.0) {
                vec[i].set(dir[i]);
                infpt[i] = 1;
            } else {
                float lambda = delta / dir[i].z;
                vec[i].set(dir[i].mulAsPt(lambda).addAsPt(anchor));
                if (test < 0.0)
                    infpt[i] = 2;
            }
        }

        for (int i = 0; i < 4; ++i) {
            if (infpt[i] == 0) {
                res.add(new ENG_Vector4D(vec[i]));
            } else {
                int nextind = (i + 1) % 4;
                int prevind = (i + 3) % 4;
                if ((infpt[prevind] == 0) || (infpt[nextind] == 0)) {
                    if (infpt[i] == 1) {
                        res.add(new ENG_Vector4D(vec[i].x, vec[i].y, vec[i].z, 0.0f));
                    } else {
                        if (infpt[prevind] == 0) {
                            ENG_Vector4D temp = vec[prevind].subAsVec(vec[i]);
                            res.add(temp);
                        }
                        if (infpt[nextind] == 0) {
                            ENG_Vector4D temp = vec[nextind].subAsVec(vec[i]);
                            res.add(temp);
                        }
                    }
                }
            }
        }
        return res;
    }

    public void forwardIntersect(ENG_Plane worldPlane,
                                 ArrayList<ENG_Vector4D> intersect3d) {
        if (intersect3d == null) {
            return;
        }

        ENG_Vector4D trCorner = getWorldSpaceCorners()[0];
        ENG_Vector4D tlCorner = getWorldSpaceCorners()[1];
        ENG_Vector4D blCorner = getWorldSpaceCorners()[2];
        ENG_Vector4D brCorner = getWorldSpaceCorners()[3];

        ENG_Plane pval = new ENG_Plane(worldPlane);
        if (pval.normal.z < 0.0f) {
            pval.normal.mul(-1.0f);
            pval.d *= (float) -1.0;
        }
        ENG_Quaternion invPlaneRot = pval.normal.getRotationTo(ENG_Math.VEC4_Z_UNIT);
        ENG_Vector4D lPos = invPlaneRot.mul(getDerivedPosition());
        ENG_Vector4D[] vec = new ENG_Vector4D[4];

        vec[0] = invPlaneRot.mul(trCorner).subAsPt(lPos);
        vec[1] = invPlaneRot.mul(tlCorner).subAsPt(lPos);
        vec[2] = invPlaneRot.mul(blCorner).subAsPt(lPos);
        vec[3] = invPlaneRot.mul(brCorner).subAsPt(lPos);

        ArrayList<ENG_Vector4D> iPnt = getRayForwardIntersect(lPos, vec, -pval.d);

        ENG_Quaternion planeRot = invPlaneRot.inverseRet();
        intersect3d.clear();
        for (int i = 0; i < iPnt.size(); ++i) {
            ENG_Vector4D intersection = planeRot.mul(new ENG_Vector4D(
                    iPnt.get(i).x, iPnt.get(i).y, iPnt.get(i).z, 0.0f));
            intersect3d.add(new ENG_Vector4D(
                    intersection.x, intersection.y, intersection.z, iPnt.get(i).w));
        }
    }

    public ENG_SceneManager getSceneManager() {
        return mSceneMgr;
    }

    public String getName() {
        return mName;
    }

    public void setPolygonMode(PolygonMode p) {
        mSceneDetail = p;
    }

    public PolygonMode getPolygonMode() {
        return mSceneDetail;
    }

    public void setPosition(float x, float y, float z) {
        wrapper.setPosition(x, y, z);
        mPosition.x = x;
        mPosition.y = y;
        mPosition.z = z;
        invalidateView();
    }

    public void setPosition(ENG_Vector4D pos) {
        wrapper.setPosition(pos.x, pos.y, pos.z);
        mPosition.set(pos);
        invalidateView();
    }

    public void setPosition(ENG_Vector3D pos) {
        wrapper.setPosition(pos.x, pos.y, pos.z);
        mPosition.set(pos);
        invalidateView();
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

    public void move(ENG_Vector4D vec) {
        mPosition.addInPlace(vec);
        invalidateView();
    }

    public void move(ENG_Vector3D vec) {
        mPosition.addInPlace(vec);
        invalidateView();
    }

    public void moveRelative(ENG_Vector4D vec) {
        mOrientation.mul(vec, moveRelativeTempVec);
        mPosition.addInPlace(moveRelativeTempVec);
        invalidateView();
    }

    public void setDirection(float x, float y, float z) {
        setDirection(new ENG_Vector4D(x, y, z, 0.0f));
    }

    public void setDirection(ENG_Vector4D vec) {
        if (vec.equals(ENG_Math.VEC4_ZERO)) {
            return;
        }

        vec.invert(zAdjustVec);
        zAdjustVec.normalize();

        if (mYawFixed) {
            mYawFixedAxis.crossProduct(zAdjustVec, xVec);
            xVec.normalize();

            zAdjustVec.crossProduct(xVec, yVec);
            yVec.normalize();

            targetWorldOrientation.fromAxes(xVec, yVec, zAdjustVec, mat);
        } else {

            updateView();
            mRealOrientation.toAxes(xAxis, yAxis, zAxis, mat);

            temp.set(zAxis);
            temp.addInPlace(zAdjustVec);

            if (temp.squaredLength() < 0.00005f) {
                rotQuat.fromAngleAxis(ENG_Math.PI_RAD, xAxis);
            } else {
                zAxis.getRotationTo(zAdjustVec, rotQuat, v0, v1, axis);
            }
            rotQuat.mul(mRealOrientation, targetWorldOrientation);
        }

        if (mParentNode != null) {
            mParentNode._getDerivedOrientation(mOrientation);
            mOrientation.inverse();
            mOrientation.mulInPlace(targetWorldOrientation);
        } else {
            mOrientation.set(targetWorldOrientation);
        }

        // TODO If we have a fixed yaw axis, we mustn't break it by using the
        // shortest arc because this will sometimes cause a relative yaw
        // which will tip the camera

        invalidateView();
    }

    public ENG_Vector4D getDirection() {
        //	updateView();
        return mOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT);
    }

    public void getDirection(ENG_Vector4D ret) {
        //	updateView();
        mOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, ret);
    }

    public ENG_Vector4D getUp() {
        //	updateView();
        return mOrientation.mul(ENG_Math.VEC4_Y_UNIT);
    }

    public void getUp(ENG_Vector4D ret) {
        //	updateView();
        mOrientation.mul(ENG_Math.VEC4_Y_UNIT, ret);
    }

    public ENG_Vector4D getRight() {
        //	updateView();
        return mOrientation.mul(ENG_Math.VEC4_X_UNIT);
    }

    public void getRight(ENG_Vector4D ret) {
        //	updateView();
        mOrientation.mul(ENG_Math.VEC4_X_UNIT, ret);
    }

    @Deprecated
    /**
     * Use Utility.lookAt() instead. This doesn't work anymore since we have switched to
     * bullet physics engine.
     */
    public void lookAt(ENG_Vector4D targetPoint) {
        wrapper.lookAt(targetPoint.x, targetPoint.y, targetPoint.z);
        updateView();
        targetPoint.sub(mRealPosition, lookAtTempVec);
        setDirection(lookAtTempVec);
    }

    @Override
    public void setFOVy(float fov) {
        super.setFOVy(fov);
        wrapper.setFOVy(fov);
    }

    public void roll(ENG_Radian angle) {
        mOrientation.mul(ENG_Math.VEC4_Z_UNIT, rotZAxis);
        rotate(rotZAxis, angle);

        invalidateView();
    }

    public void yaw(ENG_Radian angle) {
        if (mYawFixed) {
            rotYAxis.set(mYawFixedAxis);
        } else {
            mOrientation.mul(ENG_Math.VEC4_Y_UNIT, rotYAxis);
        }

        rotate(rotYAxis, angle);

        invalidateView();
    }

    public void pitch(ENG_Radian angle) {
        mOrientation.mul(ENG_Math.VEC4_X_UNIT, rotXAxis);
        rotate(rotXAxis, angle);

        invalidateView();
    }
	
/*	public void rotate(ENG_Vector3D axis, ENG_Radian angle) {
		q.fromAngleAxis(angle, axis);
		rotate(q);
	}*/

    public void rotate(ENG_Vector4D axis, ENG_Radian angle) {
        q.fromAngleAxis(angle, axis);
        rotate(q);
    }

    public void rotate(ENG_Quaternion q) {
        qnorm.set(q);
        qnorm.normalize();
        qnorm.mul(mOrientation, mOrientation);

        invalidateView();
    }

    public boolean getProjectionMatrixNative(ENG_Matrix4 ret, int writeableBuffer) {
        ENG_NativeCalls.camera_getProjectionMatrix(this, ret, tempNative, projectionMatrixSet, writeableBuffer);
        return projectionMatrixSet.getValue();
    }

    public boolean getViewMatrixNative(ENG_Matrix4 ret, int writeableBuffer) {
        ENG_NativeCalls.camera_getViewMatrix(this, ret, tempNative, viewMatrixSet, writeableBuffer);
        return viewMatrixSet.getValue();
    }


}
