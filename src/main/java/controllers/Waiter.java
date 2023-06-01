package controllers;

import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Waiting for special object condition. Condition is checked every 1 sec.
 *
 * @param <T> supplier result.
 */
public final class Waiter<T> {

    /**
     * Time in seconds to be spent on waiting of condition.
     */
    private int waitingTime;

    /**
     * Function to execute.
     */
    private Supplier<T> supplier;

    /**
     * Function execution result to be thrown if expected condition will not be met.
     */
    private Function<T, String> resultToString;

    /**
     * Idle duration in ms. 1 sec by default.
     */
    private int idleDuration = 1000;

    /**
     * Whether print intermediate response or not. False by default.
     */
    private boolean debug;

    /**
     * Shown when time exceeded.
     */
    private String timeExceededMessage;

    private Waiter() {
        // Disabled
    }

    /**
     * @param <T> supplier result.
     * @return builder
     */
    public static <T> Waiter<T>.Builder newBuilder() {
        return new Waiter<T>().new Builder();
    }

    private int getWaitingTime() {
        return waitingTime;
    }

    private Supplier<T> getSupplier() {
        return supplier;
    }

    private Function<T, String> getResultToString() {
        return resultToString;
    }

    private int getIdleDuration() {
        return idleDuration;
    }

    private boolean isDebug() {
        return debug;
    }

    /**
     * @param expectedCondition condition.
     * @return result response.
     * @throws TimeExceededRuntimeException if expected condition not met during waiting time.
     */
    public T waitCondition(final Predicate<T> expectedCondition) {
        return waitCondition(expectedCondition, null);
    }

    /**
     * @param expectedCondition condition.
     * @param predicateAsString string representation of the predicate.
     * @return result response.
     * @throws TimeExceededRuntimeException if expected condition not met during waiting time.
     */
    public T waitCondition(final Predicate<T> expectedCondition, final String predicateAsString) {
        final long startTime = DateTime.now().getMillis();
        final long endTime = startTime + getWaitingTime() * 1000L;
        boolean timeExceeded = false;
        T response = getSupplier().get();

        while (!timeExceeded && !expectedCondition.test(response)) {
            timeExceeded = DateTime.now().getMillis() >= endTime;
            response = getSupplier().get();

            // Print intermediate response
            if (isDebug()) {
                final var logger = LoggerFactory.getLogger(Waiter.class);
                logger.info(getResultToString().apply(response));
            }

            // Idle
            // TODO: avoid using sleep
            try {
                Thread.sleep(getIdleDuration());
            } catch (InterruptedException ignored) {
                // Ignored
            }
        }

        if (timeExceeded) {
            final var predicateMessagePart = predicateAsString == null ? "" : "Predicate: " + predicateAsString + "\n";
            final var msgTemplate = "%sExpected condition not met. Max waiting time exceeded%n%nResult: %s%n";
            final var defaultExceededMessage = String.format(msgTemplate, predicateMessagePart,
                    getResultToString().apply(response));
            final var message = timeExceededMessage == null ? defaultExceededMessage : timeExceededMessage;
            throw new TimeExceededRuntimeException(message);
        }

        return response;
    }

    /**
     * Waiter builder class.
     */
    @SuppressWarnings("PMD.LinguisticNaming")
    public final class Builder {

        private Builder() {
            // Disabled
        }

        public Builder setWaitingTime(final int waitingTime) {
            Waiter.this.waitingTime = waitingTime;
            return this;
        }

        public Builder setSupplier(final Supplier<T> supplier) {
            Waiter.this.supplier = supplier;
            return this;
        }

        public Builder setResultToString(final Function<T, String> resultToString) {
            Waiter.this.resultToString = resultToString;
            return this;
        }

        public Builder setIdleDuration(final int idleDuration) {
            Waiter.this.idleDuration = idleDuration;
            return this;
        }

        public Builder setDebug(final boolean debug) {
            Waiter.this.debug = debug;
            return this;
        }

        public Builder setTimeExceededMessage(final String message) {
            Waiter.this.timeExceededMessage = message;
            return this;
        }

        public Waiter<T> build() {
            return Waiter.this;
        }
    }
}
