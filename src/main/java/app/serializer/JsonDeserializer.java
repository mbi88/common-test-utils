package app.serializer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonDeserializer {

    /**
     * Method to read a data from file from passed patch and return Json object.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the patch -
     *             it is already implemented in the method.
     * @return Json object
     */
    public static JSONObject getJsonFromFile(String path) {
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
    public static JSONArray getJsonArrayFromFile(String path) {
        JSONArray json = new JSONArray();

        try {
            String s = new String(Files.readAllBytes(Paths.get(JsonDeserializer.class.getResource(path).toURI())));
            json = new JSONArray(s);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
