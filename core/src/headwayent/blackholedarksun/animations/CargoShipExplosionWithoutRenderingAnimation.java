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
public class CargoShipExplosionWithoutRenderingAnimation extends WithoutRenderingAnimation {
    protected static final long TOTAL_ANIM_TIME = 15000;
    protected static final int NUM_FRAMES_SHIP_DESTROY = 5;
    protected final EntityProperties entityProperties;
    protected final int numFrames = 26;
    protected boolean shipDestroyed;

    public CargoShipExplosionWithoutRenderingAnimation(String name, Entity entity) {
        super(name, TOTAL_ANIM_TIME);
        entityProperties = WorldManagerBase.getSingleton().getEntityPropertiesComponentMapper().get(entity);
    }

    @Override
    public void animationFinished() {



        destroyResources();
        entityProperties.setDestroyedAnimationFinished(true);
    }

    @Override
    public void update() {
        float step = getCurrentStep();
        if (step > 0.5f) {
            float expStep = (step - 0.5f) * 1.0f / 0.5f;
            int frame = (int) (expStep * (numFrames - 1));
            if (!shipDestroyed && frame > NUM_FRAMES_SHIP_DESTROY) {
                shipDestroyed = true;
                entityProperties.setDestroyedDuringAnimation(true);
            }
        }
    }
}
