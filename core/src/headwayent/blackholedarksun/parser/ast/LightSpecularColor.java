/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.renderer.ENG_ColorValue;

public class LightSpecularColor extends InitialCondParam {

    public static final String TYPE = "LightSpecularColor";

    private final ENG_ColorValue lightSpecularColor;

    public LightSpecularColor(ENG_ColorValue c) {
        super(TYPE);
        this.lightSpecularColor = c;
    }

    public ENG_ColorValue getLightSpecularColor() {
        return lightSpecularColor;
    }
}
