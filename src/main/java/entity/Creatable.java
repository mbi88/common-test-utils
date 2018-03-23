package entity;

import io.restassured.response.Response;

/**
 * Endpoints that have methods to create objects (POST, PUT) should implement this interface for accessing created
 * objects by id.
 *
 * @param <T> id class.
 */
interface Creatable<T> {
    /**
     * Extracts id from response.
     *
     * @param response rest-assured response
     * @return id.
     */
    T extractId(Response response);
}
