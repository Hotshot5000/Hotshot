/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Box;
import headwayent.hotshotengine.renderer.ENG_PixelBox;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderTexture;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureUsage;
import headwayent.hotshotengine.renderer.opengles.GLRenderTexture.GLSurfaceDesc;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.GL20;

public class GLTextureBuffer extends GLHardwarePixelBuffer {

    // In case this is a texture level
    protected final int mTarget;
    protected int mFaceTarget; // same as mTarget in case of GL_TEXTURE_xD, but cubemap face for cubemaps
    protected final int mTextureID;
    protected final int mFace;
    protected final int mLevel;
    protected final boolean mSoftwareMipmap;        // Use GLU for mip mapping

    protected final ArrayList<ENG_RenderTexture> mSliceTRT =
            new ArrayList<>();

    public GLTextureBuffer(String baseName, int width, int height, int depth,
                           int target, int id, int face, int level,
                           PixelFormat pf, int usage, boolean crappyCard,
                           boolean writeGamma, int fsaa) {
        this(baseName, width, height, depth, target, id, face, level, pf, usage,
                crappyCard, writeGamma, fsaa, false);
    }

    public GLTextureBuffer(String baseName, int width, int height, int depth,
                           int target, int id, int face, int level,
                           PixelFormat pf, int usage, boolean crappyCard,
                           boolean writeGamma, int fsaa, boolean useShadowBuffer) {
        super(width, height, depth, pf, usage, useShadowBuffer);

        mTarget = target;
        mTextureID = id;
        mFace = face;
        mLevel = level;
        mSoftwareMipmap = crappyCard;
        mGLInternalFormat = GLPixelUtil.getClosestGLInternalFormat(pf, writeGamma);

        //	int[] value = new int[1];

        //	MTGLES20.glBindTextureImmediate(mTarget, mTextureID);

        mFaceTarget = mTarget;
        if (mTarget == GL20.GL_TEXTURE_CUBE_MAP) {
            mFaceTarget = GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face;
        }

        //	MTGLES20.glGetTexParameterfv(mFaceTarget, GLES20.GL_TEXTURE_I, params)

        // Default
        rowPitch = width;
        slicePitch = height * width;
        sizeInBytes = ENG_PixelUtil.getMemorySize(width, height, depth, format);

        mBuffer = new ENG_PixelBox(width, height, depth, format);

        if (width == 0 || height == 0 || depth == 0) {
            return;
        }

        if ((usage & TextureUsage.TU_RENDERTARGET.getUsage()) != 0) {
            for (int zoffset = 0; zoffset < depth; ++zoffset) {
                //	MTGLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                //	MTGLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                String name = "rtt/" + this + "/" + baseName;
                GLSurfaceDesc surface = new GLSurfaceDesc();
                surface.buffer = this;
                surface.zoffset = zoffset;
                ENG_RenderTexture trt = GLRTTManager.getSingleton()
                        .createRenderTexture(name, surface, writeGamma, fsaa);
                mSliceTRT.add(trt);
                ENG_RenderRoot.getRenderRoot().getRenderSystem().attachRenderTarget(trt);
            }
        }
    }

