/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Controller;
import headwayent.hotshotengine.ENG_ControllerFunction;
import headwayent.hotshotengine.ENG_ControllerTypeFloat;
import headwayent.hotshotengine.ENG_IControllerValue;
import headwayent.hotshotengine.predefinedcontrollers.AnimationControllerFunction;
import headwayent.hotshotengine.predefinedcontrollers.ScaleControllerFunction;
import headwayent.hotshotengine.predefinedcontrollers.TexCoordModifierControllerValue;
import headwayent.hotshotengine.predefinedcontrollers.TextureFrameControllerValue;

import java.util.ArrayList;

public class ENG_ControllerManager {

//    private static ENG_ControllerManager mgr;// = new ENG_ControllerManager();
    protected final ArrayList<ENG_Controller<ENG_ControllerTypeFloat>> mControllers = new ArrayList<>();
    protected final ENG_IControllerValue<ENG_ControllerTypeFloat> mFrameTimeController = new ENG_FrameTimeControllerValue();
    protected final ENG_ControllerFunction<ENG_ControllerTypeFloat> mPassthroughFunction = new ENG_PassthroughControllerFunction();
    protected long mLastFrameNumber;

    public ENG_ControllerManager() {
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        mgr = this;
    }

    public ENG_Controller<ENG_ControllerTypeFloat> createController(
            ENG_IControllerValue<ENG_ControllerTypeFloat> src,
            ENG_IControllerValue<ENG_ControllerTypeFloat> dest,
            ENG_ControllerFunction<ENG_ControllerTypeFloat> func) {
        ENG_Controller<ENG_ControllerTypeFloat> c =
                new ENG_Controller<>(src, dest, func);
        mControllers.add(c);
        return c;
    }

    public ENG_Controller<ENG_ControllerTypeFloat>
    createFrameTimePassthroughController(
            ENG_IControllerValue<ENG_ControllerTypeFloat> dest) {
        return createController(
                getFrameTimeSource(), dest, getPassthroughControllerFunction());
    }

    public void updateAllControllers() {
        long thisFrameNumber = ENG_RenderRoot.getRenderRoot().getNextFrameNumber();
        if (thisFrameNumber != mLastFrameNumber) {
            int len = mControllers.size();
            for (int i = 0; i < len; ++i) {
                mControllers.get(i).update();
            }
            mLastFrameNumber = thisFrameNumber;
        }
    }

    public void clearControllers() {
        mControllers.clear();
    }

    public float getTimeFactor() {
        return
                ((ENG_FrameTimeControllerValue) mFrameTimeController).getTimeFactor().getValue();
    }

    public void setTimeFactor(float tf) {
        ((ENG_FrameTimeControllerValue) mFrameTimeController).getTimeFactor().setValue(tf);
    }

    public float getFrameDelay() {
        return
                ((ENG_FrameTimeControllerValue) mFrameTimeController).getFrameDelay().getValue();
    }

    public void setFrameDelay(float tf) {
        ((ENG_FrameTimeControllerValue) mFrameTimeController).getFrameDelay().setValue(tf);
    }

    public float getElapsedTime() {
        return
                ((ENG_FrameTimeControllerValue) mFrameTimeController).getElapsedTime().getValue();
    }

    public void setElapsedTime(float tf) {
        ((ENG_FrameTimeControllerValue) mFrameTimeController).getElapsedTime().setValue(tf);
    }

    /**
     * @return the mFrameTimeController
     */
    public ENG_IControllerValue<ENG_ControllerTypeFloat> getFrameTimeSource() {
        return mFrameTimeController;
    }

    /**
     * @return the mPassthroughFunction
     */
    public ENG_ControllerFunction<ENG_ControllerTypeFloat>
    getPassthroughControllerFunction() {
        return mPassthroughFunction;
    }

    public static ENG_ControllerManager getSingleton() {
//        return mgr;
        return MainApp.getGame().getRenderRoot().getControllerManager();
    }

    public void destroyController(
            ENG_Controller<ENG_ControllerTypeFloat> controller) {

        mControllers.remove(controller);
    }

    public ENG_Controller<ENG_ControllerTypeFloat> createTextureAnimator(
            ENG_TextureUnitState tex, float seqTime) {
        return createController(mFrameTimeController,
                new TextureFrameControllerValue(tex),
                new AnimationControllerFunction(seqTime));
    }

    public ENG_Controller<ENG_ControllerTypeFloat> createTextureUVScroller(
            ENG_TextureUnitState layer, float speed) {


        ENG_Controller<ENG_ControllerTypeFloat> ret = null;
        if (speed != 0.0f) {
            TexCoordModifierControllerValue val = new TexCoordModifierControllerValue(
                    layer, true, true, false, false, false);
            ScaleControllerFunction func = new ScaleControllerFunction(-speed, true);
            ret = createController(mFrameTimeController, val, func);
        }
        return ret;
    }

    public ENG_Controller<ENG_ControllerTypeFloat> createTextureUScroller(
            ENG_TextureUnitState layer, float speed) {


        ENG_Controller<ENG_ControllerTypeFloat> ret = null;
        if (speed != 0.0f) {
            TexCoordModifierControllerValue val = new TexCoordModifierControllerValue(
                    layer, true, false, false, false, false);
            ScaleControllerFunction func = new ScaleControllerFunction(-speed, true);
            ret = createController(mFrameTimeController, val, func);
        }
        return ret;
    }

    public ENG_Controller<ENG_ControllerTypeFloat> createTextureVScroller(
            ENG_TextureUnitState layer, float speed) {


        ENG_Controller<ENG_ControllerTypeFloat> ret = null;
        if (speed != 0.0f) {
            TexCoordModifierControllerValue val = new TexCoordModifierControllerValue(
                    layer, false, true, false, false, false);
            ScaleControllerFunction func = new ScaleControllerFunction(-speed, true);
            ret = createController(mFrameTimeController, val, func);
        }
        return ret;
    }
}
