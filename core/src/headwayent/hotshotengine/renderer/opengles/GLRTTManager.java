/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.renderer.ENG_MultiRenderTarget;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.ENG_RenderTarget;
import headwayent.hotshotengine.renderer.ENG_RenderTexture;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelComponentType;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.opengles.GLRenderTexture.GLSurfaceDesc;

public abstract class GLRTTManager {

//    private static GLRTTManager mgr;

    public GLRTTManager() {
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleDeclarationException();
//        }
    }

    public abstract ENG_RenderTexture createRenderTexture(String name, GLSurfaceDesc target,
                                                          boolean writeGamma, int fsaa);

    public abstract boolean checkFormat(PixelFormat format);

    public abstract void bind(ENG_RenderTarget target);

    public abstract void unbind(ENG_RenderTarget target);

    public ENG_MultiRenderTarget createMultiRenderTarget(String name) {
        throw new UnsupportedOperationException(
                "MultiRenderTarget can only be used with GL_EXT_framebuffer_object extension");
    }

    public PixelFormat getSupportedAlternative(PixelFormat format) {
        if (checkFormat(format)) {
            return format;
        }

        PixelComponentType pct = ENG_PixelUtil.getComponentType(format);
        switch (pct) {
            case PCT_BYTE:
                format = PixelFormat.PF_A8R8G8B8;
                break;
            case PCT_SHORT:
                format = PixelFormat.PF_SHORT_RGBA;
                break;
            case PCT_FLOAT16:
                format = PixelFormat.PF_FLOAT16_RGBA;
                break;
            case PCT_FLOAT32:
                format = PixelFormat.PF_FLOAT32_RGBA;
                break;
            case PCT_COUNT:
                break;
        }

        if (checkFormat(format)) {
            return format;
        }

        return PixelFormat.PF_A8R8G8B8;
    }

    public static GLRTTManager getSingleton() {
//        if (MainActivity.isDebugmode() && mgr == null) {
//            throw new NullPointerException("GLRTTManager not initialised");
//        }
//        return mgr;
        return ((GLRenderSystem) MainApp.getGame().getRenderRoot().getActiveRenderSystem()).getRTTManager();
    }
}
