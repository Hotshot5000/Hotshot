/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/25/21, 5:07 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esotericsoftware.kryonet.Connection;
import com.google.common.util.concurrent.*;
import headwayent.blackholedarksun.net.ClientListener;
import headwayent.blackholedarksun.net.registeredclasses.JoinServerConnectionRequest;
import headwayent.blackholedarksun.net.registeredclasses.ServerConnectionRequest;
import headwayent.hotshotengine.*;
import headwayent.hotshotengine.db.DatabaseConnection;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by sebas on 30.10.2015.
 * <p/>
 * Mostly a misnomer since we no longer virtualize multiple servers. There is only one available for each main server.
 */
public class ServerVirtualizationService {

    private final com.esotericsoftware.kryonet.Server server;

    private class ServerGameDescriptionListener implements ENG_GameDescriptionEventsListener {

        private final Connection connection;
        private final JoinServerConnectionRequest request;

        public ServerGameDescriptionListener(Connection connection, JoinServerConnectionRequest request) {
            this.connection = connection;
            this.request = request;
        }

        @Override
        public void onGameStart() {
            joinServer(request, connection);
        }

        @Override
        public void onGameActivation(boolean activated) {

        }

        @Override
        public void onGameEnd() {

        }
    }

    /** @noinspection UnstableApiUsage*/
    private final ListeningScheduledExecutorService service;
    private final HashMap<String, ServerApplication> serverApplicationMap = new HashMap<>();

    /** @noinspection UnstableApiUsage*/
    public ServerVirtualizationService(com.esotericsoftware.kryonet.Server server) {
        service = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1/*ENG_Utility.getNumberOfCores()*/));
        this.server = server;
    }

    /**
     * For playing from input state file.
     * @param arg
     */
    public void createServer(final ServerConnectionRequest request, String[] arg) {
        createServer(request, null, null, arg);
    }

    /** @noinspection UnstableApiUsage */
    public void createServer(final ServerConnectionRequest request, final Connection connection, final ClientListener clientListener, final String[] arg) {
        final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        final ApplicationStartSettings applicationStartSettings = new ApplicationStartSettings();
        applicationStartSettings.uncaughtExceptionHandler = new ENG_DefaultUncaughtExceptionHandler();
        applicationStartSettings.applicationMode = MainApp.Mode.SERVER;
        applicationStartSettings.outputDebuggingStateDefaultFilename = (request != null ? request.getSessionName() : "noname") + "_" +
                ApplicationStartSettings.OUTPUT_DEBUGGING_STATE_DEFAULT_FILENAME;
        applicationStartSettings.serverConnectionRequest = request;
        applicationStartSettings.server = server;
        applicationStartSettings.clientListener = clientListener;
        final CountDownLatch waitForServerToStart = new CountDownLatch(1);
        applicationStartSettings.waitForServerToStart = waitForServerToStart;

//        ServerApplication put = serverApplicationMap.put(request.getSessionName(), serverApplication);
//        if (put != null) {
//            throw new IllegalArgumentException("Server with name " + request.getSessionName() + " already exists");
//        }
        ListenableScheduledFuture<?> schedule = service.schedule(() -> {
            final ServerApplication serverApplication = new ServerApplication(new BlackholeDarksunMain(arg, applicationStartSettings), cfg);
            // Simulating input does not need this. When the server starts it will add the starting player from the input file.
            if (connection != null && request != null) {
                MainApp.getGame().setListener(new ServerGameDescriptionListener(connection, request));
            }
//                int frameCount = 0;
            while (true) {
                try {
                    long waitTime = serverApplication.mainLoop();
                    APP_Game game = ((BlackholeDarksunMain) serverApplication.getApplicationListener()).getMainActivity().getGame();
//                        if (game != null && game.getGameState() == APP_Game.GameState.ALL_MULTIPLAYER_RESOURCES_LOADED/* &&
//                                (frameCount++ >= ENG_RenderingThread.BUFFER_COUNT)*/) {
//                            waitForServerToStart.countDown();
//                        }
                    try {
                        //noinspection BusyWait
                        Thread.sleep(1000 / waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (MainApp.getGame().isGameEnded()) {
                        MainApp.setMainThread(null);
                        MainApp.setGame(null);
                        Looper.resetMainLooper();
                        DatabaseConnection.getConnection().closeConnection();
                        System.exit(0);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//                service.schedule(this, l, TimeUnit.MILLISECONDS);
        }, 0, TimeUnit.MILLISECONDS);
        Futures.addCallback(schedule, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                clientListener.resetServerCreated();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
        long waitBeginTime = ENG_Utility.currentTimeMillisReal();
        try {
            waitForServerToStart.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Server loading time: " + (ENG_Utility.currentTimeMillisReal() - waitBeginTime));
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void joinServer(JoinServerConnectionRequest request, Connection connection) {
        APP_MultiPlayerGame game = (APP_MultiPlayerGame) MainApp.getGame();
        game.addClient(connection, request);
    }

    public void updateServers() {

    }

    public void removeServer() {

    }
}
