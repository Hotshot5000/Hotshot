/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/24/19, 10:18 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;

public abstract class Animation {

    /**
     * @author sebi
     *         When finishing a repeatable animation move to STARTABLE instead of FINISHED
     *         FINISHED should be used for non repeatable animations.
     */
    public enum AnimationState {
        STARTABLE, STARTED, FINISHED
    }

    private final String name;
    private long startTime, endTime;
    private long totalTime;
    private float timeStep;
    private float currentStep;
    private boolean repeatable;
    private AnimationState animationState = AnimationState.STARTABLE;
    private boolean resourcesDestroyed;

//	public Animation() {
//
//	}

    public Animation(String name, long totalTime) {
        this.name = name;
        setTotalTime(totalTime);
    }

    public String getName() {
        return name;
    }

    public void start() {
        setAnimationState(AnimationState.STARTED);
        if (MainApp.getMainThread().isInputState()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            double st = (double) currentFrameInterval.getObject(getName() + " startTime");
            double et = (double) currentFrameInterval.getObject(getName() + " endTime");
            startTime = (long) st;
            endTime = (long) et;
        } else {
            startTime = ENG_Utility.currentTimeMillis();
            endTime = startTime + totalTime;
        }
        if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
            ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
            currentFrameInterval.addObject(getName() + " startTime", startTime);
            currentFrameInterval.addObject(getName() + " endTime", endTime);
        }

        if (timeStep == 0.0f) {
            throw new ENG_InvalidFieldStateException("timeStep is not initialized");
        }
    }

    public void updateAnimation() {
        if (getAnimationState() == AnimationState.STARTED) {
            long currentTime;
            if (MainApp.getMainThread().isInputState()) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                double d = (double) currentFrameInterval.getObject(getName());
                currentTime = (long) d;
            } else {
                currentTime = ENG_Utility.currentTimeMillis();
            }
            if (MainApp.isOutputDebuggingApplicationStateEnabled()) {
                ENG_FrameInterval currentFrameInterval = MainApp.getMainThread().getDebuggingState().getCurrentFrame().getCurrentFrameInterval();
                currentFrameInterval.addObject(getName(), currentTime);
            }
            if (currentTime > endTime) {
                animationFinished();
                if (!isRepeatable()) {
                    setAnimationState(AnimationState.FINISHED);
                } else {
                    start();
                }
            } else {
                currentStep = ((float) (currentTime - startTime)) * timeStep;
                update();
            }

        }
    }

    public abstract void update();

    public abstract void animationFinished();

    /**
     * To reload the entities, billboards etc after a context loss in order to
     * safely continue the animation
     */
    public abstract void reloadResources();

    public void destroyResources() {
        if (!resourcesDestroyed) {
            destroyResourcesImpl();
            resourcesDestroyed = true;
        }
    }

    public void stop() {
        animationFinished();
        setAnimationState(AnimationState.FINISHED);
    }

    public abstract void destroyResourcesImpl();

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        if (totalTime <= 0) {
            throw new IllegalArgumentException("totalTime must be > 0");
        }
        this.totalTime = totalTime;
        timeStep = 1.0f / (float) totalTime;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public void setAnimationState(AnimationState animationState) {
//        System.out.println("Animation " + name + "has state: " + animationState);
        this.animationState = animationState;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public float getCurrentStep() {
        return currentStep;
    }

    public boolean isResourcesDestroyed() {
        return resourcesDestroyed;
    }

    public void setResourcesDestroyed(boolean resourcesDestroyed) {
        this.resourcesDestroyed = resourcesDestroyed;
    }
}
