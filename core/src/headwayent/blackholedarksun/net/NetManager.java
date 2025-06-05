/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/19/21, 2:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.minlog.Log;

import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.net.clientapi.tables.Server;
import headwayent.blackholedarksun.net.registeredclasses.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Sebi on 01.06.2014.
 */
public class NetManager {

    public static final int MAX_UDP_DATAGRAM_SIZE = 1400;
    public static final int WRITE_BUFFER_SIZE = 65536;
    public static final int OBJECT_BUFFER_SIZE = 65536;

//    private static NetManager mgr;

    private static final String SERVER_IP = "127.0.0.1";
    private static final int TCP_PORT = 30003;
    private static final int UDP_PORT = 30004;
    private static final int CONN_TIMEOUT = 60000;

    private Client client;
    private final ArrayList<ArrayList<String>> serverList = new ArrayList<>();
    private final HashSet<String> serverNameList = new HashSet<>();
    private final ReentrantLock serverListLock = new ReentrantLock();
    private boolean serverListDirty;

    private Server server;
    private boolean connectionError;
    private ServerConnectionResponse serverConnectionResponse;
    private ServerListener serverListener;

    private final ObjectSpace objectSpace = new ObjectSpace();

    public NetManager() {

//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        mgr = this;
        Log.INFO();//DEBUG();
        Kryo kryo;
        if (MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
            client = new DebugClient(WRITE_BUFFER_SIZE, OBJECT_BUFFER_SIZE);
            kryo = client.getKryo();
//        kryo.register(NetBase.class);
            ClassRegistration.register(kryo);
//            client.start();
        } else {
            com.esotericsoftware.kryonet.Server server = MainApp.getMainThread().getApplicationSettings().server;
            kryo = server.getKryo();
        }
        if (MainApp.PLATFORM == MainApp.Platform.IOS || MainApp.PLATFORM == MainApp.Platform.XROS) {
            // ReflectASM works badly with roboVM.
            ObjectSpace.setAsm(false);
        }
        ObjectSpace.registerClasses(kryo);
//        objectSpace.setExecutor(Executors.newSingleThreadExecutor());
    }

    public void connectToServer(/*Server server*/String shipName,
                                                 long mapId,
                                                 long sessionId,
                                                 String sessionName,
                                                 String serverIp,
                                                 int tcpPort,
                                                 int udpPort,
                                                 boolean createSession,
                                                 String username,
                                                 long userId,
                                                 int spawnPoint,
                                                 final CountDownLatch latch) {
//        setServer(server);
        serverConnectionResponse = null;
        client.start();
        System.out.println("Connecting to: " + serverIp + " tcpPort: " + tcpPort + " udpPort: " + udpPort);
        try {
            client.connect(CONN_TIMEOUT, serverIp, tcpPort, udpPort);
        } catch (IOException e) {
            e.printStackTrace();
            connectionError = true;
        }
        if (connectionError) {
            return;
        }
        if (serverListener != null) {
            client.removeListener(serverListener);
        }
        serverListener = new ServerListener();
        serverListener.setServerConnectionResponseLatch(latch);
        serverListener.setWaitForLoading(true);
//        client.addListener(new Listener.LagListener(500, 500, serverListener));
        client.addListener(serverListener);
        if (createSession) {
            ServerConnectionRequest serverConnectionRequest = new ServerConnectionRequest();
            serverConnectionRequest.setSessionId(sessionId);
            serverConnectionRequest.setMapId(mapId);
            serverConnectionRequest.setShipName(shipName);
            serverConnectionRequest.setSessionName(sessionName);
            serverConnectionRequest.setPlayerName(username);
            serverConnectionRequest.setUserId(userId);
            serverConnectionRequest.setSpawnPoint(spawnPoint);
            client.sendTCP(serverConnectionRequest);
        } else {
            JoinServerConnectionRequest joinServerConnectionRequest = new JoinServerConnectionRequest();
            joinServerConnectionRequest.setSessionId(sessionId);
            joinServerConnectionRequest.setShipName(shipName);
            joinServerConnectionRequest.setPlayerName(username);
            joinServerConnectionRequest.setUserId(userId);
            joinServerConnectionRequest.setSpawnPoint(spawnPoint);
            client.sendTCP(joinServerConnectionRequest);
        }
    }

