package controllers;

import io.restassured.response.Response;

import java.util.function.Function;

/**
 * Interface for controllers that create resources via POST or PUT requests.
 * Provides utility for extracting ID from the response.
 */
public interface Creatable {

    /**
     * Extracts an object ID from the given response using the provided function.
     *
     * @param response the response to extract from
     * @param function the extraction function
     * @param <T>      type of ID
     * @return extracted ID
     */
    default <T> T extractId(final Response response, final Function<Response, T> function) {
        return function.apply(response);
    }
}
