package cloud.popush.exception;

public class RejectAuthException extends Exception {
    public RejectAuthException(String message, Throwable cause, Object... bads) {
        super(message, cause);
    }

    public RejectAuthException(String message, Object... bads) {
        super(message);
    }


    public RejectAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public RejectAuthException(String message) {
        super(message);
    }
}
