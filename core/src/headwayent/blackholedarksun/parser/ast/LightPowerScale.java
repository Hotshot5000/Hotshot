/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class LightPowerScale extends InitialCondParam {

    public static final String TYPE = "LightPowerScale";

    private final float lightPowerScale;

    public LightPowerScale(float scale) {
        super(TYPE);
        this.lightPowerScale = scale;
    }

    public float getLightPowerScale() {
        return lightPowerScale;
    }
}
