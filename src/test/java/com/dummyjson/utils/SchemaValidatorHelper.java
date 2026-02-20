package com.dummyjson.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.ValidatableResponse;
import java.io.InputStream;

public class SchemaValidatorHelper {

    public static void validateSchema(ValidatableResponse response, String schemaFilePath) {
        InputStream schemaStream = SchemaValidatorHelper.class.getClassLoader().getResourceAsStream(schemaFilePath);
        if (schemaStream == null) {
            throw new IllegalArgumentException("Schema file not found: " + schemaFilePath);
        }
        response.assertThat().body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }
}
