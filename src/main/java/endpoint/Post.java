package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Post {

    void testCanPost();

    void testPostOnlyRequiredFields();

    void testLocationHeader();

    void validateJson();
}
