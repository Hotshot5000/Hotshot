/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.Capabilities;
import headwayent.hotshotengine.renderer.ENG_Texture;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureType;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureUsage;
import headwayent.hotshotengine.renderer.ENG_TextureManager;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.GL20;

public class GLTextureManager extends ENG_TextureManager {

    protected int mWarningTextureID;

    public GLTextureManager() {
        
//        createWarningTexture();
    }

    /** @noinspection deprecation */
    protected void createWarningTexture() {
        int width = 8;
        int height = 8;
        int[] data = new int[width * height];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                data[y * width + x] = (((x + y) % 8) < 4) ? 0x000000 : 0xFFFF00;
            }
        }

        //	int[] id = new int[1];
        IntBuffer id = ENG_Utility.allocateDirect(4).asIntBuffer();
        MTGLES20.glGenTexturesImmediate(1, id);
        mWarningTextureID = id.get();
        MTGLES20.glBindTexture(GL20.GL_TEXTURE_2D, mWarningTextureID);

        ByteBuffer buf = ENG_Utility.allocateDirect(width * height * 4);

        MTGLES20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA,
                width, height, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
                buf.asIntBuffer().put(data).flip());
    }

    protected GLTexture createResource(String name) {
        return new GLTexture(name);
    }

    @Override
    public PixelFormat getNativeFormat(TextureType ttype, PixelFormat format,
                                       int usage) {
        
        ENG_RenderSystemCapabilities rsc =
                ENG_RenderRoot.getRenderRoot().getRenderSystem().getCapabilities();

        if (ENG_PixelUtil.isCompressed(format) &&
                !rsc.hasCapability(Capabilities.RSC_TEXTURE_COMPRESSION_DXT)) {
            return PixelFormat.PF_A8R8G8B8;
        }

        if (ENG_PixelUtil.isFloatingPoint(format) &&
                !rsc.hasCapability(Capabilities.RSC_TEXTURE_FLOAT)) {
            return PixelFormat.PF_A8R8G8B8;
        }

        if ((usage & TextureUsage.TU_RENDERTARGET.getUsage()) != 0) {
        /*	throw new ENG_InvalidFieldStateException("No RTT for now. Invalid" +
					" texture usage value!");*/
            return GLRTTManager.getSingleton().getSupportedAlternative(format);
        }

        return format;
    }

    @Override
    public boolean isHardwareFilteringSupported(TextureType ttype,
                                                PixelFormat format, Usage usage, boolean preciseFormatOnly) {
        
        if (format == PixelFormat.PF_UNKNOWN) {
            return false;
        }

        PixelFormat nativeFormat = getNativeFormat(ttype, format, usage.getUsage());
        return !(preciseFormatOnly && format != nativeFormat) && !ENG_PixelUtil.isFloatingPoint(format);

    }

    public int getWarningTextureID() {
        return mWarningTextureID;
    }

    @Override
    public ENG_Texture createImpl(String name) {
        
        return new GLTexture(name);
    }

}
