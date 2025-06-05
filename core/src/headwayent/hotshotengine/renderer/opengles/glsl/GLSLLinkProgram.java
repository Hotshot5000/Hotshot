/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.glsl;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.exception.ENG_GLException;
import headwayent.hotshotengine.renderer.ENG_GpuConstantDefinition;
import headwayent.hotshotengine.renderer.ENG_GpuProgram.GpuProgramType;
import headwayent.hotshotengine.renderer.ENG_GpuProgramParameters;
import headwayent.hotshotengine.renderer.ENG_RenderOperation.OperationType;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_VertexElement.VertexElementSemantic;
import headwayent.hotshotengine.renderer.opengles.GLGpuProgram;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import com.badlogic.gdx.graphics.GL20;

public class GLSLLinkProgram {

    public static class GLUniformReference {
        public int mLocation;
        public GpuProgramType mSourceProgType;
        public ENG_GpuConstantDefinition mConstantDef;
    }

    private final ArrayList<GLUniformReference> mGLUniformReferences =
            new ArrayList<>();

    private final GLSLGpuProgram mVertexProgram;
    private final GLSLGpuProgram mGeometryProgram;
    private final GLSLGpuProgram mFragmentProgram;

    private boolean mUniformRefsBuilt;

    private final int mGLHandle;

    private int mLinked;

    /// flag indicating skeletal animation is being performed
    private boolean mSkeletalAnimation;

    private final TreeSet<ENG_Integer> mValidAttributes = new TreeSet<>();

    static class CustomAttribute {
        public final String name;
        public final int attrib;

