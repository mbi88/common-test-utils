package app;

import com.mbi.RequestBuilder;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static app.auth.Tokens.DEFAULT_TOKEN;
import static app.serializer.JsonDeserializer.getJsonFromFile;

public final class Configuration {

    // Set if need to override values from system environment variables
    private static final Environment DEFAULT_ENVIRONMENT = null;
    static String apiUrl;
    static String secret;
    static boolean isBase64Encoded;
    private static volatile Configuration instance;

    private Configuration() {
    }

    public static Configuration getInstance() {
        if (instance == null) {
            synchronized (Configuration.class) {
                if (instance == null)
                    instance = new Configuration();
            }
        }

        return instance;
    }

    public static String getCurrentEnv() {
        return Stream.of(apiUrl.split("://"))
                .filter(s -> !s.contains("http"))
                .map(s -> s.split("-"))
                .map(string -> string[0])
                .collect(Collectors.joining());
    }

    public void printConfig() {
        System.out.println();
        System.out.println("-----------------------");
        System.out.println("API URL:              " + apiUrl);
        System.out.println("TOKEN:                " + DEFAULT_TOKEN);
        Response apiStatus = new RequestBuilder().setExpectedStatusCode(200).get(apiUrl + "v1/health/all");
        System.out.println("API STATUS:           " + apiStatus.asString());
        Response rpStatus = new RequestBuilder().get("http://report-portal.srv.ap.priv/");
        System.out.println("ReportPortal status:  " + rpStatus.getStatusCode());
        System.out.println("------------------------");
        System.out.println();
    }

    @SuppressWarnings("ConstantConditions")
    public void configure() {
        Configurable c = (DEFAULT_ENVIRONMENT == null) ? new SysVarsConfigurator() : new JsonsConfigurator();
        c.configure();
    }

    private enum Environment {
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

    private interface Configurable {
        void configure();
    }

    private static class SysVarsConfigurator implements Configurable {

        @Override
        public void configure() {
            secret = System.getenv("API__AUTH__SECRETKEY");
            isBase64Encoded = Boolean.valueOf(System.getenv("API__AUTH__BASE64"));
            apiUrl = System.getenv("API__ENDPOINT");
        }
    }

    private static class JsonsConfigurator implements Configurable {

        @SuppressWarnings("ConstantConditions")
        @Override
        public void configure() {
            secret = DEFAULT_ENVIRONMENT.getConfig().getJSONObject("auth").getString("secret");
            isBase64Encoded = DEFAULT_ENVIRONMENT.getConfig().getJSONObject("auth").getBoolean("base64");
            apiUrl = DEFAULT_ENVIRONMENT.getConfig().getJSONObject("urls").getString("api");
        }
    }
}
