/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.networking;

import headwayent.hotshotengine.ENG_CrashData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ENG_NetUtility {

    private static final ENG_NetworkChecker networkChecker = new ENG_NetworkChecker();

    public static HttpURLConnection getHttpURLConnection(String addr) throws IOException {
        //set the download URL, a url that points to a file on the internet
        //this is the file to be downloaded
        URL url = new URL(addr);

        //create the new connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //set up some things on the connection
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);

        //and connect!
        urlConnection.connect();

        return urlConnection;
    }

    public static boolean isNetworkAvailable() {
        networkChecker.startMonitoringNetworkStatus();
        return networkChecker.isNetworkAvailable();
    }

    public static void stopMonitoringNetworkAvailability() {
        networkChecker.stopMonitoringNetworkStatus();
    }

    public static boolean sendCrashData(String url, ENG_CrashData crashData) {
        boolean result = false;
        try {
            StringBuilder text = new StringBuilder();
            if (crashData.getAdditionalData() != null) {
                for (NameValuePair nv : crashData.getAdditionalData()) {
                    text.append(nv.getName()).append(": ").append(nv.getValue()).append("\n");
                }
            }
//            text.append("Stacktrace: ").append(stacktrace);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(180, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .build();

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("stacktrace",
                            crashData.getStacktraceOutputFile().name(),
                            RequestBody.create(MediaType.parse("text/html"),
                                    crashData.getStacktraceOutputFile().file()))
                    .addFormDataPart("traces",
                            crashData.getStacktraceOutputFile().name(),
                            RequestBody.create(MediaType.parse("text/html"),
                                    crashData.getZippedTraces().file()))
//                    .addFormDataPart("password", password)
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

    public static boolean sendStacktracePost(String url, List<NameValuePair> info, final String stacktrace, final long id) {
        boolean result = false;
//        HttpClient httpclient = new DefaultHttpClient();
        try {
//            HttpPost httppost = new HttpPost(url);


            // Add your data
//            List<NameValuePair> nameValuePairs = new ArrayList<>();
//            if (info != null) {
//                nameValuePairs.addAll(info);
//            }
//            nameValuePairs.add(new BasicNameValuePair("stacktrace", stacktrace));
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            StringBuilder text = new StringBuilder();
            if (info != null) {
                for (NameValuePair nv : info) {
                    text.append(nv.getName()).append(": ").append(nv.getValue()).append("\n");
                }
            }
            text.append("Stacktrace: ").append(stacktrace);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(180, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("stacktrace", stacktrace/*path.getName(), RequestBody.create(MediaType.parse("text/html"), path)*/)
//                    .addFormDataPart("username", username)
//                    .addFormDataPart("password", password)
                    .build();
//            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), stacktrace);
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                result = response.isSuccessful();
                String resultBody = response.body().string();


//            MultipartEntity mpEntity = new MultipartEntity();
//            InputStream is = IOUtils.toInputStream(text.toString());
//            InputStreamBody upfile = new InputStreamBody(is, "upfile");
//            mpEntity.addPart("upfile", upfile);
//            httppost.setEntity(mpEntity);
//
//            // Execute HTTP Post Request
//            System.out.println("executing httppost");
//            HttpResponse response = httpclient.execute(httppost);
//            int status = response.getStatusLine().getStatusCode();
//
//            String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
//            System.out.println("responseStr: " + responseStr);

                if (result) {
//                DatabaseConnection.getConnection().setExceptionSent(id);
                    System.out.println("resultBody: " + resultBody);
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            System.out.println("ClientProtocolException");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException");
        } finally {
//            httpclient.getConnectionManager().shutdown();
        }
        return result;
    }

    public interface ProgressUpdate {
        void onProgressUpdate(int downloadedSize, int totalSize);
    }

    public static boolean downloadFromServer(String url, File file, ProgressUpdate progressUpdate) {
    /*	MainActivity.getInstance().runOnUiThread(new Runnable() {

			@Override
			public void run() {

				Toast.makeText(MainActivity.getInstance(),
						"Donwloading data. Please wait...", Toast.LENGTH_LONG).show();
			}
		});*/
        boolean ok = true;
        try {
            HttpURLConnection urlConnection = ENG_NetUtility.getHttpURLConnection(url);

            urlConnection.setConnectTimeout(5000);

            // this will be used to write the downloaded data into the file we
            // created
            FileOutputStream fileOutput = new FileOutputStream(file);

            // this will be used in reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            // this is the total size of the file
            int totalSize = urlConnection.getContentLength();
            // variable to store total downloaded bytes
            int downloadedSize = 0;

            // create a buffer...
            byte[] buffer = new byte[1024 * 1024 * 2];
            int bufferLength; // used to store a temporary size of the
            // buffer

            // now, read through the input buffer and write the contents to the
            // file
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                // add the data in the buffer to the file in the file output
                // stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);
                // add up the size so we know how much is downloaded
                downloadedSize += bufferLength;
                // this is where you would do something to report the prgress,
                // like this maybe
                if (progressUpdate != null) {
                    progressUpdate.onProgressUpdate(downloadedSize, totalSize);
                }

            }
            // close the output stream when done
            fileOutput.close();

            // catch some possible errors...
        } catch (Throwable e) {
            e.printStackTrace();
            ok = false;
        }
        // see
        // http://androidsnippets.com/download-an-http-file-to-sdcard-with-progress-notification
        return ok;
    }

}
