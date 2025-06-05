/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/14/16, 1:16 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.blackholedarksun.net.clientapi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sebastian on 18.04.2015.
 */
public class RestError {

    @SerializedName("code")
    private Integer code;

    @SerializedName("httpCode")
    private Integer httpCode;

    @SerializedName("error_message")
    private final String strMessage;

    public RestError(String strMessage, int httpCode, int code) {
        this.strMessage = strMessage;
        this.code = code;
        this.httpCode = httpCode;
    }

    public RestError(String strMessage, int code) {
        this.strMessage = strMessage;
        this.code = code;
    }

    public RestError(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getMessage() {
        return strMessage;
    }

    public Integer getCode() {
        return code;
    }

    public Integer getHttpCode() {
        return httpCode;
    }
}
