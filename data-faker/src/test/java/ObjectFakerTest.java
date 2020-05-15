import com.mbi.Faker;
import com.mbi.ObjectFaker;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ObjectFakerTest {

    private final Faker faker = new ObjectFaker();

    @Test
    public void testSeveralParameters() {
        var s = faker.fakeData("asd {$uid} asd {$caller} ddd");

        assertFalse(s.contains("{$"));
        assertTrue(s.contains(" asd ObjectFakerTest.testSeveralParameters ddd"));
    }

    @Test
    public void testNoParameters() {
        var s = faker.fakeData("asd ddd");

        assertEquals(s, "asd ddd");
    }

    @Test
    public void testFakeNotString() {
        int i = faker.fakeData(1);

        assertEquals(i, 1);
    }

    @Test
    public void testNoParameterEnding() {
        boolean passed;
        try {
            faker.fakeData("asd} {$uid");
            passed = true;
        } catch (IllegalArgumentException e) {
            passed = false;
            assertEquals(e.getMessage(), "Incorrect parameter: asd} {$uid");
        }
        assertFalse(passed);
    }
}
