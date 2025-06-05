/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Sphere;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_LodListener.EntityMaterialLodChangedEvent;
import headwayent.hotshotengine.renderer.ENG_LodListener.EntityMeshLodChangedEvent;
import headwayent.hotshotengine.renderer.ENG_RenderableImpl.Visitor;
import headwayent.hotshotengine.renderer.ENG_VertexAnimationTrack.VertexAnimationType;
import headwayent.hotshotengine.renderer.ENG_VertexData.HardwareAnimationData;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

@Deprecated
public class ENG_Entity extends ENG_MovableObject implements ENG_IDisposable {

    @Override
    public void destroy() {

    }

    /// Identify which vertex data we should be sending to the renderer
    public enum VertexDataBindChoice {
        BIND_ORIGINAL,
        BIND_SOFTWARE_SKELETAL,
        BIND_SOFTWARE_MORPH,
        BIND_HARDWARE_MORPH
    }

    /** @noinspection deprecation*/
    protected ENG_Mesh mMesh;
    protected final ArrayList<ENG_SubEntity> mSubEntityList =
            new ArrayList<>();

    protected ENG_AnimationStateSet mAnimationState;

    /// Temp buffer details for software skeletal anim of shared geometry
    protected final ENG_TempBlendedBufferInfo mTempSkelAnimInfo =
            new ENG_TempBlendedBufferInfo();
    /// Vertex data details for software skeletal anim of shared geometry
    protected ENG_VertexData mSkelAnimVertexData;
    /// Temp buffer details for software vertex anim of shared geometry
    protected final ENG_TempBlendedBufferInfo mTempVertexAnimInfo =
            new ENG_TempBlendedBufferInfo();
    /// Vertex data details for software vertex anim of shared geometry
    protected ENG_VertexData mSoftwareVertexAnimVertexData;
    /// Vertex data details for hardware vertex anim of shared geometry
    /// - separate since we need to s/w anim for shadows whilst still altering
    ///   the vertex data for hardware morphing (pos2 binding)
    protected ENG_VertexData mHardwareVertexAnimVertexData;
    /// Have we applied any vertex animation to shared geometry?
    protected boolean mVertexAnimationAppliedThisFrame;
    protected boolean mPreparedForShadowVolumes;

    /// Cached bone matrices, including any world transform
    protected ENG_Matrix4[] mBoneWorldMatrices;
    /// Cached bone matrices in skeleton local space, might shares with other entity instances.
    protected ENG_Matrix4[] mBoneMatrices;
    protected short mNumBoneMatrices;
    /// Records the last frame in which animation was updated
    protected long mFrameAnimationLastUpdated;

    protected ENG_Long mFrameBonesLastUpdated;

    /** @noinspection deprecation*/
    protected HashSet<ENG_Entity> mSharedSkeletonEntities =
            new HashSet<>();

    /// Flag determines whether or not to display skeleton
    protected boolean mDisplaySkeleton;
    /// Flag indicating whether hardware animation is supported by this entities materials
    protected boolean mHardwareAnimation;
    /// Number of hardware poses supported by materials
    protected short mHardwarePoseCount;
    protected boolean mVertexProgramInUse;
    /// Counter indicating number of requests for software animation.
    protected int mSoftwareAnimationRequests;
    /// Counter indicating number of requests for software blended normals.
    protected int mSoftwareAnimationNormalsRequests;
    /// Flag indicating whether to skip automatic updating of the Skeleton's AnimationState
    protected boolean mSkipAnimStateUpdates;

    //protected short mMeshLodIndex;

    /// The LOD number of the mesh to use, calculated by _notifyCurrentCamera
    protected short mMeshLodIndex;

    /// LOD bias factor, transformed for optimisation when calculating adjusted lod value
    protected final float mMeshLodFactorTransformed = 1.0f;
    /// Index of minimum detail LOD (NB higher index is lower detail)
    protected final short mMinMeshLodIndex = 99;
    /// Index of maximum detail LOD (NB lower index is higher detail)
    protected short mMaxMeshLodIndex;

    /// LOD bias factor, not transformed
    protected final float mMaterialLodFactor = 1.0f;
    /// LOD bias factor, transformed for optimisation when calculating adjusted lod value
    protected final float mMaterialLodFactorTransformed = 1.0f;
    /// Index of minimum detail LOD (NB higher index is lower detail)
    protected final short mMinMaterialLodIndex = 99;
    /// Index of maximum detail LOD (NB lower index is higher detail)
    protected short mMaxMaterialLodIndex;

    /**
     * List of LOD Entity instances (for manual LODs).
     * We don't know when the mesh is using manual LODs whether one LOD to the next will have the
     * same number of SubMeshes, therefore we have to allow a separate Entity list
     * with each alternate one.
     * @noinspection deprecation
     */
    protected final ArrayList<ENG_Entity> mLodEntityList =
            new ArrayList<>();

    protected ENG_SkeletonInstance mSkeletonInstance;

    /// Has this entity been initialised yet?
    protected boolean mInitialised;

    /// Last parent xform
    protected final ENG_Matrix4 mLastParentXform = new ENG_Matrix4(ENG_Math.MAT4_ZERO);

