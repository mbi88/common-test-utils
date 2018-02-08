package endpoint;

/**
 * Created by mbi on 8/15/16.
 */
public interface Patch {

    void testCanPatch();

    void testCantPatchWithoutId();

    void testCantPatchWithIncorrectId();

    void testNothingChangedAfterPatch();
}
