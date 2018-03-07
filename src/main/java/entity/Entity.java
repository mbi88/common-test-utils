package entity;

import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Entity<T> {

    protected HttpRequest http = new RequestBuilder();
    protected Response response;
    private String id;

    protected Entity(final String id) {
        this.id = id;
    }

    protected Entity() {
    }

    public String getId() {
        return !Objects.isNull(this.id) ? this.id : this.response.path("id").toString();
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

    public final class QueryParameters {

        private final Map<String, String> params = new HashMap<>();

        public void addParameter(final String parameter, final String value) {
            this.params.put(parameter, value);
        }

        public String getParametersString() {
            String tmp = params.entrySet().stream()
                    .map(m -> m.getKey().concat("=").concat(m.getValue()).concat("&"))
                    .collect(Collectors.joining());

            return "?" + tmp.substring(0, tmp.length() - 1);
        }
    }
}
