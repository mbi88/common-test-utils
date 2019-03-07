## About
Based on <a href="https://github.com/everit-org/json-schema">JSON Schema Validator</a>.

Validates json schema with something of:
- `org.json.JSONObject`
- `org.json.JSONArray`
- `io.restassured.response.Response` 
- `java.lang.String`
  
## Example

```java
import com.mbi.JsonValidator;
import org.json.JSONObject;
import org.testng.annotations.Test;

public class JsonValidatorTest {
    
    private final JsonValidator validator = new JsonValidator();
    
    @Test
    public void testName() {
        // Set json schema
        JSONObject schema = new JSONObject();
        // Set json for validation
        JSONObject json = new JSONObject();
        
        validator.validate(schema, json);
    }
}
```

## See also
- <a href="http://json-schema.org/">What is json schema</a>
- <a href="https://jsonschema.net/">Where can I create schemas</a>
- <a href="http://www.jsonschemavalidator.net/">Where can I validate online</a>
- <a href="https://github.com/everit-org/json-schema">JSON Schema Validator</a>
 
