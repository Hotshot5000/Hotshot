/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:02 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_MultiRenderTarget;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderTarget;
import headwayent.hotshotengine.renderer.ENG_RenderTexture;
import headwayent.hotshotengine.renderer.opengles.GLRenderTexture.GLSurfaceDesc;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.util.TreeMap;

import com.badlogic.gdx.graphics.GL20;

public class GLFBOManager extends GLRTTManager {

    private static final int PROBE_SIZE = 16;

    private static final int[] stencilFormats = {
            GL20.GL_NONE,
            GL20.GL_STENCIL_INDEX8
    };
    private static final int[] stencilBits = {0, 8};

    private static final int[] depthFormats = {
            GL20.GL_NONE,
            GL20.GL_DEPTH_COMPONENT16
    };
    private static final int[] depthBits = {0, 16};

    private static class RBFormat implements Comparable<RBFormat> {
        public final int format;
        public final int width;
        public final int height;
        public final int samples;

        public RBFormat(int format, int width, int height, int fsaa) {
            this.format = format;
            this.width = width;
            this.height = height;
            this.samples = fsaa;
        }

        @Override
        public int compareTo(RBFormat other) {

            if (format < other.format) {
                return -1;
            } else if (format > other.format) {
                return 1;
            } else if (width < other.width) {
                return -1;
            } else if (width > other.width) {
                return 1;
            } else if (height < other.height) {
                return -1;
            } else if (height > other.height) {
                return 1;
            } else return Integer.compare(samples, other.samples);
        }
    }

    private static class RBRef {
        public final GLRenderBuffer buffer;
        public int refCount = 1;

        public RBRef(GLRenderBuffer buffer) {
            this.buffer = buffer;
        }


    }

    private final TreeMap<RBFormat, RBRef> mRenderBufferMap = new TreeMap<>();

    public GLFBOManager() {

    }

    /** @noinspection deprecation*/ // THE MIPMAP IN FILTERING MUST BE NONE (LINEAR LINEAR NONE)
    @Override
    public void bind(ENG_RenderTarget target) {


        //	int[] currentTexture = new int[1];
        //	MTGLES20.glGetIntegervImmediate(GLES20.GL_TEXTURE_BINDING_2D, currentTexture, 0);
        GLFrameBufferObject fbo =
                (GLFrameBufferObject) target.getCustomAttribute("FBO", null);
        if (fbo != null) {
            fbo.bind();
            //	System.out.println("FBO ACTIVE");
        } else {
            //	int[] oldFb = new int[1];

            //	MTGLES20.glGetIntegervImmediate(GLES20.GL_FRAMEBUFFER_BINDING, oldFb, 0);


            MTGLES20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);
            //	System.out.println("FBO INACTIVE");
        }
        //	MainApp.getMainThread().flushGLPipeline();
    /*	GLUtility.checkForGLSLError( "GLFBOManager::bind",
				"Error prior to using GLSL Program Object : ", 
				0, false, true);*/
    }

    @Override
    public boolean checkFormat(PixelFormat format) {

        return false;
    }

    @Override
    public ENG_RenderTexture createRenderTexture(String name,
                                                 GLSurfaceDesc target, boolean writeGamma, int fsaa) {

        return new GLFBORenderTexture(this, name, target, writeGamma, fsaa);
    }

    @Override
    public void unbind(ENG_RenderTarget target) {


    }

    public GLSurfaceDesc requestRenderBuffer(int format, int width, int height, int fsaa) {
        GLSurfaceDesc retval = new GLSurfaceDesc();
        if (format != GL20.GL_NONE) {
            RBFormat rbf = new RBFormat(format, width, height, fsaa);
            RBRef ref = mRenderBufferMap.get(rbf);
            if (ref != null) {
                retval.buffer = ref.buffer;
                retval.zoffset = 0;
                retval.numSamples = fsaa;
                ++ref.refCount;
            } else {
                GLRenderBuffer rb = new GLRenderBuffer(format, width, height, fsaa);
                mRenderBufferMap.put(rbf, new RBRef(rb));
                retval.buffer = rb;
                retval.zoffset = 0;
                retval.numSamples = fsaa;
            }
        }
        return retval;
    }

    public ENG_MultiRenderTarget createMultiRenderTarget(String name) {
        return new GLFBOMultiRenderTarget(this, name);
    }

    public void requestRenderBuffer(GLSurfaceDesc surface) {
        if (surface.buffer == null) {
            return;
        }

        RBFormat key = new RBFormat(surface.buffer.getGLFormat(),
                surface.buffer.getWidth(), surface.buffer.getHeight(), surface.numSamples);
        RBRef ref = mRenderBufferMap.get(key);
        if (ref != null) {
            ++ref.refCount;
        } else {
            throw new IllegalArgumentException("surface cannot be found!");
        }
    }

    public void releaseRenderBuffer(GLSurfaceDesc surface, boolean skipGLDelete) {
        if (surface.buffer == null) {
            return;
        }

        RBFormat key = new RBFormat(surface.buffer.getGLFormat(),
                surface.buffer.getWidth(), surface.buffer.getHeight(),
                surface.numSamples);
        RBRef ref = mRenderBufferMap.get(key);
        if (ref != null) {
            --ref.refCount;
            if (ref.refCount == 0) {
                ref.buffer.destroy(skipGLDelete);
                mRenderBufferMap.remove(key);
            }
        }
    }

    public void getBestDepthStencil(int internalFormat, ENG_Integer depthFormat,
                                    ENG_Integer stencilFormat) {
        depthFormat.setValue(depthFormats[1]);
        stencilFormat.setValue(stencilFormats[1]);
    }

}
