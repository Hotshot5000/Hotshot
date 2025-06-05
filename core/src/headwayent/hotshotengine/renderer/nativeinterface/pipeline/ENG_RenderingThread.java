/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer.nativeinterface.pipeline;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.ENG_Log;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.android.util.Log;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.nativeinterface.test.TestRenderingThread;

/**
 * Created by sebas on 10.02.2017.
 * This is actually the UI thread (the main application thread) that we use to render things
 * that are setup by the main thread (which is created in the main application thread).
 * The main thread is the main thread of the game, not the 'real' main thread. The 'real' main
 * thread is the rendering thread.
 */

public final class ENG_RenderingThread {

    private static final int WRITING_BUFFER_SIZE_IN_BYTES = 65536;
    public static final int READING_BUFFER_SIZE_IN_BYTES = 65536;
    public static final int BUFFER_COUNT = 3;
    private static final int RENDERED_FRAME_ADDED_PER_FRAME_COUNT = 1024;
    private static final int END_FRAME_LISTENER_COUNT = 10;
    private static final boolean ALLOW_CONTINUOUS_RENDERING_CHANGING = true;
    public static final int RENDER_FRAME_SLEEP_MILLIS = 2;
    public static final int RENDER_ATTEMPTS = 100 / RENDER_FRAME_SLEEP_MILLIS;

    //    private static ENG_RenderingThread renderingThread;
    private static final ReentrantLock slowCallLock = new ReentrantLock();
    private static ENG_SlowCall currentSlowCall;
    private static ByteBuffer writingBuffer;
    private static ByteBuffer readingBuffer;
//    private static ByteBuffer zeroBuffer;
    private static Semaphore permits;
    private static final Semaphore blockMainThreadSemaphore = new Semaphore(0);
    private static final Semaphore blockRenderingThreadSemaphore = new Semaphore(0);
    private static final Semaphore blockMainThreadForEndFrameListenerSemaphore = new Semaphore(0);
    private static final ReentrantLock permitsLock = new ReentrantLock();
    private static final ReentrantLock renderedFrameListenerListLock = new ReentrantLock();
//    private static ReentrantLock blockRenderingThreadLock = new ReentrantLock();
//    private static ReentrantLock endFrameListenerListLock = new ReentrantLock();
    private static final ReentrantLock mainThreadBlockedForEndFrameLock = new ReentrantLock();
//    private static ReentrantLock renderingThreadBlockedLock = new ReentrantLock();
    private static final ReentrantLock sleepRenderingThreadLock = new ReentrantLock();
    private static final Semaphore sleepRenderingThreadSemaphore = new Semaphore(0);
    private static boolean shouldSleepRenderingThread;
    private static final ArrayList<ENG_RenderedFrameListener> addedRenderedFrameListenerList = new ArrayList<>(RENDERED_FRAME_ADDED_PER_FRAME_COUNT);
    private static final ArrayList<ENG_RenderedFrameListener> addedToEndRenderedFrameListenerList = new ArrayList<>(RENDERED_FRAME_ADDED_PER_FRAME_COUNT);
    private static final ArrayList<ENG_RenderedFrameListener> blockingRenderedFrameListenerList = new ArrayList<>(RENDERED_FRAME_ADDED_PER_FRAME_COUNT);
    private static ArrayList<ENG_RenderedFrameListener> renderedFrameListenerList;
    // If we want to verify if a value has been updated by the native system, we register a frame listener
    // that continuously checks if the value has been updated after every rendered frame.
    // When it has been finally updated, we remove the listener.
    private static final ArrayList<ENG_IEndFrameListener> endFrameListenerList = new ArrayList<>();
    private static int writingBufferSizeInBytes;
    private static int readingBufferSizeInBytes;
    private static int bufferCount;
    private static int currentWriteableBuffer;
    private static int currentReadableBuffer;
    private static OnNothingRendered onNothingRendered;
    private static final ReentrantLock onNothingRenderedLock = new ReentrantLock();
//    private static AtomicBoolean renderOneFrame = new AtomicBoolean();
    private static boolean repeatRendering;
    private static boolean mainThreadBlocked;
    private static boolean mainThreadBlockedForEndFrame;
//    private static boolean forceBlockMainThread;
    private static byte currentFrameEndId;
    private static boolean renderRootInitialized;
    private static boolean blockRenderingThread;
    private static boolean initialized;
//    private static boolean renderingThreadBlocked;

    public interface OnNothingRendered {
        /**
         *
         * @return true if we should exit the rendering loop and not continue with
         * the retryCount.
         */
        boolean nothingRendered();
    }

    public static void initialize() {
        initialize(WRITING_BUFFER_SIZE_IN_BYTES, READING_BUFFER_SIZE_IN_BYTES, BUFFER_COUNT);
    }

