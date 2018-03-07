package testcase;

import com.mbi.*;
import org.json.JSONArray;
import org.json.JSONObject;
import serializer.JsonDeserializer;

import java.util.Random;

/**
 * Abstract test case.
 */
public abstract class BaseTestCase {

    protected final HttpRequest http = new RequestBuilder();
    protected final JsonAssert assertion = new JsonAssert();
    protected final JsonValidator validator = new JsonValidator();
    protected final DateHandler dateHandler = new DateHandler();

    /**
     * Returns random number.
     *
     * @param count digits count
     * @return number
     */
    protected static long getRandomNum(final int count) {
        String s = String.valueOf(getRandomNum());

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
    protected static String getRandomUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    protected final JSONObject getJsonFromFile(final String path) {
        return JsonDeserializer.getJsonFromFile(path);
    }

    protected final JSONArray getJsonArrayFromFile(final String path) {
        return JsonDeserializer.getJsonArrayFromFile(path);
    }

    protected final JSONObject findJsonInArray(final JSONArray sourceArray,
                                               final String fieldName,
                                               final String fieldValue) {
        return JsonDeserializer.findJsonInArray(sourceArray, fieldName, fieldValue);
    }
}
