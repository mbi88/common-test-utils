package test.tests;

import com.mbi.JsonValidator;

import com.mbi.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static test.serializer.JsonDeserializer.getJsonFromFile;

public class JsonValidatorTest extends TestHelper {

    private final JSONObject schemaJsonObject = getJsonFromFile("/schemas/schema_json_object.json");
    private final JSONObject schemaJsonArray = getJsonFromFile("/schemas/schema_json_array.json");

    @Test(dataProvider = "SuccessValidation", dataProviderClass = DataProviders.class)
    public void testValidationSuccess(Object o) {
        checkSuccess(schemaJsonObject, o);
    }

    @Test(dataProvider = "InvalidType", dataProviderClass = DataProviders.class)
    public void testValidationWithInvalidTypeFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "NullPropertySuccess", dataProviderClass = DataProviders.class)
    public void testValidationWithNullOrStringPropertySuccess(Object o) {
        checkSuccess(schemaJsonObject, o);
    }

    @Test(dataProvider = "NullPropertyFailed", dataProviderClass = DataProviders.class)
    public void testValidationWithNullOrStringPropertyFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "EmptyObject", dataProviderClass = DataProviders.class)
    public void testValidationEmptyObjectSuccess(Object o) {
        checkSuccess(schemaJsonObject, o);
    }

    @Test(dataProvider = "EmptyArray", dataProviderClass = DataProviders.class)
    public void testValidationEmptyArraySuccess(Object o) {
        checkSuccess(schemaJsonArray, o);
    }

    @Test(dataProvider = "ValidateArray", dataProviderClass = DataProviders.class)
    public void testValidateArray(Object o) {
        checkSuccess(schemaJsonArray, o);
    }

    @Test(dataProvider = "ExtraField", dataProviderClass = DataProviders.class)
    public void testValidateExtraFieldFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "ExtraObject", dataProviderClass = DataProviders.class)
    public void testValidateExtraObjectFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "WithoutField", dataProviderClass = DataProviders.class)
    public void testValidateWithoutFieldFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "InvalidValue", dataProviderClass = DataProviders.class)
    public void testValidateWithInvalidValueFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "EmptyArrayFailed", dataProviderClass = DataProviders.class)
    public void testValidateWithEmptyArrayFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "EmptyObjectFailed", dataProviderClass = DataProviders.class)
    public void testValidateEmptyObjectFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "ExtraFieldInNestedObject", dataProviderClass = DataProviders.class)
    public void testValidateExtraField(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "ObjectWithoutField", dataProviderClass = DataProviders.class)
    public void testValidateObjectWithoutFieldFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "ObjectWithInvalidType", dataProviderClass = DataProviders.class)
    public void testValidateObjectWithInvalidType(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "ObjectWithoutNestedObject", dataProviderClass = DataProviders.class)
    public void testValidateObjectWithoutNestedObjectFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "NestedObjectExtraFieldFailed", dataProviderClass = DataProviders.class)
    public void testValidateObjectNestedObjectExtraFieldFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "NestedObjectWithoutFieldFailed", dataProviderClass = DataProviders.class)
    public void testValidateObjectNestedObjectWithoutFieldFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test(dataProvider = "ArrayHasInvalidObject", dataProviderClass = DataProviders.class)
    public void testJsonArrayHasInvalidObject(Object o) {
        checkFail(schemaJsonObject, o);
    }

    @Test
    public void testEmptySchemaFailed() {
        JSONObject j1 = new JSONObject();
        JSONObject j2 = new JSONObject("{\"a\":1}");

        checkFail(j1, j2);
    }

    @Test
    public void testInvalidSchemaNotFailed() {
        JSONObject j1 = new JSONObject("{\"b\":[]}");
        JSONObject j2 = new JSONObject("{\"a\":1}");

        checkSuccess(j1, j2);
    }

    @Test(dataProvider = "ExtraField", dataProviderClass = DataProviders.class)
    public void testNullSchema(Object o) {
        boolean failed = true;
        try {
            checkSuccess(null, o);
        } catch (NullPointerException e) {
            failed = false;
        }
        assertFalse(failed);
    }

    @Test
    public void testNullJson() {
        JSONObject j2 = new JSONObject("{\"a\":1}");

        boolean failed = true;
        try {
            new JsonValidator().validate(j2, (JSONObject) null);
        } catch (NullPointerException e) {
            failed = false;
        }
        assertFalse(failed);
        failed = true;
        try {
            new JsonValidator().validate(j2, (JSONArray) null);
        } catch (NullPointerException e) {
            failed = false;
        }
        assertFalse(failed);
        failed = true;
        try {
            new JsonValidator().validate(j2, (Response) null);
        } catch (NullPointerException e) {
            failed = false;
        }
        assertFalse(failed);
        failed = true;
        try {
            new JsonValidator().validate(j2, (String) null);
        } catch (NullPointerException e) {
            failed = false;
        }
        assertFalse(failed);
    }

    @Test(dataProvider = "InvalidJson", dataProviderClass = DataProviders.class)
    public void testInvalidJsonFailed(Object o) {
        checkFail(schemaJsonObject, o);
    }
}