        public CustomAttribute(String name, int attrib) {
            this.name = name;
            this.attrib = attrib;
        }
    }

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
    private static final CustomAttribute[] msCustomAttributes = {
            new CustomAttribute("vertex", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_POSITION, 0)),
            new CustomAttribute("blendWeights", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_BLEND_WEIGHTS, 0)),
            new CustomAttribute("normal", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_NORMAL, 0)),
            new CustomAttribute("colour", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_DIFFUSE, 0)),
            new CustomAttribute("secondary_colour", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_SPECULAR, 0)),
            new CustomAttribute("blendIndices", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_BLEND_INDICES, 0)),
            new CustomAttribute("uv0", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 0)),
            new CustomAttribute("uv1", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 1)),
            new CustomAttribute("uv2", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 2)),
            new CustomAttribute("uv3", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 3)),
            new CustomAttribute("uv4", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 4)),
            new CustomAttribute("uv5", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 5)),
            new CustomAttribute("uv6", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 6)),
            new CustomAttribute("uv7", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TEXTURE_COORDINATES, 7)),
            new CustomAttribute("tangent", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_TANGENT, 0)),
            new CustomAttribute("binormal", GLGpuProgram.getFixedAttributeIndex(VertexElementSemantic.VES_BINORMAL, 0))
    };

    private static int getGLGeometryInputPrimitiveType(OperationType operationType,
                                                       boolean requiresAdjacency) {
        switch (operationType) {
            case OT_POINT_LIST:
                return GL20.GL_POINTS;
            case OT_LINE_LIST:
            case OT_LINE_STRIP:
                return GL20.GL_LINES;
            default:
            case OT_TRIANGLE_LIST:
            case OT_TRIANGLE_STRIP:
            case OT_TRIANGLE_FAN:
                return GL20.GL_TRIANGLES;
        }
    }

    private static int getGLGeometryOutputPrimitiveType(OperationType operationType) {
        switch (operationType) {
            case OT_POINT_LIST:
                return GL20.GL_POINTS;
            case OT_LINE_STRIP:
                return GL20.GL_LINE_STRIP;
            case OT_TRIANGLE_STRIP:
                return GL20.GL_TRIANGLE_STRIP;
            default:
                throw new IllegalArgumentException(
                        "Geometry shader output operation type can only be point list, " +
                                "line strip or triangle strip");

        }
    }

    private void buildGLUniformReferences() {
        if (!mUniformRefsBuilt) {
            TreeMap<String, ENG_GpuConstantDefinition> vertParams = null;
            TreeMap<String, ENG_GpuConstantDefinition> fragParams = null;
            TreeMap<String, ENG_GpuConstantDefinition> geomParams = null;

            if (mVertexProgram != null) {
                vertParams = mVertexProgram.getGLSLProgram().getConstantDefinitions().map;
            }

            if (mGeometryProgram != null) {
                geomParams = mGeometryProgram.getGLSLProgram().getConstantDefinitions().map;
            }

            if (mFragmentProgram != null) {
                fragParams = mFragmentProgram.getGLSLProgram().getConstantDefinitions().map;
            }

            GLSLLinkProgramManager.getSingleton().extractUniforms(mGLHandle,
                    vertParams, geomParams, fragParams, mGLUniformReferences);

            mUniformRefsBuilt = true;
        }
    }

    /** @noinspection deprecation*/
    private void extractAttributes() {
        int numAttribs = msCustomAttributes.length;

        for (CustomAttribute a : msCustomAttributes) {
            int attrib = MTGLES20.glGetAttribLocation(mGLHandle, a.name);

            if (attrib != -1) {
                mValidAttributes.add(new ENG_Integer(a.attrib));
            }
        }
    }

    /** @noinspection deprecation*/
    public GLSLLinkProgram(GLSLGpuProgram mActiveVertexGpuProgram,
                           GLSLGpuProgram mActiveGeometryGpuProgram,
                           GLSLGpuProgram mActiveFragmentGpuProgram) {

        this.mVertexProgram = mActiveVertexGpuProgram;
        this.mGeometryProgram = mActiveGeometryGpuProgram;
        this.mFragmentProgram = mActiveFragmentGpuProgram;

		GLUtility.checkForGLSLError(
				"GLSLLinkProgram::GLSLLinkProgram",
				"Error Before creating GLSL Program Object");

        mGLHandle = MTGLES20.glCreateProgram();
        GLUtility.checkForGLSLError(
                "GLSLLinkProgram::GLSLLinkProgram",
                "Error Creating GLSL Program Object");
        if (mVertexProgram != null) {
            mVertexProgram.getGLSLProgram().attachToProgramObject(mGLHandle);
        }
        if (mGeometryProgram != null) {
            mGeometryProgram.getGLSLProgram().attachToProgramObject(mGLHandle);
        }
        if (mFragmentProgram != null) {
            mFragmentProgram.getGLSLProgram().attachToProgramObject(mGLHandle);
        }
    }

    private boolean programDeleted;

    public void destroy() {
        destroy(false);
    }

    /** @noinspection deprecation*/
    public void destroy(boolean skipGLDelete) {
        if (!programDeleted) {
            if (!skipGLDelete) {
                MTGLES20.glDeleteProgram(mGLHandle);
            }
            programDeleted = true;
        }
    }

    /** @noinspection deprecation */
    public void activate() {
        
        if (mLinked == 0) {
            if (mVertexProgram != null) {
                int numAttribs = msCustomAttributes.length;
                String vpSource = mVertexProgram.getGLSLProgram().getSource();
                for (CustomAttribute a : msCustomAttributes) {
                    int pos = vpSource.indexOf(a.name);
                    if (pos != -1) {
                        int startPos = vpSource.indexOf("attribute", pos < 20 ? 0 : pos - 20);
                        if (startPos == -1) {
                            startPos = vpSource.indexOf("in", pos - 20);
                        }
                        if (startPos != -1 && startPos < pos) {
                            String expr = vpSource.substring(
                                    startPos, pos + a.name.length());// - startPos);
                            String[] vec = expr.split("[\t\n ]");
                            if ((vec[0].equals("in") || vec[0].equals("attribute"))
                                    && vec[2].equals(a.name)) {
                                // Is this absolutely necessary that we use the Immediate varianta??
                                MTGLES20.glBindAttribLocationImmediate(
                                        mGLHandle, a.attrib, a.name);
                            }
                        }
                    }
                }
            }

            if (mGeometryProgram != null) {
                throw new UnsupportedOperationException(
                        "Geometry programs not supported");
            }
            //	IntBuffer glHandle = ENG_Utility.allocateDirect(4).asIntBuffer();
            //	glHandle.put(mGLHandle).flip();
            MTGLES20.glLinkProgramImmediate(mGLHandle);//Immediate(mGLHandle);
        /*	MTGLES20.setRenderingAllowed(true);
			GLRenderSurface.getSingleton().requestRender(true);
			GLRenderSurface.getSingleton().waitForRenderingToFinish();*/
            //	int[] link = new int[1];
            IntBuffer link = ENG_Utility.allocateDirect(4).asIntBuffer();
            MTGLES20.glGetProgramivImmediate(mGLHandle, GL20.GL_LINK_STATUS, link);
            mLinked = link.get();
            if (mLinked == 0) {
                // We're fucked
                IntBuffer buf = ENG_Utility.allocateDirect(4).asIntBuffer();
                MTGLES20.glGetProgramivImmediate(
                        mGLHandle, GL20.GL_INFO_LOG_LENGTH, buf);

                String log;
                log = MTGLES20.glGetProgramInfoLog(mGLHandle);
                if (buf.get() > 1) {
                    buf.rewind();

                    System.out.println("Could not link program with error: " + log);
                }
                throw new ENG_GLException(/*MTGLES20.glGetError()*/);
            }
            GLUtility.checkForGLSLError("GLSLLinkProgram::Activate",
                    "Error linking GLSL Program Object : ", mGLHandle,
                    mLinked == 0, mLinked == 0);
            //	mLinked = GLES20.GL_TRUE;

            if (mLinked == GL20.GL_TRUE) {
                buildGLUniformReferences();
                extractAttributes();
            }
        }

        if (mLinked != 0) {
            if (ENG_RenderRoot.isGLDebugEnabled()) {
                GLUtility.checkForGLSLError("GLSLLinkProgram::Activate",
                        "Error prior to using GLSL Program Object : ",
                        mGLHandle, false, true);
            }

            MTGLES20.glUseProgram(mGLHandle);

            if (ENG_RenderRoot.isGLDebugEnabled()) {
                GLUtility.checkForGLSLError("GLSLLinkProgram::Activate",
                        "Error using GLSL Program Object : ", mGLHandle, false, true);
            }
        }
    }

    /** @noinspection deprecation */
    public void updateUniforms(ENG_GpuProgramParameters params, short mask,
                               GpuProgramType fromProgType) {

        for (GLUniformReference currentUniform : mGLUniformReferences) {
            // Only pull values from buffer it's supposed to be in (vertex or fragment)
            // This method will be called twice, once for vertex program params,
            // and once for fragment program params.
            if (fromProgType == currentUniform.mSourceProgType) {
                ENG_GpuConstantDefinition def = currentUniform.mConstantDef;

                if ((def.variability & mask) != 0) {

                    int glArraySize = def.arraySize;

                    switch (def.constType) {
                        case GCT_FLOAT1: {
//						MTGLES20.glUniform1fv(currentUniform.mLocation,
//								glArraySize, 
//								params.getFloatPointerArray(), 
//								def.physicalIndex);
                            FloatBuffer buffer = params.getFloatPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform1fv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_FLOAT2: {
//						MTGLES20.glUniform2fv(currentUniform.mLocation,
//								glArraySize, 
//								params.getFloatPointerArray(), 
//								def.physicalIndex);
                            FloatBuffer buffer = params.getFloatPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 2);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform2fv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_FLOAT3: {
//						MTGLES20.glUniform3fv(currentUniform.mLocation,
//								glArraySize, 
//								params.getFloatPointerArray(), 
//								def.physicalIndex);
                            FloatBuffer buffer = params.getFloatPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 3);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform3fv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_FLOAT4: {
//						MTGLES20.glUniform4fv(currentUniform.mLocation,
//								glArraySize, 
//								params.getFloatPointerArray(), 
//								def.physicalIndex);
                            FloatBuffer buffer = params.getFloatPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 4);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform4fv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_MATRIX_2X2: {
//						MTGLES20.glUniformMatrix2fv(currentUniform.mLocation,
//								glArraySize, 
//								false, 
//								params.getFloatPointerArray(), 
//								def.physicalIndex);
                            FloatBuffer buffer = params.getFloatPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 4);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniformMatrix2fv(currentUniform.mLocation,
                                    glArraySize,
                                    false,
                                    buffer);
                        }
                        break;
                        case GCT_MATRIX_3X3: {
//						MTGLES20.glUniformMatrix3fv(currentUniform.mLocation,
//								glArraySize, 
//								false, 
//								params.getFloatPointerArray(), 
//								def.physicalIndex);
                            FloatBuffer buffer = params.getFloatPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 9);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniformMatrix3fv(currentUniform.mLocation,
                                    glArraySize,
                                    false,
                                    buffer);
                        }
                        break;
                        case GCT_MATRIX_4X4: {
//						MTGLES20.glUniformMatrix4fv(currentUniform.mLocation,
//								glArraySize, 
//								false, 
//								params.getFloatPointerArray(), 
//								def.physicalIndex);
                            FloatBuffer buffer = params.getFloatPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 16);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniformMatrix4fv(currentUniform.mLocation,
                                    glArraySize,
                                    false,
                                    buffer);
                            break;
                        }
                        case GCT_INT1: {
//						MTGLES20.glUniform1iv(currentUniform.mLocation,
//								glArraySize, 
//								params.getIntPointerArray(), 
//								def.physicalIndex);
                            IntBuffer buffer = params.getIntPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform1iv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_INT2: {
//						MTGLES20.glUniform2iv(currentUniform.mLocation,
//								glArraySize, 
//								params.getIntPointerArray(), 
//								def.physicalIndex);
                            IntBuffer buffer = params.getIntPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 2);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform2iv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_INT3: {
//						MTGLES20.glUniform3iv(currentUniform.mLocation,
//								glArraySize, 
//								params.getIntPointerArray(), 
//								def.physicalIndex);
                            IntBuffer buffer = params.getIntPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 3);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform3iv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_INT4: {
//						MTGLES20.glUniform4iv(currentUniform.mLocation,
//								glArraySize, 
//								params.getIntPointerArray(), 
//								def.physicalIndex);
                            IntBuffer buffer = params.getIntPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize * 4);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform4iv(currentUniform.mLocation,
                                    glArraySize,
                                    buffer);
                        }
                        break;
                        case GCT_SAMPLER1D:
                        case GCT_SAMPLER1DSHADOW:
                        case GCT_SAMPLER2D:
                        case GCT_SAMPLER2DSHADOW:
                        case GCT_SAMPLER3D:
                        case GCT_SAMPLERCUBE: {
                            // samplers handled like 1-element ints
//						MTGLES20.glUniform1iv(currentUniform.mLocation,
//								1, 
//								params.getIntPointerArray(), 
//								def.physicalIndex);
                            IntBuffer buffer = params.getIntPointerArray();
                            buffer.limit(def.physicalIndex + glArraySize);
                            buffer.position(def.physicalIndex);
                            MTGLES20.glUniform1iv(currentUniform.mLocation,
                                    1,
                                    buffer);
                        }
                        break;
                        case GCT_UNKNOWN:
                            break;
                    }

                    if (ENG_RenderRoot.isGLDebugEnabled()) {
                        GLUtility.checkForGLSLError(
                                "GLSLLinkProgram::updateUniforms",
                                "Error updating uniform", 0, false, true);
                    }
                }
            }
        }
    }

    /** @noinspection deprecation*/
    public void updatePassIterationUniforms(ENG_GpuProgramParameters params) {
        if (params.hasPassIterationNumber()) {
            int index = params.getPassIterationNumber();

            for (GLUniformReference currentUniform : mGLUniformReferences) {
                if (index == currentUniform.mConstantDef.physicalIndex) {
//					MTGLES20.glUniform1iv(currentUniform.mLocation,
//							1, 
//							params.getIntPointerArray(), 
//							index);
                    IntBuffer buffer = params.getIntPointerArray();
                    buffer.limit(index + 1);
                    buffer.position(index);
                    MTGLES20.glUniform1iv(currentUniform.mLocation,
                            1,
                            buffer
                    );
                    return;
                }
            }
        }
    }

    public int getAttributeIndex(VertexElementSemantic semantic, int index) {
        return GLGpuProgram.getFixedAttributeIndex(semantic, index);
    }

    public boolean isAttributeValid(VertexElementSemantic semantic, int index) {
        return mValidAttributes.contains(new ENG_Integer(getAttributeIndex(semantic, index)));
    }
	
/*	public int getFixedAttributeIndex(VertexElementSemantic semantic, int index) {
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
		switch(semantic)
		{
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
			//assert(false && "Missing attribute!");
			//return 0;
		}
	}*/

}
