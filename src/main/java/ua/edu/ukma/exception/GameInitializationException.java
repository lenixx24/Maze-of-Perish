package ua.edu.ukma.exception;

public class GameInitializationException extends RuntimeException {

    public GameInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}