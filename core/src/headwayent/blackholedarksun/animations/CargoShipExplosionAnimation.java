/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/15/21, 4:32 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import java.util.ArrayList;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.compositor.SceneCompositor;
import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_BillboardNative;
import headwayent.hotshotengine.renderer.ENG_BillboardSetNative;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_ParticleSystemNative;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_TiledAnimationNative;

public class CargoShipExplosionAnimation extends CargoShipExplosionWithoutRenderingAnimation {

    private static final String EXPLOSION_SMALL_MAT = "explosion_small_mat";
    private static final String WHITENED_MAT = "whitened_screen_mat";
    private static final int NUM_SMALL_EXPLOSIONS = 32;
    public static final float MAX_SOUND_DISTANCE = 3500.0f;

    private Entity shipEntity;
    private ENG_BillboardSetNative explosionBillboardSet;
    private ENG_SceneNode explosionNode;
    private ENG_SceneNode particleSystemNode;
    private final ArrayList<ENG_BillboardSetNative> smallExplosionBillboardSetList = new ArrayList<>();
    private final ArrayList<ENG_BillboardNative> smallExplosionBillboardList = new ArrayList<>();
    private final ArrayList<ENG_SceneNode> smallExplosionNodeList = new ArrayList<>();
    private final ENG_Vector4D shipPos = new ENG_Vector4D(true);
    private int smallExplosionNumFrames;
    private float whiteScreen;
    private boolean smallExplosionsAdded;
    private boolean particlesEmmited;
    private ENG_ParticleSystemNative particleSystem;
    private ENG_BillboardNative billboard;
    private ENG_TiledAnimationNative tiledAnimation;
    private final ArrayList<ENG_TiledAnimationNative> tiledAnimationList = new ArrayList<>();
//    private long compositorTime;
//    private boolean redCompositorAdded;

    public CargoShipExplosionAnimation(String name, Entity shipEntity) {
        super(name, shipEntity);
        setup(shipEntity);
    }

    private void setup(Entity shipEntity) {
        this.shipEntity = shipEntity;

        ShipProperties shipProperties = WorldManager.getSingleton().getShipPropertiesComponentMapper().getSafe(shipEntity);
        if (shipProperties == null) {
            throw new IllegalArgumentException(entityProperties.getUniqueName() + " is not a valid ship entity");
        }

    }

    @Override
    public void start() {
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        createNodes(sceneManager);
        createBillboard(sceneManager);
        createSmallBillboard(sceneManager);
        SceneCompositor.getSingleton().addColoredCompositor(SceneCompositor.CompositorColor.WHITE, SceneCompositor.whiteCompositorId, TOTAL_ANIM_TIME);
        WorldManager worldManager = WorldManager.getSingleton();
        Entity playerShip = worldManager.getPlayerShip();
        if (playerShip != null) {
            EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(playerShip);
            if (!entityProperties.isDestroyed()) {
                ENG_Vector4D playerShipPosition = entityProperties.getNode().getPosition();
                EntityProperties cargoEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(shipEntity);
                ENG_Vector4D cargoPosition = cargoEntityProperties.getNode().getPosition();
                if (playerShipPosition.distance(cargoPosition) < MAX_SOUND_DISTANCE) {
                    MainApp.getGame().vibrate(APP_Game.VibrationEvent.CARGO_SHIP_EXPLOSION);
                }
            }
        }
        super.start();
    }

    private void createNodes(ENG_SceneManager sceneManager) {
        smallExplosionNodeList.clear();
        explosionNode = sceneManager.getRootSceneNode().createChildSceneNode("ExplosionNode_" + entityProperties.getUniqueName());
        particleSystemNode = sceneManager.getRootSceneNode().createChildSceneNode("ParticleSystemNode_" + entityProperties.getUniqueName());
        for (int i = 0; i < NUM_SMALL_EXPLOSIONS; ++i) {
            smallExplosionNodeList.add(sceneManager.getRootSceneNode().createChildSceneNode("SmallExplosionNode" + i + "_" + entityProperties.getUniqueName()));
        }
    }

    private void createBillboard(ENG_SceneManager sceneManager) {
        System.out.println("Creating billboard set: " + entityProperties.getUniqueName());
        explosionBillboardSet = sceneManager.createBillboardSetNative(entityProperties.getUniqueName() + "_ShipExplosion", 1);
        explosionNode.attachObject(explosionBillboardSet);
        explosionNode.setScale(1.0f, 1.0f, 1.0f);
        explosionBillboardSet.setDatablockName("Fx/ExplosionMaterial");
        billboard = explosionBillboardSet.createBillboard(0.0f, 0.0f, 0.0f, new ENG_ColorValue(ENG_ColorValue.WHITE));
        tiledAnimation = sceneManager.createTiledAnimation(entityProperties.getUniqueName() + "_TiledAnimation",
                ENG_Utility.getUniqueId(), explosionBillboardSet,
//                (i++ % 2 == 0) ? "Fx/ExplosionMaterial1" : "Fx/ExplosionMaterial",
                "Fx/ExplosionMaterial",
                1.0f, 16, 1);
    }

    private void createSmallBillboard(ENG_SceneManager sceneManager) {
        for (int i = 0; i < NUM_SMALL_EXPLOSIONS; ++i) {
            ENG_BillboardSetNative billboardSet = sceneManager.createBillboardSetNative(entityProperties.getUniqueName() + "_" + i, 1);
            billboardSet.setDatablockName("Fx/ExplosionMaterial");
            ENG_BillboardNative billboard = billboardSet.createBillboard(0.0f, 0.0f, 0.0f, new ENG_ColorValue(ENG_ColorValue.WHITE));

            smallExplosionBillboardSetList.add(billboardSet);
            smallExplosionBillboardList.add(billboard);
//            tiledAnimationList.add(tiledAnimation);
        }
    }

