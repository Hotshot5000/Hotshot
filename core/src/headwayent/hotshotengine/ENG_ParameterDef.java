/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine;

public class ENG_ParameterDef {

    /// List of parameter types available
    public enum ParameterType {
        PT_BOOL,
        PT_REAL,
        PT_INT,
        PT_UNSIGNED_INT,
        PT_SHORT,
        PT_UNSIGNED_SHORT,
        PT_LONG,
        PT_UNSIGNED_LONG,
        PT_STRING,
        PT_VECTOR3,
        PT_MATRIX3,
        PT_MATRIX4,
        PT_QUATERNION,
        PT_COLOURVALUE
    }

    public final String name;
    public final String description;
    public final ParameterType paramType;

    public ENG_ParameterDef(String name, String description, ParameterType paramType) {
        this.name = name;
        this.description = description;
        this.paramType = paramType;
    }
}
