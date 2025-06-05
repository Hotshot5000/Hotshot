/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/17/18, 1:31 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.util;

import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Created by sebas on 16-Feb-18.
 * NOT THREAD SAFE. BUILT FOR SPEED ONLY!!!
 */

public class ENG_FloatArrayList {

    private static final int DEFAULT_SIZE = 128;
    private float[] arr;
    private int currentPos;
    private int currentSize;

    public ENG_FloatArrayList() {
        arr = new float[DEFAULT_SIZE];
        currentSize = DEFAULT_SIZE;
    }

    public ENG_FloatArrayList(int defaultSize) {
        if (defaultSize < 2) {
            throw new IllegalArgumentException("defaultSize should be bigger than 1 :)");
        }
        arr = new float[defaultSize];
        currentSize = defaultSize;
    }

    public void add(float f) {
        if (currentPos == currentSize) {
            increaseArraySize();
        }
        arr[currentPos++] = f;
    }

    private void increaseArraySize() {
        int newSize = currentSize * 2;
        float[] newArr = new float[newSize];
        System.arraycopy(arr, 0, newArr, 0, currentSize);
        arr = newArr;
        currentSize = newSize;
    }

    public void clearWithReinit() {
        arr = new float[currentSize];
        currentPos = 0;
    }

    public void clearWithOverwrite() {
        Arrays.fill(arr, 0);
    }

    /**
     * This just clears the currentPos to 0. All elements remain intact so the user must know
     * from where to where the actual valid info is!!!
     */
    public void clearFast() {
        currentPos = 0;
    }

    public void writeToFloatBuffer(FloatBuffer buf) {
        buf.put(arr, 0, currentPos);
    }
}
