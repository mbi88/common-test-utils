package tests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import testcase.BaseTestCase;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static org.testng.Assert.*;

public class TestCaseTest extends BaseTestCase {

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

    @Test
    public void testHttpRequest() {
        http.setExpectedStatusCode(200).get(baseUrl + "/json-object");
    }

    @Test
    public void testJsonAssert() {
        assertion.jsonEquals(new JSONObject().put("a", 1), new JSONObject().put("a", 1));
    }

    @Test
    public void testJsonValidator() {
        validator.validate(new JSONObject().put("a", 1), new JSONObject().put("a", 1));
    }

    @Test
    public void testDateHandler() {
        assertEquals(dateHandler.getCurrentDate().length(), 10);
    }

    @Test
    public void tesFaker() {
        var res = jsonFaker.fakeData(new JSONObject().put("a", "Hello {$caller}!"));

        assertEquals(res.toString(), "{\"a\":\"Hello tests.TestCaseTest.tesFaker!\"}");
    }

    @Test
    public void testRandomNumLength() {
        assertEquals(String.valueOf(getRandomNum(1)).length(), 1);
        assertEquals(String.valueOf(getRandomNum(10)).length(), 10);
        assertEquals(String.valueOf(getRandomNum(5)).length(), 5);
    }

    @Test
    public void testGetRandomNumWithoutCount() {
        assertTrue(getRandomNum() > 1000);
    }

    @Test
    public void testRandomUid() {
        assertEquals(getRandomUID().length(), 36);
    }

    @Test
    public void testToJson() {
        var r = http.get(baseUrl + "/json-object");
        toJson(r);
    }

    @Test
    public void testToJsonArray() {
        var r = http.get(baseUrl + "/json-array");

        toJsonArray(r);
    }

    @Test
    public void testGetResource() {
        assertion.jsonEquals(getResource("/jsons/jo.json"), new JSONObject().put("a", 1));
    }

    @Test
    public void testGetResources() {
        assertion.jsonEquals(getResources("/jsons/ja.json"), new JSONArray().put(new JSONObject().put("a", 1)));
    }

    @Test
    public void testFindJsonInArray() {
        var json = findJsonInArray(getResources("/jsons/ja.json"), "a", 1);

        assertion.jsonEquals(json, new JSONObject().put("a", 1));
    }

    @Test
    public void testCantGetRandomNumWith0Digits() {
        var ex = expectThrows(IllegalArgumentException.class, () -> getRandomNum(0));
        assertTrue(ex.getMessage().contains("Value 0 is not in the supported range [1, 18]"));
    }

    @Test
    public void testCantGetRandomNumWithMinus1Digits() {
        var ex = expectThrows(IllegalArgumentException.class, () -> getRandomNum(-1));
        assertTrue(ex.getMessage().contains("Value -1 is not in the supported range [1, 18]"));
    }

    @Test
    public void testCantGetRandomNumWith20Digits() {
        var ex = expectThrows(IllegalArgumentException.class, () -> getRandomNum(20));
        assertTrue(ex.getMessage().contains("Value 20 is not in the supported range [1, 18]"));
    }

    @Test
    public void testGetRandomWithPositiveCases() {
        getRandomNum(1);
        getRandomNum(5);
        getRandomNum(18);
    }

    @Test
    public void testReplacing0InTheBeginning() {
        for (int i = 0; i < 1000; i++) {
            long num = getRandomNum(4);
            assertEquals(String.valueOf(num).length(), 4, num);
        }
    }

    @Test
    public void testObjectHasUpdateParameter() {
        assertion.jsonEquals(getResource("/jsons/obj_upd_par.json"),
                new JSONObject().put("a", "Hello " + dateHandler.getCurrentDate() + "!"));
    }

    @Test
    public void testArrayHasUpdateParameter() {
        assertion.jsonEquals(getResources("/jsons/array_upd_par.json"),
                new JSONArray().put(new JSONObject().put("a", "Hello " + dateHandler.getCurrentDate() + "!")));
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
