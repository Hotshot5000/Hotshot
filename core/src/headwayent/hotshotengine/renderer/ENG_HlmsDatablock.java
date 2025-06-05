/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;

/**
 * Created by sebas on 17-Aug-17.
 */

public class ENG_HlmsDatablock implements ENG_NativePointerWithSetter {

    private int name; // IdString in native.
    private final long[] ptr = new long[1];
    private boolean nativePtrSet;

    @Override
    public long getPointer() {
        return ptr[0];
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr[0] = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePtrSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePtrSet = set;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }
}
