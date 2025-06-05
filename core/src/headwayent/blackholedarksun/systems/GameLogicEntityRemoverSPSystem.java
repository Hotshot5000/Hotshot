/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/24/19, 8:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;

/**
 * Created by sebas on 13.01.2016.
 */
public class GameLogicEntityRemoverSPSystem extends GameLogicEntityRemoverClientSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;

    public GameLogicEntityRemoverSPSystem() {
        super(Aspect.all(EntityProperties.class));
    }

    @Override
    protected void process(Entity entity) {
        EntityProperties entityProperties = entityPropertiesMapper.get(entity);
//            System.out.println("Checking RemovingRemovableEntity: " + entityProperties.getEntity().getName());
        ShipProperties shipProperties = shipPropertiesMapper.getSafe(entity);
        if (isEntityRemovable(entityProperties, shipProperties)) {
            removeEntity(entity, entityProperties, shipProperties);
        }

    }

}
