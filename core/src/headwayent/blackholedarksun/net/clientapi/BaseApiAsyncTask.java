/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi;


import headwayent.hotshotengine.AsyncTask;

/**
 * Created by Sebastian on 18.04.2015.
 */
public abstract class BaseApiAsyncTask<Result> extends AsyncTask<Void, Void, Result> {

    private final OnTaskFinishedListener cb;
    private Object result;
    private RestError error;

    public interface OnTaskFinishedListener<Result> {
        void onTaskSuccess(Result result);

        void onTaskError(RestError error);
    }

    public BaseApiAsyncTask(OnTaskFinishedListener callback) {
        super();
        this.cb = callback;
    }

    @Override
    protected void onPostExecute(Result o) {
        super.onPostExecute(o);
        if (this.error != null) {
            cb.onTaskError(this.error);
        } else {
            cb.onTaskSuccess(o);
        }
    }

    public RestError getError() {
        return error;
    }

    public void setError(RestError error) {
        this.error = error;
    }
}
