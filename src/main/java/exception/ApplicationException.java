package exception;

public class ApplicationException extends RuntimeException {
    private final int errorCode;

    public ApplicationException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
