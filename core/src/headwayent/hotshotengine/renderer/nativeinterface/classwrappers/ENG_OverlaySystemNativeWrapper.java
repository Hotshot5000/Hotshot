/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/1/17, 10:41 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 11.02.2017.
 */

public class ENG_OverlaySystemNativeWrapper extends ENG_NativeClass implements ENG_IDisposable {

    public ENG_OverlaySystemNativeWrapper() {
        ENG_SlowCallExecutor.execute(() -> {
            setPtr(createOverlaySystem());
            return 0;
        });
    }

    @Override
    public void destroy() {
        ENG_SlowCallExecutor.execute(() -> {
            destroyOverlaySystem(getPtr());
            return 0;
        });

    }

    private static native long createOverlaySystem();
    private static native void destroyOverlaySystem(long ptr);
}
