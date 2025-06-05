/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_VertexMorphKeyFrame extends ENG_KeyFrame {

    protected ENG_HardwareVertexBuffer mBuffer;

    public ENG_VertexMorphKeyFrame(ENG_AnimationTrack parent, float time) {
        super(parent, time);

    }

    public void setBuffer(ENG_HardwareVertexBuffer buf) {
        mBuffer = buf;
    }

    public ENG_HardwareVertexBuffer getVertexBuffer() {
        return mBuffer;
    }

    @Override
    public ENG_KeyFrame _clone(ENG_AnimationTrack newParent) {
        
        ENG_VertexMorphKeyFrame frame = new ENG_VertexMorphKeyFrame(newParent, mTime);
        frame.mBuffer = mBuffer;
        return frame;

    }

}
