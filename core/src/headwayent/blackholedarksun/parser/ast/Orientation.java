/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Quaternion;

public class Orientation extends ObjectDefinitionParam {

    public static final String TYPE = "Orientation";

    private final ENG_Quaternion orientation;

    public Orientation(ENG_Quaternion q) {
        super(TYPE);
        this.orientation = q;
    }

    public ENG_Quaternion getOrientation() {
        return orientation;
    }
}
