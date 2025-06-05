/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.glsl;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.basictypes.ENG_Long;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition.GpuConstantType;
import headwayent.hotshotengine.renderer.ENG_GpuNamedConstants;
import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;
import headwayent.hotshotengine.renderer.opengles.GLRenderSystem;
import headwayent.hotshotengine.renderer.opengles.glsl.GLSLLinkProgram.GLUniformReference;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.GL20;

public class GLSLLinkProgramManager {

//    private static GLSLLinkProgramManager mgr;

    private final TreeMap<ENG_Long, GLSLLinkProgram> mLinkPrograms = new TreeMap<>();

    private GLSLGpuProgram mActiveVertexGpuProgram;
    private GLSLGpuProgram mActiveFragmentGpuProgram;
    private GLSLGpuProgram mActiveGeometryGpuProgram;
    private GLSLLinkProgram mActiveLinkProgram;

    private final TreeMap<String, ENG_Integer> mTypeEnumMap = new TreeMap<>();

    private static final int BUFFERSIZE = 200;
    private final byte[] uniformName = new byte[BUFFERSIZE];

    public GLSLLinkProgramManager() {
//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }

        // Fill in the relationship between type names and enums
        mTypeEnumMap.put("float", new ENG_Integer(GL20.GL_FLOAT));
        mTypeEnumMap.put("vec2", new ENG_Integer(GL20.GL_FLOAT_VEC2));
        mTypeEnumMap.put("vec3", new ENG_Integer(GL20.GL_FLOAT_VEC3));
        mTypeEnumMap.put("vec4", new ENG_Integer(GL20.GL_FLOAT_VEC4));
        //mTypeEnumMap.put("sampler1D", new ENG_Integer(GL20.GL_SAMPLER_1D));
        mTypeEnumMap.put("sampler2D", new ENG_Integer(GL20.GL_SAMPLER_2D));
        //mTypeEnumMap.put("sampler3D", new ENG_Integer(GL20.GL_SAMPLER_3D));
        mTypeEnumMap.put("samplerCube", new ENG_Integer(GL20.GL_SAMPLER_CUBE));
        //mTypeEnumMap.put("sampler1DShadow", new ENG_Integer(GL20.GL_SAMPLER_1D_SHADOW));
        //mTypeEnumMap.put("sampler2DShadow", new ENG_Integer(GL20.GL_SAMPLER_2D_SHADOW));
        mTypeEnumMap.put("int", new ENG_Integer(GL20.GL_INT));
        mTypeEnumMap.put("ivec2", new ENG_Integer(GL20.GL_INT_VEC2));
        mTypeEnumMap.put("ivec3", new ENG_Integer(GL20.GL_INT_VEC3));
        mTypeEnumMap.put("ivec4", new ENG_Integer(GL20.GL_INT_VEC4));
        mTypeEnumMap.put("mat2", new ENG_Integer(GL20.GL_FLOAT_MAT2));
        mTypeEnumMap.put("mat3", new ENG_Integer(GL20.GL_FLOAT_MAT3));
        mTypeEnumMap.put("mat4", new ENG_Integer(GL20.GL_FLOAT_MAT4));
        // GL 2.1
        mTypeEnumMap.put("mat2x2", new ENG_Integer(GL20.GL_FLOAT_MAT2));
        mTypeEnumMap.put("mat3x3", new ENG_Integer(GL20.GL_FLOAT_MAT3));
        mTypeEnumMap.put("mat4x4", new ENG_Integer(GL20.GL_FLOAT_MAT4));
        /*mTypeEnumMap.put("mat2x3", new ENG_Integer(GL20.GL_FLOAT_MAT2x3));
		mTypeEnumMap.put("mat3x2", new ENG_Integer(GL20.GL_FLOAT_MAT3x2));
		mTypeEnumMap.put("mat3x4", new ENG_Integer(GL20.GL_FLOAT_MAT3x4));
		mTypeEnumMap.put("mat4x3", new ENG_Integer(GL20.GL_FLOAT_MAT4x3));
		mTypeEnumMap.put("mat2x4", new ENG_Integer(GL20.GL_FLOAT_MAT2x4));
		mTypeEnumMap.put("mat4x2", new ENG_Integer(GL20.GL_FLOAT_MAT4x2));*/
    }

    public void destroyAllLinkedPrograms() {
        destroyAllLinkedPrograms(false);
    }

