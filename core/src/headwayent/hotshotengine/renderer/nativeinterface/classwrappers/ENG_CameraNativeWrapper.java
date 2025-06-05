/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 9:30 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.classwrappers;

import headwayent.hotshotengine.ENG_IDisposable;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_SlowCallExecutor;

/**
 * Created by sebas on 24.02.2017.
 */

public class ENG_CameraNativeWrapper extends ENG_NativeClass implements ENG_IDisposable {

    @Override
    public void destroy() {

    }

    public void setPosition(final float x, final float y, final float z) {
        ENG_SlowCallExecutor.execute(() -> {
            setPosition(getPtr(), x, y, z);
            return 0;
        });

    }

    public void lookAt(final float x, final float y, final float z) {
        ENG_SlowCallExecutor.execute(() -> {
            lookAt(getPtr(), x, y, z);
            return 0;
        });

    }

    public void setDirection(final float x, final float y, final float z) {
        ENG_SlowCallExecutor.execute(() -> {
            setDirection(getPtr(), x, y, z);
            return 0;
        });

    }

    public void setNearClipDistance(final float dist) {
        ENG_SlowCallExecutor.execute(() -> {
            setNearClipDistance(getPtr(), dist);
            return 0;
        });

    }

    public void setFarClipDistance(final float dist) {
        ENG_SlowCallExecutor.execute(() -> {
            setFarClipDistance(getPtr(), dist);
            return 0;
        });

    }

    public void setAutoAspectRatio(final boolean aspectRatio) {
        ENG_SlowCallExecutor.execute(() -> {
            setAutoAspectRatio(getPtr(), aspectRatio);
            return 0;
        });

    }

    public void setAspectRatio(final float aspectRatio) {
        ENG_SlowCallExecutor.execute(() -> {
            setAspectRatio(getPtr(), aspectRatio);
            return 0;
        });

    }

    public void setFOVy(final float foVy) {
        ENG_SlowCallExecutor.execute(() -> {
            setFOVy(getPtr(), foVy);
            return 0;
        });
    }

    public void setFixedYawAxis(final boolean fixed) {
        ENG_SlowCallExecutor.execute(() -> {
            setFixedYawAxis(getPtr(), fixed);
            return 0;
        });
    }

    private static native void setPosition(long ptr, float x, float y, float z);
    private static native void lookAt(long ptr, float x, float y, float z);
    private static native void setDirection(long ptr, float x, float y, float z);
    private static native void setNearClipDistance(long ptr, float dist);
    private static native void setFarClipDistance(long ptr, float dist);
    private static native void setAutoAspectRatio(long ptr, boolean aspectRatio);
    private static native void setAspectRatio(long ptr, float aspectRatio);
    private static native void setFOVy(long ptr, float fovInRadians);
    private static native void setFixedYawAxis(long ptr, boolean fixed);
}
