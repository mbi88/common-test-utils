package com.mbi.response;

import com.mbi.config.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Response model.
 */
public class Response {

    private Object body;
    private Integer statusCode;
    private List<Header> headers;

    public Object getBody() {
        return body;
    }

    public void setBody(final Object body) {
        this.body = body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final Integer statusCode) {
        this.statusCode = statusCode;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(final List<Header> headers) {
        this.headers = headers;
    }

    public JSONObject toJson() {
        return new JSONObject(getBody().toString());
    }

    public JSONArray toJsonArray() {
        return new JSONArray(getBody().toString());
    }

    public void print() {
        final var logger = LoggerFactory.getLogger(Response.class);

        if (this.toString().charAt(0) == new JSONObject().toString().charAt(0)) {
            logger.info(this.toJson().toString(2));
        } else if (this.toString().charAt(0) == new JSONArray().toString().charAt(0)) {
            logger.info(this.toJsonArray().toString(2));
        } else {
            logger.info(this.toString());
        }
    }

    @Override
    public String toString() {
        return getBody().toString();
    }
}
