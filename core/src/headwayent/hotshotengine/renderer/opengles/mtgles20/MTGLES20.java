/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/5/22, 9:41 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.opengles.mtgles20;

import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.basictypes.ENG_Integer;
import headwayent.hotshotengine.renderer.opengles.GLRenderSurface;
import headwayent.hotshotengine.renderer.opengles.mtgles20.GLESCall.GLCallList;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.badlogic.gdx.Gdx;

@Deprecated
public class MTGLES20 {

    //private static final MTGdx.gl20 impl = new MTGdx.gl20();

    private static final AtomicBoolean renderingAllowed = new AtomicBoolean();
    /** @noinspection deprecation*/
    private static final ConcurrentLinkedQueue<GLESCall> glCallQueue =
            new ConcurrentLinkedQueue<>();
    /** @noinspection deprecation*/
    private static GLRenderSurface glRenderSurface;
    private static int callsAhead;
    private static final ReentrantLock callsLock = new ReentrantLock();
    private static final ReentrantLock glQueueLock = new ReentrantLock();
    private static final boolean DEBUG = false;
    private static final boolean ERRORS_FATAL = true;

    private static void eventQueued(String glCall) {
        if (DEBUG) {
            System.out.println("queuedEvent " + glCall);
        }
    }

    private static void eventQueuedRun(String glCall) {
        if (DEBUG) {
            System.out.println("running queuedEvent " + glCall);
        }
    }

    public static ReentrantLock getGlQueueLock() {
        return glQueueLock;
    }

    public static void clearGlQueue() {
    /*	callsLock.lock();
		try {
			if (callsAhead > 1) {
				callsAhead = 0;
			}*/
        glQueueLock.lock();
        try {
            glCallQueue.clear();
        } finally {
            glQueueLock.unlock();
        }
	/*	} finally {
			callsLock.unlock();
		}*/
    }

    public static boolean hasCallsAhead() {
        callsLock.lock();
        try {
            if (MainApp.DEV) {
                //	System.out.println("CALLS AHEAD: " + callsAhead);
            }
            if (callsAhead > 0) {
                --callsAhead;
            }
            return callsAhead != 0;
        } finally {
            callsLock.unlock();
        }
    }

    public static int getNumCallsAhead() {
        callsLock.lock();
        try {
            return callsAhead;
        } finally {
            callsLock.unlock();
        }
    }

    private MTGLES20() {

    }

    /** @noinspection deprecation */
    public static void glNone() {
        GLESCall call = new GLESCall();
        call.setCall(GLCallList.glNone);
        glCallQueue.add(call);
        callsLock.lock();
        try {
            ++callsAhead;
            if (MainApp.DEV) {
                //	System.out.println("calls ahead value: " + callsAhead);
            }
        } finally {
            callsLock.unlock();
        }
    }

    public static void glActiveTexture(int texture) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glActiveTexture);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, texture);
//		glCallQueue.add(call);
        Gdx.gl20.glActiveTexture(texture);
    }

    public static void glActiveTexture(ENG_Integer texture) {
        glActiveTexture(texture, true);
    }

    public static void glActiveTexture(ENG_Integer texture, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glActiveTexture);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, texture, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glActiveTexture(texture.getValue());
    }

    public static void glAttachShader(int program, int shader) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glAttachShader);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, shader);
//		glCallQueue.add(call);
        Gdx.gl20.glAttachShader(program, shader);
    }

    public static void glAttachShaderImmediate(final int program, final int shader) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glAttachShaderImmediate");
//				Gdx.gl20.glAttachShader(program, shader);
//				int err = Gdx.gl20.glGetError();
////				String msg = GLU.gluErrorString(err);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glAttachShaderImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glAttachShader(program, shader);
    }

    public static void glAttachShader(ENG_Integer program, ENG_Integer shader) {
        glAttachShader(program, shader, true);
    }

    public static void glAttachShader(ENG_Integer program, ENG_Integer shader,
                                      boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glAttachShader);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, shader, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glAttachShader(program.getValue(), shader.getValue());
    }

    public static void glAttachShaderImmediate(ENG_Integer program,
                                               ENG_Integer shader) {
//		final int prg = program.getValue();
//		final int s = shader.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glAttachShaderImmediate");
//				Gdx.gl20.glAttachShader(prg, s);
//			//	int err = Gdx.gl20.glGetError();
//			//	String msg = GLU.gluErrorString(err);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glAttachShaderImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glAttachShader(program.getValue(), shader.getValue());
    }

    public static void glBindAttribLocation(int program, int index, String name) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindAttribLocation);
//		call.setIntArrayCount(2);
//		call.setStringObjCount(1);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, index);
//		call.setStringObjParam(0, name);
//		glCallQueue.add(call);
        Gdx.gl20.glBindAttribLocation(program, index, name);
    }

    public static void glBindAttribLocationImmediate(final int program,
                                                     final int index, final String name) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glBindAttribLocationImmediate");
//				Gdx.gl20.glBindAttribLocation(program, index, name);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glBindAttribLocationImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glBindAttribLocation(program, index, name);
    }

    public static void glBindAttribLocation(ENG_Integer program, ENG_Integer index,
                                            String name) {
        glBindAttribLocation(program, index, name, true);
    }

    public static void glBindAttribLocation(ENG_Integer program, ENG_Integer index,
                                            String name, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindAttribLocation);
//		call.setIntObjCount(2);
//		call.setStringObjCount(1);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, index, copy);
//		call.setStringObjParam(0, name);
//		glCallQueue.add(call);
        Gdx.gl20.glBindAttribLocation(program.getValue(), index.getValue(), name);
    }

    public static void glBindAttribLocationImmediate(ENG_Integer program, ENG_Integer index,
                                                     final String name) {
//		final int programInt = program.getValue();
//		final int indexInt = index.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glBindAttribLocationImmediate");
//				Gdx.gl20.glBindAttribLocation(programInt, indexInt, name);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glBindAttribLocationImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glBindAttribLocation(program.getValue(), index.getValue(), name);
    }

    public static void glBindBuffer(int target, int buffer) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindBuffer);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, buffer);
//		glCallQueue.add(call);
        Gdx.gl20.glBindBuffer(target, buffer);
    }

    public static void glBindBuffer(ENG_Integer target, ENG_Integer buffer) {
        glBindBuffer(target, buffer, true);
    }

    public static void glBindBuffer(ENG_Integer target, ENG_Integer buffer,
                                    boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindBuffer);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, buffer, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBindBuffer(target.getValue(), buffer.getValue());
    }

    public static void glBindFramebuffer(ENG_Integer target, ENG_Integer framebuffer) {
        glBindFramebuffer(target, framebuffer, true);
    }

    public static void glBindFramebuffer(ENG_Integer target, ENG_Integer framebuffer,
                                         boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindFramebuffer);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, framebuffer, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBindFramebuffer(target.getValue(), framebuffer.getValue());
    }

    public static void glBindFramebuffer(int target, int framebuffer) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindFramebuffer);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, framebuffer);
//		glCallQueue.add(call);
        Gdx.gl20.glBindFramebuffer(target, framebuffer);
    }

    public static void glBindRenderbuffer(ENG_Integer target,
                                          ENG_Integer renderbuffer) {
        glBindRenderbuffer(target, renderbuffer, true);
    }

    public static void glBindRenderbuffer(ENG_Integer target, ENG_Integer renderbuffer,
                                          boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindRenderbuffer);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, renderbuffer, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBindRenderbuffer(target.getValue(), renderbuffer.getValue());
    }

    public static void glBindRenderbuffer(int target, int renderbuffer) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindRenderbuffer);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, renderbuffer);
//		glCallQueue.add(call);
        Gdx.gl20.glBindRenderbuffer(target, renderbuffer);
    }

    public static void glBindTexture(ENG_Integer target, ENG_Integer texture) {
        glBindTexture(target, texture, true);
    }

    public static void glBindTexture(ENG_Integer target, ENG_Integer texture,
                                     boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindTexture);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, texture, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBindTexture(target.getValue(), texture.getValue());
    }

    public static void glBindTextureImmediate(ENG_Integer target, ENG_Integer texture) {
//		final int targetValue = target.getValue();
//		final int textureValue = texture.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glBindTextureImmediate");
//				Gdx.gl20.glBindTexture(targetValue, textureValue);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glBindTextureImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glBindTexture(target.getValue(), texture.getValue());
    }

    public static void glBindTexture(int target, int texture) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBindTexture);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, texture);
//		glCallQueue.add(call);
        Gdx.gl20.glBindTexture(target, texture);
    }

    public static void glBindTextureImmediate(final int target, final int texture) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glBindTextureImmediate");
//				Gdx.gl20.glBindTexture(target, texture);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glBindTextureImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glBindTexture(target, texture);
    }

    public static void glBlendColor(ENG_Float red, ENG_Float green, ENG_Float blue,
                                    ENG_Float alpha) {
        glBlendColor(red, green, blue, alpha, true);
    }

    public static void glBlendColor(ENG_Float red, ENG_Float green, ENG_Float blue,
                                    ENG_Float alpha, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendColor);
//		call.setFloatObjCount(4);
//		call.setFloatObjParam(0, red, copy);
//		call.setFloatObjParam(1, green, copy);
//		call.setFloatObjParam(2, blue, copy);
//		call.setFloatObjParam(3, alpha, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendColor(red.getValue(), green.getValue(), blue.getValue(),
                alpha.getValue());
    }

    public static void glBlendColor(float red, float green, float blue, float alpha) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendColor);
//		call.setFloatArrayCount(4);
//		call.setFloatArrayParam(0, red);
//		call.setFloatArrayParam(1, green);
//		call.setFloatArrayParam(2, blue);
//		call.setFloatArrayParam(3, alpha);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendColor(red, green, blue, alpha);
    }

    public static void glBlendEquation(ENG_Integer mode) {
        glBlendEquation(mode, true);
    }

    public static void glBlendEquation(ENG_Integer mode, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendEquation);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, mode, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendEquation(mode.getValue());
    }

    public static void glBlendEquation(int mode) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendEquation);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, mode);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendEquation(mode);
    }

    public static void glBlendEquationSeparate(ENG_Integer modeRGB,
                                               ENG_Integer modeAlpha) {
        glBlendEquationSeparate(modeRGB, modeAlpha, true);
    }

    public static void glBlendEquationSeparate(ENG_Integer modeRGB,
                                               ENG_Integer modeAlpha, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendEquationSeparate);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, modeRGB, copy);
//		call.setIntObjParam(1, modeAlpha, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendEquationSeparate(modeRGB.getValue(), modeAlpha.getValue());
    }

    public static void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendEquationSeparate);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, modeRGB);
//		call.setIntArrayParam(1, modeAlpha);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    public static void glBlendFunc(ENG_Integer sfactor, ENG_Integer dfactor) {
        glBlendFunc(sfactor, dfactor, true);
    }

    public static void glBlendFunc(ENG_Integer sfactor, ENG_Integer dfactor,
                                   boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendFunc);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, sfactor, copy);
//		call.setIntObjParam(1, dfactor, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendFunc(sfactor.getValue(), dfactor.getValue());
    }

    public static void glBlendFunc(int sfactor, int dfactor) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendFunc);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, sfactor);
//		call.setIntArrayParam(1, dfactor);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendFunc(sfactor, dfactor);
    }

    public static void glBlendFuncSeparate(ENG_Integer srcRGB, ENG_Integer dstRGB,
                                           ENG_Integer srcAlpha, ENG_Integer dstAlpha) {
        glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha, true);
    }

    public static void glBlendFuncSeparate(ENG_Integer srcRGB, ENG_Integer dstRGB,
                                           ENG_Integer srcAlpha, ENG_Integer dstAlpha, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendFuncSeparate);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, srcRGB, copy);
//		call.setIntObjParam(1, dstRGB, copy);
//		call.setIntObjParam(2, srcAlpha, copy);
//		call.setIntObjParam(3, dstAlpha, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendFuncSeparate(srcRGB.getValue(), dstRGB.getValue(),
                srcAlpha.getValue(), dstAlpha.getValue());
    }

    public static void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha,
                                           int dstAlpha) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBlendFuncSeparate);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, srcRGB);
//		call.setIntArrayParam(1, dstRGB);
//		call.setIntArrayParam(2, srcAlpha);
//		call.setIntArrayParam(3, dstAlpha);
//		glCallQueue.add(call);
        Gdx.gl20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    public static void glBufferData(ENG_Integer target, ENG_Integer size,
                                    Buffer data, ENG_Integer usage) {
        glBufferData(target, size, data, usage, true, true);
    }

    public static void glBufferData(ENG_Integer target, ENG_Integer size,
                                    Buffer data, ENG_Integer usage, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBufferData);
//		call.setIntObjCount(3);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, size, copy);
//		call.setIntObjParam(2, usage, copy);
//		call.setBuffer(0, data, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glBufferData(target.getValue(), size.getValue(),
                data, usage.getValue());
    }

    public static void glBufferData(int target, int size, Buffer data, int usage) {
        glBufferData(target, size, data, usage, false);
    }

    public static void glBufferData(int target, int size, Buffer data, int usage,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBufferData);
//		call.setIntArrayCount(3);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, size);
//		call.setIntArrayParam(2, usage);
//		call.setBuffer(0, data, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glBufferData(target, size, data, usage);
    }

    public static void glBufferSubData(ENG_Integer target, ENG_Integer offset,
                                       ENG_Integer size, Buffer data) {
        glBufferSubData(target, offset, size, data, true, true);
    }

    public static void glBufferSubData(ENG_Integer target, ENG_Integer offset,
                                       ENG_Integer size, Buffer data, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBufferSubData);
//		call.setIntObjCount(3);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, offset, copy);
//		call.setIntObjParam(2, size, copy);
//		call.setBuffer(0, data, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glBufferSubData(target.getValue(), offset.getValue(),
                size.getValue(), data);
    }

    public static void glBufferSubData(int target, int offset, int size, Buffer data) {
        glBufferSubData(target, offset, size, data, false);
    }

    public static void glBufferSubData(int target, int offset, int size, Buffer data,
                                       boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glBufferSubData);
//		call.setIntArrayCount(3);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, offset);
//		call.setIntArrayParam(2, size);
//		call.setBuffer(0, data, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glBufferSubData(target, offset, size, data);
    }

    public static int glCheckFramebufferStatus(final int target) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glCheckFramebufferStatus");
//				_setIntRet(Gdx.gl20.glCheckFramebufferStatus(target));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glCheckFramebufferStatus");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glCheckFramebufferStatus(target);
    }

    public static void glClear(ENG_Integer mask) {
        glClear(mask, true);
    }

    public static void glClear(ENG_Integer mask, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClear);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, mask, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glClear(mask.getValue());
    }

    public static void glClear(int mask) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClear);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, mask);
//		glCallQueue.add(call);
        Gdx.gl20.glClear(mask);
    }

    public static void glClearColor(ENG_Float red, ENG_Float green,
                                    ENG_Float blue, ENG_Float alpha) {
        glClearColor(red, green, blue, alpha, true);
    }

    public static void glClearColor(ENG_Float red, ENG_Float green,
                                    ENG_Float blue, ENG_Float alpha, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClearColor);
//		call.setFloatObjCount(4);
//		call.setFloatObjParam(0, red, copy);
//		call.setFloatObjParam(1, green, copy);
//		call.setFloatObjParam(2, blue, copy);
//		call.setFloatObjParam(3, alpha, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glClearColor(red.getValue(), green.getValue(),
                blue.getValue(), alpha.getValue());
    }

    public static void glClearColor(float red, float green, float blue, float alpha) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClearColor);
//		call.setFloatArrayCount(4);
//		call.setFloatArrayParam(0, red);
//		call.setFloatArrayParam(1, green);
//		call.setFloatArrayParam(2, blue);
//		call.setFloatArrayParam(3, alpha);
//		glCallQueue.add(call);
        Gdx.gl20.glClearColor(red, green, blue, alpha);
    }

    public static void glClearDepthf(ENG_Float depth) {
        glClearDepthf(depth, true);
    }

    public static void glClearDepthf(ENG_Float depth, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClearDepthf);
//		call.setFloatObjCount(1);
//		call.setFloatObjParam(0, depth, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glClearDepthf(depth.getValue());
    }

    public static void glClearDepthf(float depth) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClearDepthf);
//		call.setFloatArrayCount(1);
//		call.setFloatArrayParam(0, depth);
//		glCallQueue.add(call);
        Gdx.gl20.glClearDepthf(depth);
    }

    public static void glClearStencil(ENG_Integer s) {
        glClearStencil(s, true);
    }

    public static void glClearStencil(ENG_Integer s, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClearStencil);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, s, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glClearStencil(s.getValue());
    }

    public static void glClearStencil(int s) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glClearStencil);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, s);
//		glCallQueue.add(call);
        Gdx.gl20.glClearStencil(s);
    }

    public static void glColorMask(ENG_Boolean red, ENG_Boolean green,
                                   ENG_Boolean blue, ENG_Boolean alpha) {
        glColorMask(red, green, blue, alpha, true);
    }

    public static void glColorMask(ENG_Boolean red, ENG_Boolean green,
                                   ENG_Boolean blue, ENG_Boolean alpha, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glColorMask);
//		call.setBooleanObjCount(4);
//		call.setBooleanObjParam(0, red, copy);
//		call.setBooleanObjParam(1, green, copy);
//		call.setBooleanObjParam(2, blue, copy);
//		call.setBooleanObjParam(3, alpha, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glColorMask(red.getValue(), green.getValue(),
                blue.getValue(), alpha.getValue());
    }

    public static void glColorMask(boolean red, boolean green, boolean blue,
                                   boolean alpha) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glColorMask);
//		call.setBooleanArrayCount(4);
//		call.setBooleanArrayParam(0, red);
//		call.setBooleanArrayParam(1, green);
//		call.setBooleanArrayParam(2, blue);
//		call.setBooleanArrayParam(3, alpha);
//		glCallQueue.add(call);
        Gdx.gl20.glColorMask(red, green, blue, alpha);
    }

    public static void glCompileShader(ENG_Integer shader) {
        glCompileShader(shader, true);
    }

    public static void glCompileShader(ENG_Integer shader, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCompileShader);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, shader, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glCompileShader(shader.getValue());
    }

    public static void glCompileShaderImmediate(ENG_Integer shader) {
//		final int s = shader.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//				eventQueuedRun("glCompileShaderImmediate");
//
//				Gdx.gl20.glCompileShader(s);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glCompileShaderImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glCompileShader(shader.getValue());
    }

    public static void glCompileShader(int shader) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCompileShader);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, shader);
//		glCallQueue.add(call);
        Gdx.gl20.glCompileShader(shader);
    }

    public static void glCompileShaderImmediate(final int shader) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glCompileShaderImmediate");
//				Gdx.gl20.glCompileShader(shader);
//				if (DEBUG) {
////					int[] ret = new int[1];
//					IntBuffer buffer = ENG_Utility.allocateDirect(4).asIntBuffer();
//					Gdx.gl20.glGetShaderiv(shader, Gdx.gl20.GL_COMPILE_STATUS, buffer);				
//					int err = Gdx.gl20.glGetError();
////					String msg = GLU.gluErrorString(err);
//					System.out.println("glCompileShaderImmediate " + err);
//				}
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glCompileShaderImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glCompileShader(shader);
    }

    public static void glCompressedTexImage2D(ENG_Integer target, ENG_Integer level,
                                              ENG_Integer internalformat, ENG_Integer width, ENG_Integer height,
                                              ENG_Integer border, ENG_Integer imageSize, Buffer data) {
        glCompressedTexImage2D(target, level, internalformat, width, height,
                border, imageSize, data, true, true);
    }

    public static void glCompressedTexImage2D(ENG_Integer target, ENG_Integer level,
                                              ENG_Integer internalformat, ENG_Integer width, ENG_Integer height,
                                              ENG_Integer border, ENG_Integer imageSize, Buffer data, boolean copy,
                                              boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCompressedTexImage2D);
//		call.setIntObjCount(7);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, level, copy);
//		call.setIntObjParam(2, internalformat, copy);
//		call.setIntObjParam(3, width, copy);
//		call.setIntObjParam(4, height, copy);
//		call.setIntObjParam(5, border, copy);
//		call.setIntObjParam(6, imageSize, copyData);
//		call.setBuffer(0, data);
//		glCallQueue.add(call);
        Gdx.gl20.glCompressedTexImage2D(target.getValue(), level.getValue(),
                internalformat.getValue(),
                width.getValue(), height.getValue(),
                border.getValue(), imageSize.getValue(), data);
    }

    public static void glCompressedTexImage2D(int target, int level,
                                              int internalformat, int width, int height,
                                              int border, int imageSize, Buffer data) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCompressedTexImage2D);
//		call.setIntArrayCount(7);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, level);
//		call.setIntArrayParam(2, internalformat);
//		call.setIntArrayParam(3, width);
//		call.setIntArrayParam(4, height);
//		call.setIntArrayParam(5, border);
//		call.setIntArrayParam(6, imageSize);
//		call.setBuffer(0, data);
//		glCallQueue.add(call);
        Gdx.gl20.glCompressedTexImage2D(target, level, internalformat,
                width, height, border, imageSize, data);
    }

    public static void glCompressedTexSubImage2D(ENG_Integer target, ENG_Integer level,
                                                 ENG_Integer xoffset, ENG_Integer yoffset,
                                                 ENG_Integer width, ENG_Integer height, ENG_Integer format,
                                                 ENG_Integer imageSize, Buffer data) {
        glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height,
                format, imageSize, data, true, true);
    }

    public static void glCompressedTexSubImage2D(ENG_Integer target, ENG_Integer level,
                                                 ENG_Integer xoffset, ENG_Integer yoffset,
                                                 ENG_Integer width, ENG_Integer height, ENG_Integer format,
                                                 ENG_Integer imageSize, Buffer data, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCompressedTexSubImage2D);
//		call.setIntObjCount(8);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, level, copy);
//		call.setIntObjParam(2, xoffset, copy);
//		call.setIntObjParam(3, yoffset, copy);
//		call.setIntObjParam(4, width, copy);
//		call.setIntObjParam(5, height, copy);
//		call.setIntObjParam(6, format, copy);
//		call.setIntObjParam(7, imageSize, copy);
//		call.setBuffer(0, data, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glCompressedTexSubImage2D(target.getValue(), level.getValue(),
                xoffset.getValue(), yoffset.getValue(),
                width.getValue(), height.getValue(),
                format.getValue(), imageSize.getValue(), data);
    }

    public static void glCompressedTexSubImage2D(int target, int level,
                                                 int xoffset, int yoffset,
                                                 int width, int height, int format,
                                                 int imageSize, Buffer data) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCompressedTexSubImage2D);
//		call.setIntArrayCount(8);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, level);
//		call.setIntArrayParam(2, xoffset);
//		call.setIntArrayParam(3, yoffset);
//		call.setIntArrayParam(4, width);
//		call.setIntArrayParam(5, height);
//		call.setIntArrayParam(6, format);
//		call.setIntArrayParam(7, imageSize);
//		call.setBuffer(0, data);
//		glCallQueue.add(call);
        Gdx.gl20.glCompressedTexSubImage2D(target, level, xoffset, yoffset,
                width, height, format, imageSize, data);
    }

    public static void glCopyTexImage2D(ENG_Integer target, ENG_Integer level,
                                        ENG_Integer internalformat, ENG_Integer x, ENG_Integer y,
                                        ENG_Integer width, ENG_Integer height, ENG_Integer border) {
        glCopyTexImage2D(target, level, internalformat, x, y, width, height, border,
                true);
    }

    public static void glCopyTexImage2D(ENG_Integer target, ENG_Integer level,
                                        ENG_Integer internalformat, ENG_Integer x, ENG_Integer y,
                                        ENG_Integer width, ENG_Integer height, ENG_Integer border,
                                        boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCopyTexImage2D);
//		call.setIntObjCount(8);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, level, copy);
//		call.setIntObjParam(2, internalformat, copy);
//		call.setIntObjParam(3, x, copy);
//		call.setIntObjParam(4, y, copy);
//		call.setIntObjParam(5, width, copy);
//		call.setIntObjParam(6, height, copy);
//		call.setIntObjParam(7, border, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glCopyTexImage2D(target.getValue(), level.getValue(),
                internalformat.getValue(),
                x.getValue(), y.getValue(), width.getValue(), height.getValue(),
                border.getValue());
    }

    public static void glCopyTexImage2D(int target, int level,
                                        int internalformat, int x, int y,
                                        int width, int height, int border) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCopyTexImage2D);
//		call.setIntArrayCount(8);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, level);
//		call.setIntArrayParam(2, internalformat);
//		call.setIntArrayParam(3, x);
//		call.setIntArrayParam(4, y);
//		call.setIntArrayParam(5, width);
//		call.setIntArrayParam(6, height);
//		call.setIntArrayParam(7, border);
//		glCallQueue.add(call);
        Gdx.gl20.glCopyTexImage2D(target, level, internalformat,
                x, y, width, height, border);
    }

    public static void glCopyTexSubImage2D(ENG_Integer target, ENG_Integer level,
                                           ENG_Integer xoffset, ENG_Integer yoffset, ENG_Integer x, ENG_Integer y,
                                           ENG_Integer width, ENG_Integer height) {
        glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height,
                true);
    }

    public static void glCopyTexSubImage2D(ENG_Integer target, ENG_Integer level,
                                           ENG_Integer xoffset, ENG_Integer yoffset, ENG_Integer x, ENG_Integer y,
                                           ENG_Integer width, ENG_Integer height, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCopyTexSubImage2D);
