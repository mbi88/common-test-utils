package test.tests;

import com.mbi.JsonValidator;
import com.mbi.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

class TestHelper {

    private final JsonValidator validator = new JsonValidator();

    <T> void checkFail(final JSONObject j1, final T j2) {
        boolean failed = true;

        if (j2 instanceof String) {
            try {
                validator.validate(j1, (String) j2);
                failed = false;
            } catch (Throwable ignored) {
            }
        }

        if (j2 instanceof Response) {
            try {
                validator.validate(j1, (Response) j2);
                failed = false;
            } catch (Throwable ignored) {
            }
        }

        if (j2 instanceof JSONObject) {
            try {
                validator.validate(j1, (JSONObject) j2);
                failed = false;
            } catch (Throwable ignored) {
            }
        }

        if (j2 instanceof JSONArray) {
            try {
                validator.validate(j1, (JSONArray) j2);
                failed = false;
            } catch (Throwable ignored) {
            }
        }

        if (!failed) throw new AssertionError();
    }

    <T> void checkSuccess(final JSONObject schema, final T object) {
        if (object instanceof String) validator.validate(schema, (String) object);
        if (object instanceof Response) validator.validate(schema, (Response) object);
        if (object instanceof JSONObject) validator.validate(schema, (JSONObject) object);
        if (object instanceof JSONArray) validator.validate(schema, (JSONArray) object);
    }
}
