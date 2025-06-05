/*
 * Created by Sebastian Bugiu on 4/9/23, 10:12 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 5/17/21, 11:15 PM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.microedition.rms;


public class RecordStoreNotFoundException extends Exception {

    public RecordStoreNotFoundException() {

    }

    public RecordStoreNotFoundException(String detailMessage) {
        super(detailMessage);

    }

    public RecordStoreNotFoundException(Throwable throwable) {
        super(throwable);

    }

    public RecordStoreNotFoundException(String detailMessage,
                                        Throwable throwable) {
        super(detailMessage, throwable);

    }

}
