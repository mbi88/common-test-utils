package com.mbi.request;

import com.mbi.HttpRequest;
import com.mbi.config.Header;
import com.mbi.config.Method;
import com.mbi.config.RequestConfig;
import com.mbi.config.RequestDirector;
import com.mbi.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Request builder. All fields are thread local.
 * <p>
 * ThreadLocal variables are not static in this case.
 * Is meant as a single container per instance, not container per class.
 * May create memory leak.
 */
@SuppressWarnings("PMD.LinguisticNaming")
public class RequestBuilder implements HttpRequest, Performable {

    private final ThreadLocal<String> urlThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Method> methodsThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Object> dataThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Integer> statusCodeThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<List<Header>> headersThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<RequestConfig> configThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<Object[]> pathParamsThreadLocal = new ThreadLocal<>();

    @Override
    public HttpRequest setHeader(final String header, final String value) {
        final var list = Objects.isNull(getHeaders()) ? new ArrayList<Header>() : getHeaders();
        list.add(new Header(header, value));
        headersThreadLocal.set(list);
        return this;
    }

    @Override
    public HttpRequest setHeaders(final List<Header> headers) {
        headersThreadLocal.set(headers);
        return this;
    }

    public List<Header> getHeaders() {
        return headersThreadLocal.get();
    }

    @Override
    public HttpRequest setData(final Object data) {
        dataThreadLocal.set(data);
        return this;
    }

    public Object getData() {
        return dataThreadLocal.get();
    }

    @Override
    public HttpRequest setToken(final String token) {
        tokenThreadLocal.set(token);
        return this;
    }

    public String getToken() {
        return tokenThreadLocal.get();
    }

    @Override
    public HttpRequest setExpectedStatusCode(final Integer statusCode) {
        statusCodeThreadLocal.set(statusCode);
        return this;
    }

    public Integer getStatusCode() {
        return statusCodeThreadLocal.get();
    }

    @Override
    public HttpRequest setConfig(final RequestConfig config) {
        configThreadLocal.set(config);
        return this;
    }

    public RequestConfig getConfig() {
        return configThreadLocal.get();
    }

    @Override
    public HttpRequest setUrl(final String url) {
        urlThreadLocal.set(url);
        return this;
    }

    public String getUrl() {
        return urlThreadLocal.get();
    }

    public Method getMethod() {
        return methodsThreadLocal.get();
    }

    private void setMethod(final Method method) {
        methodsThreadLocal.set(method);
    }

    public Object[] getPathParams() {
        return pathParamsThreadLocal.get();
    }

    private void setPathParams(final Object... pathParams) {
        pathParamsThreadLocal.set(pathParams);
    }

    /**
     * Send a http request to provided url and get th rest-assured response. All request artifacts(headers, body,
     * specification, etc) will be reset after request invocation.
     *
     * @param url        url to send the request to.
     * @param method     http method.
     * @param pathParams path parameters.
     * @return rest-assured response.
     */
    private Response doRequest(final String url, final Method method, final Object... pathParams) {
        setUrl(url);
        setMethod(method);
        setPathParams(pathParams);

        final var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("http-request.yml");
        requestDirector.constructRequest(this);

        final var httpRequest = new HttpRequestPerformer();
        httpRequest.addRequestListener(httpRequest::onRequest);
        httpRequest.addRequestListener(this::onRequest);

        return httpRequest.request(requestDirector.getRequestConfig());
    }

    @Override
    public Response post(final String url, final Object... pathParams) {
        return doRequest(url, Method.POST, pathParams);
    }

    @Override
    public Response get(final String url, final Object... pathParams) {
        return doRequest(url, Method.GET, pathParams);
    }

    @Override
    public Response put(final String url, final Object... pathParams) {
        return doRequest(url, Method.PUT, pathParams);
    }

    @Override
    public Response patch(final String url, final Object... pathParams) {
        return doRequest(url, Method.PATCH, pathParams);
    }

    @Override
    public Response delete(final String url, final Object... pathParams) {
        return doRequest(url, Method.DELETE, pathParams);
    }

    /**
     * Resets builder after request invocation.
     */
    @Override
    public void onRequest() {
        setUrl(null);
        setData(null);
        setExpectedStatusCode(null);
        setToken(null);
        setHeaders(null);
        setMethod(null);
        setConfig(null);
    }
}
