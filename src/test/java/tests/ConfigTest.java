package tests;

import config.Configuration;
import org.json.JSONObject;
import org.testng.annotations.Test;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import java.util.Arrays;

import static org.testng.Assert.*;

public class ConfigTest implements Configuration {

    @Test
    public void testCurrentEnv() {
        assertEquals(getCurrentEnv("https://www.qa1-asdasd.com"), "www.qa1");
        assertEquals(getCurrentEnv("https://qa1-asdasd.com"), "qa1");
        assertEquals(getCurrentEnv("www.qa1-asdasd.com"), "www.qa1");
        assertEquals(getCurrentEnv("qa1asdasd.com"), "qa1asdasd.com");
        assertEquals(getCurrentEnv("qa1asdasd"), "qa1asdasd");
        assertEquals(getCurrentEnv("qa1asdasd"), "qa1asdasd");
    }

    @Test
    public void testReadEnvVar() {
        assertEquals(readVar("test"), "null");
    }

    @Test
    public void testCanReadEnvVar() {
        assertFalse(readVar("HOME").isEmpty());
    }

    @Test
    public void tesGetSSMParameter() {
        try {
            readSsmParameter("ddd");
        } catch (ParameterNotFoundException ignored) {
        }
    }

    @Test
    public void testCanGetApiStatus() {
        var response = getApiStatus("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a");

        assertEquals(new JSONObject(response.asString()).getInt("a"), 1);
    }

    @Test
    public void testCantGetApiStatusIfResourceNotFound() {
        try {
            var r = getApiStatus("http://run.mocky.io/v3/5ab8a4952c00005700");
            assertEquals(r.statusCode(), 404);
        } catch (AssertionError ae) {
            assertTrue(ae.getMessage().contains("expected [[200]] but found [404]"));
        }
    }

    @Test
    public void testCantGetApiStatusIfInvalidUrl() {
        try {
            var r = getApiStatus("http://run.mocky.io/v3/1");
            assertEquals(r.statusCode(), 404);
        } catch (Throwable t) {
            assertEquals(Arrays.stream(t.getSuppressed()).findAny().get()
                    .getMessage(), "API is not available: http://run.mocky.io/v3/1");
        }
    }
}
