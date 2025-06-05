/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.predefinedcontrollers;

import headwayent.hotshotengine.ENG_ControllerTypeFloat;
import headwayent.hotshotengine.ENG_IControllerValue;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState;

public class TextureFrameControllerValue implements
        ENG_IControllerValue<ENG_ControllerTypeFloat> {

    private final ENG_TextureUnitState mTextureLayer;

    public TextureFrameControllerValue(ENG_TextureUnitState tex) {
        mTextureLayer = tex;
    }

    @Override
    public void setValue(ENG_ControllerTypeFloat t) {
        
        int numFrames = mTextureLayer.getNumFrames();
        mTextureLayer.setCurrentFrame(
                (int) (t.get().getValue() * numFrames) % numFrames);
    }

    @Override
    public ENG_ControllerTypeFloat getValue() {
        
        int numFrames = mTextureLayer.getNumFrames();
        ENG_ControllerTypeFloat f = new ENG_ControllerTypeFloat();
        f.get().setValue((float) mTextureLayer.getCurrentFrame() / (float) numFrames);
        return f;
    }


}
