package testcase;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds ability to retry failed tests.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int DEFAULT_RETRY_LIMIT = 3;
    // Track retry count per test method
    private final Map<Method, Integer> attemptCounters = new HashMap<>();

    @Override
    public boolean retry(final ITestResult result) {
        boolean shouldRetry = true;

        // Do not retry if test case has @NonRetryable annotation
        final var annotations = result.getMethod().getConstructorOrMethod().getMethod().getAnnotations();
        for (var annotation : annotations) {
            if (annotation instanceof NonRetryable) {
                shouldRetry = false;
                break;
            }
        }

        // Do not retry if test failed due to 504 Gateway Timeout ERROR
        if (shouldRetry && result.getStatus() == ITestResult.FAILURE) {
            final var throwable = result.getThrowable();
            if (throwable != null && throwable.getMessage() != null
                    && throwable.getMessage().contains("504 Gateway Timeout ERROR")) {
                shouldRetry = false;
            }
        }

        final int currentAttempt = attemptCounters
                .getOrDefault(result.getMethod().getConstructorOrMethod().getMethod(), 0);
        attemptCounters.put(result.getMethod().getConstructorOrMethod().getMethod(), currentAttempt + 1);

        return shouldRetry && currentAttempt + 1 < getMaxAttempts(result);
    }

    /**
     * Get max retry attempts for the given test.
     *
     * @param result test result
     * @return number of retry attempts
     */
    private int getMaxAttempts(final ITestResult result) {
        final var method = result.getMethod().getConstructorOrMethod().getMethod();
        final var retryable = method.getAnnotation(Retryable.class);
        return retryable != null ? retryable.attempts() : DEFAULT_RETRY_LIMIT;
    }
}
