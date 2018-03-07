package testcase;

import com.mbi.*;
import org.json.JSONArray;
import org.json.JSONObject;
import serializer.JsonDeserializer;

public abstract class BaseTestCase {

    protected HttpRequest http = new RequestBuilder();
    protected JsonAssert assertion = new JsonAssert();
    protected JsonValidator validator = new JsonValidator();
    protected DateHandler dateHandler = new DateHandler();

    protected static long getRandomNum() {
        return System.currentTimeMillis();
    }

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
