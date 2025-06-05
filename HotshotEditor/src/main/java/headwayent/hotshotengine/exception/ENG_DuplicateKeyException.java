/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_DuplicateKeyException extends RuntimeException {

    /**
     *
     */
    public ENG_DuplicateKeyException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_DuplicateKeyException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_DuplicateKeyException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_DuplicateKeyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
