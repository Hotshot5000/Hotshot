/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 11:47 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.animations;

import headwayent.blackholedarksun.gamestatedebugger.FrameInterval;
import headwayent.hotshotengine.ENG_Utility;
import headwayent.hotshotengine.ENG_Vector4D;

/**
 * Created by sebas on 09.03.2016.
 */
public class AnimationHelper {

    public static ENG_Vector4D createPositionOnBoxAroundPoint(String s, float min, float max, ENG_Vector4D aroundPoint) {
        ENG_Vector4D ret = new ENG_Vector4D();
        createPositionOnBoxAroundPoint(s, min, max, aroundPoint, ret);
        return ret;
    }

    public static void createPositionOnBoxAroundPoint(String s, float min, float max, ENG_Vector4D aroundPoint, ENG_Vector4D ret) {
        createPositionOnBox(s, min, max, ret);
        ret.addInPlace(aroundPoint);
    }

    public static ENG_Vector4D createPositionOnBox(String s, float min, float max) {
        ENG_Vector4D ret = new ENG_Vector4D();
        createPositionOnBox(s, min, max, ret);
        return ret;
    }

    public static void createPositionOnBox(String s, float min, float max, ENG_Vector4D ret) {
        float x = ENG_Utility.rangeRandom(FrameInterval.SPHERE_AROUND_POINT_X + "_" + s, min, max);
        float y = ENG_Utility.rangeRandom(FrameInterval.SPHERE_AROUND_POINT_Y + "_" + s, min, max);
        float z = ENG_Utility.rangeRandom(FrameInterval.SPHERE_AROUND_POINT_Z + "_" + s, min, max);
        x *= ENG_Utility.getRandom().nextInt(FrameInterval.SPHERE_AROUND_POINT_SIGNUM_X + "_" + s, 2) == 0 ? -1.0f : 1.0f;
        y *= ENG_Utility.getRandom().nextInt(FrameInterval.SPHERE_AROUND_POINT_SIGNUM_Y + "_" + s, 2) == 0 ? -1.0f : 1.0f;
        z *= ENG_Utility.getRandom().nextInt(FrameInterval.SPHERE_AROUND_POINT_SIGNUM_Z + "_" + s, 2) == 0 ? -1.0f : 1.0f;
        ret.set(x, y, z, 1.0f);
    }

    public static ENG_Vector4D createPositionOnBox(String s, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
        ENG_Vector4D ret = new ENG_Vector4D();
        createPositionOnBox(s, xMin, xMax, yMin, yMax, zMin, zMax, ret);
        return ret;
    }

    public static void createPositionOnBox(String s, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax, ENG_Vector4D ret) {
        float x = ENG_Utility.rangeRandom(FrameInterval.SPHERE_AROUND_POINT_X + "_" + s, xMin, xMax);
        float y = ENG_Utility.rangeRandom(FrameInterval.SPHERE_AROUND_POINT_Y + "_" + s, yMin, yMax);
        float z = ENG_Utility.rangeRandom(FrameInterval.SPHERE_AROUND_POINT_Z + "_" + s, zMin, zMax);
        ret.set(x, y, z, 1.0f);
    }
}
