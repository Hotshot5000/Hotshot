/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import java.util.concurrent.CountDownLatch;

/**
 * Created by sebas on 18.02.2017.
 */

public class ENG_SlowCall {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final ENG_ISlowCall call;

    public ENG_SlowCall(ENG_ISlowCall call) {
        this.call = call;
    }

    public void awaitExecution() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        call.execute();
        latch.countDown();
    }
}
