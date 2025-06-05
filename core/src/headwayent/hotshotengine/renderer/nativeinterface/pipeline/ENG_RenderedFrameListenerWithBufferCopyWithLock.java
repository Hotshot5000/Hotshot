/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/30/18, 12:55 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import java.nio.ByteBuffer;

import headwayent.blackholedarksun.MainApp;

/**
 * Created by sebas on 09.03.2017.
 */

public abstract class ENG_RenderedFrameListenerWithBufferCopyWithLock extends ENG_RenderedFrameListenerWithBufferCopy {

    public ENG_RenderedFrameListenerWithBufferCopyWithLock(ByteBuffer buffer, boolean alignMemory) {
        super(buffer, alignMemory);
    }

    public ENG_RenderedFrameListenerWithBufferCopyWithLock(int bufferSize, boolean alignMemory) {
        super(bufferSize, alignMemory);
    }

    public ENG_RenderedFrameListenerWithBufferCopyWithLock(ByteBuffer buffer, boolean alignMemory, boolean waitForRender) {
        super(buffer, alignMemory, waitForRender);
    }

    public ENG_RenderedFrameListenerWithBufferCopyWithLock(int bufferSize, boolean alignMemory, boolean waitForRender) {
        super(bufferSize, alignMemory, waitForRender);
    }

    public ENG_RenderedFrameListenerWithBufferCopyWithLock(int bufferSize, boolean alignMemory, boolean waitForRender, String listenerName) {
        super(bufferSize, alignMemory, waitForRender, listenerName);
    }

    @Override
    public void frameEnded(final ByteBuffer responseBuffer) {
        MainApp.getMainThread().runOnMainThread(() -> runOnMainThread(responseBuffer));
    }

    public abstract void runOnMainThread(ByteBuffer responseBuffer);
}
