package controllers;

import com.mbi.RequestBuilder;
import io.restassured.response.Response;

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
     * Max iterations count.
     */
    private final int maxIteration;

    /**
     * Iteration.
     */
    private int iteration;

    /**
     * Waiter constructor.
     *
     * @param builder          url and token in request builder.
     * @param waitingTimeInMin how many minutes water will wait until throw exception.
     */
    public Waiter(final RequestBuilder builder, final int waitingTimeInMin) {
        this.url = builder.getUrl();
        this.token = builder.getToken();
        this.maxIteration = waitingTimeInMin * 60;
    }

    /**
     * @param expectedCondition condition.
     * @return result response.
     */
    public Response waitCondition(final Predicate<Response> expectedCondition) {
        Response response = produceRequest();

        while (!expectedCondition.test(response)) {
            iteration++;
            if (iteration > maxIteration) {
                throw new Error(String.format(
                        "Expected conditions are not met. Max waiting time is exceeded%nUrl: %s%nResponse: %s%n",
                        this.url,
                        response.asString()));
            }

            response = produceRequest();
            wait(1000);
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
     * Sleep n seconds.
     *
     * @param ms milliseconds
     */
    private void wait(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            // Ignored
        }
    }
}
