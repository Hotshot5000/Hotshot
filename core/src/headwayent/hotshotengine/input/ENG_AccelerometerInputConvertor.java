/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package headwayent.hotshotengine.input;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.input.ENG_AccelerometerInput.Rotation;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

/**
 * @author sebi
 */
public class ENG_AccelerometerInputConvertor extends ENG_InputConvertor {

    private ENG_AccelerometerInput accInput;
    private String name = "";
    private final Rotation initialRotation = new ENG_AccelerometerInput.Rotation();
    private final Rotation currentRotation = new ENG_AccelerometerInput.Rotation();
    private final Rotation retRotation = new ENG_AccelerometerInput.Rotation();
    private boolean firstSet;
    private boolean absolute;
    private float error;

    public String getName() {
        return name;
    }

    public ENG_AccelerometerInputConvertor(String instanceName) {
        name = instanceName;
    }

    public ENG_AccelerometerInputConvertor(String instanceName, ENG_AccelerometerInput input) {
        name = instanceName;
        setAccInput(input);
    }

    @Override
    public Object read() {
        if (accInput == null) {
            throw new NullPointerException("The convertor must have an accelerometer input");
        }
        Rotation rot = (Rotation) accInput.getData();

        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            currentFrameInterval.setRotation(rot);
        }
        if (MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            if (currentFrameInterval.getRotation() != null) {
                rot.set(currentFrameInterval.getRotation());
            }
        }

        if (!absolute) {
            if (!firstSet) {
                initialRotation.set(rot);
                firstSet = true;
            }
            if (Math.abs(rot.m_lastPitch - initialRotation.m_lastPitch) > error) {
                retRotation.m_lastPitch = rot.m_lastPitch - initialRotation.m_lastPitch;
            } else {
                retRotation.m_lastPitch = 0.0f;
            }
            if (Math.abs(rot.m_lastYaw - initialRotation.m_lastYaw) > error) {
                retRotation.m_lastYaw = rot.m_lastYaw - initialRotation.m_lastYaw;
            } else {
                retRotation.m_lastYaw = 0.0f;
            }
            if (Math.abs(rot.m_lastRoll - initialRotation.m_lastRoll) > error) {
                retRotation.m_lastRoll = rot.m_lastRoll - initialRotation.m_lastRoll;
            } else {
                retRotation.m_lastRoll = 0.0f;
            }
            //        retRotation.set(currentRotation);
        } else {
            retRotation.set(rot);
        }

        return retRotation;
    }

    /**
     * @return the accInput
     */
    public ENG_AccelerometerInput getAccInput() {
        return accInput;
    }

    /**
     * @param accInput the accInput to set
     */
    public void setAccInput(ENG_AccelerometerInput accInput) {
        this.accInput = accInput;
    }

    /**
     * @return the absolute
     */
    public boolean isAbsolute() {
        return absolute;
    }

    /**
     * @param absolute the absolute to set
     */
    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    /**
     * @param firstSet the firstSet to set
     */
    public void resetFirstSet() {
        this.firstSet = false;
    }

    /**
     * @return the error
     */
    public float getError() {
        return error;
    }

    /**
     * @param error the error to set. Accepted errors in degrees.
     */
    public void setError(float error) {
        this.error = error;
    }

}
