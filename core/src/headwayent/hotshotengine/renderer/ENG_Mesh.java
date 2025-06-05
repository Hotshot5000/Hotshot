/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 10:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Byte;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.LockOptions;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_VertexAnimationTrack.VertexAnimationType;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementType;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

@Deprecated
public class ENG_Mesh {

    protected final ArrayList<ENG_SubMesh> mSubMeshList = new ArrayList<>();
    protected final HashMap<String, ENG_Short> mSubMeshNameMap =
            new HashMap<>();
    protected final ENG_AxisAlignedBox mAABB = new ENG_AxisAlignedBox();
    protected float mBoundRadius;

    protected ENG_LodStrategy mLodStrategy;
    protected boolean mIsLodManual;
    protected short mNumLods = 1;
    protected final ArrayList<ENG_MeshLodUsage> mMeshLodUsageList =
            new ArrayList<>();

    protected Usage mVertexBufferUsage = Usage.HBU_STATIC_WRITE_ONLY;
    protected Usage mIndexBufferUsage = Usage.HBU_STATIC_WRITE_ONLY;

    protected boolean mVertexBufferShadowBuffer = true;
    protected boolean mIndexBufferShadowBuffer = true;


    protected boolean mPreparedForShadowVolumes;
    protected boolean mEdgeListsBuilt;
    protected boolean mAutoBuildEdgeLists;

    protected final String mName;

    protected String mSkeletonName = "";
    protected ENG_Skeleton mSkeleton;

    /*	protected TreeMap<ENG_Integer, ArrayList<ENG_VertexBoneAssignment>>
        mBoneAssignments =
        new TreeMap<ENG_Integer, ArrayList<ENG_VertexBoneAssignment>>();*/
    protected final Multimap<ENG_Integer, ENG_VertexBoneAssignment>
            mBoneAssignments = TreeMultimap.create();

    protected boolean mBoneAssignmentsOutOfDate;

    protected final TreeMap<String, ENG_Animation> mAnimationsList =
            new TreeMap<>();
    protected VertexAnimationType mSharedVertexDataAnimationType =
            VertexAnimationType.VAT_NONE;
    /// Do we need to scan animations for animation types?
    protected boolean mAnimationTypesDirty = true;

    protected final ArrayList<ENG_Pose> mPoseList = new ArrayList<>();

    public ENG_VertexData sharedVertexData;
    public final ArrayList<ENG_Short> sharedBlendIndexToBoneIndexMap =
            new ArrayList<>();

    public ENG_Mesh(String name) {
        mName = name;
        mLodStrategy = ENG_LodStrategyManager.getSingleton().getDefaultStrategy();

        ENG_MeshLodUsage lod = new ENG_MeshLodUsage();
        lod.userValue = 0;
        lod.value = mLodStrategy.getBaseValue();
        lod.manualMesh = null;
        mMeshLodUsageList.add(lod);
    }

    public String getName() {
        return mName;
    }

    protected void buildIndexMap(
            Multimap<ENG_Integer, ENG_VertexBoneAssignment>
                    boneAssignments,
            ArrayList<ENG_Short> boneIndexToBlendIndexMap,
            ArrayList<ENG_Short> blendIndexToBoneIndexMap) {
        if (boneAssignments.isEmpty()) {
            boneIndexToBlendIndexMap.clear();
            blendIndexToBoneIndexMap.clear();
            return;
        }

        TreeSet<ENG_Short> usedBoneIndices = new TreeSet<>();

        for (ENG_VertexBoneAssignment vb :
                boneAssignments.values()) {
//			for (ENG_VertexBoneAssignment bone : vb) {
            usedBoneIndices.add(new ENG_Short(vb.boneIndex));
//			}
        }
        int usedBoneIndicesSize = usedBoneIndices.size();
        int boneIndexSize = usedBoneIndices.last().getValue() + 1;
        blendIndexToBoneIndexMap.ensureCapacity(usedBoneIndicesSize);
        boneIndexToBlendIndexMap.ensureCapacity(boneIndexSize);

        for (int i = 0; i < usedBoneIndicesSize; ++i) {
            blendIndexToBoneIndexMap.add(new ENG_Short());
        }
        for (int i = 0; i < boneIndexSize; ++i) {
            boneIndexToBlendIndexMap.add(new ENG_Short());
        }

        short blendIndex = 0;
        for (ENG_Short boneIndex : usedBoneIndices) {
            boneIndexToBlendIndexMap.get(boneIndex.getValue())
                    .setValue(blendIndex);
            blendIndexToBoneIndexMap.get(blendIndex).setValue(boneIndex);
            ++blendIndex;
        }
    }

