package tests;

import app.Configuration;
import app.serializer.JsonDeserializer;
import com.mbi.HttpRequest;
import com.mbi.JsonAssert;
import com.mbi.JsonValidator;
import com.mbi.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class TestCase {

    private static boolean configured = false;
    protected HttpRequest http = new RequestBuilder();
    protected JsonAssert assertion = new JsonAssert();
    protected JsonValidator validator = new JsonValidator();

    public TestCase() {
        // Configure only 1 time
        if (!configured) {
            Configuration config = Configuration.getInstance();
            config.configure();
            config.printConfig();
        }
        configured = true;
    }

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

    /*
    Get json object from json array by object key and value
     */
    protected JSONObject getJsonByField(JSONArray array, String fieldName, String fieldValue){
        JSONObject jsonObject = new JSONObject();

        for (Object o : array) {
            JSONObject jo = new JSONObject(o.toString());
            jo.getString(fieldName);
            if (jo.getString(fieldName).equalsIgnoreCase(fieldValue))
                jsonObject = jo;
        }

        return jsonObject;
    }
}
