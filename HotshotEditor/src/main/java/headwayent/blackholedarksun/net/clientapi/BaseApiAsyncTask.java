package headwayent.blackholedarksun.net.clientapi;


import headwayent.blackholedarksun.net.clientapi.tables.GenericTransient;
import headwayent.hotshotengine.AsyncTask;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

/**
 * Created by Sebastian on 18.04.2015.
 */
public abstract class BaseApiAsyncTask<Result extends GenericTransient> extends AsyncTask<Void, Void, Result> {

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

    public Result extractBody(Call<Result> call) {
        Response<Result> execute = null;
        try {
            execute = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (execute.isSuccessful()) {
            Result resultBody = execute.body();
            if (resultBody.getError() != null) {
                setError(new RestError(resultBody.getError()));
            }
            return resultBody;
        }

        return null;
    }

    public RestError getError() {
        return error;
    }

    public void setError(RestError error) {
        this.error = error;
    }
}
