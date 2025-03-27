package controllers;

import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Waits for a specific condition to be met for a provided object.
 * Condition is checked every `idleDuration` milliseconds, with a default of 1 second.
 *
 * @param <T> the type of the object being checked.
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
     * Creates a new Waiter builder.
     *
     * @param <T> the type of the object being waited for.
     * @return a new Builder instance.
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
     * Waits for the specified condition to be met.
     *
     * @param expectedCondition the condition to be satisfied.
     * @return the object returned by the supplier once the condition is satisfied.
     * @throws TimeExceededRuntimeException if the condition is not satisfied within the wait time.
     */
    public T waitCondition(final Predicate<T> expectedCondition) {
        return waitCondition(expectedCondition, null);
    }

    /**
     * Waits for the specified condition to be met.
     *
     * @param expectedCondition the condition to be satisfied.
     * @param predicateAsString the string representation of the predicate, optional.
     * @return the object returned by the supplier once the condition is satisfied.
     * @throws TimeExceededRuntimeException if the condition is not satisfied within the wait time.
     */
    public T waitCondition(final Predicate<T> expectedCondition, final String predicateAsString) {
        if (supplier == null || resultToString == null || expectedCondition == null) {
            throw new IllegalStateException("Supplier, resultToString, and expectedCondition must not be null");
        }

        if (idleDuration < 1) {
            idleDuration = 1000;
        }

        final long startTime = DateTime.now().getMillis();
        final long endTime = startTime + getWaitingTime() * 1000L;
        T response = getSupplier().get();

        while (DateTime.now().getMillis() < endTime) {
            if (expectedCondition.test(response)) {
                return response;
            }

            // Print intermediate response
            if (isDebug()) {
                final var logger = LoggerFactory.getLogger(Waiter.class);
                logger.info(getResultToString().apply(response));
            }

            // Idle
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(getIdleDuration()));

            response = supplier.get();
        }

        final var predicatePart = predicateAsString == null ? "" : "Predicate: " + predicateAsString + "\n";
        final var defaultMsg = predicatePart
                + "Expected condition not met. Max waiting time exceeded\n\n"
                + "Result: " + resultToString.apply(response);

        throw new TimeExceededRuntimeException(timeExceededMessage != null ? timeExceededMessage : defaultMsg);
    }

    /**
     * Builder class for {@link Waiter}.
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
