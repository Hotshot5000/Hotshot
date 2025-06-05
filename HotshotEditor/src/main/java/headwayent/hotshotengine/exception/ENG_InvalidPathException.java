/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_InvalidPathException extends ENG_PathException {

    /**
     *
     */
    public ENG_InvalidPathException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_InvalidPathException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_InvalidPathException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_InvalidPathException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
