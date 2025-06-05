/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/18/17, 7:15 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.blackholedarksun.MainActivity;

/**
 * Created by sebas on 11.02.2017.
 */

public class ENG_NativeClass {

    private long ptr;

    public long getPtr() {
        return ptr;
    }

    public void setPtr(long ptr) {
        if (MainActivity.isDebugmode()) {
            if (this.ptr != 0) {
                throw new IllegalArgumentException("ptr has already been initialized");
            }
        }
        this.ptr = ptr;
    }
}
