/*
 * Created by Sebastian Bugiu on 16/02/2025, 19:57
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 16/02/2025, 19:57
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.components;

import com.artemis.Component;

public class WaypointProperties extends Component {

    private int waypointId;
    private int waypointSectorId;

    /**
     * Default empty constructor for Kryo serialization.
     */
    public WaypointProperties() {

    }

    public int getWaypointId() {
        return waypointId;
    }

    public void setWaypointId(int waypointId) {
        this.waypointId = waypointId;
    }

    public int getWaypointSectorId() {
        return waypointSectorId;
    }

    public void setWaypointSectorId(int waypointSectorId) {
        this.waypointSectorId = waypointSectorId;
    }
}
