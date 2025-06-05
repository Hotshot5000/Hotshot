/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_InvalidFieldStateException extends RuntimeException {

    /**
     *
     */
    public ENG_InvalidFieldStateException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_InvalidFieldStateException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_InvalidFieldStateException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_InvalidFieldStateException(String detailMessage,
                                          Throwable throwable) {
        super(detailMessage, throwable);

    }

}
