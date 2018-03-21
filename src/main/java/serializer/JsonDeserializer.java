package serializer;

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
 * Read content from file and map to org.json.JSONObject/JSONArray.
 * Base url prefix: src/main/resources/
 */
public final class JsonDeserializer {

    /**
     * Prohibits object initialization.
     */
    private JsonDeserializer() {
    }

    /**
     * Method to read a data from file from passed patch and return Json object.
     * Base url prefix: src/main/resources/
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the patch -
     *             it is already implemented in the method.
     * @return Json object.
     */
    public static JSONObject getJsonFromFile(final String path) {
        JSONObject json = new JSONObject();

        try {
            final String s = readStringFromFile(path);
            json = new JSONObject(s);
        } catch (URISyntaxException | IOException ignored) {
            // Ignored
        }

        return json;
    }

    /**
     * Method to read a data from file from passed patch and return Json array.
     * Base url prefix: src/main/resources/
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the patch -
     *             it is already implemented in the method.
     * @return Json array.
     */
    public static JSONArray getJsonArrayFromFile(final String path) {
        JSONArray json = new JSONArray();

        try {
            final String s = readStringFromFile(path);
            json = new JSONArray(s);
        } catch (URISyntaxException | IOException ignored) {
            // Ignored
        }

        return json;
    }

    /**
     * Get json object from json array by object key and value.
     *
     * @param sourceArray sourceArray
     * @param fieldName   fieldName of wanted json object
     * @param fieldValue  fieldValue of wanted json object
     * @return inner json object.
     */
    public static JSONObject findJsonInArray(final JSONArray sourceArray,
                                             final String fieldName,
                                             final String fieldValue) {
        JSONObject foundJson = new JSONObject();

        for (Object o : sourceArray) {
            final JSONObject jo = new JSONObject(o.toString());
            jo.getString(fieldName);
            if (jo.getString(fieldName).equalsIgnoreCase(fieldValue)) {
                foundJson = jo;
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
