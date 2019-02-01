package tests;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import testcase.BaseTestCase;

import static org.testng.Assert.*;

public class TestCaseTest extends BaseTestCase {

    @Test
    public void testHttpRequest() {
        http.setExpectedStatusCode(200).get("http://www.mocky.io/v2/5ab8a4952c00005700186093");
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
        Response r = http.get("http://www.mocky.io/v2/5ab8a4952c00005700186093");
        toJson(r);
    }

    @Test
    public void testToJsonArray() {
        Response r = http.get("http://www.mocky.io/v2/5ab8a4fa2c00005400186097");
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
        JSONObject json = findJsonInArray(getResources("/jsons/ja.json"), "a", 1);

        assertion.jsonEquals(json, new JSONObject().put("a", 1));
    }

    @Test
    public void testGetRandomWithNegativeCases() {
        boolean passed;
        try {
            getRandomNum(14);
            getRandomNum(0);
            getRandomNum(-1);
            getRandomNum(15);
            passed = true;
        } catch (IllegalArgumentException error) {
            passed = false;
            assertTrue(error.getMessage().contains("is not in the specified exclusive range"),
                    error.getMessage());
        }
        assertFalse(passed);
    }

    @Test
    public void testGetRandomWithPositiveCases() {
        getRandomNum(1);
        getRandomNum(5);
        getRandomNum(13);
    }

    @Test
    public void testCheckErrorMessageOnIncorrectRandomCount() {
        try {
            getRandomNum(14);
        } catch (IllegalArgumentException error) {
            assertEquals(error.getMessage(), "Value 14 is not in the specified exclusive range of 1 to 13");
        }
    }

    @Test
    public void testReplacing0InTheBeginning() {
        for (int i = 0; i < 1000; i++) {
            assertEquals(String.valueOf(getRandomNum(4)).length(), 4);
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
