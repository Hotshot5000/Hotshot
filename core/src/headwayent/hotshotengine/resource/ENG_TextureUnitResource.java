/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:03 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_PixelUtil.PixelFormat;
import headwayent.hotshotengine.renderer.ENG_Texture.TextureType;
import headwayent.hotshotengine.renderer.ENG_TextureUnitState.TextureAddressingMode;

import java.util.ArrayList;

public class ENG_TextureUnitResource {

    public static final int MIPMAPS_UNLIMITED = -1;

    public enum CubeType {
        combinedUVW, separateUV
    }

    public enum BindingType {
        vertex, fragment
    }

    public enum ContentType {
        named, shadow, compositor
    }

    public enum FilterType {
        none, point, linear, anisotropic
    }

    public enum ColourOpType {
        replace, add, modulate, alpha_blend
    }

    public enum EnvMapType {
        off, spherical, planar, cubic_reflection, cubic_normal
    }

    public enum TextureUsage {
        TU_STATIC,
        TU_DYNAMIC,
        TU_WRITE_ONLY,
        TU_STATIC_WRITE_ONLY,
        TU_DYNAMIC_WRITE_ONLY,
        TU_DYNAMIC_WRITE_ONLY_DISCARDABLE,
        /// mipmaps will be automatically generated for this texture
        TU_AUTOMIPMAP,
        /// this texture will be a render target, i.e. used as a target for render to texture
        /// setting this flag will ignore all other texture usages except TU_AUTOMIPMAP
        TU_RENDERTARGET,
        /// default to automatic mipmap generation static textures
        TU_DEFAULT
    }

    public String name;
    public String textureAlias;
    public ArrayList<String> textureName = new ArrayList<>();
    public TextureType type;
    public int numMipmaps;
    public PixelFormat pf;
    public boolean gamma;
    public String animTexture;
    public int numFrames;
    public float duration;
    public String cubeName;
    public CubeType cubeType;
    public BindingType bindingType;
    public ContentType contentType;
    public String referencedCompositorName;
    public String referencedTextureName;
    public int mrtTextureIndex;
    public int texCoordSet;
    public TextureAddressingMode addresingMode;
    public ENG_ColorValue textureBorder;
    public FilterType filterTypeMin;
    public FilterType filterTypeMag;
    public FilterType filterTypeMip;
    public int maxAnisotropy;
    public int mipmapBias;
    public ColourOpType colourOpType;
    public EnvMapType envMapType;
    public boolean useShadowBuffer;
    public TextureUsage usage;
}
