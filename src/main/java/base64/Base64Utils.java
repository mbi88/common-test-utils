package base64;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 code/decode.
 */
public final class Base64Utils {

    /**
     * Prohibits object initialization.
     */
    private Base64Utils() {
    }

    /**
     * Encodes bytes to string.
     *
     * @param bytes bytes
     * @return string
     */
    public static String encode(final byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    /**
     * Decodes string to bytes.
     *
     * @param s string
     * @return bytes
     */
    public static byte[] decode(final String s) {
        return Base64.getDecoder().decode(s);
    }
}
