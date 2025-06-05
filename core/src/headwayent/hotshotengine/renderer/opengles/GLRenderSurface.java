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
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.opengles.mtgles20.MTGLES20;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

@Deprecated
public class GLRenderSurface /*extends GLSurfaceView*/ {


    private static final boolean DEBUG = true;
    /** @noinspection deprecation*/
    private static GLRenderSurface glRenderSurface;
    private CountDownLatch renderingFinished;// = new CountDownLatch(1);
    private final ReentrantLock renderingFinishedLock = new ReentrantLock();
    private final ConcurrentLinkedQueue<Runnable> eventQueue = new ConcurrentLinkedQueue<>();
    private final ReentrantLock queueEventLock = new ReentrantLock();

    public GLRenderSurface(/*Context context*/) {
//		super(context);
        setup();

    }

//	public GLRenderSurface(/*Context context, AttributeSet attrs*/) {
//		super(context, attrs);
//		setup();

//	}

    /** @noinspection deprecation*/
    private void setup() {

        // Does not work it has a bug
        // http://stackoverflow.com/questions/9658863/android-opengl-es-2-0-setdebugflags-doesnt-do-anything
        // It does not work with opengl es 2.0
        //	setDebugFlags(DEBUG_LOG_GL_CALLS | DEBUG_CHECK_GL_ERROR);
//		this.setEGLContextClientVersion(2);
//		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//		getHolder().setFormat(PixelFormat.RGBA_8888); 

        setRenderSurface();
//		this.setRenderer(new GLMainThread());
        new GLMainThread();
//		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
//		Gdx.graphics.setContinuousRendering(false);
        //Is this actually necessary?
//		this.setFocusableInTouchMode(true);
//        this.requestFocus();
    }

    private void setRenderSurface() {
//		if (glRenderSurface == null) {
        glRenderSurface = this;
//		} else {
//			throw new ENG_MultipleSingletonConstructAttemptException("The render" +
//					" surface is already set!");
//		}
    }

    /** @noinspection deprecation*/
    public static GLRenderSurface getSingleton() {
        if (glRenderSurface == null && MainActivity.isDebugmode()) {
            throw new NullPointerException("glRenderSurface is not set");
        }
        return glRenderSurface;
    }

    /** @noinspection deprecation */
    public void requestRender(boolean addQueueEndCall) {
        if (addQueueEndCall) {
            // Add the queue delimiter between this and next frame
            MTGLES20.glNone();
        }
        // Must make this if atomic because it might be possible in the glThread
        // that the renderingContinued might not be set when creating a new latch
        // and then the latch gets counted down before having a chance to getting
        // to await in waitForRenderingToFinish()
        renderingFinishedLock.lock();
        try {
            if (!GLMainThread.isRenderingContinued()) {
                renderingFinished = new CountDownLatch(1);
                requestRender();
//				System.out.println("RENDERING REQUESTED");
            }
        } finally {
            renderingFinishedLock.unlock();
        }
    }

    private long lastTime = ENG_Utility.currentTimeMillis();

    public void requestRender() {
        
        ENG_RenderRoot.requestRenderingIfRequired();
        if (DEBUG) {
//			System.out.println("Requested rendering");
        }
        long currentTime = ENG_Utility.currentTimeMillis() - lastTime;
//		System.out.println("requestRender() time elapsed: " + currentTime);
        lastTime = ENG_Utility.currentTimeMillis();
    }

    public ReentrantLock getRenderingFinishedLock() {
        return renderingFinishedLock;
    }

/*	@Override
	public void requestRender() {
		
		
		
		
		super.requestRender();
	}*/

    public void waitForRenderingToFinish() {
        waitForRenderingToFinish(false);
    }

    /** @noinspection deprecation */
    public void waitForRenderingToFinish(boolean checkCallsAhead) {
        if (checkCallsAhead && MTGLES20.getNumCallsAhead() <= 1) {
            //	System.out.println("NO WAITING FOR RENDERING TO FINISH");
            return;
        }
        //	System.out.println("WAITING FOR RENDERING TO FINISH");
        try {
            renderingFinished.await();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        if (GLMainThread.isRenderingContinued()) {
            if (MainApp.DEV) {
                //	System.out.println("RENDERING IS CONTINUED!!!");
            }
            GLMainThread.resetRenderingContinued();
            MTGLES20.setRenderingAllowed(true);
            requestRender(false);
            // Allow to let one frame get behind us hoping it would speed up
            // sometime later
            waitForRenderingToFinish(true);
        } else {
            //	System.out.println("NO WAITING FOR RENDERING TO FINISH");
        }
    }

    protected void setRenderingFinished() {
        
        //	System.out.println("setRenderingFinished()");
        // After exiting the game and reentering it we flush the pipeline in
        // reloadResources(). But since we (might) have a new GLRenderSurface
        // and we haven't called waitForRenderingToFinish the renderingFinished
        // is null.
        if (renderingFinished != null) {
            renderingFinished.countDown();
        }
    }

    public void queueEvent(Runnable event) {
        
        queueEventLock.lock();
        try {
            eventQueue.add(event);
            if (DEBUG) {
                System.out.println("Added GLQueue Event. Current size: " + eventQueue.size());
            }
        } finally {
            queueEventLock.unlock();
        }
    }

    protected void runQueueEvents() {
        queueEventLock.lock();
        try {
            for (Runnable r : eventQueue) {
                r.run();
            }
            if (DEBUG) {
//				System.out.println("Cleared GLQueue. Processed size was: " + eventQueue.size());
            }
            eventQueue.clear();
        } finally {
            queueEventLock.unlock();
        }
    }

//	public void disableBufferSwap() {
//		Gdx.app.disableSwapBuffersAfterRendering();
//	}

}
