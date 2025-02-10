package testcase;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Adds ability to retry failed tests.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int RETRY_LIMIT = 3;
    private int counter;

    @Override
    public boolean retry(final ITestResult result) {
        boolean shouldRetry = true;

        // Do not retry if test case has @NonRetryable annotation
        final var testCaseAnnotations = result.getMethod().getConstructorOrMethod().getMethod().getAnnotations();
        for (var annotation : testCaseAnnotations) {
            if (annotation instanceof NonRetryable) {
                shouldRetry = false;
                break;
            }
        }

        // Do not retry if test failed dut to 504 Gateway Timeout ERROR
        if (result.getStatus() == ITestResult.FAILURE) {
            if (result.getThrowable().getMessage().contains("504 Gateway Timeout ERROR")) {
                shouldRetry = false;
            }
        }

        return shouldRetry && ++counter < RETRY_LIMIT;
    }
}
