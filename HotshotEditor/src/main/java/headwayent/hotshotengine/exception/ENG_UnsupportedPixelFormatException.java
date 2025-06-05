package headwayent.hotshotengine.exception;

public class ENG_UnsupportedPixelFormatException extends RuntimeException {

    public ENG_UnsupportedPixelFormatException() {

    }

    public ENG_UnsupportedPixelFormatException(String detailMessage) {
        super(detailMessage);

    }

    public ENG_UnsupportedPixelFormatException(Throwable throwable) {
        super(throwable);

    }

    public ENG_UnsupportedPixelFormatException(String detailMessage,
                                               Throwable throwable) {
        super(detailMessage, throwable);

    }

}
