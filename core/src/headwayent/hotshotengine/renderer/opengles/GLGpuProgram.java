/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.hotshotengine.renderer.ENG_GpuProgram;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;

public class GLGpuProgram extends ENG_GpuProgram {

    protected int mProgramID;
    protected int mProgramType;

    public GLGpuProgram(String name) {
        super(name);

    }

    public int getProgramID() {
        return mProgramID;
    }

    public int getAttributeIndex(VertexElementSemantic semantic, int index) {
        return getFixedAttributeIndex(semantic, index);
    }

    public static int getFixedAttributeIndex(VertexElementSemantic semantic, int index) {


        // Some drivers (e.g. OS X on nvidia) incorrectly determine the attribute binding automatically
        // and end up aliasing existing built-ins. So avoid! Fixed builtins are:

        //  a  builtin				custom attrib name
        // ----------------------------------------------
        //	0  gl_Vertex			vertex
        //  1  n/a					blendWeights
        //	2  gl_Normal			normal
        //	3  gl_Color				colour
        //	4  gl_SecondaryColor	secondary_colour
        //	5  gl_FogCoord			fog_coord
        //  7  n/a					blendIndices
        //	8  gl_MultiTexCoord0	uv0
        //	9  gl_MultiTexCoord1	uv1
        //	10 gl_MultiTexCoord2	uv2
        //	11 gl_MultiTexCoord3	uv3
        //	12 gl_MultiTexCoord4	uv4
        //	13 gl_MultiTexCoord5	uv5
        //	14 gl_MultiTexCoord6	uv6, tangent
        //	15 gl_MultiTexCoord7	uv7, binormal
        switch (semantic) {
            case VES_POSITION:
                return 0;
            case VES_BLEND_WEIGHTS:
                return 1;
            case VES_NORMAL:
                return 2;
            case VES_DIFFUSE:
                return 3;
            case VES_SPECULAR:
                return 4;
            case VES_BLEND_INDICES:
                return 7;
            case VES_TEXTURE_COORDINATES:
                return 8 + index;
            case VES_TANGENT:
                return 14;
            case VES_BINORMAL:
                return 15;
            default:
                throw new IllegalArgumentException("Missing attribute!");

        }
    }

    /**
     * Make all return true. We don't use old gl path.
     *
     * @param semantic
     * @param index
     * @return
     */
    public boolean isAttributeValid(VertexElementSemantic semantic, int index) {
        // default implementation
        switch (semantic) {
            case VES_POSITION:
            case VES_NORMAL:
            case VES_DIFFUSE:
            case VES_SPECULAR:
            case VES_TEXTURE_COORDINATES:
                //	return false;
            case VES_BLEND_WEIGHTS:
            case VES_BLEND_INDICES:
            case VES_BINORMAL:
            case VES_TANGENT:
                return true; // with default binding
        }

        return false;
    }

    public void bindProgramParameters(ENG_GpuProgramParameters params, short mask) {

    }

    public void bindProgramPassIterationParameters(ENG_GpuProgramParameters params) {

    }

    public void bindProgram() {

    }

    public void unbindProgram() {

    }

    @Override
    protected void loadFromSource() {


    }

    protected void unloadImpl() {

    }

}