    protected void compileBoneAssignments(
            Multimap<ENG_Integer, ENG_VertexBoneAssignment>
                    boneAssignments,
            short numBlendWeightsPerVertex,
            ArrayList<ENG_Short> blendIndexToBoneIndexMap,
            ENG_VertexData targetVertexData) {
        ENG_VertexDeclaration decl = targetVertexData.vertexDeclaration;
        ENG_VertexBufferBinding bind = targetVertexData.vertexBufferBinding;

        ArrayList<ENG_Short> boneIndexToBlendIndexMap =
                new ArrayList<>();
        buildIndexMap(boneAssignments,
                boneIndexToBlendIndexMap,
                blendIndexToBoneIndexMap);

        short bindIndex;

        ENG_VertexElement elementBySemantic =
                decl.findElementBySemantic(
                        VertexElementSemantic.VES_BLEND_INDICES, (short) 0);
        if (elementBySemantic != null) {
            bindIndex = elementBySemantic.getSource();
            bind.unsetBinding(bindIndex);
            decl.removeElement(
                    VertexElementSemantic.VES_BLEND_INDICES, (short) 0);
            decl.removeElement(
                    VertexElementSemantic.VES_BLEND_WEIGHTS, (short) 0);
        } else {
            bindIndex = bind.getNextIndex();
        }

        ENG_HardwareVertexBuffer vbuf =
                ENG_HardwareBufferManager.getSingleton().createVertexBuffer(
                        ENG_Byte.SIZE_IN_BYTES * 4 +
                                ENG_Float.SIZE_IN_BYTES * numBlendWeightsPerVertex,
                        targetVertexData.vertexCount,
                        Usage.HBU_STATIC_WRITE_ONLY.getUsage(), true);

        ENG_VertexElement pIdxElem, pWeightElem;
        bind.setBinding(bindIndex, vbuf);

        ENG_VertexElement firstElement = decl.getElement(0);
        if (firstElement.getSemantic() == VertexElementSemantic.VES_POSITION) {
            short insertPoint = 1;
            while (insertPoint < decl.getElementCount() &&
                    decl.getElement(insertPoint).getSource() ==
                            firstElement.getSource()) {
                ++insertPoint;
            }
            pIdxElem = decl.insertElement(
                    insertPoint,
                    bindIndex,
                    0,
                    VertexElementType.VET_UBYTE4,
                    VertexElementSemantic.VES_BLEND_INDICES,
                    (short) 0);
            pWeightElem = decl.insertElement(
                    insertPoint + 1,
                    bindIndex,
                    ENG_Byte.SIZE_IN_BYTES * 4,
                    ENG_VertexElement.multiplyTypeCount(
                            VertexElementType.VET_FLOAT1,
                            numBlendWeightsPerVertex),
                    VertexElementSemantic.VES_BLEND_WEIGHTS,
                    (short) 0);
        } else {
            pIdxElem = decl.addElement(

                    bindIndex,
                    0,
                    VertexElementType.VET_UBYTE4,
                    VertexElementSemantic.VES_BLEND_INDICES,
                    (short) 0);
            pWeightElem = decl.addElement(

                    bindIndex,
                    ENG_Byte.SIZE_IN_BYTES * 4,
                    ENG_VertexElement.multiplyTypeCount(
                            VertexElementType.VET_FLOAT1,
                            numBlendWeightsPerVertex),
                    VertexElementSemantic.VES_BLEND_WEIGHTS,
                    (short) 0);
        }

        ByteBuffer pBase = (ByteBuffer) vbuf.lock(LockOptions.HBL_DISCARD);
    /*	Iterator<ArrayList<ENG_VertexBoneAssignment>> i =
				boneAssignments.values().iterator();
		boolean advance = true;*/
        // Fuck this just use a multimap. A real fucking multimap
//		ArrayList<ENG_VertexBoneAssignment> next = null;
//		Iterator<ENG_VertexBoneAssignment> iterator = null;
//		ENG_VertexBoneAssignment assig = null;
	/*	boolean found = false;
		while (!found) {
			if (i.hasNext()) {
				next = i.next();				
				iterator = next.iterator();
				if (iterator.hasNext()) {
					assig = iterator.next();
					found = true;
					break;
				}
			} else {
				break;
			}
		}*/
	/*	for (ENG_VertexBoneAssignment as : boneAssignments.values()) {
			System.out.println("vertexIndex: " + as.vertexIndex);
		}*/
        Iterator<ENG_VertexBoneAssignment> it =
                boneAssignments.values().iterator();
        ENG_VertexBoneAssignment assig = null;
        if (it.hasNext()) {
            assig = it.next();
        }
        for (int v = 0; v < targetVertexData.vertexCount; ++v) {
            int pWeight =
                    pWeightElem.baseVertexPointerToElement(pBase.position());
            int pIndex = pIdxElem.baseVertexPointerToElement(pBase.position());


            // We assume this is sorted by vertex order
			
	/*		if (advance && i.hasNext() && (iterator == null || !iterator.hasNext())) {
				next = i.next();
				iterator = next.iterator();
				
			}*/

            for (short bone = 0; bone < numBlendWeightsPerVertex; ++bone) {
//				boolean found = false;

//				ArrayList<ENG_VertexBoneAssignment> next = i.next();
                if (assig != null && v == assig.vertexIndex) {
//					if (advance && iterator.hasNext()) {
//						assig = iterator.next();
//					}

                    ENG_VertexBoneAssignment boneAssignment = assig;
                    pBase.putFloat(pWeight, boneAssignment.weight);
                    pWeight += ENG_Float.SIZE_IN_BYTES;
                    pBase.put(pIndex,
                            (byte) boneIndexToBlendIndexMap.get(
                                    boneAssignment.boneIndex)
                                    .getValue());
                    pIndex += ENG_Byte.SIZE_IN_BYTES;
                    if (it.hasNext()) {
                        assig = it.next();
                    } else {
                        assig = null;
                    }
//					found = true;sebastian_bugiu
				/*	advance = true;
					if (iterator.hasNext()) {FinalColor
						assig = iterator.next();
					} else {
						found = false;
						while (!found) {
							if (i.hassebastian_bugiuNext()) {
								next = i.next();
								iterator = next.iterator();
								if (iterator.hasNext()) {
									assig = iterator.next();
									found = true;
									break;
								}
							} else {
								break;
							}
						}
						if (!found) {
							
						}
					}*/

                } else {
                    pBase.putFloat(pWeight, 0.0f);
                    pWeight += ENG_Float.SIZE_IN_BYTES;
                    pBase.put(pIndex, (byte) 0);
                    pIndex += ENG_Byte.SIZE_IN_BYTES;
//					advance = false;
                }

            }
            pBase.position(pBase.position() + vbuf.getVertexSize());

        }

        vbuf.unlock();
    }

