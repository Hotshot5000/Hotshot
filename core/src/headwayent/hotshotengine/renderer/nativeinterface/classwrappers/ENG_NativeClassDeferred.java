/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

/**
 * Created by sebas on 21.03.2017.
 * <p>
 * Sometimes you might want to defer the update of the pointer until the call from
 * the rendering thread returns. Unfortunately that means you need a long array because you
 * save the pointer now but update it later when you come back from the rendering thread.
 * We also provide a way to have an array of object pointers in case you need one with the
 * custom constructor.
 */

public class ENG_NativeClassDeferred {

    private final long[] ptr;

    public ENG_NativeClassDeferred() {
        ptr = new long[1];
    }

    public ENG_NativeClassDeferred(int length) {
        ptr = new long[length];
    }

    public long getPtr() {
        return ptr[0];
    }

    public long getPtr(int pos) {
        return ptr[pos];
    }

    public void setPtr(long val) {
        ptr[0] = val;
    }

    public void setPtr(long val, int pos) {
        ptr[pos] = val;
    }
}
