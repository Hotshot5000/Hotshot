/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/5/17, 7:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

/**
 * Created by sebas on 05.04.2017.
 */

public abstract class ENG_SlowCallWithRepeatRendering implements ENG_ISlowCall {

    private boolean repeatRendering;

    public abstract long executeWithOptionalRendering();

    @Override
    public long execute() {
        long l = executeWithOptionalRendering();
        ENG_RenderingThread.setAutomaticRenderOneFrameEnabled(repeatRendering);
        return l;
    }

    public boolean isRepeatRendering() {
        return repeatRendering;
    }

    public void setRepeatRendering(boolean repeatRendering) {
        this.repeatRendering = repeatRendering;
    }
}
