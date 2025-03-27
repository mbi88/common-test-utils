package config;

import com.mbi.request.RequestBuilder;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Common configuration utilities.
 */
public interface Configuration {

    Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    /**
     * Returns environment variable value by its name.
     *
     * @param varName environment variable name
     * @return variable value
     */
    default String readVar(final String varName) {
        final String value = System.getenv(varName);
        LOGGER.info("Reading env var '{}': {}", varName, value);
        return Objects.requireNonNullElse(value, "null");
    }

    /**
     * Reads parameter from Amazon EC2 Systems Manager (SSM).
     *
     * @param paramName parameter name
     * @return parameter value
     */
    default String readSsmParameter(final String paramName) {
        try (var client = SsmClient.builder().build()) {
            final var request = GetParameterRequest.builder()
                    .name(paramName)
                    .withDecryption(true)
                    .build();

            return client.getParameter(request)
                    .parameter()
                    .value();
        }
    }

    /**
     * Extracts environment prefix from API URL.
     *
     * @param apiUrl API URL
     * @return environment name
     */
    default String getCurrentEnv(final String apiUrl) {
        return Stream.of(apiUrl.split("://", 2)) // max 2 parts: [protocol, rest]
                .skip(apiUrl.contains("://") ? 1 : 0) // skip protocol part if present
                .map(part -> part.split("-")[0]) // take only prefix before first dash
                .findFirst()
                .orElse("");
    }

    /**
     * Checks if the API is available and returns its status.
     *
     * @param apiUrl API URL
     * @return HTTP response
     */
    default Response getApiStatus(final String apiUrl) {
        try {
            return new RequestBuilder().setExpectedStatusCode(200).get(apiUrl);
        } catch (Throwable e) {
            throw new IllegalStateException("API is not available: " + apiUrl, e);
        }
    }
}
