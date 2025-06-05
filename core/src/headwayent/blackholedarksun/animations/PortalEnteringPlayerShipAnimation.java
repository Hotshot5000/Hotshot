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
import headwayent.blackholedarksun.components.CameraProperties;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.input.ENG_InputManager;
import headwayent.hotshotengine.renderer.ENG_Camera;

/**
 * Created by sebas on 05.03.2016.
 */
public class PortalEnteringPlayerShipAnimation extends PortalEnteringAnimation {

    private CameraProperties cameraProperties;
    private final ENG_Vector4D positionToLookAt = new ENG_Vector4D(true);
    private final ENG_Quaternion shipOrientation = new ENG_Quaternion();
    private final ENG_Vector4D cameraPosOriented = new ENG_Vector4D(true);

    public PortalEnteringPlayerShipAnimation(String name, Entity shipEntity) {
        super(name, shipEntity);
    }

    @Override
    protected void setup(Entity shipEntity) {
        super.setup(shipEntity);
    }

    @Override
    public void start() {
        super.start();
        // Let the super class create the nodes and the billboards.
        cameraProperties = WorldManager.getSingleton().getCameraPropertiesComponentMapper().get(shipEntity);
        ENG_Camera camera = cameraProperties.getCamera();
        entityProperties.getNode().getPosition(positionToLookAt);
        entityProperties.getNode().getOrientation(shipOrientation);
        ENG_Vector4D cameraPos = AnimationHelper.createPositionOnBox(getName() + "_entering", -500.0f, 500.0f, -500.0f, 500.0f, -300.0f, -500.0f);
        shipOrientation.mul(cameraPos, cameraPosOriented);
        cameraPosOriented.addInPlace(positionToLookAt);
        cameraProperties.setAnimatedCamera(true);
        cameraProperties.getNode().setPosition(cameraPosOriented);
        changePlayerShipVisibility();
        HudManager.getSingleton().setVisible(false);
        ENG_InputManager.getSingleton().setInputStack(null);
        MainApp.getGame().vibrate(APP_Game.VibrationEvent.PLAYER_PORTAL_OPENING);
//        printDebug(true);
    }

    public void changePlayerShipVisibility() {
        if (cameraProperties.getType() == CameraProperties.CameraType.FIRST_PERSON) {
            entityProperties.getNode().flipVisibility(false);
        }
    }

    private final boolean first = true;

    @Override
    public void update() {
        super.update();
        // Force the initial orientation of the ship even if the player changes the ship orientation by controls.
        entityProperties.getNode().setOrientation(shipOrientation);
        entityProperties.getNode().getPosition(positionToLookAt);
//        printDebug(false);
        cameraProperties.getNode().lookAt(entityProperties.getNode().getPosition());
//        Utility.lookAt(entityProperties.getNode(), cameraProperties.getNode());
    }

    private void printDebug(boolean first) {
        ENG_Vector3D axes = new ENG_Vector3D();
        float deg = shipOrientation.toAngleAxisDeg(axes);
        System.out.println((first ? "Beginning " : "") + "positionToLookAt: " + positionToLookAt + " orientation: " + axes + " " + deg);
    }

    @Override
    public void animationFinished() {
        super.animationFinished();

        cameraProperties.setAnimatedCamera(false);
        changePlayerShipVisibility();
        HudManager.getSingleton().setVisible(true);
        HudManager.getSingleton().reset();
        ENG_InputManager.getSingleton().setInputStack(APP_Game.IN_GAME_INPUT_STACK);
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
