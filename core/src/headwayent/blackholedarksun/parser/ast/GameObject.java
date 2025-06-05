/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 8/6/21, 5:14 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class GameObject extends ObjectEventParam {

    public static final String TYPE = "GameObject";

    private final String gameObjectName;

    public GameObject(String name) {
        super(TYPE);
        this.gameObjectName = name;
    }

    public String getGameObjectName() {
        return gameObjectName;
    }
}
