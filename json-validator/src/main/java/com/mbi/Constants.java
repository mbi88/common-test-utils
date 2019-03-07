package com.mbi;

/**
 * Constants.
 */
final class Constants {

    /**
     * Assertion error if null json schema passed.
     */
    public static final String JSON_SCHEMA_NULL_ERROR_MESSAGE = "Json schema should not be null";

    /**
     * Assertion error if null validation json passed.
     */
    public static final String VALIDATION_JSON_NULL_ERROR_MESSAGE = "Validation json should not be null";

    /**
     * Json error if invalid json passed.
     */
    public static final String INVALID_JSON_ERROR_MESSAGE = "Invalid json: ";

    /**
     * Prohibit object creation.
     */
    private Constants() {
    }
}
