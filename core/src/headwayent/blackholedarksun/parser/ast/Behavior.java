/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:30 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import headwayent.blackholedarksun.levelresource.LevelObject;

public class Behavior extends ObjectDefinitionParam {

    public static final String TYPE = "Behavior";

    private final LevelObject.LevelObjectBehavior behavior;

    public Behavior(String b) {
        super(TYPE);
        this.behavior = LevelObject.LevelObjectBehavior.getBehavior(b);
    }

    public LevelObject.LevelObjectBehavior getBehavior() {
        return behavior;
    }
}
