package testcase;

import com.mbi.*;
import com.mbi.request.RequestBuilder;
import io.restassured.response.Response;
import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.IConfigurable;
import org.testng.IConfigureCallBack;
import org.testng.ITestResult;
import serializer.JsonDeserializer;

import java.util.Random;
import java.util.UUID;


/**
 * Abstract base class for test cases.
 */
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public abstract class BaseTestCase implements IConfigurable {

    private static final Random RANDOM = new Random();

    /**
     * Http requests based on rest-assured framework.
     */
    protected final HttpRequest http = new RequestBuilder();

    /**
     * Json equality assertion.
     */
    protected final JsonAssert assertion = new JsonAssert();

    /**
     * Json validation based on json schema validation.
     */
    protected final JsonValidator validator = new JsonValidator();

    /**
     * Different operations with dates.
     */
    protected final DateHandler dateHandler = new DateHandler();

    /**
     * Json faker.
     */
    protected final JsonFaker jsonFaker = new JsonFaker();

    /**
     * Returns random long number with given digit count.
     *
     * @param count digits count
     * @return number
     */
    public static long getRandomNum(final int count) {
        // Valid range
        final int minDigits = 1;
        final int maxDigits = 18;
        // Validate digits count is in a supported range
        Validate.inclusiveBetween(minDigits, maxDigits, count,
                String.format("Value %d is not in the supported range [%d, %d]", count, minDigits, maxDigits));

        final StringBuilder number = new StringBuilder();
        number.append(RANDOM.nextInt(9) + 1); // ensure first digit is non-zero

        for (int i = 1; i < count; i++) {
            number.append(RANDOM.nextInt(10));
        }

        return Long.parseLong(number.toString());
    }

    /**
     * Returns random long number with 13 digits.
     *
     * @return random long value
     */
    public static long getRandomNum() {
        return getRandomNum(13);
    }

    /**
     * Generates random UUID string.
     *
     * @return UUID as string
     */
    public static String getRandomUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Converts Rest-Assured response to JSONObject.
     *
     * @param response HTTP response
     * @return JSONObject representation
     */
    public static JSONObject toJson(final Response response) {
        return new JSONObject(response.asString());
    }

    /**
     * Converts Rest-Assured response to JSONArray.
     *
     * @param response HTTP response
     * @return JSONArray representation
     */
    public static JSONArray toJsonArray(final Response response) {
        return new JSONArray(response.asString());
    }

    /**
     * Loads JSON object from resource file.
     *
     * @param path path to resource file (relative to classpath). No need to add "src/main/resources" every time when
     *             you pass the path - it is already implemented in the method.
     * @return loaded JSONObject
     */
    protected final JSONObject getResource(final String path) {
        return JsonDeserializer.getResource(path);
    }

    /**
     * Loads JSON array from resource file.
     *
     * @param path path to resource file (relative to classpath). No need to add "src/main/resources" every time when
     *             you pass the path - it is already implemented in the method.
     * @return loaded JSONArray
     */
    protected final JSONArray getResources(final String path) {
        return JsonDeserializer.getResources(path);
    }

    /**
     * Finds object in array by field name and value.
     *
     * @param sourceArray source array
     * @param name        field name
     * @param value       field value to match
     * @return matching object
     */
    protected final JSONObject findJsonInArray(final JSONArray sourceArray, final String name, final Object value) {
        return JsonDeserializer.findJsonInArray(sourceArray, name, value);
    }

    /**
     * Supports @Retryable annotation.
     */
    @Override
    public void run(final IConfigureCallBack callBack, final ITestResult testResult) {
        final var retryable = testResult.getMethod()
                .getConstructorOrMethod().getMethod().getAnnotation(Retryable.class);
        final int attempts = retryable == null ? 0 : retryable.attempts();

        for (int attempt = 0; attempt < attempts; attempt++) {
            callBack.runConfigurationMethod(testResult);
            if (testResult.getThrowable() == null) {
                break;
            }
        }
    }
}
