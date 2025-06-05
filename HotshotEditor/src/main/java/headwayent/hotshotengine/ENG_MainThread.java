package headwayent.hotshotengine;

import headwayent.hotshotengine.vfs.ENG_FileUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static headwayent.hotshotengine.ENG_Utility.currentTimeMillis;

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
    public static final double MAXIMUM_FRAME_RATE = 10.0f;//MainApp.Platform.isMobile() ? 30.0 : 100.0;
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
    //    private final TreeMap<String, AutomationFramework> automationList = new TreeMap<>();
    //	private boolean shouldPause;
//    private boolean shouldStop;
    private CountDownLatch stopGame;
    private final ReentrantLock stopGameLock = new ReentrantLock();
    private long pauseBeginTime;
    private long pauseTimeMillis;
    //    private AsyncTask<Integer, Integer, String> asyncTask;
    private String inputStateFile;
    private boolean inputState;
    private boolean outputDebuggingStateEnabled;
    private DataOutputStream outputDebuggingStateWriter;
    //    private ENG_State debuggingState;
//    private ENG_Frame.FrameFactory frameFactory;
//    private ENG_FrameInterval.FrameIntervalFactory frameIntervalFactory;
//    private ApplicationStartSettings applicationSettings;
    private DataInputStream inputReader;
    private final ENG_FileUtils.Compression compression = ENG_FileUtils.Compression.NONE;
    private long beginFrameTime;
    private long endFrameTime;
    private boolean realtimePlayback = true;
    private final Queue<ENG_IMainThreadSequentialWorker> workerQueue = new LinkedList<>();
    private long currentElapsedTime;

    public void addWorker(ENG_IMainThreadSequentialWorker worker) {
        workerQueue.add(worker);
    }

    public void clearWorkerQueue() {
        workerQueue.clear();
    }


//    public boolean isAutomationEnabled(String automationName) {
//        return AUTOMATION && automationList.containsKey(automationName);
//    }
//
//    public void setParameterForAutomation(String automationName,
//                                          String name, Object obj) {
//        if (MainActivity.isDebugmode() && AUTOMATION) {
//            AutomationFramework framework = getAutomation(automationName);
//            framework.setParameter(name, obj);
//        }
//    }
//
//    public void setParametersForAutomation(String automationName,
//                                           TreeMap<String, Object> paramList) {
//        if (MainActivity.isDebugmode() && AUTOMATION) {
//            AutomationFramework framework = getAutomation(automationName);
//            framework.setParameters(paramList);
//        }
//    }
//
//    private AutomationFramework getAutomation(String automationName) {
//        AutomationFramework automationFramework = automationList.get(automationName);
//        if (automationFramework == null) {
//            throw new IllegalArgumentException(automationName + " is an invalid automation");
//        }
//        return automationFramework;
//    }
//
//    public void addAutomation(AutomationFramework a) {
//        if (MainActivity.isDebugmode() && AUTOMATION && !MainApp.getMainThread().isInputState()) {
//            AutomationFramework put = automationList.put(a.getName(), a);
//            if (put != null) {
//                throw new IllegalArgumentException(a.getName() + "already exists");
//            }
//        }
//    }
//
//    public void removeAutomation(String name) {
//        if (MainActivity.isDebugmode() && AUTOMATION && !MainApp.getMainThread().isInputState()) {
//            AutomationFramework remove = automationList.remove(name);
//            if (remove == null) {
//                throw new IllegalArgumentException(name + " is not a valid automation");
//            }
//        }
//    }
//
//    public void removaAllAutomations() {
//        automationList.clear();
//    }

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

//    public ApplicationStartSettings getApplicationSettings() {
//        return applicationSettings;
//    }

//    public void setApplicationSettings(ApplicationStartSettings settings) {
//        applicationSettings = settings;
//        if (settings.uncaughtExceptionHandler != null) {

    /// /			UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
