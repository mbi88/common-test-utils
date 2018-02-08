package app.auth;

import org.json.JSONArray;
import org.json.JSONObject;

import static app.serializer.JsonDeserializer.getJsonFromFile;

public class Tokens extends TokenGenerator {

    // Token allowed to do all operations
    public static final String DEFAULT_TOKEN = generateToken(getJsonFromFile("/claims/default.json"));
    public static final String INVALID_TOKEN = generateToken(getJsonFromFile("/claims/invalid.json"));

    /**
     * @param claims Claims
     * @return JWT token
     */
    public static String generateToken(JSONObject claims) {
        return buildToken(claims);
    }

    /**
     * @param json  claims as json object that will be updated
     * @param key   key to be updated
     * @param value value to be set
     * @return updated claims
     */
    public static JSONObject updateClaim(JSONObject json, String key, Object value) {
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
}
