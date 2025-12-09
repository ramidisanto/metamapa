package Modelos.Exceptions;

public class ValidacionError extends RuntimeException {
    public ValidacionError(String message) {
        super(message);
    }
}
