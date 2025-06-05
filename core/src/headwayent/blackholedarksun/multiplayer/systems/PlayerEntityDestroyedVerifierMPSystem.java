/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 3/24/16, 8:06 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Entity;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.systems.PlayerEntityDestroyedVerifierSystem;
import headwayent.blackholedarksun.world.WorldManager;

/**
 * Created by sebas on 25.01.2016.
 */
public class PlayerEntityDestroyedVerifierMPSystem extends PlayerEntityDestroyedVerifierSystem {

//    private long playerEntityId;

    @Override
    public boolean shouldCheckPlayerDestroyed() {
        return WorldManager.getSingleton().getPlayerShip() != null;
    }

    @Override
    public Entity getPlayerEntity() {
        return WorldManager.getSingleton().getPlayerShip();
    }

    @Override
    public void onPlayerDestroyedAnimationFinished() {
        WorldManager.getSingleton().onPlayerShipDestroyedAnimationFinished();
    }

    @Override
    public void onPlayerDestroyed(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties, CameraProperties cameraProperties) {
        WorldManager.getSingleton().onPlayerShipDestroyed(entity, entityProperties, shipProperties, cameraProperties);
    }

//    public void setPlayerEntityId(long playerEntityId) {
//        this.playerEntityId = playerEntityId;
//    }
}
