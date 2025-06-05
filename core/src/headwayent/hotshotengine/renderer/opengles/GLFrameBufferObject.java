/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.ENG_Config;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.opengles.GLRenderTexture.GLSurfaceDesc;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.GL20;

public class GLFrameBufferObject {

    private final GLFBOManager mManager;
    private final int mNumSamples;
    private final int mFB;
    private int mMultisampleFB;
    private GLSurfaceDesc mMultisampleColourBuffer = new GLSurfaceDesc();
    private GLSurfaceDesc mDepth = new GLSurfaceDesc();
    private final GLSurfaceDesc mStencil = new GLSurfaceDesc();

    // Arbitrary number of texture surfaces
    private final GLSurfaceDesc[] mColour =
            new GLSurfaceDesc[ENG_Config.OGRE_MAX_MULTIPLE_RENDER_TARGETS];

    /** @noinspection deprecation*/
    public GLFrameBufferObject(GLFBOManager mgr, int fsaa) {
        mManager = mgr;
        this.mNumSamples = fsaa;
        for (int i = 0; i < mColour.length; ++i) {
            mColour[i] = new GLSurfaceDesc();
        }

        //int[] fb = new int[1];
        IntBuffer fb = ENG_Utility.allocateDirect(4).asIntBuffer();
        MTGLES20.glGenFramebuffersImmediate(1, fb);
        mFB = fb.get();
    }

    /** @noinspection deprecation */
    public void destroy(boolean skipGLDelete) {
        mManager.releaseRenderBuffer(mDepth, skipGLDelete);
        mManager.releaseRenderBuffer(mStencil, skipGLDelete);
        mManager.releaseRenderBuffer(mMultisampleColourBuffer, skipGLDelete);

        if (!skipGLDelete) {
            MTGLES20.glDeleteFramebuffers(1, new int[]{mFB}, 0);
        }

        if (mMultisampleFB != 0) {
            if (!skipGLDelete) {
                MTGLES20.glDeleteFramebuffers(1, new int[]{mMultisampleFB}, 0);
            }
        }
    }

    public void bindSurface(int attachment, GLSurfaceDesc target) {
        if ((attachment < 0) || (attachment >= ENG_Config.OGRE_MAX_MULTIPLE_RENDER_TARGETS)) {
            throw new IllegalArgumentException("attachment out of range");
        }
        mColour[attachment] = target;
        if (mColour[0].buffer != null) {
            initialise();
        }
    }

    public void unbindSurface(int attachment) {
        if ((attachment < 0) ||
                (attachment >= ENG_Config.OGRE_MAX_MULTIPLE_RENDER_TARGETS)) {
            throw new IllegalArgumentException("attachment out of range");
        }
        mColour[attachment] = null;
        if (mColour[0].buffer != null) {
            initialise();
        }
    }

    /** @noinspection deprecation */
    private void initialise() {
        // Release depth and stencil, if they were bound
        mManager.releaseRenderBuffer(mDepth, false);
        mManager.releaseRenderBuffer(mStencil, false);
        mManager.releaseRenderBuffer(mMultisampleColourBuffer, false);

        if (mColour[0].buffer == null) {
            throw new ENG_InvalidFieldStateException(
                    "Attachment 0 must have surface attached");
        }

        /// Store basic stats
        int width = mColour[0].buffer.getWidth();
        int height = mColour[0].buffer.getHeight();
        int format = mColour[0].buffer.getGLFormat();
        PixelFormat ogreFormat = mColour[0].buffer.getFormat();
        short maxSupportedMRTs = ENG_RenderRoot.getRenderRoot()
                .getRenderSystem().getCapabilities().getmNumMultiRenderTargets();
        //   MTGLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        MTGLES20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, mFB);

        for (int i = 0; i < maxSupportedMRTs; ++i) {
            if (mColour[i].buffer != null) {
                if (mColour[i].buffer.getWidth() != width ||
                        mColour[i].buffer.getHeight() != height) {
                    throw new ENG_InvalidFieldStateException("Attachement " + i +
                            "has invalid size of " + mColour[i].buffer.getWidth() +
                            " " + mColour[i].buffer.getHeight() +
                            " with width " + width + " and height " + height);
                }
                if (mColour[i].buffer.getGLFormat() != format) {
                    throw new ENG_InvalidFieldStateException("Invalid format of " +
                            "attachement " + i + " " +
                            mColour[i].buffer.getGLFormat() + " format " +
                            format);
                }
                mColour[i].buffer.bindToFramebuffer(GL20.GL_COLOR_ATTACHMENT0 + i,
                        mColour[i].zoffset);
            } else {
                MTGLES20.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER,
                        GL20.GL_COLOR_ATTACHMENT0 + i,
                        GL20.GL_RENDERBUFFER, 0);
            }
        }

