package tests;

import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.internal.ConstructorOrMethod;
import testcase.BaseTestCase;
import testcase.NonRetryable;
import testcase.RetryAnalyzer;
import testcase.Retryable;

import java.lang.reflect.Method;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class BaseTestCaseTest extends BaseTestCase {

    private static int beforeTestAttemptCounter = 0;
    private static int attempts = 0;
    private static int attempt2 = 0;

    @BeforeMethod
    @Retryable
    public void setUpBeforeMethod() {
    }

    @BeforeTest
    @Retryable
    public void setUpBeforeTest() {
        beforeTestAttemptCounter++;
        if (beforeTestAttemptCounter < 3) {
            throw new AssertionError("Failing on purpose");
        }
    }

    @BeforeSuite
    public void testSingleRetryableCall() {
    }

    @Test
    public void testCanGetRandomNum() {
        var res = getRandomNum(5);
        assertEquals(String.valueOf(res).length(), 5);
    }

    @Test
    public void testCanGetRandomNumDefault() {
        var res = getRandomNum();
        assertEquals(String.valueOf(res).length(), 13);
    }

    @Test
    public void testCanGetUuid() {
        var res = getRandomUID();
        assertEquals(String.valueOf(res).length(), 36);
    }

    @Test
    public void testCanGetResource() {
        var res = getResource("/jsons/test_json.json");

        assertEquals(res.toString(), "{\"a\":1}");
    }

    @Test
    public void testCanGetResources() {
        var res = getResources("/jsons/test_json_array.json");

        assertEquals(res.toString(), "[{\"a\":1}]");
    }

    @Test
    public void testCanFindJsonInArray() {
        var res = findJsonInArray(getResources("/jsons/test_json_array.json"), "a", 1);
        assertEquals(res.toString(), "{\"a\":1}");
    }

    @Test
    public void testCanConvertResponseToJson() {
        var r = http.get("https://api.npoint.io/a4600f1cd1c37a334ccf");
        toJson(r);
    }

    @Test
    public void testCanConvertResponseToJsonArray() {
        var r = http.get("https://api.npoint.io/efb3f7b515781b5e1a7d");
        toJsonArray(r);
    }

    @Test(retryAnalyzer = testcase.RetryAnalyzer.class)
    public void testDefaultRetry() {
        attempts++;
        if (attempts < 3) {
            throw new AssertionError("Failing on purpose");
        }
    }

    @Test(retryAnalyzer = testcase.RetryAnalyzer.class)
    @Retryable(attempts = 5)
    public void testCustomRetry() {
        attempt2++;
        if (attempt2 < 5) {
            throw new AssertionError("Failing on purpose");
        }
    }

    @Test
    public void testRetryLimitReached() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        Method method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var result = mock(ITestResult.class);
        when(result.getThrowable()).thenReturn(new Throwable("Some error"));
        when(result.getStatus()).thenReturn(ITestResult.FAILURE);

        var testNgMethod = mock(ITestNGMethod.class);
        when(result.getMethod()).thenReturn(testNgMethod);

        var constructorOrMethod = new ConstructorOrMethod(method);
        when(testNgMethod.getConstructorOrMethod()).thenReturn(constructorOrMethod);

        var analyzer = new RetryAnalyzer();

        // Set attempt counter to 3
        var field = RetryAnalyzer.class.getDeclaredField("attemptCounters");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Method, Integer> counters = (Map<Method, Integer>) field.get(analyzer);
        counters.put(method, 2); // 3rd attempt → no retry

        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "Retry should not happen when max attempts reached");
    }

    @Test
    public void testRetryAnalyzerWithNullThrowable() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var result = mock(ITestResult.class);
        when(result.getThrowable()).thenReturn(null);
        when(result.getStatus()).thenReturn(ITestResult.FAILURE);

        var testNgMethod = mock(ITestNGMethod.class);
        when(result.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getConstructorOrMethod()).thenReturn(new ConstructorOrMethod(method));

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);
        assertTrue(shouldRetry, "Retry should proceed if throwable is null");
    }

    @Test
    public void testRetryWithThrowableMessageNull() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var throwable = new Throwable((String) null);

        var result = mock(ITestResult.class);
        when(result.getThrowable()).thenReturn(throwable);
        when(result.getStatus()).thenReturn(ITestResult.FAILURE);

        var testNgMethod = mock(ITestNGMethod.class);
        when(result.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getConstructorOrMethod()).thenReturn(new ConstructorOrMethod(method));

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);
        assertTrue(shouldRetry, "Retry should continue when throwable message is null");
    }

    @Test
    public void testRetryWithNonFailureStatus() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var result = mock(ITestResult.class);
        when(result.getThrowable()).thenReturn(new Throwable("Any message"));
        when(result.getStatus()).thenReturn(ITestResult.SUCCESS);

        var testNgMethod = mock(ITestNGMethod.class);
        when(result.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getConstructorOrMethod()).thenReturn(new ConstructorOrMethod(method));

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);
        assertTrue(shouldRetry, "Retry should continue if status is not FAILURE");
    }

    @Test
    public void testRetryAnalyzerSkips504GatewayError() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("gatewayErrorTestMethod");

        var result = mock(ITestResult.class);
        when(result.getThrowable()).thenReturn(new Throwable("504 Gateway Timeout ERROR"));
        when(result.getStatus()).thenReturn(ITestResult.FAILURE);

        var testNgMethod = mock(ITestNGMethod.class);
        when(result.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getConstructorOrMethod()).thenReturn(new ConstructorOrMethod(method));

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "Retry should be skipped for 504 Gateway Timeout ERROR");
    }

    @Test
    public void testNonRetryableAnnotationSkipsRetry() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("nonRetryableTestMethod");

        var result = mock(ITestResult.class);
        when(result.getThrowable()).thenReturn(new Throwable("Failing on purpose"));
        when(result.getStatus()).thenReturn(ITestResult.FAILURE);

        var testNgMethod = mock(ITestNGMethod.class);
        when(result.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getConstructorOrMethod()).thenReturn(new ConstructorOrMethod(method));

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "Retry should be skipped for @NonRetryable test");
    }

    // Support methods
    private void dummyTestMethod() {
    }

    @NonRetryable
    private void nonRetryableTestMethod() {
    }

    private void gatewayErrorTestMethod() {
    }

}
