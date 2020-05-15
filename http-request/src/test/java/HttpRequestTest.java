import com.damnhandy.uri.template.UriTemplate;
import com.mbi.HttpRequest;
import com.mbi.config.Header;
import com.mbi.config.RequestConfig;
import com.mbi.request.RequestBuilder;
import com.mbi.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.testng.Assert.*;


public class HttpRequestTest {

    private HttpRequest http = new RequestBuilder();

    @Test
    void testName() throws IOException, InterruptedException {
        var url = "https://qa1-api.userreport.com/report/v1/accounts/c982cd4c-3dd0-4eea-80f2-84b7a5807f62/personas";
        var boy = "{\"name\":\"tests.persona.PostTest.<init>\",\"questions\":[{\"answers\":[\"string\"],\"id\":790649}],\"description\":\"2019-08-27\",\"favorite\":false}";

        final Logger log = LoggerFactory.getLogger(HttpRequestTest.class);
        HttpClient httpClient = HttpClient.newBuilder().build();

        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .timeout(Duration.ofMillis(10000))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer eyJraWQiOiJyMFkyZnBGb1dvNHVqdkRNS2d2SmttbGY0aEthV2tXQ01RWlZSVW44RVA4PSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI1MzAyZmFhNy0xNzc2LTQwMjUtYmYyMi04NGIzZDU4NmRlM2QiLCJoYXNfY29udHJvbF9wYW5lbF9hY2Nlc3MiOiJ5ZXMiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV82azd4SWxZZmYiLCJwaG9uZV9udW1iZXJfdmVyaWZpZWQiOnRydWUsImNvZ25pdG86dXNlcm5hbWUiOiIyM2YzNDYxZi03MGRmLTQ0MzktODdlMS0zNzQxOWFmY2FlZWYiLCJjb250cm9sX3BhbmVsIjoiU3VwZXJBZG1pbjoqIiwiYXVkIjoiM3BnMmp1ZXBhYm1zN2Z2bWlrN25mbTBhMHYiLCJldmVudF9pZCI6IjdiMzc2YTc2LWYzNmUtNDkwOS04OTNhLWI3ZWM2Mzg4NzdkZSIsImxpY2Vuc2VfYWdyZWVtZW50X2FjY2VwdGVkIjoieWVzIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE1NjY5MDM3MDEsInBob25lX251bWJlciI6IiszODA2MzQyMjQxMzciLCJleHAiOjE1NjY5MTc1MzgsImlhdCI6MTU2NjkxMzkzOCwiZW1haWwiOiJtYkBhdWRpZW5jZXByb2plY3QuY29tIn0.MY8lYSBnR1ysMzrq6TJBMJAzlR5tR2mgrrG2HTsK6aOfg7dT9Z2YRZdglxW3X7TVHMTynPMdYcaWxNQnSgcBwGb0UcNueDXdCDMduBVj5-jeJ1H4mJAfxqjv52PVZsosNNvUXqMA5ctnWJMluZCZGd2EB7DpiYJjSbvnvO7RGtu4vRVsL0WwGgu-L_HOzYsrCyCWMU6eYqjMB4ah2UdRd-pybXaxG4kQ_LxMfKy-FV0VTmj0PsL-p9V9h3CmsP9MBPMC5uzEBT-9dN4Jq2BTAw4RTOuC82666Z5D3Hi2pEeKGq8k5AIPlYlfgWSn0Bs9HrBBwHyPKhNELSK3dqr-oQ")
                .method("POST", java.net.http.HttpRequest.BodyPublishers.ofString(new JSONObject(boy).toString()))
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Response status code: " + response.statusCode());
        log.info("Response headers: " + response.headers());
        log.info("Response body: " + response.body());
    }

