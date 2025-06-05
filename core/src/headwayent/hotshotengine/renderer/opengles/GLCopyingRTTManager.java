/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_RenderTarget;
import headwayent.hotshotengine.renderer.ENG_RenderTexture;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.opengles.GLRenderTexture.GLSurfaceDesc;

public class GLCopyingRTTManager extends GLRTTManager {

    public GLCopyingRTTManager() {
        
    }

    @Override
    public void bind(ENG_RenderTarget target) {
        

    }

    @Override
    public boolean checkFormat(PixelFormat format) {
        
        return true;
    }


    @Override
    public ENG_RenderTexture createRenderTexture(String name,
                                                 GLSurfaceDesc target, boolean writeGamma, int fsaa) {
        
        return new GLCopyingRenderTexture(this, name, target, writeGamma, fsaa);
    }

    @Override
    public void unbind(ENG_RenderTarget target) {
        

        GLSurfaceDesc surface = new GLSurfaceDesc();
        target.getCustomAttribute("TARGET", surface);
        if (surface.buffer != null) {
            ((GLTextureBuffer) surface.buffer).copyFromFramebuffer(surface.zoffset);
        }
    }

}
