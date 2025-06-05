/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Matrix4;

/**
 * Only for use with GIWS
 *
 * @author sebi
 */
public class ENG_RenderSystemWrapper {

    ENG_RenderSystem rsys;

    public ENG_RenderSystemWrapper() {
        
    }

    /**
     * Call this first in order to have a set rendersystem for the rest
     * of the calls.
     */
    public void setCurrentRenderSystem() {
        rsys = ENG_RenderRoot.getRenderRoot().getRenderSystem();
    }

    public void _setViewport(ENG_Viewport vp) {
        rsys._setViewport(vp);
    }

    public void _convertProjectionMatrix(ENG_Matrix4 mat, ENG_Matrix4 ret) {
        rsys._convertProjectionMatrix(mat, ret);
    }

}
