/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 9/16/21, 5:21 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;
import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.HudManager;
import headwayent.blackholedarksun.MainApp;
import headwayent.blackholedarksun.Utility;
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.blackholedarksun.world.WorldManagerSP;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.ENG_Camera;

/**
 * Created by sebas on 05.03.2016.
 */
public class PortalExitingPlayerShipAnimation extends PortalExitingAnimation {

    private CameraProperties cameraProperties;
    private ENG_Camera camera;
    private final ENG_Vector4D shipPosition = new ENG_Vector4D(true);
    private final ENG_Quaternion shipOrientation = new ENG_Quaternion();
    private final ENG_Vector4D cameraBeginPosOriented = new ENG_Vector4D(true);
    private final ENG_Vector4D cameraMovementOrientedDir = new ENG_Vector4D();
    private final ENG_Vector4D cameraMovementStep = new ENG_Vector4D();
    private final ENG_Vector4D cameraPos = new ENG_Vector4D(true);
//    private ENG_Vector4D beginCameraPos = new ENG_Vector4D(true);
//    private ENG_Vector4D endCameraPos = new ENG_Vector4D(true);

    public PortalExitingPlayerShipAnimation(String name, Entity shipEntity) {
        super(name, shipEntity);

    }

    @Override
    public void start() {
        super.start();
        // Let the super class create the nodes and the billboards.
        cameraProperties = WorldManager.getSingleton().getCameraPropertiesComponentMapper().get(shipEntity);
        camera = cameraProperties.getCamera();
        entityProperties.getNode().getPosition(shipPosition);
        entityProperties.getNode().getOrientation(shipOrientation);
        ENG_Vector4D beginCameraPos = AnimationHelper.createPositionOnBox(getName() + "_exitingBeginPos", -500.0f, 500.0f, 0.0f, 500.0f, 150.0f, 300.0f);
        ENG_Vector4D endCameraPos = AnimationHelper.createPositionOnBox(getName() + "_exitingEndPos", -500.0f, 500.0f, 0.0f, 500.0f, 300.0f, 500.0f);
        ENG_Vector4D cameraMovementDir = endCameraPos.subAsVec(beginCameraPos);
        shipOrientation.mul(beginCameraPos, cameraBeginPosOriented);
        shipOrientation.mul(cameraMovementDir, cameraMovementOrientedDir);
        cameraBeginPosOriented.addInPlace(shipPosition);
        cameraProperties.setAnimatedCamera(true);
        cameraProperties.getNode().setPosition(cameraBeginPosOriented);
        changePlayerShipVisibility();
        HudManager.getSingleton().setVisible(false);
        ENG_InputManager.getSingleton().setInputStack(null);
        setOnShipExitedListener(() -> {
            ENG_InputManager.getSingleton().setInputStack(APP_Game.TOUCH_INPUT_STACK);
            WorldManagerSP worldManager = (WorldManagerSP) WorldManager.getSingleton();
            worldManager.setHealth(entityProperties.getHealth());
            worldManager.setKills(shipProperties.getKills());
        });
        MainApp.getGame().vibrate(APP_Game.VibrationEvent.PLAYER_PORTAL_EXITING);
    }

    public void changePlayerShipVisibility() {
        if (cameraProperties.getType() == CameraProperties.CameraType.FIRST_PERSON) {
            entityProperties.getNode().flipVisibility(false);
        }
    }

    @Override
    public void update() {
        super.update();
        float step = getCurrentStep();
        cameraMovementOrientedDir.mulRet(step, cameraMovementStep);
        cameraBeginPosOriented.add(cameraMovementStep, cameraPos);
        cameraProperties.getNode().setPosition(cameraPos);
//        camera.lookAt(portalPosition);
        Utility.lookAt(portalPosition, cameraProperties.getNode(), false);
    }

    @Override
    public void animationFinished() {
        super.animationFinished();
        camera.detachFromParent();
        camera.invalidateView();
//        camera.lookAt(new ENG_Vector4D(0, 0, -100, 0));
        Utility.lookAt(new ENG_Vector4D(0, 0, -100, 0), cameraProperties.getNode(), false);
        cameraProperties.getNode().attachCamera(camera);

        cameraProperties.setAnimatedCamera(false);
    }
//
//    @Override
//    public void reloadResources() {
//
//    }
//
//    @Override
//    public void destroyResourcesImpl() {
//
//    }
}
