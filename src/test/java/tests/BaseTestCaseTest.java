package tests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.internal.ConstructorOrMethod;
import testcase.BaseTestCase;
import testcase.NonRetryable;
import testcase.RetryAnalyzer;
import testcase.Retryable;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class BaseTestCaseTest extends BaseTestCase {

    private static int beforeTestAttemptCounter = 0;
    private static int attempts = 0;
    private static int attempt2 = 0;

    private static String baseUrl;
    private HttpServer server;

    @BeforeClass
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // automatically assign a free port
        server.createContext("/json-object", new JsonHandler());
        server.createContext("/json-array", new JsonHandler());

        server.setExecutor(null);
        server.start();

        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
    }

    @AfterClass
    public void stopServer() {
        server.stop(0);
    }

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
        var r = http.get(baseUrl + "/json-object");
        toJson(r);
    }

    @Test
    public void testCanConvertResponseToJsonArray() {
        var r = http.get(baseUrl + "/json-array");
        toJsonArray(r);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void testDefaultRetry() {
        attempts++;
        if (attempts < 3) {
            throw new AssertionError("Failing on purpose");
        }
    }

    @Test(retryAnalyzer = RetryAnalyzer.class)
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

        var analyzer = new RetryAnalyzer();

        // Prepare attempt counter so that retry() increments it to the max (3) -> no retry.
        var field = RetryAnalyzer.class.getDeclaredField("attemptCounters");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        var counters = (ConcurrentMap<Method, AtomicInteger>) field.get(analyzer);

        counters.put(method, new AtomicInteger(2)); // next increment -> 3

        var result = mockFailedTestResult(method, new Throwable("Some error"));

        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "Retry should not happen when max attempts reached");
    }

    @Test
    public void testRetryAnalyzerWithNullThrowable() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var analyzer = new RetryAnalyzer();
        var result = mockFailedTestResult(method, null);

        boolean shouldRetry = analyzer.retry(result);
        assertTrue(shouldRetry, "Retry should proceed if throwable is null");
    }

    @Test
    public void testRetryWithThrowableMessageNull() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var analyzer = new RetryAnalyzer();
        var result = mockFailedTestResult(method, new Throwable((String) null));

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

        var analyzer = new RetryAnalyzer();
        var result = mockFailedTestResult(
                method,
                new Throwable("504 Gateway Timeout ERROR")
        );

        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "Retry should be skipped for 504 Gateway Timeout ERROR");
    }

    @Test
    public void testNonRetryableAnnotationSkipsRetry() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("nonRetryableTestMethod");

        var analyzer = new RetryAnalyzer();
        var result = mockFailedTestResult(
                method,
                new Throwable("Failing on purpose")
        );

        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "Retry should be skipped for @NonRetryable test");
    }

    @Test
    public void testRetryableAttemptsOneMeansNoRetry() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class
                .getDeclaredMethod("retryableAttemptsOneTestMethod");

        var analyzer = new RetryAnalyzer();
        var result = mockFailedTestResult(method, new Throwable("Some error"));

        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "Retry should not happen when @Retryable(attempts=1)");
    }

    @Test
    public void testNonRetryableWinsOverRetryable() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class
                .getDeclaredMethod("nonRetryableAndRetryableTogetherTestMethod");

        var analyzer = new RetryAnalyzer();
        var result = mockFailedTestResult(
                method,
                new Throwable("Failing on purpose")
        );

        boolean shouldRetry = analyzer.retry(result);
        assertFalse(shouldRetry, "@NonRetryable should disable retry even if @Retryable is present");

    }

    // Support methods
    private void dummyTestMethod() {
    }

    @NonRetryable
    private void nonRetryableTestMethod() {
    }

    @Retryable(attempts = 1)
    private void retryableAttemptsOneTestMethod() {
        // marker method for RetryAnalyzer tests
    }

    @NonRetryable
    @Retryable(attempts = 5)
    private void nonRetryableAndRetryableTogetherTestMethod() {
        // marker method for RetryAnalyzer tests
    }

    private void gatewayErrorTestMethod() {
    }

    private ITestResult mockFailedTestResult(Method method, Throwable throwable) {
        var result = mock(ITestResult.class);
        when(result.getThrowable()).thenReturn(throwable);
        when(result.getStatus()).thenReturn(ITestResult.FAILURE);

        var testNgMethod = mock(ITestNGMethod.class);
        when(result.getMethod()).thenReturn(testNgMethod);
        when(testNgMethod.getConstructorOrMethod())
                .thenReturn(new ConstructorOrMethod(method));

        return result;
    }

    static class JsonHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getHttpContext().getPath();
            String response;
            int status;

            switch (path) {
                case "/json-object" -> {
                    response = "{\"a\":1}";
                    status = 200;
                }
                case "/json-array" -> {
                    response = "[{\"a\":1}]";
                    status = 200;
                }
                default -> {
                    response = "{\"error\":\"Unknown path\"}";
                    status = 404;
                }
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
