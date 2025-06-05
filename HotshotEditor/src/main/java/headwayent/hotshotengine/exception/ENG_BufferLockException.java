/**
 *
 */
package headwayent.hotshotengine.exception;

/**
 * @author Sebi
 */
public class ENG_BufferLockException extends RuntimeException {

    /**
     *
     */
    public ENG_BufferLockException() {

    }

    /**
     * @param detailMessage
     */
    public ENG_BufferLockException(String detailMessage) {
        super(detailMessage);

    }

    /**
     * @param throwable
     */
    public ENG_BufferLockException(Throwable throwable) {
        super(throwable);

    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ENG_BufferLockException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);

    }

}
