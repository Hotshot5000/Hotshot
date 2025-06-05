/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_MultipleSingletonConstructAttemptException extends
        RuntimeException {

    /**
     *
     */
    public ENG_MultipleSingletonConstructAttemptException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_MultipleSingletonConstructAttemptException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_MultipleSingletonConstructAttemptException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_MultipleSingletonConstructAttemptException(String detailMessage,
                                                          Throwable throwable) {
        super(detailMessage, throwable);

    }

}
