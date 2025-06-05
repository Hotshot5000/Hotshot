/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_NumberOperations;

public class ENG_NumericKeyFrame extends ENG_KeyFrame {

    protected ENG_NumberOperations<?> mValue;

    public ENG_NumericKeyFrame(ENG_AnimationTrack parent, float time) {
        super(parent, time);

    }

    public void setValue(ENG_NumberOperations<?> v) {
        mValue = v;
    }

    public ENG_NumberOperations<?> getValue() {
        return mValue;
    }

    public ENG_KeyFrame _clone(ENG_AnimationTrack newParent) {
        ENG_NumericKeyFrame kf = new ENG_NumericKeyFrame(newParent, mTime);
        kf.mValue = mValue;
        return kf;
    }

}
