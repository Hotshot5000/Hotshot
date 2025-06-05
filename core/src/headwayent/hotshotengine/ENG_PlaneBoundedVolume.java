/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;


import headwayent.hotshotengine.basictypes.ENG_Boolean;
import headwayent.hotshotengine.basictypes.ENG_Float;
import headwayent.hotshotengine.ENG_Plane.Side;

import java.util.ArrayList;

public class ENG_PlaneBoundedVolume {


    public final ArrayList<ENG_Plane> planes = new ArrayList<>();
    public ENG_Plane.Side outside = ENG_Plane.Side.NEGATIVE_SIDE;
    private final ENG_Vector4D center = new ENG_Vector4D();
    private final ENG_Vector4D halfSize = new ENG_Vector4D();
    private final ENG_Boolean b = new ENG_Boolean();
    private final ENG_Float f = new ENG_Float();

    public ENG_PlaneBoundedVolume() {

    }

    public ENG_PlaneBoundedVolume(Side outside) {
        this.outside = outside;
    }

    public boolean intersects(ENG_AxisAlignedBox box) {
        /*if (box.isNull()) return false;
            if (box.isInfinite()) return true;

            // Get centre of the box
            Vector3 centre = box.getCenter();
            // Get the half-size of the box
            Vector3 halfSize = box.getHalfSize();*/

        if (box.isNull()) {
            return false;
        }

        if (box.isInfinite()) {
            return true;
        }

        box.getCenter(center);
        box.getHalfSize(halfSize);
		
		/*PlaneList::const_iterator i, iend;
            iend = planes.end();
            for (i = planes.begin(); i != iend; ++i)
            {
                const Plane& plane = *i;

                Plane::Side side = plane.getSide(centre, halfSize);
                if (side == outside)
                {
                    // Found a splitting plane therefore return not intersecting
                    return false;
                }
            }

            // couldn't find a splitting plane, assume intersecting
            return true;*/
        int num = planes.size();
        for (int i = 0; i < num; ++i) {
            Side side = planes.get(i).getSide(center, halfSize);
            if (side == outside) {
                return false;
            }
        }
        return true;
    }

    public boolean intersects(ENG_Sphere sphere) {
        int num = planes.size();
        for (int i = 0; i < num; ++i) {
            float d = planes.get(i).getDistance(sphere.center);
            if (outside == Side.NEGATIVE_SIDE) {
                d = -d;
            }
            if ((d - sphere.radius) > 0.0f) {
                return false;
            }
        }
        return true;
    }

    public boolean intersects(ENG_Ray ray) {
        return ENG_Math.intersects(ray, planes, outside == Side.POSITIVE_SIDE,
                b, f);
    }
}
