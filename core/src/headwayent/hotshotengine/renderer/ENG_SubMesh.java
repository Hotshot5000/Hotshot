/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_RenderOperation.OperationType;
import headwayent.hotshotengine.renderer.ENG_VertexAnimationTrack.VertexAnimationType;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class ENG_SubMesh {

    public boolean useSharedVertices = true;
    public OperationType operationType = OperationType.OT_TRIANGLE_LIST;
    public ENG_VertexData vertexData;
    public ENG_IndexData indexData;
    /** @noinspection deprecation*/
    public ENG_Mesh parent;
    public final ArrayList<ENG_Short> blendIndexToBoneIndexMap =
            new ArrayList<>();
    public final ArrayList<ENG_Vector3D> extremityPoints =
            new ArrayList<>();

    protected String mMaterialName;

    protected boolean mMatInitialised;

    protected boolean mBuildEdgesEnabled = true;

    protected final ArrayList<ENG_IndexData> mLodFaceList =
            new ArrayList<>();

    /*	protected TreeMap<ENG_Integer, ArrayList<ENG_VertexBoneAssignment>>
        mBoneAssignments =
        new TreeMap<ENG_Integer, ArrayList<ENG_VertexBoneAssignment>>();*/
    protected final Multimap<ENG_Integer, ENG_VertexBoneAssignment>
            mBoneAssignments = TreeMultimap.create();

    protected boolean mBoneAssignmentsOutOfDate;

    protected VertexAnimationType mVertexAnimationType =
            VertexAnimationType.VAT_NONE;

    protected final TreeMap<String, String> mTextureAliases =
            new TreeMap<>();

    protected void removeLodLevels() {
        mLodFaceList.clear();
    }

    public ENG_SubMesh() {
        indexData = new ENG_IndexData();
    }

    public void setBuildEdgesEnabled(boolean b) {
        mBuildEdgesEnabled = b;
    }

    public boolean isBuildEdgesEnabled() {
        return mBuildEdgesEnabled;
    }

    public VertexAnimationType getVertexAnimationType() {
        if (parent._getAnimationTypesDirty()) {
            parent._determineAnimationTypes();
        }
        return mVertexAnimationType;
    }

    public void addBoneAssignment(ENG_VertexBoneAssignment v) {
        if (useSharedVertices) {
            throw new UnsupportedOperationException("add the bones to the " +
                    "parent mesh when using shared vertices");
        }
    /*	ENG_Integer key = new ENG_Integer(v.vertexIndex);
		ArrayList<ENG_VertexBoneAssignment> list = mBoneAssignments.get(key);
		if (list == null) {
			list = new ArrayList<ENG_VertexBoneAssignment>();
			mBoneAssignments.put(key, list);
		}
		list.add(v);*/
        mBoneAssignments.put(new ENG_Integer(v.vertexIndex), v);

        mBoneAssignmentsOutOfDate = true;
    }

    public void clearBoneAssignments() {
        mBoneAssignments.clear();
        mBoneAssignmentsOutOfDate = true;
    }

    public Multimap<ENG_Integer, ENG_VertexBoneAssignment>
    getBoneAssignments() {
        return mBoneAssignments;
    }

    public void destroy(boolean skipGLDelete) {
        vertexData.vertexBufferBinding.unsetAllBindings(skipGLDelete);
        ENG_HardwareBufferManager.getSingleton().destroyIndexBuffer(indexData.indexBuffer, skipGLDelete);
    }

    public void setMaterialName(String name) {
        mMaterialName = name;
        mMatInitialised = true;
    }

    public String getMaterialName() {
        return mMaterialName;
    }

    public boolean isMatInitialised() {
        return mMatInitialised;
    }

    public void _getRenderOperation(ENG_RenderOperation ro, short lodIndex) {
        ro.useIndexes = indexData.indexCount != 0;
        if ((lodIndex > 0) && ((lodIndex - 1) < mLodFaceList.size())) {
            ro.indexData = mLodFaceList.get(lodIndex - 1);
        } else {
            ro.indexData = indexData;
        }
        ro.operationType = operationType;
        ro.vertexData = useSharedVertices ? parent.sharedVertexData : vertexData;
    }

    public void _compileBoneAssignments() {

        short maxBones = parent._rationaliseBoneAssignments(
                vertexData.vertexCount, mBoneAssignments);

        if (maxBones != 0) {
            parent.compileBoneAssignments(mBoneAssignments, maxBones,
                    blendIndexToBoneIndexMap, vertexData);
        }

        mBoneAssignmentsOutOfDate = false;
    }

    public void addTextureAlias(String aliasName, String textureName) {

        mTextureAliases.put(aliasName, textureName);
    }
}
