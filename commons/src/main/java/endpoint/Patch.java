package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Patch {

    /**
     * Check basic endpoint operations.
     */
    void testCanPatch();

    /**
     * Perform request without object id in url.
     * Expected 404.
     */
    void testCantPatchWithoutId();

    /**
     * Perform request with incorrect object id in url
     * Expected 404.
     */
    void testCantPatchWithIncorrectId();

    /**
     * Check there were no fields that became null after update.
     */
    void testNothingChangedAfterPatch();
}
