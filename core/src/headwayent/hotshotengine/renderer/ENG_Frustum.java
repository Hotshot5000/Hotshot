/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Plane;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Radian;
import headwayent.hotshotengine.ENG_Sphere;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_RenderableImpl.Visitor;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_CameraNativeWrapper;

import java.nio.ByteBuffer;

public class ENG_Frustum extends ENG_MovableObject {


    public enum OrientationMode {
        OR_DEGREE_0(0), OR_DEGREE_90(1),
        OR_DEGREE_180(2), OR_DEGREE_270(3),
        OR_PORTRAIT(OR_DEGREE_0.getMode()),
        OR_LANDSCAPERIGHT(OR_DEGREE_90.getMode()),
        OR_LANDSCAPELEFT(OR_DEGREE_270.getMode());

        private final int mode;

        OrientationMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }

    }

    public enum ProjectionType {PT_ORTOGRAPHIC, PT_PERSPECTIVE}

    public enum FrustumPlane {
        FRUSTUM_PLANE_NEAR(0), FRUSTUM_PLANE_FAR(1),
        FRUSTUM_PLANE_LEFT(2), FRUSTUM_PLANE_RIGHT(3),
        FRUSTUM_PLANE_TOP(4), FRUSTUM_PLANE_BOTTOM(5);

        private final int plane;

        FrustumPlane(int plane) {
            this.plane = plane;
        }

        public int getPlane() {
            return plane;
        }
    }

    public static final float INFINITE_FAR_PLANE_ADJUST = 0.00001f;
    public static final float DEFAULT_FOVY = ENG_Math.QUARTER_PI;
    public static final float DEFAULT_FAR_DIST = 100000.0f;
    public static final float DEFAULT_NEAR_DIST = 100.0f;
    public static final float DEFAULT_ASPECT = 1.33333333333333f;
    public static final float DEFAULT_ORTHOHEIGHT = 1000.0f;
    public static final ENG_Vector2D DEFAULT_FRUSTUM_OFFSET =
            new ENG_Vector2D(ENG_Math.VEC2_ZERO);

    public static final float DEFAULT_FOCAL_LENGTH = 1.0f;
    public static final ENG_Quaternion DEFAULT_LAST_PARENT_ORIENTATION =
            new ENG_Quaternion(ENG_Math.QUAT_IDENTITY);

    public static final ENG_Vector4D DEFAULT_LAST_PARENT_POSITION =
            new ENG_Vector4D(ENG_Math.VEC4_ZERO);


    protected ENG_CameraNativeWrapper wrapper;
    protected ProjectionType mProjType = ProjectionType.PT_PERSPECTIVE;
    protected float mFOVy = DEFAULT_FOVY;
    protected float mFarDist = DEFAULT_FAR_DIST;
    protected float mNearDist = DEFAULT_NEAR_DIST;
    protected float mAspect = DEFAULT_ASPECT;
    protected float orthoHeight = DEFAULT_ORTHOHEIGHT;
    protected final ENG_Vector2D frustumOffset = new ENG_Vector2D(DEFAULT_FRUSTUM_OFFSET);
    protected float focalLength = DEFAULT_FOCAL_LENGTH;
    protected final ENG_Plane[] mFrustumPlanes = new ENG_Plane[6];
    protected final ENG_Quaternion mLastParentOrientation =
            new ENG_Quaternion(DEFAULT_LAST_PARENT_ORIENTATION);
    protected final ENG_Vector4D mLastParentPosition =
            new ENG_Vector4D(DEFAULT_LAST_PARENT_POSITION);
    protected ENG_Matrix4 mProjMatrixRS = new ENG_Matrix4();
    protected final ENG_Matrix4 mProjMatrixRSDepth = new ENG_Matrix4();
    protected final ENG_Matrix4 mProjMatrix = new ENG_Matrix4();
    protected ENG_Matrix4 mViewMatrix = new ENG_Matrix4();
    protected boolean mRecalcFrustum = true;
    protected boolean mRecalcView = true;
    protected boolean mRecalcFrustumPlanes = true;
    protected boolean mRecalcWorldSpaceCorners = true;
    protected boolean mRecalcVertexData = true;
    protected boolean mCustomProjMatrix;
    protected boolean mCustomViewMatrix;
    protected boolean mFrustumExtentsManuallySet;
    protected float left, right, top, bottom;
    protected static String movableType = "Frustum";
    protected final ENG_AxisAlignedBox mBoundingBox = new ENG_AxisAlignedBox();
    protected final ENG_VertexData mVertexData = new ENG_VertexData(null);
    protected ENG_Material mMaterial;
    protected final ENG_Vector4D[] mWorldSpaceCorners = new ENG_Vector4D[8];
    protected boolean mReflect;
    protected final ENG_Matrix4 mReflectMatrix = new ENG_Matrix4();
    protected ENG_Plane mReflectPlane = new ENG_Plane();
    protected final ENG_Plane mLastLinkedReflectionPlane = new ENG_Plane();
    protected boolean mObliqueDepthProjection;
    protected final ENG_Plane mObliqueProjectionPlane = new ENG_Plane();
    protected final ENG_Plane mLastLinkedObliqueProjPlane = new ENG_Plane();
    protected OrientationMode mOrientationMode = OrientationMode.OR_DEGREE_0;
    private final ENG_Vector4D center = new ENG_Vector4D();
    private final ENG_Vector4D halfSize = new ENG_Vector4D();
    private final ENG_Matrix4 invProj = new ENG_Matrix4();
    private final ENG_Vector4D topLeft = new ENG_Vector4D();
    private final ENG_Vector4D bottomRight = new ENG_Vector4D();
    private final float[] planeList = new float[4];
    private ENG_Plane plane;
    private ENG_Matrix4 tempMat;
    private ENG_Vector4D tempVec = new ENG_Vector4D();
    private ENG_Vector4D customMatrixTempVec;
    private ENG_Vector4D qVec;
    private ENG_Vector4D clipPlane4D;
    private ENG_Vector4D c;
    private ENG_Vector4D min, max;
    private ENG_Matrix4 matTrans;
    private final ENG_Matrix4 combo = new ENG_Matrix4();
    private final ENG_Matrix4 eyeToWorld = new ENG_Matrix4();
    private final float[] planeCorners = new float[4];
    private ENG_Vector4D cornerTemp;
    private ENG_Vector4D eyeSpacePos;
    private ENG_Matrix4 tempProjMatrix;
    private ENG_Vector4D relx0;
    private ENG_Vector4D relx1;
    private ENG_Vector4D rely0;
    private ENG_Vector4D rely1;

    protected final String mName;

    /**
     * @param name
     * @param doNotUpdateViewAndFrustum Calling updateView might go into the subclass
     *                                  and since the subclass it's not initialized then we might have some
     *                                  NullPointerExceptions.
     */
    public ENG_Frustum(String name, boolean doNotUpdateViewAndFrustum) {
        mName = name;
        for (int i = 0; i < mFrustumPlanes.length; ++i) {
            mFrustumPlanes[i] = new ENG_Plane();
        }
        for (int i = 0; i < mWorldSpaceCorners.length; ++i) {
            mWorldSpaceCorners[i] = new ENG_Vector4D(true);
        }
        mLastLinkedReflectionPlane.normal.set(ENG_Math.VEC4_ZERO);
        mLastLinkedObliqueProjPlane.normal.set(ENG_Math.VEC4_ZERO);
        if (!doNotUpdateViewAndFrustum) {
            updateView();
            updateFrustum();
        }
    }

    public ENG_Frustum(String name) {
        
        this(name, false);
    }

    public void setWrapper(ENG_CameraNativeWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public void setFOVy(float fov) {
        mFOVy = fov;
        invalidateFrustum();
    }

    public float getFOVy() {
        return mFOVy;
    }

    public void setFarClipDistance(float plane) {
        if (wrapper != null) {
            wrapper.setFarClipDistance(plane);
        }
        mFarDist = plane;
        invalidateFrustum();
    }

    public float getFarClipDistance() {
        return mFarDist;
    }

    public void setNearClipDistance(float near) {
        if (near <= 0.0f) {
            throw new IllegalArgumentException("near: " + near +
                    " must be greater than 0");
        }
        if (wrapper != null) {
            wrapper.setNearClipDistance(near);
        }
        mNearDist = near;
        invalidateFrustum();
    }

    public float getNearClipDistance() {
        return mNearDist;
    }

    public void setFrustumOffset(ENG_Vector2D offs) {
        frustumOffset.set(offs);
        invalidateFrustum();
    }

    public void setFrustumOffset(float x, float y) {
        frustumOffset.set(x, y);
        invalidateFrustum();
    }

    public void getFrustumOffset(ENG_Vector2D ret) {
        ret.set(frustumOffset);
    }

    public ENG_Vector2D getFrustumOffset() {
        ENG_Vector2D ret = new ENG_Vector2D();
        getFrustumOffset(ret);
        return ret;
    }

    public void setFocalLength(float focalLength) {
        if (focalLength <= 0.0f) {
            throw new IllegalArgumentException("focalLength: " + focalLength +
                    " must be greater than 0");
        }
        this.focalLength = focalLength;
        invalidateFrustum();
    }

    public float getFocalLength() {
        return focalLength;
    }

    public void getProjectionMatrix(ENG_Matrix4 ret) {
        updateFrustum();
        ret.set(mProjMatrix);
    }

    public ENG_Matrix4 getProjectionMatrix() {
        updateFrustum();
        return mProjMatrix;
    }

    public ENG_Matrix4 getProjectionMatrixCopy() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        getProjectionMatrix(ret);
        return ret;
    }

    public void getProjectionMatrixWithRSDepth(ENG_Matrix4 ret) {
        updateFrustum();
        ret.set(mProjMatrixRSDepth);
    }

    public ENG_Matrix4 getProjectionMatrixWithRSDepthCopy() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        getProjectionMatrixWithRSDepth(ret);
        return ret;
    }

    public ENG_Matrix4 getProjectionMatrixWithRSDepth() {
        updateFrustum();
        return mProjMatrixRSDepth;
    }

    public void getProjectionMatrixRS(ENG_Matrix4 ret) {
        updateFrustum();
        ret.set(mProjMatrixRS);
    }

    public ENG_Matrix4 getProjectionMatrixRSCopy() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        getProjectionMatrixRS(ret);
        return ret;
    }

    public ENG_Matrix4 getProjectionMatrixRS() {
        updateFrustum();
        return mProjMatrixRS;
    }

    public void getViewMatrix(ENG_Matrix4 ret) {
        updateView();
        ret.set(mViewMatrix);
    }

    public ENG_Matrix4 getViewMatrixCopy() {
        ENG_Matrix4 ret = new ENG_Matrix4();
        getViewMatrix(ret);
        return ret;
    }

    public ENG_Matrix4 getViewMatrix() {
        updateView();
        return mViewMatrix;
    }

    public void getFrustumPlanes(ENG_Plane[] planeList) {
        updateFrustumPlanes();
        if (planeList.length < 6) {
            throw new IllegalArgumentException("planeList has " + planeList.length +
                    " instead of 6");
        }
        for (int i = 0; i < planeList.length; ++i) {
            planeList[i].set(mFrustumPlanes[i]);
        }
    }

    public ENG_Plane[] getFrustumPlanes() {
        ENG_Plane[] planeList = new ENG_Plane[6];
        for (int i = 0; i < planeList.length; ++i) {
            planeList[i] = new ENG_Plane();
        }

        getFrustumPlanes(planeList);
        return planeList;
    }

    public void getFrustumPlane(ENG_Plane ret, int index) {
        if ((index < 0) || (index >= 6)) {
            throw new IllegalArgumentException("index: " + index +
                    " and should be > 0 and < 6");
        }
        ret.set(mFrustumPlanes[index]);
    }

    public ENG_Plane getFrustumPlane(int index) {
        ENG_Plane ret = new ENG_Plane();
        getFrustumPlane(ret, index);
        return ret;
    }

    public boolean isVisible(ENG_AxisAlignedBox bound, int[] culledBy) {
        if (bound.isNull()) {
            return false;
        }
        if (bound.isInfinite()) {
            return true;
        }
        updateFrustumPlanes();
        bound.getCenter(center);
        bound.getHalfSize(halfSize);

        for (int plane = 0; plane < 6; ++plane) {
            if ((FrustumPlane.FRUSTUM_PLANE_FAR.ordinal() == plane) &&
                    mFarDist == 0.0f) {
                continue;
            }
            ENG_Plane.Side side = mFrustumPlanes[plane].getSide(center, halfSize);
            if (side == ENG_Plane.Side.NEGATIVE_SIDE) {
                if (culledBy != null) {
                    culledBy[0] = plane;
                }
                return false;
            }
        }
        return true;
    }

    public boolean isVisible(ENG_Vector3D vert, int[] culledBy) {
        updateFrustumPlanes();
        for (int plane = 0; plane < 6; ++plane) {
            if ((FrustumPlane.FRUSTUM_PLANE_FAR.ordinal() == plane) &&
                    mFarDist == 0.0f) {
                continue;
            }
            if (mFrustumPlanes[plane].getSide(vert) == ENG_Plane.Side.NEGATIVE_SIDE) {
                if (culledBy != null) {
                    culledBy[0] = plane;

                }
                return false;
            }
        }
        return true;
    }

    public boolean isVisible(ENG_Vector4D vert, int[] culledBy) {
        updateFrustumPlanes();
        for (int plane = 0; plane < 6; ++plane) {
            if ((FrustumPlane.FRUSTUM_PLANE_FAR.ordinal() == plane) &&
                    mFarDist == 0.0f) {
                continue;
            }
            if (mFrustumPlanes[plane].getSide(vert) == ENG_Plane.Side.NEGATIVE_SIDE) {
                if (culledBy != null) {
                    culledBy[0] = plane;

                }
                return false;
            }
        }
        return true;
    }

    public boolean isVisible(ENG_Sphere sphere, int[] culledBy) {
        updateFrustumPlanes();
        for (int plane = 0; plane < 6; ++plane) {
            if ((FrustumPlane.FRUSTUM_PLANE_FAR.ordinal() == plane) &&
                    mFarDist == 0.0f) {
                continue;
            }
            if (mFrustumPlanes[plane].getDistance(sphere.center) < (-sphere.radius)) {
                if (culledBy != null) {
                    culledBy[0] = plane;

                }
                return false;
            }
        }
        return true;
    }

    public int getTypeFlags() {
        return ENG_SceneManager.FRUSTUM_TYPE_MASK;
    }

	

