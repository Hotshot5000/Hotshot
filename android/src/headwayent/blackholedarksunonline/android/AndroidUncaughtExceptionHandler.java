/*
 * Created by Sebastian Bugiu on 4/18/23, 1:21 AM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/9/23, 10:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksunonline.android;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.util.DisplayMetrics;
import headwayent.hotshotengine.android.util.Log;
import headwayent.hotshotengine.ENG_UncaughtExceptionHandler;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sebas on 29.09.2015.
 */
public class AndroidUncaughtExceptionHandler extends ENG_UncaughtExceptionHandler {


    private String deviceInfo;
    private String appVersion;
    private String memoryInfo;
    private String username;
    private String batteryInfo;
    private String displayInfo;

    public AndroidUncaughtExceptionHandler(Context context) {
        String version = this.getVersionFingerPrint(context);
    }

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

    public void sendDb() {
        new Thread(new Runnable() {
            /** @noinspection deprecation, deprecation */
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                try {
                    HttpPost httppost = new HttpPost(CRASH_REPORT_URL);


                    // Add your data
                    List<NameValuePair> nameValuePairs = getInfo();

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//                    MultipartEntity entity = new MultipartEntity(
//                            HttpMultipartMode.BROWSER_COMPATIBLE);
                    for (NameValuePair nameValuePair : nameValuePairs) {
                        String value = nameValuePair.getValue();
                        if (value == null) {
                            value = "";
                        }
                        builder.addPart(nameValuePair.getName(), new StringBody(value));
                    }

                    File f = App.getInstance()._debugGetDatabaseFile();
                    builder.addPart("database", new FileBody(f));

                    httppost.setEntity(builder.build());

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    int status = response.getStatusLine().getStatusCode();



                } catch (ClientProtocolException e) {
                } catch (UnsupportedEncodingException e) {
                } catch (PackageManager.NameNotFoundException e) {
                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                } finally {
                    httpclient.getConnectionManager().shutdown();
                }
            }
        }).start();
    }

    private String getFormattedKernelVersion() {
        String procVersionStr = "";
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256)) {
                procVersionStr = reader.readLine();
            }
            final String PROC_VERSION_REGEX = "\\w+\\s+" + /* ignore: Linux */
                    "\\w+\\s+" + /* ignore: version */
                    "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                    "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
                                                         * group 2:
                                                         * (xxxxxx@xxxxx
                                                         * .constant)
                                                         */
                    "\\(.*?(?:\\(.*?\\)).*?\\)\\s+" + /* ignore: (gcc ..) */
                    "([^\\s]+)\\s+" + /* group 3: #26 */
                    "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                    "(.+)"; /* group 4: date */
            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);
            if (!m.matches())
                return "Unavailable";
            else if (m.groupCount() < 4)
                return "Unavailable";
            else
                return m.group(1) + " / " + m.group(2) + " " +
                        m.group(3) + " / " + m.group(4);
        } catch (IOException e) {
            procVersionStr = "Unavailable";
            return procVersionStr;
        }
    }

    private String getVersionFingerPrint(Context c) {
        deviceInfo = String
                .format("Device Model: %s\nBuild Number: %s\nFirmware Ver: %s\nBaseband Ver: %s\nKernel Ver: %s\n\n",
                        Build.MODEL, Build.DISPLAY, Build.VERSION.RELEASE,
                        System.getProperty("gsm.version.baseband", "unknown"),
                        this.getFormattedKernelVersion());
        appVersion = "";
        try {
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(),
                    PackageManager.GET_META_DATA);
            appVersion = c.getPackageName() + " version: " + pInfo.versionName + "("
                    + pInfo.versionCode + ")\n\n";
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e(this.getClass().getSimpleName(), "Name not found", e1);
            appVersion = "No version information\n\n";
        }
        int id = android.os.Process.myPid();
        ActivityManager actvityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
        actvityManager.getMemoryInfo(mInfo);
        Debug.MemoryInfo[] processMemoryInfo = actvityManager.getProcessMemoryInfo(new int[]{id});
        memoryInfo = "available memory: " + mInfo.availMem +
                "\nlow memory: " + mInfo.lowMemory +
                "\nthreshold (limit after which we consider low memory): " + mInfo.threshold;
        if (processMemoryInfo != null && processMemoryInfo[0] != null) {
            Debug.MemoryInfo pmi = processMemoryInfo[0];
            memoryInfo += "\ndalvikPrivateDirty(kb): " + pmi.dalvikPrivateDirty +
                    "\ndalvikPss(kb): " + pmi.dalvikPss +
                    "\ndalvikSharedDirty(kb): " + pmi.dalvikSharedDirty +
                    "\nnativePrivateDirty(kb): " + pmi.nativePrivateDirty +
                    "\nnativePss(kb): " + pmi.nativePss +
                    "\nnativeSharedDirty(kb): " + pmi.nativeSharedDirty +
                    "\notherPrivateDirty(kb): " + pmi.otherPrivateDirty +
                    "\notherPss(kb): " + pmi.otherPss +
                    "\notherSharedDirty(kb): " + pmi.otherSharedDirty +
                    "\n\n";
        }
//        Preferences pref = App.getInstance().getPreferences();
//
//        username = pref.getUsername();

        App instance = App.getInstance();
        ArrayList<App.BatteryLevelWithTime> batteryLevel = instance.getBatteryLevel();
        batteryInfo = "";
        for (App.BatteryLevelWithTime batteryLevelWithTime : batteryLevel) {
            batteryInfo += batteryLevelWithTime;
        }
        ArrayList<App.ChargingWithTime> charging = instance.isCharging();
        for (App.ChargingWithTime chargingWithTime : charging) {
            batteryInfo += chargingWithTime;
        }
        batteryInfo += "\n";

        DisplayMetrics displayMetrics = App.getInstance().getDisplayMetrics();
        displayInfo = displayMetrics.toString();
        displayInfo += "\n";

        String diskSpaceInfo = "Total internal disk space: " + App.getTotalMemory()
                + "\nTotal used disk space: " + App.getBusyMemory()
                + "\nTotal free disk space: " + App.getFreeMemory()
                + "\n";

        return deviceInfo + appVersion + memoryInfo + batteryInfo + displayInfo + diskSpaceInfo;
    }
}
