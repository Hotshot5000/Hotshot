/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource.shadertype;

public class ENG_ShaderTypeStandard {

    public static final int ACT_WORLD_MATRIX = 1;
    public static final int ACT_INVERSE_WORLD_MATRIX = 2;
    public static final int ACT_TRANSPOSE_WORLD_MATRIX = 3;
    public static final int ACT_INVERSE_TRANSPOSE_WORLD_MATRIX = 4;
    public static final int ACT_WORLD_MATRIX_ARRAY_3X4 = 124; //FORGOT ABOUT
    public static final int ACT_VIEW_MATRIX = 5;
    public static final int ACT_INVERSE_VIEW_MATRIX = 6;
    public static final int ACT_TRANSPOSE_VIEW_MATRIX = 7;
    public static final int ACT_INVERSE_TRANSPOSE_VIEW_MATRIX = 8;
    public static final int ACT_PROJECTION_MATRIX = 9;
    public static final int ACT_INVERSE_PROJECTION_MATRIX = 10;
    public static final int ACT_TRANSPOSE_PROJECTION_MATRIX = 11;
    public static final int ACT_INVERSE_TRANSPOSE_PROJECTION_MATRIX = 12;
    public static final int ACT_WORLDVIEW_MATRIX = 13;
    public static final int ACT_INVERSE_WORLDVIEW_MATRIX = 14;
    public static final int ACT_TRANSPOSE_WORLDVIEW_MATRIX = 15;
    public static final int ACT_INVERSE_TRANSPOSE_WORLDVIEW_MATRIX = 16;
    public static final int ACT_VIEWPROJ_MATRIX = 17;
    public static final int ACT_INVERSE_VIEWPROJ_MATRIX = 18;
    public static final int ACT_TRANSPOSE_VIEWPROJ_MATRIX = 19;
    public static final int ACT_INVERSE_TRANSPOSE_VIEWPROJ_MATRIX = 20;
    public static final int ACT_WORLDVIEWPROJ_MATRIX = 21;
    public static final int ACT_INVERSE_WORLDVIEWPROJ_MATRIX = 22;
    public static final int ACT_TRANSPOSE_WORLDVIEWPROJ_MATRIX = 23;
    public static final int ACT_INVERSE_TRANSPOSE_WORLDVIEWPROJ_MATRIX = 24;
    public static final int ACT_TEXTURE_MATRIX = 25;
    public static final int ACT_RENDER_TARGET_FLIPPING = 26;
    public static final int ACT_VERTEX_WINDING = 27;
    public static final int ACT_LIGHT_DIFFUSE_COLOUR = 28;
    public static final int ACT_LIGHT_SPECULAR_COLOUR = 29;
    public static final int ACT_LIGHT_ATTENUATION = 30;
    public static final int ACT_SPOTLIGHT_PARAMS = 31;
    public static final int ACT_LIGHT_POSITION = 32;
    public static final int ACT_LIGHT_DIRECTION = 33;
    public static final int ACT_LIGHT_POSITION_OBJECT_SPACE = 34;
    public static final int ACT_LIGHT_DIRECTION_OBJECT_SPACE = 35;
    public static final int ACT_LIGHT_DISTANCE_OBJECT_SPACE = 36;
    public static final int ACT_LIGHT_POSITION_VIEW_SPACE = 37;
    public static final int ACT_LIGHT_DIRECTION_VIEW_SPACE = 38;
    public static final int ACT_LIGHT_POWER = 39;
    public static final int ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED = 40;
    public static final int ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED = 41;
    public static final int ACT_LIGHT_NUMBER = 42;
    public static final int ACT_LIGHT_DIFFUSE_COLOUR_ARRAY = 43;
    public static final int ACT_LIGHT_SPECULAR_COLOUR_ARRAY = 44;
    public static final int ACT_LIGHT_DIFFUSE_COLOUR_POWER_SCALED_ARRAY = 45;
    public static final int ACT_LIGHT_SPECULAR_COLOUR_POWER_SCALED_ARRAY = 46;
    public static final int ACT_LIGHT_ATTENUATION_ARRAY = 47;
    public static final int ACT_SPOTLIGHT_PARAMS_ARRAY = 48;
    public static final int ACT_LIGHT_POSITION_ARRAY = 49;
    public static final int ACT_LIGHT_DIRECTION_ARRAY = 50;
    public static final int ACT_LIGHT_POSITION_OBJECT_SPACE_ARRAY = 51;
    public static final int ACT_LIGHT_DIRECTION_OBJECT_SPACE_ARRAY = 52;
    public static final int ACT_LIGHT_DISTANCE_OBJECT_SPACE_ARRAY = 53;
    public static final int ACT_LIGHT_POSITION_VIEW_SPACE_ARRAY = 54;
    public static final int ACT_LIGHT_DIRECTION_VIEW_SPACE_ARRAY = 55;
    public static final int ACT_LIGHT_POWER_ARRAY = 56;
    public static final int ACT_LIGHT_COUNT = 57;
    public static final int ACT_LIGHT_CASTS_SHADOWS = 58;
    public static final int ACT_AMBIENT_LIGHT_COLOUR = 59;
    public static final int ACT_SURFACE_AMBIENT_COLOUR = 60;
    public static final int ACT_SURFACE_DIFFUSE_COLOUR = 61;
    public static final int ACT_SURFACE_SPECULAR_COLOUR = 62;
    public static final int ACT_SURFACE_EMISSIVE_COLOUR = 63;
    public static final int ACT_SURFACE_SHININESS = 64;
    public static final int ACT_DERIVED_AMBIENT_LIGHT_COLOUR = 65;
    public static final int ACT_DERIVED_SCENE_COLOUR = 66;
    public static final int ACT_DERIVED_LIGHT_DIFFUSE_COLOUR = 67;
    public static final int ACT_DERIVED_LIGHT_SPECULAR_COLOUR = 68;
    public static final int ACT_DERIVED_LIGHT_DIFFUSE_COLOUR_ARRAY = 69;
    public static final int ACT_DERIVED_LIGHT_SPECULAR_COLOUR_ARRAY = 70;
    public static final int ACT_FOG_COLOUR = 71;
    public static final int ACT_FOG_PARAMS = 72;
    public static final int ACT_CAMERA_POSITION = 73;
    public static final int ACT_CAMERA_POSITION_OBJECT_SPACE = 74;
    public static final int ACT_LOD_CAMERA_POSITION = 75;
    public static final int ACT_LOD_CAMERA_POSITION_OBJECT_SPACE = 76;
    public static final int ACT_TIME = 77;
    public static final int ACT_TIME_0_X = 78;
    public static final int ACT_COSTIME_0_X = 79;
    public static final int ACT_SINTIME_0_X = 80;
    public static final int ACT_TANTIME_0_X = 81;
    public static final int ACT_TIME_0_X_PACKED = 82;
    public static final int ACT_TIME_0_1 = 83;
    public static final int ACT_COSTIME_0_1 = 84;
    public static final int ACT_SINTIME_0_1 = 85;
    public static final int ACT_TANTIME_0_1 = 86;
    public static final int ACT_TIME_0_1_PACKED = 87;
    public static final int ACT_TIME_0_2PI = 88;
    public static final int ACT_COSTIME_0_2PI = 89;
    public static final int ACT_SINTIME_0_2PI = 90;
    public static final int ACT_TANTIME_0_2PI = 91;
    public static final int ACT_TIME_0_2PI_PACKED = 92;
    public static final int ACT_FRAME_TIME = 93;
    public static final int ACT_FPS = 94;
    public static final int ACT_VIEWPORT_WIDTH = 95;
    public static final int ACT_VIEWPORT_HEIGHT = 96;
    public static final int ACT_INVERSE_VIEWPORT_WIDTH = 97;
    public static final int ACT_INVERSE_VIEWPORT_HEIGHT = 98;
    public static final int ACT_VIEWPORT_SIZE = 99;
    public static final int ACT_TEXEL_OFFSETS = 100;
    public static final int ACT_VIEW_DIRECTION = 101;
    public static final int ACT_VIEW_SIDE_VECTOR = 102;
    public static final int ACT_VIEW_UP_VECTOR = 103;
    public static final int ACT_FOV = 104;
    public static final int ACT_NEAR_CLIP_DISTANCE = 105;
    public static final int ACT_FAR_CLIP_DISTANCE = 106;
    public static final int ACT_TEXTURE_VIEWPROJ_MATRIX = 107;
    public static final int ACT_TEXTURE_VIEWPROJ_MATRIX_ARRAY = 108;
    public static final int ACT_TEXTURE_WORLDVIEWPROJ_MATRIX = 109;
    public static final int ACT_TEXTURE_WORLDVIEWPROJ_MATRIX_ARRAY = 110;
    public static final int ACT_SPOTLIGHT_VIEWPROJ_MATRIX = 111;
    public static final int ACT_SPOTLIGHT_WORLDVIEWPROJ_MATRIX = 112;
    public static final int ACT_SCENE_DEPTH_RANGE = 113;
    public static final int ACT_SHADOW_SCENE_DEPTH_RANGE = 114;
    public static final int ACT_SHADOW_COLOUR = 115;
    public static final int ACT_SHADOW_EXTRUSION_DISTANCE = 116;
    public static final int ACT_TEXTURE_SIZE = 117;
    public static final int ACT_INVERSE_TEXTURE_SIZE = 118;
    public static final int ACT_PACKED_TEXTURE_SIZE = 119;
    public static final int ACT_PASS_NUMBER = 120;
    public static final int ACT_PASS_ITERATION_NUMBER = 121;
    public static final int ACT_ANIMATION_PARAMETRIC = 122;
    public static final int ACT_CUSTOM = 123;

    public enum NamedParamType {
        NONE, FLOAT, INT
    }

    public String name;
    public int type;
    public int id;
    public NamedParamType extraParamType;
    public float extraParamFloat;
    public int extraParamInt;
}
