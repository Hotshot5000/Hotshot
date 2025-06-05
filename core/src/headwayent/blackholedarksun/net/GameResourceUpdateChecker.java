/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class GameResourceUpdateChecker {

    private static final GameResourceUpdateChecker singleton = new GameResourceUpdateChecker();
    private static final String NET_CHECK_URL = "http://www.headwayentertainment.net/gamedata/gamedata_version";
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean gameResouceUpdateAvailable;
    private final GameDataUpdateChecker gamedataUpdateChecker = new GameDataUpdateChecker();
    private ScheduledFuture<?> schedule;
    private final ReentrantLock lock = new ReentrantLock();
    private int gamedataVersion = -1;
    private boolean monitoringActive;

    private class GameDataUpdateChecker implements Runnable {

        @Override
        public void run() {
            HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
            Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(NET_CHECK_URL).build();
            httpRequest.setTimeOut(3000);
            Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    HttpStatus status = httpResponse.getStatus();
                    String versionStr = httpResponse.getResultAsString();
                    int version = Integer.parseInt(versionStr);
                    lock.lock();
                    try {
                        if (version > gamedataVersion) {
                            gameResouceUpdateAvailable = true;
                            gamedataVersion = version;
                        } else {
                            gameResouceUpdateAvailable = false;
                        }
                    } finally {
                        lock.unlock();
                    }
                }

                @Override
                public void failed(Throwable t) {
                    lock.lock();
                    try {
                        gameResouceUpdateAvailable = false;
                    } finally {
                        lock.unlock();
                    }
                }

                @Override
                public void cancelled() {

                }
            });
        }
    }

    private GameResourceUpdateChecker() {

    }

    public void startMonitoringLiveGameResourceUpdates() {
        lock.lock();
        try {
            if (!monitoringActive) {
                schedule = scheduler.scheduleAtFixedRate(gamedataUpdateChecker, 0, 5, TimeUnit.SECONDS);
                monitoringActive = true;
            }
        } finally {
            lock.unlock();
        }
    }

    public void stopMonitoringGameResourceUpdates() {
        lock.lock();
        try {
            if (monitoringActive) {
                schedule.cancel(false);
                monitoringActive = false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isGameResourceUpdateAvailable() {
        lock.lock();
        try {
            return gameResouceUpdateAvailable;
        } finally {
            lock.unlock();
        }
    }

    public int getGameResourceVersion() {
        lock.lock();
        try {
            return gamedataVersion;
        } finally {
            lock.unlock();
        }
    }

    public boolean isGameResourceMonitoringActive() {
        lock.lock();
        try {
            return monitoringActive;
        } finally {
            lock.unlock();
        }
    }

    public static GameResourceUpdateChecker getInstance() {
        return singleton;
    }
}
