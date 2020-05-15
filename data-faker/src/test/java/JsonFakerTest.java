import com.mbi.Faker;
import com.mbi.JsonFaker;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class JsonFakerTest {

    private final Faker jsonFaker = new JsonFaker();
    private final JSONObject json = jsonFaker.fakeData(new JSONObject().put("f", "{$caller}"));

    @Test
    public void testCanFakeData() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var json = new JSONObject();
        json.put("a", "aaa {$caller} ccc {$uid} eee");
        json.put("b", new JSONObject().put("c", 2));
        json.put("d", "{$current_date} {$current_date}");
        json.put("e", new JSONObject().put("f", "{$current_date} {$current_date}"));
        json.put("k", new JSONArray().put(new JSONObject().put("f", "{$current_date} {$CURRENT_DATE}")));

        var result = jsonFaker.fakeData(json);

        assertFalse(result.toString().contains("{$"));
        assertTrue(result.getString("a").startsWith("aaa JsonFakerTest.testCanFakeData ccc "));
        result.getJSONObject("b").similar(new JSONObject().put("c", 2));
        assertEquals(result.getString("d"), date + " " + date);
        result.getJSONObject("e").similar(new JSONObject().put("f", date + " " + date));
        result.getJSONArray("k").getJSONObject(0).similar(new JSONObject().put("f", date + " " + date));
    }

    @Test
    public void testFakeWithIncorrectJson() {
        boolean passed;
        try {
            jsonFaker.fakeData("123");
            passed = true;
        } catch (JSONException ex) {
            passed = false;
            assertEquals(ex.getMessage(), "Incorrect json: 123");
        }
        assertFalse(passed);
    }

    @Test
    public void testFakeWithUpperCaseParameter() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var json = new JSONObject();
        json.put("a", "aaa {$CURRENT_DATE} ccc");

        var result = jsonFaker.fakeData(json);

        var dt = result.getString("a");
        assertEquals(dt, "aaa " + date + " ccc");
    }

    @Test
    public void testFakeWithLowerCaseParameter() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var json = new JSONObject();
        json.put("a", "aaa {$current_date} ccc");

        var result = jsonFaker.fakeData(json);

        var dt = result.getString("a");
        assertEquals(dt, "aaa " + date + " ccc");
    }

    @Test
    public void testFakeWithCamelCaseParameter() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var json = new JSONObject();
        json.put("a", "aaa {$Current_Date} ccc");

        var result = jsonFaker.fakeData(json);

        var dt = result.getString("a");
        assertEquals(dt, "aaa " + date + " ccc");
    }

    @Test
    public void testSourceObjectNotUpdated() {
        var json1 = new JSONObject();
        json1.put("a", "aaa {$current_date} ccc {$current_date} eee");
        var json2 = new JSONObject(json1.toString());

        jsonFaker.fakeData(json1);

        json1.similar(json2);
    }

    @Test
    public void testSourceArrayNotUpdated() {
        var json1 = new JSONArray();
        json1.put(new JSONObject().put("a", "aaa {$current_date} ccc {$current_date} eee"));
        var json2 = new JSONArray(json1.toString());

        jsonFaker.fakeData(json1);

        json1.similar(json2);
    }

    @Test
    public void testInnerJsonObject() {
        var json = new JSONObject();
        json.put("e", new JSONObject().put("f", "{$current_date} {$current_date}"));
        json.put("g", new JSONObject().put("j", new JSONObject().put("f", "{$current_date} {$current_date}")));
        var j = new JSONObject();
        j.put("dd", json);
        var jj = new JSONObject();
        jj.put("ddd", j);

        var result = jsonFaker.fakeData(jj);

        assertFalse(result.toString().contains("{$"));
    }

    @Test
    public void testInnerJsonArray() {
        var json = new JSONObject();
        json.put("k", new JSONArray().put(new JSONObject().put("f", "{$current_date} {$current_date}")));

        var result = jsonFaker.fakeData(json);

        assertFalse(result.toString().contains("{$"));
    }

    @Test
    public void testJsonArray() {
        var json1 = new JSONObject();
        json1.put("e", new JSONObject().put("f", "{$current_date} {$current_date}"));
        json1.put("g", new JSONObject().put("j", new JSONObject().put("f", "{$current_date} {$current_date}")));
        var json2 = new JSONObject();
        json2.put("k", new JSONArray().put(new JSONObject().put("f", "{$current_date} {$current_date}")));
        var jsonArray = new JSONArray();
        jsonArray.put(json1).put(json2);

        var result = jsonFaker.fakeData(jsonArray);

        assertFalse(result.toString().contains("{$"));
    }

    @Test
    public void testFakeUid() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$uid}"));

        var result = jsonFaker.fakeData(json);

        var uid = result.getJSONObject("b").getString("c");
        assertTrue(uid.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"));
    }

    @Test
    public void testFakeCaller() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$caller}"));

        var result = jsonFaker.fakeData(json);

        var caller = result.getJSONObject("b").getString("c");
        assertEquals(caller, "JsonFakerTest.testFakeCaller");
    }

    @Test
    public void testCallerFakerOnClassInitialization() {
        jsonFaker.fakeData(json).similar(new JSONObject().put("f", "JsonFakerTest.<init"));
    }

    @Test
    public void testFakeDate() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$date}"));

        var result = jsonFaker.fakeData(json);

        var dt = result.getJSONObject("b").getString("c");
        assertEquals(dt, date);
    }

    @Test
    public void testFakeDateTime() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").print(new DateTime());
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$datetime}"));

        var result = jsonFaker.fakeData(json);

        var dt = result.getJSONObject("b").getString("c");
        assertTrue(dt.contains(date.substring(0, 15)), date);
    }

    @Test
    public void testFakeCurrentDate() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$current_date}"));

        var result = jsonFaker.fakeData(json);

        var dt = result.getJSONObject("b").getString("c");
        assertEquals(dt, date);
    }

    @Test
    public void testFakeCurrentDateTime() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").print(new DateTime());
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$current_datetime}"));

        var result = jsonFaker.fakeData(json);

        var dt = result.getJSONObject("b").getString("c");
        assertTrue(dt.contains(date.substring(0, 15)), date);
    }

    @Test
    public void testFakeWithUnsupportedParameter() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$incorrect}"));

        boolean passed;
        try {
            jsonFaker.fakeData(json);
            passed = true;
        } catch (IllegalArgumentException ex) {
            passed = false;
            assertEquals(ex.getMessage(), "No enum constant com.mbi.parameters.SupportedParameters.INCORRECT");
        }
        assertFalse(passed);
    }

    @Test
    public void testFakeWithInvalidParameter() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$INVALID}"));

        boolean passed;
        try {
            jsonFaker.fakeData(json);
            passed = true;
        } catch (IllegalArgumentException ex) {
            passed = false;
            assertEquals(ex.getMessage(), "Unsupported parameter: INVALID");
        }
        assertFalse(passed);
    }

    @Test
    public void testFakeArrayOfStrings() {
        var jsonArray = new JSONArray().put("string");

        var result = jsonFaker.fakeData(jsonArray);

        result.similar(jsonArray);
    }

    @Test
    public void testFakeArrayInArray() {
        var jsonArray1 = new JSONArray();
        jsonArray1.put(new JSONArray().put(new JSONObject().put("a", "{$uid}")));

        var result = jsonFaker.fakeData(jsonArray1);

        assertFalse(result.toString().contains("{$"));
        String uid = result.getJSONArray(0).getJSONObject(0).getString("a");
        assertTrue(uid.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"));
    }

    @Test
    public void testIntIsNotConvertedToString() {
        var jsonObject = new JSONObject();
        jsonObject.put("a", 1);

        assertEquals(jsonFaker.fakeData(jsonObject).toString(), jsonObject.toString());
    }

    @Test
    public void testFakeNumberWithoutArgument() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$number}"));

        var result = jsonFaker.fakeData(json);
        assertEquals(String.valueOf(result.getJSONObject("b").getLong("c")).length(), 13);
    }

    @Test
    public void testFakeNumberWithOneArgument() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$number;2}"));

        var result = new JSONObject();
        for (int i = 0; i < 500; i++) {
            result = jsonFaker.fakeData(json);
        }
        assertEquals(String.valueOf(result.getJSONObject("b").getInt("c")).length(), 2);
    }

    @Test
    public void testFakeNumberWithTwoArguments() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$number;2;second}"));

        var result = jsonFaker.fakeData(json);
        assertEquals(String.valueOf(result.getJSONObject("b").getInt("c")).length(), 2);
    }

    @Test
    public void testFakeNumberWithInvalidArgument() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$number;e}"));

        boolean passed;
        try {
            jsonFaker.fakeData(json);
            passed = true;
        } catch (IllegalArgumentException e) {
            passed = false;
            assertEquals(e.getMessage(), "Unexpected digits count! Expected int value: {$number;e}");
        }
        assertFalse(passed);
    }

    @Test
    public void testFakeNumberWithCountMoreThanAccepted() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$number;140}"));

        boolean passed;
        try {
            jsonFaker.fakeData(json);
            passed = true;
        } catch (IllegalArgumentException e) {
            passed = false;
            assertEquals(e.getMessage(), "Value 140 is not in the specified exclusive range of 1 to 18");
        }
        assertFalse(passed);
    }

    @Test
    public void testFakeNumberWithCountLessThanAccepted() {
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$number;0}"));

        boolean passed;
        try {
            jsonFaker.fakeData(json);
            passed = true;
        } catch (IllegalArgumentException e) {
            passed = false;
            assertEquals(e.getMessage(), "Value 0 is not in the specified exclusive range of 1 to 18");
        }
        assertFalse(passed);
    }

    @Test
    public void testNumberFakerWithSeveralParameters() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var json = new JSONObject();
        json.put("b", new JSONObject().put("c", "{$date} {$number;2}"));

        var result = jsonFaker.fakeData(json);

        assertTrue(result.getJSONObject("b").getString("c").startsWith(date));
        assertEquals(result.getJSONObject("b").getString("c").split(" ")[1].length(), 2);
    }

    @Test
    public void testFakerWithStringInJsonArray() {
        var date = DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime());
        var jsonArray = new JSONArray().put("{$date}");

        assertEquals(jsonFaker.fakeData(jsonArray).getString(0), date);
    }
}
