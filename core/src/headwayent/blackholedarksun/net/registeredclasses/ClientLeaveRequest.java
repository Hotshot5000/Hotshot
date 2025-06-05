/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.registeredclasses;

/**
 * Created by sebas on 16.03.2016.
 */
public class ClientLeaveRequest extends NetBase {

    private boolean forced;

    public ClientLeaveRequest() {
        super(Type.CLIENT_LEAVE_REQUEST);
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }
}