    /// Mesh state count, used to detect differences
    protected int mMeshStateCount;

    protected final TreeMap<String, ENG_MovableObject> mChildObjectList =
            new TreeMap<>();

    protected ENG_AxisAlignedBox mFullBoundingBox = new ENG_AxisAlignedBox();

    public ENG_Entity() {

        _initialise();
    }

    /** @noinspection deprecation*/
    public ENG_Entity(String name, ENG_Mesh mesh) {
        super(name);

        mMesh = mesh;
        _initialise();
    }

    /** @noinspection deprecation*/
    public ENG_Entity(String name, long id, ENG_Mesh mesh) {
        super(name, id);
        mMesh = mesh;
        _initialise();
    }

    public void destroy(boolean skipGLDelete) {
        _deinitialise(skipGLDelete);
    }


    /** @noinspection deprecation*/
    @Override
    public void _notifyAttached(ENG_Node parent, boolean isTagPoint) {

        super._notifyAttached(parent, isTagPoint);
        for (ENG_Entity entity : mLodEntityList) {
            entity._notifyAttached(parent, isTagPoint);
        }
    }

    public void _deinitialise(boolean skipGLDelete) {
        if (!mInitialised) {
            return;
        }

        for (ENG_SubEntity sub : mSubEntityList) {
            sub.destroy(skipGLDelete);
        }
        if (mSkelAnimVertexData != null) {
            mSkelAnimVertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        }
        if (mHardwareVertexAnimVertexData != null) {
            mHardwareVertexAnimVertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        }
        if (mSoftwareVertexAnimVertexData != null) {
            mSoftwareVertexAnimVertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        }
        mInitialised = false;
    }

    public void _initialise() {
        _initialise(false);
    }

    /** @noinspection deprecation */
    public void _initialise(boolean forceReinitialise) {
        if (mInitialised) {
            return;
        }

        //Add skeleton anim later
        if (mMesh.hasSkeleton() && mMesh.getSkeleton() != null) {
            mSkeletonInstance = new ENG_SkeletonInstance(mMesh.getSkeleton());
            mSkeletonInstance.loadImpl();
        }

        // Build main subentity list
        buildSubEntityList(mMesh, mSubEntityList);

        if (mMesh.isLodManual()) {
            short lodLevels = mMesh.getNumLodLevels();
            for (short i = 1; i < lodLevels; ++i) {
                ENG_MeshLodUsage usage = mMesh.getLodLevel(i);
                ENG_Entity entity = new ENG_Entity(mName + "Lod" + i, usage.manualMesh);
                mLodEntityList.add(entity);
            }
        }

        if (hasSkeleton()) {
            mFrameBonesLastUpdated = new ENG_Long(Long.MAX_VALUE);
            mNumBoneMatrices = (short) mSkeletonInstance.getNumBones();
            mBoneMatrices = new ENG_Matrix4[mNumBoneMatrices];
            for (int i = 0; i < mBoneMatrices.length; ++i) {
                mBoneMatrices[i] = new ENG_Matrix4();
            }

        }
        if (hasSkeleton() || hasVertexAnimation()) {
            mAnimationState = new ENG_AnimationStateSet();
            mMesh._initAnimationState(mAnimationState);
            prepareTempBlendBuffers();
        }

        reevaluateVertexProcessing();

        if (mParentNode != null) {
            getParentSceneNode().needUpdate(false);
        }

        mInitialised = true;
        mMeshStateCount = 0;//mMesh.getStateCount();
    }

    protected void prepareTempBlendBuffers() {

        if (hasVertexAnimation()) {
            if (mMesh.sharedVertexData != null &&
                    mMesh.getSharedVertexDataAnimationType() !=
                            VertexAnimationType.VAT_NONE) {
                mSoftwareVertexAnimVertexData =
                        mMesh.sharedVertexData.clone(false, null);
                extractTempBufferInfo(
                        mSoftwareVertexAnimVertexData, mTempVertexAnimInfo);
                mHardwareVertexAnimVertexData =
                        mMesh.sharedVertexData.clone(false, null);
            }
        }

        if (hasSkeleton()) {
            if (mMesh.sharedVertexData != null) {
                mSkelAnimVertexData =
                        cloneVertexDataRemoveBlendInfo(mMesh.sharedVertexData);
                extractTempBufferInfo(mSkelAnimVertexData, mTempSkelAnimInfo);
            }
        }

        for (ENG_SubEntity subEntity : mSubEntityList) {
            subEntity.prepareTempBlendBuffers();
        }
        mPreparedForShadowVolumes = mMesh.isPreparedForShadowVolumes();
    }

    protected void extractTempBufferInfo(ENG_VertexData vertexData,
                                         ENG_TempBlendedBufferInfo info) {
        info.extractFrom(vertexData);
    }

