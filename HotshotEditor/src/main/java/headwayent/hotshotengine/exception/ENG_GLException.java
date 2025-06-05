package headwayent.hotshotengine.exception;

public class ENG_GLException extends RuntimeException {

    public ENG_GLException() {

    }

    public ENG_GLException(String detailMessage) {
        super(detailMessage);

    }

    public ENG_GLException(Throwable throwable) {
        super(throwable);

    }

    public ENG_GLException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
