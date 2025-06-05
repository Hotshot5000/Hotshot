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
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.WeaponProperties;
import headwayent.blackholedarksun.world.WorldManager;

/**
 * Created by sebas on 07.01.2016.
 */
public class ProjectileUpdateMPSystem extends EntityProcessingSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ProjectileProperties> projectilePropertiesMapper;
    private ComponentMapper<WeaponProperties> weaponPropertiesMapper;

    public ProjectileUpdateMPSystem() {
        super(Aspect.all(ProjectileProperties.class));
    }

    @Override
    protected void process(Entity entity) {
        ProjectileProperties projectileProperties = projectilePropertiesMapper.getSafe(entity);
        if (projectileProperties != null) {
            Entity ship = WorldManager.getSingleton().getShipByGameEntityId(projectileProperties.getParentId());
            if (ship != null) {
                WeaponProperties launcher = weaponPropertiesMapper.getSafe(ship);
                EntityProperties projectileEntityProperties = entityPropertiesMapper.get(entity);
                if (launcher != null && projectileEntityProperties.isDestroyed()) {
                    // System.out.println("removing weapon id " + id +
                    // " from ship: " +
                    // getEntityFromLevelObjectEntityId(
                    // projectileProperties.getParentName())
                    // .getComponent(ShipProperties.class).getName());
                    launcher.removeId(projectileProperties.getType(), projectileProperties.getId());
//                    ((WorldManagerMP) WorldManager.getSingleton()).removeProjectileClientId(projectileEntityProperties.getEntityId());
//                    clientIdToProjectileEntityMap.remove(projectileEntityProperties.getEntityId());
                    // removeFromWorld(entity, false);
//                    entitiesToRemove.add(entity);
                }
            }
        }
    }
}
