/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package headwayent.hotshotengine.exception;

/**
 * @author sebi
 */
public class ENG_DataStorageNotFoundException extends RuntimeException {
    public ENG_DataStorageNotFoundException() {

    }

    public ENG_DataStorageNotFoundException(String detailMessage) {
        super(detailMessage);

    }

    public ENG_DataStorageNotFoundException(Throwable throwable) {
        super(throwable);

    }

    public ENG_DataStorageNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }
}
