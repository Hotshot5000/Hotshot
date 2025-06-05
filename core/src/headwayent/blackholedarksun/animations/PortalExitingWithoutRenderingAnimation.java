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
import headwayent.blackholedarksun.world.LevelEntity;
import headwayent.blackholedarksun.world.WorldManagerBase;

/**
 * Created by sebas on 19.11.2015.
 */
public class PortalExitingWithoutRenderingAnimation extends Animation {
    public static final long TOTAL_ANIM_TIME = 20000;
    protected Entity shipEntity;
    protected EntityProperties entityProperties;
    protected ShipProperties shipProperties;
    protected boolean shipExited;
    protected boolean invincible;
    private OnShipExited onShipExitedListener;

    public PortalExitingWithoutRenderingAnimation(String name, Entity shipEntity) {
        super(name, TOTAL_ANIM_TIME);
        setup(shipEntity);
    }

    protected void setup(Entity shipEntity) {
        this.shipEntity = shipEntity;
        WorldManagerBase worldManager = WorldManagerBase.getSingleton();
        entityProperties = worldManager.getEntityPropertiesComponentMapper().get(shipEntity);
        shipProperties = worldManager.getShipPropertiesComponentMapper().get(shipEntity);
        if (shipProperties == null) {
            throw new IllegalArgumentException(entityProperties.getNode().getName() + " is not a valid ship entity");
        }
    }

    @Override
    public void start() {
        shipProperties.setShowPortalExiting(true);
        shipProperties.setAiEnabled(false);
        invincible = entityProperties.isInvincible();
        entityProperties.setInvincible(true);
        super.start();
    }

    @Override
    public void update() {

    }

    public interface OnShipExited {
        void onShipExit();
    }

    protected void exitShip() {
        if (!shipExited) {
            shipProperties.setShowPortalExiting(false);
            shipProperties.setExited(true);
            entityProperties.getNode().flipVisibility(true);
            shipExited = true;
            // Since in MP the player ships are ephemeral there is no level entity associated with them.
            // But since we still have ships that are level based we still have to set them exited so the scripts can continue.
//            if (MainApp.getGame().getGameMode() == APP_Game.GameMode.SP) {
                LevelEntity levelEntity = WorldManagerBase.getSingleton().getLevelEntityFromEntityId(entityProperties.getEntityId());
                if (levelEntity != null) {
                    // We could be exiting the reloader ship which does not belong
                    // to the levelEntity list
                    levelEntity.setExited();
                }
//            }
            if (onShipExitedListener != null) {
                onShipExitedListener.onShipExit();
            }
        }
    }

    @Override
    public void animationFinished() {
        exitShip();
        destroyResources();
        entityProperties.setInvincible(invincible);
    }

    @Override
    public void reloadResources() {

    }

    @Override
    public void destroyResourcesImpl() {

    }

    public OnShipExited getOnShipExitedListener() {
        return onShipExitedListener;
    }

    public void setOnShipExitedListener(OnShipExited onShipExitedListener) {
        this.onShipExitedListener = onShipExitedListener;
    }
}
