package tests;

import listeners.BaseTestListener;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class ListenerTest extends BaseTestListener {

    @Test
    public void shouldLogFailureWithCorrectMessage() {
        AtomicReference<String> capturedLog = new AtomicReference<>();

        // Replace SLF4J Logger with a capturing one (Mocking framework like Logback-test or custom).
        BaseTestListener listener = new BaseTestListener() {
            @Override
            public void onTestFailure(ITestResult result) {
                capturedLog.set(super.formatMessage(result));
            }
        };

        // Dummy test class
        class FailingTest {
            @Test
            public void failTest() {
                Assert.fail("Intentional failure");
            }
        }

        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{FailingTest.class});
        testng.setListenerClasses(Collections.singletonList(BaseTestListener.class));
        testng.addListener(listener);
        testng.run();

        String log = capturedLog.get();
        Assert.assertNotNull(log);

        Assert.assertTrue(log.contains("gradle clean test --tests"));
        Assert.assertTrue(log.contains("failTest"));
        Assert.assertTrue(log.contains("FailingTest"));
    }
}
