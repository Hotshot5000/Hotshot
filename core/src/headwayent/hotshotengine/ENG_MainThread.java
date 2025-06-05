/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainActivity;
import headwayent.blackholedarksun.automationframework.AutomationFramework;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.gamestatedebugger.Frame;
import headwayent.blackholedarksun.gamestatedebugger.FrameDeserializer;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;
import headwayent.hotshotengine.statedebugger.ENG_Frame;
import headwayent.hotshotengine.statedebugger.ENG_FrameInterval;
import headwayent.hotshotengine.statedebugger.ENG_State;
import headwayent.hotshotengine.vfs.ENG_FileUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;
import static headwayent.hotshotengine.ENG_Utility.currentTimeMillisReal;

public class ENG_MainThread extends Thread {

    /*
     * #define MAXIMUM_FRAME_RATE 120
#define MINIMUM_FRAME_RATE 15
#define UPDATE_INTERVAL (1.0 / MAXIMUM_FRAME_RATE)
#define MAX_CYCLES_PER_FRAME (MAXIMUM_FRAME_RATE / MINIMUM_FRAME_RATE)

void runGame() {
  static double lastFrameTime = 0.0;
  static double cyclesLeftOver = 0.0;
  double currentTime;
  double updateIterations;

  currentTime = GetCurrentTime();
  updateIterations = ((currentTime - lastFrameTime) + cyclesLeftOver);

  if (updateIterations > (MAX_CYCLES_PER_FRAME * UPDATE_INTERVAL)) {
    updateIterations = (MAX_CYCLES_PER_FRAME * UPDATE_INTERVAL);
  }

  while (updateIterations > UPDATE_INTERVAL) {
    updateIterations -= UPDATE_INTERVAL;

    updateGame(); /* Update game state a variable number of times
  }

  cyclesLeftOver = updateIterations;
  lastFrameTime = currentTime;

  drawScene(); /* Draw the scene only once
}
     */
    public static final boolean AUTOMATION = true;
    public static final double MAXIMUM_FRAME_RATE = MainApp.Platform.isMobile() ? 30.0 : 100.0;
    public static final double MINIMUM_FRAME_RATE = 1.0;
    public static final double UPDATE_INTERVAL = (1.0 / MAXIMUM_FRAME_RATE);
    public static final double MIN_INV = (1.0 / MINIMUM_FRAME_RATE);
    public static final int SERVER_SLEEP = 16;
    private static ENG_MainThread mainThread;
    private double lastFrameTime;
    private double cyclesLeftOver;
    //	private MainApp main;
    private ENG_GameDescription gameDesc;
    private final AtomicBoolean shouldPause = new AtomicBoolean();
    private final AtomicBoolean shouldStop = new AtomicBoolean();
    private final AtomicInteger updateRate = new AtomicInteger();
    private final AtomicInteger frameRate = new AtomicInteger();
    private final Semaphore pause = new Semaphore(0);
    private CountDownLatch startGame = new CountDownLatch(1);
    private CountDownLatch pauseGame;
    private final ConcurrentLinkedQueue<ENG_IRunOnMainThread> runners = new ConcurrentLinkedQueue<>();
    private CountDownLatch resumeGlThread;
    private final ReentrantLock resumeGlThreadLock = new ReentrantLock();
    private final TreeMap<String, AutomationFramework> automationList = new TreeMap<>();
    //	private boolean shouldPause;
//    private boolean shouldStop;
    private CountDownLatch stopGame;
    private final ReentrantLock stopGameLock = new ReentrantLock();
    private long pauseBeginTime;
    private long pauseTimeMillis;
    private AsyncTask<Integer, Integer, String> asyncTask;
    private String inputStateFile;
    private boolean inputState;
    private boolean outputDebuggingStateEnabled;
    private DataOutputStream outputDebuggingStateWriter;
    private ENG_State debuggingState;
    private ENG_Frame.FrameFactory frameFactory;
    private ENG_FrameInterval.FrameIntervalFactory frameIntervalFactory;
    private ApplicationStartSettings applicationSettings;
    private DataInputStream inputReader;
    private final ENG_FileUtils.Compression compression = ENG_FileUtils.Compression.NONE;
    private long beginFrameTime;
    private long endFrameTime;
    private boolean realtimePlayback = true;
    private final Queue<ENG_IMainThreadSequentialWorker> workerQueue = new LinkedList<>();
    private long currentElapsedTime;
    private boolean pauseBeginTimeSet;

