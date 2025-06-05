/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/27/15, 11:45 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net;

import com.esotericsoftware.kryonet.Client;
import headwayent.blackholedarksun.MainApp;

/**
 * Created by sebas on 19.12.2015.
 */
public class DebugClient extends Client {

    public DebugClient() {

    }

    public DebugClient(int writeBufferSize, int objectBufferSize) {
        super(writeBufferSize, objectBufferSize);
    }

    @Override
    public int sendTCP(Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            return super.sendTCP(object);
        }
        return 0;
    }

    @Override
    public int sendUDP(Object object) {
        if (!MainApp.getMainThread().isInputState()) {
            return super.sendUDP(object);
        }
        return 0;
    }
}