    protected ENG_VertexData cloneVertexDataRemoveBlendInfo(
            ENG_VertexData source) {
        ENG_VertexData ret = source.clone(false, null);
        ENG_VertexElement blendIndexElem =
                source.vertexDeclaration.findElementBySemantic(
                        VertexElementSemantic.VES_BLEND_INDICES, 0);
        ENG_VertexElement blendWeightElem =
                source.vertexDeclaration.findElementBySemantic(
                        VertexElementSemantic.VES_BLEND_WEIGHTS, 0);

        if (blendIndexElem != null) {
            ret.vertexBufferBinding.unsetBinding(blendIndexElem.getSource());
        }
        if (blendWeightElem != null &&
                blendWeightElem.getSource() != blendIndexElem.getSource()) {
            ret.vertexBufferBinding.unsetBinding(blendWeightElem.getSource());
        }
        ret.vertexDeclaration.removeElement(
                VertexElementSemantic.VES_BLEND_INDICES, 0);
        ret.vertexDeclaration.removeElement(
                VertexElementSemantic.VES_BLEND_WEIGHTS, 0);

        ret.closeGapsInBindings();
        return ret;
    }

    public ENG_VertexData getVertexDataForBinding() {
        VertexDataBindChoice c =
                chooseVertexDataForBinding(
                        mMesh.getSharedVertexDataAnimationType() !=
                                VertexAnimationType.VAT_NONE);
        switch (c) {
            case BIND_ORIGINAL:
                return mMesh.sharedVertexData;
            case BIND_HARDWARE_MORPH:
                return mHardwareVertexAnimVertexData;
            case BIND_SOFTWARE_MORPH:
                return mSoftwareVertexAnimVertexData;
            case BIND_SOFTWARE_SKELETAL:
                return mSkelAnimVertexData;
        }
        return mMesh.sharedVertexData;
    }

    public VertexDataBindChoice chooseVertexDataForBinding(boolean vertexAnim) {
        if (hasSkeleton()) {
            if (!mHardwareAnimation) {
                return VertexDataBindChoice.BIND_SOFTWARE_SKELETAL;
            } else if (vertexAnim) {
                return VertexDataBindChoice.BIND_HARDWARE_MORPH;
            } else {
                return VertexDataBindChoice.BIND_ORIGINAL;
            }
        } else if (vertexAnim) {
            if (mHardwareAnimation) {
                return VertexDataBindChoice.BIND_HARDWARE_MORPH;
            } else {
                return VertexDataBindChoice.BIND_SOFTWARE_MORPH;
            }
        }
        return VertexDataBindChoice.BIND_ORIGINAL;
    }

    protected void bindMissingHardwarePoseBuffers(ENG_VertexData srcData,
                                                  ENG_VertexData destData) {
        ENG_VertexElement srcPosElem =
                srcData.vertexDeclaration.findElementBySemantic(
                        VertexElementSemantic.VES_POSITION, 0);
        ENG_HardwareVertexBuffer srcBuf =
                srcData.vertexBufferBinding.getBuffer(srcPosElem.getSource());

        for (HardwareAnimationData data : destData.hwAnimationDataList) {
            if (!destData.vertexBufferBinding.isBufferBound(
                    data.targetVertexElement.getSource())) {
                destData.vertexBufferBinding.setBinding(
                        data.targetVertexElement.getSource(), srcBuf);
            }
        }
    }

    public void restoreBuffersForUnusedAnimation(boolean hardwareAnimation) {
        if (mMesh.sharedVertexData != null &&
                !mVertexAnimationAppliedThisFrame &&
                (!hardwareAnimation ||
                        mMesh.getSharedVertexDataAnimationType() ==
                                VertexAnimationType.VAT_MORPH)) {
            ENG_VertexElement srcPosElem =
                    mMesh.sharedVertexData.vertexDeclaration
                            .findElementBySemantic(
                                    VertexElementSemantic.VES_POSITION, 0);
            ENG_HardwareVertexBuffer srcBuf =
                    mMesh.sharedVertexData.vertexBufferBinding.getBuffer(
                            srcPosElem.getSource());

            ENG_VertexElement destPosElem =
                    mSoftwareVertexAnimVertexData.vertexDeclaration
                            .findElementBySemantic(
                                    VertexElementSemantic.VES_POSITION, 0);
            mSoftwareVertexAnimVertexData.vertexBufferBinding.setBinding(
                    destPosElem.getSource(), srcBuf);
        }

        if (mMesh.sharedVertexData != null &&
                hardwareAnimation &&
                mMesh.getSharedVertexDataAnimationType() ==
                        VertexAnimationType.VAT_POSE) {
            bindMissingHardwarePoseBuffers(
                    mMesh.sharedVertexData, mHardwareVertexAnimVertexData);
        }

        for (ENG_SubEntity subEntity : mSubEntityList) {
            subEntity._restoreBuffersForUnusedAnimation(hardwareAnimation);
        }
    }

