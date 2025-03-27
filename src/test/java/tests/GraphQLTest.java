package tests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controllers.GraphQL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class GraphQLTest {

    private HttpServer server;
    private String baseUrl;

    @BeforeClass
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // 0 = авто вибір порту
        server.createContext("/", new JsonHandler());
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
    public void testCanSendGraphQLRequest() {
        var graphQL = new GraphQL(baseUrl + "/success", "CP_ADMIN_TOKEN");
        var r = graphQL.send(new JSONObject(), false);

        assertEquals(r.asString(), """
                {"data":{"a":1}}""");
    }

    @Test
    public void testCanSendGraphQLRequestWithNoHasErrors() {
        var graphQL = new GraphQL(baseUrl + "/success", "CP_ADMIN_TOKEN");
        var r = graphQL.send(new JSONObject());

        assertEquals(r.asString(), """
                {"data":{"a":1}}""");
    }

    @Test
    public void testCanSendGraphQLRequestWithNoHasErrorsIfErrorsInResponse() {
        var graphQL = new GraphQL(baseUrl + "/error", "CP_ADMIN_TOKEN");

        var ex = expectThrows(AssertionError.class, () -> graphQL.send(new JSONObject()));
        assertTrue(ex.getMessage().contains("Response has errors! expected [false] but found [true]"));
    }

    @Test
    public void testCanSendGraphQLRequestWithHasErrorsTrueIfNoErrorsInResponse() {
        var graphQL = new GraphQL(baseUrl + "/success", "CP_ADMIN_TOKEN");
        var r = graphQL.send(new JSONObject(), true);

        assertEquals(r.asString(), """
                {"data":{"a":1}}""");
    }

    @Test
    public void testCanSendGraphQLRequestWithHasErrorsTrueIfErrorsInResponse() {
        var graphQL = new GraphQL(baseUrl + "/error", "CP_ADMIN_TOKEN");
        var r = graphQL.send(new JSONObject(), true);

        assertEquals(r.asString(), """
                {"data":{"a":1},"errors":[{"message":"error"}]}""");
    }

    @Test
    public void testCanSendGraphQLRequestWithHasErrorsFalseIfErrorsInResponse() {
        var graphQL = new GraphQL(baseUrl + "/error", "CP_ADMIN_TOKEN");

        var ex = expectThrows(AssertionError.class, () -> graphQL.send(new JSONObject(), false));
        assertTrue(ex.getMessage().contains("Response has errors! expected [false] but found [true]"));
    }

    @Test
    public void testCanSendGraphQLRequestWithToken() {
        var graphQL = new GraphQL(baseUrl + "/success", "CP_ADMIN_TOKEN");
        var r = graphQL.send(new JSONObject(), "token", true);

        assertEquals(r.asString(), """
                {"data":{"a":1}}""");
    }

    @Test
    public void testCanSendGraphQLRequestWithNullToken() {
        var graphQL = new GraphQL(baseUrl + "/success", "CP_ADMIN_TOKEN");
        var r = graphQL.send(new JSONObject(), null, false);

        assertEquals(r.asString(), """
                {"data":{"a":1}}""");
    }

    @Test
    public void testCanSendMultipart() {
        var graphQL = new GraphQL(baseUrl + "/success", "CP_ADMIN_TOKEN");
        var r = graphQL.sendMultipart(Map.of("a", 2), "token");

        assertEquals(r.body(), """
                {"data":{"a":1}}""");
    }

    @Test
    public void testCanGetQuery() {
        var r = GraphQL.getGraphQLQuery("/jsons/jo.json", new JSONObject().put("a", 1));

        assertEquals(r.toString(2), """
                {
                  "variables": {"a": 1},
                  "query": "{\\n  \\"a\\": 1\\n}"
                }""");
    }

    @Test
    public void testCanSendGraphQLRequestWithArray() {
        var graphQL = new GraphQL(baseUrl + "/success", "CP_ADMIN_TOKEN");
        var r = graphQL.send(new JSONArray());

        assertEquals(r.asString(), """
                {"data":{"a":1}}""");
    }

    @Test
    public void testSendMultipartWithFile() {
        var file = Path.of("src/test/resources/files/1.png");
        assertTrue(Files.exists(file)); // переконайся, що файл точно існує

        var graphQL = new GraphQL(baseUrl + "/multipart", "token");
        var response = graphQL.sendMultipart(Map.of("file", file), "token");

        assertEquals(response.statusCode(), 200);
        assertTrue(response.body().contains("\"multipart\":true"));
    }


    @Test
    public void testIOExceptionWrappedInRuntimeException() throws Exception {
        var graphql = new GraphQL("http://localhost/fake", "token");

        // Mock HttpClient
        var clientMock = mock(HttpClient.class);


        try (var clientStatic = mockStatic(HttpClient.class)) {
            clientStatic.when(HttpClient::newHttpClient).thenReturn(clientMock);

            @SuppressWarnings("unchecked")
            HttpResponse.BodyHandler<String> stringHandler = (HttpResponse.BodyHandler<String>) any(HttpResponse.BodyHandler.class);

            when(clientMock.send(any(HttpRequest.class), stringHandler))
                    .thenThrow(new IOException("Simulated IO error"));

            var ex = expectThrows(RuntimeException.class, () ->
                    graphql.sendMultipart(Map.of("key", "value"), "token"));

            assertTrue(ex.getMessage().contains("Failed to send multipart GraphQL request"));
            assertTrue(ex.getCause() instanceof IOException);
        }
    }

    @Test
    public void testIOExceptionFromReadAllBytesIsWrapped() {
        var path = Path.of("src/test/resources/fake.txt"); // неважливо який, бо мокаємо
        var graphQL = new GraphQL("http://localhost/doesnt-matter", "token");

        try (var filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.probeContentType(any(Path.class)))
                    .thenReturn("text/plain");

            filesMock.when(() -> Files.readAllBytes(any(Path.class)))
                    .thenThrow(new IOException("Simulated IO read error"));

            var ex = expectThrows(IllegalStateException.class, () ->
                    graphQL.sendMultipart(Map.of("file", path), "token"));

            assertTrue(ex.getMessage().contains("Failed to read file:"));
            assertTrue(ex.getCause() instanceof IOException);
        }
    }

    static class JsonHandler implements HttpHandler {

        private final Map<String, String> responses = Map.of(
                "/success", """
                        {"data":{"a":1}}""",
                "/error", """
                        {"data":{"a":1},"errors":[{"message":"error"}]}"""
        );

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var path = exchange.getRequestURI().getPath();

            if ("/multipart".equals(path)) {
                var response = """
                        {"data":{"multipart":true}}""";
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            var response = responses.get(path);

            if (response == null) {
                response = """
                        {"error":"Unknown path"}""";
                exchange.sendResponseHeaders(404, response.getBytes().length);
            } else {
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
