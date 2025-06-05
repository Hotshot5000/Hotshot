/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public abstract class ENG_ShadowCameraSetup {

    /** @noinspection deprecation*/
    public abstract void getShadowCamera(ENG_SceneManager sm, ENG_Camera cam,
                                         ENG_Viewport vp, ENG_Light light, ENG_Camera texCam, int iteration);
}
