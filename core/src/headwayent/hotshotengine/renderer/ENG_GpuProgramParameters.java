/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix3;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition.GpuParamVariability;
import headwayent.hotshotengine.renderer.nativeinterface.classwrappers.ENG_NativePointerWithSetter;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ENG_GpuProgramParameters implements ENG_NativePointerWithSetter {

    /**
     * Defines the types of automatically updated values that may be bound to GpuProgram
     * parameters, or used to modify parameters on a per-object basis.
     */
    public enum AutoConstantType {
        /// The current world matrix
        ACT_WORLD_MATRIX,
        /// The current world matrix, inverted
        ACT_INVERSE_WORLD_MATRIX,
        /**
         * Provides transpose of world matrix.
         * Equivalent to RenderMonkey's "WorldTranspose".
         */
        ACT_TRANSPOSE_WORLD_MATRIX,
        /// The current world matrix, inverted & transposed
        ACT_INVERSE_TRANSPOSE_WORLD_MATRIX,


        /// The current array of world matrices, as a 3x4 matrix, used for blending
        ACT_WORLD_MATRIX_ARRAY_3x4,
        /// The current array of world matrices, used for blending
        ACT_WORLD_MATRIX_ARRAY,

        /// The current view matrix
        ACT_VIEW_MATRIX,
        /// The current view matrix, inverted
        ACT_INVERSE_VIEW_MATRIX,
        /**
         * Provides transpose of view matrix.
         * Equivalent to RenderMonkey's "ViewTranspose".
         */
        ACT_TRANSPOSE_VIEW_MATRIX,
        /**
         * Provides inverse transpose of view matrix.
         * Equivalent to RenderMonkey's "ViewInverseTranspose".
         */
        ACT_INVERSE_TRANSPOSE_VIEW_MATRIX,


        /// The current projection matrix
        ACT_PROJECTION_MATRIX,
        /**
         * Provides inverse of projection matrix.
         * Equivalent to RenderMonkey's "ProjectionInverse".
         */
        ACT_INVERSE_PROJECTION_MATRIX,
        /**
         * Provides transpose of projection matrix.
         * Equivalent to RenderMonkey's "ProjectionTranspose".
         */
        ACT_TRANSPOSE_PROJECTION_MATRIX,
        /**
         * Provides inverse transpose of projection matrix.
         * Equivalent to RenderMonkey's "ProjectionInverseTranspose".
         */
        ACT_INVERSE_TRANSPOSE_PROJECTION_MATRIX,


        /// The current view & projection matrices concatenated
        ACT_VIEWPROJ_MATRIX,
        /**
         * Provides inverse of concatenated view and projection matrices.
         * Equivalent to RenderMonkey's "ViewProjectionInverse".
         */
        ACT_INVERSE_VIEWPROJ_MATRIX,
        /**
         * Provides transpose of concatenated view and projection matrices.
         * Equivalent to RenderMonkey's "ViewProjectionTranspose".
         */
        ACT_TRANSPOSE_VIEWPROJ_MATRIX,
        /**
         * Provides inverse transpose of concatenated view and projection matrices.
         * Equivalent to RenderMonkey's "ViewProjectionInverseTranspose".
         */
        ACT_INVERSE_TRANSPOSE_VIEWPROJ_MATRIX,


        /// The current world & view matrices concatenated
        ACT_WORLDVIEW_MATRIX,
        /// The current world & view matrices concatenated, then inverted
        ACT_INVERSE_WORLDVIEW_MATRIX,
        /**
         * Provides transpose of concatenated world and view matrices.
         * Equivalent to RenderMonkey's "WorldViewTranspose".
         */
        ACT_TRANSPOSE_WORLDVIEW_MATRIX,
        /// The current world & view matrices concatenated, then inverted & transposed
        ACT_INVERSE_TRANSPOSE_WORLDVIEW_MATRIX,
        /// view matrices.


        /// The current world, view & projection matrices concatenated
        ACT_WORLDVIEWPROJ_MATRIX,
        /**
         * Provides inverse of concatenated world, view and projection matrices.
         * Equivalent to RenderMonkey's "WorldViewProjectionInverse".
         */
        ACT_INVERSE_WORLDVIEWPROJ_MATRIX,
        /**
         * Provides transpose of concatenated world, view and projection matrices.
         * Equivalent to RenderMonkey's "WorldViewProjectionTranspose".
         */
        ACT_TRANSPOSE_WORLDVIEWPROJ_MATRIX,
        /**
         * Provides inverse transpose of concatenated world, view and projection
         * matrices. Equivalent to RenderMonkey's "WorldViewProjectionInverseTranspose".
         */
        ACT_INVERSE_TRANSPOSE_WORLDVIEWPROJ_MATRIX,


        /// render target related values
        /**
         * -1 if requires texture flipping, +1 otherwise. It's useful when you bypassed
         * projection matrix transform, still able use this value to adjust transformed y position.
         */
        ACT_RENDER_TARGET_FLIPPING,

        /**
         * -1 if the winding has been inverted (e.g. for reflections), +1 otherwise.
         */
        ACT_VERTEX_WINDING,

        /// Fog colour
        ACT_FOG_COLOUR,
        /// Fog params: density, linear start, linear end, 1/(end-start)
        ACT_FOG_PARAMS,


        /// Surface ambient colour, as set in Pass::setAmbient
        ACT_SURFACE_AMBIENT_COLOUR,
        /// Surface diffuse colour, as set in Pass::setDiffuse
        ACT_SURFACE_DIFFUSE_COLOUR,
        /// Surface specular colour, as set in Pass::setSpecular
        ACT_SURFACE_SPECULAR_COLOUR,
        /// Surface emissive colour, as set in Pass::setSelfIllumination
        ACT_SURFACE_EMISSIVE_COLOUR,
        /// Surface shininess, as set in Pass::setShininess
        ACT_SURFACE_SHININESS,


        /// The number of active light sources (better than gl_MaxLights)
        ACT_LIGHT_COUNT,


        /// The ambient light colour set in the scene
        ACT_AMBIENT_LIGHT_COLOUR,

        /// Light diffuse colour (index determined by setAutoConstant call)
        ACT_LIGHT_DIFFUSE_COLOUR,
        /// Light specular colour (index determined by setAutoConstant call)
        ACT_LIGHT_SPECULAR_COLOUR,
        /// Light attenuation parameters, Vector4(range, constant, linear, quadric)
        ACT_LIGHT_ATTENUATION,
        /**
         * Spotlight parameters, Vector4(innerFactor, outerFactor, falloff, isSpot)
         * innerFactor and outerFactor are cos(angle/2)
         * The isSpot parameter is 0.0f for non-spotlights, 1.0f for spotlights.
         * Also for non-spotlights the inner and outer factors are 1 and nearly 1 respectively
         */
        ACT_SPOTLIGHT_PARAMS,
        /// A light position in world space (index determined by setAutoConstant call)
        ACT_LIGHT_POSITION,
        /// A light position in object space (index determined by setAutoConstant call)
        ACT_LIGHT_POSITION_OBJECT_SPACE,
        /// A light position in view space (index determined by setAutoConstant call)
        ACT_LIGHT_POSITION_VIEW_SPACE,
        /// A light direction in world space (index determined by setAutoConstant call)
        ACT_LIGHT_DIRECTION,
        /// A light direction in object space (index determined by setAutoConstant call)
        ACT_LIGHT_DIRECTION_OBJECT_SPACE,
        /// A light direction in view space (index determined by setAutoConstant call)
        ACT_LIGHT_DIRECTION_VIEW_SPACE,
        /**
         * The distance of the light from the center of the object
         * a useful approximation as an alternative to per-vertex distance
         * calculations.
         */
        ACT_LIGHT_DISTANCE_OBJECT_SPACE,
        /**
         * Light power level, a single scalar as set in Light::setPowerScale  (index determined by setAutoConstant call)
         */
        ACT_LIGHT_POWER_SCALE,
        /// Light diffuse colour pre-scaled by Light::setPowerScale (index determined by setAutoConstant call)
        ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED,
        /// Light specular colour pre-scaled by Light::setPowerScale (index determined by setAutoConstant call)
        ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED,
        /// Array of light diffuse colours (count set by extra param)
        ACT_LIGHT_DIFFUSE_COLOUR_ARRAY,
        /// Array of light specular colours (count set by extra param)
        ACT_LIGHT_SPECULAR_COLOUR_ARRAY,
        /// Array of light diffuse colours scaled by light power (count set by extra param)
        ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED_ARRAY,
        /// Array of light specular colours scaled by light power (count set by extra param)
        ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED_ARRAY,
        /// Array of light attenuation parameters, Vector4(range, constant, linear, quadric) (count set by extra param)
        ACT_LIGHT_ATTENUATION_ARRAY,
        /// Array of light positions in world space (count set by extra param)
        ACT_LIGHT_POSITION_ARRAY,
        /// Array of light positions in object space (count set by extra param)
        ACT_LIGHT_POSITION_OBJECT_SPACE_ARRAY,
        /// Array of light positions in view space (count set by extra param)
        ACT_LIGHT_POSITION_VIEW_SPACE_ARRAY,
        /// Array of light directions in world space (count set by extra param)
        ACT_LIGHT_DIRECTION_ARRAY,
        /// Array of light directions in object space (count set by extra param)
        ACT_LIGHT_DIRECTION_OBJECT_SPACE_ARRAY,
        /// Array of light directions in view space (count set by extra param)
        ACT_LIGHT_DIRECTION_VIEW_SPACE_ARRAY,
        /**
         * Array of distances of the lights from the center of the object
         * a useful approximation as an alternative to per-vertex distance
         * calculations. (count set by extra param)
         */
        ACT_LIGHT_DISTANCE_OBJECT_SPACE_ARRAY,
        /**
         * Array of light power levels, a single scalar as set in Light::setPowerScale
         * (count set by extra param)
         */
        ACT_LIGHT_POWER_SCALE_ARRAY,
        /**
         * Spotlight parameters array of Vector4(innerFactor, outerFactor, falloff, isSpot)
         * innerFactor and outerFactor are cos(angle/2)
         * The isSpot parameter is 0.0f for non-spotlights, 1.0f for spotlights.
         * Also for non-spotlights the inner and outer factors are 1 and nearly 1 respectively.
         * (count set by extra param)
         */
        ACT_SPOTLIGHT_PARAMS_ARRAY,

        /**
         * The derived ambient light colour, with 'r', 'g', 'b' components filled with
         * product of surface ambient colour and ambient light colour, respectively,
         * and 'a' component filled with surface ambient alpha component.
         */
        ACT_DERIVED_AMBIENT_LIGHT_COLOUR,
        /**
         * The derived scene colour, with 'r', 'g' and 'b' components filled with sum
         * of derived ambient light colour and surface emissive colour, respectively,
         * and 'a' component filled with surface diffuse alpha component.
         */
        ACT_DERIVED_SCENE_COLOUR,

        /**
         * The derived light diffuse colour (index determined by setAutoConstant call),
         * with 'r', 'g' and 'b' components filled with product of surface diffuse colour,
         * light power scale and light diffuse colour, respectively, and 'a' component filled with surface
         * diffuse alpha component.
         */
        ACT_DERIVED_LIGHT_DIFFUSE_COLOUR,
        /**
         * The derived light specular colour (index determined by setAutoConstant call),
         * with 'r', 'g' and 'b' components filled with product of surface specular colour
         * and light specular colour, respectively, and 'a' component filled with surface
         * specular alpha component.
         */
        ACT_DERIVED_LIGHT_SPECULAR_COLOUR,

        /// Array of derived light diffuse colours (count set by extra param)
        ACT_DERIVED_LIGHT_DIFFUSE_COLOUR_ARRAY,
        /// Array of derived light specular colours (count set by extra param)
        ACT_DERIVED_LIGHT_SPECULAR_COLOUR_ARRAY,
        /**
         * The absolute light number of a local light index. Each pass may have
         * a number of lights passed to it, and each of these lights will have
         * an index in the overall light list, which will differ from the local
         * light index due to factors like setStartLight and setIteratePerLight.
         * This binding provides the global light index for a local index.
         */
        ACT_LIGHT_NUMBER,
        /// Returns (int) 1 if the  given light casts shadows, 0 otherwise (index set in extra param)
        ACT_LIGHT_CASTS_SHADOWS,


        /**
         * The distance a shadow volume should be extruded when using
         * finite extrusion programs.
         */
        ACT_SHADOW_EXTRUSION_DISTANCE,
        /// The current camera's position in world space
        ACT_CAMERA_POSITION,
        /// The current camera's position in object space
        ACT_CAMERA_POSITION_OBJECT_SPACE,
        /// The view/projection matrix of the assigned texture projection frustum
        ACT_TEXTURE_VIEWPROJ_MATRIX,
        /// Array of view/projection matrices of the first n texture projection frustums
        ACT_TEXTURE_VIEWPROJ_MATRIX_ARRAY,
        /**
         * The view/projection matrix of the assigned texture projection frustum,
         * combined with the current world matrix
         */
        ACT_TEXTURE_WORLDVIEWPROJ_MATRIX,
        /// Array of world/view/projection matrices of the first n texture projection frustums
        ACT_TEXTURE_WORLDVIEWPROJ_MATRIX_ARRAY,
        /// The view/projection matrix of a given spotlight
        ACT_SPOTLIGHT_VIEWPROJ_MATRIX,
        /**
         * The view/projection matrix of a given spotlight projection frustum,
         * combined with the current world matrix
         */
        ACT_SPOTLIGHT_WORLDVIEWPROJ_MATRIX,
        /// A custom parameter which will come from the renderable, using 'data' as the identifier
        ACT_CUSTOM,
        /**
         * provides current elapsed time
         */
        ACT_TIME,
        /**
         * Single float value, which repeats itself based on given as
         * parameter "cycle time". Equivalent to RenderMonkey's "Time0_X".
         */
        ACT_TIME_0_X,
        /// Cosine of "Time0_X". Equivalent to RenderMonkey's "CosTime0_X".
        ACT_COSTIME_0_X,
        /// Sine of "Time0_X". Equivalent to RenderMonkey's "SinTime0_X".
        ACT_SINTIME_0_X,
        /// Tangent of "Time0_X". Equivalent to RenderMonkey's "TanTime0_X".
        ACT_TANTIME_0_X,
        /**
         * Vector of "Time0_X", "SinTime0_X", "CosTime0_X",
         * "TanTime0_X". Equivalent to RenderMonkey's "Time0_X_Packed".
         */
        ACT_TIME_0_X_PACKED,
        /**
         * Single float value, which represents scaled time value [0..1],
         * which repeats itself based on given as parameter "cycle time".
         * Equivalent to RenderMonkey's "Time0_1".
         */
        ACT_TIME_0_1,
        /// Cosine of "Time0_1". Equivalent to RenderMonkey's "CosTime0_1".
        ACT_COSTIME_0_1,
        /// Sine of "Time0_1". Equivalent to RenderMonkey's "SinTime0_1".
        ACT_SINTIME_0_1,
        /// Tangent of "Time0_1". Equivalent to RenderMonkey's "TanTime0_1".
        ACT_TANTIME_0_1,
        /**
         * Vector of "Time0_1", "SinTime0_1", "CosTime0_1",
         * "TanTime0_1". Equivalent to RenderMonkey's "Time0_1_Packed".
         */
        ACT_TIME_0_1_PACKED,
        /**
         * Single float value, which represents scaled time value [0..2*Pi],
         * which repeats itself based on given as parameter "cycle time".
         * Equivalent to RenderMonkey's "Time0_2PI".
         */
        ACT_TIME_0_2PI,
        /// Cosine of "Time0_2PI". Equivalent to RenderMonkey's "CosTime0_2PI".
        ACT_COSTIME_0_2PI,
        /// Sine of "Time0_2PI". Equivalent to RenderMonkey's "SinTime0_2PI".
        ACT_SINTIME_0_2PI,
        /// Tangent of "Time0_2PI". Equivalent to RenderMonkey's "TanTime0_2PI".
        ACT_TANTIME_0_2PI,
        /**
         * Vector of "Time0_2PI", "SinTime0_2PI", "CosTime0_2PI",
         * "TanTime0_2PI". Equivalent to RenderMonkey's "Time0_2PI_Packed".
         */
        ACT_TIME_0_2PI_PACKED,
        /// provides the scaled frame time, returned as a floating point value.
        ACT_FRAME_TIME,
        /// provides the calculated frames per second, returned as a floating point value.
        ACT_FPS,
        /// viewport-related values
        /**
         * Current viewport width (in pixels) as floating point value.
         * Equivalent to RenderMonkey's "ViewportWidth".
         */
        ACT_VIEWPORT_WIDTH,
        /**
         * Current viewport height (in pixels) as floating point value.
         * Equivalent to RenderMonkey's "ViewportHeight".
         */
        ACT_VIEWPORT_HEIGHT,
        /**
         * This variable represents 1.0/ViewportWidth.
         * Equivalent to RenderMonkey's "ViewportWidthInverse".
         */
        ACT_INVERSE_VIEWPORT_WIDTH,
        /**
         * This variable represents 1.0/ViewportHeight.
         * Equivalent to RenderMonkey's "ViewportHeightInverse".
         */
        ACT_INVERSE_VIEWPORT_HEIGHT,
        /**
         * Packed of "ViewportWidth", "ViewportHeight", "ViewportWidthInverse",
         * "ViewportHeightInverse".
         */
        ACT_VIEWPORT_SIZE,

        /// view parameters
        /**
         * This variable provides the view direction vector (world space).
         * Equivalent to RenderMonkey's "ViewDirection".
         */
        ACT_VIEW_DIRECTION,
        /**
         * This variable provides the view side vector (world space).
         * Equivalent to RenderMonkey's "ViewSideVector".
         */
        ACT_VIEW_SIDE_VECTOR,
        /**
         * This variable provides the view up vector (world space).
         * Equivalent to RenderMonkey's "ViewUpVector".
         */
        ACT_VIEW_UP_VECTOR,
        /**
         * This variable provides the field of view as a floating point value.
         * Equivalent to RenderMonkey's "FOV".
         */
        ACT_FOV,
        /**
         * This variable provides the near clip distance as a floating point value.
         * Equivalent to RenderMonkey's "NearClipPlane".
         */
        ACT_NEAR_CLIP_DISTANCE,
        /**
         * This variable provides the far clip distance as a floating point value.
         * Equivalent to RenderMonkey's "FarClipPlane".
         */
        ACT_FAR_CLIP_DISTANCE,

        /**
         * provides the pass index number within the technique
         * of the active materil.
         */
        ACT_PASS_NUMBER,

        /**
         * provides the current iteration number of the pass. The iteration
         * number is the number of times the current render operation has
         * been drawn for the active pass.
         */
        ACT_PASS_ITERATION_NUMBER,


        /**
         * Provides a parametric animation value [0..1], only available
         * where the renderable specifically implements it.
         */
        ACT_ANIMATION_PARAMETRIC,

        /**
         * Provides the texel offsets required by this rendersystem to map
         * texels to pixels. Packed as
         * float4(absoluteHorizontalOffset, absoluteVerticalOffset,
         * horizontalOffset / viewportWidth, verticalOffset / viewportHeight)
         */
        ACT_TEXEL_OFFSETS,

        /**
         * Provides information about the depth range of the scene as viewed
         * from the current camera.
         * Passed as float4(minDepth, maxDepth, depthRange, 1 / depthRange)
         */
        ACT_SCENE_DEPTH_RANGE,

        /**
         * Provides information about the depth range of the scene as viewed
         * from a given shadow camera. Requires an index parameter which maps
         * to a light index relative to the current light list.
         * Passed as float4(minDepth, maxDepth, depthRange, 1 / depthRange)
         */
        ACT_SHADOW_SCENE_DEPTH_RANGE,

        /**
         * Provides the fixed shadow colour as configured via SceneManager::setShadowColour;
         * useful for integrated modulative shadows.
         */
        ACT_SHADOW_COLOUR,
        /**
         * Provides texture size of the texture unit (index determined by setAutoConstant
         * call). Packed as float4(width, height, depth, 1)
         */
        ACT_TEXTURE_SIZE,
        /**
         * Provides inverse texture size of the texture unit (index determined by setAutoConstant
         * call). Packed as float4(1 / width, 1 / height, 1 / depth, 1)
         */
        ACT_INVERSE_TEXTURE_SIZE,
        /**
         * Provides packed texture size of the texture unit (index determined by setAutoConstant
         * call). Packed as float4(width, height, 1 / width, 1 / height)
         */
        ACT_PACKED_TEXTURE_SIZE,

        /**
         * Provides the current transform matrix of the texture unit (index determined by setAutoConstant
         * call), as seen by the fixed-function pipeline.
         */
        ACT_TEXTURE_MATRIX,

        /**
         * Provides the position of the LOD camera in world space, allowing you
         * to perform separate LOD calculations in shaders independent of the rendering
         * camera. If there is no separate LOD camera then this is the real camera
         * position. See Camera::setLodCamera.
         */
        ACT_LOD_CAMERA_POSITION,
        /**
         * Provides the position of the LOD camera in object space, allowing you
         * to perform separate LOD calculations in shaders independent of the rendering
         * camera. If there is no separate LOD camera then this is the real camera
         * position. See Camera::setLodCamera.
         */
        ACT_LOD_CAMERA_POSITION_OBJECT_SPACE,
        /**
         * Binds custom per-light constants to the shaders.
         */
        ACT_LIGHT_CUSTOM
    }

    /**
     * Defines the type of the extra data item used by the auto constant.
     */
    public enum ACDataType {
        /// no data is required
        ACDT_NONE,
        /// the auto constant requires data of type int
        ACDT_INT,
        /// the auto constant requires data of type real
        ACDT_REAL
    }

    /**
     * Defines the base element type of the auto constant
     */
    public enum ElementType {
        ET_INT,
        ET_REAL
    }


    protected static final ENG_AutoConstantDefinition[] AutoConstantDictionary = new ENG_AutoConstantDefinition[]{
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_WORLD_MATRIX, "world_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_WORLD_MATRIX, "inverse_world_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TRANSPOSE_WORLD_MATRIX, "transpose_world_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_TRANSPOSE_WORLD_MATRIX, "inverse_transpose_world_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_WORLD_MATRIX_ARRAY_3x4, "world_matrix_array_3x4", 12, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_WORLD_MATRIX_ARRAY, "world_matrix_array", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEW_MATRIX, "view_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_VIEW_MATRIX, "inverse_view_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TRANSPOSE_VIEW_MATRIX, "transpose_view_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_TRANSPOSE_VIEW_MATRIX, "inverse_transpose_view_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_PROJECTION_MATRIX, "projection_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_PROJECTION_MATRIX, "inverse_projection_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TRANSPOSE_PROJECTION_MATRIX, "transpose_projection_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_TRANSPOSE_PROJECTION_MATRIX, "inverse_transpose_projection_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEWPROJ_MATRIX, "viewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_VIEWPROJ_MATRIX, "inverse_viewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TRANSPOSE_VIEWPROJ_MATRIX, "transpose_viewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_TRANSPOSE_VIEWPROJ_MATRIX, "inverse_transpose_viewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_WORLDVIEW_MATRIX, "worldview_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_WORLDVIEW_MATRIX, "inverse_worldview_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TRANSPOSE_WORLDVIEW_MATRIX, "transpose_worldview_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_TRANSPOSE_WORLDVIEW_MATRIX, "inverse_transpose_worldview_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_WORLDVIEWPROJ_MATRIX, "worldviewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_WORLDVIEWPROJ_MATRIX, "inverse_worldviewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TRANSPOSE_WORLDVIEWPROJ_MATRIX, "transpose_worldviewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_TRANSPOSE_WORLDVIEWPROJ_MATRIX, "inverse_transpose_worldviewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_RENDER_TARGET_FLIPPING, "render_target_flipping", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VERTEX_WINDING, "vertex_winding", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_FOG_COLOUR, "fog_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_FOG_PARAMS, "fog_params", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SURFACE_AMBIENT_COLOUR, "surface_ambient_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SURFACE_DIFFUSE_COLOUR, "surface_diffuse_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SURFACE_SPECULAR_COLOUR, "surface_specular_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SURFACE_EMISSIVE_COLOUR, "surface_emissive_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SURFACE_SHININESS, "surface_shininess", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_COUNT, "light_count", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_AMBIENT_LIGHT_COLOUR, "ambient_light_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIFFUSE_COLOUR, "light_diffuse_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_SPECULAR_COLOUR, "light_specular_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_ATTENUATION, "light_attenuation", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SPOTLIGHT_PARAMS, "spotlight_params", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POSITION, "light_position", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POSITION_OBJECT_SPACE, "light_position_object_space", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POSITION_VIEW_SPACE, "light_position_view_space", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIRECTION, "light_direction", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIRECTION_OBJECT_SPACE, "light_direction_object_space", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIRECTION_VIEW_SPACE, "light_direction_view_space", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DISTANCE_OBJECT_SPACE, "light_distance_object_space", 1, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POWER_SCALE, "light_power", 1, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED, "light_diffuse_colour_power_scaled", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED, "light_specular_colour_power_scaled", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIFFUSE_COLOUR_ARRAY, "light_diffuse_colour_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_SPECULAR_COLOUR_ARRAY, "light_specular_colour_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED_ARRAY, "light_diffuse_colour_power_scaled_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED_ARRAY, "light_specular_colour_power_scaled_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_ATTENUATION_ARRAY, "light_attenuation_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POSITION_ARRAY, "light_position_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POSITION_OBJECT_SPACE_ARRAY, "light_position_object_space_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POSITION_VIEW_SPACE_ARRAY, "light_position_view_space_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIRECTION_ARRAY, "light_direction_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIRECTION_OBJECT_SPACE_ARRAY, "light_direction_object_space_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DIRECTION_VIEW_SPACE_ARRAY, "light_direction_view_space_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_DISTANCE_OBJECT_SPACE_ARRAY, "light_distance_object_space_array", 1, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_POWER_SCALE_ARRAY, "light_power_array", 1, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SPOTLIGHT_PARAMS_ARRAY, "spotlight_params_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_DERIVED_AMBIENT_LIGHT_COLOUR, "derived_ambient_light_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_DERIVED_SCENE_COLOUR, "derived_scene_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_DERIVED_LIGHT_DIFFUSE_COLOUR, "derived_light_diffuse_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_DERIVED_LIGHT_SPECULAR_COLOUR, "derived_light_specular_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_DERIVED_LIGHT_DIFFUSE_COLOUR_ARRAY, "derived_light_diffuse_colour_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_DERIVED_LIGHT_SPECULAR_COLOUR_ARRAY, "derived_light_specular_colour_array", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_NUMBER, "light_number", 1, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_CASTS_SHADOWS, "light_casts_shadows", 1, ElementType.ET_REAL, ACDataType.ACDT_INT),

            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SHADOW_EXTRUSION_DISTANCE, "shadow_extrusion_distance", 1, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_CAMERA_POSITION, "camera_position", 3, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_CAMERA_POSITION_OBJECT_SPACE, "camera_position_object_space", 3, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TEXTURE_VIEWPROJ_MATRIX, "texture_viewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TEXTURE_VIEWPROJ_MATRIX_ARRAY, "texture_viewproj_matrix_array", 16, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TEXTURE_WORLDVIEWPROJ_MATRIX, "texture_worldviewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TEXTURE_WORLDVIEWPROJ_MATRIX_ARRAY, "texture_worldviewproj_matrix_array", 16, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SPOTLIGHT_VIEWPROJ_MATRIX, "spotlight_viewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SPOTLIGHT_WORLDVIEWPROJ_MATRIX, "spotlight_worldviewproj_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_CUSTOM, "custom", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),  // *** needs to be tested
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TIME, "time", 1, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TIME_0_X, "time_0_x", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_COSTIME_0_X, "costime_0_x", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SINTIME_0_X, "sintime_0_x", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TANTIME_0_X, "tantime_0_x", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TIME_0_X_PACKED, "time_0_x_packed", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TIME_0_1, "time_0_1", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_COSTIME_0_1, "costime_0_1", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SINTIME_0_1, "sintime_0_1", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TANTIME_0_1, "tantime_0_1", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TIME_0_1_PACKED, "time_0_1_packed", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TIME_0_2PI, "time_0_2pi", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_COSTIME_0_2PI, "costime_0_2pi", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SINTIME_0_2PI, "sintime_0_2pi", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TANTIME_0_2PI, "tantime_0_2pi", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TIME_0_2PI_PACKED, "time_0_2pi_packed", 4, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_FRAME_TIME, "frame_time", 1, ElementType.ET_REAL, ACDataType.ACDT_REAL),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_FPS, "fps", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEWPORT_WIDTH, "viewport_width", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEWPORT_HEIGHT, "viewport_height", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_VIEWPORT_WIDTH, "inverse_viewport_width", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_VIEWPORT_HEIGHT, "inverse_viewport_height", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEWPORT_SIZE, "viewport_size", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEW_DIRECTION, "view_direction", 3, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEW_SIDE_VECTOR, "view_side_vector", 3, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_VIEW_UP_VECTOR, "view_up_vector", 3, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_FOV, "fov", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_NEAR_CLIP_DISTANCE, "near_clip_distance", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_FAR_CLIP_DISTANCE, "far_clip_distance", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_PASS_NUMBER, "pass_number", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_PASS_ITERATION_NUMBER, "pass_iteration_number", 1, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_ANIMATION_PARAMETRIC, "animation_parametric", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TEXEL_OFFSETS, "texel_offsets", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SCENE_DEPTH_RANGE, "scene_depth_range", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SHADOW_SCENE_DEPTH_RANGE, "shadow_scene_depth_range", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_SHADOW_COLOUR, "shadow_colour", 4, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TEXTURE_SIZE, "texture_size", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_INVERSE_TEXTURE_SIZE, "inverse_texture_size", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_PACKED_TEXTURE_SIZE, "packed_texture_size", 4, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_TEXTURE_MATRIX, "texture_matrix", 16, ElementType.ET_REAL, ACDataType.ACDT_INT),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LOD_CAMERA_POSITION, "lod_camera_position", 3, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LOD_CAMERA_POSITION_OBJECT_SPACE, "lod_camera_position_object_space", 3, ElementType.ET_REAL, ACDataType.ACDT_NONE),
            new ENG_AutoConstantDefinition(AutoConstantType.ACT_LIGHT_CUSTOM, "light_custom", 4, ElementType.ET_REAL, ACDataType.ACDT_INT)
    };

    /*	protected ArrayList<ENG_Float> mFloatConstants = new ArrayList<ENG_Float>();
        protected ArrayList<ENG_Integer> mIntConstants = new ArrayList<ENG_Integer>();*/
