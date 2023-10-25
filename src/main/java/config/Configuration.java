package config;

import com.mbi.request.RequestBuilder;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

import java.util.Objects;
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
     * Reads parameter from Amazon EC2 Systems Manager (SSM).
     *
     * @param paramName parameters names.
     * @return parameter value.
     */
    default String readSsmParameter(final String paramName) {
        final var client = SsmClient.builder().build();
        final var request = GetParameterRequest.builder()
                .name(paramName)
                .withDecryption(true)
                .build();

        final var parameterResponse = client.getParameter(request);

        return parameterResponse.parameter().value();
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

    /**
     * Returns API status.
     *
     * @param apiUrl api url.
     * @return response.
     */
    default Response getApiStatus(final String apiUrl) {
        try {
            return new RequestBuilder().setExpectedStatusCode(200).get(apiUrl);
        } catch (Throwable e) {
            e.addSuppressed(new RuntimeException("API is not available: " + apiUrl));
            throw e;
        }
    }
}
