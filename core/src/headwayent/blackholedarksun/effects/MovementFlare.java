/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.effects;

import java.util.Arrays;

import headwayent.blackholedarksun.APP_Game;
import headwayent.blackholedarksun.GameWorld;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.renderer.ENG_BillboardNative;
import headwayent.hotshotengine.renderer.ENG_BillboardSet.BillboardRotationType;
import headwayent.hotshotengine.renderer.ENG_BillboardSetNative;
import headwayent.hotshotengine.renderer.ENG_Camera;
import headwayent.hotshotengine.renderer.ENG_ColorValue;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_SceneManager;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;

public class MovementFlare {

    private static final float PARTICLE_ACCELERATION = 0.3f;
    private static final float PARTICLE_HEIGHT = 0.08f;
    private static final float PARTICLE_WIDTH = 0.08f;
    private static final String MOVEMENT_PARTICLE_MAT = "Fx/movement_particle_mat";
    private final MovementFlareManager movementFlareManager;
    private ENG_BillboardSetNative set;
    private final String name;
    private boolean visible;
    private ENG_BillboardNative billboard;
    private ENG_SceneNode node;
    private final ENG_Vector2D dir = new ENG_Vector2D();
    private final ENG_Vector2D currentPos = new ENG_Vector2D();
    private final MovementFlareCameraVisibilityData[] movementFlareCameraVisibilityData =
            new MovementFlareCameraVisibilityData[ENG_RenderingThread.BUFFER_COUNT];
    private int currentMovementFlareCameraVisibilityDataPos;
    private float currentAcceleration;
    private int visibilityCheckWaitFrameCount;

    public MovementFlare(String name, MovementFlareManager movementFlareManager) {
        
        this.name = name;
        this.movementFlareManager = movementFlareManager;
//        this.node = movementFlareManager.getNode();
        setup();
    }

    private void setup() {
        ENG_SceneManager sceneManager =
                ENG_RenderRoot.getRenderRoot().getSceneManager(
                        APP_Game.SCENE_MANAGER);
        String sceneNodeName = "MovementFlare_" + name;
        node = sceneManager.getRootSceneNode().createChildSceneNode(sceneNodeName, true);
        // The node has been detached in order to attach it to the player ship when the time comes.
        sceneManager.getRootSceneNode().removeChildNative(node);
        set = sceneManager.createBillboardSetNative(name, 1);
//        set.setMaterialName(MOVEMENT_PARTICLE_MAT, ENG_SceneManager.DEFAULT_RESOURCE_GROUP_NAME);
        set.setDatablockName(MOVEMENT_PARTICLE_MAT);
        set.setDefaultDimensions(PARTICLE_WIDTH, PARTICLE_HEIGHT);
//		set.setBillboardOrigin(ENG_BillboardSet.BillboardOrigin.BBO_CENTER);
//		set.setBillboardType(ENG_BillboardSet.BillboardType.BBT_ORIENTED_COMMON);
//		set.setCommonDirection(ENG_Math.VEC4_NEGATIVE_Z_UNIT);
        // The overlay is render queue group id 254.
//        set.setRenderQueueGroup(253);

        set.setBillboardRotationType(BillboardRotationType.BBR_VERTEX);
        billboard = set.createBillboard(ENG_Math.VEC3_ZERO, ENG_ColorValue.WHITE);
//        ENG_NativeCalls.movableObject_setRenderQueueGroup(set, (byte) 253);
        node.attachObject(set);
    }

    public void destroy() {
        ENG_SceneManager sceneManager =
                ENG_RenderRoot.getRenderRoot().getSceneManager(
                        APP_Game.SCENE_MANAGER);
        node.detachAllObjects();
//        node.detachObject(set.getName());
        set.destroyBillboard(billboard);
        sceneManager.destroyBillboardSetNative(set);
        //	sceneManager.getRootSceneNode().removeAndDestroyChild(sceneNodeName);
        // Delete only if in scene at this point
        // We assume it is in the scene as we no longer clear the scene
        // in gameDeactivate()
        // We again clean the scene only when exiting in gameDeactivate()
//        if (sceneManager.hasSceneNode(node.getName())) {
//            sceneManager.destroySceneNode(node);
//        }
        if (visible) {
            movementFlareManager.getPlayerShipNode().removeChildNative(node);
        }
        sceneManager.getRootSceneNode().addChildNative(node);
        sceneManager.getRootSceneNode().removeAndDestroyChild(node.getName());
    }

