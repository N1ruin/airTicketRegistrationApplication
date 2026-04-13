package exception;

public class UserAlreadyExistException extends ApplicationException {
    public UserAlreadyExistException(String email) {
        super("User with email %s exist!".formatted(email));
    }
}
