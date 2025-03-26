package encoding;

import java.util.Base64;

/**
 * Utility for Base64 encoding and decoding.
 */
public final class Base64Utils {

    /**
     * Prohibits object initialization.
     */
    private Base64Utils() {
    }

    /**
     * Encodes byte array to Base64 string using UTF-8 charset.
     *
     * @param bytes byte array to encode
     * @return Base64-encoded string
     */
    public static String encode(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decodes Base64-encoded string to byte array.
     *
     * @param string Base64 string
     * @return decoded byte array
     */
    public static byte[] decode(final String string) {
        return Base64.getDecoder().decode(string);
    }
}
