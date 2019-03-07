package com.mbi;

import io.restassured.response.Response;
import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mbi.Constants.*;

/**
 * Based on <a href="https://github.com/everit-org/json-schema">JSON Schema Validator</a>.
 * Validates json schema with something of:
 * {@link org.json.JSONObject},
 * {@link org.json.JSONArray},
 * {@link io.restassured.response.Response},
 * {@link java.lang.String}
 *
 * @see <a href="http://json-schema.org/">What is json schema</a>
 * @see <a href="https://jsonschema.net/">Where can I create schemas</a>
 * @see <a href="https://github.com/everit-org/json-schema">JSON Schema Validator</a>
 */
public final class JsonValidator {

    /**
     * Validates {@link org.json.JSONObject} according to json schema.
     *
     * @param schema schema.
     * @param json   JSONObject to compare.
     * @throws AssertionError if null passed
     */
    public void validate(final JSONObject schema, final JSONObject json) {
        Validate.notNull(schema, JSON_SCHEMA_NULL_ERROR_MESSAGE);
        Validate.notNull(json, VALIDATION_JSON_NULL_ERROR_MESSAGE);

        final Comparator<JSONObject> comparator = new SchemaValidator()::validateSchema;

        comparator.compareWithSchema(schema, json);
    }

    /**
     * Validates {@link org.json.JSONArray} according to json schema.
     *
     * @param schema schema.
     * @param json   JSONArray to compare.
     * @throws AssertionError if null passed
     */
    public void validate(final JSONObject schema, final JSONArray json) {
        Validate.notNull(schema, JSON_SCHEMA_NULL_ERROR_MESSAGE);
        Validate.notNull(json, VALIDATION_JSON_NULL_ERROR_MESSAGE);

        final Comparator<JSONArray> comparator = new SchemaValidator()::validateSchema;

        comparator.compareWithSchema(schema, json);
    }

    /**
     * Validates {@link io.restassured.response.Response} according to json schema.
     * Converts rest-assured response to JSONObject/JSONArray. Throws JSONException if conversion fails.
     *
     * @param schema   schema.
     * @param response rest-assured response to compare.
     * @throws JSONException  if json is invalid.
     * @throws AssertionError if null passed
     */
    public void validate(final JSONObject schema, final Response response) {
        Validate.notNull(schema, JSON_SCHEMA_NULL_ERROR_MESSAGE);
        Validate.notNull(response, VALIDATION_JSON_NULL_ERROR_MESSAGE);

        final Comparator<Response> comparator = (schm, rsp) -> {
            if (isJsonObject(rsp.asString())) {
                validate(schm, new JSONObject(rsp.asString()));
            } else if (isJsonArray(rsp.asString())) {
                validate(schm, new JSONArray(rsp.asString()));
            } else {
                throw new JSONException(INVALID_JSON_ERROR_MESSAGE + rsp.asString());
            }
        };

        comparator.compareWithSchema(schema, response);
    }

    /**
     * Validates {@link java.lang.String} according to json schema.
     * Converts string to JSONObject/JSONArray. Throws JSONException if conversion fails.
     *
     * @param schema schema.
     * @param string string to compare.
     * @throws JSONException  if json is invalid.
     * @throws AssertionError if null passed
     */
    public void validate(final JSONObject schema, final String string) {
        Validate.notNull(schema, JSON_SCHEMA_NULL_ERROR_MESSAGE);
        Validate.notNull(string, VALIDATION_JSON_NULL_ERROR_MESSAGE);

        final Comparator<String> comparator = (schm, str) -> {
            if (isJsonObject(str)) {
                validate(schm, new JSONObject(str));
            } else if (isJsonArray(str)) {
                validate(schm, new JSONArray(str));
            } else {
                throw new JSONException(INVALID_JSON_ERROR_MESSAGE + str);
            }
        };

        comparator.compareWithSchema(schema, string);
    }

    /**
     * If passed json as string is json object.
     *
     * @param s json as string
     * @return true is json object was passed.
     */
    @SuppressWarnings("PMD.SimplifyStartsWith")
    private boolean isJsonObject(final String s) {
        return s.startsWith("{");
    }

    /**
     * If passed json as string is json array.
     *
     * @param s json as string
     * @return true is json array was passed.
     */
    @SuppressWarnings("PMD.SimplifyStartsWith")
    private boolean isJsonArray(final String s) {
        return s.startsWith("[");
    }
}
