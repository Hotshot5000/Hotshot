/*
 * Created by Sebastian Bugiu on 24/09/2024, 10:35
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 24/09/2024, 10:35
 * Copyright (c) 2024.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

import headwayent.blackholedarksun.components.AIProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.world.WorldManager;

/**
 * DON'T FORGET TO ALSO UPDATE THE FollowingShipCounterServerSideSystem!!!
 */
public class FollowingShipCounterSPSystem extends EntityProcessingSystem {

    private static final boolean DEBUG = false;
    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<AIProperties> aiPropertiesMapper;

    /**
     * Creates a new EntityProcessingSystem.
     *
     */
    public FollowingShipCounterSPSystem() {
        super(Aspect.all(AIProperties.class));
    }

    @Override
    protected void process(Entity e) {
        AIProperties aiProperties = aiPropertiesMapper.get(e);
        long followedShipId = aiProperties.getFollowedShip();
        if (followedShipId != -1) {
            // Increment that ship's enemy chasing number.
            Entity ship = WorldManager.getSingleton().getEntityByItemId(followedShipId);
            if (ship != null) {
                AIProperties otherShipAIProperties = aiPropertiesMapper.getSafe(ship);
                if (otherShipAIProperties != null) {
                    otherShipAIProperties.incrementChasedByEnemyNum();
                    if (DEBUG) {
                        EntityProperties entityProperties = entityPropertiesMapper.get(e);
                        EntityProperties otherEntityProperties = entityPropertiesMapper.get(ship);
                        System.out.println("incremented chasing enemies for ship: " + otherEntityProperties.getName() + " to: "
                                + otherShipAIProperties.getChasedByEnemyNum() + " followed by ship: " + entityProperties.getName());
                    }
                }
            }
        }
    }
}
