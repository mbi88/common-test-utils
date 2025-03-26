package endpoint;

/**
 * Contract for testing GET-by-ID endpoint behavior.
 */
public interface GetById {

    /**
     * Tests successful retrieval of an object by ID.
     */
    void testCanGetById();

    /**
     * Ensures request fails with incorrect or non-existing object ID.
     * Expected: 404 Not Found.
     */
    void testCantGetWithIncorrectId();

    /**
     * Validates the response body against a predefined schema.
     */
    void validateJson();
}
