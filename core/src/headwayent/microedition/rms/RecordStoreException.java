/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;


public class RecordStoreException extends Exception {

    public RecordStoreException() {
        
    }

    public RecordStoreException(String detailMessage) {
        super(detailMessage);
        
    }

    public RecordStoreException(Throwable throwable) {
        super(throwable);
        
    }

    public RecordStoreException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        
    }

}
