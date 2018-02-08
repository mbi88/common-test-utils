package app.auth;

import app.base64.Base64Coder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import static app.Constants.AUTH_SECRET_KEY;
import static app.Constants.IS_BASE64_ENCODED;

class TokenGenerator {

    private static Key getKey() {
        byte[] secret = IS_BASE64_ENCODED ? Base64Coder.decode(AUTH_SECRET_KEY) : AUTH_SECRET_KEY.getBytes();

        return new SecretKeySpec(secret, SignatureAlgorithm.HS256.getJcaName());
    }

    static String buildToken(JSONObject claims) {
        return "Bearer " + Jwts.builder()
                .setClaims(claims.toMap())
                .signWith(SignatureAlgorithm.HS256, getKey())
                .setExpiration(new DateTime(DateTimeZone.UTC).plusHours(9).toDate())
                .compact();
    }
}
