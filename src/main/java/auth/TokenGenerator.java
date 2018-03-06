package auth;

import base64.Base64Coder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Generates tokens.
 */
public final class TokenGenerator {

    private final String secret;
    private final boolean encoded;

    public TokenGenerator(final String secret, final boolean encoded) {
        this.secret = secret;
        this.encoded = encoded;
    }

    /**
     * @param claims Claims
     * @return JWT token
     */
    public String generateToken(JSONObject claims) {
        return buildToken(claims, secret, encoded);
    }

    /**
     * @param json  claims as json object that will be updated
     * @param key   key to be updated
     * @param value value to be set
     * @return updated claims
     */
    public JSONObject updateClaim(JSONObject json, String key, Object value) {
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

    private Key getKey(String scr, boolean encoded) {
        byte[] secret = encoded ? Base64Coder.decode(scr) : scr.getBytes();

        return new SecretKeySpec(secret, SignatureAlgorithm.HS256.getJcaName());
    }

    private String buildToken(JSONObject claims, String secret, boolean encoded) {
        return "Bearer " + Jwts.builder()
                .setClaims(claims.toMap())
                .signWith(SignatureAlgorithm.HS256, getKey(secret, encoded))
                .setExpiration(new DateTime(DateTimeZone.UTC).plusHours(9).toDate())
                .compact();
    }
}
