/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:28 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class MeshName extends ObjectDefinitionParam {

    public static final String TYPE = "MeshName";

    private final String meshName;

    public MeshName(String name) {
        super(TYPE);
        this.meshName = name;
    }

    public String getMeshName() {
        return meshName;
    }
}
