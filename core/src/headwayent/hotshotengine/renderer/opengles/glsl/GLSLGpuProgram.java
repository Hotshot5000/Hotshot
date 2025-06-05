/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.glsl;

import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.opengles.GLGpuProgram;

public class GLSLGpuProgram extends GLGpuProgram {

    private static int mVertexShaderCount;
    private static int mFragmentShaderCount;
    private static int mGeometryShaderCount;
    private final GLSLProgram mGLSLProgram;

    public GLSLGpuProgram(GLSLProgram parent) {
        super(parent.getName());
        
        mGLSLProgram = parent;
        mType = parent.getType();
        mSyntaxCode = GLSLProgramFactory.LANGUAGE_NAME;

        if (parent.getType() == GpuProgramType.GPT_VERTEX_PROGRAM) {
            mProgramID = ++mVertexShaderCount;
        } else if (parent.getType() == GpuProgramType.GPT_FRAGMENT_PROGRAM) {
            mProgramID = ++mFragmentShaderCount;
        } else {
            mProgramID = ++mGeometryShaderCount;
        }

        // there is nothing to load
        mLoadFromFile = false;
    }

    public void bindProgram() {
        // tell the Link Program Manager what shader is to become active
        switch (mType) {
            case GPT_VERTEX_PROGRAM:
                GLSLLinkProgramManager.getSingleton().setActiveVertexShader(this);
                break;
            case GPT_FRAGMENT_PROGRAM:
                GLSLLinkProgramManager.getSingleton().setActiveFragmentShader(this);
                break;
            case GPT_GEOMETRY_PROGRAM:
                GLSLLinkProgramManager.getSingleton().setActiveGeometryShader(this);
                break;
        }
    }

    public void unbindProgram() {
        // tell the Link Program Manager what shader is to become inactive
        if (mType == GpuProgramType.GPT_VERTEX_PROGRAM) {
            GLSLLinkProgramManager.getSingleton().setActiveVertexShader(null);
        } else if (mType == GpuProgramType.GPT_GEOMETRY_PROGRAM) {
            GLSLLinkProgramManager.getSingleton().setActiveGeometryShader(null);
        } else // its a fragment shader
        {
            GLSLLinkProgramManager.getSingleton().setActiveFragmentShader(null);
        }
    }

    public void bindProgramParameters(ENG_GpuProgramParameters params, short mask) {
        GLSLLinkProgramManager.getSingleton().getActiveLinkProgram().updateUniforms(
                params, mask, mType);
    }

    public void bindProgramPassIterationParameters(ENG_GpuProgramParameters params) {
        GLSLLinkProgramManager.getSingleton().getActiveLinkProgram().
                updatePassIterationUniforms(params);
    }

    public int getAttributeIndex(VertexElementSemantic semantic, int index) {
        GLSLLinkProgram linkProgram =
                GLSLLinkProgramManager.getSingleton().getActiveLinkProgram();

        if (linkProgram.isAttributeValid(semantic, index)) {
            return linkProgram.getAttributeIndex(semantic, index);
        } else {
            return super.getAttributeIndex(semantic, index);
        }
    }

    public GLSLProgram getGLSLProgram() {
        return mGLSLProgram;
    }

    protected void loadImpl() {

    }

    protected void unloadImpl() {

    }

    protected void loadFromSource() {

    }

}
