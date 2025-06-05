/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.statedebugger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sebas on 17.09.2015.
 */
public class ENG_State {

    private static final String HEADER_PREDEFINED_PARAMS = "[PreParams]";
    private static final String INPUT = "[Input]";
    private static final String STATE = "[State]";

    public static final int PREDEFINED_PARAMS = 0;
    public static final int FRAME = 1;
    public static final int UNDEFINED_TYPE = -1;


    public static final int FRAME_NUM_BUFFER_LIMIT = 10;
    public static final int FRAME_NUM_LOAD_AHEAD = 2 * FRAME_NUM_BUFFER_LIMIT;

    private static final boolean USE_QUEUE = true;

    private final ArrayList<ENG_Frame> frameList = new ArrayList<>();
    private final LinkedList<ENG_Frame> frameQueue = new LinkedList<>();
    private static final HashMap<Integer, Class> classMap = new HashMap<>();
    // In order to not repeat strings throughout every frames or frameIntervals we use integers that refer to the strings.
    // The outer hashmap represents the key from the frame.
    private final HashMap<String, HashMap<Integer, String>> predefinedParameters = new HashMap<>();
    private final ArrayList<PredefinedParameter> predefinedParametersList = new ArrayList<>();
    private DataOutputStream outputWriter;
    private DataInputStream inputReader;
    private final Gson gson;
    private ENG_Frame currentFrame;
    private boolean currentFrameWritten;
    private final ReentrantLock predefinedParamLock = new ReentrantLock();
    private final ReentrantLock frameLock = new ReentrantLock();
    private final AtomicInteger loadedFrameNum = new AtomicInteger();
    private final Runnable nextFrameLoader = new NextFramesLoader();
    private final AtomicBoolean inputEndReached = new AtomicBoolean();
    private boolean nextFramesLoaded = true;
    private CountDownLatch getFrameBlock;
    private final ReentrantLock getFrameLock = new ReentrantLock();
    private int currentFramePos;
    private boolean lastFrameReached;
    private final ReentrantLock nextFramesLoaderThreadLock = new ReentrantLock();
    private final ArrayList<OnFrameChangedListener> frameChangesList = new ArrayList<>();
    private final transient ArrayList<OnFrameIntervalChangeListener> frameIntervalChangeListeners = new ArrayList<>();

    public interface OnFrameIntervalChangeListener {
        void onFrameIntervalChanged(ENG_FrameInterval frameInterval);
    }

    public interface OnFrameChangedListener {
        void onFrameChanged(ENG_Frame currentFrame);
    }

    private class NextFramesLoader implements Runnable {

        @Override
        public void run() {
            nextFramesLoaderThreadLock.lock();

            try {
//                System.out.println("FrameLoader thread started");
                int i = 0;
                while (true) {
                    if (i == FRAME_NUM_LOAD_AHEAD) {
                        break;
                    }
                    ChunkRet chunkRet = null;
                    try {
                        chunkRet = readChunk();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (chunkRet == null) {
                        inputEndReached.set(true);
                        break;
                    }
                    switch (chunkRet.type) {
                        case PREDEFINED_PARAMS: {
                            PredefinedParameter predefinedParameter = (PredefinedParameter) chunkRet.obj;
                            addPredefinedParameter(predefinedParameter.key, predefinedParameter.position, predefinedParameter.value);
                            break;
                        }
                        case FRAME:
                            addFrame((ENG_Frame) chunkRet.obj);
                            ++i;
                            break;
                        case UNDEFINED_TYPE:
                            throw new IllegalStateException();
                    }
                }
                getFrameLock.lock();
                try {
                    if (getFrameBlock != null) {
                        getFrameBlock.countDown();
                        getFrameBlock = null;
                    }
                } finally {
                    getFrameLock.unlock();
                }
                nextFramesLoaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                nextFramesLoaderThreadLock.unlock();
            }
        }
    }

    static {
        classMap.put(PREDEFINED_PARAMS, PredefinedParameter.class);

    }

    private static class PredefinedParameter {
        public final String key;
        public final Integer position;
        public final String value;

        public PredefinedParameter(String key, Integer position, String value) {
            this.key = key;
            this.position = position;
            this.value = value;
        }
    }

    public static class TypeAndDeserializer {
        public Class type;
        public JsonDeserializer deserializer;

        public TypeAndDeserializer() {

        }

        public TypeAndDeserializer(Class type, JsonDeserializer deserializer) {
            this.type = type;
            this.deserializer = deserializer;
        }
    }

