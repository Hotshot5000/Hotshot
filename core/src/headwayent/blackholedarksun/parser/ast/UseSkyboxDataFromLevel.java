/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class UseSkyboxDataFromLevel extends InitialCondParam {

    public static final String TYPE = "UseSkyboxDataFromLevel";
    private final boolean useSkyboxDataFromLevel;

    public UseSkyboxDataFromLevel(int i) {
        super(TYPE);
        this.useSkyboxDataFromLevel = i == 1;
    }

    public boolean isUseSkyboxDataFromLevel() {
        return useSkyboxDataFromLevel;
    }
}
