package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface GetList {

    /**
     * Check basic endpoint operations.
     */
    void testCanGetList();

    /**
     * Validate response according to specified schema.
     */
    void validateJson();

    /**
     * Check sorting order.
     */
    void testSortOrder();
}
