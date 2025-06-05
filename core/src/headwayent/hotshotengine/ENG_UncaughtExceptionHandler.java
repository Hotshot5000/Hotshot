/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/31/22, 12:04 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.hotshotengine.db.DatabaseConnection;
import headwayent.hotshotengine.networking.ENG_NetUtility;
import headwayent.hotshotengine.util.ENG_Compress;
import headwayent.hotshotengine.vfs.ENG_FileUtils;

import org.apache.http.NameValuePair;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 02.10.2015.
 */
public abstract class ENG_UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final boolean DEBUG = true;
    public static final String DEBUG_STR = "?XDEBUG_SESSION_START=netbeans-xdebug";
    public static final String CRASH_REPORT_URL = APP_Game.URL_ADDRESS +"/stacktraces/stacktrace.php" + (DEBUG ? DEBUG_STR : "");
    public static final String CRASH_PRINTLN_OUTPUT = "crash_println_output_";
    public static final String STACKTRACE = "stacktrace_";
    private final Thread.UncaughtExceptionHandler defaultUEH;

    public ENG_UncaughtExceptionHandler() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("ENG_UncaughtExceptionHandler called");
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();

        e.printStackTrace();
        // Uncomment these once we have a semblance of database working for libgdx.
        // New info: we don't support databases anymore. Just write the output to a file and send it
        // when we have a valid Internet connection.
        // If System.out.println() is redirected into a file then zip it and upload it also.
        if (APP_Game.REDIRECT_PRINTLN_OUTPUT) {
            ArrayList<FileHandle> printlnOutputsList = ENG_FileUtils.getRedirectOutputFiles(true);
            if (!printlnOutputsList.isEmpty()) {
                FileHandle latestPrintlnOutput = printlnOutputsList.get(printlnOutputsList.size() - 1);
                String timeStamp = ENG_DateUtils.getCurrentDateTimestamp();
                FileHandle zipOutputFile = Gdx.files.local(CRASH_PRINTLN_OUTPUT + timeStamp + ".zip");
                boolean compressionResult = new ENG_Compress(latestPrintlnOutput, zipOutputFile).zip();
                FileHandle stacktraceOutputFile = Gdx.files.local(STACKTRACE + timeStamp + ".txt");
                try {
                    PrintWriter outputPrinter = new PrintWriter(stacktraceOutputFile.file());
                    e.printStackTrace(outputPrinter);
                    outputPrinter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                System.out.println("compressionResult: " + compressionResult);
                if (compressionResult) {
                    // Don't send anything right now since we seem to no longer be able to handle thread creation
                    // and exceptions. Wait for the next application start to send the saved crash reports.
//                    ENG_CrashData crashData = new ENG_CrashData(stacktrace, stacktraceOutputFile, zipOutputFile, getInfo());
//                    ENG_CrashDataManager.uploadCrashData(crashData);
                }
            } else {
                System.out.println("printlnOutputsList is empty");
            }
        }

//        System.out.println("uploadCrashDataInternal called");
//        boolean postResult = ENG_NetUtility.sendStacktracePost(CRASH_REPORT_URL, null, stacktrace, 0);
//        System.out.println("uploadCrashDataInternal result: " + postResult);

//        ENG_CrashDataManager.uploadCrashData(new ENG_CrashData(stacktrace, null, null, getInfo()));
//        DatabaseConnection connection = DatabaseConnection.getConnection();
//        connection.createConnection();
//        long id = connection.addException(stacktrace);
//        connection.closeConnection();
//        sendPost(stacktrace, id);
        if (MainApp.isOutputDebuggingApplicationStateEnabled() && MainApp.getMainThread().getDebuggingState() != null) {
            MainApp.getMainThread().getDebuggingState().writeCurrentFrame();
        }

        // Hehe. On IOS we end up with stack overflow since we loop between the IOSLauncher uncaughtException and this one.
        if (MainApp.PLATFORM != MainApp.Platform.IOS && defaultUEH != null) {
            defaultUEH.uncaughtException(t, e);
        }
    }


    public void sendPost(final String stacktrace, final long id) {
        new Thread(() -> {
            ENG_NetUtility.sendStacktracePost(CRASH_REPORT_URL, getInfo(), stacktrace, id);
            DatabaseConnection.getConnection().closeConnection();
        }).start();
    }

    public List<NameValuePair> getInfo() {
        return null;
    }
}
