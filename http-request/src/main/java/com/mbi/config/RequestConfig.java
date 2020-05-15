package com.mbi.config;

import com.google.gson.Gson;

import java.util.List;

/**
 * Request configuration.
 */
public class RequestConfig {

    private Method method;
    private String url;
    private Object data;
    private List<Header> headers;
    private Integer expectedStatusCode;
    private Object[] pathParams;
    private Integer maxResponseLength;
    private Integer requestTimeOut;

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
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
        return pathParams == null ? null : pathParams.clone();
    }

    public void setPathParams(final Object... pathParams) {
        this.pathParams = pathParams;
    }

    public Integer getMaxResponseLength() {
        return maxResponseLength;
    }

    public void setMaxResponseLength(final Integer maxResponseLength) {
        this.maxResponseLength = maxResponseLength;
    }

    public Integer getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(final Integer requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
