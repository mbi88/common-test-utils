package tests;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import testcase.BaseTestCase;

import static org.testng.Assert.*;

public class TestCaseTest extends BaseTestCase {

    @Test
    public void testHttpRequest() {
        http.setExpectedStatusCode(200).get("https://api.npoint.io/3a360af4f1419f85f238");
    }

    @Test
    public void testJsonAssert() {
        assertion.jsonEquals(new JSONObject().put("a", 1), new JSONObject().put("a", 1));
    }

    @Test
    public void testJsonValidator() {
        validator.validate(new JSONObject().put("a", 1), new JSONObject().put("a", 1));
    }

    @Test
    public void testDateHandler() {
        assertEquals(dateHandler.getCurrentDate().length(), 10);
    }

    @Test
    public void testRandomNumLength() {
        assertEquals(String.valueOf(getRandomNum(1)).length(), 1);
        assertEquals(String.valueOf(getRandomNum(10)).length(), 10);
        assertEquals(String.valueOf(getRandomNum(5)).length(), 5);
    }

    @Test
    public void testGetRandomNumWithoutCount() {
        assertTrue(getRandomNum() > 1000);
    }

    @Test
    public void testRandomUid() {
        assertEquals(getRandomUID().length(), 36);
    }

    @Test
    public void testToJson() {
        var r = http.get("https://api.npoint.io/3a360af4f1419f85f238");
        toJson(r);
    }

    @Test
    public void testToJsonArray() {
        var r = http.get("https://api.npoint.io/ef1154bf179619bd3d7d");

        toJsonArray(r);
    }

    @Test
    public void testGetResource() {
        assertion.jsonEquals(getResource("/jsons/jo.json"), new JSONObject().put("a", 1));
    }

    @Test
    public void testGetResources() {
        assertion.jsonEquals(getResources("/jsons/ja.json"), new JSONArray().put(new JSONObject().put("a", 1)));
    }

    @Test
    public void testFindJsonInArray() {
        var json = findJsonInArray(getResources("/jsons/ja.json"), "a", 1);

        assertion.jsonEquals(json, new JSONObject().put("a", 1));
    }

    @Test
    public void testCantGetRandomNumWith0Digits() {
        boolean passed;
        try {
            getRandomNum(0);
            passed = true;
        } catch (IllegalArgumentException error) {
            passed = false;
            assertEquals(error.getMessage(), "Value 0 is not in the specified exclusive range of 1 to 18");
        }
        assertFalse(passed);
    }

    @Test
    public void testCantGetRandomNumWithMinus1Digits() {
        boolean passed;
        try {
            getRandomNum(-1);
            passed = true;
        } catch (IllegalArgumentException error) {
            passed = false;
            assertEquals(error.getMessage(), "Value -1 is not in the specified exclusive range of 1 to 18");
        }
        assertFalse(passed);
    }

    @Test
    public void testCantGetRandomNumWith20Digits() {
        boolean passed;
        try {
            getRandomNum(20);
            passed = true;
        } catch (IllegalArgumentException error) {
            passed = false;
            assertEquals(error.getMessage(), "Value 20 is not in the specified exclusive range of 1 to 18");
        }
        assertFalse(passed);
    }

    @Test
    public void testGetRandomWithPositiveCases() {
        getRandomNum(1);
        getRandomNum(5);
        getRandomNum(18);
    }

    @Test
    public void testReplacing0InTheBeginning() {
        for (int i = 0; i < 1000; i++) {
            long num = getRandomNum(4);
            assertEquals(String.valueOf(num).length(), 4, num);
        }
    }

    @Test
    public void testObjectHasUpdateParameter() {
        assertion.jsonEquals(getResource("/jsons/obj_upd_par.json"),
                new JSONObject().put("a", "Hello " + dateHandler.getCurrentDate() + "!"));
    }

    @Test
    public void testArrayHasUpdateParameter() {
        assertion.jsonEquals(getResources("/jsons/array_upd_par.json"),
                new JSONArray().put(new JSONObject().put("a", "Hello " + dateHandler.getCurrentDate() + "!")));
    }
}