    public void disconnectFromServer() {
        ServerLeaveRequest serverLeaveRequest = new ServerLeaveRequest();
        serverLeaveRequest.setUserId(MainApp.getGame().getUser().getId());
        client.sendTCP(serverLeaveRequest);
    }

    public void respawnPlayer() {
        ServerRespawnRequest serverRespawnRequest = new ServerRespawnRequest();
        serverRespawnRequest.setUserId(MainApp.getGame().getUser().getId());
        client.sendTCP(serverRespawnRequest);
    }

    public void closeClient() {
        client.close();
    }

    public ServerListener getServerListener() {
        return serverListener;
    }

    public int sendTCP(NetBase obj) {
        return client.sendTCP(obj);
    }

    public int sendUDP(NetBase obj) {
        return client.sendUDP(obj);
    }

    public ServerConnectionResponse getServerConnectionResponse() {
        return serverConnectionResponse;
    }

    public void setServerConnectionResponse(ServerConnectionResponse serverConnectionResponse) {
        this.serverConnectionResponse = serverConnectionResponse;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public int checkPing() {
        int ping = -1;
        if (client.isConnected()) {
            client.updateReturnTripTime();
            ping = client.getReturnTripTime();
        }
        return ping;
    }

    public void registerObjectSpace(int id, Object obj) {
        objectSpace.register(id, obj);
    }

    public void unregisterObjectSpace(int id) {
        objectSpace.remove(id);
    }

    public void addConnectionToObjectSpace(Connection connection) {
        objectSpace.addConnection(connection);
    }

    public void removeConnectionFromObjectSpace(Connection connection) {
        objectSpace.removeConnection(connection);
    }

    public Object getRemoteObject(int id, Class c) {
        return ObjectSpace.getRemoteObject(client, id, c);
    }

//    public void readServerList() {
//        if (!client.isConnected()) {
//            connect();
//        }
//        client.addListener(new Listener() {
//            @Override
//            public void received(Connection connection, Object object) {
//                if (object instanceof NetBase) {
//                    NetBase base = (NetBase) object;
//                    if (base.getType() == NetBase.Type.SERVER_LIST_RESPONSE) {
//                        ServerListResponse response = (ServerListResponse) base;
//                        serverListLock.lock();
//                        try {
//                            for (ArrayList<String> list : response.response) {
//                                if (!serverNameList.contains(list.get(0))) {
//                                    serverList.add(list);
//                                    serverNameList.add(list.get(0));
//                                    serverListDirty = true;
//                                }
//                            }
//                        } finally {
//                            serverListLock.unlock();
//                        }
////                        for (ArrayList<String> list : response.response) {
////                            String resp = "";
////                            for (String s : list) {
////                                resp += s + " ";
////                            }
////                            System.out.println(resp);
////                        }
//
//                    }
//                }
//            }
//        });
//        client.sendTCP(new ServerListRequest());
//    }
//
//    private void connect() {
//        try {
//            client.connect(CONN_TIMEOUT, SERVER_IP, TCP_PORT, UDP_PORT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isServerListDirty() {
//        return serverListDirty;
//    }
//
//    public void resetServerListDirty() {
//        serverListDirty = false;
//    }
//
//    public ArrayList<ArrayList<String>> getServerList() {
//        serverListLock.lock();
//        try {
//            return new ArrayList<ArrayList<String>>(serverList);
//        } finally {
//            serverListLock.unlock();
//        }
//    }

    public static NetManager getSingleton() {
//        if (mgr == null && MainActivity.isDebugmode()) {
//            throw new NullPointerException(
//                    "Net manager not initialized");
//        }
//        return mgr;
        return MainApp.getGame().getNetManager();
    }
}
