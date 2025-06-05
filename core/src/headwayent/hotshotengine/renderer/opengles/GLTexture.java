/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.exception.ENG_GLException;
import headwayent.hotshotengine.renderer.ENG_HardwarePixelBuffer;
import headwayent.hotshotengine.renderer.ENG_Image;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.ENG_Texture;
import headwayent.hotshotengine.renderer.ENG_TextureManager;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.GL20;

public class GLTexture extends ENG_Texture {

    private int mTextureID;
    private final ArrayList<ENG_HardwarePixelBuffer> mSurfaceList =
            new ArrayList<>();

    protected final ArrayList<ENG_Image> mLoadedImages =
            new ArrayList<>();

    public GLTexture(String name) {
        super(name);

    }

    public void createRenderTexture() {
        createInternalResources();
    }

    protected void _createSurfaceList() {
        mSurfaceList.clear();

        for (int face = 0; face < getNumFaces(); ++face) {
            //Always check <= since we can have 0 mipmaps and still have a texture!
            for (int mip = 0; mip <= getNumMipmaps(); ++mip) {
                GLHardwarePixelBuffer buf = new GLTextureBuffer(
                        name, width, height, depth,
                        getGLTextureTarget(), mTextureID, face, mip, format,
                        usage, false, false, 0, isUseShadowBuffer());
                mSurfaceList.add(buf);

                if (buf.getWidth() == 0 || buf.getHeight() == 0 || buf.getDepth() == 0) {
                    throw new ENG_GLException("GL driver refused to create the texture.");
                }
            }
        }
    }

    public ENG_HardwarePixelBuffer getBuffer(int face, int mipmap) {
        if ((face < 0) || (face >= getNumFaces())) {
            throw new IllegalArgumentException("Face index out of range");
        }
        if ((mipmap < 0) || (mipmap > getNumMipmaps())) {
            throw new IllegalArgumentException("Mipmap index out of range");
        }
        int idx = face * (numMipmaps + 1) + mipmap;
        if (idx >= mSurfaceList.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return mSurfaceList.get(idx);
    }

    /** @noinspection deprecation */
    @Override
    public void createInternalResourcesImpl() {


        format = ENG_TextureManager.getSingleton().getNativeFormat(
                textureType, format, usage);

        int maxMips = GLPixelUtil.getMaxMipmaps(width, height, depth, format);

        if (ENG_PixelUtil.isCompressed(format) && (numMipmaps == 0)) {
            numRequestedMipmaps = 0;
        }

        numMipmaps = numRequestedMipmaps;
        if (numMipmaps > maxMips) {
            numMipmaps = maxMips;
        }

        int w = width;
        int h = height;
        int d = depth;

        //	int[] id = new int[1];
        IntBuffer id = ENG_Utility.allocateDirect(4).asIntBuffer();
        MTGLES20.glGenTexturesImmediate(1, id);

        mTextureID = id.get();

        MTGLES20.glBindTexture(getGLTextureTarget(), mTextureID);

        mipmapsHardwareGenerated = true;

        MTGLES20.glTexParameteri(getGLTextureTarget(),
                GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);

        MTGLES20.glTexParameteri(getGLTextureTarget(),
                GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

        MTGLES20.glTexParameteri(getGLTextureTarget(),
                GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);

        MTGLES20.glTexParameteri(getGLTextureTarget(),
                GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);

        int f = GLPixelUtil.getClosestGLInternalFormat(format, hwGamma);

        if (ENG_PixelUtil.isCompressed(format)) {
            int size = ENG_PixelUtil.getMemorySize(width, height, depth, format);

            switch (textureType) {
                case TEX_TYPE_2D:
                    MTGLES20.glCompressedTexImage2D(GL20.GL_TEXTURE_2D, 0, f,
                            width, height, 0, size, null);
                    MTGLES20.glGenerateMipmap(GL20.GL_TEXTURE_2D);
                    break;
                case TEX_TYPE_CUBE_MAP: {
                    for (int face = 0; face < 6; ++face) {
                        MTGLES20.glCompressedTexImage2D(
                                GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,
                                f,
                                width, height, 0, size, null);
                    }
                    MTGLES20.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);

                }
                break;
                default:
                    throw new UnsupportedOperationException("texture type not supported");
            }
        } else {
            for (int mip = 0; mip <= numMipmaps; mip++) {
                switch (textureType) {
                    case TEX_TYPE_2D: {
//					 LWJGL requires a != null Buffer in any case
                        ByteBuffer buffer = ENG_Utility.allocateDirect(w * h * 4);
                        MTGLES20.glTexImage2D(GL20.GL_TEXTURE_2D, mip, f,
                                w, h, 0,
                                f, GL20.GL_UNSIGNED_BYTE,
//								(Buffer)null
                                buffer);


                        //	MTGLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
                    }
                    break;
                    case TEX_TYPE_CUBE_MAP: {
                        for (int face = 0; face < 6; ++face) {
                            // LWJGL requires a != null Buffer in any case
                            ByteBuffer buffer = ENG_Utility.allocateDirect(w * h * 4);
                            MTGLES20.glTexImage2D(
                                    GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, mip,
                                    f,
                                    w, h, 0, GL20.GL_RGBA,
                                    GL20.GL_UNSIGNED_BYTE,
//								(Buffer)null
                                    buffer);
                        }
                        //	MTGLES20.glGenerateMipmap(GLES20.GL_TEXTURE_CUBE_MAP);

                    }
                    break;
                    default:
                        throw new UnsupportedOperationException(
                                "texture type not supported");
                }
                if (w > 1) {
                    w /= 2;
                }
                if (h > 1) {
                    h /= 2;
                }
            }
        }
        _createSurfaceList();
        format = this.getBuffer(0, 0).getFormat();

    }

    /** @noinspection deprecation*/
    @Override
    public void freeInternalResourceImpl(boolean skipGLDelete) {


        for (ENG_HardwarePixelBuffer buf : mSurfaceList) {
            buf.destroy(skipGLDelete);
        }
        mSurfaceList.clear();
        if (!skipGLDelete) {
            MTGLES20.glDeleteTextures(1, new int[]{mTextureID}, 0);
        }
    }

    public void prepareImpl() {
        if ((usage & TextureUsage.TU_RENDERTARGET.getUsage()) != 0) {
//            return;
        }
    }

    public void loadImpl() {
        if ((usage & TextureUsage.TU_RENDERTARGET.getUsage()) != 0) {
            createRenderTexture();
            return;
        }

        _loadImages(mLoadedImages);
    }

    public int getGLTextureTarget() {
        switch (textureType) {
            case TEX_TYPE_1D:
                throw new IllegalArgumentException("Texture type not supported");
                //return GLES20.GL_TEXTURE_1D;
            case TEX_TYPE_2D:
                return GL20.GL_TEXTURE_2D;
            case TEX_TYPE_3D:
                throw new IllegalArgumentException("Texture type not supported");
                //return GLES20.GL_TEXTURE_3D;
            case TEX_TYPE_CUBE_MAP:
                return GL20.GL_TEXTURE_CUBE_MAP;
            default:
                return 0;
        }
    }

    public int getGLID() {
        return mTextureID;
    }

}
