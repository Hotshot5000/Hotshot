/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Short;
import headwayent.hotshotengine.util.ENG_ArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class ENG_AnimationTrack {

    public static abstract class Listener {
        public abstract boolean getInterpolatedKeyFrame(ENG_AnimationTrack t,
                                                        ENG_TimeIndex timeIndex, ENG_KeyFrame kf);
    }

    protected final ENG_ArrayList<ENG_KeyFrame> mKeyFrames =
            new ENG_ArrayList<>();
    protected final ENG_Animation mParent;
    protected final short mHandle;
    protected Listener mListener;
    protected final ENG_ArrayList<ENG_Short> mKeyFrameIndexMap =
            new ENG_ArrayList<>();

    protected abstract ENG_KeyFrame createKeyFrameImpl(float time);

    protected void populateClone(ENG_AnimationTrack clone) {
        for (ENG_KeyFrame kf : mKeyFrames) {
            clone.mKeyFrames.add(kf._clone(clone));
        }
    }

    public ENG_AnimationTrack(ENG_Animation parent, short handle) {
        mParent = parent;
        mHandle = handle;
    }

    public short getHandle() {
        return mHandle;
    }

    public int getNumKeyFrames() {
        return mKeyFrames.size();
    }

    public ENG_KeyFrame getKeyFrame(short handle) {
        assert (handle >= 0 && handle < mKeyFrames.size());

        return mKeyFrames.get(handle);
    }

    public static class KeyFramesAtTime {
        public ENG_KeyFrame k1, k2;
        public short firstKeyIndex;
    }

    public static class KeyFrameTimeLess implements Comparator<ENG_KeyFrame> {

        @Override
        public int compare(ENG_KeyFrame lhs, ENG_KeyFrame rhs) {
            
            float res = lhs.getTime() - rhs.getTime();
            if (Math.abs(res) < ENG_Math.FLOAT_EPSILON) {
                return 0;
            }

            return res < 0.0f ? -1 : 1;
        }

    }

    public float getKeyFramesAtTime(ENG_TimeIndex timeIndex,
                                    KeyFramesAtTime kft) {
        float t1, t2;

        float timePos = timeIndex.getTimePos();

        int i;
        if (timeIndex.hasKeyIndex()) {
            assert (timeIndex.getKeyIndex() < mKeyFrameIndexMap.size());
            i = mKeyFrameIndexMap.get(timeIndex.getKeyIndex()).getValue();
        } else {
            float totalAnimationLength = mParent.getLength();
            assert (totalAnimationLength > 0.0f);

            while (timePos > totalAnimationLength && totalAnimationLength > 0.0f) {
                timePos -= totalAnimationLength;
            }

            Collections.sort(mKeyFrames, new KeyFrameTimeLess());
            int pos = 0;
            for (ENG_KeyFrame kf : mKeyFrames) {
                if (timePos > kf.getTime()) {
                    break;
                }
                ++pos;
            }
            i = pos;
        }
        if (i == mKeyFrames.size()) {
            kft.k2 = mKeyFrames.get(0);
            t2 = mParent.getLength() + kft.k2.getTime();
            --i;
        } else {
            kft.k2 = mKeyFrames.get(i);
            t2 = kft.k2.getTime();

            if (i > 0 && timePos < kft.k2.getTime()) {
                --i;
            }
        }

        kft.firstKeyIndex = (short) i;
        kft.k1 = mKeyFrames.get(i);

        t1 = kft.k1.getTime();
        if (Math.abs(t1 - t2) < ENG_Math.FLOAT_EPSILON) {
            return 0.0f;
        } else {
            return (timePos - t1) / (t2 - t1);
        }

    }

    public ENG_KeyFrame createKeyFrame(float timePos) {
        ENG_KeyFrame frame = createKeyFrameImpl(timePos);
        Collections.sort(mKeyFrames, new KeyFrameTimeLess());
        int i = 0;
        for (ENG_KeyFrame key : mKeyFrames) {
            if (key.getTime() > frame.getTime()) {
                break;
            }
            ++i;
        }
        mKeyFrames.add(i, frame);

        _keyFrameDataChanged();
        mParent._keyFrameListChanged();
        return frame;
    }

    public void removeKeyFrame(short i) {
        assert (i >= 0);
        ENG_KeyFrame remove = mKeyFrames.remove(i);
        if (remove == null) {
            throw new IllegalArgumentException(i + " is not a valid key frame" +
                    " index");
        }
        _keyFrameDataChanged();
        mParent._keyFrameListChanged();
    }

    public void removeAllKeyFrames() {
        _keyFrameDataChanged();
        mParent._keyFrameListChanged();
        mKeyFrames.clear();
    }

    public void _collectKeyFrameTimes(ArrayList<ENG_Float> keyFrameTimes) {
        for (ENG_KeyFrame kf : mKeyFrames) {
            int i = 0;
            for (ENG_Float f : keyFrameTimes) {
                if (kf.getTime() > f.getValue()) {
                    break;
                }
                ++i;
            }
            keyFrameTimes.add(i, new ENG_Float(kf.getTime()));
        }
    }

    public void _buildKeyFrameIndexMap(ArrayList<ENG_Float> keyFrameTimes) {
        int i = 0, j = 0;
        while (j <= keyFrameTimes.size()) {
            mKeyFrameIndexMap.add(new ENG_Short((short) i));
            while (i < mKeyFrames.size() &&
                    mKeyFrames.get(i).getTime() <=
                            keyFrameTimes.get(j).getValue()) {
                ++i;
            }
            ++j;
        }
    }

    public abstract void getInterpolatedKeyFrame(ENG_TimeIndex time,
                                                 ENG_KeyFrame kf);

    public void apply(ENG_TimeIndex time) {
        apply(time, 1.0f, 1.0f);
    }

    public void apply(ENG_TimeIndex time, float weight) {
        apply(time, weight, 1.0f);
    }

    public abstract void apply(ENG_TimeIndex time, float weight, float scale);

    public void _keyFrameDataChanged() {

    }

    public boolean hasNonZeroKeyFrames() {
        return true;
    }

    public void optimise() {

    }

    public void setListener(Listener l) {
        mListener = l;
    }

    public ENG_Animation getParent() {
        return mParent;
    }


}
