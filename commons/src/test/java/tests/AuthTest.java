package tests;

import auth.TokenGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

public class AuthTest {

    private final TokenGenerator generator = new TokenGenerator("secret212312312312312312312312312313123123123123123123", true);

    @Test
    public void testName() {
        System.out.println(generator.generateToken(new JSONObject().put("a", 1)));
        System.out.println(new TokenGenerator("secret212312312312312312312312312313123123123123123123", false).generateToken(new JSONObject().put("a", 1)));
    }

    @Test
    public void testUpdatedClaim1() {
        JSONObject claim = new JSONObject().put("a", 1);
        System.out.println(generator.updateClaim(claim, "a", 2).toString(2));
    }

    @Test
    public void testUpdatedClaim2() {
        JSONObject claim = new JSONObject().put("a", new JSONArray().put(1));
        System.out.println(generator.updateClaim(claim, "a", 2).toString(2));
    }

    @Test
    public void testUpdatedClaim3() {
        JSONObject claim = new JSONObject().put("a", 1);
        System.out.println(generator.updateClaim(claim, "a", new JSONArray().put(2)).toString(2));
    }
    @Test
    public void testUpdatedClaim4() {
        JSONObject claim = new JSONObject().put("a", new JSONArray().put(1));
        System.out.println(generator.updateClaim(claim, "a", new JSONArray().put(2)).toString(2));
    }
}