    /** @noinspection deprecation*/
    protected void applyVertexAnimation(
            boolean hardwareAnimation, boolean stencilShadows) {
        ENG_Mesh mesh = getMesh();
        boolean swAnim = !hardwareAnimation || stencilShadows ||
                (mSoftwareAnimationRequests > 0);

        if (hardwareAnimation) {
            if (mHardwareVertexAnimVertexData != null &&
                    mesh.getSharedVertexDataAnimationType() !=
                            VertexAnimationType.VAT_NONE) {
                initHardwareAnimationElements(
                        mHardwareVertexAnimVertexData,
                        mesh.getSharedVertexDataAnimationType() ==
                                VertexAnimationType.VAT_POSE ?
                                mHardwarePoseCount :
                                1);
            }
            for (ENG_SubEntity sub : mSubEntityList) {
                if (sub.getSubMesh().getVertexAnimationType() !=
                        VertexAnimationType.VAT_NONE &&
                        !sub.getSubMesh().useSharedVertices) {
                    initHardwareAnimationElements(
                            sub._getHardwareVertexAnimVertexData(),
                            sub.getSubMesh().getVertexAnimationType() ==
                                    VertexAnimationType.VAT_POSE ?
                                    mHardwarePoseCount :
                                    1);
                }
            }
        } else {
            if (mSoftwareVertexAnimVertexData != null &&
                    mMesh.getSharedVertexDataAnimationType() ==
                            VertexAnimationType.VAT_POSE) {
                ENG_VertexElement elem =
                        mSoftwareVertexAnimVertexData.vertexDeclaration
                                .findElementBySemantic(
                                        VertexElementSemantic.VES_POSITION, 0);
                ENG_HardwareVertexBuffer buffer =
                        mSoftwareVertexAnimVertexData.vertexBufferBinding
                                .getBuffer(elem.getSource());
                buffer.supressHardwareUpdate(true);
            }
            for (ENG_SubEntity sub : mSubEntityList) {
                if (!sub.getSubMesh().useSharedVertices &&
                        sub.getSubMesh().getVertexAnimationType() ==
                                VertexAnimationType.VAT_POSE) {
                    ENG_VertexData data =
                            sub._getSoftwareVertexAnimVertexData();
                    ENG_VertexElement elem =
                            data.vertexDeclaration
                                    .findElementBySemantic(
                                            VertexElementSemantic.VES_POSITION, 0);
                    ENG_HardwareVertexBuffer buffer =
                            data.vertexBufferBinding
                                    .getBuffer(elem.getSource());
                    buffer.supressHardwareUpdate(true);
                }
            }
        }

        markBuffersUnusedForAnimation();
        Iterator<Entry<String, ENG_AnimationState>> iterator =
                mAnimationState.getAnimationStateIterator();
        while (iterator.hasNext()) {
            Entry<String, ENG_AnimationState> entry = iterator.next();
            ENG_AnimationState state = entry.getValue();
            ENG_Animation anim = mesh._getAnimationImpl(entry.getKey());
            if (anim != null) {
                anim.apply(
                        this,
                        state.getTimePosition(),
                        state.getWeight(),
                        swAnim,
                        hardwareAnimation);
            }
        }
        restoreBuffersForUnusedAnimation(hardwareAnimation);

        if (!hardwareAnimation) {
            if (mSoftwareVertexAnimVertexData != null &&
                    mMesh.getSharedVertexDataAnimationType() ==
                            VertexAnimationType.VAT_POSE) {
                ENG_VertexElement elem =
                        mSoftwareVertexAnimVertexData.vertexDeclaration
                                .findElementBySemantic(
                                        VertexElementSemantic.VES_POSITION, 0);
                ENG_HardwareVertexBuffer buffer =
                        mSoftwareVertexAnimVertexData.vertexBufferBinding
                                .getBuffer(elem.getSource());
                buffer.supressHardwareUpdate(true);
            }
            for (ENG_SubEntity sub : mSubEntityList) {
                if (!sub.getSubMesh().useSharedVertices &&
                        sub.getSubMesh().getVertexAnimationType() ==
                                VertexAnimationType.VAT_POSE) {
                    ENG_VertexData data =
                            sub._getSoftwareVertexAnimVertexData();
                    ENG_VertexElement elem =
                            data.vertexDeclaration
                                    .findElementBySemantic(
                                            VertexElementSemantic.VES_POSITION, 0);
                    ENG_HardwareVertexBuffer buffer =
                            data.vertexBufferBinding
                                    .getBuffer(elem.getSource());
                    buffer.supressHardwareUpdate(true);
                }
            }
        }
    }

    protected void initHardwareAnimationElements(ENG_VertexData vData,
                                                 short numberOfElements) {
        if (vData.hwAnimationDataList.size() < numberOfElements) {
            vData.allocateHardwareAnimationElements(numberOfElements);
        }
        for (HardwareAnimationData data : vData.hwAnimationDataList) {
            data.parametric = 0.0f;
        }
        vData.hwAnimDataItemsUsed = 0;
    }

    protected void markBuffersUnusedForAnimation() {
        mVertexAnimationAppliedThisFrame = false;
        for (ENG_SubEntity sub : mSubEntityList) {
            sub._markBuffersUnusedForAnimation();
        }
    }

    // NOT THREAD SAFE
    private static final ENG_Matrix4[] blendMatrices = new ENG_Matrix4[256];
    private static final ENG_Matrix4 parentFullTransTemp = new ENG_Matrix4();

    static {
    /*	for (int i = 0; i < blendMatrices.length; ++i) {
			blendMatrices[i] = new ENG_Matrix4();
		}*/
    }

