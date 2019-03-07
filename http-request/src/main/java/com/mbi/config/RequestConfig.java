package com.mbi.config;

import com.google.gson.Gson;
import io.restassured.http.Header;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;

import java.util.List;

/**
 * Request configuration.
 */
public class RequestConfig {

    private Method method;
    private String url;
    private Object data;
    private transient RequestSpecification requestSpecification;
    private List<Header> headers;
    private Integer expectedStatusCode;
    private Object[] pathParams;
    private int maxResponseLength;
    private boolean debug;

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    public void setRequestSpecification(final RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Object getData() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(final List<Header> headers) {
        this.headers = headers;
    }

    public Integer getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(final Integer expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public Object[] getPathParams() {
        return pathParams.clone();
    }

    public void setPathParams(final Object... pathParams) {
        this.pathParams = pathParams;
    }

    public int getMaxResponseLength() {
        return maxResponseLength;
    }

    public void setMaxResponseLength(final int maxResponseLength) {
        this.maxResponseLength = maxResponseLength;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
