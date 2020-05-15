[![Build Status](https://travis-ci.org/mbi88/http-request.svg?branch=master)](https://travis-ci.org/mbi88/http-request)
[![codecov](https://codecov.io/gh/mbi88/http-request/branch/master/graph/badge.svg)](https://codecov.io/gh/mbi88/http-request)
[![](https://jitpack.io/v/mbi88/http-request.svg)](https://jitpack.io/#mbi88/http-request)


## About
 * Perform http requests and get rest-assured response.
 * Check response status code equals to expected.
 * Error message contains url, response and request as a curl.
 * Resets builder to default after each request.
 * Logs request and response to file `build/logs/http-request.log`
 * No need to add the same headers for every request - just put it to `src/main/resources/http-request.yml`

Based on:
- <a href="https://github.com/rest-assured/rest-assured">rest-assured</a> 

## Example
```java
import com.mbi.HttpRequest;
import com.mbi.request.RequestBuilder;
import org.testng.annotations.Test;

class HttpRequestTest {

    private final HttpRequest request = new RequestBuilder();

    @Test
    public void test() {
        request
                .setData("{\"name\": \"user\"}")
                .setHeader("Content-Type", "application/json")
                .setToken("Bearer xxxxxx")
                .setExpectedStatusCode(201)
                .post("https://your.url");
    }
}
```

### Properties
Set properties `src/main/resources/http-request.yml`

```yaml
connectionTimeout: 600000
headers:
  Accept: application/json
  Content-Type: application/json; charset=UTF-8
```

### Logback classic appender
Add to logback-test.xml

```$xslt
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>build/logs/http-request.log</file>
        <append>false</append>
        <encoder>
            <pattern>%date %-4relative %-5level - %msg %n</pattern>
        </encoder>
    </appender>

    <logger name="file-logger" level="DEBUG" additivity="false">
        <appender-ref ref="FILE"/>
    </logger>

    <root level="info">
        <appender-ref ref="FILE" />
    </root>
```

## See also
- <a href="https://github.com/rest-assured/rest-assured">rest-assured</a>