package com.mbi;

import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Creates validation exception message.
 */
final class ExceptionMessageCompositor {

    /**
     * Returns validation exception message.
     *
     * @param exception validation exception.
     * @param json      validated object.
     * @param <T>       {@link org.json.JSONObject} or {@link org.json.JSONArray}.
     * @return exception message.
     */
    @SuppressWarnings("PMD.SimplifyStartsWith")
    public <T> String getMessage(final ValidationException exception, final T json) {
        // Add error message
        String msg = exception.getMessage();

        // Add all violations
        for (String s : exception.getAllMessages()) {
            msg = msg.concat("\n" + s);
        }

        // Add validated object as string with indentations
        final String jsonString = json.toString().startsWith("{")
                ? new JSONObject(json.toString()).toString(4)
                : new JSONArray(json.toString()).toString(4);
        msg = msg.concat("\n\nResponse: " + jsonString);

        return msg;
    }
}
