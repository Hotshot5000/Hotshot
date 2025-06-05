/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/1/17, 4:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers.glsles;

import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativeClass;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 21.02.2017.
 */

public class GLRenderSystemNativeWrapper extends ENG_NativeClass implements ENG_IDisposable {

    public GLRenderSystemNativeWrapper() {

    }

    @Override
    public void destroy() {

    }

    public void setConfigOption(final String name, final String value) {
        ENG_SlowCallExecutor.execute(() -> {
            setConfigOptionNative(getPtr(), name, value);
            return 0;
        });

    }

    private static native void setConfigOptionNative(long ptr, String name, String value);
}
