/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_ParsingException extends RuntimeException {

    /**
     *
     */
    public ENG_ParsingException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_ParsingException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_ParsingException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_ParsingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
