/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 6:42 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

import com.esotericsoftware.kryonet.Connection;

import java.util.Locale;

import headwayent.blackholedarksun.MainApp;

/**
 * Created by sebas on 30.10.2015.
 */
public class ServerConnectionResponse extends NetBase {

    public static final int NO_ERROR = 0;
    public static final int ERROR_SERVER_ALREADY_STARTED = -1;
    public static final int ERROR_CODE_PLAYER_ALREADY_ADDED = -2;
    public static final int ERROR_SERVER_NOT_INITIALIZED = -3;
    public static final int ERROR_SERVER_NOT_JOINED = -4;
    public static final int ERROR_SERVER_TIMEOUT = -5;
    public static final int ERROR_GAME_TYPE_NOT_COMPATIBLE = -6;

    public static final String ERROR_SERVER_ALREADY_STARTED_STR = "Server already started. Join instead!";
    public static final String ERROR_CODE_PLAYER_ALREADY_ADDED_STR = "Could not connect to server!";
    public static final String ERROR_SERVER_NOT_INITIALIZED_STR = "Please wait a while and try again!";
    public static final String ERROR_SERVER_NOT_JOINED_STR = "Please try joining the session!";
    public static final String ERROR_SERVER_TIMEOUT_STR = "Server did not respond in a timely manner!";
    public static final String ERROR_GAME_TYPE_NOT_COMPATIBLE_STR = "Server does not allow " + MainApp.PLATFORM.toString().toLowerCase(Locale.US) + " types of games!";

    private int errorCode;
    private long mapId; // Maybe the map has been changed during the time the client got connected.
    private transient Connection connection;

    public ServerConnectionResponse() {
        super(Type.SERVER_CONNECTION_RESPONSE);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case ERROR_CODE_PLAYER_ALREADY_ADDED:
                return ERROR_CODE_PLAYER_ALREADY_ADDED_STR;
            default:
                throw new IllegalArgumentException();
        }
    }
}
