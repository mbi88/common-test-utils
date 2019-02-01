package controllers;

import com.mbi.request.RequestBuilder;
import io.restassured.response.Response;
import org.joda.time.DateTime;

import java.util.function.Predicate;

/**
 * Waiting for special object condition. Condition is checked every 1 sec.
 */
public final class Waiter {

    /**
     * Request URL.
     */
    private final String url;

    /**
     * Request token.
     */
    private final String token;

    /**
     * Time in minutes to be spent on waiting of condition.
     */
    private final int waitingTimeInMin;

    /**
     * Waiter constructor.
     *
     * @param builder          url and token in request builder.
     * @param waitingTimeInMin how many minutes waiter will wait until throw exception.
     */
    public Waiter(final RequestBuilder builder, final int waitingTimeInMin) {
        this.url = builder.getUrl();
        this.token = builder.getToken();
        this.waitingTimeInMin = waitingTimeInMin;
    }

    /**
     * @param expectedCondition condition.
     * @return result response.
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public Response waitCondition(final Predicate<Response> expectedCondition) {
        Response response = produceRequest();
        final long startTime = DateTime.now().getMillis();

        while (!expectedCondition.test(response)) {
            if (!waiting(startTime)) {
                throw new Error(String.format(
                        "Expected conditions are not met. Max waiting time is exceeded%nUrl: %s%nResponse: %s%n",
                        this.url,
                        response.asString()));
            }

            response = produceRequest();
        }

        return response;
    }

    /**
     * Generates a request.
     *
     * @return response of request.
     */
    private Response produceRequest() {
        return new RequestBuilder().setToken(token).get(url);
    }

    /**
     * Whether waiter should sleep or throw exception.
     * Sleep 1 second.
     *
     * @param startTime time before first request.
     */
    private boolean waiting(final long startTime) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
            // Ignored
        }

        return (startTime + waitingTimeInMin * 60 * 1000L) > DateTime.now().getMillis();
    }
}
