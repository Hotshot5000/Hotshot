package headwayent.hotshotengine;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ENG_GameDescription {

    //    protected MainActivity main;
//    private ENG_BitmapManager bitmapManager = new ENG_BitmapManager();
    private final AtomicBoolean gameActive = new AtomicBoolean(true);
    private final AtomicBoolean resourcesCreated = new AtomicBoolean();
    private CountDownLatch resourcesRecreated;
    private boolean preinitialized;
    private boolean gameStarted;
    private boolean gameEnded;
    private boolean gameActivated;
    private boolean gameDeactivated;
    private boolean gameLoopEnded;
    private final ReentrantLock activationLock = new ReentrantLock();
    private boolean loadingScreenShown;
    private String outputDebuggingStatePathAndFilename;
    private ENG_GameDescriptionEventsListener listener;
    private boolean shouldRestart;
    private boolean ignoreResourcesCreated;
    private boolean reloadingResources;

    public void restartGame() {
        if (shouldRestart) {
            gameRestart();
            shouldRestart = false;
        }
    }

    public void gameRestart() {

    }

    public boolean isShouldRestart() {
        return shouldRestart;
    }

    public void setShouldRestart(boolean shouldRestart) {
        this.shouldRestart = shouldRestart;
    }

    public void preinitialize() {
        if (!preinitialized) {
            preStartGameInit();
            preinitialized = true;
        }
    }

    /**
     * Hack for getting the Frame class loaded by the classloader before actually starting the game.
     */
    public void preStartGameInit() {
    }

    public void resetGameStarted() {
        gameStarted = false;
    }

    public void startGame(/*ENG_Frame currentFrame*/) {
        if (!gameStarted) {
//			gameStart();
            gameStarted = gameStart(/*currentFrame*/);
            if (gameStarted && listener != null) {
                listener.onGameStart();
            }
        }
    }

    public void endGame() {
        System.out.println("End game called!");
        if (gameStarted && !gameEnded) {
            gameEnd();
            gameEnded = true;
            if (listener != null) {
                listener.onGameEnd();
            }
        }
    }

    public void setGameActivated(boolean b) {
        activationLock.lock();
        try {
            if (gameActivated != b) {
                if (b) {
                    gameActivate();
                } else {
                    gameDeactivate();
                }
            }
            gameActivated = b;
            if (listener != null) {
                listener.onGameActivation(gameActivated);
            }
        } finally {
            activationLock.unlock();
        }
    }

    public void activateGame() {
        activationLock.lock();
        try {
            if (!gameActivated) {
                gameActivate();
                gameActivated = true;
                if (listener != null) {
                    listener.onGameActivation(gameActivated);
                }
                //			gameDeactivated = false;
            }
        } finally {
            activationLock.unlock();
        }
    }

    public void deactivateGame() {
        activationLock.lock();
        try {
            if (gameActivated) {
                gameDeactivate();
                //			gameDeactivated = true;
                gameActivated = false;
                if (listener != null) {
                    listener.onGameActivation(gameActivated);
                }
            }
        } finally {
            activationLock.unlock();
        }
    }

    public void createResourceRecreatedCountDownLatch() {
        resourcesRecreated = new CountDownLatch(1);
    }

    public void waitForGLThreadToBeReady() {

        try {
            resourcesRecreated.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setGLThreadReadyForResourceReload() {
        resourcesRecreated.countDown();
    }

    public ENG_GameDescription() {
//    	main = MainActivity.getInstance();
        initEngine();
    }

    private void initEngine() {
        //	ENG_Bitmap.setBitmapManager(bitmapManager);
    }

    /**
     * @param currentFrame
     * @return a boolean that says if we are allowed to enter gameStart()
     * the next frame. Useful for showing the loading screen and then
     * continue loading the next frame. true means the gameStart() completed
     * successfully  while false means it is allowed to enter again.
     */
    public abstract boolean gameStart(/*ENG_Frame currentFrame*/);

    public abstract void gameEnd();

    public abstract void gameActivate();

    public abstract void gameDeactivate();

    public abstract void gameLoop(long currentTime, double dt, double lastTimeDt/*, ENG_Frame currentFrame*/);

    public void setGameActive(boolean gameActive) {
        this.gameActive.set(gameActive);
    }

    public boolean isGameActive() {
        return gameActive.get();
    }

    /**
     * @return the bitmapManager
     */
/*	public ENG_BitmapManager getBitmapManager() {
        return bitmapManager;
	}*/
    public boolean areResourcesCreated() {
        return resourcesCreated.get();
    }

    public void setResoucesCreated() {
        resourcesCreated.set(true);
    }

    public abstract void reloadResources();

    /**
     * end it with "/"
     *
     * @return
     */
    public abstract String getGameResourcesDir();

    /**
     * end it with "/"
     *
     * @return
     */
    public abstract String getGameDir();

    public boolean isLoadingScreenShown() {
        return loadingScreenShown;
    }

    public void setLoadingScreenShown(boolean loadingScreenShown) {
        this.loadingScreenShown = loadingScreenShown;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public String getOutputDebuggingStatePathAndFilename() {
        return outputDebuggingStatePathAndFilename;
    }

    public void setOutputDebuggingStatePathAndFilename(String outputDebuggingStatePathAndFilename) {
        this.outputDebuggingStatePathAndFilename = outputDebuggingStatePathAndFilename;
    }

    public ENG_GameDescriptionEventsListener getListener() {
        return listener;
    }

    public void setListener(ENG_GameDescriptionEventsListener listener) {
        this.listener = listener;
    }

    public boolean isIgnoreResourcesCreated() {
        return ignoreResourcesCreated;
    }

    public void setIgnoreResourcesCreated(boolean ignoreResourcesCreated) {
        this.ignoreResourcesCreated = ignoreResourcesCreated;
    }

    public boolean isReloadingResources() {
        return reloadingResources;
    }

    public void setReloadingResources(boolean reloadingResources) {
        this.reloadingResources = reloadingResources;
    }
}