    public void destroyAllLinkedPrograms(boolean skipGLDelete) {
        for (GLSLLinkProgram prg : mLinkPrograms.values()) {
            prg.destroy(skipGLDelete);
        }
        mActiveLinkProgram = null;
        setActiveVertexShader(null);
        setActiveFragmentShader(null);
        //	setActiveGeometryShader(null); Not yet implemented
        mLinkPrograms.clear();
    }

    public GLSLLinkProgram getActiveLinkProgram() {
        if (mActiveLinkProgram != null) {
            return mActiveLinkProgram;
        }

        long active = 0;

        if (mActiveVertexGpuProgram != null) {
            active = ((long) mActiveVertexGpuProgram.getProgramID()) << 32;
        }
        if (mActiveGeometryGpuProgram != null) {
            active += ((long) mActiveGeometryGpuProgram.getProgramID()) << 16;
        }
        if (mActiveFragmentGpuProgram != null) {
            active += mActiveFragmentGpuProgram.getProgramID();
        }

        if (active > 0) {
            GLSLLinkProgram prg = mLinkPrograms.get(new ENG_Long(active));
            if (prg == null) {
                mActiveLinkProgram = new GLSLLinkProgram(
                        mActiveVertexGpuProgram,
                        mActiveGeometryGpuProgram,
                        mActiveFragmentGpuProgram);
                mLinkPrograms.put(new ENG_Long(active), mActiveLinkProgram);
            } else {
                mActiveLinkProgram = prg;
            }
        }

        if (mActiveLinkProgram != null) {
            mActiveLinkProgram.activate();
        }

        return mActiveLinkProgram;
    }

    private void completeDefInfo(int gltype, ENG_GpuConstantDefinition defToUpdate) {
        // decode uniform size and type
        // Note GLSL never packs rows into float4's(from an API perspective anyway)
        // therefore all values are tight in the buffer
        switch (gltype) {
            case GL20.GL_FLOAT:
                defToUpdate.constType = GpuConstantType.GCT_FLOAT1;
                break;
            case GL20.GL_FLOAT_VEC2:
                defToUpdate.constType = GpuConstantType.GCT_FLOAT2;
                break;

            case GL20.GL_FLOAT_VEC3:
                defToUpdate.constType = GpuConstantType.GCT_FLOAT3;
                break;

            case GL20.GL_FLOAT_VEC4:
                defToUpdate.constType = GpuConstantType.GCT_FLOAT4;
                break;
	/*	case GL20.GL_SAMPLER_1D:
			// need to record samplers for GLSL
			defToUpdate.constType = GpuConstantType.GCT_SAMPLER1D;
			break;*/
            case GL20.GL_SAMPLER_2D:
                //	case GL20.GL_SAMPLER_2D_RECT_ARB:
                defToUpdate.constType = GpuConstantType.GCT_SAMPLER2D;
                break;
	/*	case GL20.GL_SAMPLER_3D:
			defToUpdate.constType = GpuConstantType.GCT_SAMPLER3D;
			break;*/
            case GL20.GL_SAMPLER_CUBE:
                defToUpdate.constType = GpuConstantType.GCT_SAMPLERCUBE;
                break;
		/*case GL20.GL_SAMPLER_1D_SHADOW:
			defToUpdate.constType = GpuConstantType.GCT_SAMPLER1DSHADOW;
			break;
		case GL20.GL_SAMPLER_2D_SHADOW:
		case GL20.GL_SAMPLER_2D_RECT_SHADOW_ARB:
			defToUpdate.constType = GpuConstantType.GCT_SAMPLER2DSHADOW;
			break;*/
            case GL20.GL_INT:
                defToUpdate.constType = GpuConstantType.GCT_INT1;
                break;
            case GL20.GL_INT_VEC2:
                defToUpdate.constType = GpuConstantType.GCT_INT2;
                break;
            case GL20.GL_INT_VEC3:
                defToUpdate.constType = GpuConstantType.GCT_INT3;
                break;
            case GL20.GL_INT_VEC4:
                defToUpdate.constType = GpuConstantType.GCT_INT4;
                break;
            case GL20.GL_FLOAT_MAT2:
                defToUpdate.constType = GpuConstantType.GCT_MATRIX_2X2;
                break;
            case GL20.GL_FLOAT_MAT3:
                defToUpdate.constType = GpuConstantType.GCT_MATRIX_3X3;
                break;
            case GL20.GL_FLOAT_MAT4:
                defToUpdate.constType = GpuConstantType.GCT_MATRIX_4X4;
                break;
		/*case GL20.GL_FLOAT_MAT2x3:
			defToUpdate.constType = GpuConstantType.GCT_MATRIX_2X3;
			break;
		case GL20.GL_FLOAT_MAT3x2:
			defToUpdate.constType = GpuConstantType.GCT_MATRIX_3X2;
			break;
		case GL20.GL_FLOAT_MAT2x4:
			defToUpdate.constType = GpuConstantType.GCT_MATRIX_2X4;
			break;
		case GL20.GL_FLOAT_MAT4x2:
			defToUpdate.constType = GpuConstantType.GCT_MATRIX_4X2;
			break;
		case GL20.GL_FLOAT_MAT3x4:
			defToUpdate.constType = GpuConstantType.GCT_MATRIX_3X4;
			break;
		case GL20.GL_FLOAT_MAT4x3:
			defToUpdate.constType = GpuConstantType.GCT_MATRIX_4X3;
			break;*/
            default:
                defToUpdate.constType = GpuConstantType.GCT_UNKNOWN;
                break;

        }

        // GL doesn't pad
        defToUpdate.elementSize =
                ENG_GpuConstantDefinition.getElementSize(defToUpdate.constType, false);
    }

