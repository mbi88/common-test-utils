package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface GetById {

    /**
     * Check basic endpoint operations.
     */
    void testCanGetById();

    /**
     * Perform request with incorrect object id in url.
     * Expected 404.
     */
    void testCantGetWithIncorrectId();

    /**
     * Validate response according to specified schema.
     */
    void validateJson();
}
