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

public class ENG_TransformKeyFrame extends ENG_KeyFrame {

    protected final ENG_Vector4D translate = new ENG_Vector4D();
    protected final ENG_Vector4D scale = new ENG_Vector4D(ENG_Math.PT4_UNIT);
    protected final ENG_Quaternion rotate = new ENG_Quaternion();

    public ENG_TransformKeyFrame(ENG_AnimationTrack parent, float time) {
        super(parent, time);

    }

    public void setTranslate(ENG_Vector4D v) {
        translate.set(v);
    }

    public ENG_Vector4D getTranslate() {
        return translate;
    }

    public void setScale(ENG_Vector4D v) {
        scale.set(v);
    }

    public ENG_Vector4D getScale() {
        return scale;
    }

    public void setRotation(ENG_Quaternion v) {
        rotate.set(v);
    }

    public ENG_Quaternion getRotation() {
        return rotate;
    }

    public ENG_KeyFrame _clone(ENG_AnimationTrack newParent) {
        ENG_TransformKeyFrame frame = new ENG_TransformKeyFrame(newParent, mTime);
        frame.translate.set(translate);
        frame.scale.set(scale);
        frame.rotate.set(rotate);
        return frame;
    }

}
