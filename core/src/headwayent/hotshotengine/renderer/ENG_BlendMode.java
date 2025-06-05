/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

public class ENG_BlendMode {

    /**
     * Type of texture blend mode.
     */
    public enum LayerBlendType {
        LBT_COLOUR,
        LBT_ALPHA;

        public static LayerBlendType getLayerBlendType(int i) {
            switch (i) {
                case 0:
                    return LBT_COLOUR;
                case 1:
                    return LBT_ALPHA;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid layer blend type");
            }
        }
    }

    /**
     * List of valid texture blending operations, for use with TextureUnitState::setColourOperation.
     *
     * @remarks This list is a more limited list than LayerBlendOperationEx because it only
     * includes operations that are supportable in both multipass and multitexture
     * rendering and thus provides automatic fallback if multitexture hardware
     * is lacking or insufficient.
     */
    public enum LayerBlendOperation {
        /// Replace all colour with texture with no adjustment
        LBO_REPLACE,
        /// Add colour components together.
        LBO_ADD,
        /// Multiply colour components together.
        LBO_MODULATE,
        /// Blend based on texture alpha
        LBO_ALPHA_BLEND;

        public static LayerBlendOperation getLayerBlendOperation(int i) {
            switch (i) {
                case 0:
                    return LBO_REPLACE;
                case 1:
                    return LBO_ADD;
                case 2:
                    return LBO_MODULATE;
                case 3:
                    return LBO_ALPHA_BLEND;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid layer blend operation");
            }
        }

    }

    /**
     * Expert list of valid texture blending operations, for use with TextureUnitState::setColourOperationEx
     * and TextureUnitState::setAlphaOperation, and internally in the LayerBlendModeEx class. It's worth
     * noting that these operations are for blending <i>between texture layers</i> and not between rendered objects
     * and the existing scene. Because all of these modes are only supported in multitexture hardware it may be
     * required to set up a fallback operation where this hardware is not available.
     */
    public enum LayerBlendOperationEx {
        /// use source1 without modification
        LBX_SOURCE1,
        /// use source2 without modification
        LBX_SOURCE2,
        /// multiply source1 and source2 together
        LBX_MODULATE,
        /// as LBX_MODULATE but brighten afterwards (x2)
        LBX_MODULATE_X2,
        /// as LBX_MODULATE but brighten more afterwards (x4)
        LBX_MODULATE_X4,
        /// add source1 and source2 together
        LBX_ADD,
        /// as LBX_ADD, but subtract 0.5 from the result
        LBX_ADD_SIGNED,
        /// as LBX_ADD, but subtract product from the sum
        LBX_ADD_SMOOTH,
        /// subtract source2 from source1
        LBX_SUBTRACT,
        /// use interpolated alpha value from vertices to scale source1, then add source2 scaled by (1-alpha)
        LBX_BLEND_DIFFUSE_ALPHA,
        /// as LBX_BLEND_DIFFUSE_ALPHA, but use alpha from texture
        LBX_BLEND_TEXTURE_ALPHA,
        /// as LBX_BLEND_DIFFUSE_ALPHA, but use current alpha from previous stages
        LBX_BLEND_CURRENT_ALPHA,
        /// as LBX_BLEND_DIFFUSE_ALPHA but use a constant manual blend value (0.0-1.0)
        LBX_BLEND_MANUAL,
        /// dot product of color1 and color2
        LBX_DOTPRODUCT,
        /// use interpolated color values from vertices to scale source1, then add source2 scaled by (1-color)
        LBX_BLEND_DIFFUSE_COLOUR;

