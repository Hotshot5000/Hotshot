/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 11/18/21, 7:57 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.effects;

import com.artemis.Entity;

import headwayent.blackholedarksun.components.EntityProperties;
import headwayent.blackholedarksun.world.WorldManager;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.renderer.ENG_Overlay;
import headwayent.hotshotengine.renderer.ENG_OverlayElement;
import headwayent.hotshotengine.renderer.ENG_OverlayManager;
import headwayent.hotshotengine.renderer.ENG_RenderRoot;
import headwayent.hotshotengine.renderer.ENG_RenderWindow;
import headwayent.hotshotengine.renderer.ENG_TextureNative;
import headwayent.hotshotengine.renderer.nativeinterface.pipeline.ENG_NativeCalls;

public class HitMarker {

    private static final long ANIMATION_DURATION = 1500;
    public static final float HIT_MARKER_OFFSET = 250.0f;
    private final ENG_Overlay hitMarker;
    private final ENG_OverlayElement leftHitMarker;
    private final ENG_OverlayElement rightHitMarker;
    private final ENG_OverlayElement upHitMarker;
    private final ENG_OverlayElement downHitMarker;
    private final ENG_Matrix4 viewMatrix;
    private final ENG_Vector4D enemyShipPos = new ENG_Vector4D(true);
    private final ENG_Vector4D enemyShipPosViewTrans = new ENG_Vector4D(true);
    private final ENG_Vector4D playerShipPos = new ENG_Vector4D(true);
    private final ENG_Vector4D playerShipPosViewTrans = new ENG_Vector4D(true);
//    private final ENG_Vector2D enemy2DPos = new ENG_Vector2D();
    private HitDirection hitDirection = HitDirection.NONE;
    private long animationBeginTime;

    private enum HitDirection {
        LEFT, UP, RIGHT, DOWN, ALL, NONE
    }

    public HitMarker(ENG_Matrix4 viewMatrix, ENG_Matrix4 projMatrix) {
        this.viewMatrix = viewMatrix;

        ENG_OverlayManager overlayManager = ENG_OverlayManager.getSingleton();
        hitMarker = overlayManager.create("hit_marker");
        leftHitMarker = hitMarker.getChild("hit_marker_left");
        rightHitMarker = hitMarker.getChild("hit_marker_right");
        upHitMarker = hitMarker.getChild("hit_marker_up");
        downHitMarker = hitMarker.getChild("hit_marker_down");

        leftHitMarker.setMetricsMode(ENG_OverlayElement.GuiMetricsMode.GMM_PIXELS);
        rightHitMarker.setMetricsMode(ENG_OverlayElement.GuiMetricsMode.GMM_PIXELS);
        upHitMarker.setMetricsMode(ENG_OverlayElement.GuiMetricsMode.GMM_PIXELS);
        downHitMarker.setMetricsMode(ENG_OverlayElement.GuiMetricsMode.GMM_PIXELS);
        ENG_TextureNative hitMarkerLeftTexture = new ENG_TextureNative();
        ENG_TextureNative hitMarkerUpTexture = new ENG_TextureNative();
        ENG_TextureNative hitMarkerRightTexture = new ENG_TextureNative();
        ENG_TextureNative hitMarkerDownTexture = new ENG_TextureNative();
        ENG_NativeCalls.textureManager_getByNameOverlayElement(hitMarkerLeftTexture, leftHitMarker, (short) 0, (short) 0, 0);
        ENG_NativeCalls.textureManager_getByNameOverlayElement(hitMarkerUpTexture, upHitMarker, (short) 0, (short) 0, 0);
        ENG_NativeCalls.textureManager_getByNameOverlayElement(hitMarkerRightTexture, rightHitMarker, (short) 0, (short) 0, 0);
        ENG_NativeCalls.textureManager_getByNameOverlayElement(hitMarkerDownTexture, downHitMarker, (short) 0, (short) 0, 0);

        float hitMarkerLeftHalfWidth = (float) (hitMarkerLeftTexture.getWidth() / 2);
        float hitMarkerLeftHalfHeight = (float) (hitMarkerLeftTexture.getHeight() / 2);

        float hitMarkerUpHalfWidth = (float) (hitMarkerUpTexture.getWidth() / 2);
        float hitMarkerUpHalfHeight = (float) (hitMarkerUpTexture.getHeight() / 2);

        ENG_RenderWindow window = ENG_RenderRoot.getRenderRoot()
                .getCurrentRenderWindow();
        int width = window.getWidth();
        int height = window.getHeight();

        leftHitMarker.setLeft((float) width / 2 - hitMarkerLeftHalfWidth - HIT_MARKER_OFFSET);
        leftHitMarker.setTop((float) height / 2 - hitMarkerLeftHalfHeight);
        leftHitMarker.setWidth(hitMarkerLeftTexture.getWidth());
        leftHitMarker.setHeight(hitMarkerLeftTexture.getHeight());

        rightHitMarker.setLeft((float) width / 2 - hitMarkerLeftHalfWidth + HIT_MARKER_OFFSET);
        rightHitMarker.setTop((float) height / 2 - hitMarkerLeftHalfHeight);
        rightHitMarker.setWidth(hitMarkerRightTexture.getWidth());
        rightHitMarker.setHeight(hitMarkerRightTexture.getHeight());

        upHitMarker.setLeft((float) width / 2 - hitMarkerUpHalfWidth);
        upHitMarker.setTop((float) height / 2 - hitMarkerUpHalfHeight - HIT_MARKER_OFFSET);
        upHitMarker.setWidth(hitMarkerUpTexture.getWidth());
        upHitMarker.setHeight(hitMarkerUpTexture.getHeight());

        downHitMarker.setLeft((float) width / 2 - hitMarkerUpHalfWidth);
        downHitMarker.setTop((float) height / 2 - hitMarkerUpHalfHeight + HIT_MARKER_OFFSET);
        downHitMarker.setWidth(hitMarkerDownTexture.getWidth());
        downHitMarker.setHeight(hitMarkerDownTexture.getHeight());

        hideOverlayElements();
        setVisible(false);
    }

