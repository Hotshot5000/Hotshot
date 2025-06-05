/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ProjectileProperties;
import headwayent.blackholedarksun.components.WeaponProperties;

/**
 * Created by sebas on 07.01.2016.
 */
public class ProjectileUpdateSPSystem extends EntityProcessingSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ProjectileProperties> projectilePropertiesMapper;
    private ComponentMapper<WeaponProperties> weaponPropertiesMapper;
//    private long beginTime;

    public ProjectileUpdateSPSystem() {
        super(Aspect.all(ProjectileProperties.class));
    }

    @Override
    protected void begin() {
        super.begin();
//        beginTime = ENG_Utility.currentTimeMillis();
    }

    @Override
    protected void process(Entity entity) {
        ProjectileProperties projectileProperties = projectilePropertiesMapper.get(entity);
        if (projectileProperties != null) {
            Entity ship = WorldManager.getSingleton().getShipByGameEntityId(projectileProperties.getParentId());
            if (ship != null) {
                WeaponProperties launcher = weaponPropertiesMapper.getSafe(ship);
                if (launcher != null && entityPropertiesMapper.get(entity).isDestroyed()) {
                    // System.out.println("removing weapon id " + id +
                    // " from ship: " +
                    // getEntityFromLevelObjectEntityId(
                    // projectileProperties.getParentName())
                    // .getComponent(ShipProperties.class).getName());
                    launcher.removeId(projectileProperties.getType(), projectileProperties.getId());
                    // removeFromWorld(entity, false);
//                    WorldManager.getSingleton().addToEntitiesToRemove(entity);
//                    System.out.println(entity.getComponent(EntityProperties.class).getName() + " added to EntitiesToRemove()");
                }
            }
        } else {
            // How the hell did we get here???
            System.out.println("projectileProperties == null");
        }
    }

    @Override
    protected void end() {
//        System.out.println("Clearing projectile list containing:");
//        LinkedList<Entity> projectileList = WorldManager.getSingleton().getProjectileList();
//        for (Entity entity : projectileList) {
//            EntityProperties entityProperties = entity.getComponent(EntityProperties.class);
//            if (entityProperties != null) {
//                System.out.println(entityProperties.getName());
//            }
//        }
//        System.out.println("End projectile list");
//
//        WorldManager.getSingleton().clearEntitiesFromList(WorldManager.getSingleton().getProjectileList());
//        System.out.println("ProjectileUpdateSPSystem time: " + (ENG_Utility.currentTimeMillis() - beginTime));
        super.end();
    }
}
