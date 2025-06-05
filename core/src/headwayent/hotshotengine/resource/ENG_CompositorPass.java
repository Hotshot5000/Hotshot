/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.renderer.ENG_RenderQueue.RenderQueueGroupID;

import java.util.ArrayList;

public class ENG_CompositorPass {

	/*Format: 'pass' 
     * (render_quad | clear | stencil | render_scene | render_custom) [custom name]*/

    public static final int TYPE_RENDER_QUAD = 1;
    public static final int TYPE_CLEAR = 2;
    public static final int TYPE_STENCIL = 3;
    public static final int TYPE_RENDER_SCENE = 4;
    public static final int TYPE_RENDER_CUSTOM = 5;

    public String name;
    public String material;
    public ArrayList<ENG_CompositorInputRenderTarget> inputList;
    public ENG_CompositorPassClear clear;
    public ENG_CompositorPassStencil stencil;
    public int type;
    public int identifier = -1;
    public int firstRenderQueue =
            RenderQueueGroupID.RENDER_QUEUE_SKIES_EARLY.getID();
    public int lastRenderQueue =
            RenderQueueGroupID.RENDER_QUEUE_SKIES_LATE.getID();
}