    @Test
    void testSetHeader() {
        try {
            http
                    .setHeader("header1", "v")
                    .setHeader("header2", "v")
                    .setExpectedStatusCode(300)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("ua' -H 'Accept: application/json' -H 'Content-Type: application/json; "
                    + "charset=UTF-8' -H 'header1: v' -H 'header2: v'\n"));
        }
    }

    @Test
    void testSetHeaders() {
        var headers = new ArrayList<Header>();
        headers.add(new Header("h1", "v"));
        headers.add(new Header("h2", "v"));

        try {
            http
                    .setHeaders(headers)
                    .setExpectedStatusCode(400)
                    .get("http://www.mocky.io/v2/5ab8a4952c00005700186093");
        } catch (AssertionError error) {
            error.printStackTrace();
            assertTrue(error.getMessage().contains("93' -H 'Accept: application/json' -H 'Content-Type: application/json;"
                    + " charset=UTF-8' -H 'h1: v' -H 'h2: v'\n"));
        }
    }

    @Test
    void testWithoutHeaders() {
        try {
            http
                    .setExpectedStatusCode(230)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://google.com.ua' -H 'Accept: application/json'"
                    + " -H 'Content-Type: application/json; charset=UTF-8'\n"));
        }
    }

    @Test
    void testConfigHeaders() {
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("config", "v"), new Header("argument", "h")));

        try {
            http
                    .setToken("wer")
                    .setConfig(config)
                    .setHeader("argument", "h")
                    .setExpectedStatusCode(342)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("ua' -H 'config: v' -H 'argument: h' -H 'argument: h' -H 'Authorization: wer'\n"));
        }
    }

    @Test
    void testHeadersOverride() {
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("Accept", "1")));

        try {
            http
                    .setConfig(config)
                    .setHeader("Accept", "2")
                    .setExpectedStatusCode(342)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://google.com.ua' -H 'Accept: 1' -H 'Accept: 2'"));
        }
    }

    @Test
    void testSetBody() {
        try {
            http
                    .setExpectedStatusCode(342)
                    .setData(1)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '1'\n\n"));
        }
    }

    @Test
    void testWithoutBody() {
        try {
            http
                    .setExpectedStatusCode(324)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains(" --data '"));
        }
    }

    @Test
    void testBodyOverridesSpec() {
        var config = new RequestConfig();
        config.setData(1);
        try {
            http
                    .setConfig(config)
                    .setExpectedStatusCode(234)
                    .setData(2)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '2'\n\n"));
        }
    }

    @Test
    void testSetToken() {
        try {
            http
                    .setToken("token")
                    .setExpectedStatusCode(4534)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token"));
        }
    }

    @Test
    void testTokenWithSpec() {
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("Authorization", "token1")));

        try {
            http
                    .setConfig(config)
                    .setExpectedStatusCode(24)
                    .setToken("token2")
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token2"));
        }
    }

    @Test
    void testWithoutToken() {
        try {
            http
                    .setExpectedStatusCode(356)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains("-H 'Authorization:"));
        }
    }

    @Test
    void testWithTokenInSpec() {
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("Authorization", "token1")));
        config.setRequestTimeOut(null);

        try {
            http
                    .setToken("asdasd")
                    .setHeader("aaa", "bbb")
                    .setExpectedStatusCode(463)
                    .setConfig(config)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token1"));
        }
    }

    @Test
    void testCodeFail() {
        boolean passed;
        try {
            http
                    .setExpectedStatusCode(404)
                    .get("https://google.com");
            passed = true;
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("expected [404] but found [301]"));
            passed = false;
        }

        assertFalse(passed);
        http.get("https://google.com");
    }

    @Test
    void testCodeSuccess() {
        http
                .setExpectedStatusCode(404)
                .get("https://google.com/asdasd");
    }

    @Test
    void testWithoutCode() {
        Response r = http.get("https://google.com/asdasd");

        assertTrue(r.toString().contains("<html"));
    }

    @Test
    void testSpec() {
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("Authorization", "token1")));
        config.setData(1);

        try {
            http
                    .setConfig(config)
                    .setExpectedStatusCode(234)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://google.com.ua' -H 'Authorization: token1' --data '1'"));
        }
    }

    @Test
    void testWithoutSpec() {
        try {
            http
                    .setExpectedStatusCode(342)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains(" curl -X GET 'https://google.com.ua' -H 'Accept: application/json'"
                    + " -H 'Content-Type: application/json; charset=UTF-8'\n"));
        }
    }

    @Test
    void testGet() {
        http.setExpectedStatusCode(200).get("https://httpbin.org/get");
    }

    @Test
    void testPost() {
        http.setExpectedStatusCode(200).post("https://httpbin.org/post");
    }

    @Test
    void testDelete() {
        http.setExpectedStatusCode(200).delete("https://httpbin.org/delete");
    }

    @Test
    void testPatch() {
        http.setExpectedStatusCode(200).patch("https://httpbin.org/patch");
    }

    @Test
    void testPut() {
        http.setExpectedStatusCode(200).put("https://httpbin.org/put");
    }

    @Test
    void testBuildersResetAfterRequest() {
        try {
            http.setExpectedStatusCode(234).setData(1).get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '1'\n\n"));
        }

        try {
            http.setExpectedStatusCode(4353).get("https://google.com.ua");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().endsWith("--data '1'\n\n"));
        }
    }

    @Test
    void testThreadLocalIsSingleContainerPerInstanceNotPerClass() {
        var httpRequest1 = new RequestBuilder();
        var httpRequest2 = new RequestBuilder();

        httpRequest1
                .setData("123")
                .setToken(httpRequest2.get("https://google.com").toString());

        assertEquals(httpRequest1.getData().toString(), "123");
        assertNull(httpRequest2.getData());
    }

    @Test
    void testDataOverriding() {
        var config = new RequestConfig();
        config.setHeaders(List.of(new Header("spec_header", "spec_header_value"),
                new Header("h1", "h1_spec")));
        config.setData("body");

        try {
            http
                    .setConfig(config)
                    .setData(100)
                    .setExpectedStatusCode(100)
                    .setHeader("h1", "h1_value")
                    .setHeader("h2", "h2_value")
                    .setToken("token")
                    .setHeader("Cookie", "cookie")
                    .get("https://google.com");
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("--data '100'"), "Incorrect --data");
            assertTrue(e.getMessage().contains(" -H 'spec_header: spec_header_value' -H 'h1: h1_spec' -H 'h1: h1_value'"
                            + " -H 'h2: h2_value' -H 'Cookie: cookie' -H 'Authorization: token'"),
                    "Incorrect headers");
        }
    }

    @Test
    void testExceptionOnInvalidUrl() {
        try {
            http.get("asd");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("URI with undefined scheme"));
        }
    }

    @Test
    void testPathParams() {
        var r = http.get("http://www.mocky.io/v2/{id}", "5ab8a4952c00005700186093");

        assertTrue(r.toJson().similar(new JSONObject().put("a", 1)));
    }

    @Test
    void testUriTemplate() {
        String a = "http://www.mocky.io/v2/{id}/sss/{sss}";
        var map = Map.<String, Object>of("id", "d d", "sss", 2);

        var str = UriTemplate.fromTemplate(a).expand(map);

        assertEquals(str, "http://www.mocky.io/v2/d%20d/sss/2");
    }

    @Test
    public void testGetHeaders() {
        var r = http.get("http://www.mocky.io/v2/{id}", "5ab8a4952c00005700186093");

        var success = new AtomicBoolean(false);
        r.getHeaders().forEach(h -> {
            if (h.getName().equalsIgnoreCase("content-type")
                    && h.getValue().equalsIgnoreCase("application/json")) {
                success.set(true);
            }
        });

        assertTrue(success.get());
    }

    @Test
    public void testCanGetJsonArray() {
        var r = http.get("http://www.mocky.io/v2/5e864ece3100006c00813977");

        var actual = r.toJsonArray();

        assertTrue(actual.similar(new JSONArray().put(new JSONObject().put("a", 1))));
    }

    @Test
    public void testPrintJsonObject() {
        var r = http.get("http://www.mocky.io/v2/5ab8a4952c00005700186093");

        r.print();
    }

    @Test
    public void testPrintJsonArray() {
        var r = http.get("http://www.mocky.io/v2/5e864ece3100006c00813977");

        r.print();
    }

    @Test
    public void testPrintString() {
        var r = http.get("http://www.mocky.io/v2/5e8653163100000929813989");

        r.print();
    }
}
