/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */
package headwayent.hotshotengine.android.util;

/**
 * Thrown when code requests a {@link Property} on a class that does
 * not expose the appropriate method or field.
 *
 * @see Property#of(java.lang.Class, java.lang.Class, java.lang.String)
 */
public class NoSuchPropertyException extends RuntimeException {

    public NoSuchPropertyException(String s) {
        super(s);
    }

}
