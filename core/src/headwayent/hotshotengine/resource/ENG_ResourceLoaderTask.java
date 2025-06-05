/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.resource;



import com.google.common.util.concurrent.FutureCallback;

import java.util.concurrent.Callable;

/**
 * Created by sebas on 28.03.2016.
 */
public abstract class ENG_ResourceLoaderTask<Params, Result> implements Comparable<ENG_ResourceLoaderTask>, Callable<Result>, FutureCallback<Result> {

    private final ENG_ResourceLoader parent;
    private Params params;
    private Result result;
    private int priority = -1;

    public ENG_ResourceLoaderTask(ENG_ResourceLoader parent) {
        this(parent, -1);
    }

    public ENG_ResourceLoaderTask(ENG_ResourceLoader parent, int priority) {
        this.parent = parent;
        this.priority = priority;
    }

    public void doInBackground(Params params) {
        this.params = params;
    }

    public abstract Result _executeInBackground(Params params);

    public abstract void _executeOnRenderThread(Result result);

    public int getPriority() {
        return priority;
    }

    public void _setPriority(int priority) {
        this.priority = priority;
    }

    public Result getResult() {
        return result;
    }

    @Override
    public int compareTo(ENG_ResourceLoaderTask o) {
        return priority - o.getPriority();
    }

    @Override
    public void onSuccess(Result result) {
        this.result = result;
        parent._notify(this);
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
        throw new RuntimeException(t);
    }

    @Override
    public Result call() throws Exception {
        return _executeInBackground(params);
    }

//    @Override
//    public void run() {
//        result = _executeInBackground(params);
//    }
}
