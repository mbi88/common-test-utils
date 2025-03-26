package endpoint;

/**
 * Contract for testing POST endpoint behavior.
 */
public interface Post {

    /**
     * Tests successful creation of a new object.
     */
    void testCanPost();

    /**
     * Tests object creation using only required fields.
     */
    void testPostOnlyRequiredFields();

    /**
     * Validates that the Location header is present and correct.
     */
    void testLocationHeader();

    /**
     * Validates the response body against a predefined schema.
     */
    void validateJson();
}
