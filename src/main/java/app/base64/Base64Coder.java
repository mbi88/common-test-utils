package app.base64;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by mbi on 7/14/16.
 */
public class Base64Coder {

    public static String encode(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    public static byte[] decode(String s) {
        return Base64.getDecoder().decode(s);
    }
}
