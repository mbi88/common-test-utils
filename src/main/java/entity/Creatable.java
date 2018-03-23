package entity;

import io.restassured.response.Response;

import java.util.function.Function;

/**
 * Endpoints that have methods to create objects (POST, PUT) should implement this interface for accessing created
 * objects by its id.
 */
public interface Creatable {

    /**
     * Extracts id from response.
     *
     * @param response rest-assured response
     * @param function extract function.
     * @param <T>      id class.
     * @return id.
     */
    default <T> T extractId(final Response response, final Function<Response, T> function) {
        return function.apply(response);
    }
}
