/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.exception;

public class ENG_GLException extends RuntimeException {

    public ENG_GLException() {
        
    }

    public ENG_GLException(String detailMessage) {
        super(detailMessage);
        
    }

    public ENG_GLException(Throwable throwable) {
        super(throwable);
        
    }

    public ENG_GLException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        
    }

}
