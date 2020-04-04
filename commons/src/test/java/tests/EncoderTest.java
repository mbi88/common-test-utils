package tests;

import encoding.Base64Utils;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;

public class EncoderTest {

    @Test
    public void testEncoder() {
        byte[] bytes = new byte[1];

        assertEquals(Base64Utils.encode(bytes), "AA==");
    }

    @Test
    public void testDecoder() {
        assertEquals(Arrays.toString(Base64Utils.decode("11")), "[-41]");
    }
}
