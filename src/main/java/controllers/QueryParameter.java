package controllers;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.stream.Collectors;

/**
 * Utility for building URL query parameters (e.g., ?key1=val1&key2=val2).
 */
public final class QueryParameter {

    /**
     * Map of parameters.
     */
    private final Multimap<String, String> params = ArrayListMultimap.create();

    /**
     * Default constructor.
     */
    public QueryParameter() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    /**
     * Add parameter via constructor.
     *
     * @param name  parameter name.
     * @param value parameter value.
     */
    public QueryParameter(final String name, final Object value) {
        params.put(name, value.toString());
    }

    /**
     * Adds parameters.
     *
     * @param key   key.
     * @param value value.
     */
    public void addParameter(final String key, final Object value) {
        this.params.put(key, value.toString());
    }

    /**
     * Removes parameter by key.
     *
     * @param key key.
     */
    public void removeParameter(final String key) {
        this.params.removeAll(key);
    }

    /**
     * Removes parameter by key and value.
     *
     * @param key   key.
     * @param value value.
     */
    public void removeParameter(final String key, final Object value) {
        this.params.remove(key, value.toString());
    }

    /**
     * Returns query parameters in format <!--?k1=v1&k2=v2<-->.
     *
     * @return encoded query parameter string
     */
    public String getParametersString() {
        final String tmp = "?" + params.entries().stream()
                .map(m -> m.getKey() + "=" + m.getValue() + "&")
                .collect(Collectors.joining());

        return tmp.substring(0, tmp.length() - 1);
    }
}
