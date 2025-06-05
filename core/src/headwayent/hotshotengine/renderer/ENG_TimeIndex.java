/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_TimeIndex {

    public static final int INVALID_KEY_INDEX = -1;

    protected final float mTimePos;
    protected int mKeyIndex = INVALID_KEY_INDEX;

    public ENG_TimeIndex(float time) {
        mTimePos = time;
    }

    public ENG_TimeIndex(float time, int keyIndex) {
        mTimePos = time;
        mKeyIndex = keyIndex;
    }

    public boolean hasKeyIndex() {
        return mKeyIndex != INVALID_KEY_INDEX;
    }

    public float getTimePos() {
        return mTimePos;
    }

    public int getKeyIndex() {
        return mKeyIndex;
    }
}
