/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 9:41 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.mtgles20;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

@Deprecated
public class GLESCall {

    public enum GLCallList {
        glActiveTexture,
        glAttachShader,
        glBindAttribLocation,
        glBindBuffer,
        glBindFramebuffer,
        glBindRenderbuffer,
        glBindTexture,
        glBlendColor,
        glBlendEquation,
        glBlendEquationSeparate,
        glBlendFunc,
        glBlendFuncSeparate,
        glBufferData,
        glBufferSubData,
        glCheckFramebufferStatus,
        glClear,
        glClearColor,
        glClearDepthf,
        glClearStencil,
        glColorMask,
        glCompileShader,
        glCompressedTexImage2D,
        glCompressedTexSubImage2D,
        glCopyTexImage2D,
        glCopyTexSubImage2D,
        glCreateProgram,
        glCreateShader,
        glCullFace,
        glDeleteBuffersArr,
        glDeleteBuffersBuf,
        glDeleteFramebuffersArr,
        glDeleteFramebuffersBuf,
        glDeleteProgram,
        glDeleteRenderbuffersBuf,
        glDeleteRenderbuffersArr,
        glDeleteShader,
        glDeleteTexturesArr,
        glDeleteTexturesBuf,
        glDepthFunc,
        glDepthMask,
        glDepthRangef,
        glDetachShader,
        glDisable,
        glDisableVertexAttribArray,
        glDrawArrays,
        glDrawElementsBuf,
        glDrawElementsOff,
        glEnable,
        glEnableVertexAttribArray,
        glFinish,
        glFlush,
        glFramebufferRenderbuffer,
        glFramebufferTexture2D,
        glFrontFace,
        glGenBuffersBuf,
        glGenBuffersArr,
        glGenFramebuffersBuf,
        glGenFramebuffersArr,
        glGenRenderbuffersBuf,
        glGenRenderbuffersArr,
        glGenTexturesBuf,
        glGenTexturesArr,
        glGenerateMipmap,
        glGetActiveAttribBuf,
        glGetActiveAttribArr,
        glGetActiveUniformBuf,
        glGetActiveUniformArr,
        glGetAttachedShadersArr,
        glGetAttachedShadersBuf,
        glGetAttribLocation,
        glGetBooleanvArr,
        glGetBooleanvBuf,
        glGetBufferParameterivBuf,
        glGetBufferParameterivArr,
        glGetError,
        glGetFloatvBuf,
        glGetFloatvArr,
        glGetFramebufferAttachmentParameterivBuf,
        glGetFramebufferAttachmentParameterivArr,
        glGetIntegervBuf,
        glGetIntegervArr,
        glGetProgramInfoLog,
        glGetProgramivBuf,
        glGetProgramivArr,
        glGetRenderbufferParameterivArr,
        glGetRenderbufferParameterivBuf,
        glGetShaderInfoLog,
        glGetShaderPrecisionFormatBuf,
        glGetShaderPrecisionFormatArr,
        glGetShaderSourceArr,
        glGetShaderSourceBuf,
        glGetShaderivBuf,
        glGetShaderivArr,
        glGetString,
        glGetTexParameterfvArr,
        glGetTexParameterfvBuf,
        glGetTexParameterivArr,
        glGetTexParameterivBuf,
        glGetUniformLocation,
        glGetUniformfvFBuf,
        glGetUniformfvFArr,
        glGetUniformivIBuf,
        glGetUniformivIArr,
        glGetVertexAttribfvFArr,
        glGetVertexAttribfvFBuf,
        glGetVertexAttribivIArr,
        glGetVertexAttribivIBuf,
        glHint,
        glIsBuffer,
        glIsEnabled,
        glIsFramebuffer,
        glIsProgram,
        glIsRenderbuffer,
        glIsShader,
        glIsTexture,
        glLineWidth,
        glLinkProgram,
        glPixelStorei,
        glPolygonOffset,
        glReadPixels,
        glReleaseShaderCompiler,
        glRenderbufferStorage,
        glSampleCoverage,
        glScissor,
        glShaderBinaryBuf,
        glShaderBinaryArr,
        glShaderSource,
        glStencilFunc,
        glStencilFuncSeparate,
        glStencilMask,
        glStencilMaskSeparate,
        glStencilOp,
        glStencilOpSeparate,
        glTexImage2D,
        glTexParameterf,
        glTexParameterfvArr,
        glTexParameterfvBuf,
        glTexParameteri,
        glTexParameterivBuf,
        glTexParameterivArr,
        glTexSubImage2D,
        glUniform1f,
        glUniform1fvArr,
        glUniform1fvBuf,
        glUniform1i,
        glUniform1ivBuf,
        glUniform1ivArr,
        glUniform2f,
        glUniform2fvBuf,
        glUniform2fvArr,
        glUniform2i,
        glUniform2ivBuf,
        glUniform2ivArr,
        glUniform3f,
        glUniform3fvArr,
        glUniform3fvBuf,
        glUniform3i,
        glUniform3ivBuf,
        glUniform3ivArr,
        glUniform4f,
        glUniform4fvBuf,
        glUniform4fvArr,
        glUniform4i,
        glUniform4ivArr,
        glUniform4ivBuf,
        glUniformMatrix2fvBuf,
        glUniformMatrix2fvArr,
        glUniformMatrix3fvArr,
        glUniformMatrix3fvBuf,
        glUniformMatrix4fvArr,
        glUniformMatrix4fvBuf,
        glUseProgram,
        glValidateProgram,
        glVertexAttrib1f,
        glVertexAttrib1fvBuf,
        glVertexAttrib1fvArr,
        glVertexAttrib2f,
        glVertexAttrib2fvArr,
        glVertexAttrib2fvBuf,
        glVertexAttrib3f,
        glVertexAttrib3fvBuf,
        glVertexAttrib3fvArr,
        glVertexAttrib4f,
        glVertexAttrib4fvBuf,
        glVertexAttrib4fvArr,
        glVertexAttribPointerBuf,
        glVertexAttribPointer,
        glViewport,
        glUnknown,
        glNone
    }

