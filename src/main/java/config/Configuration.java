package config;

import org.json.JSONObject;

import static serializer.JsonDeserializer.getJsonFromFile;

public interface Configuration {

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
