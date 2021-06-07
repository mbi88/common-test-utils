package config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersResult;
import com.mbi.request.RequestBuilder;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests configuration.
 */
public interface Configuration {

    /**
     * Returns environment variable value by its name.
     *
     * @param varName environment variable name.
     * @return variable value.
     */
    default String readVar(final String varName) {
        final Logger log = LoggerFactory.getLogger(Configuration.class);
        log.info("Reading env var " + varName + ": ");
        final String result = Objects.isNull(System.getenv(varName)) ? "null" : System.getenv(varName);
        log.info(result + "\n");

        return result;
    }

    private GetParametersResult getSsmParameters(final String... paramNames) {
        final var client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
        final var request = new GetParametersRequest();
        request.withNames(paramNames).setWithDecryption(true);

        return client.getParameters(request);
    }

    /**
     * Reads parameters from Amazon EC2 Systems Manager (SSM).
     *
     * @param paramNames parameters names.
     * @return parameters and its values.
     */
    default Map<String, String> readSsmParameters(final String... paramNames) {
        final Map<String, String> store = new HashMap<>();
        getSsmParameters(paramNames)
                .getParameters()
                .forEach(parameter -> store.put(parameter.getName(), parameter.getValue()));

        return store;
    }

    /**
     * Reads parameter from Amazon EC2 Systems Manager (SSM).
     *
     * @param paramName parameters names.
     * @return parameters and its values.
     */
    default String readSsmParameter(final String paramName) {
        final var value = new AtomicReference<>();
        getSsmParameters(paramName)
                .getParameters()
                .forEach(parameter -> value.set(parameter.getValue()));

        return value.toString();
    }

    /**
     * Returns current environment.
     *
     * @param apiUrl url of tested API.
     * @return environment prefix.
     */
    default String getCurrentEnv(final String apiUrl) {
        return Stream.of(apiUrl.split("://"))
                .filter(s -> !s.contains("http"))
                .map(s -> s.split("-"))
                .map(string -> string[0])
                .collect(Collectors.joining());
    }

    default Response getApiStatus(final String apiUrl) {
        try {
            return new RequestBuilder().setExpectedStatusCode(200).get(apiUrl);
        } catch (Throwable e) {
            e.addSuppressed(new RuntimeException("API is not available: " + apiUrl));
            throw e;
        }
    }
}