    public short _rationaliseBoneAssignments(int vertexCount,
                                             Multimap<ENG_Integer, ENG_VertexBoneAssignment>
                                                     boneAssignments) {
        short maxBones = 0;
        boolean existsNonSkinnedVertices = false;

        for (int i = 0; i < vertexCount; ++i) {
		/*	ArrayList<ENG_VertexBoneAssignment> list = 
					assignments.get(new ENG_Integer(i));
			short currBones = 0;
			if (list != null && list.size() > 0) {
				currBones = (short) list.size();
			} else {
				existsNonSkinnedVertices = true;
			}*/
            Collection<ENG_VertexBoneAssignment> list =
                    boneAssignments.get(new ENG_Integer(i));
            short currBones = (short) list.size();
            if (currBones <= 0) {
                existsNonSkinnedVertices = true;
            }

            if (maxBones < currBones) {
                maxBones = currBones;
            }

            if (currBones > ENG_Config.MAX_BLEND_WEIGHTS) {
                TreeMap<ENG_Float, ENG_VertexBoneAssignment> temp =
                        new TreeMap<>();
                for (ENG_VertexBoneAssignment assig : list) {
                    temp.put(new ENG_Float(assig.weight), assig);
                }
                for (ENG_VertexBoneAssignment assig : temp.values()) {
                    if (list.size() <= ENG_Config.MAX_BLEND_WEIGHTS) {
                        break;
                    }
                    list.remove(assig);
                }

            }
            if (currBones > 0) {
                float totalWeight = 0.0f;
                for (ENG_VertexBoneAssignment assig : list) {
                    totalWeight += assig.weight;
                }

                if (ENG_Float.compareTo(totalWeight, 1.0f) !=
                        ENG_Utility.COMPARE_EQUAL_TO) {
                    for (ENG_VertexBoneAssignment assig : list) {
                        assig.weight /= totalWeight;
                    }
                }
            }
        }

        if (maxBones > ENG_Config.MAX_BLEND_WEIGHTS) {
            ENG_Log.getInstance().log("The skinned mesh " + mName +
                    " contains more than " +
                    ENG_Config.MAX_BLEND_WEIGHTS + " weights " +
                    "per bone", ENG_Log.TYPE_WARNING);
        }

        if (existsNonSkinnedVertices) {
            ENG_Log.getInstance().log("The skinned mesh " + mName +
                    " contains non skinned vertices. These will " +
                    "transform wrong", ENG_Log.TYPE_WARNING);
        }
        return maxBones;
    }

    public void _compileBoneAssignments() {
        short maxBones = _rationaliseBoneAssignments(
                sharedVertexData.vertexCount, mBoneAssignments);

        if (maxBones != 0) {
            compileBoneAssignments(mBoneAssignments, maxBones,
                    sharedBlendIndexToBoneIndexMap, sharedVertexData);
        }

        mBoneAssignmentsOutOfDate = false;
    }

    public void _updateCompiledBoneAssignments() {
        if (mBoneAssignmentsOutOfDate) {
            _compileBoneAssignments();
        }

        for (ENG_SubMesh subMesh : mSubMeshList) {
            if (subMesh.mBoneAssignmentsOutOfDate) {
                subMesh._compileBoneAssignments();
            }
        }
    }

    public ENG_SubMesh createSubMesh() {
        ENG_SubMesh sub = new ENG_SubMesh();
        sub.parent = this;
        mSubMeshList.add(sub);
        return sub;
    }

    public ENG_SubMesh createSubMesh(String name) {
        ENG_SubMesh sub = createSubMesh();
        nameSubMesh(name.toLowerCase(Locale.US), (short) (mSubMeshList.size() - 1));
        return sub;
    }

