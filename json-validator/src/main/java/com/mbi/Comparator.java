package com.mbi;

import org.json.JSONObject;

/**
 * Compares object with json schema.
 *
 * @param <T> {@link org.json.JSONObject},
 *            {@link org.json.JSONArray},
 *            {@link io.restassured.response.Response},
 *            {@link java.lang.String}
 * @see <a href="http://json-schema.org/">What is json schema</a>
 * @see <a href="https://jsonschema.net/">Where can I create schemas</a>
 */
@FunctionalInterface
interface Comparator<T> {
    void compareWithSchema(JSONObject schema, T object);
}
