package auth;

import base64.Base64Utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;

/**
 * Generates tokens.
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
     * Creates a token generator objects.
     *
     * @param secret  secret for token generation.
     * @param encoded if the secret is base64 encoded.
     * @throws AssertionError if secret/encoded is null.
     */
    public TokenGenerator(final String secret, final boolean encoded) {
        assert !Objects.isNull(secret) : "'Secret' can't be null. Set appropriate environment variable";
        assert !Objects.isNull(encoded) : "'Encoded' can't be null. Set appropriate environment variable";

        this.secret = secret;
        this.encoded = encoded;
    }

    /**
     * @param claims Claims
     * @return JWT token
     */
    public String generateToken(final JSONObject claims) {
        return buildToken(claims, secret, encoded);
    }

    /**
     * @param json  claims as json object that will be updated
     * @param key   key to be updated
     * @param value value to be set
     * @return updated claims
     */
    public JSONObject updateClaim(final JSONObject json, final String key, final Object value) {
        // In case if I forget that the key accepts JSONArray instead of object or string
        Object object = json.get(key);
        if (object instanceof JSONArray && !(value instanceof JSONArray)) {
            JSONArray putJson = new JSONArray();
            putJson.put(value);
            json.put(key, putJson);

            return json;
        }

        return json.put(key, value);
    }

    /**
     * Returns signed key. Decoded if the secret is base64 encoded.
     *
     * @param scr     secret for token generation.
     * @param encoded if the secret is base64 encoded.
     * @return key.
     */
    private Key getKey(final String scr, final boolean encoded) {
        byte[] secretBytes = encoded ? Base64Utils.decode(scr) : scr.getBytes();

        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Token builder.
     *
     * @param claims  claims.
     * @param secret  secret for token generation.
     * @param encoded if the secret is base64 encoded.
     * @return string token in format 'Bearer ...'.
     */
    private String buildToken(final JSONObject claims, final String secret, final boolean encoded) {
        return "Bearer " + Jwts.builder()
                .setClaims(claims.toMap())
                .signWith(SignatureAlgorithm.HS256, getKey(secret, encoded))
                .setExpiration(new DateTime(DateTimeZone.UTC).plusHours(TTL).toDate())
                .compact();
    }
}
