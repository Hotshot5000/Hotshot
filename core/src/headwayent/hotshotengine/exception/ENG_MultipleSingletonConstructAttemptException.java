/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_MultipleSingletonConstructAttemptException extends
        RuntimeException {

    /**
     *
     */
    public ENG_MultipleSingletonConstructAttemptException() {
        
    }

    /**
     * @param detailMessage
     */
    public ENG_MultipleSingletonConstructAttemptException(String detailMessage) {
        super(detailMessage);
        
    }

    /**
     * @param throwable
     */
    public ENG_MultipleSingletonConstructAttemptException(Throwable throwable) {
        super(throwable);
        
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_MultipleSingletonConstructAttemptException(String detailMessage,
                                                          Throwable throwable) {
        super(detailMessage, throwable);
        
    }

}
