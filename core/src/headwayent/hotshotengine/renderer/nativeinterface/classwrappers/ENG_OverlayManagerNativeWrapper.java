/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 7/31/17, 12:24 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 30.07.2017.
 */

public class ENG_OverlayManagerNativeWrapper extends ENG_NativeClass implements ENG_IDisposable {

    /**
     * Make sure overlay system is created.
     */
    public ENG_OverlayManagerNativeWrapper() {
        ENG_SlowCallExecutor.execute(() -> {
            setPtr(getOverlayManager());
            return 0;
        });
    }

    @Override
    public void destroy() {
        setPtr(0);
    }

    private static native long getOverlayManager();
}