    public void hit(long entityId) {
        WorldManager worldManager = WorldManager.getSingleton();
        Entity ship = worldManager.getShipByGameEntityId(entityId);
        if (ship == null) {
            return;
        }
//        Entity playerShip = worldManager.getPlayerShip();
//        if (playerShip == null) {
//            return;
//        }
        EntityProperties entityProperties = worldManager.getEntityPropertiesComponentMapper().get(ship);
//        EntityProperties playerShipEntityProperties = worldManager.getEntityPropertiesComponentMapper().get(playerShip);

        entityProperties.getNode().getPosition(enemyShipPos);
//        playerShipEntityProperties.getNode().getPosition(playerShipPos);

        viewMatrix.transform(enemyShipPos, enemyShipPosViewTrans);
//        viewMatrix.transform(playerShipPos, playerShipPosViewTrans);

        enemyShipPosViewTrans.normalize();
        if (hitDirection != HitDirection.NONE) {
            // If we are already showing a hit reset for now.
            // TODO maybe blend in the animations.
            hideOverlayElements();
        }
        if (Math.abs(enemyShipPosViewTrans.z) > 0.8f) {
            // The enemy is either in front or behind us.
            // Activate all hit markers.
            hitDirection = HitDirection.ALL;
        } else {
//            enemy2DPos.set(enemyShipPosViewTrans);
            if (Math.abs(enemyShipPosViewTrans.x) > Math.abs(enemyShipPosViewTrans.y)) {
                if (enemyShipPosViewTrans.x > 0.0f) {
                    hitDirection = HitDirection.RIGHT;
                } else {
                    hitDirection = HitDirection.LEFT;
                }
            } else {
                if (enemyShipPosViewTrans.y > 0.0f) {
                    hitDirection = HitDirection.UP;
                } else {
                    hitDirection = HitDirection.DOWN;
                }
            }
        }
        animationBeginTime = ENG_Utility.currentTimeMillis();
//        System.out.println("ship hit from pos: " + enemyShipPosViewTrans + " hit dir: " + hitDirection);
    }

    public void update() {
        if (hitDirection != HitDirection.NONE) {
            if (ENG_Utility.hasTimePassed(animationBeginTime, ANIMATION_DURATION)) {
                hitDirection = HitDirection.NONE;
                hideOverlayElements();
            } else {
                // TODO add fade out animations when hit.
                switch (hitDirection) {
                    case LEFT:
                        leftHitMarker.show();
                        break;
                    case UP:
                        upHitMarker.show();
                        break;
                    case RIGHT:
                        rightHitMarker.show();
                        break;
                    case DOWN:
                        downHitMarker.show();
                        break;
                    case ALL:
                        leftHitMarker.show();
                        upHitMarker.show();
                        rightHitMarker.show();
                        downHitMarker.show();
                        break;
                    case NONE:
                        throw new IllegalStateException();
                }
            }
        }
    }

    private void hideOverlayElements() {
        leftHitMarker.hide();
        rightHitMarker.hide();
        upHitMarker.hide();
        downHitMarker.hide();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            hitMarker.show();
        } else {
            hitMarker.hide();
        }
    }

    public boolean isVisible() {
        return hitMarker.isVisible();
    }
}
