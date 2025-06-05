/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public abstract class ENG_FrameListener {

    public abstract boolean frameStarted(ENG_FrameEvent evt);

    public abstract boolean frameRenderingQueued(ENG_FrameEvent evt);

    public abstract boolean frameEnded(ENG_FrameEvent evt);

}