    /** @noinspection deprecation */
    protected void updateAnimation() {
        if (!mInitialised) {
            return;
        }

        ENG_RenderRoot root = ENG_RenderRoot.getRenderRoot();
        boolean hwAnimation = isHardwareAnimationEnabled();
        boolean forcedSwAnimation = getSoftwareAnimationRequests() > 0;
        boolean forcedNormals = getSoftwareAnimationNormalsRequests() > 0;
        boolean stencilShadows = false;
        if (getCastShadows() && hasEdgeList() &&
                root._getCurrentSceneManager() != null) {
            stencilShadows =
                    root._getCurrentSceneManager()
                            .isShadowTechniqueStencilBased();
        }
        boolean softwareAnimation = !hwAnimation || stencilShadows ||
                forcedSwAnimation;
        // Blend normals in s/w only if we're not using h/w animation,
        // since shadows only require positions
        boolean blendNormals = !hwAnimation || forcedNormals;
        // Animation dirty if animation state modified or manual bones modified
        boolean animationDirty =
                (mFrameAnimationLastUpdated !=
                        mAnimationState.getDirtyFrameNumber()) ||
                        (hasSkeleton() && getSkeleton().getManualBonesDirty());
        if (animationDirty ||
                (softwareAnimation && hasVertexAnimation() &&
                        !tempVertexAnimBuffersBound()) ||
                (softwareAnimation && hasSkeleton() &&
                        !tempSkelAnimBuffersBound(blendNormals))) {
            if (hasVertexAnimation()) {
                if (softwareAnimation) {
                    if (mSoftwareVertexAnimVertexData != null &&
                            mMesh.getSharedVertexDataAnimationType() !=
                                    VertexAnimationType.VAT_NONE) {
                        mTempVertexAnimInfo.checkoutTempCopies(true, false);
                        mTempVertexAnimInfo.bindTempCopies(
                                mSoftwareVertexAnimVertexData, hwAnimation);
                    }
                    for (ENG_SubEntity sub : mSubEntityList) {
                        if (sub.isVisible() &&
                                sub.mSoftwareVertexAnimVertexData != null &&
                                sub.getSubMesh().getVertexAnimationType() !=
                                        VertexAnimationType.VAT_NONE) {
                            sub.mTempVertexAnimInfo
                                    .checkoutTempCopies(true, false);
                            sub.mTempVertexAnimInfo.bindTempCopies(
                                    sub.mSoftwareVertexAnimVertexData,
                                    hwAnimation);
                        }
                    }
                }
                applyVertexAnimation(hwAnimation, stencilShadows);
            }

            if (hasSkeleton()) {
                cacheBoneMatrices();

                if (softwareAnimation) {
                    if (mSkelAnimVertexData != null) {
                        mTempSkelAnimInfo.checkoutTempCopies(
                                true, blendNormals);
                        mTempSkelAnimInfo.bindTempCopies(
                                mSkelAnimVertexData, hwAnimation);
                        ENG_Mesh.prepareMatricesForVertexBlend(
                                blendMatrices,
                                mBoneMatrices,
                                mMesh.sharedBlendIndexToBoneIndexMap);
                        ENG_Mesh.softwareVertexBlend(
                                mMesh.getSharedVertexDataAnimationType() !=
                                        VertexAnimationType.VAT_NONE ?
                                        mSoftwareVertexAnimVertexData :
                                        mMesh.sharedVertexData,
                                mSkelAnimVertexData,
                                blendMatrices,
                                mMesh.sharedBlendIndexToBoneIndexMap
                                        .size(),
                                blendNormals);
                    }
                    for (ENG_SubEntity sub : mSubEntityList) {
                        if (sub.isVisible() &&
                                sub.mSkelAnimVertexData != null) {
                            sub.mTempSkelAnimInfo.checkoutTempCopies(
                                    true, blendNormals);
                            sub.mTempSkelAnimInfo.bindTempCopies(
                                    sub.mSkelAnimVertexData, hwAnimation);
                            ENG_Mesh.prepareMatricesForVertexBlend(
                                    blendMatrices,
                                    mBoneMatrices,
                                    sub.mSubMesh.blendIndexToBoneIndexMap);
                            ENG_Mesh.softwareVertexBlend(
                                    sub.getSubMesh().getVertexAnimationType() !=
                                            VertexAnimationType.VAT_NONE ?
                                            sub.mSoftwareVertexAnimVertexData :
                                            sub.mSubMesh.vertexData,
                                    sub.mSkelAnimVertexData,
                                    blendMatrices,
                                    sub.mSubMesh
                                            .blendIndexToBoneIndexMap
                                            .size(),
                                    blendNormals);
                        }
                    }
                }
            }

            if (!mChildObjectList.isEmpty()) {
                mParentNode.needUpdate();
            }

            mFrameAnimationLastUpdated = mAnimationState.getDirtyFrameNumber();


        }
        if (hasSkeleton()) {
            _getParentNodeFullTransform(parentFullTransTemp);
            if (animationDirty ||
                    mLastParentXform.notEquals(parentFullTransTemp)) {
                mLastParentXform.set(parentFullTransTemp);

                for (ENG_MovableObject obj : mChildObjectList.values()) {
                    obj.getParentNode()._update(true, true);
                }

                if (hwAnimation && _isSkeletonAnimated()) {
                    if (mBoneWorldMatrices == null) {
                        mBoneWorldMatrices =
                                new ENG_Matrix4[mNumBoneMatrices];
                        for (int i = 0; i < mBoneWorldMatrices.length;
                             ++i) {
                            mBoneWorldMatrices[i] = new ENG_Matrix4();
                        }
                    }

                    ENG_OptimisedUtil.getImplementation()
                            .concatenateAffineMatrices(
                                    mLastParentXform,
                                    mBoneMatrices,
                                    mBoneWorldMatrices,
                                    mNumBoneMatrices);
                }
            }
        }
    }

