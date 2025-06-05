/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/5/16, 5:15 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 29.09.2015.
 */
public class ENG_DefaultUncaughtExceptionHandler extends ENG_UncaughtExceptionHandler {

    private static final String CRASH_REPORT_URL = "http://www.crashes.com/";

    private String version;
    private String deviceInfo;
    private String appVersion;
    private String procVersionStr;
    private String memoryInfo;
    private String username;
    private String batteryInfo;
    private String displayInfo;
    private String diskSpaceInfo;


    public List<NameValuePair> getInfo() {
        // Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("deviceInfo", deviceInfo));
        nameValuePairs.add(new BasicNameValuePair("appVersion", appVersion));
        nameValuePairs.add(new BasicNameValuePair("memoryInfo", memoryInfo));
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("batteryInfo", batteryInfo));
        nameValuePairs.add(new BasicNameValuePair("displayInfo", displayInfo));
        return nameValuePairs;
    }

//    public void sendPost(final String stacktrace, long id) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpClient httpclient = new DefaultHttpClient();
//                try {
//                    HttpPost httppost = new HttpPost(CRASH_REPORT_URL);
//
//
//                    // Add your data
//                    List<NameValuePair> nameValuePairs = getInfo();
//                    nameValuePairs.add(new BasicNameValuePair("stacktrace", stacktrace));
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                    // Execute HTTP Post Request
//                    HttpResponse response = httpclient.execute(httppost);
//                    int status = response.getStatusLine().getStatusCode();
//
//
//                } catch (ClientProtocolException e) {
//                } catch (IOException e) {
//                } finally {
//                    httpclient.getConnectionManager().shutdown();
//                }
//            }
//        }).start();
//    }
}
