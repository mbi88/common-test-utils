package entity;

import com.mbi.RequestBuilder;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.function.Predicate;

import static app.Constants.USERS_URL;
import static app.auth.Tokens.DEFAULT_TOKEN;

public class User extends Entity<User> {

    public User(String id) {
        super(id);
    }

    public User() {
    }

    public Response post(JSONObject userJson) {
        return http
                .setData(userJson)
                .setExpectedStatusCode(201)
                .setToken(DEFAULT_TOKEN)
                .post(USERS_URL);
    }

    public Response get() {
        return http
                .setExpectedStatusCode(200)
                .setToken(DEFAULT_TOKEN)
                .get(USERS_URL + this.getId());
    }

    public Response get(Entity<User> user, int code) {
        return http
                .setExpectedStatusCode(code)
                .setToken(DEFAULT_TOKEN)
                .get(USERS_URL + user.getId());
    }

    public Response get(Entity<User> user) {
        RequestBuilder rb = new RequestBuilder();
        rb
                .setToken(DEFAULT_TOKEN)
                .setPath(USERS_URL + user.getId());

        Waiter waiter = new Waiter(rb);
        Predicate<Response> predicate = resp -> !new JSONObject(resp.asString())
                .get("statistics")
                .toString()
                .equalsIgnoreCase("null");

        return waiter.waitCondition(predicate);
    }
}
