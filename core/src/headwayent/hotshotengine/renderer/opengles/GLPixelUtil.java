/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.ENG_Bitwise;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderSystemCapabilities.Capabilities;

import java.nio.ByteOrder;

import com.badlogic.gdx.graphics.GL20;

public class GLPixelUtil {

    public static int getGLOriginFormat(PixelFormat mFormat) {
        switch (mFormat) {
            case PF_A8:
                return GL20.GL_ALPHA;
            case PF_L8:
                return GL20.GL_LUMINANCE;
            case PF_L16:
                return GL20.GL_LUMINANCE;
            case PF_BYTE_LA:
                return GL20.GL_LUMINANCE_ALPHA;
            case PF_R3G3B2:
                return GL20.GL_RGB;
            case PF_A1R5G5B5:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GL20.GL_BGRA;
            case PF_R5G6B5:
                return GL20.GL_RGB;
            case PF_B5G6R5:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_BGR;
            case PF_A4R4G4B4:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_BGRA;

                // Formats are in native endian, so R8G8B8 on little endian is
                // BGR, on big endian it is RGB.
            case PF_R8G8B8:
                if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                    return GL20.GL_RGB;
                } else {
                    throw new IllegalArgumentException("GLES20 does not support this pixel format");
                    //return GLES20.GL_BGR;
                }
            case PF_B8G8R8:
                if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
                    throw new IllegalArgumentException("GLES20 does not support this pixel format");
                    //return GLES20.GL_BGR;
                } else {
                    return GL20.GL_RGB;
                }


