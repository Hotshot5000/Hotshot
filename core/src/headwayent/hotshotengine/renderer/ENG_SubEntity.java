/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_Entity.VertexDataBindChoice;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters.AutoConstantType;
import headwayent.hotshotengine.renderer.ENG_VertexAnimationTrack.VertexAnimationType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;

import java.util.ArrayList;

public class ENG_SubEntity extends ENG_RenderableImpl {

    /** @noinspection deprecation*/
    protected final ENG_Entity mParentEntity;
    protected String mMaterialName;
    protected ENG_Material mpMaterial;
    protected final ENG_SubMesh mSubMesh;
    protected boolean mVisible = true;
    protected short mMaterialLodIndex;

    /// blend buffer details for dedicated geometry
    protected ENG_VertexData mSkelAnimVertexData;
    /// Quick lookup of buffers
    protected final ENG_TempBlendedBufferInfo mTempSkelAnimInfo =
            new ENG_TempBlendedBufferInfo();
    /// Temp buffer details for software Vertex anim geometry
    protected final ENG_TempBlendedBufferInfo mTempVertexAnimInfo =
            new ENG_TempBlendedBufferInfo();
    /// Vertex data details for software Vertex anim of shared geometry
    protected ENG_VertexData mSoftwareVertexAnimVertexData;
    /// Vertex data details for hardware Vertex anim of shared geometry
    /// - separate since we need to s/w anim for shadows whilst still altering
    ///   the vertex data for hardware morphing (pos2 binding)
    protected ENG_VertexData mHardwareVertexAnimVertexData;
    /// Have we applied any vertex animation to geometry?
    protected boolean mVertexAnimationAppliedThisFrame;
    /// Number of hardware blended poses supported by material
    protected short mHardwarePoseCount;

    protected float mCachedCameraDist;
    protected ENG_Camera mCachedCamera;

    private final ENG_Vector4D temp = new ENG_Vector4D();

    /** @noinspection deprecation*/
    protected ENG_SubEntity(ENG_Entity parent, ENG_SubMesh subMeshBasis) {
        mParentEntity = parent;
        mSubMesh = subMeshBasis;
    }

    public void destroy(boolean skipGLDelete) {
        if (mSkelAnimVertexData != null) {
            mSkelAnimVertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        }
        if (mHardwareVertexAnimVertexData != null) {
            mHardwareVertexAnimVertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        }
        if (mSoftwareVertexAnimVertexData != null) {
            mSoftwareVertexAnimVertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        }
    }

    public ENG_SubMesh getSubMesh() {
        return mSubMesh;
    }

    public String getMaterialName() {
        return mMaterialName;
    }

    public void setMaterialName(String name) {
        ENG_Material material = ENG_MaterialManager.getSingleton().getByName(name);

        if (MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
            if (material == null) {
                throw new NullPointerException("Can't assign material " + name +
                        " to SubEntity of " + mParentEntity.getName() + " because this " +
                        "Material does not exist. Have you forgotten to define it in a " +
                        ".material script?");
                //material = ENG_MaterialManager.getSingleton().getByName("BaseWhite");
            }
            setMaterial(material);
        }
    }

    public void setMaterial(ENG_Material material) {
        mpMaterial = material;
        mMaterialName = material.mName;

        //mpMaterial.load();
        //mParentEntity.reevaluateVertexProcessing();
    }

/*	public ENG_Material getMaterial() {
		return mpMaterial;
	}*/

    public ENG_Technique getTechnique() {
        return mpMaterial.getBestTechnique(mMaterialLodIndex, this);
    }

    /** @noinspection deprecation*/
    @Override
    public ArrayList<ENG_Light> getLights() {
        
        return null;
    }

    @Override
    public ENG_Material getMaterial() {
        
        return mpMaterial;
    }

    @Override
    public void getRenderOperation(ENG_RenderOperation op) {
        
        // Use LOD
        mSubMesh._getRenderOperation(op, mParentEntity.mMeshLodIndex);
        // Deal with any vertex data overrides
        op.vertexData = getVertexDataForBinding();
    }

    @Override
    public float getSquaredViewDepth(ENG_Camera cam) {
        
        if (mCachedCamera == cam) {
            return mCachedCameraDist;
        }
        ENG_Node n = mParentEntity.getParentNode();
        float dist = n.getSquaredViewDepth(cam, temp);
        mCachedCameraDist = dist;
        mCachedCamera = cam;

        return dist;
    }

    @Override
    public void getWorldTransforms(ENG_Matrix4[] xform) {
        
        if (mParentEntity.mNumBoneMatrices == 0 ||
                !mParentEntity.isHardwareAnimationEnabled()) {
            mParentEntity._getParentNodeFullTransform(xform[0]);
        } else {
            ArrayList<ENG_Short> indexMap =
                    mSubMesh.useSharedVertices ?
                            mSubMesh.parent.sharedBlendIndexToBoneIndexMap :
                            mSubMesh.blendIndexToBoneIndexMap;
            if (mParentEntity._isSkeletonAnimated()) {
                assert (mParentEntity.mBoneWorldMatrices != null);
                int i = 0;
                for (ENG_Short ind : indexMap) {
                    xform[i++].set(
                            mParentEntity.mBoneWorldMatrices[ind.getValue()]);
                }
            } else {
                for (int i = 0; i < indexMap.size(); ++i) {
                    mParentEntity._getParentNodeFullTransform(xform[i]);
                }
            }
        }
    }

