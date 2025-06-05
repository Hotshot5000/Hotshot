/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_PathException extends RuntimeException {

    /**
     *
     */
    public ENG_PathException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_PathException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_PathException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_PathException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
