package endpoint;

/**
 * Contract for testing GET list endpoint behavior.
 */
public interface GetList {

    /**
     * Tests successful retrieval of an object list.
     */
    void testCanGetList();

    /**
     * Validates the response body against a predefined schema.
     */
    void validateJson();

    /**
     * Tests sorting behavior and verifies correct order of results.
     */
    void testSortOrder();
}
