package config;

import org.json.JSONObject;

import java.util.Objects;

import static serializer.JsonDeserializer.getJsonFromFile;

/**
 * Tests configurations.
 */
public interface Configuration {

    default void readVar(final String varName) {
        System.out.println("Reading env var " + varName + ": ");
        String result = Objects.isNull(System.getenv(varName)) ? System.getenv(varName) : "null";
        System.out.println(result);
    }

    /**
     * Available environments.
     */
    enum Environment {
        QA1,
        QA2,
        TESTS,
        LOCAL;

        public JSONObject getConfig() {
            return getJsonFromFile(getConfigPath());
        }

        // Configuration files are in src/resources/configuration/
        private String getConfigPath() {
            return "/configuration/" + this.toString().toLowerCase() + ".json";
        }

    }

    @FunctionalInterface
    interface Configurable {
        void configure();
    }
}
