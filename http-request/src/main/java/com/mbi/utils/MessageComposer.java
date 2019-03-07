package com.mbi.utils;


import com.mbi.config.RequestConfig;
import io.restassured.response.Response;

/**
 * Composes error message on request failure.
 */
public final class MessageComposer {

    private final String error;
    private final String url;
    private final String response;
    private final String request;
    private final int responseLength;

    public MessageComposer(final AssertionError error, final RequestConfig config, final Response response) {
        this.error = error.getMessage();
        this.url = String.format("%nUrl: %s", config.getUrl());
        this.response = String.format("%n%nResponse: %s%n", response.asString());
        this.request = String.format("%n%nRequest: %s%n%n", new CurlGenerator(config).getCurl());
        this.responseLength = config.getMaxResponseLength();
    }

    public String composeMessage() {
        return error
                .concat(url)
                .concat(cutResponse(response))
                .concat(request);
    }

    private String cutResponse(final String response) {
        return (responseLength > 0) ? response.substring(0, Math.min(responseLength, response.length())) : response;
    }
}
