/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 9:20 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.world.WorldManagerBase;

/**
 * Created by sebas on 19.11.2015.
 */
public class ExplosionWithoutRenderingAnimation extends WithoutRenderingAnimation {
    private static final int NUM_FRAMES_ASTEROID_DESTROY = 5;
    protected static final long TOTAL_ANIM_TIME = 3000;
    protected final EntityProperties entityProperties;
    protected final Entity entity;
    protected int framesBeforeDestruction = NUM_FRAMES_ASTEROID_DESTROY;
    protected boolean shipDestroyed;

    public ExplosionWithoutRenderingAnimation(String name, Entity entity, long totalTime) {
        super(name, totalTime);
        entityProperties = WorldManagerBase.getSingleton().getEntityPropertiesComponentMapper().get(entity);
        this.entity = entity;
    }

    public ExplosionWithoutRenderingAnimation(String s, Entity gameEntity, String explosionSmallMat, float v) {
        super(s, TOTAL_ANIM_TIME);
        entityProperties = WorldManagerBase.getSingleton().getEntityPropertiesComponentMapper().get(gameEntity);
        this.entity = gameEntity;
    }

    @Override
    public void animationFinished() {


        destroyResources();
        entityProperties.setDestroyedAnimationFinished(true);
    }

    @Override
    public void update() {
        float step = getCurrentStep();
        // Ugly hack
        int numFrames = 26;
        int frame = (int) (step * (numFrames - 1));
//        System.out.println("Explosion: " + getName() + " frameNum: " + frame);
        if (!shipDestroyed && frame > framesBeforeDestruction) {
            shipDestroyed = true;
            entityProperties.setDestroyedDuringAnimation(true);

        }
    }
}
