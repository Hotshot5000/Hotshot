/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net;

import com.esotericsoftware.kryonet.Server;
import headwayent.blackholedarksun.MainApp;

/**
 * Created by sebas on 19.12.2015.
 */
public class DebugServer extends Server {

    public DebugServer() {

    }

    public DebugServer(int writeBufferSize, int objectBufferSize) {
        super(writeBufferSize, objectBufferSize);
    }

    @Override
    public void sendToAllExceptTCP(int connectionID, Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            super.sendToAllExceptTCP(connectionID, object);
        }
    }

    @Override
    public void sendToAllExceptUDP(int connectionID, Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            super.sendToAllExceptUDP(connectionID, object);
        }
    }

    @Override
    public void sendToAllTCP(Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            super.sendToAllTCP(object);
        }
    }

    @Override
    public void sendToAllUDP(Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            super.sendToAllUDP(object);
        }
    }

    @Override
    public void sendToTCP(int connectionID, Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            super.sendToTCP(connectionID, object);
        }
    }

    @Override
    public void sendToUDP(int connectionID, Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            super.sendToUDP(connectionID, object);
        }
    }
}
