/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_UndeclaredIdentifierException extends ENG_ParsingException {

    /**
     *
     */
    public ENG_UndeclaredIdentifierException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_UndeclaredIdentifierException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_UndeclaredIdentifierException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_UndeclaredIdentifierException(String detailMessage,
                                             Throwable throwable) {
        super(detailMessage, throwable);

    }

}