//		call.setIntObjCount(8);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, level, copy);
//		call.setIntObjParam(2, xoffset, copy);
//		call.setIntObjParam(3, yoffset, copy);
//		call.setIntObjParam(4, x, copy);
//		call.setIntObjParam(5, y, copy);
//		call.setIntObjParam(6, width, copy);
//		call.setIntObjParam(7, height, copy);
//		glCallQueue.add(call);
    }

    public static void glCopyTexSubImage2D(int target, int level,
                                           int xoffset, int yoffset, int x, int y,
                                           int width, int height) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCopyTexSubImage2D);
//		call.setIntArrayCount(8);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, level);
//		call.setIntArrayParam(2, xoffset);
//		call.setIntArrayParam(3, yoffset);
//		call.setIntArrayParam(4, x);
//		call.setIntArrayParam(5, y);
//		call.setIntArrayParam(6, width);
//		call.setIntArrayParam(7, height);
//		glCallQueue.add(call);
        Gdx.gl20.glCopyTexSubImage2D(target, level,
                xoffset, yoffset, x, y, width, height);
    }

    public static int glCreateProgram() {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glCreateProgram");
//				_setIntRet(Gdx.gl20.glCreateProgram());
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glCreateProgram");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glCreateProgram();
    }

    public static int glCreateShader(final int type) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//				eventQueuedRun("glCreateShader");
//
//				_setIntRet(Gdx.gl20.glCreateShader(type));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glCreateShader");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glCreateShader(type);
    }

    public static void glCullFace(ENG_Integer mode) {
        glCullFace(mode, true);
    }

    public static void glCullFace(ENG_Integer mode, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCullFace);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, mode, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glCullFace(mode.getValue());
    }

    public static void glCullFace(int mode) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glCullFace);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, mode);
//		glCallQueue.add(call);
        Gdx.gl20.glCullFace(mode);
    }

    public static void glDeleteBuffers(ENG_Integer n, ENG_Integer[] buffers,
                                       ENG_Integer offset) {
        glDeleteBuffers(n, buffers, offset, true);
    }

    public static void glDeleteBuffers(ENG_Integer n, ENG_Integer[] buffers,
                                       ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteBuffersArr);
//		int bufLen = buffers.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, buffers[offset.getValue() + i - 1], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(buffers, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buffers,
                offset.getValue(), buffers.length);
        Gdx.gl20.glDeleteBuffers(n.getValue(), buffer);
    }

    public static void glDeleteBuffers(int n, int[] buffers, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteBuffersArr);
//		int bufLen = buffers.length - offset;
//		call.setIntArrayCount(bufLen + 2);
//		call.setIntArrayParam(0, n);
//		int[] buf = call.getIntArrParam();
//		System.arraycopy(buffers, offset, buf, 1, bufLen);
//		call.setIntArrayParam(bufLen + 1, offset);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buffers,
                offset, buffers.length);
        Gdx.gl20.glDeleteBuffers(n, buffer);
    }

    public static void glDeleteBuffers(ENG_Integer n, IntBuffer buffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteBuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, buffers);
//		glCallQueue.add(call);

        Gdx.gl20.glDeleteBuffers(n.getValue(), buffers);
    }

    public static void glDeleteBuffers(ENG_Integer n, IntBuffer buffers,
                                       boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteBuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n, copy);
//		call.setBuffer(0, buffers, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteBuffers(n.getValue(), buffers);
    }

    public static void glDeleteBuffers(int n, IntBuffer buffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteBuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, buffers);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteBuffers(n, buffers);
    }

    public static void glDeleteBuffers(int n, IntBuffer buffers, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteBuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, buffers, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteBuffers(n, buffers);
    }

    public static void glDeleteFramebuffers(ENG_Integer n, ENG_Integer[] framebuffers,
                                            ENG_Integer offset) {
        glDeleteFramebuffers(n, framebuffers, offset, true);
    }

    public static void glDeleteFramebuffers(ENG_Integer n, ENG_Integer[] framebuffers,
                                            ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteFramebuffersArr);
//		int bufLen = framebuffers.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, framebuffers[offset.getValue() + i - 1], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(framebuffers, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(framebuffers,
                offset.getValue(), framebuffers.length);
        Gdx.gl20.glDeleteFramebuffers(n.getValue(), buffer);
    }

    public static void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteFramebuffersArr);
//		int bufLen = framebuffers.length - offset;
//		call.setIntArrayCount(bufLen + 2);
//		call.setIntArrayParam(0, n);
//		int[] buf = call.getIntArrParam();
//		System.arraycopy(framebuffers, offset, buf, 1, bufLen);
//		call.setIntArrayParam(bufLen + 1, offset);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(framebuffers, offset, framebuffers.length);
        Gdx.gl20.glDeleteFramebuffers(n, buffer);
    }

    public static void glDeleteFramebuffers(ENG_Integer n, IntBuffer framebuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteFramebuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, framebuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteFramebuffers(n.getValue(), framebuffers);
    }

    public static void glDeleteFramebuffers(ENG_Integer n, IntBuffer framebuffers,
                                            boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteFramebuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n, copy);
//		call.setBuffer(0, framebuffers, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteFramebuffers(n.getValue(), framebuffers);
    }

    public static void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteFramebuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, framebuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteFramebuffers(n, framebuffers);
    }

    public static void glDeleteFramebuffers(int n, IntBuffer framebuffers,
                                            boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteFramebuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, framebuffers, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteFramebuffers(n, framebuffers);
    }

    public static void glDeleteProgram(ENG_Integer program) {
        glDeleteProgram(program, true);
    }

    public static void glDeleteProgram(ENG_Integer program, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteProgram);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, program, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteProgram(program.getValue());
    }

    public static void glDeleteProgram(int program) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteProgram);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, program);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteProgram(program);
    }

    public static void glDeleteRenderbuffers(ENG_Integer n, IntBuffer renderbuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteRenderbuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, renderbuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteRenderbuffers(n.getValue(), renderbuffers);
    }

    public static void glDeleteRenderbuffers(ENG_Integer n, IntBuffer renderbuffers,
                                             boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteRenderbuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n, copy);
//		call.setBuffer(0, renderbuffers, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteRenderbuffers(n.getValue(), renderbuffers);
    }

    public static void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteRenderbuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, renderbuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteRenderbuffers(n, renderbuffers);
    }

    public static void glDeleteRenderbuffers(int n, IntBuffer renderbuffers,
                                             boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteRenderbuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, renderbuffers, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteRenderbuffers(n, renderbuffers);
    }

    public static void glDeleteRenderbuffers(ENG_Integer n, ENG_Integer[] renderbuffers,
                                             ENG_Integer offset) {
        glDeleteRenderbuffers(n, renderbuffers, offset, true);
    }

    public static void glDeleteRenderbuffers(ENG_Integer n, ENG_Integer[] renderbuffers,
                                             ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteRenderbuffersArr);
//		int bufLen = renderbuffers.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, renderbuffers[offset.getValue() + i - 1], true);
//			}					
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(renderbuffers, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(renderbuffers,
                offset.getValue(), renderbuffers.length);
        Gdx.gl20.glDeleteBuffers(n.getValue(), buffer);
    }

    public static void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteRenderbuffersArr);
//		int bufLen = renderbuffers.length - offset;
//		call.setIntArrayCount(bufLen + 2);
//		call.setIntArrayParam(0, n);
//		int[] buf = call.getIntArrParam();
//		System.arraycopy(renderbuffers, offset, buf, 1, bufLen);
//		call.setIntArrayParam(bufLen + 1, offset);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(renderbuffers,
                offset, renderbuffers.length);
        Gdx.gl20.glDeleteBuffers(n, buffer);
    }

    public static void glDeleteShader(ENG_Integer shader) {
        glDeleteShader(shader, true);
    }

    public static void glDeleteShader(ENG_Integer shader, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteShader);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, shader, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteShader(shader.getValue());
    }

    public static void glDeleteShader(int shader) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteShader);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, shader);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteShader(shader);
    }

    public static void glDeleteTextures(ENG_Integer n, ENG_Integer[] textures,
                                        ENG_Integer offset) {
        glDeleteTextures(n, textures, offset, true);
    }

    public static void glDeleteTextures(ENG_Integer n, ENG_Integer[] textures,
                                        ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteTexturesArr);
//		int bufLen = textures.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, textures[offset.getValue() + i - 1], true);
//			}					
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(textures, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(textures,
                offset.getValue(), textures.length);
        Gdx.gl20.glDeleteTextures(n.getValue(), buffer);
    }

    public static void glDeleteTextures(int n, int[] textures, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteTexturesArr);
//		int bufLen = textures.length - offset;
//		call.setIntArrayCount(bufLen + 2);
//		call.setIntArrayParam(0, n);
//		int[] buf = call.getIntArrParam();
//		System.arraycopy(textures, offset, buf, 1, bufLen);
//		call.setIntArrayParam(bufLen + 1, offset);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(textures,
                offset, textures.length);
        Gdx.gl20.glDeleteTextures(n, buffer);
    }

    public static void glDeleteTextures(ENG_Integer n, IntBuffer textures) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteTexturesBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, textures);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteTextures(n.getValue(), textures);
    }

    public static void glDeleteTextures(ENG_Integer n, IntBuffer textures,
                                        boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteTexturesBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n, copy);
//		call.setBuffer(0, textures, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteTextures(n.getValue(), textures);
    }

    public static void glDeleteTextures(int n, IntBuffer textures) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteTexturesBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, textures);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteTextures(n, textures);
    }

    public static void glDeleteTextures(int n, IntBuffer textures, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDeleteTexturesBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, textures, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDeleteTextures(n, textures);
    }

    public static void glDepthFunc(ENG_Integer func) {
        glDepthFunc(func, true);
    }

    public static void glDepthFunc(ENG_Integer func, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDepthFunc);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, func, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDepthFunc(func.getValue());
    }

    public static void glDepthFunc(int func) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDepthFunc);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, func);
//		glCallQueue.add(call);
        Gdx.gl20.glDepthFunc(func);
    }

    public static void glDepthMask(ENG_Boolean flag) {
        glDepthMask(flag, true);
    }

    public static void glDepthMask(ENG_Boolean flag, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDepthMask);
//		call.setBooleanObjCount(1);
//		call.setBooleanObjParam(0, flag, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDepthMask(flag.getValue());
    }

    public static void glDepthMask(boolean flag) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDepthMask);
//		call.setBooleanArrayCount(1);
//		call.setBooleanArrayParam(0, flag);
//		glCallQueue.add(call);
        Gdx.gl20.glDepthMask(flag);
    }

    public static void glDepthRangef(ENG_Float zNear, ENG_Float zFar) {
        glDepthRangef(zNear, zFar, true);
    }

    public static void glDepthRangef(ENG_Float zNear, ENG_Float zFar, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDepthRangef);
//		call.setFloatObjCount(2);
//		call.setFloatObjParam(0, zNear, copy);
//		call.setFloatObjParam(1, zFar, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDepthRangef(zNear.getValue(), zFar.getValue());
    }

    public static void glDepthRangef(float zNear, float zFar) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDepthRangef);
//		call.setFloatArrayCount(2);
//		call.setFloatArrayParam(0, zNear);
//		call.setFloatArrayParam(1, zFar);
//		glCallQueue.add(call);
        Gdx.gl20.glDepthRangef(zNear, zFar);
    }

    public static void glDetachShader(ENG_Integer program, ENG_Integer shader) {
        glDetachShader(program, shader, true);
    }

    public static void glDetachShader(ENG_Integer program, ENG_Integer shader,
                                      boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDetachShader);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, shader, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDetachShader(program.getValue(), shader.getValue());
    }

    public static void glDetachShader(int program, int shader) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDetachShader);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, shader);
//		glCallQueue.add(call);
        Gdx.gl20.glDetachShader(program, shader);
    }

    public static void glDisable(ENG_Integer cap) {
        glDisable(cap, true);
    }

    public static void glDisable(ENG_Integer cap, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDisable);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, cap, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDisable(cap.getValue());
    }

    public static void glDisable(int cap) {
//		if (cap == 256) {
//			System.out.println();
//		}
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDisable);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, cap);
//		glCallQueue.add(call);
        Gdx.gl20.glDisable(cap);
    }

    public static void glDisableVertexAttribArray(ENG_Integer index) {
        glDisableVertexAttribArray(index, true);
    }

    public static void glDisableVertexAttribArray(ENG_Integer index, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDisableVertexAttribArray);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, index, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDisableVertexAttribArray(index.getValue());
    }

    public static void glDisableVertexAttribArray(int index) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDisableVertexAttribArray);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, index);
//		glCallQueue.add(call);
        Gdx.gl20.glDisableVertexAttribArray(index);
    }

    public static void glDrawArrays(ENG_Integer mode, ENG_Integer first,
                                    ENG_Integer count, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawArrays);
//		call.setIntObjCount(3);
//		call.setIntObjParam(0, mode, copy);
//		call.setIntObjParam(1, first, copy);
//		call.setIntObjParam(2, count, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawArrays(mode.getValue(), first.getValue(), count.getValue());
    }

    public static void glDrawArrays(int mode, int first, int count) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawArrays);
//		call.setIntArrayCount(3);
//		call.setIntArrayParam(0, mode);
//		call.setIntArrayParam(1, first);
//		call.setIntArrayParam(2, count);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawArrays(mode, first, count);
    }

    public static void glDrawElements(ENG_Integer mode, ENG_Integer count,
                                      ENG_Integer type, Buffer indices) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawElementsBuf);
//		call.setIntObjCount(3);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, mode);
//		call.setIntObjParam(1, count);
//		call.setIntObjParam(2, type);
//		call.setBuffer(0, indices);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawElements(mode.getValue(), count.getValue(), type.getValue(),
                indices);
    }

    public static void glDrawElements(ENG_Integer mode, ENG_Integer count,
                                      ENG_Integer type, Buffer indices, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawElementsBuf);
//		call.setIntObjCount(3);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, mode, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, type, copy);
//		call.setBuffer(0, indices, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawElements(mode.getValue(), count.getValue(), type.getValue(),
                indices);
    }

    public static void glDrawElements(int mode, int count, int type, Buffer indices) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawElementsBuf);
//		call.setIntArrayCount(3);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, mode);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, type);
//		call.setBuffer(0, indices);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawElements(mode, count, type, indices);
    }

    public static void glDrawElements(int mode, int count, int type, Buffer indices,
                                      boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawElementsBuf);
//		call.setIntArrayCount(3);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, mode);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, type);
//		call.setBuffer(0, indices, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawElements(mode, count, type, indices);
    }

    public static void glDrawElements(ENG_Integer mode, ENG_Integer count,
                                      ENG_Integer type, ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawElementsOff);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, mode, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, type, copy);
//		call.setIntObjParam(3, offset, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawElements(mode.getValue(), count.getValue(), type.getValue(),
                offset.getValue());
    }

    public static void glDrawElements(int mode, int count, int type, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glDrawElementsOff);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, mode);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, type);
//		call.setIntArrayParam(3, offset);
//		glCallQueue.add(call);
        Gdx.gl20.glDrawElements(mode, count, type,
                offset);
    }

    public static void glEnable(ENG_Integer cap) {
        glEnable(cap, true);
    }

    public static void glEnable(ENG_Integer cap, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glEnable);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, cap, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glEnable(cap.getValue());
    }

    public static void glEnable(int cap) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glEnable);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, cap);
//		glCallQueue.add(call);
        Gdx.gl20.glEnable(cap);
    }

    public static void glEnableVertexAttribArray(ENG_Integer index) {
        glEnableVertexAttribArray(index, true);
    }

    public static void glEnableVertexAttribArray(ENG_Integer index, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glEnableVertexAttribArray);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, index, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glEnableVertexAttribArray(index.getValue());
    }

    public static void glEnableVertexAttribArray(int index) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glEnableVertexAttribArray);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, index);
//		glCallQueue.add(call);
        Gdx.gl20.glEnableVertexAttribArray(index);
    }

    public static void glFinish() {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFinish);
//		glCallQueue.add(call);
        Gdx.gl20.glFinish();
    }

    public static void glFlush() {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFlush);
//		glCallQueue.add(call);
        Gdx.gl20.glFlush();
    }

    public static void glFramebufferRenderbuffer(ENG_Integer target,
                                                 ENG_Integer attachment, ENG_Integer renderbuffertarget,
                                                 ENG_Integer renderbuffer) {
        glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer,
                true);
    }

    public static void glFramebufferRenderbuffer(ENG_Integer target,
                                                 ENG_Integer attachment, ENG_Integer renderbuffertarget,
                                                 ENG_Integer renderbuffer, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFramebufferRenderbuffer);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, attachment, copy);
//		call.setIntObjParam(2, renderbuffertarget, copy);
//		call.setIntObjParam(3, renderbuffer, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glFramebufferRenderbuffer(target.getValue(), attachment.getValue(),
                renderbuffertarget.getValue(), renderbuffer.getValue());
    }

    public static void glFramebufferRenderbuffer(int target, int attachment,
                                                 int renderbuffertarget, int renderbuffer) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFramebufferRenderbuffer);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, attachment);
//		call.setIntArrayParam(2, renderbuffertarget);
//		call.setIntArrayParam(3, renderbuffer);
//		glCallQueue.add(call);
        Gdx.gl20.glFramebufferRenderbuffer(target, attachment,
                renderbuffertarget, renderbuffer);
    }

    public static void glFramebufferTexture2D(ENG_Integer target,
                                              ENG_Integer attachment, ENG_Integer textarget, ENG_Integer texture,
                                              ENG_Integer level) {
        glFramebufferTexture2D(target, attachment, textarget, texture, level, true);
    }

    public static void glFramebufferTexture2D(ENG_Integer target,
                                              ENG_Integer attachment, ENG_Integer textarget, ENG_Integer texture,
                                              ENG_Integer level, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFramebufferTexture2D);
//		call.setIntObjCount(5);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, attachment, copy);
//		call.setIntObjParam(2, textarget, copy);
//		call.setIntObjParam(3, texture, copy);
//		call.setIntObjParam(4, level, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glFramebufferTexture2D(target.getValue(), attachment.getValue(),
                textarget.getValue(), texture.getValue(), level.getValue());
    }

    public static void glFramebufferTexture2D(int target, int attachment,
                                              int textarget, int texture, int level) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFramebufferTexture2D);
//		call.setIntArrayCount(5);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, attachment);
//		call.setIntArrayParam(2, textarget);
//		call.setIntArrayParam(3, texture);
//		call.setIntArrayParam(4, level);
//		glCallQueue.add(call);
        Gdx.gl20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    public static void glFrontFace(ENG_Integer mode) {
        glFrontFace(mode, true);
    }

    public static void glFrontFace(ENG_Integer mode, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFrontFace);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, mode, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glFrontFace(mode.getValue());
    }

    public static void glFrontFace(int mode) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glFrontFace);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, mode);
//		glCallQueue.add(call);
        Gdx.gl20.glFrontFace(mode);
    }

    public static void glGenBuffers(ENG_Integer n, IntBuffer buffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenBuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, buffers);
//		glCallQueue.add(call);
        Gdx.gl20.glGenBuffers(n.getValue(), buffers);
    }

    public static void glGenBuffers(int n, IntBuffer buffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenBuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, buffers);
//		glCallQueue.add(call);
        Gdx.gl20.glGenBuffers(n, buffers);
    }

    public static void glGenBuffersImmediate(final ENG_Integer n,
                                             final IntBuffer buffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenBuffersImmediate");
//				Gdx.gl20.glGenBuffers(n.getValue(), buffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenBuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGenBuffers(n.getValue(), buffers);
    }

    public static void glGenBuffersImmediate(final int n, final IntBuffer buffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenBuffersImmediate");
//				Gdx.gl20.glGenBuffers(n, buffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenBuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		//return event.getInt();
        Gdx.gl20.glGenBuffers(n, buffers);
    }

    public static void glGenBuffers(ENG_Integer n, ENG_Integer[] buffers,
                                    ENG_Integer offset) {
        glGenBuffers(n, buffers, offset, true);
    }

    private static void glGenBuffers(ENG_Integer n, ENG_Integer[] buffers,
                                     ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenBuffersArr);
//		int bufLen = buffers.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, buffers[offset.getValue() + i - 1], true);
//			}					
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(buffers, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buffers,
                offset.getValue(), buffers.length);
        Gdx.gl20.glGenBuffers(n.getValue(), buffer);
    }

    public static void glGenBuffersImmediate(ENG_Integer n, ENG_Integer[] buffers,
                                             ENG_Integer offset) {
//		final int nParam = n.getValue();
//		final IntBuffer buffersParam = 
//				ENG_Utility.getIntArrayAsBuffer(buffers, 
//						offset.getValue(), buffers.length);
//		final int offsetParam = offset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//				eventQueuedRun("glGenBuffersImmediate");
//
//				Gdx.gl20.glGenBuffers(nParam, buffersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenBuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffersParam, offsetParam,
//				buffers, offsetParam);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buffers,
                offset.getValue(), buffers.length);
        Gdx.gl20.glGenBuffers(n.getValue(), buffer);
    }

    public static void glGenBuffersImmediate(final int n, final int[] buffers,
                                             final int offset) {
////		final int nParam = n.getValue();
//		final IntBuffer buffersParam = ENG_Utility.wrapBuffer(buffers);//ENG_Utility.getIntAsPrimitiveArray(buffers);
////		final int offsetParam = offset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenBuffersImmediate");
//				Gdx.gl20.glGenBuffers(n, buffersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenBuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffersParam, 0, buffers, offset);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(buffers,
                offset, buffers.length);
        Gdx.gl20.glGenBuffers(n, buffer);
    }
	
/*	public static void glGenBuffers(ENG_Integer n, IntBuffer buffers) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenBuffersBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n);
		call.setBuffer(0, buffers);
		glCallQueue.add(call);
	}
	
	public static void glGenBuffers(ENG_Integer n, IntBuffer buffers, 
			boolean copy, boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenBuffersBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n, copy);
		call.setBuffer(0, buffers, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGenBuffers(int n, IntBuffer buffers, boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenBuffersBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, buffers, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGenBuffers(int n, IntBuffer buffers) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenBuffersBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, buffers);
		glCallQueue.add(call);
	}
	
	public static void glGenBuffers(ENG_Integer n, ENG_Integer[] buffers,
			ENG_Integer offset) {
		glGenBuffers(n, buffers, offset, true);
	}
	
	public static void glGenBuffers(ENG_Integer n, ENG_Integer[] buffers,
			ENG_Integer offset, boolean copy) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glDeleteTexturesArr);
		int bufLen = buffers.length - offset.getValue();
		call.setIntObjCount(bufLen + 2);
		call.setIntObjParam(0, n, copy);
		if (copy) {
			for (int i = 1; i <= bufLen; ++i) {
				call.setIntObjParam(i, buffers[offset.getValue() + i - 1], true);
			}					
		} else {
			ENG_Integer[] buf = call.getIntObjParam();
			System.arraycopy(buffers, offset.getValue(), buf, 1, bufLen);
		}
		call.setIntObjParam(bufLen + 1, offset, copy);
		glCallQueue.add(call);
	}
	
	public static void glGenBuffers(int n, int[] buffers, int offset) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenBuffersArr);
		int bufLen = buffers.length - offset;
		call.setIntArrayCount(bufLen + 2);
		call.setIntArrayParam(0, n);
		int[] buf = call.getIntArrParam();
		System.arraycopy(buffers, offset, buf, 1, bufLen);
		call.setIntArrayParam(bufLen + 1, offset);
		glCallQueue.add(call);
	}*/

    public static void glGenFramebuffers(ENG_Integer n, IntBuffer framebuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenFramebuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, framebuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glGenFramebuffers(n.getValue(), framebuffers);
    }

    public static void glGenFramebuffers(int n, IntBuffer framebuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenFramebuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, framebuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glGenFramebuffers(n, framebuffers);
    }

    public static void glGenFramebuffersImmediate(final ENG_Integer n,
                                                  final IntBuffer framebuffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenFramebuffersImmediate");
//				Gdx.gl20.glGenFramebuffers(n.getValue(), framebuffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenFramebuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGenFramebuffers(n.getValue(), framebuffers);
    }

    public static void glGenFramebuffersImmediate(final int n,
                                                  final IntBuffer framebuffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenFramebuffersImmediate");
//				Gdx.gl20.glGenFramebuffers(n, framebuffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenFramebuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGenFramebuffers(n, framebuffers);
    }

    public static void glGenFramebuffers(ENG_Integer n, ENG_Integer[] framebuffers,
                                         ENG_Integer offset) {
        glGenFramebuffers(n, framebuffers, offset, true);
    }

    private static void glGenFramebuffers(ENG_Integer n, ENG_Integer[] framebuffers,
                                          ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenFramebuffersArr);
