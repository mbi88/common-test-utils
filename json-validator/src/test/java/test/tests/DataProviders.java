package test.tests;


import com.mbi.request.RequestBuilder;
import org.testng.annotations.DataProvider;


import static test.serializer.JsonDeserializer.getJsonArrayFromFile;
import static test.serializer.JsonDeserializer.getJsonFromFile;

public class DataProviders {

    @DataProvider(name = "SuccessValidation")
    static Object[][] getSuccessValidationData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/success_validation_jo.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a38f710000028021aa3a1")},
                {"{\"a\": 1,\"b\": null,\"c\": \"1\",\"d\": [],\"e\": {},\"f\": [{\"aa\": 1},{\"aa\": 2}],\"g\": {\"aa\": 1,\"bb\": {\"aaa\": 1}}}"}
        };
    }

    @DataProvider(name = "InvalidType")
    static Object[][] getInvalidTypeData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/invalid_type_jo.json")},
                {getJsonArrayFromFile("/jsons/invalid_type_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a3a7110000052021aa3a6")},
                {"{\"a\":\"1\",\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "NullPropertySuccess")
    static Object[][] getNullPropertySuccessData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/null_property_success_jo.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a3bd41000006c021aa3ad")},
                {"{\"a\":1,\"b\":\"a\",\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "NullPropertyFailed")
    static Object[][] getNullPropertyFailedData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/null_property_failed_jo.json")},
                {getJsonArrayFromFile("/jsons/null_property_failed_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a3db410000094021aa3b0")},
                {"{\"a\":1,\"b\":1,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "EmptyObject")
    static Object[][] getEmptyObjectData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/empty_object_jo.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a3ec1100000a4021aa3b4")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{\"a\":1},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "EmptyArray")
    static Object[][] getEmptyArrayData() {
        return new Object[][]{
                {getJsonArrayFromFile("/jsons/empty_array_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a409c100000c0021aa3c7")},
                {"[{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[1],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}},{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[1],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}]"}
        };
    }

    @DataProvider(name = "ValidateArray")
    static Object[][] getValidateArrayData() {
        return new Object[][]{
                {getJsonArrayFromFile("/jsons/validate_array_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/5aaa7729330000bb082da97e")},
                {"[\n  {\n\"a\": 1,\n\"b\": null,\n\"c\": \"1\",\n\"d\": [],\n\"e\": {},\n\"f\": [\n{\n\"aa\": 1\n}\n],\n\"g\": {\n\"aa\": 1,\n\"bb\": {\n\"aaa\": 1\n}\n}\n},\n{\n\"a\": 2,\n\"b\": null,\n\"c\": \"1\",\n\"d\": [],\n\"e\": {},\n\"f\": [\n{\n\"aa\": 1\n}\n],\n\"g\": {\n\"aa\": 1,\n\"bb\": {\n\"aaa\": 1\n}\n}\n}\n]"}
        };
    }

    @DataProvider(name = "ExtraField")
    static Object[][] getExtraFieldData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/extra_field_jo.json")},
                {getJsonArrayFromFile("/jsons/extra_field_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a416c100000dc021aa3ca")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1,\"a\":1}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "ExtraObject")
    static Object[][] getExtraObjectData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/extra_object_jo.json")},
                {getJsonArrayFromFile("/jsons/extra_object_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a4237100000f3021aa3ce")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"bb\":1}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "WithoutField")
    static Object[][] getWithoutFieldData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/wo_field_jo.json")},
                {getJsonArrayFromFile("/jsons/wo_field_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a42b710000005031aa3d0")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"bb\":1}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "InvalidValue")
    static Object[][] getInvalidValueData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/invalid_value_jo.json")},
                {getJsonArrayFromFile("/jsons/invalid_value_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a546e10000088041aa435")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":\"1\"}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "EmptyArrayFailed")
    static Object[][] getEmptyArrayFailedData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/empty_array_failed_jo.json")},
                {getJsonArrayFromFile("/jsons/empty_array_failed_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a8ebc10000044091aa526")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "EmptyObjectFailed")
    static Object[][] getEmptyObjectFailedData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/empty_object_failed_jo.json")},
                {getJsonArrayFromFile("/jsons/empty_object_failed_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a8fbb10000067091aa52a")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{}}"}
        };
    }

    @DataProvider(name = "ExtraFieldInNestedObject")
    static Object[][] getExtraFieldInNestedObjectData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/extra_field_in_nested_object_jo.json")},
                {getJsonArrayFromFile("/jsons/extra_field_in_nested_object_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a90a610000074091aa52e")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1},\"ccc\":[]}}"}
        };
    }

    @DataProvider(name = "ObjectWithoutField")
    static Object[][] getObjectWithoutFieldData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/object_without_field_jo.json")},
                {getJsonArrayFromFile("/jsons/object_without_field_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a913610000084091aa52f")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"bb\":{\"aaa\":1}}}   "}
        };
    }

    @DataProvider(name = "ObjectWithInvalidType")
    static Object[][] getObjectWithInvalidTypeData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/object_with_invalid_type_jo.json")},
                {getJsonArrayFromFile("/jsons/object_with_invalid_type_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a932d100000bf091aa533")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":\"string\",\"bb\":{\"aaa\":1}}}"}
        };
    }

    @DataProvider(name = "ObjectWithoutNestedObject")
    static Object[][] getObjectWithoutNestedObjectData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/object_without_nested_object_jo.json")},
                {getJsonArrayFromFile("/jsons/object_without_nested_object_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a9760100000220a1aa542")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{}}}"}
        };
    }

    @DataProvider(name = "NestedObjectExtraFieldFailed")
    static Object[][] getNestedObjectExtraFieldFailedData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/nested_object_extra_field_failed_jo.json")},
                {getJsonArrayFromFile("/jsons/nested_object_extra_field_failed_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a98561000003b0a1aa546")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1,\"bbb\":\"asd\"}}}"}
        };
    }

    @DataProvider(name = "NestedObjectWithoutFieldFailed")
    static Object[][] getNestedObjectWithoutFieldFailedData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/nested_object_without_field_failed_jo.json")},
                {getJsonArrayFromFile("/jsons/nested_object_without_field_failed_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a9897100000560a1aa548")},
                {"{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"bbb\":\"asd\"}}}"}
        };
    }

    @DataProvider(name = "ArrayHasInvalidObject")
    static Object[][] getArrayHasInvalidObjectData() {
        return new Object[][]{
                {getJsonFromFile("/jsons/array_has_invalid_object_jo.json")},
                {getJsonArrayFromFile("/jsons/array_has_invalid_object_ja.json")},
                {new RequestBuilder().get("http://www.mocky.io/v2/594a9a9e100000800a1aa54f")},
                {"[{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"aa\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}},{\"a\":1,\"b\":null,\"c\":\"1\",\"d\":[],\"e\":{},\"f\":[{\"aa\":1},{\"bb\":2}],\"g\":{\"aa\":1,\"bb\":{\"aaa\":1}}}]"}
        };
    }

    @DataProvider(name = "InvalidJson")
    static Object[][] getInvalidJson() {
        return new Object[][]{
                {new RequestBuilder().get("http://www.mocky.io/v2/5aaa6e1d330000bd082da94a")},
                {"123"}
        };
    }
}