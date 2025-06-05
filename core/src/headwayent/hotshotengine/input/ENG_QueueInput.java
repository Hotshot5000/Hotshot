/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.input;

import java.util.concurrent.locks.ReentrantLock;

public class ENG_QueueInput {

    private final ReentrantLock lock = new ReentrantLock();
    private int queueLen;
    private String name = "";

    public ENG_QueueInput() {

    }

    public ENG_QueueInput(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void clearQueue() {
        lock.lock();
        try {
            queueLen = 0;
        } finally {
            lock.unlock();
        }
    }

    public void incrementQueueLength() {
        lock.lock();
        try {
            ++queueLen;
        } finally {
            lock.unlock();
        }
    }

    public int getQueueLength() {
        lock.lock();
        try {
            return queueLen;
        } finally {
            lock.unlock();
        }

    }

    public void decrementQueueLengthConverted(int queueLen) {
        lock.lock();
        try {
            this.queueLen -= queueLen;
        } finally {
            lock.unlock();
        }

    }

    public ReentrantLock getLock() {
        return lock;
    }

}