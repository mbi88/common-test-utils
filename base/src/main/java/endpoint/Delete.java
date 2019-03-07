package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Delete {

    /**
     * Check basic endpoint operations.
     */
    void testCanDelete();

    /**
     * Check there is impossible to delete already deleted object.
     * Expected 404.
     */
    void testCantDeleteTwice();

    /**
     * Perform request without object id in url.
     * Expected 404.
     */
    void testCantDeleteWithoutId();

    /**
     * Perform request with incorrect object id in url.
     * Expected 404.
     */
    void testCantDeleteWithIncorrectId();
}
