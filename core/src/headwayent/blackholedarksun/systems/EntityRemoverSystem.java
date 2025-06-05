/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/24/19, 10:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;

import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.StaticEntityProperties;
import headwayent.blackholedarksun.physics.PhysicsUtility;
import headwayent.hotshotengine.renderer.ENG_Item;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

abstract class EntityRemoverSystem extends EntityProcessingSystem {
    public EntityRemoverSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    public static void deleteEntity(Entity entity, EntityProperties entityProperties, ENG_SceneManager sceneManager) {
//        System.out.println("Removing entity: " + entityProperties.getUniqueName());
        ENG_Item item = entityProperties.getItem();
        ENG_SceneNode node = entityProperties.getNode();

        node.detachObject(item.getName());
        sceneManager.destroyItem(item);
        sceneManager.getRootSceneNode().removeAndDestroyChild(node.getName());
        GameWorld.getWorld().deleteEntity(entity);

        PhysicsUtility.disposePhysicsObject(entityProperties);
    }

    public static void deleteStaticEntity(Entity entity, StaticEntityProperties entityProperties, ENG_SceneManager sceneManager) {
//        System.out.println("Removing entity: " + entityProperties.getUniqueName());
        ENG_Item item = entityProperties.getItem();
        ENG_SceneNode node = entityProperties.getNode();

        node.detachObject(item.getName());
        sceneManager.destroyItem(item);
        sceneManager.getRootSceneNode(ENG_SceneManager.SceneMemoryMgrTypes.SCENE_STATIC).removeAndDestroyChild(node.getName());
        GameWorld.getWorld().deleteEntity(entity);

        PhysicsUtility.disposePhysicsObject(entityProperties);
    }
}
