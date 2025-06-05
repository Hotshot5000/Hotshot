/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/15, 11:39 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nullrendersystem;

import headwayent.hotshotengine.renderer.ENG_HardwareBuffer;
import headwayent.hotshotengine.renderer.ENG_PixelUtil;
import headwayent.hotshotengine.renderer.ENG_Texture;
import headwayent.hotshotengine.renderer.ENG_TextureManager;

/**
 * Created by sebas on 18.11.2015.
 */
public class NullTextureManager extends ENG_TextureManager {
    @Override
    public ENG_PixelUtil.PixelFormat getNativeFormat(ENG_Texture.TextureType ttype, ENG_PixelUtil.PixelFormat format, int usage) {
        return null;
    }

    @Override
    public boolean isHardwareFilteringSupported(ENG_Texture.TextureType ttype, ENG_PixelUtil.PixelFormat format, ENG_HardwareBuffer.Usage usage, boolean preciseFormatOnly) {
        return false;
    }

    @Override
    public ENG_Texture createImpl(String name) {
        return null;
    }
}