//		int bufLen = framebuffers.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, framebuffers[offset.getValue() + i - 1], true);
//			}					
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(framebuffers, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(framebuffers,
                offset.getValue(), framebuffers.length);
        Gdx.gl20.glGenFramebuffers(n.getValue(), buffer);
    }

    public static void glGenFramebuffersImmediate(ENG_Integer n,
                                                  ENG_Integer[] framebuffers, ENG_Integer offset) {
//		final int nParam = n.getValue();
//		final IntBuffer buffersParam = 
//				ENG_Utility.getIntArrayAsBuffer(
//						framebuffers, offset.getValue(), framebuffers.length);
//		final int offsetParam = offset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenFramebuffersImmediate");
//				Gdx.gl20.glGenFramebuffers(nParam, buffersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenFramebuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffersParam, offsetParam,
//				framebuffers, offsetParam);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(framebuffers,
                offset.getValue(), framebuffers.length);
        Gdx.gl20.glGenFramebuffers(n.getValue(), buffer);
    }

    public static void glGenFramebuffersImmediate(final int n,
                                                  final int[] framebuffers, final int offset) {
//		final IntBuffer buffersParam = ENG_Utility.wrapBuffer(framebuffers);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenFramebuffersImmediate");
//				Gdx.gl20.glGenFramebuffers(n, buffersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenFramebuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffersParam, 0, framebuffers, offset);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(framebuffers,
                offset, framebuffers.length);
        Gdx.gl20.glGenFramebuffers(n, buffer);
    }
	
/*	public static void glGenFramebuffers(ENG_Integer n, IntBuffer framebuffers) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenFramebuffersBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n);
		call.setBuffer(0, framebuffers);
		glCallQueue.add(call);
	}
	
	public static void glGenFramebuffers(ENG_Integer n, IntBuffer framebuffers,
			boolean copy, boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenFramebuffersBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n, copy);
		call.setBuffer(0, framebuffers, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGenFramebuffers(int n, IntBuffer framebuffers) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenFramebuffersBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, framebuffers);
		glCallQueue.add(call);
	}
	
	public static void glGenFramebuffers(int n, IntBuffer framebuffers, 
			boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenFramebuffersBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, framebuffers, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGenFramebuffers(ENG_Integer n, ENG_Integer[] framebuffers,
			ENG_Integer offset) {
		glGenFramebuffers(n, framebuffers, offset, true);
	}
	
	public static void glGenFramebuffers(ENG_Integer n, ENG_Integer[] framebuffers,
			ENG_Integer offset, boolean copy) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenFramebuffersArr);
		int bufLen = framebuffers.length - offset.getValue();
		call.setIntObjCount(bufLen + 2);
		call.setIntObjParam(0, n, copy);
		if (copy) {
			for (int i = 1; i <= bufLen; ++i) {
				call.setIntObjParam(i, framebuffers[offset.getValue() + i - 1], true);
			}					
		} else {
			ENG_Integer[] buf = call.getIntObjParam();
			System.arraycopy(framebuffers, offset.getValue(), buf, 1, bufLen);
		}
		call.setIntObjParam(bufLen + 1, offset, copy);
		glCallQueue.add(call);
	}
	
	public static void glGenFramebuffers(int n, int[] framebuffers, int offset) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenFramebuffersArr);
		int bufLen = framebuffers.length - offset;
		call.setIntArrayCount(bufLen + 2);
		call.setIntArrayParam(0, n);
		int[] buf = call.getIntArrParam();
		System.arraycopy(framebuffers, offset, buf, 1, bufLen);
		call.setIntArrayParam(bufLen + 1, offset);
		glCallQueue.add(call);
	}*/

    public static void glGenRenderbuffers(ENG_Integer n, IntBuffer renderbuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenRenderbuffersBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, renderbuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glGenRenderbuffers(n.getValue(), renderbuffers);
    }

    public static void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenRenderbuffersBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, renderbuffers);
//		glCallQueue.add(call);
        Gdx.gl20.glGenRenderbuffers(n, renderbuffers);
    }

    public static void glGenRenderbuffersImmediate(final ENG_Integer n,
                                                   final IntBuffer framebuffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenRenderbuffersImmediate");
//				Gdx.gl20.glGenRenderbuffers(n.getValue(), framebuffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenRenderbuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGenRenderbuffers(n.getValue(), framebuffers);
    }

    public static void glGenRenderbuffersImmediate(final int n,
                                                   final IntBuffer framebuffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenRenderbuffersImmediate");
//				Gdx.gl20.glGenRenderbuffers(n, framebuffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenRenderbuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGenRenderbuffers(n, framebuffers);
    }

    public static void glGenRenderbuffers(ENG_Integer n, ENG_Integer[] renderbuffers,
                                          ENG_Integer offset) {
        glGenRenderbuffers(n, renderbuffers, offset, true);
    }

    private static void glGenRenderbuffers(ENG_Integer n, ENG_Integer[] renderbuffers,
                                           ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenRenderbuffersArr);
//		int bufLen = renderbuffers.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, renderbuffers[offset.getValue() + i - 1], true);
//			}					
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(renderbuffers, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(renderbuffers,
                offset.getValue(), renderbuffers.length);
        Gdx.gl20.glGenRenderbuffers(n.getValue(), buffer);

    }

    public static void glGenRenderbuffersImmediate(ENG_Integer n,
                                                   ENG_Integer[] renderbuffers, ENG_Integer offset) {
//		final int nParam = n.getValue();
//		final IntBuffer buffersParam = 
//				ENG_Utility.getIntArrayAsBuffer(
//						renderbuffers, offset.getValue(), renderbuffers.length);
//		final int offsetParam = offset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenRenderbuffersImmediate");
//				Gdx.gl20.glGenRenderbuffers(nParam, buffersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenRenderbuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffersParam, offsetParam,
//				renderbuffers, offsetParam);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(renderbuffers,
                offset.getValue(), renderbuffers.length);
        Gdx.gl20.glGenRenderbuffers(n.getValue(), buffer);
    }

    public static void glGenRenderbuffersImmediate(final int n,
                                                   final int[] renderbuffers, final int offset) {
//		final IntBuffer wrapBuffer = ENG_Utility.wrapBuffer(renderbuffers);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenRenderbuffersImmediate");
//				Gdx.gl20.glGenRenderbuffers(n, wrapBuffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenRenderbuffersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				wrapBuffer, 0, renderbuffers, offset);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(renderbuffers,
                offset, renderbuffers.length);
        Gdx.gl20.glGenRenderbuffers(n, buffer);
    }
	
/*	public static void glGenRenderbuffers(ENG_Integer n, IntBuffer renderbuffers) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenRenderbuffersBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n);
		call.setBuffer(0, renderbuffers);
		glCallQueue.add(call);
	}
	
	public static void glGenRenderbuffers(ENG_Integer n, IntBuffer renderbuffers,
			boolean copy, boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenRenderbuffersBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n, copy);
		call.setBuffer(0, renderbuffers, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenRenderbuffersBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, renderbuffers);
		glCallQueue.add(call);
	}
	
	public static void glGenRenderbuffers(int n, IntBuffer renderbuffers,
			boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenRenderbuffersBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, renderbuffers, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGenRenderbuffers(ENG_Integer n, ENG_Integer[] renderbuffers,
			ENG_Integer offset) {
		glGenRenderbuffers(n, renderbuffers, offset, true);
	}
	
	public static void glGenRenderbuffers(ENG_Integer n, ENG_Integer[] renderbuffers,
			ENG_Integer offset, boolean copy) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenRenderbuffersArr);
		int bufLen = renderbuffers.length - offset.getValue();
		call.setIntObjCount(bufLen + 2);
		call.setIntObjParam(0, n, copy);
		if (copy) {
			for (int i = 1; i <= bufLen; ++i) {
				call.setIntObjParam(i, renderbuffers[offset.getValue() + i - 1], true);
			}					
		} else {
			ENG_Integer[] buf = call.getIntObjParam();
			System.arraycopy(renderbuffers, offset.getValue(), buf, 1, bufLen);
		}
		call.setIntObjParam(bufLen + 1, offset, copy);
		glCallQueue.add(call);
	}
	
	public static void glGenRenderbuffers(int n, int[] renderbuffers, int offset) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenRenderbuffersArr);
		int bufLen = renderbuffers.length - offset;
		call.setIntArrayCount(bufLen + 2);
		call.setIntArrayParam(0, n);
		int[] buf = call.getIntArrParam();
		System.arraycopy(renderbuffers, offset, buf, 1, bufLen);
		call.setIntArrayParam(bufLen + 1, offset);
		glCallQueue.add(call);
	}*/

    public static void glGenTextures(ENG_Integer n, IntBuffer textures) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenTexturesBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, n);
//		call.setBuffer(0, textures);
//		glCallQueue.add(call);
        Gdx.gl20.glGenTextures(n.getValue(), textures);
    }

    public static void glGenTextures(int n, IntBuffer textures) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenTexturesBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setBuffer(0, textures);
//		glCallQueue.add(call);
        Gdx.gl20.glGenTextures(n, textures);
    }

    public static void glGenTexturesImmediate(final ENG_Integer n,
                                              final IntBuffer framebuffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenTexturesImmediate");
//				Gdx.gl20.glGenTextures(n.getValue(), framebuffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenTexturesImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGenTextures(n.getValue(), framebuffers);
    }

    public static void glGenTexturesImmediate(final int n,
                                              final IntBuffer framebuffers) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenTexturesImmediate");
//				Gdx.gl20.glGenTextures(n, framebuffers);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenTexturesImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGenTextures(n, framebuffers);
    }

    public static void glGenTextures(ENG_Integer n, ENG_Integer[] textures,
                                     ENG_Integer offset) {
        glGenTextures(n, textures, offset, true);
    }

    private static void glGenTextures(ENG_Integer n, ENG_Integer[] textures,
                                      ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenTexturesArr);
//		int bufLen = textures.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, n, copy);
//		if (copy) {
//			for (int i = 1; i <= bufLen; ++i) {
//				call.setIntObjParam(i, textures[offset.getValue() + i - 1], true);
//			}					
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(textures, offset.getValue(), buf, 1, bufLen);
//		}
//		call.setIntObjParam(bufLen + 1, offset, copy);
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(textures,
                offset.getValue(), textures.length);
        Gdx.gl20.glGenTextures(n.getValue(), buffer);
    }

    public static void glGenTexturesImmediate(ENG_Integer n,
                                              ENG_Integer[] textures, ENG_Integer offset) {
//		final int nParam = n.getValue();
//		final IntBuffer buffersParam = 
//				ENG_Utility.getIntArrayAsBuffer(
//						renderbuffers, offset.getValue(), renderbuffers.length);
//		final int offsetParam = offset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenTexturesImmediate");
//				Gdx.gl20.glGenTextures(nParam, buffersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenTexturesImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffersParam, offsetParam,
//				renderbuffers, offsetParam);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(textures,
                offset.getValue(), textures.length);
        Gdx.gl20.glGenTextures(n.getValue(), buffer);
    }

    public static void glGenTexturesImmediate(final int n,
                                              final int[] textures, final int offset) {
//		final IntBuffer buf = ENG_Utility.wrapBuffer(renderbuffers);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGenTexturesImmediate");
//				Gdx.gl20.glGenTextures(n, buf);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGenTexturesImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buf, 0, renderbuffers, offset);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(textures,
                offset, textures.length);
        Gdx.gl20.glGenTextures(n, buffer);
    }
	
/*	public static void glGenTextures(ENG_Integer n, IntBuffer textures) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenTexturesBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n);
		call.setBuffer(0, textures);
		glCallQueue.add(call);
	}
	
	public static void glGenTextures(ENG_Integer n, IntBuffer textures,
			boolean copy, boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenTexturesBuf);
		call.setIntObjCount(1);
		call.setBufferCount(1);
		call.setIntObjParam(0, n, copy);
		call.setBuffer(0, textures, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGenTextures(int n, IntBuffer textures) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenTexturesBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, textures);
		glCallQueue.add(call);
	}
	
	public static void glGenTextures(int n, IntBuffer textures, boolean copy) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenTexturesBuf);
		call.setIntArrayCount(1);
		call.setBufferCount(1);
		call.setIntArrayParam(0, n);
		call.setBuffer(0, textures, copy);
		glCallQueue.add(call);
	}
	
	public static void glGenTextures(ENG_Integer n, ENG_Integer[] textures, 
			ENG_Integer offset) {
		glGenTextures(n, textures, offset, true);
	}
	
	public static void glGenTextures(ENG_Integer n, ENG_Integer[] textures, 
			ENG_Integer offset, boolean copy) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenTexturesArr);
		int bufLen = textures.length - offset.getValue();
		call.setIntObjCount(bufLen + 2);
		call.setIntObjParam(0, n, copy);
		if (copy) {
			for (int i = 1; i <= bufLen; ++i) {
				call.setIntObjParam(i, textures[offset.getValue() + i - 1], true);
			}					
		} else {
			ENG_Integer[] buf = call.getIntObjParam();
			System.arraycopy(textures, offset.getValue(), buf, 1, bufLen);
		}
		call.setIntObjParam(bufLen + 1, offset, copy);
		glCallQueue.add(call);
	}
	
	public static void glGenTextures(int n, int[] textures, int offset) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGenTexturesArr);
		int bufLen = textures.length - offset;
		call.setIntArrayCount(bufLen + 2);
		call.setIntArrayParam(0, n);
		int[] buf = call.getIntArrParam();
		System.arraycopy(textures, offset, buf, 1, bufLen);
		call.setIntArrayParam(bufLen + 1, offset);
		glCallQueue.add(call);
	}*/

    public static void glGenerateMipmap(ENG_Integer target) {
        glGenerateMipmap(target, true);
    }

    public static void glGenerateMipmap(ENG_Integer target, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenerateMipmap);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, target, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glGenerateMipmap(target.getValue());
    }

    public static void glGenerateMipmap(int target) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGenerateMipmap);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, target);
//		glCallQueue.add(call);
        Gdx.gl20.glGenerateMipmap(target);
    }

//	public static void glGetActiveAttrib(ENG_Integer program, ENG_Integer index,
//			ENG_Integer bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
//			ENG_Integer name) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetActiveAttribBuf);
//		call.setIntObjCount(4);
//		call.setBufferCount(3);
//		call.setIntObjParam(0, program);
//		call.setIntObjParam(1, index);
//		call.setIntObjParam(2, bufsize);
//		call.setIntObjParam(3, name);
//		call.setBuffer(0, length);
//		call.setBuffer(1, size);
//		call.setBuffer(2, type);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetActiveAttrib(int program, int index,
//			int bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
//			byte name) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetActiveAttribBuf);
//		call.setIntArrayCount(4);
//		call.setBufferCount(3);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, index);
//		call.setIntArrayParam(2, bufsize);
//		call.setIntArrayParam(3, name);
//		call.setBuffer(0, length);
//		call.setBuffer(1, size);
//		call.setBuffer(2, type);
//		glCallQueue.add(call);
//	}

    public static String glGetActiveAttribImmediate(final ENG_Integer program,
                                                    final ENG_Integer index, final IntBuffer size,
                                                    final IntBuffer type) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetActiveAttribImmediate");
//				_setStringRet(Gdx.gl20.glGetActiveAttrib(
//						program.getValue(), index.getValue(),
//						size, type));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetActiveAttribImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		return event.getString();
        return Gdx.gl20.glGetActiveAttrib(program.getValue(), index.getValue(),
                size, type);
    }

    public static String glGetActiveAttribImmediate(final int program, final int index,

                                                    final IntBuffer size, final IntBuffer type) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetActiveAttribImmediate");
//				_setStringRet(Gdx.gl20.glGetActiveAttrib(program, index, size,
//						type));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetActiveAttribImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		return event.getString();
        return Gdx.gl20.glGetActiveAttrib(program, index,
                size, type);
    }

//	public static void glGetActiveAttrib(ENG_Integer program, ENG_Integer index,
//			ENG_Integer bufsize, ENG_Integer[] length, ENG_Integer lengthOffset,
//			ENG_Integer[] size, ENG_Integer sizeOffset,
//			ENG_Integer[] type, ENG_Integer typeOffset,
//			ENG_Integer[] name, ENG_Integer nameOffset) {
//		glGetActiveAttrib(program, index, bufsize, length, lengthOffset,
//				size, sizeOffset, type, typeOffset, name, nameOffset, true, true);
//	}
//	
//	private static void glGetActiveAttrib(ENG_Integer program, ENG_Integer index,
//			ENG_Integer bufsize, ENG_Integer[] length, ENG_Integer lengthOffset,
//			ENG_Integer[] size, ENG_Integer sizeOffset,
//			ENG_Integer[] type, ENG_Integer typeOffset,
//			ENG_Integer[] name, ENG_Integer nameOffset, 
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetActiveAttribArr);
//		int firstBuf = length.length - lengthOffset.getValue();
//		int secondBuf = size.length - sizeOffset.getValue();
//		int thirdBuf = type.length - typeOffset.getValue();
//		int fourthBuf = name.length - nameOffset.getValue();
//		int bufLen = firstBuf + secondBuf + thirdBuf + fourthBuf;
//		call.setIntObjCount(bufLen + 7);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, index, copy);
//		call.setIntObjParam(2, bufsize, copy);
//		call.setIntObjParam(3, lengthOffset, copy);
//		call.setIntObjParam(4, sizeOffset, copy);
//		call.setIntObjParam(5, typeOffset, copy);
//		call.setIntObjParam(6, nameOffset, copy);
//		final int sub = 7;
//		if (copyData) {
//			
//			int i = sub;
//			for (; i < firstBuf + sub; ++i) {
//				call.setIntObjParam(i, length[lengthOffset.getValue() + i - sub], true);
//			}
//			int j = i;
//			for (; j < secondBuf + sub + i; ++j) {
//				call.setIntObjParam(j, size[sizeOffset.getValue() + j - i - sub], true);
//			}
//			int k = j;
//			for (; k < thirdBuf + sub + j; ++k) {
//				call.setIntObjParam(k, type[typeOffset.getValue() + k - j - sub], true);
//			}
//			int l = k;
//			for (; l < fourthBuf + sub + k; ++l) {
//				call.setIntObjParam(l, name[nameOffset.getValue() + l - k - sub], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(length, lengthOffset.getValue(),
//					buf, sub, firstBuf);
//			System.arraycopy(size, sizeOffset.getValue(),
//					buf, firstBuf + sub, secondBuf);
//			System.arraycopy(type, typeOffset.getValue(),
//					buf, firstBuf + secondBuf + sub, thirdBuf);
//			System.arraycopy(name, nameOffset.getValue(),
//					buf, firstBuf + secondBuf + thirdBuf + sub, fourthBuf);
//		}
//		
//		glCallQueue.add(call);
//	}

//	public static String glGetActiveAttribImmediate(ENG_Integer program, 
//			ENG_Integer index,
//			
//			ENG_Integer[] size, ENG_Integer sizeOffset,
//			ENG_Integer[] type, ENG_Integer typeOffset,
//			ENG_Byte[] name, ENG_Integer nameOffset) {
////		final int programParam = program.getValue();
////		final int indexParam = index.getValue();
//////		final int bufsizeParam = bufsize.getValue();
//////		final IntBuffer lengthParam = ENG_Utility.getIntArrayAsBuffer(length);
//////		final int lengthOffsetParam = lengthOffset.getValue();
////		final IntBuffer sizeParam = ENG_Utility.getIntArrayAsBuffer(size);
////		final int sizeOffsetParam = sizeOffset.getValue();
////		final IntBuffer typeParam = ENG_Utility.getIntArrayAsBuffer(type);
////		final int typeOffsetParam = typeOffset.getValue();
//////		final ByteBuffer nameParam = ENG_Utility.getByteAsPrimitiveArray(name);
//////		final int nameOffsetParam = nameOffset.getValue();
////		GLRunnableEvent event = new GLRunnableEvent() {
////
////			@Override
////			public void run() {
////
////				eventQueuedRun("glGetActiveAttribImmediate");
////				_setStringRet(Gdx.gl20.glGetActiveAttrib(programParam, indexParam,
//////						bufsizeParam, lengthParam, lengthOffsetParam,
////						sizeParam, typeParam));
////				_getSemaphore().release();
////			}
////			
////		};
////		eventQueued("glGetActiveAttribImmediate");
////		GLRenderSurface.getSingleton().queueEvent(event);
////		GLRenderSurface.getSingleton().disableBufferSwap();
////		GLRenderSurface.getSingleton().requestRender();
////		event.checkSemaphoreReleased();
//////		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(lengthParam, lengthOffsetParam, 
//////				length, lengthOffsetParam);
////		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(sizeParam, sizeOffsetParam,
////				size, sizeOffsetParam);
////		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(typeParam, typeOffsetParam,
////				type, typeOffsetParam);
//////		ENG_Utility.getBytePrimitiveArrayAsByteObjArray(nameParam, nameOffsetParam,
//////				name, nameOffsetParam);
////	/*	byte[] bytes = event.getString().getBytes();
////		int len = name.length - nameOffset.getValue();
////		for (int i = 0; i < len; ++i) {
////			name[i + nameOffset.getValue()].setValue(bytes[i]);
////		}*/
////		return event.getString();
//		
//	}

//	public static String glGetActiveAttribImmediate(final int program, 
//			final int index,
//			
//			final int[] size, final int sizeOffset,
//			final int[] type, final int typeOffset) {
//		
////		final IntBuffer lengthParam = ENG_Utility.wrapBuffer(length);
////		final int lengthOffsetParam = lengthOffset.getValue();
//		final IntBuffer sizeParam = ENG_Utility.wrapBuffer(size);
////		final int sizeOffsetParam = sizeOffset.getValue();
//		final IntBuffer typeParam = ENG_Utility.wrapBuffer(type);
////		final int typeOffsetParam = typeOffset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetActiveAttribImmediate");
////				Gdx.gl20.glGetActiveAttrib(program, index, bufsize, length, lengthOffset,
////						size, sizeOffset, type, typeOffset, name, nameOffset);
//				_setStringRet(Gdx.gl20.glGetActiveAttrib(program, index,
////						bufsizeParam, lengthParam, lengthOffsetParam,
//						sizeParam, typeParam));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetActiveAttribImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				sizeParam, 0, size, sizeOffset);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				typeParam, 0, type, typeOffset);
//	/*	byte[] bytes = event.getString().getBytes();
//		int len = name.length - nameOffset;
//		for (int i = 0; i < len; ++i) {
//			name[i + nameOffset] = bytes[i];
//		}*/
//		return event.getString();
//	}
	
/*	public static void glGetActiveAttrib(ENG_Integer program, ENG_Integer index,
			ENG_Integer bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
			ENG_Integer name) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetActiveAttribBuf);
		call.setIntObjCount(4);
		call.setBufferCount(3);
		call.setIntObjParam(0, program);
		call.setIntObjParam(1, index);
		call.setIntObjParam(2, bufsize);
		call.setIntObjParam(3, name);
		call.setBuffer(0, length);
		call.setBuffer(1, size);
		call.setBuffer(2, type);
		glCallQueue.add(call);
	}
	
	public static void glGetActiveAttrib(ENG_Integer program, ENG_Integer index,
			ENG_Integer bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
			ENG_Integer name, boolean copy, boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetActiveAttribBuf);
		call.setIntObjCount(4);
		call.setBufferCount(3);
		call.setIntObjParam(0, program, copy);
		call.setIntObjParam(1, index, copy);
		call.setIntObjParam(2, bufsize, copy);
		call.setIntObjParam(3, name, copy);
		call.setBuffer(0, length, copyData);
		call.setBuffer(1, size, copyData);
		call.setBuffer(2, type, copyData);
		glCallQueue.add(call);
	}
	
	public static void glGetActiveAttrib(int program, int index,
			int bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
			byte name) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetActiveAttribBuf);
		call.setIntArrayCount(4);
		call.setBufferCount(3);
		call.setIntArrayParam(0, program);
		call.setIntArrayParam(1, index);
		call.setIntArrayParam(2, bufsize);
		call.setIntArrayParam(3, name);
		call.setBuffer(0, length);
		call.setBuffer(1, size);
		call.setBuffer(2, type);
		glCallQueue.add(call);
	}
	
	public static void glGetActiveAttrib(int program, int index,
			int bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
			byte name, boolean copy) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetActiveAttribBuf);
		call.setIntArrayCount(4);
		call.setBufferCount(3);
		call.setIntArrayParam(0, program);
		call.setIntArrayParam(1, index);
		call.setIntArrayParam(2, bufsize);
		call.setIntArrayParam(3, name);
		call.setBuffer(0, length, copy);
		call.setBuffer(1, size, copy);
		call.setBuffer(2, type, copy);
		glCallQueue.add(call);
	}
	
	public static void glGetActiveAttrib(ENG_Integer program, ENG_Integer index,
			ENG_Integer bufsize, ENG_Integer[] length, ENG_Integer lengthOffset,
			ENG_Integer[] size, ENG_Integer sizeOffset,
			ENG_Integer[] type, ENG_Integer typeOffset,
			ENG_Integer[] name, ENG_Integer nameOffset) {
		glGetActiveAttrib(program, index, bufsize, length, lengthOffset,
				size, sizeOffset, type, typeOffset, name, nameOffset, true, true);
	}
	
	public static void glGetActiveAttrib(ENG_Integer program, ENG_Integer index,
			ENG_Integer bufsize, ENG_Integer[] length, ENG_Integer lengthOffset,
			ENG_Integer[] size, ENG_Integer sizeOffset,
			ENG_Integer[] type, ENG_Integer typeOffset,
			ENG_Integer[] name, ENG_Integer nameOffset, 
			boolean copy, boolean copyData) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetActiveAttribArr);
		int firstBuf = length.length - lengthOffset.getValue();
		int secondBuf = size.length - sizeOffset.getValue();
		int thirdBuf = type.length - typeOffset.getValue();
		int fourthBuf = name.length - nameOffset.getValue();
		int bufLen = firstBuf + secondBuf + thirdBuf + fourthBuf;
		call.setIntObjCount(bufLen + 7);
		call.setIntObjParam(0, program, copy);
		call.setIntObjParam(1, index, copy);
		call.setIntObjParam(2, bufsize, copy);
		if (copyData) {
			int i = 3;
			for (; i < firstBuf + 3; ++i) {
				call.setIntObjParam(i, length[lengthOffset.getValue() + i - 3], true);
			}
			int j = i;
			for (; j < secondBuf + 3; ++j) {
				call.setIntObjParam(j, size[sizeOffset.getValue() + j - i - 3], true);
			}
			int k = j;
			for (; k < thirdBuf + 3; ++k) {
				call.setIntObjParam(k, type[typeOffset.getValue() + k - j - 3], true);
			}
			int l = k;
			for (; l < fourthBuf + 3; ++l) {
				call.setIntObjParam(l, name[nameOffset.getValue() + l - k - 3], true);
			}
		} else {
			ENG_Integer[] buf = call.getIntObjParam();
			System.arraycopy(length, lengthOffset.getValue(), buf, 1, firstBuf);
			System.arraycopy(size, sizeOffset.getValue(), buf, firstBuf + 1, secondBuf);
			System.arraycopy(type, typeOffset.getValue(), buf, secondBuf + 1, thirdBuf);
			System.arraycopy(name, nameOffset.getValue(), buf, thirdBuf + 1, fourthBuf);
		}
		call.setIntObjParam(bufLen + 1, lengthOffset, copy);
		call.setIntObjParam(bufLen + 2, sizeOffset, copy);
		call.setIntObjParam(bufLen + 3, typeOffset, copy);
		call.setIntObjParam(bufLen + 4, nameOffset, copy);
		glCallQueue.add(call);
	}
	
	public static void glGetActiveAttrib(int program, int index,
			int bufsize, int[] length, int lengthOffset,
			int[] size, int sizeOffset,
			int[] type, int typeOffset,
			byte[] name, int nameOffset) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetActiveAttribArr);
		int firstBuf = length.length - lengthOffset;
		int secondBuf = size.length - sizeOffset;
		int thirdBuf = type.length - typeOffset;
		int fourthBuf = name.length - nameOffset;
		int bufLen = firstBuf + secondBuf + thirdBuf + fourthBuf;
		call.setIntArrayCount(bufLen + 7);
		call.setIntArrayParam(0, program);
		call.setIntArrayParam(1, index);
		call.setIntArrayParam(2, bufsize);
		int[] buf = call.getIntArrParam();
		System.arraycopy(length, lengthOffset, buf, 1, firstBuf);
		System.arraycopy(size, sizeOffset, buf, firstBuf + 1, secondBuf);
		System.arraycopy(type, typeOffset, buf, secondBuf + 1, thirdBuf);
		System.arraycopy(name, nameOffset, buf, thirdBuf + 1, fourthBuf);
		call.setIntArrayParam(bufLen + 1, lengthOffset);
		call.setIntArrayParam(bufLen + 2, sizeOffset);
		call.setIntArrayParam(bufLen + 3, typeOffset);
		call.setIntArrayParam(bufLen + 4, nameOffset);
		glCallQueue.add(call);
	}*/

