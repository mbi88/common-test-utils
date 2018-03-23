package entity;

import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import org.apache.commons.lang3.Validate;

/**
 * Base controller class.
 */
public abstract class Controller {

    /**
     * Http request builder.
     */
    protected final HttpRequest http = new RequestBuilder();

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
     * @throws NullPointerException if object was not initialized.
     */
    public Object getId() {
        Validate.notNull(this.id, this.getClass().getSimpleName() + ": Object id is not initialized");
        return this.id;
    }

    /**
     * Set object id.
     *
     * @param id object id.
     */
    protected void setId(final Object id) {
        this.id = id;
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
