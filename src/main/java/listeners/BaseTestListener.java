package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener for enhanced test failure logging.
 * Logs failed tests with duration and re-run command.
 */
public class BaseTestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTestListener.class);

    @Override
    public void onTestFailure(final ITestResult result) {
        LOG.error(formatMessage(result));
    }

    /**
     * Returns terminal command to rerun test.
     *
     * @param result ITestResult test result.
     * @return command.
     */
    private String formatMessage(final ITestResult result) {
        return String.format(
                " [ FAILED ] [ %dms ] [ gradle clean test --tests %s.%s ]",
                result.getEndMillis() - result.getStartMillis(),
                result.getTestClass().getName(),
                result.getMethod().getMethodName()
        );
    }
}
