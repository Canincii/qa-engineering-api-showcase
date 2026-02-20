package com.dummyjson.clients;

import com.dummyjson.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestClient {

    static {
        // Configure RestAssured to handle all status codes without throwing
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static RequestSpecification getBaseSpec() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        return new RequestSpecBuilder()
                .setBaseUri(config.getString("base.url"))
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public static RequestSpecification getAuthenticatedSpec(String token) {
        return getBaseSpec().auth().oauth2(token);
    }
}
