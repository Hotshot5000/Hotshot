/*
 * Created by Sebastian Bugiu on 4/18/23, 1:21 AM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/9/23, 10:13 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksunonline.android;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import headwayent.hotshotengine.TimeUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by sebas on 29.09.2015.
 */
public class App extends Application {

    private static App sInstance;
    private final ArrayList<BatteryLevelWithTime> mBatteryLevel = new ArrayList<>();
    private final ArrayList<ChargingWithTime> mCharging = new ArrayList<>();
    private DisplayMetrics mDisplayMetrics;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mDisplayMetrics = getResources().getDisplayMetrics();
    }

    public File _debugGetDatabaseFile() throws PackageManager.NameNotFoundException {
        PackageManager m = getPackageManager();
        String s = getPackageName();
        PackageInfo p = m.getPackageInfo(s, 0);
        s = p.applicationInfo.dataDir;
        File dbFile = new File(s, "databases/dreamstime.db");
        return dbFile;
    }

    public static class BatteryLevelWithTime {
        public final float batteryLevel;
        public final String date;

        public BatteryLevelWithTime(float batteryLevel, String date) {
            this.batteryLevel = batteryLevel;
            this.date = date;
        }

        @Override
        public String toString() {
            return "batteryLevel: " + batteryLevel + " date: " + date + "\n";
        }
    }

    public static class ChargingWithTime {
        public final boolean charging;
        public final String date;

        public ChargingWithTime(boolean charging, String date) {
            this.charging = charging;
            this.date = date;
        }

        @Override
        public String toString() {
            return "charging status: " + charging + " date: " + date + "\n";
        }
    }

    public ArrayList<BatteryLevelWithTime> getBatteryLevel() {
        return mBatteryLevel;
    }

    public void setBatteryLevel(float mBatteryLevel) {
        this.mBatteryLevel.add(new BatteryLevelWithTime(mBatteryLevel, TimeUtils.getCurrentDate()));
    }

    public ArrayList<ChargingWithTime> isCharging() {
        return mCharging;
    }

    public void setCharging(boolean mCharging) {
        this.mCharging.add(new ChargingWithTime(mCharging, TimeUtils.getCurrentDate()));
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    /**
     * **********************************************************************************************
     * Returns size in bytes.
     * <p/>
     * If you need calculate external memory, change this:
     * StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
     * to this:
     * StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
     * ************************************************************************************************
     */
    public static long getTotalMemory() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
        return Total;
    }

    public static long getFreeMemory() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
        return Free;
    }

    public static long getBusyMemory() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize());
        long Free = (statFs.getAvailableBlocks() * (long) statFs.getBlockSize());
        long Busy = Total - Free;
        return Busy;
    }

    public static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }


    public static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " Kb";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " Mb";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " Gb";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " Tb";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " Pb";
        if (size >= Eb) return floatForm((double) size / Eb) + " Eb";

        return "???";
    }

    public static App getInstance() {
        return sInstance;
    }
}
