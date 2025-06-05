/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/21/15, 12:52 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

/**
 * Created by sebas on 30.10.2015.
 */
public class ServerConnectionRequest extends JoinServerConnectionRequest {

    private long mapId;
    private String sessionName;

    public ServerConnectionRequest() {
        super(Type.SERVER_CONNECTION_REQUEST);
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
}
