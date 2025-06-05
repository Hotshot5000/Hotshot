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
import headwayent.hotshotengine.renderer.ENG_Frustum.ProjectionType;

import java.util.ArrayList;

public class ENG_DistanceLodStrategy extends ENG_LodStrategy {

//    private static ENG_DistanceLodStrategy distanceLodStrategy;

    private boolean mReferenceViewEnabled;
    private float mReferenceViewValue = -1.0f;
    private final ENG_Vector4D temp = new ENG_Vector4D();

    public ENG_DistanceLodStrategy() {
        super("Distance");
        
//        if (distanceLodStrategy == null) {
//            distanceLodStrategy = this;
//        } else {
//            throw new ENG_MultipleSingletonConstructAttemptException();
//        }
//        distanceLodStrategy = this;
    }

    @Override
    public float getBaseValue() {
        
        return 0.0f;
    }

    public float transformUserValue(float userValue) {
        return ENG_Math.sqr(userValue);
    }

    @Override
    public short getIndexMaterial(float value,
                                  ArrayList<ENG_Float> materialLodValueList) {
        
        return getIndexAscendingMaterial(value, materialLodValueList);
    }

    @Override
    public short getIndexMesh(float value,
                              ArrayList<ENG_MeshLodUsage> meshLodUsageList) {
        
        return getIndexAscendingMesh(value, meshLodUsageList);
    }

    @Override
    protected float getValueImpl(ENG_MovableObject movableObject,
                                 ENG_Camera camera) {
        
        float squaredDepth =
                movableObject.getParentNode().getSquaredViewDepth(camera, temp) -
                        ENG_Math.sqr(movableObject.getBoundingRadius());

        if (mReferenceViewEnabled) {
            if (camera.getProjectionType() != ProjectionType.PT_PERSPECTIVE) {
                throw new IllegalArgumentException(
                        "Camera projection type must be perspective!");
            }

            ENG_Viewport viewport = camera.getViewport();

            float viewportArea = (float) (viewport.getActualWidth() * viewport.getActualHeight());

            ENG_Matrix4 projectionMatrix = camera.getProjectionMatrix();

            float biasValue = viewportArea *
                    projectionMatrix.get(0, 0) *
                    projectionMatrix.get(1, 1);

            squaredDepth *= (mReferenceViewValue / biasValue);
        }

        squaredDepth = Math.max(squaredDepth, 0.0f);
        return squaredDepth * camera._getLodBiasInverse();
    }

    @Override
    public boolean isSorted(ArrayList<ENG_Float> values) {
        
        return isSortedAscending(values);
    }

    @Override
    public void sort(ArrayList<ENG_MeshLodUsage> meshLodUsageList) {
        
        sortAscending(meshLodUsageList);
    }

    @Override
    public float transformBias(float factor) {
        
        if (factor <= 0.0f) {
            throw new IllegalArgumentException("Bias factor must be > 0!");
        }
        return 1.0f / factor;
    }

    public void setReferenceView(float viewportWidth, float viewportHeight,
                                 float fovY) {
        float fovX = fovY * (viewportWidth / viewportHeight);
        float viewportArea = viewportWidth * viewportHeight;
        mReferenceViewValue = viewportArea *
                ENG_Math.tan(fovX * 0.5f) * ENG_Math.tan(fovY * 0.5f);
        mReferenceViewEnabled = true;
    }

    public void setReferenceViewEnabled(boolean value) {
        if (value && mReferenceViewValue == -1.0f) {
            throw new IllegalArgumentException(
                    "Reference view must be set before being enabled!");
        }
        mReferenceViewEnabled = value;
    }

    public boolean getReferenceViewEnabled() {
        return mReferenceViewEnabled;
    }

//    public static ENG_DistanceLodStrategy getSingleton() {
//        if (MainActivity.isDebugmode() && distanceLodStrategy == null) {
//            throw new NullPointerException("DistanceLodStrategy not initialized");
//        }
//        return distanceLodStrategy;
//    }

}
