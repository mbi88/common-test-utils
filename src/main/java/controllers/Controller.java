package controllers;

import com.mbi.HttpRequest;
import com.mbi.RequestBuilder;
import org.apache.commons.lang3.Validate;

import static testcase.BaseTestCase.getRandomUID;

/**
 * Base controller class.
 *
 * @param <T> controller inheritor.
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
     * Is needed for test cases that check performing requests without object id in URL.
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
     * Is needed for test cases that check performing requests with incorrect object id in URL.
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