    public void addWorker(ENG_IMainThreadSequentialWorker worker) {
        workerQueue.add(worker);
    }

    public void clearWorkerQueue() {
        workerQueue.clear();
    }


    public boolean isAutomationEnabled(String automationName) {
        return AUTOMATION && automationList.containsKey(automationName);
    }

    public void setParameterForAutomation(String automationName,
                                          String name, Object obj) {
        if (MainActivity.isDebugmode() && AUTOMATION) {
            AutomationFramework framework = getAutomation(automationName);
            framework.setParameter(name, obj);
        }
    }

    public void setParametersForAutomation(String automationName,
                                           TreeMap<String, Object> paramList) {
        if (MainActivity.isDebugmode() && AUTOMATION) {
            AutomationFramework framework = getAutomation(automationName);
            framework.setParameters(paramList);
        }
    }

    private AutomationFramework getAutomation(String automationName) {
        AutomationFramework automationFramework = automationList.get(automationName);
        if (automationFramework == null) {
            throw new IllegalArgumentException(automationName + " is an invalid automation");
        }
        return automationFramework;
    }

    public void addAutomation(AutomationFramework a) {
        if (MainActivity.isDebugmode() && AUTOMATION && !MainApp.getMainThread().isInputState()) {
            AutomationFramework put = automationList.put(a.getName(), a);
            if (put != null) {
                throw new IllegalArgumentException(a.getName() + "already exists");
            }
        }
    }

    public void removeAutomation(String name) {
        if (MainActivity.isDebugmode() && AUTOMATION && !MainApp.getMainThread().isInputState()) {
            AutomationFramework remove = automationList.remove(name);
            if (remove == null) {
                throw new IllegalArgumentException(name + " is not a valid automation");
            }
        }
    }

    public void removaAllAutomations() {
        automationList.clear();
    }

    public void runOnMainThread(ENG_IRunOnMainThread run) {
        runners.add(run);
    }

    public void allowGameStart() {
        startGame.countDown();
    }

    public ENG_MainThread() {
        setName("MainGameThread");
        //	main = MainActivity.getInstance();
        //	gameDesc = MainApp.getGame();

//        resetThreadLocals();
    }

    public void resetThreadLocals() {
        Looper.resetMainLooper();
        Looper.prepareMainLooper();
    }

    public ApplicationStartSettings getApplicationSettings() {
        return applicationSettings;
    }

    public void setApplicationSettings(ApplicationStartSettings settings) {
        applicationSettings = settings;
        if (settings.uncaughtExceptionHandler != null) {
//			UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(settings.uncaughtExceptionHandler);
            System.out.println("uncaughtExceptionHandler set");
        }
    }

    public void setGameDesc(ENG_GameDescription gameDesc) {
        this.gameDesc = gameDesc;
    }

    public void pauseGame() {
        System.out.println("Game paused attempt");
//        pauseGame = new CountDownLatch(1);
//        try {
//            pause.acquire();
//        } catch (InterruptedException e) {
//
//            System.err.println("Could not pause game!");
//            e.printStackTrace();
//        }
        shouldPause.set(true);
        setPauseBeginTime();
    }

    public void resumeGame() {
        addPauseTimeMillis();
        shouldPause.set(false);
        System.out.println("Game resumed attempt");
        pause.release();
    }

    public boolean getShouldPause() {
        return shouldPause.get();
    }

    public void setShouldStop() {
        stopGameLock.lock();
        try {
            stopGame = new CountDownLatch(1);
            shouldStop.set(true);
        } finally {
            stopGameLock.unlock();
        }
    }

    public boolean getShouldStop() {
        return shouldStop.get();
    }

