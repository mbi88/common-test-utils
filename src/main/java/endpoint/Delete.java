package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Delete {

    void testCanDelete();

    void testCantDeleteTwice();

    void testCantDeleteWithoutId();

    void testCantDeleteWithIncorrectId();
}
