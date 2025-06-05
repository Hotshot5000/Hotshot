/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:30 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class Ai extends ObjectDefinitionParam {

    public static final String TYPE = "Ai";

    private final boolean enabled;

    public Ai(int i) {
        super(TYPE);
        this.enabled = i == 1;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
