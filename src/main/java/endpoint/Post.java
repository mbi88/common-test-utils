package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Post {

    /**
     * Check basic endpoint operations.
     */
    void testCanPost();

    /**
     * Check object creation with required fields only.
     */
    void testPostOnlyRequiredFields();

    /**
     * Check content of Location header.
     */
    void testLocationHeader();

    /**
     * Validate response according to specified schema.
     */
    void validateJson();
}
