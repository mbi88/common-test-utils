package config;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static serializer.JsonDeserializer.getJsonFromFile;

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
        log.info(result);

        return result;
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
     * Available environments.
     */
    enum Environment {
        QA1,
        QA2,
        TESTS,
        LOCAL;

        /**
         * Reads config from 'src/resources/configuration/' and returns as a JSONObject.
         *
         * @return config.
         */
        public JSONObject getConfig() {
            return getJsonFromFile(getConfigPath());
        }

        // Configuration files are expected in src/resources/configuration/
        private String getConfigPath() {
            return "/configuration/" + this.toString().toLowerCase() + ".json";
        }

    }

    /**
     * Configure the system with system environment variables or read from json config file.
     */
    @FunctionalInterface
    interface Configurable {
        void configure();
    }
}
