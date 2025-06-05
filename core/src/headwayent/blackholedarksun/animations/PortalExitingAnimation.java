/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import com.artemis.Entity;

import headwayent.blackholedarksun.APP_Game;
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

public class PortalExitingAnimation extends PortalExitingWithoutRenderingAnimation {

    private static final float SHIP_OFFSET_DISTANCE = 100.0f;

    public static final String PORTAL_MAT = "portal_mat";

    private static final float INIT_PORTAL_SCALE = 10.0f;
    private static final float FINAL_PORTAL_SCALE_SHIP = 100.0f;
    private static final float FINAL_PORTAL_SCALE_CARGO = 300.0f;

    private ENG_BillboardSetNative portalBillboardSet;
    private ENG_SceneNode portalNode;
    private final ENG_Quaternion shipOrientation = new ENG_Quaternion();
    private final ENG_Quaternion billboardOrientation = new ENG_Quaternion();
    private final ENG_Vector4D shipMovement = new ENG_Vector4D(true);
    private final ENG_Vector4D shipMovementDirection = new ENG_Vector4D();
    private final ENG_Vector4D shipMovementInverseDirection = new ENG_Vector4D();
    private final ENG_Vector4D shipMovementUpDirection = new ENG_Vector4D();
    private final ENG_Vector4D shipInitialPosition = new ENG_Vector4D(true);
    protected final ENG_Vector4D portalPosition = new ENG_Vector4D(true);
    private final ENG_Vector4D shipBoundingRadiusDir = new ENG_Vector4D(true);
    private final ENG_Vector4D positionToMoveShipTo = new ENG_Vector4D(true);

    private final ENG_Vector4D shipFinalPosition = new ENG_Vector4D(true);

    private float portalScale;
    private ENG_BillboardNative billboard;


    public PortalExitingAnimation(String name, Entity shipEntity) {
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
        entityProperties.getNode().getOrientation(shipOrientation);
        ENG_Quaternion billRot = new ENG_Quaternion();
        ENG_Quaternion.fromAngleAxisDeg(180.0f, ENG_Math.VEC4_Y_UNIT, billRot);
        shipOrientation.mul(billRot, billboardOrientation);

//		portalNode.setOrientation(billboardOrientation);
        shipOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, shipMovementDirection);
        billboardOrientation.mul(ENG_Math.VEC4_NEGATIVE_Z_UNIT, shipMovementInverseDirection);
        billboardOrientation.mul(ENG_Math.VEC4_Y_UNIT, shipMovementUpDirection);
        shipMovementUpDirection.normalize();
        shipMovementInverseDirection.normalize();
        portalBillboardSet.setCommonUpVector(shipMovementUpDirection);
        portalBillboardSet.setCommonDirection(shipMovementInverseDirection);

        // Find the final position for the ship
        shipMovement.set(shipMovementDirection);
        shipMovement.mul(SHIP_OFFSET_DISTANCE);
        entityProperties.getNode().getPosition(shipInitialPosition);
        shipInitialPosition.add(shipMovement, shipFinalPosition);

        // Set portal position
        shipBoundingRadiusDir.set(shipMovementDirection);
        // MIGHT NEED TO THINK THIS AGAIN!!!
        shipBoundingRadiusDir.mul(entityProperties.getItem().getWorldAABB().getHalfSize().length());
        shipFinalPosition.add(shipBoundingRadiusDir, portalPosition);
        portalNode.setPosition(portalPosition);
        portalBillboardSet.setDefaultDimensions(INIT_PORTAL_SCALE, INIT_PORTAL_SCALE);
        WorldManager.getSingleton().playSoundBasedOnDistance(entityProperties, headwayent.blackholedarksun.entitydata.ShipData.getPortalExitingSoundName(shipProperties.getShipData().shipType));
        super.start();
    }

    private void createNode(ENG_SceneManager sceneManager) {
        portalNode = sceneManager.getRootSceneNode().createChildSceneNode("ExitPortalNode_" + entityProperties.getUniqueName());
    }

    private void createBillboard(ENG_SceneManager sceneManager) {
        portalBillboardSet = sceneManager.createBillboardSetNative(entityProperties.getUniqueName(), 1);
        portalNode.attachObject(portalBillboardSet);
        billboard = portalBillboardSet.createBillboard(0.0f, 0.0f, 0.0f, new ENG_ColorValue(ENG_ColorValue.WHITE));
//        ENG_MaterialManager.getSingleton().getByName(PORTAL_MAT).getTechnique((short) 0).getPass((short) 0).setCullingMode(CullingMode.CULL_NONE);
        // We always use the getNode.setPosition() instead of the direct setPosition()
        // in order to bypass the
        // Portal entering uses old style materials not datablocks.
        portalBillboardSet.setMaterialName(PORTAL_MAT, ENG_SceneManager.DEFAULT_RESOURCE_GROUP_NAME);
        portalBillboardSet.setBillboardType(BillboardType.BBT_PERPENDICULAR_COMMON);
    }

    private boolean shipAnimationPartDone;

    @Override
    public void update() {
        

        // We only go to half step and then get rid of the ship
        // after that we still have to close the portal and finish the whole animation
        float step = getCurrentStep();
        float doubleStep = step * 2.0f;
        if (doubleStep < 1.0f) {
            shipMovement.mul(doubleStep, positionToMoveShipTo);
            positionToMoveShipTo.addInPlace(shipInitialPosition);
//            System.out.println("positionToMoveShipTo: " + positionToMoveShipTo);
            entityProperties.setPositionWithoutPhysics(positionToMoveShipTo);
        } else if (!shipAnimationPartDone) {
            shipAnimationPartDone = true;
            exitShip();
        }
        if (doubleStep < 1.0f) {
            float portalScaling = doubleStep * portalScale + INIT_PORTAL_SCALE;
            portalBillboardSet.setDefaultDimensions(portalScaling, portalScaling);
        } else {
            doubleStep -= 1.0f;
            float portalScaling = portalScale - doubleStep * portalScale + INIT_PORTAL_SCALE;
            portalBillboardSet.setDefaultDimensions(portalScaling, portalScaling);
        }
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

        portalBillboardSet.destroyBillboard(billboard);
        sceneManager.destroyBillboardSetNative(portalBillboardSet);
        sceneManager.getRootSceneNode().removeAndDestroyChild("ExitPortalNode_" + entityProperties.getUniqueName());

        setResourcesDestroyed(true);
    }

}
