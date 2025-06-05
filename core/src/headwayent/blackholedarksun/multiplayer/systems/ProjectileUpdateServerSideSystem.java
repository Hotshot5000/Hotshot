/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.multiplayer.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.components.WeaponProperties;

/**
 * Created by sebas on 09.01.2016.
 */
public class ProjectileUpdateServerSideSystem extends EntityProcessingSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ProjectileProperties> projectilePropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private ComponentMapper<WeaponProperties> weaponPropertiesMapper;

    public ProjectileUpdateServerSideSystem() {
        super(Aspect.all(ProjectileProperties.class));
    }

    @Override
    protected void process(Entity entity) {
        ProjectileProperties projectileProperties = projectilePropertiesMapper.get(entity);
        Entity ship = WorldManagerServerSide.getSingleton().getEntityByGameEntityId(projectileProperties.getParentId());
        if (ship != null) {
            WeaponProperties launcher = weaponPropertiesMapper.getSafe(ship);
            ShipProperties shipProperties = shipPropertiesMapper.getSafe(ship);
            EntityProperties projectileEntityProperties = entityPropertiesMapper.getSafe(entity);
            if (launcher != null && entityPropertiesMapper.get(entity).isDestroyed()) {
                // System.out.println("removing weapon id " + id +
                // " from ship: " +
                // getEntityFromLevelObjectEntityId(
                // projectileProperties.getParentName())
                // .getComponent(ShipProperties.class).getName());
                launcher.removeId(projectileProperties.getType(), projectileProperties.getId());
//                        if (entityByUserIdMap.containsKey(shipProperties.getUserId())) {
//                            userIdToServerProjectileIdToEntityUDP.get(shipProperties.getUserId()).remove(projectileEntityProperties.getEntityId());
//                        }
                // removeFromWorld(entity, false);
//                entitiesToRemove.add(entity);
            }
        }
    }
}
