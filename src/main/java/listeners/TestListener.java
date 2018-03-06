package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private String prevClassName = "";

    private void printTestResult(ITestResult iTestResult, String status, String message) {
        String testName = "    " + iTestResult.getMethod().getMethodName();
        String dots = getDots(testName);
        String time = getTime(iTestResult);

        System.out.println(testName + dots + status + time + message);
    }

    private String getTime(ITestResult iTestResult) {
        return "[ " + (iTestResult.getEndMillis() - iTestResult.getStartMillis()) + "ms ]";
    }

    private String getDots(String testName) {
        String dots = ".";
        for (int i = testName.length(); i < 70; i++) {
            dots = dots.concat(".");
        }

        return dots;
    }

    private String getMessage(ITestResult iTestResult) {
        return String.valueOf(new StringBuilder()
                .append(" [ ")
                .append("gradle clean test --tests ")
                .append(iTestResult.getTestClass().getName())
                .append(".")
                .append(iTestResult.getMethod().getMethodName())
                .append(" ]"));
    }

    private void printClassName(ITestResult iTestResult) {
        // Store only class name without packages
        String[] s = iTestResult.getTestClass().getName().split("\\.");
        String className = s[s.length - 1];

        // Print class name only 1 time
        if (!className.equals(prevClassName))
            System.out.println("  " + className);
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