    private GLCallList call = GLCallList.glUnknown;
    private Buffer[] bufferArrParam;
    private float[] floatArrParam;
    private int[] intArrParam;
    private boolean[] booleanArrParam;
    private ENG_Float[] floatObjParam;
    private ENG_Integer[] intObjParam;
    private ENG_Boolean[] booleanObjParam;
    private String[] stringObjParam;

    public GLESCall() {

    }

    public void setStringObjCount(int count) {
        stringObjParam = new String[count];
    }

    public void setStringObjParam(int pos, String value) {
        if (pos < 0 || pos >= stringObjParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        stringObjParam[pos] = value;
    }

    public void setBufferCount(int count) {
        bufferArrParam = new Buffer[count];
    }

    public void setFloatArrayCount(int count) {
        floatArrParam = new float[count];
    }

    public void setIntArrayCount(int count) {
        intArrParam = new int[count];
    }

    public void setBooleanArrayCount(int count) {
        booleanArrParam = new boolean[count];
    }

    public void setFloatObjCount(int count) {
        floatObjParam = new ENG_Float[count];
    }

    public void setIntObjCount(int count) {
        intObjParam = new ENG_Integer[count];
    }

    public void setBooleanObjCount(int count) {
        booleanObjParam = new ENG_Boolean[count];
    }

    public void setBuffer(int pos, Buffer buf) {
        setBuffer(pos, buf, false);
    }

    public void setBuffer(int pos, Buffer buf, boolean copy) {
        if (buf instanceof FloatBuffer) {
            setBuffer(pos, (FloatBuffer) buf, copy);
        } else if (buf instanceof IntBuffer) {
            setBuffer(pos, (IntBuffer) buf, copy);
        } else if (buf instanceof ShortBuffer) {
            setBuffer(pos, (ShortBuffer) buf, copy);
        } else {
            // Also set the ByteBuffer!!!
            setBuffer(pos, (ByteBuffer) buf, copy);
        }
    }

    public void setBuffer(int pos, ByteBuffer buf) {
        setBuffer(pos, buf, false);
    }

    public void setBuffer(int pos, ByteBuffer buf, boolean copy) {
        if (pos < 0 || pos >= bufferArrParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        if (copy) {
            bufferArrParam[pos] = ENG_Utility.cloneBuffer(buf);
        } else {
            bufferArrParam[pos] = buf;
        }
    }

    public void setBuffer(int pos, FloatBuffer buf) {
        setBuffer(pos, buf, false);
    }

    public void setBuffer(int pos, FloatBuffer buf, boolean copy) {
        if (pos < 0 || pos >= bufferArrParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        if (copy) {
            bufferArrParam[pos] = ENG_Utility.cloneBuffer(buf);
        } else {
            bufferArrParam[pos] = buf;
        }
    }

    public void setBuffer(int pos, IntBuffer buf) {
        setBuffer(pos, buf, false);
    }

    public void setBuffer(int pos, IntBuffer buf, boolean copy) {
        if (pos < 0 || pos >= bufferArrParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        if (copy) {
            bufferArrParam[pos] = ENG_Utility.cloneBuffer(buf);
        } else {
            bufferArrParam[pos] = buf;
        }
    }

    public void setBuffer(int pos, ShortBuffer buf) {
        setBuffer(pos, buf, false);
    }

    public void setBuffer(int pos, ShortBuffer buf, boolean copy) {
        if (pos < 0 || pos >= bufferArrParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        if (copy) {
            bufferArrParam[pos] = ENG_Utility.cloneBuffer(buf);
        } else {
            bufferArrParam[pos] = buf;
        }
    }

    public void setFloatArrayParam(int pos, float value) {
        if (pos < 0 || pos >= floatArrParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        floatArrParam[pos] = value;
    }

    public void setIntArrayParam(int pos, int value) {
        if (pos < 0 || pos >= intArrParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        intArrParam[pos] = value;
    }

    public void setBooleanArrayParam(int pos, boolean value) {
        if (pos < 0 || pos >= booleanArrParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        booleanArrParam[pos] = value;
    }

    public void setFloatObjParam(int pos, ENG_Float value) {
        setFloatObjParam(pos, value, false);
    }

    public void setFloatObjParam(int pos, ENG_Float value, boolean copy) {
        if (pos < 0 || pos >= floatObjParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        if (copy) {
            floatObjParam[pos] = new ENG_Float(value);
        } else {
            floatObjParam[pos] = value;
        }
    }

    public void setIntObjParam(int pos, ENG_Integer value) {
        setIntObjParam(pos, value, false);
    }

    public void setIntObjParam(int pos, ENG_Integer value, boolean copy) {
        if (pos < 0 || pos >= intObjParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        if (copy) {
            intObjParam[pos] = new ENG_Integer(value);
        } else {
            intObjParam[pos] = value;
        }
    }

    public void setBooleanObjParam(int pos, ENG_Boolean value) {
        setBooleanObjParam(pos, value, false);
    }

    public void setBooleanObjParam(int pos, ENG_Boolean value, boolean copy) {
        if (pos < 0 || pos >= booleanObjParam.length) {
            throw new IllegalArgumentException("pos must be positive and less than " +
                    "the buffer length");
        }
        if (copy) {
            booleanObjParam[pos] = new ENG_Boolean(value);
        } else {
            booleanObjParam[pos] = value;
        }
    }

    public void setFloatObjParam(int pos, float value) {
        setFloatObjParam(pos, new ENG_Float(value), false);
    }

    public void setIntObjParam(int pos, int value) {
        setIntObjParam(pos, new ENG_Integer(value), false);
    }

    public void setBooleanObjParam(int pos, boolean value) {
        setBooleanObjParam(pos, new ENG_Boolean(value), false);
    }

    /**
     * @return the call
     */
    public GLCallList getCall() {
        return call;
    }

    /**
     * @param call the call to set
     */
    public void setCall(GLCallList call) {
        this.call = call;
    }

    /**
     * @return the bufferArrParam
     */
    public Buffer[] getBufferArrParam() {
        return bufferArrParam;
    }

    /**
     * @param bufferArrParam the bufferArrParam to set
     */
    public void setBufferArrParam(Buffer[] bufferArrParam) {
        this.bufferArrParam = bufferArrParam;
    }

    /**
     * @return the floatArrParam
     */
    public float[] getFloatArrParam() {
        return floatArrParam;
    }

    /**
     * @param floatArrParam the floatArrParam to set
     */
    public void setFloatArrParam(float[] floatArrParam) {
        this.floatArrParam = floatArrParam;
    }

    /**
     * @return the intArrParam
     */
    public int[] getIntArrParam() {
        return intArrParam;
    }

    /**
     * @param intArrParam the intArrParam to set
     */
    public void setIntArrParam(int[] intArrParam) {
        this.intArrParam = intArrParam;
    }

    /**
     * @return the booleanArrParam
     */
    public boolean[] getBooleanArrParam() {
        return booleanArrParam;
    }

    /**
     * @param booleanArrParam the booleanArrParam to set
     */
    public void setBooleanArrParam(boolean[] booleanArrParam) {
        this.booleanArrParam = booleanArrParam;
    }

    /**
     * @return the floatObjParam
     */
    public ENG_Float[] getFloatObjParam() {
        return floatObjParam;
    }

    /**
     * @param floatObjParam the floatObjParam to set
     */
    public void setFloatObjParam(ENG_Float[] floatObjParam) {
        this.floatObjParam = floatObjParam;
    }

    /**
     * @return the intObjParam
     */
    public ENG_Integer[] getIntObjParam() {
        return intObjParam;
    }

    /**
     * @param intObjParam the intObjParam to set
     */
    public void setIntObjParam(ENG_Integer[] intObjParam) {
        this.intObjParam = intObjParam;
    }

    /**
     * @return the booleanObjParam
     */
    public ENG_Boolean[] getBooleanObjParam() {
        return booleanObjParam;
    }

    /**
     * @param booleanObjParam the booleanObjParam to set
     */
    public void setBooleanObjParam(ENG_Boolean[] booleanObjParam) {
        this.booleanObjParam = booleanObjParam;
    }

    /**
     * @return the stringObjParam
     */
    public String[] getStringObjParam() {
        return stringObjParam;
    }

    /**
     * @param stringObjParam the stringObjParam to set
     */
    public void setStringObjParam(String[] stringObjParam) {
        this.stringObjParam = stringObjParam;
    }
}
