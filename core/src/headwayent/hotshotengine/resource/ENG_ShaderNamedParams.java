/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import headwayent.hotshotengine.resource.shadertype.ENG_ShaderTypeBool;
import headwayent.hotshotengine.resource.shadertype.ENG_ShaderTypeFloat;
import headwayent.hotshotengine.resource.shadertype.ENG_ShaderTypeInt;

import java.util.ArrayList;

public class ENG_ShaderNamedParams {

    public final ArrayList<ENG_ShaderTypeBool> BoolList = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeBool> BVec2List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeBool> BVec3List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeBool> BVec4List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeFloat> FloatList = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeInt> IntList = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeInt> IVec2List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeInt> IVec3List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeInt> IVec4List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeFloat> Mat2List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeFloat> Mat3List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeFloat> Mat4List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeFloat> Vec2List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeFloat> Vec3List = new ArrayList<>();
    public final ArrayList<ENG_ShaderTypeFloat> Vec4List = new ArrayList<>();
}