        if (mMultisampleFB != 0) {
            MTGLES20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, mMultisampleFB);

            // Create AA render buffer (colour)
            // note, this can be shared too because we blit it to the final FBO
            // right after the render is finished
            mMultisampleColourBuffer = mManager.requestRenderBuffer(
                    format, width, height, mNumSamples);

            // Attach it, because we won't be attaching below and non-multisample has
            // actually been attached to other FBO
            mMultisampleColourBuffer.buffer.bindToFramebuffer(
                    GL20.GL_COLOR_ATTACHMENT0,
                    mMultisampleColourBuffer.zoffset);

            // depth & stencil will be dealt with below

        }

        ENG_Integer depthFormat = new ENG_Integer();
        ENG_Integer stencilFormat = new ENG_Integer();
        mManager.getBestDepthStencil(ogreFormat.getFormat(),
                depthFormat, stencilFormat);

        /// Request surfaces
        mDepth = mManager.requestRenderBuffer(depthFormat.getValue(),
                width, height, mNumSamples);

        // separate stencil
        //	mStencil = mManager.requestRenderBuffer(stencilFormat.getValue(),
        //			width, height, mNumSamples);

        if (mDepth.buffer != null) {
            mDepth.buffer.bindToFramebuffer(
                    GL20.GL_DEPTH_ATTACHMENT, mDepth.zoffset);
        } else {
            MTGLES20.glFramebufferRenderbuffer(
                    GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT,
                    GL20.GL_RENDERBUFFER, 0);
        }

        if (mStencil.buffer != null) {
            //	mStencil.buffer.bindToFramebuffer(
            //			GL20.GL_STENCIL_ATTACHMENT, mStencil.zoffset);
        } else {
            MTGLES20.glFramebufferRenderbuffer(
                    GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT,
                    GL20.GL_RENDERBUFFER, 0);
        }

    /* 	MTGLES20.setRenderingAllowed(true);
    	GLRenderSurface.getSingleton().requestRender(true);
    	GLRenderSurface.getSingleton().waitForRenderingToFinish();*/

        //  	MainApp.getMainThread().flushGLPipeline();

        int status = GL20.GL_FRAMEBUFFER_COMPLETE;//MTGLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

        switch (status) {
            case GL20.GL_FRAMEBUFFER_COMPLETE:
                break;
            case GL20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                throw new IllegalArgumentException("Framebuffer incomplete attachment");
            case GL20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                throw new IllegalArgumentException("Framebuffer incomplete " +
                        "missing attachment");
            case GL20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                throw new IllegalArgumentException("Framebuffer incomplete dimensions");
    /*	case GL20.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
    		break;*/
            case GL20.GL_FRAMEBUFFER_UNSUPPORTED:
                throw new IllegalArgumentException("Framebuffer unsupported");
            default:
                //Should never get here
                throw new IllegalArgumentException("Serious issue with GLES20");
        }

        MTGLES20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);

    }

    public GLFBOManager getManager() {
        return mManager;
    }

    public GLSurfaceDesc getSurface(int attachment) {
        return mColour[attachment];
    }

    /** @noinspection deprecation*/
    public void bind() {
        MTGLES20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, mFB);
    }

    public void swapBuffers() {

    }

    public int getWidth() {
        if (mColour[0].buffer == null) {
            throw new NullPointerException("Buffer is null");
        }
        return mColour[0].buffer.getWidth();
    }

    public int getHeight() {
        if (mColour[0].buffer == null) {
            throw new NullPointerException("Buffer is null");
        }
        return mColour[0].buffer.getHeight();
    }

    public PixelFormat getFormat() {
        if (mColour[0].buffer == null) {
            throw new NullPointerException("Buffer is null");
        }
        return mColour[0].buffer.getFormat();
    }
}
