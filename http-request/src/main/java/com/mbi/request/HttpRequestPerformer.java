package com.mbi.request;

import com.mbi.config.Header;
import com.mbi.config.RequestConfig;
import com.mbi.response.Response;
import com.mbi.utils.MessageComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mbi.utils.Commons.buildPathParams;
import static org.testng.Assert.assertEquals;


/**
 * Performs request.
 */
final class HttpRequestPerformer implements Performable {

    private final List<OnRequestPerformedListener> requestListeners = new ArrayList<>();
    private final Response response = new Response();
    private final Logger logger = LoggerFactory.getLogger("file-logger");
    private RequestConfig config;

    /**
     * Checks equality of actual response status code and expected status code if it was set.
     *
     * @param response response of a request.
     * @param config   request configuration.
     * @throws AssertionError if actual response status code is not equal to expected.
     */
    private void checkStatusCode(final Response response, final RequestConfig config) {
        // No need to check status code if it's not set
        if (Objects.isNull(config.getExpectedStatusCode())) {
            return;
        }

        try {
            assertEquals(response.getStatusCode(), config.getExpectedStatusCode());
        } catch (AssertionError assertionError) {
            final var message = new MessageComposer(assertionError, config, response).composeMessage();
            throw new AssertionError(message, assertionError);
        }
    }

    /**
     * Performs request. Compares status code with expected. Finally resets request builder to default.
     *
     * @param requestConfig request configuration.
     * @return response.
     * @throws AssertionError on errors. Exception message contains url, response and request as a curl.
     */
    public Response request(final RequestConfig requestConfig) {
        final var httpClient = HttpClient.newBuilder().build();
        config = requestConfig;

        final var timeout = Duration.ofMillis(requestConfig.getRequestTimeOut() == null
                ? 600_000
                : requestConfig.getRequestTimeOut());
        final var requestData = requestConfig.getData() == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(requestConfig.getData().toString());

        try {
            final var request = HttpRequest.newBuilder()
                    .timeout(timeout)
                    .method(requestConfig.getMethod().name(), requestData)
                    .uri(URI.create(buildPathParams(requestConfig.getUrl(), requestConfig.getPathParams())));

            if (requestConfig.getHeaders() != null && !requestConfig.getHeaders().isEmpty()) {
                final var list = new ArrayList<String>();
                requestConfig.getHeaders().forEach(header -> {
                    list.add(header.getName());
                    list.add(header.getValue());
                });

                request.headers(list.toArray(new String[0]));
            }

            final var httpResponse = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());

            response.setBody(httpResponse.body());
            response.setHeaders(httpResponse.headers().map().entrySet().stream()
                    .map(m -> new Header(m.getKey(), m.getValue().get(0))).collect(Collectors.toList()));
            response.setStatusCode(httpResponse.statusCode());

            checkStatusCode(response, requestConfig);
        } catch (InterruptedException | IOException error) {
            logger.error(error.getMessage());
        } finally {
            requestListeners.forEach(OnRequestPerformedListener::onRequestPerformed);
        }

        return response;
    }

    /**
     * Listens to request invocation.
     *
     * @param requestListener listener.
     */
    public void addRequestListener(final OnRequestPerformedListener requestListener) {
        this.requestListeners.add(requestListener);
    }

    @Override
    public void onRequest() {
        logger.info(String.format("Request: %s%nResponse: %s%n",
                config.toString(),
                Objects.isNull(response.getBody()) ? "null" : response.getBody().toString()));
    }
}
