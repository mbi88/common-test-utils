package entity;

import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import io.restassured.response.Response;

import java.util.Objects;

/**
 * Base controller class.
 */
public abstract class Controller {

    /**
     * Http request builder.
     */
    protected final HttpRequest http = new RequestBuilder();

    /**
     * Rest-assured response.
     */
    protected Response response;

    /**
     * Created object id.
     */
    private Object id;

    /**
     * Default constructor.
     */
    protected Controller() {
    }

    /**
     * Creation objects with specified id.
     *
     * @param id object id.
     */
    protected Controller(final Object id) {
        this.id = id;
    }

    /**
     * Returns object id.
     *
     * @return id
     */
    public Object getId() {
        return !Objects.isNull(this.id) ? this.id : this.response.path("id");
    }

    /**
     * Is needed for test cases that check performing requests without object id in URL.
     *
     * @return ""
     */
    public final Controller withoutId() {
        return new Controller() {
            @Override
            public Object getId() {
                return "";
            }
        };
    }

    /**
     * Is needed for test cases that check performing requests with incorrect object id in URL.
     *
     * @return UUID
     */
    public final Controller withIncorrectId() {
        return new Controller() {
            @Override
            public Object getId() {
                return "09fe8a5c-b09a-4794-1741-08d42d809985";
            }
        };
    }
}
