/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.glsl;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters;
import headwayent.hotshotengine.renderer.ENG_HighLevelGpuProgram;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.GL20;

public class GLSLProgram extends ENG_HighLevelGpuProgram {

    private int mGLHandle;
    /// attached Shader names
//    private String mAttachedShaderNames;
    /// Preprocessor options
//    private String mPreprocessorDefines;
    /// container of attached programs
//    private final ArrayList<GLSLProgram> mAttachedGLSLPrograms = new ArrayList<>();

    public GLSLProgram(String name) {
        super(name);
        
        // Manually assign language now since we use it immediately
        mSyntaxCode = GLSLProgramFactory.LANGUAGE_NAME;
    }

    @Override
    protected void buildConstantDefinitions() {


        // We need an accurate list of all the uniforms in the shader, but we
        // can't get at them until we link all the shaders into a program object.


        // Therefore instead, parse the source code manually and extract the uniforms
        createParameterMappingStructures(true);
        GLSLLinkProgramManager.getSingleton().extractConstantDefs(
                mSource, mConstantDefs, mName);

    }

    public boolean getPassSurfaceAndLightStates() {
        // scenemanager should pass on light & material state to the rendersystem
        return true;
    }

    public boolean getPassTransformStates() {
        // scenemanager should pass on transform state to the rendersystem
        return true;
    }

    public String getLanguage() {
        return GLSLProgramFactory.LANGUAGE_NAME;
    }

    @Override
    protected void createLowLevelImpl() {

        mAssemblerProgram = new GLSLGpuProgram(this);
    }

    private boolean shaderDeleted;

    /** @noinspection deprecation*/
    @Override
    protected void unloadHighLevelImpl(boolean skipGLDelete) {

        if (!shaderDeleted) {
            if (isSupported()) {
                if (!skipGLDelete) {
                    MTGLES20.glDeleteShader(mGLHandle);
                }
                shaderDeleted = true;
            }
        }
    }

    public void destroy(boolean skipGLDelete) {
        unloadHighLevel(skipGLDelete);
    }

    public void populateParameterNames(ENG_GpuProgramParameters params) {
        getConstantDefinitions();
        params._setNamedConstants(mConstantDefs);
    }


    /** @noinspection deprecation */
    public void loadFromSource() {
        if (isSupported()) {
            int shaderType = 0;

            switch (mType) {
                case GPT_VERTEX_PROGRAM:
                    shaderType = GL20.GL_VERTEX_SHADER;
                    break;
                case GPT_FRAGMENT_PROGRAM:
                    shaderType = GL20.GL_FRAGMENT_SHADER;
                    break;
                case GPT_GEOMETRY_PROGRAM:
                    //shaderType = GL_GEOMETRY_SHADER_EXT;
                    throw new IllegalArgumentException("Geometry shader not supported");
                    //break;
            }
            GLUtility.checkForGLSLError("loadFromSource", "Error creating GLSL shader object ",
                    0, false, true);
            mGLHandle = MTGLES20.glCreateShader(shaderType);
            GLUtility.checkForGLSLError("loadFromSource", "Error creating GLSL shader object ",
                    0, false, true);
        }

        if (mSource != null && !mSource.isEmpty()) {
            MTGLES20.glShaderSourceImmediate(mGLHandle, mSource);
            GLUtility.checkForGLSLError("loadFromSource",
                    "Cannot load GLSL high-level shader source : ",
                    0, false, true);
        }

        compile(true);
    }

    public boolean compile() {
        return compile(false);
    }

    /** @noinspection deprecation */
    public boolean compile(boolean checkErrors) {

        MTGLES20.glCompileShaderImmediate(mGLHandle);
        IntBuffer compileStatus = ENG_Utility.allocateDirect(4).asIntBuffer();
        MTGLES20.glGetShaderivImmediate(
                mGLHandle, GL20.GL_COMPILE_STATUS, compileStatus);

        if (checkErrors) {
            if (compileStatus.get() == 0) {
                compileStatus.rewind();
                //	MTGLES20.glGetShaderiv(mGLHandle, GLES20.GL_INFO_LOG_LENGTH,
                //			compileStatus);
                String log;
                log = MTGLES20.glGetShaderInfoLog(mGLHandle);
                if (compileStatus.get() > 1) {
                    log = MTGLES20.glGetShaderInfoLog(mGLHandle);

                }
                throw new IllegalArgumentException(
                        "GL compile shader error: " + log);
            }
            GLUtility.checkForGLSLError("compile",
                    "Cannot compile GLSL high-level shader : " + mName + " ",
                    0, false, true);
        }
        int mCompiled = 1;

        return mCompiled == 1;
    }

    /** @noinspection deprecation*/
    public void attachToProgramObject(int programObject) {

//        for (GLSLProgram p : mAttachedGLSLPrograms) {
//            p.compile(true);
//
//            p.attachToProgramObject(programObject);
//        }
        MTGLES20.glAttachShaderImmediate(programObject, mGLHandle);
    }


}
