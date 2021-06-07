package controllers;

/**
 * Thrown if waiting time of operation is exceeded.
 */
public class TimeExceededRuntimeException extends RuntimeException {

    public TimeExceededRuntimeException(final String message) {
        super(message);
    }
}

