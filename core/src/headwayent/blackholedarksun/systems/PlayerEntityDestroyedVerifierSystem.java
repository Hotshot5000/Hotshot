/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/17/21, 8:26 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.Animation;
import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.components.ShipProperties;
import headwayent.hotshotengine.input.ENG_InputManager;

/**
 * Created by sebas on 25.01.2016.
 */
public abstract class PlayerEntityDestroyedVerifierSystem extends VoidEntitySystem {

    private ComponentMapper<EntityProperties> entityPropertiesMapper;
    private ComponentMapper<ShipProperties> shipPropertiesMapper;
    private ComponentMapper<CameraProperties> cameraPropertiesMapper;
    private Animation playerShipDestroyedAnimation;
    private boolean playerDestroyedAnimationFinished;

//    public PlayerEntityDestroyedVerifierSystem() {
//        super(Aspect.getAspectFor(CameraProperties.class));
//    }

    @Override
    protected void processSystem() {
        if (playerDestroyedAnimationFinished) {
            return;
        }
        if (playerShipDestroyedAnimation != null) {
//            System.out.println("Checking destruction animation finished");
            if (playerShipDestroyedAnimation.getAnimationState() == Animation.AnimationState.FINISHED) {
//                System.out.println("Destruction animation finished");
                onPlayerDestroyedAnimationFinished();
                playerDestroyedAnimationFinished = true;
            }
        }
        if (shouldCheckPlayerDestroyed()) {
            if (playerShipDestroyedAnimation != null) {
                return;
            }
            Entity entity = getPlayerEntity();
            if (entity != null) {
                EntityProperties entityProperties = entityPropertiesMapper.getSafe(entity);
                if (entityProperties != null) {
                    if (entityProperties.isDestroyed()) {
                        HudManager.getSingleton().setVisible(false);
                        ENG_InputManager inputManager = ENG_InputManager.getSingleton();
                        inputManager.setInputStack(APP_Game.TOUCH_INPUT_STACK);
                        CameraProperties cameraProperties = cameraPropertiesMapper.get(entity);
                        ShipProperties shipProperties = shipPropertiesMapper.get(entity);
                        onPlayerDestroyed(entity, entityProperties, shipProperties, cameraProperties);
                        playerShipDestroyedAnimation = entityProperties.getDestroyedAnimation();
                        System.out.println("PlayerShip destroyed");
                    }
                } else {
                    // The ship has been removed by the entity remover and now we have nothing.
                    onPlayerDestroyedAnimationFinished();
                }
            }
        }
    }

    public void reset() {
        playerDestroyedAnimationFinished = false;
        playerShipDestroyedAnimation = null;
    }

    public abstract boolean shouldCheckPlayerDestroyed();

    public abstract Entity getPlayerEntity();

    public abstract void onPlayerDestroyedAnimationFinished();

    public abstract void onPlayerDestroyed(Entity entity, EntityProperties entityProperties, ShipProperties shipProperties, CameraProperties cameraProperties);
}
