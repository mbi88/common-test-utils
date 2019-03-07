import com.mbi.HttpRequest;
import com.mbi.request.RequestBuilder;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

public class HttpRequestTest {

    private HttpRequest http = new RequestBuilder();

    @Test
    public void testSetHeader() {
        try {
            http
                    .setHeader("header1", "v")
                    .setHeader("header2", "v")
                    .setExpectedStatusCode(300)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'header1: v' -H 'header2: v'"));
        }
    }

    @Test
    public void testSetHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("h1", "v"));
        headers.add(new Header("h2", "v"));

        try {
            http
                    .setHeaders(headers)
                    .setExpectedStatusCode(300)
                    .get("http://www.mocky.io/v2/5ab8a4952c00005700186093");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'h2: v'"));
        }
    }

    @Test
    public void testWithoutHeaders() {
        try {
            http
                    .setExpectedStatusCode(230)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://google.com.ua'"));
        }
    }

    @Test
    public void testHeadersWithSpec() {
        RequestSpecification spec = given().header("h1", "v");
        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(342)
                    .setHeader("h2", "v")
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'h2: v'"));
        }
    }

    @Test
    public void testSpecHeaders() {
        RequestSpecification spec = given().header("h1", "v");
        try {
            http
                    .setToken("wer")
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(342)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'h1: v' -H 'Authorization: wer'"));
        }
    }

    @Test
    public void testHeadersOverride() {
        RequestSpecification spec = given().header("Accept", "1");
        try {
            http
                    .setRequestSpecification(spec)
                    .setHeader("Accept", "2")
                    .setExpectedStatusCode(342)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://google.com.ua' -H 'Accept: 2'"));
        }
    }

    @Test
    public void testSetBody() {
        try {
            http
                    .setExpectedStatusCode(342)
                    .setData(1).get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '1'\n\n"));
        }
    }

    @Test
    public void testWithoutBody() {
        try {
            http
                    .setExpectedStatusCode(324)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains(" --data '"));
        }
    }

    @Test
    public void testBodyOverridesSpec() {
        RequestSpecification spec = given().body(1);
        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(234)
                    .setData(2)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().endsWith("--data '2'\n\n"));
        }
    }

    @Test
    public void testSetToken() {
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
    public void testTokenWithSpec() {
        RequestSpecification spec = given().header(new Header("Authorization", "token1"));
        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(24)
                    .setToken("token2")
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token2"));
        }
    }

    @Test
    public void testWithoutToken() {
        try {
            http
                    .setExpectedStatusCode(356)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertFalse(error.getMessage().contains("-H 'Authorization:"));
        }
    }

    @Test
    public void testWithTokenInSpec() {
        RequestSpecification spec = given().header(new Header("Authorization", "token1"));
        try {
            http
                    .setExpectedStatusCode(463)
                    .setRequestSpecification(spec)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("-H 'Authorization: token1"));
        }
    }

    @Test
    public void testCodeFail() {
        boolean passed;
        try {
            http
                    .setExpectedStatusCode(404)
                    .get("https://google.com");
            passed = true;
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("expected [404] but found [200]"));
            passed = false;
        }

        assertFalse(passed);
        http.get("https://google.com");
    }

    @Test
    public void testCodeSuccess() {
        http
                .setExpectedStatusCode(404)
                .get("https://google.com/asdasd");
    }

    @Test
    public void testWithoutCode() {
        Response r = http.get("https://google.com/asdasd");

        assertTrue(r.asString().contains("<html"));
    }

    @Test
    public void testSpec() {
        RequestSpecification spec = given()
                .header(new Header("Authorization", "token1"))
                .body(1);

        try {
            http
                    .setRequestSpecification(spec)
                    .setExpectedStatusCode(234)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains("curl -X GET 'https://google.com.ua' -H 'Authorization: token1' --data '1'"));
        }
    }

    @Test
    public void testWithoutSpec() {
        try {
            http
                    .setExpectedStatusCode(342)
                    .get("https://google.com.ua");
        } catch (AssertionError error) {
            assertTrue(error.getMessage().contains(" curl -X GET 'https://google.com.ua'"));
        }
    }

    @Test
    public void testGet() {
        http.setExpectedStatusCode(200).get("https://httpbin.org/get");
    }

    @Test
    public void testPost() {
        http.setExpectedStatusCode(200).post("https://httpbin.org/post");
    }

    @Test
    public void testDelete() {
        http.setExpectedStatusCode(200).delete("https://httpbin.org/delete");
    }

    @Test
    public void testPatch() {
        http.setExpectedStatusCode(200).patch("https://httpbin.org/patch");
    }

    @Test
    public void testPut() {
        http.setExpectedStatusCode(200).put("https://httpbin.org/put");
    }

    @Test
    public void testBuildersResetAfterRequest() {
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
    public void testThreadLocalIsSingleContainerPerInstanceNotPerClass() {
        RequestBuilder httpRequest1 = new RequestBuilder();
        RequestBuilder httpRequest2 = new RequestBuilder();

        httpRequest1
                .setData("123")
                .setToken(httpRequest2.get("https://google.com").asString());

        assertEquals(httpRequest1.getData().toString(), "123");
        assertNull(httpRequest2.getData());
    }

    @Test
    public void testDataOverriding() {
        RequestSpecification specification = given()
                .header("spec_header", "spec_header_value")
                .header("h1", "h1_spec")
                .body("body");

        try {
            http
                    .setRequestSpecification(specification)
                    .setData(100)
                    .setExpectedStatusCode(100)
                    .setHeader("h1", "h1_value")
                    .setHeader("h2", "h2_value")
                    .setToken("token")
                    .setHeader("Cookie", "cookie")
                    .get("https://google.com");
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("--data '100'"), "Incorrect --data");
            assertTrue(e.getMessage().contains("-H 'spec_header: spec_header_value' -H 'h1: h1_spec' -H 'Authorization:"
                            + " token' -H 'h1: h1_value' -H 'h2: h2_value' -H 'Cookie: cookie'"),
                    "Incorrect headers");
        }
    }

    @Test
    public void testExceptionOnInvalidUrl() {
        try {
            http.get("asd");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Connection refused"));
        }
    }

    @Test
    public void testDebugTrue() {
        RequestBuilder b1 = new RequestBuilder();
        b1
                .debug()
                .setUrl("https://google.com");

        assertTrue(b1.getDebug());
    }

    @Test
    public void testDebugFalse() {
        RequestBuilder b1 = new RequestBuilder();
        b1.debug().get("https://google.com");

        RequestBuilder b2 = new RequestBuilder();
        b2.setUrl("https://google.com");

        assertFalse(b1.getDebug());
        assertFalse(b2.getDebug());
    }

    @Test
    public void testPathParams() {
        Response r = http.get("http://www.mocky.io/v2/{id}", "5ab8a4952c00005700186093");

        assertTrue(r.asString().contains("\"a\": 1"), r.asString());
    }
}
