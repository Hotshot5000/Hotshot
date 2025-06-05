/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import java.util.ArrayList;

/**
 * Created by sebas on 23.07.2017.
 */

public class ENG_ArrayListRing<T> {

    private final ArrayList<T>[] list;
    private int currentPos;

    public ENG_ArrayListRing(int num) {
        list = new ArrayList[num];
        for (int i = 0; i < num; ++i) {
            list[i] = new ArrayList<>();
        }
    }

    public ArrayList<T> getNextList() {
        ArrayList<T> list = this.list[currentPos];
        if ((++currentPos) >= this.list.length) {
            currentPos = 0;
        }
        list.clear();
        return list;
    }

    public ArrayList<T> getList(int num) {
        return list[num];
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public int getPreviousBuf() {
        int buf = this.currentPos;
        if ((--buf) < 0) {
            buf = list.length - 1;
        }
        return buf;
    }
}
