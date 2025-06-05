/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.components;

import com.artemis.Component;

/**
 * Created by sebas on 20.11.2015.
 */
public class MultiplayerEntity extends Component {

    private long entityId;
    private /*transient*/ String entityName;

    public MultiplayerEntity() {

    }

    public MultiplayerEntity(long entityId) {
        this.entityId = entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

//    public void set(MultiplayerEntity entity) {
//
//    }

    @Override
    public String toString() {
        return "entityId: " + entityId + " entityName: " + entityName + " ";
    }
}
