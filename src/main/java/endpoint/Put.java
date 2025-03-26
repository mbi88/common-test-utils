package endpoint;

/**
 * Contract for testing PUT endpoint behavior.
 */
public interface Put {

    /**
     * Tests successful full update (PUT) of an object.
     */
    void testCanPut();

    /**
     * Ensures PUT request fails when object ID is missing in the URL.
     * Expected: 404 Not Found.
     */
    void testCantPutWithoutId();

    /**
     * Ensures PUT request fails with an incorrect object ID.
     * Expected: 404 Not Found.
     */
    void testCantPutWithIncorrectId();
}
