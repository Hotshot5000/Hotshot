/*
 * Created by Sebastian Bugiu on 24/09/2024, 10:42
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 24/09/2024, 10:42
 * Copyright (c) 2024.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

import headwayent.blackholedarksun.components.AIProperties;

public class FollowingShipCounterResetSystem extends EntityProcessingSystem {

    private ComponentMapper<AIProperties> aiPropertiesMapper;

    /**
     * Creates a new EntityProcessingSystem.
     *
     */
    public FollowingShipCounterResetSystem() {
        super(Aspect.all(AIProperties.class));
    }

    @Override
    protected void process(Entity e) {
        AIProperties aiProperties = aiPropertiesMapper.get(e);
        aiProperties.resetChasedByEnemyNum();
    }
}
