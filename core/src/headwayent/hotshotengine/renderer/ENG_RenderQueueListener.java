/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:01 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.basictypes.ENG_Boolean;

public interface ENG_RenderQueueListener {


    void preRenderQueues();

    void postRenderQueues();

    void renderQueueStarted(byte queueGroupId, String invocation,
                            ENG_Boolean skipThisInvocation);

    void renderQueueEnded(byte queueGroupId, String invocation,
                          ENG_Boolean repeatThisInvocation);
}
