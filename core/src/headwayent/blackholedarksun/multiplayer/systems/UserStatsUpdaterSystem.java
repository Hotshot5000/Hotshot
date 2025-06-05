/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/24/19, 10:09 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.multiplayer.components.PlayerState;
import headwayent.blackholedarksun.multiplayer.rmi.UserStats;
import headwayent.blackholedarksun.world.WorldManagerServerSide;

import java.util.HashMap;

/**
 * Created by sebas on 26.01.2016.
 */
public class UserStatsUpdaterSystem extends VoidEntitySystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;

    public UserStatsUpdaterSystem() {
//        super(Aspect.getAspectFor(PlayerState.class));
    }

    @Override
    protected void processSystem(/*Entity e*/) {
        WorldManagerServerSide worldManagerServerSide = WorldManagerServerSide.getSingleton();
        HashMap<Long, PlayerState> playerStateMap = worldManagerServerSide.getPlayerStateMap();
        GameLogicEntityRemoverServerSideSystem entityRemoverServerSideSystem = GameWorld.getWorld().getSystem(GameLogicEntityRemoverServerSideSystem.class);
        for (PlayerState playerState : playerStateMap.values()) {
            Entity entity = worldManagerServerSide.getEntityByUserId(playerState.getUserId(), false);
            if (entity != null) {
                EntityProperties entityProperties = entityPropertiesMapper.get(entity);
                ShipProperties shipProperties = shipPropertiesMapper.get(entity);
                UserStats userStats = playerState.getUserStats();
                if (entityRemoverServerSideSystem.isEntityRemovable(entityProperties, shipProperties)) {
                    playerState.setLastKills(userStats.getKills());
                    userStats.incrementDeaths();
                } else {
                    userStats.setKills(playerState.getLastKills() + shipProperties.getKills());
                }

                worldManagerServerSide.getUserStatsList().addUserStats(userStats);
            }
        }
    }
}
