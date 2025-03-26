package tests;

import config.Configuration;
import org.json.JSONObject;
import org.testng.annotations.Test;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import java.util.Arrays;

import static org.testng.Assert.*;

public class ConfigTest implements Configuration {

    @Test
    public void testCurrentEnv() {
        assertEquals(getCurrentEnv("https://www.qa1-asdasd.com"), "www.qa1");
        assertEquals(getCurrentEnv("https://qa1-asdasd.com"), "qa1");
        assertEquals(getCurrentEnv("www.qa1-asdasd.com"), "www.qa1");
        assertEquals(getCurrentEnv("qa1.domain.com"), "qa1.domain.com");
        assertEquals(getCurrentEnv("www.qa1-asdasd"), "www.qa1");
        assertEquals(getCurrentEnv("http://qa1-asdasd"), "qa1");
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
        } catch (ParameterNotFoundException | SdkClientException ignored) {
        }
    }

    @Test
    public void testCanGetApiStatus() {
        var response = getApiStatus("https://api.npoint.io/3a360af4f1419f85f238");

        assertEquals(new JSONObject(response.asString()).getInt("a"), 1);
    }

    @Test
    public void testCantGetApiStatusIfResourceNotFound() {
        var ex = expectThrows(IllegalStateException.class, () -> getApiStatus("http://run.mocky.io/v3/5ab8a4952c00005700"));
        assertTrue(ex.getMessage().contains("API is not available: http://run.mocky.io/v3/5ab8a4952c00005700"));
    }

    @Test
    public void testCantGetApiStatusIfInvalidUrl() {
        var ex = expectThrows(IllegalStateException.class, () -> getApiStatus("http://run.mocky"));
        assertTrue(ex.getMessage().contains("API is not available: http://run.mocky"));
    }
}
