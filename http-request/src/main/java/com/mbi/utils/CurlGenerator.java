package com.mbi.utils;

import com.mbi.config.RequestConfig;
import io.restassured.http.Header;

import java.util.List;

/**
 * Generates a curl.
 */
final class CurlGenerator {

    private final RequestConfig config;

    CurlGenerator(final RequestConfig config) {
        this.config = config;
    }

    public String getCurl() {
        return "curl"
                .concat(getMethod())
                .concat(getUrl())
                .concat(getHeaders())
                .concat(getData());
    }

    private String getMethod() {
        return String.format(" -X %s", config.getMethod());
    }

    private String getUrl() {
        return String.format(" '%s'", config.getUrl());
    }

    private String getHeaders() {
        String headersStr = "";

        final List<Header> headers = config.getHeaders();
        for (Header header : headers) {
            headersStr = headersStr
                    .concat(" -H")
                    .concat(String.format(" '%s: %s'", header.getName(), header.getValue()));
        }

        return headersStr;
    }

    private String getData() {
        String data = "";

        if (config.getData() != null) {
            data = String.format(" --data '%s'", config.getData().toString());
        }

        return data;
    }
}