//	public static void glGetActiveUniform(ENG_Integer program, ENG_Integer index,
//			ENG_Integer bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
//			ENG_Integer name) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetActiveUniformBuf);
//		call.setIntObjCount(4);
//		call.setBufferCount(3);
//		call.setIntObjParam(0, program);
//		call.setIntObjParam(1, index);
//		call.setIntObjParam(2, bufsize);
//		call.setIntObjParam(3, name);
//		call.setBuffer(0, length);
//		call.setBuffer(1, size);
//		call.setBuffer(2, type);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetActiveUniform(int program, int index,
//			int bufsize, IntBuffer length, IntBuffer size, IntBuffer type,
//			byte name) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetActiveUniformBuf);
//		call.setIntArrayCount(4);
//		call.setBufferCount(3);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, index);
//		call.setIntArrayParam(2, bufsize);
//		call.setIntArrayParam(3, name);
//		call.setBuffer(0, length);
//		call.setBuffer(1, size);
//		call.setBuffer(2, type);
//		glCallQueue.add(call);
//	}

    public static String glGetActiveUniformImmediate(final ENG_Integer program,
                                                     final ENG_Integer index, final IntBuffer size,
                                                     final IntBuffer type) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetActiveUniformImmediate");
//				_setStringRet(
//						Gdx.gl20.glGetActiveUniform(
//								program.getValue(), index.getValue(),
//								size, type));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetActiveUniformImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		return event.getString();
        return Gdx.gl20.glGetActiveUniform(program.getValue(), index.getValue(),
                size, type);
    }

    public static String glGetActiveUniformImmediate(final int program,
                                                     final int index,
                                                     final IntBuffer size, final IntBuffer type) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetActiveUniformImmediate");
//				_setStringRet(Gdx.gl20.glGetActiveUniform(program, index, 
//						size,
//						type));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetActiveUniformImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		return event.getString();
        return Gdx.gl20.glGetActiveUniform(program, index, size, type);
    }

//	public static void glGetActiveUniform(ENG_Integer program, ENG_Integer index,
//			ENG_Integer bufsize, ENG_Integer[] length, ENG_Integer lengthOffset,
//			ENG_Integer[] size, ENG_Integer sizeOffset,
//			ENG_Integer[] type, ENG_Integer typeOffset,
//			ENG_Integer[] name, ENG_Integer nameOffset) {
//		glGetActiveUniform(program, index, bufsize, length, lengthOffset,
//				size, sizeOffset, type, typeOffset, name, nameOffset, true, true);
//	}
//	
//	private static void glGetActiveUniform(ENG_Integer program, ENG_Integer index,
//			ENG_Integer bufsize, ENG_Integer[] length, ENG_Integer lengthOffset,
//			ENG_Integer[] size, ENG_Integer sizeOffset,
//			ENG_Integer[] type, ENG_Integer typeOffset,
//			ENG_Integer[] name, ENG_Integer nameOffset, 
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetActiveUniformArr);
//		int firstBuf = length.length - lengthOffset.getValue();
//		int secondBuf = size.length - sizeOffset.getValue();
//		int thirdBuf = type.length - typeOffset.getValue();
//		int fourthBuf = name.length - nameOffset.getValue();
//		int bufLen = firstBuf + secondBuf + thirdBuf + fourthBuf;
//		call.setIntObjCount(bufLen + 7);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, index, copy);
//		call.setIntObjParam(2, bufsize, copy);
//		if (copyData) {
//			int i = 3;
//			for (; i < firstBuf + 3; ++i) {
//				call.setIntObjParam(i, length[lengthOffset.getValue() + i - 3], true);
//			}
//			int j = i;
//			for (; j < secondBuf + 3 + i; ++j) {
//				call.setIntObjParam(j, size[sizeOffset.getValue() + j - i - 3], true);
//			}
//			int k = j;
//			for (; k < thirdBuf + 3 + j; ++k) {
//				call.setIntObjParam(k, type[typeOffset.getValue() + k - j - 3], true);
//			}
//			int l = k;
//			for (; l < fourthBuf + 3 + k; ++l) {
//				call.setIntObjParam(l, name[nameOffset.getValue() + l - k - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(length, lengthOffset.getValue(), buf, 1, firstBuf);
//			System.arraycopy(size, sizeOffset.getValue(), buf, firstBuf + 1, secondBuf);
//			System.arraycopy(type, typeOffset.getValue(), buf, secondBuf + 1, thirdBuf);
//			System.arraycopy(name, nameOffset.getValue(), buf, thirdBuf + 1, fourthBuf);
//		}
//		call.setIntObjParam(bufLen + 1, lengthOffset, copy);
//		call.setIntObjParam(bufLen + 2, sizeOffset, copy);
//		call.setIntObjParam(bufLen + 3, typeOffset, copy);
//		call.setIntObjParam(bufLen + 4, nameOffset, copy);
//		glCallQueue.add(call);
//	}

//	public static String glGetActiveUniformImmediate(ENG_Integer program, 
//			ENG_Integer index,
//			
//			ENG_Integer[] size, ENG_Integer sizeOffset,
//			ENG_Integer[] type, ENG_Integer typeOffset) {
//		final int programParam = program.getValue();
//		final int indexParam = index.getValue();
////		final int bufsizeParam = bufsize.getValue();
////		final IntBuffer lengthParam = ENG_Utility.getIntArrayAsBuffer(length);
////		final int lengthOffsetParam = lengthOffset.getValue();
//		final IntBuffer sizeParam = ENG_Utility.getIntArrayAsBuffer(size);
//		final int sizeOffsetParam = sizeOffset.getValue();
//		final IntBuffer typeParam = ENG_Utility.getIntArrayAsBuffer(type);
//		final int typeOffsetParam = typeOffset.getValue();
////		final ByteBuffer nameParam = ENG_Utility.getByteAsPrimitiveArray(name);
////		final int nameOffsetParam = nameOffset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetActiveUniformImmediate");
//				_setStringRet(Gdx.gl20.glGetActiveUniform(programParam, indexParam,
////						bufsizeParam, lengthParam, lengthOffsetParam,
//						sizeParam, typeParam));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetActiveUniformImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
////		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(lengthParam, lengthOffsetParam, 
////				length, lengthOffsetParam);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(sizeParam, sizeOffsetParam,
//				size, sizeOffsetParam);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(typeParam, typeOffsetParam,
//				type, typeOffsetParam);
////		ENG_Utility.getBytePrimitiveArrayAsByteObjArray(nameParam, nameOffsetParam,
////				name, nameOffsetParam);
//	/*	byte[] bytes = event.getString().getBytes();
//		int len = name.length - nameOffset.getValue();
//		for (int i = 0; i < len; ++i) {
//			name[i + nameOffset.getValue()].setValue(bytes[i]);
//		}*/
//		return event.getString();
//	}

//	public static String glGetActiveUniformImmediate(final int program, 
//			final int index,
//			
//			final int[] size, final int sizeOffset,
//			final int[] type, final int typeOffset) {
////		final IntBuffer lengthParam = ENG_Utility.wrapBuffer(length);
////		final int lengthOffsetParam = lengthOffset.getValue();
//		final IntBuffer sizeParam = ENG_Utility.wrapBuffer(size);
////		final int sizeOffsetParam = sizeOffset.getValue();
//		final IntBuffer typeParam = ENG_Utility.wrapBuffer(type);
////		final int typeOffsetParam = typeOffset.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetActiveUniformImmediate");
////				Gdx.gl20.glGetActiveAttrib(program, index, bufsize, length, lengthOffset,
////						size, sizeOffset, type, typeOffset, name, nameOffset);
//				_setStringRet(Gdx.gl20.glGetActiveUniform(program, index,
////						bufsizeParam, lengthParam, lengthOffsetParam,
//						sizeParam, typeParam));
////				System.out.println(sizeParam.get(0));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetActiveUniformImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				sizeParam, 0, size, sizeOffset);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				typeParam, 0, type, typeOffset);
//	/*	byte[] bytes = event.getString().getBytes();
//		int len = name.length - nameOffset;
//		for (int i = 0; i < len; ++i) {
//			name[i + nameOffset] = bytes[i];
//		}*/
//		return event.getString();
//	}

//	public static void glGetAttachedShaders(ENG_Integer program, ENG_Integer maxcount,
//			ENG_Integer[] count, ENG_Integer countOffset,
//			ENG_Integer[] shaders, ENG_Integer shadersOffset) {
//		glGetAttachedShaders(program, maxcount, count, countOffset, shaders,
//				shadersOffset, true, true);
//	}
//	
//	private static void glGetAttachedShaders(ENG_Integer program, ENG_Integer maxcount,
//			ENG_Integer[] count, ENG_Integer countOffset,
//			ENG_Integer[] shaders, ENG_Integer shadersOffset, boolean copy,
//			boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetAttachedShadersArr);
//		int firstBuf = count.length - countOffset.getValue();
//		int secondBuf = shaders.length - shadersOffset.getValue();
//		
//		int bufLen = firstBuf + secondBuf;// + thirdBuf + fourthBuf;
//		call.setIntObjCount(bufLen + 4);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, maxcount, copy);
//		if (copyData) {
//			int i = 2;
//			for (; i < firstBuf + 2; ++i) {
//				call.setIntObjParam(i, count[countOffset.getValue() + i - 2], true);
//			}
//			int j = i;
//			for (; j < secondBuf + 2 + i; ++j) {
//				call.setIntObjParam(j, shaders[shadersOffset.getValue() + j - i - 2],
//						true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(count, countOffset.getValue(), buf, 1, firstBuf);
//			System.arraycopy(shaders, shadersOffset.getValue(), buf, firstBuf + 1, 
//					secondBuf);
//		}
//		call.setIntObjParam(bufLen + 1, countOffset, copy);
//		call.setIntObjParam(bufLen + 2, shadersOffset, copy);		
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetAttachedShadersImmediate(ENG_Integer program, 
//			ENG_Integer maxcount,
//			ENG_Integer[] count, ENG_Integer countOffset,
//			ENG_Integer[] shaders, ENG_Integer shadersOffset) {
//		final int programParam = program.getValue();
//		final int maxcountParam = maxcount.getValue();
//		final IntBuffer countParam = ENG_Utility.getIntArrayAsBuffer(count);
//		final int countOffsetParam = countOffset.getValue();
//		final IntBuffer shadersParam = ENG_Utility.getIntArrayAsBuffer(shaders);
//		final int shadersOffsetParam = shadersOffset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetAttachedShadersImmediate");
//				Gdx.gl20.glGetAttachedShaders(programParam, maxcountParam,
//						countParam, shadersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetAttachedShadersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(countParam, countOffsetParam,
//				count, countOffsetParam);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(shadersParam, shadersOffsetParam,
//				shaders, shadersOffsetParam);
//	}
//	
//	public static void glGetAttachedShadersImmediate(final int program, 
//			final int maxcount,
//			final int[] count, final int countOffset,
//			final int[] shaders, final int shadersOffset) {
//		final IntBuffer countParam = ENG_Utility.wrapBuffer(count);
//		final IntBuffer shadersParam = ENG_Utility.wrapBuffer(shaders);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetAttachedShadersImmediate");
//				Gdx.gl20.glGetAttachedShaders(program, maxcount,
//						countParam, shadersParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetAttachedShadersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				countParam, 0, count, countOffset);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				shadersParam, 0, shaders, shadersOffset);
//	}

//	public static void glGetAttachedShaders(int program, int maxcount,
//			IntBuffer count, IntBuffer shaders) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetAttachedShadersBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(2);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, maxcount);
//		call.setBuffer(0, count);
//		call.setBuffer(1, shaders);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetAttachedShaders(ENG_Integer program, ENG_Integer maxcount,
//			IntBuffer count, IntBuffer shaders) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetAttachedShadersBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(2);
//		call.setIntObjParam(0, program);
//		call.setIntObjParam(1, maxcount);
//		call.setBuffer(0, count);
//		call.setBuffer(1, shaders);
//		glCallQueue.add(call);
//	}

    public static void glGetAttachedShadersImmediate(final int program,
                                                     final int maxcount, final IntBuffer count, final IntBuffer shaders) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetAttachedShadersImmediate");
//				Gdx.gl20.glGetAttachedShaders(program, maxcount,
//						count, shaders);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetAttachedShadersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetAttachedShaders(program, maxcount, count, shaders);
    }

    public static void glGetAttachedShadersImmediate(final ENG_Integer program,
                                                     final ENG_Integer maxcount, final IntBuffer count,
                                                     final IntBuffer shaders) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetAttachedShadersImmediate");
//				Gdx.gl20.glGetAttachedShaders(program.getValue(), maxcount.getValue(),
//						count, shaders);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetAttachedShadersImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetAttachedShaders(program.getValue(), maxcount.getValue(),
                count, shaders);
    }

    public static int glGetAttribLocation(final int program, final String name) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetAttribLocation");
//				_setIntRet(Gdx.gl20.glGetAttribLocation(program, name));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetAttribLocation");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glGetAttribLocation(program, name);
    }

    public static int glGetAttribLocation(final ENG_Integer program,
                                          final String name) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//				eventQueuedRun("glGetAttribLocation");
//
//				_setIntRet(Gdx.gl20.glGetAttribLocation(program.getValue(), name));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetAttribLocation");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glGetAttribLocation(program.getValue(), name);
    }

//	public static void glGetBooleanv(ENG_Integer pname,
//			ENG_Boolean[] params, ENG_Integer offset) {
//		glGetBooleanv(pname, params, offset, true, true);
//	}
//	
//	private static void glGetBooleanv(ENG_Integer pname,
//			ENG_Boolean[] params, ENG_Integer offset, boolean copy,
//			boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetBooleanvArr);
//		int bufLen = params.length;
//		call.setIntObjCount(2);
//		call.setBooleanObjCount(bufLen);
//		call.setIntObjParam(0, pname, copy);
//		call.setIntObjParam(1, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setBooleanObjParam(i, params[i], true);
//			}
//		} else {
//			ENG_Boolean[] buf = call.getBooleanObjParam();
//			System.arraycopy(params, 0, buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetBooleanvImmediate(ENG_Integer pname,
//			ENG_Boolean[] params, ENG_Integer offset) {
//		final int pnameParam = pname.getValue();
//		final ByteBuffer paramsParam = ENG_Utility.getBooleanArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBooleanvImmediate");
//				Gdx.gl20.glGetBooleanv(pnameParam, paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBooleanvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getBooleanPrimitiveArrayAsBooleanObjArray(
//				paramsParam, offsetParam,
//				params, offsetParam);
//	}
//	
//	public static void glGetBooleanvImmediate(final int pname,
//			final boolean[] params, final int offset) {
//		final ByteBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBooleanvImmediate");
//				Gdx.gl20.glGetBooleanv(pname, buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBooleanvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getBooleanPrimitiveArrayAsBooleanObjArray(
//				buffer, 0, params, offset);
//	}
//	
//	public static void glGetBooleanv(int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetBooleanvBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetBooleanv(ENG_Integer pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetBooleanvBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetBooleanvImmediate(final int pname,
                                              final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBooleanvImmediate");
//				Gdx.gl20.glGetBooleanv(pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBooleanvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetBooleanv(pname, params);
    }

    public static void glGetBooleanvImmediate(final ENG_Integer pname,
                                              final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBooleanvImmediate");
//				Gdx.gl20.glGetBooleanv(pname.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBooleanvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetBooleanv(pname.getValue(), params);
    }

//	public static void glGetBufferParameteriv(int target, 
//			int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetBufferParameterivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetBufferParameteriv(ENG_Integer target, 
//			ENG_Integer pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetBufferParameterivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetBufferParameterivImmediate(final int target,
                                                       final int pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBufferParameterivImmediate");
//				Gdx.gl20.glGetBufferParameteriv(target, pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetBufferParameteriv(target, pname, params);
    }

    public static void glGetBufferParameterivImmediate(final ENG_Integer target,
                                                       final ENG_Integer pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBufferParameterivImmediate");
//				Gdx.gl20.glGetBufferParameteriv(target.getValue(), pname.getValue(),
//						params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetBufferParameteriv(target.getValue(), pname.getValue(), params);
    }

//	public static void glGetBufferParameteriv(ENG_Integer target, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {
//		glGetBufferParameteriv(target, pname, params, offset, true, true);
//	}
//	
//	public static void glGetBufferParameteriv(ENG_Integer target, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetBufferParameterivArr);
//		int bufLen = params.length;
//		call.setIntObjCount(bufLen + 3);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetBufferParameterivImmediate(ENG_Integer target, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {
//		final int targetParam = target.getValue();
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBufferParameterivImmediate");
//				Gdx.gl20.glGetBufferParameteriv(targetParam, pnameParam, 
//						paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam,
//				params, offsetParam);
//	}
//	
//	public static void glGetBufferParameterivImmediate(final int target, 
//			final int pname, final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetBufferParameterivImmediate");
//				Gdx.gl20.glGetBufferParameteriv(target, pname, 
//						buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetBufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(buffer, offset, params, offset);
//	}

    public static int glGetError() {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetError");
//				int err = Gdx.gl20.glGetError();
//				_setIntRet(err);
//				if (DEBUG) {
//					System.out.println("glGetError " + err);
//					if (ERRORS_FATAL && err != 0) {
//						throw new ENG_GLException(String.valueOf(err));
//					}
//				}
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetError");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glGetError();
    }

//	public static void glGetFloatv(int pname, FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetFloatvBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetFloatv(ENG_Integer pname, FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetFloatvBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetFloatvImmediate(final int pname,
                                            final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFloatvImmediate");
//				Gdx.gl20.glGetFloatv(pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFloatvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        FloatBuffer buffer = ENG_Utility.checkBufferLen(params);
        Gdx.gl20.glGetFloatv(pname, buffer);
    }

    public static void glGetFloatvImmediate(final ENG_Integer pname,
                                            final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFloatvImmediate");
//				Gdx.gl20.glGetFloatv(pname.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFloatvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        FloatBuffer buffer = ENG_Utility.checkBufferLen(params);
        Gdx.gl20.glGetFloatv(pname.getValue(), buffer);
    }

//	public static void glGetFloatv(ENG_Integer pname, 
//			ENG_Float[] params, ENG_Integer offset) {
//		glGetFloatv(pname, params, offset, true, true);
//	}
//	
//	private static void glGetFloatv(ENG_Integer pname, 
//			ENG_Float[] params, ENG_Integer offset, boolean copy, boolean copyData) {
//		
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetFloatvArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(2);
//		call.setFloatObjCount(1);
//		call.setIntObjParam(0, pname, copy);
//		call.setIntObjParam(1, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, params[offset.getValue() - i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetFloatvImmediate(ENG_Integer pname, 
//			ENG_Float[] params, ENG_Integer offset) {
//		final int pnameParam = pname.getValue();
//		final FloatBuffer paramsParam = ENG_Utility.getFloatArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFloatvImmediate");
//				Gdx.gl20.glGetFloatv(pnameParam, paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFloatvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(paramsParam, offsetParam, 
//				params, offsetParam);
//	}
//	
//	public static void glGetFloatvImmediate(final int pname, 
//			final float[] params, final int offset) {
//		final FloatBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFloatvImmediate");
//				Gdx.gl20.glGetFloatv(pname, buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFloatvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(
//				buffer, 0, params, offset);
//	}

//	public static void glGetFramebufferAttachmentParameteriv(int target,
//			int attachment, int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetFramebufferAttachmentParameterivBuf);
//		call.setIntArrayCount(3);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, attachment);
//		call.setIntArrayParam(2, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetFramebufferAttachmentParameteriv(ENG_Integer target,
//			ENG_Integer attachment, ENG_Integer pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetFramebufferAttachmentParameterivBuf);
//		call.setIntObjCount(3);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target);
//		call.setIntObjParam(1, attachment);
//		call.setIntObjParam(2, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetFramebufferAttachmentParameterivImmediate(final int target,
                                                                      final int attachment, final int pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFramebufferAttachmentParameterivImmediate");
//				Gdx.gl20.glGetFramebufferAttachmentParameteriv(target,
//						attachment, pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFramebufferAttachmentParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
    }

    public static void glGetFramebufferAttachmentParameterivImmediate(
            final ENG_Integer target, final ENG_Integer attachment,
            final ENG_Integer pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFramebufferAttachmentParameterivImmediate");
//				Gdx.gl20.glGetFramebufferAttachmentParameteriv(target.getValue(),
//						attachment.getValue(), pname.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFramebufferAttachmentParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetFramebufferAttachmentParameteriv(target.getValue(),
                attachment.getValue(), pname.getValue(), params);
    }

//	public static void glGetFramebufferAttachmentParameteriv(ENG_Integer target,
//			ENG_Integer attachment, ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset) {
//		glGetFramebufferAttachmentParameteriv(target, attachment, pname, params,
//				offset, true, true);
//	}
//	
//	private static void glGetFramebufferAttachmentParameteriv(ENG_Integer target,
//			ENG_Integer attachment, ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetFramebufferAttachmentParameterivArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 4);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, attachment, copy);
//		call.setIntObjParam(2, pname, copy);
//		call.setIntObjParam(3, offset, copy);
//		if (copyData) {
//			for (int i = 4; i < bufLen + 4; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 4]);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 4, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetFramebufferAttachmentParameterivImmediate(
//			ENG_Integer target, ENG_Integer attachment, ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset) {
//		final int targetParam = target.getValue();
//		final int attachmentParam = attachment.getValue();
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFramebufferAttachmentParameterivImmediate");
//				Gdx.gl20.glGetFramebufferAttachmentParameteriv(targetParam,
//						attachmentParam, pnameParam, paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFramebufferAttachmentParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam,
//				params, offsetParam);
//	}
//	
//	public static void glGetFramebufferAttachmentParameterivImmediate(
//			final int target, final int attachment, final int pname, 
//			final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetFramebufferAttachmentParameterivImmediate");
//				Gdx.gl20.glGetFramebufferAttachmentParameteriv(target,
//						attachment, pname, buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetFramebufferAttachmentParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}

//	public static void glGetIntegerv(ENG_Integer pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetIntegervBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetIntegerv(int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetIntegervBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetIntegervImmediate(final int pname,
                                              final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetIntegervImmediate");
//				Gdx.gl20.glGetIntegerv(pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetIntegervImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        IntBuffer buffer = ENG_Utility.checkBufferLen(params);
        Gdx.gl20.glGetIntegerv(pname, buffer);
    }

    public static void glGetIntegervImmediate(final ENG_Integer pname,
                                              final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetIntegervImmediate");
//				Gdx.gl20.glGetIntegerv(pname.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetIntegervImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        IntBuffer buffer = ENG_Utility.checkBufferLen(params);
        Gdx.gl20.glGetIntegerv(pname.getValue(), buffer);
    }

