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
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState;

public class TexCoordModifierControllerValue implements ENG_IControllerValue<ENG_ControllerTypeFloat> {

    private final boolean mTransU;
    private final boolean mTransV;
    private final boolean mScaleU;
    private final boolean mScaleV;
    private final boolean mRotate;
    private final ENG_TextureUnitState mTextureLayer;

    public TexCoordModifierControllerValue(ENG_TextureUnitState t,
                                           boolean translateU, boolean translateV, boolean scaleU, boolean scaleV,
                                           boolean rotate) {
        mTextureLayer = t;
        mTransU = translateU;
        mTransV = translateV;
        mScaleU = scaleU;
        mScaleV = scaleV;
        mRotate = rotate;
    }

    @Override
    public void setValue(ENG_ControllerTypeFloat t) {
        
        if (mTransU) {
            mTextureLayer.setTextureUScroll(t.value.getValue());
        }
        if (mTransV) {
            mTextureLayer.setTextureVScroll(t.value.getValue());
        }
        if (mScaleU) {
            mTextureLayer.setTextureUScale(t.value.getValue());
        }
        if (mScaleV) {
            mTextureLayer.setTextureVScale(t.value.getValue());
        }
        if (mRotate) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    private final ENG_ControllerTypeFloat ret = new ENG_ControllerTypeFloat();

    @Override
    public ENG_ControllerTypeFloat getValue() {
        
        ENG_Matrix4 t = mTextureLayer.getTextureTransform();

        if (mTransU) {
            ret.value.setValue(t.get(0, 3));
            return ret;
        }
        if (mTransV) {
            ret.value.setValue(t.get(1, 3));
            return ret;
        }
        if (mScaleU) {
            ret.value.setValue(t.get(0, 0));
            return ret;
        }
        if (mScaleV) {
            ret.value.setValue(t.get(1, 1));
            return ret;
        }
        return ret;
    }


}
