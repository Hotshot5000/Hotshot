/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.util.objectpool;

import java.util.LinkedList;

/**
 * Created by sebas on 22-Feb-18.
 */

public class ENG_ObjectPool<T extends ENG_PoolObject> {

    private static final boolean DEBUG = false;
    private String debugName = "";
    private final LinkedList<T> list = new LinkedList<>();
    private final ENG_ObjectFactory<T> factory;
    private final boolean extensible;

    public ENG_ObjectPool(ENG_ObjectFactory<T> factory, int initialElementsNum, boolean extensible) {
        for (int i = 0; i < initialElementsNum; ++i) {
            list.offer(factory.create());
        }
        this.factory = factory;
        this.extensible = extensible;
    }

    public ENG_ObjectPool(ENG_ObjectFactory<T> factory, int initialElementsNum, boolean extensible, String debugName) {
        this(factory, initialElementsNum, extensible);
        this.debugName = debugName;
    }

    public T get() {
        if (extensible && list.isEmpty()) {
            if (DEBUG) {
                System.out.println("Extending the pool for pool: " + debugName);
            }
            list.offer(factory.create());
        }
        if (DEBUG) {
            System.out.println("Obtaining object from pool: " + debugName + " current pool size: " + list.size());
        }
        return list.poll();
    }

    public void add(T obj) {
        if (DEBUG) {
            System.out.println("Readding obj for pool: " + debugName + " current pool size: " + list.size());
        }
        obj.reset();
        list.offer(obj);
    }

    /**
     * Make sure that all objects have been readded to the pool before calling this or else
     * they will not be destroyed.
     */
    public void destroyPool() {
        if (DEBUG) {
            System.out.println("destroying pool: " + debugName + " current pool size: " + list.size());
        }
        T obj;
        while ((obj = list.poll()) != null) {
            factory.destroy(obj);
        }
        list.clear();
    }
}
