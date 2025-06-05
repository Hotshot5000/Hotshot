/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import java.nio.ByteBuffer;

import headwayent.hotshotengine.ENG_Utility;

/**
 * Created by sebas on 11.02.2017.
 */

public abstract class ENG_RenderedFrameListenerWithBufferCopy extends ENG_RenderedFrameListener {

    // Make sure the buffer size is exactly the needed bytes size for reading the response.
    private final ByteBuffer buffer;
    // Use this to know from which frame the response is coming.
    private int currentFrame;
    private final boolean alignMemory;

    public ENG_RenderedFrameListenerWithBufferCopy(int bufferSize, boolean alignMemory) {
        this(ENG_Utility.allocate(bufferSize), alignMemory);
    }

    public ENG_RenderedFrameListenerWithBufferCopy(ByteBuffer buffer, boolean alignMemory) {
        this.buffer = buffer;
        this.alignMemory = alignMemory;
    }

    public ENG_RenderedFrameListenerWithBufferCopy(int bufferSize, boolean alignMemory, boolean waitForRender) {
        this(ENG_Utility.allocate(bufferSize), alignMemory, waitForRender);
    }

    public ENG_RenderedFrameListenerWithBufferCopy(ByteBuffer buffer, boolean alignMemory, boolean waitForRender) {
        this.buffer = buffer;
        this.alignMemory = alignMemory;
        this.waitForRender = waitForRender;
    }

    public ENG_RenderedFrameListenerWithBufferCopy(int bufferSize, boolean alignMemory, boolean waitForRender, String listenerName) {
        this(ENG_Utility.allocate(bufferSize), alignMemory, waitForRender, listenerName);
    }

    public ENG_RenderedFrameListenerWithBufferCopy(ByteBuffer buffer, boolean alignMemory, boolean waitForRender, String listenerName) {
        this.buffer = buffer;
        this.alignMemory = alignMemory;
        this.waitForRender = waitForRender;
        setListenerName(listenerName);
    }

    @Override
    public ReturnValue checkFrameId(ByteBuffer responseBuffer, byte frameId) {
        if ((frameId & 0x7f) == this.frameId) {
            if (!waitForRender || (frameId & 0x80) != 0) {
                if (alignMemory) {
                    ENG_Utility.alignMemory(responseBuffer, 4);
                }
                int oldLimit = responseBuffer.limit();
                responseBuffer.limit(responseBuffer.position() + buffer.limit());
//            System.out.println("checkFrameId responseBuffer pos: " + responseBuffer.position() + " responseBuffer limit: " + responseBuffer.limit() +
//            " buffer pos: " + buffer.position() + " buffer limit: " + buffer.limit());
                buffer.put(responseBuffer);
                responseBuffer.limit(oldLimit);
                buffer.flip();
                frameEnded(buffer);
                return ReturnValue.TRUE;
            } else {
                return ReturnValue.WAITING_FOR_RENDERING;
            }
        }
        return ReturnValue.FALSE;

    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void reset() {
        buffer.rewind();
    }
}
