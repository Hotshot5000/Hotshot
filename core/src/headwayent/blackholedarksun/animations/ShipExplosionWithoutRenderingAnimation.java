/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/7/21, 9:08 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.world.WorldManagerBase;

/**
 * Created by sebas on 19.11.2015.
 */
public class ShipExplosionWithoutRenderingAnimation extends WithoutRenderingAnimation {
    protected static final long TOTAL_ANIM_TIME = 3000;
    protected static final int NUM_FRAMES_SHIP_DESTROY = 5;
    protected EntityProperties entityProperties;
    protected Entity shipEntity;
    protected ShipProperties shipProperties;
    protected final int numFrames = 26;
    protected boolean shipDestroyed;

    public ShipExplosionWithoutRenderingAnimation(String name, Entity entity) {
        super(name, TOTAL_ANIM_TIME);
        setup(entity);
    }

    private void setup(Entity shipEntity) {
        this.shipEntity = shipEntity;
        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        entityProperties = worldManager.getEntityPropertiesComponentMapper().get(shipEntity);
        shipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(shipEntity);
        if (shipProperties == null) {
            throw new IllegalArgumentException(entityProperties.getNode().getName() + " is not a valid ship entity");
        }

    }

    @Override
    public void animationFinished() {
        

        destroyResources();
        entityProperties.setDestroyedAnimationFinished(true);
    }

    @Override
    public void update() {
        float step = getCurrentStep();
        int frame = (int) (step * (numFrames - 1));
//        System.out.println("Explosion: " + getName() + " frameNum: " + frame);
        if (!shipDestroyed && frame > NUM_FRAMES_SHIP_DESTROY) {
            shipDestroyed = true;
            entityProperties.setDestroyedDuringAnimation(true);
        }
    }
}
