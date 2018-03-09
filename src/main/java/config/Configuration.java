package config;

import org.json.JSONObject;

import java.util.Objects;

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
        System.out.println("Reading env var " + varName + ": ");
        String result = Objects.isNull(System.getenv(varName)) ? "null" : System.getenv(varName);
        System.out.println(result);

        return result;
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

    @FunctionalInterface
    interface Configurable {
        void configure();
    }
}
