/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_VertexData.HardwareAnimationData;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.ENG_VertexPoseKeyFrame.PoseRef;

import java.util.ArrayList;

public class ENG_VertexAnimationTrack extends ENG_AnimationTrack {

    public enum VertexAnimationType {
        /// No animation
        VAT_NONE(0),
        /// Morph animation is made up of many interpolated snapshot keyframes
        VAT_MORPH(1),
        /// Pose animation is made up of a single delta pose keyframe
        VAT_POSE(2);

        private final short val;

        VertexAnimationType(int type) {
            val = (short) type;
        }

        public short getType() {
            return val;
        }

        public static VertexAnimationType getVertexAnimationType(short t) {
            switch (t) {
                case 0:
                    return VAT_NONE;
                case 1:
                    return VAT_MORPH;
                case 2:
                    return VAT_POSE;
                default:
                    throw new IllegalArgumentException(t + " type not a valid " +
                            "VertexAnimationType");
            }
        }
    }

    public enum TargetMode {
        /// Interpolate vertex positions in software
        TM_SOFTWARE,
        /**
         * Bind keyframe 1 to position, and keyframe 2 to a texture coordinate
         * for interpolation in hardware
         */
        TM_HARDWARE
    }

    /// Animation type
    protected final VertexAnimationType mAnimationType;
    /// Target to animate
    protected ENG_VertexData mTargetVertexData;
    /// Mode to apply
    protected TargetMode mTargetMode;

    public ENG_VertexAnimationTrack(ENG_Animation parent, short handle,
                                    VertexAnimationType animType) {
        super(parent, handle);
        
        mAnimationType = animType;
    }

    public ENG_VertexAnimationTrack(ENG_Animation parent, short handle,
                                    VertexAnimationType animType,
                                    ENG_VertexData data, TargetMode mode) {
        super(parent, handle);
        mAnimationType = animType;
        mTargetVertexData = data;
        mTargetMode = mode;
    }

    public ENG_VertexAnimationTrack(ENG_Animation parent, short handle,
                                    VertexAnimationType animType,
                                    ENG_VertexData data) {
        this(parent, handle, animType, data, TargetMode.TM_SOFTWARE);
    }


    @Override
    protected ENG_KeyFrame createKeyFrameImpl(float time) {
        
        ENG_KeyFrame kf;
        switch (mAnimationType) {
            default:
            case VAT_MORPH:
                kf = new ENG_VertexMorphKeyFrame(this, time);
                break;
            case VAT_POSE:
                kf = new ENG_VertexPoseKeyFrame(this, time);
                break;
        }
        return kf;
    }

    /** @noinspection deprecation*/
    public void applyPoseToVertexData(ENG_Pose pose, ENG_VertexData data,
                                      float influence) {
        if (mTargetMode == TargetMode.TM_HARDWARE) {
            assert (data.hwAnimationDataList.isEmpty());
            int hwIndex = data.hwAnimDataItemsUsed++;
            if (hwIndex < data.hwAnimationDataList.size()) {
                HardwareAnimationData animData =
                        data.hwAnimationDataList.get(hwIndex);
                data.vertexBufferBinding.setBinding(
                        animData.targetVertexElement.getSource(),
                        pose._getHardwareVertexBuffer(data.vertexCount));
                animData.parametric = influence;
            }
        } else {
            ENG_Mesh.softwareVertexPoseBlend(influence, pose.getVertexOffsets(), data);
        }
    }

    @Override
    public void optimise() {
        

    }

    @Override
    public void getInterpolatedKeyFrame(ENG_TimeIndex time, ENG_KeyFrame kf) {
        

    }

    @Override
    public void apply(ENG_TimeIndex time, float weight, float scale) {
        

        applyToVertexData(mTargetVertexData, time, weight, null);
    }

    public void applyToVertexData(ENG_VertexData data, ENG_TimeIndex timeIndex) {
        applyToVertexData(data, timeIndex, 1.0f, null);
    }

