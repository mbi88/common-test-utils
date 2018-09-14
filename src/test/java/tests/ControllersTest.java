package tests;

import com.mbi.request.RequestBuilder;
import controllers.Controller;
import controllers.QueryParameter;
import controllers.Waiter;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.function.Predicate;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
        RequestBuilder httpRequest = new RequestBuilder();

        httpRequest.setUrl("https://www.google.com.ua/");
        Waiter waiter = new Waiter(httpRequest, 0);
        Predicate<Response> predicate = response -> response.statusCode() == 200;

        waiter.waitCondition(predicate);
    }

    @Test
    public void testWaiterIfConditionIsNoMet() {
        RequestBuilder httpRequest = new RequestBuilder();

        httpRequest.setUrl("https://www.google.com.ua/");
        Waiter waiter = new Waiter(httpRequest, 0);
        Predicate<Response> predicate = response -> response.statusCode() == 20;

        try {
            waiter.waitCondition(predicate);
        } catch (Throwable t) {
            assertTrue(t.getMessage().contains("Max waiting time is exceeded"));
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
        class TestClass extends Controller<TestClass> {
        }
        TestClass testClass = new TestClass();

        assertEquals(testClass.withoutId().getId().toString().length(), 0);
    }
}