    @Override
    public void update() {
        float step = getCurrentStep();
        ENG_TiledAnimationNative tiledAnimation = getTiledAnimation();
        tiledAnimation.updateCurrentFrame();
        int currentFrameNum = tiledAnimation.getCurrentFrameNum();
        if (currentFrameNum != ENG_TiledAnimationNative.FRAME_NUM_UNINITIALIZED) {
            if (!shipDestroyed && currentFrameNum > NUM_FRAMES_SHIP_DESTROY) {
                shipDestroyed = true;
                entityProperties.setDestroyedDuringAnimation(true);
                String destructionSoundName = entityProperties.getDestructionSoundName();
                if (destructionSoundName != null) {
                    WorldManager.getSingleton().playSoundBasedOnDistance(entityProperties, destructionSoundName);
                }
                WorldManagerBase.getSingleton().createDebris(shipEntity, 50);
            }
            //	System.out.println(frame + " current unit state frame");
            updateNodePosition();

            ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
            ENG_Camera camera = sceneManager.getCamera(APP_Game.MAIN_CAM);

//            if (!redCompositorAdded) {
//                if (compositorTime == 0) {
//                    compositorTime = ENG_Utility.currentTimeMillis();
//                }
//                if (ENG_Utility.currentTimeMillis() - compositorTime > 3000) {
//                    SceneCompositor.getSingleton().addColoredCompositor(SceneCompositor.CompositorColor.RED, redCompositorId);
//                    redCompositorAdded = true;
//                }
//            }


            // Add small explosions
            if (!smallExplosionsAdded) {
                smallExplosionsAdded = true;
                for (int i = 0; i < NUM_SMALL_EXPLOSIONS; ++i) {
                    ENG_TiledAnimationNative smallTiledAnimation = sceneManager.createTiledAnimation(entityProperties.getUniqueName() + "_SmallTiledAnimation_" + i,
                            ENG_Utility.getUniqueId(), smallExplosionBillboardSetList.get(i),
//                (i++ % 2 == 0) ? "Fx/ExplosionMaterial1" : "Fx/ExplosionMaterial",
                            "Fx/ExplosionMaterial",
                            1.0f, 16, 1);
                    tiledAnimationList.add(smallTiledAnimation);

                    // Randomize a position across the ship
                    ENG_SceneNode node = smallExplosionNodeList.get(i);
                    // MIGHT NEED TO THINK THIS AGAIN!!!
                    float boundingRadius = entityProperties.getItem().getWorldAABB().getHalfSize().length();
                    node.setPosition(
                            ENG_Math.generateRandomPositionOnRadius(
                                    FrameInterval.CARGO_SHIP_EXPLOSION_SMALL + entityProperties.getNode().getName() + " " + i, boundingRadius)
                                    .addAsPt(entityProperties.getNode().getPosition()));
                    node.setScale(0.5f, 0.5f, 0.5f);
                    node.attachObject(smallExplosionBillboardSetList.get(i));
                }
            }

            if (smallExplosionsAdded) {
                for (int i = 0; i < NUM_SMALL_EXPLOSIONS; ++i) {
                    tiledAnimationList.get(i).updateCurrentFrame();
                }
            }

            if (!particlesEmmited) {
                particlesEmmited = true;
                particleSystem = sceneManager.createParticleSystemNative(entityProperties.getUniqueName() + "_particle_system", 20,"cargo_ship_explosion_system");
//                particleSystem.setMaterialName("Fx/ExplosionMaterial1");
                particleSystemNode.attachParticleSystem(particleSystem);
                particleSystemNode.setPosition(entityProperties.getNode().getPosition());
                particleSystemNode.setOrientation(entityProperties.getNode().getOrientation());

            }
        }
    }

    protected void updateNodePosition() {
        entityProperties.getNode().getPosition(shipPos);
        explosionNode.setPosition(shipPos);
    }

    @Override
    public void reloadResources() {
    }

    @Override
    public void destroyResourcesImpl() {
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);


        SceneCompositor.getSingleton().removeColoredCompositor(SceneCompositor.whiteCompositorId);
//        SceneCompositor.getSingleton().removeColoredCompositor(redCompositorId);
        explosionBillboardSet.destroyBillboard(billboard);
        sceneManager.destroyBillboardSetNative(explosionBillboardSet);
        for (int i = 0; i < NUM_SMALL_EXPLOSIONS; ++i) {
            ENG_BillboardSetNative set = smallExplosionBillboardSetList.get(i);
            set.destroyBillboard(smallExplosionBillboardList.get(i));
            sceneManager.destroyBillboardSetNative(set);
            sceneManager.destroyTiledAnimation(tiledAnimationList.get(i));
        }
        if (particleSystem != null) {
            sceneManager.destroyParticleSystemNative(particleSystem);
        }

        sceneManager.getRootSceneNode().removeAndDestroyChild("ParticleSystemNode_" + entityProperties.getUniqueName());
        sceneManager.getRootSceneNode().removeAndDestroyChild("ExplosionNode_" + entityProperties.getUniqueName());
        for (int i = 0; i < NUM_SMALL_EXPLOSIONS; ++i) {
            sceneManager.getRootSceneNode().removeAndDestroyChild("SmallExplosionNode" + i + "_" + entityProperties.getUniqueName());
        }

        setResourcesDestroyed(true);
        System.out.println("Destroying billboard set: " + entityProperties.getUniqueName());
    }

    public ENG_TiledAnimationNative getTiledAnimation() {
        return tiledAnimation;
    }
}
