package tests;

import com.mbi.request.RequestBuilder;
import controllers.*;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyData;
import org.testng.annotations.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.testng.Assert.*;
import static testcase.BaseTestCase.toJson;

public class ControllersTest {

    @Test
    public void testCanAddSameParameters() {
        QueryParameter parameter = new QueryParameter();
        parameter.addParameter("q", 1);
        parameter.addParameter("q", 1);

        assertEquals(parameter.getParametersString(), "?q=1&q=1");
    }

    @Test
    public void testCanSeveralParameters() {
        QueryParameter parameter = new QueryParameter();
        parameter.addParameter("q", 1);
        parameter.addParameter("w", 2);

        assertEquals(parameter.getParametersString(), "?q=1&w=2");
    }

    @Test
    public void testAddParametersViaConstructor() {
        QueryParameter parameter = new QueryParameter("q", 1);
        parameter.addParameter("w", 2);

        assertEquals(parameter.getParametersString(), "?q=1&w=2");
    }

    @Test
    public void testWaiterIfConditionIsMet() {
        Waiter<Response> waiter = new Waiter<>(() -> new RequestBuilder().get("https://www.google.com.ua/"),
                ResponseBodyData::asString,
                10);
        Predicate<Response> predicate = response -> response.statusCode() == 200;

        waiter.waitCondition(predicate);
    }

    @Test
    public void testWaiterIfConditionIsNoMet() {
        Waiter<Response> waiter = new Waiter<>(() -> new RequestBuilder().get("http://www.mocky.io/v2/5ab8a4952c00005700186093"),
                ResponseBodyData::asString,
                10);
        Predicate<Response> predicate = response -> response.statusCode() == 20;

        try {
            waiter.waitCondition(predicate);
        } catch (Throwable t) {
            assertTrue(t.getMessage().contains("Max waiting time exceeded"));
        }
    }

    @Test
    public void testWithIncorrectId() {
        class TestClass extends Controller<TestClass> {
        }
        TestClass testClass = new TestClass();

        assertTrue(testClass.withIncorrectId().getId().toString()
                .matches("^([0-9A-Fa-f]{8}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{4}[-][0-9A-Fa-f]{12})$"));
    }

    @Test
    public void testWithoutId() {
        TestClass testClass = new TestClass();

        assertEquals(testClass.withoutId().getId().toString().length(), 0);
    }

    @Test
    public void testSettingIdViaConstructor() {
        TestClass testClass = new TestClass(1);

        assertEquals(testClass.getId(), 1);
    }

    @Test
    public void testErrorMessageOnNullId() {
        TestClass testClass = new TestClass(null);

        boolean passed;
        try {
            testClass.getId();
            passed = true;
        } catch (NullPointerException ex) {
            passed = false;
            assertEquals(ex.getMessage(), "TestClass: Object id is not initialized");
        }
        assertFalse(passed);
    }

    @Test
    public void testExtractingIdFromResponse() {
        TestClass testClass = new TestClass();
        testClass.getResponse();

        assertEquals(testClass.getId(), 1);
    }

    class TestClass extends Controller<TestClass> implements Creatable {
        private final Function<Response, Integer> getIdFunction = response -> toJson(response).optInt("a");

        TestClass() {
        }

        TestClass(Integer id) {
            super(id);
        }

        Response getResponse() {
            Response response = http.get("http://www.mocky.io/v2/5ab8a4952c00005700186093");
            setId(extractId(response, getIdFunction));

            return response;
        }
    }
}
