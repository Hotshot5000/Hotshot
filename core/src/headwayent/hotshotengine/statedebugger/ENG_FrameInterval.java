/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/19/20, 12:15 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.statedebugger;

import com.google.gson.Gson;
import headwayent.hotshotengine.input.ENG_AccelerometerInput;
import headwayent.hotshotengine.input.ENG_MouseAndKeyboardInput;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sebas on 22.09.2015.
 */
public class ENG_FrameInterval {

    public static final String CHECKBOX_WAIT_BETWEEN_CLICKS_DELAY = "checkbox_wait_between_clicks_delay ";
    public static final String TEXTFIELD_KEY_CODE_DELAY = "textfield_key_code_delay ";
    public static final String TEXTFIELD_SPECIAL_KEY_CODE_DELAY = "textfield_special_key_code_delay ";
    public static final String PASSWORD_CHAR_TIME = "password_char_time ";

    public abstract static class FrameIntervalFactory {
        public abstract ENG_FrameInterval createFrameInterval(long intervalNum);
    }

    private final long intervalNum;
    private transient long currentTime;
    private long timeDelta;
    private final ENG_AccelerometerInput.Rotation rotation = new ENG_AccelerometerInput.Rotation();
    private final ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents touchMouseAndKeyboardEvents = new ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents();
    private final ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents mouseAndKeyboardEvents = new ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents();
    private transient ReentrantLock lock = new ReentrantLock();
    private final HashMap<String, Object> objMap = new HashMap<>();
    private final TreeSet<String> usedObjects = new TreeSet<>();

    public ENG_FrameInterval(long intervalNum) {
        this.intervalNum = intervalNum;
    }

    public long getIntervalNum() {
        return intervalNum;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getTimeDelta() {
        return timeDelta;
    }

    public void setTimeDelta(long timeDelta) {
        this.timeDelta = timeDelta;
    }

    public ENG_AccelerometerInput.Rotation getRotation() {
        return rotation;
    }

    public void setRotation(ENG_AccelerometerInput.Rotation rotation) {
        this.rotation.set(rotation);
    }

    public ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents getTouchMouseAndKeyboardEvents() {
        return touchMouseAndKeyboardEvents;
    }

    public void setTouchMouseAndKeyboardEvents(ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents touchMouseAndKeyboardEvents) {
        this.touchMouseAndKeyboardEvents.set(touchMouseAndKeyboardEvents);
    }

    public ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents getMouseAndKeyboardEvents() {
        return mouseAndKeyboardEvents;
    }

    public void setMouseAndKeyboardEvents(ENG_MouseAndKeyboardInput.MouseAndKeyboardEvents mouseAndKeyboardEvents) {
        this.mouseAndKeyboardEvents.set(mouseAndKeyboardEvents);
    }

    public void addObject(String s, Object o) {
        lock.lock();
        try {
            Object put = objMap.put(s, o);
            if (put != null) {
                throw new IllegalArgumentException(s + " already exists");
            }
            System.out.println("addObject " + s + " " + o);
        } finally {
            lock.unlock();
        }
    }

    public Object getObject(String s) {
        lock.lock();
        try {
            Object o = objMap.get(s);
//        System.out.println("getObject " + s + " " + o);
            usedObjects.add(s);
            return o;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Only used in checkAllObjectsFromIntervalUser() so we can use the lock in one place.
     * @return
     */
    public TreeSet<String> getUsedObjects() {
        return usedObjects;
    }

    /**
     * Only used in checkAllObjectsFromIntervalUser() so we can use the lock in one place.
     * @return
     */
    public HashMap<String, Object> getObjMap() {
        return objMap;
    }

    /**
     * Always needed for usedObjects and objMap accesses.
     * @return
     */
    public ReentrantLock getLock() {
        return lock;
    }

    /**
     * For deserialization garbage as the lock does not get created for some reason...
     */
    public void createLock() {
        lock = new ReentrantLock();
    }

    /**
     * In case you want to write every frame interval manually. Normally we just write the whole frame at the end of the rendering.
     *
     * @param outputWriter
     * @param gson
     */
    public void write(PrintWriter outputWriter, Gson gson) {
//        String s = gson.toJson(this, ENG_FrameInterval.class);
//        outputWriter.print(s);
//        ENG_FrameInterval frameInterval = gson.fromJson(s, ENG_FrameInterval.class);
    }
}
