/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/16/21, 5:21 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;
import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.blackholedarksun.world.WorldManagerBase;

/**
 * Created by sebas on 19.11.2015.
 */
public class PortalEnteringWithoutRenderingAnimation extends Animation {
    public static final long TOTAL_ANIM_TIME = 5000;
    protected Entity shipEntity;
    protected ShipProperties shipProperties;
    protected EntityProperties entityProperties;
    protected boolean aiEnabled; // Save the ai state to reenable it at the end of anim
    protected boolean invincible;

    public PortalEnteringWithoutRenderingAnimation(String name, Entity shipEntity) {
        super(name, TOTAL_ANIM_TIME);
        setup(shipEntity);
    }

    protected void setup(Entity shipEntity) {
        this.shipEntity = shipEntity;
        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        entityProperties = worldManager.getEntityPropertiesComponentMapper().get(shipEntity);
        shipProperties = worldManager.getShipPropertiesComponentMapper().getSafe(shipEntity);
        if (shipProperties == null) {
            throw new IllegalArgumentException(entityProperties.getNode().getName() + " is not a valid ship entity");
        }
    }

    @Override
    public void start() {
        shipProperties.setShowPortalEntering(true);
        aiEnabled = shipProperties.isAiEnabled();
        shipProperties.setAiEnabled(false);
        invincible = entityProperties.isInvincible();
        entityProperties.setInvincible(true);
        super.start();
    }

    @Override
    public void update() {

    }

    @Override
    public void reloadResources() {

    }

    @Override
    public void destroyResourcesImpl() {

    }

    @Override
    public void animationFinished() {
        
        destroyResources();

        // Restore the previous ai value
        shipProperties.setAiEnabled(aiEnabled);
        shipProperties.setShowPortalEntering(false);
        entityProperties.setInvincible(invincible);
    }
}
