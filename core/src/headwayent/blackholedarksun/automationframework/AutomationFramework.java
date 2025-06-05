/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.automationframework;

import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AutomationFramework {

    private final String name;
    private final TreeMap<String, Object> paramList = new TreeMap<>();
    private final ReentrantLock paramLock = new ReentrantLock();
    private final ReentrantLock paramNotificationLock = new ReentrantLock();

    public AutomationFramework(String name) {
        this.name = name;
    }

    public abstract void execute();

    public String getName() {
        return name;
    }

    public void setParameter(String name, Object value) {
        paramLock.lock();
        try {
            paramList.put(name, value);
        } finally {
            paramLock.unlock();
        }
        notifyParam(name);
    }

    public void setParameters(TreeMap<String, Object> params) {
        paramLock.lock();
        try {
            paramList.putAll(params);
        } finally {
            paramLock.unlock();
        }
        notifyParam(params.keySet());
    }

    public Object getParameter(String name) {
        paramLock.lock();
        try {
            return paramList.get(name);
        } finally {
            paramLock.unlock();
        }
    }

    private void notifyParam(String name) {
        paramNotificationLock.lock();
        try {
            notifyParameterSet(name);
        } finally {
            paramNotificationLock.unlock();
        }
    }

    private void notifyParam(Set<String> names) {
        paramNotificationLock.lock();
        try {
            for (String name : names) {
                notifyParameterSet(name);
            }
        } finally {
            paramNotificationLock.unlock();
        }
    }

    /**
     * Don't do anything that might block this since we don't know what thread
     * this is coming from!
     * If you use getParameter() make sure to check for null as some other thread
     * might snatch your parameter from under your nose.
     *
     * @param name
     */
    public abstract void notifyParameterSet(String name);
}
