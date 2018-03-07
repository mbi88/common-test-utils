package entity;

import com.mbi.RequestBuilder;
import io.restassured.response.Response;

import java.util.function.Predicate;

public final class Waiter {

    private final String path;
    private final String token;
    private final int MAX_ITERATION;
    private int iteration = 0;

    public Waiter(final RequestBuilder builder, final int waitingTimeInMin) {
        this.path = builder.getPath();
        this.token = builder.getToken();
        this.MAX_ITERATION = waitingTimeInMin * 60;
    }

    public Response waitCondition(final Predicate<Response> expectedCondition) {
        Response response = produceRequest();

        while (!expectedCondition.test(response)) {
            iteration++;
            if (iteration > MAX_ITERATION) {
                throw new Error("Expected conditions are not met. Max waiting time is exceeded");
            }

            response = produceRequest();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        return response;
    }

    private Response produceRequest() {
        return new RequestBuilder().setToken(token).get(path);
    }
}
