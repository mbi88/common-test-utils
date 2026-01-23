package config;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.mockito.MockedStatic;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import static config.LoadConfigUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ConfigTest {

    private static String baseUrl;
    private HttpServer server;

    @BeforeClass
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // automatically assign a free port
        server.createContext("/success", new JsonHandler());

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
    public void testCurrentEnv() {
        assertEquals(getCurrentEnv("https://www.qa1-asdasd.com"), "www.qa1");
        assertEquals(getCurrentEnv("https://qa1-asdasd.com"), "qa1");
        assertEquals(getCurrentEnv("www.qa1-asdasd.com"), "www.qa1");
        assertEquals(getCurrentEnv("qa1.domain.com"), "qa1.domain.com");
        assertEquals(getCurrentEnv("www.qa1-asdasd"), "www.qa1");
        assertEquals(getCurrentEnv("http://qa1-asdasd"), "qa1");
        assertEquals(getCurrentEnv("qa1asdasd.com"), "qa1asdasd.com");
        assertEquals(getCurrentEnv("qa1asdasd"), "qa1asdasd");
        assertEquals(getCurrentEnv("qa1asdasd"), "qa1asdasd");
    }

    @Test
    public void testReadEnvVar() {
        assertEquals(readVar("test"), "null");
    }

    @Test
    public void testCanReadEnvVar() {
        assertFalse(readVar("HOME").isEmpty());
    }

    @Test
    public void tesGetSSMParameter() {
        var ex = expectThrows(Throwable.class, () -> readSsmParameter("ddd"));
        assertTrue(ex.getMessage().contains("Unable to read SSM parameter: ddd"));
    }

    @Test
    public void testCanGetApiStatus() {
        var response = getApiStatus(baseUrl + "/success");

        assertEquals(new JSONObject(response.asString()).getInt("a"), 1);
    }

    @Test
    public void testCantGetApiStatusIfResourceNotFound() {
        var ex = expectThrows(IllegalStateException.class, () -> getApiStatus("http://run.mocky.io/v3/5ab8a4952c00005700"));
        assertTrue(ex.getMessage().contains("API is not available: http://run.mocky.io/v3/5ab8a4952c00005700"));
    }

    @Test
    public void testCantGetApiStatusIfInvalidUrl() {
        var ex = expectThrows(IllegalStateException.class, () -> getApiStatus("http://run.mocky"));
        assertTrue(ex.getMessage().contains("API is not available: http://run.mocky"));
    }

    @Test
    void testReadSsmParameter() {
        // Arrange
        var mockClient = mock(SsmClient.class);
        var mockParam = Parameter.builder().value("mocked-value").build();
        var mockResponse = GetParameterResponse.builder().parameter(mockParam).build();

        when(mockClient.getParameter(any(GetParameterRequest.class))).thenReturn(mockResponse);

        try (MockedStatic<LoadConfigUtils> utilsMock = mockStatic(LoadConfigUtils.class, CALLS_REAL_METHODS)) {
            utilsMock.when(LoadConfigUtils::ssmClient).thenReturn(mockClient);

            // Act
            String value = LoadConfigUtils.readSsmParameter("dummy");

            // Assert
            assertEquals("mocked-value", value);
        }
    }

    @Test
    void testReadSsmParameters() {
        // Arrange
        var mockClient = mock(SsmClient.class);
        var mockParam1 = Parameter.builder().name("dummy").value("mocked-value1").build();
        var mockParam2 = Parameter.builder().name("noparam").value("mocked-value2").build();
        var mockResponse = GetParametersResponse.builder()
                .parameters(mockParam1, mockParam2)
                .build();

        when(mockClient.getParameters(any(GetParametersRequest.class))).thenReturn(mockResponse);

        try (MockedStatic<LoadConfigUtils> utilsMock = mockStatic(LoadConfigUtils.class, CALLS_REAL_METHODS)) {
            utilsMock.when(LoadConfigUtils::ssmClient).thenReturn(mockClient);

            // Act
            Map<String, String> values = LoadConfigUtils.readSsmParameters("dummy", "noparam");

            // Assert
            assertEquals("mocked-value1", values.get("dummy"));
            assertEquals("mocked-value2", values.get("noparam"));
        }
    }

    @Test
    void testReadSsmParametersExceptionThrown() {
        var mockClient = mock(SsmClient.class);
        when(mockClient.getParameters(any(GetParametersRequest.class)))
                .thenThrow(new RuntimeException("SSM failure"));

        try (MockedStatic<LoadConfigUtils> utilsMock = mockStatic(LoadConfigUtils.class, CALLS_REAL_METHODS)) {
            utilsMock.when(LoadConfigUtils::ssmClient).thenReturn(mockClient);

            var ex = expectThrows(RuntimeException.class, () ->
                    LoadConfigUtils.readSsmParameters("param1111"));

            assertTrue(ex.getMessage().contains("Unable to batch read SSM parameters"));
        }
    }

    @Test
    void testReadSsmParametersWithInvalid() {
        var mockClient = mock(SsmClient.class);
        var param = Parameter.builder().name("found").value("value").build();
        var response = GetParametersResponse.builder()
                .parameters(param)
                .invalidParameters("missing")
                .build();

        when(mockClient.getParameters(any(GetParametersRequest.class))).thenReturn(response);

        try (MockedStatic<LoadConfigUtils> utilsMock = mockStatic(LoadConfigUtils.class, CALLS_REAL_METHODS)) {
            utilsMock.when(LoadConfigUtils::ssmClient).thenReturn(mockClient);

            Map<String, String> values = LoadConfigUtils.readSsmParameters("found", "missing");

            assertEquals("value", values.get("found"));
            assertFalse(values.containsKey("missing"));
        }
    }

    @Test
    void testReadSsmParametersAllParametersCached() {
        // Clear cache and pre-populate it
        LoadConfigUtils.clearCacheForTests();
        LoadConfigUtils.putToCacheForTests("param1", "cached-value1");
        LoadConfigUtils.putToCacheForTests("param2", "cached-value2");

        Map<String, String> values = LoadConfigUtils.readSsmParameters("param1", "param2");

        assertEquals(2, values.size());
        assertEquals("cached-value1", values.get("param1"));
        assertEquals("cached-value2", values.get("param2"));
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