    public void destroySubMesh(short index, boolean skipGLDelete) {
        if (index < 0 || index >= mSubMeshList.size()) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        ENG_SubMesh subMesh = mSubMeshList.remove(index);
        subMesh.destroy(skipGLDelete);
        ENG_Short ind = new ENG_Short(index);
        for (Entry<String, ENG_Short> entry : mSubMeshNameMap.entrySet()) {
            if (entry.getValue().equals(ind)) {
                //	mSubMeshList.remove(entry.getKey());
            } else {
                if (entry.getValue().getValue() > index) {
                    entry.getValue().setValue(
                            (short) (entry.getValue().getValue() - 1));
                    //	mSubMeshNameMap.put(entry.getKey(),
                    //			new ENG_Short((short) (entry.getValue().getValue() - 1)));
                }
            }
        }
    }

    public void destroy(boolean skipGLDelete) {
        for (ENG_SubMesh subMesh : mSubMeshList) {
            subMesh.destroy(skipGLDelete);
        }
        mSubMeshList.clear();
        mSubMeshNameMap.clear();
    }

    public void destroySubMesh(String name) {
        destroySubMesh(name, false);
    }

    public void destroySubMesh(String name, boolean skipGLDelete) {
        destroySubMesh(_getSubMeshIndex(name).getValue(), skipGLDelete);
    }

    public void nameSubMesh(String name, short index) {
        mSubMeshNameMap.put(name, new ENG_Short(index));
    }

    public short getNumSubMeshes() {
        return (short) mSubMeshList.size();
    }

    public void unnameSubMesh(String name) {
        mSubMeshNameMap.remove(name);
    }

    public ENG_SubMesh getSubMesh(String name) {
        return getSubMesh(_getSubMeshIndex(name));
    }

    public ENG_SubMesh getSubMesh(short index) {
        if (index < 0 || index >= mSubMeshList.size()) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        return mSubMeshList.get(index);
    }

    public ENG_SubMesh getSubMesh(ENG_Short index) {
        if (index.getValue() < 0 || index.getValue() >= mSubMeshList.size()) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        return mSubMeshList.get(index.getValue());
    }

    public ENG_Short _getSubMeshIndex(String name) {
        ENG_Short i = mSubMeshNameMap.get(name.toLowerCase(Locale.US));
        if (i == null) {
            throw new IllegalArgumentException("No SubMesh named " + name + " found.");
        }
        return i;
    }

    public ENG_AxisAlignedBox getBounds() {
        return mAABB;
    }

    public void _setBounds(ENG_AxisAlignedBox bounds) {
        _setBounds(bounds, true);
    }

    /** @noinspection deprecation */
    public void _setBounds(ENG_AxisAlignedBox bounds, boolean pad) {
        mAABB.set(bounds);
        ENG_Vector4D min = mAABB.getMin();
        ENG_Vector4D max = mAABB.getMax();
        mBoundRadius = ENG_Math.boundingRadiusFromAABB(mAABB);
        //	System.out.println("model volume: " + mAABB.volume());
        if (pad) {
            ENG_Vector4D scaler = max.subAsPt(min).mulAsPt(
                    ENG_MeshManager.getSingleton().getBoundsPaddingFactor());
            mAABB.setExtents(min.subAsPt(scaler), max.addAsPt(scaler));

            mBoundRadius += mBoundRadius *
                    ENG_MeshManager.getSingleton().getBoundsPaddingFactor();
        } else {
            mAABB.setExtents(min, max);
        }
    }

    public void _setBoundingSphereRadius(float radius) {
        mBoundRadius = radius;
    }

    public ENG_MeshLodUsage getLodLevel(ENG_Short index) {
        if (index.getValue() < 0 || index.getValue() >= mMeshLodUsageList.size()) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        return mMeshLodUsageList.get(index.getValue());
    }

    public ENG_MeshLodUsage getLodLevel(short index) {
        if (index < 0 || index >= mMeshLodUsageList.size()) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        return mMeshLodUsageList.get(index);
    }

    public short getNumLodLevels() {
        return mNumLods;
    }

    public void createManualLodLevel(float lodValue, String meshName) {
        mIsLodManual = true;
        ENG_MeshLodUsage lod = new ENG_MeshLodUsage();
        lod.userValue = lodValue;
        lod.value = mLodStrategy.transformUserValue(lodValue);
        lod.manualName = meshName;
        mMeshLodUsageList.add(lod);
        ++mNumLods;
        mLodStrategy.sort(mMeshLodUsageList);
    }

    public void updateManualLodLevel(short index, String meshName) {
        if (!mIsLodManual) {
            throw new IllegalArgumentException("Not using manual LODs!");
        }
        if (index == 0) {
            throw new IllegalArgumentException(
                    "Can't modify first lod level (full detail)");
        }
        if (index < 0 || index >= mMeshLodUsageList.size()) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        ENG_MeshLodUsage lod = mMeshLodUsageList.get(index);
        lod.manualName = meshName;
        lod.manualMesh = null;
    }

    public short getLodIndex(float lodValue) {
        return mLodStrategy.getIndexMesh(lodValue, mMeshLodUsageList);
    }

    public void _setLodInfo(short numLevels, boolean isManual) {
        mNumLods = numLevels;
        mIsLodManual = isManual;
    }

