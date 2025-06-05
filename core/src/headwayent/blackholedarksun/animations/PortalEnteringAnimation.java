/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.entitydata.ShipData;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Quaternion;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_BillboardNative;
import headwayent.hotshotengine.renderer.ENG_BillboardSet.BillboardType;
import headwayent.hotshotengine.renderer.ENG_BillboardSetNative;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;

import com.artemis.Entity;

public class PortalEnteringAnimation extends PortalEnteringWithoutRenderingAnimation {

    private static final float SHIP_OFFSET_DISTANCE = 100.0f;

    public static final String PORTAL_MAT = "portal_mat";

    private static final float INIT_PORTAL_SCALE = 10.0f;
    private static final float FINAL_PORTAL_SCALE_SHIP = 100.0f;
    private static final float FINAL_PORTAL_SCALE_CARGO = 300.0f;

    private ENG_BillboardSetNative portalBillboardSet;
    private ENG_SceneNode portalNode;
    private final ENG_Quaternion billboardOrientation = new ENG_Quaternion();
    private final ENG_Vector4D shipMovement = new ENG_Vector4D(true);
    private final ENG_Vector4D shipMovementDirection = new ENG_Vector4D();
    private final ENG_Vector4D shipMovementUpDirection = new ENG_Vector4D();
    private final ENG_Vector4D shipInitialPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D portalPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D shipBoundingRadiusDir = new ENG_Vector4D(true);
    private final ENG_Vector4D positionToMoveShipTo = new ENG_Vector4D(true);

    private final ENG_Vector4D shipFinalPosition = new ENG_Vector4D(true);
    private float portalScale;
    private ENG_BillboardNative billboard;

/*	public PortalOpeningAnimation(Entity shipEntity) {

		setup(shipEntity);
	}*/

    public PortalEnteringAnimation(String name, Entity shipEntity) {
        super(name, shipEntity);


    }

    @Override
    protected void setup(Entity shipEntity) {
        super.setup(shipEntity);
        switch (shipProperties.getShipData().shipType) {
            case RELOADER:
            case FIGHTER:
                portalScale = FINAL_PORTAL_SCALE_SHIP - INIT_PORTAL_SCALE;
                break;
            case CARGO:
                portalScale = FINAL_PORTAL_SCALE_CARGO - INIT_PORTAL_SCALE;
                break;
            default:
                throw new IllegalArgumentException("Invalid ship type " + shipProperties.getShipData().shipType);
        }

    }

    @Override
    public void start() {



        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        createNode(sceneManager);
        createBillboard(sceneManager);
        // We always use the getNode.setPosition() instead of the direct setPosition()
        // in order to bypass the

        entityProperties.getNode().getOrientation(billboardOrientation);
//        ENG_Vector4D axis = new ENG_Vector4D(false);
//        float angle = billboardOrientation.toAngleAxisDeg(axis);
//        System.out.println("PortalEnteringAnimation axis: " + axis + " angle: " + angle);
        //	portalNode.setOrientation(billboardOrientation);
        billboardOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, shipMovementDirection);
        billboardOrientation.mul(ENG_Math.VEC4_Y_UNIT, shipMovementUpDirection);
        shipMovementUpDirection.normalize();
        shipMovementDirection.normalize();
        portalBillboardSet.setCommonUpVector(shipMovementUpDirection);
        portalBillboardSet.setCommonDirection(shipMovementDirection);

        // Set ship initial position
        shipMovement.set(shipMovementDirection);
        shipMovement.mul(SHIP_OFFSET_DISTANCE);
        entityProperties.getNode().getPosition(shipInitialPosition);
        shipFinalPosition.set(shipInitialPosition);
        shipInitialPosition.subInPlace(shipMovement);
        entityProperties.setPositionWithoutPhysics(shipInitialPosition);

        System.out.println("SHIP " + entityProperties.getUniqueName() + " initial position: " +
                shipInitialPosition + " final position: " + shipFinalPosition +
                " shipMovementDirection: " + shipMovementDirection + " shipMovementUpDirection: " + shipMovementUpDirection);

