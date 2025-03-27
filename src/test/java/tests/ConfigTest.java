package tests;

import config.Configuration;
import org.json.JSONObject;
import org.testng.annotations.Test;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
        var ex = expectThrows(Throwable.class, () -> readSsmParameter("ddd"));
        assertTrue(ex instanceof ParameterNotFoundException || ex instanceof SdkClientException);
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

    @Test
    void testReadSsmParameter() {
        var mockClient = mock(SsmClient.class);
        var mockParam = Parameter.builder().value("mocked-value").build();
        var mockResponse = GetParameterResponse.builder().parameter(mockParam).build();
        when(mockClient.getParameter(any(GetParameterRequest.class))).thenReturn(mockResponse);

        // Anonymously implement Configuration with ssmClient() provided
        Configuration config = new Configuration() {
            @Override
            public SsmClient ssmClient() {
                return mockClient;
            }
        };

        String value = config.readSsmParameter("dummy");
        assertEquals(value, "mocked-value");
    }
}
