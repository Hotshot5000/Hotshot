/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/14/16, 6:31 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Entity;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.world.WorldManager;

/**
 * Created by sebas on 25.01.2016.
 */
public class PlayerEntityDestroyedVerifierSPSystem extends PlayerEntityDestroyedVerifierSystem {

    private long playerShipEntityId = -1;

    @Override
    public boolean shouldCheckPlayerDestroyed() {
        return playerShipEntityId != -1;
    }

    @Override
    public Entity getPlayerEntity() {
        return WorldManager.getSingleton().getEntityFromLevelObjectEntityId(playerShipEntityId);
    }

    @Override
    public void onPlayerDestroyedAnimationFinished() {
        WorldManager.getSingleton().onPlayerShipDestroyedAnimationFinished();
        playerShipEntityId = -1;
    }

    @Override
    public void onPlayerDestroyed(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties, CameraProperties cameraProperties) {
        WorldManager.getSingleton().onPlayerShipDestroyed(entity, entityProperties, shipProperties, cameraProperties);
    }

    public void setPlayerShipEntityId(long playerShipEntityId) {
        this.playerShipEntityId = playerShipEntityId;
    }
}
