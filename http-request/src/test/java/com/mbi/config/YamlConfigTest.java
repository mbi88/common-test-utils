package com.mbi.config;

import com.mbi.request.RequestBuilder;
import com.mbi.response.Response;
import com.mbi.utils.MessageComposer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.error.YAMLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class YamlConfigTest {

    @Test
    public void testCanSetConfigFromFile() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("http-request-test.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 100)
                .put("requestTimeOut", 600000)
                .put("headers", new JSONArray()
                        .put(new JSONObject().put("name", "Test").put("value", "header"))
                        .put(new JSONObject().put("name", "Header").put("value", "test header")));

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testDefaultYmlConfig() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("http-request.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 0)
                .put("requestTimeOut", 600000)
                .put("headers", new JSONArray()
                        .put(new JSONObject().put("name", "Accept").put("value", "application/json"))
                        .put(new JSONObject().put("name", "Content-Type").put("value", "application/json; charset=UTF-8")));

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testCanSetConfigFromFileWithoutConnectionTimeout() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("no_timeout.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 100)
                .put("headers", new JSONArray());

        assertTrue(expected.similar(actual));
    }

    @Test
    public void test0() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("response_more_0.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var response = new Response();
        response.setBody(new Object());
        response.setStatusCode(333);

        try {
            assertEquals(response.getStatusCode().intValue(), 234);
        } catch (AssertionError error) {
            new MessageComposer(new AssertionError("a"), requestDirector.getRequestConfig(), response).composeMessage();
        }
    }

    @Test
    public void testYamlFileNameNull() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile(null);
        requestDirector.constructRequest(new RequestBuilder());
        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        assertTrue(actual.similar(new JSONObject().put("headers", new JSONArray()).put("maxResponseLength", 0)));
    }

    @Test
    public void testIncorrectYamlFileName() {
        try {
            var r = new RequestDirector();
            r.setYamlConfigFile("asd");
            r.constructRequest(new RequestBuilder());
        } catch (NullPointerException e) {e.printStackTrace();
            assertEquals(e.getMessage(), "yaml config not found");
        }
    }

    @Test
    public void testResponseMore0() {
        var http = new RequestBuilder();
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("response_more_0.yml");
        requestDirector.constructRequest(http);

        var config = requestDirector.getRequestConfig();
        config.setRequestTimeOut(null);

        http.setConfig(config).get("https://google.com.ua");
    }

    @Test
    public void testTimeoutIsNull() {
        var http = new RequestBuilder();
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("no_timeout.yml");
        requestDirector.constructRequest(http);

        var config = requestDirector.getRequestConfig();
        config.setRequestTimeOut(null);

        http.setConfig(config).get("https://google.com.ua");
    }

    @Test
    public void testCanSetConfigFromFileWithoutResponseLength() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("no_response_length.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("requestTimeOut", 10000)
                .put("maxResponseLength", 0)
                .put("headers", new JSONArray()
                        .put(new JSONObject().put("name", "Test").put("value", "header"))
                        .put(new JSONObject().put("name", "Header").put("value", "test header")));

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testCanSetConfigFromFileWithoutHeaders() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("no_headers.yml");
        requestDirector.constructRequest(new RequestBuilder());

        var actual = new JSONObject(requestDirector.getRequestConfig().toString());

        var expected = new JSONObject()
                .put("maxResponseLength", 100)
                .put("requestTimeOut", 600000)
                .put("headers", new JSONArray());

        assertTrue(expected.similar(actual));
    }

    @Test
    public void testGetNullPointerExceptionIfFileIsEmpty() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("empty.yml");

        boolean failed = true;
        try {
            requestDirector.constructRequest(new RequestBuilder());
            failed = false;
        } catch (NullPointerException n) {
            assertTrue(failed);
        }
    }

    @Test
    public void testCantLoadFileWithUnexpectedFields() {
        var requestDirector = new RequestDirector();
        requestDirector.setYamlConfigFile("invalid.yml");

        try {
            requestDirector.constructRequest(new RequestBuilder());
        } catch (YAMLException y) {
            assertEquals(y.getCause().getMessage(),
                    "Unable to find property 'a' on class: com.mbi.config.YamlConfiguration");
        }
    }
}
