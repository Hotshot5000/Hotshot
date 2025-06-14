/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.app;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Internal utility class to keep track of process-global work that's
 * outstanding and hasn't been finished yet.
 * <p/>
 * This was created for writing SharedPreference edits out
 * asynchronously so we'd have a mechanism to wait for the writes in
 * Activity.onPause and similar places, but we may use this mechanism
 * for other things in the future.
 *
 * @hide
 */
public class QueuedWork {

    // The set of Runnables that will finish or wait on any async
    // activities started by the application.
    private static final ConcurrentLinkedQueue<Runnable> sPendingWorkFinishers =
            new ConcurrentLinkedQueue<>();

    private static ExecutorService sSingleThreadExecutor = null; // lazy, guarded by class

    /**
     * Returns a single-thread Executor shared by the entire process,
     * creating it if necessary.
     */
    public static ExecutorService singleThreadExecutor() {
        synchronized (QueuedWork.class) {
            if (sSingleThreadExecutor == null) {
                // TODO: can we give this single thread a thread name?
                sSingleThreadExecutor = Executors.newSingleThreadExecutor();
            }
            return sSingleThreadExecutor;
        }
    }

    /**
     * Add a runnable to finish (or wait for) a deferred operation
     * started in this context earlier.  Typically finished by e.g.
     * an Activity#onPause.  Used by SharedPreferences$Editor#startCommit().
     * <p/>
     * Note that this doesn't actually start it running.  This is just
     * a scratch set for callers doing async work to keep updated with
     * what's in-flight.  In the common case, caller code
     * (e.g. SharedPreferences) will pretty quickly call remove()
     * after an add().  The only time these Runnables are run is from
     * waitToFinish(), below.
     */
    public static void add(Runnable finisher) {
        sPendingWorkFinishers.add(finisher);
    }

    public static void remove(Runnable finisher) {
        sPendingWorkFinishers.remove(finisher);
    }

    /**
     * Finishes or waits for async operations to complete.
     * (e.g. SharedPreferences$Editor#startCommit writes)
     * <p/>
     * Is called from the Activity base class's onPause(), after
     * BroadcastReceiver's onReceive, after Service command handling,
     * etc.  (so async work is never lost)
     */
    public static void waitToFinish() {
        Runnable toFinish;
        while ((toFinish = sPendingWorkFinishers.poll()) != null) {
            toFinish.run();
        }
    }

    /**
     * Returns true if there is pending work to be done.  Note that the
     * result is out of data as soon as you receive it, so be careful how you
     * use it.
     */
    public static boolean hasPendingWork() {
        return !sPendingWorkFinishers.isEmpty();
    }

}
