/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.effects;

import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector2D;
import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_SceneNode;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_RenderingThread;
import headwayent.hotshotengine.util.objectpool.ENG_ObjectFactory;
import headwayent.hotshotengine.util.objectpool.ENG_ObjectPool;

import java.util.ArrayList;
import java.util.LinkedList;

public class MovementFlareManager {

    private static final int PARTICLE_LAUNCH_RAND = 30;

    private static final int MAX_PARTICLE_NUM = 32;

    public static final float MIN_SHIP_SPEED = 50.0f;

//    private static MovementFlareManager mgr;

    private final LinkedList<MovementFlare> freeParticles = new LinkedList<>();
    private final LinkedList<MovementFlare> activeParticles = new LinkedList<>();
    private boolean visible;

    private final ENG_Matrix4 inverseVPTemp0 = new ENG_Matrix4();

    private final ENG_Matrix4 inverseVPTemp1 = new ENG_Matrix4();

    private final ENG_Matrix4 inverseVP = new ENG_Matrix4();

    private final ENG_Vector3D midPoint = new ENG_Vector3D();

    private final ENG_Vector3D rayTarget = new ENG_Vector3D();

    private final ENG_Vector4D pos = new ENG_Vector4D(true);

    private final ENG_Vector2D origDir = new ENG_Vector2D(0.0f, 1.0f);
    private final ENG_Vector2D currentDir = new ENG_Vector2D();
    private final ArrayList<MovementFlare> emptyList = new ArrayList<>();
    private final ENG_Vector4D retPos = new ENG_Vector4D();

    private final ENG_ObjectPool<MovementFlareCameraVisibilityData> movementFlareCameraVisibilityDataPool =
            new ENG_ObjectPool<>(new ENG_ObjectFactory<MovementFlareCameraVisibilityData>() {
                @Override
                public MovementFlareCameraVisibilityData create() {
                    return new MovementFlareCameraVisibilityData();
                }

                @Override
                public void destroy(MovementFlareCameraVisibilityData obj) {

                }
            }, MAX_PARTICLE_NUM * ENG_RenderingThread.BUFFER_COUNT,
                    true, "movementFlareCameraVisibilityDataPool");

    public MovementFlareManager() {

//        if (mgr == null) {
//            mgr = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
    }

    public void setup() {
        for (int i = 0; i < MAX_PARTICLE_NUM; ++i) {
            freeParticles.add(new MovementFlare(String.valueOf(i), this));
        }
    }

    public void destroy() {
        for (MovementFlare f : activeParticles) {
            f.destroy();
        }
        for (MovementFlare f : freeParticles) {
            f.destroy();
        }
        activeParticles.clear();
        freeParticles.clear();
    }

    public void update(ENG_Matrix4 projMatrix) {
        if (visible) {
            calculateInverseProjectionMatrix(projMatrix);
            int tryNum = ENG_Utility.getRandom().nextInt(5);
            ++tryNum;
            for (int tryCurrentCount = 0; tryCurrentCount < tryNum; ++tryCurrentCount) {
                if (ENG_Utility.hasRandomChanceHit(PARTICLE_LAUNCH_RAND) && getPlayerShipVelocity() > MIN_SHIP_SPEED) {
                    if (!freeParticles.isEmpty()) {
                        MovementFlare flare = freeParticles.poll();
                        flare.resetMovementFlareCameraVisibilityData();
                        for (int i = 0; i < ENG_RenderingThread.BUFFER_COUNT; ++i) {
                            flare.addMovementFlareCameraVisibilityData(movementFlareCameraVisibilityDataPool.get(), i);
                        }
                        float x, y;
                        do {
                            x = ENG_Utility.rangeRandom(-1.0f, 1.0f);
                            y = ENG_Utility.rangeRandom(-1.0f, 1.0f);
                        } while (Math.abs(x) < 0.1f || Math.abs(y) < 0.1f);

//					float z = camera.getNearClipDistance();


                        currentDir.set(x, y);
                        float angleBetween = currentDir.angleBetween(origDir);
                        // In case we are in the right part we don't need to rotate
                        // 30 but 330 degrees. We don't assume the png is
                        // symmetrical.
                        if (x > 0.0f) {
                            angleBetween = ENG_Math.TWO_PI - angleBetween;
                        }
                        currentDir.normalize();
                        flare.setDir(currentDir);
                        // There is a bug in vertex billboard type
                        // Fix later
                        flare.setRotation(-angleBetween);
                        ENG_Vector2D mul = currentDir.mul(1.5f);
                        flare.setCurrentPos(mul.x, mul.y);
                        flare.setVisible(true);
                        activeParticles.add(flare);
                    }
                }
            }
            for (MovementFlare f : activeParticles) {
//				ENG_Vector2D currentPos = f.getCurrentPos();
//				midPoint.set(currentPos.x, currentPos.y, 0.0f);				
//				inverseVP.transform(midPoint, rayTarget);
                f.update(inverseVP);
                if (!f.isVisible()) {
                    emptyList.add(f);
                }
            }
            for (MovementFlare f : emptyList) {
                activeParticles.remove(f);
                freeParticles.add(f);
            }
            emptyList.clear();
        }
    }

    private void calculateInverseProjectionMatrix(ENG_Matrix4 projMatrix) {
//        ENG_Camera camera = ENG_RenderRoot.getRenderRoot()
//                .getSceneManager(APP_Game.SCENE_MANAGER)
//                .getCamera(APP_Game.MAIN_CAM);
//        camera.getProjectionMatrix(inverseVP);
//		camera.getViewMatrix(true, inverseVPTemp1);

//		inverseVPTemp0.concatenate(inverseVPTemp1, inverseVP);
        inverseVP.set(projMatrix);
        inverseVP.invert();
    }

//    public ENG_SceneNode getNode() {
//        return node;
//    }

    private void reset() {
        for (MovementFlare f : activeParticles) {
            f.setVisible(false);
            f.resetMovementFlareCameraVisibilityData();
        }
        freeParticles.addAll(activeParticles);
        activeParticles.clear();
    }

    public float getPlayerShipVelocity() {
        return WorldManager.getSingleton().getEntityPropertiesComponentMapper().get(WorldManager.getSingleton().getPlayerShip()).getVelocity();
    }

    public ENG_Vector4D getPlayerShipPosition() {
        WorldManager.getSingleton().getEntityPropertiesComponentMapper().get(WorldManager.getSingleton().getPlayerShip()).getNode().getPosition(retPos);
        return retPos;
    }

    public ENG_SceneNode getPlayerShipNode() {
        return WorldManager.getSingleton().getEntityPropertiesComponentMapper().get(WorldManager.getSingleton().getPlayerShip()).getNode();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            if (!visible) {
                reset();
            }
        }
    }

//    public static MovementFlareManager getSingleton() {
//        if (MainActivity.isDebugmode() && mgr == null) {
//            throw new NullPointerException("MovementFlareManager is not " +
//                    "initialized");
//        }
//        return mgr;
//    }

}
