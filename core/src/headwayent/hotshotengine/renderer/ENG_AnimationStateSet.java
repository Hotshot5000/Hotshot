/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class ENG_AnimationStateSet {

    protected final ReentrantLock lock = new ReentrantLock();
    protected long mDirtyFrameNumber = Long.MAX_VALUE;
    protected final HashMap<String, ENG_AnimationState> mAnimationStates =
            new HashMap<>();
    protected final LinkedList<ENG_AnimationState> mEnabledAnimationStates =
            new LinkedList<>();

    public ENG_AnimationStateSet() {

    }

    public ENG_AnimationStateSet(ENG_AnimationStateSet rhs) {

    }

    public ENG_AnimationState createAnimationState(String animName,
                                                   float timePos, float length
    ) {
        return createAnimationState(animName, timePos, length, 1.0f, false);
    }

    public ENG_AnimationState createAnimationState(String animName,
                                                   float timePos, float length,
                                                   float weight) {
        return createAnimationState(animName, timePos, length, weight, false);
    }

    public ENG_AnimationState createAnimationState(String animName,
                                                   float timePos, float length,
                                                   float weight, boolean enabled) {
        lock.lock();
        try {
            if (mAnimationStates.containsKey(animName)) {
                throw new IllegalArgumentException(animName +
                        " already exists");
            }
            ENG_AnimationState state = new ENG_AnimationState(animName, this,
                    timePos, length, weight, enabled);
            mAnimationStates.put(animName, state);
            return state;
        } finally {
            lock.unlock();
        }
    }

    public ENG_AnimationState getAnimationState(String animName) {
        lock.lock();
        try {
            ENG_AnimationState state = mAnimationStates.get(animName);
            if (state == null) {
                throw new IllegalArgumentException(animName +
                        " is an invalid animation state name");
            }
            return state;
        } finally {
            lock.unlock();
        }
    }

    public boolean hasAnimationState(String animName) {
        lock.lock();
        try {
            return mAnimationStates.containsKey(animName);
        } finally {
            lock.unlock();
        }
    }

    public void removeAnimationState(String animName) {
        lock.lock();
        try {
            ENG_AnimationState remove = mAnimationStates.remove(animName);
            if (remove == null) {
                throw new IllegalArgumentException(animName +
                        " is an invalid animation state name");
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeAllAnimationStates() {
        lock.lock();
        try {
            mAnimationStates.clear();
            mEnabledAnimationStates.clear();
        } finally {
            lock.unlock();
        }
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public Iterator<Entry<String, ENG_AnimationState>>
    getAnimationStateIterator() {
        lock.lock();
        try {
            return mAnimationStates.entrySet().iterator();
        } finally {
            lock.unlock();
        }
    }

    public void copyMatchingState(ENG_AnimationStateSet target) {
        target.getLock().lock();
        lock.lock();
        try {
            Iterator<Entry<String, ENG_AnimationState>> iterator =
                    target.getAnimationStateIterator();
            while (iterator.hasNext()) {
                Entry<String, ENG_AnimationState> entry = iterator.next();
                ENG_AnimationState state = mAnimationStates.get(entry.getKey());
                if (state == null) {
                    throw new IllegalArgumentException(entry.getKey() +
                            " could not be found in this animation state set");
                } else {
                    entry.getValue().copyStateFrom(state);
                }
            }

            target.mEnabledAnimationStates.clear();

            for (ENG_AnimationState src : mEnabledAnimationStates) {
                ENG_AnimationState state =
                        target.mAnimationStates.get(src.getAnimationName());
                if (state != null) {
                    target.mEnabledAnimationStates.add(state);
                }
            }
            target.mDirtyFrameNumber = mDirtyFrameNumber;

        } finally {

            lock.unlock();
            target.getLock().unlock();
        }
    }

    public void _notifyDirty() {
        lock.lock();
        try {
            ++mDirtyFrameNumber;
        } finally {
            lock.unlock();
        }
    }

    public void _notifyAnimationStateEnabled(
            ENG_AnimationState target, boolean enabled) {
        lock.lock();
        try {
            mEnabledAnimationStates.remove(target);
            if (enabled) {
                mEnabledAnimationStates.add(target);
            }
            _notifyDirty();
        } finally {
            lock.unlock();
        }
    }

    public Iterator<ENG_AnimationState> getEnabledAnimationStateIterator() {
        lock.lock();
        try {
            return mEnabledAnimationStates.iterator();
        } finally {
            lock.unlock();
        }
    }

    public long getDirtyFrameNumber() {
        return mDirtyFrameNumber;
    }

    public boolean hasEnabledAnimationState() {
        return !mEnabledAnimationStates.isEmpty();
    }

}
