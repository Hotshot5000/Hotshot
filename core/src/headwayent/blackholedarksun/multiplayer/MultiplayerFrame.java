/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer;

import headwayent.blackholedarksun.net.registeredclasses.NetBase;
import headwayent.hotshotengine.ENG_Utility;

/**
 * Created by sebas on 10.11.2015.
 */
public abstract class MultiplayerFrame extends NetBase {

    public static final int NO_ERROR = 0;

    private long timestamp;
    private long frameNum;
    private int errorCode;

    public MultiplayerFrame(Type type) {
        super(type);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestampToCurrentTime() {
        this.timestamp = ENG_Utility.currentTimeMillis();
    }

    public long getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(long frameNum) {
        this.frameNum = frameNum;
    }

    public long getNextFrameNum() {
        return frameNum + 1;
    }

    public void incrementFrameNum(int num) {
        frameNum += num;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void prepareNextFrame() {
        setTimestampToCurrentTime();
//        ++frameNum;
    }

    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return "frameNum: " + frameNum + " timestamp: " + timestamp + " errorCode: " + errorCode;
    }
}
