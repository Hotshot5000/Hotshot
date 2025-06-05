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
