/*
 * Created by Sebastian Bugiu on 4/19/23, 10:21 AM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/19/23, 10:21 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.util.objectpool;

import java.util.concurrent.locks.ReentrantLock;

public class ENG_ConcurrentObjectPool<T extends ENG_PoolObject> extends ENG_ObjectPool<T> {

    private final ReentrantLock lock = new ReentrantLock();

    public ENG_ConcurrentObjectPool(ENG_ObjectFactory<T> factory, int initialElementsNum, boolean extensible) {
        super(factory, initialElementsNum, extensible);
    }

    public ENG_ConcurrentObjectPool(ENG_ObjectFactory<T> factory, int initialElementsNum, boolean extensible, String debugName) {
        super(factory, initialElementsNum, extensible, debugName);
    }

    @Override
    public T get() {
        lock.lock();
        try {
            return super.get();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(T obj) {
        lock.lock();
        try {
            super.add(obj);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroyPool() {
        lock.lock();
        try {
            super.destroyPool();
        } finally {
            lock.unlock();
        }
    }
}
