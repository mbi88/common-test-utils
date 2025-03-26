package endpoint;

/**
 * Contract for testing DELETE endpoint behavior.
 */
public interface Delete {

    /**
     * Tests successful deletion of an object.
     */
    void testCanDelete();

    /**
     * Ensures deletion of the same object twice is not allowed.
     * Expected: 404 Not Found.
     */
    void testCantDeleteTwice();

    /**
     * Ensures deletion fails when object ID is missing in the URL.
     * Expected: 404 Not Found.
     */
    void testCantDeleteWithoutId();

    /**
     * Ensures deletion fails with an incorrect or non-existing object ID.
     * Expected: 404 Not Found.
     */
    void testCantDeleteWithIncorrectId();
}
