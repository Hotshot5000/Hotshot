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
public class ENG_BufferLockException extends RuntimeException {

    /**
     *
     */
    public ENG_BufferLockException() {
        
    }

    /**
     * @param detailMessage
     */
    public ENG_BufferLockException(String detailMessage) {
        super(detailMessage);
        
    }

    /**
     * @param throwable
     */
    public ENG_BufferLockException(Throwable throwable) {
        super(throwable);
        
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_BufferLockException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        
    }

}
