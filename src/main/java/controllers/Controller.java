package controllers;

import com.mbi.HttpRequest;
import com.mbi.request.RequestBuilder;
import org.apache.commons.lang3.Validate;

import static testcase.BaseTestCase.getRandomUID;

/**
 * Abstract base controller class for managing API resources.
 *
 * @param <T> the type of the controller
 */
public abstract class Controller<T> {

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
        // This constructor is intentionally empty. Nothing special is needed here.
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
     * Returns a controller with empty ID (for testing endpoints without ID).
     *
     * @return ""
     */
    public final Controller<T> withoutId() {
        return new Controller<T>() {
            @Override
            public Object getId() {
                return "";
            }
        };
    }

    /**
     * Returns a controller with a random UUID as ID (for testing invalid ID cases).
     *
     * @return UUID
     */
    public final Controller<T> withIncorrectId() {
        return new Controller<T>() {
            @Override
            public Object getId() {
                return getRandomUID();
            }
        };
    }
}