    /** @noinspection deprecation*/
    public void applyToVertexData(ENG_VertexData data, ENG_TimeIndex timeIndex,
                                  float weight, ArrayList<ENG_Pose> poseList) {
        if (mKeyFrames.isEmpty() || data == null) {
            return;
        }
        KeyFramesAtTime kft = new KeyFramesAtTime();
        float t = getKeyFramesAtTime(timeIndex, kft);

        if (mAnimationType == VertexAnimationType.VAT_MORPH) {
            ENG_VertexMorphKeyFrame vkf1 = (ENG_VertexMorphKeyFrame) kft.k1;
            ENG_VertexMorphKeyFrame vkf2 = (ENG_VertexMorphKeyFrame) kft.k2;

            if (mTargetMode == TargetMode.TM_HARDWARE) {
                assert (!data.hwAnimationDataList.isEmpty());

                ENG_VertexElement posElem =
                        data.vertexDeclaration
                                .findElementBySemantic(
                                        VertexElementSemantic.VES_POSITION, 0);
                data.vertexBufferBinding.setBinding(
                        posElem.getSource(), vkf1.getVertexBuffer());
                data.vertexBufferBinding.setBinding(
                        data.hwAnimationDataList.get(0)
                                .targetVertexElement.getSource(),
                        vkf2.getVertexBuffer());
                data.hwAnimationDataList.get(0).parametric = t;
            } else {
                ENG_Mesh.softwareVertexMorph(
                        t,
                        vkf1.getVertexBuffer(),
                        vkf2.getVertexBuffer(),
                        data);
            }
        } else {
            ENG_VertexPoseKeyFrame vkf1 = (ENG_VertexPoseKeyFrame) kft.k1;
            ENG_VertexPoseKeyFrame vkf2 = (ENG_VertexPoseKeyFrame) kft.k2;

            ArrayList<PoseRef> poseList1 = vkf1.getPoseReferences();
            ArrayList<PoseRef> poseList2 = vkf2.getPoseReferences();

            for (PoseRef p1 : poseList1) {
                float startInfluence = p1.influence;
                float endInfluence = 0.0f;

                for (PoseRef p2 : poseList2) {
                    if (p1.poseIndex == p2.poseIndex) {
                        endInfluence = p2.influence;
                        break;
                    }
                }

                float influence = startInfluence +
                        t * (endInfluence - startInfluence);
                influence *= weight;
                assert (p1.poseIndex >= 0 && p1.poseIndex <= poseList.size());
                ENG_Pose pose = poseList.get(p1.poseIndex);
                applyPoseToVertexData(pose, data, influence);
            }
            for (PoseRef p2 : poseList2) {
                boolean found = false;
                for (PoseRef p1 : poseList1) {
                    if (p2.poseIndex == p1.poseIndex) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    float influence = t * p2.influence;
                    influence *= weight;
                    assert (p2.poseIndex >= 0 && p2.poseIndex <= poseList.size());
                    ENG_Pose pose = poseList.get(p2.poseIndex);
                    applyPoseToVertexData(pose, data, influence);
                }
            }
        }
    }

    public VertexAnimationType getAnimationType() {
        return mAnimationType;
    }

    public ENG_VertexMorphKeyFrame createVertexMorphKeyFrame(float time) {
        if (mAnimationType != VertexAnimationType.VAT_MORPH) {
            throw new UnsupportedOperationException("animation type is not " +
                    "morph");
        }
        return (ENG_VertexMorphKeyFrame) createKeyFrame(time);
    }

    public ENG_VertexPoseKeyFrame createVertexPoseKeyFrame(float time) {
        if (mAnimationType != VertexAnimationType.VAT_POSE) {
            throw new UnsupportedOperationException("animation type is not " +
                    "pose");
        }
        return (ENG_VertexPoseKeyFrame) createKeyFrame(time);
    }

    public ENG_VertexMorphKeyFrame getVertexMorphKeyFrame(short index) {
        if (mAnimationType != VertexAnimationType.VAT_MORPH) {
            throw new UnsupportedOperationException("animation type is not " +
                    "morph");
        }
        return (ENG_VertexMorphKeyFrame) getKeyFrame(index);
    }

    public ENG_VertexPoseKeyFrame getVertexPoseKeyFrame(short index) {
        if (mAnimationType != VertexAnimationType.VAT_POSE) {
            throw new UnsupportedOperationException("animation type is not " +
                    "pose");
        }
        return (ENG_VertexPoseKeyFrame) getKeyFrame(index);
    }

    public ENG_VertexAnimationTrack _clone(ENG_Animation parent) {
        ENG_VertexAnimationTrack track =
                parent.createVertexTrack(mHandle, mAnimationType);
        track.mTargetMode = mTargetMode;
        populateClone(track);
        return track;
    }

    public void setAssociatedVertexData(ENG_VertexData data) {
        mTargetVertexData = data;
    }

    public ENG_VertexData getAssociatedVertexData() {
        return mTargetVertexData;
    }

    public void setTargetMode(TargetMode m) {
        mTargetMode = m;
    }

    public TargetMode getTargetMode() {
        return mTargetMode;
    }

}
