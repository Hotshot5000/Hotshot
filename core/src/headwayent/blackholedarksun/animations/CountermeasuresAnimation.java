/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.renderer.ENG_ParticleSystemNative;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

public class CountermeasuresAnimation extends CountermeasuresWithoutRenderingAnimation {

    private static final String COUNTERMEASURE_SYSTEM = "countermeasure_system";
    private static final String EXPLOSION_SMALL_MAT = "countermeasure_mat";
    private final EntityProperties shipEntityProperties;
    private ENG_ParticleSystemNative ps;
    private boolean psCreated;
    private ENG_SceneNode node;


    public CountermeasuresAnimation(String name, Entity shipEntity) {
        super(name, shipEntity, TOTAL_ANIM_TIME);

        shipEntityProperties = WorldManager.getSingleton().getEntityPropertiesComponentMapper().get(shipEntity);

    }

    @Override
    public void start() {

        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        createNode(sceneManager);
        WorldManager.getSingleton().playSoundBasedOnDistance(
                shipEntityProperties,
                ShipData.getCountermeasuresSoundName(
                        ENG_Utility.getRandom().nextInt(FrameInterval.COUNTERMEASURE_ANIMATION_PLAY_SOUND + shipEntityProperties.getUniqueName(),
                                ShipData.COUNTERMEASURES_SOUND_NUM)));
        super.start();
    }

    private void createNode(ENG_SceneManager sceneManager) {
        // We need to take into account that the same ship can have multiple
        // countermeasures launched and still animating. That is why we need unique names.
        node = sceneManager.getRootSceneNode().createChildSceneNode("CountermeasureNode_" + shipEntityProperties.getUniqueName() + "_" + ENG_Utility.currentTimeMillis() + " " + ENG_Utility.getRandom().nextInt());
    }

    @Override
    public void update() {

        super.update();
        if (!psCreated) {
            psCreated = true;
            ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
            // We need to take into account that the same ship can have multiple
            // countermeasures launched and still animating. That is why we need unique names.
            String name = shipEntityProperties.getUniqueName() + "_countermeasure_particle_system_" + ENG_Utility.currentTimeMillis() + " " + ENG_Utility.getRandom().nextInt();

            ps = sceneManager.createParticleSystemNative(name, 20, COUNTERMEASURE_SYSTEM);
//            particleSystem = sceneManager.createParticleSystemNative(entityProperties.getUniqueName() + "_particle_system", 20,"cargo_ship_explosion_system");
//            ps.setMaterialName("Fx/CountermeasureMaterial");
            node.attachParticleSystem(ps);
//            node.setPosition(shipEntityProperties.getNode().getPosition());
//            node.setOrientation(shipEntityProperties.getNode().getOrientation());
//            node.attachObject(ps);
            // Moved to start()
//            shipProperties.setCountermeasureLaunched(true);
        }
        node.setPosition(shipEntityProperties.getNode().getPosition());
        node.setOrientation(shipEntityProperties.getNode().getOrientation());
    }

    @Override
    public void animationFinished() {


        destroyResources();
    }

    @Override
    public void reloadResources() {


        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        if (psCreated) {
            psCreated = false;
            node.detachObject(ps.getName());

            sceneManager.destroyParticleSystemNative(ps);
        }
//		createNode(sceneManager);
    }

    @Override
    public void destroyResourcesImpl() {


        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        sceneManager.getRootSceneNode().removeAndDestroyChild(node.getName());
        if (ps != null) {
            sceneManager.destroyParticleSystemNative(ps);
        }
        shipProperties.setCountermeasureLaunched(false);

//        System.out.println("CountermeasureAnimation destroyResourcesImpl for " + entityProperties.getName());
    }

}
