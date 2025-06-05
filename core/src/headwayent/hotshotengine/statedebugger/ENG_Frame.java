/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/19/20, 12:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.statedebugger;

import com.google.gson.Gson;
import headwayent.blackholedarksun.MainApp;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by sebas on 17.09.2015.
 */
public class ENG_Frame {


    public static abstract class FrameFactory {
        public abstract ENG_Frame createFrame(ENG_State state);
    }

    private long frameNum;
    private transient long frameTime;
    private long timeDelta;
    private final ArrayList<ENG_FrameInterval> frameIntervalList = new ArrayList<>();
    private transient ENG_FrameInterval currentFrameInterval;
    private transient boolean frameWritten;
    private transient ENG_State state;

    public ENG_Frame(ENG_State state) {
        setState(state);
    }

    public ENG_State getState() {
        return state;
    }

    public void setState(ENG_State state) {
        this.state = state;
    }

    public void addFrameInterval(ENG_FrameInterval frameInterval) {
        frameIntervalList.add(frameInterval);
    }

    public int getFrameIntervalListSize() {
        return frameIntervalList.size();
    }

    public void _clearFrameIntervalList() {
        frameIntervalList.clear();
    }

    public void _addFrameIntervalList(ArrayList<ENG_FrameInterval> list) {
        frameIntervalList.addAll(list);
    }

    public ENG_FrameInterval getFrameInterval(int i) {
        return frameIntervalList.get(i);
    }

    public ENG_FrameInterval getCurrentFrameInterval() {
        return currentFrameInterval;
    }

    public void setCurrentFrameInterval(ENG_FrameInterval currentFrameInterval) {
        checkAllObjectsFromIntervalUsed();
        this.currentFrameInterval = currentFrameInterval;
        System.out.println("added currentFrameInterval for frame: " + frameNum);
        state.notifyOnFrameIntervalChangedListeners(currentFrameInterval);
    }

    public void checkAllObjectsFromIntervalUsed() {
        if (MainApp.getMainThread().isInputState() && this.currentFrameInterval != null) {
            // Check if all objs have been used
            this.currentFrameInterval.getLock().lock();
            try {
                TreeSet<String> usedObjects = this.currentFrameInterval.getUsedObjects();
                HashMap<String, Object> objMap = this.currentFrameInterval.getObjMap();
                ArrayList<String> strings = new ArrayList<>();
                for (String s : objMap.keySet()) {
                    if (!s.startsWith("AI_STATE") && !s.startsWith("AI_POSITION")) {
                        strings.add(s);
                    }
                }
                boolean b = usedObjects.containsAll(strings);
                if (!b) {
                    for (String s : strings) {
                        if (!usedObjects.contains(s)) {
                            System.out.println("Could not find input state object: " + s);
                        }
                    }
                    throw new IllegalStateException();
                }
//            for (String mapStr : objMap.keySet()) {
//                if (usedObjects.c)
//            }
            } finally {
                this.currentFrameInterval.getLock().unlock();
            }
        }
    }

    public boolean isFrameWritten() {
        return frameWritten;
    }

    private void setFrameWritten() {
        this.frameWritten = true;
    }

    public long getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(long frameNum) {
        this.frameNum = frameNum;
    }

    public long getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }

    public long getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(long timeDelta) {
        this.timeDelta = timeDelta;
    }

    public void write(DataOutputStream outputWriter, Gson gson) {
        if (!isFrameWritten()) {
//            String s = gson.toJson(this, ENG_Frame.class);
//            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
//            outputWriter.
            state.writeChunk(ENG_State.FRAME, this);
            setFrameWritten();
        }
//        for (ENG_FrameInterval frameInterval : frameIntervalList) {
//            frameInterval.write(outputWriter, gson);
//        }
    }
}
