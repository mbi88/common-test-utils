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
    protected static long getRandomNum(final int count) {
        final String s = String.valueOf(getRandomNum());

        assert count > 0 && count <= s.length() : "getRandomNum(int count): incorrect digits count";

        return Long.parseLong(s.substring(s.length() - count));
    }

    /**
     * Returns random number.
     *
     * @return number
     */
    protected static long getRandomNum() {
        return System.currentTimeMillis() + new Random().nextInt(100000) + 1;
    }

    /**
     * Returns random UUID.
     *
     * @return uuid
     */
    protected static String getRandomUID() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Method to read a data from file from passed patch and return Json object.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the patch -
     *             it is already implemented in the method.
     * @return Json object
     */
    protected final JSONObject getJsonFromFile(final String path) {
        return JsonDeserializer.getJsonFromFile(path);
    }

    /**
     * Method to read a data from file from passed patch and return Json array.
     *
     * @param path Path to the file with data. No need to add "src/main/resources" every time when you pass the patch -
     *             it is already implemented in the method.
     * @return Json array
     */
    protected final JSONArray getJsonArrayFromFile(final String path) {
        return JsonDeserializer.getJsonArrayFromFile(path);
    }

    /**
     * Get json object from json array by object key and value.
     *
     * @param sourceArray sourceArray
     * @param fieldName   fieldName of wanted json object
     * @param fieldValue  fieldValue of wanted json object
     * @return inner json object
     */
    protected final JSONObject findJsonInArray(final JSONArray sourceArray,
                                               final String fieldName,
                                               final String fieldValue) {
        return JsonDeserializer.findJsonInArray(sourceArray, fieldName, fieldValue);
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
    public JSONArray toJsonArray(final Response response) {
        return new JSONArray(response.asString());
    }
}
