package exception;

public class EntityAlreadyExistException extends ApplicationException{
    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
