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
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Read content from src/main/resources/ file and map to org.json.JSONObject/JSONArray.
 */
public final class JsonDeserializer {

    /**
     * Fields separator in flattened json.
     */
    private static final String FIELDS_SEPARATOR = ".";
    private static final Faker FAKER = new JsonFaker();

    /**
     * Checks if field is present in flattened json.
     */
    private static final BiPredicate<String, Set<String>> TEST_KEY_IS_PARENT = (parentKey, children) -> children
            .stream()
            .anyMatch(parentField -> parentField.startsWith(parentKey.concat(FIELDS_SEPARATOR))
                    || parentField.equalsIgnoreCase(parentKey)
                    || parentField.startsWith(parentKey.concat("[")));

    /**
     * Returns children of parent from set.
     */
    private static final BiFunction<Set<String>, String, List<String>> GET_CHILDREN = (parentFields, parent) -> {
        final List<String> list = new ArrayList<>();
        parentFields.forEach(s -> {
            if (s.startsWith(parent)) {
                list.add(s);
            }
        });
        return list;
    };

    private static final Logger LOGGER = Logger.getLogger(JsonDeserializer.class.getCanonicalName());

    /**
     * Prohibits object initialization.
     */
    private JsonDeserializer() {
    }

    /**
     * Reads a data from file in src/main/resources/ from passed path and returns Json object.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the path -
     *             it is already implemented in the method.
     * @return Json object.
     */
    public static JSONObject getResource(final String path) {
        final JSONObject json = new JSONObject(readStringFromFile(path));

        return FAKER.fakeData(json);
    }

    /**
     * Reads a data from file in src/main/resources/ from passed path and returns Json array.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the path -
     *             it is already implemented in the method.
     * @return Json array.
     */
    public static JSONArray getResources(final String path) {
        final JSONArray json = new JSONArray(readStringFromFile(path));

        return FAKER.fakeData(json);
    }

    /**
     * Get json object from json array by object key and value.
     *
     * @param sourceArray sourceArray
     * @param name        field name of wanted json object
     * @param value       field value of wanted json object
     * @return inner json object.
     */
    public static JSONObject findJsonInArray(final JSONArray sourceArray, final String name, final Object value) {
        JSONObject foundJson = new JSONObject();

        for (Object json : sourceArray) {
            // Find field
            try {
                ((JSONObject) json).get(name);
            } catch (JSONException ignored) {
                continue;
            }

            // Objects are equal
            if (((JSONObject) json).get(name).equals(value)) {
                foundJson = (JSONObject) json;
            }
        }

        return foundJson;
    }

    /**
     * Returns string representation of file content.
     *
     * @param path representation of a system dependent file path.
     * @return content from file as a string.
     */
    private static String readStringFromFile(final String path) {
        try (
                var data = JsonDeserializer.class.getResourceAsStream(path);
                var inputStream = new InputStreamReader(Objects.requireNonNull(data, "Can't find a file: " + path),
                        Charset.defaultCharset());
                var bufferedReader = new BufferedReader(inputStream)
        ) {
            return bufferedReader
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }

        return null;
    }

    /**
     * Updates values of json by passed values in map.
     *
     * @param json Json object to be updated.
     * @param map  Map of json field name as a key and json field value as a value.
     * @return Json object.
     */
    public static JSONObject updateJson(final JSONObject json, final Map<String, Object> map) {
        var res = new JSONObject(json.toString());

        for (var entry : map.entrySet()) {
            res = updateJson(res, entry.getKey(), entry.getValue());
        }

        return new JSONObject(unflatten(res));
    }

    /**
     * Updates values of json.
     *
     * @param json   json to be updated.
     * @param field  field to be updated.
     * @param update new value of field.
     * @return updated json.
     */
    public static JSONObject updateJson(final JSONObject json, final String field, final Object update) {
        final var res = flatten(json.toString());

        if (TEST_KEY_IS_PARENT.test(field, res.keySet())) {
            for (var key : GET_CHILDREN.apply(res.keySet(), field)) {
                res.remove(key);
            }
            res.put(field, update);
        }

        return new JSONObject(unflatten(res));
    }

    /**
     * Updates values of json.
     *
     * @param json   json to be updated.
     * @param field  field to be updated.
     * @param update new value of field.
     * @return updated json.
     */
    public static JSONArray updateJson(final JSONArray json, final String field, final Object update) {
        final var res = flatten(json.toString());

        if (isFieldExist(res, field)) {
            res.put(field, update);
        }

        return new JSONArray(unflatten(res));
    }

    private static boolean isFieldExist(final JSONObject json, final String field) {
        boolean fieldExists;
        try {
            json.get(field);
            fieldExists = true;
        } catch (JSONException ignored) {
            fieldExists = false;
        }

        return fieldExists;
    }

    private static JSONObject flatten(final String resource) {
        final var flattener = new JsonFlattener(resource);
        return new JSONObject(flattener.flatten());
    }

    private static String unflatten(final JSONObject resource) {
        return new JsonUnflattener(resource.toString()).unflatten();
    }
}
