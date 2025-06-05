package headwayent.hotshotengine.exception;

public class ENG_MultipleDeclarationException extends ENG_ParsingException {

    /**
     *
     */
    public ENG_MultipleDeclarationException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_MultipleDeclarationException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_MultipleDeclarationException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_MultipleDeclarationException(String detailMessage,
                                            Throwable throwable) {
        super(detailMessage, throwable);

    }
}
