/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;


public class RecordStoreFullException extends RecordStoreException {

    public RecordStoreFullException() {

    }

    public RecordStoreFullException(String detailMessage) {
        super(detailMessage);

    }

    public RecordStoreFullException(Throwable throwable) {
        super(throwable);

    }

    public RecordStoreFullException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
