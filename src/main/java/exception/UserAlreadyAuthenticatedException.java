package exception;

public class UserAlreadyAuthenticatedException extends ApplicationException {
    public UserAlreadyAuthenticatedException() {
        super("User already authenticated!");
    }
}
