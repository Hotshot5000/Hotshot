/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/10/16, 11:25 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.world;

import com.artemis.Entity;

/**
 * Created by sebas on 10.01.2016.
 * We need a class that monitors objects from the level even if the entities representing those objects are long gone.
 * We need this for the level events checks.
 */
public class LevelEntity {
    private Entity entity;
    private boolean destroyed;
    private boolean exited;

    public LevelEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed() {
        this.destroyed = true;
        entity = null;
        if (destroyed) {

        }
    }

    public boolean isExited() {
        return exited;
    }

    public void setExited() {
        this.exited = true;
        entity = null;
        if (exited) {

        }
    }
}