    public void removelLodLevels() {
        if (!mIsLodManual) {
            int len = mSubMeshList.size();
            for (int i = 0; i < len; ++i) {
                mSubMeshList.get(i).removeLodLevels();
            }
        }
        mMeshLodUsageList.clear();

        // Reinitialise
        mNumLods = 1;
        ENG_MeshLodUsage lod = new ENG_MeshLodUsage();
        lod.userValue = 0;
        lod.value = mLodStrategy.getBaseValue();
        mMeshLodUsageList.add(lod);
        mIsLodManual = false;
    }

    public float getBoundingSphereRadius() {
        return mBoundRadius;
    }

    public ENG_LodStrategy getLodStrategy() {
        return mLodStrategy;
    }

    public void setLodStrategy(ENG_LodStrategy lodStrategy) {
        mLodStrategy = lodStrategy;

        mMeshLodUsageList.get(0).value = mLodStrategy.getBaseValue();

        for (int i = 1; i < mMeshLodUsageList.size(); ++i) {
            mMeshLodUsageList.get(i).value = mLodStrategy.transformUserValue(
                    mMeshLodUsageList.get(i).userValue);
        }
    }

    /** @noinspection deprecation */
    public ENG_Mesh clone(String name) {
        ENG_Mesh newMesh = new ENG_Mesh(name);

        int len = mSubMeshList.size();
        for (int i = 0; i < len; ++i) {
            ENG_SubMesh sub = mSubMeshList.get(i);
            ENG_SubMesh newSub = newMesh.createSubMesh();
            newSub.mMaterialName = sub.mMaterialName;
            newSub.mMatInitialised = sub.mMatInitialised;
            newSub.operationType = sub.operationType;
            newSub.useSharedVertices = sub.useSharedVertices;

            if (!sub.useSharedVertices) {
                newSub.vertexData = sub.vertexData.clone();
            }

            newSub.indexData = sub.indexData.clone();

            for (int j = 0; j < sub.mLodFaceList.size(); ++j) {
                newSub.mLodFaceList.add(sub.mLodFaceList.get(j).clone());
            }
        }

        if (sharedVertexData != null) {
            newMesh.sharedVertexData = sharedVertexData.clone();


        }

        newMesh.mSubMeshNameMap.putAll(mSubMeshNameMap);
        newMesh.mAABB.set(mAABB);
        newMesh.mBoundRadius = mBoundRadius;
        newMesh.mLodStrategy = mLodStrategy;
        newMesh.mIsLodManual = mIsLodManual;
        newMesh.mNumLods = mNumLods;
        newMesh.mMeshLodUsageList.addAll(mMeshLodUsageList);

        newMesh.mVertexBufferUsage = mVertexBufferUsage;
        newMesh.mIndexBufferUsage = mIndexBufferUsage;
        newMesh.mVertexBufferShadowBuffer = mVertexBufferShadowBuffer;
        newMesh.mIndexBufferShadowBuffer = mIndexBufferShadowBuffer;

        newMesh.mPreparedForShadowVolumes = mPreparedForShadowVolumes;

        return newMesh;
    }

    public boolean isLodManual() {

        return mIsLodManual;
    }

    public void setSkeletonName(String skelName) {
        if (!mSkeletonName.equals(skelName)) {
            mSkeletonName = skelName;
            if (skelName.isEmpty()) {
                mSkeleton = null;
            } else {
                mSkeleton = ENG_SkeletonManager.getSingleton().load(skelName);
            }
        }
    }

    public boolean hasSkeleton() {
        return !mSkeletonName.isEmpty();
    }

    public boolean hasVertexAnimation() {
        return !mAnimationsList.isEmpty();
    }

    public ENG_Skeleton getSkeleton() {
        return mSkeleton;
    }

    public String getSkeletonName() {
        return mSkeletonName;
    }

