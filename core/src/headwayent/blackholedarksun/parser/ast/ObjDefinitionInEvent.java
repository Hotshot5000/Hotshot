/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/1/19, 3:00 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.parser.ast;

import java.util.HashMap;

@Deprecated
/**
 * So new.... yet so dead.... Should be removed at some point.
 */
public class ObjDefinitionInEvent extends ObjectEventParam {

    public static final String TYPE = "ObjDefinitionInEvent";
    private final String objName;
    private final HashMap<String, ObjectDefinitionParam> map;

    public ObjDefinitionInEvent(String name, HashMap<String, ObjectDefinitionParam> map) {
        super(TYPE);
        this.objName = name;
        this.map = map;
    }

    public String getObjName() {
        return objName;
    }

    public HashMap<String, ObjectDefinitionParam> getMap() {
        return map;
    }
}