    public static void initialize(int writingBufferSizeInBytes, int readingBufferSizeInBytes, int bufferCount) {
        if (initialized) {
            return;
        }
//        if (renderingThread == null) {
//            renderingThread = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
        writingBuffer = ENG_Utility.allocateDirectMemoryAligned(bufferCount * writingBufferSizeInBytes, 1)[0];
        readingBuffer = ENG_Utility.allocateDirectMemoryAligned(readingBufferSizeInBytes, 1)[0];
//        zeroBuffer = ENG_Utility.allocateDirect(writingBufferSizeInBytes);
        ENG_RenderingThread.permits = new Semaphore(bufferCount - 1);
        ENG_RenderingThread.writingBufferSizeInBytes = writingBufferSizeInBytes;
        ENG_RenderingThread.readingBufferSizeInBytes = readingBufferSizeInBytes;
        ENG_RenderingThread.bufferCount = bufferCount;
        ENG_RenderingThread.renderedFrameListenerList = new ArrayList<>(RENDERED_FRAME_ADDED_PER_FRAME_COUNT * bufferCount);
        ENG_NativeCalls.initialize(writingBufferSizeInBytes);
        initializeNative(writingBufferSizeInBytes, readingBufferSizeInBytes, bufferCount);
        initialized = true;
    }

    public static void executeSlowCall(ENG_SlowCall slowCall) {
        executeSlowCall(slowCall, false);
    }

    public static void executeSlowCall(ENG_SlowCall slowCall, boolean runningFromRenderingThread) {
        ENG_SlowCall currentSlowCallLocal;
        slowCallLock.lock();
        try {
            currentSlowCall = slowCall;
            currentSlowCallLocal = slowCall;
        } finally {
            slowCallLock.unlock();
        }
        sleepRenderingThreadLock.lock();
        try {
            if (shouldSleepRenderingThread) {
                shouldSleepRenderingThread = false;
                sleepRenderingThreadSemaphore.release();
            }
        } finally {
            sleepRenderingThreadLock.unlock();
        }

        // UNCOMMENT THIS IF WE SOMEHOW ADD RENDER_WHEN_DIRTY_MODE BACK.
//        ENG_RenderRoot renderRoot = ENG_RenderRoot.getRenderRoot();
//        renderRoot.requestRenderingIfRequired();


        if (!runningFromRenderingThread) {
            currentSlowCallLocal.awaitExecution();
        }
//        if (renderRoot.isContinuousRendering()) {
//
//        } else {
//
//        }
    }

//    /**
//     * For now you can only add one end frame listener before blocking the main thread.
//     * @param endFrameListener
//     */
//    public static void addEndFrameListener(ENG_IEndFrameListener endFrameListener) {
//        addEndFrameListener(endFrameListener, false);
//    }

    /**
     * For now you can only add one end frame listener before blocking the main thread.
     * @param endFrameListener
     * @param containsRenderOneFrameCall if the request contains a full render request. If this
     *                                   is just a temporary request we need to wait in the
     *                                   rendering thread until a real render frame call comes.
     *                                   This should be set to false if you just need to use
     *                                   the rendering thread to initialize some things on the
     *                                   native side that you will quickly need in the main thread.
     *                                   Be aware that false will introduce a pipeline stall.
     *                                   (NO LONGER USED)
     */
    public static void addEndFrameListener(ENG_IEndFrameListener endFrameListener,
//                                           boolean containsRenderOneFrameCall,
                                           boolean dataAlreadyFlushed) {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("addEndFrameListener dataAlreadyFlushed: " + dataAlreadyFlushed);
        }
//        endFrameListenerListLock.lock();
//        try {
            endFrameListenerList.add(endFrameListener);
//        } finally {
//            endFrameListenerListLock.unlock();
//        }
//        writeBufferEnding();
//        checkAndReleaseRenderingThread(containsRenderOneFrameCall);
//        blockRenderingThreadLock.lock();
//        try {
            blockRenderingThread = !dataAlreadyFlushed;
//        } finally {
//            blockRenderingThreadLock.unlock();
//        }

