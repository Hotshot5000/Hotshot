/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_Common {

    // Moved to ENG_SceneManager.
//    public enum SceneMemoryMgrTypes
//    {
//        SCENE_DYNAMIC(0),
//        SCENE_STATIC(1);
//
//        private final byte pos;
//
//        SceneMemoryMgrTypes(int i) {
//            pos = (byte) i;
//        }
//
//        public byte getPos() {
//            return pos;
//        }
//    }

    /**
     * Comparison functions used for the depth/stencil buffer operations and
     * others.
     */
    public enum CompareFunction {
        CMPF_ALWAYS_FAIL,
        CMPF_ALWAYS_PASS,
        CMPF_LESS,
        CMPF_LESS_EQUAL,
        CMPF_EQUAL,
        CMPF_NOT_EQUAL,
        CMPF_GREATER_EQUAL,
        CMPF_GREATER;

        public static CompareFunction getCompareFunction(int i) {
            switch (i) {
                case 0:
                    return CMPF_ALWAYS_FAIL;
                case 1:
                    return CMPF_ALWAYS_PASS;
                case 2:
                    return CMPF_LESS;
                case 3:
                    return CMPF_LESS_EQUAL;
                case 4:
                    return CMPF_EQUAL;
                case 5:
                    return CMPF_NOT_EQUAL;
                case 6:
                    return CMPF_GREATER_EQUAL;
                case 7:
                    return CMPF_GREATER;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid compare function");
            }
        }
    }

    /**
     * High-level filtering options providing shortcuts to settings the
     * minification, magnification and mip filters.
     */
    public enum TextureFilterOptions {
        /// Equal to: min=FO_POINT, mag=FO_POINT, mip=FO_NONE
        TFO_NONE,
        /// Equal to: min=FO_LINEAR, mag=FO_LINEAR, mip=FO_POINT
        TFO_BILINEAR,
        /// Equal to: min=FO_LINEAR, mag=FO_LINEAR, mip=FO_LINEAR
        TFO_TRILINEAR,
        /// Equal to: min=FO_ANISOTROPIC, max=FO_ANISOTROPIC, mip=FO_LINEAR
        TFO_ANISOTROPIC
    }

    public enum FilterType {
        /// The filter used when shrinking a texture
        FT_MIN,
        /// The filter used when magnifying a texture
        FT_MAG,
        /// The filter used when determining the mipmap
        FT_MIP;

        public static FilterType getFilterType(int i) {
            switch (i) {
                case 0:
                    return FT_MIN;
                case 1:
                    return FT_MAG;
                case 2:
                    return FT_MIP;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid filter type");
            }
        }
    }

    /**
     * Filtering options for textures / mipmaps.
     */
    public enum FilterOptions {
        /// No filtering, used for FILT_MIP to turn off mipmapping
        FO_NONE,
        /// Use the closest pixel
        FO_POINT,
        /// Average of a 2x2 pixel area, denotes bilinear for MIN and MAG, trilinear for MIP
        FO_LINEAR,
        /// Similar to FO_LINEAR, but compensates for the angle of the texture plane
        FO_ANISOTROPIC;

        public static FilterOptions getFilterOptions(int i) {
            switch (i) {
                case 0:
                    return FO_NONE;
                case 1:
                    return FO_POINT;
                case 2:
                    return FO_LINEAR;
                case 3:
                    return FO_ANISOTROPIC;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid filter option");
            }
        }
    }

    /**
     * Light shading modes.
     */
    public enum ShadeOptions {
        SO_FLAT,
        SO_GOURAUD,
        SO_PHONG
    }

    /**
     * Fog modes.
     */
    public enum FogMode {
        /// No fog. Duh.
        FOG_NONE,
        /// Fog density increases  exponentially from the camera (fog = 1/e^(distance * density))
        FOG_EXP,
        /// Fog density increases at the square of FOG_EXP, i.e. even quicker (fog = 1/e^(distance * density)^2)
        FOG_EXP2,
        /// Fog density increases linearly between the start and end distances
        FOG_LINEAR
    }

    /**
     * Hardware culling modes based on vertex winding.
     * This setting applies to how the hardware API culls triangles it is sent.
     */
    public enum CullingMode {
        /// Hardware never culls triangles and renders everything it receives.
        CULL_NONE(1),
        /// Hardware culls triangles whose vertices are listed clockwise in the view (default).
        CULL_CLOCKWISE(2),
        /// Hardware culls triangles whose vertices are listed anticlockwise in the view.
        CULL_ANTICLOCKWISE(3);

        private final int mode;

        CullingMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }

        public static CullingMode getCullingMode(int i) {
            switch (i) {
                case 1:
                    return CULL_NONE;
                case 2:
                    return CULL_CLOCKWISE;
                case 3:
                    return CULL_ANTICLOCKWISE;
                default:
                    throw new IllegalArgumentException("Culling mode " + i +
                            " is not supported");
            }
        }
    }

    /**
     * Manual culling modes based on vertex normals.
     * This setting applies to how the software culls triangles before sending them to the
     * hardware API. This culling mode is used by scene managers which choose to implement it -
     * normally those which deal with large amounts of fixed world geometry which is often
     * planar (software culling movable variable geometry is expensive).
     */
    public enum ManualCullingMode {
        /// No culling so everything is sent to the hardware.
        MANUAL_CULL_NONE(1),
        /// Cull triangles whose normal is pointing away from the camera (default).
        MANUAL_CULL_BACK(2),
        /// Cull triangles whose normal is pointing towards the camera.
        MANUAL_CULL_FRONT(3);

        private final int mode;

        ManualCullingMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }

    /**
     * Enumerates the wave types usable with the Ogre engine.
     */
    public enum WaveformType {
        /// Standard sine wave which smoothly changes from low to high and back again.
        WFT_SINE,
        /// An angular wave with a constant increase / decrease speed with pointed peaks.
        WFT_TRIANGLE,
        /// Half of the time is spent at the min, half at the max with instant transition between.
        WFT_SQUARE,
        /// Gradual steady increase from min to max over the period with an instant return to min at the end.
        WFT_SAWTOOTH,
        /// Gradual steady decrease from max to min over the period, with an instant return to max at the end.
        WFT_INVERSE_SAWTOOTH,
        /// Pulse Width Modulation. Works like WFT_SQUARE, except the high to low transition is controlled by duty cycle.
        /// With a duty cycle of 50% (0.5) will give the same output as WFT_SQUARE.
        WFT_PWM
    }

    /**
     * The polygon mode to use when rasterising.
     */
    public enum PolygonMode {
        /// Only points are rendered.
        PM_POINTS(1),
        /// Wireframe models are rendered.
        PM_WIREFRAME(2),
        /// Solid polygons are rendered.
        PM_SOLID(3);

        private final int mode;

        PolygonMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }

    /**
     * An enumeration of broad shadow techniques
     */
    public enum ShadowTechnique {
        /**
         * No shadows
         */
        SHADOWTYPE_NONE(0x00),
        /**
         * Mask for additive shadows (not for direct use, use  SHADOWTYPE_ enum instead)
         */
        SHADOWDETAILTYPE_ADDITIVE(0x01),
        /**
         * Mask for modulative shadows (not for direct use, use  SHADOWTYPE_ enum instead)
         */
        SHADOWDETAILTYPE_MODULATIVE(0x02),
        /**
         * Mask for integrated shadows (not for direct use, use SHADOWTYPE_ enum instead)
         */
        SHADOWDETAILTYPE_INTEGRATED(0x04),
        /**
         * Mask for stencil shadows (not for direct use, use  SHADOWTYPE_ enum instead)
         */
        SHADOWDETAILTYPE_STENCIL(0x10),
        /**
         * Mask for texture shadows (not for direct use, use  SHADOWTYPE_ enum instead)
         */
        SHADOWDETAILTYPE_TEXTURE(0x20),

        /**
         * Stencil shadow technique which renders all shadow volumes as
         * a modulation after all the non-transparent areas have been
         * rendered. This technique is considerably less fillrate intensive
         * than the additive stencil shadow approach when there are multiple
         * lights, but is not an accurate model.
         */
        SHADOWTYPE_STENCIL_MODULATIVE(0x12),
        /**
         * Stencil shadow technique which renders each light as a separate
         * additive pass to the scene. This technique can be very fillrate
         * intensive because it requires at least 2 passes of the entire
         * scene, more if there are multiple lights. However, it is a more
         * accurate model than the modulative stencil approach and this is
         * especially apparent when using coloured lights or bump mapping.
         */
        SHADOWTYPE_STENCIL_ADDITIVE(0x11),
        /**
         * Texture-based shadow technique which involves a monochrome render-to-texture
         * of the shadow caster and a projection of that texture onto the
         * shadow receivers as a modulative pass.
         */
        SHADOWTYPE_TEXTURE_MODULATIVE(0x22),

        /**
         * Texture-based shadow technique which involves a render-to-texture
         * of the shadow caster and a projection of that texture onto the
         * shadow receivers, built up per light as additive passes.
         * This technique can be very fillrate intensive because it requires numLights + 2
         * passes of the entire scene. However, it is a more accurate model than the
         * modulative approach and this is especially apparent when using coloured lights
         * or bump mapping.
         */
        SHADOWTYPE_TEXTURE_ADDITIVE(0x21),

        /**
         * Texture-based shadow technique which involves a render-to-texture
         * of the shadow caster and a projection of that texture on to the shadow
         * receivers, with the usage of those shadow textures completely controlled
         * by the materials of the receivers.
         * This technique is easily the most flexible of all techniques because
         * the material author is in complete control over how the shadows are
         * combined with regular rendering. It can perform shadows as accurately
         * as SHADOWTYPE_TEXTURE_ADDITIVE but more efficiently because it requires
         * less passes. However it also requires more expertise to use, and
         * in almost all cases, shader capable hardware to really use to the full.
         *
         * @note The 'additive' part of this mode means that the colour of
         * the rendered shadow texture is by default plain black. It does
         * not mean it does the adding on your receivers automatically though, how you
         * use that result is up to you.
         */
        SHADOWTYPE_TEXTURE_ADDITIVE_INTEGRATED(0x25),
        /**
         * Texture-based shadow technique which involves a render-to-texture
         * of the shadow caster and a projection of that texture on to the shadow
         * receivers, with the usage of those shadow textures completely controlled
         * by the materials of the receivers.
         * This technique is easily the most flexible of all techniques because
         * the material author is in complete control over how the shadows are
         * combined with regular rendering. It can perform shadows as accurately
         * as SHADOWTYPE_TEXTURE_ADDITIVE but more efficiently because it requires
         * less passes. However it also requires more expertise to use, and
         * in almost all cases, shader capable hardware to really use to the full.
         *
         * @note The 'modulative' part of this mode means that the colour of
         * the rendered shadow texture is by default the 'shadow colour'. It does
         * not mean it modulates on your receivers automatically though, how you
         * use that result is up to you.
         */
        SHADOWTYPE_TEXTURE_MODULATIVE_INTEGRATED(0x26);

        private final int technique;

        ShadowTechnique(int technique) {
            this.technique = technique;
        }

        public int getTechnique() {
            return technique;
        }
    }

    /**
     * An enumeration describing which material properties should track the vertex colours
     */
    //typedef int TrackVertexColourType;
    public enum TrackVertexColourEnum {
        TVC_NONE(0x0),
        TVC_AMBIENT(0x1),
        TVC_DIFFUSE(0x2),
        TVC_SPECULAR(0x4),
        TVC_EMISSIVE(0x8);

        private final int colourEnum;

        TrackVertexColourEnum(int colourEnum) {
            this.colourEnum = colourEnum;
        }

        public int getColourEnum() {
            return colourEnum;
        }
    }

    /**
     * Sort mode for billboard-set and particle-system
     */
    public enum SortMode {
        /**
         * Sort by direction of the camera
         */
        SM_DIRECTION,
        /**
         * Sort by distance from the camera
         */
        SM_DISTANCE
    }

    /**
     * Defines the frame buffer types.
     */
    public enum FrameBufferType {
        FBT_COLOUR(0x1),
        FBT_DEPTH(0x2),
        FBT_STENCIL(0x4);

        private final int type;

        FrameBufferType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    /// Generic result of clipping
    public enum ClipResult {
        /// Nothing was clipped
        CLIPPED_NONE,
        /// Partially clipped
        CLIPPED_SOME,
        /// Everything was clipped away
        CLIPPED_ALL
    }
}
