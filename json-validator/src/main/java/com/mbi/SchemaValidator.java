package com.mbi;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Validates json data according to json schema.
 */
final class SchemaValidator {

    /**
     * Validates json data.
     *
     * @param schema     json schema.
     * @param jsonObject json object.
     */
    public void validateSchema(final JSONObject schema, final JSONObject jsonObject) {
        validate(schema, jsonObject);
    }

    /**
     * Validates json data.
     *
     * @param schema    json schema.
     * @param jsonArray json array.
     */
    public void validateSchema(final JSONObject schema, final JSONArray jsonArray) {
        validate(schema, jsonArray);
    }

    /**
     * Validates json data. Throws exception if validation failed.
     *
     * @param schemaJson json schema.
     * @param json       object to validate.
     * @param <T>        {@link org.json.JSONObject} or {@link org.json.JSONArray}.
     * @throws ValidationException if validation failed.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    private <T> void validate(final JSONObject schemaJson, final T json) {
        final var schema = getSchema(schemaJson);
        try {
            schema.validate(json);
        } catch (ValidationException ve) {
            final var msg = new ExceptionMessageCompositor().getMessage(ve, json);

            throw new ValidationException(ve.getViolatedSchema(), msg, ve.getKeyword(), ve.getSchemaLocation());
        }
    }

    /**
     * Reads json schema to schema validator object.
     *
     * @param schemaJson json schema
     * @return schema validator
     * @throws JSONException if schema is incorrect
     */
    private Schema getSchema(final JSONObject schemaJson) {
        if (schemaJson.toString().equalsIgnoreCase("{}")) {
            final var message = String.format("Invalid schema!%nSchema:%n%s", schemaJson.toString(4));

            throw new JSONException(message);
        }

        return SchemaLoader.load(schemaJson);
    }
}
