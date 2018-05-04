package config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersResult;
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

    /**
     * Reads parameters from Amazon EC2 Systems Manager (SSM).
     *
     * @param paramNames parameters names.
     * @return parameters and its values.
     */
    default Map<String, String> readSsmParameters(final String... paramNames) {
        final AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
        final GetParametersRequest request = new GetParametersRequest();
        request.withNames(paramNames).setWithDecryption(true);
        final GetParametersResult result = client.getParameters(request);

        final Map<String, String> store = new HashMap<>();
        result.getParameters().forEach(parameter -> store.put(parameter.getName(), parameter.getValue()));

        return store;
    }

    /**
     * Reads parameter from Amazon EC2 Systems Manager (SSM).
     *
     * @param paramName parameters names.
     * @return parameters and its values.
     */
    default String readSsmParameter(final String paramName) {
        final AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
        final GetParametersRequest request = new GetParametersRequest();
        request.withNames(paramName).setWithDecryption(true);
        final GetParametersResult result = client.getParameters(request);

        final AtomicReference<String> value = new AtomicReference<>();
        result.getParameters().forEach(parameter -> value.set(parameter.getValue()));

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
}
