/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:29 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class Health extends ObjectDefinitionParam {

    public static final String TYPE = "Health";

    private final int health;

    public Health(int h) {
        super(TYPE);
        this.health = h;
    }

    public int getHealth() {
        return health;
    }
}
