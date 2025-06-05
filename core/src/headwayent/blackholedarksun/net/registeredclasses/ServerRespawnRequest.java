/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/24/16, 7:08 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

/**
 * Created by sebas on 23.03.2016.
 */
public class ServerRespawnRequest extends NetBase {

    private long userId;

    public ServerRespawnRequest() {
        super(Type.SERVER_RESPAWN_REQUEST);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
