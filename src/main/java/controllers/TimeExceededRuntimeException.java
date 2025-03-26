package controllers;

/**
 * Exception thrown when the expected condition is not met within the allowed time frame.
 */
public class TimeExceededRuntimeException extends RuntimeException {

    /**
     * Constructs a new exception with the specified message.
     *
     * @param message the detail message
     */
    public TimeExceededRuntimeException(final String message) {
        super(message);
    }
}
