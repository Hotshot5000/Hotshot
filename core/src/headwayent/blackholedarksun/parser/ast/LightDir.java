/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Vector3D;

public class LightDir extends InitialCondParam {

    public static final String TYPE = "LightDir";

    private final ENG_Vector3D lightDir;

    public LightDir(ENG_Vector3D lightDir) {
        super(TYPE);
        this.lightDir = lightDir;
    }

    public ENG_Vector3D getLightDir() {
        return lightDir;
    }
}