    public void addBoneAssignment(ENG_VertexBoneAssignment v) {
		
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

    public void _initAnimationState(ENG_AnimationStateSet animSet) {
        if (mSkeleton != null) {
            mSkeleton._initAnimationState(animSet);
            _updateCompiledBoneAssignments();
        }
        for (ENG_Animation anim : mAnimationsList.values()) {
            if (!animSet.hasAnimationState(anim.getName())) {
                animSet.createAnimationState(
                        anim.getName(), 0.0f, anim.getLength());
            }
        }
    }

    public void _refreshAnimationState(ENG_AnimationStateSet animSet) {
        if (mSkeleton != null) {
            mSkeleton._refreshAnimationState(animSet);
        }
        for (ENG_Animation anim : mAnimationsList.values()) {
            String name = anim.getName();
            if (!animSet.hasAnimationState(name)) {
                animSet.createAnimationState(
                        name, 0.0f, anim.getLength());
            } else {
                ENG_AnimationState animState = animSet.getAnimationState(name);
                animState.setLength(anim.getLength());
                animState.setTimePosition(
                        Math.min(anim.getLength(),
                                animState.getTimePosition()));
            }
        }
    }

    public boolean _getAnimationTypesDirty() {
        return mAnimationTypesDirty;
    }

    public void _determineAnimationTypes() {
        mSharedVertexDataAnimationType = VertexAnimationType.VAT_NONE;
        for (ENG_SubMesh sm : mSubMeshList) {
            sm.mVertexAnimationType = VertexAnimationType.VAT_NONE;
        }

        for (ENG_Animation anim : mAnimationsList.values()) {
            for (ENG_VertexAnimationTrack track :
                    anim.getVertexTrackList().values()) {
                short handle = track.getHandle();
                if (handle == 0) {
                    if (mSharedVertexDataAnimationType != VertexAnimationType.VAT_NONE &&
                            mSharedVertexDataAnimationType != track.getAnimationType()) {
                        throw new IllegalArgumentException(
                                "Animation tracks for shared vertex data on mesh "
                                        + mName + " try to mix vertex animation types, which is "
                                        + "not allowed.");
                    }
                    mSharedVertexDataAnimationType = track.getAnimationType();
                } else {
                    ENG_SubMesh sm = getSubMesh((short) (handle - 1));
                    if (sm.mVertexAnimationType != VertexAnimationType.VAT_NONE &&
                            sm.mVertexAnimationType != track.getAnimationType()) {
                        throw new IllegalArgumentException(
                                "Animation tracks for dedicated vertex data " +
                                        (handle - 1) +
                                        "on mesh "
                                        + mName + " try to mix vertex animation types, which is "
                                        + "not allowed.");
                    }
                    sm.mVertexAnimationType = track.getAnimationType();
                }
            }
        }
        mAnimationTypesDirty = false;
    }

    public ENG_Pose createPose(short handle) {
        return createPose(handle, "");
    }

    public ENG_Pose createPose(short target, String name) {
        ENG_Pose pose = new ENG_Pose(target, name);
        mPoseList.add(pose);
        return pose;
    }

    public ENG_Pose getPose(short index) {
        assert (index >= 0 && index < mPoseList.size());
        return mPoseList.get(index);
    }

    public ENG_Pose getPose(String name) {
        for (ENG_Pose pose : mPoseList) {
            if (pose.getName().equals(name)) {
                return pose;
            }
        }
        throw new IllegalArgumentException(name + " is not a valid pose name");
    }

    public int getPoseCount() {
        return mPoseList.size();
    }

    public void removePose(short index) {
        assert (index >= 0 && index < mPoseList.size());
        mPoseList.remove(index);
    }

    public void removePose(String name) {
        for (Iterator<ENG_Pose> it = mPoseList.iterator(); it.hasNext(); ) {
            if (it.next().getName().equals(name)) {
                it.remove();
                return;
            }
        }
        throw new IllegalArgumentException(name + " is not a valid pose name");
    }

    public void removeAllPoses() {
        mPoseList.clear();
    }

    public ArrayList<ENG_Pose> getPoseList() {
        return mPoseList;
    }

    public VertexAnimationType getSharedVertexDataAnimationType() {
        if (mAnimationTypesDirty) {
            _determineAnimationTypes();
        }
        return mSharedVertexDataAnimationType;
    }

    public ENG_Animation createAnimation(String name, float length) {
        if (mAnimationsList.containsKey(name)) {
            throw new IllegalArgumentException(name + " is already an " +
                    "animation name");
        }
        ENG_Animation animation = new ENG_Animation(name, length);
        mAnimationsList.put(name, animation);
        mAnimationTypesDirty = true;
        return animation;
    }

    public boolean hasAnimation(String name) {
        return mAnimationsList.containsKey(name);
    }

    public ENG_Animation getAnimation(String name) {
        ENG_Animation animation = _getAnimationImpl(name);
        if (animation == null) {
            throw new IllegalArgumentException(name + " is not a valid " +
                    "animation name");
        }
        return animation;
    }

    public ENG_Animation _getAnimationImpl(String name) {
        return mAnimationsList.get(name);
    }

    public void removeAnimation(String name) {
        ENG_Animation remove = mAnimationsList.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " is not a valid " +
                    "animation name");
        }
        mAnimationTypesDirty = true;
    }

    public void removeAllAnimations() {
        mAnimationsList.clear();
        mAnimationTypesDirty = true;
    }

    public short getNumAnimations() {
        return (short) mAnimationsList.size();
    }

    public ENG_VertexData getVertexDataByTrackHandle(short handle) {
        if (handle == 0) {
            return sharedVertexData;
        } else {
            return getSubMesh((short) (handle - 1)).vertexData;
        }
    }

