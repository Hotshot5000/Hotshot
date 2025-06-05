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
