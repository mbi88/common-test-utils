package testcase;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestNG retry analyzer that controls how many times a failed test
 * is allowed to be re-executed.
 * <p>
 * Key characteristics:
 * - Retries are counted per test METHOD (not per instance).
 * - The retry limit represents TOTAL executions, including the first one.
 * - Thread-safe and safe to use with parallel test execution.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    /**
     * Default total number of test executions, INCLUDING the first run.
     * <p>
     * Example:
     * DEFAULT_TOTAL_ATTEMPTS = 3
     * -> 1 initial execution + up to 2 retries.
     */
    private static final int DEFAULT_TOTAL_ATTEMPTS = 3;

    /**
     * Tracks how many times retry() has been evaluated per test method.
     * <p>
     * Important:
     * - The counter is incremented AFTER each test FAILURE.
     * - Keyed by Method, which is safe as long as DataProvider / Factory tests are NOT used (which is the case here).
     */
    private final ConcurrentMap<Method, AtomicInteger> attemptCounters = new ConcurrentHashMap<>();

    /**
     * Determines whether a failed test should be retried.
     * <p>
     * This method is called by TestNG AFTER a test execution fails.
     *
     * @param result TestNG test result
     * @return true if the test should be retried; false otherwise
     */
    @Override
    public boolean retry(final ITestResult result) {
        boolean shouldRetry = true;

        // Do not retry tests explicitly marked as @NonRetryable.
        // This annotation has absolute priority over all other retry logic.
        final var annotations = result.getMethod().getConstructorOrMethod().getMethod().getAnnotations();

        for (final var annotation : annotations) {
            if (annotation instanceof NonRetryable) {
                shouldRetry = false;
                break;
            }
        }

        // Do not retry if test failed due to 504 Gateway Timeout ERROR
        // Do not retry known unrecoverable failures.
        // 504 Gateway Timeout errors indicate server-side timeouts
        // and retrying them usually makes the test suite slower without increasing stability.
        if (shouldRetry && result.getStatus() == ITestResult.FAILURE) {
            final var throwable = result.getThrowable();
            if (throwable != null && throwable.getMessage() != null
                    && throwable.getMessage().contains("504 Gateway Timeout ERROR")) {
                shouldRetry = false;
            }
        }

        // Each retry decision is tracked per test method.
        // retry() is invoked AFTER a failure,
        // so the counter represents how many failed executions have already occurred.
        final var method = result.getMethod().getConstructorOrMethod().getMethod();

        // Increment the counter atomically for thread safety
        final int attempt = attemptCounters
                .computeIfAbsent(method, m -> new AtomicInteger())
                .incrementAndGet();

        // retry() is invoked AFTER a test failure.
        // 'attempt' counts how many times retry() has already been evaluated for this test method
        // (i.e. how many failed runs have happened so far).
        // Example with maxAttempts = 3 (total executions including the first one):
        // - after 1st failure: attempt = 1 -> 1 < 3 -> retry (2nd execution)
        // - after 2nd failure: attempt = 2 -> 2 < 3 -> retry (3rd execution)
        // - after 3rd failure: attempt = 3 -> 3 < 3 -> false -> stop retrying
        return shouldRetry && attempt < getMaxAttempts(result);
    }

    /**
     * Resolves the total allowed number of executions for a test method.
     * <p>
     * - If the method is annotated with @Retryable, its 'attempts' value is used.
     * - Otherwise, the global default is applied.
     *
     * @param result TestNG test result
     * @return total allowed executions (including the first one)
     */
    private int getMaxAttempts(final ITestResult result) {
        final var method = result.getMethod().getConstructorOrMethod().getMethod();
        final var retryable = method.getAnnotation(Retryable.class);

        return retryable != null
                ? retryable.attempts()
                : DEFAULT_TOTAL_ATTEMPTS;
    }
}
