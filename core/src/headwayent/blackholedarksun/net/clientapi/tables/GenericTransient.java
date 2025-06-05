/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 10/19/15, 5:29 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi.tables;

import headwayent.blackholedarksun.net.clientapi.ErrorCodes;

/**
 * Created by sebas on 18.10.2015.
 */
public class GenericTransient {

    private String error;
    private int errorCode = ErrorCodes.NO_ERROR;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
