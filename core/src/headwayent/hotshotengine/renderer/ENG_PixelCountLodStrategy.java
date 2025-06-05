/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_Math;
import headwayent.hotshotengine.ENG_Matrix4;
import headwayent.hotshotengine.ENG_Vector4D;
import headwayent.hotshotengine.basictypes.ENG_Float;

import java.util.ArrayList;

public class ENG_PixelCountLodStrategy extends ENG_LodStrategy {

//    private static ENG_PixelCountLodStrategy pixelCountLodStrategy;
    private final ENG_Vector4D temp = new ENG_Vector4D();

    public ENG_PixelCountLodStrategy() {
        super("PixelCount");

//        if (pixelCountLodStrategy == null) {
//            pixelCountLodStrategy = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        pixelCountLodStrategy = this;
    }

    @Override
    public float getBaseValue() {

        return Float.MAX_VALUE;
    }

    @Override
    public short getIndexMaterial(float value,
                                  ArrayList<ENG_Float> materialLodValueList) {

        return getIndexDescendingMaterial(value, materialLodValueList);
    }

    @Override
    public short getIndexMesh(float value,
                              ArrayList<ENG_MeshLodUsage> meshLodUsageList) {

        return getIndexDescendingMesh(value, meshLodUsageList);
    }

    @Override
    protected float getValueImpl(ENG_MovableObject movableObject,
                                 ENG_Camera camera) {

        ENG_Viewport viewport = camera.getViewport();
        float viewportArea = viewport.getActualWidth() * viewport.getActualHeight();
        float boundingArea =
                ENG_Math.PI * ENG_Math.sqr(movableObject.getBoundingRadius());

        switch (camera.getProjectionType()) {
            case PT_PERSPECTIVE: {
                float distanceSquared =
                        movableObject.getParentNode().getSquaredViewDepth(camera, temp);

                if (distanceSquared <= ENG_Math.FLT_EPSILON) {
                    return getBaseValue();
                }

                ENG_Matrix4 projectionMatrix = camera.getProjectionMatrix();

                return (boundingArea * viewportArea * projectionMatrix.get(0, 0) *
                        projectionMatrix.get(1, 1)) / distanceSquared;
            }
            case PT_ORTOGRAPHIC: {
                float orthoArea =
                        camera.getOrthoWindowWidth() * camera.getOrthoWindowHeight();

                if (orthoArea <= ENG_Math.FLT_EPSILON) {
                    return getBaseValue();
                }

                return (boundingArea * viewportArea) / orthoArea;
            }
            default:
                throw new IllegalArgumentException();

        }
    }

    @Override
    public boolean isSorted(ArrayList<ENG_Float> values) {

        return isSortedDescending(values);
    }

    @Override
    public void sort(ArrayList<ENG_MeshLodUsage> meshLodUsageList) {

        sortDescending(meshLodUsageList);
    }

    @Override
    public float transformBias(float factor) {

        return factor;
    }

//    public static ENG_PixelCountLodStrategy getSingleton() {
//        if (MainActivity.isDebugmode() && pixelCountLodStrategy == null) {
//            throw new NullPointerException("pixelCountLodStrategy not initialized!");
//        }
//        return pixelCountLodStrategy;
//    }

}