        public static LayerBlendOperationEx getLayerBlendOperationEx(int i) {
            switch (i) {
                case 0:
                    return LBX_SOURCE1;
                case 1:
                    return LBX_SOURCE2;
                case 2:
                    return LBX_MODULATE;
                case 3:
                    return LBX_MODULATE_X2;
                case 4:
                    return LBX_MODULATE_X4;
                case 5:
                    return LBX_ADD;
                case 6:
                    return LBX_ADD_SIGNED;
                case 7:
                    return LBX_ADD_SMOOTH;
                case 8:
                    return LBX_SUBTRACT;
                case 9:
                    return LBX_BLEND_DIFFUSE_ALPHA;
                case 10:
                    return LBX_BLEND_TEXTURE_ALPHA;
                case 11:
                    return LBX_BLEND_CURRENT_ALPHA;
                case 12:
                    return LBX_BLEND_MANUAL;
                case 13:
                    return LBX_DOTPRODUCT;
                case 14:
                    return LBX_BLEND_DIFFUSE_COLOUR;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid layer blend operation ex");
            }
        }
    }

    /**
     * List of valid sources of values for blending operations used
     * in TextureUnitState::setColourOperation and TextureUnitState::setAlphaOperation,
     * and internally in the LayerBlendModeEx class.
     */
    public enum LayerBlendSource {
        /// the colour as built up from previous stages
        LBS_CURRENT,
        /// the colour derived from the texture assigned to this layer
        LBS_TEXTURE,
        /// the interpolated diffuse colour from the vertices
        LBS_DIFFUSE,
        /// the interpolated specular colour from the vertices
        LBS_SPECULAR,
        /// a colour supplied manually as a separate argument
        LBS_MANUAL;

        public static LayerBlendSource getLayerBlendSource(int i) {
            switch (i) {
                case 0:
                    return LBS_CURRENT;
                case 1:
                    return LBS_TEXTURE;
                case 2:
                    return LBS_DIFFUSE;
                case 3:
                    return LBS_SPECULAR;
                case 4:
                    return LBS_MANUAL;
                default:
                    throw new IllegalArgumentException("The value " + i +
                            " is not a valid layer blend source");
            }
        }
    }

    public static class LayerBlendModeEx {
        /// The type of blending (colour or alpha)
        public LayerBlendType blendType;
        /// The operation to be applied
        public LayerBlendOperationEx operation;
        /// The first source of colour/alpha
        public LayerBlendSource source1;
        /// The second source of colour/alpha
        public LayerBlendSource source2;

        /// Manual colour value for manual source1
        public ENG_ColorValue colourArg1;
        /// Manual colour value for manual source2
        public ENG_ColorValue colourArg2;
        /// Manual alpha value for manual source1
        public float alphaArg1;
        /// Manual alpha value for manual source2
        public float alphaArg2;
        /// Manual blending factor
        public float factor;

        public void set(LayerBlendModeEx mode) {
            blendType = mode.blendType;
            operation = mode.operation;
            source1 = mode.source1;
            source2 = mode.source2;
            if (colourArg1 != null && mode.colourArg1 != null) {
                colourArg1.set(mode.colourArg1);
            }
            if (colourArg2 != null && mode.colourArg2 != null) {
                colourArg2.set(mode.colourArg2);
            }
            alphaArg1 = mode.alphaArg1;
            alphaArg2 = mode.alphaArg2;
            factor = mode.factor;
        }

        public LayerBlendModeEx() {

        }

        public LayerBlendModeEx(int type, int opEx, int src1, int src2) {
            set(type, opEx, src1, src2);
        }

        public void set(int type, int opEx, int src1, int src2) {
            blendType = LayerBlendType.getLayerBlendType(type);
            operation = LayerBlendOperationEx.getLayerBlendOperationEx(opEx);
            source1 = LayerBlendSource.getLayerBlendSource(src1);
            source2 = LayerBlendSource.getLayerBlendSource(src2);
        }

        public boolean equals(LayerBlendModeEx rhs) {
            if (blendType != rhs.blendType) return false;

            if (blendType == LayerBlendType.LBT_COLOUR) {

                if (operation == rhs.operation &&
                        source1 == rhs.source1 &&
                        source2 == rhs.source2 &&
                        colourArg1 == rhs.colourArg1 &&
                        colourArg2 == rhs.colourArg2 &&
                        factor == rhs.factor) {
                    return true;
                }
            } else // if (blendType == LBT_ALPHA)
            {
                if (operation == rhs.operation &&
                        source1 == rhs.source1 &&
                        source2 == rhs.source2 &&
                        alphaArg1 == rhs.alphaArg1 &&
                        alphaArg2 == rhs.alphaArg2 &&
                        factor == rhs.factor) {
                    return true;
                }
            }
            return false;
        }

