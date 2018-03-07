package entity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Build query parameters as a string in format '?k1=v1&k2=v2'.
 */
public final class QueryParameter {

    /**
     * Map of parameters.
     */
    private final Map<String, String> params = new HashMap<>();

    /**
     * Adds parameters.
     *
     * @param key   key.
     * @param value value.
     */
    public void addParameter(final String key, final String value) {
        this.params.put(key, value);
    }

    /**
     * Returns query parameters in format '?k1=v1&k2=v2'.
     *
     * @return query parameters
     */
    public String getParametersString() {
        String tmp = "?" + params.entrySet().stream()
                .map(m -> m.getKey().concat("=").concat(m.getValue()).concat("&"))
                .collect(Collectors.joining());

        return tmp.substring(0, tmp.length() - 1);
    }
}
