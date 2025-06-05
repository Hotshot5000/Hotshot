/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/2/17, 2:50 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

/**
 * Created by sebas on 01.04.2017.
 */

public interface ENG_IEndFrameListener {

    /**
     *
     * @return true if the value has been updated so it can be removed from the queue.
     */
    boolean frameEnded();
}
