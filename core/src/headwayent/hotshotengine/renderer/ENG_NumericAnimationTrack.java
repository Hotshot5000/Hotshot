/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_NumberOperations;

public class ENG_NumericAnimationTrack extends ENG_AnimationTrack {

    protected ENG_AnimableValue mTargetAnim;

    public ENG_NumericAnimationTrack(ENG_Animation parent, short handle) {
        super(parent, handle);

    }

    public ENG_NumericAnimationTrack(ENG_Animation parent, short handle,
                                     ENG_AnimableValue v) {
        super(parent, handle);
        mTargetAnim = v;
    }

    @Override
    protected ENG_KeyFrame createKeyFrameImpl(float time) {

        return new ENG_NumericKeyFrame(this, time);
    }

    public ENG_NumericKeyFrame createNumericKeyFrame(float time) {
        return (ENG_NumericKeyFrame) createKeyFrame(time);
    }

    @Override
    public void getInterpolatedKeyFrame(ENG_TimeIndex time, ENG_KeyFrame kf) {


        if (mListener != null) {
            if (mListener.getInterpolatedKeyFrame(this, time, kf)) {
                return;
            }
        }

        ENG_NumericKeyFrame kret = (ENG_NumericKeyFrame) kf;

        KeyFramesAtTime kft = new KeyFramesAtTime();
        float t = getKeyFramesAtTime(time, kft);

        ENG_NumericKeyFrame k1 = (ENG_NumericKeyFrame) kft.k1;
        ENG_NumericKeyFrame k2 = (ENG_NumericKeyFrame) kft.k2;

        if (t == 0.0f) {
            kret.setValue(k1.getValue());
        } else {
            ENG_NumberOperations<?> sub =
                    k2.getValue().sub((ENG_NumberOperations) k2.getValue());
            kret.setValue(k1.getValue().add((ENG_NumberOperations) sub.mul(t)));
        }
    }

    @Override
    public void apply(ENG_TimeIndex time, float weight, float scale) {


        applyToAnimable(mTargetAnim, time, weight, scale);
    }

    private void applyToAnimable(ENG_AnimableValue mTargetAnim2,
                                 ENG_TimeIndex time, float weight, float scale) {

        if (mKeyFrames.isEmpty() || weight == 0.0f || scale == 0.0f) {
            return;
        }

        ENG_NumericKeyFrame kf = new ENG_NumericKeyFrame(null, time.getTimePos());
        getInterpolatedKeyFrame(time, kf);
        ENG_NumberOperations<?> mul = kf.getValue().mul(weight * scale);

        mTargetAnim2.applyDeltaValue(mul);
    }

    public ENG_AnimableValue getAssociatedAnimable() {
        return mTargetAnim;
    }

    public void setAssociatedAnimable(ENG_AnimableValue v) {
        mTargetAnim = v;
    }

    public ENG_NumericKeyFrame getNumericKeyFrame(short index) {
        return (ENG_NumericKeyFrame) getKeyFrame(index);
    }

    public ENG_NumericAnimationTrack _clone(ENG_Animation newParent) {
        ENG_NumericAnimationTrack kf = newParent.createNumericTrack(mHandle);
        kf.mTargetAnim = mTargetAnim;
        populateClone(kf);
        return kf;
    }


}
