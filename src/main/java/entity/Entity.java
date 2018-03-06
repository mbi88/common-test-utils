package entity;

import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public abstract class Entity<T> {

    public HttpRequest http = new RequestBuilder();
    public Response response;
    private String id;

    public Entity(String id) {
        this.id = id;
    }

    public Entity() {
    }

    public String getId() {
        if (id != null) {
            return id;
        } else {
            return this.response.path("id").toString();
        }
    }

    public WithoutId withoutId() {
        return new WithoutId();
    }

    public WithIncorrectId withIncorrectId() {
        return new WithIncorrectId();
    }

    private class WithoutId extends Entity<T> {
        public String getId() {
            return "";
        }
    }

    private class WithIncorrectId extends Entity<T> {
        public String getId() {
            return "09fe8a5c-b09a-4794-1741-08d42d809985";
        }
    }

    public class QueryParameters {

        private Map<String, String> params = new HashMap<>();

        public void setParameter(String parameter, String value) {
            params.put(parameter, value);
        }

        public String getParametersString() {
            String result = "?";
            String tmp = "";

            for (String k : params.keySet()) {
                tmp = tmp.concat(k).concat("=").concat(params.get(k)).concat("&");
            }

            result = result.concat(tmp);

            return result.substring(0, result.length() - 1);
        }
    }
}
