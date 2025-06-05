/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class LightDiffuseColor extends InitialCondParam {

    public static final String TYPE = "LightDiffuseColor";

    private final ENG_ColorValue lightDiffuseColor;

    public LightDiffuseColor(ENG_ColorValue c) {
        super(TYPE);
        this.lightDiffuseColor = c;
    }

    public ENG_ColorValue getLightDiffuseColor() {
        return lightDiffuseColor;
    }
}