    public void _updateAnimation() {
        if (hasSkeleton() || hasVertexAnimation()) {
            updateAnimation();
        }
    }

    public boolean _isSkeletonAnimated() {
        return getSkeleton() != null &&
                (mAnimationState.hasEnabledAnimationState() ||
                        getSkeleton().hasManualBones());
    }

    protected void cacheBoneMatrices() {
        long currentFrameNumber =
                ENG_RenderRoot.getRenderRoot().getNextFrameNumber();
        if (mFrameBonesLastUpdated.getValue() != currentFrameNumber) {
            if (!mSkipAnimStateUpdates) {
                mSkeletonInstance.setAnimationState(mAnimationState);
            }
            mSkeletonInstance._getBoneMatrices(mBoneMatrices);
            mFrameBonesLastUpdated.setValue(currentFrameNumber);
        }
    }

    protected boolean tempVertexAnimBuffersBound() {
        boolean ret = true;
        if (mMesh.sharedVertexData != null &&
                mMesh.getSharedVertexDataAnimationType() !=
                        VertexAnimationType.VAT_NONE) {
            ret = ret && mTempVertexAnimInfo.buffersCheckedOut(true, false);
        }
        for (ENG_SubEntity sub : mSubEntityList) {
            if (!sub.getSubMesh().useSharedVertices &&
                    sub.getSubMesh().getVertexAnimationType() !=
                            VertexAnimationType.VAT_NONE) {
                ret = ret && sub._getVertexAnimTempBufferInfo()
                        .buffersCheckedOut(true, false);
            }
        }
        return ret;
    }

    protected boolean tempSkelAnimBuffersBound(boolean requestNormals) {
        if (mSkelAnimVertexData != null) {
            if (!mTempSkelAnimInfo.buffersCheckedOut(true, requestNormals)) {
                return false;
            }
        }
        for (ENG_SubEntity sub : mSubEntityList) {
            if (sub.isVisible() && sub.mSkelAnimVertexData != null) {
                if (!sub.mTempSkelAnimInfo.buffersCheckedOut(
                        true, requestNormals)) {
                    return false;
                }
            }
        }
        return true;
    }

    public ENG_SkeletonInstance getSkeleton() {
        return mSkeletonInstance;
    }

    public int getSoftwareAnimationRequests() {

        return mSoftwareAnimationRequests;
    }

    public int getSoftwareAnimationNormalsRequests() {
        return mSoftwareAnimationNormalsRequests;
    }

    public void addSoftwareAnimationRequest(boolean normalsAlso) {
        ++mSoftwareAnimationRequests;
        if (normalsAlso) {
            ++mSoftwareAnimationNormalsRequests;
        }
    }

    public void removeSoftwareAnimationRequest(boolean normalsAlso) {
        if (mSoftwareAnimationRequests == 0 ||
                (normalsAlso && mSoftwareAnimationNormalsRequests == 0)) {
            throw new ENG_InvalidFieldStateException("Cannot remove " +
                    "non existat software animation request");
        }
        --mSoftwareAnimationRequests;
        if (normalsAlso) {
            --mSoftwareAnimationNormalsRequests;
        }
    }

    public boolean getCastShadows() {
        return mCastShadows;
    }

    public boolean hasEdgeList() {
        return mMesh.getEdgeList() != null;
    }

    public boolean isHardwareAnimationEnabled() {
        return mHardwareAnimation;
    }

    public void _markBuffersUnusedForAnimation() {
        mVertexAnimationAppliedThisFrame = false;
    }


    public boolean hasVertexAnimation() {

        return mMesh.hasVertexAnimation();
    }

    public boolean hasSkeleton() {

        return mSkeletonInstance != null;
    }

