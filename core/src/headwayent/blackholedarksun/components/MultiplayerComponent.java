/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/16/21, 4:43 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import com.artemis.Component;

/**
 * Created by sebas on 28.12.2015.
 */
public class MultiplayerComponent extends Component {
    private transient boolean dirtyTcp; // No need to set it to true since first frame at creation the object gets sent anyway.
    private transient boolean dirtyUdp = true; // Probably not needed to be set to true.

    /**
     * For multiplayer only send if there is a modification.
     */
    protected void makeDirtyTcp() {
        dirtyTcp = true;
    }

    public void resetDirtyTcp() {
        dirtyTcp = false;
    }

    public boolean isDirtyTcp() {
        return dirtyTcp;
    }

    protected void makeDirtyUdp() {
        dirtyUdp = true;
    }

    public void resetDirtyUdp() {
        dirtyUdp = false;
    }

    public boolean isDirtyUdp() {
        return dirtyUdp;
    }

    public void resetDirty() {
        resetDirtyTcp();
        resetDirtyUdp();
    }
}
