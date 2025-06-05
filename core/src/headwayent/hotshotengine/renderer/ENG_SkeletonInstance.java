/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

public class ENG_SkeletonInstance extends ENG_Skeleton {

    protected final ENG_Skeleton mSkeleton;
    protected final LinkedList<ENG_TagPoint> mActiveTagPoints =
            new LinkedList<>();
    protected final LinkedList<ENG_TagPoint> mFreeTagPoints =
            new LinkedList<>();
    protected short mNextTagPointAutoHandle;

    public ENG_SkeletonInstance(ENG_Skeleton masterCopy) {
        super();

        mSkeleton = masterCopy;
    }

    public short getNumAnimations() {
        return mSkeleton.getNumAnimations();
    }

    public ENG_Animation getAnimation(short index) {
        return mSkeleton.getAnimation(index);
    }

    @Override
    public ENG_Animation _getAnimationImpl(String name) {

        return mSkeleton._getAnimationImpl(name);
    }

    @Override
    public LinkedSkeletonAnimationSource
    getLinkedSkeletonAnimation(String name) {

        return mSkeleton.getLinkedSkeletonAnimation(name);
    }

    @Override
    public ENG_Animation createAnimation(String name, float length) {

        return mSkeleton.createAnimation(name, length);
    }

    @Override
    public ENG_Animation getAnimation(String name) {

        return mSkeleton.getAnimation(name);
    }

    @Override
    public void removeAnimation(String name) {

        mSkeleton.removeAnimation(name);
    }

    public ENG_TagPoint createTagPointOnBone(ENG_Bone bone) {
        return createTagPointOnBone(bone, ENG_Math.QUAT_IDENTITY,
                ENG_Math.PT4_ZERO);
    }

    public ENG_TagPoint createTagPointOnBone(ENG_Bone bone,
                                             ENG_Quaternion offsetOrientation, ENG_Vector4D offsetPosition) {
        ENG_TagPoint ret;
        if (mFreeTagPoints.isEmpty()) {
            ret = new ENG_TagPoint(mNextTagPointAutoHandle++, mSkeleton);
            mActiveTagPoints.add(ret);
        } else {
            ret = mFreeTagPoints.poll();
            mActiveTagPoints.add(ret);
            ret.setParentEntity(null);
            ret.setChildObject(null);
            ret.setInheritOrientation(true);
            ret.setInheritScale(true);
            ret.setInheritParentEntityOrientation(true);
            ret.setInheritParentEntityScale(true);
        }

        ret.setPosition(offsetPosition);
        ret.setOrientation(offsetOrientation);
        ret.setScale(ENG_Math.PT4_UNIT);
        ret.setBindingPose();
        bone.addChild(ret);

        return ret;
    }

    public void freeTagPoint(ENG_TagPoint tp) {
        if (mActiveTagPoints.remove(tp)) {
            if (tp.getParent() != null) {
                tp.getParent().removeChild(tp);
            }

            mFreeTagPoints.add(tp);
        } else {
            throw new IllegalArgumentException(tp.getName() + " is " +
                    "not a tag point for this skeleton instance");
        }
    }

    @Override
    public void addLinkedSkeletonAnimationSource(String skelName, float scale) {

        mSkeleton.addLinkedSkeletonAnimationSource(skelName, scale);
    }

    @Override
    public void removeAllLinkedSkeletonAnimationSources() {

        mSkeleton.removeAllLinkedSkeletonAnimationSources();
    }

    @Override
    public Iterator<LinkedSkeletonAnimationSource>
    getLinkedSkeletonAnimationSourceIterator() {

        return mSkeleton.getLinkedSkeletonAnimationSourceIterator();
    }

    @Override
    public void _initAnimationState(ENG_AnimationStateSet animSet) {

        mSkeleton._initAnimationState(animSet);
    }

    @Override
    public void _refreshAnimationState(ENG_AnimationStateSet animSet) {

        mSkeleton._refreshAnimationState(animSet);
    }

    @Override
    public String getName() {

        return mSkeleton.getName();
    }

    protected void cloneBoneAndChildren(ENG_Bone source, ENG_Bone parent) {
        ENG_Bone newBone;
        if (source.getName().isEmpty()) {
            newBone = createBone(source.getHandle());
        } else {
            newBone = createBone(source.getName(), source.getHandle());
        }
        if (parent == null) {
            mRootBones.add(newBone);
        } else {
            parent.addChild(newBone);
        }
        newBone.setOrientation(source.getOrientation());
        newBone.setPosition(source.getPosition());
        newBone.setScale(source.getScale());

        Iterator<Entry<String, ENG_Node>> iterator = source.getChildIterator();
        while (iterator.hasNext()) {
            cloneBoneAndChildren((ENG_Bone) iterator.next().getValue(), newBone);
        }
    }

    protected void loadImpl() {
        mNextAutoHandle = mSkeleton.mNextAutoHandle;
        mNextTagPointAutoHandle = 0;
        mBlendState = mSkeleton.mBlendState;

        Iterator<ENG_Bone> iterator = mSkeleton.getBoneIterator();
        while (iterator.hasNext()) {
            ENG_Bone next = iterator.next();
            cloneBoneAndChildren(next, null);
            next._update(true, false);
        }
        setBindingsPose();
    }

}
