package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Put {

    void testCanPut();

    void testCantPutWithoutId();

    void testCantPutWithIncorrectId();
}
