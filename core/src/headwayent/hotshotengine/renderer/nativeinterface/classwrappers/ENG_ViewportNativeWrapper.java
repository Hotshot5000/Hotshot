/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/9/17, 9:45 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 08.05.2017.
 */

public class ENG_ViewportNativeWrapper extends ENG_NativeClass {

    public ENG_ViewportNativeWrapper(final long renderWindowPtr) {
        ENG_SlowCallExecutor.execute(() -> {
            setPtr(getViewport(renderWindowPtr));
            return 0;
        });
    }

    public static native long getViewport(long renderWindowPtr);
}
