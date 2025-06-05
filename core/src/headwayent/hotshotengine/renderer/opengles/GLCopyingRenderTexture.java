/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

public class GLCopyingRenderTexture extends GLRenderTexture {

    public GLCopyingRenderTexture(GLCopyingRTTManager manager,
                                  String name, GLSurfaceDesc target,
                                  boolean writeGamma, int fsaa) {
        super(name, target, writeGamma, fsaa);
        
    }

    public Object getCustomAttribute(String name, Object data) {
        if (name.equals("TARGET")) {
            GLSurfaceDesc target = (GLSurfaceDesc) data;
            target.buffer = (GLHardwarePixelBuffer) mBuffer;
            target.zoffset = mZOffset;
        }
        return null;
    }

}
