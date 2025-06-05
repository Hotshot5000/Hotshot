/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix3;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_RealRect;
import headwayent.hotshotengine.ENG_Sphere;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_Common.SortMode;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_HardwareIndexBuffer.IndexType;
import headwayent.hotshotengine.renderer.ENG_RenderOperation.OperationType;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.Capabilities;
import headwayent.hotshotengine.renderer.ENG_RenderableImpl.Visitor;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

@Deprecated
public class ENG_BillboardSet extends ENG_MovableObject implements
        ENG_Renderable {

    /**
     * Enum covering what exactly a billboard's position means (center,
     * top-left etc).
     *
     * @see BillboardSet::setBillboardOrigin
     */
    public enum BillboardOrigin {
        BBO_TOP_LEFT,
        BBO_TOP_CENTER,
        BBO_TOP_RIGHT,
        BBO_CENTER_LEFT,
        BBO_CENTER,
        BBO_CENTER_RIGHT,
        BBO_BOTTOM_LEFT,
        BBO_BOTTOM_CENTER,
        BBO_BOTTOM_RIGHT
    }

    /**
     * The rotation type of billboard.
     */
    public enum BillboardRotationType {
        /// Rotate the billboard's vertices around their facing direction
        BBR_VERTEX,
        /// Rotate the billboard's texture coordinates
        BBR_TEXCOORD
    }

    /**
     * The type of billboard to use.
     */
    public enum BillboardType {
        /// Standard point billboard (default), always faces the camera completely and is always upright
        BBT_POINT,
        /// Billboards are oriented around a shared direction vector (used as Y axis) and only rotate around this to face the camera
        BBT_ORIENTED_COMMON,
        /// Billboards are oriented around their own direction vector (their own Y axis) and only rotate around this to face the camera
        BBT_ORIENTED_SELF,
        /// Billboards are perpendicular to a shared direction vector (used as Z axis, the facing direction) and X, Y axis are determined by a shared up-vertor
        BBT_PERPENDICULAR_COMMON,
        /// Billboards are perpendicular to their own direction vector (their own Z axis, the facing direction) and X, Y axis are determined by a shared up-vertor
        BBT_PERPENDICULAR_SELF
    }

    /// Bounds of all billboards in this set
    protected final ENG_AxisAlignedBox mAABB = new ENG_AxisAlignedBox();
    /// Bounding radius
    protected float mBoundingRadius;

    /// Origin of each billboard
    protected BillboardOrigin mOriginType = BillboardOrigin.BBO_CENTER;
    /// Rotation type of each billboard
    protected BillboardRotationType mRotationType = BillboardRotationType.BBR_TEXCOORD;

    /// Default width of each billboard
    protected float mDefaultWidth;
    /// Default height of each billboard
    protected float mDefaultHeight;

    /// Name of the material to use
    protected String mMaterialName;
    /// Pointer to the material to use
    protected ENG_Material mpMaterial;

    /// True if no billboards in this set have been resized - greater efficiency.
    protected boolean mAllDefaultSize = true;

    /// Flag indicating whether to autoextend pool
    protected boolean mAutoExtendPool = true;

    /// Flag indicating whether the billboards has to be sorted
    protected boolean mSortingEnabled;

    // Use 'true' billboard to cam position facing, rather than camera direcion
    protected boolean mAccurateFacing;

    protected boolean mAllDefaultRotation = true;
    protected boolean mWorldSpace;

    /** @noinspection deprecation*/
    protected final LinkedList<ENG_Billboard> mActiveBillboards =
            new LinkedList<>();
    /** @noinspection deprecation*/
    protected final LinkedList<ENG_Billboard> mFreeBillboards =
            new LinkedList<>();
    /** @noinspection deprecation*/
    protected final ArrayList<ENG_Billboard> mBillboardPool =
            new ArrayList<>();

    /// The vertex position data for all billboards in this set.
    protected ENG_VertexData mVertexData;
    /// Shortcut to main buffer (positions, colours, texture coords)
    protected ENG_HardwareVertexBuffer mMainBuf;
    /// Locked pointer to buffer
    protected FloatBuffer mLockPtr;
    /// Boundary offsets based on origin and camera orientation
    /// Vector3 vLeftOff, vRightOff, vTopOff, vBottomOff;
    /// Final vertex offsets, used where sizes all default to save calcs
    protected final ENG_Vector4D[] mVOffset = new ENG_Vector4D[4];
    /// Current camera
    protected ENG_Camera mCurrentCamera;
    // Parametric offsets of origin
    protected float mLeftOff, mRightOff, mTopOff, mBottomOff;
    // Camera axes in billboard space
    protected final ENG_Vector4D mCamX = new ENG_Vector4D(true);
    protected final ENG_Vector4D mCamY = new ENG_Vector4D(true);
    // Camera direction in billboard space
    protected final ENG_Vector4D mCamDir = new ENG_Vector4D();
    // Camera orientation in billboard space
    protected final ENG_Quaternion mCamQ = new ENG_Quaternion();
    // Camera position in billboard space
    protected final ENG_Vector4D mCamPos = new ENG_Vector4D(true);

    /// The vertex index data for all billboards in this set (1 set only)
    //unsigned short* mpIndexes;
    protected ENG_IndexData mIndexData;

    /// Flag indicating whether each billboard should be culled separately (default: false)
    protected boolean mCullIndividual;


    protected final ArrayList<ENG_RealRect> mTextureCoords =
            new ArrayList<>();

    /// The type of billboard to render
    protected BillboardType mBillboardType = BillboardType.BBT_POINT;

    /// Common direction for billboards of type BBT_ORIENTED_COMMON and BBT_PERPENDICULAR_COMMON
    protected final ENG_Vector4D mCommonDirection = new ENG_Vector4D(ENG_Math.VEC4_Z_UNIT);
    /// Common up-vector for billboards of type BBT_PERPENDICULAR_SELF and BBT_PERPENDICULAR_COMMON
    protected final ENG_Vector4D mCommonUpVector = new ENG_Vector4D(ENG_Math.VEC4_Y_UNIT);

    private final ENG_Sphere sph = new ENG_Sphere();
    private final ENG_Matrix4[] xworld = new ENG_Matrix4[]{new ENG_Matrix4()};

    /** @noinspection deprecation*/
    protected boolean billboardVisible(ENG_Camera cam, ENG_Billboard bill) {
        // Return always visible if not culling individually
        if (!mCullIndividual) return true;

        getWorldTransforms(xworld);

        sph.setCenter(xworld[0].transformAffineRet(bill.mPosition));

        if (bill.mOwnDimensions) {
            sph.setRadius(Math.max(bill.mWidth, bill.mHeight));
        } else {
            sph.setRadius(Math.max(mDefaultWidth, mDefaultHeight));
        }

        return cam.isVisible(sph);
    }

    protected short mNumVisibleBillboards;

    /** @noinspection deprecation*/
    protected void increasePool(int size) {
        int oldSize = mBillboardPool.size();

        for (int i = oldSize; i < size; ++i) {
            mBillboardPool.add(new ENG_Billboard());
        }
    }

    protected void genBillboardAxes(ENG_Vector4D pX, ENG_Vector4D pY) {
        genBillboardAxes(pX, pY, null);
    }

    /** @noinspection deprecation*/
    protected void genBillboardAxes(ENG_Vector4D pX, ENG_Vector4D pY,
                                    ENG_Billboard bb) {
        // If we're using accurate facing, recalculate camera direction per BB
        if (mAccurateFacing &&
                (mBillboardType == BillboardType.BBT_POINT ||
                        mBillboardType == BillboardType.BBT_ORIENTED_COMMON ||
                        mBillboardType == BillboardType.BBT_ORIENTED_SELF)) {
            // cam -> bb direction
            bb.mPosition.subFull(mCamPos, mCamDir);
            mCamDir.normalize();
        }

        switch (mBillboardType) {
            case BBT_POINT:
                if (mAccurateFacing) {
                    // Point billboards will have 'up' based on but not equal to cameras
                    // Use pY temporarily to avoid allocation
                    mCamQ.mul(ENG_Math.VEC4_Y_UNIT, pY);
                    mCamDir.crossProduct(pY, pX);
                    pX.normalize();
                    pX.crossProduct(mCamDir, pY); // both normalised already
                } else {
                    // Get camera axes for X and Y (depth is irrelevant)
                    mCamQ.mul(ENG_Math.VEC4_X_UNIT, pX);
                    mCamQ.mul(ENG_Math.VEC4_Y_UNIT, pY);
                }
                break;
            case BBT_ORIENTED_COMMON:
                // Y-axis is common direction
                // X-axis is cross with camera direction
                pY.set(mCommonDirection);
                mCamDir.crossProduct(pY, pX);
                pX.normalize();
                break;

            case BBT_ORIENTED_SELF:
                // Y-axis is direction
                // X-axis is cross with camera direction
                // Scale direction first
                pY.set(bb.mDirection);
                mCamDir.crossProduct(pY, pX);
                pX.normalize();
                break;

            case BBT_PERPENDICULAR_COMMON:
                // X-axis is up-vector cross common direction
                // Y-axis is common direction cross X-axis
                mCommonUpVector.crossProduct(mCommonDirection, pX);
                mCommonDirection.crossProduct(pX, pY);
                break;

            case BBT_PERPENDICULAR_SELF:
                // X-axis is up-vector cross own direction
                // Y-axis is own direction cross X-axis
                mCommonUpVector.crossProduct(bb.mDirection, pX);
                pX.normalize();
                bb.mDirection.crossProduct(pX, pY); // both should be normalised
                break;
        }

    }

    /**
     * 0 - left 1 - right 2 - top 3 - bottom
     *
     * @param f
     */
    protected void getParametricOffsets(float[] f) {
        float left = 0.0f, right = 0.0f, top = 0.0f, bottom = 0.0f;
        switch (mOriginType) {
            case BBO_TOP_LEFT:
                left = 0.0f;
                right = 1.0f;
                top = 0.0f;
                bottom = -1.0f;
                break;

            case BBO_TOP_CENTER:
                left = -0.5f;
                right = 0.5f;
                top = 0.0f;
                bottom = -1.0f;
                break;

            case BBO_TOP_RIGHT:
                left = -1.0f;
                right = 0.0f;
                top = 0.0f;
                bottom = -1.0f;
                break;

            case BBO_CENTER_LEFT:
                left = 0.0f;
                right = 1.0f;
                top = 0.5f;
                bottom = -0.5f;
                break;

            case BBO_CENTER:
                left = -0.5f;
                right = 0.5f;
                top = 0.5f;
                bottom = -0.5f;
                break;

            case BBO_CENTER_RIGHT:
                left = -1.0f;
                right = 0.0f;
                top = 0.5f;
                bottom = -0.5f;
                break;

            case BBO_BOTTOM_LEFT:
                left = 0.0f;
                right = 1.0f;
                top = 1.0f;
                bottom = 0.0f;
                break;

            case BBO_BOTTOM_CENTER:
                left = -0.5f;
                right = 0.5f;
                top = 1.0f;
                bottom = 0.0f;
                break;

            case BBO_BOTTOM_RIGHT:
                left = -1.0f;
                right = 0.0f;
                top = 1.0f;
                bottom = 0.0f;
                break;
        }
        f[0] = left;
        f[1] = right;
        f[2] = top;
        f[3] = bottom;
    }

    private final ENG_Integer colour = new ENG_Integer();

    /** @noinspection deprecation*/
    protected void genVertices(ENG_Vector4D[] offsets, ENG_Billboard bb) {
        ENG_RenderRoot.getRenderRoot().convertColourValue(bb.mColour, colour);

        // Texcoords
        assert (bb.mUseTexcoordRect || bb.mTexcoordIndex < mTextureCoords.size());

        ENG_RealRect r =
                (bb.mUseTexcoordRect)
                        ? bb.mTexcoordRect : mTextureCoords.get(bb.mTexcoordIndex);


        if (mPointRendering) {
            // Single vertex per billboard, ignore offsets
            // position
            mLockPtr.put(bb.mPosition.x);
            mLockPtr.put(bb.mPosition.y);
            mLockPtr.put(bb.mPosition.z);

            // Colour
            // Convert float* to RGBA*
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
        } else if (mAllDefaultRotation || bb.mRotation == 0.0f) {
            // Left-top
            // Positions
            mLockPtr.put(offsets[0].x + bb.mPosition.x);
            mLockPtr.put(offsets[0].y + bb.mPosition.y);
            mLockPtr.put(offsets[0].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.left);
            mLockPtr.put(r.top);


            // Right-top
            // Positions
            mLockPtr.put(offsets[1].x + bb.mPosition.x);
            mLockPtr.put(offsets[1].y + bb.mPosition.y);
            mLockPtr.put(offsets[1].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.right);
            mLockPtr.put(r.top);


            // Left-bottom
            // Positions
            mLockPtr.put(offsets[2].x + bb.mPosition.x);
            mLockPtr.put(offsets[2].y + bb.mPosition.y);
            mLockPtr.put(offsets[2].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.left);
            mLockPtr.put(r.bottom);


            // Right-bottom
            // Positions

            mLockPtr.put(offsets[3].x + bb.mPosition.x);
            mLockPtr.put(offsets[3].y + bb.mPosition.y);
            mLockPtr.put(offsets[3].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.right);
            mLockPtr.put(r.bottom);

        } else if (mRotationType == BillboardRotationType.BBR_VERTEX) {
            // TODO: Find out why this rotates to right and tex coord
            // rotate to left.
            // TODO: Cache axis when billboard type is BBT_POINT or BBT_PERPENDICULAR_COMMON
            ENG_Vector4D axis = (offsets[3].subAsPt(offsets[0]))
                    .crossProduct(offsets[2].subAsPt(offsets[1])).normalizedCopy();

            ENG_Matrix3 rotation = new ENG_Matrix3();
            rotation.fromAxisAngle(axis, bb.mRotation);

            ENG_Vector3D pt = new ENG_Vector3D();

            // Left-top
            // Positions
            rotation.transform(offsets[0], pt);
            mLockPtr.put(pt.x + bb.mPosition.x);
            mLockPtr.put(pt.y + bb.mPosition.y);
            mLockPtr.put(pt.z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.left);
            mLockPtr.put(r.top);


            // Right-top
            // Positions
            rotation.transform(offsets[1], pt);
            mLockPtr.put(pt.x + bb.mPosition.x);
            mLockPtr.put(pt.y + bb.mPosition.y);
            mLockPtr.put(pt.z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.right);
            mLockPtr.put(r.top);


            // Left-bottom
            // Positions
            rotation.transform(offsets[2], pt);
            mLockPtr.put(pt.x + bb.mPosition.x);
            mLockPtr.put(pt.y + bb.mPosition.y);
            mLockPtr.put(pt.z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.left);
            mLockPtr.put(r.bottom);


            // Right-bottom
            // Positions
            rotation.transform(offsets[3], pt);
            mLockPtr.put(pt.x + bb.mPosition.x);
            mLockPtr.put(pt.y + bb.mPosition.y);
            mLockPtr.put(pt.z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(r.right);
            mLockPtr.put(r.bottom);

        } else {
            float cos_rot = (ENG_Math.cos(bb.mRotation));
            float sin_rot = (ENG_Math.sin(bb.mRotation));

            float width = (r.right - r.left) / 2;
            float height = (r.bottom - r.top) / 2;
            float mid_u = r.left + width;
            float mid_v = r.top + height;

            float cos_rot_w = cos_rot * width;
            float cos_rot_h = cos_rot * height;
            float sin_rot_w = sin_rot * width;
            float sin_rot_h = sin_rot * height;

            mLockPtr.put(offsets[0].x + bb.mPosition.x);
            mLockPtr.put(offsets[0].y + bb.mPosition.y);
            mLockPtr.put(offsets[0].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(mid_u - cos_rot_w + sin_rot_h);
            mLockPtr.put(mid_v - sin_rot_w - cos_rot_h);

            mLockPtr.put(offsets[1].x + bb.mPosition.x);
            mLockPtr.put(offsets[1].y + bb.mPosition.y);
            mLockPtr.put(offsets[1].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(mid_u + cos_rot_w + sin_rot_h);
            mLockPtr.put(mid_v + sin_rot_w - cos_rot_h);

            mLockPtr.put(offsets[2].x + bb.mPosition.x);
            mLockPtr.put(offsets[2].y + bb.mPosition.y);
            mLockPtr.put(offsets[2].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(mid_u - cos_rot_w - sin_rot_h);
            mLockPtr.put(mid_v - sin_rot_w + cos_rot_h);

            mLockPtr.put(offsets[3].x + bb.mPosition.x);
            mLockPtr.put(offsets[3].y + bb.mPosition.y);
            mLockPtr.put(offsets[3].z + bb.mPosition.z);
            mLockPtr.put(Float.intBitsToFloat(colour.getValue()));
            mLockPtr.put(mid_u + cos_rot_w - sin_rot_h);
            mLockPtr.put(mid_v + sin_rot_w + cos_rot_h);
        }
    }

    protected void genVertOffsets(float inleft, float inright,
                                  float intop, float inbottom,
                                  float width, float height,
                                  ENG_Vector4D x, ENG_Vector4D y, ENG_Vector4D[] pDestVec) {
        ENG_Vector4D vLeftOff = new ENG_Vector4D(),
                vRightOff = new ENG_Vector4D(),
                vTopOff = new ENG_Vector4D(),
                vBottomOff = new ENG_Vector4D();
        /* Calculate default offsets. Scale the axes by
           parametric offset and dimensions, ready to be added to
           positions.
        */

        x.mul(inleft * width, vLeftOff);
        x.mul(inright * width, vRightOff);
        y.mul(intop * height, vTopOff);
        y.mul(inbottom * height, vBottomOff);

        // Make final offsets to vertex positions
        vLeftOff.add(vTopOff, pDestVec[0]);
        vRightOff.add(vTopOff, pDestVec[1]);
        vLeftOff.add(vBottomOff, pDestVec[2]);
        vRightOff.add(vBottomOff, pDestVec[3]);
    }

    /** @noinspection deprecation*/
    protected static class SortByDirectionFunctor
            implements Comparator<ENG_Billboard> {

        /// Direction to sort in
        public final ENG_Vector4D sortDir = new ENG_Vector4D();
        private final ENG_Vector4D pos = new ENG_Vector4D(true);

        public SortByDirectionFunctor(ENG_Vector4D dir) {
            sortDir.set(dir);
        }

        public void set(ENG_Vector4D dir) {
            sortDir.set(dir);
        }

        /** @noinspection deprecation */
        @Override
        public int compare(ENG_Billboard lhs, ENG_Billboard rhs) {

            lhs.getPosition(pos);
            float f1 = sortDir.dotProduct(pos);
            rhs.getPosition(pos);
            float f2 = sortDir.dotProduct(pos);
            if (f1 < f2) {
                return -1;
            } else if (f1 > f2) {
                return 1;
            }
            return 0;
        }


    }

    /** @noinspection deprecation*/
    protected static class SortByDistanceFunctor
            implements Comparator<ENG_Billboard> {

        public final ENG_Vector4D sortPos = new ENG_Vector4D();
        private final ENG_Vector4D pos = new ENG_Vector4D(true);
        private final ENG_Vector4D temp = new ENG_Vector4D(true);

        public SortByDistanceFunctor(ENG_Vector4D pos) {
            sortPos.set(pos);
        }

        public void set(ENG_Vector4D pos) {
            sortPos.set(pos);
        }

        /** @noinspection deprecation */
        @Override
        public int compare(ENG_Billboard lhs, ENG_Billboard rhs) {

            lhs.getPosition(pos);
            sortPos.sub(pos, temp);
            float f1 = -temp.squaredLength();
            rhs.getPosition(pos);
            sortPos.sub(pos, temp);
            float f2 = -temp.squaredLength();
            if (f1 < f2) {
                return -1;
            } else if (f1 > f2) {
                return 1;
            }
            return 0;
        }

    }

    /// Use point rendering?
    protected boolean mPointRendering;

    /// Flag indicating whether the HW buffers have been created.
    private boolean mBuffersCreated;
    /// The number of billboard in the pool.
    private int mPoolSize;
    /// Is external billboard data in use?
    private boolean mExternalData;
    /// Tell if vertex buffer should be update automatically.
    private boolean mAutoUpdate = true;
    /// True if the billboard data changed. Will cause vertex buffer update.
    private boolean mBillboardDataChanged = true;

    /**
     * Internal method creates vertex and index buffers.
     */
    private void _createBuffers() {
        if (mPointRendering && mBillboardType != BillboardType.BBT_POINT) {
            throw new ENG_InvalidFieldStateException("Point rendering active but " +
                    "the billboard type is not point!");
        }

        mVertexData = new ENG_VertexData();
        if (mPointRendering) {
            mVertexData.vertexCount = mPoolSize;
        } else {
            mVertexData.vertexCount = mPoolSize * 4;
        }

        mVertexData.vertexStart = 0;

        ENG_VertexDeclaration decl = mVertexData.vertexDeclaration;
        ENG_VertexBufferBinding binding = mVertexData.vertexBufferBinding;

        int offset = 0;
        decl.addElement((short) 0, offset,
                VertexElementType.VET_FLOAT3, VertexElementSemantic.VES_POSITION);
        offset += ENG_VertexElement.getTypeSize(VertexElementType.VET_FLOAT3);
        decl.addElement((short) 0, offset,
                VertexElementType.VET_COLOUR, VertexElementSemantic.VES_DIFFUSE);
        offset += ENG_VertexElement.getTypeSize(VertexElementType.VET_COLOUR);
        // Texture coords irrelevant when enabled point rendering (generated
        // in point sprite mode, and unused in standard point mode)
        if (!mPointRendering) {
            decl.addElement((short) 0, offset,
                    VertexElementType.VET_FLOAT2,
                    VertexElementSemantic.VES_TEXTURE_COORDINATES, (short) 0);
        }

        mMainBuf = ENG_HardwareBufferManager.getSingleton().createVertexBuffer(
                decl.getVertexSize((short) 0),
                mVertexData.vertexCount,
                mAutoUpdate ? Usage.HBU_DYNAMIC_WRITE_ONLY_DISCARDABLE.getUsage() :
                        Usage.HBU_STATIC_WRITE_ONLY.getUsage(), true);

        binding.setBinding((short) 0, mMainBuf);

        if (!mPointRendering) {
            mIndexData = new ENG_IndexData();
            mIndexData.indexStart = 0;
            mIndexData.indexCount = mPoolSize * 6;

            mIndexData.indexBuffer = ENG_HardwareBufferManager.getSingleton()
                    .createIndexBuffer(IndexType.IT_16BIT,
                            mIndexData.indexCount,
                            Usage.HBU_STATIC_WRITE_ONLY.getUsage(), true);

            ShortBuffer pIdx = ((ByteBuffer) mIndexData.indexBuffer.lock(
                    0, mIndexData.indexBuffer.getSizeInBytes(),
                    LockOptions.HBL_DISCARD)).asShortBuffer();

            // Be verty careful with the pool size since it's so easy to get over
            // short's limits!!!

            for (int idx, idxOff, bboard = 0; bboard < mPoolSize; ++bboard) {
                // Do indexes
                idx = bboard * 6;
                idxOff = bboard * 4;

                pIdx.put(idx, (short) idxOff);
                pIdx.put(idx + 1, (short) (idxOff + 2));
                pIdx.put(idx + 2, (short) (idxOff + 1));
                pIdx.put(idx + 3, (short) (idxOff + 1));
                pIdx.put(idx + 4, (short) (idxOff + 2));
                pIdx.put(idx + 5, (short) (idxOff + 3));
            }
            mIndexData.indexBuffer.unlock();
        }
        mBuffersCreated = true;
    }

    /**
     * Internal method destroys vertex and index buffers.
     */
    private void _destroyBuffers(boolean skipGLDelete) {
        if (mVertexData != null) {
            mVertexData.destroy(skipGLDelete);
        }
        if (mIndexData != null) {
            mIndexData.indexBuffer.destroy(skipGLDelete);
        }
        mVertexData = null;
        mIndexData = null;
        mMainBuf = null;
        mBuffersCreated = false;
    }

    public void destroy(boolean skipGLDelete) {
        _destroyBuffers(skipGLDelete);
    }

    private void initVOffsets() {
        for (int i = 0; i < mVOffset.length; ++i) {
            mVOffset[i] = new ENG_Vector4D();
        }
    }

    public ENG_BillboardSet() {
        
        initVOffsets();
    }

    public ENG_BillboardSet(String name, int poolSize) {
        this(name, poolSize, false);
    }

    public ENG_BillboardSet(String name, int poolSize, boolean externalData) {
        super(name);
        
        initVOffsets();
        mPoolSize = poolSize;
        mExternalData = externalData;

        setDefaultDimensions(100, 100);
        setMaterialName("BaseWhite");
        setPoolSize(poolSize);
        mCastShadows = false;
        setTextureStacksAndSlices((byte) 1, (byte) 1);
    }

    private final ENG_Vector4D vecAdjust = new ENG_Vector4D();
    private final ENG_Vector4D newMin = new ENG_Vector4D();
    private final ENG_Vector4D newMax = new ENG_Vector4D();

    /** @noinspection deprecation */
    public ENG_Billboard createBillboard(ENG_Vector4D position, ENG_ColorValue col) {
        if (mFreeBillboards.isEmpty()) {
            if (mAutoExtendPool) {
                setPoolSize(getPoolSize() * 2);
            } else {
                return null;
            }
        }

        ENG_Billboard newBill = mFreeBillboards.getFirst();
        mActiveBillboards.add(mFreeBillboards.remove());
        newBill.setPosition(position);
        newBill.setColour(col);
        newBill.mDirection.set(ENG_Math.VEC4_ZERO);
        newBill.setRotation(0.0f);
        newBill.setTexcoordIndex((short) 0);
        newBill.resetDimensions();
        newBill._notifyOwner(this);

        float adjust = Math.max(mDefaultWidth, mDefaultHeight);
        vecAdjust.set(adjust, adjust, adjust);
        position.sub(vecAdjust, newMin);
        position.add(vecAdjust, newMax);

        mAABB.merge(newMin);
        mAABB.merge(newMax);

        mBoundingRadius = ENG_Math.boundingRadiusFromAABB(mAABB);

        return newBill;
    }

    /** @noinspection deprecation*/
    public ENG_Billboard createBillboard(
            float x, float y, float z, ENG_ColorValue col) {
        return createBillboard(new ENG_Vector4D(x, y, z, 1.0f), col);
    }

    public int getNumBillboards() {
        return mActiveBillboards.size();
    }

    public void setAutoextend(boolean b) {
        mAutoExtendPool = b;
    }

    public boolean getAutoextend() {
        return mAutoExtendPool;
    }

    public void setSortingEnabled(boolean b) {
        mSortingEnabled = b;
    }

    public boolean getSortingEnabled() {
        return mSortingEnabled;
    }

    public void setPoolSize(int size) {
        // If we're driving this from our own data, allocate billboards
        if (!mExternalData) {
            // Never shrink below size()
            int currSize = mBillboardPool.size();
            if (currSize >= size)
                return;

            increasePool(size);

            for (int i = currSize; i < size; ++i) {
                // Add new items to the queue
                mFreeBillboards.add(mBillboardPool.get(i));
            }
        }

        mPoolSize = size;

        _destroyBuffers(false);
    }

    public int getPoolSize() {
        return mBillboardPool.size();
    }

    public void clear() {
        mFreeBillboards.addAll(mActiveBillboards);
        mActiveBillboards.clear();
    }

    /** @noinspection deprecation*/
    public ENG_Billboard getBillboard(int index) {
        return mActiveBillboards.get(index);
    }

    public void removeBillboard(int index) {
        mFreeBillboards.add(mActiveBillboards.remove(index));
    }

    /** @noinspection deprecation*/
    public void removeBillboard(ENG_Billboard b) {
        int index = mActiveBillboards.indexOf(b);
        removeBillboard(index);
    }

    public void setBillboardOrigin(BillboardOrigin o) {
        mOriginType = o;
    }

    public BillboardOrigin getBillboardOrigin() {
        return mOriginType;
    }

    public void setBillboardRotationType(BillboardRotationType t) {
        mRotationType = t;
    }

    public BillboardRotationType getBillboardRotationType() {
        return mRotationType;
    }

    public void setDefaultDimensions(float width, float height) {
        mDefaultWidth = width;
        mDefaultHeight = height;
    }

    public void setDefaultWidth(float width) {
        mDefaultWidth = width;
    }

    public float getDefaultWidth() {
        return mDefaultWidth;
    }

    public void setDefaultHeight(float height) {
        mDefaultHeight = height;
    }

    public float getDefaultHeight() {
        return mDefaultHeight;
    }

    public void setMaterialName(String name) {
        mMaterialName = name;

        mpMaterial = ENG_MaterialManager.getSingleton().getByName(name);
        mpMaterial.load();
    }

    public String getMaterialName() {
        return mMaterialName;
    }

    private final ENG_Quaternion temp1 = new ENG_Quaternion();
    private final ENG_Vector4D temp2 = new ENG_Vector4D(true);
    private final ENG_Vector4D temp3 = new ENG_Vector4D(true);
    private boolean mUseIdentityProjection;
    private boolean mUseIdentityView;

    public void _notifyCurrentCamera(ENG_Camera cam) {
        super._notifyCurrentCamera(cam);

        mCurrentCamera = cam;

        mCurrentCamera.getDerivedOrientation(mCamQ);
        mCurrentCamera.getDerivedPosition(mCamPos);

        if (!mWorldSpace) {
            mParentNode._getDerivedOrientation(temp1);
            temp1.unitInverse();
            temp1.mul(mCamQ, mCamQ);

            mParentNode._getDerivedOrientation(temp1);
            temp1.unitInverse();

            mParentNode._getDerivedPosition(temp2);
            mCamPos.subInPlace(temp2);

            mParentNode._getDerivedScale(temp3);
            temp2.divInPlace(temp3);

            temp1.mul(temp2, mCamPos);
        }

        mCamQ.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, mCamDir);
    }

    public void setTextureCoords(ENG_RealRect[] coords, int numCoords) {
        if (coords == null || numCoords == 0) {
            setTextureStacksAndSlices((byte) 1, (byte) 1);
            return;
        }

        mTextureCoords.clear();
        for (int i = 0; i < numCoords; ++i) {
            mTextureCoords.add(new ENG_RealRect(coords[i]));
        }
    }

    public void setTextureStacksAndSlices(byte stacks, byte slices) {
        if (stacks == 0) stacks = 1;
        if (slices == 0) slices = 1;

        mTextureCoords.clear();
        int size = stacks * slices;
        for (int i = 0; i < size; ++i) {
            mTextureCoords.add(new ENG_RealRect());
        }

        int coordIndex = 0;
        //  spread the U and V coordinates across the rects
        for (int v = 0; v < stacks; ++v) {
            //  (float)X / X is guaranteed to be == 1.0f for X up to 8 million, so
            //  our range of 1..256 is quite enough to guarantee perfect coverage.
            float top = (float) v / (float) stacks;
            float bottom = ((float) v + 1) / (float) stacks;
            for (int u = 0; u < slices; ++u) {
                ENG_RealRect r = mTextureCoords.get(coordIndex);
                r.left = (float) u / (float) slices;
                r.bottom = bottom;
                r.right = ((float) u + 1) / (float) slices;
                r.top = top;
                ++coordIndex;
            }
        }
        assert (coordIndex == size);
    }

    public ENG_RealRect getTextureCoords(ENG_Short numCoords) {
        numCoords.setValue((short) mTextureCoords.size());
        return mTextureCoords.get(0);
    }

    public void setPointRenderingEnabled(boolean enabled) {
        if (enabled && !ENG_RenderRoot.getRenderRoot()
                .getRenderSystem().getCapabilities().hasCapability(
                        Capabilities.RSC_POINT_SPRITES)) {
            enabled = false;
        }

        if (enabled != mPointRendering) {
            mPointRendering = enabled;
            // Different buffer structure (1 or 4 verts per billboard)
            _destroyBuffers(false);
        }
    }

    public boolean isPointRenderingEnabled() {
        return mPointRendering;
    }

    public int getTypeFlags() {
        return ENG_SceneManager.FX_TYPE_MASK;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        // Case auto update buffers changed we have to destroy the current buffers
        // since their usage will be different.
        if (autoUpdate != mAutoUpdate) {
            mAutoUpdate = autoUpdate;
            _destroyBuffers(false);
        }
    }

    public boolean getAutoUpdate() {
        return mAutoUpdate;
    }

    public void notifyBillboardDataChanged() {
        mBillboardDataChanged = true;
    }

    @Override
    public ENG_Material getMaterial() {

        return mpMaterial;
    }

    public void setMaterial(ENG_Material mat) {
        mpMaterial = mat;

        if (mat == null) {
            mpMaterial = ENG_MaterialManager.getSingleton().getByName("BaseWhite");

            if (mpMaterial == null) {
                throw new NullPointerException("dont forget initialize");
            }
        }

        mMaterialName = mpMaterial.getName();

        mpMaterial.load();
    }

    @Override
    public ENG_Technique getTechnique() {

        return getMaterial().getBestTechnique(ENG_Material.defaultLodIndex, this);
    }

    @Override
    public void getRenderOperation(ENG_RenderOperation op) {


        op.vertexData = mVertexData;
        op.vertexData.vertexStart = 0;

        if (mPointRendering) {
            op.operationType = OperationType.OT_POINT_LIST;
            op.useIndexes = false;
            op.indexData = null;
            op.vertexData.vertexCount = mNumVisibleBillboards;
        } else {
            op.operationType = OperationType.OT_TRIANGLE_LIST;
            op.useIndexes = true;

            op.vertexData.vertexCount = mNumVisibleBillboards * 4;

            op.indexData = mIndexData;
            op.indexData.indexCount = mNumVisibleBillboards * 6;
            op.indexData.indexStart = 0;
        }
    }

    @Override
    public boolean preRender(ENG_SceneManager sm, ENG_RenderSystem rsys) {

        return true;
    }

    @Override
    public boolean postRender(ENG_SceneManager sm, ENG_RenderSystem rsys) {

        return true;
    }

    @Override
    public void getWorldTransforms(ENG_Matrix4[] xform) {


        if (mWorldSpace) {
            xform[0].setIdentity();
        } else {
            _getParentNodeFullTransform(xform[0]);
        }
    }

    @Override
    public short getNumWorldTransforms() {

        return 1;
    }

    @Override
    public void setUseIdentityProjection(boolean useIdentityProjection) {


        mUseIdentityProjection = useIdentityProjection;
    }

    @Override
    public boolean getUseIdentityProjection() {

        return mUseIdentityProjection;
    }

    @Override
    public void setUseIdentityView(boolean useIdentityView) {


        mUseIdentityView = useIdentityView;
    }

    @Override
    public boolean getUseIdentityView() {

        return mUseIdentityView;
    }

    private final ENG_Vector4D squaredViewDepthVec = new ENG_Vector4D();
    private final TreeMap<ENG_Integer, ENG_Vector4D> mCustomParameters =
            new TreeMap<>();
    private boolean mPolygonModeOverrideable;

    @Override
    public float getSquaredViewDepth(ENG_Camera cam) {

        assert (mParentNode != null);
        return mParentNode.getSquaredViewDepth(cam, squaredViewDepthVec);
    }

    /** @noinspection deprecation*/
    public void _updateBounds() {
        //Never called
        // Until now
        if (mActiveBillboards.isEmpty()) {
            mAABB.setNull();
            mBoundingRadius = 0.0f;
        } else {
            float maxSqLen = -1.0f;
            ENG_Vector4D min = new ENG_Vector4D(Float.POSITIVE_INFINITY,
                    Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0f);
            ENG_Vector4D max = new ENG_Vector4D(Float.NEGATIVE_INFINITY,
                    Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 1.0f);

            ENG_Matrix4 invWorld = null;
            if (mWorldSpace && getParentNode() != null) {
                invWorld = getParentNode()._getFullTransform().invertRet();
            }

            for (ENG_Billboard b : mActiveBillboards) {
                ENG_Vector4D position = b.getPosition();
                if (mWorldSpace && getParentNode() != null) {
                    /*position = */
                    //noinspection DataFlowIssue
                    invWorld.transform(position);
                }
                min.makeFloor(position);
                max.makeCeil(position);

                maxSqLen = Math.max(maxSqLen, position.squaredLength());


            }

            float adjust = Math.max(mDefaultWidth, mDefaultHeight);
            ENG_Vector4D v = new ENG_Vector4D(adjust, adjust, adjust, 0.0f);
            min.addInPlace(v);
            max.subInPlace(v);

            mAABB.setExtents(min, max);
            mBoundingRadius = ENG_Math.sqrt(maxSqLen);
        }

        mParentNode.needUpdate();
    }

    /** @noinspection deprecation*/
    @Override
    public ArrayList<ENG_Light> getLights() {

        return queryLights();
    }

    @Override
    public boolean getCastsShadows() {

        return false;
    }

    @Override
    public void setCustomParameter(int index, ENG_Vector4D value) {


        mCustomParameters.put(new ENG_Integer(index), value);
    }

    @Override
    public void setCustomParameter(ENG_Integer index, ENG_Vector4D value) {


        mCustomParameters.put(index, value);
    }

    @Override
    public ENG_Vector4D getCustomParameter(int index) {

        return mCustomParameters.get(new ENG_Integer(index));
    }

    @Override
    public ENG_Vector4D getCustomParameter(ENG_Integer index) {

        return mCustomParameters.get(index);
    }

    @Override
    public void _updateCustomGpuParameter(ENG_AutoConstantEntry constantEntry,
                                          ENG_GpuProgramParameters params) {


        ENG_Vector4D i = mCustomParameters.get(new ENG_Integer(constantEntry.data));
        if (i != null) {
            params._writeRawConstant(constantEntry.physicalIndex, i,
                    constantEntry.elementCount);
        }
    }

    @Override
    public void setPolygonModeOverrideable(boolean override) {


        mPolygonModeOverrideable = override;
    }

    @Override
    public boolean getPolygonModeOverrideable() {

        return mPolygonModeOverrideable;
    }

    /** @noinspection deprecation*/
    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue) {


        // If we're driving this from our own data, update geometry if need to.
        if (!mExternalData &&
                (mAutoUpdate || mBillboardDataChanged || !mBuffersCreated)) {
            if (mSortingEnabled) {
                _sortBillboards(mCurrentCamera);
            }

            beginBillboards(mActiveBillboards.size());
            for (ENG_Billboard b : mActiveBillboards) {
                injectBillboard(b);
            }

            endBillboards();
            mBillboardDataChanged = false;
        }

        //only set the render queue group if it has been explicitly set.
        if (mRenderQueuePrioritySet) {
            assert (mRenderQueueIDSet);
            queue.addRenderable(this, mRenderQueueID, mRenderQueuePriority);
        } else if (mRenderQueueIDSet) {
            queue.addRenderable(this, mRenderQueueID);
        } else {
            queue.addRenderable(this);
        }
    }

    public void setBounds(ENG_AxisAlignedBox box, float radius) {
        mAABB.set(box);
        mBoundingRadius = radius;
    }

    @Override
    public void getBoundingBox(ENG_AxisAlignedBox ret) {


        ret.set(mAABB);
    }

    @Override
    public float getBoundingRadius() {

        return mBoundingRadius;
    }

    @Override
    public void visitRenderables(Visitor visitor, boolean debugRenderables) {


        visitor.visit(this, (short) 0, false);
    }

    public SortMode _getSortMode() {
        // Need to sort by distance if we're using accurate facing,
        //or perpendicular billboard type.
        if (mAccurateFacing ||
                mBillboardType == BillboardType.BBT_PERPENDICULAR_SELF ||
                mBillboardType == BillboardType.BBT_PERPENDICULAR_COMMON) {
            return SortMode.SM_DISTANCE;
        } else {
            return SortMode.SM_DIRECTION;
        }
    }

    private final SortByDirectionFunctor sortByDirectionFunctor =
            new SortByDirectionFunctor(mCamDir.invert());

    private final SortByDistanceFunctor sortByDistanceFunctor =
            new SortByDistanceFunctor(mCamPos);

    private final ENG_Vector4D dirFuncTemp = new ENG_Vector4D();

    public void _sortBillboards(ENG_Camera cam) {
        switch (_getSortMode()) {
            case SM_DIRECTION:
                mCamDir.invert(dirFuncTemp);
                sortByDirectionFunctor.set(dirFuncTemp);
                Collections.sort(mActiveBillboards, sortByDirectionFunctor);
                break;
            case SM_DISTANCE:
                sortByDistanceFunctor.set(mCamPos);
                Collections.sort(mActiveBillboards, sortByDistanceFunctor);
                break;
        }
    }

    public void setBillboardsInWorldSpace(boolean ws) {
        mWorldSpace = ws;
    }

    public boolean getBillboardsInWorldSpace() {
        return mWorldSpace;
    }

    /** @noinspection deprecation*/
    @Override
    public String getMovableType() {

        return ENG_BillboardSetFactory.FACTORY_TYPE_NAME;
    }

    public void _notifyBillboardRotated() {

        mAllDefaultRotation = false;
    }

    public void _notifyBillboardResized() {

        mAllDefaultSize = false;
    }

    public boolean getCullIndividually() {
        return mCullIndividual;
    }

    public void setCullIndividually(boolean b) {
        mCullIndividual = b;
    }

    public BillboardType getBillboardType() {
        return mBillboardType;
    }

    public void setBillboardType(BillboardType type) {
        mBillboardType = type;
    }

    public void setCommonDirection(ENG_Vector4D dir) {
        mCommonDirection.set(dir);
    }

    public void getCommonDirection(ENG_Vector4D ret) {
        ret.set(mCommonDirection);
    }

    public ENG_Vector4D getCommonDirection() {
        return new ENG_Vector4D(mCommonDirection);
    }

    public void setCommonUpVector(ENG_Vector4D vec) {
        mCommonUpVector.set(vec);
    }

    public void getCommonUpVector(ENG_Vector4D ret) {
        ret.set(mCommonUpVector);
    }

    public ENG_Vector4D getCommonUpVector() {
        return new ENG_Vector4D(mCommonUpVector);
    }

    public void setUseAccurateFacing(boolean b) {
        mAccurateFacing = b;
    }

    public boolean getUseAccurateFacing() {
        return mAccurateFacing;
    }

    private final float[] tempRect = new float[4];

    public void beginBillboards() {
        beginBillboards(0);
    }

    public void beginBillboards(int numBillboards) {
        if (!mBuffersCreated)
            _createBuffers();

        // Only calculate vertex offets et al if we're not point rendering
        if (!mPointRendering) {

            // Get offsets for origin type
            getParametricOffsets(tempRect);
            float mLeftOff = tempRect[0],
                    mRightOff = tempRect[1],
                    mTopOff = tempRect[2],
                    mBottomOff = tempRect[3];

            // Generate axes etc up-front if not oriented per-billboard
            if (mBillboardType != BillboardType.BBT_ORIENTED_SELF &&
                    mBillboardType != BillboardType.BBT_PERPENDICULAR_SELF &&
                    !(mAccurateFacing && mBillboardType !=
                            BillboardType.BBT_PERPENDICULAR_COMMON)) {
                genBillboardAxes(mCamX, mCamY);

				/* If all billboards are the same size we can precalculate the
				   offsets and just use '+' instead of '*' for each billboard,
				   and it should be faster.
				*/
                genVertOffsets(mLeftOff, mRightOff, mTopOff, mBottomOff,
                        mDefaultWidth, mDefaultHeight, mCamX, mCamY, mVOffset);

            }
        }

        // Init num visible
        mNumVisibleBillboards = 0;

        // Lock the buffer
        if (numBillboards > 0) // optimal lock
        {
            // clamp to max
            numBillboards = Math.min(mPoolSize, numBillboards);

            int billboardSize;
            if (mPointRendering) {
                // just one vertex per billboard (this also excludes texcoords)
                billboardSize = mMainBuf.getVertexSize();
            } else {
                // 4 corners
                billboardSize = mMainBuf.getVertexSize() * 4;
            }
            assert (numBillboards * billboardSize <= mMainBuf.getSizeInBytes());


            mLockPtr = ((ByteBuffer) mMainBuf.lock(0, numBillboards * billboardSize,
                    ((mMainBuf.getUsage() & Usage.HBU_DYNAMIC.getUsage()) != 0) ?
                            LockOptions.HBL_DISCARD : LockOptions.HBL_NORMAL))
                    .asFloatBuffer();
        } else // lock the entire thing


            mLockPtr = ((ByteBuffer) mMainBuf.lock(
                    ((mMainBuf.getUsage() & Usage.HBU_DYNAMIC.getUsage()) != 0) ?
                            LockOptions.HBL_DISCARD : LockOptions.HBL_NORMAL))
                    .asFloatBuffer();
    }

    /** @noinspection deprecation*/
    public void injectBillboard(ENG_Billboard bb) {
        // Don't accept injections beyond pool size
        if (mNumVisibleBillboards == mPoolSize) return;

        // Skip if not visible (NB always true if not bounds checking individual billboards)
        if (!billboardVisible(mCurrentCamera, bb)) return;

        if (!mPointRendering &&
                (mBillboardType == BillboardType.BBT_ORIENTED_SELF ||
                        mBillboardType == BillboardType.BBT_PERPENDICULAR_SELF ||
                        (mAccurateFacing && mBillboardType !=
                                BillboardType.BBT_PERPENDICULAR_COMMON))) {
            // Have to generate axes & offsets per billboard
            genBillboardAxes(mCamX, mCamY, bb);
        }

        // If they're all the same size or we're point rendering
        if (mAllDefaultSize || mPointRendering) {
            /* No per-billboard checking, just blast through.
            Saves us an if clause every billboard which may
            make a difference.
            */

            if (!mPointRendering &&
                    (mBillboardType == BillboardType.BBT_ORIENTED_SELF ||
                            mBillboardType == BillboardType.BBT_PERPENDICULAR_SELF ||
                            (mAccurateFacing && mBillboardType !=
                                    BillboardType.BBT_PERPENDICULAR_COMMON))) {
                genVertOffsets(mLeftOff, mRightOff, mTopOff, mBottomOff,
                        mDefaultWidth, mDefaultHeight, mCamX, mCamY, mVOffset);
            }
            genVertices(mVOffset, bb);
        } else // not all default size and not point rendering
        {
            ENG_Vector4D[] vOwnOffset = new ENG_Vector4D[4];
            for (int i = 0; i < vOwnOffset.length; ++i) {
                vOwnOffset[i] = new ENG_Vector4D();
            }
            // If it has own dimensions, or self-oriented, gen offsets
            if (mBillboardType == BillboardType.BBT_ORIENTED_SELF ||
                    mBillboardType == BillboardType.BBT_PERPENDICULAR_SELF ||
                    bb.mOwnDimensions ||
                    (mAccurateFacing && mBillboardType !=
                            BillboardType.BBT_PERPENDICULAR_COMMON)) {
                // Generate using own dimensions
                genVertOffsets(mLeftOff, mRightOff, mTopOff, mBottomOff,
                        bb.mWidth, bb.mHeight, mCamX, mCamY, vOwnOffset);
                // Create vertex data
                genVertices(vOwnOffset, bb);
            } else // Use default dimension, already computed before the loop, for faster creation
            {
                genVertices(mVOffset, bb);
            }
        }
        // Increment visibles
        mNumVisibleBillboards++;
    }

    public void endBillboards() {
        mMainBuf.unlock();
    }

}
