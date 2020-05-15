package com.mbi.config;

import com.mbi.request.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class ObjectConfigTest {

    @Test
    public void testCanSetHeaders() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("http-request.yml");
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("h1", "v1"), new Header("h2", "v2"), new Header("h3", "v3")));

        var builder = new RequestBuilder();
        builder.setConfig(config);

        requestDirector.constructRequest(builder);

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject();
        expected.put("maxResponseLength", 0);
        expected.put("requestTimeOut", 600000);
        expected.put("headers", new JSONArray()
                .put(new JSONObject().put("name", "h1").put("value", "v1"))
                .put(new JSONObject().put("name", "h2").put("value", "v2"))
                .put(new JSONObject().put("name", "h3").put("value", "v3")));

        assertTrue(actual.similar(expected));
    }

    @Test
    public void testCanSetCode() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("http-request.yml");
        var config = new RequestConfig();
        config.setExpectedStatusCode(200);

        var builder = new RequestBuilder();
        builder.setConfig(config);

        requestDirector.constructRequest(builder);

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        assertTrue(actual.similar(new JSONObject(
                "{" +
                        "   \"maxResponseLength\": 0, \"expectedStatusCode\": 200, \"requestTimeOut\": 600000,\n" +
                        "   \"headers\": [\n" +
                        "       { \"name\": \"Accept\", \"value\": \"application/json\" },\n" +
                        "       { \"name\": \"Content-Type\", \"value\": \"application/json; charset=UTF-8\" }]\n" +
                        "}")));
    }
}
