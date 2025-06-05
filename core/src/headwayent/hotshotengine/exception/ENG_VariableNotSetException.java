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
public class ENG_VariableNotSetException extends ENG_ParsingException {

    /**
     *
     */
    public ENG_VariableNotSetException() {
        
    }

    /**
     * @param detailMessage
     */
    public ENG_VariableNotSetException(String detailMessage) {
        super(detailMessage);
        
    }

    /**
     * @param throwable
     */
    public ENG_VariableNotSetException(Throwable throwable) {
        super(throwable);
        
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_VariableNotSetException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        
    }

}
