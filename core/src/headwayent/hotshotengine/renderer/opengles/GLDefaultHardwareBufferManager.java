/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_HardwareBufferManager;

public class GLDefaultHardwareBufferManager extends ENG_HardwareBufferManager {

    public GLDefaultHardwareBufferManager() {
        super(new GLDefaultHardwareBufferManagerBase());

    }

}
