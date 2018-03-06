package testcase;

import com.mbi.*;
import org.json.JSONArray;
import org.json.JSONObject;
import serializer.JsonDeserializer;

public abstract class BaseTestCase {

    protected HttpRequest http = new RequestBuilder();
    protected JsonAssert assertion = new JsonAssert();
    protected JsonValidator validator = new JsonValidator();
    private DateHandler dateHandler = new DateHandler();

    protected static long getRandomNum() {
        return System.currentTimeMillis();
    }

    protected static String getRandomGUID() {
        return java.util.UUID.randomUUID().toString();
    }

    protected JSONObject getJsonFromFile(String path) {
        return JsonDeserializer.getJsonFromFile(path);
    }

    protected JSONArray getJsonArrayFromFile(String path) {
        return JsonDeserializer.getJsonArrayFromFile(path);
    }
}
