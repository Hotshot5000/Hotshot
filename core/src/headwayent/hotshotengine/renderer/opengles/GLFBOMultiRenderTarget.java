/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_MultiRenderTarget;
import headwayent.hotshotengine.renderer.ENG_RenderTexture;

public class GLFBOMultiRenderTarget extends ENG_MultiRenderTarget {

    private final GLFrameBufferObject fbo;

    public GLFBOMultiRenderTarget(GLFBOManager manager, String name) {
        super(name);
        
        fbo = new GLFrameBufferObject(manager, 0);
    }

    @Override
    protected void bindSurfaceImpl(int attachment, ENG_RenderTexture target) {


        GLFrameBufferObject fb = (GLFrameBufferObject) target.getCustomAttribute("FBO", null);
        if (fb == null) {
            throw new IllegalArgumentException("Could not get the frame buffer object");
        }
        fbo.bindSurface(attachment, fb.getSurface(0));
        mWidth = fbo.getWidth();
        mHeight = fbo.getHeight();
    }

    @Override
    protected void unbindSurfaceImpl(int attachment) {


        fbo.unbindSurface(attachment);

        mWidth = fbo.getWidth();

        mHeight = fbo.getHeight();
    }

    @Override
    public boolean requiresTextureFlipping() {

        return true;
    }

    public Object getCustomAttribute(String name, Object data) {
        if (name.equals("FBO")) {
            return fbo;
        }
        return null;
    }

}
