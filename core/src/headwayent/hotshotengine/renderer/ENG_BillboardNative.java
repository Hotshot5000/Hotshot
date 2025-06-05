/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

/**
 * Created by sebas on 23-Oct-17.
 */

public class ENG_BillboardNative implements ENG_NativePointerWithSetter, ENG_IdObject {

    private final long[] ptr = new long[1];
    private long id;
    private String name;
    private boolean nativePtrSet;

    public ENG_BillboardNative() {

    }

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

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setRotation(float rotation) {
        ENG_NativeCalls.billboard_setRotation(this, rotation);
    }
}