    private boolean completeParamSource(String paramName,
                                        TreeMap<String, ENG_GpuConstantDefinition> vertexConstantDefs,
                                        TreeMap<String, ENG_GpuConstantDefinition> geometryConstantDefs,
                                        TreeMap<String, ENG_GpuConstantDefinition> fragmentConstantDefs,
                                        GLUniformReference refToUpdate) {

        if (vertexConstantDefs != null) {
            ENG_GpuConstantDefinition parami = vertexConstantDefs.get(paramName);
            if (parami != null) {
                refToUpdate.mSourceProgType = GpuProgramType.GPT_VERTEX_PROGRAM;
                refToUpdate.mConstantDef = parami;
                return true;
            }
        }

        if (geometryConstantDefs != null) {
            throw new UnsupportedOperationException("Geometry programs not supported");
        }

        if (fragmentConstantDefs != null) {
            ENG_GpuConstantDefinition parami = fragmentConstantDefs.get(paramName);
            if (parami != null) {
                refToUpdate.mSourceProgType = GpuProgramType.GPT_FRAGMENT_PROGRAM;
                refToUpdate.mConstantDef = parami;
                return true;
            }
        }

        return false;
    }

    public void setActiveFragmentShader(GLSLGpuProgram fragmentShader) {
        if (mActiveFragmentGpuProgram != fragmentShader) {
            mActiveFragmentGpuProgram = fragmentShader;
            mActiveLinkProgram = null;
        }
    }

    public void setActiveVertexShader(GLSLGpuProgram vertexShader) {
        if (mActiveVertexGpuProgram != vertexShader) {
            mActiveVertexGpuProgram = vertexShader;
            mActiveLinkProgram = null;
        }
    }

    public void setActiveGeometryShader(GLSLGpuProgram geometryShader) {
        throw new UnsupportedOperationException("Geometry shaders not currently implemented");
    }

    /** @noinspection deprecation */
    public void extractUniforms(int programObject,
                                TreeMap<String, ENG_GpuConstantDefinition> vertexConstantDefs,
                                TreeMap<String, ENG_GpuConstantDefinition> geometryConstantDefs,
                                TreeMap<String, ENG_GpuConstantDefinition> fragmentConstantDefs,
                                ArrayList<GLUniformReference> list) {
        //	int[] uniformCount = new int[1];
        IntBuffer uniformCount = ENG_Utility.allocateDirect(4).asIntBuffer();


        MTGLES20.glGetProgramivImmediate(programObject,
                GL20.GL_ACTIVE_UNIFORMS, uniformCount);

        IntBuffer arraySize = ENG_Utility.allocateDirect(4).asIntBuffer();
        IntBuffer glType = ENG_Utility.allocateDirect(4).asIntBuffer();
        int[] len = new int[1];
        int unCount = uniformCount.get();
        //	IntBuffer arraySize = ENG_Utility.allocateDirect(1).asIntBuffer();
        //	IntBuffer glType = ENG_Utility.allocateDirect(1).asIntBuffer();
        for (int i = 0; i < unCount; ++i) {
            arraySize.position(0);
            glType.position(0);
            GLUniformReference newGLUniformReference = new GLUniformReference();
			
			/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 * Due to a bug in libgdx where the arraySize is written in glType
			 * we must do the following line. Make sure this gets fixed when
			 * libgdx gets fixed! Warning: Cannot use glType!
			 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
            // FIXED
//			arraySize[0] = glType[0];
//			arraySize.put(0, glType.get(0));

            String paramName = MTGLES20.glGetActiveUniformImmediate(programObject,
                    i, arraySize, glType);
            //new String(uniformName, 0,
//					ENG_CompilerUtil.byteArrayEndCharacterPos(uniformName));

            newGLUniformReference.mLocation =
                    MTGLES20.glGetUniformLocation(programObject, paramName);

            if (newGLUniformReference.mLocation >= 0) {
                boolean foundSource = completeParamSource(paramName,
                        vertexConstantDefs, geometryConstantDefs, fragmentConstantDefs,
                        newGLUniformReference);
//				System.out.println("arraySize: " + arraySize.get(0) + " glType: " + glType.get(0));
                if (foundSource) {
                    if (newGLUniformReference.mConstantDef.arraySize != arraySize.get(0)) {
                        throw new IllegalArgumentException(
                                "GL doesn't agree with our array size!");
                    }
                    list.add(newGLUniformReference);
                }
            }
        }
    }

