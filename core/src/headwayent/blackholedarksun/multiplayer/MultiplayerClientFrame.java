/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:48 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer;

/**
 * Created by sebas on 10.11.2015.
 */
public abstract class MultiplayerClientFrame extends MultiplayerFrame {

    private long userId;

    public MultiplayerClientFrame(Type type) {
        super(type);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "userId: " + userId + " " + super.toString();
    }
}
