/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/24/17, 11:22 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 15-Nov-17.
 */

public class ENG_SceneCompositorWrapper extends ENG_NativeClass implements ENG_IDisposable {

    public ENG_SceneCompositorWrapper() {
        ENG_SlowCallExecutor.execute(() -> {
            setPtr(createSceneCompositor());
            return 0;
        });
    }

    @Override
    public void destroy() {
        destroySceneCompositor(getPtr());
    }

    private static native long createSceneCompositor();
    private static native void destroySceneCompositor(long ptr);
}
