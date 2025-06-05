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
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 20.02.2017.
 */

public class ENG_SceneManagerNativeWrapper extends ENG_NativeClass implements ENG_IDisposable {

    public ENG_SceneManagerNativeWrapper(final long rootPtr, final short typeMask, final int numThreads, final int threadCullingMethod,
                                         final String name) {
        ENG_SlowCallExecutor.execute(() -> {
            setPtr(createSceneManager(rootPtr, typeMask, numThreads, threadCullingMethod, name));
            initSceneManager(getPtr(), "ZoneType_Default");
            return 0;
        });
    }

    @Override
    public void destroy() {
        ENG_SlowCallExecutor.execute(() -> {
            destroySceneManager(ENG_RenderRoot.getRenderRoot().getPointer(), getPtr());
            return 0;
        }, MainApp.getApplicationMode() == MainApp.Mode.SERVER);
    }

    public void addRenderQueueListener(final long renderQueueListener) {
        ENG_SlowCallExecutor.execute(() -> {
            addRenderQueueListenerNative(getPtr(), renderQueueListener);
            return 0;
        });

    }

    public void removeRenderQueueListener(final long renderQueueListener) {
        ENG_SlowCallExecutor.execute(() -> {
            removeRenderQueueListenerNative(getPtr(), renderQueueListener);
            return 0;
        });

    }

    public long createCamera(final String name, final boolean isVisible, final boolean forCubemapping) {
        final long[] camera = new long[1];
        ENG_SlowCallExecutor.execute(() -> {
            camera[0] = createCamera(getPtr(), name, isVisible, forCubemapping);
            return camera[0];
        });
        return camera[0];
    }

    private static native long createSceneManager(long ptr, short typeMask, int numThreads, int threadCullingMethod,
                                                  String name);
    private static native void destroySceneManager(long rootPtr, long ptr);

    private static native void initSceneManager(long ptr, String zoneTypeName);

    private static native void addRenderQueueListenerNative(long sceneManagerPtr, long renderQueueListener);
    private static native void removeRenderQueueListenerNative(long sceneManagerPtr, long renderQueueListener);

    private static native long createCamera(long ptr, String name, boolean isVisible, boolean forCubemapping);
}
