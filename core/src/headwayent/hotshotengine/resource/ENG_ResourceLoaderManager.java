/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/22/16, 2:35 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;

import com.google.common.collect.TreeMultimap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.Handler;
import headwayent.hotshotengine.Looper;

/**
 * Created by sebas on 28.03.2016.
 */
public class ENG_ResourceLoaderManager {

    private static ENG_ResourceLoaderManager mgr;// = new ENG_ResourceLoaderManager();
    /** @noinspection UnstableApiUsage */
    private ListeningScheduledExecutorService service = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(ENG_Utility.getNumberOfCores()));
    private final TreeMultimap<Integer, ENG_ResourceLoader> resourceLoaderMap = TreeMultimap.create();
    private final PriorityQueue<ENG_ResourceLoader> resourceLoadersQueue = new PriorityQueue<>();
    private final ReentrantLock submitLock = new ReentrantLock();
    private final ReentrantLock resourceLoadersQueueLock = new ReentrantLock();
    private ENG_ResourceLoader currentResourceLoader;
    private boolean alreadyQueuedForNextFrame;
    private boolean blockModeActive;

    private ENG_ResourceLoaderManager() {

    }

    public void addResourceLoaderBlock(ENG_ResourceLoaderBlock resourceLoaderBlock) {
        for (ENG_ResourceLoader resourceLoader : resourceLoaderBlock.getResourceLoaderList()) {
            addResourceLoader(resourceLoader);
        }
        if (!resourceLoaderBlock.getResourceLoaderList().isEmpty()) {
            blockModeActive = true;
        }
    }

    public void addResourceLoader(ENG_ResourceLoader resourceLoader) {
        if (blockModeActive) {
            throw new IllegalArgumentException("Cannot add a resource loader when a block order is in progress");
        }
        Collection<ENG_ResourceLoader> loaderList = resourceLoaderMap.get(resourceLoader.getPriority());
        loaderList.add(resourceLoader);
    }

    public void removeResourceLoader(ENG_ResourceLoader resourceLoader) {
        removeResourceLoader(resourceLoader, false);
    }

    private void removeResourceLoader(ENG_ResourceLoader resourceLoader, boolean ignoreBlockMode) {
        if (blockModeActive && !ignoreBlockMode) {
            throw new IllegalArgumentException("Cannot remove a resource loader when a block order is in progress");
        }
        boolean remove = resourceLoaderMap.remove(resourceLoader.getPriority(), resourceLoader);
        if (!remove) {
            throw new IllegalArgumentException("Resource loader not in list");
        }
    }

    public void clearResourceLoaders() {
        resourceLoaderMap.clear();
    }

    /** @noinspection UnstableApiUsage */
    public void execute(ENG_ResourceLoader resourceLoader) {
        submitLock.lock();
        try {
            ENG_ResourceLoaderTask task;
            while ((task = resourceLoader._getNextTask()) != null) {
                ListenableFuture<?> listenableFuture = service.submit(task);
                Futures.addCallback(listenableFuture, task);
            }
        } finally {
            submitLock.unlock();
        }
    }

    private void executeOnRenderThread() {
        resourceLoadersQueueLock.lock();
        try {
            if (currentResourceLoader == null) {
                // Check if the next one is the next one in the resourceLoadersMap
                ENG_ResourceLoader resourceLoader = resourceLoadersQueue.peek();
                if (resourceLoader != null) {
                    NavigableSet<Integer> priorities = resourceLoaderMap.keySet();
                    if (!priorities.isEmpty() && priorities.first() < resourceLoader.getPriority()) {
                        return;
                    }
                }
                currentResourceLoader = resourceLoadersQueue.poll();
            }
            if (currentResourceLoader != null) {
                ENG_ResourceLoaderTask task = currentResourceLoader._getNextTask();
                if (task != null) {
                    task._executeOnRenderThread(task.getResult());
                } else {
                    currentResourceLoader = null;
                    executeOnRenderThread();
                }
            }
        } finally {
            resourceLoadersQueueLock.unlock();
        }
    }

    public void _notify(ENG_ResourceLoader resourceLoader) {
        resourceLoadersQueueLock.lock();
        try {
            resourceLoadersQueue.add(resourceLoader);
            // If we are in block mode we cannot afford to have a loader with priority 1 execute before a loader with priority 0.
            // We must make sure we execute on the render thread in the order of the loaders' priorities in the block.
            if (resourceLoaderMap.size() > 1) {
                if (resourceLoaderMap.keySet().first() == resourceLoader.getPriority()) {
                    removeResourceLoader(resourceLoader, true);
                    enqueueForNextFrame();
                }
            } else {
                enqueueForNextFrame();
            }
        } finally {
            resourceLoadersQueueLock.unlock();
        }
    }

    private void enqueueForNextFrame() {
        if (!alreadyQueuedForNextFrame) {
            alreadyQueuedForNextFrame = true;
            new Handler(Looper.getMainLooper()).post(() -> {
                executeOnRenderThread();
                alreadyQueuedForNextFrame = false;
                enqueueForNextFrame();
            });
        }
    }

    /** @noinspection UnstableApiUsage*/
    public void reinit() {
        service.shutdown();
        service = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(ENG_Utility.getNumberOfCores()));
        clearResourceLoaders();
    }

    public static ENG_ResourceLoaderManager getSingleton() {
        return mgr;
    }
}
