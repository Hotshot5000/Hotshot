/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/24/19, 8:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.blackholedarksun.world.WorldManagerServerSide;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;

/**
 * Created by sebas on 13.01.2016.
 */
public abstract class GameLogicEntityRemoverSystem extends EntityRemoverSystem {
    protected ENG_SceneManager sceneManager;
    protected WorldManagerBase worldManager;

    public GameLogicEntityRemoverSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    protected void begin() {
        super.begin();
        sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        if (MainApp.getApplicationMode() == MainApp.Mode.CLIENT) {
            worldManager = WorldManager.getSingleton();
        } else {
            worldManager = WorldManagerServerSide.getSingleton();
        }
    }

    public abstract boolean isEntityRemovable(EntityProperties entityProperties, ShipProperties shipProperties);

    public void removeEntity(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties) {
//        System.out.println("Removing RemovableEntity: " + entityProperties.getEntity().getName());
        EntityProperties.IRemovable onRemove = entityProperties.getOnRemove();
        if (onRemove != null) {
            onRemove.onRemove(entity);
        }

        // Only used in single player.
        worldManager.onRemoveRemovableEntity(entity, entityProperties, shipProperties);
        worldManager.removeFromWorld(entity, false);

        worldManager.removeFromAvailableNameList(entityProperties.getItem().getName());

        deleteEntity(entity, entityProperties, sceneManager);
    }
}
