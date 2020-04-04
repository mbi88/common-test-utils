package controllers;

import org.joda.time.DateTime;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Waiting for special object condition. Condition is checked every 1 sec.
 *
 * @param <T> type of result of function to execute.
 */
public final class Waiter<T> {

    /**
     * Time in seconds to be spent on waiting of condition.
     */
    private final int waitingTime;

    /**
     * Function to execute.
     */
    private final Supplier<T> function;

    /**
     * Function execution result to be thrown if expected condition will not be met.
     */
    private final Function<T, String> result;

    /**
     * @param function    function to execute.
     * @param result      string representation of function result to be used in exception if occurs.
     * @param waitingTime how many seconds waiter will wait until throw exception.
     */
    public Waiter(final Supplier<T> function, final Function<T, String> result, final int waitingTime) {
        this.function = function;
        this.result = result;
        this.waitingTime = waitingTime;
    }

    /**
     * @param expectedCondition condition.
     * @return result response.
     * @throws Error if expected condition not met during waiting time.
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public T waitCondition(final Predicate<T> expectedCondition) {
        final long startTime = DateTime.now().getMillis();
        final long endTime = startTime + waitingTime * 1000L;
        boolean timeExceeded = false;
        T response = function.get();

        while (!timeExceeded && !expectedCondition.test(response)) {
            timeExceeded = DateTime.now().getMillis() >= endTime;
            response = function.get();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                // Ignored
            }
        }

        if (timeExceeded) {
            throw new Error(String.format("Expected condition not met. Max waiting time exceeded%n%nResult: %s%n",
                    result.apply(response)));
        }

        return response;
    }
}
