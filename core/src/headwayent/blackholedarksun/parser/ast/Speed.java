/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Vector3D;

public class Speed extends ObjectDefinitionParam {

    public static final String TYPE = "Speed";

    private final ENG_Vector3D speed;

    public Speed(ENG_Vector3D speed) {
        super(TYPE);
        this.speed = speed;
    }

    public ENG_Vector3D getSpeed() {
        return speed;
    }
}
