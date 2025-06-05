/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;

import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.systems.GameLogicEntityRemoverClientSystem;
import headwayent.blackholedarksun.world.WorldManagerMP;

/**
 * Created by sebas on 13.01.2016.
 */
public class GameLogicEntityRemoverMPSystem extends GameLogicEntityRemoverClientSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private long playerShipEntityId;

    public GameLogicEntityRemoverMPSystem() {
        super(Aspect.all(EntityProperties.class));
    }

    @Override
    protected void process(Entity entity) {
        EntityProperties entityProperties = entityPropertiesMapper.get(entity);
        if (entityProperties.isDestroyed()) {
            System.out.println("Destroying entity GameLogicEntityRemoverMPSystem: " + entityProperties.getName());
            worldManager.destroyEntity(entityProperties);
        }
//            System.out.println("Checking RemovingRemovableEntity: " + entityProperties.getEntity().getName());
        ShipProperties shipProperties = shipPropertiesMapper.getSafe(entity);
        if (isEntityRemovable(entityProperties, shipProperties)) {
            ((WorldManagerMP) worldManager).removeEntityByEntityId(entityProperties.getEntityId());
            removeEntity(entity, entityProperties, shipProperties);
        }
    }

    @Override
    public void removeEntity(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties) {
        super.removeEntity(entity, entityProperties, shipProperties);
        if (playerShipEntityId == entityProperties.getEntityId()) {
            ((WorldManagerMP) worldManager).resetPlayerShip();
            playerShipEntityId = -1;
        }
    }

    @Override
    public boolean isEntityRemovable(EntityProperties entityProperties, ShipProperties shipProperties) {
        boolean playerLeft = shipProperties != null && shipProperties.isPlayerLeft();
        if (playerLeft) {
            HudManager.getSingleton().setPlayerSpawnInfoText("Player: " + entityProperties.getName() + " has left the fight");
        }
        return super.isEntityRemovable(entityProperties, shipProperties) || playerLeft;
    }

    public void setPlayerShipEntityId(long playerShipEntityId) {
        this.playerShipEntityId = playerShipEntityId;
    }
}
