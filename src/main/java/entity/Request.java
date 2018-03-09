package entity;

import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Base request class.
 *
 * @param <T> class inheritor.
 * @param <U> object id type.
 */
public abstract class Request<T, U> {

    /**
     * Http request builder.
     */
    protected final HttpRequest http = new RequestBuilder();
    /**
     * Response.
     */
    protected Response response;
    /**
     * Created object id.
     */
    private U id;

    /**
     * Default constructor.
     */
    protected Request() {
    }

    /**
     * Is needed for creation objects with specified id.
     *
     * @param id object id.
     */
    protected Request(final U id) {
        this.id = id;
    }

    /**
     * Returns object id.
     *
     * @return id
     */
    public U getId() {
        return !Objects.isNull(this.id) ? this.id : this.response.path("id");
    }


    /**
     * Converts response to json object.
     *
     * @param response rest-assured response
     * @return json object.
     */
    public JSONObject toJson(final Response response) {
        return new JSONObject(response.asString());
    }

    /**
     * Is needed for test cases that check performing requests without object id in URL.
     *
     * @return ""
     */
    public final WithoutId withoutId() {
        return new WithoutId();
    }

    /**
     * Is needed for test cases that check performing requests with incorrect object id in URL.
     *
     * @return UUID
     */
    public final WithIncorrectId withIncorrectId() {
        return new WithIncorrectId();
    }

    /**
     * Is needed for test cases that check performing requests without object id in URL.
     */
    private final class WithoutId extends Request<T, U> {
        /**
         * @return "".
         */
        public U getId() {
            return (U) "";
        }
    }

    /**
     * Is needed for test cases that check performing requests with incorrect object id in URL.
     */
    private final class WithIncorrectId extends Request<T, U> {
        /**
         * @return UUID
         */
        public U getId() {
            return (U) "09fe8a5c-b09a-4794-1741-08d42d809985";
        }
    }
}
