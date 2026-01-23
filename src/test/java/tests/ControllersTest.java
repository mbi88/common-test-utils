package tests;

import com.mbi.request.RequestBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controllers.*;
import io.restassured.response.Response;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.function.Function;

import static org.testng.Assert.*;
import static testcase.BaseTestCase.toJson;

public class ControllersTest {

    private static String baseUrl;
    private HttpServer server;

    @BeforeClass
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // automatically assign a free port
        server.createContext("/success", new JsonHandler());
        server.createContext("/error", new JsonHandler());
        server.setExecutor(null);
        server.start();

        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
    }

    @AfterClass
    public void stopServer() {
        server.stop(0);
    }

    @Test
    public void testCanAddSameParameters() {
        var parameter = new QueryParameter();
        parameter.addParameter("q", 1);
        parameter.addParameter("q", 1);

        assertEquals(parameter.getParametersString(), "?q=1&q=1");
    }

    @Test
    public void testCanSeveralParameters() {
        var parameter = new QueryParameter();
        parameter.addParameter("q", 1);
        parameter.addParameter("w", 2);

        assertEquals(parameter.getParametersString(), "?q=1&w=2");
    }

    @Test
    public void testAddParametersViaConstructor() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("w", 2);

        assertEquals(parameter.getParametersString(), "?q=1&w=2");
    }

    @Test
    public void testCanRemoveAllParameters() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("q", 2);

        parameter.removeParameter("q");

        assertEquals(parameter.getParametersString(), "");
    }

    @Test
    public void testCanRemoveParameterByValue() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("q", 2);
        parameter.addParameter("w", 1);

        parameter.removeParameter("q", 1);

        assertEquals(parameter.getParametersString(), "?q=2&w=1");
    }

    @Test
    public void testCanRemoveParameter() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("q", 2);
        parameter.addParameter("w", 1);

        parameter.removeParameter("q");

        assertEquals(parameter.getParametersString(), "?w=1");
    }

    @Test
    public void testWaiterIfConditionIsMet() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(10))
                .build();

        waiter.waitCondition(response -> response.statusCode() == 200, "response -> response.statusCode() == 200");
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testWaiterIfConditionIsMetWithDeprecatedMethods() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setWaitingTime(10)
                .setIdleDuration(1000)
                .build();

        waiter.waitCondition(response -> response.statusCode() == 200, "response -> response.statusCode() == 200");
    }

    @Test
    public void testWaiterIfConditionIsNoMet() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(10))
                .build();

        var ex = expectThrows(RuntimeException.class, () -> waiter
                .waitCondition(response -> response.statusCode() == 20, "response.statusCode() == 20"));
        assertTrue(ex.getMessage().contains("Expected condition not met. Max waiting time exceeded"));
    }

    @Test
    public void testCanSetIdleDuration() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(3))
                .setIdleDuration(Duration.ofSeconds(5))
                .build();

        var ex = expectThrows(TimeExceededRuntimeException.class, () -> waiter
                .waitCondition(response -> response.statusCode() == 20));
        assertTrue(ex.getMessage().contains("Expected condition not met. Max waiting time exceeded"));
    }

    @Test
    public void testCanSetDebug() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(3))
                .setDebug(true)
                .build();

        var ex = expectThrows(TimeExceededRuntimeException.class, () -> waiter
                .waitCondition(response -> response.statusCode() == 20));
        assertTrue(ex.getMessage().contains("Expected condition not met. Max waiting time exceeded"));
    }

    @Test
    public void testCanReadWaiterResponse() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(10))
                .build();

        var r = waiter.waitCondition(response -> response.statusCode() == 200);

        assertEquals(toJson(r).getInt("a"), 1);
    }

    @Test
    public void testCantWaitWithoutSupplier() {
        var waiter = Waiter.<Response>newBuilder()
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(10))
                .build();

        var ex = expectThrows(IllegalStateException.class, () -> waiter
                .waitCondition(response -> response.statusCode() == 200));
        assertTrue(ex.getMessage().contains("Supplier, resultToString, and expectedCondition must not be null"));
    }

    @Test
    public void testCantWaitWithoutResult() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setTimeout(Duration.ofSeconds(10))
                .build();

        var ex = expectThrows(IllegalStateException.class, () -> waiter
                .waitCondition(response -> response.statusCode() == 200));
        assertTrue(ex.getMessage().contains("Supplier, resultToString, and expectedCondition must not be null"));
    }

    @Test
    public void testCantWaitWithoutExpectedResult() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(10))
                .build();

        var ex = expectThrows(IllegalStateException.class, () -> waiter
                .waitCondition(null));
        assertTrue(ex.getMessage().contains("Supplier, resultToString, and expectedCondition must not be null"));
    }

    @Test
    public void testWithAllNull() {
        var waiter = Waiter.<Response>newBuilder()
                .build();

        var ex = expectThrows(IllegalStateException.class, () -> waiter
                .waitCondition(null));
        assertTrue(ex.getMessage().contains("Supplier, resultToString, and expectedCondition must not be null"));
    }

    @Test
    public void testCanGetWithIdle() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(10))
                .setIdleDuration(Duration.ofMillis(70))
                .setDebug(true)
                .build();

        var predicate = PredicateLogger.log(
                response -> response.statusCode() == 200,
                LoggerFactory.getLogger(this.getClass()),
                "status == 200",
                Response::asString
        );

        var r = waiter.waitCondition(predicate);

        assertEquals(toJson(r).getInt("a"), 1);
    }

    @Test
    public void testWithIncorrectId() {
        class TestClass extends Controller<TestClass> {
        }
        var testClass = new TestClass();

        assertTrue(testClass.withIncorrectId().getId().toString()
                .matches("^([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12})$"));
    }

    @Test
    public void testWithoutId() {
        var testClass = new TestClass();

        assertEquals(testClass.withoutId().getId().toString().length(), 0);
    }

    @Test
    public void testSettingIdViaConstructor() {
        var testClass = new TestClass(1);

        assertEquals(testClass.getId(), 1);
    }

    @Test
    public void testErrorMessageOnNullId() {
        var testClass = new TestClass(null);

        var ex = expectThrows(NullPointerException.class, testClass::getId);
        assertTrue(ex.getMessage().contains("TestClass: Object id is not initialized"));
    }

    @Test
    public void testExtractingIdFromResponse() {
        var testClass = new TestClass();
        var r = testClass.getResponse();

        assertEquals(testClass.getId(), 1);
        assertEquals(r.asString(), """
                {"a":1}""");
    }

    @Test
    public void testCanSetTimeExceededMessage() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(3))
                .setDebug(true)
                .setTimeExceededMessage("hello! time exceeded")
                .build();

        var ex = expectThrows(TimeExceededRuntimeException.class, () -> waiter
                .waitCondition(response -> response.statusCode() == 20));
        assertTrue(ex.getMessage().contains("hello! time exceeded"));
    }

    @Test
    public void testWarningIsLoggedIfPredicateThrowsException() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(3))
                .setDebug(true)
                .build();

        try {
            var predicate = PredicateLogger.<Response>log(
                    _ -> {
                        throw new RuntimeException("oh no");
                    },
                    LoggerFactory.getLogger(this.getClass()),
                    "status == 200",
                    _ -> ""
            );

            var r = waiter.waitCondition(predicate);

            assertEquals(toJson(r).getInt("a"), 1);
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("oh no"));
        }
    }

    @Test
    public void testPredicateHasNullStringifier() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get(baseUrl + "/success"))
                .setResultToString(Response::asString)
                .setTimeout(Duration.ofSeconds(3))
                .setDebug(true)
                .build();

        var predicate = PredicateLogger.<Response>log(
                response -> response.statusCode() == 200,
                LoggerFactory.getLogger(this.getClass()),
                "status == 200",
                _ -> null
        );

        var r = waiter.waitCondition(predicate);

        assertEquals(toJson(r).getInt("a"), 1);
    }

    static class JsonHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getHttpContext().getPath();
            String response;
            int status;

            switch (path) {
                case "/success" -> {
                    response = "{\"a\":1}";
                    status = 200;
                }
                case "/error" -> {
                    response = "{\"data\":{\"a\":1},\"errors\":[{\"message\":\"error\"}]}";
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

    static class TestClass extends Controller<TestClass> implements Creatable {
        private final Function<Response, Integer> getIdFunction = response -> toJson(response).optInt("a");

        TestClass() {
        }

        TestClass(Integer id) {
            super(id);
        }

        Response getResponse() {
            Response response = http.get(baseUrl + "/success");
            setId(extractId(response, getIdFunction));

            return response;
        }
    }
}
