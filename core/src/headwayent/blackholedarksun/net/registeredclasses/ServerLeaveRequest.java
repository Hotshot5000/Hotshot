/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/17/16, 9:09 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

/**
 * Created by sebas on 16.03.2016.
 */
public class ServerLeaveRequest extends NetBase {

    private long userId;
    private boolean forced;

    public ServerLeaveRequest() {
        super(Type.SERVER_LEAVE_REQUEST);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }
}
