/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/24/19, 10:07 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package headwayent.blackholedarksun.animations;

import com.artemis.Entity;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.renderer.ENG_TiledAnimationNative;

/**
 * @author sebi
 */
public class ProjectileExplosionAnimation extends ExplosionAnimation {

    public ProjectileExplosionAnimation(String name, Entity shipEntity) {
        super(name, shipEntity);
//        System.out.println("Created ProjectileExplosionAnimation for entity: " + entityProperties.getUniqueName());
    }


    @Override
    public void update() {
        ENG_TiledAnimationNative tiledAnimation = getTiledAnimation();
        tiledAnimation.updateCurrentFrame();
        int currentFrameNum = tiledAnimation.getCurrentFrameNum();
        if (currentFrameNum != ENG_TiledAnimationNative.FRAME_NUM_UNINITIALIZED) {
            if (currentFrameNum > 7) {
//                System.out.println("projectile destruction reached");
            }
//            System.out.println("currentFrameNum: " + currentFrameNum);
        }
        if (!isShipDestroyed()) {
//            System.out.println("ProjectileExplosionAnimation setShipDestroyed(true): " + entityProperties.getUniqueName());
            setShipDestroyed(true);
            getEntityProperties().setDestroyedDuringAnimation(true);
            String destructionSoundName = getEntityProperties().getDestructionSoundName();
            if (destructionSoundName != null) {
                WorldManager.getSingleton().playSoundBasedOnDistance(getEntityProperties(), destructionSoundName);
            }
        }
    }

    @Override
    public void animationFinished() {
//        System.out.println("Animation finished for entity: " + entityProperties.getUniqueName());
        super.animationFinished();
    }
}