    public ENG_State(TypeAndDeserializer... typeAndDeserializer) {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        for (TypeAndDeserializer deserializer : typeAndDeserializer) {
            registerDeserializer(deserializer.type, deserializer.deserializer, gsonBuilder);
        }
        gson = gsonBuilder.create();
    }

    private void registerDeserializer(Class c, JsonDeserializer deserializer, GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(c, deserializer);
    }

    public static void addClass(int pos, Class c) {
        classMap.put(pos, c);
    }

    public ENG_Frame getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(ENG_Frame currentFrame) {
        this.currentFrame = currentFrame;
        notifyOnFrameChangedListeners(currentFrame);
    }

    private void notifyOnFrameChangedListeners(ENG_Frame currentFrame) {
        ArrayList<OnFrameChangedListener> onFrameChangedListeners = new ArrayList<>(frameChangesList);
        for (OnFrameChangedListener listener : onFrameChangedListeners) {
            listener.onFrameChanged(currentFrame);
        }
    }

    public void addFrame(ENG_Frame frame) {
        frameLock.lock();
        try {
            if (USE_QUEUE) {
                frameQueue.offer(frame);
            } else {
                frameList.add(frame);
            }
        } finally {
            frameLock.unlock();
        }
    }

    public ENG_Frame getNextFrame() {
        ENG_Frame currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            currentFrame.checkAllObjectsFromIntervalUsed();
        }
        return getFrame(currentFramePos++);
    }

    public ENG_Frame getFrame(int pos) {
        if ((!USE_QUEUE && !inputEndReached.get() && frameList.size() < pos + FRAME_NUM_BUFFER_LIMIT)
                || (USE_QUEUE && !inputEndReached.get() && frameQueue.size() < FRAME_NUM_BUFFER_LIMIT)) {
            loadNextFrames();
            if ((!USE_QUEUE && frameList.size() == pos) || (USE_QUEUE && frameQueue.isEmpty())) {
                // We have reached the end and we must block while we wait for loading the next frames from file.
//                    getFrameLock.lock();
//                frameLock.unlock();
                try {
                    getFrameBlock.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
//                        getFrameLock.unlock();
//                    frameLock.lock();
                }
            }

        }
        frameLock.lock();
        try {
            if ((!USE_QUEUE && inputEndReached.get() && pos == frameList.size() - 1)
                    || (USE_QUEUE && inputEndReached.get() && frameQueue.isEmpty())) {
                lastFrameReached = true;
            }
            if (USE_QUEUE) {
                return frameQueue.poll();
            } else {
                return frameList.get(pos);
            }
        } finally {
            frameLock.unlock();
        }
    }

    private void loadNextFrames() {
        nextFramesLoaderThreadLock.lock();
        try {
            if (nextFramesLoaded) {
                nextFramesLoaded = false;
                getFrameBlock = new CountDownLatch(1);
                Thread nextFrameLoaderThread = new Thread(nextFrameLoader);
                nextFrameLoaderThread.start();
            }
        } finally {
            nextFramesLoaderThreadLock.unlock();
        }
    }

    public int getFrameListSize() {
        frameLock.lock();
        try {
            if (USE_QUEUE) {
                return frameQueue.size();
            } else {
                return frameList.size();
            }
        } finally {
            frameLock.unlock();
        }
    }

    public void addPredefinedParameter(String key, Integer position, String value) {
        predefinedParamLock.lock();
        try {
            HashMap<Integer, String> map = predefinedParameters.get(key);
            if (map == null) {
                map = new HashMap<>();
                predefinedParameters.put(key, map);
            }
            String put = map.put(position, value);
            if (put != null) {
                throw new IllegalArgumentException("position " + position + " already occupied");
            }
            createPredefinedParameter(key, position, value);
        } finally {
            predefinedParamLock.unlock();
        }
    }