//	protected float[] mFloatConstants;
//	protected int[] mIntConstants;
    protected FloatBuffer mFloatConstants;
    protected IntBuffer mIntConstants;
    protected ENG_GpuNamedConstants mNamedConstants;// = new ENG_GpuNamedConstants();
    protected ArrayList<ENG_AutoConstantEntry> mAutoConstants =
            new ArrayList<>();
    protected short mCombinedVariability =
            GpuParamVariability.GPV_GLOBAL.getVariability();
    protected boolean mTransposeMatrices = true;
    protected boolean mIgnoreMissingParams;
    protected int mActivePassIterationIndex = Integer.MAX_VALUE;
    protected final ArrayList<ENG_GpuSharedParametersUsage> mSharedParamSets =
            new ArrayList<>();

    private final ENG_Matrix3 transposeMat3 = new ENG_Matrix3();
    private final ENG_Matrix4 transposeMat4 = new ENG_Matrix4();
    private final float[] fval = new float[4];
    private final int[] ival = new int[4];
    private ENG_Vector4D vec3 = new ENG_Vector4D();
    private final ENG_Vector4D vec4 = new ENG_Vector4D();
    private final ENG_Matrix4 m4 = new ENG_Matrix4();

    private final long[] ptr = new long[1];
    private long id;
    private String name;
    private boolean nativePtrSet;

    protected short deriveVariability(AutoConstantType act) {
        switch (act) {
            case ACT_VIEW_MATRIX:
            case ACT_INVERSE_VIEW_MATRIX:
            case ACT_TRANSPOSE_VIEW_MATRIX:
            case ACT_INVERSE_TRANSPOSE_VIEW_MATRIX:
            case ACT_PROJECTION_MATRIX:
            case ACT_INVERSE_PROJECTION_MATRIX:
            case ACT_TRANSPOSE_PROJECTION_MATRIX:
            case ACT_INVERSE_TRANSPOSE_PROJECTION_MATRIX:
            case ACT_VIEWPROJ_MATRIX:
            case ACT_INVERSE_VIEWPROJ_MATRIX:
            case ACT_TRANSPOSE_VIEWPROJ_MATRIX:
            case ACT_INVERSE_TRANSPOSE_VIEWPROJ_MATRIX:
            case ACT_RENDER_TARGET_FLIPPING:
            case ACT_VERTEX_WINDING:
            case ACT_AMBIENT_LIGHT_COLOUR:
            case ACT_DERIVED_AMBIENT_LIGHT_COLOUR:
            case ACT_DERIVED_SCENE_COLOUR:
            case ACT_FOG_COLOUR:
            case ACT_FOG_PARAMS:
            case ACT_SURFACE_AMBIENT_COLOUR:
            case ACT_SURFACE_DIFFUSE_COLOUR:
            case ACT_SURFACE_SPECULAR_COLOUR:
            case ACT_SURFACE_EMISSIVE_COLOUR:
            case ACT_SURFACE_SHININESS:
            case ACT_CAMERA_POSITION:
            case ACT_TIME:
            case ACT_TIME_0_X:
            case ACT_COSTIME_0_X:
            case ACT_SINTIME_0_X:
            case ACT_TANTIME_0_X:
            case ACT_TIME_0_X_PACKED:
            case ACT_TIME_0_1:
            case ACT_COSTIME_0_1:
            case ACT_SINTIME_0_1:
            case ACT_TANTIME_0_1:
            case ACT_TIME_0_1_PACKED:
            case ACT_TIME_0_2PI:
            case ACT_COSTIME_0_2PI:
            case ACT_SINTIME_0_2PI:
            case ACT_TANTIME_0_2PI:
            case ACT_TIME_0_2PI_PACKED:
            case ACT_FRAME_TIME:
            case ACT_FPS:
            case ACT_VIEWPORT_WIDTH:
            case ACT_VIEWPORT_HEIGHT:
            case ACT_INVERSE_VIEWPORT_WIDTH:
            case ACT_INVERSE_VIEWPORT_HEIGHT:
            case ACT_VIEWPORT_SIZE:
            case ACT_TEXEL_OFFSETS:
            case ACT_TEXTURE_SIZE:
            case ACT_INVERSE_TEXTURE_SIZE:
            case ACT_PACKED_TEXTURE_SIZE:
            case ACT_SCENE_DEPTH_RANGE:
            case ACT_VIEW_DIRECTION:
            case ACT_VIEW_SIDE_VECTOR:
            case ACT_VIEW_UP_VECTOR:
            case ACT_FOV:
            case ACT_NEAR_CLIP_DISTANCE:
            case ACT_FAR_CLIP_DISTANCE:
            case ACT_PASS_NUMBER:
            case ACT_TEXTURE_MATRIX:
            case ACT_LOD_CAMERA_POSITION:

                return GpuParamVariability.GPV_GLOBAL.getVariability();

            case ACT_WORLD_MATRIX:
            case ACT_INVERSE_WORLD_MATRIX:
            case ACT_TRANSPOSE_WORLD_MATRIX:
            case ACT_INVERSE_TRANSPOSE_WORLD_MATRIX:
            case ACT_WORLD_MATRIX_ARRAY_3x4:
            case ACT_WORLD_MATRIX_ARRAY:
            case ACT_WORLDVIEW_MATRIX:
            case ACT_INVERSE_WORLDVIEW_MATRIX:
            case ACT_TRANSPOSE_WORLDVIEW_MATRIX:
            case ACT_INVERSE_TRANSPOSE_WORLDVIEW_MATRIX:
            case ACT_WORLDVIEWPROJ_MATRIX:
            case ACT_INVERSE_WORLDVIEWPROJ_MATRIX:
            case ACT_TRANSPOSE_WORLDVIEWPROJ_MATRIX:
            case ACT_INVERSE_TRANSPOSE_WORLDVIEWPROJ_MATRIX:
            case ACT_CAMERA_POSITION_OBJECT_SPACE:
            case ACT_LOD_CAMERA_POSITION_OBJECT_SPACE:
            case ACT_CUSTOM:
            case ACT_ANIMATION_PARAMETRIC:

                return GpuParamVariability.GPV_PER_OBJECT.getVariability();

            case ACT_LIGHT_POSITION_OBJECT_SPACE:
            case ACT_LIGHT_DIRECTION_OBJECT_SPACE:
            case ACT_LIGHT_DISTANCE_OBJECT_SPACE:
            case ACT_LIGHT_POSITION_OBJECT_SPACE_ARRAY:
            case ACT_LIGHT_DIRECTION_OBJECT_SPACE_ARRAY:
            case ACT_LIGHT_DISTANCE_OBJECT_SPACE_ARRAY:
            case ACT_TEXTURE_WORLDVIEWPROJ_MATRIX:
            case ACT_TEXTURE_WORLDVIEWPROJ_MATRIX_ARRAY:
            case ACT_SPOTLIGHT_WORLDVIEWPROJ_MATRIX:

                // These depend on BOTH lights and objects
                return (short) ((GpuParamVariability.GPV_PER_OBJECT.getVariability()) |
                        (GpuParamVariability.GPV_LIGHTS.getVariability()));

            case ACT_LIGHT_COUNT:
            case ACT_LIGHT_DIFFUSE_COLOUR:
            case ACT_LIGHT_SPECULAR_COLOUR:
            case ACT_LIGHT_POSITION:
            case ACT_LIGHT_DIRECTION:
            case ACT_LIGHT_POSITION_VIEW_SPACE:
            case ACT_LIGHT_DIRECTION_VIEW_SPACE:
            case ACT_SHADOW_EXTRUSION_DISTANCE:
            case ACT_SHADOW_SCENE_DEPTH_RANGE:
            case ACT_SHADOW_COLOUR:
            case ACT_LIGHT_POWER_SCALE:
            case ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED:
            case ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED:
            case ACT_LIGHT_NUMBER:
            case ACT_LIGHT_CASTS_SHADOWS:
            case ACT_LIGHT_ATTENUATION:
            case ACT_SPOTLIGHT_PARAMS:
            case ACT_LIGHT_DIFFUSE_COLOUR_ARRAY:
            case ACT_LIGHT_SPECULAR_COLOUR_ARRAY:
            case ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED_ARRAY:
            case ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED_ARRAY:
            case ACT_LIGHT_POSITION_ARRAY:
            case ACT_LIGHT_DIRECTION_ARRAY:
            case ACT_LIGHT_POSITION_VIEW_SPACE_ARRAY:
            case ACT_LIGHT_DIRECTION_VIEW_SPACE_ARRAY:
            case ACT_LIGHT_POWER_SCALE_ARRAY:
            case ACT_LIGHT_ATTENUATION_ARRAY:
            case ACT_SPOTLIGHT_PARAMS_ARRAY:
            case ACT_TEXTURE_VIEWPROJ_MATRIX:
            case ACT_TEXTURE_VIEWPROJ_MATRIX_ARRAY:
            case ACT_SPOTLIGHT_VIEWPROJ_MATRIX:
            case ACT_LIGHT_CUSTOM:

                return GpuParamVariability.GPV_LIGHTS.getVariability();

            case ACT_DERIVED_LIGHT_DIFFUSE_COLOUR:
            case ACT_DERIVED_LIGHT_SPECULAR_COLOUR:
            case ACT_DERIVED_LIGHT_DIFFUSE_COLOUR_ARRAY:
            case ACT_DERIVED_LIGHT_SPECULAR_COLOUR_ARRAY:

                return (short) (GpuParamVariability.GPV_GLOBAL.getVariability() |
                        GpuParamVariability.GPV_LIGHTS.getVariability());

            case ACT_PASS_ITERATION_NUMBER:

                return GpuParamVariability.GPV_PASS_ITERATION_NUMBER.getVariability();

            default:
                return GpuParamVariability.GPV_GLOBAL.getVariability();
        }
    }

    protected void copySharedParamSetUsage(
            ArrayList<ENG_GpuSharedParametersUsage> srcList) {
        mSharedParamSets.clear();
        int len = srcList.size();
        for (int i = 0; i < len; ++i) {
            mSharedParamSets.add(new ENG_GpuSharedParametersUsage(
                    srcList.get(i).getSharedParams(), this));
        }
    }

    public ENG_GpuProgramParameters() {

    }

    public ENG_GpuProgramParameters(ENG_GpuProgramParameters oth) {
        set(oth);
    }

    public void set(ENG_GpuProgramParameters oth) {
    /*	mFloatConstants.clear();
		for (ENG_Float i : oth.mFloatConstants) {
			mFloatConstants.add(new ENG_Float(i));
		}
		mIntConstants.clear();
		for (ENG_Integer i : oth.mIntConstants) {
			mIntConstants.add(new ENG_Integer(i));
		}*/
//		if (mFloatConstants != null) {
//			if (mFloatConstants.length < oth.mFloatConstants.length) {
//				ENG_Utility.extendArray(mFloatConstants, 
//						oth.mFloatConstants.length/* - mFloatConstants.length*/);				
//			}
//			
//		} else {
//			mFloatConstants = new float[oth.mFloatConstants.length];
//		}
//		for (int i = 0; i < oth.mFloatConstants.length; ++i) {
//			mFloatConstants[i] = oth.mFloatConstants[i];
//		}
//		if (mIntConstants != null) {
//			if (mIntConstants.length < oth.mIntConstants.length) {
//				ENG_Utility.extendArray(mIntConstants, 
//						oth.mIntConstants.length/* - mIntConstants.length*/);
//			}
//		} else {
//			mIntConstants = new int[oth.mIntConstants.length];
//		}
        if (oth.mFloatConstants != null) {
            if (mFloatConstants != null) {
                if (mFloatConstants.capacity() < oth.mFloatConstants.capacity()) {
                    mFloatConstants = ENG_Utility.extendArray(mFloatConstants,
                            oth.mFloatConstants.capacity() * ENG_Float.SIZE_IN_BYTES/* - mFloatConstants.length*/);
                }

            } else {
                mFloatConstants = ENG_Utility.allocateDirect(
                        oth.mFloatConstants.capacity() * ENG_Float.SIZE_IN_BYTES).asFloatBuffer();
            }
            oth.mFloatConstants.limit(oth.mFloatConstants.capacity());
            oth.mFloatConstants.position(0);
            ENG_Utility.memcpy(mFloatConstants, oth.mFloatConstants,
                    oth.mFloatConstants.capacity());
        } else {
            mFloatConstants = null;
        }

        if (oth.mIntConstants != null) {
            if (mIntConstants != null) {
                if (mIntConstants.capacity() < oth.mIntConstants.capacity()) {
                    ENG_Utility.extendArray(mIntConstants,
                            oth.mIntConstants.capacity() * ENG_Integer.SIZE_IN_BYTES/* - mIntConstants.length*/);
                }
            } else {
                mIntConstants = ENG_Utility.allocateDirect(
                        oth.mIntConstants.capacity() * ENG_Integer.SIZE_IN_BYTES).asIntBuffer();
            }
            oth.mIntConstants.limit(oth.mIntConstants.capacity());
            oth.mIntConstants.position(0);
            ENG_Utility.memcpy(mIntConstants, oth.mIntConstants,
                    oth.mIntConstants.capacity());
        } else {
            mIntConstants = null;
        }


        mAutoConstants.clear();
        for (ENG_AutoConstantEntry entry : oth.mAutoConstants) {
            mAutoConstants.add(new ENG_AutoConstantEntry(entry));
        }

        //	mFloatLogicalToPhysical = oth.mFloatLogicalToPhysical;
        //	mIntLogicalToPhysical = oth.mIntLogicalToPhysical;
        mNamedConstants = oth.mNamedConstants;
        copySharedParamSetUsage(oth.mSharedParamSets);

        mCombinedVariability = oth.mCombinedVariability;
        mTransposeMatrices = oth.mTransposeMatrices;
        mIgnoreMissingParams = oth.mIgnoreMissingParams;
        mActivePassIterationIndex = oth.mActivePassIterationIndex;
    }

    public void setTransposeMatrices(boolean b) {
        mTransposeMatrices = b;
    }

    public boolean getTransposeMatrices() {
        return mTransposeMatrices;
    }

    public void _setNamedConstants(ENG_GpuNamedConstants namedConstants) {
        mNamedConstants = namedConstants;

//		if (mFloatConstants != null) {
//			if (mFloatConstants.length < namedConstants.floatBufferSize) {
//				mFloatConstants = ENG_Utility.extendArray(mFloatConstants, 
//						namedConstants.floatBufferSize/* - mFloatConstants.length*/);
//			}
//		} else {
//			mFloatConstants = new float[namedConstants.floatBufferSize];
//		}
//		if (mIntConstants != null) {
//			if (mIntConstants.length < namedConstants.intBufferSize) {
//				mIntConstants = ENG_Utility.extendArray(mIntConstants, 
//						namedConstants.intBufferSize/* - mIntConstants.length*/);
//			}
//		} else {
//			mIntConstants = new int[namedConstants.intBufferSize];
//		}
        int floatBufSize = namedConstants.floatBufferSize * ENG_Float.SIZE_IN_BYTES;
        if (floatBufSize > 0) {
            if (mFloatConstants != null) {
                if (mFloatConstants.capacity() < floatBufSize) {
                    mFloatConstants = ENG_Utility.extendArray(mFloatConstants,
                            floatBufSize/* - mFloatConstants.length*/);
                }
            } else {
                mFloatConstants = ENG_Utility.allocateDirect(
                        floatBufSize).asFloatBuffer();
            }
        }
        int intBufSize = namedConstants.intBufferSize * ENG_Integer.SIZE_IN_BYTES;
        if (intBufSize > 0) {
            if (mIntConstants != null) {
                if (mIntConstants.capacity() < intBufSize) {
                    mIntConstants = ENG_Utility.extendArray(mIntConstants,
                            intBufSize/* - mIntConstants.length*/);
                }
            } else {
                mIntConstants = ENG_Utility.allocateDirect(
                        intBufSize).asIntBuffer();
            }
        }
    }

    public boolean hasNamedParameters() {
        return mNamedConstants != null;
    }

    public void setConstant() {

    }

    public boolean hasPassIterationNumber() {
        return mActivePassIterationIndex != Integer.MAX_VALUE;
    }

    public int getPassIterationNumber() {
        return mActivePassIterationIndex;
    }

    public void incPassIterationNumber() {
        if (mActivePassIterationIndex != Integer.MAX_VALUE) {
//			++mFloatConstants[mActivePassIterationIndex];
		/*	ENG_Float f = mFloatConstants.get(mActivePassIterationIndex);
			f.setValue(f.getValue() + 1.0f);*/
            mFloatConstants.put(mActivePassIterationIndex,
                    mFloatConstants.get(mActivePassIterationIndex) + 1);
        }
    }