        if (!dataAlreadyFlushed) {
            System.out.println("flushPipeline called from addEndFrameListener");
            flushPipeline(false, false);
        } else {
            setMainThreadBlockedForEndFrame(true);
            blockMainThreadForEndFrame();
        }
//        setBlockMainThreadForEndFrame(dataAlreadyFlushed);
    }

    private static boolean updateEndFrameListeners() {
//        endFrameListenerListLock.lock();
//        try {
            int initialSize = endFrameListenerList.size();
            if (initialSize == 0) {
                return false;
            }
            // The main thread is now blocked so there is no race condition waiting to happen
            // if we change things from the rendering thread.
            // No need to queue the commands on the main thread and run it from there.
            // We still need to run the queued commands since the rendering thread answers
            // have been added to the queue but not executed.
            MainApp.getMainThread().runQueuedCommands();
            for (Iterator<ENG_IEndFrameListener> it = endFrameListenerList.iterator(); it.hasNext(); ) {
                ENG_IEndFrameListener frameListener = it.next();
                if (frameListener.frameEnded()) {
                    it.remove();
                }
            }

//            MainApp.getMainThread().runOnMainThread(new ENG_IRunOnMainThread() {
//                @Override
//                public void run() {
//                    for (Iterator<ENG_IEndFrameListener> it = endFrameListenerList.iterator(); it.hasNext(); ) {
//                        ENG_IEndFrameListener frameListener = it.next();
//                        if (frameListener.frameEnded()) {
//                            it.remove();
//                        }
//                    }
//                }
//            });
//            MainApp.getMainThread().runQueuedCommands();
            return initialSize > 0 && endFrameListenerList.isEmpty();
//        } finally {
//            endFrameListenerListLock.unlock();
//        }
    }

    public static void addFrameEndListener(ENG_RenderedFrameListener listener) {
        addFrameEndListener(listener, false);
    }

    /**
     *
     * @param listener
     * @param addToEnd for example if you need to execute a ray query after the frame has been rendered
     *                 but you are calling execute() from java earlier than reanderOneFrame() and flushPipeline()
     *                 the execute() call on the native side is actually pushed after all the elements in the buffer
     *                 have been executed. Which also means that we must push the frame end listener to the back
     *                 of the array for when work comes back from the native side.
     *                 This is not foolproof unfortunately: what happens if you need to execute a ray query,
     *                 you move it to the end of the array and call flushPipeline() without renderOneFrame() ?
     *                 For example you need a quick flush to get some data back in some (very) special case (note to self:
     *                 don't do flushPipeline() calls during gameplay without a renderOneFrame() before). In this case,
     *                 the frame end listeners will be written to in the native side but you will get an error as
     *                 the scene graph hasn't been yet updated in order to get the latest aabbs so you can execute
     *                 the ray query. In order to differentiate between pipeline flushes that contained a renderOneFrame()
     *                 and those that didn't, on the native side there will be a boolean that tells us if there was a renderOneFrame()
     *                 call. Based on this, still on the native side, the frame end listener will decide if it will write the values back.
     *                 If it can't execute its function because there was no renderOneFrame(), then we will be notified on the java side
     *                 in the frameId value (0x80 & frameId tells us if there was a renderOneFrame() or not). Based on this info
     *                 we can either execute the frame end listener callback or leave it for the next frame, where, hopefully, there will
     *                 be a renderOneFrame() call. If there still is no renderOneFrame() call we simply let it in the array until there is one.
     *                 If addToEnd is true then ENG_RenderedFrameListener should also have waitForRender set to true so we can check
     *                 if (frameId & 0x80) is true, if it cares about executing after rendering one frame.
     *
     *                 The addToEnd param has been moved to the RenderedFrameListener.
     */
    private static void addFrameEndListener(ENG_RenderedFrameListener listener, boolean addToEnd) {
        if (MainActivity.isDebugmode()) {
            if (listener.waitForRender) {
                if (addedToEndRenderedFrameListenerList.size() >= RENDERED_FRAME_ADDED_PER_FRAME_COUNT) {
                    throw new IllegalArgumentException("addedToEndRenderedFrameListenerList is already full");
                }
                if (addedToEndRenderedFrameListenerList.size() >= RENDERED_FRAME_ADDED_PER_FRAME_COUNT / 4) {
                    System.out.println("WARNING: addedToEndRenderedFrameListenerList.size(): " + addedToEndRenderedFrameListenerList.size());
                }
            } else {
                if (addedRenderedFrameListenerList.size() >= RENDERED_FRAME_ADDED_PER_FRAME_COUNT) {
                    throw new IllegalArgumentException("addedRenderedFrameListenerList is already full");
                }
                if (addedRenderedFrameListenerList.size() >= RENDERED_FRAME_ADDED_PER_FRAME_COUNT / 4) {
                    System.out.println("WARNING: addedRenderedFrameListenerList.size(): " + addedRenderedFrameListenerList.size());
                }
            }
        }
//        if (TestRenderingThread.TEST_ENABLE_TRACING) {
//            System.out.println("addedRenderedFrameListenerList.size(): " + addedRenderedFrameListenerList.size());
//        }
        if (TestRenderingThread.TEST_ENABLE_TRACING && listener.getListenerName() != null) {
            System.out.println("addFrameEndListener: " + listener.getListenerName());
        }
        // We need this separation so we add the end frame listeners at the actual end of the addedRenderedFrameListenerList.
        if (listener.waitForRender) {
            addedToEndRenderedFrameListenerList.add(listener);
            if (TestRenderingThread.TEST_ENABLE_TRACING) {
                System.out.println("addedToEndRenderedFrameListenerList.size(): " + addedToEndRenderedFrameListenerList.size());
            }
        } else {
            addedRenderedFrameListenerList.add(listener);
        }
    }

    private static void prepareFrameEndListeners(boolean waitForRenderingToFinish) {
        if (!addedRenderedFrameListenerList.isEmpty() || !addedToEndRenderedFrameListenerList.isEmpty()) {
            int inFramePos = 0;
            addedRenderedFrameListenerList.addAll(addedToEndRenderedFrameListenerList);
            for (ENG_RenderedFrameListener frameListener : addedRenderedFrameListenerList) {
                frameListener.setFrameId(currentFrameEndId);
                frameListener.setInFramePos(inFramePos++);
            }
            writeByte(ENG_NativeCalls.NativeCallsList.FRAME_ID_POS.getCallPos());
            writeByte(currentFrameEndId);
            renderedFrameListenerListLock.lock();
            try {
                renderedFrameListenerList.addAll(addedRenderedFrameListenerList);
                if (waitForRenderingToFinish) {
                    blockingRenderedFrameListenerList.addAll(addedRenderedFrameListenerList);
                }
            } finally {
                renderedFrameListenerListLock.unlock();
            }
            addedRenderedFrameListenerList.clear();
            addedToEndRenderedFrameListenerList.clear();
        } else {
            writeBufferEnding();

        }
    }

    private static void writeBufferEnding() {
        // We still need a way to tell that the end has been reached. Since the buffer
        // might already contain data of longer length than the current pipeline flush
        // there might be other commands that will be wrongly executed.
        // To avoid that we add a 0 which signals that the buffer has ended and that we
        // must return from the native thread.
        writeByte((byte) 0);
    }

    private static void updateFrameEndListeners(ByteBuffer responseBuffer) {
        boolean removed = false;
        // The first byte is the frame id. We start reading from the next one.
        responseBuffer.position(0);
        byte frameId = responseBuffer.get();
        renderedFrameListenerListLock.lock();
        try {
            for (Iterator<ENG_RenderedFrameListener> it = renderedFrameListenerList.iterator(); it.hasNext(); ) {
                ENG_RenderedFrameListener frameListener = it.next();
                ENG_RenderedFrameListener.ReturnValue returnValue = frameListener.checkFrameId(responseBuffer, frameId);
                if (returnValue == ENG_RenderedFrameListener.ReturnValue.TRUE) {
                    it.remove();
                    if (!removed) {
                        if (!blockingRenderedFrameListenerList.isEmpty() && blockingRenderedFrameListenerList.contains(frameListener)) {
                            // The response here is the response we have been waiting for in order
                            // to unblock the main thread. Getting one means we have them all, so no
                            // need to check all of them.
                            removed = true;
                            blockingRenderedFrameListenerList.clear();
                        }
                    }
                    if (TestRenderingThread.TEST_ENABLE_TRACING && frameListener.getListenerName() != null) {
                        System.out.println("frameListener: " + frameListener.getListenerName() +
                                " returnValue: " + returnValue + " responseBuffer.position(): " + responseBuffer.position());
                    }
                } else if (returnValue == ENG_RenderedFrameListener.ReturnValue.WAITING_FOR_RENDERING) {
                    // Increment the frameId for which we are waiting. Only for debugging purposes. The actual setting
                    // of the frame Id happens in prepareFrameEndListeners().
                    int currentFrameId = frameListener.getFrameId();
                    if ((++currentFrameId) > bufferCount) {
                        currentFrameId = 1;
                    }
//                    frameListener.setFrameId(currentFrameId);
                    // If we have a frame end listener waiting for rendering that means it's one of those
                    // 'add to the end of the frame queue' listeners. So we remove it from here and add it again like
                    // we would normally add it the first time.
                    it.remove();
                    addFrameEndListener(frameListener);
                    if (TestRenderingThread.TEST_ENABLE_TRACING && frameListener.getListenerName() != null) {
                        System.out.println("WAITING_FOR_RENDERING incremented FrameId to " + currentFrameId +
                                " for frameListener: " + frameListener.getListenerName() +
                                " responseBuffer.position(): " + responseBuffer.position());
                    }
                }
            }

        } finally {
            renderedFrameListenerListLock.unlock();
        }
//        if (updateEndFrameListeners()) {
//            removed = true;
//        }
        if (removed) {
            releaseMainThreadBlock();
        }
    }

    private static void releaseMainThreadBlock() {
        permitsLock.lock();
        try {
            if (mainThreadBlocked) {
                blockMainThreadSemaphore.release();
                mainThreadBlocked = false;
                printTestMainThreadUnblocked();
            }
        } finally {
            permitsLock.unlock();
        }
    }

    private static void blockMainThreadForEndFrame() {
        try {
            blockMainThreadForEndFrameListenerSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void releaseMainThreadForEndFrameBlock() {
        blockMainThreadForEndFrameListenerSemaphore.release();
    }

    private static void printTestMainThreadUnblocked() {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("mainThread released");
        }
    }

    private static void printTestMainThreadBlocked(boolean acquireAllPermits) {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            if (acquireAllPermits) {
//                System.out.println("mainThread blocked in generic acquire");
            }
        }
    }

    private static void printTestMainThreadBlocked2() {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
//            System.out.println("mainThread blocked in main block");
        }
    }

    private static void incrementCurrentFrameEndId() {
        if ((++currentFrameEndId) > bufferCount) {
            currentFrameEndId = 1;
        }
    }

    private static void incrementCurrentWriteableBuffer() {
        if ((++currentWriteableBuffer) >= bufferCount) {
            currentWriteableBuffer = 0;
        }
//        int newPosition = currentWriteableBuffer * writingBufferSizeInBytes;
        writingBuffer.position(currentWriteableBuffer * writingBufferSizeInBytes);
//        ENG_Log.getInstance().log("currentWriteableBuffer: " + currentWriteableBuffer);
//        writingBuffer.put(zeroBuffer);
//        zeroBuffer.position(0);
//        writingBuffer.position(newPosition);
    }

    private static void incrementCurrentReadableBuffer() {
        if ((++currentReadableBuffer) >= bufferCount) {
            currentReadableBuffer = 0;
        }
    }

    public static void writeBytes(ByteBuffer src) {
        if (MainActivity.isDebugmode()) {
            if (src.remaining() > writingBufferSizeInBytes) {
                throw new IllegalArgumentException(src.remaining() + " too large for writingBuffer of size: " + writingBufferSizeInBytes);
            }
            if (src.remaining() > (writingBufferSizeInBytes * (currentWriteableBuffer + 1) - writingBuffer.position())) {
                throw new IllegalArgumentException(src.remaining() + " too large for the remaining of the writingBuffer of size: " + writingBufferSizeInBytes);
            }
        }
        int originalPos = src.position();
        writingBuffer.put(src);
        src.position(originalPos);
    }

    public static void writeByte(byte b) {
        writingBuffer.put(b);
    }

    public static void writeShort(short s) {
        writingBuffer.putShort(s);
    }

    public static void writeInt(int i) {
        writingBuffer.putInt(i);
    }

    public static void writeLong(long l) {
        // Fix alignment issue.
        alignMemory();
        writingBuffer.putLong(l);
    }

    public static void writeFloat(float f) {
        writingBuffer.putFloat(f);
    }

    public static void writeDouble(double d) {
        // Fix alignment issue.
        alignMemory();
        writingBuffer.putDouble(d);
    }

    public static void writeBoolean(boolean b) {
        writingBuffer.put((byte) (b ? 1 : 0));
    }

    public static void alignMemory() {
        ENG_Utility.alignMemory(writingBuffer, 4);
//        for (int i = 0; i < align; ++i) {
//            writingBuffer.put((byte) 0);
//        }
    }
    
    public static void flushPipeline() {
        flushPipeline(false);
    }

    public static void flushPipeline(boolean waitForRenderingToFinish) {
        flushPipeline(waitForRenderingToFinish, true);
    }

    private static void flushPipeline(boolean waitForRenderingToFinish, boolean dataAlreadyFlushed) {
//        System.out.println("FLUSHPIPELINE CALLED --------------");
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("flushPipeline(" + waitForRenderingToFinish + ", " + dataAlreadyFlushed + ")");
        }

        boolean shouldBlock = false;
        checkPipelineOverflow();
        sleepRenderingThreadLock.lock();
        try {
            if (shouldSleepRenderingThread) {
                shouldSleepRenderingThread = false;
//                System.out.println("Releasing rendering thread");
                sleepRenderingThreadSemaphore.release();
            }

            // Even if we are rendering a buffer with no renderOneFrame() call and we have a blocked
            // rendering thread, we still need to release it even if we will block it again.
            // Without that, we don't actually get to executing the half-filled buffer so we will wait
            // forever.
            checkAndReleaseRenderingThread(dataAlreadyFlushed);
            incrementCurrentFrameEndId();
            prepareFrameEndListeners(waitForRenderingToFinish);
    //        Gdx.graphics.requestRendering();
            shouldBlock = acquirePermit(waitForRenderingToFinish);
        } finally {
            sleepRenderingThreadLock.unlock();
        }
        // If this acquire causes a block then it should be outside of the sleepRenderingThreadLock in order to avoid deadlock.
        if (shouldBlock) {
            long shouldBlockBeginTime;
            try {
//                System.out.println("shouldBlock permits acquired.");
//                shouldBlockBeginTime = ENG_Utility.currentTimeMillis();
                permits.acquire();
//                System.out.println("shouldBlockTime: " + (ENG_Utility.currentTimeMillis() - shouldBlockBeginTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (waitForRenderingToFinish) {
            printTestMainThreadBlocked2();
            acquireMainThreadBlock();
        }
        if (!dataAlreadyFlushed) {
            blockMainThreadForEndFrame();
        }
        incrementCurrentWriteableBuffer();
    }

    private static void checkAndReleaseRenderingThread(boolean dataAlreadyFlushed) {
//        boolean blockRenderingThreadLocal = false;
//        blockRenderingThreadLock.lock();
//        try {
            if (blockRenderingThread) {
                if (TestRenderingThread.TEST_ENABLE_TRACING) {
                    System.out.println("Releasing rendering thread. " +
                            (dataAlreadyFlushed ? "Data already flushed. Resetting blockRenderingThread" :
                                    "Data not flushed. blockRenderingThread remains true"));
                }
//                blockRenderingThreadLocal = true;
                if (dataAlreadyFlushed) {
                    blockRenderingThread = false;
                } else {
                    setMainThreadBlockedForEndFrame(true);
                }
//            if (isRenderingThreadBlocked()) {
//                setRenderingThreadBlocked(false);

//            }
                blockRenderingThreadSemaphore.release();
            }
//        } finally {
//            blockRenderingThreadLock.unlock();
//        }
//        if (blockRenderingThreadLocal) {
//            blockRenderingThreadSemaphore.release();
//        }
    }

    private static void checkPipelineOverflow() {
        // The last 2 bytes of a buffer cannot be used as they are needed in the native part
        // to determine when the end of the buffer has been reached.
        if (writingBufferSizeInBytes * (currentWriteableBuffer + 1) - writingBuffer.position() < 2) {
            throw new IllegalStateException("The pipeline has overflown with: "
                    + (writingBuffer.position() - writingBufferSizeInBytes * (currentWriteableBuffer + 1) + 2)
                    + " bytes!");
        }
    }

