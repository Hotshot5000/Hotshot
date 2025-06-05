/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/7/21, 8:37 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.systems.GameLogicEntityRemoverSystem;

/**
 * Created by sebas on 13.01.2016.
 */
public class GameLogicEntityRemoverServerSideSystem extends GameLogicEntityRemoverSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;

    public GameLogicEntityRemoverServerSideSystem() {
        super(Aspect.all(EntityProperties.class));
    }

    @Override
    protected void process(Entity entity) {
        EntityProperties entityProperties = entityPropertiesMapper.get(entity);
        if (entityProperties.isDestroyed()) {
            System.out.println("Destroying entity GameLogicEntityRemoverServerSideSystem: " + entityProperties.getName());
            worldManager.destroyEntity(entityProperties);
        }
//            System.out.println("Checking RemovingRemovableEntity: " + entityProperties.getEntity().getName());
        ShipProperties shipProperties = shipPropertiesMapper.getSafe(entity);
        if (isEntityRemovable(entityProperties, shipProperties)) {
            removeEntity(entity, entityProperties, shipProperties);
        }
    }

    @Override
    public boolean isEntityRemovable(EntityProperties entityProperties, ShipProperties shipProperties) {
        return (entityProperties.isDestroyed() && entityProperties.getDestroyedAnimation() == null && entityProperties.isDestroyedSent())
                || (shipProperties != null && shipProperties.isExited() && shipProperties.isExitedSent())
                || (entityProperties.isDestroyed() && entityProperties.isDestroyedAnimationFinished() && entityProperties.isDestroyedAnimationFinishedSent())
                || (entityProperties.isDestroyed() && entityProperties.isDestroyedDuringAnimation() && entityProperties.isDestroyedDuringAnimationSent())
                || (shipProperties != null && shipProperties.isPlayerLeft() && shipProperties.isPlayerLeftSent());
    }
}
