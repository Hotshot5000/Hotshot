/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/4/21, 4:49 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public abstract class ENG_MainThreadSequentialWorkerWithTimer implements ENG_IMainThreadSequentialWorker {

    private final String taskName;
    private final boolean printTime;

    public ENG_MainThreadSequentialWorkerWithTimer(String taskName, boolean printTime) {
        if (taskName == null) {
            taskName = "";
        }
        this.taskName = taskName;
        this.printTime = printTime;
    }

    public abstract void runWithTimer();

    @Override
    public void run() {
        long startTime = ENG_Utility.currentTimeMillis();
        runWithTimer();
        long endTime = ENG_Utility.currentTimeMillis() - startTime;
        if (printTime) {
            System.out.println("Tasks: " + taskName + " time to completion: " + endTime);
        }
    }
}