/*	@Override
	public ENG_AxisAlignedBox getBoundingBox() {
		
		return null;
	}*/

    /**
     * @param plane plane[0] - left plane[1] - top plane[2] - right plane[3] - bottom
     */
    protected void calcProjectionParameters(float[] plane) {
        if (plane.length < 4) {
            throw new IllegalArgumentException("plane length must be >= 4");
        }
        if (mCustomProjMatrix) {
            mProjMatrix.invert(invProj);
            topLeft.set(-0.5f, 0.5f, 0.0f, 1.0f);
            bottomRight.set(0.5f, -0.5f, 0.0f, 1.0f);
            invProj.transform(topLeft);
            invProj.transform(bottomRight);
            plane[0] = topLeft.x;
            plane[1] = topLeft.y;
            plane[2] = bottomRight.x;
            plane[3] = bottomRight.y;
        } else {
            if (mFrustumExtentsManuallySet) {
                plane[0] = left;
                plane[1] = top;
                plane[2] = right;
                plane[3] = bottom;
            } else if (mProjType == ProjectionType.PT_PERSPECTIVE) {
				/*Radian thetaY (mFOVy * 0.5f);
				Real tanThetaY = Math::Tan(thetaY);
				Real tanThetaX = tanThetaY * mAspect;

				Real nearFocal = mNearDist / mFocalLength;
				Real nearOffsetX = mFrustumOffset.x * nearFocal;
				Real nearOffsetY = mFrustumOffset.y * nearFocal;
				Real half_w = tanThetaX * mNearDist;
				Real half_h = tanThetaY * mNearDist;

				left   = - half_w + nearOffsetX;
				right  = + half_w + nearOffsetX;
				bottom = - half_h + nearOffsetY;
				top    = + half_h + nearOffsetY;

				mLeft = left;
				mRight = right;
				mTop = top;
				mBottom = bottom;*/
                float thetaY = mFOVy * 0.5f;
                float tanThetaY = ENG_Math.tan(thetaY);
                float tanThetaX = tanThetaY * mAspect;

                float nearFocal = mNearDist / focalLength;
                float nearOffsetX = frustumOffset.x * nearFocal;
                float nearOffsetY = frustumOffset.y * nearFocal;
                float half_w = tanThetaX * mNearDist;
                float half_h = tanThetaY * mNearDist;

                left = -half_w + nearOffsetX;
                right = half_w + nearOffsetX;
                bottom = -half_h + nearOffsetY;
                top = half_h + nearOffsetY;
			/*	System.out.println("mFOVy " + mFOVy + " tanThetaY " + tanThetaY +
						" mAspect " + mAspect + " mNearDist " + mNearDist +
						" frustumOffset " + frustumOffset + " focalLength " +
						focalLength);*/
                plane[0] = left;
                plane[1] = top;
                plane[2] = right;
                plane[3] = bottom;
            } else {
				/*Real half_w = getOrthoWindowWidth() * 0.5f;
				Real half_h = getOrthoWindowHeight() * 0.5f;

				left   = - half_w;
				right  = + half_w;
				bottom = - half_h;
				top    = + half_h;

				mLeft = left;
				mRight = right;
				mTop = top;
				mBottom = bottom;*/

                float half_w = getOrthoWindowWidth() * 0.5f;
                float half_h = getOrthoWindowHeight() * 0.5f;

                left = -half_w;
                right = half_w;
                bottom = -half_h;
                top = half_h;

                plane[0] = left;
                plane[1] = top;
                plane[2] = right;
                plane[3] = bottom;
            }

        }
    }


    public void setOrthoWindow(float w, float h) {
        orthoHeight = h;
        mAspect = w / h;
        invalidateFrustum();
    }

    public void setOrthoWindowHeight(float h) {
        orthoHeight = h;
        invalidateFrustum();
    }

    public void setOrthoWindowWidth(float w) {
        orthoHeight = w / mAspect;
        invalidateFrustum();
    }

    public float getOrthoWindowHeight() {
        return orthoHeight;
    }

    public float getOrthoWindowWidth() {
        return orthoHeight * mAspect;
    }

    protected void updateFrustum() {
//        if (isFrustumOutOfDate()) {
            updateFrustumImpl();
//        }
    }

    protected void updateView() {
        if (isViewOutOfDate()) {
            updateViewImpl();
        }
    }

    protected void updateFrustumImpl() {
        calcProjectionParameters(planeList);
        float left = planeList[0];
        float top = planeList[1];
        float right = planeList[2];
        float bottom = planeList[3];
        //	System.out.println("updateFrustumImpl left " + left + " top " + top +
        //			" right " + right + " bottom " + bottom);
        if (!mCustomProjMatrix) {
			/*Real inv_w = 1 / (right - left);
			Real inv_h = 1 / (top - bottom);
			Real inv_d = 1 / (mFarDist - mNearDist);*/

            float inv_w = 1.0f / (right - left);
            float inv_h = 1.0f / (top - bottom);
            float inv_d = 1.0f / (mFarDist - mNearDist);

            if (mProjType == ProjectionType.PT_PERSPECTIVE) {
				/*Real A = 2 * mNearDist * inv_w;
				Real B = 2 * mNearDist * inv_h;
				Real C = (right + left) * inv_w;
				Real D = (top + bottom) * inv_h;*/

                float A = 2.0f * mNearDist * inv_w;
                float B = 2.0f * mNearDist * inv_h;
                float C = (right + left) * inv_w;
                float D = (top + bottom) * inv_h;
                float q, qn;

                if (mFarDist == 0.0f) {
                    q = INFINITE_FAR_PLANE_ADJUST - 1.0f;
                    qn = mNearDist * (INFINITE_FAR_PLANE_ADJUST - 2.0f);
                } else {
                    q = -(mFarDist + mNearDist) * inv_d;
                    qn = (-2.0f) * (mFarDist * mNearDist) * inv_d;
                }

                // NB: This creates 'uniform' perspective projection matrix,
                // which depth range [-1,1], right-handed rules
                //
                // [ A   0   C   0  ]
                // [ 0   B   D   0  ]
                // [ 0   0   q   qn ]
                // [ 0   0   -1  0  ]
                //
                // A = 2 * near / (right - left)
                // B = 2 * near / (top - bottom)
                // C = (right + left) / (right - left)
                // D = (top + bottom) / (top - bottom)
                // q = - (far + near) / (far - near)
                // qn = - 2 * (far * near) / (far - near)
				
				/*mProjMatrix = Matrix4::ZERO;
				mProjMatrix[0][0] = A;
				mProjMatrix[0][2] = C;
				mProjMatrix[1][1] = B;
				mProjMatrix[1][2] = D;
				mProjMatrix[2][2] = q;
				mProjMatrix[2][3] = qn;
				mProjMatrix[3][2] = -1;*/

                mProjMatrix.set(ENG_Math.MAT4_ZERO);
                mProjMatrix.set(0, 0, A);
                mProjMatrix.set(0, 2, C);
                mProjMatrix.set(1, 1, B);
                mProjMatrix.set(1, 2, D);
                mProjMatrix.set(2, 2, q);
                mProjMatrix.set(2, 3, qn);
                mProjMatrix.set(3, 2, -1.0f);

                if (mObliqueDepthProjection) {
                    // Translate the plane into view space

                    // Don't use getViewMatrix here, incase overrided by
                    // camera and return a cull frustum view matrix
					
					/*updateView();
					Plane plane = mViewMatrix * mObliqueProjPlane;*/
                    if (plane == null) {
                        plane = new ENG_Plane();
                    }
                    if (tempMat == null) {
                        tempMat = new ENG_Matrix4();
                    }
                    if (tempVec == null) {
                        tempVec = new ENG_Vector4D();
                    }
                    updateView();
                    mViewMatrix.transform(
                            mObliqueProjectionPlane, plane, tempMat, tempVec);

                    // Thanks to Eric Lenyel for posting this calculation
                    // at www.terathon.com

                    // Calculate the clip-space corner point opposite the
                    // clipping plane
                    // as (sgn(clipPlane.x), sgn(clipPlane.y), 1, 1) and
                    // transform it into camera space by multiplying it
                    // by the inverse of the projection matrix

					/* generalised version
					Vector4 q = matrix.inverse() * 
					Vector4(Math::Sign(plane.normal.x), 
					Math::Sign(plane.normal.y), 1.0f, 1.0f);
					*/
					
					/*Vector4 q;
					q.x = (Math::Sign(plane.normal.x) + mProjMatrix[0][2]) / mProjMatrix[0][0];
					q.y = (Math::Sign(plane.normal.y) + mProjMatrix[1][2]) / mProjMatrix[1][1];
					q.z = -1;
					q.w = (1 + mProjMatrix[2][2]) / mProjMatrix[2][3];

					// Calculate the scaled plane vector
					Vector4 clipPlane4d(plane.normal.x, plane.normal.y, plane.normal.z, plane.d);
					Vector4 c = clipPlane4d * (2 / (clipPlane4d.dotProduct(q)));

					// Replace the third row of the projection matrix
					mProjMatrix[2][0] = c.x;
					mProjMatrix[2][1] = c.y;
					mProjMatrix[2][2] = c.z + 1;
					mProjMatrix[2][3] = c.w; */

                    if (qVec == null) {
                        qVec = new ENG_Vector4D();
                    }
                    qVec.x = (ENG_Math.sign(plane.normal.x) + mProjMatrix.get(0, 2)) /
                            mProjMatrix.get(0, 0);
                    qVec.y = (ENG_Math.sign(plane.normal.y) + mProjMatrix.get(1, 2)) /
                            mProjMatrix.get(1, 1);
                    qVec.z = -1.0f;
                    qVec.w = (1.0f + mProjMatrix.get(2, 2)) / mProjMatrix.get(2, 3);

                    if (clipPlane4D == null) {
                        clipPlane4D = new ENG_Vector4D();
                    }
                    if (c == null) {
                        c = new ENG_Vector4D();
                    }
                    clipPlane4D.set(plane.normal.x, plane.normal.y, plane.normal.z, plane.d);
                    clipPlane4D.mul(2.0f / clipPlane4D.dotProduct(qVec), c);

                    mProjMatrix.set(2, 0, c.x);
                    mProjMatrix.set(2, 1, c.y);
                    mProjMatrix.set(2, 2, c.z + 1.0f);
                    mProjMatrix.set(2, 3, c.w);
                }
            } else if (mProjType == ProjectionType.PT_ORTOGRAPHIC) {
				/*Real A = 2 * inv_w;
				Real B = 2 * inv_h;
				Real C = - (right + left) * inv_w;
				Real D = - (top + bottom) * inv_h;
				Real q, qn;
				if (mFarDist == 0)
				{
					// Can not do infinite far plane here, avoid divided zero only
					q = - Frustum::INFINITE_FAR_PLANE_ADJUST / mNearDist;
					qn = - Frustum::INFINITE_FAR_PLANE_ADJUST - 1;
				}
				else
				{
					q = - 2 * inv_d;
					qn = - (mFarDist + mNearDist)  * inv_d;
				}
*/
                float A = 2.0f * inv_w;
                float B = 2.0f * inv_h;
                float C = -(right + left) * inv_w;
                float D = -(top + bottom) * inv_h;
                float q, qn;
                if (mFarDist == 0.0f) {
                    q = -INFINITE_FAR_PLANE_ADJUST / mNearDist;
                    qn = -INFINITE_FAR_PLANE_ADJUST - 1.0f;
                } else {
                    q = -2.0f * inv_d;
                    qn = -(mFarDist + mNearDist) * inv_d;
                }

                // NB: This creates 'uniform' orthographic projection matrix,
                // which depth range [-1,1], right-handed rules
                //
                // [ A   0   0   C  ]
                // [ 0   B   0   D  ]
                // [ 0   0   q   qn ]
                // [ 0   0   0   1  ]
                //
                // A = 2 * / (right - left)
                // B = 2 * / (top - bottom)
                // C = - (right + left) / (right - left)
                // D = - (top + bottom) / (top - bottom)
                // q = - 2 / (far - near)
                // qn = - (far + near) / (far - near)
				
				/*mProjMatrix = Matrix4::ZERO;
				mProjMatrix[0][0] = A;
				mProjMatrix[0][3] = C;
				mProjMatrix[1][1] = B;
				mProjMatrix[1][3] = D;
				mProjMatrix[2][2] = q;
				mProjMatrix[2][3] = qn;
				mProjMatrix[3][3] = 1;*/

                mProjMatrix.set(ENG_Math.MAT4_ZERO);
                mProjMatrix.set(0, 0, A);
                mProjMatrix.set(0, 3, C);
                mProjMatrix.set(1, 1, B);
                mProjMatrix.set(1, 3, D);
                mProjMatrix.set(2, 2, q);
                mProjMatrix.set(2, 3, qn);
                mProjMatrix.set(3, 3, 1.0f);
            }
        }
		
		/*RenderSystem* renderSystem = Root::getSingleton().getRenderSystem();
		// API specific
		renderSystem->_convertProjectionMatrix(mProjMatrix, mProjMatrixRS);
		// API specific for Gpu Programs
		renderSystem->_convertProjectionMatrix(mProjMatrix, mProjMatrixRSDepth, true);*/

        ENG_RenderSystem renderSystem =
                ENG_RenderRoot.getRenderRoot().getRenderSystem();
        renderSystem._convertProjectionMatrix(mProjMatrix, mProjMatrixRS, false);
        renderSystem._convertProjectionMatrix(mProjMatrix, mProjMatrixRSDepth, true);
		
		/*// Calculate bounding box (local)
		// Box is from 0, down -Z, max dimensions as determined from far plane
		// If infinite view frustum just pick a far value
		Real farDist = (mFarDist == 0) ? 100000 : mFarDist;
		// Near plane bounds
		Vector3 min(left, bottom, -farDist);
		Vector3 max(right, top, 0);

		if (mCustomProjMatrix)
		{
			// Some custom projection matrices can have unusual inverted settings
			// So make sure the AABB is the right way around to start with
			Vector3 tmp = min;
			min.makeFloor(max);
			max.makeCeil(tmp);
		}*/

        float farDist = (this.mFarDist == 0.0f) ? 100000.0f : this.mFarDist;
        if (min == null) {
            min = new ENG_Vector4D();
        }
        if (max == null) {
            max = new ENG_Vector4D();
        }
        min.set(left, bottom, -farDist);
        max.set(right, top, 0.0f);

        if (mCustomProjMatrix) {
            if (customMatrixTempVec == null) {
                customMatrixTempVec = new ENG_Vector4D();
            }
            customMatrixTempVec.set(min);
            min.makeFloor(max);
            max.makeCeil(customMatrixTempVec);
        }
		
		/*if (mProjType == PT_PERSPECTIVE)
		{
			// Merge with far plane bounds
			Real radio = farDist / mNearDist;
			min.makeFloor(Vector3(left * radio, bottom * radio, -farDist));
			max.makeCeil(Vector3(right * radio, top * radio, 0));
		}
		mBoundingBox.setExtents(min, max);

		mRecalcFrustum = false;

		// Signal to update frustum clipping planes
		mRecalcFrustumPlanes = true;*/

        if (mProjType == ProjectionType.PT_PERSPECTIVE) {
            float radio = farDist / mNearDist;
            tempVec.set(left * radio, bottom * radio, -farDist);
            min.makeFloor(tempVec);
            tempVec.set(right * radio, top * radio, 0.0f);
            max.makeCeil(tempVec);
        }
        //	System.out.println("Setting frustum bounding box min: " + min + " max " + max);
        mBoundingBox.setExtents(min, max);
        mRecalcFrustum = false;
        mRecalcFrustumPlanes = true;
    }

    protected void updateViewImpl() {
		/*// ----------------------
		// Update the view matrix
		// ----------------------

		// Get orientation from quaternion

		if (!mCustomViewMatrix)
		{
			Matrix3 rot;
			const Quaternion& orientation = getOrientationForViewUpdate();
			const Vector3& position = getPositionForViewUpdate();

			mViewMatrix = Math::makeViewMatrix(position, orientation, mReflect? &mReflectMatrix : 0);
		}*/

        if (!mCustomViewMatrix) {
            ENG_Math.makeViewMatrix(getPositionForViewUpdate(),
                    getOrientationForViewUpdate(), mViewMatrix, mReflect ? mReflectMatrix : null);
        }
		
		/*mRecalcView = false;

		// Signal to update frustum clipping planes
		mRecalcFrustumPlanes = true;
		// Signal to update world space corners
		mRecalcWorldSpaceCorners = true;
		// Signal to update frustum if oblique plane enabled,
		// since plane needs to be in view space
		if (mObliqueDepthProjection)
		{
			mRecalcFrustum = true;
		}*/

        mRecalcView = false;

        mRecalcFrustumPlanes = true;
        mRecalcWorldSpaceCorners = true;

        if (mObliqueDepthProjection) {
            mRecalcFrustum = true;
        }
    }

    public void calcViewMatrixRelative(ENG_Vector4D relPos, ENG_Matrix4 matToUpdate) {
		/*Matrix4 matTrans = Matrix4::IDENTITY;
		matTrans.setTrans(relPos);
		matToUpdate = getViewMatrix() * matTrans;*/

        if (matTrans == null) {
            matTrans = new ENG_Matrix4();
        }
        matTrans.setIdentity();
        matTrans.setTrans(relPos);
        getViewMatrixCopy().concatenate(matTrans, matToUpdate);
    }

    protected ProjectionType getProjectionType() {
        return mProjType;
    }

    protected void setProjectionType(ProjectionType projType) {
        this.mProjType = projType;
        invalidateFrustum();
    }

    protected ENG_Vector4D getPositionForViewUpdate() {
        return mLastParentPosition;
    }

    protected ENG_Quaternion getOrientationForViewUpdate() {
        return mLastParentOrientation;
    }

    private boolean shouldRecalcFrustumPlanes = true;

    protected void updateFrustumPlanes() {
        if (shouldRecalcFrustumPlanes) {
//        updateView();
            updateFrustum();

//        if (mRecalcFrustumPlanes) {
            updateFrustumPlanesImpl();
            shouldRecalcFrustumPlanes = false;
//        }
        }
    }

    public boolean isShouldRecalcFrustumPlanes() {
        return shouldRecalcFrustumPlanes;
    }

    public void setShouldRecalcFrustumPlanes(boolean shouldRecalcFrustumPlanes) {
        this.shouldRecalcFrustumPlanes = shouldRecalcFrustumPlanes;
    }

    protected void updateFrustumPlanesImpl() {
        mProjMatrix.concatenate(mViewMatrix, combo);
		
		/*mFrustumPlanes[FRUSTUM_PLANE_LEFT].normal.x = combo[3][0] + combo[0][0];
		mFrustumPlanes[FRUSTUM_PLANE_LEFT].normal.y = combo[3][1] + combo[0][1];
		mFrustumPlanes[FRUSTUM_PLANE_LEFT].normal.z = combo[3][2] + combo[0][2];
		mFrustumPlanes[FRUSTUM_PLANE_LEFT].d = combo[3][3] + combo[0][3];*/

        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_LEFT.getPlane()].normal.x =
                combo.get(3, 0) + combo.get(0, 0);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_LEFT.getPlane()].normal.y =
                combo.get(3, 1) + combo.get(0, 1);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_LEFT.getPlane()].normal.z =
                combo.get(3, 2) + combo.get(0, 2);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_LEFT.getPlane()].d =
                combo.get(3, 3) + combo.get(0, 3);
		
		/*mFrustumPlanes[FRUSTUM_PLANE_RIGHT].normal.x = combo[3][0] - combo[0][0];
		mFrustumPlanes[FRUSTUM_PLANE_RIGHT].normal.y = combo[3][1] - combo[0][1];
		mFrustumPlanes[FRUSTUM_PLANE_RIGHT].normal.z = combo[3][2] - combo[0][2];
		mFrustumPlanes[FRUSTUM_PLANE_RIGHT].d = combo[3][3] - combo[0][3];*/

        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_RIGHT.getPlane()].normal.x =
                combo.get(3, 0) - combo.get(0, 0);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_RIGHT.getPlane()].normal.y =
                combo.get(3, 1) - combo.get(0, 1);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_RIGHT.getPlane()].normal.z =
                combo.get(3, 2) - combo.get(0, 2);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_RIGHT.getPlane()].d =
                combo.get(3, 3) - combo.get(0, 3);
		
		/*mFrustumPlanes[FRUSTUM_PLANE_TOP].normal.x = combo[3][0] - combo[1][0];
		mFrustumPlanes[FRUSTUM_PLANE_TOP].normal.y = combo[3][1] - combo[1][1];
		mFrustumPlanes[FRUSTUM_PLANE_TOP].normal.z = combo[3][2] - combo[1][2];
		mFrustumPlanes[FRUSTUM_PLANE_TOP].d = combo[3][3] - combo[1][3];*/

        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_TOP.getPlane()].normal.x =
                combo.get(3, 0) - combo.get(1, 0);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_TOP.getPlane()].normal.y =
                combo.get(3, 1) - combo.get(1, 1);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_TOP.getPlane()].normal.z =
                combo.get(3, 2) - combo.get(1, 2);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_TOP.getPlane()].d =
                combo.get(3, 3) - combo.get(1, 3);
		
		/*mFrustumPlanes[FRUSTUM_PLANE_BOTTOM].normal.x = combo[3][0] + combo[1][0];
		mFrustumPlanes[FRUSTUM_PLANE_BOTTOM].normal.y = combo[3][1] + combo[1][1];
		mFrustumPlanes[FRUSTUM_PLANE_BOTTOM].normal.z = combo[3][2] + combo[1][2];
		mFrustumPlanes[FRUSTUM_PLANE_BOTTOM].d = combo[3][3] + combo[1][3];*/

        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_BOTTOM.getPlane()].normal.x =
                combo.get(3, 0) + combo.get(1, 0);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_BOTTOM.getPlane()].normal.y =
                combo.get(3, 1) + combo.get(1, 1);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_BOTTOM.getPlane()].normal.z =
                combo.get(3, 2) + combo.get(1, 2);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_BOTTOM.getPlane()].d =
                combo.get(3, 3) + combo.get(1, 3);
		
		/*mFrustumPlanes[FRUSTUM_PLANE_NEAR].normal.x = combo[3][0] + combo[2][0];
		mFrustumPlanes[FRUSTUM_PLANE_NEAR].normal.y = combo[3][1] + combo[2][1];
		mFrustumPlanes[FRUSTUM_PLANE_NEAR].normal.z = combo[3][2] + combo[2][2];
		mFrustumPlanes[FRUSTUM_PLANE_NEAR].d = combo[3][3] + combo[2][3];*/

        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_NEAR.getPlane()].normal.x =
                combo.get(3, 0) + combo.get(2, 0);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_NEAR.getPlane()].normal.y =
                combo.get(3, 1) + combo.get(2, 1);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_NEAR.getPlane()].normal.z =
                combo.get(3, 2) + combo.get(2, 2);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_NEAR.getPlane()].d =
                combo.get(3, 3) + combo.get(2, 3);
		
		/*mFrustumPlanes[FRUSTUM_PLANE_FAR].normal.x = combo[3][0] - combo[2][0];
		mFrustumPlanes[FRUSTUM_PLANE_FAR].normal.y = combo[3][1] - combo[2][1];
		mFrustumPlanes[FRUSTUM_PLANE_FAR].normal.z = combo[3][2] - combo[2][2];
		mFrustumPlanes[FRUSTUM_PLANE_FAR].d = combo[3][3] - combo[2][3];*/

        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_FAR.getPlane()].normal.x =
                combo.get(3, 0) - combo.get(2, 0);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_FAR.getPlane()].normal.y =
                combo.get(3, 1) - combo.get(2, 1);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_FAR.getPlane()].normal.z =
                combo.get(3, 2) - combo.get(2, 2);
        mFrustumPlanes[FrustumPlane.FRUSTUM_PLANE_FAR.getPlane()].d =
                combo.get(3, 3) - combo.get(2, 3);
		
		/*// Renormalise any normals which were not unit length
		for(int i=0; i<6; i++ ) 
		{
			Real length = mFrustumPlanes[i].normal.normalise();
			mFrustumPlanes[i].d /= length;
		}

		mRecalcFrustumPlanes = false;*/

        for (int i = 0; i < 6; ++i) {
            mFrustumPlanes[i].d /= mFrustumPlanes[i].normal.normalizeRet();
        }
        mRecalcFrustumPlanes = false;
    }

    protected void updateWorldSpaceCorners() {
        updateView();

        if (mRecalcWorldSpaceCorners) {
            updateWorldSpaceCornersImpl();
        }
    }

    protected void updateWorldSpaceCornersImpl() {
        ENG_Matrix4.invertAffine(mViewMatrix, eyeToWorld);
        calcProjectionParameters(planeCorners);
        float nearLeft = planeCorners[0];
        float nearTop = planeCorners[1];
        float nearRight = planeCorners[2];
        float nearBottom = planeCorners[3];
		
		/*// Treat infinite fardist as some arbitrary far value
		Real farDist = (mFarDist == 0) ? 100000 : mFarDist;

		// Calc far palne corners
		Real radio = mProjType == PT_PERSPECTIVE ? farDist / mNearDist : 1;
		Real farLeft = nearLeft * radio;
		Real farRight = nearRight * radio;
		Real farBottom = nearBottom * radio;
		Real farTop = nearTop * radio;*/

        float farDist = (this.mFarDist == 0.0f) ? 100000.0f : this.mFarDist;

        float radio = (mProjType == ProjectionType.PT_PERSPECTIVE) ?
                (farDist / this.mNearDist) : 1.0f;
        float farLeft = nearLeft * radio;
        float farRight = nearRight * radio;
        float farBottom = nearBottom * radio;
        float farTop = nearTop * radio;
        if (cornerTemp == null) {
            cornerTemp = new ENG_Vector4D(true);
        }
		/*// near
		mWorldSpaceCorners[0] = eyeToWorld.transformAffine(Vector3(nearRight, nearTop,    -mNearDist));
		mWorldSpaceCorners[1] = eyeToWorld.transformAffine(Vector3(nearLeft,  nearTop,    -mNearDist));
		mWorldSpaceCorners[2] = eyeToWorld.transformAffine(Vector3(nearLeft,  nearBottom, -mNearDist));
		mWorldSpaceCorners[3] = eyeToWorld.transformAffine(Vector3(nearRight, nearBottom, -mNearDist));*/


        cornerTemp.set(nearRight, nearTop, -this.mNearDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[0]);
        cornerTemp.set(nearLeft, nearTop, -this.mNearDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[1]);
        cornerTemp.set(nearLeft, nearBottom, -this.mNearDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[2]);
        cornerTemp.set(nearRight, nearBottom, -this.mNearDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[3]);
		
		/*// far
		mWorldSpaceCorners[4] = eyeToWorld.transformAffine(Vector3(farRight,  farTop,     -farDist));
		mWorldSpaceCorners[5] = eyeToWorld.transformAffine(Vector3(farLeft,   farTop,     -farDist));
		mWorldSpaceCorners[6] = eyeToWorld.transformAffine(Vector3(farLeft,   farBottom,  -farDist));
		mWorldSpaceCorners[7] = eyeToWorld.transformAffine(Vector3(farRight,  farBottom,  -farDist));*/

        cornerTemp.set(farRight, farTop, -farDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[4]);
        cornerTemp.set(farLeft, farTop, -farDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[5]);
        cornerTemp.set(farLeft, farBottom, -farDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[6]);
        cornerTemp.set(farRight, farBottom, -farDist);
        eyeToWorld.transformAffine(cornerTemp, mWorldSpaceCorners[7]);

        mRecalcWorldSpaceCorners = false;
    }

    public float getAspectRatio() {
        return mAspect;
    }

    public void setAspectRatio(float r) {
        if (wrapper != null) {
            wrapper.setAspectRatio(r);
        }
        mAspect = r;
        invalidateFrustum();
    }

    public ENG_AxisAlignedBox getBoundingBox() {
        return mBoundingBox;
    }

    protected void updateVertexData() {
        if (mRecalcVertexData) {
            if (mVertexData.vertexBufferBinding.getBufferCount() <= 0) {
				/*// Initialise vertex & index data
                mVertexData.vertexDeclaration->addElement(0, 0, VET_FLOAT3, VES_POSITION);
                mVertexData.vertexCount = 32;
                mVertexData.vertexStart = 0;
                mVertexData.vertexBufferBinding->setBinding( 0,
                    HardwareBufferManager::getSingleton().createVertexBuffer(
                        sizeof(float)*3, 32, HardwareBuffer::HBU_DYNAMIC_WRITE_ONLY) );*/

                mVertexData.vertexDeclaration.addElement((short) 0, 0,
                        VertexElementType.VET_FLOAT3,
                        VertexElementSemantic.VES_POSITION, (short) 0);
                mVertexData.vertexCount = 32;
                mVertexData.vertexStart = 0;
                mVertexData.vertexBufferBinding.setBinding((short) 0,
                        ENG_HardwareBufferManager.getSingleton().createVertexBuffer(
                                ENG_Float.SIZE_IN_BYTES * 3,
                                32, Usage.HBU_DYNAMIC_WRITE_ONLY.getUsage(), false));
            }
			
			/*// Calc near plane corners
            Real vpLeft, vpRight, vpBottom, vpTop;
            calcProjectionParameters(vpLeft, vpRight, vpBottom, vpTop);

            // Treat infinite fardist as some arbitrary far value
            Real farDist = (mFarDist == 0) ? 100000 : mFarDist;

            // Calc far plane corners
            Real radio = mProjType == PT_PERSPECTIVE ? farDist / mNearDist : 1;
            Real farLeft = vpLeft * radio;
            Real farRight = vpRight * radio;
            Real farBottom = vpBottom * radio;
            Real farTop = vpTop * radio;*/

            float[] temp = new float[4];
            calcProjectionParameters(temp);
            float vpLeft = temp[0];
            float vpTop = temp[1];
            float vpRight = temp[2];
            float vpBottom = temp[3];

            float farDist = (this.mFarDist == 0.0f) ? 100000.0f : this.mFarDist;

            float radio = (mProjType == ProjectionType.PT_PERSPECTIVE) ?
                    (farDist / this.mNearDist) : 1.0f;
            float farLeft = vpLeft * radio;
            float farRight = vpRight * radio;
            float farBottom = vpBottom * radio;
            float farTop = vpTop * radio;
			
			/*// Calculate vertex positions (local)
            // 0 is the origin
            // 1, 2, 3, 4 are the points on the near plane, top left first, clockwise
            // 5, 6, 7, 8 are the points on the far plane, top left first, clockwise
            HardwareVertexBufferSharedPtr vbuf = mVertexData.vertexBufferBinding->getBuffer(0);
            float* pFloat = static_cast<float*>(vbuf->lock(HardwareBuffer::HBL_DISCARD));*/

            ENG_HardwareVertexBuffer vbuf = mVertexData.vertexBufferBinding.getBuffer((short) 0);
            ByteBuffer pFloat = (ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD);
            pFloat.limit(pFloat.capacity());
            pFloat.position(0);
			
			/*
            // near plane (remember frustum is going in -Z direction)
            *pFloat++ = vpLeft;  *pFloat++ = vpTop;    *pFloat++ = -mNearDist;
            *pFloat++ = vpRight; *pFloat++ = vpTop;    *pFloat++ = -mNearDist;

            *pFloat++ = vpRight; *pFloat++ = vpTop;    *pFloat++ = -mNearDist;
            *pFloat++ = vpRight; *pFloat++ = vpBottom; *pFloat++ = -mNearDist;

            *pFloat++ = vpRight; *pFloat++ = vpBottom; *pFloat++ = -mNearDist;
            *pFloat++ = vpLeft;  *pFloat++ = vpBottom; *pFloat++ = -mNearDist;

            *pFloat++ = vpLeft;  *pFloat++ = vpBottom; *pFloat++ = -mNearDist;
            *pFloat++ = vpLeft;  *pFloat++ = vpTop;    *pFloat++ = -mNearDist;*/
            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);
			
			/*// far plane (remember frustum is going in -Z direction)
            *pFloat++ = farLeft;  *pFloat++ = farTop;    *pFloat++ = -farDist;
            *pFloat++ = farRight; *pFloat++ = farTop;    *pFloat++ = -farDist;

            *pFloat++ = farRight; *pFloat++ = farTop;    *pFloat++ = -farDist;
            *pFloat++ = farRight; *pFloat++ = farBottom; *pFloat++ = -farDist;

            *pFloat++ = farRight; *pFloat++ = farBottom; *pFloat++ = -farDist;
            *pFloat++ = farLeft;  *pFloat++ = farBottom; *pFloat++ = -farDist;

            *pFloat++ = farLeft;  *pFloat++ = farBottom; *pFloat++ = -farDist;
            *pFloat++ = farLeft;  *pFloat++ = farTop;    *pFloat++ = -farDist;*/

            pFloat.putFloat(farLeft);
            pFloat.putFloat(farTop);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farRight);
            pFloat.putFloat(farTop);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farRight);
            pFloat.putFloat(farTop);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farRight);
            pFloat.putFloat(farBottom);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farRight);
            pFloat.putFloat(farBottom);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farLeft);
            pFloat.putFloat(farBottom);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farLeft);
            pFloat.putFloat(farBottom);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farLeft);
            pFloat.putFloat(farTop);
            pFloat.putFloat(-farDist);
			
			/*// Sides of the pyramid
            *pFloat++ = 0.0f;    *pFloat++ = 0.0f;   *pFloat++ = 0.0f;
            *pFloat++ = vpLeft;  *pFloat++ = vpTop;  *pFloat++ = -mNearDist;

            *pFloat++ = 0.0f;    *pFloat++ = 0.0f;   *pFloat++ = 0.0f;
            *pFloat++ = vpRight; *pFloat++ = vpTop;    *pFloat++ = -mNearDist;

            *pFloat++ = 0.0f;    *pFloat++ = 0.0f;   *pFloat++ = 0.0f;
            *pFloat++ = vpRight; *pFloat++ = vpBottom; *pFloat++ = -mNearDist;

            *pFloat++ = 0.0f;    *pFloat++ = 0.0f;   *pFloat++ = 0.0f;
            *pFloat++ = vpLeft;  *pFloat++ = vpBottom; *pFloat++ = -mNearDist;*/

            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);

            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);
            pFloat.putFloat(0.0f);

            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);
			
			/*// Sides of the box

            *pFloat++ = vpLeft;  *pFloat++ = vpTop;  *pFloat++ = -mNearDist;
            *pFloat++ = farLeft;  *pFloat++ = farTop;  *pFloat++ = -farDist;

            *pFloat++ = vpRight; *pFloat++ = vpTop;    *pFloat++ = -mNearDist;
            *pFloat++ = farRight; *pFloat++ = farTop;    *pFloat++ = -farDist;

            *pFloat++ = vpRight; *pFloat++ = vpBottom; *pFloat++ = -mNearDist;
            *pFloat++ = farRight; *pFloat++ = farBottom; *pFloat++ = -farDist;

            *pFloat++ = vpLeft;  *pFloat++ = vpBottom; *pFloat++ = -mNearDist;
            *pFloat++ = farLeft;  *pFloat++ = farBottom; *pFloat++ = -farDist;*/

            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(farLeft);
            pFloat.putFloat(farTop);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpTop);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(farRight);
            pFloat.putFloat(farTop);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(vpRight);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(vpLeft);
            pFloat.putFloat(vpBottom);
            pFloat.putFloat(-this.mNearDist);

            pFloat.putFloat(farRight);
            pFloat.putFloat(farBottom);
            pFloat.putFloat(-farDist);

            pFloat.putFloat(farLeft);
            pFloat.putFloat(farBottom);
            pFloat.putFloat(-farDist);

            vbuf.unlock();

            mRecalcVertexData = false;
        }
    }

    protected boolean isViewOutOfDate() {
        if (mParentNode != null) {
			/*if (mRecalcView ||
                mParentNode->_getDerivedOrientation() != mLastParentOrientation ||
                mParentNode->_getDerivedPosition() != mLastParentPosition)
            {
                // Ok, we're out of date with SceneNode we're attached to
                mLastParentOrientation = mParentNode->_getDerivedOrientation();
                mLastParentPosition = mParentNode->_getDerivedPosition();
                mRecalcView = true;
            }*/

            if ((mRecalcView) ||
                    (mParentNode._getDerivedOrientationCopy().notEquals(mLastParentOrientation)) ||
                    (mParentNode._getDerivedPositionCopy().notEquals(mLastParentPosition))) {
                mLastParentOrientation.set(mParentNode._getDerivedOrientationCopy());
                mLastParentPosition.set(mParentNode._getDerivedPositionCopy());
                mRecalcView = true;
            }
        }
        //No oblique stuff
        return mRecalcView;
    }

    protected boolean isFrustumOutOfDate() {
        //Rid the oblique projection..cannot have multiple inheritance
        return mRecalcFrustum;
    }

    protected void invalidateFrustum() {
        mRecalcFrustum = true;
        mRecalcFrustumPlanes = true;
        mRecalcWorldSpaceCorners = true;
        mRecalcVertexData = true;
    }

    protected void invalidateView() {
        mRecalcView = true;
        mRecalcFrustumPlanes = true;
        mRecalcWorldSpaceCorners = true;
    }

    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue) {
        

    }

    @Override
    public void getBoundingBox(ENG_AxisAlignedBox ret) {
        
        ret.set(mBoundingBox);
    }

    @Override
    public float getBoundingRadius() {
        
        return (mFarDist == 0.0f) ? 100000.0f : mFarDist;
    }

    public ENG_Material getMaterial() {
        return mMaterial;
    }

    public void getRenderOperation(ENG_RenderOperation op) {
        updateVertexData();
        op.operationType = ENG_RenderOperation.OperationType.OT_LINE_LIST;
        op.useIndexes = false;
        op.vertexData = mVertexData;
    }

    public void getWorldTransforms(ENG_Matrix4 xForm) {
        if (mParentNode != null) {
            xForm.set(mParentNode._getFullTransform());
        } else {
            xForm.setIdentity();
        }
    }

    public float getSquaredViewDepth(ENG_Camera cam, ENG_Vector4D temp0,
                                     ENG_Vector4D temp1) {
        if (mParentNode != null) {
            cam.getDerivedPosition(temp0);
            mParentNode._getDerivedPosition(temp1);
            temp0.subInPlace(temp1);
            return temp0.squaredLength();
        } else {
            return 0.0f;
        }
    }

    public ENG_Vector4D[] getWorldSpaceCorners() {
        updateWorldSpaceCorners();
        return mWorldSpaceCorners;
    }

    public boolean projectSphere(ENG_Sphere sphere, float[] pos) {

        updateView();

        if (eyeSpacePos == null) {
            eyeSpacePos = new ENG_Vector4D();
        }
        mViewMatrix.transformAffine(sphere.center, eyeSpacePos);

        pos[0] = pos[3] = -1.0f;
        pos[2] = pos[1] = 1.0f;

        if (eyeSpacePos.z < 0.0f) {
			/*updateFrustum();
			const Matrix4& projMatrix = getProjectionMatrix();
            Real r = sphere.getRadius();
			Real rsq = r * r;

            // early-exit
            if (eyeSpacePos.squaredLength() <= rsq)
                return false;

			Real Lxz = Math::Sqr(eyeSpacePos.x) + Math::Sqr(eyeSpacePos.z);
			Real Lyz = Math::Sqr(eyeSpacePos.y) + Math::Sqr(eyeSpacePos.z);*/

            updateFrustum();
            if (tempProjMatrix == null) {
                tempProjMatrix = new ENG_Matrix4();
            }
            getProjectionMatrix(tempProjMatrix);
            float r = sphere.radius;
            float rsq = r * r;

            if (eyeSpacePos.squaredLength() <= rsq) {
                return false;
            }

            float Lxz = ENG_Math.sqr(eyeSpacePos.x) + ENG_Math.sqr(eyeSpacePos.z);
            float Lyz = ENG_Math.sqr(eyeSpacePos.y) + ENG_Math.sqr(eyeSpacePos.z);
			
			/*// Find the tangent planes to the sphere
			// XZ first
			// calculate quadratic discriminant: b*b - 4ac
			// x = Nx
			// a = Lx^2 + Lz^2
			// b = -2rLx
			// c = r^2 - Lz^2
			Real a = Lxz;
			Real b = -2.0f * r * eyeSpacePos.x;
			Real c = rsq - Math::Sqr(eyeSpacePos.z);
			Real D = b*b - 4.0f*a*c;*/

            float a = Lxz;
            float b = -2.0f * r * eyeSpacePos.x;
            float c = rsq - ENG_Math.sqr(eyeSpacePos.z);
            float D = b * b - 4.0f * a * c;

            if (D > 0.0f) {
				/*Real sqrootD = Math::Sqrt(D);
				// solve the quadratic to get the components of the normal
				Real Nx0 = (-b + sqrootD) / (2 * a);
				Real Nx1 = (-b - sqrootD) / (2 * a);
				
				// Derive Z from this
				Real Nz0 = (r - Nx0 * eyeSpacePos.x) / eyeSpacePos.z;
				Real Nz1 = (r - Nx1 * eyeSpacePos.x) / eyeSpacePos.z;*/

                float sqrootD = ENG_Math.sqrt(D);

                float Nx0 = (-b + sqrootD) / (2.0f * a);
                float Nx1 = (-b - sqrootD) / (2.0f * a);

                float Nz0 = (r - Nx0 * eyeSpacePos.x) / eyeSpacePos.z;
                float Nz1 = (r - Nx1 * eyeSpacePos.x) / eyeSpacePos.z;

                // Get the point of tangency
                // Only consider points of tangency in front of the camera
                float Pz0 = (Lxz - rsq) /
                        (eyeSpacePos.z - ((Nz0 / Nx0) * eyeSpacePos.x));

                if (Pz0 < 0.0f) {
					/*// Project point onto near plane in worldspace
					Real nearx0 = (Nz0 * mNearDist) / Nx0;
					// now we need to map this to viewport coords
					// use projection matrix since that will take into account all factors
					Vector3 relx0 = projMatrix * Vector3(nearx0, 0, -mNearDist);

					// find out whether this is a left side or right side
					Real Px0 = -(Pz0 * Nz0) / Nx0;
					if (Px0 > eyeSpacePos.x)
					{
						*right = std::min(*right, relx0.x);
					}
					else
					{
						*left = std::max(*left, relx0.x);
					}*/

                    float nearx0 = (Nz0 * mNearDist) / Nx0;
                    if (relx0 == null) {
                        relx0 = new ENG_Vector4D();
                    }
                    relx0.set(nearx0, 0.0f, -mNearDist);
                    mProjMatrix.transform(relx0);

                    float Px0 = -(Pz0 * Nz0) / Nx0;

                    if (Px0 > eyeSpacePos.x) {
                        pos[2] = Math.min(pos[2], relx0.x);
                    } else {
                        pos[0] = Math.max(pos[0], relx0.x);
                    }
                }
                float Pz1 = (Lxz - rsq) /
                        (eyeSpacePos.z - ((Nz1 / Nx1) * eyeSpacePos.x));

                if (Pz1 < 0.0f) {
					/*// Project point onto near plane in worldspace
					Real nearx1 = (Nz1 * mNearDist) / Nx1;
					// now we need to map this to viewport coords
					// use projection matrix since that will take into account all factors
					Vector3 relx1 = projMatrix * Vector3(nearx1, 0, -mNearDist);

					// find out whether this is a left side or right side
					Real Px1 = -(Pz1 * Nz1) / Nx1;
					if (Px1 > eyeSpacePos.x)
					{
						*right = std::min(*right, relx1.x);
					}
					else
					{
						*left = std::max(*left, relx1.x);
					}*/

                    float nearx1 = (Nz1 * mNearDist) / Nx1;

                    if (relx1 == null) {
                        relx1 = new ENG_Vector4D();
                    }
                    relx1.set(nearx1, 0.0f, -mNearDist);
                    mProjMatrix.transform(relx1);

                    float Px1 = -(Pz1 * Nz1) / Nx1;

                    if (Px1 > eyeSpacePos.x) {
                        pos[2] = Math.min(pos[2], relx1.x);
                    } else {
                        pos[0] = Math.max(pos[0], relx1.x);
                    }
                }
            }
			/*
			// Now YZ 
			// calculate quadratic discriminant: b*b - 4ac
			// x = Ny
			// a = Ly^2 + Lz^2
			// b = -2rLy
			// c = r^2 - Lz^2
			a = Lyz;
			b = -2.0f * r * eyeSpacePos.y;
			c = rsq - Math::Sqr(eyeSpacePos.z);
			D = b*b - 4.0f*a*c;*/

            a = Lyz;
            b = -2.0f * r * eyeSpacePos.y;
            c = rsq - ENG_Math.sqr(eyeSpacePos.z);
            D = b * b - 4.0f * a * c;

            if (D > 0.0f) {
				/*Real sqrootD = Math::Sqrt(D);
				// solve the quadratic to get the components of the normal
				Real Ny0 = (-b + sqrootD) / (2 * a);
				Real Ny1 = (-b - sqrootD) / (2 * a);

				// Derive Z from this
				Real Nz0 = (r - Ny0 * eyeSpacePos.y) / eyeSpacePos.z;
				Real Nz1 = (r - Ny1 * eyeSpacePos.y) / eyeSpacePos.z;

				// Get the point of tangency
				// Only consider points of tangency in front of the camera
				Real Pz0 = (Lyz - rsq) / (eyeSpacePos.z - ((Nz0 / Ny0) * eyeSpacePos.y));*/
                float sqrootD = ENG_Math.sqrt(D);

                float Ny0 = (-b + sqrootD) / (2.0f * a);
                float Ny1 = (-b - sqrootD) / (2.0f * a);

                float Nz0 = (r - Ny0 * eyeSpacePos.y) / eyeSpacePos.z;
                float Nz1 = (r - Ny1 * eyeSpacePos.y) / eyeSpacePos.z;

                float Pz0 = (Lyz - rsq) /
                        (eyeSpacePos.z - ((Nz0 / Ny0) * eyeSpacePos.y));

                if (Pz0 < 0.0f) {
					/*// Project point onto near plane in worldspace
					Real neary0 = (Nz0 * mNearDist) / Ny0;
					// now we need to map this to viewport coords
					// use projection matriy since that will take into account all factors
					Vector3 rely0 = projMatrix * Vector3(0, neary0, -mNearDist);

					// find out whether this is a top side or bottom side
					Real Py0 = -(Pz0 * Nz0) / Ny0;
					if (Py0 > eyeSpacePos.y)
					{
						*top = std::min(*top, rely0.y);
					}
					else
					{
						*bottom = std::max(*bottom, rely0.y);
					}*/

                    float neary0 = (Nz0 * mNearDist) / Ny0;

                    if (rely0 == null) {
                        rely0 = new ENG_Vector4D();
                    }

                    rely0.set(0.0f, neary0, -mNearDist);
                    mProjMatrix.transform(rely0);

                    float Py0 = -(Pz0 * Nz0) / Ny0;

                    if (Py0 > eyeSpacePos.y) {
                        pos[1] = Math.min(pos[1], rely0.y);
                    } else {
                        pos[3] = Math.max(pos[3], rely0.y);
                    }
                }

                float Pz1 = (Lyz - rsq) /
                        (eyeSpacePos.z - ((Nz1 / Ny1) * eyeSpacePos.y));
                if (Pz1 < 0.0f) {
					/*// Project point onto near plane in worldspace
					Real neary1 = (Nz1 * mNearDist) / Ny1;
					// now we need to map this to viewport coords
					// use projection matriy since that will take into account all factors
					Vector3 rely1 = projMatrix * Vector3(0, neary1, -mNearDist);

					// find out whether this is a top side or bottom side
					Real Py1 = -(Pz1 * Nz1) / Ny1;
					if (Py1 > eyeSpacePos.y)
					{
						*top = std::min(*top, rely1.y);
					}
					else
					{
						*bottom = std::max(*bottom, rely1.y);
					}*/

                    float neary1 = (Nz1 * mNearDist) / Ny1;

                    if (rely1 == null) {
                        rely1 = new ENG_Vector4D();
                    }

                    rely1.set(0.0f, neary1, -mNearDist);
                    mProjMatrix.transform(rely1);

                    float Py1 = -(Pz1 * Nz1) / Ny1;

                    if (Py1 > eyeSpacePos.y) {
                        pos[1] = Math.min(pos[1], rely1.y);
                    } else {
                        pos[3] = Math.max(pos[3], rely1.y);
                    }
                }
            }
        }
		
		/*return (*left != -1.0f) || (*top != 1.0f) || (*right != 1.0f) || (*bottom != -1.0f);*/
        return (pos[0] != -1.0f) || (pos[1] != 1.0f) ||
                (pos[2] != 1.0f) || (pos[3] != -1.0f);
    }

    public void setFrustumExtends(float left, float right, float top, float bottom) {
        mFrustumExtentsManuallySet = true;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;

        invalidateFrustum();
    }

    public void resetFrustumExtents() {
        mFrustumExtentsManuallySet = false;
        invalidateFrustum();
    }

    /*0 left 1 right 2 top 3 bottom*/
    public void getFrustumExtents(float[] out) {
        updateFrustum();

        out[0] = left;
        out[1] = right;
        out[2] = top;
        out[3] = bottom;
    }

    public void setOrientationMode(OrientationMode mode) {
        mOrientationMode = mode;

        invalidateFrustum();
    }

    public OrientationMode getOrientationMode() {
        return mOrientationMode;
    }

    public void enableReflection(ENG_Plane p) {
        mReflect = true;
        mReflectPlane = p;
        //    mLinkedReflectPlane = null;
        ENG_Math.buildReflectionMatrix(p, mReflectMatrix);
        invalidateView();
    }

    public void disableReflection() {
        mReflect = false;
        //    mLinkedReflectPlane = 0;
        mLastLinkedReflectionPlane.normal.set(ENG_Math.VEC4_ZERO);
        invalidateView();
    }

    public boolean isReflected() {
        return mReflect;
    }

    public ENG_Matrix4 getReflectionMatrix() {
        return mReflectMatrix;
    }

    public ENG_Matrix4 getReflectionMatrixCopy() {
        return new ENG_Matrix4(mReflectMatrix);
    }

    public void getReflectionMatrix(ENG_Matrix4 ret) {
        ret.set(mReflectMatrix);
    }

    public ENG_Plane getReflectionPlane() {
        return mReflectPlane;
    }

    public ENG_Plane getReflectionPlaneCopy() {
        return new ENG_Plane(mReflectPlane);
    }

    public void getReflectionPlane(ENG_Plane ret) {
        ret.set(mReflectPlane);
    }

    public void setFOVy(ENG_Radian spotlightOuterAngle) {
        
        setFOVy(spotlightOuterAngle.valueRadians());
    }

    @Override
    public void visitRenderables(Visitor visitor, boolean debugRenderables) {
        

    }

    @Override
    public String getMovableType() {
        
        return "Frustum";
    }

    /**
     * Used to set the view matrix from the native side for when we need to do culling in java
     * with the matrices from native.
     * @param viewMatrix
     */
    public void setViewMatrix(ENG_Matrix4 viewMatrix) {
        mViewMatrix.set(viewMatrix);
    }

    public void setProjectionMatrix(ENG_Matrix4 projMatrix) {
        mProjMatrix.set(projMatrix);
    }

}
