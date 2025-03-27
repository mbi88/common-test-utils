[![Java CI with Gradle](https://github.com/mbi88/common-test-utils/actions/workflows/gradle.yml/badge.svg)](https://github.com/mbi88/common-test-utils/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/mbi88/common-test-utils/branch/master/graph/badge.svg)](https://codecov.io/gh/mbi88/common-test-utils)
[![Latest Version](https://img.shields.io/github/v/tag/mbi88/common-test-utils?label=version)](https://github.com/mbi88/common-test-utils/releases)
[![jitpack](https://jitpack.io/v/mbi88/common-test-utils.svg)](https://jitpack.io/#mbi88/common-test-utils)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

# common-test-utils

Lightweight test automation framework that combines 5 separate libraries into one:

| [json-assert](https://github.com/mbi88/json-assert) | [json-validator](https://github.com/mbi88/json-validator) | [date-handler](https://github.com/mbi88/date-handler) | [http-request](https://github.com/mbi88/http-request) | [data-faker](https://github.com/mbi88/data-faker) |
|---|---|---|---|---|

Modular test utility library that unifies JSON assertions, schema validation, date handling, HTTP testing, fake data generation, retry mechanisms, and more — all in one place.

Built to extend TestNG-based test automation in a clean, readable, and flexible way.


---

## Features

✅ JSON comparison and assertion  
✅ Schema validation  
✅ Fake data injection  
✅ Fluent HTTP and GraphQL requests  
✅ Date manipulation using formulas  
✅ Retryable configuration  
✅ Waiter utility with polling logic  
✅ Token generation  
✅ Query builder  
✅ TestNG base test class and listener

---

## Installation

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation("com.github.mbi88:common-test-utils:master-SNAPSHOT")
}
```
</details>

<details>
<summary>Gradle (Groovy DSL)</summary>

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    testImplementation 'com.github.mbi88:common-test-utils:master-SNAPSHOT'
}
```

</details>

---

## Example

```java
public class MyTest extends BaseTestCase {

    @Test
    public void testCreateUser() {
        JSONObject payload = getResource("/user.json");

        Response response = http
                .setData(payload)
                .setExpectedStatusCode(201)
                .post("https://api.example.com/users");

        JSONObject expected = getResource("/expected-user.json");

        assertion
                .ignore("id", "createdAt")
                .jsonEquals(response, expected);

        validator
                .validate(response, "/schemas/user-schema.json");
    }

    @Test
    public void testApi() {
        JSONObject request = getResource("/json/request.json");

        Response response = http
            .setData(request)
            .setToken("Bearer " + generateToken())
            .setExpectedStatusCode(201)
            .post("https://api");

        validator.validate(response, "/schema/response-schema.json");

        JSONObject expected = getResource("/json/expected.json");
        assertion.ignore("timestamp").jsonEquals(response, expected);
    }

    private String generateToken() {
        return new TokenGenerator("secret", false).generateToken(new JSONObject().put("role", "admin"));
    }
}
```

---

## Usage Highlights

### JsonAssert

```java
assertion
  .withMode(CompareMode.NOT_ORDERED)
  .ignore("id", "meta.timestamp")
  .jsonEquals(actual, expected);
```

Modes:
- `STRICT`
- `NOT_ORDERED`
- `EXTENSIBLE_OBJECT`
- `EXTENSIBLE_ARRAY`
- `ORDERED_EXTENSIBLE_ARRAY`

Supports `Response`, `JSONObject`, `JSONArray`, or arrays of JSON objects.

---

### JsonValidator

Validate schema:

```java
validator.validate(getResource("/schema/file.json"), response);
```

---

### HttpRequest

```java
http
  .setToken("Bearer x")
  .setHeader("Accept", "application/json")
  .setExpectedStatusCode(200)
  .get("https://api");
```

Logged with curl output and can write to SLF4J file logger (`file-logger`).

---

### DateHandler

```java
DateTime future = dateHandler.add(DateTime.now(), "1y2M3d");
DateTime past = dateHandler.subtract(DateTime.now(), "4d6h");
```

---

### DataFaker

Replace dynamic fields in JSON:

```json
{
  "id": "{$uid}",
  "created": "{$date}",
  "caller": "{$caller}"
}
```

Is automatically used inside `getResource(...)`.

---

### Waiter

```java
Waiter<Response> waiter = Waiter.<Response>newBuilder()
    .setSupplier(() -> http.get("https://api/status"))
    .setResultToString(Response::asString)
    .setWaitingTime(30)
    .setDebug(true)
    .build();

Response ready = waiter.waitCondition(resp -> resp.jsonPath().getBoolean("ready"));
```

---

### TokenGenerator

```java
TokenGenerator generator = new TokenGenerator("secret", false);
String token = generator.generateToken(new JSONObject().put("role", "user"));
```

---

### QueryParameter

```java
QueryParameter qp = new QueryParameter();
qp.addParameter("limit", 5);
qp.addParameter("sort", "desc");
String query = qp.getParametersString(); // ?limit=5&sort=desc
```

---

### GraphQL Example

```java
GraphQL graphQL = new GraphQL("https://api.example.com/graphql", "Bearer token");
JSONObject query = GraphQL.getGraphQLQuery("/queries/getUser.graphql", new JSONObject().put("id", 123));
Response response = graphQL.send(query);

assertion.jsonEquals(response, getResource("/expected-user.json"));
```

---

## Retry Mechanism

This library supports **automatic retry** of both test methods and configuration methods using two separate mechanisms:

---

### 🔁 TestNG Retry (`RetryAnalyzer`)

The default retry logic for `@Test` methods is provided by `RetryAnalyzer`:

- Retries **up to 3 times** by default if no `@Retryable` annotation is present
- Supports `@Retryable(attempts = N)` to customize retry count
- Skips retry if test fails due to `"504 Gateway Timeout ERROR"`
- Respects `@NonRetryable` **on method only** — does **not** check class-level annotation

```ajva
@Test  
@Retryable(attempts = 5)  
public void flakyTest() {  
    // Will retry up to 5 times  
}
```

If `@Retryable` is **not present**, the test is retried up to 3 times (default limit in `RetryAnalyzer`).

To **disable** retry for a specific test method:

```java
@Test  
@NonRetryable  
public void testWithoutRetry() {  
    ...  
}
```

---

### ⚙️ Configuration Method Retry (`@BeforeClass`, `@BeforeMethod`, etc.)

Implemented via `IConfigurable` in `BaseTestCase`.

- Retries only happen if `@Retryable` is present on a **configuration method**
- Without `@Retryable`, the method executes **only once**
- Works for: `@BeforeMethod`, `@BeforeClass`, `@BeforeTest`, `@BeforeGroups`, `@BeforeSuite`

Example:

```java
@Retryable(attempts = 3)  
@BeforeMethod  
public void flakySetup() {  
    ...  
}
```

⚠️ Unlike test methods, there is **no retry by default** for configuration methods.

---

### ❌ `@NonRetryable` annotation

Use `@NonRetryable` to **opt out** of retry:

| Scope         | Affects                         | Notes                                                        |
|---------------|----------------------------------|--------------------------------------------------------------|
| Method        | `RetryAnalyzer` only             | Skips retry of specific test method                          |
| Class         | Gradle Retry Plugin only         | Disables retry for entire class during Gradle builds only    |

Example — skip retry for a test class (Gradle plugin only):

```java
@NonRetryable  
public class SomeTestClass {  
    // Gradle plugin will not retry tests here  
}
```

> ⚠️ TestNG's `RetryAnalyzer` **does not** check class-level `@NonRetryable` — use method-level annotation to skip retry per test.

---

### 🧩 Gradle Retry Plugin (Optional)

This library is compatible with the [Gradle Test Retry Plugin](https://github.com/gradle/test-retry-gradle-plugin), which allows retrying failed `@Test` methods during CI builds.

✅ Works automatically on test failure  
❌ Cannot retry config methods like `@BeforeClass`  
❌ Cannot skip individual test methods — only full test classes

To skip retry by Gradle plugin:

```java
@NonRetryable  
public class NoRetryTests {  
    // All tests in this class will not be retried by Gradle  
}
```

### ✅ Summary

| Retry Mechanism            | Controlled by | Method-level opt-out | Class-level opt-out |
|----------------------------|---------------|-----------------------|---------------------|
| `RetryAnalyzer` (TestNG)   | `@Retryable`  | ✅ `@NonRetryable`     | ✅ `@NonRetryable`   |
| Gradle Retry Plugin        | Build config  | ❌ Not supported       | ✅ `@NonRetryable`   |

---

## Logging

This library supports logging of request/response content to a file using SLF4J.

To enable it, configure `file-logger` in `logback-test.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOG_PATTERN_FILE" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n"/>
    <property name="LOG_DIR" value="build/logs"/>
    <property name="LOG_FILE" value="${LOG_DIR}/test.log"/>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${LOG_FILE}</file>
        <append>true</append>
        <encoder>
            <pattern>${LOG_PATTERN_FILE}</pattern>
        </encoder>
    </appender>

    <logger name="file-logger" level="INFO" additivity="false">
        <appender-ref ref="FILE"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

By default, HTTP requests and responses (from http-request module) are logged via this logger, including:

- Request method, URL, headers, and body

- Response status code, body, and elapsed time

- Curl command for easy reproduction


Logs are saved to `build/logs/test.log` by default.

---

## Utility Classes

| Utility          | Description |
|------------------|-------------|
| `BaseTestCase`   | Base class with JSON, HTTP, faker, and validator support |
| `JsonDeserializer` | Load and update JSON from resources |
| `TokenGenerator` | Create JWT tokens from claims |
| `Waiter`         | Wait for condition with retries and debug output |
| `QueryParameter` | Fluent query string builder |
| `GraphQL`        | GraphQL client (raw or file-based) |
| `Retryable` / `NonRetryable` | Control retry behavior in setup/test methods |
| `Configuration`  | Fetch values from env or SSM |
| `BaseTestListener` | Logs rerun commands for failed tests |


---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