        public boolean notEquals(LayerBlendModeEx rhs) {
            return !this.equals(rhs);
        }
    }

    /**
     * Types of blending that you can specify between an object and the existing contents of the scene.
     *
     * @remarks As opposed to the LayerBlendType, which classifies blends between texture layers, these blending
     * types blend between the output of the texture units and the pixels already in the viewport,
     * allowing for object transparency, glows, etc.
     * @par These types are provided to give quick and easy access to common effects. You can also use
     * the more manual method of supplying source and destination blending factors.
     * See Material::setSceneBlending for more details.
     * @see Material::setSceneBlending
     */
    public enum SceneBlendType {
        /// Make the object transparent based on the final alpha values in the texture
        SBT_TRANSPARENT_ALPHA,
        /// Make the object transparent based on the colour values in the texture (brighter = more opaque)
        SBT_TRANSPARENT_COLOUR,
        /// Add the texture values to the existing scene content
        SBT_ADD,
        /// Multiply the 2 colours together
        SBT_MODULATE,
        /// The default blend mode where source replaces destination
        SBT_REPLACE
        // TODO : more
    }

    /**
     * Blending factors for manually blending objects with the scene. If there isn't a predefined
     * SceneBlendType that you like, then you can specify the blending factors directly to affect the
     * combination of object and the existing scene. See Material::setSceneBlending for more details.
     */
    public enum SceneBlendFactor {
        SBF_ONE,
        SBF_ZERO,
        SBF_DEST_COLOUR,
        SBF_SOURCE_COLOUR,
        SBF_ONE_MINUS_DEST_COLOUR,
        SBF_ONE_MINUS_SOURCE_COLOUR,
        SBF_DEST_ALPHA,
        SBF_SOURCE_ALPHA,
        SBF_ONE_MINUS_DEST_ALPHA,
        SBF_ONE_MINUS_SOURCE_ALPHA;

        public static SceneBlendFactor getSceneBlendFactor(int i) {
            switch (i) {
                case 0:
                    return SBF_ONE;
                case 1:
                    return SBF_ZERO;
                case 2:
                    return SBF_DEST_COLOUR;
                case 3:
                    return SBF_SOURCE_COLOUR;
                case 4:
                    return SBF_ONE_MINUS_DEST_COLOUR;
                case 5:
                    return SBF_ONE_MINUS_SOURCE_COLOUR;
                case 6:
                    return SBF_DEST_ALPHA;
                case 7:
                    return SBF_SOURCE_ALPHA;
                case 8:
                    return SBF_ONE_MINUS_DEST_ALPHA;
                case 9:
                    return SBF_ONE_MINUS_SOURCE_ALPHA;
                default:
                    throw new IllegalArgumentException("Scene blend factor " + i +
                            " is not a valid value");
            }
        }

    }

    /**
     * Blending operations controls how objects are blended into the scene. The default operation
     * is add (+) but by changing this you can change how drawn objects are blended into the
     * existing scene.
     */
    public enum SceneBlendOperation {
        SBO_ADD,
        SBO_SUBTRACT,
        SBO_REVERSE_SUBTRACT,
        SBO_MIN,
        SBO_MAX;

        public static SceneBlendOperation getSceneBlendOperation(int i) {
            switch (i) {
                case 0:
                    return SBO_ADD;
                case 1:
                    return SBO_SUBTRACT;
                case 2:
                    return SBO_REVERSE_SUBTRACT;
                case 3:
                    return SBO_MIN;
                case 4:
                    return SBO_MAX;
                default:
                    throw new IllegalArgumentException("Scene blend operation " +
                            i + "is not a valid value");
            }
        }
    }
}