    public void update(ENG_Matrix4 inverseVP) {
        float playerShipVelocity = movementFlareManager.getPlayerShipVelocity();
        if (playerShipVelocity < ENG_Math.FLOAT_EPSILON) {
            setVisible(false);
            return;
        }
        if (playerShipVelocity < MovementFlareManager.MIN_SHIP_SPEED) {
            playerShipVelocity = MovementFlareManager.MIN_SHIP_SPEED;
        }
        float delta = GameWorld.getWorld().getDelta();
        float finalSpeed = playerShipVelocity * delta * 0.1f;
        currentAcceleration += PARTICLE_ACCELERATION * delta;
        finalSpeed *= currentAcceleration;
        ENG_Vector2D mul = dir.mul(finalSpeed);
        currentPos.addInPlace(mul);
//        System.out.println("movament flare pos: " + currentPos);
        ENG_Vector3D midPoint = new ENG_Vector3D(currentPos.x, currentPos.y, 0.0f);
        ENG_Vector3D rayTarget = inverseVP.transform(midPoint);
//        System.out.println("rayTarget pos: " + rayTarget);
//        ENG_Vector4D position =
//                MovementFlareManager.getSingleton().getPlayerShipPosition();
//		rayTarget.addInPlace(
//				new ENG_Vector3D(position.x, position.y, position.z));
        ENG_Camera camera = ENG_RenderRoot.getRenderRoot()
                .getSceneManager(APP_Game.SCENE_MANAGER)
                .getCamera(APP_Game.MAIN_CAM);
        // We need the delta since the worldaabb that we have at this point isn't
        // necessarily the latest one so if we send this one to check
        // for visibility we may get a false response since the camera may have
        // advanced with the player beyond the worldaabb point.
        // TODO find a nicer solution.
        rayTarget.z -= camera.getNearClipDistance() + 15.0f;
//        System.out.println("rayTarget pos after z update: " + rayTarget);
//        System.out.println("billboard set worldaabb: " + set.getWorldAABB());
        node.setPosition(rayTarget);
//		set._updateBounds();
//        node._update(true, false);

        // Check the previous visibility data if the ret has been set.
        for (MovementFlareCameraVisibilityData prevVisibilityData : movementFlareCameraVisibilityData) {
            if (prevVisibilityData.retSet.getValue() && !prevVisibilityData.visibility.getValue()) {
                setVisible(false);
                return;
            }
        }

        // Prepare the next batch for visibility check.
        MovementFlareCameraVisibilityData visibilityData =
                movementFlareCameraVisibilityData[currentMovementFlareCameraVisibilityDataPos++];
        if (currentMovementFlareCameraVisibilityDataPos >= movementFlareCameraVisibilityData.length) {
            currentMovementFlareCameraVisibilityDataPos = 0;
        }
        if ((visibilityCheckWaitFrameCount++) >= ENG_RenderingThread.BUFFER_COUNT) {
            camera.isVisibleNative(set.getWorldAABB(), visibilityData.visibility, visibilityData.retSet);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            ENG_SceneManager sceneManager =
                    ENG_RenderRoot.getRenderRoot().getSceneManager(
                            APP_Game.SCENE_MANAGER);
            if (visible) {
                // Since we have just attached the child it means that the current worldAABB of
                // the billboardset is useless. We must first wait for it to be updated in order
                // to be of any use.
                visibilityCheckWaitFrameCount = 0;
                movementFlareManager.getPlayerShipNode().addChildNative(node);
                currentAcceleration = 1.0f;
            } else {
//                if (movementFlareManager.getPlayerShipNode().hasChild(sceneNodeName)) {
                    movementFlareManager.getPlayerShipNode().removeChildNative(node);
//                }
            }
        }
    }

    public ENG_SceneNode getNode() {
        return node;
    }

    public void setDir(ENG_Vector2D dir) {
        this.dir.set(dir);
    }

//	public ENG_Vector2D getCurrentPos() {
//		return currentPos;
//	}

    public void setCurrentPos(float x, float y) {
        currentPos.set(x, y);
    }

    public void setRotation(float angle) {
        billboard.setRotation(angle);
//		node.setOrientation(ENG_Quaternion.fromAngleAxisRadRet(
//				angle, ENG_Math.PT4_Z_UNIT));
    }

    public MovementFlareCameraVisibilityData[] getMovementFlareCameraVisibilityData() {
        return movementFlareCameraVisibilityData;
    }

    public void addMovementFlareCameraVisibilityData(MovementFlareCameraVisibilityData movementFlareCameraVisibilityData, int pos) {
        this.movementFlareCameraVisibilityData[pos] = movementFlareCameraVisibilityData;
    }

    public void resetMovementFlareCameraVisibilityData() {
        Arrays.fill(movementFlareCameraVisibilityData, null);
        currentMovementFlareCameraVisibilityDataPos = 0;
    }
}
