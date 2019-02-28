package tests;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static serializer.JsonDeserializer.*;

public class SerializerTest {

    @Test
    public void testWithInvalidResourcePath() {
        try {
            getResource("asd");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "Can't find a file: asd");
        }
    }

    @Test
    public void testFieldFoundButObjectsNotEquals() {
        JSONArray json = new JSONArray();
        json.put(new JSONObject().put("a", new JSONObject().put("b", 1)));

        findJsonInArray(json, "a", new JSONObject().put("b", 2)).similar(new JSONObject());
    }

    @Test
    public void testNoJsonObjects() {
        JSONArray json = new JSONArray();
        json.put(new JSONObject().put("a", new JSONArray("[1]")));

        findJsonInArray(json, "a", "[1]").similar(new JSONObject());
    }

    @Test
    public void testPositiveCase() {
        JSONArray jsonArray = new JSONArray();
        JSONObject putJson1 = new JSONObject().put("a", new JSONObject().put("b", 1));
        JSONObject putJson2 = new JSONObject().put("b", new JSONObject().put("c", 1));
        jsonArray.put(putJson1).put(putJson2);

        findJsonInArray(jsonArray, "a", putJson1.getJSONObject("a")).similar(putJson1.getJSONObject("a"));
    }

    @Test
    public void testFieldNotFound() {
        JSONArray jsonArray = new JSONArray();
        JSONObject putJson1 = new JSONObject().put("a", new JSONObject().put("b", 1));

        jsonArray.put(putJson1);

        findJsonInArray(jsonArray, "b", putJson1.getJSONObject("a")).similar(new JSONObject());
    }

    @Test
    public void testGetResourceFromFile() {
        getResource("/jsons/jo.json").similar(new JSONObject().put("a", 1));
    }

    @Test
    public void testGetResourcesFromFile() {
        getResources("/jsons/ja.json").similar(new JSONArray().put(new JSONObject().put("a", 1)));
    }

    @Test
    public void testValuesOverriding() {
        JSONObject json = updateJson(getResource("/jsons/override_test.json"),
                Map.of("field1", 111, "field4", "new-value"));

        json.similar(getResource("/jsons/override_result.json"));
        assertFalse(json.similar(getResource("/jsons/override_test.json")));
    }

    @Test
    public void testValuesNotOverriddenIfKeyNotFound() {
        JSONObject json = updateJson(getResource("/jsons/jo.json"), Map.of("field1", 111));

        json.similar(getResource("/jsons/jo.json"));
    }
}
