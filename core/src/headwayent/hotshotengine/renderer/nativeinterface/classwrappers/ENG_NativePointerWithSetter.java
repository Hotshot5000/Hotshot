/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/4/17, 8:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

/**
 * Created by sebas on 02.05.2017.
 */

public interface ENG_NativePointerWithSetter extends ENG_NativePointer {

    void setPointer(long ptr);
    boolean isNativePointerSet();
    void setNativePointer(boolean set);
}