//            Thread.setDefaultUncaughtExceptionHandler(settings.uncaughtExceptionHandler);
//            System.out.println("uncaughtExceptionHandler set");
//        }
//    }
    public void setGameDesc(ENG_GameDescription gameDesc) {
        this.gameDesc = gameDesc;
    }

    public void pauseGame() {
        System.out.println("Game paused attempt");
//        pauseGame = new CountDownLatch(1);
//        try {
//            pause.acquire();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            System.err.println("Could not pause game!");
//            e.printStackTrace();
//        }
        shouldPause.set(true);
        pauseBeginTime = currentTimeMillis();
    }

    public void resumeGame() {
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
            // TODO Auto-generated catch block
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void waitForMainThreadToExitMainLoop() {
        try {
            stopGame.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private double currentTime = 0.0;
    private double updateIterations = 0.0;
    private int update = 0;
    private int frame = 0;
    private long beginUpdate = currentTimeMillis();
    private long endUpdate = 0;
    private long beginFrame = currentTimeMillis();
    private long endFrame = 0;
    private long deltaCurrentTime = currentTimeMillis();
    private long deltaLastTime = currentTimeMillis();
    private long frameNum = 0;


    private int currentFrameNum;

    private long beginFrameDiff;
    private long beginFrameDiffMain;

    @Override
    public void run() {
        resetThreadLocals();
        while (true) {
            if (shouldStop.get()) {
                // If we are on the server we must make sure that we wait for the whole pipeline
                // to be flushed.

                break; // Only used to get out of the multiplayer server side loop.
            }
            while (shouldPause.get()) {

                try {
                    System.out.println("Game paused");

                    System.out.println("acquiring pause");
                    pause.acquire();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pauseTimeMillis += currentTimeMillis() - pauseBeginTime;
                System.out.println("Game resumed");
                if (gameDesc.areResourcesCreated()) {
                } else {
                }
            }

            gameDesc.preinitialize();

            gameDesc.startGame();
            ENG_IMainThreadSequentialWorker worker = workerQueue.poll();
            if (worker != null) {
                worker.run();
            }
            if (lastFrameTime == 0.0) {
                lastFrameTime = (double) currentTimeMillis();
            }

            long time = currentTimeMillis();

            // Run everything queued from other threads
            runQueuedCommands();

            Looper.loop();

            if (inputState) {
                sendInputData();
                gameDesc.gameLoop(endUpdate, UPDATE_INTERVAL, UPDATE_INTERVAL);
                //			}
            } else {

                currentTime = (double) currentTimeMillis();
                updateIterations = ((currentTime - lastFrameTime) + cyclesLeftOver) * 0.001;

                //	System.out.println("updateIterations: " + updateIterations);
                if (updateIterations > MIN_INV) {
                    updateIterations = MIN_INV;
                }
                int gameLoopRunCount = 0;
                sendInputData();
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
                deltaCurrentTime = currentTimeMillis();
                long elapsed = deltaCurrentTime - deltaLastTime;
//                        System.out.println("elapsed: " + elapsed);
                currentElapsedTime = elapsed;
                gameDesc.gameLoop(endUpdate, UPDATE_INTERVAL, elapsed);
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

            renderScene();

            updateInputState();
//                System.out.println("UPDATING FRAMENUM: " + frameNum);
            frameNum++;
            //	    	System.out.println("RENDERING DONE NUM " + (frameNum));

            endFrame = currentTimeMillis();
            if ((endFrame - beginFrame) < 1000) {
                ++frame;
            } else {
                beginFrame = currentTimeMillis();
                setFrameRate(frame);
                //				System.out.println("FrameRate: " + frame);
                frame = 0;

            }
        }
    }

    private void sendInputData() {
    }

    private void updateInputState() {
        if (inputState) {
            endFrameTime = currentTimeMillis();
            ++currentFrameNum;
        }
    }


    public long getFrameNum() {
        return frameNum;
    }

    public void runQueuedCommands() {
        ENG_IRunOnMainThread thread;
        while ((thread = runners.poll()) != null) {
            thread.run();
        }
    }

    private void renderScene() {
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

    public void setInputStateFile(String inputStateFile) {
        this.inputStateFile = inputStateFile;
    }

    public String getInputStateFile() {
        return inputStateFile;
    }


    public boolean isInputState() {
        return inputState;
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
