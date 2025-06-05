/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 12/2/15, 6:14 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.basictypes;

public enum ENG_PrimitiveType {

    BOOLEAN, BYTE, DOUBLE, FLOAT, INTEGER, LONG, SHORT;

    public static int getSizeInBytes(ENG_PrimitiveType type) {
        switch (type) {
            case BOOLEAN:
                return ENG_Boolean.SIZE_IN_BYTES;
            case BYTE:
                return ENG_Byte.SIZE_IN_BYTES;
            case DOUBLE:
                return ENG_Double.SIZE_IN_BYTES;
            case FLOAT:
                return ENG_Float.SIZE_IN_BYTES;
            case INTEGER:
                return ENG_Integer.SIZE_IN_BYTES;
            case LONG:
                return ENG_Long.SIZE_IN_BYTES;
            case SHORT:
                return ENG_Short.SIZE_IN_BYTES;
            default:
                throw new IllegalArgumentException();
        }
    }
}
