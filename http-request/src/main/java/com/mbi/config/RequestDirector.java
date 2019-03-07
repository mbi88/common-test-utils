package com.mbi.config;

import com.mbi.request.RequestBuilder;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Header;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static io.restassured.RestAssured.given;

/**
 * Configures request.
 */
public class RequestDirector {

    private final RequestBuilder requestBuilder;
    private final YamlConfiguration yamlConfiguration;
    private final RequestConfig requestConfig = new RequestConfig();

    public RequestDirector(final RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
        yamlConfiguration = readYamlConfiguration();
    }

    public RequestConfig getRequestConfig() {
        return this.requestConfig;
    }

    public void constructRequest() {
        final RequestSpecification spec = configureRequest();

        requestConfig.setRequestSpecification(spec);
        requestConfig.setMethod(requestBuilder.getMethod());
        requestConfig.setUrl(requestBuilder.getUrl());
        requestConfig.setData(((FilterableRequestSpecification) spec).getBody());
        requestConfig.setHeaders(new ArrayList<>(((FilterableRequestSpecification) spec).getHeaders().asList()));
        requestConfig.setExpectedStatusCode(requestBuilder.getStatusCode());
        requestConfig.setPathParams(requestBuilder.getPathParams());
        requestConfig.setDebug(requestBuilder.getDebug());
        requestConfig.setMaxResponseLength(getMaxResponseLength());
    }

    private RequestSpecification configureRequest() {
        final RequestSpecification spec = given();

        setDefaultHeaders(spec);
        setRequestTimeout(spec);
        setSpecification(spec);
        setToken(spec);
        setData(spec);
        appendHeaders(spec);
        setDebug(spec);

        return spec;
    }

    private YamlConfiguration readYamlConfiguration() {
        final InputStream in = getClass().getClassLoader().getResourceAsStream("http-request.yml");
        if (Objects.isNull(in)) {
            return new YamlConfiguration();
        }

        return new Yaml().loadAs(in, YamlConfiguration.class);
    }

    private void setDefaultHeaders(final RequestSpecification spec) {
        if (!Objects.isNull(yamlConfiguration.getHeaders())) {
            spec.headers(yamlConfiguration.getHeaders());
        }
    }

    private void setRequestTimeout(final RequestSpecification spec) {
        if (!Objects.isNull(yamlConfiguration.getConnectionTimeout())) {
            final RestAssuredConfig config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", yamlConfiguration.getConnectionTimeout())
                    .setParam("http.socket.timeout", yamlConfiguration.getConnectionTimeout()));
            spec.config(config);
        }
    }

    private void setSpecification(final RequestSpecification spec) {
        if (requestBuilder.getSpecification() != null) {
            spec.spec(requestBuilder.getSpecification());
        }
    }

    private void setToken(final RequestSpecification spec) {
        if (requestBuilder.getToken() != null) {
            spec.header("Authorization", requestBuilder.getToken());
        }
    }

    private void setData(final RequestSpecification spec) {
        if (requestBuilder.getData() != null) {
            spec.body(requestBuilder.getData().toString());
        }
    }

    private void appendHeaders(final RequestSpecification spec) {
        if (requestBuilder.getHeaders() != null) {
            for (Header header : requestBuilder.getHeaders()) {
                spec.header(header);
            }
        }
    }

    private void setDebug(final RequestSpecification spec) {
        if (requestBuilder.getDebug()) {
            spec.log().everything();
        }
    }

    private int getMaxResponseLength() {
        return (!Objects.isNull(yamlConfiguration.getMaxResponseLength()))
                ? yamlConfiguration.getMaxResponseLength()
                : 0;
    }
}
