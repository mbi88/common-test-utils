package serializer;

import java.io.InputStream;

/**
 * Utility class for loading JSON resources from the classpath.
 * <p>
 * This class is intentionally extracted from {@link JsonDeserializer} to allow for better testability.
 * In unit tests, methods of this class can be mocked using frameworks such as Mockito:
 *
 * <pre>{@code
 * try (MockedStatic<JsonResourceLoader> mocked = mockStatic(JsonResourceLoader.class)) {
 *     mocked.when(() -> JsonResourceLoader.getResourceAsStream("/jsons/file.json"))
 *           .thenReturn(mockedInputStream);
 *     // Test logic here
 * }
 * }</pre>
 *
 * <p>
 * Having a dedicated class ensures that static resource-loading logic is test-friendly
 * and not tightly coupled with core deserialization logic.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * InputStream inputStream = JsonResourceLoader.getResourceAsStream("/jsons/example.json");
 * }</pre>
 */
public final class JsonResourceLoader {

    private JsonResourceLoader() {
        // Utility class, no instantiation
    }

    /**
     * Loads a file from the classpath as an {@link InputStream}.
     *
     * @param path path to the resource (e.g., "/jsons/example.json")
     * @return InputStream of the resource, or null if not found
     */
    public static InputStream getResourceAsStream(final String path) {
        return JsonDeserializer.class.getResourceAsStream(path);
    }
}
