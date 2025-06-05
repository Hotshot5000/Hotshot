/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_InvalidFormatParsingException extends ENG_ParsingException {

    /**
     *
     */
    public ENG_InvalidFormatParsingException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_InvalidFormatParsingException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_InvalidFormatParsingException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_InvalidFormatParsingException(String detailMessage,
                                             Throwable throwable) {
        super(detailMessage, throwable);

    }

}
