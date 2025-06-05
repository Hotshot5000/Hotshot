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
 * Created by sebas on 23.07.2017.
 */

public class ENG_ByteBufferRing {

    private final ByteBuffer[] bufList;
    private int currentBuf;

    public ENG_ByteBufferRing(int bufNum, int bufSize) {
        bufList = new ByteBuffer[bufNum];
        for (int i = 0; i < bufNum; ++i) {
            bufList[i] = ENG_Utility.allocateDirect(bufSize);
        }
    }

    public ByteBuffer getNextBuffer() {
        ByteBuffer buffer = bufList[currentBuf];
        if ((++currentBuf) >= bufList.length) {
            currentBuf = 0;
        }
        return buffer;
    }

    public int getCurrentBuf() {
        return currentBuf;
    }

    public int getPreviousBuf() {
        int buf = this.currentBuf;
        if ((--buf) < 0) {
            buf = bufList.length - 1;
        }
        return buf;
    }

    public ByteBuffer[] getBufList() {
        return bufList;
    }
}
