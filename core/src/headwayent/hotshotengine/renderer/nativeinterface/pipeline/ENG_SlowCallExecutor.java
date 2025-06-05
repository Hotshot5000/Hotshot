/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 9:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

/**
 * Created by sebas on 18.02.2017.
 * For convenience instead of using ENG_NativeCalls. It's much slower and blocks until the
 * response but it's also much easier to use. You just wrap your native calls in ENG_ISlowCall.
 */

public class ENG_SlowCallExecutor {

    public static void execute(ENG_ISlowCall call) {
        execute(call, false);
    }

    public static void execute(ENG_ISlowCall call, boolean runningFromRenderingThread) {
        ENG_RenderingThread.executeSlowCall(new ENG_SlowCall(call), runningFromRenderingThread);
    }
}
