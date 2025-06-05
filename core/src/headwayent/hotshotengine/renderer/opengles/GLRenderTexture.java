/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_RenderTexture;

public abstract class GLRenderTexture extends ENG_RenderTexture {

    static class GLSurfaceDesc {
        public GLHardwarePixelBuffer buffer;
        public int zoffset;
        public int numSamples;
    }

    public GLRenderTexture(String name, GLSurfaceDesc target,
                           boolean writeGamma, int fsaa) {
        super(target.buffer, target.zoffset);
        
        this.mName = name;
        this.mHwGamma = writeGamma;
        this.mFSAA = fsaa;
    }

    @Override
    public boolean requiresTextureFlipping() {

        return true;
    }

/*	@Override
    public void swapBuffers(boolean waitForVSync) {


	}*/

}
