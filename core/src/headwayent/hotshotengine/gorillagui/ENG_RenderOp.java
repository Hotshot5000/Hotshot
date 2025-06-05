/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/14/17, 9:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.gorillagui;

import headwayent.hotshotengine.renderer.ENG_HardwareVertexBuffer;
import headwayent.hotshotengine.renderer.ENG_RenderOperation;

/**
 * Created by sebas on 13.03.2017.
 */
class ENG_RenderOp {
    public ENG_HardwareVertexBuffer mVertexBuffer;
    public int mVertexBufferSize;
    public final ENG_RenderOperation mRenderOp;

    ENG_RenderOp() {
        mRenderOp = new ENG_RenderOperation();
    }
}
