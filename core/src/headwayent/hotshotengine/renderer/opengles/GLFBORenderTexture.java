/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;


public class GLFBORenderTexture extends GLRenderTexture {

    protected final GLFrameBufferObject mFB;

    public GLFBORenderTexture(GLFBOManager manager, String name, GLSurfaceDesc target,
                              boolean writeGamma, int fsaa) {
        super(name, target, writeGamma, fsaa);

        mFB = new GLFrameBufferObject(manager, fsaa);

        // Bind target to surface 0 and initialise
        mFB.bindSurface(0, target);
        // Get attributes
        mWidth = mFB.getWidth();
        mHeight = mFB.getHeight();
    }

    public Object getCustomAttribute(String name, Object data) {
        if (name.equals("FBO")) {
            return mFB;
        }
        return null;
    }

    public void swapBuffers(boolean waitForVSync) {
        mFB.swapBuffers();
    }

    @Override
    public void destroy(boolean skipGLDelete) {

        super.destroy(skipGLDelete);
        mFB.destroy(skipGLDelete);
    }

}
