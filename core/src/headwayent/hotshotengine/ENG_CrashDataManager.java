/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/31/22, 4:10 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import com.badlogic.gdx.files.FileHandle;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.ArrayList;

import headwayent.hotshotengine.networking.ENG_NetUtility;
import headwayent.hotshotengine.vfs.ENG_FileUtils;

import static headwayent.hotshotengine.ENG_UncaughtExceptionHandler.CRASH_REPORT_URL;

public class ENG_CrashDataManager {

    private static final ENG_CrashDataManager mgr = new ENG_CrashDataManager();

    private ENG_CrashDataManager() {

    }

    public void uploadUnsentCrashData() {
        ArrayList<FileHandle> crashTraceList = ENG_FileUtils.getFilesFromLocalFolder(
                ENG_UncaughtExceptionHandler.CRASH_PRINTLN_OUTPUT, true);
        ArrayList<FileHandle> stacktraceList =
                ENG_FileUtils.getFilesFromLocalFolder(ENG_UncaughtExceptionHandler.STACKTRACE, true);
        final ArrayList<ENG_CrashData> crashDataList = new ArrayList<>();
        // Find the traces and the stacktrace with the same date.
        for (FileHandle crashFile : crashTraceList) {
            int underscoreLastPos = crashFile.name().lastIndexOf("_");
            int dotLastPos = crashFile.name().lastIndexOf(".");
            // Between these we should have the date.
            if (underscoreLastPos != -1 && dotLastPos != -1 && underscoreLastPos + 1 < dotLastPos) {
                String date = crashFile.name().substring(underscoreLastPos + 1, dotLastPos);
                for (FileHandle stacktraceFile : stacktraceList) {
                    if (stacktraceFile.name().contains(date)) {
                        try {
                            String stacktrace = FileUtils.readFileToString(stacktraceFile.file());
                            crashDataList.add(new ENG_CrashData(stacktrace, stacktraceFile, crashFile, null));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        new Thread(() -> {
            for (ENG_CrashData crashData : crashDataList) {
                uploadCrashDataInternal(crashData);
            }

        }).start();


    }

    public static void uploadCrashData(final ENG_CrashData crashData) {
        uploadCrashDataInternal(crashData);
//        final CountDownLatch latch = new CountDownLatch(1);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                uploadCrashDataInternal(crashData);
//                latch.countDown();
//            }
//        }).start();
//        try {
//            latch.await(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private static void uploadCrashDataInternal(ENG_CrashData crashData) {
//        boolean result = ENG_NetUtility.sendCrashData(CRASH_REPORT_URL, crashData);
        System.out.println("uploadCrashDataInternal called");
        boolean result = ENG_NetUtility.sendCrashData(CRASH_REPORT_URL, crashData);
        System.out.println("uploadCrashDataInternal result: " + result);
        if (result) {
            // Delete the crash data so we don't upload it again at the next application start.
            if (!crashData.getZippedTraces().delete()) {
                System.out.println("Could not delete zipped traces: " + crashData.getZippedTraces().name());
            }
            if (!crashData.getStacktraceOutputFile().delete()) {
                System.out.println("Could not delete stacktrace output: " + crashData.getStacktraceOutputFile().name());
            }
        }
    }

    public static ENG_CrashDataManager getSingleton() {
        return mgr;
    }
}
