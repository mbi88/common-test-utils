package testcase;

import com.mbi.*;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import serializer.JsonDeserializer;

import java.util.Random;

/**
 * Abstract test case.
 */
public abstract class BaseTestCase {

    /**
     * Http requests based on rest-assured framework.
     */
    protected final HttpRequest http = new RequestBuilder();

    /**
     * Json comparison.
     */
    protected final JsonAssert assertion = new JsonAssert();

    /**
     * Json validation based on json schema validation.
     */
    protected final JsonValidator validator = new JsonValidator();

    /**
     * Different operations with dates.
     */
    protected final DateHandler dateHandler = new DateHandler();

    /**
     * Returns random number.
     *
     * @param count digits count
     * @return number
     */
    public static long getRandomNum(final int count) {
        final String s = String.valueOf(getRandomNum());

        assert count > 0 && count <= s.length() : "getRandomNum(int count): incorrect digits count";

        // Replace 0 in the beginning
        String result = s.substring(s.length() - count);
        if (result.startsWith("0")) {
            result = result.replaceFirst("^.", "8");
        }

        return Long.parseLong(result);
    }

    /**
     * Returns random number.
     *
     * @return number
     */
    public static long getRandomNum() {
        return System.currentTimeMillis() + new Random().nextInt(100000) + 1;
    }

    /**
     * Returns random UUID.
     *
     * @return uuid
     */
    public static String getRandomUID() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Converts response to json object.
     *
     * @param response rest-assured response
     * @return json object.
     */
    public static JSONObject toJson(final Response response) {
        return new JSONObject(response.asString());
    }

    /**
     * Converts response to json array.
     *
     * @param response rest-assured response
     * @return json array.
     */
    public static JSONArray toJsonArray(final Response response) {
        return new JSONArray(response.asString());
    }

    /**
     * Reads a data from file in src/main/resources/ from passed path and returns Json object.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the path -
     *             it is already implemented in the method.
     * @return Json object.
     */
    protected final JSONObject getResource(final String path) {
        return JsonDeserializer.getResource(path);
    }

    /**
     * Reads a data from file in src/main/resources/ from passed path and returns Json array.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the path -
     *             it is already implemented in the method.
     * @return Json array.
     */
    protected final JSONArray getResources(final String path) {
        return JsonDeserializer.getResources(path);
    }

    /**
     * Get json object from json array by object key and value.
     *
     * @param sourceArray sourceArray
     * @param name        field name of wanted json object
     * @param value       field value of wanted json object
     * @return inner json object
     */
    protected final JSONObject findJsonInArray(final JSONArray sourceArray, final String name, final Object value) {
        return JsonDeserializer.findJsonInArray(sourceArray, name, value);
    }
}