    /** @noinspection deprecation */
    public void upload(ENG_PixelBox data, ENG_Box dst) {
        MTGLES20.glBindTexture(mTarget, mTextureID);
        if (ENG_PixelUtil.isCompressed(data.pixelFormat)) {
            if (data.pixelFormat != format || !data.isConsecutive()) {
                throw new UnsupportedOperationException(
                        "Compressed images must be consecutive, in the source format");
            }
            int fmt = GLPixelUtil.getClosestGLInternalFormat(format, false);

            switch (mTarget) {
                case GL20.GL_TEXTURE_2D:
                case GL20.GL_TEXTURE_CUBE_MAP:
                    if (dst.left == 0 && dst.top == 0) {
                        MTGLES20.glCompressedTexImage2D(mFaceTarget, mLevel,
                                format.getFormat(),
                                dst.getWidth(),
                                dst.getHeight(),
                                0,
                                data.getConsecutiveSize(),
                                data.data);
                    } else {
                        MTGLES20.glCompressedTexSubImage2D(mFaceTarget, mLevel,
                                dst.left, dst.top,
                                dst.getWidth(), dst.getHeight(),
                                format.getFormat(), data.getConsecutiveSize(),
                                data.data);
                    }
                    break;
            }
        } else if (mSoftwareMipmap) {
            throw new UnsupportedOperationException("Software mipmap not neccessary");
        } else {
            if (((data.getWidth() * ENG_PixelUtil.getNumElemBytes(data.pixelFormat)) & 3) != 0) {
                // Standard alignment of 4 is not right
                MTGLES20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
            }
//			while (data.data.remaining() > 0) {
//				byte b = data.data.get();
//				if (b != 0) {
//					System.out.println(b);
//				}
//			}
            data.data.rewind();
            switch (mTarget) {
                case GL20.GL_TEXTURE_2D:
                case GL20.GL_TEXTURE_CUBE_MAP:
                    MTGLES20.glTexSubImage2D(mFaceTarget, mLevel,
                            dst.left, dst.top,
                            dst.getWidth(), dst.getHeight(),
                            GLPixelUtil.getGLOriginFormat(data.pixelFormat),
                            GLPixelUtil.getGLOriginDataType(data.pixelFormat),
                            data.data);
                    MTGLES20.glGenerateMipmap(GL20.GL_TEXTURE_2D);
                    break;
                default:
                    throw new UnsupportedOperationException("Texture operation " +
                            "not supported");
            }
        }
        MTGLES20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 4);
    }

    public void download(ENG_PixelBox data) {
        throw new UnsupportedOperationException("Downloading texture not supported");
    }

    /** @noinspection deprecation*/
    public void bindToFramebuffer(int attachment, int zoffset) {
        if ((zoffset < 0) || (zoffset >= depth)) {
            throw new IllegalArgumentException("zoffset must be between 0 and depth");
        }
        switch (mTarget) {
            case GL20.GL_TEXTURE_2D:
            case GL20.GL_TEXTURE_CUBE_MAP:
                MTGLES20.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, attachment,
                        mFaceTarget, mTextureID, mLevel);
                break;
            default:
                throw new UnsupportedOperationException("Only 2D and cube " +
                        "map textures supported");
        }
    }

    /** @noinspection deprecation */
    public void copyFromFramebuffer(int zoffset) {
        MTGLES20.glBindTexture(mTarget, mTextureID);
        switch (mTarget) {
            case GL20.GL_TEXTURE_2D:
            case GL20.GL_TEXTURE_CUBE_MAP:
                MTGLES20.glCopyTexImage2D(mFaceTarget, mLevel, 0, 0, 0, width, height, 0);
                break;
            default:
                throw new UnsupportedOperationException("Only 2D and cube " +
                        "map textures supported");
        }
    }

    public ENG_RenderTexture getRenderTarget(int zoffset) {
        if ((usage & TextureUsage.TU_RENDERTARGET.getUsage()) == 0) {
            throw new UnsupportedOperationException("It is not a render target");
        }
        if ((zoffset < 0) || (zoffset >= depth)) {
            throw new IllegalArgumentException("zoffset must be between 0 and depth");
        }
        return mSliceTRT.get(zoffset);
    }

    public void _clearSliceRTT(int zoffset) {
        mSliceTRT.add(zoffset, null);
    }

    @Override
    public void destroy(boolean skipGLDelete) {

        super.destroy(skipGLDelete);
        if ((usage & TextureUsage.TU_RENDERTARGET.getUsage()) != 0) {
            for (ENG_RenderTexture tex : mSliceTRT) {
                ENG_RenderRoot.getRenderRoot().getRenderSystem().destroyRenderTarget(tex.getName(), skipGLDelete);
            }
        }
    }

}
