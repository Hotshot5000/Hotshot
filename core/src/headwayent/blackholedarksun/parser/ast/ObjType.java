/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/8/21, 5:10 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.levelresource.LevelObject;

public class ObjType extends ObjectDefinitionParam {

    public static final String TYPE = "ObjType";

    private final LevelObject.LevelObjectType shipType;

    public ObjType(String type) {
        super(TYPE);
        this.shipType = LevelObject.LevelObjectType.getLevelObjectType(type);
    }

    public LevelObject.LevelObjectType getShipType() {
        return shipType;
    }
}
