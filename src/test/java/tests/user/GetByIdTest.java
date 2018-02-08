package tests.user;

import endpoint.GetById;
import entity.User;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tests.TestCase;

public class GetByIdTest extends TestCase implements GetById {

    private User user = new User();
    private JSONObject userJson = getJsonFromFile("/jsons/user/default.json");

    @BeforeClass
    public void init() {
        user.post(userJson);
    }

    @Test
    public void testCanGetById() {
        Response r = user.get();

        assertion
                .ignore("id")
                .jsonEquals(r, userJson);
    }

    @Test
    public void testCantGetWithIncorrectId() {
        user.get(user.withIncorrectId(), 404);
    }

    @Test
    public void validateJson() {
        Response r = user.get();

        validator.validate(getJsonFromFile("/schemas/user/get.json"), r);
    }
}
