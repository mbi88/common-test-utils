package tests;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;
import static serializer.JsonDeserializer.*;

public class SerializerTest {

    @Test
    public void testWithInvalidResourcePath() {
        var ex = expectThrows(NullPointerException.class, () -> getResource("asd"));
        assertTrue(ex.getMessage().contains("Can't find a file: asd"));
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

    @Test
    public void testUpdateSameFields() {
        JSONObject json = getResource("/jsons/update_test.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.put("a", "updated");
        expected.getJSONObject("c").put("a", "updated");

        assertTrue(updateJson(json, Map.of("a", "updated", "c.a", "updated")).similar(expected));
    }

    @Test
    public void testUpdateParent() {
        JSONObject json = getResource("/jsons/update_test.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.put("c", "updated");

        assertTrue(updateJson(json, Map.of("c", "updated")).similar(expected));
    }

    @Test
    public void testUpdateChildOfParent() {
        JSONObject json = getResource("/jsons/update_test.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.getJSONObject("c").put("bb", "updated");

        assertTrue(updateJson(json, Map.of("c.bb", "updated")).similar(expected));
    }

    @Test
    public void testUpdateChild() {
        JSONObject json = getResource("/jsons/update_test.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.getJSONObject("c").getJSONObject("bb").put("q", "updated");

        assertTrue(updateJson(json, Map.of("c.bb.q", "updated")).similar(expected));
    }

    @Test
    public void testUpdateSeveralValues() {
        JSONObject json = getResource("/jsons/update_test.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.getJSONObject("c").getJSONObject("bb").put("q", "updated");
        expected.getJSONObject("c").getJSONObject("bb").put("w", "updated");
        expected.getJSONObject("c").put("a", "updated");
        expected.put("a", "updated");

        assertTrue(updateJson(json, Map.of("c.bb.q", "updated", "c.bb.w", "updated", "c.a", "updated", "a", "updated")).similar(expected));
    }

    @Test
    public void testJsonNotUpdatedIfFieldNotFound() {
        JSONObject json = getResource("/jsons/update_test.json");

        assertTrue(updateJson(json, Map.of("aaaa", "updated")).similar(json));
    }

    @Test
    public void testUpdateParentInArray() {
        JSONObject json = getResource("/jsons/update_test_array.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.put("c", "updated");

        assertTrue(updateJson(json, Map.of("c", "updated")).similar(expected));
    }

    @Test
    public void testUpdateChildOfParentInArray() {
        JSONObject json = getResource("/jsons/update_test_array.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.getJSONArray("c").getJSONObject(0).put("bb", "updated");

        assertTrue(updateJson(json, Map.of("c[0].bb", "updated")).similar(expected));
    }

    @Test
    public void testUpdateChildInArray() {
        JSONObject json = getResource("/jsons/update_test_array.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.getJSONArray("c").getJSONObject(0).getJSONObject("bb").put("q", "updated");

        assertTrue(updateJson(json, Map.of("c[0].bb.q", "updated")).similar(expected));
    }

    @Test
    public void testUpdateSameFieldsIncludeArray() {
        JSONObject json = getResource("/jsons/update_test_array.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.put("a", "updated");
        expected.getJSONArray("c").getJSONObject(0).put("a", "updated");

        assertTrue(updateJson(json, Map.of("a", "updated", "c[0].a", "updated")).similar(expected));
    }

    @Test
    public void testUpdateRootElementAndChild() {
        JSONObject json = getResource("/jsons/update_test.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.put("a", "updated");
        expected.put("c", "new");

        assertTrue(updateJson(json, Map.of("a", "updated", "q", "new 2", "c", "new")).similar(expected));
    }

    @Test
    public void testUpdateRootElementAndChildInArray() {
        JSONObject json = getResource("/jsons/update_test_array.json");
        JSONObject expected = new JSONObject(json.toString());
        expected.put("a", "updated");
        expected.put("c", "new");

        assertTrue(updateJson(json, Map.of("a", "updated", "q", "new 2", "c", "new")).similar(expected));
    }

    @Test
    public void testUpdateFieldInJsonObject() {
        var json = new JSONObject();
        json.put("a", 1);
        json.put("b", 2);

        var r = updateJson(json, "a", 3);

        assertTrue(r.similar(json.put("a", 3)));
    }

    @Test
    public void testUpdateSkippedIfNoField() {
        var json = new JSONObject();
        json.put("a", 1);
        json.put("b", 2);

        var r = updateJson(json, "aa", 3);

        assertTrue(r.similar(json));
    }

    @Test
    public void testUpdateFieldInInnerJsonObject() {
        var json = new JSONObject();
        json.put("c", 1);
        json.put("b", new JSONObject().put("c", 1).put("d", 1));

        var r = updateJson(json, "b.c", 3);

        assertTrue(r.similar(json.put("b", new JSONObject().put("c", 3).put("d", 1))));
    }

    @Test
    public void testUpdateFieldInInnerJsonArray() {
        var json = new JSONObject();
        json.put("c", 1);
        json.put("b", new JSONArray().put(new JSONObject().put("c", 1)).put(new JSONObject().put("c", 2)));

        var r = updateJson(json, "b[1].c", 3);

        assertTrue(r.similar(json.put("b", new JSONArray().put(new JSONObject().put("c", 1)).put(new JSONObject().put("c", 3)))));
    }

    @Test
    public void testUpdateObjectFieldInJsonArray() {
        var json = new JSONArray();
        json.put(new JSONObject().put("c", 1));
        json.put(new JSONObject().put("c", 2));

        var r = updateJson(json, "[1].c", 3);

        assertTrue(r.similar(new JSONArray().put(new JSONObject().put("c", 1)).put(new JSONObject().put("c", 3))));
    }

    @Test
    public void testUpdateObjectFieldInJsonArrayIfNoField() {
        var json = new JSONArray();
        json.put(new JSONObject().put("c", 1));
        json.put(new JSONObject().put("c", 2));

        var r = updateJson(json, "[1].a", 3);

        assertTrue(r.similar(json));
    }
}
