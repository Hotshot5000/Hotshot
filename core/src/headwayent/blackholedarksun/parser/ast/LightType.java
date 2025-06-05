/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.renderer.ENG_Light;

public class LightType extends InitialCondParam {

    public static final String TYPE = "LightType";

    /** @noinspection deprecation*/
    private final ENG_Light.LightTypes lightType;

    /** @noinspection deprecation*/
    public LightType(String type) {
        super(TYPE);
        this.lightType = ENG_Light.LightTypes.getType(type);
    }

    /** @noinspection deprecation*/
    public ENG_Light.LightTypes getLightType() {
        return lightType;
    }
}