    public void extractConstantDefs(String src, ENG_GpuNamedConstants defs, String filename) {
        int currPos = src.indexOf("uniform");
        while (currPos != -1) {
            ENG_GpuConstantDefinition def = new ENG_GpuConstantDefinition();
            String paramName = null;

            boolean inLargerString = false;
            if (currPos != 0) {
                char prev = src.charAt(currPos - 1);
                if (prev != ' ' && prev != '\t' && prev != '\r' && prev != '\n'
                        && prev != ';') {
                    inLargerString = true;
                }
            }
            if (!inLargerString && currPos + 7 < src.length()) {
                char next = src.charAt(currPos + 7);
                if (next != ' ' && next != '\t' && next != '\r' && next != '\n') {
                    inLargerString = true;
                }
            }

            // skip 'uniform'
            currPos += 7;

            if (!inLargerString) {
                int endPos = src.indexOf(';', currPos);
                if (endPos == -1) {
                    // problem, missing semicolon, abort
                    break;
                }
                String line = src.substring(currPos, endPos);
                for (int sqp = line.indexOf(" ["); sqp != -1; sqp = line.indexOf(" [")) {
                    line = line.substring(0, sqp) + line.substring(sqp + 1);
                }
                String[] parts = line.split("[, \t\r\n]");

                for (String part : parts) {
                    ENG_Integer typei = mTypeEnumMap.get(part);
                    if (typei != null) {
                        completeDefInfo(typei.getValue(), def);
                    } else {
                        String s = part.trim();
                        if (s.isEmpty()) {
                            continue;
                        }
                        int arrayStart = s.indexOf("[");
                        if (arrayStart != -1) {
                            String name = s.substring(0, arrayStart);
                            name = name.trim();
                            if (!name.isEmpty()) {
                                paramName = name;
                            }

                            int arrayEnd = s.indexOf("]", arrayStart);
                            String arrayDimTerm =
                                    s.substring(arrayStart + 1, arrayEnd).trim();

                            def.arraySize = Integer.parseInt(arrayDimTerm);
                        } else {
                            paramName = s;
                            def.arraySize = 1;
                        }

                        if (def.constType == GpuConstantType.GCT_UNKNOWN) {
                            throw new IllegalArgumentException(
                                    "Problem parsing the following GLSL Uniform: '"
                                            + line + "' in file " + filename);
                        }

                        // Complete def and add
                        // increment physical buffer location
                        def.logicalIndex = 0; // not valid in GLSL
                        if (def.isFloat()) {
                            def.physicalIndex = defs.floatBufferSize;
                            defs.floatBufferSize += def.arraySize * def.elementSize;
                        } else {
                            def.physicalIndex = defs.intBufferSize;
                            defs.intBufferSize += def.arraySize * def.elementSize;
                        }
                        defs.map.put(paramName, def);

                        // Generate array accessors
                        defs.generateConstantDefinitionArrayEntries(paramName, def);
                    }
                }
            }

            currPos = src.indexOf("uniform", currPos);
        }
    }

    public static GLSLLinkProgramManager getSingleton() {
//        if (MainActivity.isDebugmode() && mgr == null) {
//            throw new NullPointerException("LinkProgramManager == null!");
//        }
//        return mgr;
        return ((GLRenderSystem) MainApp.getGame().getRenderRoot().getActiveRenderSystem()).getGLSLProgramFactory().getLinkProgramManager();
    }
}