    public void addPredefinedParameter(String key, String value) {
        predefinedParamLock.lock();
        try {
            HashMap<Integer, String> map = predefinedParameters.get(key);
            if (map == null) {
                map = new HashMap<>();
                predefinedParameters.put(key, map);
            }
            boolean found = false;
            for (String v : map.values()) {
                if ((v != null && v.equals(value)) || (v == null && Objects.equals(v, value))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                map.put(map.size(), value);
                createPredefinedParameter(key, map.size() - 1, value);
            }
        } finally {
            predefinedParamLock.unlock();
        }
    }

    public String getPredefinedParameter(String key, Integer position) {
        predefinedParamLock.lock();
        try {
            HashMap<Integer, String> map = predefinedParameters.get(key);
            if (map == null) {
                throw new IllegalArgumentException("Invalid " + key);
            }
            String value = map.get(position);
            if (value == null) {
                throw new IllegalArgumentException("Invalid " + position);
            }
            return value;
        } finally {
            predefinedParamLock.unlock();
        }
    }

    public void setOutputWriter(DataOutputStream outputWriter) {
        this.outputWriter = outputWriter;
    }

    public void setInputReader(DataInputStream inputReader) {
        this.inputReader = inputReader;
    }

    private void createPredefinedParameter(String key, Integer position, String value) {
        predefinedParametersList.add(new PredefinedParameter(key, position, value));

    }

    public void writeOutput() {
        if (USE_QUEUE) {
            ENG_Frame frame;
            while ((frame = frameQueue.poll()) != null) {
                writeFrame(frame);
            }
        } else {
            for (ENG_Frame frame : frameList) {
                writeFrame(frame);
            }
        }
    }

    public void writeCurrentFrame() {
        if (getCurrentFrame() != null) {
            writeFrame(getCurrentFrame());
        }

    }

    public void writeFrame(ENG_Frame frame) {
        if (!predefinedParametersList.isEmpty()) {
//            outputWriter.print(ENG_StringUtility.wrapWithNewLines(HEADER_PREDEFINED_PARAMS));
            for (PredefinedParameter param : predefinedParametersList) {
//                String predParamStr = gson.toJson(param, PredefinedParameter.class);
//                outputWriter.print(predParamStr);
                writeChunk(PREDEFINED_PARAMS, param);
            }
            predefinedParametersList.clear();
        }
        frame.write(outputWriter, gson);
    }

    public void readInputFrames() {
        if (inputEndReached.get()) {
            return;
        }
        while (true) {
            ChunkRet chunkRet = readChunk();
            if (chunkRet == null) {
                inputEndReached.set(true);
                break;
            }
            switch (chunkRet.type) {
                case PREDEFINED_PARAMS: {
                    PredefinedParameter predefinedParameter = (PredefinedParameter) chunkRet.obj;
                    addPredefinedParameter(predefinedParameter.key, predefinedParameter.position, predefinedParameter.value);
                    break;
                }
                case FRAME:
                    addFrame((ENG_Frame) chunkRet.obj);
                    break;
                case UNDEFINED_TYPE:
                    throw new IllegalStateException();
            }
        }
    }

    public void readFrame() {

    }

    public void writeChunk(int type, Object obj) {
        try {
            outputWriter.writeInt(type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = gson.toJson(obj, classMap.get(type));
        writeString(s);
    }

    private void writeString(String s, boolean addLength) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        try {
            if (addLength) {
                outputWriter.writeInt(bytes.length);
            }
            outputWriter.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeString(String s) {
        writeString(s, true);
    }

    public static class ChunkRet {
        public Object obj;
        public int type;
    }

    public ChunkRet readChunk() {
        int type = UNDEFINED_TYPE;
        try {
            type = inputReader.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = readString();
        if (s == null) {
            return null;
        }
        ChunkRet chunkRet = new ChunkRet();
        chunkRet.obj = gson.fromJson(s, classMap.get(type));
        chunkRet.type = type;
        return chunkRet;
    }

    private String readString() {
        try {
            int i = inputReader.readInt();
            byte[] bytes = new byte[i];
            int read = inputReader.read(bytes);
            if (read != i) {
                return null;
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public boolean getInputEndReached() {
//        return inputEndReached.get();
//    }


    public boolean isLastFrameReached() {
        return lastFrameReached;
    }

    public void registerOnFrameChangedListener(OnFrameChangedListener listener) {
        frameChangesList.add(listener);
    }

    public void unregisterOnFrameChangedListener(OnFrameChangedListener listener) {
        frameChangesList.remove(listener);
    }

    public void registerOnFrameIntervalChangedListener(OnFrameIntervalChangeListener listener) {
        frameIntervalChangeListeners.add(listener);
    }

    public void unregisterOnFrameIntervalChangedListener(OnFrameIntervalChangeListener listener) {
        frameIntervalChangeListeners.remove(listener);
    }

    public void notifyOnFrameIntervalChangedListeners(ENG_FrameInterval currentFrameInterval) {
        ArrayList<OnFrameIntervalChangeListener> onFrameIntervalChangeListeners = new ArrayList<>(frameIntervalChangeListeners);
        for (OnFrameIntervalChangeListener listener : onFrameIntervalChangeListeners) {
            listener.onFrameIntervalChanged(currentFrameInterval);
        }
    }
}