//	public static void glGetIntegerv(ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset) {
//		glGetIntegerv(pname, params, offset, true, true);
//	}
//	
//	private static void glGetIntegerv(ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetIntegervArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 2);
//		call.setIntObjParam(0, pname, copy);
//		call.setIntObjParam(1, offset, copy);
//		if (copyData) {
//			for (int i = 2; i < bufLen + 2; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 2], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 2, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetIntegervImmediate(ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset) {
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetIntegervImmediate");
//				Gdx.gl20.glGetIntegerv(pnameParam, paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetIntegervImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam, 
//				params, offsetParam);
//	}
//	
//	public static void glGetIntegervImmediate(final int pname, 
//			final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetIntegervImmediate");
//				Gdx.gl20.glGetIntegerv(pname, buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetIntegervImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}

    public static String glGetProgramInfoLog(final int program) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetProgramInfoLog");
//				_setStringRet(Gdx.gl20.glGetProgramInfoLog(program));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetProgramInfoLog");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getString();
        return Gdx.gl20.glGetProgramInfoLog(program);
    }

    public static String glGetProgramInfoLog(final ENG_Integer program) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetProgramInfoLog");
//				_setStringRet(Gdx.gl20.glGetProgramInfoLog(program.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetProgramInfoLog");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getString();
        return Gdx.gl20.glGetProgramInfoLog(program.getValue());
    }

//	public static void glGetProgramiv(ENG_Integer program, ENG_Integer pname,
//			IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetProgramivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, program);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetProgramiv(int program, int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetProgramivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetProgramivImmediate(final int program, final int pname,
                                               final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetProgramivImmediate");
//				Gdx.gl20.glGetProgramiv(program, pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetProgramivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetProgramiv(program, pname, params);
    }

    public static void glGetProgramivImmediate(final ENG_Integer program,
                                               final ENG_Integer pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetProgramivImmediate");
//				Gdx.gl20.glGetProgramiv(program.getValue(), pname.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetProgramivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetProgramiv(program.getValue(), pname.getValue(), params);
    }

//	public static void glGetProgramiv(ENG_Integer program,
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {		
//		glGetProgramiv(program, pname, params, offset, true, true);
//	}
//	
//	private static void glGetProgramiv(ENG_Integer program,
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {		
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetProgramivArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetProgramivImmediate(ENG_Integer program,
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {	
//		final int programParam = program.getValue();
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetProgramivImmediate");
//				Gdx.gl20.glGetProgramiv(programParam, pnameParam,
//						paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetProgramivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam, 
//				params, offsetParam);
//	}
//	
//	public static void glGetProgramivImmediate(final int program,
//			final int pname, final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetProgramivImmediate");
//				Gdx.gl20.glGetProgramiv(program, pname,
//						buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetProgramivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}

//	public static void glGetRenderbufferParameteriv(ENG_Integer target, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {
//		glGetRenderbufferParameteriv(target, pname, params, offset, true, true);
//	}
//	
//	private static void glGetRenderbufferParameteriv(ENG_Integer target, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset, 
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetRenderbufferParameterivArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetRenderbufferParameterivImmediate(ENG_Integer target, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {
//		final int targetParam = target.getValue();
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetRenderbufferParameterivImmediate");
//				Gdx.gl20.glGetRenderbufferParameteriv(targetParam, pnameParam,
//						paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetRenderbufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam, 
//				params, offsetParam);
//	}
//	
//	public static void glGetRenderbufferParameterivImmediate(final int target, 
//			final int pname, final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetRenderbufferParameterivImmediate");
//				Gdx.gl20.glGetRenderbufferParameteriv(target, pname,
//						buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetRenderbufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}
//	
//	public static void glGetRenderbufferParameteriv(ENG_Integer target, 
//			ENG_Integer pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetRenderbufferParameterivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetRenderbufferParameteriv(int target, 
//			int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetRenderbufferParameterivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetRenderbufferParameterivImmediate(final int target,
                                                             final int pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetRenderbufferParameterivImmediate");
//				Gdx.gl20.glGetRenderbufferParameteriv(target,
//						pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetRenderbufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetRenderbufferParameteriv(target, pname, params);
    }

    public static void glGetRenderbufferParameterivImmediate(final ENG_Integer target,
                                                             final ENG_Integer pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetRenderbufferParameterivImmediate");
//				Gdx.gl20.glGetRenderbufferParameteriv(target.getValue(),
//						pname.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetRenderbufferParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetRenderbufferParameteriv(target.getValue(), pname.getValue(),
                params);
    }

    public static String glGetShaderInfoLog(final int shader) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderInfoLog");
//				_setStringRet(Gdx.gl20.glGetShaderInfoLog(shader));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderInfoLog");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getString();
        return Gdx.gl20.glGetShaderInfoLog(shader);
    }

    public static String glGetShaderInfoLog(final ENG_Integer shader) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderInfoLog");
//				_setStringRet(Gdx.gl20.glGetShaderInfoLog(shader.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderInfoLog");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getString();
        return Gdx.gl20.glGetShaderInfoLog(shader.getValue());
    }

//	public static void glGetShaderPrecisionFormat(ENG_Integer shadertype, 
//			ENG_Integer precisiontype, IntBuffer range, IntBuffer precision) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetShaderPrecisionFormatBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(2);
//		call.setIntObjParam(0, shadertype);
//		call.setIntObjParam(1, precisiontype);
//		call.setBuffer(0, range);
//		call.setBuffer(1, precision);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetShaderPrecisionFormat(int shadertype, 
//			int precisiontype, IntBuffer range, IntBuffer precision) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetShaderPrecisionFormatBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(2);
//		call.setIntArrayParam(0, shadertype);
//		call.setIntArrayParam(1, precisiontype);
//		call.setBuffer(0, range);
//		call.setBuffer(1, precision);
//		glCallQueue.add(call);
//	}

    public static void glGetShaderPrecisionFormatImmediate(
            final ENG_Integer shadertype, final ENG_Integer precisiontype,
            final IntBuffer range, final IntBuffer precision) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderPrecisionFormatImmediate");
//				Gdx.gl20.glGetShaderPrecisionFormat(shadertype.getValue(), 
//						precisiontype.getValue(),
//						range, precision);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderPrecisionFormatImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetShaderPrecisionFormat(shadertype.getValue(),
                precisiontype.getValue(), range, precision);
    }

    public static void glGetShaderPrecisionFormatImmediate(final int shadertype,
                                                           final int precisiontype, final IntBuffer range, final IntBuffer precision) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderPrecisionFormatImmediate");
//				Gdx.gl20.glGetShaderPrecisionFormat(shadertype, precisiontype,
//						range, precision);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderPrecisionFormatImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
    }

//	public static void glGetShaderPrecisionFormat(ENG_Integer shadertype,
//			ENG_Integer precisiontype, ENG_Integer[] range, ENG_Integer rangeOffset,
//			ENG_Integer[] precision, ENG_Integer precisionOffset) {
//		glGetShaderPrecisionFormat(shadertype, precisiontype, range, rangeOffset,
//				precision, precisionOffset, true, true);
//	}
//	
//	private static void glGetShaderPrecisionFormat(ENG_Integer shadertype,
//			ENG_Integer precisiontype, ENG_Integer[] range, ENG_Integer rangeOffset,
//			ENG_Integer[] precision, ENG_Integer precisionOffset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetShaderPrecisionFormatArr);
//		int firstBuf = range.length - rangeOffset.getValue();
//		int secondBuf = precision.length - precisionOffset.getValue();
//		int bufLen = firstBuf + secondBuf;
//		call.setIntObjCount(bufLen + 4);
//		call.setIntObjParam(0, shadertype, copy);
//		call.setIntObjParam(1, precisiontype, copy);
//		call.setIntObjParam(2, rangeOffset, copy);
//		call.setIntObjParam(3, precisionOffset, copy);
//		final int sub = 4;
//		if (copyData) {
//			int i = sub;
//			for (; i < firstBuf + sub; ++i) {
//				call.setIntObjParam(i, range[rangeOffset.getValue() + i - sub], true);
//			}
//			int j = i;
//			for (; j < secondBuf + sub; ++j) {
//				call.setIntObjParam(j, 
//						precision[precisionOffset.getValue() + j - i - sub], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(range, rangeOffset.getValue(), buf, sub, firstBuf);
//			System.arraycopy(precision, precisionOffset.getValue(),
//					buf, firstBuf + sub, secondBuf);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetShaderPrecisionFormatImmediate(ENG_Integer shadertype,
//			ENG_Integer precisiontype, ENG_Integer[] range, ENG_Integer rangeOffset,
//			ENG_Integer[] precision, ENG_Integer precisionOffset) {
//		final int shadertypeParam = shadertype.getValue();
//		final int precisiontypeParam = precisiontype.getValue();
//		final IntBuffer rangeParam = ENG_Utility.getIntArrayAsBuffer(range);
//		final int rangeOffsetParam = rangeOffset.getValue();
//		final IntBuffer precisionParam = ENG_Utility.getIntArrayAsBuffer(precision);
//		final int precisionOffsetParam = precisionOffset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderPrecisionFormatImmediate");
//				Gdx.gl20.glGetShaderPrecisionFormat(shadertypeParam, precisiontypeParam,
//						rangeParam,  
//						precisionParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderPrecisionFormatImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(rangeParam, rangeOffsetParam, 
//				range, rangeOffsetParam);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(precisionParam, 
//				precisionOffsetParam, precision, precisionOffsetParam);
//	}
//	
//	public static void glGetShaderPrecisionFormatImmediate(final int shadertype,
//			final int precisiontype, final int[] range, final int rangeOffset,
//			final int[] precision, final int precisionOffset) {
//		final IntBuffer rangeBuf = ENG_Utility.wrapBuffer(range);
//		final IntBuffer precisionBuf = ENG_Utility.wrapBuffer(precision);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderPrecisionFormatImmediate");
//				Gdx.gl20.glGetShaderPrecisionFormat(shadertype, precisiontype,
//						rangeBuf, 
//						precisionBuf);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderPrecisionFormatImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				rangeBuf, rangeOffset, range, rangeOffset);
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				precisionBuf, precisionOffset, precision, precisionOffset);
//	}

//	public static void glGetShaderSource(ENG_Integer shader, ENG_Integer bufsize,
//			ENG_Integer[] length, ENG_Integer lengthOffset,
//			ENG_Integer[] source, ENG_Integer sourceOffset) {
//		glGetShaderSource(shader, bufsize, length, lengthOffset,
//				source, sourceOffset, true, true);
//	}
//	
//	private static void glGetShaderSource(ENG_Integer shader, ENG_Integer bufsize,
//			ENG_Integer[] length, ENG_Integer lengthOffset,
//			ENG_Integer[] source, ENG_Integer sourceOffset, 
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetShaderSourceArr);
//		int lengthLen = length.length - lengthOffset.getValue();
//		int sourceLen = source.length - sourceOffset.getValue();
//		int bufLen = lengthLen + sourceLen;
//		call.setIntObjCount(bufLen + 4);
//		call.setIntObjParam(0, shader, copy);
//		call.setIntObjParam(1, bufsize, copy);
//		call.setIntObjParam(2, lengthOffset, copy);
//		call.setIntObjParam(3, sourceOffset, copy);
//		final int sub = 4;
//		if (copyData) {
//			int i = sub;
//			for (; i < lengthLen + sub; ++i) {
//				call.setIntObjParam(i, length[lengthOffset.getValue() + i - sub], true);
//			}
//			int j = i;
//			for (; j < sourceLen + sub; ++j) {
//				call.setIntObjParam(j, 
//						source[sourceOffset.getValue() + j - i - sub], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(length, lengthOffset.getValue(), buf, sub, lengthLen);
//			System.arraycopy(source, sourceOffset.getValue(),
//					buf, lengthLen + sub, sourceLen);
//		}
//		glCallQueue.add(call);
//	}
	
/*	public static void glGetShaderSourceImmediate(ENG_Integer shader, 
			ENG_Integer bufsize,
			ENG_Integer[] length, ENG_Integer lengthOffset,
			ENG_Integer[] source, ENG_Integer sourceOffset) {
		final int shaderParam = shader.getValue();
		final int bufsizeParam = bufsize.getValue();
		final IntBuffer lengthParam = ENG_Utility.getIntArrayAsBuffer(length);
		final int lengthOffsetParam = lengthOffset.getValue();
		final byte[] sourceParam = new byte[source.length];
		for (int i = 0; i < source.length; ++i) {
			sourceParam[i] = (byte) source[i].getValue();
		}
		final ByteBuffer sourceParamBuf = ENG_Utility.wrapBuffer(sourceParam);
		final int sourceOffsetParam = sourceOffset.getValue();
		
		GLRunnableEvent event = new GLRunnableEvent() {

			@Override
			public void run() {

				eventQueuedRun("glGetShaderSourceImmediate");
				Gdx.gl20.glGetShaderSource(shaderParam, bufsizeParam,
						lengthParam, 
						sourceParamBuf);
				_getSemaphore().release();
			}
			
		};
		eventQueued("glGetShaderSourceImmediate");
		GLRenderSurface.getSingleton().queueEvent(event);
		GLRenderSurface.getSingleton().requestRender();
		event.checkSemaphoreReleased();
		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(lengthParam, lengthOffsetParam,
				length, lengthOffsetParam);
		for (int i = 0; i < source.length; ++i) {
			source[sourceOffsetParam + i].setValue(sourceParam[sourceOffsetParam + i]);
		}
	}
	
	public static void glGetShaderSourceImmediate(final int shader, 
			final int bufsize,
			final int[] length, final int lengthOffset,
			final byte[] source, final int sourceOffset) {
		GLRunnableEvent event = new GLRunnableEvent() {

			@Override
			public void run() {

				eventQueuedRun("glGetShaderSourceImmediate");
				Gdx.gl20.glGetShaderSource(shader, bufsize,
						length, lengthOffset, 
						source, sourceOffset);
				_getSemaphore().release();
			}
			
		};
		eventQueued("glGetShaderSourceImmediate");
		GLRenderSurface.getSingleton().queueEvent(event);
		GLRenderSurface.getSingleton().requestRender();
		event.checkSemaphoreReleased();
	}
	
	public static void glGetShaderSource(int shader, int bufsize,
			IntBuffer length, byte source) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetShaderSourceBuf);
		call.setIntArrayCount(3);
		call.setBufferCount(1);
		call.setIntArrayParam(0, shader);
		call.setIntArrayParam(1, bufsize);
		call.setIntArrayParam(2, source);
		call.setBuffer(0, length);
		glCallQueue.add(call);
	}
	
	public static void glGetShaderSource(ENG_Integer shader, ENG_Integer bufsize,
			IntBuffer length, ENG_Integer source) {
		GLESCall call = new GLESCall();
		call.setCall(GLCallList.glGetShaderSourceBuf);
		call.setIntObjCount(3);
		call.setBufferCount(1);
		call.setIntObjParam(0, shader);
		call.setIntObjParam(1, bufsize);
		call.setIntObjParam(2, source);
		call.setBuffer(0, length);
		glCallQueue.add(call);
	}
	
	public static void glGetShaderSourceImmediate(final int shader, final int bufsize,
			final IntBuffer length, final byte source) {
		GLRunnableEvent event = new GLRunnableEvent() {

			@Override
			public void run() {

				eventQueuedRun("glGetShaderSourceImmediate");
				Gdx.gl20.glGetShaderSource(shader, bufsize,
						length, source);
				_getSemaphore().release();
			}
			
		};
		eventQueued("glGetShaderSourceImmediate");
		GLRenderSurface.getSingleton().queueEvent(event);
		GLRenderSurface.getSingleton().requestRender();
		event.checkSemaphoreReleased();
	}
	
	public static void glGetShaderSourceImmediate(final ENG_Integer shader, 
			final ENG_Integer bufsize,
			final IntBuffer length, final ENG_Integer source) {
		GLRunnableEvent event = new GLRunnableEvent() {

			@Override
			public void run() {

				eventQueuedRun("glGetShaderSourceImmediate");
				Gdx.gl20.glGetShaderSource(shader.getValue(), bufsize.getValue(),
						length, (byte) source.getValue());
				_getSemaphore().release();
			}
			
		};
		eventQueued("glGetShaderSourceImmediate");
		GLRenderSurface.getSingleton().queueEvent(event);
		GLRenderSurface.getSingleton().requestRender();
		event.checkSemaphoreReleased();
	}*/

//	public static void glGetShaderiv(ENG_Integer shader, ENG_Integer pname,
//			IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetShaderivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, shader);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetShaderiv(int shader, int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetShaderivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, shader);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetShaderivImmediate(final int shader, final int pname,
                                              final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderivImmediate");
//				Gdx.gl20.glGetShaderiv(shader, pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetShaderiv(shader, pname, params);
    }

    public static void glGetShaderivImmediate(final ENG_Integer shader,
                                              final ENG_Integer pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderivImmediate");
//				Gdx.gl20.glGetShaderiv(shader.getValue(), pname.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetShaderiv(shader.getValue(), pname.getValue(), params);
    }

//	public static void glGetShaderiv(ENG_Integer shader, ENG_Integer pname,
//			ENG_Integer[] params, ENG_Integer offset) {
//		glGetShaderiv(shader, pname, params, offset, true, true);
//	}
//	
//	private static void glGetShaderiv(ENG_Integer shader, ENG_Integer pname,
//			ENG_Integer[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetShaderivArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		call.setIntObjParam(0, shader, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetShaderivImmediate(ENG_Integer shader, ENG_Integer pname,
//			ENG_Integer[] params, ENG_Integer offset) {
//		final int shaderParam = shader.getValue();
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderivImmediate");
//				Gdx.gl20.glGetShaderiv(shaderParam, pnameParam, paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam,
//				params, offsetParam);
//	}
//	
//	public static void glGetShaderivImmediate(final int shader, final int pname,
//			final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetShaderivImmediate");
//				Gdx.gl20.glGetShaderiv(shader, pname, buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetShaderivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}

    public static String glGetString(final int name) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetString");
//				String ret = Gdx.gl20.glGetString(name);
//				if (DEBUG) {
//					System.out.println("glGetString with param " + name + " returns " + ret);
//				}
//				_setStringRet(ret);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetString");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getString();
        return Gdx.gl20.glGetString(name);
    }

    public static String glGetString(final ENG_Integer name) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetString");
//				_setStringRet(Gdx.gl20.glGetString(name.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetString");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getString();
        return Gdx.gl20.glGetString(name.getValue());
    }

//	public static void glGetTexParameterfv(ENG_Integer target, ENG_Integer pname,
//			ENG_Float[] params, ENG_Integer offset) {
//		glGetTexParameterfv(target, pname, params, offset, true, true);
//	}
//	
//	private static void glGetTexParameterfv(ENG_Integer target, ENG_Integer pname,
//			ENG_Float[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetTexParameterfvArr);
//		call.setIntObjCount(3);
//		int bufLen = params.length - offset.getValue();
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, params[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetTexParameterfvImmediate(ENG_Integer target, 
//			ENG_Integer pname, ENG_Float[] params, ENG_Integer offset) {
//		final int targetParam = target.getValue();
//		final int pnameParam = pname.getValue();
//		final FloatBuffer paramsParam = ENG_Utility.getFloatArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterfvImmediate");
//				Gdx.gl20.glGetTexParameterfv(targetParam, pnameParam, paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(paramsParam, offsetParam,
//				params, offsetParam);
//	}
//	
//	public static void glGetTexParameterfvImmediate(final int target, 
//			final int pname, final float[] params, final int offset) {
//		final FloatBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterfvImmediate");
//				Gdx.gl20.glGetTexParameterfv(target, pname, buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(
//				buffer, offset, params, offset);
//	}
//	
//	public static void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetTexParameterfvBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetTexParameterfv(ENG_Integer target, ENG_Integer pname,
//			FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetTexParameterfvBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetTexParameterfvImmediate(final int target,
                                                    final int pname, final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterfvImmediate");
//				Gdx.gl20.glGetTexParameterfv(target, pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetTexParameterfv(target, pname, params);
    }

    public static void glGetTexParameterfvImmediate(final ENG_Integer target,
                                                    final ENG_Integer pname, final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterfvImmediate");
//				Gdx.gl20.glGetTexParameterfv(target.getValue(), pname.getValue(),
//						params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetTexParameterfv(target.getValue(), pname.getValue(), params);
    }

//	public static void glGetTexParameteriv(ENG_Integer target, ENG_Integer pname,
//			ENG_Integer[] params, ENG_Integer offset) {
//		glGetTexParameteriv(target, pname, params, offset, true, true);
//	}
//	
//	private static void glGetTexParameteriv(ENG_Integer target, ENG_Integer pname,
//			ENG_Integer[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetTexParameterivArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		
//		//call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetTexParameterivImmediate(ENG_Integer target, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {
//		final int targetParam = target.getValue();
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterivImmediate");
//				Gdx.gl20.glGetTexParameteriv(targetParam, pnameParam, paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam,
//				params, offsetParam);
//	}
//	
//	public static void glGetTexParameterivImmediate(final int target, 
//			final int pname, final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterivImmediate");
//				Gdx.gl20.glGetTexParameteriv(target, pname, buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}
//	
//	public static void glGetTexParameteriv(int target, int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetTexParameterivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetTexParameterfv(ENG_Integer target, ENG_Integer pname,
//			IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetTexParameterivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetTexParameterivImmediate(final int target,
                                                    final int pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterivImmediate");
//				Gdx.gl20.glGetTexParameteriv(target, pname, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetTexParameteriv(target, pname, params);
    }

    public static void glGetTexParameterivImmediate(final ENG_Integer target,
                                                    final ENG_Integer pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetTexParameterivImmediate");
//				Gdx.gl20.glGetTexParameteriv(target.getValue(), pname.getValue(),
//						params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetTexParameterivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetTexParameteriv(target.getValue(), pname.getValue(), params);
    }

    public static int glGetUniformLocation(final int program, final String name) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformLocation");
//				_setIntRet(Gdx.gl20.glGetUniformLocation(program, name));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformLocation");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glGetUniformLocation(program, name);
    }

    public static int glGetUniformLocation(final ENG_Integer program,
                                           final String name) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformLocation");
//				_setIntRet(Gdx.gl20.glGetUniformLocation(program.getValue(), name));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformLocation");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getInt();
        return Gdx.gl20.glGetUniformLocation(program.getValue(), name);
    }

//	public static void glGetUniformfv(int program, int location, FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetUniformfvFBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, location);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetUniformfv(ENG_Integer program, ENG_Integer location,
//			FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetUniformfvFBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, program);
//		call.setIntObjParam(1, location);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetUniformfvImmediate(final int program, final int location,
                                               final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformfvImmediate");
//				Gdx.gl20.glGetUniformfv(program, location, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetUniformfv(program, location, params);
    }

    public static void glGetUniformfvImmediate(final ENG_Integer program,
                                               final ENG_Integer location, final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformfvImmediate");
//				Gdx.gl20.glGetUniformfv(program.getValue(), location.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetUniformfv(program.getValue(), location.getValue(), params);
    }

//	public static void glGetUniformfv(ENG_Integer program, ENG_Integer location,
//			ENG_Float[] params, ENG_Integer offset) {
//		glGetUniformfv(program, location, params, offset, true, true);
//	}
//	
//	private static void glGetUniformfv(ENG_Integer program, ENG_Integer location,
//			ENG_Float[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetUniformfvFArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setFloatArrayCount(bufLen);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, location, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, params[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetUniformfvImmediate(ENG_Integer program, 
//			ENG_Integer location, ENG_Float[] params, ENG_Integer offset) {
//		final int programParam = program.getValue();
//		final int locationParam = location.getValue();
//		final FloatBuffer paramsParam = ENG_Utility.getFloatArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformfvImmediate");
//				Gdx.gl20.glGetUniformfv(programParam, locationParam, 
//						paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(paramsParam, offsetParam, 
//				params, offsetParam);
//	}
//	
//	public static void glGetUniformfvImmediate(final int program, 
//			final int location, final float[] params, final int offset) {
//		final FloatBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformfvImmediate");
//				Gdx.gl20.glGetUniformfv(program, location, 
//						buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(
//				buffer, offset, params, offset);
//	}
//	
//	public static void glGetUniformiv(int program, int location, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetUniformivIBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, program);
//		call.setIntArrayParam(1, location);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetUniformiv(ENG_Integer program, ENG_Integer location,
//			IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetUniformivIBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, program);
//		call.setIntObjParam(1, location);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetUniformivImmediate(final int program, final int location,
                                               final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformivImmediate");
//				Gdx.gl20.glGetUniformiv(program, location, params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetUniformiv(program, location, params);
    }

    public static void glGetUniformivImmediate(final ENG_Integer program,
                                               final ENG_Integer location, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformivImmediate");
//				Gdx.gl20.glGetUniformiv(program.getValue(), location.getValue(), params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetUniformiv(program.getValue(), location.getValue(), params);
    }