    public void reevaluateVertexProcessing() {


        // Temporary solution. In the end we will have to add skeletal animation to the server side since the calculations will be made on the server.
        // It sucks if we use shader skeletal animation since we have to start that whole subsystem.
        // For now just return.
        if (MainApp.getMainThread().getApplicationSettings().applicationMode == MainApp.Mode.SERVER) {
            return;
        }
        mHardwareAnimation = false;
        mVertexProgramInUse = false;
        boolean firstPass = true;

        for (ENG_SubEntity sub : mSubEntityList) {
            ENG_Material mat = sub.getMaterial();
            mat.load();
            ENG_Technique t =
                    mat.getBestTechnique(new ENG_Short((short) 0), sub);
            if (t == null) {
                continue;
            }
            if (t.getNumPasses() == 0) {
                continue;
            }
            ENG_Pass pass = t.getPass((short) 0);
            if (pass.hasVertexProgram()) {
                mVertexProgramInUse = true;
                if (hasSkeleton()) {
                    if (firstPass) {
                        mHardwareAnimation =
                                pass.getVertexProgram()
                                        .isSkeletalAnimationIncluded();
                        firstPass = false;
                    } else {
                        mHardwareAnimation = mHardwareAnimation &&
                                pass.getVertexProgram()
                                        .isSkeletalAnimationIncluded();
                    }
                }
                VertexAnimationType animType;
                if (sub.getSubMesh().useSharedVertices) {
                    animType = mMesh.getSharedVertexDataAnimationType();
                } else {
                    animType = sub.getSubMesh().getVertexAnimationType();
                }
                if (animType == VertexAnimationType.VAT_MORPH) {
                    if (firstPass) {
                        mHardwareAnimation = pass.getVertexProgram()
                                .isMorphAnimationIncluded();
                        firstPass = false;
                    } else {
                        mHardwareAnimation = mHardwareAnimation &&
                                pass.getVertexProgram()
                                        .isMorphAnimationIncluded();
                    }
                } else if (animType == VertexAnimationType.VAT_POSE) {
                    if (firstPass) {
                        mHardwareAnimation = pass.getVertexProgram()
                                .isPoseAnimationIncluded();
                        if (sub.getSubMesh().useSharedVertices) {
                            mHardwarePoseCount = pass.getVertexProgram()
                                    .getNumberOfPosesIncluded();
                        } else {
                            sub.mHardwarePoseCount = pass.getVertexProgram()
                                    .getNumberOfPosesIncluded();
                        }
                        firstPass = false;
                    } else {
                        mHardwareAnimation = mHardwareAnimation &&
                                pass.getVertexProgram()
                                        .isPoseAnimationIncluded();
                        if (sub.getSubMesh().useSharedVertices) {
                            mHardwarePoseCount =
                                    (short) Math.max(mHardwarePoseCount,
                                            pass.getVertexProgram()
                                                    .getNumberOfPosesIncluded());
                        } else {
                            sub.mHardwarePoseCount =
                                    (short) Math.max(mHardwarePoseCount,
                                            pass.getVertexProgram()
                                                    .getNumberOfPosesIncluded());
                        }
                    }
                }
            }
        }
    }

    /** @noinspection deprecation*/
    private void buildSubEntityList(ENG_Mesh mesh,
                                    ArrayList<ENG_SubEntity> sublist) {

        short subMeshes = mesh.getNumSubMeshes();
        for (short i = 0; i < subMeshes; ++i) {
            ENG_SubMesh subMesh = mesh.getSubMesh(i);
            ENG_SubEntity subEntity = new ENG_SubEntity(this, subMesh);
            if (subMesh.isMatInitialised()) {
                subEntity.setMaterialName(subMesh.getMaterialName());
            }
            sublist.add(subEntity);
        }
    }

    /** @noinspection deprecation*/
    public ENG_Mesh getMesh() {
        return mMesh;
    }

    public ENG_SubEntity getSubEntity(int index) {
        if (index < 0 || index >= mSubEntityList.size()) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return mSubEntityList.get(index);
    }

    public ENG_SubEntity getSubEntity(String name) {
        return getSubEntity(mMesh._getSubMeshIndex(name).getValue());
    }

    public int getNumSubEntities() {
        return mSubEntityList.size();
    }

    public void setMaterialName(String name) {
        int len = mSubEntityList.size();
        for (int i = 0; i < len; ++i) {
            mSubEntityList.get(i).setMaterialName(name);
        }
    }

    public void setMaterial(ENG_Material material) {
        int len = mSubEntityList.size();
        for (int i = 0; i < len; ++i) {
            mSubEntityList.get(i).setMaterial(material);
        }
    }

    public void _notifyCurrentCamera(ENG_Camera cam) {
        super._notifyCurrentCamera(cam);

        if (mParentNode != null) {
            ENG_LodStrategy meshStrategy = mMesh.getLodStrategy();

            float lodValue = meshStrategy.getValue(this, cam);

            float biasedMeshLodValue = lodValue * mMeshLodFactorTransformed;

            short newMeshLodIndex = mMesh.getLodIndex(biasedMeshLodValue);
            newMeshLodIndex = (short) Math.max(mMaxMeshLodIndex, newMeshLodIndex);
            newMeshLodIndex = (short) Math.min(mMinMeshLodIndex, newMeshLodIndex);

            EntityMeshLodChangedEvent evt = new EntityMeshLodChangedEvent();
            evt.entity = this;
            evt.camera = cam;
            evt.lodValue = biasedMeshLodValue;
            evt.previousLodIndex = mMeshLodIndex;
            evt.newLodIndex = newMeshLodIndex;

            cam.getSceneManager()._notifyEntityMeshLodChanged(evt);

            // Change lod index
            mMeshLodIndex = evt.newLodIndex;

            // Now do material LOD
            lodValue *= mMaterialLodFactorTransformed;

            for (int i = 0; i < mSubEntityList.size(); ++i) {
                ENG_Material material = mSubEntityList.get(i).getMaterial();

                ENG_LodStrategy materialStrategy = material.getLodStrategy();

                float biasedMaterialLodValue;
                if (meshStrategy == materialStrategy) {
                    biasedMaterialLodValue = lodValue;
                } else {
                    biasedMaterialLodValue = materialStrategy.getValue(this, cam) *
                            materialStrategy.transformBias(mMaterialLodFactor);
                }

                short idx = material.getLodIndex(biasedMaterialLodValue);
                idx = (short) Math.max(mMaxMaterialLodIndex, idx);
                idx = (short) Math.min(mMinMaterialLodIndex, idx);

                EntityMaterialLodChangedEvent evt2 =
                        new EntityMaterialLodChangedEvent();
                evt2.subEntity = mSubEntityList.get(i);
                evt2.camera = cam;
                evt2.lodValue = biasedMaterialLodValue;
                evt2.previousLodIndex = mSubEntityList.get(i).mMaterialLodIndex;
                evt2.newLodIndex = idx;

                cam.getSceneManager()._notifyEntityMaterialLodChanged(evt2);

                mSubEntityList.get(i).mMaterialLodIndex = evt.newLodIndex;

                mSubEntityList.get(i)._invalidateCameraCache();

            }
        }

        for (Entry<String, ENG_MovableObject> stringENGMovableObjectEntry : mChildObjectList.entrySet()) {
            stringENGMovableObjectEntry.getValue()._notifyCurrentCamera(cam);
        }
    }

