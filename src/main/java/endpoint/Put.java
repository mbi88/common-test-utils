package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Put {

    /**
     * Check basic endpoint operations.
     */
    void testCanPut();

    /**
     * Perform request without object id in url.
     * Expected 404.
     */
    void testCantPutWithoutId();

    /**
     * Perform request with incorrect object id in url.
     * Expected 404.
     */
    void testCantPutWithIncorrectId();
}
