/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_NodeException extends RuntimeException {

    /**
     *
     */
    public ENG_NodeException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_NodeException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_NodeException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_NodeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
