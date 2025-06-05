/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 2/28/19, 1:42 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

public class CameraAttach extends ObjectEventParam {

    public static final String TYPE = "CameraAttach";
    private final String objectName;

    public CameraAttach(String name) {
        super(TYPE);
        this.objectName = name;
    }

    public String getObjectName() {
        return objectName;
    }
}
