package tests;

import com.amazonaws.SdkClientException;
import config.Configuration;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

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
    public void tesGetSSMParameter() {
        try {
            readSsmParameter("ddd");
        } catch (SdkClientException e) {

        }
    }

    @Test
    public void tesGetSSMParameters() {
        try {
            readSsmParameters("ddd", "ddda");
        } catch (SdkClientException e) {

        }
    }
}
