package tests;

import com.mbi.request.RequestBuilder;
import controllers.Controller;
import controllers.Creatable;
import controllers.QueryParameter;
import controllers.Waiter;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.function.Function;

import static org.testng.Assert.*;
import static testcase.BaseTestCase.toJson;

public class ControllersTest {

    @Test
    public void testCanAddSameParameters() {
        var parameter = new QueryParameter();
        parameter.addParameter("q", 1);
        parameter.addParameter("q", 1);

        assertEquals(parameter.getParametersString(), "?q=1&q=1");
    }

    @Test
    public void testCanSeveralParameters() {
        var parameter = new QueryParameter();
        parameter.addParameter("q", 1);
        parameter.addParameter("w", 2);

        assertEquals(parameter.getParametersString(), "?q=1&w=2");
    }

    @Test
    public void testAddParametersViaConstructor() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("w", 2);

        assertEquals(parameter.getParametersString(), "?q=1&w=2");
    }

    @Test
    public void testCanRemoveAllParameters() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("q", 2);

        parameter.removeParameter("q");

        assertEquals(parameter.getParametersString(), "");
    }

    @Test
    public void testCanRemoveParameterByValue() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("q", 2);
        parameter.addParameter("w", 1);

        parameter.removeParameter("q", 1);

        assertEquals(parameter.getParametersString(), "?q=2&w=1");
    }

    @Test
    public void testCanRemoveParameter() {
        var parameter = new QueryParameter("q", 1);
        parameter.addParameter("q", 2);
        parameter.addParameter("w", 1);

        parameter.removeParameter("q");

        assertEquals(parameter.getParametersString(), "?w=1");
    }

    @Test
    public void testWaiterIfConditionIsMet() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a/"))
                .setResultToString(Response::asString)
                .setWaitingTime(10)
                .build();

        waiter.waitCondition(response -> response.statusCode() == 200, "response -> response.statusCode() == 200");
    }

    @Test
    public void testWaiterIfConditionIsNoMet() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a/"))
                .setResultToString(Response::asString)
                .setWaitingTime(10)
                .build();

        try {
            waiter.waitCondition(response -> response.statusCode() == 20, "response.statusCode() == 20");
        } catch (RuntimeException t) {
            assertTrue(t.getMessage().contains("Max waiting time exceeded"));
        }
    }

    @Test
    public void testCanSetIdleDuration() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a//"))
                .setResultToString(Response::asString)
                .setWaitingTime(10)
                .setIdleDuration(5000)
                .build();

        try {
            waiter.waitCondition(response -> response.statusCode() == 20);
        } catch (RuntimeException t) {
            assertTrue(t.getMessage().contains("Max waiting time exceeded"));
        }
    }

    @Test
    public void testCanSetDebug() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a//"))
                .setResultToString(Response::asString)
                .setWaitingTime(3)
                .setDebug(true)
                .build();

        try {
            waiter.waitCondition(response -> response.statusCode() == 20);
        } catch (RuntimeException t) {
            assertTrue(t.getMessage().contains("Max waiting time exceeded"));
        }
    }

    @Test
    public void testCanReadWaiterResponse() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a/"))
                .setResultToString(Response::asString)
                .setWaitingTime(10)
                .build();

        var r = waiter.waitCondition(response -> response.statusCode() == 200);

        assertEquals(toJson(r).getInt("a"), 1);
    }

    @Test
    public void testWithIncorrectId() {
        class TestClass extends Controller<TestClass> {
        }
        var testClass = new TestClass();

        assertTrue(testClass.withIncorrectId().getId().toString()
                .matches("^([0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12})$"));
    }

    @Test
    public void testWithoutId() {
        var testClass = new TestClass();

        assertEquals(testClass.withoutId().getId().toString().length(), 0);
    }

    @Test
    public void testSettingIdViaConstructor() {
        var testClass = new TestClass(1);

        assertEquals(testClass.getId(), 1);
    }

    @Test
    public void testErrorMessageOnNullId() {
        var testClass = new TestClass(null);

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
        var testClass = new TestClass();
        testClass.getResponse();

        assertEquals(testClass.getId(), 1);
    }

    static class TestClass extends Controller<TestClass> implements Creatable {
        private final Function<Response, Integer> getIdFunction = response -> toJson(response).optInt("a");

        TestClass() {
        }

        TestClass(Integer id) {
            super(id);
        }

        Response getResponse() {
            Response response = http.get("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a");
            setId(extractId(response, getIdFunction));

            return response;
        }
    }

    @Test
    public void testCanSetTimeExceededMessage() {
        var waiter = Waiter.<Response>newBuilder()
                .setSupplier(() -> new RequestBuilder().get("https://run.mocky.io/v3/c4da5edc-27e6-4fe3-92d6-92d9e6ddf36a//"))
                .setResultToString(Response::asString)
                .setWaitingTime(3)
                .setDebug(true)
                .setTimeExceededMessage("hello! time exceeded")
                .build();

        try {
            waiter.waitCondition(response -> response.statusCode() == 20);
        } catch (RuntimeException t) {
            t.printStackTrace();
            assertTrue(t.getMessage().contains("hello! time exceeded"));
        }
    }
}