//	public FloatBuffer getFloatConstantList() {
//		return mFloatConstants;
//	}
//	
//	public FloatBuffer getFloatPointer() {
//	//	return mFloatConstants.get(pos);
//		return mFloatConstants;
//	}

    public FloatBuffer getFloatPointerArray() {
	/*	List<ENG_Float> list = mFloatConstants.subList(pos, mFloatConstants.size());
		ENG_Float[] fList = new ENG_Float[mFloatConstants.size() - pos];
		int i = 0;
		for (ENG_Float f : list) {
			fList[i++] = f;
		}
		return fList;*/
        return mFloatConstants;
    }

//	public IntBuffer getIntConstantList() {
//		return mIntConstants;
//	}
//	
//	public IntBuffer getIntPointer() {
//		return mIntConstants;
//	}

    public IntBuffer getIntPointerArray() {
	/*	List<ENG_Integer> list = mIntConstants.subList(pos, mIntConstants.size());
		ENG_Integer[] iList = new ENG_Integer[mIntConstants.size() - pos];
		int i = 0;
		for (ENG_Integer e : list) {
			iList[i++] = e;
		}
		return iList;*/
        return mIntConstants;
    }

    public ArrayList<ENG_AutoConstantEntry> getAutoConstantList() {
        return mAutoConstants;
    }

    public ENG_GpuConstantDefinition _findNamedConstantDefinition(String name) {
        return _findNamedConstantDefinition(name, false);
    }

    public ENG_GpuConstantDefinition _findNamedConstantDefinition(String name,
                                                                  boolean throwExceptionIfNotFound) {
        if (mNamedConstants == null) {
            if (throwExceptionIfNotFound) {
                throw new NullPointerException(
                        "Named constants have not been initialised, perhaps a compile error.");

            }
            return null;
        }
        ENG_GpuConstantDefinition i = mNamedConstants.map.get(name);
        if (i == null) {
            if (throwExceptionIfNotFound) {
                throw new IllegalArgumentException(
                        "Parameter called " + name + " does not exist. ");
            }
            return null;
        }
        return i;
    }

    public void _writeRawConstant(int physicalIndex, float f) {
        fval[0] = f;
        _writeRawConstants(physicalIndex, fval, 1);
    }

    public void _writeRawConstant(int physicalIndex, int f) {
        ival[0] = f;
        _writeRawConstants(physicalIndex, ival, 1);
    }

    public void _writeRawConstant(int physicalIndex, ENG_Vector3D vec) {
        fval[0] = vec.x;
        fval[1] = vec.y;
        fval[2] = vec.z;
        _writeRawConstants(physicalIndex, fval, 3);
    }

    public void _writeRawConstant(int physicalIndex, ENG_Vector3D vec, int count) {
        switch (count) {
            default:
            case 3:
                fval[2] = vec.z;
            case 2:
                fval[1] = vec.y;
            case 1:
                fval[0] = vec.x;
                break;


        }
        _writeRawConstants(physicalIndex, fval,
                ((count < 3) && (count > 0)) ? count : 3);

    }

    public void _writeRawConstant(int physicalIndex, ENG_Vector4D vec) {
        fval[0] = vec.x;
        fval[1] = vec.y;
        fval[2] = vec.z;
        fval[3] = vec.w;
        _writeRawConstants(physicalIndex, fval, 4);
    }

    public void _writeRawConstant(int physicalIndex, ENG_Vector4D vec, int count) {
        switch (count) {
            default:
            case 4:
                fval[3] = vec.w;
            case 3:
                fval[2] = vec.z;
            case 2:
                fval[1] = vec.y;
            case 1:
                fval[0] = vec.x;
                break;


        }
        _writeRawConstants(physicalIndex, fval,
                ((count < 4) && (count > 0)) ? count : 4);

    }

    public void _writeRawConstant(int physicalIndex, ENG_Matrix3[] m, int numEntries) {
        for (int i = 0; i < numEntries; ++i) {
            _writeRawConstant(physicalIndex, m[i], 9);
            physicalIndex += 9;
        }
    }

    public void _writeRawConstant(int physicalIndex, ENG_Matrix4[] m, int numEntries) {
        for (int i = 0; i < numEntries; ++i) {
            _writeRawConstant(physicalIndex, m[i], 16);
            physicalIndex += 16;
        }
    }

    public void _writeRawConstant(int physicalIndex, ENG_Matrix3 m, int elementCount) {
        if (mTransposeMatrices) {
            transposeMat3.set(m);
            transposeMat3.transpose();
            _writeRawConstants(physicalIndex, transposeMat3.get(),
                    Math.min(elementCount, 9));
        } else {
            _writeRawConstants(physicalIndex, m.get(),
                    Math.min(elementCount, 9));
        }
    }

    public void _writeRawConstant(int physicalIndex, ENG_Matrix4 m, int elementCount) {
        if (mTransposeMatrices) {
            transposeMat4.set(m);
            transposeMat4.transpose();
            _writeRawConstants(physicalIndex, transposeMat4.get(),
                    Math.min(elementCount, 16));
        } else {
            _writeRawConstants(physicalIndex, m.get(),
                    Math.min(elementCount, 16));
        }
    }

    public void _writeRawConstant(int physicalIndex, ENG_ColorValue val) {
        float[] fval = new float[]{val.r, val.g, val.b, val.a};
        _writeRawConstants(physicalIndex, fval, 4);
    }

    public void _writeRawConstant(int physicalIndex, ENG_ColorValue val, int count) {
        float[] fval = new float[]{val.r, val.g, val.b, val.a};
        _writeRawConstants(physicalIndex, fval,
                ((count < 4) && (count > 0)) ? count : 4);
    }

    public void _writeRawConstants(int physicalIndex, float[] val, int count) {
        assert (mFloatConstants != null &&
                physicalIndex + count < mFloatConstants.capacity());
        mFloatConstants.limit(physicalIndex + count);
        mFloatConstants.position(physicalIndex);
        mFloatConstants.put(val, 0, count);
//		for (int i = 0; i < count; ++i) {
		/*	if (physicalIndex + i >= mFloatConstants.size()) {
				mFloatConstants.add(physicalIndex + i, new ENG_Float(val[i]));
			} else {
				ENG_Float f = mFloatConstants.get(physicalIndex + i);
				f.setValue(val[i]);
			}*/
//			assert (physicalIndex + i < mFloatConstants.length);
//			mFloatConstants[physicalIndex + i] = val[i];


//		}
    }

    public void _writeRawConstant(int physicalIndex, ENG_Float val) {
//		assert (physicalIndex < mFloatConstants.length);
//		mFloatConstants[physicalIndex] = val.getValue();
        assert (mFloatConstants != null &&
                physicalIndex < mFloatConstants.capacity());
        mFloatConstants.limit(physicalIndex + 1);
        mFloatConstants.put(physicalIndex, val.getValue());
	/*	if (physicalIndex >= mFloatConstants.size()) {
			mFloatConstants.add(physicalIndex, val);
		} else {
			mFloatConstants.get(physicalIndex).setValue(val);
		}*/
    }

    public void _writeRawConstants(int physicalIndex, ENG_Float[] val, int count) {
        assert (mFloatConstants != null &&
                physicalIndex + count < mFloatConstants.capacity());
        mFloatConstants.limit(physicalIndex + count);
        mFloatConstants.position(physicalIndex);
        for (int i = 0; i < count; ++i) {
            mFloatConstants.put(val[i].getValue());
        }
//		for (int i = 0; i < count; ++i) {
//			assert (physicalIndex + i < mFloatConstants.length);
//			mFloatConstants[physicalIndex + i] = val[i].getValue();
		/*	if (physicalIndex + i >= mFloatConstants.size()) {
				mFloatConstants.add(physicalIndex + i, val[i]);
			} else {
				ENG_Float f = mFloatConstants.get(physicalIndex + i);
				f.setValue(val[i]);
			}*/
//		}
    }

    public void _writeRawConstantsFloat(int physicalIndex, ArrayList<ENG_Float> val) {
        throw new UnsupportedAddressTypeException();
	/*	if (physicalIndex >= mFloatConstants.size()) {
			mFloatConstants.addAll(physicalIndex, val);
		} else {
			int limit = physicalIndex + val.size();
			if (limit >= mFloatConstants.size()) {
				//fuck it not used anyway
				throw new IllegalArgumentException();
			}
		}*/
    }

    public void _writeRawConstants(int physicalIndex, int[] val, int count) {
        assert (mIntConstants != null &&
                physicalIndex + count < mIntConstants.capacity());
        mIntConstants.limit(physicalIndex + count);
        mIntConstants.position(physicalIndex);
        mIntConstants.put(val, 0, count);
//		for (int i = 0; i < count; ++i) {
//			assert (physicalIndex + i < mIntConstants.length);
//			mIntConstants[physicalIndex + i] = val[i];
		/*	if (physicalIndex + i >= mIntConstants.size()) {
				mIntConstants.add(physicalIndex + i, new ENG_Integer(val[i]));
			} else {
				mIntConstants.get(physicalIndex + i).setValue(val[i]);
			}*/
//		}
    }

    public void _writeRawConstant(int physicalIndex, ENG_Integer val) {
//		assert (physicalIndex < mIntConstants.length);
//		mIntConstants[physicalIndex] = val.getValue();
        assert (mIntConstants != null &&
                physicalIndex < mIntConstants.capacity());
        mIntConstants.limit(physicalIndex + 1);
        mIntConstants.put(physicalIndex, val.getValue());
	/*	if (physicalIndex >= mIntConstants.size()) {
			mIntConstants.add(physicalIndex, val);
		} else {
			mIntConstants.get(physicalIndex).setValue(val);
		}*/
    }

    public void _writeRawConstants(int physicalIndex, ENG_Integer[] val, int count) {
        assert (mIntConstants != null &&
                physicalIndex + count < mIntConstants.capacity());
        mIntConstants.limit(physicalIndex + count);
        mIntConstants.position(physicalIndex);
        for (int i = 0; i < count; ++i) {
            mIntConstants.put(val[i].getValue());
        }
//		for (int i = 0; i < count; ++i) {
//			assert (physicalIndex + i < mIntConstants.length);
//			mIntConstants[physicalIndex + i] = val[i].getValue();
		/*	if (physicalIndex + i >= mIntConstants.size()) {
				mIntConstants.add(physicalIndex + i, val[i]);
			} else {
				mIntConstants.get(physicalIndex + i).setValue(val[i]);
			}*/
//		}
    }

    public void _writeRawConstantsInt(int physicalIndex, ArrayList<ENG_Integer> val) {
        throw new UnsupportedOperationException();
	/*	mIntConstants.addAll(physicalIndex, val);
		// fuck it again not used
		throw new IllegalArgumentException();*/
    }

    public void setNamedConstant(String name, float val) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val);
        }
    }

    public void setNamedConstant(String name, ENG_Float val) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val);
        }
    }

    public void setNamedConstant(String name, int val) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val);
        }
    }

    public void setNamedConstant(String name, ENG_Integer val) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val);
        }
    }

    public void setNamedConstant(String name, ENG_Vector3D val) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val, def.elementSize);
        }
    }

    public void setNamedConstant(String name, ENG_Vector4D val) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val);
        }
    }

    public void setNamedConstant(String name, ENG_Matrix4 val) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val, def.elementSize);
        }
    }

    /**
     * @param name
     * @param val
     * @param numEntries number of matrices
     */
    public void setNamedConstant(String name, ENG_Matrix4[] val, int numEntries) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val, numEntries);
        }
    }

    public void setNamedConstant(String name, float[] val, int count) {
        setNamedConstant(name, val, count, 4);
    }

    /**
     * @param name
     * @param val
     * @param count
     * @param multiple multiple as in how many per count
     */
    public void setNamedConstant(String name, float[] val, int count, int multiple) {
        int rawCount = count * multiple;
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstants(def.physicalIndex, val, rawCount);
        }
    }

    public void setNamedConstant(String name, ENG_Float[] val, int count) {
        setNamedConstant(name, val, count, 4);
    }

    public void setNamedConstant(String name, ENG_Float[] val, int count, int multiple) {
        int rawCount = count * multiple;
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstants(def.physicalIndex, val, rawCount);
        }
    }

    public void setNamedConstant(String name, int[] val, int count) {
        setNamedConstant(name, val, count, 4);
    }

    public void setNamedConstant(String name, int[] val, int count, int multiple) {
        int rawCount = count * multiple;
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstants(def.physicalIndex, val, rawCount);
        }
    }

    public void setNamedConstant(String name, ENG_Integer[] val,
                                 int count) {
        setNamedConstant(name, val, count, 4);
    }

    public void setNamedConstant(String name, ENG_Integer[] val,
                                 int count, int multiple) {
        int rawCount = count * multiple;
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstants(def.physicalIndex, val, rawCount);
        }
    }

    public void setNamedConstant(String name, ENG_ColorValue val) {

        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            _writeRawConstant(def.physicalIndex, val, def.elementSize);
        }
    }

    public void _setRawAutoConstant(int physicalIndex, AutoConstantType acType,
                                    int extraInfo, short variability, int elementSize) {
        boolean found = false;
        int len = mAutoConstants.size();
        for (int i = 0; i < len; ++i) {
            ENG_AutoConstantEntry type = mAutoConstants.get(i);
            if (type.physicalIndex == physicalIndex) {
                type.paramType = acType;
                type.data = extraInfo;
                type.variability = variability;
                type.elementCount = elementSize;
                found = true;
                break;
            }
        }
        if (!found) {
            mAutoConstants.add(new ENG_AutoConstantEntry(acType, physicalIndex, extraInfo, variability, elementSize));
        }
        mCombinedVariability |= variability;
    }

    public void _setRawAutoConstant(int physicalIndex, AutoConstantType acType,
                                    float rData, short variability, int elementSize) {
        boolean found = false;
        int len = mAutoConstants.size();
        for (int i = 0; i < len; ++i) {
            ENG_AutoConstantEntry type = mAutoConstants.get(physicalIndex);
            if (type.physicalIndex == physicalIndex) {
                type.paramType = acType;
                type.fdata = rData;
                type.variability = variability;
                type.elementCount = elementSize;
                found = true;
                break;
            }
        }
        if (!found) {
            mAutoConstants.add(new ENG_AutoConstantEntry(acType, physicalIndex, rData, variability, elementSize));
        }
        mCombinedVariability |= variability;
    }

    public void clearNameAutoConstant(String name) {
        ENG_GpuConstantDefinition def = _findNamedConstantDefinition(name);
        if (def != null) {
            def.variability = GpuParamVariability.GPV_GLOBAL.getVariability();
            if (def.isFloat()) {
                for (int i = 0; i < mAutoConstants.size(); ++i) {
                    if (mAutoConstants.get(i).physicalIndex == def.physicalIndex) {
                        mAutoConstants.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public void clearAutoConstants() {
        mAutoConstants.clear();
        mCombinedVariability = GpuParamVariability.GPV_GLOBAL.getVariability();
    }

    public void setNamedAutoConstant(String name, AutoConstantType acType,
                                     int extraInfo) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            def.variability = deriveVariability(acType);
            _setRawAutoConstant(def.physicalIndex, acType,
                    extraInfo, def.variability, def.elementSize);
        }
    }

    public void setNamedAutoConstant(String name, AutoConstantType acType,
                                     float extraInfo) {
        ENG_GpuConstantDefinition def =
                _findNamedConstantDefinition(name, !mIgnoreMissingParams);
        if (def != null) {
            def.variability = deriveVariability(acType);
            _setRawAutoConstant(def.physicalIndex, acType,
                    extraInfo, def.variability, def.elementSize);
        }
    }

    public void setNamedAutoConstant(String name, AutoConstantType acType,
                                     short extraInfo1, short extraInfo2) {
        int extraInfo = extraInfo1 | extraInfo2 << 16;
        setNamedAutoConstant(name, acType, extraInfo);
    }

    public void setNamedConstantFromTime(String name, float factor) {
        setNamedAutoConstant(name, AutoConstantType.ACT_TIME, factor);
    }

    public ENG_AutoConstantEntry getAutoConstantEntry(int index) {
        if (index < mAutoConstants.size()) {
            return mAutoConstants.get(index);
        } else {
            return null;
        }
    }

    public ENG_AutoConstantEntry _findRawAutoConstantEntryFloat(int physicalIndex) {
        int len = mAutoConstants.size();
        for (int i = 0; i < len; ++i) {
            ENG_AutoConstantEntry ac = mAutoConstants.get(physicalIndex);
            if (ac.physicalIndex == physicalIndex) {
                return ac;
            }
        }
        return null;
    }

    public ENG_AutoConstantEntry _findRawAutoConstantEntryInt(int physicalIndex) {
        return null;
    }

    public Iterator<Entry<String, ENG_GpuConstantDefinition>>
    getConstantDefinitionIterator() {
        if (mNamedConstants == null) {
            throw new NullPointerException(
                    "This params object is not based on a program with named parameters.");
        }
        return mNamedConstants.map.entrySet().iterator();
    }

    public ENG_GpuNamedConstants getConstantDefinitions() {
        if (mNamedConstants == null) {
            throw new NullPointerException(
                    "This params object is not based on a program with named parameters.");
        }
        return mNamedConstants;
    }

    public ENG_GpuConstantDefinition getConstantDefinition(String name) {
        if (mNamedConstants == null) {
            throw new NullPointerException(
                    "This params object is not based on a program with named parameters.");
        }
        return _findNamedConstantDefinition(name, true);
    }

    public ENG_AutoConstantEntry findAutoConstantEntry(String name) {
        if (mNamedConstants == null) {
            throw new NullPointerException(
                    "This params object is not based on a program with named parameters.");
        }
        ENG_GpuConstantDefinition def = getConstantDefinition(name);
        if (def.isFloat()) {
            return _findRawAutoConstantEntryFloat(def.physicalIndex);
        } else {
            return _findRawAutoConstantEntryInt(def.physicalIndex);
        }

    }

    public void copyConstantsFrom(ENG_GpuProgramParameters source) {
        mFloatConstants = source.getFloatPointerArray();
        mIntConstants = source.getIntPointerArray();
        mAutoConstants = source.getAutoConstantList();
        mCombinedVariability = source.mCombinedVariability;
        copySharedParamSetUsage(source.mSharedParamSets);
    }

    public void copyMatchingNamedConstantsFrom(ENG_GpuProgramParameters source) {
        if ((mNamedConstants != null) && (source.mNamedConstants != null)) {
            TreeMap<ENG_Integer, String> srcToDestNamedMap =
                    new TreeMap<>();
            for (Entry<String, ENG_GpuConstantDefinition> entry : source.mNamedConstants.map.entrySet()) {
                String paramName = entry.getKey();
                ENG_GpuConstantDefinition olddef = entry.getValue();
                ENG_GpuConstantDefinition newdef =
                        _findNamedConstantDefinition(paramName, false);
                if (newdef != null) {
                    int srcsz = olddef.elementSize * olddef.arraySize;
                    int destsz = newdef.elementSize * newdef.arraySize;
                    int sz = Math.min(srcsz, destsz);
                    if (newdef.isFloat()) {
                        FloatBuffer f = getFloatPointerArray();
                        f.limit(f.capacity());
                        for (int j = 0; j < sz; ++j) {
//							float[] f = getFloatPointer();

//							f[newdef.physicalIndex + j] = f[olddef.physicalIndex + j];
                            f.put(newdef.physicalIndex + j, f.get(olddef.physicalIndex + j));
						/*	getFloatPointer(
									newdef.physicalIndex + j).setValue(
											getFloatPointer(olddef.physicalIndex + j));*/
                        }
                    } else {
                        IntBuffer f = getIntPointerArray();
                        f.limit(f.capacity());
                        for (int j = 0; j < sz; ++j) {
//							int[] f = getIntPointer();

//							f[newdef.physicalIndex + j] = f[olddef.physicalIndex + j];
                            f.put(newdef.physicalIndex + j, f.get(olddef.physicalIndex + j));
						/*	getIntPointer(
									newdef.physicalIndex + j).setValue(
											getIntPointer(olddef.physicalIndex + j));*/
                        }
                    }

                    if (!paramName.endsWith("[0]")) {
                        srcToDestNamedMap.put(
                                new ENG_Integer(olddef.physicalIndex), paramName);
                    }
                }
            }
            int len = source.mAutoConstants.size();
            for (int i = 0; i < len; ++i) {
                ENG_AutoConstantEntry autoEntry = source.mAutoConstants.get(i);
                String mi = srcToDestNamedMap.get(
                        new ENG_Integer(autoEntry.physicalIndex));
                if (mi != null) {
                    if (autoEntry.fdata != 0.0f) {
                        setNamedAutoConstant(mi, autoEntry.paramType, autoEntry.fdata);
                    } else {
                        setNamedAutoConstant(mi, autoEntry.paramType, autoEntry.data);
                    }
                }
            }

            len = source.mSharedParamSets.size();
            for (int i = 0; i < len; ++i) {
                ENG_GpuSharedParametersUsage usage = source.mSharedParamSets.get(i);
                if (!isUsingSharedParameters(usage.getName())) {
                    addSharedParameters(usage.getSharedParams());
                }
            }
        }
    }

    public void addSharedParameters(ENG_GpuSharedParameters sharedParams) {
        if (!isUsingSharedParameters(sharedParams.getName())) {
            mSharedParamSets.add(new ENG_GpuSharedParametersUsage(sharedParams, this));
        }
    }

    public void removeSharedParameters(String sharedParamsName) {
        int len = mSharedParamSets.size();
        for (int i = 0; i < len; ++i) {
            ENG_GpuSharedParametersUsage sharedParametersUsage = mSharedParamSets.get(i);
            if (sharedParametersUsage.getName().equals(sharedParamsName)) {
                mSharedParamSets.remove(i);
                break;
            }
        }
    }

    public void removeAllSharedParameters() {
        mSharedParamSets.clear();
    }

    public ArrayList<ENG_GpuSharedParametersUsage> getSharedParameters() {
        return mSharedParamSets;
    }

    public void _copySharedParams() {
        int len = mSharedParamSets.size();
        for (int i = 0; i < len; ++i) {
            mSharedParamSets.get(i)._copySharedParamsToTargetParams();
        }
    }

    public boolean isUsingSharedParameters(String name) {
        int len = mSharedParamSets.size();
        for (int i = 0; i < len; ++i) {
            ENG_GpuSharedParametersUsage parametersUsage = mSharedParamSets.get(i);
            if (parametersUsage.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAutoConstants() {
        return !mAutoConstants.isEmpty();
    }

    public void _updateAutoParams(ENG_AutoParamDataSource source, short mask) {
        if (!hasAutoConstants()) {
            return;
        }

        if ((mCombinedVariability & mask) == 0) {
            return;
        }

        int index;
        int numMatrices;
        int m;
        ENG_Matrix4[] pMatrix;

        mActivePassIterationIndex = Integer.MAX_VALUE;

        int len = mAutoConstants.size();
        for (int ind = 0; ind < len; ++ind) {
            ENG_AutoConstantEntry i = mAutoConstants.get(ind);

            if ((i.variability & mask) != 0) {
                switch (i.paramType) {
                    case ACT_VIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getViewMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_VIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseViewMatrix(), i.elementCount);
                        break;
                    case ACT_TRANSPOSE_VIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getTransposeViewMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_TRANSPOSE_VIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseTransposeViewMatrix(), i.elementCount);
                        break;

                    case ACT_PROJECTION_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getProjectionMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_PROJECTION_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseProjectionMatrix(), i.elementCount);
                        break;
                    case ACT_TRANSPOSE_PROJECTION_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getTransposeProjectionMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_TRANSPOSE_PROJECTION_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseTransposeProjectionMatrix(), i.elementCount);
                        break;

                    case ACT_VIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getViewProjectionMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_VIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseViewProjMatrix(), i.elementCount);
                        break;
                    case ACT_TRANSPOSE_VIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getTransposeViewProjMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_TRANSPOSE_VIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseTransposeViewProjMatrix(), i.elementCount);
                        break;
                    case ACT_RENDER_TARGET_FLIPPING:
                        _writeRawConstant(i.physicalIndex, source.getCurrentRenderTarget().requiresTextureFlipping() ? -1.f : +1.f);
                        break;
                    case ACT_VERTEX_WINDING: {
                        ENG_RenderSystem rsys = ENG_RenderRoot.getRenderRoot().getRenderSystem();
                        _writeRawConstant(i.physicalIndex, rsys.getVertexWindingInverted() ? -1.f : +1.f);
                    }
                    break;

                    // NB ambient light still here because it's not related to a specific light
                    case ACT_AMBIENT_LIGHT_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getAmbientLightColour(),
                                i.elementCount);
                        break;
                    case ACT_DERIVED_AMBIENT_LIGHT_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getDerivedAmbientLightColour(),
                                i.elementCount);
                        break;
                    case ACT_DERIVED_SCENE_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getDerivedSceneColour(),
                                i.elementCount);
                        break;

                    case ACT_FOG_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getFogColour());
                        break;
                    case ACT_FOG_PARAMS:
                        _writeRawConstant(i.physicalIndex, source.getFogParams(), i.elementCount);
                        break;

                    case ACT_SURFACE_AMBIENT_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getSurfaceAmbientColour(),
                                i.elementCount);
                        break;
                    case ACT_SURFACE_DIFFUSE_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getSurfaceDiffuseColour(),
                                i.elementCount);
                        break;
                    case ACT_SURFACE_SPECULAR_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getSurfaceSpecularColour(),
                                i.elementCount);
                        break;
                    case ACT_SURFACE_EMISSIVE_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getSurfaceEmissiveColour(),
                                i.elementCount);
                        break;
                    case ACT_SURFACE_SHININESS:
                        _writeRawConstant(i.physicalIndex, source.getSurfaceShininess());
                        break;
                    case ACT_CAMERA_POSITION:
                        _writeRawConstant(i.physicalIndex, source.getCameraPosition(), i.elementCount);
                        break;
                    case ACT_TIME:
                        _writeRawConstant(i.physicalIndex, source.getTime() * i.fdata);
                        break;
                    case ACT_TIME_0_X:
                        _writeRawConstant(i.physicalIndex, source.getTime_0_X(i.fdata));
                        break;
                    case ACT_COSTIME_0_X:
                        _writeRawConstant(i.physicalIndex, source.getCosTime_0_X(i.fdata));
                        break;
                    case ACT_SINTIME_0_X:
                        _writeRawConstant(i.physicalIndex, source.getSinTime_0_X(i.fdata));
                        break;
                    case ACT_TANTIME_0_X:
                        _writeRawConstant(i.physicalIndex, source.getTanTime_0_X(i.fdata));
                        break;
                    case ACT_TIME_0_X_PACKED:
                        _writeRawConstant(i.physicalIndex, source.getTime_0_X_packed(i.fdata), i.elementCount);
                        break;
                    case ACT_TIME_0_1:
                        _writeRawConstant(i.physicalIndex, source.getTime_0_1(i.fdata));
                        break;
                    case ACT_COSTIME_0_1:
                        _writeRawConstant(i.physicalIndex, source.getCosTime_0_1(i.fdata));
                        break;
                    case ACT_SINTIME_0_1:
                        _writeRawConstant(i.physicalIndex, source.getSinTime_0_1(i.fdata));
                        break;
                    case ACT_TANTIME_0_1:
                        _writeRawConstant(i.physicalIndex, source.getTanTime_0_1(i.fdata));
                        break;
                    case ACT_TIME_0_1_PACKED:
                        _writeRawConstant(i.physicalIndex, source.getTime_0_1_packed(i.fdata), i.elementCount);
                        break;
                    case ACT_TIME_0_2PI:
                        _writeRawConstant(i.physicalIndex, source.getTime_0_2Pi(i.fdata));
                        break;
                    case ACT_COSTIME_0_2PI:
                        _writeRawConstant(i.physicalIndex, source.getCosTime_0_2Pi(i.fdata));
                        break;
                    case ACT_SINTIME_0_2PI:
                        _writeRawConstant(i.physicalIndex, source.getSinTime_0_2Pi(i.fdata));
                        break;
                    case ACT_TANTIME_0_2PI:
                        _writeRawConstant(i.physicalIndex, source.getTanTime_0_2Pi(i.fdata));
                        break;
                    case ACT_TIME_0_2PI_PACKED:
                        _writeRawConstant(i.physicalIndex, source.getTime_0_2Pi_packed(i.fdata), i.elementCount);
                        break;
                    case ACT_FRAME_TIME:
                        _writeRawConstant(i.physicalIndex, source.getFrameTime() * i.fdata);
                        break;
                    case ACT_FPS:
                        _writeRawConstant(i.physicalIndex, source.getFPS());
                        break;
                    case ACT_VIEWPORT_WIDTH:
                        _writeRawConstant(i.physicalIndex, source.getViewportWidth());
                        break;
                    case ACT_VIEWPORT_HEIGHT:
                        _writeRawConstant(i.physicalIndex, source.getViewportHeight());
                        break;
                    case ACT_INVERSE_VIEWPORT_WIDTH:
                        _writeRawConstant(i.physicalIndex, source.getInverseViewportWidth());
                        break;
                    case ACT_INVERSE_VIEWPORT_HEIGHT:
                        _writeRawConstant(i.physicalIndex, source.getInverseViewportHeight());
                        break;
                    case ACT_VIEWPORT_SIZE:
                        _writeRawConstant(i.physicalIndex, new ENG_Vector4D(
                                source.getViewportWidth(),
                                source.getViewportHeight(),
                                source.getInverseViewportWidth(),
                                source.getInverseViewportHeight()), i.elementCount);
                        break;
                    case ACT_TEXEL_OFFSETS: {
                        ENG_RenderSystem rsys = ENG_RenderRoot.getRenderRoot().getRenderSystem();
                        _writeRawConstant(i.physicalIndex, new ENG_Vector4D(
                                        rsys.getHorizontalTexelOffset(),
                                        rsys.getVerticalTexelOffset(),
                                        rsys.getHorizontalTexelOffset() * source.getInverseViewportWidth(),
                                        rsys.getVerticalTexelOffset() * source.getInverseViewportHeight()),
                                i.elementCount);
                    }
                    break;
                    case ACT_TEXTURE_SIZE:
                        _writeRawConstant(i.physicalIndex, source.getTextureSize(i.data), i.elementCount);
                        break;
                    case ACT_INVERSE_TEXTURE_SIZE:
                        _writeRawConstant(i.physicalIndex, source.getInverseTextureSize(i.data), i.elementCount);
                        break;
                    case ACT_PACKED_TEXTURE_SIZE:
                        _writeRawConstant(i.physicalIndex, source.getPackedTextureSize(i.data), i.elementCount);
                        break;
                    case ACT_SCENE_DEPTH_RANGE:
                        _writeRawConstant(i.physicalIndex, source.getSceneDepthRange(), i.elementCount);
                        break;
                    case ACT_VIEW_DIRECTION:
                        _writeRawConstant(i.physicalIndex, source.getViewDirection());
                        break;
                    case ACT_VIEW_SIDE_VECTOR:
                        _writeRawConstant(i.physicalIndex, source.getViewSideVector());
                        break;
                    case ACT_VIEW_UP_VECTOR:
                        _writeRawConstant(i.physicalIndex, source.getViewUpVector());
                        break;
                    case ACT_FOV:
                        _writeRawConstant(i.physicalIndex, source.getFOV());
                        break;
                    case ACT_NEAR_CLIP_DISTANCE:
                        _writeRawConstant(i.physicalIndex, source.getNearClipDistance());
                        break;
                    case ACT_FAR_CLIP_DISTANCE:
                        _writeRawConstant(i.physicalIndex, source.getFarClipDistance());
                        break;
                    case ACT_PASS_NUMBER:
                        _writeRawConstant(i.physicalIndex, (float) source.getPassNumber());
                        break;
                    case ACT_PASS_ITERATION_NUMBER:
                        // this is actually just an initial set-up, it's bound separately, so still global
                        _writeRawConstant(i.physicalIndex, 0.0f);
                        mActivePassIterationIndex = i.physicalIndex;
                        break;
                    case ACT_TEXTURE_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getTextureTransformMatrix(i.data), i.elementCount);
                        break;
                    case ACT_LOD_CAMERA_POSITION:
                        _writeRawConstant(i.physicalIndex, source.getLodCameraPosition(), i.elementCount);
                        break;
                    case ACT_TEXTURE_WORLDVIEWPROJ_MATRIX:
                        // can also be updated in lights
                        _writeRawConstant(i.physicalIndex, source.getTextureWorldViewProjMatrix(i.data), i.elementCount);
                        break;
                    case ACT_TEXTURE_WORLDVIEWPROJ_MATRIX_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            // can also be updated in lights
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getTextureWorldViewProjMatrix(l), i.elementCount);
                        }
                        break;
                    case ACT_SPOTLIGHT_WORLDVIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getSpotlightWorldViewProjMatrix(i.data), i.elementCount);
                        break;
                    case ACT_LIGHT_POSITION_OBJECT_SPACE:
                        source.getLightAs4DVector(i.data, vec4);
                        vec3.set(vec4.x, vec4.y, vec4.z, 0.0f);
                        if (vec4.w > 0.0f) {
                            // point light
                            vec3 = source.getInverseWorldMatrix().transformAffineRet(vec3);
                        } else {
                            // directional light
                            // We need the inverse of the inverse transpose
                            source.getInverseTransposeWorldMatrix().invert(m4);
                            m4.transform(vec3);
                            vec3.normalize();
                        }
                        _writeRawConstant(i.physicalIndex,
                                new ENG_Vector4D(vec3.x, vec3.y, vec3.z, vec4.w),
                                i.elementCount);
                        break;
                    case ACT_LIGHT_DIRECTION_OBJECT_SPACE:
                        // We need the inverse of the inverse transpose
                        source.getInverseTransposeWorldMatrix().invert();
                        m4.transform(source.getLightDirection(i.data), vec3);
                        vec3.normalize();
                        // Set as 4D vector for compatibility
                        _writeRawConstant(i.physicalIndex, new ENG_Vector4D(vec3.x, vec3.y, vec3.z, 0.0f), i.elementCount);
                        break;
                    case ACT_LIGHT_DISTANCE_OBJECT_SPACE:
                        source.getInverseWorldMatrix().transformAffine(source.getLightPosition(i.data), vec3);
                        _writeRawConstant(i.physicalIndex, vec3.length());
                        break;
                    case ACT_LIGHT_POSITION_OBJECT_SPACE_ARRAY:
                        // We need the inverse of the inverse transpose
                        source.getInverseTransposeWorldMatrix().invert(m4);
                        for (int l = 0; l < i.data; ++l) {
                            source.getLightAs4DVector(l, vec4);
                            vec3 = new ENG_Vector4D(vec4.x, vec4.y, vec4.z, 0.0f);
                            if (vec4.w > 0.0f) {
                                // point light
                                source.getInverseWorldMatrix().transformAffine(vec3);
                            } else {
                                // directional light
                                m4.transform(vec3);
                                vec3.normalize();
                                //vec3 = (m3 * vec3).normalisedCopy();
                            }
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    new ENG_Vector4D(vec3.x, vec3.y, vec3.z, vec4.w),
                                    i.elementCount);
                        }
                        break;

                    case ACT_LIGHT_DIRECTION_OBJECT_SPACE_ARRAY:
                        // We need the inverse of the inverse transpose
                        source.getInverseTransposeWorldMatrix().invert(m4);
                        for (int l = 0; l < i.data; ++l) {
                            source.getLightDirection(l, vec3);
                            m4.transform(vec3);
                            vec3.normalize();
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    new ENG_Vector4D(vec3.x, vec3.y, vec3.z, 0.0f), i.elementCount);
                        }
                        break;

                    case ACT_LIGHT_DISTANCE_OBJECT_SPACE_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            source.getLightPosition(l, vec3);
                            source.getInverseWorldMatrix().transformAffine(vec3);
                            _writeRawConstant(i.physicalIndex + l * i.elementCount, vec3.length());
                        }
                        break;
				/*case ACT_LIGHT_DISTANCE_OBJECT_SPACE_ARRAY:
					for (int l = 0; l < i.data; ++l)
					{
						source.getLightPosition(l, vec3);
						source.getInverseWorldMatrix().transformAffine(vec3);
						_writeRawConstant(i.physicalIndex + l * i.elementCount, vec3.length());
					}
					break;*/

                    case ACT_WORLD_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getWorldMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_WORLD_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseWorldMatrix(), i.elementCount);
                        break;
                    case ACT_TRANSPOSE_WORLD_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getTransposeWorldMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_TRANSPOSE_WORLD_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseTransposeWorldMatrix(), i.elementCount);
                        break;
                    case ACT_WORLD_MATRIX_ARRAY_3x4:
                        // Loop over matrices
                        pMatrix = source.getWorldMatrixArray();
                        numMatrices = source.getWorldMatrixCount();
                        index = i.physicalIndex;
                        for (m = 0; m < numMatrices; ++m) {
                            _writeRawConstants(index, pMatrix[m].get(), 12);
                            index += 12;
                            //++pMatrix;
                        }
                        break;
                    case ACT_WORLD_MATRIX_ARRAY:
                        _writeRawConstant(i.physicalIndex, source.getWorldMatrixArray(),
                                source.getWorldMatrixCount());
                        break;
                    case ACT_WORLDVIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getWorldViewMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_WORLDVIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseWorldViewMatrix(), i.elementCount);
                        break;
                    case ACT_TRANSPOSE_WORLDVIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getTransposeWorldViewMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_TRANSPOSE_WORLDVIEW_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseTransposeWorldViewMatrix(), i.elementCount);
                        break;

                    case ACT_WORLDVIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getWorldViewProjMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_WORLDVIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseWorldViewProjMatrix(), i.elementCount);
                        break;
                    case ACT_TRANSPOSE_WORLDVIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getTransposeWorldViewProjMatrix(), i.elementCount);
                        break;
                    case ACT_INVERSE_TRANSPOSE_WORLDVIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getInverseTransposeWorldViewProjMatrix(), i.elementCount);
                        break;
                    case ACT_CAMERA_POSITION_OBJECT_SPACE:
                        _writeRawConstant(i.physicalIndex, source.getCameraPositionObjectSpace(), i.elementCount);
                        break;
                    case ACT_LOD_CAMERA_POSITION_OBJECT_SPACE:
                        _writeRawConstant(i.physicalIndex, source.getLodCameraPositionObjectSpace(), i.elementCount);
                        break;
                    case ACT_CUSTOM:
                    case ACT_ANIMATION_PARAMETRIC:
                        source.getCurrentRenderable()._updateCustomGpuParameter(i, this);
                        break;
                    case ACT_LIGHT_CUSTOM:
                        source.updateLightCustomGpuParameter(i, this);
                        break;
                    case ACT_LIGHT_COUNT:
                        _writeRawConstant(i.physicalIndex, source.getLightCount());
                        break;
                    case ACT_LIGHT_DIFFUSE_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getLightDiffuseColour(i.data), i.elementCount);
                        break;
                    case ACT_LIGHT_SPECULAR_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getLightSpecularColour(i.data), i.elementCount);
                        break;
                    case ACT_LIGHT_POSITION:
                        // Get as 4D vector, works for directional lights too
                        // Use element count in case uniform slot is smaller
                        _writeRawConstant(i.physicalIndex,
                                source.getLightAs4DVector(i.data), i.elementCount);
                        break;
                    case ACT_LIGHT_DIRECTION:
                        vec3 = source.getLightDirection(i.data);
                        // Set as 4D vector for compatibility
                        // Use element count in case uniform slot is smaller
                        _writeRawConstant(i.physicalIndex, new ENG_Vector4D(vec3.x, vec3.y, vec3.z, 1.0f), i.elementCount);
                        break;
                    case ACT_LIGHT_POSITION_VIEW_SPACE:
                        _writeRawConstant(i.physicalIndex,
                                source.getViewMatrix().transformAffineRet(source.getLightAs4DVector(i.data)), i.elementCount);
                        break;
                    case ACT_LIGHT_DIRECTION_VIEW_SPACE:
                        //source.getInverseTransposeViewMatrix();
                        // inverse transpose in case of scaling
                        source.getInverseTransposeViewMatrix().transform(source.getLightDirection(i.data), vec3);
                        vec3.normalize();
                        // Set as 4D vector for compatibility
                        _writeRawConstant(i.physicalIndex, new ENG_Vector4D(vec3.x, vec3.y, vec3.z, 0.0f), i.elementCount);
                        break;
                    case ACT_SHADOW_EXTRUSION_DISTANCE:
                        // extrusion is in object-space, so we have to rescale by the inverse
                        // of the world scaling to deal with scaled objects
                        ENG_Matrix4 mat = source.getWorldMatrix();
                        _writeRawConstant(i.physicalIndex, source.getShadowExtrusionDistance() /
                                ENG_Math.sqrt(Math.max(Math.max(mat.getColumnAsVec4(0).squaredLength(), mat.getColumnAsVec4(1).squaredLength()), mat.getColumnAsVec4(2).squaredLength())));
                        break;
                    case ACT_SHADOW_SCENE_DEPTH_RANGE:
                        _writeRawConstant(i.physicalIndex, source.getShadowSceneDepthRange(i.data));
                        break;
                    case ACT_SHADOW_COLOUR:
                        _writeRawConstant(i.physicalIndex, source.getShadowColour(), i.elementCount);
                        break;
                    case ACT_LIGHT_POWER_SCALE:
                        _writeRawConstant(i.physicalIndex, source.getLightPowerScale(i.data));
                        break;
                    case ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED:
                        _writeRawConstant(i.physicalIndex, source.getLightDiffuseColourWithPower(i.data), i.elementCount);
                        break;
                    case ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED:
                        _writeRawConstant(i.physicalIndex, source.getLightSpecularColourWithPower(i.data), i.elementCount);
                        break;
                    case ACT_LIGHT_NUMBER:
                        _writeRawConstant(i.physicalIndex, source.getLightNumber(i.data));
                        break;
                    case ACT_LIGHT_CASTS_SHADOWS:
                        _writeRawConstant(i.physicalIndex, source.getLightCastsShadows(i.data));
                        break;
                    case ACT_LIGHT_ATTENUATION:
                        _writeRawConstant(i.physicalIndex, source.getLightAttenuation(i.data), i.elementCount);
                        break;
                    case ACT_SPOTLIGHT_PARAMS:
                        _writeRawConstant(i.physicalIndex, source.getSpotlightParams(i.data), i.elementCount);
                        break;
                    case ACT_LIGHT_DIFFUSE_COLOUR_ARRAY:
                        for (int l = 0; l < i.data; ++l)
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightDiffuseColour(l), i.elementCount);
                        break;

                    case ACT_LIGHT_SPECULAR_COLOUR_ARRAY:
                        for (int l = 0; l < i.data; ++l)
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightSpecularColour(l), i.elementCount);
                        break;
                    case ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED_ARRAY:
                        for (int l = 0; l < i.data; ++l)
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightDiffuseColourWithPower(l), i.elementCount);
                        break;

                    case ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED_ARRAY:
                        for (int l = 0; l < i.data; ++l)
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightSpecularColourWithPower(l), i.elementCount);
                        break;

                    case ACT_LIGHT_POSITION_ARRAY:
                        // Get as 4D vector, works for directional lights too
                        for (int l = 0; l < i.data; ++l)
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightAs4DVector(l), i.elementCount);
                        break;

                    case ACT_LIGHT_DIRECTION_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            source.getLightDirection(l, vec3);
                            // Set as 4D vector for compatibility
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    new ENG_Vector4D(vec3.x, vec3.y, vec3.z, 0.0f), i.elementCount);
                        }
                        break;

                    case ACT_LIGHT_POSITION_VIEW_SPACE_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            source.getViewMatrix().transformAffine(
                                    source.getLightAs4DVector(l), vec3);
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    vec3,
                                    i.elementCount);
                        }
                        break;

                    case ACT_LIGHT_DIRECTION_VIEW_SPACE_ARRAY:

                        for (int l = 0; l < i.data; ++l) {
                            source.getInverseTransposeViewMatrix().transform(source.getLightDirection(l), vec3);
                            vec3.normalize();
                            // Set as 4D vector for compatibility
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    new ENG_Vector4D(vec3.x, vec3.y, vec3.z, 0.0f), i.elementCount);
                        }
                        break;

                    case ACT_LIGHT_POWER_SCALE_ARRAY:
                        for (int l = 0; l < i.data; ++l)
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightPowerScale(l));
                        break;

                    case ACT_LIGHT_ATTENUATION_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightAttenuation(l), i.elementCount);
                        }
                        break;
                    case ACT_SPOTLIGHT_PARAMS_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            _writeRawConstant(i.physicalIndex + l * i.elementCount, source.getSpotlightParams(l),
                                    i.elementCount);
                        }
                        break;

                    case ACT_DERIVED_LIGHT_DIFFUSE_COLOUR:
                        _writeRawConstant(i.physicalIndex,
                                source.getLightDiffuseColourWithPower(i.data).mul(source.getSurfaceDiffuseColour()),
                                i.elementCount);
                        break;
                    case ACT_DERIVED_LIGHT_SPECULAR_COLOUR:
                        _writeRawConstant(i.physicalIndex,
                                source.getLightSpecularColourWithPower(i.data).mul(source.getSurfaceSpecularColour()),
                                i.elementCount);
                        break;
                    case ACT_DERIVED_LIGHT_DIFFUSE_COLOUR_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightDiffuseColourWithPower(l).mul(source.getSurfaceDiffuseColour()),
                                    i.elementCount);
                        }
                        break;
                    case ACT_DERIVED_LIGHT_SPECULAR_COLOUR_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getLightSpecularColourWithPower(l).mul(source.getSurfaceSpecularColour()),
                                    i.elementCount);
                        }
                        break;
                    case ACT_TEXTURE_VIEWPROJ_MATRIX:
                        // can also be updated in lights
                        _writeRawConstant(i.physicalIndex, source.getTextureViewProjMatrix(i.data), i.elementCount);
                        break;
                    case ACT_TEXTURE_VIEWPROJ_MATRIX_ARRAY:
                        for (int l = 0; l < i.data; ++l) {
                            // can also be updated in lights
                            _writeRawConstant(i.physicalIndex + l * i.elementCount,
                                    source.getTextureViewProjMatrix(l), i.elementCount);
                        }
                        break;
                    case ACT_SPOTLIGHT_VIEWPROJ_MATRIX:
                        _writeRawConstant(i.physicalIndex, source.getSpotlightViewProjMatrix(i.data), i.elementCount);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public long getPointer() {
        return ptr[0];
    }

    @Override
    public void setPointer(long ptr) {
        this.ptr[0] = ptr;
    }

    @Override
    public boolean isNativePointerSet() {
        return nativePtrSet;
    }

    @Override
    public void setNativePointer(boolean set) {
        nativePtrSet = set;
    }

}
