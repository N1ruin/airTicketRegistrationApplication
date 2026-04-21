package exception;

public class DontHavePermissionException extends ApplicationException{
    public DontHavePermissionException(String message) {
        super(message);
    }
}
