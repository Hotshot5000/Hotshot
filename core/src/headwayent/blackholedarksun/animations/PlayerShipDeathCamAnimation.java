/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/13/21, 6:11 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_Camera;

/**
 * Created by sebas on 09.03.2016.
 */
public class PlayerShipDeathCamAnimation extends Animation {

    protected static final long TOTAL_ANIM_TIME = 3000;
    private final Entity playerShip;
    private final ENG_Vector4D positionToLookAt = new ENG_Vector4D();
    private CameraProperties cameraProperties;

    public PlayerShipDeathCamAnimation(String name, Entity playerShip, ENG_Camera camera) {
        super(name, TOTAL_ANIM_TIME);
        this.playerShip = playerShip;
    }

    @Override
    public void start() {
        WorldManager worldManager = WorldManager.getSingleton();
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().getSafe(playerShip);
        CameraProperties cameraProperties = worldManager.getCameraPropertiesComponentMapper().getSafe(playerShip);
        if (entityProperties != null && cameraProperties != null) {
            this.cameraProperties = cameraProperties;
            entityProperties.getNode().getPosition(positionToLookAt);
            ENG_Vector4D cameraPos = AnimationHelper.createPositionOnBoxAroundPoint(getName(), 300.0f, 500.0f, positionToLookAt);
            cameraProperties.setAnimatedCamera(true);
            cameraProperties.getNode().setPosition(cameraPos);
            MainApp.getGame().vibrate(APP_Game.VibrationEvent.PLAYER_DEATH);
        } else {
            stop();
        }
        super.start();
    }

    @Override
    public void update() {
        // Get the entityProperties every frame. If we can't get it it means that the ship has been destroyed and removed from the world.
        EntityProperties entityProperties = WorldManager.getSingleton().getEntityPropertiesComponentMapper().get(playerShip);
        if (entityProperties != null) {
            entityProperties.getNode().getPosition(positionToLookAt);
            cameraProperties.getNode().lookAt(entityProperties.getNode().getPosition());
//            Utility.lookAt(entityProperties.getNode(), cameraProperties.getNode());
        } else {
            // Continue to look at the projection of the position vector to follow the explosion trails.
            // TODO To be done in the future version.
        }
//        camera.lookAt(positionToLookAt);

    }

    @Override
    public void animationFinished() {
        destroyResources();
//        CameraProperties cameraProperties = WorldManager.getSingleton().getCameraPropertiesComponentMapper().getSafe(playerShip);
//        if (cameraProperties != null) {
//            cameraProperties.setAnimatedCamera(false);
//        }
    }

    @Override
    public void reloadResources() {

    }

    @Override
    public void destroyResourcesImpl() {

    }
}