    /** @noinspection deprecation*/
    @Override
    public void _updateRenderQueue(ENG_RenderQueue queue) {

        ENG_Entity displayEntity = this;

        if (mMeshLodIndex > 0 && mMesh.mIsLodManual) {
            assert (mMeshLodIndex - 1 < mLodEntityList.size());
            if (hasSkeleton() &&
                    mLodEntityList.get(mMeshLodIndex - 1).hasSkeleton()) {
                mAnimationState.copyMatchingState(
                        mLodEntityList.get(mMeshLodIndex - 1).mAnimationState);
            }
            displayEntity = mLodEntityList.get(mMeshLodIndex - 1);
        }

        for (int i = 0; i < displayEntity.mSubEntityList.size(); ++i) {
            ENG_SubEntity it = displayEntity.mSubEntityList.get(i);

            if (it.isVisible()) {
                if (mRenderQueuePrioritySet) {
                    queue.addRenderable(it, mRenderQueueID, mRenderQueuePriority);
                } else if (mRenderQueueIDSet) {
                    queue.addRenderable(it, mRenderQueueID);
                } else {
                    queue.addRenderable(it);
                }
            }
        }

        if (displayEntity.hasSkeleton() || displayEntity.hasVertexAnimation()) {
            displayEntity.updateAnimation();

            for (ENG_MovableObject obj : mChildObjectList.values()) {
                boolean visible = obj.isVisible();
                if (visible && displayEntity != this) {
                    ENG_Bone bone = (ENG_Bone) obj.getParentNode().getParent();
                    if (!displayEntity.getSkeleton().hasBone(bone.getName())) {
                        visible = false;
                    }
                }
                if (visible) {
                    obj._updateRenderQueue(queue);
                }
            }
        }

    }

    @Override
    public void getBoundingBox(ENG_AxisAlignedBox ret) {

        //Not fully implemented. no bones!
        mFullBoundingBox = mMesh.getBounds();
        ret.set(mFullBoundingBox);
    }

    public ENG_AxisAlignedBox getWorldBoundingBox(boolean derive) {
        if (derive) {
            for (Entry<String, ENG_MovableObject> stringENGMovableObjectEntry : mChildObjectList.entrySet()) {
                stringENGMovableObjectEntry.getValue().getWorldBoundingBox(true);
            }
        }
        return super.getWorldBoundingBox(derive);
    }

    public ENG_Sphere getWorldBoundingSphere(boolean derive) {
        if (derive) {
            for (Entry<String, ENG_MovableObject> stringENGMovableObjectEntry : mChildObjectList.entrySet()) {
                stringENGMovableObjectEntry.getValue().getWorldBoundingSphere(true);
            }
        }

        return super.getWorldBoundingSphere(derive);
    }

    @Override
    public float getBoundingRadius() {

        return mMesh.getBoundingSphereRadius();
    }

    @Override
    public void visitRenderables(Visitor visitor, boolean debugRenderables) {


    }

    @Override
    public String getMovableType() {

        return "Entity";
    }

    public boolean _getBuffersMarkedForAnimation() {

        return mVertexAnimationAppliedThisFrame;
    }

    public ENG_VertexData _getSoftwareVertexAnimVertexData() {

        assert (mSoftwareVertexAnimVertexData != null);
        return mSoftwareVertexAnimVertexData;
    }

    public ENG_VertexData _getHardwareVertexAnimVertexData() {
        assert (mHardwareVertexAnimVertexData != null);
        return mHardwareVertexAnimVertexData;
    }

    public void _markBuffersUsedForAnimation() {

        mVertexAnimationAppliedThisFrame = true;
    }

    public ENG_AnimationState getAnimationState(String name) {
        if (mAnimationState == null) {
            throw new NullPointerException("Animation not enabled");
        }
        return mAnimationState.getAnimationState(name);
    }

    public ENG_AnimationStateSet getAllAnimationStates() {
        return mAnimationState;
    }


}
