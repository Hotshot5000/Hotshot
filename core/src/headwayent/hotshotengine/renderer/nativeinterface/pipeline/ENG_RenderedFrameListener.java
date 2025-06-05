/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/30/18, 12:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import java.nio.ByteBuffer;

/**
 * Created by sebas on 11.02.2017.
 * Make sure that frameEnded advances the responseBuffer to the position after the
 * expected data size.
 */

public abstract class ENG_RenderedFrameListener {

    public enum ReturnValue {
        TRUE,
        FALSE,
        WAITING_FOR_RENDERING
    }

    // For debugging. Remove this for release.
    protected String listenerName;
    // The frame id that we listen for when the renderer gets back from native code.
    protected int frameId;
    // The current position in the listener buffer so we can know which part of the
    // response buffer belongs to us.
    protected int inFramePos;
    protected boolean waitForRender;

    public ReturnValue checkFrameId(ByteBuffer responseBuffer, byte frameId) {
        if ((frameId & 0x7f) == this.frameId) {
            if (!waitForRender || (frameId & 0x80) != 0) {
                frameEnded(responseBuffer);
                return ReturnValue.TRUE;
            } else {
                return ReturnValue.WAITING_FOR_RENDERING;
            }
        }
        return ReturnValue.FALSE;
    }

    public abstract void frameEnded(ByteBuffer responseBuffer);

    public int getFrameId() {
        return frameId;
    }

    /**
     * Set by ENG_RenderingThread.flushPipeline(). Do not touch this!
     * @param frameId
     */
    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public int getInFramePos() {
        return inFramePos;
    }

    /**
     * Set by ENG_RenderingThread.flushPipeline(). Do not touch this!
     * @param inFramePos
     */
    public void setInFramePos(int inFramePos) {
        this.inFramePos = inFramePos;
    }

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }
}
