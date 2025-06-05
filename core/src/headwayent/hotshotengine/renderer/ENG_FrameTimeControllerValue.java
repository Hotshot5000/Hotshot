/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_ControllerTypeFloat;
import headwayent.hotshotengine.ENG_IControllerValue;
import headwayent.hotshotengine.basictypes.ENG_Float;

public class ENG_FrameTimeControllerValue extends ENG_FrameListener
        implements ENG_IControllerValue<ENG_ControllerTypeFloat> {

    protected final ENG_ControllerTypeFloat mFrameTime = new ENG_ControllerTypeFloat();
    protected final ENG_ControllerTypeFloat mTimeFactor = new ENG_ControllerTypeFloat();
    protected final ENG_ControllerTypeFloat mElapsedTime = new ENG_ControllerTypeFloat();
    protected final ENG_ControllerTypeFloat mFrameDelay = new ENG_ControllerTypeFloat();

    private final ENG_Float divFloat = new ENG_Float();
    private final ENG_Float frameDelayTemp = new ENG_Float();
    private final ENG_Float timeFactorTemp = new ENG_Float();

    public ENG_FrameTimeControllerValue() {
        ENG_RenderRoot.getRenderRoot().addFrameListener(this);
        mTimeFactor.value.setValue(1.0f);
        
    }

    @Override
    public boolean frameEnded(ENG_FrameEvent evt) {

        return true;
    }

    @Override
    public boolean frameRenderingQueued(ENG_FrameEvent evt) {

        return false;
    }

    @Override
    public boolean frameStarted(ENG_FrameEvent evt) {

        if (mFrameDelay.value.getValue() != 0.0f) {
            mFrameTime.value.setValue(mFrameDelay.value);
            divFloat.setValue(evt.timeSinceLastFrame);
            frameDelayTemp.setValue(mFrameDelay.value);

            mTimeFactor.value.setValue(
                    frameDelayTemp.getValue() / evt.timeSinceLastFrame);
        } else {
            timeFactorTemp.setValue(mTimeFactor.value);
            mFrameTime.value.setValue(
                    timeFactorTemp.getValue() * evt.timeSinceLastFrame);
        }
        mElapsedTime.add(mFrameTime.value);
        return true;
    }

    public ENG_Float getTimeFactor() {
        return mTimeFactor.value;
    }

    public void setTimeFactor(ENG_Float tf) {
        if (tf.getValue() > 0.0f) {
            mTimeFactor.value.setValue(tf);
            mFrameDelay.value.setValue(0.0f);
        }
    }

    public ENG_Float getFrameDelay() {
        return mFrameDelay.value;
    }

    public void setFrameDelay(ENG_Float fd) {
        mTimeFactor.value.setValue(0.0f);
        mFrameDelay.value.setValue(fd);
    }

    public ENG_Float getElapsedTime() {
        return mElapsedTime.value;
    }

    public void setElapsedTime(ENG_Float et) {
        mElapsedTime.value.setValue(et);
    }

    @Override
    public ENG_ControllerTypeFloat getValue() {

        return mFrameTime;
    }

    @Override
    public void setValue(ENG_ControllerTypeFloat value) {


    }

/*	@Override
    public int compareTo(ENG_IComparableFrameListener another) {

	/*	if (another instanceof ENG_FrameTimeControllerValue) {
			((ENG_FrameTimeControllerValue)another).g
		}*/
	/*	return hashCode() - another.hashCode();
	}*/
	
/*	public boolean equals(Object obj) {
		if (hashCode() == obj.hashCode()) {
			return true;
		}
		return false;
	}*/

}
