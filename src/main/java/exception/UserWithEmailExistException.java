package exception;

public class UserWithEmailExistException extends ApplicationException {
    public UserWithEmailExistException(String email) {
        super("User with email %s exist!".formatted(email));
    }
}