//	public static void glGetUniformiv(ENG_Integer program, ENG_Integer location,
//			ENG_Integer[] params, ENG_Integer offset) {
//		glGetUniformiv(program, location, params, offset, true, true);
//	}
//	
//	private static void glGetUniformiv(ENG_Integer program, ENG_Integer location,
//			ENG_Integer[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetUniformivIArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		//call.setFloatArrayCount(bufLen);
//		call.setIntObjParam(0, program, copy);
//		call.setIntObjParam(1, location, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetUniformivImmediate(ENG_Integer program, 
//			ENG_Integer location, ENG_Integer[] params, ENG_Integer offset) {
//		final int programParam = program.getValue();
//		final int locationParam = location.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformivImmediate");
//				Gdx.gl20.glGetUniformiv(programParam, locationParam, 
//						paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam, 
//				params, offsetParam);
//	}
//	
//	public static void glGetUniformivImmediate(final int program, 
//			final int location, final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetUniformivImmediate");
//				Gdx.gl20.glGetUniformiv(program, location, 
//						buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetUniformivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}

//	public static void glGetVertexAttribfv(ENG_Integer index, ENG_Integer pname,
//			ENG_Float[] params, ENG_Integer offset) {
//		glGetVertexAttribfv(index, pname, params, offset, true, true);
//	}
//	
//	private static void glGetVertexAttribfv(ENG_Integer index, ENG_Integer pname,
//			ENG_Float[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetVertexAttribfvFArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setFloatArrayCount(bufLen);
//		call.setIntObjParam(0, index, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, params[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetVertexAttribfvImmediate(ENG_Integer index, 
//			ENG_Integer pname, ENG_Float[] params, ENG_Integer offset) {
//		final int indexParam = index.getValue();
//		final int pnameParam = pname.getValue();
//		final FloatBuffer paramsParam = ENG_Utility.getFloatArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribfvImmediate");
//				Gdx.gl20.glGetVertexAttribfv(indexParam, pnameParam,
//						paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(paramsParam, offsetParam,
//				params, offsetParam);
//	}
//	
//	public static void glGetVertexAttribfvImmediate(final int index, 
//			final int pname, final float[] params, final int offset) {
//		final FloatBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribfvImmediate");
//				Gdx.gl20.glGetVertexAttribfv(index, pname,
//						buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getFloatPrimitiveArrayAsFloatObjArray(
//				buffer, offset, params, offset);
//	}
//	
//	public static void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetVertexAttribfvFBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, index);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetVertexAttribfv(ENG_Integer index, ENG_Integer pname,
//			FloatBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetVertexAttribfvFBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, index);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetVertexAttribfvImmediate(final int index,
                                                    final int pname, final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribfvImmediate");
//				Gdx.gl20.glGetVertexAttribfv(index, pname,
//						params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetVertexAttribfv(index, pname, params);
    }

    public static void glGetVertexAttribfvImmediate(final ENG_Integer index,
                                                    final ENG_Integer pname, final FloatBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribfvImmediate");
//				Gdx.gl20.glGetVertexAttribfv(index.getValue(), pname.getValue(),
//						params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribfvImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetVertexAttribfv(index.getValue(), pname.getValue(), params);
    }

//	public static void glGetVertexAttribiv(ENG_Integer index, ENG_Integer pname,
//			ENG_Integer[] params, ENG_Integer offset) {
//		glGetVertexAttribiv(index, pname, params, offset, true, true);
//	}
//	
//	private static void glGetVertexAttribiv(ENG_Integer index, ENG_Integer pname,
//			ENG_Integer[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetVertexAttribivIArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		//call.setFloatArrayCount(bufLen);
//		call.setIntObjParam(0, index, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetVertexAttribivImmediate(ENG_Integer index, 
//			ENG_Integer pname, ENG_Integer[] params, ENG_Integer offset) {
//		final int indexParam = index.getValue();
//		final int pnameParam = pname.getValue();
//		final IntBuffer paramsParam = ENG_Utility.getIntArrayAsBuffer(params);
//		final int offsetParam = offset.getValue();
//		
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribivImmediate");
//				Gdx.gl20.glGetVertexAttribiv(indexParam, pnameParam,
//						paramsParam);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(paramsParam, offsetParam, 
//				params, offsetParam);
//	}
//	
//	public static void glGetVertexAttribivImmediate(final int index, 
//			final int pname, final int[] params, final int offset) {
//		final IntBuffer buffer = ENG_Utility.wrapBuffer(params);
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribivImmediate");
//				Gdx.gl20.glGetVertexAttribiv(index, pname,
//						buffer);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
//		ENG_Utility.getIntPrimitiveArrayAsIntObjArray(
//				buffer, offset, params, offset);
//	}
//	
//	public static void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetVertexAttribivIBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, index);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}
//	
//	public static void glGetVertexAttribiv(ENG_Integer index, ENG_Integer pname,
//			IntBuffer params) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glGetVertexAttribivIBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, index);
//		call.setIntObjParam(1, pname);
//		call.setBuffer(0, params);
//		glCallQueue.add(call);
//	}

    public static void glGetVertexAttribivImmediate(final int index,
                                                    final int pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribivImmediate");
//				Gdx.gl20.glGetVertexAttribiv(index, pname,
//						params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetVertexAttribiv(index, pname, params);
    }

    public static void glGetVertexAttribivImmediate(final ENG_Integer index,
                                                    final ENG_Integer pname, final IntBuffer params) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glGetVertexAttribivImmediate");
//				Gdx.gl20.glGetVertexAttribiv(index.getValue(), pname.getValue(),
//						params);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glGetVertexAttribivImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glGetVertexAttribiv(index.getValue(), pname.getValue(), params);
    }

    public static void glHint(int target, int mode) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glHint);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, mode);
//		glCallQueue.add(call);
        Gdx.gl20.glHint(target, mode);
    }

    public static void glHint(ENG_Integer target, ENG_Integer mode) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glHint);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, target);
//		call.setIntObjParam(1, mode);
//		glCallQueue.add(call);
        Gdx.gl20.glHint(target.getValue(), mode.getValue());
    }

    public static boolean glIsBuffer(final int buffer) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsBuffer");
//				_setBooleanRet(Gdx.gl20.glIsBuffer(buffer));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsBuffer");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsBuffer(buffer);
    }

    public static boolean glIsBuffer(final ENG_Integer buffer) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsBuffer");
//				_setBooleanRet(Gdx.gl20.glIsBuffer(buffer.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsBuffer");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsBuffer(buffer.getValue());
    }

    public static boolean glIsEnabled(final int cap) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsEnabled");
//				_setBooleanRet(Gdx.gl20.glIsEnabled(cap));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsEnabled");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsEnabled(cap);
    }

    public static boolean glIsEnabled(final ENG_Integer cap) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsEnabled");
//				_setBooleanRet(Gdx.gl20.glIsEnabled(cap.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsEnabled");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsEnabled(cap.getValue());
    }

    public static boolean glIsFramebuffer(final int framebuffer) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsFramebuffer");
//				_setBooleanRet(Gdx.gl20.glIsFramebuffer(framebuffer));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsFramebuffer");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsFramebuffer(framebuffer);
    }

    public static boolean glIsFramebuffer(final ENG_Integer framebuffer) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsFramebuffer");
//				_setBooleanRet(Gdx.gl20.glIsFramebuffer(framebuffer.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsFramebuffer");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsFramebuffer(framebuffer.getValue());
    }

    public static boolean glIsProgram(final int program) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsProgram");
//				_setBooleanRet(Gdx.gl20.glIsProgram(program));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsProgram");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsProgram(program);
    }

    public static boolean glIsProgram(final ENG_Integer program) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsProgram");
//				_setBooleanRet(Gdx.gl20.glIsProgram(program.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsProgram");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsProgram(program.getValue());
    }

    public static boolean glIsRenderbuffer(final int renderbuffer) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsRenderbuffer");
//				_setBooleanRet(Gdx.gl20.glIsRenderbuffer(renderbuffer));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsRenderbuffer");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsRenderbuffer(renderbuffer);
    }

    public static boolean glIsRenderbuffer(final ENG_Integer renderbuffer) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsRenderbuffer");
//				_setBooleanRet(Gdx.gl20.glIsRenderbuffer(renderbuffer.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsRenderbuffer");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsRenderbuffer(renderbuffer.getValue());
    }

    public static boolean glIsShader(final int shader) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsShader");
//				_setBooleanRet(Gdx.gl20.glIsShader(shader));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsShader");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsShader(shader);
    }

    public static boolean glIsShader(final ENG_Integer shader) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsShader");
//				_setBooleanRet(Gdx.gl20.glIsShader(shader.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsShader");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsShader(shader.getValue());
    }

    public static boolean glIsTexture(final int texture) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsTexture");
//				_setBooleanRet(Gdx.gl20.glIsTexture(texture));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsTexture");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsTexture(texture);
    }

    public static boolean glIsTexture(final ENG_Integer texture) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glIsTexture");
//				_setBooleanRet(Gdx.gl20.glIsTexture(texture.getValue()));
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glIsTexture");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		return event.getBoolean();
        return Gdx.gl20.glIsTexture(texture.getValue());
    }

    public static void glLineWidth(final float width) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glLineWidth);
//		call.setFloatArrayCount(1);
//		call.setFloatArrayParam(0, width);
//		glCallQueue.add(call);
        Gdx.gl20.glLineWidth(width);
    }

    public static void glLineWidth(final ENG_Float width) {
        glLineWidth(width, true);
    }

    public static void glLineWidth(final ENG_Float width, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glLineWidth);
//		call.setFloatObjCount(1);
//		call.setFloatObjParam(0, width, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glLineWidth(width.getValue());
    }

//	public static void glLinkProgram(final int program) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glLinkProgram);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, program);
//		glCallQueue.add(call);
//	}

    public static void glLinkProgramImmediate(final int program) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glLinkProgramImmediate");
//				Gdx.gl20.glLinkProgram(program);
//				if (DEBUG) {
//					int err = Gdx.gl20.glGetError();
////					String msg = GLU.gluErrorString(err);
//					System.out.println("glLinkProgramImmediate " + err);
//				}
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glLinkProgramImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glLinkProgram(program);
    }

//	public static void glLinkProgram(final ENG_Integer program) {
//		glLinkProgram(program, true);
//	}
//	
//	public static void glLinkProgram(final ENG_Integer program, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glLinkProgram);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, program, copy);
//		glCallQueue.add(call);
//	}

    public static void glLinkProgramImmediate(ENG_Integer program) {
//		final int programInt = program.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glLinkProgramImmediate");
//				Gdx.gl20.glLinkProgram(programInt);
//				if (DEBUG) {
//					int err = Gdx.gl20.glGetError();
////					String msg = GLU.gluErrorString(err);
//					System.out.println("glLinkProgramImmediate " + err);
//				}
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glLinkProgramImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glLinkProgram(program.getValue());
    }

    public static void glPixelStorei(final int pname, final int param) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glPixelStorei);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, pname);
//		call.setIntArrayParam(1, param);
//		glCallQueue.add(call);
        Gdx.gl20.glPixelStorei(pname, param);
    }

    public static void glPixelStorei(final ENG_Integer pname, final ENG_Integer param) {
        glPixelStorei(pname, param, true);
    }

    public static void glPixelStorei(final ENG_Integer pname, final ENG_Integer param,
                                     boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glPixelStorei);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, pname, copy);
//		call.setIntObjParam(1, param, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glPixelStorei(pname.getValue(), param.getValue());
    }

    public static void glPolygonOffset(float factor, float units) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glPolygonOffset);
//		call.setFloatArrayCount(2);
//		call.setFloatArrayParam(0, factor);
//		call.setFloatArrayParam(1, units);
//		glCallQueue.add(call);
        Gdx.gl20.glPolygonOffset(factor, units);
    }

    public static void glPolygonOffset(ENG_Float factor, ENG_Float units) {
        glPolygonOffset(factor, units, true);
    }

    public static void glPolygonOffset(ENG_Float factor, ENG_Float units,
                                       boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glPolygonOffset);
//		call.setFloatObjCount(2);
//		call.setFloatObjParam(0, factor, copy);
//		call.setFloatObjParam(1, units, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glPolygonOffset(factor.getValue(), units.getValue());
    }

//	public static void glReadPixels(int x, int y,
//			int width, int height, int format, int type, Buffer pixels) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glReadPixels);
//		call.setIntArrayCount(6);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, x);
//		call.setIntArrayParam(1, y);
//		call.setIntArrayParam(2, width);
//		call.setIntArrayParam(3, height);
//		call.setIntArrayParam(4, format);
//		call.setIntArrayParam(5, type);
//		call.setBuffer(0, pixels);
//		glCallQueue.add(call);
//	}
//	
//	public static void glReadPixels(ENG_Integer x, ENG_Integer y,
//			ENG_Integer width, ENG_Integer height, 
//			ENG_Integer format, ENG_Integer type, Buffer pixels) {
//		glReadPixels(x, y, width, height, format, type, pixels, true);
//	}
//	
//	public static void glReadPixels(ENG_Integer x, ENG_Integer y,
//			ENG_Integer width, ENG_Integer height, 
//			ENG_Integer format, ENG_Integer type, Buffer pixels, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glReadPixels);
//		call.setIntObjCount(6);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, x, copy);
//		call.setIntObjParam(1, y, copy);
//		call.setIntObjParam(2, width, copy);
//		call.setIntObjParam(3, height, copy);
//		call.setIntObjParam(4, format, copy);
//		call.setIntObjParam(5, type, copy);
//		call.setBuffer(0, pixels, copy);
//		glCallQueue.add(call);
//	}

    public static void glReadPixelsImmediate(final ENG_Integer x, final ENG_Integer y,
                                             final ENG_Integer width, final ENG_Integer height,
                                             final ENG_Integer format, final ENG_Integer type, final Buffer pixels) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glReadPixelsImmediate");
//				Gdx.gl20.glReadPixels(x.getValue(), y.getValue(),
//						width.getValue(), height.getValue(), format.getValue(),
//						type.getValue(), pixels);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glReadPixelsImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glReadPixels(x.getValue(), y.getValue(),
                width.getValue(), height.getValue(),
                format.getValue(), type.getValue(), pixels);
    }

    public static void glReadPixelsImmediate(final int x, final int y,
                                             final int width, final int height,
                                             final int format, final int type, final Buffer pixels) {
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glReadPixelsImmediate");
//				Gdx.gl20.glReadPixels(x, y,
//						width, height, format,
//						type, pixels);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glReadPixelsImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glReadPixels(x, y, width, height, format, type, pixels);
    }

    public static void glReleaseShaderCompiler() {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glReleaseShaderCompiler);
//		glCallQueue.add(call);
        Gdx.gl20.glReleaseShaderCompiler();
    }

    public static void glRenderbufferStorage(int target,
                                             int internalformat, int width, int height) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glRenderbufferStorage);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, internalformat);
//		call.setIntArrayParam(2, width);
//		call.setIntArrayParam(3, height);
//		glCallQueue.add(call);
        Gdx.gl20.glRenderbufferStorage(target, internalformat, width, height);
    }

    public static void glRenderbufferStorage(ENG_Integer target,
                                             ENG_Integer internalformat, ENG_Integer width, ENG_Integer height) {
        glRenderbufferStorage(target, internalformat, width, height, true);
    }

    public static void glRenderbufferStorage(ENG_Integer target,
                                             ENG_Integer internalformat, ENG_Integer width, ENG_Integer height,
                                             boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glRenderbufferStorage);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, internalformat, copy);
//		call.setIntObjParam(2, width, copy);
//		call.setIntObjParam(3, height, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glRenderbufferStorage(target.getValue(), internalformat.getValue(),
                width.getValue(), height.getValue());
    }

    public static void glSampleCoverage(float value, boolean invert) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glSampleCoverage);
//		call.setFloatArrayCount(1);
//		call.setBooleanArrayCount(1);
//		call.setFloatArrayParam(0, value);
//		call.setBooleanArrayParam(0, invert);
//		glCallQueue.add(call);
        Gdx.gl20.glSampleCoverage(value, invert);
    }

    public static void glSampleCoverage(ENG_Float value, ENG_Boolean invert) {
        glSampleCoverage(value, invert, true);
    }

    public static void glSampleCoverage(ENG_Float value, ENG_Boolean invert,
                                        boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glSampleCoverage);
//		call.setFloatObjCount(1);
//		call.setBooleanObjCount(1);
//		call.setFloatObjParam(0, value, copy);
//		call.setBooleanObjParam(0, invert, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glSampleCoverage(value.getValue(), invert.getValue());
    }

    public static void glScissor(int x, int y, int width, int height) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glScissor);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, x);
//		call.setIntArrayParam(1, y);
//		call.setIntArrayParam(2, width);
//		call.setIntArrayParam(3, height);
//		glCallQueue.add(call);
        Gdx.gl20.glScissor(x, y, width, height);
    }

    public static void glScissor(ENG_Integer x, ENG_Integer y,
                                 ENG_Integer width, ENG_Integer height) {
        glScissor(x, y, width, height, true);
    }

    public static void glScissor(ENG_Integer x, ENG_Integer y,
                                 ENG_Integer width, ENG_Integer height,
                                 boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glScissor);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, x, copy);
//		call.setIntObjParam(1, y, copy);
//		call.setIntObjParam(2, width, copy);
//		call.setIntObjParam(3, height, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glScissor(x.getValue(), y.getValue(),
                width.getValue(), height.getValue());
    }

    public static void glShaderBinary(int n, IntBuffer shaders,
                                      int binaryformat, Buffer binary, int length) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glShaderBinaryBuf);
//		call.setIntArrayCount(3);
//		call.setBufferCount(2);
//		call.setIntArrayParam(0, n);
//		call.setIntArrayParam(1, binaryformat);
//		call.setIntArrayParam(2, length);
//		call.setBuffer(0, shaders);
//		call.setBuffer(1, binary);
//		glCallQueue.add(call);
        Gdx.gl20.glShaderBinary(n, shaders, binaryformat, binary, length);
    }

    public static void glShaderBinary(ENG_Integer n, IntBuffer shaders,
                                      ENG_Integer binaryformat, Buffer binary, ENG_Integer length) {
        glShaderBinary(n, shaders, binaryformat, binary, length, true, true);
    }

    public static void glShaderBinary(ENG_Integer n, IntBuffer shaders,
                                      ENG_Integer binaryformat, Buffer binary, ENG_Integer length,
                                      boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glShaderBinaryBuf);
//		call.setIntObjCount(3);
//		call.setBufferCount(2);
//		call.setIntObjParam(0, n, copy);
//		call.setIntObjParam(1, binaryformat, copy);
//		call.setIntObjParam(2, length, copy);
//		call.setBuffer(0, shaders, copyData);
//		call.setBuffer(1, binary, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glShaderBinary(n.getValue(), shaders, binaryformat.getValue(),
                binary, length.getValue());
    }

//	public static void glShaderBinary(int n, int[] shaders, int offset,
//			int binaryformat, Buffer binary, int length) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glShaderBinaryArr);
//		int bufLen = shaders.length - offset;
//		call.setIntArrayCount(bufLen + 4);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, n);
//		call.setIntArrayParam(1, offset);
//		call.setIntArrayParam(2, binaryformat);
//		call.setIntArrayParam(3, length);
//		for (int i = 4; i < bufLen + 4; ++i) {
//			call.setIntArrayParam(i, shaders[offset + i -4]);
//		}
//		call.setBuffer(0, binary);
//		glCallQueue.add(call);
//	}
//	
//	public static void glShaderBinary(ENG_Integer n, ENG_Integer[] shaders,
//			ENG_Integer offset, ENG_Integer binaryformat, 
//			Buffer binary, ENG_Integer length) {
//		glShaderBinary(n, shaders, offset, binaryformat, binary, length,
//				true, true);
//	}
//	
//	public static void glShaderBinary(ENG_Integer n, ENG_Integer[] shaders,
//			ENG_Integer offset, ENG_Integer binaryformat, 
//			Buffer binary, ENG_Integer length, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glShaderBinaryArr);
//		int bufLen = shaders.length - offset.getValue();
//		call.setIntObjCount(bufLen + 4);
//		call.setIntObjParam(0, n, copy);
//		call.setIntObjParam(1, offset, copy);
//		call.setIntObjParam(2, binaryformat, copy);
//		call.setIntObjParam(3, length, copy);
//		if (copyData) {
//			for (int i = 4; i < bufLen + 4; ++i) {
//				call.setIntObjParam(i, shaders[offset.getValue() + i - 4], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(shaders, offset.getValue(), buf, 4, bufLen);
//		}
//		call.setBuffer(0, binary);
//		glCallQueue.add(call);
//	}

//	public static void glShaderSource(int shader, String string) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glShaderSource);
//		call.setIntArrayCount(1);
//		call.setStringObjCount(1);
//		call.setIntArrayParam(0, shader);
//		call.setStringObjParam(0, string);
//		glCallQueue.add(call);
//	}

    public static void glShaderSourceImmediate(final int shader, final String string) {

//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//
//				eventQueuedRun("glShaderSourceImmediate");
//				Gdx.gl20.glShaderSource(shader, string);
//				if (DEBUG) {
//					int err = Gdx.gl20.glGetError();
////					String msg = GLU.gluErrorString(err);
//					System.out.println("glShaderSourceImmediate " + err);
//				}
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glShaderSourceImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glShaderSource(shader, string);
    }

//	public static void glShaderSource(ENG_Integer shader, String string) {
//		glShaderSource(shader, string, true);
//	}
//	
//	public static void glShaderSource(ENG_Integer shader, String string,
//			boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glShaderSource);
//		call.setIntObjCount(1);
//		call.setStringObjCount(1);
//		call.setIntObjParam(0, shader, copy);
//		call.setStringObjParam(0, string);
//		glCallQueue.add(call);
//	}

    public static void glShaderSourceImmediate(ENG_Integer shader,
                                               final String string) {
//		final int s = shader.getValue();
//		GLRunnableEvent event = new GLRunnableEvent() {
//
//			@Override
//			public void run() {
//				eventQueuedRun("glShaderSourceImmediate");
//
//				Gdx.gl20.glShaderSource(s, string);
//				_getSemaphore().release();
//			}
//			
//		};
//		eventQueued("glShaderSourceImmediate");
//		GLRenderSurface.getSingleton().queueEvent(event);
//		GLRenderSurface.getSingleton().disableBufferSwap();
//		GLRenderSurface.getSingleton().requestRender();
//		event.checkSemaphoreReleased();
        Gdx.gl20.glShaderSource(shader.getValue(), string);
    }

    public static void glStencilFunc(int func, int ref, int mask) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilFunc);
//		call.setIntArrayCount(3);
//		call.setIntArrayParam(0, func);
//		call.setIntArrayParam(1, ref);
//		call.setIntArrayParam(2, mask);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilFunc(func, ref, mask);
    }

    public static void glStencilFunc(ENG_Integer func, ENG_Integer ref,
                                     ENG_Integer mask) {
        glStencilFunc(func, ref, mask, true);
    }

    public static void glStencilFunc(ENG_Integer func, ENG_Integer ref,
                                     ENG_Integer mask, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilFunc);
//		call.setIntObjCount(3);
//		call.setIntObjParam(0, func, copy);
//		call.setIntObjParam(1, ref, copy);
//		call.setIntObjParam(2, mask, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilFunc(func.getValue(), ref.getValue(), mask.getValue());
    }

    public static void glStencilFuncSeparate(int face, int func, int ref, int mask) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilFuncSeparate);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, face);
//		call.setIntArrayParam(1, func);
//		call.setIntArrayParam(2, ref);
//		call.setIntArrayParam(3, mask);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilFuncSeparate(face, func, ref, mask);
    }

    public static void glStencilFuncSeparate(ENG_Integer face,
                                             ENG_Integer func, ENG_Integer ref, ENG_Integer mask) {
        glStencilFuncSeparate(face, func, ref, mask, true);
    }

    public static void glStencilFuncSeparate(ENG_Integer face,
                                             ENG_Integer func, ENG_Integer ref,
                                             ENG_Integer mask, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilFuncSeparate);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, face, copy);
//		call.setIntObjParam(1, func, copy);
//		call.setIntObjParam(2, ref, copy);
//		call.setIntObjParam(3, mask, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilFuncSeparate(face.getValue(), func.getValue(),
                ref.getValue(), mask.getValue());
    }

    public static void glStencilMask(int mask) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilMask);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, mask);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilMask(mask);
    }

    public static void glStencilMask(ENG_Integer mask) {
        glStencilMask(mask, true);
    }

    public static void glStencilMask(ENG_Integer mask, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilMask);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, mask, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilMask(mask.getValue());
    }

    public static void glStencilMaskSeparate(int face, int mask) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilMaskSeparate);
//		call.setIntArrayCount(2);
//		call.setIntArrayParam(0, face);
//		call.setIntArrayParam(1, mask);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilMaskSeparate(face, mask);
    }

    public static void glStencilMaskSeparate(ENG_Integer face, ENG_Integer mask) {
        glStencilMaskSeparate(face, mask, true);
    }

    public static void glStencilMaskSeparate(ENG_Integer face, ENG_Integer mask,
                                             boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilMaskSeparate);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, face, copy);
//		call.setIntObjParam(1, mask, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilMaskSeparate(face.getValue(), mask.getValue());
    }

    public static void glStencilOp(int fail, int zfail, int zpass) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilOp);
//		call.setIntArrayCount(3);
//		call.setIntArrayParam(0, fail);
//		call.setIntArrayParam(1, zfail);
//		call.setIntArrayParam(2, zpass);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilOp(fail, zfail, zpass);
    }

    public static void glStencilOp(ENG_Integer fail, ENG_Integer zfail,
                                   ENG_Integer zpass) {
        glStencilOp(fail, zfail, zpass, true);
    }

    public static void glStencilOp(ENG_Integer fail, ENG_Integer zfail,
                                   ENG_Integer zpass, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilOp);
