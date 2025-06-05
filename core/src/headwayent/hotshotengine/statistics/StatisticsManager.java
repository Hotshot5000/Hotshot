/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.statistics;

import com.badlogic.gdx.files.FileHandle;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import headwayent.hotshotengine.util.ENG_Compress;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class StatisticsManager {

    private Statistics statistics;

    public boolean uploadStatistics(String url, FileHandle statisticsArchive) {
        boolean result = false;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(180, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .build();

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("hotshot_statistics",
                            statisticsArchive.name(),
                            RequestBody.create(MediaType.parse("text/html"),
                                    statisticsArchive.file()))
                    .build();
//            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), stacktrace);
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                result = response.isSuccessful();
                String resultBody = response.body().string();

                result = resultBody.equals("Success");
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean archiveStatistics(FileHandle fileToCompress, FileHandle destination) {
        return new ENG_Compress(fileToCompress, destination).zip();
    }

    public void init() {
        loadCurrentStatistics();
        if (statistics == null) {
            throw new NullPointerException("Call setStatistics() after deserializing the data!");
        }
    }

    public abstract void collectStatistics();

    protected abstract void loadCurrentStatistics();

    public abstract void saveCurrentStatistics();

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
