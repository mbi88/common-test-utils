package serializer;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import com.mbi.Faker;
import com.mbi.JsonFaker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Read content from src/main/resources/ file and map to org.json.JSONObject/JSONArray.
 * Deserializes and manipulates JSON content from resource files.
 */
public final class JsonDeserializer {

    /**
     * Fields separator in flattened json.
     */
    private static final String FIELDS_SEPARATOR = ".";
    private static final Faker FAKER = new JsonFaker();
    private static final Logger LOGGER = Logger.getLogger(JsonDeserializer.class.getCanonicalName());

    /**
     * Checks if field is present in flattened json.
     */
    private static final BiPredicate<String, Set<String>> TEST_KEY_IS_PARENT = (parentKey, children) ->
            children.stream().anyMatch(child ->
                    child.startsWith(parentKey + FIELDS_SEPARATOR)
                            || child.equalsIgnoreCase(parentKey)
                            || child.startsWith(parentKey + "["));

    /**
     * Returns children of parent from set.
     */
    private static final BiFunction<Set<String>, String, List<String>> GET_CHILDREN = (fields, parent) ->
            fields.stream()
                    .filter(key -> key.startsWith(parent))
                    .collect(Collectors.toList());

    /**
     * Prohibits object initialization.
     */
    private JsonDeserializer() {
    }

    /**
     * Loads a JSON object from a resource file and injects fake data where needed.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the path -
     *             it is already implemented in the method
     * @return JSONObject with optional fake data
     */
    public static JSONObject getResource(final String path) {
        return FAKER.fakeData(new JSONObject(readStringFromFile(path)));
    }

    /**
     * Loads a JSON array from a resource file and injects fake data where needed.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the path -
     *             it is already implemented in the method
     * @return JSONArray with optional fake data
     */
    public static JSONArray getResources(final String path) {
        return FAKER.fakeData(new JSONArray(readStringFromFile(path)));
    }

    /**
     * Get json object from json array by object key and value.
     *
     * @param sourceArray sourceArray
     * @param name        field name of wanted json object
     * @param value       field value of wanted json object
     * @return matching JSONObject or empty object
     */
    public static JSONObject findJsonInArray(final JSONArray sourceArray, final String name, final Object value) {
        for (var element : sourceArray) {
            if (!(element instanceof JSONObject json)) continue;

            try {
                final var fieldValue = json.get(name);
                if (Objects.equals(fieldValue, value)) return json;
            } catch (JSONException ignored) {
            }
        }
        return new JSONObject();
    }

    /**
     * Updates values of json by passed values in map.
     *
     * @param json Json object to be updated
     * @param map  Map of json field name as a key and json field value as a value
     * @return Json object
     */
    public static JSONObject updateJson(final JSONObject json, final Map<String, Object> map) {
        var updated = new JSONObject(json.toString());
        for (var entry : map.entrySet()) {
            updated = updateJson(updated, entry.getKey(), entry.getValue());
        }
        return new JSONObject(unflatten(updated));
    }

    /**
     * Updates a field in a flattened JSON object, replacing all subfields if the field is a parent.
     *
     * @param json   json to be updated
     * @param field  field to be updated
     * @param update new value of field
     * @return updated json.
     */
    public static JSONObject updateJson(final JSONObject json, final String field, final Object update) {
        final var flat = flatten(json.toString());

        if (TEST_KEY_IS_PARENT.test(field, flat.keySet())) {
            GET_CHILDREN.apply(flat.keySet(), field).forEach(flat::remove);
            flat.put(field, update);
        }

        return new JSONObject(unflatten(flat));
    }

    /**
     * Updates a field in a flattened JSON array.
     *
     * @param array  json array to be updated
     * @param field  field to be updated
     * @param update new value of field
     * @return updated json.
     */
    public static JSONArray updateJson(final JSONArray array, final String field, final Object update) {
        final JSONObject flat = flatten(array.toString());
        if (flat.has(field)) {
            flat.put(field, update);
        }
        return new JSONArray(unflatten(flat));
    }

    /**
     * Returns string representation of file content.
     *
     * @param path representation of a system dependent file path
     * @return content from file as a string
     */
    private static String readStringFromFile(final String path) {
        try (
                var data = JsonDeserializer.class.getResourceAsStream(path);
                var inputStream = new InputStreamReader(
                        Objects.requireNonNull(data, "Can't find a file: " + path), Charset.defaultCharset());
                var reader = new BufferedReader(inputStream)
        ) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }

        return null;
    }

    private static JSONObject flatten(final String json) {
        return new JSONObject(new JsonFlattener(json).flatten());
    }

    private static String unflatten(final JSONObject flatJson) {
        return new JsonUnflattener(flatJson.toString()).unflatten();
    }
}
