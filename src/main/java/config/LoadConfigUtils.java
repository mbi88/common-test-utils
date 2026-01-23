package config;

import com.google.common.annotations.VisibleForTesting;
import com.mbi.request.RequestBuilder;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Common configuration utilities.
 */
public final class LoadConfigUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadConfigUtils.class);
    private static final Map<String, String> SSM_CACHE = new ConcurrentHashMap<>();

    private LoadConfigUtils() {
        // Prevent instantiation
    }

    /**
     * Reads a single SSM parameter, with caching.
     *
     * @param paramName parameter name
     * @return parameter value
     */
    public static String readSsmParameter(final String paramName) {
        return SSM_CACHE.computeIfAbsent(paramName, key -> {
            try (var client = ssmClient()) {
                final var request = GetParameterRequest.builder()
                        .name(key)
                        .withDecryption(true)
                        .build();

                return client.getParameter(request)
                        .parameter()
                        .value();
            } catch (Exception e) {
                LOGGER.error("Failed to read SSM parameter '{}': {}", key, e.toString());
                throw new IllegalStateException("Unable to read SSM parameter: " + key, e);
            }
        });
    }

    /**
     * Reads multiple SSM parameters in a batch, with caching.
     *
     * @param names list of parameter names
     * @return map of name → value
     */
    public static Map<String, String> readSsmParameters(final String... names) {
        // Collect missing keys
        final List<String> missingKeys = Arrays.stream(names)
                .filter(name -> !SSM_CACHE.containsKey(name))
                .toList();

        if (!missingKeys.isEmpty()) {
            try (var client = ssmClient()) {
                final var request = GetParametersRequest.builder()
                        .names(missingKeys)
                        .withDecryption(true)
                        .build();

                final var response = client.getParameters(request);
                response.parameters().forEach(p -> SSM_CACHE.put(p.name(), p.value()));

                if (!response.invalidParameters().isEmpty()) {
                    LOGGER.warn("Some SSM parameters not found: {}", response.invalidParameters());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to batch read SSM parameters: {}", e.toString());
                throw new IllegalStateException("Unable to batch read SSM parameters", e);
            }
        }

        // Build result map from cache
        return Arrays.stream(names)
                .filter(SSM_CACHE::containsKey)
                .collect(Collectors.toMap(name -> name, SSM_CACHE::get));
    }

    /**
     * Creates an instance of SSM client.
     *
     * @return SSM client
     */
    public static SsmClient ssmClient() {
        return SsmClient.builder().build();
    }

    /**
     * Returns environment variable value by its name.
     *
     * @param varName environment variable name
     * @return variable value
     */
    public static String readVar(final String varName) {
        final String value = System.getenv(varName);
        LOGGER.info("Reading env var '{}': {}", varName, value);
        return Objects.requireNonNullElse(value, "null");
    }

    /**
     * Extracts environment prefix from API URL.
     *
     * @param apiUrl API URL
     * @return environment name
     */
    public static String getCurrentEnv(final String apiUrl) {
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
    public static Response getApiStatus(final String apiUrl) {
        try {
            return new RequestBuilder().setExpectedStatusCode(200).get(apiUrl);
        } catch (Throwable e) {
            throw new IllegalStateException("API is not available: " + apiUrl, e);
        }
    }

    /**
     * For test purposes only.
     */
    @VisibleForTesting
    static void clearCacheForTests() {
        SSM_CACHE.clear();
    }

    /**
     * For test purposes only.
     */
    @VisibleForTesting
    static void putToCacheForTests(final String key, final String value) {
        SSM_CACHE.put(key, value);
    }
}
