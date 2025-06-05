/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/6/20, 8:33 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerBase;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_BillboardNative;
import headwayent.hotshotengine.renderer.ENG_BillboardSetNative;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.ENG_TiledAnimationNative;

public class ShipExplosionAnimation extends ShipExplosionWithoutRenderingAnimation {

    private static final String EXPLOSION_SMALL_MAT = "explosion_small_mat";
    private ENG_BillboardSetNative explosionBillboardSet;
    private ENG_SceneNode explosionNode;
    private final ENG_Vector4D shipPos = new ENG_Vector4D(true);
    private ENG_BillboardNative billboard;
    private ENG_TiledAnimationNative tiledAnimation;

    public ShipExplosionAnimation(String name, Entity shipEntity) {
        super(name, shipEntity);
    }

    @Override
    public void start() {
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        createNode(sceneManager);
        createBillboard(sceneManager);
        updateNodePosition();
        super.start();
    }

    private void createNode(ENG_SceneManager sceneManager) {
        explosionNode = sceneManager.getRootSceneNode().createChildSceneNode("ExplosionNode_" + entityProperties.getUniqueName());
    }

    private void createBillboard(ENG_SceneManager sceneManager) {
//        System.out.println("Creating billboard set: " + entityProperties.getUniqueName());
        explosionBillboardSet = sceneManager.createBillboardSetNative(entityProperties.getUniqueName() + "_ShipExplosion", 1);
        explosionBillboardSet.setDatablockName("Fx/ExplosionMaterial");
        explosionNode.attachObject(explosionBillboardSet);
        explosionNode.setScale(2.0f, 2.0f, 2.0f);
        billboard = explosionBillboardSet.createBillboard(0.0f, 0.0f, 0.0f, new ENG_ColorValue(ENG_ColorValue.WHITE));
        tiledAnimation = sceneManager.createTiledAnimation(entityProperties.getUniqueName() + "_TiledAnimation",
                ENG_Utility.getUniqueId(), explosionBillboardSet,
//                (i++ % 2 == 0) ? "Fx/ExplosionMaterial1" : "Fx/ExplosionMaterial",
                "Fx/ExplosionMaterial",
                1.0f, 16, 1);
    }

    @Override
    public void update() {
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
                WorldManagerBase.getSingleton().createDebris(shipEntity, 20);
            }
        }
        //	System.out.println(frame + " current unit state frame");
        updateNodePosition();
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



        explosionBillboardSet.destroyBillboard(billboard);
        sceneManager.destroyBillboardSetNative(explosionBillboardSet);
        sceneManager.getRootSceneNode().removeAndDestroyChild("ExplosionNode_" + entityProperties.getUniqueName());
        sceneManager.destroyTiledAnimation(tiledAnimation);


        setResourcesDestroyed(true);
//        System.out.println("Destroying billboard set: " + entityProperties.getUniqueName());
    }

    public ENG_TiledAnimationNative getTiledAnimation() {
        return tiledAnimation;
    }
}
