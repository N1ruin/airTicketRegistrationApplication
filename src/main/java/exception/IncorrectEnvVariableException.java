package exception;

public class IncorrectEnvVariableException extends RuntimeException {
    public IncorrectEnvVariableException() {
        super("Incorrect env variable");
    }
}