    public short getNumWorldTransforms() {
        return 1;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void _invalidateCameraCache() {
        mCachedCameraDist = 0.0f;
    }

    protected void prepareTempBlendBuffers() {
        
        if (mSubMesh.useSharedVertices) {
            return;
        }
        if (mSubMesh.getVertexAnimationType() != VertexAnimationType.VAT_NONE) {
            mSoftwareVertexAnimVertexData =
                    mSubMesh.vertexData.clone(false, null);
            mParentEntity.extractTempBufferInfo(
                    mSoftwareVertexAnimVertexData, mTempVertexAnimInfo);
            mHardwareVertexAnimVertexData =
                    mSubMesh.vertexData.clone(false, null);

        }
        if (mParentEntity.hasSkeleton()) {
            mSkelAnimVertexData =
                    mParentEntity.cloneVertexDataRemoveBlendInfo(
                            mSubMesh.vertexData);
            mParentEntity.extractTempBufferInfo(
                    mSkelAnimVertexData, mTempSkelAnimInfo);
        }
    }

    public ENG_VertexData _getSkelAnimVertexData() {
        assert (mSkelAnimVertexData != null);
        return mSkelAnimVertexData;
    }

    public ENG_VertexData _getSoftwareVertexAnimVertexData() {
        assert (mSoftwareVertexAnimVertexData != null);
        return mSoftwareVertexAnimVertexData;
    }

    public ENG_VertexData _getHardwareVertexAnimVertexData() {
        assert (mHardwareVertexAnimVertexData != null);
        return mHardwareVertexAnimVertexData;
    }

    public ENG_TempBlendedBufferInfo _getSkelAnimTempBufferInfo() {
        return mTempSkelAnimInfo;
    }

    public ENG_TempBlendedBufferInfo _getVertexAnimTempBufferInfo() {
        return mTempVertexAnimInfo;
    }

    public ENG_VertexData getVertexDataForBinding() {
        if (mSubMesh.useSharedVertices) {
            return mParentEntity.getVertexDataForBinding();
        } else {
            VertexDataBindChoice c = mParentEntity.chooseVertexDataForBinding(mSubMesh.getVertexAnimationType() != VertexAnimationType.VAT_NONE);
            switch (c) {
                case BIND_ORIGINAL:
                    return mSubMesh.vertexData;
                case BIND_HARDWARE_MORPH:
                    return mHardwareVertexAnimVertexData;
                case BIND_SOFTWARE_MORPH:
                    return mSoftwareVertexAnimVertexData;
                case BIND_SOFTWARE_SKELETAL:
                    return mSkelAnimVertexData;
            }
            // keep compiler happy
            return mSubMesh.vertexData;
        }
    }

    public void _markBuffersUnusedForAnimation() {
        mVertexAnimationAppliedThisFrame = false;
    }

    public void _markBuffersUsedForAnimation() {
        mVertexAnimationAppliedThisFrame = true;
    }

    public boolean _getBuffersMarkedForAnimation() {
        return mVertexAnimationAppliedThisFrame;
    }

    public void _restoreBuffersForUnusedAnimation(boolean hardwareAnimation) {
        if (mSubMesh.getVertexAnimationType() != VertexAnimationType.VAT_NONE &&
                !mSubMesh.useSharedVertices &&
                !mVertexAnimationAppliedThisFrame &&
                (!hardwareAnimation ||
                        mSubMesh.getVertexAnimationType() ==
                                VertexAnimationType.VAT_MORPH)) {
            ENG_VertexElement srcPosElem =
                    mSubMesh.vertexData.vertexDeclaration.findElementBySemantic(
                            VertexElementSemantic.VES_POSITION, 0);
            ENG_HardwareVertexBuffer srcBuf =
                    mSubMesh.vertexData.vertexBufferBinding.getBuffer(
                            srcPosElem.getSource());

            ENG_VertexElement destPosElem =
                    mSoftwareVertexAnimVertexData.vertexDeclaration
                            .findElementBySemantic(
                                    VertexElementSemantic.VES_POSITION, 0);
            mSoftwareVertexAnimVertexData.vertexBufferBinding.setBinding(
                    destPosElem.getSource(), srcBuf);
        }

        if (!mSubMesh.useSharedVertices && hardwareAnimation &&
                mSubMesh.getVertexAnimationType() ==
                        VertexAnimationType.VAT_POSE) {
            mParentEntity.bindMissingHardwarePoseBuffers(
                    mSubMesh.vertexData, mHardwareVertexAnimVertexData);
        }
    }


    @Override
    public void _updateCustomGpuParameter(ENG_AutoConstantEntry constantEntry,
                                          ENG_GpuProgramParameters params) {
        
        if (constantEntry.paramType ==
                AutoConstantType.ACT_ANIMATION_PARAMETRIC) {
            float[] vec = new float[4];
            int animIndex = constantEntry.data * 4;
            for (int i = 0; i < 4 &&
                    animIndex < mHardwareVertexAnimVertexData
                            .hwAnimationDataList.size();
                 ++i, ++animIndex) {
                vec[i] =
                        mHardwareVertexAnimVertexData.hwAnimationDataList
                                .get(i).parametric;
            }
            params._writeRawConstant(constantEntry.physicalIndex,
                    new ENG_Vector4D(vec));
        } else {
            super._updateCustomGpuParameter(constantEntry, params);
        }
    }

}
