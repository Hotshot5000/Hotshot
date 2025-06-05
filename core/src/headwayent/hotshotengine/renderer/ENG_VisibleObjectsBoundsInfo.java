/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.renderer;

import headwayent.hotshotengine.ENG_AxisAlignedBox;
import headwayent.hotshotengine.ENG_Sphere;
import headwayent.hotshotengine.ENG_Vector4D;

public class ENG_VisibleObjectsBoundsInfo {

    /// The axis-aligned bounds of the visible objects
    public final ENG_AxisAlignedBox aabb = new ENG_AxisAlignedBox();
    /// The axis-aligned bounds of the visible shadow receiver objects
    public final ENG_AxisAlignedBox receiverAabb = new ENG_AxisAlignedBox();
    /// The closest a visible object is to the camera
    public float minDistance;
    /// The farthest a visible objects is from the camera
    public float maxDistance;
    /// The closest a object in the frustum regardless of visibility / shadow caster flags
    public float minDistanceInFrustum;
    /// The farthest object in the frustum regardless of visibility / shadow caster flags
    public float maxDistanceInFrustum;

    private final ENG_Vector4D vsSpherePos = new ENG_Vector4D();

    public ENG_VisibleObjectsBoundsInfo() {
        reset();
    }

    public void reset() {
        aabb.setNull();
        receiverAabb.setNull();
        minDistance = minDistanceInFrustum = Float.NEGATIVE_INFINITY;
        maxDistance = maxDistanceInFrustum = 0.0f;
    }

    public void merge(ENG_AxisAlignedBox boxBounds, ENG_Sphere sphereBounds,
                      ENG_Camera cam) {
        merge(boxBounds, sphereBounds, cam, true);
    }

    public void merge(ENG_AxisAlignedBox boxBounds, ENG_Sphere sphereBounds,
                      ENG_Camera cam, boolean receiver) {
        aabb.merge(boxBounds);
        if (receiver) {
            receiverAabb.merge(boxBounds);
        }
        cam.getViewMatrix(true).transform(sphereBounds.center, vsSpherePos);
        float camDistToCenter = vsSpherePos.length();

        minDistance = Math.min(minDistance,
                Math.max(0.0f, camDistToCenter - sphereBounds.radius));
        maxDistance = Math.max(maxDistance, camDistToCenter + sphereBounds.radius);

        minDistanceInFrustum = Math.min(minDistanceInFrustum,
                Math.max(0.0f, camDistToCenter - sphereBounds.radius));
        maxDistanceInFrustum = Math.max(maxDistanceInFrustum,
                camDistToCenter + sphereBounds.radius);
    }

    public void mergeNonRenderedButInFrustum(ENG_Sphere sphereBounds, ENG_Camera cam) {
        cam.getViewMatrix(true).transform(sphereBounds.center, vsSpherePos);
        float camDistToCenter = vsSpherePos.length();

        minDistanceInFrustum = Math.min(minDistanceInFrustum,
                Math.max(0.0f, camDistToCenter - sphereBounds.radius));
        maxDistanceInFrustum = Math.max(maxDistanceInFrustum,
                camDistToCenter + sphereBounds.radius);
    }
}
