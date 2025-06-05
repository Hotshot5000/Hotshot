/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * Created by sebas on 28.03.2016.
 */
public class ENG_ResourceLoader implements Comparable<ENG_ResourceLoader> {

    private final PriorityQueue<ENG_ResourceLoaderTask> resourceLoaderTasksQueue = new PriorityQueue<>();
    private final TreeSet<ENG_ResourceLoaderTask> taskSet = new TreeSet<>();
    private final int priority;
    private int taskNum;
    private boolean tasksHavePriority = true;

    public ENG_ResourceLoader(int priority) {
        this.priority = priority;
    }

    public void addResourceLoadTask(ENG_ResourceLoaderTask resourceLoaderTask) {
        if (resourceLoaderTask.getPriority() == -1) {
            if (tasksHavePriority) {
                throw new IllegalArgumentException("Added task without priority when until now everything had priority");
            } else {
                tasksHavePriority = false;
            }
        }
        if (resourceLoaderTask.getPriority() > -1) {
            if (!tasksHavePriority) {
                throw new IllegalArgumentException("Added task with priority when until now there was no priority");
            }
        }
        resourceLoaderTasksQueue.add(resourceLoaderTask);
        if (!tasksHavePriority) {
            resourceLoaderTask._setPriority(taskNum);
        }
        ++taskNum;
    }

    public void execute() {
        ENG_ResourceLoaderManager.getSingleton().execute(this);
    }

    public ENG_ResourceLoaderTask _getNextTask() {
        return resourceLoaderTasksQueue.poll();
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(ENG_ResourceLoader o) {
        return priority - o.getPriority();
    }

    public <Params, Result> void _notify(ENG_ResourceLoaderTask task) {
        taskSet.add(task);
        if (taskSet.size() == taskNum) {
            // All tasks have been completed. Now moving on to running each task's result on the render thread.
            ENG_ResourceLoaderManager.getSingleton()._notify(this);
        }
    }
}
