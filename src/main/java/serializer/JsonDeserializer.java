package serializer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Read content from file and map to org.json.JSONObject/JSONArray.
 */
public final class JsonDeserializer {

    private JsonDeserializer() {
    }

    /**
     * Method to read a data from file from passed patch and return Json object.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the patch -
     *             it is already implemented in the method.
     * @return Json object
     */
    public static JSONObject getJsonFromFile(final String path) {
        JSONObject json = new JSONObject();

        try {
            String s = new String(Files.readAllBytes(Paths.get(JsonDeserializer.class.getResource(path).toURI())));
            json = new JSONObject(s);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Method to read a data from file from passed patch and return Json array.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the patch -
     *             it is already implemented in the method.
     * @return Json array
     */
    public static JSONArray getJsonArrayFromFile(final String path) {
        JSONArray json = new JSONArray();

        try {
            String s = new String(Files.readAllBytes(Paths.get(JsonDeserializer.class.getResource(path).toURI())));
            json = new JSONArray(s);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Get json object from json array by object key and value.
     *
     * @param sourceArray sourceArray
     * @param fieldName   fieldName of wanted json object
     * @param fieldValue  fieldValue of wanted json object
     * @return inner json object
     */
    public static JSONObject findJsonInArray(final JSONArray sourceArray,
                                             final String fieldName,
                                             final String fieldValue) {
        JSONObject jsonObject = new JSONObject();

        for (Object o : sourceArray) {
            JSONObject jo = new JSONObject(o.toString());
            jo.getString(fieldName);
            if (jo.getString(fieldName).equalsIgnoreCase(fieldValue)) {
                jsonObject = jo;
            }
        }

        return jsonObject;
    }
}
