package controllers;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Controller for sending GraphQL requests.
 */
public final class GraphQL extends Controller<GraphQL> {

    private static final Function<String, String> GET_STRING_FROM_FILE = path -> {
        try {
            final var url = GraphQL.class.getResource(path);
            final var filePath = Paths.get(Objects.requireNonNull(url).toURI());
            return Files.readString(filePath);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("Can't read file: " + path, e);
        }
    };
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private final String token;
    private final String apiUrl;

    /**
     * Creates a new GraphQL controller.
     *
     * @param apiUrl GraphQL endpoint URL.
     * @param token  Authorization token.
     */
    public GraphQL(final String apiUrl, final String token) {
        this.apiUrl = apiUrl;
        this.token = token;
    }

    /**
     * Reads a GraphQL query from the resource file.
     *
     * @param path path to query file.
     * @return query as JSON object.
     */
    public static JSONObject getGraphQLQuery(final String path) {
        return new JSONObject().put("query", GET_STRING_FROM_FILE.apply(path));
    }

    /**
     * Reads a GraphQL query with variables from the resource file.
     *
     * @param path      path to query file.
     * @param variables variables.
     * @return query + variables.
     */
    public static JSONObject getGraphQLQuery(final String path, final JSONObject variables) {
        return getGraphQLQuery(path).put("variables", variables);
    }

    public Response send(final JSONObject json) {
        return send(json, token, false);
    }

    public Response send(final JSONArray json) {
        return http
                .setData(json)
                .setExpectedStatusCode(200)
                .setToken(token)
                .checkNoErrors(true)
                .post(apiUrl);
    }

    public Response send(final JSONObject json, final boolean hasErrors) {
        return send(json, token, hasErrors);
    }

    public Response send(final JSONObject json, final String token, final boolean hasErrors) {
        return http
                .setData(json)
                .setExpectedStatusCode(200)
                .setToken(token)
                .checkNoErrors(!hasErrors)
                .post(apiUrl);
    }

    /**
     * Sends a multipart GraphQL request.
     *
     * @param data  key-value map for multipart body.
     * @param token auth token.
     * @return HTTP response.
     */
    public HttpResponse<String> sendMultipart(final Map<Object, Object> data, final String token) {
        // Random 256 length string is used as multipart boundary
        final var boundary = new BigInteger(256, new Random()).toString();

        final var request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .headers("authorization", token,
                        "content-type", STR."multipart/form-data;boundary=\{boundary}",
                        "GraphQL-Preflight", "1")
                .POST(ofMimeMultipartData(data, boundary))
                .build();

        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send multipart GraphQL request", e);
        }
    }

    /**
     * Builds multipart form body.
     *
     * @param data     form data.
     * @param boundary boundary string.
     * @return body publisher.
     */
    private HttpRequest.BodyPublisher ofMimeMultipartData(final Map<Object, Object> data, final String boundary) {
        // Result request body
        final var byteArrays = new ArrayList<byte[]>();

        // Separator with boundary
        final var separator = STR."--\{boundary}\r\nContent-Disposition: form-data; name="
                .getBytes(StandardCharsets.UTF_8);

        // Iterating over data parts
        for (var entry : data.entrySet()) {
            // Opening boundary
            byteArrays.add(separator);

            // If value is type of Path (file) append content type with file name and file binaries, otherwise simply
            // append key=value
            if (entry.getValue() instanceof Path path) {
                try {
                    final var mimeType = Files.probeContentType(path);
                    byteArrays.add(STR."\"\{entry.getKey()}\"; filename=\"\{path
                            .getFileName()}\"\r\nContent-Type: \{mimeType}\r\n\r\n"
                            .getBytes(StandardCharsets.UTF_8));
                    byteArrays.add(Files.readAllBytes(path));
                    byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to read file: " + path, e);
                }
            } else {
                byteArrays.add(STR."\"\{entry.getKey()}\"\r\n\r\n\{entry.getValue()}\r\n"
                        .getBytes(StandardCharsets.UTF_8));
            }
        }

        // Closing boundary
        byteArrays.add(STR."--\{boundary}--".getBytes(StandardCharsets.UTF_8));
        // Serializing as byte array
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
