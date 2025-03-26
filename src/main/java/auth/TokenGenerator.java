package auth;

import encoding.Base64Utils;
import io.jsonwebtoken.Jwts;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

/**
 * Utility for generating JWT tokens with custom claims.
 */
public final class TokenGenerator {

    /**
     * How many hours token will be alive.
     */
    private static final int TTL = 9;

    /**
     * Secret for token generation.
     */
    private final String secret;

    /**
     * If the secret is base64 encoded.
     */
    private final boolean encoded;

    /**
     * Creates a new token generator.
     *
     * @param secret  the secret for signing the token
     * @param encoded whether the secret is base64 encoded
     * @throws NullPointerException if any argument is null
     */
    public TokenGenerator(final String secret, final Boolean encoded) {
        this.secret = Objects.requireNonNull(secret, "'secret' must not be null");
        this.encoded = Objects.requireNonNull(encoded, "'encoded' must not be null");
    }

    /**
     * Generates a signed JWT token with the provided claims.
     *
     * @param claims the claims to embed in the token
     * @return a signed token with Bearer prefix
     */
    public String generateToken(final JSONObject claims) {
        return "Bearer " + Jwts.builder()
                .claims(claims.toMap())
                .signWith(getKey())
                .expiration(expirationDate())
                .compact();
    }

    /**
     * Updates the provided claim in a JSON object.
     * <p>
     * Automatically wraps the value in JSONArray if the current field is an array.
     *
     * @param json  the claims object
     * @param key   the key to update
     * @param value the value to set
     * @return the updated claims object
     */
    public JSONObject updateClaim(final JSONObject json, final String key, final Object value) {
        // In case if I forget that the key accepts JSONArray instead of object or string
        final var current = json.get(key);
        if (current instanceof JSONArray && !(value instanceof JSONArray)) {
            return json.put(key, new JSONArray().put(value));
        }
        return json.put(key, value);
    }

    /**
     * Returns signed key. Decoded if the secret is base64 encoded.
     *
     * @return key.
     */
    private Key getKey() {
        final byte[] secretBytes = encoded ? Base64Utils.decode(secret) : secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    private Date expirationDate() {
        return new DateTime(DateTimeZone.UTC).plusHours(TTL).toDate();
    }
}
