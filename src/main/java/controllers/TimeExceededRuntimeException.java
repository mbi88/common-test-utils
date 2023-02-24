package controllers;

/**
 * Thrown if waiting time of operation is exceeded.
 */
public class TimeExceededRuntimeException extends RuntimeException {

    /**
     * @param message time exceeded message.
     */
    public TimeExceededRuntimeException(final String message) {
        super(message);
    }
}

