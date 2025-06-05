/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_KeyFrame {

    protected final float mTime;
    protected final ENG_AnimationTrack mParentTrack;

    public ENG_KeyFrame(ENG_AnimationTrack parent, float time) {
        mParentTrack = parent;
        mTime = time;
    }

    public float getTime() {
        return mTime;
    }

    public ENG_KeyFrame _clone(ENG_AnimationTrack newParent) {
        return new ENG_KeyFrame(newParent, mTime);
    }
}
