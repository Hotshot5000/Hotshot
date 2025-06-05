/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/22/21, 2:35 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;

/**
 * Created by sebas on 13.01.2016.
 * This is used at the end of a level to cleanly remove all entities from the scene.
 * GameLogicEntityRemoverSystem is the one that removes entities during gameplay.
 */
public class EntityDeleterSystem extends EntityRemoverSystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ENG_SceneManager sceneManager;

    public EntityDeleterSystem() {
        super(Aspect.all(EntityProperties.class));
    }

    @Override
    protected void begin() {
        super.begin();
        sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
    }

    @Override
    protected void process(Entity entity) {
        EntityProperties entityProperties = entityPropertiesMapper.get(entity);
        System.out.println("destroying entity: " + entityProperties.getItem().getName());
        deleteEntity(entity, entityProperties, sceneManager);
    }

    @Override
    protected void end() {
//        GameWorld.getWorld().updateDeletedEntities();
        GameWorld.getWorld().updateEntityStates();
        super.end();
    }
}