/*	public static void softwareVertexPoseBlend(float influence,
			TreeMap<ENG_Integer, ENG_Vector3D> vertexOffsets,
			ENG_VertexData data) {

		
	}*/

    public boolean isPreparedForShadowVolumes() {

        return mPreparedForShadowVolumes;
    }

    public static void softwareVertexBlend(ENG_VertexData sourceVertexData,
                                           ENG_VertexData targetVertexData,
                                           ENG_Matrix4[] blendMatrices, int numMatrices,
                                           boolean blendNormals) {
        ByteBuffer pSrcPos;
        ByteBuffer pSrcNorm = null;
        ByteBuffer pDestPos;
        ByteBuffer pDestNorm = null;
        ByteBuffer pBlendWeight;
        ByteBuffer pBlendIdx;
        int srcPosStride;
        int srcNormStride = 0;
        int destPosStride;
        int destNormStride = 0;
        int blendWeightStride;
        int blendIdxStride;

        ENG_VertexElement srcElemPos =
                sourceVertexData.vertexDeclaration
                        .findElementBySemantic(VertexElementSemantic.VES_POSITION, 0);
        ENG_VertexElement srcElemNorm =
                sourceVertexData.vertexDeclaration
                        .findElementBySemantic(VertexElementSemantic.VES_NORMAL, 0);
        ENG_VertexElement srcElemBlendIndices =
                sourceVertexData.vertexDeclaration
                        .findElementBySemantic(VertexElementSemantic.VES_BLEND_INDICES, 0);
        ENG_VertexElement srcElemBlendWeights =
                sourceVertexData.vertexDeclaration
                        .findElementBySemantic(
                                VertexElementSemantic.VES_BLEND_WEIGHTS, 0);

        assert (srcElemPos != null && srcElemBlendIndices != null &&
                srcElemBlendWeights != null);

        ENG_VertexElement destElemPos =
                targetVertexData.vertexDeclaration
                        .findElementBySemantic(VertexElementSemantic.VES_POSITION, 0);
        ENG_VertexElement destElemNorm =
                targetVertexData.vertexDeclaration
                        .findElementBySemantic(VertexElementSemantic.VES_NORMAL, 0);

        boolean includeNormals = blendNormals && (srcElemNorm != null) &&
                (destElemNorm != null);

        ENG_HardwareVertexBuffer srcPosBuf =
                sourceVertexData.vertexBufferBinding
                        .getBuffer(srcElemPos.getSource());
        ENG_HardwareVertexBuffer srcIdxBuf =
                sourceVertexData.vertexBufferBinding
                        .getBuffer(srcElemBlendIndices.getSource());
        ENG_HardwareVertexBuffer srcWeightBuf =
                sourceVertexData.vertexBufferBinding
                        .getBuffer(srcElemBlendWeights.getSource());
        ENG_HardwareVertexBuffer srcNormBuf = null;

        srcPosStride = srcPosBuf.getVertexSize();
        blendIdxStride = srcIdxBuf.getVertexSize();
        blendWeightStride = srcWeightBuf.getVertexSize();

        if (includeNormals) {
            srcNormBuf =
                    sourceVertexData.vertexBufferBinding
                            .getBuffer(srcElemNorm.getSource());
            srcNormStride = srcNormBuf.getVertexSize();
        }

        ENG_HardwareVertexBuffer destPosBuf =
                targetVertexData.vertexBufferBinding
                        .getBuffer(destElemPos.getSource());
        ENG_HardwareVertexBuffer destNormBuf = null;

        destPosStride = destPosBuf.getVertexSize();

        if (includeNormals) {
            destNormBuf =
                    targetVertexData.vertexBufferBinding
                            .getBuffer(destElemNorm.getSource());
            destNormStride = destNormBuf.getVertexSize();
        }

        Buffer lock = srcPosBuf.lock(LockOptions.HBL_READ_ONLY);
        pSrcPos = (ByteBuffer) lock;
        int srcPosPtrBase =
                srcElemPos.baseVertexPointerToElement(lock.position());
        int srcNormPtrBase = 0;
        if (includeNormals) {
            if (srcPosBuf != srcNormBuf) {
                lock = srcNormBuf.lock(LockOptions.HBL_READ_ONLY);

            }
            pSrcNorm = (ByteBuffer) lock;
            srcNormPtrBase =
                    srcElemNorm.baseVertexPointerToElement(lock.position());
        }

        assert (srcElemBlendIndices.getType() == VertexElementType.VET_UBYTE4);

        lock = srcIdxBuf.lock(LockOptions.HBL_READ_ONLY);
        pBlendIdx = (ByteBuffer) lock;
        int blendIndexBase =
                srcElemBlendIndices.baseVertexPointerToElement(lock.position());
        if (srcIdxBuf != srcWeightBuf) {
            lock = srcWeightBuf.lock(LockOptions.HBL_READ_ONLY);

        }
        pBlendWeight = (ByteBuffer) lock;
        int blendWeightBase =
                srcElemBlendWeights.baseVertexPointerToElement(lock.position());

        short numWeightsPerVertex =
                ENG_VertexElement.getTypeCount(srcElemBlendWeights.getType());

        lock = destPosBuf.lock(
                (destNormBuf != destPosBuf && destPosBuf.getVertexSize() ==
                        destElemPos.getSize()) ||
                        (destNormBuf == destPosBuf && destPosBuf.getVertexSize() ==
                                destElemPos.getSize() + destElemNorm.getSize()) ?
                        LockOptions.HBL_DISCARD : LockOptions.HBL_NORMAL);
        pDestPos = (ByteBuffer) lock;

        int destPosBase =
                destElemPos.baseVertexPointerToElement(lock.position());

        int destNormBase = 0;

        if (includeNormals) {
            if (destPosBuf != destNormBuf) {
                lock = destNormBuf.lock(
                        destNormBuf.getVertexSize() == destElemNorm.getSize() ?
                                LockOptions.HBL_DISCARD :
                                LockOptions.HBL_NORMAL);

            }
            pDestNorm = (ByteBuffer) lock;
            destNormBase =
                    destElemNorm.baseVertexPointerToElement(lock.position());
        }

        ENG_OptimisedUtil.getImplementation().softwareVertexSkinning(
                pSrcPos, srcPosPtrBase,
                pDestPos, destPosBase,
                pSrcNorm, srcNormPtrBase,
                pDestNorm, destNormBase,
                pBlendWeight, blendWeightBase,
                pBlendIdx, blendIndexBase,
                blendMatrices,
                srcPosStride,
                destPosStride,
                srcNormStride,
                destNormStride,
                blendWeightStride,
                blendIdxStride,
                numWeightsPerVertex,
                targetVertexData.vertexCount);

        srcPosBuf.unlock();
        srcIdxBuf.unlock();
        if (srcWeightBuf != srcIdxBuf) {
            srcWeightBuf.unlock();
        }
        if (includeNormals && srcPosBuf != srcNormBuf) {
            srcNormBuf.unlock();
        }
        destPosBuf.unlock();
        if (includeNormals && destPosBuf != destNormBuf) {
            destNormBuf.unlock();
        }
    }

    public static void softwareVertexMorph(float t,
                                           ENG_HardwareVertexBuffer b1,
                                           ENG_HardwareVertexBuffer b2,
                                           ENG_VertexData targetVertexData) {
        ByteBuffer pb1 = (ByteBuffer) b1.lock(LockOptions.HBL_READ_ONLY);
        ByteBuffer pb2;
        if (b1 != b2) {
            pb2 = (ByteBuffer) b2.lock(LockOptions.HBL_READ_ONLY);
        } else {
            pb2 = pb1;
        }

        ENG_VertexElement destElemPos =
                targetVertexData.vertexDeclaration
                        .findElementBySemantic(VertexElementSemantic.VES_POSITION, 0);
        assert (destElemPos != null);

        ENG_HardwareVertexBuffer destBuf =
                targetVertexData.vertexBufferBinding
                        .getBuffer(destElemPos.getSource());
        assert (destBuf.getVertexSize() == destElemPos.getSize());

        ByteBuffer pDest = (ByteBuffer) destBuf.lock(LockOptions.HBL_DISCARD);

        ENG_OptimisedUtil.getImplementation().softwareVertexMorph(
                t,
                pb1,
                pb2,
                pDest,
                targetVertexData.vertexCount);

        destBuf.unlock();
        b1.unlock();
        if (b1 != b2) {
            b2.unlock();
        }
    }

    public static void softwareVertexPoseBlend(
            float weight,
            TreeMap<ENG_Integer, ENG_Vector3D> vertexOffsetMap,
            ENG_VertexData targetVertexData) {
        if (weight == 0.0f) {
            return;
        }

        ENG_VertexElement destElemPos =
                targetVertexData.vertexDeclaration
                        .findElementBySemantic(VertexElementSemantic.VES_POSITION, 0);
        assert (destElemPos != null);

        ENG_HardwareVertexBuffer destBuf =
                targetVertexData.vertexBufferBinding
                        .getBuffer(destElemPos.getSource());
        assert (destBuf.getVertexSize() == destElemPos.getSize());

        ByteBuffer buf = (ByteBuffer) destBuf.lock(LockOptions.HBL_NORMAL);
        int pBase = buf.position();

        for (Entry<ENG_Integer, ENG_Vector3D> entry :
                vertexOffsetMap.entrySet()) {
            ENG_Vector3D value = entry.getValue();
            int pos =
                    pBase +
                            entry.getKey().getValue() * 3 * ENG_Float.SIZE_IN_BYTES;

            buf.putFloat(pos, buf.getFloat(pos) + value.x * weight);
            pos += ENG_Float.SIZE_IN_BYTES;
            buf.putFloat(pos, buf.getFloat(pos) + value.y * weight);
            pos += ENG_Float.SIZE_IN_BYTES;
            buf.putFloat(pos, buf.getFloat(pos) + value.z * weight);
//			pos += ENG_Float.SIZE_IN_BYTES;
        }

        destBuf.unlock();
    }

    public static void prepareMatricesForVertexBlend(
            ENG_Matrix4[] blendMatrices, ENG_Matrix4[] boneMatrices,
            ArrayList<ENG_Short> indexMap) {
        assert (indexMap.size() < 256);
        int i = 0;
        for (ENG_Short s : indexMap) {
            blendMatrices[i++] = boneMatrices[s.getValue()];
        }
    }

    public ENG_EdgeData getEdgeList() {
        return getEdgeList((short) 0);
    }

    public ENG_EdgeData getEdgeList(short lodIndex) {
        if (!mEdgeListsBuilt || mAutoBuildEdgeLists) {
            buildEdgeList();
        }
        return getLodLevel(lodIndex).edgeData;
    }

    public void buildEdgeList() {


    }

    public Multimap<ENG_Integer, ENG_VertexBoneAssignment>
    getBoneAssignments() {
        return mBoneAssignments;
    }

    public Iterator<Entry<ENG_Integer, ENG_VertexBoneAssignment>>
    getBoneAssignmentsIterator() {
        return mBoneAssignments.entries().iterator();
    }
}