//		call.setIntObjCount(3);
//		call.setIntObjParam(0, fail, copy);
//		call.setIntObjParam(1, zfail, copy);
//		call.setIntObjParam(2, zpass, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilOp(fail.getValue(), zfail.getValue(), zpass.getValue());
    }

    public static void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilOpSeparate);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, face);
//		call.setIntArrayParam(1, fail);
//		call.setIntArrayParam(2, zfail);
//		call.setIntArrayParam(3, zpass);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilOpSeparate(face, fail, zfail, zpass);
    }

    public static void glStencilOpSeparate(ENG_Integer face,
                                           ENG_Integer fail, ENG_Integer zfail, ENG_Integer zpass) {
        glStencilOpSeparate(face, fail, zfail, zpass, true);
    }

    public static void glStencilOpSeparate(ENG_Integer face,
                                           ENG_Integer fail, ENG_Integer zfail,
                                           ENG_Integer zpass, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glStencilOpSeparate);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, face, copy);
//		call.setIntObjParam(1, fail, copy);
//		call.setIntObjParam(2, zfail, copy);
//		call.setIntObjParam(3, zpass, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glStencilOpSeparate(face.getValue(), fail.getValue(),
                zfail.getValue(), zpass.getValue());
    }

    public static void glTexImage2D(int target, int level,
                                    int internalformat, int width, int height,
                                    int border, int format, int type, Buffer pixels) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexImage2D);
//		call.setIntArrayCount(8);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, level);
//		call.setIntArrayParam(2, internalformat);
//		call.setIntArrayParam(3, width);
//		call.setIntArrayParam(4, height);
//		call.setIntArrayParam(5, border);
//		call.setIntArrayParam(6, format);
//		call.setIntArrayParam(7, type);
//		call.setBuffer(0, pixels);
//		glCallQueue.add(call);
        Gdx.gl20.glTexImage2D(target, level, internalformat,
                width, height, border, format, type, pixels);
    }

    public static void glTexImage2D(ENG_Integer target, ENG_Integer level,
                                    ENG_Integer internalformat, ENG_Integer width, ENG_Integer height,
                                    ENG_Integer border, ENG_Integer format, ENG_Integer type, Buffer pixels) {
        glTexImage2D(target, level, internalformat, width, height, border,
                format, type, pixels, true, true);
    }

    public static void glTexImage2D(ENG_Integer target, ENG_Integer level,
                                    ENG_Integer internalformat, ENG_Integer width, ENG_Integer height,
                                    ENG_Integer border, ENG_Integer format, ENG_Integer type, Buffer pixels,
                                    boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexImage2D);
//		call.setIntObjCount(8);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, level, copy);
//		call.setIntObjParam(2, internalformat, copy);
//		call.setIntObjParam(3, width, copy);
//		call.setIntObjParam(4, height, copy);
//		call.setIntObjParam(5, border, copy);
//		call.setIntObjParam(6, format, copy);
//		call.setIntObjParam(7, type, copy);
//		call.setBuffer(0, pixels, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glTexImage2D(target.getValue(), level.getValue(),
                internalformat.getValue(),
                width.getValue(), height.getValue(), border.getValue(),
                format.getValue(), type.getValue(), pixels);
    }

    public static void glTexParameterf(int target, int pname, float param) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterf);
//		call.setIntArrayCount(2);
//		call.setFloatArrayCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setFloatArrayParam(0, param);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameterf(target, pname, param);
    }

    public static void glTexParameterf(ENG_Integer target, ENG_Integer pname,
                                       ENG_Float param) {
        glTexParameterf(target, pname, param, true);
    }

    public static void glTexParameterf(ENG_Integer target, ENG_Integer pname,
                                       ENG_Float param, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterf);
//		call.setIntObjCount(2);
//		call.setFloatObjCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setFloatObjParam(0, param, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameterf(target.getValue(), pname.getValue(), param.getValue());
    }

//	public static void glTexParameterfv(int target, int pname,
//			float[] params, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterfvArr);
//		int bufLen = params.length - offset;
//		call.setIntArrayCount(3);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setIntArrayParam(2, offset);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, params[offset + i]);
//		}
//		glCallQueue.add(call);		
//	}
//	
//	public static void glTexParameterfv(ENG_Integer target, ENG_Integer pname,
//			ENG_Float[] params, ENG_Integer offset) {
//		glTexParameterfv(target, pname, params, offset, true, true);
//	}
//	
//	public static void glTexParameterfv(ENG_Integer target, ENG_Integer pname,
//			ENG_Float[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterfvArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, params[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
//	}

    public static void glTexParameterfv(int target, int pname, FloatBuffer params) {
        glTexParameterfv(target, pname, params, true);
    }

    public static void glTexParameterfv(int target, int pname, FloatBuffer params,
                                        boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterfvBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameterfv(target, pname, params);
    }

    public static void glTexParameterfv(ENG_Integer target, ENG_Integer pname,
                                        FloatBuffer params) {
        glTexParameterfv(target, pname, params, true, true);
    }

    public static void glTexParameterfv(ENG_Integer target, ENG_Integer pname,
                                        FloatBuffer params,
                                        boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterfvBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setBuffer(0, params, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameterfv(target.getValue(), pname.getValue(), params);
    }

    public static void glTexParameteri(int target, int pname, int param) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameteri);
//		call.setIntArrayCount(3);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setIntArrayParam(2, param);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameteri(target, pname, param);
    }

    public static void glTexParameteri(ENG_Integer target, ENG_Integer pname,
                                       ENG_Integer param) {
        glTexParameteri(target, pname, param, true);
    }

    public static void glTexParameteri(ENG_Integer target, ENG_Integer pname,
                                       ENG_Integer param, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameteri);
//		call.setIntObjCount(3);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, param, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameteri(target.getValue(), pname.getValue(), param.getValue());
    }

    public static void glTexParameteriv(int target, int pname, IntBuffer params) {
        glTexParameteriv(target, pname, params, true);
    }

    public static void glTexParameteriv(int target, int pname, IntBuffer params,
                                        boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setBuffer(0, params, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameteriv(target, pname, params);
    }

    public static void glTexParameteriv(ENG_Integer target, ENG_Integer pname,
                                        IntBuffer params) {
        glTexParameteriv(target, pname, params, true, true);
    }

    public static void glTexParameteriv(ENG_Integer target, ENG_Integer pname,
                                        IntBuffer params, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setBuffer(0, params, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glTexParameteriv(target.getValue(), pname.getValue(), params);
    }

//	public static void glTexParameteriv(int target, int pname, 
//			int[] params, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterivArr);
//		int bufLen = params.length - offset;
//		call.setIntArrayCount(bufLen + 3);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, pname);
//		call.setIntArrayParam(2, offset);
//		for (int i = 3; i < bufLen + 3; ++i) {
//			call.setIntArrayParam(i, params[offset + i - 3]);
//		}
//		glCallQueue.add(call);
//	}
//	
//	public static void glTexParameteriv(ENG_Integer target, ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset) {
//		glTexParameteriv(target, pname, params, offset, true, true);
//	}
//	
//	public static void glTexParameteriv(ENG_Integer target, ENG_Integer pname, 
//			ENG_Integer[] params, ENG_Integer offset,
//			boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexParameterivArr);
//		int bufLen = params.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, pname, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, params[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(params, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
//	}

    public static void glTexSubImage2D(int target, int level,
                                       int xoffset, int yoffset,
                                       int width, int height,
                                       int format, int type, Buffer pixels) {
        glTexSubImage2D(target, level, xoffset, yoffset, width, height,
                format, type, pixels, true);
    }

    public static void glTexSubImage2D(int target, int level,
                                       int xoffset, int yoffset,
                                       int width, int height,
                                       int format, int type, Buffer pixels, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexSubImage2D);
//		call.setIntArrayCount(8);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, target);
//		call.setIntArrayParam(1, level);
//		call.setIntArrayParam(2, xoffset);
//		call.setIntArrayParam(3, yoffset);
//		call.setIntArrayParam(4, width);
//		call.setIntArrayParam(5, height);
//		call.setIntArrayParam(6, format);
//		call.setIntArrayParam(7, type);
//		call.setBuffer(0, pixels, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glTexSubImage2D(target, level, xoffset, yoffset,
                width, height, format, type, pixels);
    }

    public static void glTexSubImage2D(ENG_Integer target, ENG_Integer level,
                                       ENG_Integer xoffset, ENG_Integer yoffset,
                                       ENG_Integer width, ENG_Integer height,
                                       ENG_Integer format, ENG_Integer type, Buffer pixels) {
        glTexSubImage2D(target, level, xoffset, yoffset, width, height,
                format, type, pixels, true, true);
    }

    public static void glTexSubImage2D(ENG_Integer target, ENG_Integer level,
                                       ENG_Integer xoffset, ENG_Integer yoffset,
                                       ENG_Integer width, ENG_Integer height,
                                       ENG_Integer format, ENG_Integer type, Buffer pixels,
                                       boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glTexSubImage2D);
//		call.setIntObjCount(8);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, target, copy);
//		call.setIntObjParam(1, level, copy);
//		call.setIntObjParam(2, xoffset, copy);
//		call.setIntObjParam(3, yoffset, copy);
//		call.setIntObjParam(4, width, copy);
//		call.setIntObjParam(5, height, copy);
//		call.setIntObjParam(6, format, copy);
//		call.setIntObjParam(7, type, copy);
//		call.setBuffer(0, pixels, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glTexSubImage2D(target.getValue(), level.getValue(),
                xoffset.getValue(), yoffset.getValue(),
                width.getValue(), height.getValue(), format.getValue(),
                type.getValue(), pixels);
    }

    public static void glUniform1f(int location, float x) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(1);
//		call.setIntArrayParam(0, location);
//		call.setFloatArrayParam(0, x);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1f(location, x);
    }

    public static void glUniform1f(ENG_Integer location, ENG_Float x) {
        glUniform1f(location, x, true);
    }

    public static void glUniform1f(ENG_Integer location, ENG_Float x, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1f);
//		call.setIntObjCount(1);
//		call.setFloatObjCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setFloatObjParam(0, x, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1f(location.getValue(), x.getValue());
    }

    public static void glUniform1fv(int location, int count, float[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1fvArr);
//		int bufLen = count;//v.length - offset;
//		call.setIntArrayCount(3);		
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, v[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset, offset + count);
        Gdx.gl20.glUniform1fv(location, count, buffer);
    }

    public static void glUniform1fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset) {
        glUniform1fv(location, count, v, offset, true, true);
    }

    public static void glUniform1fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1fvArr);
//		int bufLen = count.getValue();//v.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, v[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset.getValue(), offset.getValue() + count.getValue());
        Gdx.gl20.glUniform1fv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniform1fv(int location, int count, FloatBuffer v) {
        glUniform1fv(location, count, v, true);
    }

    public static void glUniform1fv(int location, int count, FloatBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1fvBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1fv(location, count, v);
    }

    public static void glUniform1fv(ENG_Integer location, ENG_Integer count,
                                    FloatBuffer v) {
        glUniform1fv(location, count, v, true, true);
    }

    public static void glUniform1fv(ENG_Integer location, ENG_Integer count,
                                    FloatBuffer v, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1fvBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1fv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform1i(int location, int x) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1i);
//		call.setIntArrayCount(2);
//		//call.setFloatArrayCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, x);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1i(location, x);
    }

    public static void glUniform1i(ENG_Integer location, ENG_Integer x) {
        glUniform1i(location, x, true);
    }

    public static void glUniform1i(ENG_Integer location, ENG_Integer x, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1i);
//		call.setIntObjCount(2);
//		//call.setFloatObjCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, x, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1i(location.getValue(), x.getValue());
    }

    public static void glUniform1iv(int location, int count, int[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1ivArr);
//		int bufLen = count;//v.length - offset;
//		call.setIntArrayCount(bufLen + 3);		
//		//call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		for (int i = 3; i < bufLen + 3; ++i) {
//			call.setIntArrayParam(i, v[offset + i - 3]);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v, offset, offset + count);
        Gdx.gl20.glUniform1iv(location, count, buffer);
    }

    public static void glUniform1iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset) {
        glUniform1iv(location, count, v, offset, true, true);
    }

    public static void glUniform1iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1ivArr);
//		int bufLen = count.getValue();//v.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		//call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setIntObjParam(i + 3, v[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v, offset.getValue(),
                offset.getValue() + count.getValue());
        Gdx.gl20.glUniform1iv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniform1iv(int location, int count, IntBuffer v) {
        glUniform1iv(location, count, v, true);
    }

    public static void glUniform1iv(int location, int count, IntBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1ivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1iv(location, count, v);
    }

    public static void glUniform1iv(ENG_Integer location, ENG_Integer count,
                                    IntBuffer v) {
        glUniform1iv(location, count, v, true, true);
    }

    public static void glUniform1iv(ENG_Integer location, ENG_Integer count,
                                    IntBuffer v, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform1ivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform1iv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform2f(int location, float x, float y) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(2);
//		call.setIntArrayParam(0, location);
//		call.setFloatArrayParam(0, x);
//		call.setFloatArrayParam(1, y);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2f(location, x, y);
    }

    public static void glUniform2f(ENG_Integer location,
                                   ENG_Float x, ENG_Float y) {
        glUniform2f(location, x, y, true);
    }

    public static void glUniform2f(ENG_Integer location,
                                   ENG_Float x, ENG_Float y, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2f);
//		call.setIntObjCount(1);
//		call.setFloatObjCount(2);
//		call.setIntObjParam(0, location, copy);
//		call.setFloatObjParam(0, x, copy);
//		call.setFloatObjParam(1, y, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2f(location.getValue(), x.getValue(), y.getValue());
    }

    public static void glUniform2fv(int location, int count, FloatBuffer v) {
        glUniform2fv(location, count, v, true);
    }

    public static void glUniform2fv(int location, int count, FloatBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2fvBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2fv(location, count, v);
    }

    public static void glUniform2fv(ENG_Integer location, ENG_Integer count,
                                    FloatBuffer v) {
        glUniform2fv(location, count, v, true, true);
    }

    public static void glUniform2fv(ENG_Integer location, ENG_Integer count,
                                    FloatBuffer v, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2fvBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2fv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform2fv(int location, int count, float[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2fvArr);
//		int bufLen = count * 2;//v.length - offset;
//		call.setIntArrayCount(3);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, v[i + offset]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset, offset + count * 2);
        Gdx.gl20.glUniform2fv(location, count, buffer);
    }

    public static void glUniform2fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset) {
        glUniform2fv(location, count, v, offset, true, true);
    }

    public static void glUniform2fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset,
                                    boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2fvArr);
//		int bufLen = count.getValue() * 2;//v.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, v[i + offset.getValue()], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset.getValue(), offset.getValue() + count.getValue() * 2);
        Gdx.gl20.glUniform2fv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniform2i(int location, int x, int y) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2i);
//		call.setIntArrayCount(3);
//		//call.setIntArrayCount(2);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, x);
//		call.setIntArrayParam(2, y);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2i(location, x, y);
    }

    public static void glUniform2i(ENG_Integer location,
                                   ENG_Integer x, ENG_Integer y) {
        glUniform2i(location, x, y, true);
    }

    public static void glUniform2i(ENG_Integer location,
                                   ENG_Integer x, ENG_Integer y, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2i);
//		call.setIntObjCount(3);
//		//call.setIntObjCount(2);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, x, copy);
//		call.setIntObjParam(2, y, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2i(location.getValue(), x.getValue(), y.getValue());
    }

    public static void glUniform2iv(int location, int count, IntBuffer v) {
        glUniform2iv(location, count, v, true);
    }

    public static void glUniform2iv(int location, int count, IntBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2ivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2iv(location, count, v);
    }

    public static void glUniform2iv(ENG_Integer location, ENG_Integer count,
                                    IntBuffer v) {
        glUniform2iv(location, count, v, true, true);
    }

    public static void glUniform2iv(ENG_Integer location, ENG_Integer count,
                                    IntBuffer v, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2ivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform2iv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform2iv(int location, int count, int[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2ivArr);
//		int bufLen = count * 2;//v.length - offset;
//		call.setIntArrayCount(2);
//		call.setIntArrayCount(bufLen + 3);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		for (int i = 3; i < bufLen + 3; ++i) {
//			call.setIntArrayParam(i, v[i + offset - 3]);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v, offset, offset + count * 2);
        Gdx.gl20.glUniform2iv(location, count, buffer);
    }

    public static void glUniform2iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset) {
        glUniform2iv(location, count, v, offset, true, true);
    }

    public static void glUniform2iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset,
                                    boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform2ivArr);
//		int bufLen = count.getValue() * 2;//v.length - offset.getValue();
//		//call.setIntObjCount(3);
//		call.setIntObjCount(bufLen + 3);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, v[i + offset.getValue() - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v,
                offset.getValue(), offset.getValue() + count.getValue() * 2);
        Gdx.gl20.glUniform2iv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniform3f(int location, float x, float y, float z) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(3);
//		call.setIntArrayParam(0, location);
//		call.setFloatArrayParam(0, x);
//		call.setFloatArrayParam(1, y);
//		call.setFloatArrayParam(2, z);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3f(location, x, y, z);
    }

    public static void glUniform3f(ENG_Integer location,
                                   ENG_Float x, ENG_Float y, ENG_Float z) {
        glUniform3f(location, x, y, z, true);
    }

    public static void glUniform3f(ENG_Integer location,
                                   ENG_Float x, ENG_Float y, ENG_Float z, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3f);
//		call.setIntObjCount(1);
//		call.setFloatObjCount(3);
//		call.setIntObjParam(0, location, copy);
//		call.setFloatObjParam(0, x, copy);
//		call.setFloatObjParam(1, y, copy);
//		call.setFloatObjParam(2, z, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3f(location.getValue(),
                x.getValue(), y.getValue(), z.getValue());
    }

    public static void glUniform3fv(int location, int count, float[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3fvArr);
//		int bufLen = count * 3;//v.length - offset;
//		call.setIntArrayCount(3);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, v[i + offset]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset, offset + count * 3);
        Gdx.gl20.glUniform3fv(location, count, buffer);
    }

    public static void glUniform3fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset) {
        glUniform3fv(location, count, v, offset, true, true);
    }

    public static void glUniform3fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3fvArr);
//		int bufLen = count.getValue() * 3;//v.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setFloatArrayCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, v[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset.getValue(), offset.getValue() + count.getValue() * 3);
        Gdx.gl20.glUniform3fv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniform3fv(int location, int count, FloatBuffer v) {
        glUniform3fv(location, count, v, true);
    }

    public static void glUniform3fv(int location, int count, FloatBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3fvBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3fv(location, count, v);
    }

    public static void glUniform3fv(ENG_Integer location,
                                    ENG_Integer count, FloatBuffer v) {
        glUniform3fv(location, count, v, true, true);
    }

    public static void glUniform3fv(ENG_Integer location,
                                    ENG_Integer count, FloatBuffer v,
                                    boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3fvBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3fv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform3i(int location, int x, int y, int z) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3i);
//		call.setIntArrayCount(4);
//		//call.setFloatArrayCount(3);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, x);
//		call.setIntArrayParam(2, y);
//		call.setIntArrayParam(3, z);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3i(location, x, y, z);
    }

    public static void glUniform3i(ENG_Integer location,
                                   ENG_Integer x, ENG_Integer y, ENG_Integer z) {
        glUniform3i(location, x, y, z, true);
    }

    public static void glUniform3i(ENG_Integer location,
                                   ENG_Integer x, ENG_Integer y, ENG_Integer z, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3i);
//		call.setIntObjCount(4);
//		//call.setFloatObjCount(3);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, x, copy);
//		call.setIntObjParam(2, y, copy);
//		call.setIntObjParam(3, z, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3i(location.getValue(),
                x.getValue(), y.getValue(), z.getValue());
    }

    public static void glUniform3iv(int location, int count, int[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3ivArr);
//		int bufLen = count * 3;//v.length - offset;
//		call.setIntArrayCount(bufLen + 3);
//		//call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		for (int i = 3; i < bufLen + 3; ++i) {
//			call.setIntArrayParam(i, v[i + offset - 3]);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v,
                offset, offset + count * 3);
        Gdx.gl20.glUniform3iv(location, count, buffer);
    }

    public static void glUniform3iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset) {
        glUniform3iv(location, count, v, offset, true, true);
    }

    public static void glUniform3iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3fvArr);
//		int bufLen = count.getValue() * 3;//v.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		//call.setFloatArrayCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, v[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v,
                offset.getValue(), offset.getValue() + count.getValue() * 3);
        Gdx.gl20.glUniform3iv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniform3iv(int location, int count, IntBuffer v) {
        glUniform3iv(location, count, v, true);
    }

    public static void glUniform3iv(int location, int count, IntBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3ivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3iv(location, count, v);
    }

    public static void glUniform3iv(ENG_Integer location,
                                    ENG_Integer count, IntBuffer v) {
        glUniform3iv(location, count, v, true, true);
    }

    public static void glUniform3iv(ENG_Integer location,
                                    ENG_Integer count, IntBuffer v,
                                    boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform3ivBuf);
//		call.setIntObjCount(2);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform3iv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform4f(int location, float x, float y, float z, float w) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(4);
//		call.setIntArrayParam(0, location);
//		call.setFloatArrayParam(0, x);
//		call.setFloatArrayParam(1, y);
//		call.setFloatArrayParam(2, z);
//		call.setFloatArrayParam(3, w);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4f(location, x, y, z, w);
    }

    public static void glUniform4f(ENG_Integer location,
                                   ENG_Float x, ENG_Float y, ENG_Float z, ENG_Float w) {
        glUniform4f(location, x, y, z, w, true);
    }

    public static void glUniform4f(ENG_Integer location,
                                   ENG_Float x, ENG_Float y, ENG_Float z, ENG_Float w, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4f);
//		call.setIntObjCount(1);
//		call.setFloatArrayCount(4);
//		call.setIntObjParam(0, location, copy);
//		call.setFloatObjParam(0, x, copy);
//		call.setFloatObjParam(1, y, copy);
//		call.setFloatObjParam(2, z, copy);
//		call.setFloatObjParam(3, w, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4f(location.getValue(),
                x.getValue(), y.getValue(), z.getValue(), w.getValue());
    }

    public static void glUniform4fv(int location, int count, FloatBuffer v) {
        glUniform4fv(location, count, v, true);
    }

    public static void glUniform4fv(int location, int count, FloatBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4fvBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4fv(location, count, v);
    }

    public static void glUniform4fv(ENG_Integer location, ENG_Integer count,
                                    FloatBuffer v) {
        glUniform4fv(location, count, v, true, true);
    }

    public static void glUniform4fv(ENG_Integer location, ENG_Integer count,
                                    FloatBuffer v, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4fvBuf);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4fv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform4fv(int location, int count, float[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4fvArr);
//		int bufLen = count * 4;//v.length - offset;
//		call.setIntArrayCount(3);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, v[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset, offset + count * 4);
        Gdx.gl20.glUniform4fv(location, count, buffer);
    }

    public static void glUniform4fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset) {
        glUniform4fv(location, count, v, offset, true, true);
    }

    public static void glUniform4fv(ENG_Integer location, ENG_Integer count,
                                    ENG_Float[] v, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4fvArr);
//		int bufLen = count.getValue() * 4;//v.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, v[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(v,
                offset.getValue(), offset.getValue() + count.getValue() * 4);
        Gdx.gl20.glUniform4fv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniform4i(int location, int x, int y, int z, int w) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4i);
//		call.setIntArrayCount(5);
//		//call.setFloatArrayCount(4);
//		call.setIntArrayParam(0, location);
//		call.setFloatArrayParam(1, x);
//		call.setFloatArrayParam(2, y);
//		call.setFloatArrayParam(3, z);
//		call.setFloatArrayParam(4, w);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4i(location, x, y, z, w);
    }

    public static void glUniform4i(ENG_Integer location,
                                   ENG_Integer x, ENG_Integer y, ENG_Integer z, ENG_Integer w) {
        glUniform4i(location, x, y, z, w, true);
    }

    public static void glUniform4i(ENG_Integer location,
                                   ENG_Integer x, ENG_Integer y, ENG_Integer z, ENG_Integer w,
                                   boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4i);
//		call.setIntObjCount(5);
//		//call.setFloatArrayCount(4);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, x, copy);
//		call.setIntObjParam(2, y, copy);
//		call.setIntObjParam(3, z, copy);
//		call.setIntObjParam(4, w, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4i(location.getValue(),
                x.getValue(), y.getValue(), z.getValue(), w.getValue());
    }

    public static void glUniform4iv(int location, int count, IntBuffer v) {
        glUniform4iv(location, count, v, true);
    }

    public static void glUniform4iv(int location, int count, IntBuffer v,
                                    boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4ivBuf);
//		call.setIntArrayCount(2);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4iv(location, count, v);
    }

    public static void glUniform4iv(ENG_Integer location, ENG_Integer count,
                                    IntBuffer v) {
        glUniform4iv(location, count, v, true, true);
    }

    public static void glUniform4iv(ENG_Integer location, ENG_Integer count,
                                    IntBuffer v, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4ivBuf);
//		call.setIntObjCount(2);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBuffer(0, v, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniform4iv(location.getValue(), count.getValue(), v);
    }

    public static void glUniform4iv(int location, int count, int[] v, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4ivArr);
