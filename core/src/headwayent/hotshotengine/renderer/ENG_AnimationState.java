/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Float;

import java.util.ArrayList;
import java.util.Objects;

public class ENG_AnimationState {

    protected ArrayList<ENG_Float> mBlendMask;

    protected final String mAnimationName;
    protected final ENG_AnimationStateSet mParent;
    protected float mTimePos;
    protected float mLength;
    protected float mWeight;
    protected boolean mEnabled;
    protected boolean mLoop = true;

    public ENG_AnimationState(String animName, ENG_AnimationStateSet parent,
                              float timePos, float length) {
        this(animName, parent, timePos, length, 1.0f, false);
    }

    public ENG_AnimationState(String animName, ENG_AnimationStateSet parent,
                              float timePos, float length, float weight) {
        this(animName, parent, timePos, length, weight, false);
    }

    public ENG_AnimationState(String animName, ENG_AnimationStateSet parent,
                              float timePos, float length, float weight, boolean enabled) {

        mAnimationName = animName;
        mParent = parent;
        mTimePos = timePos;
        mLength = length;
        mWeight = weight;
        mEnabled = enabled;
        parent._notifyDirty();
    }

    public ENG_AnimationState(ENG_AnimationStateSet parent, ENG_AnimationState set) {
        mAnimationName = set.mAnimationName;
        mParent = parent;
        mTimePos = set.mTimePos;
        mLength = set.mLength;
        mWeight = set.mWeight;
        mEnabled = set.mEnabled;
        parent._notifyDirty();
    }

    public String getAnimationName() {
        return mAnimationName;
    }

    public float getTimePosition() {
        return mTimePos;
    }

    public void setTimePosition(float t) {
        if (t != mTimePos) {
            mTimePos = t;
            if (mLoop) {
                mTimePos = mTimePos % mLength;
                if (mTimePos < 0.0f) {
                    mTimePos += mLength;
                }
            } else {
                if (mTimePos < 0.0f) {
                    mTimePos = 0.0f;
                }
                if (mTimePos > mLength) {
                    mTimePos = mLength;
                }
            }
            if (mEnabled) {
                mParent._notifyDirty();
            }
        }
    }

    public float getLength() {
        return mLength;
    }

    public void setLength(float l) {
        mLength = l;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float w) {
        mWeight = w;
        if (mEnabled) {
            mParent._notifyDirty();
        }
    }

    public void addTime(float offset) {
        setTimePosition(mTimePos + offset);
    }

    public boolean hasEnded() {
        return (mTimePos >= mLength && !mLoop);
    }

    public boolean getEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {

        mEnabled = enabled;
        mParent._notifyAnimationStateEnabled(this, enabled);
    }

    public boolean getLoop() {
        return mLoop;
    }

    public void setLoop(boolean b) {
        mLoop = b;
    }

    public ENG_AnimationStateSet getParent() {
        return mParent;
    }

    @Override
    public boolean equals(Object o) {
        
        if (o instanceof ENG_AnimationState) {
            ENG_AnimationState rhs = (ENG_AnimationState) o;
            if (Objects.equals(mAnimationName, rhs.mAnimationName) &&
                    mEnabled == rhs.mEnabled &&
                    mTimePos == rhs.mTimePos &&
                    mWeight == rhs.mWeight &&
                    mLength == rhs.mLength &&
                    mLoop == rhs.mLoop) {
                return true;
            }
        }
        return false;
    }

    public void copyStateFrom(ENG_AnimationState animState) {
        mTimePos = animState.mTimePos;
        mLength = animState.mLength;
        mWeight = animState.mWeight;
        mEnabled = animState.mEnabled;
        mLoop = animState.mLoop;
        mParent._notifyDirty();
    }

    public void createBlendMask(int blendMaskSizeHint) {
        createBlendMask(blendMaskSizeHint, 1.0f);
    }

    public void createBlendMask(int blendMaskSizeHint, float initialWeight) {
        if (mBlendMask == null) {
            mBlendMask = new ArrayList<>();
            if (initialWeight > 0.0f) {
                for (int i = 0; i < blendMaskSizeHint; ++i) {
                    mBlendMask.add(new ENG_Float(initialWeight));
                }
            }
        }
    }

    public void destroyBlendMask() {
        mBlendMask = null;
    }

    public void _setBlendMaskData(ArrayList<ENG_Float> blendMaskData) {
        assert (mBlendMask != null);

        if (blendMaskData == null) {
            destroyBlendMask();
            return;
        }
        int size = blendMaskData.size();
        if (size > mBlendMask.size()) {
            size = mBlendMask.size();
        }
        for (int i = 0; i < size; ++i) {
            mBlendMask.get(i).setValue(blendMaskData.get(i));
        }
        if (mEnabled) {
            mParent._notifyDirty();
        }
    }

    public void _setBlendMask(ArrayList<ENG_Float> blendMaskData) {
        if (mBlendMask == null) {
            createBlendMask(blendMaskData.size(), 0.0f);
        }
        for (ENG_Float f : blendMaskData) {
            mBlendMask.add(new ENG_Float(f));
        }
    }

    public ArrayList<ENG_Float> getBlendMask() {
        return mBlendMask;
    }

    public boolean hasBlendMask() {
        return mBlendMask != null;
    }

    public void setBlendMaskEntry(int boneHandle, float weight) {
        assert (mBlendMask != null && mBlendMask.size() > boneHandle);
        mBlendMask.get(boneHandle).setValue(weight);
        if (mEnabled) {
            mParent._notifyDirty();
        }
    }

    public float getBlendMaskEntry(int boneHandle) {
        assert (mBlendMask != null && mBlendMask.size() > boneHandle);
        return mBlendMask.get(boneHandle).getValue();
    }
}
