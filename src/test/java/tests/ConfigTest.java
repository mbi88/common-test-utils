package tests;

import com.amazonaws.SdkClientException;
import config.Configuration;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
        assertTrue(readVar("HOME").length() > 0);
    }

    @Test
    public void tesGetSSMParameter() {
        try {
            readSsmParameter("ddd");
        } catch (SdkClientException ignored) {
        }
    }

    @Test
    public void tesGetSSMParameters() {
        try {
            readSsmParameters("ddd", "ddda");
        } catch (SdkClientException ignored) {
        }
    }

    @Test
    public void testCanGetApiStatus() {
        var response = getApiStatus("http://www.mocky.io/v2/5ab8a4952c00005700186093");

        assertEquals(new JSONObject(response.asString()).getInt("a"), 1);
    }

    @Test
    public void testCantGetApiStatusIfResourceNotFound() {
        try {
            var r = getApiStatus("http://www.mocky.io/v2/5ab8a4952c00005700");
            assertEquals(r.statusCode(), 404);
        } catch (AssertionError ae) {
            assertTrue(ae.getMessage().contains("expected [[200]] but found [404]"));
        }
    }

    @Test
    public void testCantGetApiStatusIfInvalidUrl() {
        try {
            var r = getApiStatus("mocky");
            assertEquals(r.statusCode(), 404);
        } catch (Throwable t) {
            assertEquals(Arrays.stream(t.getSuppressed()).findAny().get().getMessage(), "API is not available: mocky");
        }
    }
}