        // Set portal position
        shipBoundingRadiusDir.set(shipMovementDirection);
        // MIGHT NEED TO THINK THIS AGAIN!!!
        shipBoundingRadiusDir.mul(entityProperties.getItem().getWorldAABB().getHalfSize().length());
        shipInitialPosition.sub(shipBoundingRadiusDir, portalPosition);
        portalNode.setPosition(portalPosition);
        portalBillboardSet.setDefaultDimensions(INIT_PORTAL_SCALE, INIT_PORTAL_SCALE);
        //	portalNode.setScale(INIT_PORTAL_SCALE, INIT_PORTAL_SCALE, INIT_PORTAL_SCALE);
        //	portalNode.setScale(10, 10, 10);
//		Timer timer = ENG_Utility.createTimerAndStart();
        WorldManager.getSingleton().playSoundBasedOnDistance(entityProperties, ShipData.getPortalEnteringSoundName(shipProperties.getShipData().shipType));

        super.start();
//		ENG_Utility.stopTimer(timer, "start()");
    }

    private void createNode(ENG_SceneManager sceneManager) {
        portalNode = sceneManager.getRootSceneNode().createChildSceneNode("PortalNode_" + entityProperties.getUniqueName());
    }

    private void createBillboard(ENG_SceneManager sceneManager) {
//		Timer timer = ENG_Utility.createTimerAndStart();
        portalBillboardSet = sceneManager.createBillboardSetNative(entityProperties.getUniqueName() + "_OpeningPortal", 1);
        portalNode.attachObject(portalBillboardSet);
        billboard = portalBillboardSet.createBillboard(0.0f, 0.0f, 0.0f, new ENG_ColorValue(ENG_ColorValue.WHITE));
//        ENG_MaterialManager.getSingleton().getByName(PORTAL_MAT).getTechnique((short) 0).getPass((short) 0).setCullingMode(CullingMode.CULL_NONE);
        // Portal entering uses old style materials not datablocks.
        portalBillboardSet.setMaterialName(PORTAL_MAT, ENG_SceneManager.DEFAULT_RESOURCE_GROUP_NAME);
        portalBillboardSet.setBillboardType(BillboardType.BBT_PERPENDICULAR_COMMON);
//		ENG_Utility.stopTimer(timer, "createBillboard()");
    }

    @Override
    public void update() {


        float step = getCurrentStep();
        shipMovement.mul(step, positionToMoveShipTo);
        positionToMoveShipTo.addInPlace(shipInitialPosition);
        entityProperties.setPositionWithoutPhysics(positionToMoveShipTo);
        float portalScaling = step * portalScale + INIT_PORTAL_SCALE;
        portalBillboardSet.setDefaultDimensions(portalScaling, portalScaling);
        //	portalNode.setScale(portalScaling, portalScaling, portalScaling);
    }

    @Override
    public void reloadResources() {

        portalBillboardSet.detachFromParent();
        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);
        sceneManager.destroyBillboardSetNative(portalBillboardSet);
//		createNode(sceneManager);
        portalNode.setPosition(portalPosition);
        createBillboard(sceneManager);
        portalBillboardSet.setCommonUpVector(shipMovementUpDirection);
        portalBillboardSet.setCommonDirection(shipMovementDirection);

    }

    @Override
    public void destroyResourcesImpl() {

        ENG_SceneManager sceneManager = ENG_RenderRoot.getRenderRoot().getSceneManager(APP_Game.SCENE_MANAGER);

        entityProperties.setPositionWithoutPhysics(shipFinalPosition);
        portalBillboardSet.destroyBillboard(billboard);
        sceneManager.destroyBillboardSetNative(portalBillboardSet);
        sceneManager.getRootSceneNode().removeAndDestroyChild("PortalNode_" + entityProperties.getUniqueName());

        setResourcesDestroyed(true);
    }

}
