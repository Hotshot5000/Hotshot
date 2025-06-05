/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.exception;

public class ENG_MultipleDeclarationException extends ENG_ParsingException {

    /**
     *
     */
    public ENG_MultipleDeclarationException() {
        
    }

    /**
     * @param detailMessage
     */
    public ENG_MultipleDeclarationException(String detailMessage) {
        super(detailMessage);
        
    }

    /**
     * @param throwable
     */
    public ENG_MultipleDeclarationException(Throwable throwable) {
        super(throwable);
        
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_MultipleDeclarationException(String detailMessage,
                                            Throwable throwable) {
        super(detailMessage, throwable);
        
    }
}
