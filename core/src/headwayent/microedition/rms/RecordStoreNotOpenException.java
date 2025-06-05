/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;


public class RecordStoreNotOpenException extends RecordStoreException {

    public RecordStoreNotOpenException() {
        
    }

    public RecordStoreNotOpenException(String detailMessage) {
        super(detailMessage);
        
    }

    public RecordStoreNotOpenException(Throwable throwable) {
        super(throwable);
        
    }

    public RecordStoreNotOpenException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        
    }

}