    private void waitForGLSurfaceCreation() {
        try {
            startGame.await();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    private void recreateStartGameCountDownLatch() {
        startGame = new CountDownLatch(1);
    }

    public void waitForMainThreadPause() {
        try {
            pauseGame.await();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public void waitForMainThreadToExitMainLoop() {
        try {
            stopGame.await();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    private int update = 0;
    private int frame = 0;
    private long beginUpdate = currentTimeMillisReal();
    private long endUpdate = 0;
    private long beginFrame = currentTimeMillisReal();
    private long deltaLastTime = currentTimeMillisReal();
    private long frameNum = 0;



    private int currentFrameNum;

    private long beginFrameDiff;
    private long beginFrameDiffMain;

    @Override
    public void run() {
//        try {
//        System.out.println("Started run()");
//		waitForGLSurfaceCreation();
//		recreateStartGameCountDownLatch();
//		gameDesc.gameStart();

//		UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

//		if (frameNum == 0) {
//			throw new NullPointerException("bla bla");
//		}

//        if (ENG_Utility.getRandom() == null) {
//            ENG_Utility.createRandomNumberGenerator();
//        }

//        long endFrameDiffMain = ENG_Utility.currentTimeMillis();
//        System.out.println("Frame diff Main: " + (endFrameDiffMain - beginFrameDiffMain));
//        beginFrameDiffMain = endFrameDiffMain;

//        long beginFrameTime = ENG_Utility.currentTimeMillis();


//		double currentTime = 0.0;
//		double updateIterations = 0.0;
//		int update = 0;
//		int frame = 0;
//		long beginUpdate = System.currentTimeMillis();
//        long endUpdate = 0;
//        long beginFrame = System.currentTimeMillis();
//        long endFrame = 0;
//        int frameNum = 0;
        long lastGCTime = 0;
        long averageCycleTime = ENG_Utility.currentTimeMillis();
        final int averageStepNum = 100;
        int currentStep = 0;
            while (true) {
//                if ((++currentStep) == averageStepNum) {
//                    currentStep = 0;
//                    long currentTime = ENG_Utility.currentTimeMillis();
//                    System.out.println("average FrameTime: " + ((currentTime - averageCycleTime) / (float) averageStepNum));
//                    averageCycleTime = currentTime;
//                }

//                if (ENG_Utility.hasTimePassed(lastGCTime, 5000)) {
//                    lastGCTime = ENG_Utility.currentTimeMillis();
////                    System.gc();
//                    System.out.println("System.gc() duration: " +
//                            (ENG_Utility.currentTimeMillis() - lastGCTime) +  " java heap: " + Gdx.app.getJavaHeap() + " native heap: " + Gdx.app.getNativeHeap());
//                }

//                long mainLoopBeginTime = currentTimeMillis();
//                if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
//                    if (MainApp.getGame().isGameEnded()) {
//                        // Since we are exiting we can simply kill the process. No need for the nothing rendered shit.
//                        break;
//                    }
//                }
                if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(SERVER_SLEEP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (shouldStop.get()) {
                    // If we are on the server we must make sure that we wait for the whole pipeline
                    // to be flushed.
                    if (MainApp.getApplicationMode() == MainApp.Mode.SERVER) {
                        ENG_RenderingThread.setOnNothingRendered(new ENG_RenderingThread.OnNothingRendered() {

                            private boolean called;

                            @Override
                            public boolean nothingRendered() {
                                // We can reach this even before the following flushPipeline().
                                // That flushPipeline() is just for safety.
                                // Since it could happen multiple times we must guard for that.
                                // Only the first call of nothingRendered() makes sense.
                                if (called) {
                                    return false;
                                }
                                // THEY JUST LET US OUT OF THE CAGEEEEE! Overwrite the OnNothingRendered
                                // and then just release the lock. When the whole slow calls have
                                // finished we will get on the new OnNothingRendered.
                                // At which point we just kill everything including the calls
                                // to the rendering thread renderFrame().
                                ENG_RenderingThread.setOnNothingRendered(new ENG_RenderingThread.OnNothingRendered() {

                                    private boolean called;

                                    @Override
                                    public boolean nothingRendered() {
                                        if (called) {
                                            return false;
                                        }
                                        // We must interrupt the rendering thread that is now probably
                                        // blocked.
                                        ENG_RenderingThread.interruptBlockingOfRenderingThread();
                                        // We are running these from the rendering thread,
                                        // but the main thread is dead by now so there is no
                                        // chance of race condition. Just make sure there are no
                                        // thread locals going on.
                                        MainApp.getGame().endGame();
                                        MainApp.getGame().exitGame();
                                        called = true;
                                        return true;
                                    }
                                });
                                // Right now we are on the rendering thread. We cannot make calls
                                // to slow execution calls from here, only from the main thread.
                                ENG_RenderRoot.getRenderRoot().destroy();
                                renderScene();

                                called = true;
                                return false;
                            }
                        });
                        // Send a fake flush just to make sure we are not blocking and will never
                        // hit the OnNothingRendered event.
                        renderScene();
                    }
                    break; // Only used to get out of the multiplayer server side loop.
                }
                while (shouldPause.get()) {

                    try {
                        System.out.println("Game paused");
                        // Tell everybody interested that the main thread has been paused
//                        pauseGame.countDown();
                        // Init the latch for the gl thread
//                        resumeGlThreadLock.lock();

                        System.out.println("acquiring pause");
                        pause.acquire();
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
//                    pause.release();
                    addPauseTimeMillis();
                    System.out.println("Game resumed pauseTimeMillis: " + pauseTimeMillis);
//                    try {
//                        resumeGlThread = new CountDownLatch(1);
//                    } finally {
//                        resumeGlThreadLock.unlock();
//                    }
                    if (gameDesc.areResourcesCreated()) {
                        // Make sure the glThread is up
//                        waitForGLSurfaceCreation();
//                        recreateStartGameCountDownLatch();
//                        gameDesc.reloadResources();
                        //	gameDesc.waitForResourcesToBeRecreated();
                    } else {
//                        MTGLES20.clearGlQueue();
//                        resumeGlThread.countDown();
                        // Since we are unable to continue without resources
                        // we might as well get out now
                        //		    		setShouldStop();
                    }
                }

                gameDesc.preinitialize();
                ENG_Frame currentFrame = null;
                if (outputDebuggingStateEnabled) {
                    currentFrame = frameFactory.createFrame(debuggingState);
                    currentFrame.setFrameNum(currentFrameNum);
                    currentFrame.setFrameTime(currentTimeMillis());
                    if (currentFrameNum > 0) {
                        // Save the delta from previous frame
                        currentFrame.setTimeDelta(currentFrame.getFrameTime() - debuggingState.getCurrentFrame().getFrameTime());
                    }
                    debuggingState.addFrame(currentFrame);
                    debuggingState.setCurrentFrame(currentFrame);
                    System.out.println("added debugging frame: " + currentFrameNum);
                }
                if (inputState) {
//            if (ENG_Utility.getRandom() == null) {
//                ENG_Utility.setWriteableRandom(true);
//                if (debuggingState != null) {
//                    registerStateListeners(debuggingState);
//                }
//            }
                    currentFrame = debuggingState.getNextFrame();
                    if (currentFrame != null) {
                        System.out.println("Reading input state frame: " + currentFrame.getFrameNum());
                    }
                }

                gameDesc.startGame(currentFrame);
                if (!gameDesc.isGameStarted() && gameDesc.isLoadingScreenShown()) {
                    updateOutputDebuggingState(currentFrame);
                    return;
                }
                ENG_IMainThreadSequentialWorker worker = workerQueue.poll();
                if (worker != null) {
                    worker.run();
                    if (MainApp.getGame().isSkipMainThread()) {
                        return;
                    }
                }
//		System.out.println("game Started");
                if (lastFrameTime == 0.0) {
                    lastFrameTime = (double) currentTimeMillis();
                }

                long time = currentTimeMillis();

                // Run everything queued from other threads
                runQueuedCommands();

                runAutomations();

                //		checkAsyncTask();

                //			new Handler(Looper.getMainLooper()).post(new Runnable() {
                //				@Override
                //				public void run() {
                //					System.out.println("Message ran");
                //				}
                //			});
                Looper.loop();



                if (inputState) {
                    // We simulate every update interval so no need to actually measure the real deltas
                    //			debuggingState.readInputFrames();
                    //			int frameListSize = debuggingState.getFrameListSize();
                    //			if (currentFrameNum < frameListSize) {
                    if (debuggingState.isLastFrameReached()) {
                        setInputState(false);
                    } else {
                        ENG_Frame frame = currentFrame == null ? debuggingState.getNextFrame() : currentFrame;//debuggingState.getFrame(currentFrameNum);
                        frame.setState(debuggingState);
                        debuggingState.setCurrentFrame(frame);
                        //                System.out.println("Frame num: " + frame.getFrameNum());
                        long frameTimeDiff = endFrameTime - beginFrameTime;
                        if (realtimePlayback && frame.getFrameTime() > frameTimeDiff) {
                            long frameTimeDelta = frame.getFrameTime() - frameTimeDiff;
                            if (frameTimeDelta > 0) {
                                System.out.println("Sleeping at frame " + currentFrameNum + " for " + frameTimeDelta + " millis");
                                try {
                                    //noinspection BusyWait
                                    Thread.sleep(frameTimeDelta);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //                System.out.println("NEW FRAME: " + frame.getFrameNum());
                        beginFrameTime = currentTimeMillis();
                        int frameIntervalListSize = frame.getFrameIntervalListSize();
                        long beginFrameIntervalTime = 0;
                        long endFrameIntervalTime = 0;

                        for (int i = 0; i < frameIntervalListSize; ++i) {
                            //                    System.out.println("NEW FRAME INTERVAL FOR FRAME: " + frame.getFrameNum() + " FRAME INTERVAL: " + i);
                            ENG_FrameInterval frameInterval = frame.getFrameInterval(i);
                            frame.setCurrentFrameInterval(frameInterval);
                            long timeDiff = endFrameIntervalTime - beginFrameIntervalTime;
                            if (realtimePlayback && frameInterval.getCurrentTime() > timeDiff) {
                                long timeDelta = frameInterval.getCurrentTime() - timeDiff;
                                System.out.println("Sleeping at frameInterval " + i + " for " + timeDelta + " millis");
                                try {
                                    //noinspection BusyWait
                                    Thread.sleep(timeDelta);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            sendInputData();
                            beginFrameIntervalTime = currentTimeMillis();
                            gameDesc.gameLoop(endUpdate, UPDATE_INTERVAL, UPDATE_INTERVAL, frame);
                            endFrameIntervalTime = currentTimeMillis();
                        }
                    }
                    //			}
                } else {

                    //            if (outputDebuggingStateEnabled) {
                    //                ENG_FrameInterval frameInterval = frameIntervalFactory.createFrameInterval(1);
                    //                currentFrame.addFrameInterval(frameInterval);
                    //                frameInterval.setCurrentTime(currentTimeMillis());
                    ////            if (gameLoopRunCount > 0) {
                    ////                // Save only the delta from previous frame interval
                    ////                frameInterval.setTimeDelta(frameInterval.getCurrentTime() - currentFrame.getCurrentFrameInterval().getCurrentTime());
                    ////            }
                    //                currentFrame.setCurrentFrameInterval(currentFrame.getFrameInterval(currentFrame.getFrameIntervalListSize() - 1));
                    //            }
                    //
                    //            gameDesc.gameLoop(currentTimeMillis(), UPDATE_INTERVAL, currentFrame);
                    //            long endFrameDiff = ENG_Utility.currentTimeMillis();
                    //            System.out.println("Frame diff: " + (endFrameDiff - beginFrameDiff));
                    //            beginFrameDiff = endFrameDiff;

                    double currentTime = (double) currentTimeMillis();
                    double updateIterations = ((currentTime - lastFrameTime) + cyclesLeftOver) * 0.001;

                    //	System.out.println("updateIterations: " + updateIterations);
                    if (updateIterations > MIN_INV) {
                        updateIterations = MIN_INV;
                    }
                    int gameLoopRunCount = 0;
                    //            if (updateIterations <= UPDATE_INTERVAL) {
                    //                System.out.println("NOT ENTERING MAIN LOOP");
                    //            } else {
                    //                System.out.println("ENTERING MAIN LOOP");
                    //            }




//                    while (updateIterations > UPDATE_INTERVAL) {
                        if (outputDebuggingStateEnabled) {
                            ENG_FrameInterval frameInterval = frameIntervalFactory.createFrameInterval(gameLoopRunCount);
                            //noinspection DataFlowIssue
                            currentFrame.addFrameInterval(frameInterval);
                            frameInterval.setCurrentTime(currentTimeMillis());
                            if (gameLoopRunCount > 0) {
                                // Save only the delta from previous frame interval
                                frameInterval.setTimeDelta(frameInterval.getCurrentTime() - currentFrame.getCurrentFrameInterval().getCurrentTime());
                            }
                            currentFrame.setCurrentFrameInterval(currentFrame.getFrameInterval(currentFrame.getFrameIntervalListSize() - 1));
                        }

//                      long sendInputDataBeginTime = currentTimeMillis();
                        sendInputData();
//                      long sendInputDataEndTime = currentTimeMillis();
//                      System.out.println("sendInputData() time: " + (sendInputDataEndTime - sendInputDataBeginTime));

                        updateIterations -= UPDATE_INTERVAL;
                        endUpdate = currentTimeMillis();
                        if ((endUpdate - beginUpdate) < 1000) {
                            ++update;
                        } else {
                            beginUpdate = currentTimeMillis();
                            setUpdateRate(update);
                            //					System.out.println("update rate: " + update);
                            update = 0;
                        }
                        //                if (gameLoopRunCount == 0) {
                        //                    System.out.println("New frame");
                        //                }
//                                        System.out.println("Running gameLoop()");
                        //                long beginFrameTime = ENG_Utility.currentTimeMillis();
                    long deltaCurrentTime = currentTimeMillis();
                        long elapsed = deltaCurrentTime - deltaLastTime;
//                        System.out.println("elapsed: " + elapsed);
                        currentElapsedTime = elapsed;
                        gameDesc.gameLoop(endUpdate, UPDATE_INTERVAL, elapsed, currentFrame);
//                        long endFrameDiff = ENG_Utility.currentTimeMillis();
                        deltaLastTime = currentTimeMillis();
//                        System.out.println("GameLoop time: " + (endFrameDiff - deltaCurrentTime));
//                        beginFrameDiff = endFrameDiff;
                        //                long endDiff = ENG_Utility.currentTimeMillis() - beginFrameTime;
                        //                System.out.println("Frame time: " + endDiff);
                        //                System.out.println("gameLoop() run");
                        ++gameLoopRunCount;
                        // No longer need to run the game loop multiple times if the rendering happens
                        // on a different thread. Also, the physics engine now runs the simulation so no need
                        // for a finer granularity.
//                        break;
//                    }
//                    System.out.println("gameLoopRunCount: " + gameLoopRunCount);

                    cyclesLeftOver = updateIterations;
                    lastFrameTime = currentTime;
                }

                if (applicationSettings.applicationMode == MainApp.Mode.CLIENT) {
                    //Here we render the scene
//                    long beginFrameTime = ENG_Utility.currentTimeMillis();

//                    GLUtility.checkForGLSLError(
//                            "GLSLLinkProgram::GLSLLinkProgram",
//                            "Error Before creating GLSL Program Object");
                    //            System.out.println("Rendering frame");
//                    System.out.println("renderScene called");
                    renderScene();
//                    long endDiff = ENG_Utility.currentTimeMillis() - beginFrameTime;
//                    System.out.println("RenderScene() time: " + endDiff);
//                    System.out.println("rendered scene");
                }

                updateOutputDebuggingState(currentFrame);
                updateInputState();
                closeDebuggingStreamsIfExiting();
//                System.out.println("UPDATING FRAMENUM: " + frameNum);
                frameNum++;
                //	    	System.out.println("RENDERING DONE NUM " + (frameNum));

                long endFrame = currentTimeMillis();
                if ((endFrame - beginFrame) < 1000) {
                    ++frame;
                } else {
                    beginFrame = currentTimeMillis();
                    setFrameRate(frame);
                    //				System.out.println("FrameRate: " + frame);
                    frame = 0;

                }

//                long mainLoopEndTime = currentTimeMillis();
//                System.out.println("Full mainLoop time: " + (mainLoopEndTime - mainLoopBeginTime));

//        System.out.println("Finished run()");

//        long endDiff = ENG_Utility.currentTimeMillis() - beginFrameTime;
//        System.out.println("Frame time: " + endDiff);
//			System.out.println("MainThread renderScene frame time: " +
//					(System.currentTimeMillis() - time));
            }
            // Nullify everything
            //	MainApp.setGame(null);
            // Disable these on non threaded rendering
    /*	MainApp.setMainThread(null);
        MTGLES20.setGlRenderSurface(null);
		stopGameLock .lock();
		try {
			stopGame.countDown();
		} finally {
			stopGameLock.unlock();
		}*/
            // No more GL calls from here on
//		gameDesc.gameEnd();
//		gameDesc.endGame();
//        } catch (Throwable e) {
//            e.printStackTrace();
//            // This is important to have so the UncaughtExceptionHandler gets a chance to do its thing.
//            throw e;
//        }

    }

    private void sendInputData() {
        if ((gameDesc.areResourcesCreated() || gameDesc.isIgnoreResourcesCreated()) &&
                !gameDesc.isReloadingResources() && !gameDesc.isLoadingScreenShown()) {
            ENG_InputManager.getSingleton().sendInputData();
        }
    }

    private void updateInputState() {
        if (inputState) {
            endFrameTime = currentTimeMillis();
            ++currentFrameNum;
        }
    }

    private void updateOutputDebuggingState(ENG_Frame currentFrame) {
        if (outputDebuggingStateEnabled) {
            debuggingState.writeFrame(currentFrame);
            ++currentFrameNum;
        }
    }

    public long getFrameNum() {
        return frameNum;
    }

    private void closeDebuggingStreamsIfExiting() {
        if (MainApp.getGame().isExiting()) {
            if (outputDebuggingStateEnabled) {
                try {
                    outputDebuggingStateWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputState) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkAsyncTask() {
        if (asyncTask == null) {
            asyncTask = new AsyncTask<Integer, Integer, String>() {

                @Override
                protected String doInBackground(Integer... params) {
                    int i = 0;
                    for (int p : params) {
                        publishProgress(p);
                        i += p;
                    }
                    return "AsyncTask " + i;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                    System.out.println(values[0]);
                }

                @Override
                protected void onPostExecute(String aVoid) {
                    super.onPostExecute(aVoid);
                    System.out.println(aVoid);
                }
            };
            asyncTask.execute(1, 2, 3);
        }
    }

    private void runAutomations() {
        if (MainActivity.isDebugmode() && AUTOMATION) {
            for (AutomationFramework a : automationList.values()) {
                a.execute();
            }
        }
    }

    public void runQueuedCommands() {
        ENG_IRunOnMainThread thread;
        while ((thread = runners.poll()) != null) {
            thread.run();
        }
    }

    private void renderScene() {
//        System.out.println("RENDER SCENE CALLED");
//        ENG_RenderRoot.getRenderRoot().renderOneFrame();
//        long prepareForRenderingBeginTime = ENG_Utility.currentTimeMillis();
        ENG_RenderRoot renderRoot = ENG_RenderRoot.getRenderRoot();
        renderRoot.prepareForRenderingOneFrameNative();
//        long prepareForRenderingEndTime = ENG_Utility.currentTimeMillis();
//        System.out.println("prepareForRendering time: " + (prepareForRenderingEndTime - prepareForRenderingBeginTime));
        // Temporary shit. Change it to better state whether we should render a frame each call or not.
//        long allResourcesBeginTime = ENG_Utility.currentTimeMillis();
        if (MainApp.getGame().getGameState() == APP_Game.GameState.ALL_RESOURCES_LOADED) {
            ENG_NativeCalls.callRoot_RenderOneFrame(renderRoot.getPointer());
            ENG_NativeCalls.getItemsAabbs();
            renderRoot.prepareForRenderingOneFrameNativeLastCallBeforeFlush();
        }
//        long allResourcesEndTime = ENG_Utility.currentTimeMillis();
//        System.out.println("allResources time: " + (allResourcesEndTime - allResourcesBeginTime));
//		flushGLPipeline(true);
        // Update the pipeline data and send it to native code.
//        long flushPipelineBeginTime = ENG_Utility.currentTimeMillis();
        ENG_RenderingThread.flushPipeline();
//        long flushPipelineEndTime = ENG_Utility.currentTimeMillis();
//        System.out.println("flushPipeline time: " + (flushPipelineEndTime - flushPipelineBeginTime));
    }

    public int getUpdateRate() {
        return updateRate.get();
    }

    private void setUpdateRate(int update) {
        updateRate.set(update);
    }

    public int getFrameRate() {
        return frameRate.get();
    }

    private void setFrameRate(int frame) {
        frameRate.set(frame);
    }

    public CountDownLatch getResumeGlThread() {
        return resumeGlThread;
    }

    public void resetResumeGlThread() {
        resumeGlThread = null;
    }

    public ReentrantLock getResumeGlThreadLock() {
        return resumeGlThreadLock;
    }

    public long getPauseTimeMillis() {
        return pauseTimeMillis;
    }

    public void addPauseTimeMillis() {
        pauseBeginTimeSet = false;
        long pauseTimeMillisDiff = currentTimeMillisReal() - pauseBeginTime;
        this.pauseTimeMillis += pauseTimeMillisDiff;
        System.out.println("setPauseTimeMillis() pauseTimeMillis: " + this.pauseTimeMillis + " pauseBeginTime: " + (pauseBeginTime / 1000) + " pauseTimeMillisDiff: " + pauseTimeMillisDiff);
    }

    public long getPauseBeginTime() {
        return pauseBeginTime;
    }

    public void setPauseBeginTime() {
        // If we are already skipping time (for example we are in the
        // in game menu and all is paused)  we must add those seconds before
        // moving the application in background.
        if (pauseBeginTimeSet) {
            addPauseTimeMillis();
        }
        pauseBeginTimeSet = true;
        this.pauseBeginTime = currentTimeMillisReal();
        System.out.println("setPauseBeginTime() pauseBeginTime: " + (pauseBeginTime / 1000));
    }

    public void setInputStateFile(String inputStateFile) {
        this.inputStateFile = inputStateFile;
    }

    public String getInputStateFile() {
        return inputStateFile;
    }

    public void setInputState(boolean inputState) {
        this.inputState = inputState;

        if (inputState && inputStateFile == null) {
            throw new IllegalStateException("Must set input file before starting to playback from file");
        }
        if (inputState) {
            MainApp.setOutputDebuggingApplicationStateEnabled(false);
            setOutputDebuggingStateEnabled(false);
            createDebuggingState();
            inputReader = ENG_FileUtils.createBufferedInputStream(inputStateFile, compression);
            debuggingState.setInputReader(inputReader);
        } else {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputReader = null;
            }

            if (debuggingState != null) {
                unregisterStateListeners(debuggingState);
            }
            ENG_Utility.setWriteableRandom(false);
            debuggingState = null;
        }

    }

    public boolean isInputState() {
        return inputState;
    }

    public void setOutputDebuggingStateEnabled(boolean outputDebuggingStateEnabled) {
        if (this.outputDebuggingStateEnabled != outputDebuggingStateEnabled) {
            if (outputDebuggingStateEnabled) {
                String outputDebuggingStatePathAndFilename = gameDesc.getOutputDebuggingStatePathAndFilename();
                if (outputDebuggingStatePathAndFilename != null) {
                    outputDebuggingStateWriter = ENG_FileUtils.createBufferedOutputStream(
                            outputDebuggingStatePathAndFilename,
                            ENG_FileUtils.FileCreationMode.INCREMENT,
                            compression, true);
                    createDebuggingState();
                    debuggingState.setOutputWriter(outputDebuggingStateWriter);
                }
            } else {
                if (outputDebuggingStateWriter != null) {
                    try {
                        outputDebuggingStateWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    outputDebuggingStateWriter = null;
                }
                ENG_Utility.setWriteableRandom(false);
                if (debuggingState != null) {
                    unregisterStateListeners(debuggingState);
                }
                debuggingState = null;
            }

        }
        this.outputDebuggingStateEnabled = outputDebuggingStateEnabled;
    }

    private void createDebuggingState() {
        debuggingState = new ENG_State(new ENG_State.TypeAndDeserializer(Frame.class, new FrameDeserializer()));
    }

    private void registerStateListeners(ENG_State state) {
        state.registerOnFrameIntervalChangedListener((ENG_State.OnFrameIntervalChangeListener) ENG_Utility.getRandom());
    }

    private void unregisterStateListeners(ENG_State state) {
        state.unregisterOnFrameIntervalChangedListener((ENG_State.OnFrameIntervalChangeListener) ENG_Utility.getRandom());
    }

    public void registerFrameFactory(ENG_Frame.FrameFactory frameFactory) {
        this.frameFactory = frameFactory;
    }

    public ENG_Frame.FrameFactory getFrameFactory() {
        return frameFactory;
    }

    public ENG_FrameInterval.FrameIntervalFactory getFrameIntervalFactory() {
        return frameIntervalFactory;
    }

    public void registerFrameIntervalFactory(ENG_FrameInterval.FrameIntervalFactory intervalFactory) {
        this.frameIntervalFactory = intervalFactory;
    }

    public ENG_State getDebuggingState() {
        return debuggingState;
    }

    public boolean isRealtimePlayback() {
        return realtimePlayback;
    }

    public void setRealtimePlayback(boolean realtimePlayback) {
        this.realtimePlayback = realtimePlayback;
    }

    public long getCurrentElapsedTime() {
        return currentElapsedTime;
    }
}
