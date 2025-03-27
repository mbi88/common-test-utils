package tests;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Headers;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.config.XmlPathConfig;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import org.testng.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.internal.ConstructorOrMethod;
import org.testng.xml.XmlTest;
import testcase.BaseTestCase;
import testcase.NonRetryable;
import testcase.RetryAnalyzer;
import testcase.Retryable;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var result = new FakeTestResult(method);
        result.setThrowable(new Throwable("Some error"));
        result.setStatus(ITestResult.FAILURE);

        var analyzer = new RetryAnalyzer();

        // Set attemptCounters via reflection
        var field = RetryAnalyzer.class.getDeclaredField("attemptCounters");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Method, Integer> counters = (Map<Method, Integer>) field.get(analyzer);
        counters.put(method, 2);

        // getMaxAttempts will return 3 → 2+1 == 3 → NOT less → retry = false
        boolean shouldRetry = analyzer.retry(result);

        assertFalse(shouldRetry, "Retry should not happen when max attempts reached");
    }

    @Test
    public void testRetryAnalyzerWithNullThrowable() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");
        var result = new FakeTestResult(method);
        result.setThrowable(null); // simulate null throwable
        result.setStatus(ITestResult.FAILURE); // simulate test failure

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);

        assertTrue(shouldRetry, "Retry should proceed if throwable is null");
    }

    private void dummyTestMethod() {
        // Used for method reference
    }

    @Test
    public void testRetryWithThrowableMessageNull() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");
        var throwable = new Throwable((Throwable) null); // message is null

        var result = new FakeTestResult(method);
        result.setThrowable(throwable);
        result.setStatus(ITestResult.FAILURE);

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);

        assertTrue(shouldRetry, "Retry should continue when throwable message is null");
    }

    @Test
    public void testRetryWithNonFailureStatus() throws NoSuchMethodException {
        var method = BaseTestCaseTest.class.getDeclaredMethod("dummyTestMethod");

        var result = new FakeTestResult(method);
        result.setThrowable(new Throwable("Any message"));
        result.setStatus(ITestResult.SUCCESS); // Not FAILURE

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);

        assertTrue(shouldRetry, "Retry should continue if status is not FAILURE");
    }

    @Test
    public void testRetryAnalyzerSkips504GatewayError() throws NoSuchMethodException {
        var testMethod = BaseTestCaseTest.class.getDeclaredMethod("gatewayErrorTestMethod");
        var result = new FakeTestResult(testMethod);
        result.setThrowable(new AssertionError("504 Gateway Timeout ERROR"));

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(result);

        assertFalse(shouldRetry, "Retry should be skipped for 504 Gateway Timeout ERROR");
    }

    private void gatewayErrorTestMethod() {
        // Used for annotation resolution only
    }

    @Test
    public void testNonRetryableAnnotationSkipsRetry() throws NoSuchMethodException {
        var testMethod = BaseTestCaseTest.class.getDeclaredMethod("nonRetryableTestMethod");
        var fakeResult = new FakeTestResult(testMethod);
        fakeResult.setThrowable(new AssertionError("Failing on purpose"));

        var analyzer = new RetryAnalyzer();
        boolean shouldRetry = analyzer.retry(fakeResult);

        assertFalse(shouldRetry, "Retry should be skipped for @NonRetryable test");
    }

    @NonRetryable
    private void nonRetryableTestMethod() {
        // just to attach the annotation
    }

    public static class FakeTestNGMethod implements ITestNGMethod {

        private final ConstructorOrMethod constructorOrMethod;

        public FakeTestNGMethod(ConstructorOrMethod constructorOrMethod) {
            this.constructorOrMethod = constructorOrMethod;
        }

        @Override
        public ConstructorOrMethod getConstructorOrMethod() {
            return constructorOrMethod;
        }

        @Override
        public Map<String, String> findMethodParameters(XmlTest test) {
            return Map.of();
        }

        @Override
        public String getQualifiedName() {
            return "";
        }

        // All other methods return null/default/empty since they're unused
        @Override
        public Class<?> getRealClass() {
            return null;
        }

        @Override
        public ITestClass getTestClass() {
            return null;
        }

        @Override
        public void setTestClass(ITestClass cls) {

        }

        @Override
        public String getMethodName() {
            return null;
        }

        @Override
        public Object getInstance() {
            return null;
        }

        @Override
        public long[] getInstanceHashCodes() {
            return new long[0];
        }

        @Override
        public String[] getGroups() {
            return new String[0];
        }

        @Override
        public String[] getGroupsDependedUpon() {
            return new String[0];
        }

        @Override
        public String getMissingGroup() {
            return "";
        }

        @Override
        public void setMissingGroup(String group) {

        }

        @Override
        public String[] getBeforeGroups() {
            return new String[0];
        }

        @Override
        public String[] getAfterGroups() {
            return new String[0];
        }

        @Override
        public String[] getMethodsDependedUpon() {
            return new String[0];
        }

        @Override
        public void addMethodDependedUpon(String methodName) {

        }

        @Override
        public int getInvocationCount() {
            return 0;
        }

        @Override
        public void setInvocationCount(int count) {

        }

        @Override
        public int getSuccessPercentage() {
            return 0;
        }

        @Override
        public String getId() {
            return "";
        }

        @Override
        public void setId(String id) {

        }

        @Override
        public long getDate() {
            return 0;
        }

        @Override
        public void setDate(long date) {

        }

        @Override
        public boolean canRunFromClass(IClass testClass) {
            return false;
        }

        @Override
        public boolean isAlwaysRun() {
            return false;
        }

        @Override
        public int getThreadPoolSize() {
            return 0;
        }

        @Override
        public void setThreadPoolSize(int threadPoolSize) {

        }

        @Override
        public boolean getEnabled() {
            return false;
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public void setDescription(String description) {

        }

        @Override
        public void incrementCurrentInvocationCount() {

        }

        @Override
        public int getCurrentInvocationCount() {
            return 0;
        }

        @Override
        public int getParameterInvocationCount() {
            return 0;
        }

        @Override
        public void setParameterInvocationCount(int n) {

        }

        @Override
        public void setMoreInvocationChecker(Callable<Boolean> moreInvocationChecker) {

        }

        @Override
        public boolean hasMoreInvocation() {
            return false;
        }

        @Override
        public ITestNGMethod clone() {
            return null;
        }

        @Override
        public IRetryAnalyzer getRetryAnalyzer(ITestResult result) {
            return null;
        }

        @Override
        public Class<? extends IRetryAnalyzer> getRetryAnalyzerClass() {
            return null;
        }

        @Override
        public void setRetryAnalyzerClass(Class<? extends IRetryAnalyzer> clazz) {

        }

        @Override
        public boolean skipFailedInvocations() {
            return false;
        }

        @Override
        public void setSkipFailedInvocations(boolean skip) {

        }

        @Override
        public long getInvocationTimeOut() {
            return 0;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public void setPriority(int priority) {

        }

        @Override
        public int getInterceptedPriority() {
            return 0;
        }

        @Override
        public void setInterceptedPriority(int priority) {

        }

        @Override
        public XmlTest getXmlTest() {
            return null;
        }

        @Override
        public boolean isTest() {
            return true;
        }

        @Override
        public boolean isBeforeMethodConfiguration() {
            return false;
        }

        @Override
        public boolean isAfterMethodConfiguration() {
            return false;
        }

        @Override
        public boolean isBeforeClassConfiguration() {
            return false;
        }

        @Override
        public boolean isAfterClassConfiguration() {
            return false;
        }

        @Override
        public boolean isBeforeSuiteConfiguration() {
            return false;
        }

        @Override
        public boolean isAfterSuiteConfiguration() {
            return false;
        }

        @Override
        public boolean isBeforeTestConfiguration() {
            return false;
        }

        @Override
        public boolean isAfterTestConfiguration() {
            return false;
        }

        @Override
        public boolean isBeforeGroupsConfiguration() {
            return false;
        }

        @Override
        public boolean isAfterGroupsConfiguration() {
            return false;
        }

        @Override
        public long getTimeOut() {
            return 0;
        }

        @Override
        public void setTimeOut(long timeOut) {

        }

        @Override
        public List<Integer> getInvocationNumbers() {
            return Collections.emptyList();
        }

        @Override
        public void setInvocationNumbers(List<Integer> numbers) {

        }

        @Override
        public void addFailedInvocationNumber(int number) {

        }

        @Override
        public List<Integer> getFailedInvocationNumbers() {
            return List.of();
        }

        @Override
        public boolean ignoreMissingDependencies() {
            return false;
        }

        @Override
        public void setIgnoreMissingDependencies(boolean b) {
        }
    }

    public static class FakeTestResult implements ITestResult {

        private final ITestNGMethod method;
        private Throwable throwable;
        private int status = ITestResult.FAILURE;

        public FakeTestResult(Method testMethod) {
            this.method = new FakeTestNGMethod(new ConstructorOrMethod(testMethod));
        }

        @Override
        public ITestNGMethod getMethod() {
            return method;
        }

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public void setThrowable(Throwable t) {
            this.throwable = t;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setStatus(int status) {
            this.status = status;
        }

        // Stub rest
        @Override
        public Object getInstance() {
            return null;
        }

        @Override
        public Object[] getFactoryParameters() {
            return new Object[0];
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public long getStartMillis() {
            return 0;
        }

        @Override
        public long getEndMillis() {
            return 0;
        }

        @Override
        public void setEndMillis(long millis) {
        }

        @Override
        public String getHost() {
            return null;
        }

        @Override
        public Object[] getParameters() {
            return new Object[0];
        }

        @Override
        public void setParameters(Object[] parameters) {

        }

        @Override
        public IClass getTestClass() {
            return null;
        }

        @Override
        public String getTestName() {
            return null;
        }

        @Override
        public void setTestName(String name) {

        }

        @Override
        public String getInstanceName() {
            return null;
        }

        @Override
        public ITestContext getTestContext() {
            return null;
        }

        @Override
        public boolean wasRetried() {
            return false;
        }

        @Override
        public void setWasRetried(boolean wasRetried) {

        }

        @Override
        public String id() {
            return "";
        }

        @Override
        public String getAttribute(String name) {
            return null;
        }

        @Override
        public void setAttribute(String name, Object value) {
        }

        @Override
        public Object removeAttribute(String name) {
            return null;
        }

        @Override
        public int compareTo(ITestResult o) {
            return 0;
        }

        @Override
        public java.util.Set<String> getAttributeNames() {
            return null;
        }
    }
}
