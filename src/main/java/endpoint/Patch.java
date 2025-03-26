package endpoint;

/**
 * Contract for testing PATCH endpoint behavior.
 */
public interface Patch {

    /**
     * Tests successful update of an existing object.
     */
    void testCanPatch();

    /**
     * Ensures PATCH request fails when object ID is missing in the URL.
     * Expected: 404 Not Found.
     */
    void testCantPatchWithoutId();

    /**
     * Ensures PATCH request fails with an incorrect object ID.
     * Expected: 404 Not Found.
     */
    void testCantPatchWithIncorrectId();

    /**
     * Ensures no fields become null after applying a patch.
     */
    void testNothingChangedAfterPatch();
}
