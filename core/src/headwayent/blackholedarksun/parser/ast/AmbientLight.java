/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:30 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Vector3D;
import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class AmbientLight extends InitialCondParam {

    public static final String TYPE = "AmbientLight";

    private final ENG_ColorValue upper;
    private final ENG_ColorValue lower;
    private final ENG_Vector3D dir;

    public AmbientLight(ENG_ColorValue upper, ENG_ColorValue lower, ENG_Vector3D dir) {
        super(TYPE);
        this.upper = upper;
        this.lower = lower;
        this.dir = dir;
    }

    public ENG_ColorValue getUpper() {
        return upper;
    }

    public ENG_ColorValue getLower() {
        return lower;
    }

    public ENG_Vector3D getDir() {
        return dir;
    }
}
