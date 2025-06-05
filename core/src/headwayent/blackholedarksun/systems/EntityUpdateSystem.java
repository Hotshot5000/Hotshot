/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 4/25/16, 7:40 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import headwayent.blackholedarksun.components.EntityProperties;

/**
 * Created by sebas on 07.01.2016.
 */
public class EntityUpdateSystem extends EntityProcessingSystem {
    public EntityUpdateSystem() {
        super(Aspect.all(EntityProperties.class));
    }

    @Override
    protected void process(Entity e) {

    }
}
