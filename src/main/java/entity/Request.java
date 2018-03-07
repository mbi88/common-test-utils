package entity;

import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;

import java.util.Objects;

/**
 * Base request class.
 *
 * @param <T> class inheritors.
 */
public abstract class Request<T> {

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
    private String id;

    /**
     * Is needed for creation objects with specified id.
     *
     * @param id object id.
     */
    protected Request(final String id) {
        this.id = id;
    }

    /**
     * Default constructor.
     */
    protected Request() {
    }

    /**
     * Returns object id.
     *
     * @return id
     */
    public String getId() {
        return !Objects.isNull(this.id) ? this.id : this.response.path("id").toString();
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
    private final class WithoutId extends Request<T> {
        /**
         * @return "".
         */
        public String getId() {
            return "";
        }
    }

    /**
     * Is needed for test cases that check performing requests with incorrect object id in URL.
     */
    private final class WithIncorrectId extends Request<T> {
        /**
         * @return UUID
         */
        public String getId() {
            return "09fe8a5c-b09a-4794-1741-08d42d809985";
        }
    }
}
