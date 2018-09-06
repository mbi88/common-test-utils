package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener for test running.
 */
public class BaseTestListener implements ITestListener {

    /**
     * Prints test results.
     *
     * @param iTestResult result.
     * @param status      test status.
     * @param message     message to print.
     */
    private void logTestResult(final ITestResult iTestResult, final String status, final String message) {
        final Logger log = LoggerFactory.getLogger(BaseTestListener.class);
        log.error(status + getTime(iTestResult) + message);
    }

    /**
     * Get elapsed test case time.
     *
     * @param iTestResult result.
     * @return elapsed time.
     */
    private String getTime(final ITestResult iTestResult) {
        return "[ " + (iTestResult.getEndMillis() - iTestResult.getStartMillis()) + "ms ]";
    }

    /**
     * Returns terminal command to rerun test.
     *
     * @param iTestResult result.
     * @return command.
     */
    private String getMessage(final ITestResult iTestResult) {
        return String.valueOf(new StringBuilder()
                .append(" [ ")
                .append("gradle clean test --tests ")
                .append(iTestResult.getTestClass().getName())
                .append(".")
                .append(iTestResult.getMethod().getMethodName())
                .append(" ]"));
    }

    @Override
    public void onTestStart(final ITestResult iTestResult) {
        // Not used
    }

    @Override
    public void onTestSuccess(final ITestResult iTestResult) {
        // Not used
    }

    @Override
    public void onTestFailure(final ITestResult iTestResult) {
        logTestResult(iTestResult, " [ FAILED ] ", getMessage(iTestResult));
    }

    @Override
    public void onTestSkipped(final ITestResult iTestResult) {
        // Not used
    }

    @Override
    public void onStart(final ITestContext iTestContext) {
        // Not used
    }

    @Override
    public void onFinish(final ITestContext iTestContext) {
        // Not used
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(final ITestResult iTestResult) {
        // Not used
    }
}
