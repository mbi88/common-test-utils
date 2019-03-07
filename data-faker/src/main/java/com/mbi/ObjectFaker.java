package com.mbi;

import com.mbi.parameters.SupportedParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Set update parameter in string to get it replaced with corresponding data.
 * Example:
 * String field = "Today is {$current_date}.";
 * <p>
 * Returns:
 * String field = "Today is 2019-01-29.";
 * <p>
 * See {@link SupportedParameters} for available supported parameters.
 */
public class ObjectFaker implements Faker {

    /**
     * Replace update parameters with data in string.
     *
     * @param resource string to be updated.
     * @param <T>      string.
     * @return updated string.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T fakeData(final T resource) {
        if (resource instanceof String) {
            final List<String> parameters = getParams((String) resource);
            Object updatedValue = resource;
            // Update only if parameters exist
            if (!parameters.isEmpty()) {
                // Support several parameters in a field
                for (String par : parameters) {
                    // Replace update parameter with appropriate data
                    updatedValue = FAKE_DIRECTOR.fake(par, updatedValue);
                }
            }

            return (T) updatedValue;
        }

        return resource;
    }

    /**
     * Get parameters from field.
     *
     * @param fieldValue field value.
     * @return list of parameters if exist or empty list.
     */
    private List<String> getParams(final String fieldValue) {
        final List<String> list = new ArrayList<>();
        String s = fieldValue;

        while (s.contains(PARAMETER_START)) {
            // Remove the sign of the beginning of the parameter and all before
            s = s.substring(s.indexOf(PARAMETER_START) + PARAMETER_START.length());
            // Remove the sign of the end of the parameter and all after
            final String param;
            try {
                param = s.substring(0, s.indexOf(PARAMETER_END));
            } catch (StringIndexOutOfBoundsException ex) {
                throw new IllegalArgumentException("Incorrect parameter: " + fieldValue, ex);
            }
            // Store parameter
            list.add(param);
        }

        return list;
    }
}
