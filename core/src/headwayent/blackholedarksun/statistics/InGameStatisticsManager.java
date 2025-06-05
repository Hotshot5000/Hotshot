/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 8:20 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import headwayent.blackholedarksun.APP_Game;
import headwayent.hotshotengine.ENG_DateUtils;
import headwayent.hotshotengine.statistics.StatisticsManager;
import headwayent.hotshotengine.util.ENG_Compress;
import headwayent.hotshotengine.vfs.ENG_FileUtils;

public class InGameStatisticsManager extends StatisticsManager {

    private static final String STATISTICS = "statistics_";
    private static final boolean STATISTICS_DEBUG = true;
    private static final int STATISTICS_UPLOAD_RATE = STATISTICS_DEBUG ? 1 : 5;
    public static final String STATISTICS_URL = APP_Game.URL_ADDRESS + "/statistics/statistics_upload.php";
    private static final InGameStatisticsManager mgr = new InGameStatisticsManager();
    private Gson gson;

    private InGameStatisticsManager() {

    }

    @Override
    public void collectStatistics() {
        // Make sure we don't have dangling zip files that will never be uploaded.
        ArrayList<FileHandle> statisticsZippedFiles = ENG_FileUtils.getFilesFromLocalFolder(STATISTICS, "zip", true);
        for (FileHandle statisticsZippedFile : statisticsZippedFiles) {
            if (!statisticsZippedFile.delete()) {
                System.out.println("Could not delete statistics zip file: " + statisticsZippedFile.name());
            }
        }

        ArrayList<FileHandle> statisticsFiles = ENG_FileUtils.getFilesFromLocalFolder(STATISTICS, "txt", true);
        if (statisticsFiles.isEmpty()) {
            return;
        }
        final FileHandle latestStatisticsFile = statisticsFiles.get(statisticsFiles.size() - 1);
        int underscoreLastPos = latestStatisticsFile.name().lastIndexOf("_");
        int dotLastPos = latestStatisticsFile.name().lastIndexOf(".");
        // Between these we should have the date.
        if (underscoreLastPos != -1 && dotLastPos != -1 && underscoreLastPos + 1 < dotLastPos) {
            String timeStamp = latestStatisticsFile.name().substring(underscoreLastPos + 1, dotLastPos);
            final FileHandle zipOutputFile = Gdx.files.local(STATISTICS + timeStamp + ".zip");
            boolean compressionResult = new ENG_Compress(latestStatisticsFile, zipOutputFile).zip();
            if (!compressionResult) {
                System.out.println("Could not compress file: " + zipOutputFile.name());
                return;
            }
            // If we archived the statistics and are ready for upload then we can delete
            // the session list contents.
            getInGameStatistics().sessionStatisticsList.clear();
            new Thread(() -> {
                if (uploadStatistics(STATISTICS_URL, zipOutputFile)) {
                    if (!latestStatisticsFile.delete()) {
                        System.out.println("Could not delete latestStatisticsFile: " + latestStatisticsFile);
                    }
                    if (!zipOutputFile.delete()) {
                        System.out.println("Could not delete statistics zip file: " + zipOutputFile.name());
                    }
                } else {
                    System.out.println("Could not upload statistics");
                    // We will recreate the zip file next time.
                    if (!zipOutputFile.delete()) {
                        System.out.println("Could not delete statistics zip file: " + zipOutputFile.name());
                    }
                }
            }).start();
        }
    }

    @Override
    protected void loadCurrentStatistics() {
        ArrayList<FileHandle> statisticsFiles = ENG_FileUtils.getFilesFromLocalFolder(STATISTICS, "txt", true);
        if (statisticsFiles.isEmpty()) {
            InGameStatistics statistics = new InGameStatistics();
            initializeSession(statistics);
            setStatistics(statistics);
        } else {
            boolean foundStatisticsFile = false;
            for (int i = statisticsFiles.size() - 1; i >= 0; --i) {
                FileHandle latestStatisticsFile = statisticsFiles.get(i);
                try {
                    String statisticsText = FileUtils.readFileToString(latestStatisticsFile.file());
                    System.out.println("statistics json: " + statisticsText);
                    InGameStatistics inGameStatistics = gson.fromJson(statisticsText, InGameStatistics.class);
                    setStatistics(inGameStatistics);
                    if (!inGameStatistics.sessionStatisticsList.isEmpty()) {
                        collectStatistics();
                    }
                    initializeSession(inGameStatistics);
                    foundStatisticsFile = true;
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    // TODO in production if we change the format of the statistics class
                    // we probably will need an adapter before uploading.
                    // Might not want to delete the current data in production, but for testing
                    // it's fine.
                    if (!latestStatisticsFile.delete()) {
                        System.out.println("Could not delete latest statistics file: " + latestStatisticsFile.name());
                    }
                }
            }
            if (!foundStatisticsFile) {
                InGameStatistics statistics = new InGameStatistics();
                initializeSession(statistics);
                setStatistics(statistics);
            }
        }
    }

    private void initializeSession(InGameStatistics inGameStatistics) {
        SessionStatistics sessionStatistics = new SessionStatistics();
        inGameStatistics.sessionStatisticsList.add(sessionStatistics);
        sessionStatistics.sessionStartDate = ENG_DateUtils.getCurrentDateTimestamp();
    }

    @Override
    public void saveCurrentStatistics() {
        String timeStamp = ENG_DateUtils.getCurrentDateTimestamp();
        FileHandle statisticsOutputFile = Gdx.files.local(STATISTICS + timeStamp + ".txt");
        try {
            String statisticsText = gson.toJson(getStatistics(), InGameStatistics.class);
            try {
                PrintWriter printWriter = new PrintWriter(statisticsOutputFile.file());
                printWriter.write(statisticsText);
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public InGameStatistics getInGameStatistics() {
        return (InGameStatistics) getStatistics();
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public static InGameStatisticsManager getInstance() {
        return mgr;
    }
}
