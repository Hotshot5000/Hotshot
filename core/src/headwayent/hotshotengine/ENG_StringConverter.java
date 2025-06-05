/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class ENG_StringConverter {


    public static ENG_ColorValue parseColourValue(String col) {
        String[] split = col.split(" ");
        ENG_ColorValue ret = new ENG_ColorValue();
        if (split.length == 4) {
            ret.set(Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3]));
        } else if (split.length == 3) {
            ret.set(Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    1.0f);
        }
        return ret;
    }

    public static ENG_Vector2D parseVector2(String val) {
        String[] split = val.split(" ");
        ENG_Vector2D ret = new ENG_Vector2D();
        if (split.length == 2) {
            ret.set(Float.parseFloat(split[0]),
                    Float.parseFloat(split[1])
            );
        }
        return ret;
    }

    public static ENG_Vector3D parseVector3(String val) {
        String[] split = val.split(" ");
        ENG_Vector3D ret = new ENG_Vector3D();
        if (split.length == 3) {
            ret.set(Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]));
        }
        return ret;
    }

    public static ENG_Vector4D parseVector4(String val) {
        String[] split = val.split(" ");
        ENG_Vector4D ret = new ENG_Vector4D();
        if (split.length == 4) {
            ret.set(Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]),
                    Float.parseFloat(split[3]));
        } else if (split.length == 3) {
            ret.set(Float.parseFloat(split[0]),
                    Float.parseFloat(split[1]),
                    Float.parseFloat(split[2]));
        }
        return ret;
    }
}
