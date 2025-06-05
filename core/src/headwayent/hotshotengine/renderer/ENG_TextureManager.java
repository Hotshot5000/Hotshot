/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/1/17, 6:32 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_ByteBufferInputStream;
import headwayent.hotshotengine.renderer.ENG_HardwareBuffer.Usage;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureMipmap;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureType;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureUsage;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class ENG_TextureManager {

    protected final HashMap<String, ENG_Texture> resources = new HashMap<>();
    protected short preferredIntegerBitDepth;
    protected short preferredFloatBitDepth;
    protected int defaultNumMipmaps = TextureMipmap.MIP_UNLIMITED.getMipmap();
//    private static ENG_TextureManager textureManager;

    public ENG_TextureManager() {
//        if (textureManager == null) {
//            textureManager = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        textureManager = this;
    }

    public static ENG_TextureManager getSingleton() {
//        if (MainActivity.isDebugmode() && (textureManager == null)) {
//            throw new NullPointerException();
//        }
//        return textureManager;
        return MainApp.getGame().getRenderRoot().getActiveRenderSystem().getTextureManager();
    }

    public ENG_Texture getByName(String name) {
        return resources.get(name);
    }

    public void destroyTexture(String name) {
        destroyTexture(name, false);
    }

    public void destroyTexture(String name, boolean skipGLDelete) {
        ENG_Texture texture = resources.remove(name);
        if (texture != null) {
            texture.unloadImpl(skipGLDelete);
        } else {
            throw new IllegalArgumentException(name +
                    " is not a valid texture name");
        }
    }

    public void remove(String name) {
        destroyTexture(name);
    }

    public void destroyAllTextures() {
        destroyAllTextures(false);
    }

    public void destroyAllTextures(boolean skipGLDelete) {
        for (ENG_Texture texture : resources.values()) {
            texture.unloadImpl(skipGLDelete);
        }
        resources.clear();
    }

    public ENG_Texture createOrRetrieve(String name, int usage) {
        return createOrRetrieve(name, TextureType.TEX_TYPE_2D,
                TextureMipmap.MIP_DEFAULT.getMipmap(),
                1.0f, false, PixelFormat.PF_UNKNOWN, usage, false);
    }

    public ENG_Texture createOrRetrieve(String name,
                                        TextureType texType, int numMipmaps,
                                        float gamma, boolean isAlpha, PixelFormat desiredFormat,
                                        int usage,
                                        boolean hwGammaCorrection) {
        ENG_Texture tex = resources.get(name);
        if (tex == null) {
            tex = createImpl(name);
            resources.put(name, tex);
            tex.setTextureType(texType);
            tex.setNumMipmaps((numMipmaps ==
                    TextureMipmap.MIP_DEFAULT.getMipmap()) ?
                    defaultNumMipmaps : numMipmaps);
            tex.setGamma(gamma);
            tex.setTreatLuminanceAsAlpha(isAlpha);
            tex.setFormat(desiredFormat);
            tex.setHardwareGammaEnabled(hwGammaCorrection);
            tex.setUsage(usage);
        }
        return tex;
    }

    public ENG_Texture prepare(String name, TextureType texType, int numMipmaps,
                               float gamma, boolean isAlpha, PixelFormat desiredFormat,
                               int usage,
                               boolean hwGammaCorrection) {
        ENG_Texture createOrRetrieve = createOrRetrieve(
                name, texType, numMipmaps, gamma, isAlpha,
                desiredFormat, usage, hwGammaCorrection);
        createOrRetrieve.prepareImpl();
        return createOrRetrieve;
    }

    public ENG_Texture load(String name) {
        return load(name, TextureUsage.TU_DEFAULT.getUsage());
    }

    public ENG_Texture load(String name, int usage) {
        return load(name, TextureType.TEX_TYPE_2D,
                TextureMipmap.MIP_DEFAULT.getMipmap(),
                1.0f, false, PixelFormat.PF_UNKNOWN, usage, false);
    }

    public ENG_Texture load(String name, TextureType texType, int numMipmaps,
                            float gamma, boolean isAlpha, PixelFormat desiredFormat,
                            int usage,
                            boolean hwGammaCorrection) {
        //Dont load here. We use _loadImages() upper in the hierarchy.
        //	createOrRetrieve.loadImpl();
        return createOrRetrieve(
                name, texType, numMipmaps, gamma, isAlpha,
                desiredFormat, usage, hwGammaCorrection);
    }

    public ENG_Texture loadImage(String name, ENG_Image img) {
        return loadImage(name, img, TextureType.TEX_TYPE_2D,
                TextureMipmap.MIP_DEFAULT.getMipmap(),
                1.0f, false, PixelFormat.PF_UNKNOWN, false);
    }

    public ENG_Texture loadImage(String name, ENG_Image img,
                                 TextureType texType, int numMipmaps,
                                 float gamma, boolean isAlpha, PixelFormat desiredFormat,
                                 boolean hwGammaCorrection) {
        ENG_Texture tex = createImpl(name);
        ENG_Texture put = resources.put(name, tex);
        if (put != null) {
            throw new IllegalArgumentException(name + " is already a texture");
        }
        tex.setTextureType(texType);
        tex.setNumMipmaps((numMipmaps == TextureMipmap.MIP_DEFAULT.getMipmap()) ?
                defaultNumMipmaps : numMipmaps);
        tex.setGamma(gamma);
        tex.setTreatLuminanceAsAlpha(isAlpha);
        tex.setFormat(desiredFormat);
        tex.setHardwareGammaEnabled(hwGammaCorrection);
        tex.loadImage(img);
        return tex;
    }

    /**
     * For GIWS interface with CEGUI. Don't use otherwise!
     *
     * @param name
     * @param is
     * @param width
     * @param height
     * @param format
     * @param texType
     * @param numMipmaps
     * @param gamma
     * @param hwgamma
     * @return
     */
    public ENG_Texture loadRawData(String name, ENG_ByteBufferInputStream is,
                                   int width, int height, int format, int texType,
                                   int numMipmaps, float gamma, boolean hwgamma) {
        return loadRawData(name, is, width, height,
                PixelFormat.getPixelFormat(format),
                TextureType.getTextureType(texType),
                numMipmaps, gamma, hwgamma);
    }

    /**
     * For GIWS interface with CEGUI. Don't use otherwise!
     *
     * @param name
     * @param is
     * @param width
     * @param height
     * @param format
     * @param texType
     * @param numMipmaps
     * @param gamma
     * @param hwgamma
     * @return
     */
    public ENG_Texture loadRawData(String name, ENG_ByteBufferInputStream is,
                                   int width, int height, PixelFormat format, TextureType texType,
                                   int numMipmaps, float gamma, boolean hwgamma) {
        ENG_Texture tex = createImpl(name);
        ENG_Texture put = resources.put(name, tex);
        if (put != null) {
            throw new IllegalArgumentException(name + " is already a texture");
        }
        tex.setTextureType(texType);
        tex.setNumMipmaps((numMipmaps == TextureMipmap.MIP_DEFAULT.getMipmap()) ?
                defaultNumMipmaps : numMipmaps);
        tex.setGamma(gamma);

        tex.setHardwareGammaEnabled(hwgamma);
        tex.loadRawData(is.getByteBufferInputStream(), width, height, format);
        return tex;
    }

    public ENG_Texture loadRawData(String name, ByteBuffer is,
                                   int width, int height, int format, int texType,
                                   int numMipmaps, float gamma, boolean hwgamma) {
        return loadRawData(name, is, width, height,
                PixelFormat.getPixelFormat(format),
                TextureType.getTextureType(texType),
                numMipmaps, gamma, hwgamma);
    }

    public ENG_Texture loadRawData(String name, ByteBuffer is,
                                   int width, int height, PixelFormat format, TextureType texType,
                                   int numMipmaps, float gamma, boolean hwgamma) {
        ENG_Texture tex = createImpl(name);
        ENG_Texture put = resources.put(name, tex);
        if (put != null) {
            throw new IllegalArgumentException(name + " is already a texture");
        }
        tex.setTextureType(texType);
        tex.setNumMipmaps((numMipmaps == TextureMipmap.MIP_DEFAULT.getMipmap()) ?
                defaultNumMipmaps : numMipmaps);
        tex.setGamma(gamma);

        tex.setHardwareGammaEnabled(hwgamma);
        byte[] b = new byte[is.remaining()];
        is.get(b);
        is.flip();
        tex.loadRawData(new ByteArrayInputStream(b), width, height, format);
        return tex;
    }

    /**
     * For GIWS only
     *
     * @param name
     * @param texType
     * @param width
     * @param height
     * @param numMipmaps
     * @param desiredFormat
     * @return
     */
    public ENG_Texture createManual(String name, int texType,
                                    int width, int height,
                                    int numMipmaps, int desiredFormat) {
        return createManual(name,
                TextureType.getTextureType(texType),
                width, height, 1, numMipmaps,
                PixelFormat.getPixelFormat(desiredFormat),
                TextureUsage.TU_DEFAULT.getUsage(), false, 0, "");
    }

    public ENG_Texture createManual(String name, TextureType texType,
                                    int width, int height, int depth,
                                    int numMipmaps, PixelFormat desiredFormat) {
        return createManual(name, texType, width, height, depth, numMipmaps,
                desiredFormat, TextureUsage.TU_DEFAULT.getUsage(), false, 0, "");
    }

    public ENG_Texture createManual(String name, TextureType texType,
                                    int width, int height, int depth,
                                    int numMipmaps, PixelFormat desiredFormat, int usage,
                                    boolean hwGammaCorrection, int fsaa, String fsaaHint) {
        ENG_Texture tex = createImpl(name);
        ENG_Texture put = resources.put(name, tex);
        if (put != null) {
            throw new IllegalArgumentException(name + " is already a texture");
        }
        tex.setTextureType(texType);
        tex.setWidth(width);
        tex.setHeight(height);
        tex.setDepth(depth);
        tex.setNumMipmaps((numMipmaps == TextureMipmap.MIP_DEFAULT.getMipmap()) ?
                defaultNumMipmaps : numMipmaps);
        tex.setFormat(desiredFormat);
        tex.setUsage(usage);
        tex.setHardwareGammaEnabled(hwGammaCorrection);
        tex.setFSAA(fsaa, fsaaHint);
        tex.createInternalResources();
        return tex;
    }

    public abstract PixelFormat getNativeFormat(TextureType ttype, PixelFormat format,
                                                int usage);

    public abstract boolean isHardwareFilteringSupported(TextureType ttype,
                                                         PixelFormat format, Usage usage, boolean preciseFormatOnly);

    public int getDefaultNumMipmaps() {
        return defaultNumMipmaps;
    }

    public void setDefaultNumMipmaps(int mips) {
        defaultNumMipmaps = mips;
    }

    public boolean isFormatSupported(TextureType ttype, PixelFormat format,
                                     Usage usage) {
        return (getNativeFormat(ttype, format, usage.getUsage()) == format);
    }

    /**
     * Only for GIWS
     *
     * @param ttype
     * @param format
     * @param usage
     * @return
     */
    public boolean isEquivalentFormatSupported(int ttype, int format,
                                               int usage) {
        return isEquivalentFormatSupported(TextureType.getTextureType(ttype),
                PixelFormat.getPixelFormat(format), Usage.getUsage(usage));
    }

    public boolean isEquivalentFormatSupported(TextureType ttype,
                                               PixelFormat format,
                                               Usage usage) {
        PixelFormat supportedFormat = getNativeFormat(
                ttype, format, usage.getUsage());
        return (ENG_PixelUtil.getNumElemBits(supportedFormat) >=
                ENG_PixelUtil.getNumElemBits(format));
    }

    public void setPreferredBitDepths(short integerBits, short floatBits,
                                      boolean reloadTextures) {
        preferredIntegerBitDepth = integerBits;
        preferredFloatBitDepth = floatBits;

        if (reloadTextures) {

            for (Entry<String, ENG_Texture> stringENG_textureEntry : resources.entrySet()) {
                stringENG_textureEntry.getValue().setDesiredBitDepths(integerBits, floatBits);
            }
        }
    }

    public void setPreferredIntegerBitDepths(short integerBits,
                                             boolean reloadTextures) {
        preferredIntegerBitDepth = integerBits;
        //preferredFloatBitDepth = floatBits;

        if (reloadTextures) {

            for (Entry<String, ENG_Texture> stringENG_textureEntry : resources.entrySet()) {
                stringENG_textureEntry.getValue().setDesiredIntegerBitDepth(integerBits);
            }
        }
    }

    public void setPreferredBitDepths(short floatBits,
                                      boolean reloadTextures) {
        //	preferredIntegerBitDepth = integerBits;
        preferredFloatBitDepth = floatBits;

        if (reloadTextures) {

            for (Entry<String, ENG_Texture> stringENG_textureEntry : resources.entrySet()) {
                stringENG_textureEntry.getValue().setDesiredFloatBitDepth(floatBits);
            }
        }
    }

    public abstract ENG_Texture createImpl(String name);

    /**
     * @return the preferredIntegerBitDepth
     */
    public short getPreferredIntegerBitDepth() {
        return preferredIntegerBitDepth;
    }

    /**
     * @return the preferredFloatBitDepth
     */
    public short getPreferredFloatBitDepth() {
        return preferredFloatBitDepth;
    }
}
