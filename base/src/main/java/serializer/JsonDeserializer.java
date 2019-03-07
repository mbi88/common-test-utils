package serializer;

import com.github.wnameless.json.flattener.FlattenMode;
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
import java.util.Map;
import java.util.Set;

/**
 * Read content from src/main/resources/ file and map to org.json.JSONObject/JSONArray.
 */
public final class JsonDeserializer {

    private static final Faker FAKER = new JsonFaker();

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
     * @param map  Map of json field name as a key and json field value as a value
     * @return Json object.
     */
    public static JSONObject updateJson(final JSONObject json, final Map<String, Object> map) {
        // Get flattened JSON
        final String flattenStr = new JsonFlattener(json.toString())
                .withFlattenMode(FlattenMode.NORMAL)
                .flatten();
        final JSONObject flattened = new JSONObject(flattenStr);

        // Update every value in map
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Go throw all flattened keys: {"a": {"b":1}, "c":2} -> ["a.b", "c"]
            for (String flattenedKey : Set.copyOf(flattened.keySet())) {
                // Go throw all flattened keys parts: {"a": {"b":1}, "c":2} -> [["a", "b"], "c"]
                for (String flattenedKeyPart : flattenedKey.split("\\.")) {
                    // Handle json arrays. Flattened keys can contain "[]": {"a":[{"c":1}]} -> {"a[0].c":1}.
                    // "[0]" will be removed.
                    final String atomicKey = (flattenedKeyPart.matches("^.*\\[[0-9]+]$"))
                            ? flattenedKeyPart.substring(0, flattenedKeyPart.indexOf('['))
                            : flattenedKeyPart;
                    // Update value
                    if (atomicKey.equals(entry.getKey())) {
                        flattened.remove(flattenedKey);
                        final String newKey = flattenedKey
                                .substring(0, flattenedKey.indexOf(atomicKey))
                                .concat(atomicKey);
                        flattened.put(newKey, entry.getValue());
                    }
                }
            }
        }

        return new JSONObject(JsonUnflattener.unflatten(flattened.toString()));
    }
}
