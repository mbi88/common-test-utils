package tests;

import auth.TokenGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class AuthTest {

    private final TokenGenerator generator = new TokenGenerator("Secret", true);

    @Test
    public void testCanRegenerateToken() {
        var json = new JSONObject().put("a", 1);

        assertNotEquals(generator.generateToken(json), new TokenGenerator("secret", false).generateToken(json));
    }

    @Test
    public void testCanUpdateObjectByNumber() {
        var claim = new JSONObject().put("a", 1);
        var r = generator.updateClaim(claim, "a", 2);

        assertTrue(r.similar(new JSONObject("""
                {"a": 2}""")));
    }

    @Test
    public void testCanUpdateArrayByNumber() {
        var claim = new JSONObject().put("a", new JSONArray().put(1));
        var r = generator.updateClaim(claim, "a", 2);

        assertTrue(r.similar(new JSONObject("""
                {"a": [2]}""")));
    }

    @Test
    public void testCanUpdateObject() {
        var claim = new JSONObject().put("a", 1);
        var r = generator.updateClaim(claim, "a", new JSONArray().put(2));

        assertTrue(r.similar(new JSONObject("""
                {"a": [2]}""")));
    }

    @Test
    public void testCanUpdateArray() {
        var claim = new JSONObject().put("a", new JSONArray().put(1));
        var r = generator.updateClaim(claim, "a", new JSONArray().put(2));

        assertTrue(r.similar(new JSONObject("""
                {"a": [2]}""")));
    }
}
