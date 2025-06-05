/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

/**
 * Created by Sebi on 01.06.2014.
 */
public abstract class NetBase {

    public enum Type {
        NET_BASE,
        SERVER_LIST_REQUEST,
        SERVER_LIST_RESPONSE,
        SERVER_CONNECTION_REQUEST,
        SERVER_CONNECTION_RESPONSE,
        JOIN_SERVER_CONNECTION_REQUEST,
        MULTIPLAYER_SERVER_FRAME_TCP,
        MULTIPLAYER_SERVER_FRAME_UDP,
        MULTIPLAYER_CLIENT_FRAME_TCP,
        MULTIPLAYER_CLIENT_FRAME_UDP,
        SERVER_LEAVE_REQUEST,
        SERVER_LEAVE_RESPONSE,
        CLIENT_LEAVE_REQUEST,
        CLIENT_LEAVE_RESPONSE,
        SERVER_RESPAWN_REQUEST
    }

    private final Type type;// = Type.NET_BASE;
    // For debugging to calculate the difference between the time we have received the frame and the moment we actually use it.
    private transient long receivedTimestamp;

    public NetBase(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public long getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public void setReceivedTimestamp(long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }
}
