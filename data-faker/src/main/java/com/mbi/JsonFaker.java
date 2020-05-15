package com.mbi;

import com.mbi.parameters.SupportedParameters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Set update parameter in json field to get it replaced with corresponding data.
 * Example:
 * {
 * "field": "Today is {$current_date}."
 * }
 * <p>
 * Returns:
 * {
 * "field": "Today is 2019-01-29."
 * }
 * <p>
 * See {@link SupportedParameters} for available supported parameters.
 */
public class JsonFaker implements Faker {

    private final ObjectFaker objectFaker = new ObjectFaker();

    /**
     * Replace update parameters with data in json.
     *
     * @param resource json object/array to be updated.
     * @param <T>      json object/array.
     * @return updated json.
     * @throws JSONException if incorrect json passed.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T fakeData(final T resource) {
        final T result;

        if (resource instanceof JSONObject) {
            result = (T) fakeJsonObjectData(new JSONObject(resource.toString()));
        } else if (resource instanceof JSONArray) {
            result = (T) fakeJsonArrayData(new JSONArray(resource.toString()));
        } else {
            throw new JSONException("Incorrect json: " + resource.toString());
        }

        return result;
    }

    /**
     * Replace update parameters with data in json object.
     *
     * @param json json to be updated.
     * @return updated json.
     */
    private JSONObject fakeJsonObjectData(final JSONObject json) {
        // Avoid updating of passed json
        final JSONObject updatedJson = new JSONObject(json.toString());
        // Update every json field
        for (String field : updatedJson.keySet()) {
            final Object beUpdated = updatedJson.get(field);
            if (beUpdated instanceof JSONObject) {
                updatedJson.put(field, fakeJsonObjectData((JSONObject) beUpdated));
            } else if (beUpdated instanceof JSONArray) {
                updatedJson.put(field, fakeJsonArrayData((JSONArray) beUpdated));
            } else if (beUpdated instanceof String) {
                updatedJson.put(field, objectFaker.fakeData(updatedJson.get(field)));
            } else {
                updatedJson.put(field, beUpdated);
            }
        }

        return updatedJson;
    }

    /**
     * Replace update parameters with data in json array.
     *
     * @param json json to be updated.
     * @return updated json.
     */
    private JSONArray fakeJsonArrayData(final JSONArray json) {
        // Avoid updating of passed json
        final JSONArray resultArray = new JSONArray();
        // Update every json object
        for (Object beUpdated : json) {
            if (beUpdated instanceof JSONObject) {
                resultArray.put(fakeJsonObjectData((JSONObject) beUpdated));
            } else if (beUpdated instanceof JSONArray) {
                resultArray.put(fakeJsonArrayData((JSONArray) beUpdated));
            } else {
                resultArray.put(objectFaker.fakeData(beUpdated));
            }
        }

        return resultArray;
    }
}
