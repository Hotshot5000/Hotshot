/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 4:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_StringIntefaceInterface;
import headwayent.hotshotengine.ENG_StringInterface;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Short;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class ENG_Skeleton implements ENG_StringIntefaceInterface {

    public static final short MAX_NUM_BONES = 256;

    public enum SkeletonAnimationBlendMode {
        /// Animations are applied by calculating a weighted average of all animations
        ANIMBLEND_AVERAGE,
        /// Animations are applied by calculating a weighted cumulative total
        ANIMBLEND_CUMULATIVE
    }

    public static class LinkedSkeletonAnimationSource {
        public final String skeletonName;
        public ENG_Skeleton pSkeleton;
        public final float scale;

        public LinkedSkeletonAnimationSource(String name, float scale) {
            skeletonName = name;
            this.scale = scale;
        }

        public LinkedSkeletonAnimationSource(String name, float scale,
                                             ENG_Skeleton skel) {
            this(name, scale);
            pSkeleton = skel;
        }
    }

    protected SkeletonAnimationBlendMode mBlendState =
            SkeletonAnimationBlendMode.ANIMBLEND_AVERAGE;
    protected final headwayent.hotshotengine.util.ENG_ArrayList<ENG_Bone> mBoneList =
            new headwayent.hotshotengine.util.ENG_ArrayList<>();
    protected final HashMap<String, ENG_Bone> mBoneListByName =
            new HashMap<>();
    protected final ArrayList<ENG_Bone> mRootBones = new ArrayList<>();
    protected short mNextAutoHandle;
    protected final HashSet<ENG_Bone> mManualBones = new HashSet<>();
    protected boolean mManualBonesDirty;
    protected final TreeMap<String, ENG_Animation> mAnimationsList =
            new TreeMap<>();
    protected final ArrayList<LinkedSkeletonAnimationSource>
            mLinkedSkeletonAnimSourceList =
            new ArrayList<>();

    private final ENG_StringInterface stringInterface = new ENG_StringInterface(this);
    private String mName;

    public String getName() {
        return mName;
    }

    protected ENG_Skeleton() {

    }

    public ENG_Skeleton(String name) {
        mName = name;
        if (getStringInterface().createParamDictionary("Skeleton")) {

        }
    }

    protected void deriveRootBone() {
        if (mBoneList.isEmpty()) {
            throw new IllegalArgumentException("Cannot derive root bone as this " +
                    "skeleton has no bones!");
        }

        mRootBones.clear();

        for (ENG_Bone bone : mBoneList) {
            if (bone.getParent() == null) {
                mRootBones.add(bone);
            }
        }
    }

    public int getNumBones() {
        return mBoneList.size();
    }

    public ENG_Bone getRootBone() {
        if (mRootBones.isEmpty()) {
            deriveRootBone();
        }
        return mRootBones.get(0);
    }

    public Iterator<ENG_Bone> getRootBoneIterator() {
        if (mRootBones.isEmpty()) {
            deriveRootBone();
        }
        return mRootBones.iterator();
    }

    public Iterator<ENG_Bone> getBoneIterator() {
        return mBoneList.iterator();
    }

    public ENG_Bone getBone(short bone) {
        assert (bone >= 0 && bone < mBoneList.size());
        return mBoneList.get(bone);
    }

    public ENG_Bone getBone(String name) {
        ENG_Bone bone = mBoneListByName.get(name);
        if (bone == null) {
            throw new IllegalArgumentException(name + " is not a valid bone");
        }
        return bone;
    }

    public boolean hasBone(String name) {
        return mBoneListByName.containsKey(name);
    }

    public ENG_Bone createBone() {
        return createBone(mNextAutoHandle++);
    }

    public ENG_Bone createBone(short handle) {
        return createBone(ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, handle);
    }

    public ENG_Bone createBone(ENG_SceneManager.SceneMemoryMgrTypes type, short handle) {
        if (handle >= MAX_NUM_BONES) {
            throw new IllegalArgumentException(handle + " exceeds max allowed " +
                    "bones num");
        }
        if (handle < mBoneList.size() && mBoneList.get(handle) != null) {
            throw new IllegalArgumentException(handle + " handle " +
                    "already in bone list");
        }

        ENG_Bone ret = new ENG_Bone(handle, type, this);
    /*	if (mBoneList.size() < handle) {
			mBoneList.add(mBoneList.get(mBoneList.size() - 1));
			for (int i = mBoneList.size() - 2; i > handle; --i) {
				mBoneList.add(i, mBoneList.get(i - 1));
			}
			mBoneList.add(handle, ret);
		} else if (mBoneList.size() == handle) {
			mBoneList.add(ret);
		}*/
        mBoneList.add(handle, ret);
        mBoneListByName.put(ret.getName(), ret);
        return ret;
    }

    public ENG_Bone createBone(String name) {
        return createBone(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC);
    }

    public ENG_Bone createBone(String name, ENG_SceneManager.SceneMemoryMgrTypes type) {
        return createBone(name, type, mNextAutoHandle++);
    }

    public ENG_Bone createBone(String name, short handle) {
        return createBone(name, ENG_SceneManager.SceneMemoryMgrTypes.SCENE_DYNAMIC, handle);
    }

    public ENG_Bone createBone(String name, ENG_SceneManager.SceneMemoryMgrTypes type, short handle) {
        if (handle >= MAX_NUM_BONES) {
            throw new IllegalArgumentException(handle + " exceeds max allowed " +
                    "bones num");
        }
        if (handle < mBoneList.size() && mBoneList.get(handle) != null) {
            throw new IllegalArgumentException(handle + " handle with name " +
                    name + " already in bone list");
        }
        if (mBoneListByName.get(name) != null) {
            throw new IllegalArgumentException(name + " bone already in this " +
                    "skeleton");
        }
        ENG_Bone ret = new ENG_Bone(name, type, handle, this);
	/*	if (mBoneList.size() < handle) {
			mBoneList.add(mBoneList.get(mBoneList.size() - 1));
			for (int i = mBoneList.size() - 2; i > handle; --i) {
				mBoneList.add(i, mBoneList.get(i - 1));
			}
			mBoneList.add(handle, ret);
		} else if (mBoneList.size() == handle) {
			mBoneList.add(ret);
		}*/
        mBoneList.add(handle, ret);
        mBoneListByName.put(name, ret);
        return ret;
    }

    public void setBindingsPose() {
        _updateTransforms();

        for (ENG_Bone bone : mBoneList) {
            bone.setBindingPose();
        }
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean resetManualBones) {
        for (ENG_Bone bone : mBoneList) {
            if (!bone.isManuallyControlled() || resetManualBones) {
                bone.reset();
            }
        }
    }

    public ENG_Animation createAnimation(String name, float length) {
        if (mAnimationsList.containsKey(name)) {
            throw new IllegalArgumentException(name + " already exists in " +
                    "the animation list");
        }
        ENG_Animation animation = new ENG_Animation(name, length);
        mAnimationsList.put(name, animation);
        return animation;
    }

    public ENG_Animation getAnimation(String name) {
        ENG_Animation animation = _getAnimationImpl(name);
        if (animation == null) {
            throw new IllegalArgumentException(name + " is an invalid " +
                    "animation name");
        }
        return animation;
    }

    public ENG_Animation _getAnimationImpl(String name) {
        ENG_Animation animation = mAnimationsList.get(name);
        if (animation == null) {
            for (LinkedSkeletonAnimationSource src :
                    mLinkedSkeletonAnimSourceList) {
                animation = src.pSkeleton._getAnimationImpl(name);
            }
        }
        return animation;
    }

    public LinkedSkeletonAnimationSource getLinkedSkeletonAnimation(String name) {

        for (LinkedSkeletonAnimationSource src :
                mLinkedSkeletonAnimSourceList) {

            if (src.pSkeleton._getAnimationImpl(name) != null) {
                return src;
            }
        }
        return null;
    }

    public boolean hasAnimation(String name) {
        return _getAnimationImpl(name) != null;
    }

    public void removeAnimation(String name) {
        ENG_Animation remove = mAnimationsList.remove(name);
        if (remove == null) {
            throw new IllegalArgumentException(name + " does not exist " +
                    "in the animation list");
        }
    }

    public void setAnimationState(ENG_AnimationStateSet set) {
        reset();
        float weightFactor = 1.0f;
        if (mBlendState == SkeletonAnimationBlendMode.ANIMBLEND_AVERAGE) {
            float totalWeights = 0.0f;
            Iterator<Entry<String, ENG_AnimationState>>
                    animationStateIterator = set.getAnimationStateIterator();
            while (animationStateIterator.hasNext()) {
                Entry<String, ENG_AnimationState> entry =
                        animationStateIterator.next();
                ENG_AnimationState animState = entry.getValue();
                ENG_Animation animation =
                        _getAnimationImpl(animState.getAnimationName());
                LinkedSkeletonAnimationSource linkedSkeletonAnimation =
                        getLinkedSkeletonAnimation(
                                animState.getAnimationName());
                if (animation != null) {
                    totalWeights += animState.getWeight();
                }
            }
            if (totalWeights > 1.0f) {
                weightFactor = 1.0f / totalWeights;
            }
        }

        Iterator<ENG_AnimationState> iterator =
                set.getEnabledAnimationStateIterator();
        while (iterator.hasNext()) {
            ENG_AnimationState animState = iterator.next();
            ENG_Animation animation =
                    _getAnimationImpl(animState.getAnimationName());
            LinkedSkeletonAnimationSource linkedSkeletonAnimation =
                    getLinkedSkeletonAnimation(
                            animState.getAnimationName());
            if (animation != null) {
                if (animState.hasBlendMask()) {
                    animation.apply(this, animState.getTimePosition(),
                            animState.getWeight() * weightFactor,
                            animState.getBlendMask(),
                            linkedSkeletonAnimation != null ?
                                    linkedSkeletonAnimation.scale : 1.0f);
                } else {
                    animation.apply(this, animState.getTimePosition(),
                            animState.getWeight() * weightFactor,

                            linkedSkeletonAnimation != null ?
                                    linkedSkeletonAnimation.scale : 1.0f);
                }
            }
        }
    }

    public void _initAnimationState(ENG_AnimationStateSet animSet) {
        animSet.removeAllAnimationStates();
        for (ENG_Animation anim : mAnimationsList.values()) {
            animSet.createAnimationState(
                    anim.getName(), 0.0f, anim.getLength());
        }
        for (LinkedSkeletonAnimationSource li : mLinkedSkeletonAnimSourceList) {
            if (li.pSkeleton != null) {
                li.pSkeleton._refreshAnimationState(animSet);
            }
        }
    }

    public void _refreshAnimationState(ENG_AnimationStateSet animSet) {
        for (ENG_Animation anim : mAnimationsList.values()) {
            String animName = anim.getName();
            if (!animSet.hasAnimationState(animName)) {
                animSet.createAnimationState(animName, 0.0f, anim.getLength());
            } else {
                ENG_AnimationState animationState =
                        animSet.getAnimationState(animName);
                animationState.setLength(anim.getLength());
                animationState.setTimePosition(
                        Math.min(anim.getLength(),
                                animationState.getTimePosition()));
            }
        }

        for (LinkedSkeletonAnimationSource li : mLinkedSkeletonAnimSourceList) {
            if (li.pSkeleton != null) {
                li.pSkeleton._refreshAnimationState(animSet);
            }
        }
    }

    public void _getBoneMatrices(ArrayList<ENG_Matrix4> matList) {
        _updateTransforms();
        int i = 0;
        for (ENG_Bone bone : mBoneList) {
            bone._getOffsetTransform(matList.get(i++));
        }
    }

    public void _getBoneMatrices(ENG_Matrix4[] matList) {
        _updateTransforms();
        int i = 0;
        for (ENG_Bone bone : mBoneList) {
            bone._getOffsetTransform(matList[i++]);
        }
    }

    public short getNumAnimations() {
        return (short) mAnimationsList.size();
    }

    public ENG_Animation getAnimation(short index) {
        assert (index >= 0 && index < mAnimationsList.size());
        ENG_Animation animFound = null;
        short i = 0;
        for (ENG_Animation anim : mAnimationsList.values()) {
            if (i++ >= index) {
                animFound = anim;
                break;
            }
        }
        return animFound;
    }

    public SkeletonAnimationBlendMode getBlendMode() {
        return mBlendState;
    }

    public void setBlendMode(SkeletonAnimationBlendMode mode) {
        mBlendState = mode;
    }

    public void optimiseAllAnimations() {
        optimiseAllAnimations(false);
    }

    public void optimiseAllAnimations(boolean preservingIdentityNodeTracks) {
        if (!preservingIdentityNodeTracks) {
            TreeSet<ENG_Short> set = new TreeSet<>();
            for (int i = 0; i < getNumBones(); ++i) {
                set.add(new ENG_Short((short) i));
            }
            for (ENG_Animation anim : mAnimationsList.values()) {
                anim._collectIdentityNodeTracks(set);
            }
            for (ENG_Animation anim : mAnimationsList.values()) {
                anim._destroyNodeTracks(set);
            }
        }
        for (ENG_Animation anim : mAnimationsList.values()) {
            anim.optimise(false);
        }
    }

    public void _updateTransforms() {
        for (ENG_Bone bone : mRootBones) {
            bone._update(true, false);
        }
        mManualBonesDirty = false;
    }

    public void addLinkedSkeletonAnimationSource(String skelName) {
        addLinkedSkeletonAnimationSource(skelName, 1.0f);
    }

    public void addLinkedSkeletonAnimationSource(String skelName, float scale) {
        for (LinkedSkeletonAnimationSource l : mLinkedSkeletonAnimSourceList) {
            if (l.skeletonName.equals(skelName)) {
                return;
            }
        }

        ENG_Skeleton skeleton =
                ENG_SkeletonManager.getSingleton().load(skelName);
        mLinkedSkeletonAnimSourceList.add(new LinkedSkeletonAnimationSource(
                skelName, scale, skeleton));
    }

    public void removeAllLinkedSkeletonAnimationSources() {
        mLinkedSkeletonAnimSourceList.clear();
    }

    public Iterator<LinkedSkeletonAnimationSource>
    getLinkedSkeletonAnimationSourceIterator() {
        return mLinkedSkeletonAnimSourceList.iterator();
    }

    public void _notifyManualBonesDirty() {
        mManualBonesDirty = true;
    }

    public void _notifyManualBoneStateChange(ENG_Bone bone) {
        if (bone.isManuallyControlled()) {
            mManualBones.add(bone);
        } else {
            mManualBones.remove(bone);
        }
    }

    public boolean getManualBonesDirty() {
        return mManualBonesDirty;
    }

    public boolean hasManualBones() {
        return !mManualBones.isEmpty();
    }

    private static class DeltaTransform {
        public final ENG_Vector4D translate = new ENG_Vector4D();
        public final ENG_Quaternion rotate = new ENG_Quaternion();
        public final ENG_Vector4D scale = new ENG_Vector4D();
        public boolean isIdentity;
    }

    public void _mergeSkeletonAnimations(ENG_Skeleton source,
                                         ArrayList<ENG_Short> boneHandleMap) {
        _mergeSkeletonAnimations(source, boneHandleMap,
                new ArrayList<>());
    }

    public void _mergeSkeletonAnimations(ENG_Skeleton source,
                                         ArrayList<ENG_Short> boneHandleMap,
                                         ArrayList<String> animations) {
        int srcNumBones = source.getNumBones();
        int dstNumBones = getNumBones();

        if (boneHandleMap.size() != srcNumBones) {
            throw new IllegalArgumentException(
                    "Number of bones in the bone handle map must equal to " +
                            "number of bones in the source skeleton.");
        }

        boolean existsMissingBone = false;

        for (int handle = 0; handle < srcNumBones; ++handle) {
            ENG_Bone srcBone = source.getBone((short) handle);
            ENG_Short dstHandle = boneHandleMap.get(handle);

            if (dstHandle.getValue() < dstNumBones) {
                ENG_Bone dstBone = getBone(dstHandle.getValue());

                ENG_Bone srcParent = (ENG_Bone) srcBone.getParent();
                ENG_Bone dstParent = (ENG_Bone) dstBone.getParent();

                if ((srcParent != null || dstParent != null) &&
                        (srcParent == null || dstParent == null ||
                                boneHandleMap.get(srcParent.getHandle()).getValue() !=
                                        dstParent.getHandle())) {
                    throw new IllegalArgumentException(
                            "Source skeleton incompatible with this skeleton: " +
                                    "difference hierarchy between bone '" +
                                    srcBone.getName() +
                                    "' and '" + dstBone.getName() + "'.");
                }
            } else {
                existsMissingBone = true;
            }
        }

        if (existsMissingBone) {
            for (int handle = 0; handle < srcNumBones; ++handle) {
                ENG_Bone srcBone = source.getBone((short) handle);
                ENG_Short dstHandle = boneHandleMap.get(handle);

                if (dstHandle.getValue() >= dstNumBones) {
                    ENG_Bone bone =
                            createBone(srcBone.getName(), dstHandle.getValue());
                    bone.setPosition(srcBone.getInitialPosition());
                    bone.setOrientation(srcBone.getInitialOrientation());
                    bone.setScale(srcBone.getInitialScale());
                    bone.setInitialState();
                }
            }

            for (int handle = 0; handle < srcNumBones; ++handle) {
                ENG_Bone srcBone = source.getBone((short) handle);
                ENG_Short dstHandle = boneHandleMap.get(handle);

                if (dstHandle.getValue() >= dstNumBones) {
                    ENG_Bone srcParent = (ENG_Bone) srcBone.getParent();
                    if (srcParent != null) {
                        ENG_Bone dstParent =
                                getBone(boneHandleMap.get(srcParent.getHandle())
                                        .getValue());
                        ENG_Bone dstBone = getBone(dstHandle.getValue());
                        dstParent.addChild(dstBone);
                    }
                }
            }

            deriveRootBone();

            reset(true);
            setBindingsPose();
        }

        ArrayList<DeltaTransform> deltaTransforms =
                new ArrayList<>();
        for (int i = 0; i < srcNumBones; ++i) {
            deltaTransforms.add(new DeltaTransform());
        }

        for (int handle = 0; handle < srcNumBones; ++handle) {
            ENG_Bone srcBone = source.getBone((short) handle);
            DeltaTransform deltaTransform = deltaTransforms.get(handle);
            ENG_Short dstHandle = boneHandleMap.get(handle);

            if (dstHandle.getValue() < dstNumBones) {
                ENG_Bone dstBone = getBone(dstHandle.getValue());
                srcBone.getInitialPosition().sub(dstBone.getInitialPosition(),
                        deltaTransform.translate);
                dstBone.getInitialOrientation().inverseRet().mul(
                        srcBone.getInitialOrientation(), deltaTransform.rotate);
                srcBone.getInitialScale().div(
                        dstBone.getInitialScale(), deltaTransform.scale);

                float tolerance = 1e-3f;
                ENG_Vector4D axis = new ENG_Vector4D();
                float angle = deltaTransform.rotate.toAngleAxisRad(axis);
                deltaTransform.isIdentity =
                        deltaTransform.translate.equals(ENG_Math.VEC4_ZERO) &&
                                deltaTransform.scale.equals(ENG_Math.VEC4_SCALE) &&
                                ENG_Float.compareTo(angle, 0.0f, tolerance) ==
                                        ENG_Utility.COMPARE_EQUAL_TO;
            } else {
                deltaTransform.translate.set(ENG_Math.VEC3_ZERO);
                deltaTransform.rotate.set(ENG_Math.QUAT_IDENTITY);
                deltaTransform.scale.set(ENG_Math.VEC3_SCALE);
                deltaTransform.isIdentity = true;
            }
        }

        short numAnimations;
        if (animations.isEmpty()) {
            numAnimations = source.getNumAnimations();
        } else {
            numAnimations = (short) animations.size();
        }

        for (short i = 0; i < numAnimations; ++i) {
            ENG_Animation animation;
            if (animations.isEmpty()) {
                animation = source.getAnimation(i);
            } else {
                animation =
                        source._getAnimationImpl(animations.get(i));
                LinkedSkeletonAnimationSource linkedSkeletonAnimation =
                        source.getLinkedSkeletonAnimation(animations.get(i));

                if (animation == null || linkedSkeletonAnimation != null) {
                    throw new IllegalArgumentException(
                            "No animation entry found named " +
                                    animations.get(i));
                }

            }

            ENG_Animation dstAnimation =
                    createAnimation(animation.getName(), animation.getLength());

            dstAnimation.setInterpolationMode(animation.getInterpolationMode());
            dstAnimation.setRotationInterpolationMode(
                    animation.getRotationInterpolationMode());

            for (int handle = 0; handle < srcNumBones; ++handle) {

                DeltaTransform deltaTransform = deltaTransforms.get(handle);
                ENG_Short dstHandle = boneHandleMap.get(handle);

                if (animation.hasNodeTrack((short) handle)) {
                    ENG_NodeAnimationTrack srcTrack =
                            animation.getNodeTrack((short) handle);
                    ENG_NodeAnimationTrack dstTrack =
                            dstAnimation.createNodeTrack(dstHandle.getValue(),
                                    getBone((short) handle));
                    dstTrack.setUseShortestRotationPath(
                            srcTrack.getUseShortestRotationPath());

                    for (short k = 0; k < srcTrack.getNumKeyFrames(); ++k) {
                        ENG_TransformKeyFrame srcKeyFrame =
                                srcTrack.getNodeKeyFrame(k);
                        ENG_TransformKeyFrame dstKeyFrame =
                                dstTrack.
                                        createNodeKeyFrame(srcKeyFrame.getTime());

                        if (deltaTransform.isIdentity) {
                            dstKeyFrame.setTranslate(
                                    srcKeyFrame.getTranslate());
                            dstKeyFrame.setRotation(
                                    srcKeyFrame.getRotation());
                            dstKeyFrame.setScale(srcKeyFrame.getScale());
                        } else {
                            dstKeyFrame.setTranslate(
                                    deltaTransform.translate.addAsPt(
                                            srcKeyFrame.getTranslate()));
                            dstKeyFrame.setRotation(
                                    deltaTransform.rotate.mulRet(
                                            srcKeyFrame.getRotation()));
                            dstKeyFrame.setScale(
                                    deltaTransform.scale.mulAsPt(
                                            srcKeyFrame.getScale()));
                        }
                    }
                } else if (!deltaTransform.isIdentity) {
                    ENG_NodeAnimationTrack dstTrack =
                            dstAnimation.createNodeTrack(dstHandle.getValue(),
                                    getBone((short) handle));
                    ENG_TransformKeyFrame dstKeyFrame;

                    dstKeyFrame = dstTrack.createNodeKeyFrame(0.0f);
                    dstKeyFrame.setTranslate(
                            deltaTransform.translate);
                    dstKeyFrame.setRotation(
                            deltaTransform.rotate);
                    dstKeyFrame.setScale(
                            deltaTransform.scale);

                    dstKeyFrame =
                            dstTrack.createNodeKeyFrame(
                                    dstAnimation.getLength());
                    dstKeyFrame.setTranslate(
                            deltaTransform.translate);
                    dstKeyFrame.setRotation(
                            deltaTransform.rotate);
                    dstKeyFrame.setScale(
                            deltaTransform.scale);
                }
            }
        }
    }

    public void _buildMapBoneByHandle(ENG_Skeleton source,
                                      ArrayList<ENG_Short> boneHandleMap) {
        int numBones = source.getNumBones();
        boneHandleMap.ensureCapacity(numBones);
        for (int i = 0; i < numBones; ++i) {
            boneHandleMap.add(new ENG_Short((short) i));
        }
    }

    public void _buildMapBoneByName(ENG_Skeleton source,
                                    ArrayList<ENG_Short> boneHandleMap) {
        int numBones = source.getNumBones();
        boneHandleMap.ensureCapacity(numBones);
        int numBones2 = getNumBones();
        for (int i = 0; i < numBones; ++i) {
            ENG_Bone bone = getBone((short) i);
            if (mBoneList.contains(bone)) {
                boneHandleMap.add(new ENG_Short(bone.getHandle()));
            } else {
                boneHandleMap.add(new ENG_Short((short) numBones2++));
            }
        }
    }

    protected void loadImpl() {
        new ENG_SkeletonSerializer().importSkeleton(
                MainApp.getGame().getGameResourcesDir() + mName, this);
    }

    @Override
    public ENG_StringInterface getStringInterface() {

        return stringInterface;
    }
}
