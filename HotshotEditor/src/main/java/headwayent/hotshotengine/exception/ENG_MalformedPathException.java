/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_MalformedPathException extends ENG_PathException {

    /**
     *
     */
    public ENG_MalformedPathException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_MalformedPathException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_MalformedPathException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_MalformedPathException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
