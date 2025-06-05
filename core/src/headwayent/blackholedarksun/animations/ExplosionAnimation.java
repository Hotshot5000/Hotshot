/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 1/6/20, 8:32 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.components.EntityProperties;
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

public class ExplosionAnimation extends ExplosionWithoutRenderingAnimation {

    public static final String EXPLOSION_SMALL_MAT = "explosion_small_mat";


    private ENG_BillboardSetNative explosionBillboardSet;
    private ENG_SceneNode explosionNode;
    private final ENG_Vector4D pos = new ENG_Vector4D(true);
    private final ENG_Vector4D scaling = new ENG_Vector4D(1.0f, 1.0f, 1.0f, 1.0f);
    private ENG_TiledAnimationNative tiledAnimation;
    private ENG_BillboardNative billboard;

    public ExplosionAnimation(String name, Entity entity) {
        super(name, entity, TOTAL_ANIM_TIME);
    }

    public ExplosionAnimation(String name, Entity entity, String mat) {
        super(name, entity, TOTAL_ANIM_TIME);
    }

    public ExplosionAnimation(String name, Entity entity, String mat, float scale) {
        super(name, entity, TOTAL_ANIM_TIME);
        scaling.set(scale, scale, scale);
    }

    @Override
    public void start() {
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        createNode(sceneManager);
        createBillboard(sceneManager);
        updateNodePosition();
        super.start();
    }

    protected void updateNodePosition() {
        entityProperties.getNode().getPosition(pos);
        explosionNode.setPosition(pos);
    }

    private void createNode(ENG_SceneManager sceneManager) {
        explosionNode = sceneManager.getRootSceneNode().createChildSceneNode("ExplosionNode_" + entityProperties.getItem().getName());
    }

    private void createBillboard(ENG_SceneManager sceneManager) {
//        System.out.println("Creating billboard set: " + entityProperties.getUniqueName());
        explosionBillboardSet = sceneManager.createBillboardSetNative(entityProperties.getUniqueName() + "_AsteroidExplosion", 1);
        explosionNode.attachObject(explosionBillboardSet);
        explosionNode.setScale(scaling);
        explosionBillboardSet.setDatablockName("Fx/ExplosionMaterial");
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
//        tiledAnimation.updateCurrentFrame();
        int currentFrameNum = tiledAnimation.getCurrentFrameNum();
        if (currentFrameNum != ENG_TiledAnimationNative.FRAME_NUM_UNINITIALIZED) {
            if (!shipDestroyed && currentFrameNum > framesBeforeDestruction) {
                shipDestroyed = true;
                entityProperties.setDestroyedDuringAnimation(true);
                String destructionSoundName = entityProperties.getDestructionSoundName();
                if (destructionSoundName != null) {
                    WorldManager.getSingleton().playSoundBasedOnDistance(entityProperties, destructionSoundName);
                }
                WorldManagerBase.getSingleton().createDebris(entity, 10);
            }
        }
        //	System.out.println(frame + " current unit state frame");
        updateNodePosition();
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

    /**
     * @return the framesBeforeDestruction
     */
    public int getFramesBeforeDestruction() {
        return framesBeforeDestruction;
    }

    /**
     * @param framesBeforeDestruction the framesBeforeDestruction to set
     */
    public void setFramesBeforeDestruction(int framesBeforeDestruction) {
        this.framesBeforeDestruction = framesBeforeDestruction;
    }

    /**
     * @return the entityProperties
     */
    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    /**
     * @return the explosionBillboardSet
     */
    public ENG_BillboardSetNative getExplosionBillboardSet() {
        return explosionBillboardSet;
    }

    /**
     * @return the explosionNode
     */
    public ENG_SceneNode getExplosionNode() {
        return explosionNode;
    }

    /**
     * @return the shipDestroyed
     */
    public boolean isShipDestroyed() {
        return shipDestroyed;
    }

    /**
     * @param shipDestroyed the shipDestroyed to set
     */
    public void setShipDestroyed(boolean shipDestroyed) {
        this.shipDestroyed = shipDestroyed;
    }

    /**
     * @return the pos
     */
    public ENG_Vector4D getPos() {
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(ENG_Vector4D pos) {
        this.pos.set(pos);
    }

    public ENG_TiledAnimationNative getTiledAnimation() {
        return tiledAnimation;
    }
}
