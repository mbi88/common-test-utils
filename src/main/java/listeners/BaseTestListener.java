package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener for test running.
 */
@SuppressWarnings("PMD")
public class BaseTestListener implements ITestListener {

    /**
     * Simple dot char.
     */
    private static final String DOT = ".";
    /**
     * Needed to avoid printing class name twice.
     */
    private String prevClassName = "";

    /**
     * Prints test result.
     *
     * @param iTestResult result.
     * @param status      test status.
     * @param message     message to print.
     */
    private void printTestResult(final ITestResult iTestResult, final String status, final String message) {
        final String testName = "    " + iTestResult.getMethod().getMethodName();
        final String dots = getDots(testName);
        final String time = getTime(iTestResult);

        System.out.println(testName + dots + status + time + message);
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
     * Counts dots according to test case name length.
     *
     * @param testName tc name
     * @return dots.
     */
    private String getDots(final String testName) {
        String dots = DOT;
        for (int i = testName.length(); i < 70; i++) {
            dots = dots.concat(DOT);
        }

        return dots;
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
                .append(DOT)
                .append(iTestResult.getMethod().getMethodName())
                .append(" ]"));
    }

    /**
     * Prints class name.
     *
     * @param iTestResult result.
     */
    private void printClassName(final ITestResult iTestResult) {
        // Store only class name without packages
        final String[] s = iTestResult.getTestClass().getName().split("\\.");
        final String className = s[s.length - 1];

        // Print class name only 1 time
        if (!className.equals(prevClassName)) {
            System.out.println("  " + className);
        }
        prevClassName = className;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
        printClassName(iTestResult);
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        printTestResult(iTestResult, " [ OK ] ", "");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        printTestResult(iTestResult, " [ ERROR ] ", getMessage(iTestResult));
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        printTestResult(iTestResult, " [ SKIPPED ] ", "");
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println(iTestContext.getSuite().getName());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }
}