//    private static void setBlockMainThreadForEndFrame(boolean dataAlreadyFlushed) {
////        permitsLock.lock();
////        try {
////            mainThreadBlocked = true;
////        } finally {
////            permitsLock.unlock();
////        }
////        acquireMainThreadBlock();
//        setMainThreadBlockedForEndFrame(true);
//        if (!dataAlreadyFlushed) {
//            flushPipeline(false, false);
//        } else {
//            blockMainThreadForEndFrame();
//        }
//
//    }

    /**
     *
     * @param acquireAllPermits
     * @return true if should block the main thread since all permits have been acquired. This is needed
     * in order to avoid blocking while sleepRenderingLock still active and causing a deadlock.
     */
    private static boolean acquirePermit(boolean acquireAllPermits) {
        boolean shouldBlock = false;
        permitsLock.lock();
        try {
            if (acquireAllPermits) {
                mainThreadBlocked = true;
            }
        } finally {
            permitsLock.unlock();
        }
        // We keep acquiring permits until blocking. If however this would have been over the
        // last permit but acquireAllPermits is true, we block at permits.acquire() not at
        // block.acquire(). When the rendering thread finishes it first checks if the blocking
        // call has a response and if not, it calls permits.release() that result in the main
        // thread being blocked in the following block.acquire(). The block.acquire() is
        // released only when the FrameEndedListener is the one corresponding to the blocking call.
        // If the corresponding FrameEndedListener is first found then block.release() is called,
        // and after that permits.release(), which mean that block.acquire() in the main
        // thread is allowed to fall through.
        printTestMainThreadBlocked(acquireAllPermits);
        shouldBlock = acquirePermit();
//        if (acquireAllPermits) {
//            printTestMainThreadBlocked2();
//            acquireMainThreadBlock();
//        }
        return shouldBlock;
    }

    private static void acquireMainThreadBlock() {
        try {
            blockMainThreadSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return true if this was the last permit and we need to block later. False otherwise.
     */
    private static boolean acquirePermit() {
        try {
            if (permits.availablePermits() > 0) {
                permits.acquire();
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void releasePermit() {
        permits.release();
    }

    public static void renderFrame() {

        // If we happen to block the main thread while waiting for a result to come back
        // from the rendering thread, once that result comes in we can resume the main thread
        // execution. But, while continuing the main thread's execution, until we reach the first
        // flushPipeline() call, the rendering thread might come again and have nothing to render
        // which means we will get a useless eglSwapBuffers() that does no good.
        // In order to avoid that, if we have nothing to render, we insert a wait with a timeout,
        // to give a chance to the main thread to reach a flushPipeline() call.
        // This makes sense only with continuous rendering, but we can't be sure that
        // rendering when dirty actually works as advertised so, until proven otherwise,
        // we will enable it for all cases.
        boolean renderedSomething;
        boolean mainThreadBlockedForEndFrame;
//        boolean blockRenderingThreadLocal;
        int renderAttempts = RENDER_ATTEMPTS;
        do {
//            testSleepRenderingThread();

//            blockRenderingThreadLocal = isBlockRenderingThread();
            renderedSomething = false;
            boolean slowCallExecuted = false;
            if ((permits.availablePermits() < bufferCount - 1)/* || blockMainThread*/) {
                // We have flushed the pipeline at least once and we have work to do.
                runTestCode();
//                long beginRenderOneFrame = ENG_Utility.currentTimeMillis();
                renderOneFrame(writingBuffer, readingBuffer, currentReadableBuffer);
//                System.out.println("renderOneFrame time: " + (ENG_Utility.currentTimeMillis() - beginRenderOneFrame));
                if (TestRenderingThread.TEST_ENABLE_TRACING) {
                    System.out.println("currentReadableBuffer: " + currentReadableBuffer);
                }
                incrementCurrentReadableBuffer();
                updateFrameEndListeners(readingBuffer);
                releasePermit();
                renderedSomething = true;

            } else {
//                System.out.println("Nothing to render");
                // Check if we have a slow call when idle.
                slowCallExecuted = runSlowCall();
                renderedSomething = slowCallExecuted;
//                renderedSomething = automateRendering(renderedSomething);


            }

            mainThreadBlockedForEndFrame = isMainThreadBlockedForEndFrame();
            boolean shouldDecrementRenderAttempts = !mainThreadBlockedForEndFrame;
//            blockRenderingThreadLocal = isBlockRenderingThread();
            if (mainThreadBlockedForEndFrame) {
                if (updateEndFrameListeners()) {
                    boolean blockRenderingThreadLocal = ENG_RenderingThread.blockRenderingThread;
//                    if (blockRenderingThreadLocal) {
//                        setRenderingThreadBlocked(true);
//                    }
//                    releaseMainThreadBlock();
                    printTestReleasingMainThread();
                    setMainThreadBlockedForEndFrame(false);
                    releaseMainThreadForEndFrameBlock();
                    // We must find a way to guarantee that the rendering thread gets released
                    // eventually.
                    printTestBlockRenderingThreadLocal(blockRenderingThreadLocal);
                    // We only block the rendering thread if !dataAlreadyFlushed.
                    if (blockRenderingThreadLocal) {
                        printTestRenderingThreadBlocked();
                        try {
                            blockRenderingThreadSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        printTestRenderingThreadReleased();
//                        ENG_RenderingThread.blockRenderingThread = false;
                    } else {
                        // If we have dataAlreadyFlushed then we don't block the rendering thread.
                        // In this case we don't want to be looping the rendering thread.
                        mainThreadBlockedForEndFrame = false;
                    }
                }
            }
//            if (blockMainThread || slowCallExecuted) {
                renderedSomething = automateRendering(renderedSomething);
//            }
            if (!renderedSomething) {
//                printTestSleepRenderingThread();
//                ENG_Utility.sleep(RENDER_FRAME_SLEEP_MILLIS);
                if (blockRenderingThread()) {
                    // We are forcing the loop to break ignoring the renderAttempts.
                    renderedSomething = true;
                }
            }

            // If mainThreadBlockedForEndFrame don't decrement the renderAttempts.
            if (shouldDecrementRenderAttempts) {
                --renderAttempts;
            }

//            printTestRenderAttempts(renderAttempts);

//            printTestMainThreadBlockedForEndFrame(renderedSomething, mainThreadBlockedForEndFrame, shouldDecrementRenderAttempts);

            checkTestEnd();

        } while (!renderedSomething && ((renderAttempts > 0) || (mainThreadBlockedForEndFrame))
                /*&& ENG_RenderRoot.getRenderRoot().isContinuousRendering()*/
                );

        // If we are currently on continuous rendering and switch to render when dirty we
        // cannot just switch to it before we make sure that we have flushed the pipeline and
        // all the slow calls have happened.
        if (ALLOW_CONTINUOUS_RENDERING_CHANGING) {
            checkShouldSwitchRenderingMode();
        }


    }

    /**
     * If we have nothing to render then we must wait so as to avoid a buffer swap in GL.
     * In order to do so while avoiding race conditions we only sleep if there is no flushPipeline
     * command running. For this we have the sleepRenderingThreadLock that makes permits.acquire()
     * and permits.availablePermits() mutually exclusive.
     * If in renderFrame where we first check permits.availablePermits() we reach the conclusion
     * that there is nothing to render then we check again here if the conditions have changed.
     * If they did then we just go around in the do {} while() for another go.
     * If the condition didn't change at the second check here but flushPipeline() is called
     * right after the check and lock have been released then we will end up blocking until
     * the sleepRenderingThreadSemaphore.release() is called in the flushPipeline().
     * In this case flushPipeline() knows that shouldSleepRenderingThread == true.
     */
    private static boolean blockRenderingThread() {
        boolean shouldBlockThread = false;
        sleepRenderingThreadLock.lock();
        try {
            if (permits.availablePermits() == bufferCount - 1 && !isSlowCallAvailable()) {
                shouldSleepRenderingThread = true;
                shouldBlockThread = true;
            }
        } finally {
            sleepRenderingThreadLock.unlock();
        }
        boolean avoidRetryCount = false;
        if (shouldBlockThread) {
            // Before blocking make sure we notify first.
            OnNothingRendered onNothingRendered = getOnNothingRendered();
            if (onNothingRendered != null) {
                avoidRetryCount = onNothingRendered.nothingRendered();
            }
            long blockRenderingThreadBeginTime;
            try {
//                System.out.println("blockRenderingThread waiting");
                blockRenderingThreadBeginTime = ENG_Utility.currentTimeMillis();
                sleepRenderingThreadSemaphore.acquire();
                System.out.println("blockedRenderingTime: " + (ENG_Utility.currentTimeMillis() - blockRenderingThreadBeginTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return avoidRetryCount;
    }

    private static void printTestMainThreadBlockedForEndFrame(boolean renderedSomething,
                                                              boolean mainThreadBlockedForEndFrame,
                                                              boolean shouldDecrementRenderAttempts) {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("mainThreadBlockedForEndFrame: " + (!shouldDecrementRenderAttempts) +
                    " real mainThreadBlockedForEndFrame: " + mainThreadBlockedForEndFrame +
                    " renderedSomething: " + renderedSomething);
        }
    }

    private static void printTestRenderAttempts(int renderAttempts) {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("renderAttempts: " + renderAttempts);
        }
    }

    private static void printTestSleepRenderingThread() {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("Sleeping rendering thread");
        }
    }

    private static void printTestRenderingThreadReleased() {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("Rendering thread released");
        }
    }

    private static void printTestRenderingThreadBlocked() {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("Rendering thread blocked");
        }
    }

    private static void printTestBlockRenderingThreadLocal(boolean blockRenderingThreadLocal) {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("blockRenderingThreadLocal: " + blockRenderingThreadLocal);
        }
    }

    private static void printTestReleasingMainThread() {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            System.out.println("Releasing main thread");
        }
    }

    private static void testSleepRenderingThread() {
        if (TestRenderingThread.TEST_ENABLE_TRACING) {
            ENG_Utility.sleep(1500);
        }
    }

//    private static boolean isBlockRenderingThread() {
//        blockRenderingThreadLock.lock();
//        try {
//            return blockRenderingThread;
//        } finally {
//            blockRenderingThreadLock.unlock();
//        }
//    }

    private static boolean isMainThreadBlocked() {
        permitsLock.lock();
        try {
            return mainThreadBlocked;
        } finally {
            permitsLock.unlock();
        }
    }

    private static boolean isMainThreadBlockedForEndFrame() {
        mainThreadBlockedForEndFrameLock.lock();
        try {
            return mainThreadBlockedForEndFrame;
        } finally {
            mainThreadBlockedForEndFrameLock.unlock();
        }
    }

    private static void setMainThreadBlockedForEndFrame(boolean b) {
        mainThreadBlockedForEndFrameLock.lock();
        try {
            mainThreadBlockedForEndFrame = b;
        } finally {
            mainThreadBlockedForEndFrameLock.unlock();
        }
    }

//    private static boolean isRenderingThreadBlocked() {
//        renderingThreadBlockedLock.lock();
//        try {
//            return renderingThreadBlocked;
//        } finally {
//            renderingThreadBlockedLock.unlock();
//        }
//    }
//
//    private static void setRenderingThreadBlocked(boolean renderingThreadBlocked) {
//        renderingThreadBlockedLock.lock();
//        try {
//            ENG_RenderingThread.renderingThreadBlocked = renderingThreadBlocked;
//        } finally {
//            renderingThreadBlockedLock.unlock();
//        }
//    }

    private static boolean automateRendering(boolean renderedSomething) {
        // Force a renderOneFrame() if we are showing a loading screen for example,
        // while doing other stuff in the background that require usage of the rendering thread.
        if (isAutomaticRenderOneFrameEnabled()) {
            ENG_RenderRoot.getRenderRoot().renderOneFrameSlowCall();
            renderedSomething = true;
        }
        return renderedSomething;
    }

    private static void checkShouldSwitchRenderingMode() {
        ENG_RenderRoot renderRoot;
        if (renderRootInitialized) {
            renderRoot = ENG_RenderRoot.getRenderRoot();
        } else {
            renderRoot = ENG_RenderRoot.getRenderRootWithLock();
            if (renderRoot == null) {
                return;
            } else {
                renderRootInitialized = true;
            }
        }

//        System.out.println("Checking continuous rendering");
        ReentrantLock continuousRenderingLock = ENG_RenderRoot.getContinuousRenderingLock();
        continuousRenderingLock.lock();
        try {
            if (!ENG_RenderRoot.isContinuousRenderingWithoutLock()) {
                if ((permits.availablePermits() == bufferCount - 1) && !isSlowCallAvailable()) {
                    ENG_RenderRoot.releaseContinuousRenderingLatch();
                }
            }
        } finally {
            continuousRenderingLock.unlock();
        }
//        System.out.println("Checked continuous rendering");
    }

    private static boolean runSlowCall() {
        slowCallLock.lock();
        try {
            if (currentSlowCall != null) {
                currentSlowCall.execute();
                currentSlowCall = null;
                return true;
            }
            return false;
        } finally {
            slowCallLock.unlock();
        }
    }

    private static boolean isSlowCallAvailable() {
        slowCallLock.lock();
        try {
            return currentSlowCall != null;
        } finally {
            slowCallLock.unlock();
        }
    }

    private static boolean isTrySlowCallAvailable() {
        boolean b = slowCallLock.tryLock();
        if (!b) {
            return false;
        }
        try {
            return currentSlowCall != null;
        } finally {
            slowCallLock.unlock();
        }
    }

    public static boolean isAutomaticRenderOneFrameEnabled() {
//        return renderOneFrame.get();
        return repeatRendering;
    }

    /**
     * Make sure this is called only from the rendering thread!
     * @param enabled
     */
    public static void setAutomaticRenderOneFrameEnabled(boolean enabled) {
//        renderOneFrame.set(enabled);
        repeatRendering = enabled;
    }

    private static void checkTestEnd() {
        if (TestRenderingThread.TEST) {
            TestRenderingThread testRenderingThread = TestRenderingThread.getSingleton();
            testRenderingThread.checkTestEnd();
        }
    }

    private static void runTestCode() {
        if (TestRenderingThread.TEST) {
            TestRenderingThread testRenderingThread = TestRenderingThread.getSingleton();
//            System.out.println("Waiting on render thread");
            synchronized (ENG_RenderingThread.class) {
                try {
                    long waitTime = testRenderingThread.getWaitTime();
                    if (waitTime > 0) {
                        ENG_RenderingThread.class.wait(waitTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println("Ended wait on render thread");
        }
    }

    public static int getBufferCount() {
        return bufferCount;
    }

    public static int getWritingBufferSizeInBytes() {
        return writingBufferSizeInBytes;
    }

    public static int getReadingBufferSizeInBytes() {
        return readingBufferSizeInBytes;
    }

    public static int getCurrentWriteableBuffer() {
        return currentWriteableBuffer;
    }

    public static OnNothingRendered getOnNothingRendered() {
        onNothingRenderedLock.lock();
        try {
            return onNothingRendered;
        } finally {
            onNothingRenderedLock.unlock();
        }
    }

    /**
     * This should only be called when you want to check that the pipeline is empty for the
     * end of the game. It should notify you when there is nothing more to render and is about
     * to go into blocking the rendering thread.
     * Use this only after making sure there are no more flushPipeline() calls (the main thread is dead).
     * @param onNothingRendered
     */
    public static void setOnNothingRendered(OnNothingRendered onNothingRendered) {
        onNothingRenderedLock.lock();
        try {
            ENG_RenderingThread.onNothingRendered = onNothingRendered;
        } finally {
            onNothingRenderedLock.unlock();
        }
    }

    public static void interruptBlockingOfRenderingThread() {
        sleepRenderingThreadLock.lock();
        try {
            if (shouldSleepRenderingThread) {
                shouldSleepRenderingThread = false;
                sleepRenderingThreadSemaphore.release();
            }
        } finally {
            sleepRenderingThreadLock.unlock();
        }
    }

    public static native void initializeNative(int readingBufferSizeInBytes, int writingBufferSizeInBytes, int bufferCount);
    public static native void renderOneFrame(ByteBuffer readingBuffer, ByteBuffer writingBuffer, int currentBuffer);

//    public static ENG_RenderingThread getSingleton() {
//        return renderingThread;
//    }
}