//		int bufLen = count * 4;//v.length - offset;
//		call.setIntArrayCount(bufLen + 3);
//		//call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, offset);
//		for (int i = 3; i < bufLen + 3; ++i) {
//			call.setIntArrayParam(i, v[offset + i - 3]);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v,
                offset, offset + count * 4);
        Gdx.gl20.glUniform4iv(location, count, buffer);
    }

    public static void glUniform4iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset) {
        glUniform4iv(location, count, v, offset, true, true);
    }

    public static void glUniform4iv(ENG_Integer location, ENG_Integer count,
                                    ENG_Integer[] v, ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniform4ivArr);
//		int bufLen = count.getValue() * 4;//v.length - offset.getValue();
//		call.setIntObjCount(bufLen + 3);
//		//call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, offset, copy);
//		if (copyData) {
//			for (int i = 3; i < bufLen + 3; ++i) {
//				call.setIntObjParam(i, v[offset.getValue() + i - 3], true);
//			}
//		} else {
//			ENG_Integer[] buf = call.getIntObjParam();
//			System.arraycopy(v, offset.getValue(), buf, 3, bufLen);
//		}
//		glCallQueue.add(call);
        IntBuffer buffer = ENG_Utility.getIntArrayAsBuffer(v,
                offset.getValue(), offset.getValue() + count.getValue() * 4);
        Gdx.gl20.glUniform4iv(location.getValue(), count.getValue(), buffer);
    }

    public static void glUniformMatrix2fv(int location, int count,
                                          boolean transpose, FloatBuffer value) {
        glUniformMatrix2fv(location, count, transpose, value, true);
    }

    public static void glUniformMatrix2fv(int location, int count,
                                          boolean transpose, FloatBuffer value, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix2fvBuf);
//		call.setIntArrayCount(2);
//		call.setBooleanArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBooleanArrayParam(0, transpose);
//		call.setBuffer(0, value, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniformMatrix2fv(location, count, transpose, value);
    }

    public static void glUniformMatrix2fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, FloatBuffer value) {
        glUniformMatrix2fv(location, count, transpose, value, true, true);
    }

    public static void glUniformMatrix2fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, FloatBuffer value,
                                          boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix2fvBuf);
//		call.setIntObjCount(2);
//		call.setBooleanObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBooleanObjParam(0, transpose, copy);
//		call.setBuffer(0, value, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniformMatrix2fv(location.getValue(),
                count.getValue(), transpose.getValue(), value);
    }

    public static void glUniformMatrix2fv(int location, int count,
                                          boolean transpose, float[] value, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix2fvArr);
//		int bufLen = count * 4;//value.length - offset;
//		call.setIntArrayCount(3);
//		call.setBooleanArrayCount(1);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		call.setBooleanArrayParam(0, transpose);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, value[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(value,
                offset, offset + count * 4);
        Gdx.gl20.glUniformMatrix2fv(location, count, transpose, buffer);
    }

    public static void glUniformMatrix2fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, ENG_Float[] value, ENG_Integer offset) {
        glUniformMatrix2fv(location, count, transpose, value, offset, true, true);
    }

    public static void glUniformMatrix2fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, ENG_Float[] value, ENG_Integer offset,
                                          boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix2fvArr);
//		int bufLen = count.getValue() * 4;//value.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setBooleanObjCount(1);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		call.setBooleanObjParam(0, transpose, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, value[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(value, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(value,
                offset.getValue(), offset.getValue() + count.getValue() * 4);
        Gdx.gl20.glUniformMatrix2fv(location.getValue(),
                count.getValue(), transpose.getValue(), buffer);
    }

    public static void glUniformMatrix3fv(int location, int count,
                                          boolean transpose, FloatBuffer value) {
        glUniformMatrix3fv(location, count, transpose, value, true);
    }

    public static void glUniformMatrix3fv(int location, int count,
                                          boolean transpose, FloatBuffer value, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix3fvBuf);
//		call.setIntArrayCount(2);
//		call.setBooleanArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBooleanArrayParam(0, transpose);
//		call.setBuffer(0, value, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniformMatrix3fv(location, count, transpose, value);
    }

    public static void glUniformMatrix3fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, FloatBuffer value) {
        glUniformMatrix3fv(location, count, transpose, value, true, true);
    }

    public static void glUniformMatrix3fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, FloatBuffer value,
                                          boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix3fvBuf);
//		call.setIntObjCount(2);
//		call.setBooleanObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBooleanObjParam(0, transpose, copy);
//		call.setBuffer(0, value, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniformMatrix3fv(location.getValue(),
                count.getValue(), transpose.getValue(), value);
    }

    public static void glUniformMatrix3fv(int location, int count,
                                          boolean transpose, float[] value, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix3fvArr);
//		int bufLen = count * 9;//value.length - offset;
//		call.setIntArrayCount(3);
//		call.setBooleanArrayCount(1);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		call.setBooleanArrayParam(0, transpose);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, value[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(value,
                offset, offset + count * 9);
        Gdx.gl20.glUniformMatrix3fv(location, count, transpose, buffer);
    }

    public static void glUniformMatrix3fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, ENG_Float[] value, ENG_Integer offset) {
        glUniformMatrix3fv(location, count, transpose, value, offset, true, true);
    }

    public static void glUniformMatrix3fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, ENG_Float[] value, ENG_Integer offset,
                                          boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix3fvArr);
//		int bufLen = count.getValue() * 9;//value.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setBooleanObjCount(1);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		call.setBooleanObjParam(0, transpose, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, value[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(value, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(value,
                offset.getValue(), offset.getValue() + count.getValue() * 9);
        Gdx.gl20.glUniformMatrix3fv(location.getValue(),
                count.getValue(), transpose.getValue(), buffer);
    }

    public static void glUniformMatrix4fv(int location, int count,
                                          boolean transpose, FloatBuffer value) {
        glUniformMatrix4fv(location, count, transpose, value, true);
    }

    public static void glUniformMatrix4fv(int location, int count,
                                          boolean transpose, FloatBuffer value, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix4fvBuf);
//		call.setIntArrayCount(2);
//		call.setBooleanArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setBooleanArrayParam(0, transpose);
//		call.setBuffer(0, value, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniformMatrix4fv(location, count, transpose, value);
    }

    public static void glUniformMatrix4fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, FloatBuffer value) {
        glUniformMatrix4fv(location, count, transpose, value, true, true);
    }

    public static void glUniformMatrix4fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, FloatBuffer value,
                                          boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix4fvBuf);
//		call.setIntObjCount(2);
//		call.setBooleanObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setBooleanObjParam(0, transpose, copy);
//		call.setBuffer(0, value, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glUniformMatrix4fv(location.getValue(),
                count.getValue(), transpose.getValue(), value);
    }

    public static void glUniformMatrix4fv(int location, int count,
                                          boolean transpose, float[] value, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix4fvArr);
//		int bufLen = count * 16;//value.length - offset;
//		call.setIntArrayCount(3);
//		call.setBooleanArrayCount(1);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, location);
//		call.setIntArrayParam(1, count);
//		call.setIntArrayParam(2, 0);
//		call.setBooleanArrayParam(0, transpose);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, value[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(value,
                offset, offset + count * 16);
        Gdx.gl20.glUniformMatrix4fv(location, count, transpose, buffer);
    }

    public static void glUniformMatrix4fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, ENG_Float[] value, ENG_Integer offset) {
        glUniformMatrix4fv(location, count, transpose, value, offset, true, true);
    }

    public static void glUniformMatrix4fv(ENG_Integer location, ENG_Integer count,
                                          ENG_Boolean transpose, ENG_Float[] value, ENG_Integer offset,
                                          boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUniformMatrix4fvArr);
//		int bufLen = count.getValue() * 16;//value.length - offset.getValue();
//		call.setIntObjCount(3);
//		call.setBooleanObjCount(1);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, location, copy);
//		call.setIntObjParam(1, count, copy);
//		call.setIntObjParam(2, new ENG_Integer(0), false);
//		call.setBooleanObjParam(0, transpose, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, value[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(value, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(value,
                offset.getValue(), offset.getValue() + count.getValue() * 16);
        Gdx.gl20.glUniformMatrix4fv(location.getValue(),
                count.getValue(), transpose.getValue(), buffer);
    }

    public static void glUseProgram(int program) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUseProgram);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, program);
//		glCallQueue.add(call);
        Gdx.gl20.glUseProgram(program);
    }

    public static void glUseProgram(ENG_Integer program) {
        glUseProgram(program, true);
    }

    public static void glUseProgram(ENG_Integer program, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glUseProgram);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, program, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glUseProgram(program.getValue());
    }

    public static void glValidateProgram(int program) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glValidateProgram);
//		call.setIntArrayCount(1);
//		call.setIntArrayParam(0, program);
//		glCallQueue.add(call);
        Gdx.gl20.glValidateProgram(program);
    }

    public static void glValidateProgram(ENG_Integer program) {
        glValidateProgram(program, true);
    }

    public static void glValidateProgram(ENG_Integer program, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glValidateProgram);
//		call.setIntObjCount(1);
//		call.setIntObjParam(0, program, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glValidateProgram(program.getValue());
    }

    public static void glVertexAttrib1f(int indx, float x) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib1f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(1);
//		call.setIntArrayParam(0, indx);
//		call.setFloatArrayParam(0, x);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib1f(indx, x);
    }

    public static void glVertexAttrib1f(ENG_Integer indx, ENG_Float x) {
        glVertexAttrib1f(indx, x, true);
    }

    public static void glVertexAttrib1f(ENG_Integer indx, ENG_Float x,
                                        boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib1f);
//		call.setIntObjCount(1);
//		call.setFloatObjCount(1);
//		call.setIntObjParam(0, indx, copy);
//		call.setFloatObjParam(0, x, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib1f(indx.getValue(), x.getValue());
    }

    public static void glVertexAttrib1fv(int indx, FloatBuffer values) {
        glVertexAttrib1fv(indx, values, true);
    }

    public static void glVertexAttrib1fv(int indx, FloatBuffer values,
                                         boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib1fvBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, indx);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib1fv(indx, values);
    }

    public static void glVertexAttrib1fv(ENG_Integer indx, FloatBuffer values) {
        glVertexAttrib1fv(indx, values, true, true);
    }

    public static void glVertexAttrib1fv(ENG_Integer indx, FloatBuffer values,
                                         boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib1fvBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, indx, copy);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib1fv(indx.getValue(), values);
    }

    public static void glVertexAttrib1fv(int indx, float[] values, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib1fvArr);
//		int bufLen = values.length - offset;
//		call.setIntArrayCount(2);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, indx);
//		call.setIntArrayParam(1, offset);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(0, values[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values, offset,
                offset + 1);
        Gdx.gl20.glVertexAttrib1fv(indx, buffer);
    }

    public static void glVertexAttrib1fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset) {
        glVertexAttrib1fv(indx, values, offset, true, true);
    }

    public static void glVertexAttrib1fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib1fvArr);
//		int bufLen = values.length - offset.getValue();
//		call.setIntObjCount(2);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, indx, copy);
//		call.setIntObjParam(1, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, values[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(values, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values,
                offset.getValue(), offset.getValue() + 1);
        Gdx.gl20.glVertexAttrib1fv(indx.getValue(), buffer);
    }

    public static void glVertexAttrib2f(int indx, float x, float y) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib2f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(2);
//		call.setIntArrayParam(0, indx);
//		call.setFloatArrayParam(0, x);
//		call.setFloatArrayParam(1, y);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib2f(indx, x, y);
    }

    public static void glVertexAttrib2f(ENG_Integer indx, ENG_Float x, ENG_Float y) {
        glVertexAttrib2f(indx, x, y, true);
    }

    public static void glVertexAttrib2f(ENG_Integer indx, ENG_Float x, ENG_Float y,
                                        boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib2f);
//		call.setIntObjCount(1);
//		call.setFloatObjCount(2);
//		call.setIntObjParam(0, indx, copy);
//		call.setFloatObjParam(0, x, copy);
//		call.setFloatObjParam(1, y, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib2f(indx.getValue(), x.getValue(), y.getValue());
    }

    public static void glVertexAttrib2fv(int indx, float[] values, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib2fvArr);
//		int bufLen = values.length - offset;
//		call.setIntArrayCount(2);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, indx);
//		call.setIntArrayParam(1, offset);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, values[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values,
                offset, offset + 2);
        Gdx.gl20.glVertexAttrib2fv(indx, buffer);
    }

    public static void glVertexAttrib2fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset) {
        glVertexAttrib2fv(indx, values, offset, true, true);
    }

    public static void glVertexAttrib2fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib2fvArr);
//		int bufLen = values.length - offset.getValue();
//		call.setIntObjCount(2);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, indx, copy);
//		call.setIntObjParam(1, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, values[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(values, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values,
                offset.getValue(), offset.getValue() + 2);
        Gdx.gl20.glVertexAttrib2fv(indx.getValue(), buffer);
    }

    public static void glVertexAttrib2fv(int indx, FloatBuffer values) {
        glVertexAttrib2fv(indx, values, true);
    }

    public static void glVertexAttrib2fv(int indx, FloatBuffer values,
                                         boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib2fvBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, indx);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib2fv(indx, values);
    }

    public static void glVertexAttrib2fv(ENG_Integer indx, FloatBuffer values) {
        glVertexAttrib2fv(indx, values, true, true);
    }

    public static void glVertexAttrib2fv(ENG_Integer indx, FloatBuffer values,
                                         boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib2fvBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, indx, copy);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib2fv(indx.getValue(), values);
    }

    public static void glVertexAttrib3f(int indx, float x, float y, float z) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib3f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(3);
//		call.setIntArrayParam(0, indx);
//		call.setFloatArrayParam(0, x);
//		call.setFloatArrayParam(1, y);
//		call.setFloatArrayParam(2, z);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib3f(indx, x, y, z);
    }

    public static void glVertexAttrib3f(ENG_Integer indx, ENG_Float x, ENG_Float y,
                                        ENG_Float z) {
        glVertexAttrib3f(indx, x, y, z, true);
    }

    public static void glVertexAttrib3f(ENG_Integer indx, ENG_Float x, ENG_Float y,
                                        ENG_Float z, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib3f);
//		call.setIntObjCount(1);
//		call.setFloatObjCount(3);
//		call.setIntObjParam(0, indx, copy);
//		call.setFloatObjParam(0, x, copy);
//		call.setFloatObjParam(1, y, copy);
//		call.setFloatObjParam(2, z, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib3f(indx.getValue(),
                x.getValue(), y.getValue(), z.getValue());
    }

    public static void glVertexAttrib3fv(int indx, float[] values, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib3fvArr);
//		int bufLen = values.length - offset;
//		call.setIntArrayCount(2);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, indx);
//		call.setIntArrayParam(1, offset);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, values[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values,
                offset, offset + 3);
        Gdx.gl20.glVertexAttrib3fv(indx, buffer);
    }

    public static void glVertexAttrib3fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset) {
        glVertexAttrib3fv(indx, values, offset, true, true);
    }

    public static void glVertexAttrib3fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib3fvArr);
//		int bufLen = values.length - offset.getValue();
//		call.setIntObjCount(2);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, indx, copy);
//		call.setIntObjParam(1, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, values[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(values, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values,
                offset.getValue(), offset.getValue() + 3);
        Gdx.gl20.glVertexAttrib3fv(indx.getValue(), buffer);
    }

    public static void glVertexAttrib3fv(int indx, FloatBuffer values) {
        glVertexAttrib3fv(indx, values, true);
    }

    public static void glVertexAttrib3fv(int indx, FloatBuffer values,
                                         boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib3fvBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, indx);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib3fv(indx, values);
    }

    public static void glVertexAttrib3fv(ENG_Integer indx, FloatBuffer values) {
        glVertexAttrib3fv(indx, values, true, true);
    }

    public static void glVertexAttrib3fv(ENG_Integer indx, FloatBuffer values,
                                         boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib3fvBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, indx, copy);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib3fv(indx.getValue(), values);
    }

    public static void glVertexAttrib4f(int indx, float x, float y, float z,
                                        float w) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib4f);
//		call.setIntArrayCount(1);
//		call.setFloatArrayCount(4);
//		call.setIntArrayParam(0, indx);
//		call.setFloatArrayParam(0, x);
//		call.setFloatArrayParam(1, y);
//		call.setFloatArrayParam(2, z);
//		call.setFloatArrayParam(3, w);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib4f(indx, x, y, z, w);
    }

    public static void glVertexAttrib4f(ENG_Integer indx, ENG_Float x, ENG_Float y,
                                        ENG_Float z, ENG_Float w) {
        glVertexAttrib4f(indx, x, y, z, w, true);
    }

    public static void glVertexAttrib4f(ENG_Integer indx, ENG_Float x, ENG_Float y,
                                        ENG_Float z, ENG_Float w, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib4f);
//		call.setIntObjCount(1);
//		call.setFloatObjCount(4);
//		call.setIntObjParam(0, indx, copy);
//		call.setFloatObjParam(0, x, copy);
//		call.setFloatObjParam(1, y, copy);
//		call.setFloatObjParam(2, z, copy);
//		call.setFloatObjParam(3, w, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib4f(indx.getValue(),
                x.getValue(), y.getValue(), z.getValue(), w.getValue());
    }

    public static void glVertexAttrib4fv(int indx, float[] values, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib4fvArr);
//		int bufLen = values.length - offset;
//		call.setIntArrayCount(2);
//		call.setFloatArrayCount(bufLen);
//		call.setIntArrayParam(0, indx);
//		call.setIntArrayParam(1, offset);
//		for (int i = 0; i < bufLen; ++i) {
//			call.setFloatArrayParam(i, values[offset + i]);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values,
                offset, offset + 4);
        Gdx.gl20.glVertexAttrib4fv(indx, buffer);
    }

    public static void glVertexAttrib4fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset) {
        glVertexAttrib4fv(indx, values, offset, true, true);
    }

    public static void glVertexAttrib4fv(ENG_Integer indx, ENG_Float[] values,
                                         ENG_Integer offset, boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib4fvArr);
//		int bufLen = values.length - offset.getValue();
//		call.setIntObjCount(2);
//		call.setFloatObjCount(bufLen);
//		call.setIntObjParam(0, indx, copy);
//		call.setIntObjParam(1, offset, copy);
//		if (copyData) {
//			for (int i = 0; i < bufLen; ++i) {
//				call.setFloatObjParam(i, values[offset.getValue() + i], true);
//			}
//		} else {
//			ENG_Float[] buf = call.getFloatObjParam();
//			System.arraycopy(values, offset.getValue(), buf, 0, bufLen);
//		}
//		glCallQueue.add(call);
        FloatBuffer buffer = ENG_Utility.getFloatArrayAsBuffer(values,
                offset.getValue(), offset.getValue() + 4);
        Gdx.gl20.glVertexAttrib4fv(indx.getValue(), buffer);
    }

    public static void glVertexAttrib4fv(int indx, FloatBuffer values) {
        glVertexAttrib4fv(indx, values, true);
    }

    public static void glVertexAttrib4fv(int indx, FloatBuffer values,
                                         boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib4fvBuf);
//		call.setIntArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, indx);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib4fv(indx, values);
    }

    public static void glVertexAttrib4fv(ENG_Integer indx, FloatBuffer values) {
        glVertexAttrib4fv(indx, values, true, true);
    }

    public static void glVertexAttrib4fv(ENG_Integer indx, FloatBuffer values,
                                         boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttrib4fvBuf);
//		call.setIntObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, indx, copy);
//		call.setBuffer(0, values, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttrib4fv(indx.getValue(), values);
    }

    public static void glVertexAttribPointer(int indx, int size, int type,
                                             boolean normalized, int stride, Buffer ptr) {
        glVertexAttribPointer(indx, size, type, normalized, stride, ptr, true);
    }

    public static void glVertexAttribPointer(int indx, int size, int type,
                                             boolean normalized, int stride, Buffer ptr, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttribPointerBuf);
//		call.setIntArrayCount(4);
//		call.setBooleanArrayCount(1);
//		call.setBufferCount(1);
//		call.setIntArrayParam(0, indx);
//		call.setIntArrayParam(1, size);
//		call.setIntArrayParam(2, type);
//		call.setIntArrayParam(3, stride);
//		call.setBooleanArrayParam(0, normalized);
//		call.setBuffer(0, ptr, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }

    public static void glVertexAttribPointer(ENG_Integer indx, ENG_Integer size,
                                             ENG_Integer type, ENG_Boolean normalized, ENG_Integer stride, Buffer ptr) {
        glVertexAttribPointer(indx, size, type, normalized, stride, ptr, true,
                true);
    }

    public static void glVertexAttribPointer(ENG_Integer indx, ENG_Integer size,
                                             ENG_Integer type, ENG_Boolean normalized, ENG_Integer stride, Buffer ptr,
                                             boolean copy, boolean copyData) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttribPointerBuf);
//		call.setIntObjCount(4);
//		call.setBooleanObjCount(1);
//		call.setBufferCount(1);
//		call.setIntObjParam(0, indx, copy);
//		call.setIntObjParam(1, size, copy);
//		call.setIntObjParam(2, type, copy);
//		call.setIntObjParam(3, stride, copy);
//		call.setBooleanObjParam(0, normalized, copy);
//		call.setBuffer(0, ptr, copyData);
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttribPointer(indx.getValue(), size.getValue(),
                type.getValue(), normalized.getValue(), stride.getValue(), ptr);
    }

    public static void glVertexAttribPointer(int indx, int size, int type,
                                             boolean normalized, int stride, int offset) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttribPointer);
//		call.setIntArrayCount(5);
//		call.setBooleanArrayCount(1);
//		
//		call.setIntArrayParam(0, indx);
//		call.setIntArrayParam(1, size);
//		call.setIntArrayParam(2, type);
//		call.setIntArrayParam(3, stride);
//		call.setIntArrayParam(4, offset);
//		call.setBooleanArrayParam(0, normalized);
//		
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
    }

    public static void glVertexAttribPointer(ENG_Integer indx, ENG_Integer size,
                                             ENG_Integer type, ENG_Boolean normalized, ENG_Integer stride,
                                             ENG_Integer offset) {
        glVertexAttribPointer(indx, size, type, normalized, stride, offset, true);
    }

    public static void glVertexAttribPointer(ENG_Integer indx, ENG_Integer size,
                                             ENG_Integer type, ENG_Boolean normalized, ENG_Integer stride,
                                             ENG_Integer offset, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glVertexAttribPointer);
//		call.setIntObjCount(5);
//		call.setBooleanObjCount(1);
//		
//		call.setIntObjParam(0, indx, copy);
//		call.setIntObjParam(1, size, copy);
//		call.setIntObjParam(2, type, copy);
//		call.setIntObjParam(3, stride, copy);
//		call.setIntObjParam(4, offset, copy);
//		call.setBooleanObjParam(0, normalized, copy);
//		
//		glCallQueue.add(call);
        Gdx.gl20.glVertexAttribPointer(indx.getValue(), size.getValue(),
                type.getValue(), normalized.getValue(), stride.getValue(),
                offset.getValue());
    }

    public static void glViewport(int x, int y, int width, int height) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glViewport);
//		call.setIntArrayCount(4);
//		call.setIntArrayParam(0, x);
//		call.setIntArrayParam(1, y);
//		call.setIntArrayParam(2, width);
//		call.setIntArrayParam(3, height);
//		glCallQueue.add(call);
        Gdx.gl20.glViewport(x, y, width, height);
    }

    public static void glViewport(ENG_Integer x, ENG_Integer y,
                                  ENG_Integer width, ENG_Integer height) {
        glViewport(x, y, width, height, true);
    }

    public static void glViewport(ENG_Integer x, ENG_Integer y,
                                  ENG_Integer width, ENG_Integer height, boolean copy) {
//		GLESCall call = new GLESCall();
//		call.setCall(GLCallList.glViewport);
//		call.setIntObjCount(4);
//		call.setIntObjParam(0, x, copy);
//		call.setIntObjParam(1, y, copy);
//		call.setIntObjParam(2, width, copy);
//		call.setIntObjParam(3, height, copy);
//		glCallQueue.add(call);
        Gdx.gl20.glViewport(x.getValue(), y.getValue(),
                width.getValue(), height.getValue());
    }


    /**
     * When the rendering is allowed issuing a request to render will render the
     * scene. The rendering may be blocked when a request is made because the user
     * is attempting to call a GL method that must return immediately. In this
     * case no rendering will be performed.
     *
     * @return The AtomicBoolean that allows or denies the rendering.
     */
    public static boolean getRenderingAllowed() {
        return renderingAllowed.get();
    }

    public static void setRenderingAllowed(boolean b) {
        renderingAllowed.set(b);
        //	System.out.println("setRenderingAllowed " + b);
    }

    /**
     * The call queue containing the issued GL calls. Note that not all issued
     * Gl calls are in this queue. Some calls are executed immediately by starting
     * a thread when the rendering has been requested with the rendering allowed
     * set on false.
     *
     * @return The GL call queue.
     * @noinspection deprecation
     */
    public static Queue<GLESCall> getGLCallQueue() {
        return glCallQueue;
    }

    /**
     * @param glRenderSurface the glRenderSurface to set
     * @noinspection deprecation
     */
    public static void setGlRenderSurface(GLRenderSurface glRenderSurface) {
        MTGLES20.glRenderSurface = glRenderSurface;
    }

    /**
     * @return the glRenderSurface
     * @noinspection deprecation
     */
    public static GLRenderSurface getGlRenderSurface() {
        return glRenderSurface;
    }
}
