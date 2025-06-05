/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 9:41 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles;

import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_MainThread;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.exception.ENG_GLException;
import headwayent.hotshotengine.exception.ENG_InvalidFieldStateException;
import headwayent.hotshotengine.renderer.opengles.mtgles20.GLESCall;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.graphics.GL20;

import static com.badlogic.gdx.Gdx.*;
import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

@Deprecated
public class GLMainThread /*implements GLSurfaceView.Renderer*/ {

    /** @noinspection deprecation*/
    private static GLMainThread glMainThread;
    private static final AtomicBoolean renderingContinued = new AtomicBoolean();
    private static final boolean TEST = false;
    private static final boolean ERRORS_FATAL = true;


    public GLMainThread() {
//		if (glMainThread == null) {
        glMainThread = this;
//		} else {
//			throw new ENG_MultipleSingletonConstructAttemptException("There can't " + 
//					"be more than one rendering thread");
//		}
    }

    public static void checkErrors(String methodName) {
        if (TEST) {
            int err = gl20.glGetError();

//			String msg = GLU.gluErrorString(err);
            System.out.println(
                    "glCall: " + methodName + " " + err + " error code: " + err);
            if (ERRORS_FATAL && err != 0) {
                throw new ENG_GLException("Error glGetError " +
                        err);
            }
        }
    }

    /** @noinspection deprecation*/
    private static void handleGLActiveTexture(GLESCall call) {
        if (call.getIntArrParam() != null) {
            gl20.glActiveTexture(call.getIntArrParam()[0]);
        } else {
            gl20.glActiveTexture(call.getIntObjParam()[0].getValue());
        }
        checkErrors("glActiveTexture");
    }

