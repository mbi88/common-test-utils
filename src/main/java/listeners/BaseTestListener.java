package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener for test running.
 */
public class BaseTestListener implements ITestListener {

    /**
     * Prints test result.
     *
     * @param iTestResult result.
     * @param status      test status.
     * @param message     message to print.
     */
    private void printTestResult(final ITestResult iTestResult, final String status, final String message) {
        System.out.println(status + getTime(iTestResult) + message);
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
    public void onTestStart(ITestResult iTestResult) {
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        printTestResult(iTestResult, " [ FAILED ] ", getMessage(iTestResult));
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
    }

    @Override
    public void onStart(ITestContext iTestContext) {
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
    }
}
