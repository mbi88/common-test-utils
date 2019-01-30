package serializer;

import com.mbi.Faker;
import com.mbi.JsonFaker;
import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        JSONObject json = new JSONObject();

        try {
            final String s = readStringFromFile(path);
            json = new JSONObject(s);
        } catch (URISyntaxException | IOException ignored) {
            // Ignored
        }

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
        JSONArray json = new JSONArray();

        try {
            final String s = readStringFromFile(path);
            json = new JSONArray(s);
        } catch (URISyntaxException | IOException ignored) {
            // Ignored
        }

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

        for (Object o : sourceArray) {
            ((JSONObject) o).get(name);
            if (((JSONObject) o).get(name).toString().equalsIgnoreCase(value.toString())) {
                foundJson = (JSONObject) o;
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
     * @throws URISyntaxException if this URL is not formatted strictly according to
     *                            to RFC2396 and cannot be converted to a URI.
     * @throws IOException        if an I/O error occurs reading from the stream.
     */
    private static String readStringFromFile(final String path) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(getSourcePath(path)), Charset.forName("UTF-8"));
    }
}
