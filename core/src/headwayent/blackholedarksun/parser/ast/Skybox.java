/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 10:27 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class Skybox extends InitialCondParam {

    public static final String TYPE = "Skybox";
    private final String skyboxName;

    public Skybox(String name) {
        super(TYPE);
        this.skyboxName = name;
    }

    public String getSkyboxName() {
        return skyboxName;
    }
}