    /** @noinspection deprecation*/
    private static void handleGLAttachShader(GLESCall call) {
        //	int err = Gdx.gl20.glGetError();
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glAttachShader(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glAttachShader(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glAttachShader");
    }

    /** @noinspection deprecation*/
    private static void handleGLBindAttribLocation(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBindAttribLocation(intArr[0], intArr[1], call.getStringObjParam()[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBindAttribLocation(intArr[0].getValue(), intArr[1].getValue(),
                    call.getStringObjParam()[0]);
        }
        checkErrors("glBindAttribLocation");
    }

    /** @noinspection deprecation*/
    private static void handleGLBindBuffer(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBindBuffer(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBindBuffer(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glBindBuffer");
    }

    /** @noinspection deprecation*/
    private static void handleGLBindFramebuffer(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBindFramebuffer(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBindFramebuffer(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glBindFrameBuffer");
    }

    /** @noinspection deprecation*/
    private static void handleGLBindRenderbuffer(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBindRenderbuffer(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBindRenderbuffer(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glBindRenderBuffer");
    }

    /** @noinspection deprecation*/
    private static void handleGLBindTexture(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBindTexture(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBindTexture(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glBindTexture");
    }

    /** @noinspection deprecation*/
    private static void handleGLBlendColor(GLESCall call) {
        if (call.getFloatArrParam() != null) {
            float[] floatArr = call.getFloatArrParam();
            gl20.glBlendColor(floatArr[0], floatArr[1], floatArr[2], floatArr[3]);
        } else {
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glBlendColor(floatArr[0].getValue(), floatArr[1].getValue(),
                    floatArr[2].getValue(), floatArr[3].getValue());
        }
        checkErrors("glBlendColor");
    }

    /** @noinspection deprecation*/
    private static void handleGLBlendEquation(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBlendEquation(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBlendEquation(intArr[0].getValue());
        }
        checkErrors("glBlendEquation");
    }

    /** @noinspection deprecation*/
    private static void handleGLBlendEquationSeparate(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBlendEquationSeparate(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBlendEquationSeparate(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glBlendEquationSeparate");
    }

    /** @noinspection deprecation*/
    private static void handleGLBlendFunc(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBlendFunc(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBlendFunc(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glBlendFunc");
    }

    /** @noinspection deprecation*/
    private static void handleGLBlendFuncSeparate(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glBlendFuncSeparate(intArr[0], intArr[1], intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBlendFuncSeparate(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glBlendFuncSeparate");
    }

    /** @noinspection deprecation*/
    private static void handleGLBufferData(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
        /*	if (call.getBufferArrParam()[0] != null) {
				ShortBuffer buffer = ((ByteBuffer)call.getBufferArrParam()[0]).asShortBuffer();
				while (buffer.position() < buffer.limit()) {
					System.out.println(buffer.get());
				}
			}*/
            gl20.glBufferData(intArr[0], intArr[1],
                    call.getBufferArrParam()[0], intArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBufferData(intArr[0].getValue(), intArr[1].getValue(),
                    call.getBufferArrParam()[0], intArr[2].getValue());
        }
        checkErrors("glBufferData");
    }

    /** @noinspection deprecation*/
    private static void handleGLBufferSubData(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
		/*	if (call.getBufferArrParam()[0] != null) {
				FloatBuffer buffer = ((ByteBuffer)call.getBufferArrParam()[0]).asFloatBuffer();
				while (buffer.position() < buffer.limit()) {
					System.out.println(buffer.get());
				}
			}*/
            gl20.glBufferSubData(intArr[0], intArr[1],
                    intArr[2], call.getBufferArrParam()[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glBufferSubData(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), call.getBufferArrParam()[0]);
        }
        checkErrors("glBufferSubData");
    }

    /** @noinspection deprecation*/
    private static void handleGLClear(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glClear(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glClear(intArr[0].getValue());
        }
        checkErrors("glClear");
    }

    /** @noinspection deprecation*/
    private static void handleGLClearColor(GLESCall call) {
        if (call.getFloatArrParam() != null) {
            float[] floatArr = call.getFloatArrParam();
            gl20.glClearColor(floatArr[0], floatArr[1], floatArr[2], floatArr[3]);
        } else {
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glClearColor(floatArr[0].getValue(), floatArr[1].getValue(),
                    floatArr[2].getValue(), floatArr[3].getValue());
        }
        checkErrors("glClearColor");
    }

    /** @noinspection deprecation*/
    private static void handleGLClearDepthf(GLESCall call) {
        if (call.getFloatArrParam() != null) {
            float[] floatArr = call.getFloatArrParam();
            gl20.glClearDepthf(floatArr[0]);
        } else {
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glClearDepthf(floatArr[0].getValue());
        }
        checkErrors("glClearDepth");
    }

    /** @noinspection deprecation*/
    private static void handleGLClearStencil(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glClearStencil(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glClearStencil(intArr[0].getValue());
        }
        checkErrors("glClearStencil");
    }

    /** @noinspection deprecation*/
    private static void handleGLColorMask(GLESCall call) {
        if (call.getBooleanArrParam() != null) {
            boolean[] boolArr = call.getBooleanArrParam();
            gl20.glColorMask(boolArr[0], boolArr[1], boolArr[2], boolArr[3]);
        } else {
            ENG_Boolean[] boolArr = call.getBooleanObjParam();
            gl20.glColorMask(boolArr[0].getValue(), boolArr[1].getValue(),
                    boolArr[2].getValue(), boolArr[3].getValue());
        }
        checkErrors("glColorMask");
    }

    /** @noinspection deprecation*/
    private static void handleGLCompileShader(GLESCall call) {

        IntBuffer ret = ENG_Utility.allocateDirect(4).asIntBuffer();
//		int[] ret = new int[1];
        String log = null;
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glCompileShader(intArr[0]);
            gl20.glGetShaderiv(intArr[0], GL20.GL_COMPILE_STATUS, ret/*, 0*/);
            if (ret.get() == 0) {
                log = gl20.glGetShaderInfoLog(intArr[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glCompileShader(intArr[0].getValue());
            gl20.glGetShaderiv(intArr[0].getValue(), GL20.GL_COMPILE_STATUS, ret/*, 0*/);
            if (ret.get() == 0) {
                log = gl20.glGetShaderInfoLog(intArr[0].getValue());
            }
        }
        checkErrors("glCompileShader");
    }

    /** @noinspection deprecation*/
    private static void handleGLCompressedTexImage2D(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glCompressedTexImage2D(intArr[0], intArr[1], intArr[2], intArr[3],
                    intArr[4], intArr[5], intArr[6], call.getBufferArrParam()[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glCompressedTexImage2D(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(),
                    intArr[4].getValue(), intArr[5].getValue(),
                    intArr[6].getValue(), call.getBufferArrParam()[0]);
        }
        checkErrors("glCompressedTexImage2D");
    }

    /** @noinspection deprecation*/
    private static void handleGLCompressedTexSubImage2D(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glCompressedTexSubImage2D(intArr[0], intArr[1], intArr[2], intArr[3],
                    intArr[4], intArr[5], intArr[6], intArr[7], call.getBufferArrParam()[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glCompressedTexSubImage2D(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(),
                    intArr[4].getValue(), intArr[5].getValue(),
                    intArr[6].getValue(), intArr[7].getValue(), call.getBufferArrParam()[0]);
        }
        checkErrors("glCompressedTexSubImage2D");
    }

    /** @noinspection deprecation*/
    private static void handleGLCopyTexImage2D(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glCopyTexImage2D(intArr[0], intArr[1], intArr[2], intArr[3],
                    intArr[4], intArr[5], intArr[6], intArr[7]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glCopyTexImage2D(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(),
                    intArr[4].getValue(), intArr[5].getValue(),
                    intArr[6].getValue(), intArr[7].getValue());
        }
        checkErrors("glCopyTexImage2D");
    }

    /** @noinspection deprecation*/
    private static void handleGLCopyTexSubImage2D(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glCopyTexSubImage2D(intArr[0], intArr[1], intArr[2], intArr[3],
                    intArr[4], intArr[5], intArr[6], intArr[7]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glCopyTexSubImage2D(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(),
                    intArr[4].getValue(), intArr[5].getValue(),
                    intArr[6].getValue(), intArr[7].getValue());
        }
        checkErrors("glCopyTexSubImage2D");
    }

    /** @noinspection deprecation*/
    private static void handleGLCullFace(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glCullFace(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glCullFace(intArr[0].getValue());
        }
        checkErrors("glCullFace");
    }

    /** @noinspection deprecation*/
    private static void handleGLDeleteBuffers(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glDeleteBuffers(intArr[0], (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glDeleteBuffers(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glDeleteBuffers(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glDeleteBuffers(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glDeleteBuffers");
    }

    /** @noinspection deprecation*/
    private static void handleGLDeleteFramebuffers(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glDeleteFramebuffers(intArr[0],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glDeleteFramebuffers(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glDeleteFramebuffers(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glDeleteFramebuffers(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glDeleteFramebuffers");
    }

    /** @noinspection deprecation*/
    private static void handleGLDeleteProgram(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glDeleteProgram(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glDeleteProgram(intArr[0].getValue());
        }
        checkErrors("glDeleteProgram");
    }

    /** @noinspection deprecation*/
    private static void handleGLDeleteRenderbuffers(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glDeleteRenderbuffers(intArr[0],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glDeleteRenderbuffers(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glDeleteRenderbuffers(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glDeleteRenderbuffers(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glDeleteRenderbuffers");
    }

    /** @noinspection deprecation*/
    private static void handleGLDeleteShader(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glDeleteShader(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glDeleteShader(intArr[0].getValue());
        }
        checkErrors("glDeleteShader");
    }

    /** @noinspection deprecation*/
    private static void handleGLDeleteTextures(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glDeleteTextures(intArr[0],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glDeleteTextures(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glDeleteTextures(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glDeleteTextures(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glDeleteTextures");
    }

    /** @noinspection deprecation*/
    private static void handleGLDepthFunc(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glDepthFunc(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glDepthFunc(intArr[0].getValue());
        }
        checkErrors("glDepthFunc");
    }

    /** @noinspection deprecation*/
    private static void handleGLDepthMask(GLESCall call) {
        if (call.getBooleanArrParam() != null) {
            boolean[] boolArr = call.getBooleanArrParam();
            gl20.glDepthMask(boolArr[0]);
        } else {
            ENG_Boolean[] boolArr = call.getBooleanObjParam();
            gl20.glDepthMask(boolArr[0].getValue());
        }
        checkErrors("glDepthMask");
    }

    /** @noinspection deprecation*/
    private static void handleGLDepthRangef(GLESCall call) {
        if (call.getFloatArrParam() != null) {
            float[] floatArr = call.getFloatArrParam();
            gl20.glDepthRangef(floatArr[0], floatArr[1]);
        } else {
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glDepthRangef(floatArr[0].getValue(), floatArr[1].getValue());
        }
        checkErrors("glDepthRange");
    }

    /** @noinspection deprecation*/
    private static void handleGLDetachShader(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glDetachShader(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glDetachShader(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glDetachShader");
    }

    /** @noinspection deprecation*/
    private static void handleGLDisable(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glDisable(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glDisable(intArr[0].getValue());
        }
        checkErrors("glDisable");
    }

    /** @noinspection deprecation*/
    private static void handleGLDisableVertexAttribArray(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glDisableVertexAttribArray(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glDisableVertexAttribArray(intArr[0].getValue());
        }
        checkErrors("glDisableVertexAttribArray");
    }

    /** @noinspection deprecation*/
    private static void handleGLDrawArrays(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glDrawArrays(intArr[0], intArr[1], intArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glDrawArrays(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue());
        }
        checkErrors("glDrawArrays");
    }

    /** @noinspection deprecation*/
    private static void handleGLDrawElements(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glDrawElements(intArr[0], intArr[1], intArr[2],
                        call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glDrawElements(intArr[0].getValue(), intArr[1].getValue(),
                        intArr[2].getValue(), call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glDrawElements(intArr[0], intArr[1], intArr[2],
                        intArr[3]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glDrawElements(intArr[0].getValue(), intArr[1].getValue(),
                        intArr[2].getValue(), intArr[3].getValue());
            }
        }
        checkErrors("glDrawElements");
    }

    /** @noinspection deprecation*/
    private static void handleGLEnable(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glEnable(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glEnable(intArr[0].getValue());
        }
        checkErrors("glEnable");
    }

    /** @noinspection deprecation*/
    private static void handleGLEnableVertexAttribArray(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glEnableVertexAttribArray(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glEnableVertexAttribArray(intArr[0].getValue());
        }
        checkErrors("glEnableVertexAttribArray");
    }

    private static void handleGLFinish() {
        gl20.glFinish();
        checkErrors("glFinish");
    }

    private static void handleGLFlush() {
        gl20.glFlush();
        checkErrors("glFlush");
    }

    /** @noinspection deprecation*/
    private static void handleGLFramebufferRenderbuffer(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glFramebufferRenderbuffer(intArr[0], intArr[1], intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glFramebufferRenderbuffer(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glFramebufferRenderbuffer");
    }

    /** @noinspection deprecation*/
    private static void handleGLFramebufferTexture2D(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glFramebufferTexture2D(intArr[0], intArr[1], intArr[2], intArr[3],
                    intArr[4]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glFramebufferTexture2D(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(), intArr[4].getValue());
        }
        checkErrors("glFramebufferTexture2D");
    }

    /** @noinspection deprecation*/
    private static void handleGLFrontFace(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glFrontFace(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glFrontFace(intArr[0].getValue());
        }
        checkErrors("glFrontFace");
    }

    /** @noinspection deprecation*/
    private static void handleGLGenBuffers(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glGenBuffers(intArr[0], (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glGenBuffers(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glGenBuffers(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glGenBuffers(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glGenBuffers");
    }

    /** @noinspection deprecation*/
    private static void handleGLGenFramebuffers(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glGenFramebuffers(intArr[0], (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glGenFramebuffers(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glGenFramebuffers(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glGenFramebuffers(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glGenFramebuffers");
    }

    /** @noinspection deprecation*/
    private static void handleGLGenRenderbuffers(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glGenRenderbuffers(intArr[0], (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glGenRenderbuffers(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glGenRenderbuffers(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glGenRenderbuffers(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glGenRenderbuffers");
    }

    /** @noinspection deprecation*/
    private static void handleGLGenTextures(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glGenTextures(intArr[0], (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glGenTextures(intArr[0].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 1, intArr.length);
                gl20.glGenTextures(intArr[0], buffer/*intArr, intArr[intArr.length - 1] + 1*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 1, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
                gl20.glGenTextures(intArr[0].getValue(), buffer//buf,
						/*intArr[intArr.length - 1].getValue()*/);
                ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buf, 0,
                        intArr, 1, buf.length);
            }
        }
        checkErrors("glGenTextures");
    }

    /** @noinspection deprecation*/
    private static void handleGLGenerateMipmap(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glGenerateMipmap(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glGenerateMipmap(intArr[0].getValue());
        }
        checkErrors("glGenerateMipmap");
    }
	
/*	private static void handleGLGetActiveAttrib(GLESCall call) {
		if (call.getBufferArrParam() != null) {
			if (call.getIntArrParam() != null) {
				int[] intArr = call.getIntArrParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetActiveAttrib(intArr[0], intArr[1], intArr[2],
						(IntBuffer) buf[0], (IntBuffer) buf[1], (IntBuffer) buf[2],
						(byte) intArr[3]);
			} else {
				ENG_Integer[] intArr = call.getIntObjParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetActiveAttrib(intArr[0].getValue(), intArr[1].getValue(),
						intArr[2].getValue(),
						(IntBuffer) buf[0], (IntBuffer) buf[1], (IntBuffer) buf[2],
						(byte) intArr[3].getValue());
			}
		} else {
			ENG_Integer[] intArr = call.getIntObjParam();
			int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 7, 
					intArr[3].getValue() + 7);
			int[] secondBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 
					intArr[3].getValue() + 7, 
					intArr[3].getValue() + intArr[4].getValue() + 7);
			int[] thirdBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 
					intArr[3].getValue() + intArr[4].getValue() + 7, 
					intArr[3].getValue() + intArr[4].getValue() + intArr[5].getValue() + 7);			
			int[] fourthBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 
					intArr[3].getValue() + intArr[4].getValue() + intArr[5].getValue() + 7, 
					intArr[3].getValue() + intArr[4].getValue() + intArr[5].getValue() + 
					intArr[6].getValue() + 7);
			byte[] b = new byte[fourthBuf.length];
			for (int i = 0; i < b.length; ++i) {
				b[i] = (byte) fourthBuf[i];
			}
			Gdx.gl20.glGetActiveAttrib(intArr[0].getValue(), intArr[1].getValue(), 
					intArr[2].getValue(), firstBuf, intArr[3].getValue(), secondBuf, 
					intArr[4].getValue(), thirdBuf, intArr[5].getValue(), b, 
					intArr[6].getValue());
			for (int i = 0; i < b.length; ++i) {
				fourthBuf[i] = b[i];
			}
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(firstBuf, 0, 
					intArr, 7, firstBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(secondBuf, 0, 
					intArr, 7 + intArr[3].getValue(), secondBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(thirdBuf, 0, 
					intArr, 7 + intArr[3].getValue() + intArr[4].getValue(), thirdBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(fourthBuf, 0, 
					intArr, 7 + intArr[3].getValue() + intArr[4].getValue() + 
					intArr[5].getValue(), fourthBuf.length);
		}
		checkErrors("glGetActiveAttrib");
	}*/
	
/*	private static void handleGLGetActiveUniform(GLESCall call) {
		if (call.getBufferArrParam() != null) {
			if (call.getIntArrParam() != null) {
				int[] intArr = call.getIntArrParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetActiveUniform(intArr[0], intArr[1], intArr[2],
						(IntBuffer) buf[0], (IntBuffer) buf[1], (IntBuffer) buf[2],
						(byte) intArr[3]);
			} else {
				ENG_Integer[] intArr = call.getIntObjParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetActiveUniform(intArr[0].getValue(), intArr[1].getValue(),
						intArr[2].getValue(),
						(IntBuffer) buf[0], (IntBuffer) buf[1], (IntBuffer) buf[2],
						(byte) intArr[3].getValue());
			}
		} else {
			ENG_Integer[] intArr = call.getIntObjParam();
			int lengthOffset = intArr[intArr.length - 4].getValue();
			int sizeOffset = intArr[intArr.length - 3].getValue();
			int typeOffset = intArr[intArr.length - 2].getValue();
			int nameOffset = intArr[intArr.length - 1].getValue();
			int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3,
					lengthOffset + 3);
			int[] secondBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, lengthOffset + 3,
					lengthOffset + sizeOffset + 3);
			int[] thirdBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 
					lengthOffset + sizeOffset + 3,
					lengthOffset + sizeOffset + typeOffset + 3);
			int[] fourthBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 
					lengthOffset + sizeOffset + typeOffset + 3,
					lengthOffset + sizeOffset + typeOffset + nameOffset + 3);
			byte[] b = new byte[fourthBuf.length];
			for (int i = 0; i < b.length; ++i) {
				b[i] = (byte) fourthBuf[i];
			}
			Gdx.gl20.glGetActiveUniform(intArr[0].getValue(), intArr[1].getValue(), 
					intArr[2].getValue(), firstBuf, lengthOffset, secondBuf, 
					sizeOffset, thirdBuf, typeOffset, b, 
					nameOffset);
			for (int i = 0; i < b.length; ++i) {
				fourthBuf[i] = b[i];
			}
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(firstBuf, 0, 
					intArr, 3, firstBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(secondBuf, 0, 
					intArr, 3 + lengthOffset, secondBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(thirdBuf, 0, 
					intArr, 3 + lengthOffset + sizeOffset, thirdBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(fourthBuf, 0, 
					intArr, 3 + lengthOffset + sizeOffset + typeOffset, fourthBuf.length);
		}
		checkErrors("glGetActiveUniform");
	}*/
	
/*	private static void handleGLGetAttachedShaders(GLESCall call) {
		if (call.getBufferArrParam() != null) {
			if (call.getIntArrParam() != null) {
				int[] intArr = call.getIntArrParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetAttachedShaders(intArr[0], intArr[1], 
						(IntBuffer) buf[0], (IntBuffer) buf[1]);
			} else {
				ENG_Integer[] intArr = call.getIntObjParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetAttachedShaders(intArr[0].getValue(), intArr[1].getValue(), 
						(IntBuffer) buf[0], (IntBuffer) buf[1]);
			}
		} else {
			ENG_Integer[] intArr = call.getIntObjParam();
			int countOffset = intArr[intArr.length - 2].getValue();
			int shaderOffset = intArr[intArr.length - 1].getValue();
			int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 2,
					countOffset + 2);
			int[] secondBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, countOffset + 2,
					countOffset + shaderOffset + 2);
			Gdx.gl20.glGetAttachedShaders(intArr[0].getValue(), intArr[1].getValue(),
					firstBuf, countOffset, secondBuf, shaderOffset);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(firstBuf, 0, 
					intArr, 3, firstBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(secondBuf, 0, 
					intArr, countOffset + 2, secondBuf.length);
		}
		checkErrors("glGetAttachedShaders");
	}*/

    /** @noinspection deprecation*/
    private static void handleGLGetBooleanv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetBooleanv(intArr[0], buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetBooleanv(intArr[0].getValue(), buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            boolean[] boolArr = ENG_Utility.getBooleanAsPrimitiveArray(
                    call.getBooleanObjParam());
            ByteBuffer buffer = ENG_Utility.getBooleanArrayAsBuffer(boolArr);
            gl20.glGetBooleanv(intArr[0].getValue(), buffer/*boolArr, intArr[1].getValue()*/);
            ENG_Utility.getBooleanPrimitiveArrayAsBooleanObjArray(buffer, 0,
                    call.getBooleanObjParam(), 0, boolArr.length);
        }
        checkErrors("glGetBooleanv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetBufferParameteriv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetBufferParameteriv(intArr[0], intArr[1], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetBufferParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
            gl20.glGetBufferParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*, intArr[2].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 3, buf.length);
        }
        checkErrors("glGetBufferParameteriv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetFloatv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetFloatv(intArr[0], (FloatBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetFloatv(intArr[0].getValue(), (FloatBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
            FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(buf);
            gl20.glGetFloatv(intArr[0].getValue(), buffer/*, intArr[1].getValue()*/);
            ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(buffer, 0, floatArr, 0, buf.length);
        }
        checkErrors("glGetFloatv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetFramebufferAttachmentParameteriv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetFramebufferAttachmentParameteriv(intArr[0], intArr[1],
                        intArr[2], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetFramebufferAttachmentParameteriv(intArr[0].getValue(),
                        intArr[1].getValue(), intArr[2].getValue(), (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 4, intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
            gl20.glGetFramebufferAttachmentParameteriv(intArr[0].getValue(),
                    intArr[1].getValue(), intArr[2].getValue(), buffer/*, intArr[3].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 4, buf.length);
        }
        checkErrors("glGetFramebufferAttachmentParameteriv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetIntegerv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetIntegerv(intArr[0], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetIntegerv(intArr[0].getValue(), (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 2, intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
            gl20.glGetIntegerv(intArr[0].getValue(), buffer/*, intArr[1].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 2, buf.length);
        }
        checkErrors("glGetIntegerv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetProgramiv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetProgramiv(intArr[0], intArr[1], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetProgramiv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
            gl20.glGetProgramiv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*, intArr[2].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 3, buf.length);
        }
        checkErrors("glGetProgramiv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetRenderbufferParameteriv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetRenderbufferParameteriv(intArr[0], intArr[1], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetRenderbufferParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buf);
            gl20.glGetRenderbufferParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*, intArr[2].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 3, buf.length);
        }
        checkErrors("glGetRenderbufferParameteriv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetShaderPrecisionFormat(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetShaderPrecisionFormat(intArr[0], intArr[1], (IntBuffer) buf[0],
                        (IntBuffer) buf[1]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetShaderPrecisionFormat(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0], (IntBuffer) buf[1]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 4,
                    4 + intArr[2].getValue());
            IntBuffer fbuffer = ENG_Utility.getIntArrayAsBuffer(firstBuf);
            int[] secondBuf = ENG_Utility.getIntAsPrimitiveArray(intArr,
                    4 + intArr[2].getValue(), 4 + intArr[2].getValue() + intArr[3].getValue());
            IntBuffer sbuffer = ENG_Utility.getIntArrayAsBuffer(secondBuf);
            gl20.glGetShaderPrecisionFormat(intArr[0].getValue(), intArr[1].getValue(),
                    fbuffer,//  intArr[2].getValue(),
                    sbuffer/*, intArr[3].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(fbuffer, 0, intArr, 4,
                    firstBuf.length);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(sbuffer, 0, intArr,
                    4 + intArr[2].getValue(), secondBuf.length);
        }
        checkErrors("glGetShaderPrecisionFormat");
    }
	
/*	private static void handleGLGetShaderSource(GLESCall call) {
		if (call.getBufferArrParam() != null) {
			if (call.getIntArrParam() != null) {
				int[] intArr = call.getIntArrParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetShaderSource(intArr[0], intArr[1], (IntBuffer) buf[0],
						(byte) intArr[2]);
			} else {
				ENG_Integer[] intArr = call.getIntObjParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glGetShaderSource(intArr[0].getValue(), intArr[1].getValue(),
						(IntBuffer) buf[0], (byte) intArr[2].getValue());
			}
		} else {
			ENG_Integer[] intArr = call.getIntObjParam();
			int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 4, 
					4 + intArr[2].getValue());
			int[] secondBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 
					4 + intArr[2].getValue(), 4 + intArr[2].getValue() + intArr[3].getValue());
			byte[] b = new byte[secondBuf.length];
			for (int i = 0; i < b.length; ++i) {
				b[i] = (byte) secondBuf[i];
			}
			Gdx.gl20.glGetShaderSource(intArr[0].getValue(), intArr[1].getValue(), 
					firstBuf,  intArr[2].getValue(), 
					b, intArr[3].getValue());
			for (int i = 0; i < b.length; ++i) {
				secondBuf[i] = b[i];
			}
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(firstBuf, 0, intArr, 4, 
					firstBuf.length);
			ENG_Utility.getIntPrimitiveArrayAsIntObjArray(secondBuf, 0, intArr, 
					4 + intArr[2].getValue(), secondBuf.length);
		}
		checkErrors("glGetShaderSource");
	}*/

    /** @noinspection deprecation*/
    private static void handleGLGetShaderiv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetShaderiv(intArr[0], intArr[1], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetShaderiv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3,
                    intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(firstBuf);
            gl20.glGetShaderiv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*firstBuf, intArr[2].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 3,
                    firstBuf.length);
        }
        checkErrors("glGetShaderiv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetTexParameterfv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetTexParameterfv(intArr[0], intArr[1], (FloatBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetTexParameterfv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            float[] firstBuf = ENG_Utility.getFloatAsPrimitiveArray(floatArr, 0,
                    floatArr.length);
            FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(firstBuf);
            gl20.glGetTexParameterfv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*firstBuf, intArr[2].getValue()*/);
            ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(buffer, 0, floatArr, 0,
                    firstBuf.length);
        }
        checkErrors("glGetTexParameterfv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetTexParameteriv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetTexParameteriv(intArr[0], intArr[1], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetTexParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            //ENG_Float[] floatArr = call.getFloatObjParam();
            int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3,
                    intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(firstBuf);
            gl20.glGetTexParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*firstBuf, intArr[2].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 3,
                    firstBuf.length);
        }
        checkErrors("glGetTexParameteriv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetUniformfv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetUniformfv(intArr[0], intArr[1], (FloatBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetUniformfv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            float[] firstBuf = ENG_Utility.getFloatAsPrimitiveArray(floatArr, 0,
                    floatArr.length);
            FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(firstBuf);
            gl20.glGetUniformfv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*firstBuf, intArr[2].getValue()*/);
            ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(buffer, 0, floatArr, 0,
                    firstBuf.length);
        }
        checkErrors("glGetUniformfv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetUniformiv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetUniformiv(intArr[0], intArr[1], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetUniformiv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            //ENG_Float[] floatArr = call.getFloatObjParam();
            int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3,
                    intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(firstBuf);
            gl20.glGetUniformiv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*firstBuf, intArr[2].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 3,
                    firstBuf.length);
        }
        checkErrors("glGetUniformiv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetVertexAttribfv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetVertexAttribfv(intArr[0], intArr[1], (FloatBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetVertexAttribfv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            float[] firstBuf = ENG_Utility.getFloatAsPrimitiveArray(floatArr, 0,
                    floatArr.length);
            FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(firstBuf);
            gl20.glGetVertexAttribfv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*firstBuf, intArr[2].getValue()*/);
            ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(buffer, 0, floatArr, 0,
                    firstBuf.length);
        }
        checkErrors("glGetVertexAttribfv");
    }

    /** @noinspection deprecation*/
    private static void handleGLGetVertexAttribiv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetVertexAttribiv(intArr[0], intArr[1], (IntBuffer) buf[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                Buffer[] buf = call.getBufferArrParam();
                gl20.glGetVertexAttribiv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) buf[0]);
            }
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            //ENG_Float[] floatArr = call.getFloatObjParam();
            int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3,
                    intArr.length);
            IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(firstBuf);
            gl20.glGetVertexAttribiv(intArr[0].getValue(), intArr[1].getValue(),
                    buffer/*firstBuf, intArr[2].getValue()*/);
            ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, 0, intArr, 3,
                    firstBuf.length);
        }
        checkErrors("glGetVertexAttribiv");
    }

    /** @noinspection deprecation*/
    private static void handleGLHint(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glHint(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glHint(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glHint");
    }

    /** @noinspection deprecation*/
    private static void handleGLLineWidth(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glLineWidth(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glLineWidth(intArr[0].getValue());
        }
        checkErrors("glLineWidth");
    }

    /** @noinspection deprecation*/
    private static void handleGLLinkProgram(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glLinkProgram(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glLinkProgram(intArr[0].getValue());
        }

        checkErrors("glLinkProgram");
    }

    /** @noinspection deprecation*/
    private static void handleGLPixelStorei(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glPixelStorei(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glPixelStorei(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glPixelStorei");
    }

    /** @noinspection deprecation*/
    private static void handleGLPolygonOffset(GLESCall call) {
        if (call.getFloatArrParam() != null) {
            float[] floatArr = call.getFloatArrParam();
            gl20.glPolygonOffset(floatArr[0], floatArr[1]);
        } else {
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glPolygonOffset(floatArr[0].getValue(), floatArr[1].getValue());
        }
        checkErrors("glPolygonOffset");
    }

    /** @noinspection deprecation*/
    private static void handleGLReadPixels(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glReadPixels(intArr[0], intArr[1],
                    intArr[2], intArr[3], intArr[3],
                    intArr[4], call.getBufferArrParam()[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glReadPixels(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(), intArr[3].getValue(),
                    intArr[4].getValue(), call.getBufferArrParam()[0]);
        }
        checkErrors("glReadPixels");
    }

    private static void handleGLReleaseShaderCompiler() {
        gl20.glReleaseShaderCompiler();
        checkErrors("glReleaseShaderCompiler");
    }

    /** @noinspection deprecation*/
    private static void handleGLRenderbufferStorage(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glRenderbufferStorage(intArr[0], intArr[1],
                    intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glRenderbufferStorage(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glRenderbufferStorage");
    }

    /** @noinspection deprecation*/
    private static void handleGLSampleCoverage(GLESCall call) {
        if (call.getFloatArrParam() != null) {
            float[] floatArr = call.getFloatArrParam();
            boolean[] boolArr = call.getBooleanArrParam();
            gl20.glSampleCoverage(floatArr[0], boolArr[0]);
        } else {
            ENG_Float[] floatArr = call.getFloatObjParam();
            ENG_Boolean[] boolArr = call.getBooleanObjParam();
            gl20.glSampleCoverage(floatArr[0].getValue(), boolArr[0].getValue());
        }
        checkErrors("glSampleCoverage");
    }

    /** @noinspection deprecation*/
    private static void handleGLScissor(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glScissor(intArr[0], intArr[1],
                    intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glScissor(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glScissor");
    }

    /** @noinspection deprecation*/
    private static void handleGLShaderBinary(GLESCall call) {
        throw new UnsupportedOperationException();
	/*	if (call.getBufferArrParam() != null) {
			if (call.getIntArrParam() != null) {
				int[] intArr = call.getIntArrParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glShaderBinary(intArr[0], (IntBuffer) buf[0], 
						intArr[1], buf[1], intArr[2]);
			} else {
				ENG_Integer[] intArr = call.getIntObjParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glShaderBinary(intArr[0].getValue(), (IntBuffer) buf[0], 
						intArr[1].getValue(), buf[1], intArr[2].getValue());
			}
		} else {
			if (call.getIntArrParam() != null) {
				int[] intArr = call.getIntArrParam();
				Buffer[] buf = call.getBufferArrParam();
				Gdx.gl20.glShaderBinary(intArr[0], intArr, intArr[1] + 4,
						intArr[2], buf[0], intArr[3]);
			} else {
				ENG_Integer[] intArr = call.getIntObjParam();
				Buffer[] buf = call.getBufferArrParam();
				int[] firstBuf = ENG_Utility.getIntAsPrimitiveArray(intArr, 4, intArr.length);
				Gdx.gl20.glShaderBinary(intArr[0].getValue(), firstBuf, intArr[1].getValue(),
						intArr[2].getValue(), buf[0], intArr[3].getValue());
				ENG_Utility.getIntPrimitiveArrayAsIntObjArray(firstBuf, 0, 
						intArr, 4, firstBuf.length);
			}
		}
		checkErrors("glShaderBinary");*/
    }

    /** @noinspection deprecation*/
    private static void handleGLShaderSource(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glShaderSource(intArr[0], call.getStringObjParam()[0]);
        } else {
            gl20.glShaderSource(call.getIntObjParam()[0].getValue(),
                    call.getStringObjParam()[0]);
        }

        checkErrors("glShaderSource");
    }

    /** @noinspection deprecation*/
    private static void handleGLStencilFunc(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glStencilFunc(intArr[0], intArr[1], intArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glStencilFunc(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue());
        }
        checkErrors("glStencilFunc");
    }

    /** @noinspection deprecation*/
    private static void handleGLStencilFuncSeparate(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glStencilFuncSeparate(intArr[0], intArr[1], intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glStencilFuncSeparate(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glStencilFuncSeparate");
    }

    /** @noinspection deprecation*/
    private static void handleGLStencilMask(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glStencilMask(intArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glStencilMask(intArr[0].getValue());
        }
        checkErrors("glStencilMask");
    }

    /** @noinspection deprecation*/
    private static void handleGLStencilMaskSeparate(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glStencilMaskSeparate(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glStencilMaskSeparate(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glStencilMaskSeparate");
    }

    /** @noinspection deprecation*/
    private static void handleGLStencilOp(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glStencilOp(intArr[0], intArr[1], intArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glStencilOp(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue());
        }
        checkErrors("glStencilOp");
    }

    /** @noinspection deprecation*/
    private static void handleGLStencilOpSeparate(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glStencilOpSeparate(intArr[0], intArr[1], intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glStencilOpSeparate(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glStencilOpSeparate");
    }

    /** @noinspection deprecation*/
    private static void handleGLTexImage2D(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glTexImage2D(intArr[0], intArr[1],
                    intArr[2], intArr[3],
                    intArr[4], intArr[5],
                    intArr[6], intArr[7], call.getBufferArrParam()[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glTexImage2D(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(),
                    intArr[4].getValue(), intArr[5].getValue(),
                    intArr[6].getValue(), intArr[7].getValue(), call.getBufferArrParam()[0]);
        }

        checkErrors("glTexImage2D");
    }

    /** @noinspection deprecation*/
    private static void handleGLTexParameterf(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glTexParameterf(intArr[0], intArr[1], floatArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glTexParameterf(intArr[0].getValue(), intArr[1].getValue(),
                    floatArr[0].getValue());
        }
        checkErrors("glTexParameterf");
    }

    /** @noinspection deprecation*/
    private static void handleGLTexParameterfv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glTexParameterfv(intArr[0], intArr[1],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glTexParameterfv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glTexParameterfv(intArr[0], intArr[1], buffer/*, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glTexParameterfv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glTexParameterfv");
    }

    /** @noinspection deprecation*/
    private static void handleGLTexParameteri(GLESCall call) {

        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glTexParameteri(intArr[0], intArr[1], intArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glTexParameteri(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue());
        }
        checkErrors("glTexParameteri");

    }

    /** @noinspection deprecation*/
    private static void handleGLTexParameteriv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glTexParameteriv(intArr[0], intArr[1],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glTexParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glTexParameteriv(intArr[0], intArr[1],
                        buffer/*intArr, intArr[2] + 3*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glTexParameteriv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glTexParameteriv");
    }

    /** @noinspection deprecation*/
    private static void handleGLTexSubImage2D(GLESCall call) {
        int err = gl20.glGetError();
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glTexSubImage2D(intArr[0], intArr[1],
                    intArr[2], intArr[3],
                    intArr[4], intArr[5],
                    intArr[6], intArr[7], call.getBufferArrParam()[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glTexSubImage2D(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(),
                    intArr[4].getValue(), intArr[5].getValue(),
                    intArr[6].getValue(), intArr[7].getValue(), call.getBufferArrParam()[0]);
        }
        checkErrors("glTexSubImage2D");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform1f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glUniform1f(intArr[0], floatArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glUniform1f(intArr[0].getValue(), floatArr[0].getValue());
        }
        checkErrors("glUniform1f");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform1fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform1fv(intArr[0], intArr[1],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform1fv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform1fv(intArr[0], intArr[1], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform1fv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform1fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform1i(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glUniform1i(intArr[0], intArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glUniform1i(intArr[0].getValue(), intArr[1].getValue());
        }
        checkErrors("glUniform1i");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform1iv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform1iv(intArr[0], intArr[1],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform1iv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
//				(IntBuffer) ENG_Utility.allocateDirect(4).asIntBuffer().put(intArr[3]).flip();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform1iv(intArr[0], intArr[1], buffer/*intArr, intArr[2] + 3*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform1iv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform1iv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform2f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glUniform2f(intArr[0], floatArr[0], floatArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glUniform2f(intArr[0].getValue(), floatArr[0].getValue(),
                    floatArr[1].getValue());
        }
        checkErrors("glUniform2f");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform2fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform2fv(intArr[0], intArr[1],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform2fv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform2fv(intArr[0], intArr[1], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform2fv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform2fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform2i(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glUniform2i(intArr[0], intArr[1], intArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glUniform2i(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue());
        }
        checkErrors("glUniform2i");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform2iv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform2iv(intArr[0], intArr[1],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform2iv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform2iv(intArr[0], intArr[1], buffer/*intArr, intArr[2] + 3*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform2iv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform2iv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform3f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glUniform3f(intArr[0], floatArr[0], floatArr[1], floatArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glUniform3f(intArr[0].getValue(), floatArr[0].getValue(),
                    floatArr[1].getValue(), floatArr[2].getValue());
        }
        checkErrors("glUniform3f");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform3fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform3fv(intArr[0], intArr[1],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform3fv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform3fv(intArr[0], intArr[1], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform3fv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform3fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform3i(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glUniform3i(intArr[0], intArr[1], intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glUniform3i(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glUniform3i");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform3iv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform3iv(intArr[0], intArr[1],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform3iv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform3iv(intArr[0], intArr[1], buffer/*intArr, intArr[2] + 3*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform3iv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform3iv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform4f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glUniform4f(intArr[0], floatArr[0], floatArr[1], floatArr[2],
                    floatArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glUniform4f(intArr[0].getValue(), floatArr[0].getValue(),
                    floatArr[1].getValue(), floatArr[2].getValue(), floatArr[3].getValue());
        }
        checkErrors("glUniform4f");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform4fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform4fv(intArr[0], intArr[1],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform4fv(intArr[0].getValue(), intArr[1].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform4fv(intArr[0], intArr[1], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniform4fv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform4fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform4i(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glUniform4i(intArr[0], intArr[1], intArr[2], intArr[3], intArr[4]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glUniform4i(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue(), intArr[4].getValue());
        }
        checkErrors("glUniform4i");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniform4iv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniform4iv(intArr[0], intArr[1],
                        (IntBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniform4iv(intArr[0].getValue(), intArr[1].getValue(),
                        (IntBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform4iv(intArr[0], intArr[1], buffer/*intArr, intArr[2] + 3*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                int[] buf = ENG_Utility.getIntAsPrimitiveArray(intArr, 3, intArr.length);
                IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(intArr, 3, intArr.length);
                gl20.glUniform4iv(intArr[0].getValue(), intArr[1].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniform4iv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniformMatrix2fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniformMatrix2fv(intArr[0], intArr[1], call.getBooleanArrParam()[0],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniformMatrix2fv(intArr[0].getValue(), intArr[1].getValue(),
                        call.getBooleanObjParam()[0].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniformMatrix2fv(intArr[0], intArr[1], call.getBooleanArrParam()[0],
                        buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] buf = call.getFloatObjParam();
                float[] floatArr = ENG_Utility.getFloatAsPrimitiveArray(buf);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniformMatrix2fv(intArr[0].getValue(), intArr[1].getValue(),
                        call.getBooleanArrParam()[0], buffer/*floatArr, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniformMatrix2fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniformMatrix3fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniformMatrix3fv(intArr[0], intArr[1], call.getBooleanArrParam()[0],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniformMatrix3fv(intArr[0].getValue(), intArr[1].getValue(),
                        call.getBooleanObjParam()[0].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniformMatrix3fv(intArr[0], intArr[1], call.getBooleanArrParam()[0],
                        buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] buf = call.getFloatObjParam();
                float[] floatArr = ENG_Utility.getFloatAsPrimitiveArray(buf);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniformMatrix3fv(intArr[0].getValue(), intArr[1].getValue(),
                        call.getBooleanArrParam()[0], buffer/*floatArr, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniformMatrix3fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUniformMatrix4fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glUniformMatrix4fv(intArr[0], intArr[1], call.getBooleanArrParam()[0],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glUniformMatrix4fv(intArr[0].getValue(), intArr[1].getValue(),
                        call.getBooleanObjParam()[0].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniformMatrix4fv(intArr[0], intArr[1], call.getBooleanArrParam()[0],
                        buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] buf = call.getFloatObjParam();
                float[] floatArr = ENG_Utility.getFloatAsPrimitiveArray(buf);
			/*	for (int i = 0; i< floatArr.length; ++i) {
					System.out.println("float arr " + i + " " + floatArr[i]);
				}*/
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glUniformMatrix4fv(intArr[0].getValue(), intArr[1].getValue(),
                        call.getBooleanObjParam()[0].getValue(),
                        buffer/*floatArr, intArr[2].getValue()*/);
            }
        }
        checkErrors("glUniformMatrix4fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLUseProgram(GLESCall call) {
        if (call.getIntArrParam() != null) {
            gl20.glUseProgram(call.getIntArrParam()[0]);
        } else {
            gl20.glUseProgram(call.getIntObjParam()[0].getValue());
        }
        checkErrors("glUseProgram");
    }

    /** @noinspection deprecation*/
    private static void handleGLValidateProgram(GLESCall call) {
        if (call.getIntArrParam() != null) {
            gl20.glValidateProgram(call.getIntArrParam()[0]);
        } else {
            gl20.glValidateProgram(call.getIntObjParam()[0].getValue());
        }
        checkErrors("glValidateProgram");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib1f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glVertexAttrib1f(intArr[0], floatArr[0]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glVertexAttrib1f(intArr[0].getValue(), floatArr[0].getValue());
        }
        checkErrors("glVertexAttrib1f");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib1fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glVertexAttrib1fv(intArr[0],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glVertexAttrib1fv(intArr[0].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib1fv(intArr[0], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib1fv(intArr[0].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glVertexAttrib1fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib2f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glVertexAttrib2f(intArr[0], floatArr[0], floatArr[1]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glVertexAttrib2f(intArr[0].getValue(), floatArr[0].getValue(),
                    floatArr[1].getValue());
        }
        checkErrors("glVertexAttrib2f");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib2fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glVertexAttrib2fv(intArr[0],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glVertexAttrib2fv(intArr[0].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib2fv(intArr[0], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib2fv(intArr[0].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glVertexAttrib2fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib3f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glVertexAttrib3f(intArr[0], floatArr[0], floatArr[1], floatArr[2]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glVertexAttrib3f(intArr[0].getValue(), floatArr[0].getValue(),
                    floatArr[1].getValue(), floatArr[2].getValue());
        }
        checkErrors("glVertexAttrib3f");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib3fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glVertexAttrib3fv(intArr[0],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glVertexAttrib3fv(intArr[0].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib3fv(intArr[0], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib3fv(intArr[0].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glVertexAttrib3fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib4f(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            float[] floatArr = call.getFloatArrParam();
            gl20.glVertexAttrib4f(intArr[0], floatArr[0], floatArr[1], floatArr[2],
                    floatArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            ENG_Float[] floatArr = call.getFloatObjParam();
            gl20.glVertexAttrib4f(intArr[0].getValue(), floatArr[0].getValue(),
                    floatArr[1].getValue(), floatArr[2].getValue(), floatArr[3].getValue());
        }
        checkErrors("glVertexAttrib4f");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttrib4fv(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                gl20.glVertexAttrib4fv(intArr[0],
                        (FloatBuffer) call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                gl20.glVertexAttrib4fv(intArr[0].getValue(),
                        (FloatBuffer) call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                float[] floatArr = call.getFloatArrParam();
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib4fv(intArr[0], buffer/*floatArr, intArr[2]*/);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Float[] floatArr = call.getFloatObjParam();
                float[] buf = ENG_Utility.getFloatAsPrimitiveArray(floatArr);
                FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(floatArr);
                gl20.glVertexAttrib4fv(intArr[0].getValue(),
                        buffer/*, intArr[2].getValue()*/);
            }
        }
        checkErrors("glVertexAttrib4fv");
    }

    /** @noinspection deprecation*/
    private static void handleGLVertexAttribPointer(GLESCall call) {
        if (call.getBufferArrParam() != null) {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                boolean[] boolArr = call.getBooleanArrParam();
                gl20.glVertexAttribPointer(intArr[0], intArr[1], intArr[2],
                        boolArr[0], intArr[3], call.getBufferArrParam()[0]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Boolean[] boolArr = call.getBooleanObjParam();
                gl20.glVertexAttribPointer(intArr[0].getValue(), intArr[1].getValue(),
                        intArr[2].getValue(), boolArr[0].getValue(),
                        intArr[3].getValue(), call.getBufferArrParam()[0]);
            }
        } else {
            if (call.getIntArrParam() != null) {
                int[] intArr = call.getIntArrParam();
                boolean[] boolArr = call.getBooleanArrParam();
                gl20.glVertexAttribPointer(intArr[0], intArr[1], intArr[2],
                        boolArr[0], intArr[3], intArr[4]);
            } else {
                ENG_Integer[] intArr = call.getIntObjParam();
                ENG_Boolean[] boolArr = call.getBooleanObjParam();
                gl20.glVertexAttribPointer(intArr[0].getValue(), intArr[1].getValue(),
                        intArr[2].getValue(), boolArr[0].getValue(),
                        intArr[3].getValue(), intArr[4].getValue());
            }
        }
        checkErrors("glVertexAttribPointer");
    }

    /** @noinspection deprecation*/
    private static void handleGLViewport(GLESCall call) {
        if (call.getIntArrParam() != null) {
            int[] intArr = call.getIntArrParam();
            gl20.glViewport(intArr[0], intArr[1], intArr[2], intArr[3]);
        } else {
            ENG_Integer[] intArr = call.getIntObjParam();
            gl20.glViewport(intArr[0].getValue(), intArr[1].getValue(),
                    intArr[2].getValue(), intArr[3].getValue());
        }
        checkErrors("glViewport");
    }

    /** @noinspection deprecation */
    private static void handleGLCallQueue() {
		/*
		 * Sometimes it happens that if in game and a new activity pops up
		 * and then closes down, even if the onDrawFrame() has been completed
		 * we may have a new renderRequest() waiting to complete after activity close
		 * What that means is that it may start processing the glCallQueue
		 * before we get a chance to empty it. Why empty it? Because after
		 * activity close we lose all resources along with the gl context meaning
		 * we have to reload. The problem with reloading is that to reload we have
		 * to destroy all the resources in the renderer and that also calls
		 * glDelete*() methods with expired gl handles that would throw exception.
		 * So we must make sure we empty the glCallQueue before allowing
		 * an onDrawFrame() from a renderRequest() that remained in there
		 * because of an ahead frame rendering (the renderer is slower than the game).
		 */
        MainApp.getMainThread().getResumeGlThreadLock().lock();
        try {
            CountDownLatch resumeGlThread = MainApp.getMainThread().getResumeGlThread();
            if (resumeGlThread != null) {
                try {
                    resumeGlThread.await();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                MainApp.getMainThread().resetResumeGlThread();
            }
        } finally {
            MainApp.getMainThread().getResumeGlThreadLock().unlock();
        }
        Queue<GLESCall> glCallQueue = MTGLES20.getGLCallQueue();
        GLESCall call;
        boolean next = true;
        // next must be first check or we lose the first element in the queue
        // when allowing background rendering!!!
        MTGLES20.getGlQueueLock().lock();
        try {
            while ((next) && ((call = glCallQueue.poll()) != null)) {
                switch (call.getCall()) {
                    case glActiveTexture:
                        handleGLActiveTexture(call);
                        break;
                    case glAttachShader:
                        handleGLAttachShader(call);
                        break;
                    case glBindAttribLocation:
                        handleGLBindAttribLocation(call);
                        break;
                    case glBindBuffer:
                        handleGLBindBuffer(call);
                        break;
                    case glBindFramebuffer:
                        handleGLBindFramebuffer(call);
                        break;
                    case glBindRenderbuffer:
                        handleGLBindRenderbuffer(call);
                        break;
                    case glBindTexture:
                        handleGLBindTexture(call);
                        break;
                    case glBlendColor:
                        handleGLBlendColor(call);
                        break;
                    case glBlendEquation:
                        handleGLBlendEquation(call);
                        break;
                    case glBlendEquationSeparate:
                        handleGLBlendEquationSeparate(call);
                        break;
                    case glBlendFunc:
                        handleGLBlendFunc(call);
                        break;
                    case glBlendFuncSeparate:
                        handleGLBlendFuncSeparate(call);
                        break;
                    case glBufferData:
                        handleGLBufferData(call);
                        break;
                    case glBufferSubData:
                        handleGLBufferSubData(call);
                        break;
                    case glClear:
                        handleGLClear(call);
                        break;
                    case glClearColor:
                        handleGLClearColor(call);
                        break;
                    case glClearDepthf:
                        handleGLClearDepthf(call);
                        break;
                    case glClearStencil:
                        handleGLClearStencil(call);
                        break;
                    case glColorMask:
                        handleGLColorMask(call);
                        break;
                    case glCompileShader:
                        handleGLCompileShader(call);
                        break;
                    case glCompressedTexImage2D:
                        handleGLCompressedTexImage2D(call);
                        break;
                    case glCompressedTexSubImage2D:
                        handleGLCompressedTexSubImage2D(call);
                        break;
                    case glCopyTexImage2D:
                        handleGLCopyTexImage2D(call);
                        break;
                    case glCopyTexSubImage2D:
                        handleGLCopyTexSubImage2D(call);
                        break;
                    case glCullFace:
                        handleGLCullFace(call);
                        break;
                    case glDeleteBuffersArr:
                        handleGLDeleteBuffers(call);
                        break;
                    case glDeleteBuffersBuf:
                        handleGLDeleteBuffers(call);
                        break;
                    case glDeleteFramebuffersArr:
                        handleGLDeleteFramebuffers(call);
                        break;
                    case glDeleteFramebuffersBuf:
                        handleGLDeleteFramebuffers(call);
                        break;
                    case glDeleteProgram:
                        handleGLDeleteProgram(call);
                        break;
                    case glDeleteRenderbuffersBuf:
                        handleGLDeleteRenderbuffers(call);
                        break;
                    case glDeleteRenderbuffersArr:
                        handleGLDeleteRenderbuffers(call);
                        break;
                    case glDeleteShader:
                        handleGLDeleteShader(call);
                        break;
                    case glDeleteTexturesArr:
                        handleGLDeleteTextures(call);
                        break;
                    case glDeleteTexturesBuf:
                        handleGLDeleteTextures(call);
                        break;
                    case glDepthFunc:
                        handleGLDepthFunc(call);
                        break;
                    case glDepthMask:
                        handleGLDepthMask(call);
                        break;
                    case glDepthRangef:
                        handleGLDepthRangef(call);
                        break;
                    case glDetachShader:
                        handleGLDetachShader(call);
                        break;
                    case glDisable:
                        handleGLDisable(call);
                        break;
                    case glDisableVertexAttribArray:
                        handleGLDisableVertexAttribArray(call);
                        break;
                    case glDrawArrays:
                        handleGLDrawArrays(call);
                        break;
                    case glDrawElementsBuf:
                        handleGLDrawElements(call);
                        break;
                    case glDrawElementsOff:
                        handleGLDrawElements(call);
                        break;
                    case glEnable:
                        handleGLEnable(call);
                        break;
                    case glEnableVertexAttribArray:
                        handleGLEnableVertexAttribArray(call);
                        break;
                    case glFinish:
                        handleGLFinish();
                        break;
                    case glFlush:
                        handleGLFlush();
                        break;
                    case glFramebufferRenderbuffer:
                        handleGLFramebufferRenderbuffer(call);
                        break;
                    case glFramebufferTexture2D:
                        handleGLFramebufferTexture2D(call);
                        break;
                    case glFrontFace:
                        handleGLFrontFace(call);
                        break;
                    case glGenBuffersBuf:
                        handleGLGenBuffers(call);
                        break;
                    case glGenBuffersArr:
                        handleGLGenBuffers(call);
                        break;
                    case glGenFramebuffersBuf:
                        handleGLGenFramebuffers(call);
                        break;
                    case glGenFramebuffersArr:
                        handleGLGenFramebuffers(call);
                        break;
                    case glGenRenderbuffersBuf:
                        handleGLGenRenderbuffers(call);
                        break;
                    case glGenRenderbuffersArr:
                        handleGLGenRenderbuffers(call);
                        break;
                    case glGenTexturesBuf:
                        handleGLGenTextures(call);
                        break;
                    case glGenTexturesArr:
                        handleGLGenTextures(call);
                        break;
                    case glGenerateMipmap:
                        handleGLGenerateMipmap(call);
                        break;
                    case glGetActiveAttribBuf:
                        throw new UnsupportedOperationException();
//					handleGLGetActiveAttrib(call);
//					break;
                    case glGetActiveAttribArr:
                        throw new UnsupportedOperationException();
//					handleGLGetActiveAttrib(call);
//					break;
                    case glGetActiveUniformBuf:
                        throw new UnsupportedOperationException();
//					handleGLGetActiveUniform(call);
//					break;
                    case glGetActiveUniformArr:
                        throw new UnsupportedOperationException();
//					handleGLGetActiveUniform(call);
//					break;
                    case glGetAttachedShadersArr:
                        throw new UnsupportedOperationException();
//					handleGLGetAttachedShaders(call);
//					break;
                    case glGetAttachedShadersBuf:
                        throw new UnsupportedOperationException();
//					handleGLGetAttachedShaders(call);
//					break;
                    case glGetBooleanvArr:
                        handleGLGetBooleanv(call);
                        break;
                    case glGetBooleanvBuf:
                        handleGLGetBooleanv(call);
                        break;
                    case glGetBufferParameterivBuf:
                        handleGLGetBufferParameteriv(call);
                        break;
                    case glGetBufferParameterivArr:
                        handleGLGetBufferParameteriv(call);
                        break;
                    case glGetFloatvBuf:
                        handleGLGetFloatv(call);
                        break;
                    case glGetFloatvArr:
                        handleGLGetFloatv(call);
                        break;
                    case glGetFramebufferAttachmentParameterivBuf:
                        handleGLGetFramebufferAttachmentParameteriv(call);
                        break;
                    case glGetFramebufferAttachmentParameterivArr:
                        handleGLGetFramebufferAttachmentParameteriv(call);
                        break;
                    case glGetIntegervBuf:
                        handleGLGetIntegerv(call);
                        break;
                    case glGetIntegervArr:
                        handleGLGetIntegerv(call);
                        break;
                    case glGetProgramivBuf:
                        handleGLGetProgramiv(call);
                        break;
                    case glGetProgramivArr:
                        handleGLGetProgramiv(call);
                        break;
                    case glGetRenderbufferParameterivArr:
                        handleGLGetRenderbufferParameteriv(call);
                        break;
                    case glGetRenderbufferParameterivBuf:
                        handleGLGetRenderbufferParameteriv(call);
                        break;
                    case glGetShaderPrecisionFormatBuf:
                        handleGLGetShaderPrecisionFormat(call);
                        break;
                    case glGetShaderPrecisionFormatArr:
                        handleGLGetShaderPrecisionFormat(call);
                        break;
                    case glGetShaderSourceArr:
                        throw new UnsupportedOperationException();
//					handleGLGetShaderSource(call);
//					break;
                    case glGetShaderSourceBuf:
                        throw new UnsupportedOperationException();
//					handleGLGetShaderSource(call);
//					break;
                    case glGetShaderivBuf:
                        handleGLGetShaderiv(call);
                        break;
                    case glGetShaderivArr:
                        handleGLGetShaderiv(call);
                        break;
                    case glGetTexParameterfvArr:
                        handleGLGetTexParameterfv(call);
                        break;
                    case glGetTexParameterfvBuf:
                        handleGLGetTexParameterfv(call);
                        break;
                    case glGetTexParameterivArr:
                        handleGLGetTexParameteriv(call);
                        break;
                    case glGetTexParameterivBuf:
                        handleGLGetTexParameteriv(call);
                        break;
                    case glGetUniformfvFBuf:
                        handleGLGetUniformfv(call);
                        break;
                    case glGetUniformfvFArr:
                        handleGLGetUniformfv(call);
                        break;
                    case glGetUniformivIBuf:
                        handleGLGetUniformiv(call);
                        break;
                    case glGetUniformivIArr:
                        handleGLGetUniformiv(call);
                        break;
                    case glGetVertexAttribfvFArr:
                        handleGLGetVertexAttribfv(call);
                        break;
                    case glGetVertexAttribfvFBuf:
                        handleGLGetVertexAttribfv(call);
                        break;
                    case glGetVertexAttribivIArr:
                        handleGLGetVertexAttribiv(call);
                        break;
                    case glGetVertexAttribivIBuf:
                        handleGLGetVertexAttribiv(call);
                        break;
                    case glHint:
                        handleGLHint(call);
                        break;
                    case glLineWidth:
                        handleGLLineWidth(call);
                        break;
                    case glLinkProgram:
                        handleGLLinkProgram(call);
                        break;
                    case glPixelStorei:
                        handleGLPixelStorei(call);
                        break;
                    case glPolygonOffset:
                        handleGLPolygonOffset(call);
                        break;
                    case glReadPixels:
                        handleGLReadPixels(call);
                        break;
                    case glReleaseShaderCompiler:
                        handleGLReleaseShaderCompiler();
                        break;
                    case glRenderbufferStorage:
                        handleGLRenderbufferStorage(call);
                        break;
                    case glSampleCoverage:
                        handleGLSampleCoverage(call);
                        break;
                    case glScissor:
                        handleGLScissor(call);
                        break;
                    case glShaderBinaryBuf:
                        handleGLShaderBinary(call);
                        break;
                    case glShaderBinaryArr:
                        handleGLShaderBinary(call);
                        break;
                    case glShaderSource:
                        handleGLShaderSource(call);
                        break;
                    case glStencilFunc:
                        handleGLStencilFunc(call);
                        break;
                    case glStencilFuncSeparate:
                        handleGLStencilFuncSeparate(call);
                        break;
                    case glStencilMask:
                        handleGLStencilMask(call);
                        break;
                    case glStencilMaskSeparate:
                        handleGLStencilMaskSeparate(call);
                        break;
                    case glStencilOp:
                        handleGLStencilOp(call);
                        break;
                    case glStencilOpSeparate:
                        handleGLStencilOpSeparate(call);
                        break;
                    case glTexImage2D:
                        handleGLTexImage2D(call);
                        break;
                    case glTexParameterf:
                        handleGLTexParameterf(call);
                        break;
                    case glTexParameterfvArr:
                        handleGLTexParameterfv(call);
                        break;
                    case glTexParameterfvBuf:
                        handleGLTexParameterfv(call);
                        break;
                    case glTexParameteri:
                        handleGLTexParameteri(call);
                        break;
                    case glTexParameterivBuf:
                        handleGLTexParameteriv(call);
                        break;
                    case glTexParameterivArr:
                        handleGLTexParameteriv(call);
                        break;
                    case glTexSubImage2D:
                        handleGLTexSubImage2D(call);
                        break;
                    case glUniform1f:
                        handleGLUniform1f(call);
                    case glUniform1fvArr:
                        handleGLUniform1fv(call);
                        break;
                    case glUniform1fvBuf:
                        handleGLUniform1fv(call);
                        break;
                    case glUniform1i:
                        handleGLUniform1i(call);
                    case glUniform1ivArr:
                        handleGLUniform1iv(call);
                        break;
                    case glUniform1ivBuf:
                        handleGLUniform2iv(call);
                        break;
                    case glUniform2f:
                        handleGLUniform2f(call);
                    case glUniform2fvArr:
                        handleGLUniform2fv(call);
                        break;
                    case glUniform2fvBuf:
                        handleGLUniform2fv(call);
                        break;
                    case glUniform2i:
                        handleGLUniform2i(call);
                    case glUniform2ivArr:
                        handleGLUniform2iv(call);
                        break;
                    case glUniform2ivBuf:
                        handleGLUniform2iv(call);
                        break;
                    case glUniform3f:
                        handleGLUniform3f(call);
                    case glUniform3fvArr:
                        handleGLUniform3fv(call);
                        break;
                    case glUniform3fvBuf:
                        handleGLUniform3fv(call);
                        break;
                    case glUniform3i:
                        handleGLUniform3i(call);
                    case glUniform3ivArr:
                        handleGLUniform3iv(call);
                        break;
                    case glUniform3ivBuf:
                        handleGLUniform3iv(call);
                        break;
                    case glUniform4f:
                        handleGLUniform4f(call);
                    case glUniform4fvArr:
                        handleGLUniform4fv(call);
                        break;
                    case glUniform4fvBuf:
                        handleGLUniform4fv(call);
                        break;
                    case glUniform4i:
                        handleGLUniform4i(call);
                    case glUniform4ivArr:
                        handleGLUniform4iv(call);
                        break;
                    case glUniform4ivBuf:
                        handleGLUniform4iv(call);
                        break;
                    case glUniformMatrix2fvBuf:
                        handleGLUniformMatrix2fv(call);
                        break;
                    case glUniformMatrix2fvArr:
                        handleGLUniformMatrix2fv(call);
                        break;
                    case glUniformMatrix3fvBuf:
                        handleGLUniformMatrix3fv(call);
                        break;
                    case glUniformMatrix3fvArr:
                        handleGLUniformMatrix3fv(call);
                        break;
                    case glUniformMatrix4fvBuf:
                        handleGLUniformMatrix4fv(call);
                        break;
                    case glUniformMatrix4fvArr:
                        handleGLUniformMatrix4fv(call);
                        break;
                    case glUseProgram:
                        handleGLUseProgram(call);
                        break;
                    case glValidateProgram:
                        handleGLValidateProgram(call);
                        break;
                    case glVertexAttrib1f:
                        handleGLVertexAttrib1f(call);
                        break;
                    case glVertexAttrib1fvBuf:
                        handleGLVertexAttrib1fv(call);
                        break;
                    case glVertexAttrib1fvArr:
                        handleGLVertexAttrib1fv(call);
                        break;
                    case glVertexAttrib2f:
                        handleGLVertexAttrib2f(call);
                        break;
                    case glVertexAttrib2fvBuf:
                        handleGLVertexAttrib2fv(call);
                        break;
                    case glVertexAttrib2fvArr:
                        handleGLVertexAttrib2fv(call);
                        break;
                    case glVertexAttrib3f:
                        handleGLVertexAttrib3f(call);
                        break;
                    case glVertexAttrib3fvBuf:
                        handleGLVertexAttrib3fv(call);
                        break;
                    case glVertexAttrib3fvArr:
                        handleGLVertexAttrib3fv(call);
                        break;
                    case glVertexAttrib4f:
                        handleGLVertexAttrib4f(call);
                        break;
                    case glVertexAttrib4fvBuf:
                        handleGLVertexAttrib4fv(call);
                        break;
                    case glVertexAttrib4fvArr:
                        handleGLVertexAttrib4fv(call);
                        break;
                    case glVertexAttribPointerBuf:
                        handleGLVertexAttribPointer(call);
                        break;
                    case glVertexAttribPointer:
                        handleGLVertexAttribPointer(call);
                        break;
                    case glViewport:
                        handleGLViewport(call);
                        break;
                    case glNone:
                        next = false;
                        break;
                    default:
                        //Should never get here
                        throw new ENG_InvalidFieldStateException(
                                "No known command detected!");
                }
            }
        } finally {
            MTGLES20.getGlQueueLock().unlock();
        }
	/*	synchronized (glMainThread) {
			try {
				glMainThread.wait(2000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}*/
        ReentrantLock renderingFinishedLock =
                GLRenderSurface.getSingleton().getRenderingFinishedLock();
        renderingFinishedLock.lock();
        try {
            if (MTGLES20.hasCallsAhead()) {
                renderingContinued.set(true);
            }
            GLRenderSurface.getSingleton().setRenderingFinished();
        } finally {
            renderingFinishedLock.unlock();
        }

    }


    private long lastTime = currentTimeMillis();

    //	@Override
    public void onDrawFrame(/*GL10 gl*/) {

        long currentTime = currentTimeMillis() - lastTime;
//		System.out.println("onDrawFrame() time since last call: " + currentTime);

        long time = currentTimeMillis();
        if (TEST) {
            System.out.println("onDrawFrame called");
        }
//		GLRenderSurface.getSingleton().runQueueEvents();
//		if (MTGLES20.getRenderingAllowed()) {
//			// Set to false before handling callQueue to avoid race condition
//			// in waitForRenderingToFinish() where getting from the await to
//			// setRenderingAllowed(true) might take more then coming back from
//			// GLRenderSurface.getSingleton().setRenderingFinished(); to
//			// MTGdx.gl20.setRenderingAllowed(false); in the glThread.
//			MTGLES20.setRenderingAllowed(false);
//			handleGLCallQueue();	
//		/*	synchronized (this) {
//				try {
//					wait(1000);
//				} catch (InterruptedException e) {
//
//					e.printStackTrace();
//				}
//			}*/
//			
//			if (TEST) {
//				System.out.println("onDrawFrame rendering allowed");
//			}
//		} else {
//			
//		}
        // No longer a real thread
        //noinspection CallToThreadRun
        MainApp.getMainThread().run();
        long elapsedTime = currentTimeMillis() - time;
//		System.out.println("onDrawFrame() elapsed time rendering: " + elapsedTime);
        lastTime = currentTimeMillis();
    }

    //	@Override
    public void onSurfaceChanged(int width, int height) {


    }

    //	@Override
    public void onSurfaceCreated(/*GL10 gl, EGLConfig config*/) {

        ENG_MainThread mainThread = MainApp.getMainThread();
        // Main thread should always be set when getting here
        if (MainApp.getGame().areResourcesCreated()) {
            // We need to reload all gl resources, because the android guys are smart...
//            MainApp.getGame().setGLThreadReadyForResourceReload();
            MainApp.getGame().reloadResources();
        } else {
            MainApp.getGame().resetGameStarted();
        }
//		MainApp.getGame().reloadResources();
//        mainThread.allowGameStart();
        System.out.println("GLSurface created");

    }

    public static void resetRenderingContinued() {
        renderingContinued.set(false);
    }

    public static boolean isRenderingContinued() {
        return renderingContinued.get();
    }

    /** @noinspection deprecation*/
    public static GLMainThread getSingleton() {
        if (glMainThread == null && MainActivity.isDebugmode()) {
            throw new NullPointerException("glMainThread is null!");
        }
        return glMainThread;
    }

}
