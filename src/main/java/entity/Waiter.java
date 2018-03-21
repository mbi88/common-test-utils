package entity;

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
    private final String path;

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
     * @param builder          path and token in request builder.
     * @param waitingTimeInMin how many minutes water will wait until throw exception.
     */
    public Waiter(final RequestBuilder builder, final int waitingTimeInMin) {
        this.path = builder.getPath();
        this.token = builder.getToken();
        this.maxIteration = waitingTimeInMin * 60;
    }

    /**
     * @param expectedCondition condition.
     * @return result response.
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public Response waitCondition(final Predicate<Response> expectedCondition) {
        Response response = produceRequest();

        while (!expectedCondition.test(response)) {
            iteration++;
            if (iteration > maxIteration) {
                throw new Error("Expected conditions are not met. Max waiting time is exceeded");
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
        return new RequestBuilder().setToken(token).get(path);
    }

    /**
     * Sleep n seconds.
     *
     * @param ms milliseconds
     */
    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    private void wait(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
