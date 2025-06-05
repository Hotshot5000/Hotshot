/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 9:30 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 20.02.2017.
 */

public class ENG_RootNativeWrapper extends ENG_NativeClass implements ENG_IDisposable {

    public ENG_RootNativeWrapper(final String pluginFileName,
                                 final String configFileName,
                                 final String logFileName) {
        ENG_SlowCallExecutor.execute(() -> {
            setPtr(createRoot(pluginFileName, configFileName, logFileName));
            return 0;
        });
    }

    @Override
    public void destroy() {
        ENG_SlowCallExecutor.execute(() -> {
            destroyRoot(getPtr());
            return 0;
        }, MainApp.getApplicationMode() == MainApp.Mode.SERVER);
    }

    public void initialise(final boolean autoCreateWindow, final String windowTitle,
                           final String customCapabilitiesConfig) {
        ENG_SlowCallExecutor.execute(() -> {
            initialiseNative(getPtr(), autoCreateWindow, windowTitle, customCapabilitiesConfig);
            return 0;
        });

    }

    public long getRenderSystem() {
        final long[] l = new long[1];
        ENG_SlowCallExecutor.execute(() -> {
            l[0] = getRenderSystemNative(getPtr());
            return l[0];
        });
        return l[0];
    }

    private static native long createRoot(String pluginFileName,
                                          String configFileName,
                                          String logFileName);
    private static native void destroyRoot(long ptr);

    private static native void initialiseNative(long ptr, boolean autoCreateWindow, String windowTitle,
                                          String customCapabilitiesConfig);

    private static native long getRenderSystemNative(long ptr);
}
