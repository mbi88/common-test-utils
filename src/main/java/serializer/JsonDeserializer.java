package serializer;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import com.mbi.Faker;
import com.mbi.JsonFaker;
import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

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
    private static BiPredicate<String, Set<String>> testKeyIsParent = (parentKey, children) -> children
            .stream()
            .anyMatch(parentField -> parentField.startsWith(parentKey.concat(FIELDS_SEPARATOR))
                    || parentField.equalsIgnoreCase(parentKey)
                    || parentField.startsWith(parentKey.concat("[")));

    /**
     * Returns children of parent from set.
     */
    private static BiFunction<Set<String>, String, List<String>> getChildren = (parentFields, parent) -> {
        final List<String> list = new ArrayList<>();
        parentFields.forEach(s -> {
            if (s.startsWith(parent)) {
                list.add(s);
            }
        });
        return list;
    };

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
     * Returns a representation of a system dependent file path.
     * Base url prefix: src/main/resources/
     *
     * @param path path to file.
     * @return represent a system dependent file path.
     * @throws URISyntaxException   if this URL is not formatted strictly according to
     *                              to RFC2396 and cannot be converted to a URI.
     * @throws NullPointerException if can't find a file.
     */
    private static Path getSourcePath(final String path) throws URISyntaxException {
        final URL url = JsonDeserializer.class.getResource(path);
        Validate.notNull(url, "Can't find a file: " + path);
        return Paths.get(url.toURI());
    }

    /**
     * Returns string representation of file content.
     *
     * @param path representation of a system dependent file path.
     * @return content from file as a string.
     */
    private static String readStringFromFile(final String path) {
        String s = null;
        try {
            s = Files.readString(getSourcePath(path));
        } catch (IOException | URISyntaxException ignored) {
            // Nothing to do here. Just skip
        }

        return s;
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

        if (testKeyIsParent.test(field, res.keySet())) {
            for (var key : getChildren.apply(res.keySet(), field)) {
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