            case PF_X8R8G8B8:
            case PF_A8R8G8B8:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_BGRA;
            case PF_X8B8G8R8:
            case PF_A8B8G8R8:
                return GL20.GL_RGBA;
            case PF_B8G8R8A8:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_BGRA;
            case PF_R8G8B8A8:
                return GL20.GL_RGBA;
            case PF_A2R10G10B10:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_BGRA;
            case PF_A2B10G10R10:
                return GL20.GL_RGBA;
            case PF_FLOAT16_R:
                return GL20.GL_LUMINANCE;
            case PF_FLOAT16_GR:
                return GL20.GL_LUMINANCE_ALPHA;
            case PF_FLOAT16_RGB:
                return GL20.GL_RGB;
            case PF_FLOAT16_RGBA:
                return GL20.GL_RGBA;
            case PF_FLOAT32_R:
                return GL20.GL_LUMINANCE;
            case PF_FLOAT32_GR:
                return GL20.GL_LUMINANCE_ALPHA;
            case PF_FLOAT32_RGB:
                return GL20.GL_RGB;
            case PF_FLOAT32_RGBA:
                return GL20.GL_RGBA;
            case PF_SHORT_RGBA:
                return GL20.GL_RGBA;
            case PF_SHORT_RGB:
                return GL20.GL_RGB;
            case PF_SHORT_GR:
                return GL20.GL_LUMINANCE_ALPHA;
            case PF_DXT1:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
            case PF_DXT3:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
            case PF_DXT5:
                throw new IllegalArgumentException("GLES20 does not support this pixel format");
                //return GLES20.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
            default:
                return 0;
        }


    }

    public static int getGLOriginDataType(PixelFormat mFormat) {
        switch (mFormat) {
            case PF_A8:
            case PF_L8:
            case PF_R8G8B8:
            case PF_B8G8R8:
            case PF_BYTE_LA:
            case PF_X8B8G8R8:
            case PF_A8B8G8R8:
            case PF_X8R8G8B8:
            case PF_A8R8G8B8:
            case PF_B8G8R8A8:
            case PF_R8G8B8A8:
                return GL20.GL_UNSIGNED_BYTE;
            case PF_R3G3B2:
                throw new IllegalArgumentException("Data type not supported in GLES20");
                //return GLES20.GL_UNSIGNED_BYTE_3_3_2;
            case PF_A1R5G5B5:
                throw new IllegalArgumentException("Data type not supported in GLES20");
                //return GLES20.GL_UNSIGNED_SHORT_1_5_5_5_REV;
            case PF_R5G6B5:
            case PF_B5G6R5:
                return GL20.GL_UNSIGNED_SHORT_5_6_5;
            case PF_A4R4G4B4:
                throw new IllegalArgumentException("Data type not supported in GLES20");
                //return GLES20.GL_UNSIGNED_SHORT_4_4_4_4_REV;
            case PF_L16:
                return GL20.GL_UNSIGNED_SHORT;
      /*      case PF_B8G8R8A8:
                if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            		return GLES20.GL_UNSIGNED_BYTE;
            	} else {
            		throw new IllegalArgumentException("Data type not supported in GLES20");
            		//return GLES20.GL_UNSIGNED_INT_8_8_8_8;
            	}
            case PF_R8G8B8A8:
            	if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            		return GLES20.GL_UNSIGNED_BYTE;
            	} else {
            		throw new IllegalArgumentException("Data type not supported in GLES20");
            		//return GLES20.GL_UNSIGNED_INT_8_8_8_8;
            	}*/

            case PF_A2R10G10B10:
                throw new IllegalArgumentException("Data type not supported in GLES20");
                //return GLES20.GL_UNSIGNED_INT_2_10_10_10_REV;
            case PF_A2B10G10R10:
                throw new IllegalArgumentException("Data type not supported in GLES20");
                //return GLES20.GL_UNSIGNED_INT_2_10_10_10_REV;
            case PF_FLOAT16_R:
            case PF_FLOAT16_GR:
            case PF_FLOAT16_RGB:
            case PF_FLOAT16_RGBA:
                throw new IllegalArgumentException("Data type not supported in GLES20");
                //return GLES20.GL_HALF_FLOAT_ARB;
            case PF_FLOAT32_R:
            case PF_FLOAT32_GR:
            case PF_FLOAT32_RGB:
            case PF_FLOAT32_RGBA:
                return GL20.GL_FLOAT;
            case PF_SHORT_RGBA:
            case PF_SHORT_RGB:
            case PF_SHORT_GR:
                return GL20.GL_UNSIGNED_SHORT;
            default:
                return 0;
        }
    }

    public static int getGLInternalFormat(PixelFormat mFormat, boolean hwGamma) {
        switch (mFormat) {
            case PF_L8:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_LUMINANCE8;
            case PF_L16:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_LUMINANCE16;
            case PF_A8:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_ALPHA8;
            case PF_A4L4:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_LUMINANCE4_ALPHA4;
            case PF_BYTE_LA:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GL_LUMINANCE8_ALPHA8;
            case PF_R3G3B2:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_R3_G3_B2;
            case PF_A1R5G5B5:
                return GL20.GL_RGB5_A1;
            case PF_R5G6B5:
            case PF_B5G6R5:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_RGB5;
            case PF_A4R4G4B4:
                return GL20.GL_RGBA4;
            case PF_R8G8B8:
            case PF_B8G8R8:
            case PF_X8B8G8R8:
            case PF_X8R8G8B8:
                if (hwGamma)
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                    //return GLES20.GL_SRGB8;
                else
                    //throw new IllegalArgumentException("Pixel format not supported int GLES20");
                    return GL20.GL_RGB;
            case PF_A8R8G8B8:
            case PF_B8G8R8A8:
                if (hwGamma)
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                    //return GLES20.GL_SRGB8_ALPHA8;
                else
                    //throw new IllegalArgumentException("Pixel format not supported int GLES20");
                    return GL20.GL_RGBA;
            case PF_A2R10G10B10:
            case PF_A2B10G10R10:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_RGB10_A2;
            case PF_FLOAT16_R:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_LUMINANCE16F_ARB;
            case PF_FLOAT16_RGB:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_RGB16F_ARB;
            case PF_FLOAT16_GR:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_LUMINANCE_ALPHA16F_ARB;
            case PF_FLOAT16_RGBA:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_RGBA16F_ARB;
            case PF_FLOAT32_R:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_LUMINANCE32F_ARB;
            case PF_FLOAT32_GR:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_LUMINANCE_ALPHA32F_ARB;
            case PF_FLOAT32_RGB:
                throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_RGB32F_ARB;
            case PF_FLOAT32_RGBA:
                //return GLES20.GL_RGBA32F_ARB;
            case PF_SHORT_RGBA:
                //return GLES20.GL_RGBA16;
            case PF_SHORT_RGB:
                //return GLES20.GL_RGB16;
            case PF_SHORT_GR:
                //return GLES20.GL_LUMINANCE16_ALPHA16;
            case PF_DXT1:
                if (hwGamma)
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                    //return GLES20.GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT1_EXT;
                else
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
            case PF_DXT3:
                if (hwGamma)
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                    //return GLES20.GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT3_EXT;
                else
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
            case PF_DXT5:
                if (hwGamma)
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                    //return GLES20.GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT;
                else
                    throw new IllegalArgumentException("Pixel format not supported int GLES20");
                //return GLES20.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
            default:
                return GL20.GL_NONE;
        }
    }

    public static int getClosestGLInternalFormat(PixelFormat mFormat, boolean hwGamma) {
        int format = getGLInternalFormat(mFormat, hwGamma);
        if (format == GL20.GL_NONE) {
            if (hwGamma)
                return GL20.GL_RGBA;
                //return GL20.GL_SRGB8;
            else
                return GL20.GL_RGBA;
        } else
            return format;
    }

    public static PixelFormat getClosestOGREFormat(int fmt) {
        switch (fmt) {
            case GL20.GL_LUMINANCE:
                return PixelFormat.PF_L8;
            case GL20.GL_ALPHA:
                return PixelFormat.PF_A8;
            case GL20.GL_LUMINANCE_ALPHA:
                return PixelFormat.PF_BYTE_LA;
            case GL20.GL_RGB565:
                return PixelFormat.PF_R5G6B5;
            case GL20.GL_RGB:
                return PixelFormat.PF_X8R8G8B8;
            case GL20.GL_RGBA:
                return PixelFormat.PF_A8R8G8B8;
            case GL20.GL_RGBA4:
                return PixelFormat.PF_A4R4G4B4;
            case GL20.GL_RGB5_A1:
                return PixelFormat.PF_A1R5G5B5;
            default:
                return PixelFormat.PF_A8R8G8B8;
        }
    }

    public static int getMaxMipmaps(int width, int height, int depth, PixelFormat format) {
        int count = 0;
        do {
            if (width > 1) width = width / 2;
            if (height > 1) height = height / 2;
            if (depth > 1) depth = depth / 2;
			/*
			NOT needed, compressed formats will have mipmaps up to 1x1
			if(PixelUtil::isValidExtent(width, height, depth, format))
				count ++;
			else
				break;
			*/

            count++;
        } while (!(width == 1 && height == 1 && depth == 1));

        return count;
    }

    public static int optionalPO2(int value) {
        if (ENG_RenderRoot.getRenderRoot().getRenderSystem().getCapabilities().
                hasCapability(Capabilities.RSC_NON_POWER_OF_2_TEXTURES)) {
            return value;
        } else {
            return ENG_Bitwise.firstPO2From(value);
        }
    }
}
