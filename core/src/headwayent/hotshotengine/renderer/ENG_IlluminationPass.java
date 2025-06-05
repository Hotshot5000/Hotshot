/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:46 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.renderer.ENG_Pass.IlluminationStage;

public class ENG_IlluminationPass {

    public IlluminationStage stage;
    /// The pass to use in this stage
    public ENG_Pass pass;
    /// Whether this pass is one which should be deleted itself
    public boolean destroyOnShutdown;
    /// The original pass which spawned this one
    public ENG_Pass originalPass;

    public ENG_IlluminationPass() {

    }
}
