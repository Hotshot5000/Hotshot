/*
 * Created by Sebastian Bugiu on 07/04/2025, 19:43
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 07/04/2025, 19:43
 * Copyright (c) 2025.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.GameWorld;
import headwayent.blackholedarksun.components.StaticEntityProperties;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;

public class StaticEntityDeleterSystem  extends EntityRemoverSystem {

    private ComponentMapper<StaticEntityProperties> staticEntityPropertiesMapper;
    private ENG_SceneManager sceneManager;

    public StaticEntityDeleterSystem() {
        super(Aspect.all(StaticEntityProperties.class));
    }

    @Override
    protected void begin() {
        super.begin();
        sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
    }

    @Override
    protected void process(Entity entity) {
        StaticEntityProperties staticEntityProperties = staticEntityPropertiesMapper.get(entity);
        System.out.println("destroying static entity: " + staticEntityProperties.getItem().getName());
        deleteStaticEntity(entity, staticEntityProperties, sceneManager);
    }

    @Override
    protected void end() {
//        GameWorld.getWorld().updateDeletedEntities();
        GameWorld.getWorld().updateEntityStates();
        super.end();
    }
}
