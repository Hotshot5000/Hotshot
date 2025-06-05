/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.hotshotengine.ENG_Vector3D;

public class Position extends ObjectEventParam {

    public static final String TYPE = "Position";
    private final ENG_Vector3D position;

    public Position(ENG_Vector3D pos) {
        super(TYPE);
        this.position = pos;
    }

    public ENG_Vector3D getPosition() {
        return position;
    }
}
